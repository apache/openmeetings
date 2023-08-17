/*
 * (C) Copyright 2017-2022 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// taken from here:
// https://github.com/OpenVidu/openvidu/blob/master/openvidu-browser/src/OpenViduInternal/WebRtcPeer/WebRtcPeer.ts
// and monkey-patched
const OmUtil = require('../main/omutils');

const freeice = require('freeice');

const ExceptionEventName = {
	/**
	 * The [ICE connection state](https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection/iceConnectionState)
	 * of an [RTCPeerConnection](https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection) reached `failed` status.
	 *
	 * This is a terminal error that won't have any kind of possible recovery. If the client is still connected to OpenVidu Server,
	 * then an automatic reconnection process of the media stream is immediately performed. If the ICE connection has broken due to
	 * a total network drop, then no automatic reconnection process will be possible.
	 *
	 * {@link ExceptionEvent} objects with this {@link ExceptionEvent.name} will have as {@link ExceptionEvent.origin} property a {@link Stream} object.
	 */
	 ICE_CONNECTION_FAILED: 'ICE_CONNECTION_FAILED',

	/**
	 * The [ICE connection state](https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection/iceConnectionState)
	 * of an [RTCPeerConnection](https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection) reached `disconnected` status.
	 *
	 * This is not a terminal error, and it is possible for the ICE connection to be reconnected. If the client is still connected to
	 * OpenVidu Server and after certain timeout the ICE connection has not reached a success or terminal status, then an automatic
	 * reconnection process of the media stream is performed. If the ICE connection has broken due to a total network drop, then no
	 * automatic reconnection process will be possible.
	 *
	 * You can customize the timeout for the reconnection attempt with property {@link OpenViduAdvancedConfiguration.iceConnectionDisconnectedExceptionTimeout},
	 * which by default is 4000 milliseconds.
	 *
	 * {@link ExceptionEvent} objects with this {@link ExceptionEvent.name} will have as {@link ExceptionEvent.origin} property a {@link Stream} object.
	 */
	 ICE_CONNECTION_DISCONNECTED: 'ICE_CONNECTION_DISCONNECTED',
};

class WebRtcPeer {
	constructor(configuration) {
		this.remoteCandidatesQueue = [];
		this.localCandidatesQueue = [];
		this.iceCandidateList = [];
		this.candidategatheringdone = false;

		// Same as WebRtcPeerConfiguration but without optional fields.
		this.configuration = {
			...configuration,
			iceServers: !!configuration.iceServers && configuration.iceServers.length > 0 ? configuration.iceServers : freeice(),
			mediaStream: configuration.mediaStream !== undefined ? configuration.mediaStream : null,
			mode: !!configuration.mode ? configuration.mode : 'sendrecv',
			id: !!configuration.id ? configuration.id : this.generateUniqueId()
		};
		// prettier-ignore
		OmUtil.log(`[WebRtcPeer] configuration:\n${JSON.stringify(this.configuration, null, 2)}`);

		this.pc = new RTCPeerConnection({ iceServers: this.configuration.iceServers });

		this._iceCandidateListener = (event) => {
			if (event.candidate !== null) {
				// `RTCPeerConnectionIceEvent.candidate` is supposed to be an RTCIceCandidate:
				// https://w3c.github.io/webrtc-pc/#dom-rtcpeerconnectioniceevent-candidate
				//
				// But in practice, it is actually an RTCIceCandidateInit that can be used to
				// obtain a proper candidate, using the RTCIceCandidate constructor:
				// https://w3c.github.io/webrtc-pc/#dom-rtcicecandidate-constructor
				const candidateInit = event.candidate;
				const iceCandidate = new RTCIceCandidate(candidateInit);

				this.configuration.onIceCandidate(iceCandidate);
				if (iceCandidate.candidate !== '') {
					this.localCandidatesQueue.push(iceCandidate);
				}
			}
		};
		this.pc.addEventListener('icecandidate', this._iceCandidateListener);

		this._signalingStateChangeListener = async () => {
			if (this.pc.signalingState === 'stable') {
				// SDP Offer/Answer finished. Add stored remote candidates.
				while (this.iceCandidateList.length > 0) {
					let candidate = this.iceCandidateList.shift();
					try {
						await this.pc.addIceCandidate(candidate);
					} catch (error) {
						console.error('Error when calling RTCPeerConnection#addIceCandidate for RTCPeerConnection ' + this.getId(), error);
					}
				}
			}
		};
		this.pc.addEventListener('signalingstatechange', this._signalingStateChangeListener);
		if (this.configuration.onConnectionStateChange) {
			this.pc.addEventListener('connectionstatechange', this.configuration.onConnectionStateChange);
		}
	}

	getId() {
		return this.configuration.id;
	}

	/**
	 * This method frees the resources used by WebRtcPeer
	 */
	dispose() {
		OmUtil.log('Disposing WebRtcPeer');
		if (this.pc) {
			if (this.pc.signalingState === 'closed') {
				return;
			}
			this.pc.removeEventListener('icecandidate', this._iceCandidateListener);
			this._iceCandidateListener = undefined;
			this.pc.removeEventListener('signalingstatechange', this._signalingStateChangeListener);
			this._signalingStateChangeListener = undefined;
			if (this._iceConnectionStateChangeListener) {
				this.pc.removeEventListener('iceconnectionstatechange', this._iceConnectionStateChangeListener);
				this._iceConnectionStateChangeListener = undefined;
			}
			if (this.configuration.onConnectionStateChange) {
				this.pc.removeEventListener('connectionstatechange', this.configuration.onConnectionStateChange);
			}
			this.configuration = {};
			this.pc.close();
			this.remoteCandidatesQueue = [];
			this.localCandidatesQueue = [];
		}
	}

	/**
	 * Creates an SDP offer from the local RTCPeerConnection to send to the other peer.
	 * Only if the negotiation was initiated by this peer.
	 */
	async createOffer() {
		// TODO: Delete this conditional when all supported browsers are
		// modern enough to implement the Transceiver methods.
		if (!('addTransceiver' in this.pc)) {
			OmUtil.error(
				'[createOffer] Method RTCPeerConnection.addTransceiver() is NOT available; using LEGACY offerToReceive{Audio,Video}'
			);
			return this.createOfferLegacy();
		} else {
			OmUtil.log('[createOffer] Method RTCPeerConnection.addTransceiver() is available; using it');
		}

		// Spec doc: https://w3c.github.io/webrtc-pc/#dom-rtcpeerconnection-addtransceiver

		if (this.configuration.mode !== 'recvonly') {
			// To send media, assume that all desired media tracks have been
			// already added by higher level code to our MediaStream.

			if (!this.configuration.mediaStream) {
				throw new Error(
					`[WebRtcPeer.createOffer] Direction is '${this.configuration.mode}', but no stream was configured to be sent`
				);
			}

			for (const track of this.configuration.mediaStream.getTracks()) {
				const tcInit = {
					direction: this.configuration.mode,
					streams: [this.configuration.mediaStream]
				};

				if (track.kind === 'video' && this.configuration.simulcast) {
					// Check if the requested size is enough to ask for 3 layers.
					const trackSettings = track.getSettings();
					const trackConsts = track.getConstraints();

					const trackWidth = typeof(trackSettings.width) === 'object' ? trackConsts.width.ideal : trackConsts.width || 0;
					const trackHeight = typeof(trackSettings.height) === 'object' ? trackConsts.height.ideal : trackConsts.height || 0;
					OmUtil.info(`[createOffer] Video track dimensions: ${trackWidth}x${trackHeight}`);

					const trackPixels = trackWidth * trackHeight;
					let maxLayers = 0;
					if (trackPixels >= 960 * 540) {
						maxLayers = 3;
					} else if (trackPixels >= 480 * 270) {
						maxLayers = 2;
					} else {
						maxLayers = 1;
					}

					tcInit.sendEncodings = [];
					for (let l = 0; l < maxLayers; l++) {
						const layerDiv = 2 ** (maxLayers - l - 1);

						const encoding = {
							rid: 'rdiv' + layerDiv.toString(),

							// @ts-ignore -- Property missing from DOM types.
							scalabilityMode: 'L1T1'
						};

						if (['detail', 'text'].includes(track.contentHint)) {
							// Prioritize best resolution, for maximum picture detail.
							encoding.scaleResolutionDownBy = 1.0;

							// @ts-ignore -- Property missing from DOM types.
							encoding.maxFramerate = Math.floor(30 / layerDiv);
						} else {
							encoding.scaleResolutionDownBy = layerDiv;
						}

						tcInit.sendEncodings.push(encoding);
					}
				}

				const tc = this.pc.addTransceiver(track, tcInit);

				if (track.kind === 'video') {
					let sendParams = tc.sender.getParameters();
					let needSetParams = false;

					if (sendParams.degradationPreference && !sendParams.degradationPreference.length) {
						// degradationPreference for video: "balanced", "maintain-framerate", "maintain-resolution".
						// https://www.w3.org/TR/2018/CR-webrtc-20180927/#dom-rtcdegradationpreference
						if (['detail', 'text'].includes(track.contentHint)) {
							sendParams.degradationPreference = 'maintain-resolution';
						} else {
							sendParams.degradationPreference = 'balanced';
						}

						OmUtil.info(`[createOffer] Video sender Degradation Preference set: ${sendParams.degradationPreference}`);

						// Firefox implements degradationPreference on each individual encoding!
						// (set it on every element of the sendParams.encodings array)

						needSetParams = true;
					}

					// Check that the simulcast encodings were applied.
					// Firefox doesn't implement `RTCRtpTransceiverInit.sendEncodings`
					// so the only way to enable simulcast is with `RTCRtpSender.setParameters()`.
					//
					// This next block can be deleted when Firefox fixes bug #1396918:
					// https://bugzilla.mozilla.org/show_bug.cgi?id=1396918
					//
					// NOTE: This is done in a way that is compatible with all browsers, to save on
					// browser-conditional code. The idea comes from WebRTC Adapter.js:
					// * https://github.com/webrtcHacks/adapter/issues/998
					// * https://github.com/webrtcHacks/adapter/blob/v7.7.0/src/js/firefox/firefox_shim.js#L231-L255
					if (this.configuration.simulcast) {
						if (sendParams.encodings.length !== tcInit.sendEncodings.length) {
							sendParams.encodings = tcInit.sendEncodings;

							needSetParams = true;
						}
					}

					if (needSetParams) {
						OmUtil.log(`[createOffer] Setting new RTCRtpSendParameters to video sender`);
						try {
							await tc.sender.setParameters(sendParams);
						} catch (error) {
							let message = `[WebRtcPeer.createOffer] Cannot set RTCRtpSendParameters to video sender`;
							if (error instanceof Error) {
								message += `: ${error.message}`;
							}
							throw new Error(message);
						}
					}
				}
			}
		} else {
			// To just receive media, create new recvonly transceivers.
			for (const kind of ['audio', 'video']) {
				// Check if the media kind should be used.
				if (!this.configuration.mediaConstraints[kind]) {
					continue;
				}

				this.configuration.mediaStream = new MediaStream();
				this.pc.addTransceiver(kind, {
					direction: this.configuration.mode,
					streams: [this.configuration.mediaStream]
				});
			}
		}

		let sdpOffer;
		try {
			sdpOffer = await this.pc.createOffer();
		} catch (error) {
			let message = `[WebRtcPeer.createOffer] Browser failed creating an SDP Offer`;
			if (error instanceof Error) {
				message += `: ${error.message}`;
			}
			throw new Error(message);
		}

		return sdpOffer;
	}

	/**
	 * Creates an SDP answer from the local RTCPeerConnection to send to the other peer
	 * Only if the negotiation was initiated by the other peer
	 */
	createAnswer() {
		return new Promise((resolve, reject) => {
			// TODO: Delete this conditional when all supported browsers are
			// modern enough to implement the Transceiver methods.
			if ('getTransceivers' in this.pc) {
				OmUtil.log('[createAnswer] Method RTCPeerConnection.getTransceivers() is available; using it');

				// Ensure that the PeerConnection already contains one Transceiver
				// for each kind of media.
				// The Transceivers should have been already created internally by
				// the PC itself, when `pc.setRemoteDescription(sdpOffer)` was called.

				for (const kind of ['audio', 'video']) {
					// Check if the media kind should be used.
					if (!this.configuration.mediaConstraints[kind]) {
						continue;
					}

					let tc = this.pc.getTransceivers().find((tc) => tc.receiver.track.kind === kind);

					if (tc) {
						// Enforce our desired direction.
						tc.direction = this.configuration.mode;
					} else {
						return reject(new Error(`${kind} requested, but no transceiver was created from remote description`));
					}
				}

				this.pc
					.createAnswer()
					.then((sdpAnswer) => resolve(sdpAnswer))
					.catch((error) => reject(error));
			} else {
				// TODO: Delete else branch when all supported browsers are
				// modern enough to implement the Transceiver methods

				let offerAudio,
					offerVideo = true;
				if (!!this.configuration.mediaConstraints) {
					offerAudio =
						typeof this.configuration.mediaConstraints.audio === 'boolean' ? this.configuration.mediaConstraints.audio : true;
					offerVideo =
						typeof this.configuration.mediaConstraints.video === 'boolean' ? this.configuration.mediaConstraints.video : true;
					const constraints = {
						offerToReceiveAudio: offerAudio,
						offerToReceiveVideo: offerVideo
					};
					(this.pc).createAnswer(constraints)
						.then((sdpAnswer) => resolve(sdpAnswer))
						.catch((error) => reject(error));
				}
			}

			// else, there is nothing to do; the legacy createAnswer() options do
			// not offer any control over which tracks are included in the answer.
		});
	}

	/**
	 * This peer initiated negotiation. Step 1/4 of SDP offer-answer protocol
	 */
	processLocalOffer(offer) {
		return new Promise((resolve, reject) => {
			this.pc
				.setLocalDescription(offer)
				.then(() => {
					const localDescription = this.pc.localDescription;
					if (!!localDescription) {
						OmUtil.log('Local description set', localDescription.sdp);
						return resolve();
					} else {
						return reject('Local description is not defined');
					}
				})
				.catch((error) => reject(error));
		});
	}

	/**
	 * Other peer initiated negotiation. Step 2/4 of SDP offer-answer protocol
	 */
	processRemoteOffer(sdpOffer) {
		return new Promise((resolve, reject) => {
			const offer = {
				type: 'offer',
				sdp: sdpOffer
			};
			OmUtil.log('SDP offer received, setting remote description', offer);

			if (this.pc.signalingState === 'closed') {
				return reject('RTCPeerConnection is closed when trying to set remote description');
			}
			this.setRemoteDescription(offer)
				.then(() => resolve())
				.catch((error) => reject(error));
		});
	}

	/**
	 * Other peer initiated negotiation. Step 3/4 of SDP offer-answer protocol
	 */
	processLocalAnswer(answer) {
		return new Promise((resolve, reject) => {
			OmUtil.log('SDP answer created, setting local description');
			if (this.pc.signalingState === 'closed') {
				return reject('RTCPeerConnection is closed when trying to set local description');
			}
			this.pc
				.setLocalDescription(answer)
				.then(() => resolve())
				.catch((error) => reject(error));
		});
	}

	/**
	 * This peer initiated negotiation. Step 4/4 of SDP offer-answer protocol
	 */
	processRemoteAnswer(sdpAnswer) {
		return new Promise((resolve, reject) => {
			const answer = {
				type: 'answer',
				sdp: sdpAnswer
			};
			OmUtil.log('SDP answer received, setting remote description');

			if (this.pc.signalingState === 'closed') {
				return reject('RTCPeerConnection is closed when trying to set remote description');
			}
			this.setRemoteDescription(answer)
				.then(() => {
					resolve();
				})
				.catch((error) => reject(error));
		});
	}

	/**
	 * @hidden
	 */
	async setRemoteDescription(sdp) {
		return this.pc.setRemoteDescription(sdp);
	}

	/**
	 * Callback function invoked when an ICE candidate is received
	 */
	addIceCandidate(iceCandidate) {
		return new Promise((resolve, reject) => {
			OmUtil.log('Remote ICE candidate received', iceCandidate);
			this.remoteCandidatesQueue.push(iceCandidate);
			switch (this.pc.signalingState) {
				case 'closed':
					reject(new Error('PeerConnection object is closed'));
					break;
				case 'stable':
					if (!!this.pc.remoteDescription) {
						this.pc
							.addIceCandidate(iceCandidate)
							.then(() => resolve())
							.catch((error) => reject(error));
					} else {
						this.iceCandidateList.push(iceCandidate);
						resolve();
					}
					break;
				default:
					this.iceCandidateList.push(iceCandidate);
					resolve();
			}
		});
	}

	addIceConnectionStateChangeListener(otherId) {
		if (!this._iceConnectionStateChangeListener) {
			this._iceConnectionStateChangeListener = () => {
				const iceConnectionState = this.pc.iceConnectionState;
				switch (iceConnectionState) {
					case 'disconnected':
						// Possible network disconnection
						const msg1 =
							'IceConnectionState of RTCPeerConnection ' +
							this.configuration.id +
							' (' +
							otherId +
							') change to "disconnected". Possible network disconnection';
						console.warn(msg1);
						this.configuration.onIceConnectionStateException(ExceptionEventName.ICE_CONNECTION_DISCONNECTED, msg1);
						break;
					case 'failed':
						const msg2 = 'IceConnectionState of RTCPeerConnection ' + this.configuration.id + ' (' + otherId + ') to "failed"';
						console.error(msg2);
						this.configuration.onIceConnectionStateException(ExceptionEventName.ICE_CONNECTION_FAILED, msg2);
						break;
					case 'closed':
						OmUtil.log(
							'IceConnectionState of RTCPeerConnection ' + this.configuration.id + ' (' + otherId + ') change to "closed"'
						);
						break;
					case 'new':
						OmUtil.log('IceConnectionState of RTCPeerConnection ' + this.configuration.id + ' (' + otherId + ') change to "new"');
						break;
					case 'checking':
						OmUtil.log(
							'IceConnectionState of RTCPeerConnection ' + this.configuration.id + ' (' + otherId + ') change to "checking"'
						);
						break;
					case 'connected':
						OmUtil.log(
							'IceConnectionState of RTCPeerConnection ' + this.configuration.id + ' (' + otherId + ') change to "connected"'
						);
						break;
					case 'completed':
						OmUtil.log(
							'IceConnectionState of RTCPeerConnection ' + this.configuration.id + ' (' + otherId + ') change to "completed"'
						);
						break;
				}
			};
		}
		this.pc.addEventListener('iceconnectionstatechange', this._iceConnectionStateChangeListener);
	}

	/**
	 * @hidden
	 */
	generateUniqueId() {
		return crypto.randomUUID();
	}

	get stream() {
		return this.pc.getLocalStreams()[0] || this.pc.getRemoteStreams()[0];
	}

	// LEGACY code
	deprecatedPeerConnectionTrackApi() {
		for (const track of this.configuration.mediaStream.getTracks()) {
			this.pc.addTrack(track, this.configuration.mediaStream);
		}
	}

	// DEPRECATED LEGACY METHOD: Old WebRTC versions don't implement
	// Transceivers, and instead depend on the deprecated
	// "offerToReceiveAudio" and "offerToReceiveVideo".
	createOfferLegacy() {
		if (!!this.configuration.mediaStream) {
			this.deprecatedPeerConnectionTrackApi();
		}

		const hasAudio = this.configuration.mediaConstraints.audio;
		const hasVideo = this.configuration.mediaConstraints.video;

		const options = {
			offerToReceiveAudio: this.configuration.mode !== 'sendonly' && hasAudio,
			offerToReceiveVideo: this.configuration.mode !== 'sendonly' && hasVideo
		};

		OmUtil.log('[createOfferLegacy] RTCPeerConnection.createOffer() options:', JSON.stringify(options));

		return this.pc.createOffer(options);
	}
}

class WebRtcPeerRecvonly extends WebRtcPeer {
	constructor(configuration) {
		configuration.mode = 'recvonly';
		super(configuration);
	}
};

class WebRtcPeerSendonly extends WebRtcPeer {
	constructor(configuration) {
		configuration.mode = 'sendonly';
		super(configuration);
	}
};

class WebRtcPeerSendrecv extends WebRtcPeer {
	constructor(configuration) {
		configuration.mode = 'sendrecv';
		super(configuration);
	}
};

module.exports = {
	Recvonly: WebRtcPeerRecvonly,
	Sendonly: WebRtcPeerSendonly
};
