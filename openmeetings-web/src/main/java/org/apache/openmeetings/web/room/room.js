/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WBA_SEL = '.room.wb.area .ui-tabs-panel.ui-corner-bottom.ui-widget-content:visible';
const VID_SEL = '.video.user-video';
var VideoUtil = (function() {
	var self = {};
	function _getVid(uid) {
		return "video" + uid;
	}
	function _isSharing(c) {
		return 'sharing' === c.type && c.screenActivities.indexOf('sharing') > -1;
	}
	function _isRecording(c) {
		return 'sharing' === c.type
			&& c.screenActivities.indexOf('recording') > -1
			&& c.screenActivities.indexOf('sharing') < 0;
	}
	function _hasAudio(c) {
		return c.activities.indexOf('broadcastA') > -1;
	}
	function _hasVideo(c) {
		return c.activities.indexOf('broadcastV') > -1;
	}
	function _getRects(sel, excl) {
		var list = [];
		var elems = $(sel);
		for (let i = 0; i < elems.length; ++i) {
			if (excl !== $(elems[i]).attr('aria-describedby')) {
				list.push(_getRect(elems[i]));
			}
		}
		return list;
	}
	function _getRect(e) {
		let win = $(e), winoff = win.offset();
		return {left: winoff.left
			, top: winoff.top
			, right: winoff.left + win.width()
			, bottom: winoff.top + win.height()};
	}
	function _getPos(list, w, h) {
		if (Room.getOptions().interview) {
			return {left: 0, top: 0};
		}
		var wba = $(WBA_SEL);
		var woffset = wba.offset();
		const offsetX = 20, offsetY = 10
			, area = {left: woffset.left, top: woffset.top, right: woffset.left + wba.width(), bottom: woffset.top + wba.height()};
		var rectNew = {
				_left: area.left
				, _top: area.top
				, right: area.left + w
				, bottom: area.top + h
				, get left() {
					return this._left
				}
				, set left(l) {
					this._left = l;
					this.right = l + w;
				}
				, get top() {
					return this._top
				}
				, set top(t) {
					this._top = t;
					this.bottom = t + h;
				}
			};
		//console.log("Area " + JSON.stringify(area));
		do {
			let minY = area.bottom;
			var posFound = true;
			//console.log("Checking RECT " + JSON.stringify(rectNew));
			for (let i = 0; i < list.length; ++i) {
				let rect = list[i];
				minY = Math.min(minY, rect.bottom);

				if (rectNew.left < rect.right && rectNew.right > rect.left && rectNew.top < rect.bottom && rectNew.bottom > rect.top) {
					rectNew.left = rect.right + offsetX;
					//console.log("Intersecting with " + JSON.stringify(rect) + ", new RECT " + JSON.stringify(rectNew));
					posFound = false;
				}
				if (rectNew.right >= area.right) {
					rectNew.left = area.left;
					rectNew.top = minY + offsetY;
					//console.log("End of the row, new RECT " + JSON.stringify(rectNew));
					posFound = false;
				}
				if (rectNew.bottom >= area.bottom) {
					rectNew.top = area.top;
					//console.log("Bottom of the area, new RECT " + JSON.stringify(rectNew));
					posFound = true;
					break;
				}
			}
		} while (!posFound);
		return {left: rectNew.left, top: rectNew.top};
	}
	function _arrange() {
		let list = [], elems = $(VID_SEL);
		for (let i = 0; i < elems.length; ++i) {
			let v = $(elems[i]);
			v.css(_getPos(list, v.width(), v.height()));
			list.push(_getRect(v));
		}
	}

	self.getVid = _getVid;
	self.isSharing = _isSharing;
	self.isRecording = _isRecording;
	self.hasAudio = _hasAudio;
	self.hasVideo = _hasVideo;
	self.getRects = _getRects;
	self.getPos = _getPos;
	self.arrange = _arrange;
	return self;
})();
var Video = (function() {
	var self = {}, c, v, vc, t, f, swf, size, vol, slider, handle
		, lastVolume = 50;

	function _getName() {
		return c.user.firstName + ' ' + c.user.lastName;
	}
	function _resizeDlg(_w, _h) {
		let h = _h + t.height() + 2 + (f.is(":visible") ? f.height() : 0);
		v.dialog("option", "width", _w).dialog("option", "height", h);
		_resize(_w, _h);
		return h;
	}
	function _securityMode(on) {
		if (on) {
			//TODO buttons
			v.dialog("option", "position", {my: "center", at: "center", of: WBA_SEL});
		} else {
			let h = _resizeDlg(size.width, size.height);
			v.dialog("widget").css(VideoUtil.getPos(VideoUtil.getRects(VID_SEL, VideoUtil.getVid(c.uid)), c.width, h));
		}
	}
	function _resize(w, h) {
		vc.width(w).height(h);
		swf.attr('width', w).attr('height', h);
	}
	function _handleMicStatus(state) {
		if (!f.is(":visible")) {
			return;
		}
		if (state) {
			f.find('.off').hide();
			f.find('.on').show();
			f.addClass('ui-state-highlight');
			t.addClass('ui-state-highlight');
		} else {
			f.find('.off').show();
			f.find('.on').hide();
			f.removeClass('ui-state-highlight');
			t.removeClass('ui-state-highlight');
		}
	}
	function _handleVolume(val) {
		handle.text(val);
		var ico = vol.find('.ui-icon');
		if (val > 0 && ico.hasClass('ui-icon-volume-off')) {
			ico.toggleClass('ui-icon-volume-off ui-icon-volume-on');
			vol.removeClass('ui-state-error');
			_handleMicStatus(true);
		} else if (val === 0 && ico.hasClass('ui-icon-volume-on')) {
			ico.toggleClass('ui-icon-volume-on ui-icon-volume-off');
			vol.addClass('ui-state-error');
			_handleMicStatus(false);
		}
		if (swf[0].setVolume !== undefined) {
			swf[0].setVolume(val);
		}
	}
	function _mute(mute) {
		if (!slider) {
			return;
		}
		if (mute) {
			let val = slider.slider("option", "value");
			if (val > 0) {
				lastVolume = val;
			}
			slider.slider("option", "value", 0);
			_handleVolume(0);
		} else {
			slider.slider("option", "value", lastVolume);
			_handleVolume(lastVolume);
		}
	}
	function _init(_c, _pos) {
		c = _c;
		pos = _pos;
		size = {width: c.width, height: c.height};
		var _id = VideoUtil.getVid(c.uid)
			, name = _getName()
			, _w = c.self ? Math.max(300, c.width) : c.width
			, _h = c.self ? Math.max(200, c.height) : c.height
			, opts = Room.getOptions();
		{ //scope
			let cont = opts.interview ? $('.pod.pod-' + c.pod) : $('.room.box');
			cont.append($('#user-video').clone().attr('id', _id).attr('title', name)
					.attr('data-client-uid', c.type + c.cuid).data(self));
		}
		v = $('#' + _id);
		v.dialog({
			classes: {
				'ui-dialog': 'ui-corner-all video user-video' + (opts.showMicStatus ? ' mic-status' : '')
				, 'ui-dialog-titlebar': 'ui-corner-all' + (opts.showMicStatus ? ' ui-state-highlight' : '')
			}
			, width: _w
			, minWidth: 40
			, minHeight: 50
			, autoOpen: true
			, appendTo: opts.interview ? '.pod.pod-' + c.pod : '.room.box'
			, draggable: !opts.interview
			, resizable: !opts.interview
			, modal: false
			, resizeStop: function(event, ui) {
				var w = ui.size.width - 2
					, h = ui.size.height - t.height() - 4 - (f.is(":visible") ? f.height() : 0);
				_resize(w, h);
				swf[0].vidResize(w, h);
			}
			, close: function() {
				VideoManager.close(c.uid, true);
			}
		}).dialogExtend({
			icons: {
				'collapse': 'ui-icon-minus'
			}
			, closable: VideoUtil.isSharing(c)
			, collapsable: true
			, dblclick: "collapse"
		});
		t = v.parent().find('.ui-dialog-titlebar').attr('title', name);
		f = v.find('.footer');
		if (!VideoUtil.isSharing(c)) {
			v.parent().find('.ui-dialog-titlebar-buttonpane')
				.append($('#video-volume-btn').children().clone())
				.append($('#video-refresh-btn').children().clone());
			var volume = v.parent().find('.dropdown-menu.video.volume');
			slider = v.parent().find('.slider');
			if (opts.interview) {
				v.parent().find('.ui-dialog-titlebar-collapse').hide();
			}
			vol = v.parent().find('.ui-dialog-titlebar-volume')
				.on('mouseenter', function(e) {
					e.stopImmediatePropagation();
					volume.toggle();
				})
				.click(function(e) {
					e.stopImmediatePropagation();
					let muted = $(this).find('.ui-icon').hasClass('ui-icon-volume-off');
					roomAction('mute', JSON.stringify({uid: c.cuid, mute: !muted}));
					_mute(!muted);
					volume.hide();
					return false;
				}).dblclick(function(e) {
					e.stopImmediatePropagation();
					return false;
				});
			v.parent().find('.ui-dialog-titlebar-refresh')
				.click(function(e) {
					e.stopImmediatePropagation();
					_refresh();
					return false;
				}).dblclick(function(e) {
					e.stopImmediatePropagation();
					return false;
				});
			volume.on('mouseleave', function() {
				$(this).hide();
			});
			handle = v.parent().find('.slider .handle');
			slider.slider({
				orientation: 'vertical'
				, range: 'min'
				, min: 0
				, max: 100
				, value: lastVolume
				, create: function() {
					handle.text($(this).slider("value"));
				}
				, slide: function(event, ui) {
					_handleVolume(ui.value);
				}
			});
			let hasAudio = VideoUtil.hasAudio(c);
			_handleMicStatus(hasAudio);
			if (!hasAudio) {
				vol.hide();
			}
		}
		vc = v.find('.video');
		vc.width(_w).height(_h);
		//broadcast
		var o = Room.getOptions();
		if (c.self) {
			o.cam = c.cam;
			o.mic = c.mic;
			o.mode = 'broadcast';
		} else {
			o.mode = 'play';
		}
		o.av = c.activities.join();
		o.rights = o.rights.join();
		o.width = c.width;
		o.height = c.height;
		o.sid = c.sid;
		o.uid = c.uid;
		o.cuid = c.cuid;
		o.userId = c.user.id;
		o.broadcastId = c.broadcastId;
		o.type = c.type;
		delete o.keycode;
		swf = initVideo(vc, _id + '-swf', o);
		swf.attr('width', _w).attr('height', _h);
		v.dialog("widget").css(_pos);
	}
	function _update(_c) {
		var opts = Room.getOptions();
		c.screenActivities = _c.screenActivities;
		c.activities = _c.activities;
		let hasAudio = VideoUtil.hasAudio(c);
		_handleMicStatus(hasAudio);
		if (hasAudio) {
			vol.show();
		} else {
			vol.hide();
			v.parent().find('.dropdown-menu.video.volume').hide();
		}
		if (opts.interview && c.pod !== _c.pod) {
			c.pod = _c.pod;
			v.dialog('option', 'appendTo', '.pod.pod-' + c.pod);
		}
		if (swf[0].update !== undefined) {
			c.self ? swf[0].update() : swf[0].update(c);
		}
	}
	function _refresh(_opts) {
		if (swf[0].refresh !== undefined) {
			let opts = _opts || {};
			if (!isNaN(opts.width)) {
				_resizeDlg(opts.width, opts.height);
			}
			swf[0].refresh(opts);
		}
	}
	function _setRights(_r) {
		if (swf[0].setRights !== undefined) {
			swf[0].setRights(_r);
		}
	}

	self.update = _update;
	self.refresh = _refresh;
	self.mute = _mute;
	self.isMuted = function() { return 0 === slider.slider("option", "value"); };
	self.init = _init;
	self.securityMode = _securityMode;
	self.client = function() { return c; };
	self.setRights = _setRights;
	return self;
});
var VideoManager = (function() {
	var self = {}, share, inited = false;

	function _init() {
		VideoSettings.init(Room.getOptions());
		share = $('.room.box').find('.icon.shared.ui-button');
		inited = true;
	}
	function _update(c) {
		if (!inited) {
			return;
		}
		for (let i = 0; i < c.streams.length; ++i) {
			let cl = JSON.parse(JSON.stringify(c)), s = c.streams[i];
			delete cl.streams;
			$.extend(cl, s);
			if (cl.self && VideoUtil.isSharing(cl) || VideoUtil.isRecording(cl)) {
				continue;
			}
			let _id = VideoUtil.getVid(cl.uid)
				, av = VideoUtil.hasAudio(cl) || VideoUtil.hasVideo(cl)
				, v = $('#' + _id);
			if (av && v.length !== 1 && !!cl.self) {
				Video().init(cl, VideoUtil.getPos(VideoUtil.getRects(VID_SEL), cl.width, cl.height + 25));
			} else if (av && v.length === 1) {
				v.data().update(cl);
			} else if (!av && v.length === 1) {
				_closeV(v);
			}
		}
		if (c.uid === Room.getOptions().uid) {
			Room.setRights(c.rights);
			var windows = $(VID_SEL + ' .ui-dialog-content');
			for (var i = 0; i < windows.length; ++i) {
				let w = $(windows[i]);
				w.data().setRights(c.rights);
			}

		}
		if (c.streams.length === 0) {
			// check for non inited video window
			let v = $('#' + VideoUtil.getVid(c.uid));
			if (v.length === 1) {
				_closeV(v);
			}
		}
	}
	function _closeV(v) {
		if (v.dialog('instance') !== undefined) {
			v.dialog('destroy');
		}
		v.remove();
	}
	function _play(c) {
		if (!inited) {
			return;
		}
		if (VideoUtil.isSharing(c)) {
			_highlight(share
					.attr('title', share.data('user') + ' ' + c.user.firstName + ' ' + c.user.lastName + ' ' + share.data('text'))
					.data('uid', c.uid)
					.show(), 10);
			share.tooltip().off('click').click(function() {
				var v = $('#' + VideoUtil.getVid(c.uid))
				if (v.length !== 1) {
					Video().init(c, $(WBA_SEL).offset());
				} else {
					v.dialog('open');
				}
			});
		} else if ('sharing' !== c.type) {
			Video().init(c, VideoUtil.getPos(VideoUtil.getRects(VID_SEL), c.width, c.height + 25));
		}
	}
	function _close(uid, showShareBtn) {
		var _id = VideoUtil.getVid(uid), v = $('#' + _id);
		if (v.length === 1) {
			_closeV(v);
		}
		if (!showShareBtn && uid === share.data('uid')) {
			share.off('click').hide();
		}
	}
	function _highlight(el, count) {
		if (count < 0) {
			return;
		}
		el.addClass('ui-state-highlight', 2000, function() {
			el.removeClass('ui-state-highlight', 2000, function() {
				_highlight(el, --count);
			});
		});
	}
	function _find(uid) {
		return $(VID_SEL + ' div[data-client-uid="room' + uid + '"]');
	}
	function _micActivity(uid, active) {
		var u = $('#user' + uid + ' .audio-activity.ui-icon');
		var v = _find(uid).parent();
		if (active) {
			u.addClass("speaking");
			v.addClass('user-speaks')
		} else {
			u.removeClass("speaking");
			v.removeClass('user-speaks')
		}
	}
	function _refresh(uid, opts) {
		var v = _find(uid);
		if (v.length > 0) {
			v.data().refresh(opts);
		}
	}
	function _mute(uid, mute) {
		var v = _find(uid);
		if (v.length > 0) {
			v.data().mute(mute);
		}
	}
	function _clickExclusive(uid) {
		let s = VideoSettings.load();
		if (false !== s.video.confirmExclusive) {
			let dlg = $('#exclusive-confirm');
			dlg.dialog({
				buttons: [
					{
						text: dlg.data('btn-ok')
						, click: function() {
							s.video.confirmExclusive = !$('#exclusive-confirm-dont-show').prop('checked');
							VideoSettings.save();
							roomAction('exclusive', uid);
							$(this).dialog('close');
						}
					}
					, {
						text: dlg.data('btn-cancel')
						, click: function() {
							$(this).dialog('close');
						}
					}
				]
			})
		}
	}
	function _exclusive(uid) {
		var windows = $(VID_SEL + ' .ui-dialog-content');
		for (var i = 0; i < windows.length; ++i) {
			let w = $(windows[i]);
			w.data().mute('room' + uid !== w.data('client-uid'));
		}
	}

	self.init = _init;
	self.update = _update;
	self.play = _play;
	self.close = _close;
	self.securityMode = function(uid, on) { $('#' + VideoUtil.getVid(uid)).data().securityMode(on); };
	self.micActivity = _micActivity;
	self.refresh = _refresh;
	self.mute = _mute;
	self.clickExclusive = _clickExclusive;
	self.exclusive = _exclusive;
	return self;
})();
var Room = (function() {
	var self = {}, options;

	function _init(_options) {
		options = _options;
		VideoManager.init();
	}
	function _getSelfAudioClient() {
		let vw = $('#video' + Room.getOptions().uid);
		if (vw.length > 0) {
			let v = vw.data();
			if (VideoUtil.hasAudio(v.client())) {
				return v;
			}
		}
		return null;
	}
	function _keyHandler(e) {
		if (e.shiftKey) {
			switch (e.which) {
				case options.keycode.arrange: // Shift+F8 by default
					VideoUtil.arrange();
					break;
				case options.keycode.exclusive: // Shift+F12 by default
				{
					let v = _getSelfAudioClient();
					if (v !== null) {
						VideoManager.clickExclusive(Room.getOptions().uid);
					}
				}
					break;
				case options.keycode.mute: // Shift+F7 by default
				{
					let v = _getSelfAudioClient();
					if (v !== null) {
						v.mute(!v.isMuted());
					}
				}
					break;
			}
		}
	}
	function _setSize() {
		var sb = $(".room.sidebar.left")
			, w = $(window).width() - sb.width() - 8
			, h = $(window).height() - $('#menu').height() - 3
			, p = sb.find('.tabs');
		sb.height(h);
		var hh = h - 5;
		p.height(hh);
		$(".user.list", p).height(hh - $("ul", p).height() - $(".user.header", p).height() - 5);
		var holder = $('.room.holder');
		if (sb.width() > 230) {
			holder.addClass('big').removeClass('small');
		} else {
			holder.removeClass('big').addClass('small');
		}
		if (typeof WbArea !== 'undefined') {
			WbArea.resize(sb.width() + 5, w, h);
		}
	}
	function _reload() {
		if (!!options.reloadUrl) {
			window.location.href = options.reloadUrl;
		} else {
			window.location.reload();
		}
	}
	function _close() {
		_unload();
		$(".room.holder").remove();
		$("#chatPanel").remove();
		var dlg = $('#disconnected-dlg');
		dlg.dialog({
			modal: true
			, close: _reload
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
	function _load() {
		$(".room.sidebar.left").ready(function() {
			_setSize();
		});
		$(window).on('resize.openmeetings', function() {
			_setSize();
		});
		$(".room.sidebar.left").resizable({
			handles: "e"
			, stop: function() {
				_setSize();
			}
		});
		Wicket.Event.subscribe("/websocket/closed", _close);
		Wicket.Event.subscribe("/websocket/error", _close);
		$(window).keyup(_keyHandler);
	}
	function _unload() {
		$(window).off('resize.openmeetings');
		Wicket.Event.unsubscribe("/websocket/closed", _close);
		Wicket.Event.unsubscribe("/websocket/error", _close);
		if (!!WbArea) {
			WbArea.destroy();
		}
		if (typeof VideoSettings !== 'undefined') {
			VideoSettings.close();
		}
		$('.ui-dialog.user-video').remove();
		$(window).off('keyup', _keyHandler);
	}
	function _showClipboard(txt) {
		let dlg = $('#clipboard-dialog');
		dlg.find('p .text').text(txt);
		dlg.dialog({
			resizable: false
			, height: "auto"
			, width: 400
			, modal: true
			, buttons: [
				{
					text: dlg.data('btn-ok')
					, click: function() {
						$(this).dialog('close');
					}
				}
			]
		});
	}

	self.init = _init;
	self.getOptions = function() { return JSON.parse(JSON.stringify(options)); };
	self.setRights = function(_r) { return options.rights = _r; };
	self.setSize = _setSize;
	self.keyHandler = _keyHandler;
	self.load = _load;
	self.unload = _unload;
	self.showClipboard = _showClipboard;
	return self;
})();
function startPrivateChat(el) {
	Chat.addTab('chatTab-u' + el.parent().parent().data("userid"), el.parent().parent().find('.user.name').text());
	Chat.open();
	$('#chatMessage .wysiwyg-editor').click();
}
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
function typingActivity(uid, active) {
	var u = $('#user' + uid + ' .typing-activity.ui-icon');
	if (active) {
		u.addClass("typing");
	} else {
		u.removeClass("typing");
	}
}
