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

function setRoomSizes() {
	var sb = $(".room.sidebar.left")
		, w = $(window).width() - sb.width() - 5
		, h = $(window).height() - $('#menu').height()
		, p = sb.find('.tabs');
	sb.height(h);
	var hh = h - 5;
	p.height(hh);
	$(".user.list", p).height(hh - $("ul", p).height() - $(".user.header", p).height() - 5);
	if (!!WbArea) {
		WbArea.resize(sb.width(), w, h);
	}
}
function roomReload(event, ui) {
	window.location.reload();
}
function roomClosed(jqEvent, msg) {
	roomUnload();
	$(".room.holder").remove();
	$("#chatPanel").remove();
	var dlg = $('#disconnected-dlg');
	dlg.dialog({
		modal: true
		, close: roomReload
		, buttons: [
			{
				text: dlg.data('reload')
				, icons: {primary: "ui-icon-refresh"}
				, click: function() {
					$(this).dialog("close");
				}
			}
		]
	});
}
function roomLoad() {
	$(".room.sidebar.left").ready(function() {
		setRoomSizes();
	});
	$(window).on('resize.openmeetings', function() {
		setRoomSizes();
	});
	$(".room.sidebar.left").resizable({
		handles: "e"
		, stop: function(event, ui) {
			setRoomSizes();
		}
	});
	Wicket.Event.subscribe("/websocket/closed", roomClosed);
}
function roomUnload() {
	$(window).off('resize.openmeetings');
	Wicket.Event.unsubscribe("/websocket/closed", roomClosed);
	if (!!WbArea) {
		WbArea.destroy();
	}
	VideoSettings.close();
}
function startPrivateChat(el) {
	Chat.addTab('chatTab-u' + el.parent().parent().data("userid"), el.parent().parent().find('.user.name').text());
	Chat.open();
	$('#chatMessage .wysiwyg-editor').click();
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
		localStorage.setItem('openmeetings', JSON.stringify(s));
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

/***** functions required by SIP   ******/
function sipBtnClick() {
	var txt = $('.sip-number');
	txt.val(txt.val() + $(this).data('value'));
}
function sipBtnEraseClick() {
	var txt = $('.sip-number');
	var t = txt.val();
	if (!!t) {
		txt.val(t.substring(0, t.length -1));
	}
}
function sipGetKey(evt) {
	var k = -1;
	if (evt.keyCode > 47 && evt.keyCode < 58) {
		k = evt.keyCode - 48;
	}
	if (evt.keyCode > 95 && evt.keyCode < 106) {
		k = evt.keyCode - 96;
	}
	return k;
}
function sipKeyDown(evt) {
	var k = sipGetKey(evt);
	if (k > 0) {
		$('#sip-dialer-btn-' + k).addClass('ui-state-active');
	}
}
function sipKeyUp(evt) {
	var k = sipGetKey(evt);
	if (k > 0) {
		$('#sip-dialer-btn-' + k).removeClass('ui-state-active');
	}
}

/***** functions required by SWF   ******/
function audioActivity(uid, active) {
	var u = $('#user' + uid + ' .audio-activity.ui-icon');
	if (active) {
		u.addClass("speaking");
	} else {
		u.removeClass("speaking");
	}
}
function typingActivity(uid, active) {
	var u = $('#user' + uid + ' .typing-activity.ui-icon');
	if (active) {
		u.addClass("typing");
	} else {
		u.removeClass("typing");
	}
}
