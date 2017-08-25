/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
	var self = {}, vs, lm, swf, s, cam, mic, res,
		vidScroll, recBtn, playBtn, inited = false, recAllowed = false;
	function _load() {
		s = {};
		try {
			s = JSON.parse(localStorage.getItem('openmeetings')) || s;
		} catch (e) {}
		if (!s.video) {
			s.video = {};
		}
	}
	function _save() {
		var _s = JSON.stringify(s);
		localStorage.setItem('openmeetings', _s);
		if (typeof avSettings === 'function') {
			avSettings(_s);
		}
	}
	function _init(options) {
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
						_save();
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
		options.width = 300;
		options.height = 200;
		options.mode = 'settings';
		swf = initVideo(vidScroll, 'video-settings-swf', options)[0];
		vs.find('input, button').prop('disabled', true);
		vs.find('button').button();
		var rr = vs.find('.cam-resolution').parent('.sett-row');
		if (!!options.interview) {
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
	};
})();
