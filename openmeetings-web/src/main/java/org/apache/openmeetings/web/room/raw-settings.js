/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
$.widget('openmeetings.iconselectmenu', $.ui.selectmenu, {
	_renderItem: function(ul, item) {
		ul.addClass('settings-menu');
		const li = $('<li>'), wrapper = $('<div>', {text: item.label});
		if (item.disabled) {
			li.addClass('ui-state-disabled');
		}
		$('<span>', {
			style: item.element.attr('data-style')
			, 'class': 'ui-icon ' + (item.element.attr('data-class') || 'ui-icon-blank')
		}).appendTo(wrapper);
		return li.append(wrapper).appendTo(ul);
	}
});
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
				_micActivity(vol);
			};
		} catch (err) {
			_error(err);
		}
	}
	function _dispose() {
		if (!!ctx) {
			VideoUtil.cleanStream(mic.mediaStream);
			mic.disconnect();
			ctx.destination.disconnect();
			script.disconnect();
			script.onaudioprocess = null;
			ctx.close();
			ctx = null;
		}
	}
	return {
		meter: _meter
		, dispose: _dispose
	};
});
var VideoSettings = (function() {
	const DEV_AUDIO = 'audioinput', DEV_VIDEO = 'videoinput';
	let vs, lm, s, cam, mic, res, o, rtcPeer, timer
		, vidScroll, vid, recBtn, playBtn, recAllowed = false
		, level;
	const MsgBase = {type: 'kurento', mode: 'test'};
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
		return s;
	}
	function _save(refr) {
		const _s = Settings.save(s);
		if (typeof(avSettings) === 'function') {
			avSettings(_s);
		}
		if (refr && typeof(VideoManager) === 'object' && o.uid) {
			VideoManager.refresh(o.uid);
		}
	}
	function _clear(_ms) {
		const ms = _ms || (vid.length === 1 ? vid[0].srcObject : null);
		VideoUtil.cleanStream(ms);
		if (vid.length === 1) {
			vid[0].srcObject = null;
		}
		VideoUtil.cleanPeer(rtcPeer);
		if (!!level) {
			level.dispose();
			level = null;
		}
		_micActivity(0);
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
			$('#jsInfo').kendoNotification({
				autoHideAfter: 0
				, button: true
				, hideOnClick: false
			}).getKendoNotification().info(o.infoMsg);
		}
		OmUtil.initErrs($('#jsNotifications').kendoNotification({
			autoHideAfter: 20000
			, button: true
			, hideOnClick: false
			, stacking: 'up'
		}));
		vs = $('#video-settings');
		lm = vs.find('.level-meter');
		cam = vs.find('select.cam').iconselectmenu({
			appendTo: '.cam-row'
			, change: function() {
				_readValues();
			}
		});
		mic = vs.find('select.mic').iconselectmenu({
			appendTo: '.mic-row'
			, change: function() {
				_readValues();
			}
		});
		res = vs.find('select.cam-resolution').iconselectmenu({
			appendTo: '.res-row'
			, change: function() {
				_readValues();
			}
		});
		vidScroll = vs.find('.vid-block .video-conainer');
		timer = vs.find('.timer');
		vid = vidScroll.find('video');
		recBtn = vs.find('.rec-start')
			.button({icon: "ui-icon-bullet"})
			.click(function() {
				recBtn.prop('disabled', true).button('refresh');
				_setEnabled(true);
				OmUtil.sendMessage({
					id : 'wannaRecord'
				}, MsgBase);
			});
		playBtn = vs.find('.play')
			.button({icon: "ui-icon-play"})
			.click(function() {
				recBtn.prop('disabled', true).button('refresh');
				_setEnabled(true);
				OmUtil.sendMessage({
					id : 'wannaPlay'
				}, MsgBase);
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
		lm.kendoProgressBar({ value: 0, showStatus: false });
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
	//each bool OR https://developer.mozilla.org/en-US/docs/Web/API/MediaTrackConstraints
	// min/ideal/max/exact/mandatory can also be used
	function _constraints(sd, callback) {
		_getDevConstraints(function(devCnts){
			const cnts = {};
			if (devCnts.video && false === o.audioOnly && VideoUtil.hasVideo(sd) && s.video.cam > -1) {
				cnts.video = {
					width: s.video.width
					, height: s.video.height
					, frameRate: o.camera.fps
				};
				if (!!s.video.camDevice) {
					cnts.video.deviceId = {
						ideal: s.video.camDevice
					};
				}
			} else {
				cnts.video = false;
			}
			if (devCnts.audio && VideoUtil.hasAudio(sd) && s.video.mic > -1) {
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
					localVideo: vid[0]
					, mediaConstraints: cnts
				}, msg);
				rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(
					options
					, function(error) {
						if (error) {
							return OmUtil.error(error);
						}
						level = MicLevel();
						level.meter(rtcPeer, _micActivity, OmUtil.error);
						rtcPeer.generateOffer(function(error, _offerSdp) {
							if (error) {
								return OmUtil.error('Error generating the offer');
							}
							if (typeof(func) === 'function') {
								func(_offerSdp, cnts);
							} else {
								_allowRec(true);
							}
						});
					});
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
	function _allowPlay() {
		_updateRec();
		playBtn.prop('disabled', false).button('refresh');
	}
	function _micActivity(level) {
		lm.getKendoProgressBar().value(140 * level); // magic number
	}
	function _setLoading(el) {
		el.find('option').remove();
		el.append(OmUtil.tmpl('#settings-option-loading'));
		el.iconselectmenu('refresh');
	}
	function _setDisabled(els) {
		els.forEach(function(el) {
			el.find('option').remove();
			el.append(OmUtil.tmpl('#settings-option-disabled'));
			el.iconselectmenu('refresh');
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
		const devCnts = {audio: false, video: false};
		navigator.mediaDevices.enumerateDevices()
			.then(function(devices) {
				devices.forEach(function(device) {
					if (DEV_AUDIO === device.kind) {
						devCnts.audio = true;
					} else if (DEV_VIDEO === device.kind) {
						devCnts.video = true;
					}
				});
				callback(devCnts);
			})
			.catch(function() {
				OmUtil.error('Unable to get the list of multimedia devices');
				callback(devCnts);
			});
	}
	function _initDevices() {
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
					_load();
					_setDisabled([cam, mic]);
					devices.forEach(function(device) {
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
					cam.iconselectmenu('refresh');
					mic.iconselectmenu('refresh');
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
						case 'canRecord':
							_readValues(m, function(_offerSdp, cnts) {
								OmUtil.info('Invoking SDP offer callback function');
								OmUtil.sendMessage({
									id : 'record'
									, sdpOffer: _offerSdp
									, video: cnts.video !== false
									, audio: cnts.audio !== false
								}, MsgBase);
								rtcPeer.on('icecandidate', _onIceCandidate);
							});
							break;
						case 'canPlay':
							{
								const options = VideoUtil.addIceServers({
									remoteVideo: vid[0]
									, mediaConstraints: {audio: true, video: true}
									, onicecandidate: _onIceCandidate
								}, m);
								_clear();
								rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(
									options
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
								}
							break;
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
		, close: function() {
			_close();
			vs.dialog('close');
		}
		, load: _load
		, save: _save
		, constraints: _constraints
	};
})();
