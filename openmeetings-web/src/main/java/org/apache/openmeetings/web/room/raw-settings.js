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
var VideoSettings = (function() {
	let vs, lm, swf, s, cam, mic, res, o
		, vidScroll, recBtn, playBtn, recAllowed = false;
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
			, change: function(event, ui) {
				_readValues();
				swf.camChanged(s.video.cam);
			}
		});
		mic = vs.find('select.mic').iconselectmenu({
			appendTo: '.mic-row'
			, change: function(event, ui) {
				_readValues();
				swf.micChanged(s.video.mic);
			}
		});
		res = vs.find('select.cam-resolution').iconselectmenu({
			appendTo: '.res-row'
			, change: function(event, ui) {
				_readValues();
				swf.resChanged(s.video.width, s.video.height);
			}
		});
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
	function _setLoading(el) {
		el.find('option').remove();
		el.append(OmUtil.tmpl('#settings-option-loading'));//!settings-option-disabled
		el.iconselectmenu('refresh');
	}
	function _fillDevices(devices) {
		let cCount = 0, mCount = 0;
		_load();
		cam.find('option').remove();
		cam.append(OmUtil.tmpl('#settings-option-disabled'));
		mic.find('option').remove();
		mic.append(OmUtil.tmpl('#settings-option-disabled'));
		devices.forEach(function(device) {
			if ('audioinput' === device.kind) {
				const o = $('<option></option>').attr('value', mCount).text(device.label)
					.data('device-id', device.deviceId);
				if (mCount === s.video.mic) {
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
		cam.iconselectmenu('refresh');
		mic.iconselectmenu('refresh');
		res.find('option').each(function() {
			const o = $(this).data();
			if (o.width === s.video.width && o.height === s.video.height) {
				$(this).prop('selected', true);
				return false;
			}
		});
		res.iconselectmenu('refresh');
		_readValues();
		swf.init(s.video.cam, s.video.mic
				, o.interview ? 320 : s.video.width, o.interview ? 260 : s.video.height);
	}
	function _initDevices() {
		if (!navigator.mediaDevices || !navigator.mediaDevices.enumerateDevices) {
			_fillDevices(swf.getDevices());
		} else {
			navigator.mediaDevices.getUserMedia({video: true, audio: true})
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
					_fillDevices(devices);
				})
				.catch(function(err) {
					_fillDevices(swf.getDevices());
				});
		}
	}
	function _initSwf() {
		_initDevices();
		_readValues();
	}
	function _open() {
		_setLoading(cam);
		_setLoading(mic);
		recAllowed = false;
		vs.dialog('open');
		_initSwf();
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
