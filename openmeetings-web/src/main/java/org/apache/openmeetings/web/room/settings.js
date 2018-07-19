/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var VideoSettings = (function() {
	let vs, lm, swf, s, cam, mic, res, o
		, vidScroll, recBtn, playBtn, recAllowed = false;
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
		const ms = _ms;
		if (ms !== null && 'function' === typeof(ms.getAudioTracks)) {
			ms.getAudioTracks().forEach(function(track) {
				track.stop();
			});
			ms.getVideoTracks().forEach(function(track) {
				track.stop();
			});
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
		recBtn = vs.find('.rec-start').click(function() {
			recBtn.prop('disabled', true).button('refresh'); //TODO disable drop-downs
			swf.startRec();
		});
		playBtn = vs.find('.play').click(function() {
			swf.play();
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
						primary: 'ui-icon-disk'
					}
					, click: function() {
						_save(true);
						vs.dialog('close');
					}
				}
				, {
					text: vs.data('btn-cancel')
					, click: function() {
						vs.dialog('close');
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
		swf = initSwf(vidScroll, 'main.swf', 'video-settings-swf', o)[0];
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
		s.video.cam = 1 * cam.val();
		s.video.mic = 1 * mic.val();
		const o = res.find('option:selected').data();
		s.video.width = o.width;
		s.video.height = o.height;
		$(swf).attr('width', Math.max(300, s.video.width)).attr('height', Math.max(200, s.video.height));
		vidScroll.scrollLeft(Math.max(0, s.video.width / 2 - 150))
			.scrollTop(Math.max(0, s.video.height / 2 - 110));
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
		lm.progressbar('value', Math.max(0, level));
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
				cam.prop('disabled', false).off().change(function() {
					_readValues();
					swf.camChanged(s.video.cam);
				});
				mic.prop('disabled', false).off().change(function() {
					_readValues();
					swf.micChanged(s.video.mic);
				});
				res.off().change(function() {
					_readValues();
					swf.resChanged(s.video.width, s.video.height);
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
	function _initSwf() {
		_initDevices();
		_readValues();
		swf.init(s.video.cam, s.video.mic
			, o.interview ? 320 : s.video.width, o.interview ? 260 : s.video.height);
	}
	function _open() {
		recAllowed = false;
		vs.dialog('open');
	}
	return {
		init: _init
		, initSwf: _initSwf
		, open: _open
		, allowRec: _allowRec
		, allowPlay: _allowPlay
		, micActivity: _micActivity
		, close: function() { vs.dialog('close'); }
		, load: _load
		, save: _save
	};
})();
