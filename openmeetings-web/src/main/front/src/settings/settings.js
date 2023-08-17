/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const Settings = require('../main/settings');

const MicLevel = require('./mic-level');
const VideoUtil = require('./video-util');
const WebRtcPeer = require('./WebRtcPeer');

const DEV_AUDIO = 'audioinput'
	, DEV_VIDEO = 'videoinput'
	, MsgBase = {type: 'kurento', mode: 'test'};
let vs, lm, s, cam, mic, res, o, rtcPeer, timer
	, vidScroll, vid, recBtn, playBtn, recAllowed = false
	, level;

function _load() {
	s = Settings.load();
	if (!s.video) {
		const _res = $('#video-settings .cam-resolution option:selected').data();
		s.video = {
			cam: 0
			, mic: 0
			, width: _res.width
			, height: _res.height
		};
	}
	if (!s.fixed) {
		s.fixed = {
			enabled: false
			, width: 120
			, height: 90
		};
	}
	return s;
}
function _save() {
	Settings.save(s);
	OmUtil.sendMessage({
		type: 'av'
		, area: 'room'
		, settings: s
	});
}
function _clear(_ms) {
	const ms = _ms || (vid && vid.length === 1 ? vid[0].srcObject : null);
	VideoUtil.cleanStream(ms);
	if (vid && vid.length === 1) {
		vid[0].srcObject = null;
	}
	VideoUtil.cleanPeer(rtcPeer);
	if (!!lm) {
		lm.hide();
	}
	if (!!level) {
		level.dispose();
		level = null;
	}
}
function _close() {
	_clear();
	Wicket.Event.unsubscribe('/websocket/message', _onWsMessage);
}
function _onIceCandidate(candidate) {
	OmUtil.log('Local candidate' + JSON.stringify(candidate));
	OmUtil.sendMessage({
		id : 'iceCandidate'
		, candidate: candidate
	}, MsgBase);
}
function _init(options) {
	o = JSON.parse(JSON.stringify(options));
	if (!!o.infoMsg) {
		OmUtil.alert('info', o.infoMsg, 0);
	}
	vs = $('#video-settings');
	lm = vs.find('.level-meter');
	cam = vs.find('select.cam').change(function() {
		_readValues();
	});
	mic = vs.find('select.mic').change(function() {
		_readValues();
	});
	res = vs.find('select.cam-resolution').change(function() {
		_readValues();
	});
	vidScroll = vs.find('.vid-block .video-conainer');
	timer = vs.find('.timer');
	vid = vidScroll.find('video');
	recBtn = vs.find('.rec-start')
		.click(function() {
			recBtn.prop('disabled', true);
			_setEnabled(true);
			OmUtil.sendMessage({
				id : 'wannaRecord'
			}, MsgBase);
		});
	playBtn = vs.find('.play')
		.click(function() {
			recBtn.prop('disabled', true);
			_setEnabled(true);
			OmUtil.sendMessage({
				id : 'wannaPlay'
			}, MsgBase);
		});
	vs.find('.btn-save').off().click(function() {
		_save();
		_close();
		vs.modal("hide");
	});
	vs.find('.btn-cancel').off().click(function() {
		_close();
		vs.modal("hide");
	});
	vs.off().on('hidden.bs.modal', function () {
		_close();
	});
	o.width = 300;
	o.height = 200;
	o.mode = 'settings';
	o.rights = (o.rights || []).join();
	delete o.keycode;
	vs.find('.modal-body input, .modal-body button').prop('disabled', true);
	const rr = vs.find('.cam-resolution').parents('.sett-row');
	if (!o.interview) {
		rr.show();
	} else {
		rr.hide();
	}
	_load();
	_save(); // trigger settings update
}
function _updateRec() {
	recBtn.prop('disabled', !recAllowed || (s.video.cam < 0 && s.video.mic < 0));
}
function _setCntsDimensions(cnts) {
	if (VideoUtil.isSafari()) {
		let width = s.video.width;
		//valid widths are 320, 640, 1280
		[320, 640, 1280].some(function(w) {
			if (width < w + 1) {
				width = w;
				return true;
			}
			return false;
		});
		cnts.video.width = width < 1281 ? width : 1280;
	} else {
		cnts.video.width = o.interview ? 320 : s.video.width;
		cnts.video.height = o.interview ? 260 : s.video.height;
	}
}
//each bool OR https://developer.mozilla.org/en-US/docs/Web/API/MediaTrackConstraints
// min/ideal/max/exact/mandatory can also be used
function _constraints(sd, callback) {
	_getDevConstraints(function(devCnts) {
		const cnts = {
			videoEnabled: VideoUtil.hasCam(sd)
			, audioEnabled: VideoUtil.hasMic(sd)
		};
		if (devCnts.video && false === o.audioOnly && s.video.cam > -1) {
			cnts.video = {
				frameRate: o.camera.fps
			};
			_setCntsDimensions(cnts)
			if (!!s.video.camDevice) {
				cnts.video.deviceId = {
					ideal: s.video.camDevice
				};
			} else {
				cnts.video.facingMode = {
					ideal: 'user'
				}
			}
		} else {
			cnts.video = false;
		}
		if (devCnts.audio && s.video.mic > -1) {
			cnts.audio = {
				sampleRate: o.microphone.rate
				, echoCancellation: o.microphone.echo
				, noiseSuppression: o.microphone.noise
			};
			if (!!s.video.micDevice) {
				cnts.audio.deviceId = {
					ideal: s.video.micDevice
				};
			}
		} else {
			cnts.audio = false;
		}
		callback(cnts);
	});
}
function _readValues(msg, func) {
	const v = cam.find('option:selected')
		, m = mic.find('option:selected')
		, o = res.find('option:selected').data();
	s.video.cam = 1 * cam.val();
	s.video.camDevice = v.data('device-id');
	s.video.mic = 1 * mic.val();
	s.video.micDevice = m.data('device-id');
	s.video.width = o.width;
	s.video.height = o.height;
	vid.width(o.width).height(o.height);
	vidScroll.scrollLeft(Math.max(0, s.video.width / 2 - 150))
		.scrollTop(Math.max(0, s.video.height / 2 - 110));
	_clear();
	_constraints(null, function(cnts) {
		if (cnts.video !== false || cnts.audio !== false) {
			const options = VideoUtil.addIceServers({
				mediaConstraints: cnts
				, onIceCandidate: _onIceCandidate
			}, msg);
			navigator.mediaDevices.getUserMedia(cnts)
				.then(stream => {
					VideoUtil.playSrc(vid[0], stream, true);
					options.mediaStream = stream;

					rtcPeer = new WebRtcPeer.Sendonly(options);
					if (cnts.audio) {
						lm.show();
						level = new MicLevel();
						level.meterStream(stream, lm, function(){}, OmUtil.error, false);
					} else {
						lm.hide();
					}
					return rtcPeer.createOffer();
				})
				.then(sdpOffer => {
					rtcPeer.processLocalOffer(sdpOffer);
					if (typeof(func) === 'function') {
						func(sdpOffer.sdp, cnts);
					} else {
						_allowRec(true);
					}
				}).catch(_ => OmUtil.error('Error generating the offer'));
		}
		if (!msg) {
			_updateRec();
		}
	});
}

function _allowRec(allow) {
	recAllowed = allow;
	_updateRec();
}
function _setLoading(el) {
	el.find('option').remove();
	el.append(OmUtil.tmpl('#settings-option-loading'));
}
function _setDisabled(els) {
	els.forEach(function(el) {
		el.find('option').remove();
		el.append(OmUtil.tmpl('#settings-option-disabled'));
	});
}
function _setSelectedDevice(dev, devIdx) {
	let o = dev.find('option[value="' + devIdx + '"]');
	if (o.length === 0 && devIdx !== -1) {
		o = dev.find('option[value="0"]');
	}
	o.prop('selected', true);
}
function _getDevConstraints(callback) {
	const devCnts = {audio: false, video: false, devices: []};
	if (window.isSecureContext === false) {
		OmUtil.error($('#settings-https-required').text());
		return;
	}
	if (!navigator.mediaDevices || !navigator.mediaDevices.enumerateDevices) {
		OmUtil.error('enumerateDevices() not supported.');
		return;
	}
	navigator.mediaDevices.enumerateDevices()
		.then(devices => devices.forEach(device => {
				if (DEV_AUDIO === device.kind || DEV_VIDEO === device.kind) {
					devCnts.devices.push({
						kind: device.kind
						, label: device.label || (device.kind + ' ' + devCnts.devices.length)
						, deviceId: device.deviceId
					});
				}
				if (DEV_AUDIO === device.kind) {
					devCnts.audio = true;
				} else if (DEV_VIDEO === device.kind) {
					devCnts.video = true;
				}
			}))
		.catch(() => OmUtil.error('Unable to get the list of multimedia devices'))
		.finally(() => callback(devCnts));
}
function _initDevices() {
	if (window.isSecureContext === false) {
		OmUtil.error($('#settings-https-required').text());
		return;
	}
	if (!navigator.mediaDevices || !navigator.mediaDevices.enumerateDevices) {
		OmUtil.error('enumerateDevices() not supported.');
		return;
	}
	_setLoading(cam);
	_setLoading(mic);
	_getDevConstraints(function(devCnts) {
		if (!devCnts.audio && !devCnts.video) {
			_setDisabled([cam, mic]);
			return;
		}
		navigator.mediaDevices.getUserMedia(devCnts)
			.then(stream => {
				const devices = navigator.mediaDevices.enumerateDevices()
					.catch(function(err) {
						throw err;
					})
					.finally(() => _clear(stream));
				return devices || devCnts.devices;
			})
			.catch(function() {
				return devCnts.devices;
			})
			.then(devices => {
				let cCount = 0, mCount = 0;
				_load();
				_setDisabled([cam, mic]);
				devices.forEach(device => {
					if (DEV_AUDIO === device.kind) {
						const o = $('<option></option>').attr('value', mCount).text(device.label)
							.data('device-id', device.deviceId);
						mic.append(o);
						mCount++;
					} else if (DEV_VIDEO === device.kind) {
						const o = $('<option></option>').attr('value', cCount).text(device.label)
							.data('device-id', device.deviceId);
						cam.append(o);
						cCount++;
					}
				});
				_setSelectedDevice(cam, s.video.cam);
				_setSelectedDevice(mic, s.video.mic);
				res.find('option').each(function() {
					const o = $(this).data();
					if (o.width === s.video.width && o.height === s.video.height) {
						$(this).prop('selected', true);
						return false;
					}
				});
				_readValues();
			})
			.catch(function(err) {
				_setDisabled([cam, mic]);
				OmUtil.error(err);
			});
	});
}
function _open() {
	Wicket.Event.subscribe('/websocket/message', _onWsMessage);
	recAllowed = false;
	timer.hide();
	playBtn.prop('disabled', true);
	vs.modal('show');
	_load();
	_initDevices();
}
function _setEnabled(enabled) {
	playBtn.prop('disabled', enabled);
	cam.prop('disabled', enabled);
	mic.prop('disabled', enabled);
	res.prop('disabled', enabled);
}
function _onStop() {
	_updateRec();
	_setEnabled(false);
}
function _onKMessage(m) {
	OmUtil.info('Received message: ', m);
	switch (m.id) {
		case 'canRecord':
			_readValues(m, function(_offerSdp, cnts) {
				OmUtil.info('Invoking SDP offer callback function');
				OmUtil.sendMessage({
					id : 'record'
					, sdpOffer: _offerSdp
					, video: cnts.video !== false
					, audio: cnts.audio !== false
				}, MsgBase);
			});
			break;
		case 'canPlay': {
			const options = VideoUtil.addIceServers({
				mediaConstraints: {audio: true, video: true}
				, onIceCandidate: _onIceCandidate
			}, m);
			_clear();
			rtcPeer = new WebRtcPeer.Recvonly(options);
			rtcPeer.createOffer()
				.then(sdpOffer => {
					rtcPeer.processLocalOffer(sdpOffer);
					OmUtil.sendMessage({
						id : 'play'
						, sdpOffer: sdpOffer.sdp
					}, MsgBase);
				})
				.catch(_ => OmUtil.error('Error generating the offer'));
			}
			break;
		case 'playResponse':
			OmUtil.log('Play SDP answer received from server. Processing ...');

			rtcPeer.processRemoteAnswer(m.sdpAnswer)
				.then(() => {
					const stream = rtcPeer.stream;
					if (stream) {
						VideoUtil.playSrc(vid[0], stream, false);
						lm.show();
						level = new MicLevel();
						level.meterStream(stream, lm, function(){}, OmUtil.error, true);
					};
				})
				.catch(error => OmUtil.error(error));
			break;
		case 'startResponse':
			OmUtil.log('SDP answer received from server. Processing ...');
			rtcPeer.processRemoteAnswer(m.sdpAnswer)
				.catch(error => OmUtil.error(error));
			break;
		case 'iceCandidate':
			rtcPeer.addIceCandidate(m.candidate)
				.catch(error => OmUtil.error('Error adding candidate: ' + error));
			break;
		case 'recording':
			timer.show().find('.time').text(m.time);
			break;
		case 'recStopped':
			timer.hide();
			_onStop();
			break;
		case 'playStopped':
			_onStop();
			_readValues();
			break;
		default:
			// no-op
	}
}
function _onWsMessage(jqEvent, msg) {
	try {
		if (msg instanceof Blob) {
			return; //ping
		}
		const m = JSON.parse(msg);
		if (m && 'kurento' === m.type) {
			if ('test' === m.mode) {
				_onKMessage(m);
			}
			switch (m.id) {
				case 'error':
					OmUtil.error(m.message);
					break;
				default:
					//no-op
			}
		}
	} catch (err) {
		OmUtil.error(err);
	}
}

module.exports = {
	init: _init
	, open: _open
	, close: function() {
		_close();
		vs && vs.modal('hide');
	}
	, load: _load
	, save: _save
	, constraints: _constraints
};
