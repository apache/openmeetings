/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WB_AREA_SEL = '.room.wb.area';
const WBA_WB_SEL = '.room.wb.area .ui-tabs-panel.ui-corner-bottom.ui-widget-content:visible';
var WBA_SEL = WB_AREA_SEL;
const VID_SEL = '.video.user-video';
var RoomUtil = (function() {
	const self = {};
	function _confirmDlg(_id, okHandler) {
		const confirm = $('#' + _id);
		confirm.dialog({
			modal: true
			, buttons: [
				{
					text: confirm.data('btn-ok')
					, click: function() {
						okHandler();
						$(this).dialog('close');
					}
				}
				, {
					text: confirm.data('btn-cancel')
					, click: function() {
						$(this).dialog('close');
					}
				}
			]
		});
		return confirm;
	}

	self.confirmDlg = _confirmDlg;
	return self;
})();
var VideoUtil = (function() {
	const self = {};
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
		const list = [], elems = $(sel);
		for (let i = 0; i < elems.length; ++i) {
			if (excl !== $(elems[i]).attr('aria-describedby')) {
				list.push(_getRect(elems[i]));
			}
		}
		return list;
	}
	function _getRect(e) {
		const win = $(e), winoff = win.offset();
		return {left: winoff.left
			, top: winoff.top
			, right: winoff.left + win.width()
			, bottom: winoff.top + win.height()};
	}
	function _getPos(list, w, h) {
		if (Room.getOptions().interview) {
			return {left: 0, top: 0};
		}
		const wba = $(WBA_SEL), woffset = wba.offset()
			, offsetX = 20, offsetY = 10
			, area = {left: woffset.left, top: woffset.top, right: woffset.left + wba.width(), bottom: woffset.top + wba.height()};
		const rectNew = {
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
		let minY = area.bottom, posFound;
		do {
			posFound = true
			for (let i = 0; i < list.length; ++i) {
				const rect = list[i];
				minY = Math.min(minY, rect.bottom);

				if (rectNew.left < rect.right && rectNew.right > rect.left && rectNew.top < rect.bottom && rectNew.bottom > rect.top) {
					rectNew.left = rect.right + offsetX;
					posFound = false;
				}
				if (rectNew.right >= area.right) {
					rectNew.left = area.left;
					rectNew.top = minY + offsetY;
					posFound = false;
				}
				if (rectNew.bottom >= area.bottom) {
					rectNew.top = area.top;
					posFound = true;
					break;
				}
			}
		} while (!posFound);
		return {left: rectNew.left, top: rectNew.top};
	}
	function _arrange() {
		const list = [], elems = $(VID_SEL);
		for (let i = 0; i < elems.length; ++i) {
			const v = $(elems[i]);
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
	const self = {};
	let c, v, vc, t, f, swf, size, vol, slider, handle
		, lastVolume = 50;

	function _getName() {
		return c.user.firstName + ' ' + c.user.lastName;
	}
	function _resizeDlg(_ww, _hh) {
		const interview = Room.getOptions().interview;
		const _w = interview ? 320 : _ww, _h = interview ? 260 : _hh;
		const h = _h + t.height() + 2 + (f.is(":visible") ? f.height() : 0);
		v.dialog("option", "width", _w).dialog("option", "height", h);
		_resize(_w, _h);
		return h;
	}
	function _securityMode(on) {
		if (Room.getOptions().interview) {
			return;
		}
		if (on) {
			//TODO buttons
			v.dialog("option", "position", {my: "center", at: "center", of: WBA_SEL});
		} else {
			const h = _resizeDlg(size.width, size.height);
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
		const ico = vol.find('.ui-icon');
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
			const val = slider.slider("option", "value");
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
		size = {width: c.width, height: c.height};
		const _id = VideoUtil.getVid(c.uid)
			, name = _getName()
			, _w = c.self ? Math.max(300, c.width) : c.width
			, _h = c.self ? Math.max(200, c.height) : c.height
			, opts = Room.getOptions();
		{ //scope
			const cont = opts.interview ? $('.pod.pod-' + c.pod) : $('.room.box');
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
				const w = ui.size.width - 2
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
			const volume = v.parent().find('.dropdown-menu.video.volume');
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
					const muted = $(this).find('.ui-icon').hasClass('ui-icon-volume-off');
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
			const hasAudio = VideoUtil.hasAudio(c);
			_handleMicStatus(hasAudio);
			if (!hasAudio) {
				vol.hide();
			}
		}
		vc = v.find('.video');
		vc.width(_w).height(_h);
		//broadcast
		const o = Room.getOptions();
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
		const opts = Room.getOptions();
		c.screenActivities = _c.screenActivities;
		c.activities = _c.activities;
		const hasAudio = VideoUtil.hasAudio(c);
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
			const opts = _opts || {};
			if (!Room.getOptions().interview && !isNaN(opts.width)) {
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
	const self = {};
	let share, inited = false;

	function _init() {
		if ($(WB_AREA_SEL + ' .wb-area .tabs').length > 0) {
			WBA_SEL = WBA_WB_SEL;
		}
		VideoSettings.init(Room.getOptions());
		share = $('.room.box').find('.icon.shared.ui-button');
		inited = true;
	}
	function _update(c) {
		if (!inited) {
			return;
		}
		for (let i = 0; i < c.streams.length; ++i) {
			const cl = JSON.parse(JSON.stringify(c)), s = c.streams[i];
			delete cl.streams;
			$.extend(cl, s);
			if (cl.self && VideoUtil.isSharing(cl) || VideoUtil.isRecording(cl)) {
				continue;
			}
			const _id = VideoUtil.getVid(cl.uid)
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
			const windows = $(VID_SEL + ' .ui-dialog-content');
			for (let i = 0; i < windows.length; ++i) {
				const w = $(windows[i]);
				w.data().setRights(c.rights);
			}

		}
		if (c.streams.length === 0) {
			// check for non inited video window
			const v = $('#' + VideoUtil.getVid(c.uid));
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
				const v = $('#' + VideoUtil.getVid(c.uid))
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
		const _id = VideoUtil.getVid(uid), v = $('#' + _id);
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
		const u = $('#user' + uid + ' .audio-activity.ui-icon')
			, v = _find(uid).parent();
		if (active) {
			u.addClass("speaking");
			v.addClass('user-speaks')
		} else {
			u.removeClass("speaking");
			v.removeClass('user-speaks')
		}
	}
	function _refresh(uid, opts) {
		const v = _find(uid);
		if (v.length > 0) {
			v.data().refresh(opts);
		}
	}
	function _mute(uid, mute) {
		const v = _find(uid);
		if (v.length > 0) {
			v.data().mute(mute);
		}
	}
	function _clickExclusive(uid) {
		const s = VideoSettings.load();
		if (false !== s.video.confirmExclusive) {
			const dlg = $('#exclusive-confirm');
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
		const windows = $(VID_SEL + ' .ui-dialog-content');
		for (let i = 0; i < windows.length; ++i) {
			const w = $(windows[i]);
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
	const self = {}, isRtl = "rtl" === $('html').attr('dir'), sbSide = isRtl ? 'right' : 'left';
	let options, menuHeight, chat, sb, dock;

	function _init(_options) {
		options = _options;
		window.WbArea = options.interview ? InterviewWbArea() : DrawWbArea();
		const menu = $('.room.box .room.menu');
		chat = $('#chatPanel');
		sb = $('.room.sidebar').css(sbSide, '0px');
		dock = sb.find('.btn-dock').button({
			icon: "ui-icon icon-undock"
			, showLabel: false
		}).click(function() {
			const offset = parseInt(sb.css(sbSide));
			if (offset < 0) {
				sb.removeClass('closed');
			}
			dock.button('option', 'disabled', true);
			const props = {};
			props[sbSide] = offset < 0 ? '0px' : (-sb.width() + 45) + 'px';
			sb.animate(props, 1500
				, function() {
					dock.button('option', 'disabled', false)
						.button('option', 'icon', 'ui-icon ' + (offset < 0 ? 'icon-undock' : 'icon-dock'));
					if (offset < 0) {
						dock.attr('title', dock.data('ttl-undock'));
						_sbAddResizable();
					} else {
						dock.attr('title', dock.data('ttl-dock'));
						sb.addClass('closed').resizable('destroy');
					}
					_setSize();
				});
		});
		dock.addClass(isRtl ? 'align-left' : 'align-right').attr('title', dock.data('ttl-undock'))
			.button('option', 'label', dock.data('ttl-undock'))
			.button('refresh');
		menuHeight = menu.length === 0 ? 0 : menu.height();
		VideoManager.init();
		Activities.init();
	}
	function _getSelfAudioClient() {
		const vw = $('#video' + Room.getOptions().uid);
		if (vw.length > 0) {
			const v = vw.data();
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
					const v = _getSelfAudioClient();
					if (v !== null) {
						VideoManager.clickExclusive(Room.getOptions().uid);
					}
				}
					break;
				case options.keycode.mute: // Shift+F7 by default
				{
					const v = _getSelfAudioClient();
					if (v !== null) {
						v.mute(!v.isMuted());
					}
				}
					break;
			}
		}
		if (e.which === 27) {
			$('#wb-rename-menu').hide();
		}
	}
	function _mouseHandler(e) {
		if (e.which === 1) {
			$('#wb-rename-menu').hide();
		}
	}
	function _sbWidth() {
		if (sb === undefined) {
			sb = $('.room.sidebar');
		}
		return sb === undefined ? 0 : sb.width() + parseInt(sb.css(sbSide));
	}
	function _setSize() {
		const sbW = _sbWidth()
			, w = $(window).width() - sbW - 8
			, h = $(window).height() - menuHeight - 3
			, p = sb.find('.tabs')
			, holder = $('.room.holder');
		sb.height(h);
		const hh = h - 5;
		p.height(hh);
		$(".user.list", p).height(hh - $("ul", p).height() - $(".user.header", p).height() - 5);
		if (sbW > 255) {
			holder.addClass('big').removeClass('small');
		} else {
			holder.removeClass('big').addClass('small');
		}
		Chat.setHeight(h);
		if (typeof WbArea !== 'undefined') {
			const chW = chat.width();
			WbArea.resize(sbW + 5, chW + 5, w - chW, h);
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
		const dlg = $('#disconnected-dlg');
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
	function _sbAddResizable() {
		sb.resizable({
			handles: isRtl ? 'w' : 'e'
			, stop: function() {
				_setSize();
			}
		});
	}
	function _load() {
		sb.ready(function() {
			_setSize();
		});
		_sbAddResizable();
		$(window).on('resize.openmeetings', function() {
			_setSize();
		});
		Wicket.Event.subscribe("/websocket/closed", _close);
		Wicket.Event.subscribe("/websocket/error", _close);
		$(window).keyup(_keyHandler);
		$(document).click(_mouseHandler);
	}
	function _unload() {
		$(window).off('resize.openmeetings');
		Wicket.Event.unsubscribe("/websocket/closed", _close);
		Wicket.Event.unsubscribe("/websocket/error", _close);
		if (typeof WbArea !== 'undefined') {
			WbArea.destroy();
			WbArea = undefined;
		}
		if (typeof VideoSettings !== 'undefined') {
			VideoSettings.close();
		}
		$('.ui-dialog.user-video').remove();
		$(window).off('keyup', _keyHandler);
		$(document).off('click', _mouseHandler);
	}
	function _showClipboard(txt) {
		const dlg = $('#clipboard-dialog');
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
	self.getMenuHeight = function() { return menuHeight; };
	self.getOptions = function() { return typeof options === 'object' ? JSON.parse(JSON.stringify(options)) : {}; };
	self.setRights = function(_r) { return options.rights = _r; };
	self.setSize = _setSize;
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
	const txt = $('.sip-number');
	txt.val(txt.val() + $(this).data('value'));
}
function sipBtnEraseClick() {
	const txt = $('.sip-number')
		, t = txt.val();
	if (!!t) {
		txt.val(t.substring(0, t.length -1));
	}
}
function sipGetKey(evt) {
	let k = -1;
	if (evt.keyCode > 47 && evt.keyCode < 58) {
		k = evt.keyCode - 48;
	}
	if (evt.keyCode > 95 && evt.keyCode < 106) {
		k = evt.keyCode - 96;
	}
	return k;
}
function sipKeyDown(evt) {
	const k = sipGetKey(evt);
	if (k > 0) {
		$('#sip-dialer-btn-' + k).addClass('ui-state-active');
	}
}
function sipKeyUp(evt) {
	const k = sipGetKey(evt);
	if (k > 0) {
		$('#sip-dialer-btn-' + k).removeClass('ui-state-active');
	}
}
/***** functions required by SWF   ******/
function typingActivity(uid, active) {
	const u = $('#user' + uid + ' .typing-activity.ui-icon');
	if (active) {
		u.addClass("typing");
	} else {
		u.removeClass("typing");
	}
}
