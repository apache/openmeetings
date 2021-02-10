/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Video = (function() {
	const self = {}, states = []
		, AudioCtx = window.AudioContext || window.webkitAudioContext;
	let sd, v, vc, t, footer, size, vol, iceServers
		, lm, level, userSpeaks = false, muteOthers
		, hasVideo, isSharing, isRecording;

	function __getVideo(_state) {
		const vid = self.video(_state);
		return vid && vid.length > 0 ? vid[0] : null;
	}
	function _resizeDlgArea(_w, _h) {
		if (Room.getOptions().interview) {
			VideoUtil.setPos(v, VideoUtil.getPos());
		} else if (v.dialog('instance')) {
			v.dialog('option', 'width', _w).dialog('option', 'height', _h);
		}
	}
	function _micActivity(speaks) {
		if (speaks !== userSpeaks) {
			userSpeaks = speaks;
			OmUtil.sendMessage({type: 'mic', id: 'activity', active: speaks});
		}
	}
	function _getScreenStream(msg, state, callback) {
		function __handleScreenError(err) {
			VideoManager.sendMessage({id: 'errorSharing'});
			Sharer.setShareState(SHARE_STOPPED);
			Sharer.setRecState(SHARE_STOPPED);
			OmUtil.error(err);
		}
		const b = kurentoUtils.WebRtcPeer.browser;
		let promise, cnts;
		if (VideoUtil.isEdge(b) && b.major > 16) {
			cnts = {
				video: true
			};
			promise = navigator.getDisplayMedia(cnts);
		} else if (b.name === 'Firefox') {
			// https://mozilla.github.io/webrtc-landing/gum_test.html
			cnts = Sharer.baseConstraints(sd);
			cnts.video.mediaSource = sd.shareType;
			promise = navigator.mediaDevices.getUserMedia(cnts);
		} else if (VideoUtil.sharingSupported()) {
			cnts = {
				video: true
			};
			promise = navigator.mediaDevices.getDisplayMedia(cnts);
		} else {
			promise = new Promise(() => {
				Sharer.close();
				throw 'Screen-sharing is not supported in ' + b.name + '[' + b.major + ']';
			});
		}
		promise.then(stream => {
			if (!state.disposed) {
				__createVideo(state);
				state.stream = stream;
				callback(msg, state, cnts);
			}
		}).catch(__handleScreenError);
	}
	function _getVideoStream(msg, state, callback) {
		console.log("_getVideoStream", JSON.stringify(msg));
		VideoSettings.constraints(sd, function(cnts) {
			if ((VideoUtil.hasCam(sd) && !cnts.video) || (VideoUtil.hasMic(sd) && !cnts.audio)) {
				VideoManager.sendMessage({
					id : 'devicesAltered'
					, uid: sd.uid
					, audio: !!cnts.audio
					, video: !!cnts.video
				});
			}
			if (!cnts.audio && !cnts.video) {
				OmUtil.error('Requested devices are not available');
				VideoManager.close(sd.uid)
				return;
			}
			navigator.mediaDevices.getUserMedia(cnts)
				.then(stream => {
					if (state.disposed || msg.instanceUid !== v.data('instance-uid')) {
						return;
					}
					state.localStream = stream;
					let _stream = stream;
					const data = {};
					if (stream.getAudioTracks().length !== 0) {
						lm = vc.find('.level-meter');
						lm.show();
						data.aCtx = new AudioCtx();
						data.gainNode = data.aCtx.createGain();
						data.analyser = data.aCtx.createAnalyser();
						data.aSrc = data.aCtx.createMediaStreamSource(stream);
						data.aSrc.connect(data.gainNode);
						data.gainNode.connect(data.analyser);
						if (VideoUtil.isEdge()) {
							data.analyser.connect(data.aCtx.destination);
						} else {
							data.aDest = data.aCtx.createMediaStreamDestination();
							data.analyser.connect(data.aDest);
							_stream = data.aDest.stream;
							stream.getVideoTracks().forEach(function(track) {
								_stream.addTrack(track);
							});
						}
					}
					state.data = data;
					__createVideo(state);
					state.stream = _stream;
					callback(msg, state, cnts);
				})
				.catch(function(err) {
					VideoManager.sendMessage({
						id : 'devicesAltered'
						, uid: sd.uid
						, audio: false
						, video: false
					});
					VideoManager.close(sd.uid);
					if ('NotReadableError' === err.name) {
						OmUtil.error('Camera/Microphone is busy and can\'t be used');
					} else {
						OmUtil.error(err);
					}
				});
		});
	}
	function __attachListener(state) {
		if (!state.disposed && state.data.rtcPeer) {
			const pc = state.data.rtcPeer.peerConnection;
			pc.onconnectionstatechange = function(event) {
				console.warn(`!!RTCPeerConnection state changed: ${pc.connectionState}, user: ${sd.user.displayName}, uid: ${sd.uid}`);
				switch(pc.connectionState) {
					case "connected":
						if (sd.self) {
							// The connection has become fully connected
							OmUtil.alert('info', `Connection to Media server has been established`, 3000);//notify user
						}
						break;
					case "disconnected":
					case "failed":
						//connection has been dropped
						OmUtil.alert('warning', `Media server connection for user ${sd.user.displayName} is ${pc.connectionState}, will try to re-connect`, 3000);//notify user
						_refresh();
						break;
					case "closed":
						// The connection has been closed
						break;
				}
			}
		}
	}
	function __createSendPeer(msg, state, cnts) {
		console.log('video __createSendPeer', JSON.stringify(msg), state, cnts);
		state.options = {
			videoStream: state.stream
			, mediaConstraints: cnts
			, onicecandidate: self.onIceCandidate
		};
		if (!isSharing) {
			state.options.localVideo = __getVideo(state);
		}
		const data = state.data;
		data.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(
			VideoUtil.addIceServers(state.options, msg)
			, function (error) {
				if (state.disposed || true === data.rtcPeer.cleaned) {
					return;
				}
				if (error) {
					return OmUtil.error(error);
				}
				if (data.analyser) {
					level = MicLevel();
					level.meter(data.analyser, lm, _micActivity, OmUtil.error);
				}
				data.rtcPeer.generateOffer(function(genErr, offerSdp) {
					if (state.disposed || true === data.rtcPeer.cleaned) {
						return;
					}
					if (genErr) {
						console.error('Sender sdp offer error ' + genErr);
						return OmUtil.error('Sender sdp offer error ' + genErr);
					}
					console.log('Invoking Sender SDP offer callback function - broadcastStarted');
					OmUtil.log('Invoking Sender SDP offer callback function');
					const bmsg = {
							id : 'broadcastStarted'
							, uid: sd.uid
							, sdpOffer: offerSdp
						}, vtracks = state.stream.getVideoTracks();
					if (vtracks && vtracks.length > 0) {
						const vts = vtracks[0].getSettings();
						bmsg.width = vts.width;
						bmsg.height = vts.height;
						bmsg.fps = vts.frameRate;
					}
					VideoManager.sendMessage(bmsg);
					if (isSharing) {
						Sharer.setShareState(SHARE_STARTED);
					}
					if (isRecording) {
						Sharer.setRecState(SHARE_STARTED);
					}
				});
			});
		data.rtcPeer.cleaned = false;
		__attachListener(state);
	}
	function _createSendPeer(msg, state) {
		if (isSharing || isRecording) {
			_getScreenStream(msg, state, __createSendPeer);
		} else {
			_getVideoStream(msg, state, __createSendPeer);
		}
	}
	function _createResvPeer(msg, state) {
		__createVideo(state);
		const options = VideoUtil.addIceServers({
			remoteVideo : __getVideo(state)
			, onicecandidate : self.onIceCandidate
		}, msg);
		const data = state.data;
		data.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(
			options
			, function(error) {
				if (state.disposed || true === data.rtcPeer.cleaned) {
					return;
				}
				if (error) {
					return OmUtil.error(error);
				}
				data.rtcPeer.generateOffer(function(genErr, offerSdp) {
					if (state.disposed || true === data.rtcPeer.cleaned) {
						return;
					}
					if (genErr) {
						return OmUtil.error('Receiver sdp offer error ' + genErr);
					}
					OmUtil.log('Invoking Receiver SDP offer callback function');
					VideoManager.sendMessage({
						id : 'addListener'
						, sender: sd.uid
						, sdpOffer: offerSdp
					});
				});
			});
		data.rtcPeer.cleaned = false;
		__attachListener(state);
	}
	function _handleMicStatus(state) {
		if (!footer || !footer.is(':visible')) {
			return;
		}
		if (state) {
			footer.text(footer.data('on'));
			footer.addClass('mic-on');
			t.addClass('mic-on');
		} else {
			footer.text(footer.data('off'));
			footer.removeClass('mic-on');
			t.removeClass('mic-on');
		}
	}
	function _initContainer(_id, name, opts, instanceUid) {
		console.log('video _initContainer', _id, name, opts, instanceUid);
		let contSel = '#user' + sd.cuid;
		const _hasVideo = VideoUtil.hasVideo(sd)
		size = {width: _hasVideo ? sd.width : 120, height: _hasVideo ? sd.height : 90};
		hasVideo = _hasVideo || $(contSel).length < 1;
		if (hasVideo) {
			if (opts.interview) {
				const area = $('.pod-area');
				const contId = uuidv4();
				contSel = '#' + contId;
				area.append($('<div class="pod"></div>').attr('id', contId));
				WbArea.updateAreaClass();
			} else {
				contSel = '.room-block .room-container';
			}
		}
		$(contSel).append(OmUtil.tmpl('#user-video', _id)
				.attr('title', name)
				.attr('data-client-uid', sd.cuid)
				.attr('data-client-type', sd.type)
				.attr('data-instance-uid', instanceUid)
				.data(self));
		v = $('#' + _id);
		vc = v.find('.video');
		muteOthers = vc.find('.mute-others');
		_setRights();
		return contSel;
	}
	function _initDialog(v, opts) {
		if (opts.interview) {
			v.dialog('option', 'draggable', false);
			v.dialog('option', 'resizable', false);
			$('.pod-area').sortable('refresh');
		} else {
			v.dialog('option', 'draggable', true);
			v.dialog('option', 'resizable', true);
			if (isSharing) {
				v.on('dialogclose', function() {
					VideoManager.close(sd.uid, true);
				});
			}
		}
		_initDialogBtns(opts);
	}
	function _initDialogBtns(opts) {
		function noDblClick(e) {
			e.dblclick(function(e) {
				e.stopImmediatePropagation();
				return false;
			});
		}
		v.parent().find('.ui-dialog-titlebar-close').remove();
		v.parent().append(OmUtil.tmpl('#video-button-bar'));
		const refresh = v.parent().find('.btn-refresh')
			, tgl = v.parent().find('.btn-toggle')
			, cls = v.parent().find('.btn-wclose');
		if (isSharing) {
			cls.click(function (e) {
				v.dialog('close');
				return false;
			});
			noDblClick(cls);
			refresh.remove();
		} else {
			cls.remove();
			refresh.click(function(e) {
				e.stopImmediatePropagation();
				_refresh();
				return false;
			});
		}
		if (opts.interview) {
			tgl.remove();
		} else {
			tgl.click(function (e) {
				e.stopImmediatePropagation();
				$(this).toggleClass('minimized');
				v.toggle();
				return false;
			});
			noDblClick(tgl);
		}
	}
	function _init(msg) {
		console.log('video init with msg', JSON.stringify(msg));
		sd = msg.stream;
		msg.instanceUid = uuidv4();
		if (!vol) {
			vol = Volume();
		}
		iceServers = msg.iceServers;
		sd.activities = sd.activities.sort();
		isSharing = VideoUtil.isSharing(sd);
		isRecording = VideoUtil.isRecording(sd);
		const _id = VideoUtil.getVid(sd.uid)
			, name = sd.user.displayName
			, _w = sd.width
			, _h = sd.height
			, opts = Room.getOptions();
		sd.self = sd.cuid === opts.uid;
		const contSel = _initContainer(_id, name, opts, msg.instanceUid);
		footer = v.find('.footer');
		if (!opts.showMicStatus) {
			footer.hide();
		}
		if (!sd.self && isSharing) {
			Sharer.close();
		}
		if (sd.self && (isSharing || isRecording)) {
			v.hide();
		} else if (hasVideo) {
			v.dialog({
				classes: {
					'ui-dialog': 'video user-video' + (opts.showMicStatus ? ' mic-status' : '')
					, 'ui-dialog-titlebar': '' + (opts.showMicStatus ? ' ui-state-highlight' : '')
				}
				, width: _w
				, minWidth: 40
				, minHeight: 50
				, autoOpen: true
				, modal: false
				, appendTo: contSel
			});
			_initDialog(v, opts);
		}
		t = v.parent().find('.ui-dialog-titlebar').attr('title', name);
		v.on('remove', _cleanup);
		if (hasVideo) {
			vc.width(_w).height(_h);
		}

		_refresh(msg);
		return v;
	}
	function _update(_c) {
		console.log("_update ", JSON.stringify(_c));
		const prevA = sd.activities
			, prevW = sd.width
			, prevH = sd.height
			, prevCam = sd.cam
			, prevMic = sd.mic;
		sd.activities = _c.activities.sort();
		sd.level = _c.level;
		sd.user.firstName = _c.user.firstName;
		sd.user.lastName = _c.user.lastName;
		sd.user.displayName = _c.user.displayName;
		sd.width = _c.width;
		sd.height = _c.height;
		sd.cam = _c.cam;
		sd.mic = _c.mic;
		const name = sd.user.displayName;
		if (hasVideo) {
			v.dialog('option', 'title', name).parent().find('.ui-dialog-titlebar').attr('title', name);
		}
		const same = prevA.length === sd.activities.length
			&& prevA.every(function(value, index) { return value === sd.activities[index]})
			&& prevW === sd.width && prevH === sd.height
			&& prevCam == sd.cam && prevMic === sd.mic;
		if (sd.self && !same) {
			console.log("_update now dispose");
			_cleanup();
			v.remove();
			_init({stream: sd, iceServers: iceServers});
		}
	}
	function __createVideo(state) {
		const _id = VideoUtil.getVid(sd.uid);
		_resizeDlgArea(size.width, size.height);
		if (hasVideo && !isSharing && !isRecording) {
			VideoUtil.setPos(v, VideoUtil.getPos(VideoUtil.getRects(VIDWIN_SEL), sd.width, sd.height + 25));
		}
		state.video = $(hasVideo ? '<video>' : '<audio>').attr('id', 'vid' + _id)
			.attr('playsinline', 'playsinline')
			//.attr('autoplay', 'autoplay')
			.prop('controls', false)
			.width(vc.width()).height(vc.height());
		if (state.data) {
			state.video.data(state.data);
		}
		if (hasVideo) {
			vc.removeClass('audio-only').css('background-image', '');
			vc.parents('.ui-dialog').removeClass('audio-only');
			state.video.attr('poster', sd.user.pictureUri);
		} else {
			vc.addClass('audio-only');
		}
		vc.append(state.video);
		if (VideoUtil.hasMic(sd)) {
			const volIco = vol.create(self)
			if (hasVideo) {
				v.parent().find('.buttonpane').append(volIco);
			} else {
				volIco.addClass('ulist-small');
				volIco.insertAfter('#user' + sd.cuid + ' .typing-activity');
			}
		} else {
			vol.destroy();
		}
	}
	function _refresh(_msg) {
		const msg = _msg || {
			iceServers: iceServers
			, instanceUid: v.length > 0 ? v.data('instance-uid') : undefined
		};
		if (sd.self) {
			console.log('video _refresh', JSON.stringify(_msg));
			VideoManager.sendMessage({
				id : 'broadcastRestarted'
				, uid: sd.uid
			});
		}
		_cleanup();
		const hasAudio = VideoUtil.hasMic(sd)
			, state = {
				disposed: false
				, data: {}
			};
		states.push(state);
		if (sd.self) {
			_createSendPeer(msg, state);
		} else {
			_createResvPeer(msg, state);
		}
		_handleMicStatus(hasAudio);
	}
	function _setRights() {
		if (Room.hasRight(['MUTE_OTHERS']) && VideoUtil.hasMic(sd)) {
			muteOthers.addClass('enabled').off().click(function() {
				VideoManager.clickMuteOthers(sd.cuid);
			});
		} else {
			muteOthers.removeClass('enabled').off();
		}
	}
	function _cleanData(data) {
		console.log("!!_cleanData", data);
		if (!data) {
			return;
		}
		if (data.analyser) {
			VideoUtil.disconnect(data.analyser);
			data.analyser = null;
		}
		if (data.gainNode) {
			VideoUtil.disconnect(data.gainNode);
			data.gainNode = null;
		}
		if (data.aSrc) {
			VideoUtil.cleanStream(data.aSrc.mediaStream);
			VideoUtil.disconnect(data.aSrc);
			data.aSrc = null;
		}
		if (data.aDest) {
			VideoUtil.disconnect(data.aDest);
			data.aDest = null;
		}
		if (data.aCtx) {
			if (data.aCtx.destination) {
				VideoUtil.disconnect(data.aCtx.destination);
			}
			if ('closed' !== data.aCtx.state) {
				try {
					data.aCtx.close();
				} catch(e) {
					console.error(e);
				}
			}
			data.aCtx = null;
		}
		VideoUtil.cleanPeer(data.rtcPeer);
		data.rtcPeer = null;
	}
	function _cleanup(evt) {
		console.log('!!Disposing participant ' + sd.uid, evt);
		OmUtil.log('!!Disposing participant ' + sd.uid);
		let state;
		while(state = states.pop()) {
			state.disposed = true;
			if (state.options) {
				delete state.options.videoStream;
				delete state.options.mediaConstraints;
				delete state.options.onicecandidate;
				delete state.options.localVideo;
				state.options = null;
			}
			_cleanData(state.data);
			VideoUtil.cleanStream(state.localStream);
			VideoUtil.cleanStream(state.stream);
			state.data = null;
			state.localStream = null;
			state.stream = null;
			const video = state.video;
			if (video && video.length > 0) {
				video.attr('id', 'dummy');
				const vidNode = video[0];
				VideoUtil.cleanStream(vidNode.srcObject);
				vidNode.srcObject = null;
				vidNode.load();
				vidNode.removeAttribute("src");
				vidNode.removeAttribute("srcObject");
				vidNode.parentNode.removeChild(vidNode);
				state.video.data({});
				state.video = null;
			}
		}
		if (lm && lm.length > 0) {
			_micActivity(false);
			lm.hide();
			muteOthers.removeClass('enabled').off();
		}
		if (level) {
			level.dispose();
			level = null;
		}
		vc.find('audio,video').remove();
		vol.destroy();
		if (evt && evt.target) {
			$(evt).off();
		}
	}
	function _reattachStream() {
		states.forEach(state => {
			if (state.video && state.video.length > 0) {
				const data = state.data
					, videoEl = state.video[0];
				if (data.rtcPeer && (!videoEl.srcObject || !videoEl.srcObject.active)) {
					videoEl.srcObject = sd.self ? data.rtcPeer.getLocalStream() : data.rtcPeer.getRemoteStream();
				}
			}
		});
	}
	function _processSdpAnswer(answer) {
		const state = states.length > 0 ? states[0] : null;
		if (!state || state.disposed || !state.data.rtcPeer || state.data.rtcPeer.cleaned) {
			return;
		}
		state.data.rtcPeer.processAnswer(answer, function (error) {
			if (true === this.cleaned) {
				return;
			}
			const video = __getVideo(state);
			if (this.peerConnection.signalingState === 'stable' && video && video.paused) {
				video.play().catch(function (err) {
					if ('NotAllowedError' === err.name) {
						VideoUtil.askPermission(function () {
							video.play();
						});
					}
				});
				return;
			}
			if (error) {
				OmUtil.error(error, true);
			}
		});
	}
	function _processIceCandidate(candidate) {
		const state = states.length > 0 ? states[0] : null;
		if (!state || state.disposed || !state.data.rtcPeer || state.data.rtcPeer.cleaned) {
			return;
		}
		state.data.rtcPeer.addIceCandidate(candidate, function (error) {
			if (true === this.cleaned) {
				return;
			}
			if (error) {
				OmUtil.error('Error adding candidate: ' + error, true);
			}
		});
	}

	self.update = _update;
	self.refresh = _refresh;
	self.mute = function(_mute) {
		vol.mute(_mute);
	};
	self.isMuted = function() {
		return vol.muted();
	};
	self.init = _init;
	self.stream = function() { return sd; };
	self.setRights = _setRights;
	self.processSdpAnswer = _processSdpAnswer;
	self.processIceCandidate = _processIceCandidate;
	self.onIceCandidate = function(candidate) {
		const opts = Room.getOptions();
		OmUtil.log('Local candidate ' + JSON.stringify(candidate));
		VideoManager.sendMessage({
			id: 'onIceCandidate'
			, candidate: candidate
			, uid: sd.uid
			, luid: sd.self ? sd.uid : opts.uid
		});
	};
	self.reattachStream = _reattachStream;
	self.video = function(_state) {
		const state = _state || (states.length > 0 ? states[0] : null);
		if (!state || state.disposed) {
			return null;
		}
		return state.video;
	};
	self.handleMicStatus = _handleMicStatus;
	return self;
});
