/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var MicLevel = (function() {
	let ctx, mic, script, vol = .0;

	function _meter(rtcPeer, _micActivity, _error) {
		if (!rtcPeer || 'function' !== typeof(rtcPeer.getLocalStream)) {
			return;
		}
		const stream = rtcPeer.getLocalStream();
		if (!stream || stream.getAudioTracks().length < 1) {
			return;
		}
		try {
			ctx = new AudioContext();
			script = ctx.createScriptProcessor(512);
			mic = ctx.createMediaStreamSource(stream);
			mic.connect(script);
			script.connect(ctx.destination);
			let t = Date.now();
			script.onaudioprocess = function(event) {
				const arr = event.inputBuffer.getChannelData(0)
					, al = arr.length;
				let avg = 0.0;
				for (let i = 0; i < al; ++i) {
					avg += arr[i] * arr[i];
				}
				avg = Math.sqrt(avg / al);
				vol = Math.max(avg, vol * .95);
				//we will continuously get volume but do not perform re-draw too often
				if (Date.now() - t < 200) {
					return;
				}
				t = Date.now();
				_micActivity(140 * vol); // magic number
			};
		} catch (err) {
			_error(err);
		}
	}
	function _dispose() {
		if (!!ctx) {
			mic.disconnect(script);
			script.disconnect(ctx.destination);
			script.onaudioprocess = null;
			ctx = null;
		}
	}
	return {
		meter: _meter
		, dispose: _dispose
	};
});
var VideoSettings = (function() {
	let vs, lm, s, cam, mic, res, o, rtcPeer, offerSdp, timer
		, vidScroll, vid, recBtn, playBtn, recAllowed = false
		, level;
	const MsgBase = {type: 'kurento', mode: 'test'};
	function _load() {
		s = Settings.load();
		if (!s.video) {
			s.video = {};
		}
		return s;
	}
	function _save(refr) {
		const _s = Settings.save(s);
		if (typeof(avSettings) === 'function') {
			avSettings(_s);
		}
		if (refr && typeof(VideoManager) !== 'undefined' && o.uid) {
			VideoManager.refresh(o.uid, s.video);
		}
	}
	function _clear(_ms) {
		const ms = _ms || (vid.length === 1 ? vid[0].srcObject : null);
		if (ms !== null && 'function' === typeof(ms.getAudioTracks)) {
			ms.getAudioTracks().forEach(function(track) {
				track.stop();
			});
			ms.getVideoTracks().forEach(function(track) {
				track.stop();
			});
			if (vid.length === 1) {
				vid[0].srcObject = null;
			}
		}
		if (!!rtcPeer) {
			rtcPeer.dispose();
			rtcPeer = null;
		}
		if (!!level) {
			level.dispose();
			level = null;
		}
		_micActivity(0);
		offerSdp = null;
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
		OmUtil.initErrs($('#jsNotifications').kendoNotification({
			autoHideAfter: 20000
		}));
		vs = $('#video-settings');
		lm = vs.find('.level-meter');
		cam = vs.find('select.cam');
		mic = vs.find('select.mic');
		res = vs.find('select.cam-resolution');
		vidScroll = vs.find('.vid-block .video-conainer');
		timer = vs.find('.timer');
		vid = vidScroll.find('video');
		recBtn = vs.find('.rec-start')
			.button({icon: "ui-icon-bullet"})
			.click(function() {
				recBtn.prop('disabled', true).button('refresh');
				_setEnabled(true);

				OmUtil.info('Invoking SDP offer callback function');
				const cnts = _constraints();
				OmUtil.sendMessage({
					id : 'start'
					, sdpOffer: offerSdp
					, video: cnts.video !== false
					, audio: cnts.audio !== false
				}, MsgBase);
				rtcPeer.on('icecandidate', _onIceCandidate);
			});
		playBtn = vs.find('.play')
			.button({icon: "ui-icon-play"})
			.click(function() {
				recBtn.prop('disabled', true).button('refresh');
				_setEnabled(true);
				_clear();
				rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(
					{
						remoteVideo: vid[0]
						, mediaConstraints: {
							audio: true
							, video: true
						}
						, onicecandidate: _onIceCandidate
					}
					, function(error) {
						if (error) {
							return OmUtil.error(error);
						}
						rtcPeer.generateOffer(function(error, offerSdp) {
							if (error) {
								return OmUtil.error('Error generating the offer');
							}
							OmUtil.sendMessage({
								id : 'play'
								, sdpOffer: offerSdp
							}, MsgBase);
						});
					});
			});
		vs.dialog({
			classes: {
				'ui-dialog': 'ui-corner-all video'
			}
			, width: 640
			, autoOpen: false
			, buttons: [
				{
					text: vs.data('btn-save')
					, icons: {
						primary: "ui-icon-disk"
					}
					, click: function() {
						_save(true);
						_close();
						vs.dialog("close");
					}
				}
				, {
					text: vs.data('btn-cancel')
					, click: function() {
						_close();
						vs.dialog("close");
					}
				}
			]
			, close: function() {
				_close();
			}
		});
		lm.progressbar({ value: 0 });
		o.width = 300;
		o.height = 200;
		o.mode = 'settings';
		o.rights = (o.rights || []).join();
		delete o.keycode;
		vs.find('input, button').prop('disabled', true);
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
		recBtn.prop('disabled', !recAllowed || (s.video.cam < 0 && s.video.mic < 0)).button('refresh');
	}
	function _constraints() {
		const cnts = {}
			, v = cam.find('option:selected')
			, m = mic.find('option:selected');
		//TODO add check if constraint is supported
		if (s.video.cam > -1) {
			cnts.video = {
				width: s.video.width
				, height: s.video.height
				, deviceId: { exact: v.data('device-id')  }
				, frameRate: { max: 30 }
			};
		} else {
			cnts.video = false;
		}
		if (s.video.mic > -1) {
			//TODO remove hardcodings
			cnts.audio = {
				sampleSize: 22
				, deviceId: { exact: m.data('device-id')  }
				, echoCancellation: true
			};
		} else {
			cnts.audio = false;
		}
		return cnts;
	}
	function _readValues() {
		const v = cam.find('option:selected')
			, m = mic.find('option:selected')
			, o = res.find('option:selected').data();
		s.video.cam = 1 * cam.val();
		s.video.mic = 1 * mic.val();
		s.video.width = o.width;
		s.video.height = o.height;
		vid.width(o.width).height(o.height);
		vidScroll.scrollLeft(Math.max(0, s.video.width / 2 - 150))
			.scrollTop(Math.max(0, s.video.height / 2 - 110));
		_clear();
		const cnts = _constraints();
		if (cnts.video !== false || cnts.audio !== false) {
			rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(
				{
					localVideo: vid[0], mediaConstraints: cnts
				}, function(error) {
					if (error) {
						return OmUtil.error(error);
					}
					level = MicLevel();
					level.meter(rtcPeer, _micActivity, OmUtil.error);
					rtcPeer.generateOffer(function(error, _offerSdp) {
						if (error) {
							return OmUtil.error('Error generating the offer');
						}
						offerSdp = _offerSdp;
						_allowRec(true);
					});
				});
		}
		_updateRec();
	}

	function _allowRec(allow) {
		recAllowed = allow;
		_updateRec();
	}
	function _allowPlay() {
		_updateRec();
		playBtn.prop('disabled', false).button('refresh');
	}
	function _micActivity(level) {
		lm.progressbar("value", Math.max(0, level));
	}
	function _initDevices() {
		if (!navigator.mediaDevices || !navigator.mediaDevices.enumerateDevices) {
			OmUtil.error('enumerateDevices() not supported.');
			return;
		}
		cam.find('option[value!="-1"]').remove();
		mic.find('option[value!="-1"]').remove();
		navigator.mediaDevices.getUserMedia({video:true, audio:true})
			.then(function(stream) {
				const devices = navigator.mediaDevices.enumerateDevices()
					.then(function(devices) {
						_clear(stream);
						return devices;
					})
					.catch(function(err) {
						_clear(stream);
						throw err;
					});
				return devices;
			})
			.then(function(devices) {
				let cCount = 0, mCount = 0;
				devices.forEach(function(device) {
					if ('audioinput' === device.kind) {
						const o = $('<option></option>').attr('value', mCount).text(device.label)
							.data('device-id', device.deviceId);
						if (mCount === s.video.cam) {
							o.prop('selected', true);
						}
						mic.append(o);
						mCount++;
					} else if ('videoinput' === device.kind) {
						const o = $('<option></option>').attr('value', cCount).text(device.label)
							.data('device-id', device.deviceId);
						if (cCount === s.video.cam) {
							o.prop('selected', true);
						}
						cam.append(o);
						cCount++;
					}
				});
				cam.prop('disabled', false).off().change(function() {
					_readValues();
				});
				mic.prop('disabled', false).off().change(function() {
					_readValues();
				});
				res.off().change(function() {
					_readValues();
				});
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
				OmUtil.error(err);
			});
	}
	function _open() {
		Wicket.Event.subscribe('/websocket/message', _onWsMessage);
		recAllowed = false;
		timer.hide();
		playBtn.prop('disabled', true).button('refresh');
		vs.dialog('open');
		_load();
		_initDevices();
	}
	function _setEnabled(enabled) {
		playBtn.prop('disabled', enabled).button('refresh');
		cam.prop('disabled', enabled);
		mic.prop('disabled', enabled);
		res.prop('disabled', enabled);
	}
	function _onStop() {
		_updateRec();
		_setEnabled(false);
	}
	//FIXME TODO, try to unify this
	function _onWsMessage(jqEvent, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = jQuery.parseJSON(msg);
			if (m && 'kurento' === m.type) {
				if ('test' === m.mode) {
					OmUtil.info('Received message: ', m);
					switch (m.id) {
						case 'playResponse':
						case 'startResponse':
							OmUtil.log('SDP answer received from server. Processing ...');
							rtcPeer.processAnswer(m.sdpAnswer, function(error) {
								if (error) {
									return OmUtil.error(error);
								}
							});
							break;
						case 'iceCandidate':
							rtcPeer.addIceCandidate(m.candidate, function(error) {
								if (error) {
									return OmUtil.error('Error adding candidate: ' + error);
								}
							});
							break;
						case 'recording':
							timer.show().find('.time').text(m.time);
							break;
						case 'recStopped':
							timer.hide();
							_onStop()
							break;
						case 'playStopped':
							_onStop();
							_readValues();
							break;
						default:
							// no-op
					}
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
	return {
		init: _init
		, open: _open
		, close: function() { _close(); vs.dialog('close'); }
		, load: _load
		, save: _save
	};
})();
