/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function initVideo(el, id, options) {
	var type = 'application/x-shockwave-flash';
	var src = 'public/main.swf?cache' + new Date().getTime();
	var o = $('<object>').attr('id', id).attr('type', type).attr('data', src).attr('width', options.width).attr('height', options.height);
	o.append($('<param>').attr('name', 'quality').attr('value', 'best'))
		.append($('<param>').attr('name', 'wmode').attr('value', options.wmode))
		.append($('<param>').attr('name', 'allowscriptaccess').attr('value', 'sameDomain'))
		.append($('<param>').attr('name', 'allowfullscreen').attr('value', 'false'))
		.append($('<param>').attr('name', 'flashvars').attr('value', $.param(options)));
	el.append(o);
	return o;
}
var VideoSettings = (function() {
	var self = {}, vs, lm, swf, s, cam, mic, res, o
		, vidScroll, recBtn, playBtn, inited = false, recAllowed = false;
	function _load() {
		s = {};
		try {
			s = JSON.parse(localStorage.getItem('openmeetings')) || s;
		} catch (e) {}
		if (!s.video) {
			s.video = {};
		}
		return s;
	}
	function _save(refr) {
		var _s = JSON.stringify(s);
		localStorage.setItem('openmeetings', _s);
		if (typeof avSettings === 'function') {
			avSettings(_s);
		}
		if (refr && typeof VideoManager !== 'undefined' && o.uid) {
			VideoManager.refresh(o.uid, s.video);
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
						primary: "ui-icon-disk"
					}
					, click: function() {
						_save(true);
						vs.dialog("close");
					}
				}
				, {
					text: vs.data('btn-cancel')
					, click: function() {
						vs.dialog("close");
					}
				}
			]
		});
		lm.progressbar({ value: 0 });
		o.width = 300;
		o.height = 200;
		o.mode = 'settings';
		o.rights = o.rights.join();
		delete o.keycode;
		swf = initVideo(vidScroll, 'video-settings-swf', o)[0];
		vs.find('input, button').prop('disabled', true);
		vs.find('button').button();
		var rr = vs.find('.cam-resolution').parent('.sett-row');
		if (!!o.interview) {
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
		var o = res.find('option:selected').data();
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
		console.log("activity: ", level)
		lm.progressbar("value", Math.max(0, level));
	}
	function _initSwf() {
		if (!inited) {
			var obj = swf.getDevices();
			cam.find('option[value!="-1"]').remove();
			for (var i = 0; i < obj.cams.length; ++i) {
				var o = $('<option></option>').attr('value', i).text(obj.cams[i]);
				if (i == s.video.cam) {
					o.prop('selected', true);
				}
				cam.append(o);
			}
			cam.prop('disabled', false).change(function() {
				_readValues();
				swf.camChanged(s.video.cam);
			});
			mic.find('option[value!="-1"]').remove();
			for (var i = 0; i < obj.mics.length; ++i) {
				var o = $('<option></option>').attr('value', i).text(obj.mics[i]);
				if (i == s.video.mic) {
					o.prop('selected', true);
				}
				mic.append(o);
			}
			mic.prop('disabled', false).change(function() {
				_readValues();
				swf.micChanged(s.video.mic);
			});
			res.change(function() {
				_readValues();
				swf.resChanged(s.video.width, s.video.height);
			});
			res.find('option').each(function(idx) {
				var o = $(this).data();
				if (o.width == s.video.width && o.height == s.video.height) {
					$(this).prop('selected', true);
					return false;
				}
			});
		}
		_readValues();
		swf.init(s.video.cam, s.video.mic, s.video.width, s.video.height);
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
