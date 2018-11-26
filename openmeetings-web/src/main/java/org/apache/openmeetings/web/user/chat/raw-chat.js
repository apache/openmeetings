/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Chat = function() {
	const align = Settings.isRtl ? 'align-right' : 'align-left'
		, alignIco = Settings.isRtl ? 'align-left' : 'align-right'
		, msgIdPrefix = 'chat-msg-id-'
		, closedSize = 20
		, closedSizePx = closedSize + "px"
		, emoticon = new CSSEmoticon()
		, doneTypingInterval = 5000 //time in ms, 5 second for example
		, iconOpen = 'ui-icon-caret-1-n'
		, iconOpenRoom = 'ui-icon-caret-1-' + (Settings.isRtl ? 'e' : 'w')
		, iconClose = 'ui-icon-caret-1-s'
		, iconCloseRoom = 'ui-icon-caret-1-' + (Settings.isRtl ? 'w' : 'e')
		, SEND_ENTER = 'enter', SEND_CTRL = 'ctrl'
		;
	let p, pp, ctrl, icon, tabs, openedHeight = "345px", openedWidth = "300px", allPrefix = "All"
		, roomPrefix = "Room ", typingTimer, audio, roomMode = false, globalWidth = 600
		, editor = $('#chatMessage .wysiwyg-editor'), muted = false, sendOn, DEF_SEND
		, userId;
		;

	try {
		audio = new Audio('./public/chat_message.mp3');
	} catch (e) {
		//not implemented in IE
		audio = {
			play: function() {}
		};
	}
	function _load() {
		const s = Settings.load();
		if (typeof(s.chat) === 'undefined') {
			s.chat = {
				muted: false
				, sendOn: DEF_SEND
			};
		}
		muted = s.chat.muted === true;
		sendOn = s.chat.sendOn === SEND_ENTER ? SEND_ENTER : SEND_CTRL;
		return s;
	}
	function _updateAudioBtn(btn) {
		btn.removeClass('sound' + (muted ? '' : '-mute')).addClass('sound' + (muted ? '-mute' : ''))
				.attr('title', btn.data(muted ? 'sound-enabled' : 'sound-muted'));
	}
	function _updateSendBtn(btn) {
		const ctrl = sendOn === SEND_CTRL;
		if (ctrl) {
			btn.addClass('send-ctrl');
			editor.off('keydown', _sendOnEnter).keydown('Ctrl+return', _sendOnEnter);
		} else {
			btn.removeClass('send-ctrl');
			editor.off('keydown', _sendOnEnter).keydown('return', _sendOnEnter);
		}
		btn.attr('title', btn.data(ctrl ? 'send-ctrl' : 'send-enter'));
	}
	function _sendOnEnter() {
		$('#chat .send').trigger('click');
	}
	function doneTyping () {
		typingTimer = null;
		chatActivity('typing_stop', $('.room.box').data('room-id'));
	}
	function _emtClick() {
		_editorAppend($(this).data('emt'));
	}
	function initToolbar() {
		const emots = emoticon.emoticons;
		const rowSize = 20, emotMenuList = $('#emotMenuList');
		emotMenuList.html('');
		let row;
		for (let i = 0; i < emots.length; ++i) {
			if (i % rowSize === 0) {
				row = $('<tr></tr>');
				emotMenuList.append(row);
			}
			row.append($('<td>').append(
					$('<div>').addClass('emt').html(emoticon.emoticonize(emots[i]))
						.data('emt', emots[i]).click(_emtClick)
				));
		}
		const emtBtn = $('#emoticons');
		emtBtn.html('');
		emtBtn.append(' ' + emoticon.emoticonize(':)') + ' <b class="caret"></b>');
		const a = $('#chat .audio');
		const sbtn = $('#chat .send-btn');
		{ //scope
			_load();
			_updateAudioBtn(a);
			_updateSendBtn(sbtn)
		}
		$('#chat .chat-btn').hover(function(){ $(this).addClass('ui-state-hover') }, function(){ $(this).removeClass('ui-state-hover') });
		a.off().click(function() {
			const s = _load();
			muted = s.chat.muted = !s.chat.muted;
			_updateAudioBtn(a);
			Settings.save(s);
		});
		sbtn.off().click(function() {
			const s = _load();
			sendOn = s.chat.sendOn = s.chat.sendOn !== SEND_CTRL ? SEND_CTRL : SEND_ENTER;
			_updateSendBtn(sbtn);
			Settings.save(s);
		});
		$('#chat #hyperlink').parent().find('button').off().click(function() {
			_insertLink();
		});
		emoticon.animate();
	}
	function isClosed() {
		return p.hasClass('closed');
	}
	function activateTab(id) {
		tabs.tabs("option", "active", tabs.find('a[href="#' + id + '"]').parent().index());
	}
	function isInited() {
		return !!$("#chatTabs").data("ui-tabs");
	}
	function _reinit(opts) {
		userId = opts.userId;
		allPrefix = opts.all;
		roomPrefix = opts.room;
		DEF_SEND = opts.sendOnEnter === true ? SEND_ENTER : SEND_CTRL;
		sendOn = DEF_SEND;
		p = $('#chatPanel');
		clearTimeout(p.data('timeout'));
		pp = $('#chatPanel, #chatPopup');
		ctrl = $('#chatPopup .control.block');
		icon = $('#chatPopup .control.block .ui-icon');
		editor = $('#chatMessage .wysiwyg-editor');
		icon.removeClass(function(index, className) {
			return (className.match (/(^|\s)ui-icon-caret-\S+/g) || []).join(' ');
		});
		initToolbar();
		tabs = $("#chatTabs").tabs({
			activate: function(event, ui) {
				const ct = ui.newPanel[0].id;
				_scrollDown($('#' + ct));
				$('#activeChatTab').val(ct).trigger('change');
			}
		});
		// close icon: removing the tab on click
		tabs.delegate("span.ui-icon-close", "click", function() {
			const panelId = $(this).closest("li").remove().attr("aria-controls");
			$("#" + panelId).remove();
			tabs.tabs("refresh");
		});
		if (roomMode) {
			icon.addClass(isClosed() ? iconOpenRoom : iconCloseRoom);
			p.addClass('room');
			pp.width(closedSize);
			_removeResize();
		} else {
			ctrl.attr('title', '');
			icon.addClass(isClosed() ? iconOpen : iconClose);
			ctrl.height(closedSize).width(globalWidth);
			pp.width(globalWidth).height(closedSize);
			p.removeClass('room opened').addClass('closed')
				.off('mouseenter mouseleave')
				.resizable({
					handles: 'n, ' + (Settings.isRtl ? 'e' : 'w')
					, disabled: isClosed()
					, alsoResize: "#chatPopup,#chatPopup .control.block,#chat .ui-tabs .ui-tabs-panel.messageArea, #chatMessage .wysiwyg-editor"
					, minHeight: 195
					, minWidth: 260
					, stop: function(event, ui) {
						p.css({'top': '', 'left': ''});
						editor.width(p.width() - 30);
						openedHeight = ui.size.height + "px";
						globalWidth = ui.size.width;
						ctrl.width(globalWidth);
					}
				});
		}
		ctrl.off('click').click(Chat.toggle);
		$('#chatMessage').off().on('input propertychange paste', function () {
			const room = $('.room.box');
			if (room.length) {
				if (!!typingTimer) {
					clearTimeout(typingTimer);
				} else {
					chatActivity('typing_start', room.data('room-id'));
				}
				typingTimer = setTimeout(doneTyping, doneTypingInterval);
			}
		});
	}
	function _removeTab(id) {
		$('#chat li[aria-controls="' + id + '"]').remove();
		$('#' + id).remove();
		if (isInited()) {
			tabs.tabs("refresh");
		}
	}
	function _addTab(id, label) {
		if (!isInited()) {
			_reinit({});
		}
		if ($('#chat').length < 1 || $('#' + id).length) {
			return;
		}
		if (!label) {
			label = id === "chatTab-all" ? allPrefix : roomPrefix + id.substr(9);
		}
		const li = $('<li>').append($('<a>').attr('href', '#' + id).text(label));
		if (id.indexOf("chatTab-r") !== 0) {
			li.append(OmUtil.tmpl('#chat-close-block'));
		}
		tabs.find(".ui-tabs-nav").append(li);
		tabs.append("<div class='messageArea' id='" + id + "'></div>");
		tabs.tabs("refresh");
		activateTab(id);
	}
	function _addMessage(m) {
		if ($('#chat').length > 0 && m && m.type === "chat") {
			let msg, cm, notify = false;
			while (!!(cm = m.msg.pop())) {
				let area = $('#' + cm.scope);
				if (cm.from.id !== userId) {
					notify = true;
				}
				msg = OmUtil.tmpl('#chat-msg-template', msgIdPrefix + cm.id)
				msg.find('.user-row').css('background-image', 'url(' + (!!cm.from.img ? cm.from.img : './profile/' + cm.from.id + '?anticache=' + Date.now()) + ')');
				msg.find('.from').addClass(align).data('user-id', cm.from.id).html(cm.from.name || cm.from.displayName);
				msg.find('.time').addClass(alignIco).html(cm.time).attr('title', cm.sent);
				const icons = msg.find('.icons').addClass(align)
					.append(OmUtil.tmpl('#chat-info-template').addClass(alignIco).data('user-id', cm.from.id));
				if ('full' === cm.actions) {
					icons.append(OmUtil.tmpl('#chat-add-template').addClass(alignIco).data('user-id', cm.from.id))
						.append(OmUtil.tmpl('#chat-message-template').addClass(alignIco).data('user-id', cm.from.id))
						.append(OmUtil.tmpl('#chat-invite-template').addClass(alignIco).data('user-id', cm.from.id));
				}
				if (cm.needModeration) {
					msg.append(OmUtil.tmpl('#chat-accept-template')
							.data('msgid', cm.id).data('roomid', cm.scope.substring(9)).find('.tick').addClass(alignIco));
				}
				if (!area.length) {
					_addTab(cm.scope, cm.scopeName);
					area = $('#' + cm.scope);
				}
				if (m.mode === "accept") {
					$('#chat-msg-id-' + cm.id).remove();
				}
				const btm = area[0].scrollHeight - (area.scrollTop() + area.innerHeight()) < 3; //approximately equal
				if (area.data('lastDate') !== cm.date) {
					area.append(OmUtil.tmpl('#chat-date-template').html(cm.date));
					area.data('lastDate', cm.date);
				}
				area.append(msg);
				msg.find('.msg').addClass(align).html(emoticon.emoticonize(!!cm.message ? cm.message : ""));
				if (btm) {
					_scrollDown(area);
				}
			}
			if (notify) {
				ctrl.addClass('ui-state-highlight');
				if (p.is(':visible') && !muted) {
					const playPromise = audio.play();

					// In browsers that don’t yet support this functionality,
					// playPromise won’t be defined.
					if (playPromise !== undefined) {
						playPromise.then(function() {
							// Automatic playback started!
						}).catch(function() {
							// Automatic playback failed.
						});
					}
				}
			}
			emoticon.animate();
		}
	}
	function _setOpened() {
		editor.width(p.width() - 30);
		p.resizable({
			handles: (Settings.isRtl ? 'e' : 'w')
			, alsoResize: '#chatPopup, #chatMessage .wysiwyg-editor'
			, minWidth: 120
			, stop: function(event, ui) {
				p.css({'left': ''});
				editor.width(p.width() - 30);
				openedWidth = ui.size.width + 'px';
			}
		});
	}
	function _removeResize() {
		if (p.resizable('instance') !== undefined) {
			p.resizable('destroy');
		}
	}
	function _open(handler) {
		if (isClosed()) {
			icon.removeClass(roomMode ? iconOpenRoom : iconOpen).addClass(roomMode ? iconCloseRoom : iconClose);
			ctrl.removeClass('ui-state-highlight');
			let opts;
			if (roomMode) {
				opts = {width: openedWidth};
				ctrl.height(closedSize);
			} else {
				opts = {height: openedHeight};
				p.resizable("option", "disabled", false);
			}
			p.removeClass('closed');
			pp.animate(opts, 1000, function() {
				p.removeClass('closed');
				if (typeof(handler) === 'function') {
					handler();
				}
				editor.width(p.width() - 30);
				ctrl.attr('title', ctrl.data('ttl-undock'));
				if (roomMode) {
					_setOpened();
					Room.setSize();
				}
				_setAreaHeight();
			});
		}
	}
	function _close(handler) {
		if (!isClosed()) {
			icon.removeClass(roomMode ? iconCloseRoom : iconClose).addClass(roomMode ? iconOpenRoom : iconOpen);
			let opts;
			if (roomMode) {
				opts = {width: closedSizePx};
			} else {
				opts = {height: closedSizePx};
				p.resizable("option", "disabled", true);
			}
			pp.animate(opts, 1000, function() {
				p.addClass('closed');
				if (roomMode) {
					ctrl.height(p.height());
					_removeResize();
				}
				if (typeof(handler) === 'function') {
					handler();
				}
				ctrl.attr('title', ctrl.data('ttl-dock'));
				if (roomMode) {
					Room.setSize();
				}
			});
		}
	}
	function _toggle() {
		if (isClosed()) {
			_open();
		} else {
			_close();
		}
	}
	function _editorAppend(emoticon) {
		editor.html(editor.html() + ' ' + emoticon + ' ').trigger('change');
	}
	function _clean() {
		editor.html('').trigger('change');
	}
	function _setRoomMode(_mode) {
		roomMode = _mode;
		_reinit({userId: userId, all: allPrefix, room: roomPrefix, sendOnEnter: sendOn === SEND_ENTER});
	}
	function _scrollDown(area) {
		area.animate({
			scrollTop: area[0].scrollHeight
		}, 300);
	}
	function _setAreaHeight() {
		$('#chat .ui-tabs .ui-tabs-panel.messageArea').height(p.height() - closedSize - $('#chat .ui-tabs-nav').height() - $('#chat form').height() - 5);
		$('#chat .messageArea').each(function() {
			_scrollDown($(this));
		});
	}
	function _setHeight(h) {
		if (!isInited()) return;
		pp.height(h);
		if (isClosed()) {
			ctrl.height(h);
		} else {
			_setAreaHeight();
		}
	}
	function _insertLink() {
		const text = $('#chat #hyperlink').parent().find('input').val();
		if ('' === text) {
			return;
		}
		let url = text.trim();
		if ('' === url) {
			return;
		}
		if (!/^(https?:)?\/\//i.test(url)) {
			url = 'http://' + url;
		}
		const a = $('<div>').append($('<a></a>').attr('target', '_blank').attr('href', url).text(url)).html();
		if (window.getSelection) {
			const sel = window.getSelection();
			if (sel.rangeCount) {
				const range = sel.getRangeAt(0);
				if ($(range.startContainer).parents('.wysiwyg-editor').length > 0) {
					range.deleteContents();
					range.insertNode(a);
				} else {
					_editorAppend(a);
				}
			}
		}
	}
	return {
		reinit: _reinit
		, removeTab: _removeTab
		, addTab: _addTab
		, addMessage: _addMessage
		, open: _open
		, setOpened: function() {
			_open(function() {
				_setOpened();
			});
		}
		, close: _close
		, toggle: _toggle
		, setRoomMode: _setRoomMode
		, setHeight: _setHeight
		, clean: _clean
		, validate: function() {
			return !!editor && editor.text().trim().length > 0;
		}
	};
}();
$(function() {
	Wicket.Event.subscribe("/websocket/message", function(jqEvent, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = jQuery.parseJSON(msg);
			if (m) {
				switch(m.type) {
					case "chat":
						Chat.addMessage(m);
						break;
					case "typing":
						if (typeof(typingActivity) === "function") {
							typingActivity(m.uid, m.active);
						}
						break;
				}
			}
		} catch (err) {
			//no-op
		}
	});
});
