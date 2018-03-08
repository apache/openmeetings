/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var VideoSettings = (function() {
	let vs, lm, s, cam, mic, res, o
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
	function _clear() {
		if (vid.length === 1) {
			const ms = vid[0].srcObject;
			if (ms !== null && 'function' === typeof(ms.getAudioTracks)) {
				ms.getAudioTracks().forEach(function(track) {
					track.stop();
				});
				ms.getVideoTracks().forEach(function(track) {
					track.stop();
				});
				vid[0].srcObject = null;
			}
		}
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
			//FIXME TODO swf.startRec();
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
		let enabled = false;
		const constraints = {};
		//TODO add check if constraint is supported
		if (s.video.cam > -1) {
			constraints.video = {
				width: o.width
				, height: o.height
				, deviceId: { exact: v.data('device-id')  }
				, frameRate: { max: 30 }
			};
			enabled = true;
		} else {
			constraints.video = false;
		}
		//TODO enable audio for recordings only
		if (s.video.mic > -1) {
			constraints.audio = {
				sampleSize: 22
				, echoCancellation: true
			};
			enabled = true;
		} else {
			constraints.audio = false;
		}
		if (enabled) {
			navigator.mediaDevices.getUserMedia(constraints)
				.then(function(stream) {
					vid[0].srcObject = stream;
				})
				.catch(function(err) {
					_error(err.name + ": " + err.message);
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
		console.error(msg);
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
				return navigator.mediaDevices.enumerateDevices()
					.then(function(devices) {
						return devices;
					})
					.catch(function(err) { throw err; });
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
		recAllowed = false;
		vs.dialog('open');
		_initDevices();
	}
	return {
		init: _init
		, open: _open
		, allowRec: _allowRec
		, allowPlay: _allowPlay
		, micActivity: _micActivity
		, close: function() { vs.dialog('close'); }
		, load: _load
		, save: _save
	};
})();
