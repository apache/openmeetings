/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var VideoSettings = (function() {
	let vs, lm, s, cam, mic, res, o, rtcPeer, offerSdp
		, vidScroll, vid, recBtn, playBtn, recAllowed = false;
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
		}
		offerSdp = null;
		Wicket.Event.unsubscribe("/websocket/message", _onWsMessage);
	}
	function _init(options) {
		o = JSON.parse(JSON.stringify(options));
		vs = $('#video-settings');
		lm = vs.find('.level-meter');
		cam = vs.find('select.cam');
		mic = vs.find('select.mic');
		res = vs.find('select.cam-resolution');
		vidScroll = vs.find('.vid-block .video-conainer');
		vid = vidScroll.find('video');
		recBtn = vs.find('.rec-start').click(function() {
			recBtn.prop('disabled', true).button('refresh'); //TODO disable drop-downs
			cam.prop('disabled', true);
			mic.prop('disabled', true);
			res.prop('disabled', true);

			console.info('Invoking SDP offer callback function');
			const cnts = _constraints();
			OmUtil.sendMessage({
				id : 'testStart'
				, type: 'kurento'
				, sdpOffer: offerSdp
				, video: cnts.video !== false
				, audio: cnts.audio !== false
			});
		});
		playBtn = vs.find('.play').click(function() {
			//FIXME TODO swf.play();
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
						_clear();
						vs.dialog("close");
					}
				}
				, {
					text: vs.data('btn-cancel')
					, click: function() {
						_clear();
						vs.dialog("close");
					}
				}
			]
			, close: function() {
				_clear();
			}
		});
		lm.progressbar({ value: 0 });
		o.width = 300;
		o.height = 200;
		o.mode = 'settings';
		o.rights = (o.rights || []).join();
		delete o.keycode;
		vs.find('input, button').prop('disabled', true);
		vs.find('button').button();
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
		recBtn.prop('disabled', !recAllowed && (s.video.cam > -1 || s.video.mic > -1)).button('refresh');
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
		//TODO enable audio for recordings only
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
			const options = {
				localVideo: vid[0]
				, mediaConstraints: cnts
				, onicecandidate: function (candidate) {
					console.log('Local candidate' + JSON.stringify(candidate));
					OmUtil.sendMessage({
						id : 'onTestIceCandidate'
						, type: 'kurento'
						, candidate: candidate
					});
				}

			}
			rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(
				options
				, function(error) {
					if (error) {
						return _error(error);
					}
					rtcPeer.generateOffer(function(error, _offerSdp) {
						if (error) {
							return console.error('Error generating the offer');
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
	function _error(msg) {
		//FIXME TODO status field
		return console.error(msg);
	}
	function _initDevices() {
		if (!navigator.mediaDevices || !navigator.mediaDevices.enumerateDevices) {
			_error("enumerateDevices() not supported.");
			return;
		}
		cam.find('option[value!="-1"]').remove();
		mic.find('option[value!="-1"]').remove();
		navigator.mediaDevices.getUserMedia({video:true, audio:true})
			.then(function(stream) {
				const devices = navigator.mediaDevices.enumerateDevices()
					.then(function(devices) {
						return devices;
					})
					.catch(function(err) { throw err; });
				_clear(stream);
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
				cam.prop('disabled', false).change(function() {
					_readValues();
				});
				mic.prop('disabled', false).change(function() {
					_readValues();
				});
				res.change(function() {
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
				_error(err.name + ": " + err.message);
			});
	}
	function _open() {
		Wicket.Event.subscribe("/websocket/message", _onWsMessage);
		recAllowed = false;
		vs.dialog('open');
		_load();
		_initDevices();
	}
	//FIXME TODO, try to unify this
	function _onWsMessage(jqEvent, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = jQuery.parseJSON(msg);
			if (m && 'kurento' === m.type) {
				console.info('Received message: ' + m);
				/* FIXME TODO
				switch (m.id) {
					case 'broadcast':
						onBroadcast(m);
						break;
					case 'videoResponse':
						onVideoResponse(m);
						break;
					case 'iceCandidate':
						{
							const w = $('#' + VideoUtil.getVid(m.uid))
								, v = w.data()

							v.getPeer().addIceCandidate(m.candidate, function (error) {
								if (error) {
									console.error("Error adding candidate: " + error);
									return;
								}
							});
						}
						break;
					default:
						console.error('Unrecognized message', m);
				}
				*/
			}
		} catch (err) {
			//no-op
			console.error(err);
		}
	}
	return {
		init: _init
		, open: _open
		, close: function() { vs.dialog('close'); }
		, load: _load
		, save: _save
	};
})();
