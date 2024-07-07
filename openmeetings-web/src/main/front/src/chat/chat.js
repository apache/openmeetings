/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const Settings = require('../main/settings');
const CSSEmoticon = require('./cssemoticons');

const msgIdPrefix = 'chat-msg-id-'
	, closedSize = 20
	, closedSizePx = closedSize + "px"
	, doneTypingInterval = 5000 //time in ms, 5 second for example
	, SEND_ENTER = 'enter', SEND_CTRL = 'ctrl'
	, audio = new Audio('./public/chat_message.mp3')
	;
let chatPanel, ctrlBlk, tabs, openedHeight = "345px", openedWidth = "300px", allPrefix = "All"
	, roomPrefix = "Room ", typingTimer, roomMode = false
	, editor = $('#chatMessage .wysiwyg-editor'), muted = false, sendOn, DEF_SEND
	, userId, inited = false, newMsgNotification
	;

function __setCssVar(key, _val) {
	const val = ('' + _val).endsWith('px') ? _val : _val + 'px';
	if (roomMode) {
		if (typeof(Room) == 'object' && typeof(Room.setCssVar) === 'function') {
			Room.setCssVar(key, val);
		}
	} else {
		OmUtil.setCssVar(key, val);
	}
}
function __setCssWidth(val) {
	__setCssVar('--chat-width', val)
}
function __setCssHeight(val) {
	__setCssVar('--chat-height', val)
}
function _load() {
	const s = Settings.load();
	if (typeof(s.chat) === 'undefined') {
		s.chat = {
			muted: false
			, sendOn: DEF_SEND
		};
		Settings.save(s)
	}
	muted = s.chat.muted === true;
	sendOn = s.chat.sendOn === SEND_ENTER ? SEND_ENTER : SEND_CTRL;
	return s;
}
function _updateAudioBtn() {
	const btn = $('#chat .audio');
	btn.removeClass('sound' + (muted ? '' : '-mute')).addClass('sound' + (muted ? '-mute' : ''))
			.attr('title', btn.data(muted ? 'sound-enabled' : 'sound-muted'));
}
function _updateSendBtn() {
	const btn = $('#chat .send-btn')
		, ctrl = sendOn === SEND_CTRL;
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
	chatActivity('typing_stop', $('.room-block .room-container').data('room-id'));
}
function _emtClick() {
	_editorAppend($(this).data('emt'));
}
function initToolbar() {
	const emots = CSSEmoticon.emoticons;
	const rowSize = 20, emotMenuList = $('#emotMenuList');
	emotMenuList.html('');
	let row;
	for (let i = 0; i < emots.length; ++i) {
		if (i % rowSize === 0) {
			row = $('<tr></tr>');
			emotMenuList.append(row);
		}
		row.append($('<td>').append(
				$('<div>').addClass('emt').html(CSSEmoticon.emoticonize(emots[i]))
					.data('emt', emots[i]).click(_emtClick)
			));
	}
	const emtBtn = $('#emoticons');
	emtBtn.html('');
	emtBtn.append(' ' + CSSEmoticon.emoticonize(':)'));
	const a = $('#chat .audio');
	const sbtn = $('#chat .send-btn');
	{ //scope
		_load();
		_updateAudioBtn();
		_updateSendBtn()
	}
	a.off().click(function() {
		const s = _load();
		muted = s.chat.muted = !s.chat.muted;
		_updateAudioBtn();
		Settings.save(s);
	});
	sbtn.off().click(function() {
		const s = _load();
		sendOn = s.chat.sendOn = s.chat.sendOn !== SEND_CTRL ? SEND_CTRL : SEND_ENTER;
		_updateSendBtn();
		Settings.save(s);
	});
	$('#chat #hyperlink').parent().find('button').off().click(function() {
		_insertLink();
	});
	CSSEmoticon.animate();
}
function isClosed() {
	return chatPanel.hasClass('closed');
}
function activateTab(id) {
	if (isClosed()) {
		tabs.find('.nav.nav-tabs .nav-link').each(function() {
			const self = $(this)
				, tabId = self.attr('aria-controls')
				, tab = $('#' + tabId);

			if (tabId === id) {
				self.addClass('active');
				tab.addClass('active');
				self.attr('aria-selected', true);
			} else {
				self.removeClass('active');
				tab.removeClass('active');
				self.attr('aria-selected', false);
			}
		});
	} else {
		$('#chatTabs li a[aria-controls="' + id + '"]').tab('show');
	}
	$('#activeChatTab').val(id).trigger('change');
}
function _reinit(opts) {
	userId = opts.userId;
	allPrefix = opts.all;
	roomPrefix = opts.room;
	DEF_SEND = opts.sendOnEnter === true ? SEND_ENTER : SEND_CTRL;
	sendOn = DEF_SEND;
	chatPanel = $('#chatPanel');
	clearTimeout(chatPanel.data('timeout'));
	ctrlBlk = $('#chatPopup .control.block');
	newMsgNotification = ctrlBlk.data('new-msg');
	editor = $('#chatMessage .wysiwyg-editor');
	initToolbar();
	tabs = $("#chatTabs");
	tabs.off().on('shown.bs.tab', function (e) {
		const ct = $(e.target).attr('aria-controls');
		_scrollDown($('#' + ct));
		$('#activeChatTab').val(ct).trigger('change');
	});
	tabs.delegate(".btn.close-chat", "click", function() {
		const panelId = $(this).closest("a").attr("aria-controls");
		_removeTab(panelId);
		$('#chatTabs li:last-child a').tab('show');
	});
	if (roomMode) {
		_removeResize();
	} else {
		ctrlBlk.attr('title', '');
		chatPanel.removeClass('room opened').addClass('closed')
			.off('mouseenter mouseleave').resizable({
				handles: 'n, ' + (Settings.isRtl ? 'e' : 'w')
				, disabled: isClosed()
				, minHeight: 195
				, minWidth: 260
				, stop: function(_, ui) {
					chatPanel.css({'top': '', 'left': ''});
					openedHeight = ui.size.height + 'px';
					__setCssHeight(openedHeight);
					__setCssWidth(ui.size.width);
				}
			});
		__setCssHeight(closedSize);
	}
	ctrlBlk.off().click(_toggle);
	$('#chatMessage').off().on('input propertychange paste', function () {
		const room = $('.room-block .room-container');
		if (room.length) {
			if (!!typingTimer) {
				clearTimeout(typingTimer);
			} else {
				chatActivity('typing_start', room.data('room-id'));
			}
			typingTimer = setTimeout(doneTyping, doneTypingInterval);
		}
	});
	$('#chat .chat-toolbar .link-field').off().on('keypress', function(evt) {
		if (evt.keyCode === 13) {
			$(this).parent().find('button').trigger('click');
		}
		return evt.keyCode !== 13;
	});
	inited = true;
}
function _removeTab(id) {
	$('#chatTabs li a[aria-controls="' + id + '"]').parent().remove();
	$('#' + id).remove();
}
function _addTab(id, label) {
	if (!inited) {
		_reinit({});
	}
	if ($('#chat').length < 1 || $('#' + id).length) {
		return;
	}
	if (!label) {
		label = id === "chatTab-all" ? allPrefix : roomPrefix + id.substr(9);
	}
	const link = $('<a class="nav-link" data-bs-toggle="tab">')
		.attr('aria-controls', id)
		.attr('href', '#' + id).text(label)
		, li = $('<li class="nav-item">').append(link);
	if (id.indexOf("chatTab-u") === 0) {
		link.append(OmUtil.tmpl('#chat-close-block'));
	}
	tabs.find('.nav.nav-tabs').append(li);
	const msgArea = OmUtil.tmpl('#chat-msg-area-template', id);
	tabs.find('.tab-content').append(msgArea);
	msgArea.append($('<div class="clear icons actions float-start">').addClass('short')
			.append(OmUtil.tmpl('#chat-actions-short-template')));
	msgArea.append($('<div class="clear icons actions float-start">').addClass('short-mod')
			.append(OmUtil.tmpl('#chat-actions-short-template'))
			.append(OmUtil.tmpl('#chat-actions-accept-template')));
	msgArea.append($('<div class="clear icons actions float-start">').addClass('full')
			.append(OmUtil.tmpl('#chat-actions-short-template'))
			.append(OmUtil.tmpl('#chat-actions-others-template').children().clone()));
	msgArea.append($('<div class="clear icons actions float-start">').addClass('full-mod')
			.append(OmUtil.tmpl('#chat-actions-short-template'))
			.append(OmUtil.tmpl('#chat-actions-others-template').children().clone())
			.append(OmUtil.tmpl('#chat-actions-accept-template')));
	const actions = __hideActions();
	actions.find('.user').off().click(function() {
		const e = $(this).parent();
		showUserInfo(e.data("userId"));
	});
	actions.find('.add').off().click(function() {
		const e = $(this).parent();
		addContact(e.data("userId"));
	});
	actions.find('.new-email').off().click(function() {
		const e = $(this).parent();
		privateMessage(e.data("userId"));
	});
	actions.find('.invite').off().click(function() {
		const e = $(this).parent();
		inviteUser(e.data("userId"));
	});
	actions.find('.accept').off().click(function() {
		const e = $(this).parent()
			, msgId = e.data('msgId');
		chatActivity('accept', e.data('roomId'), msgId);
		__hideActions();
		$('#chat-msg-id-' + msgId).remove();
	});
	activateTab(id);
}
function __hideActions() {
	return $('#chat .tab-content .messageArea .icons').hide();
}
function __getActions(row) {
	return row.closest('.messageArea').find('.actions.' + row.data('actions'));
}
function _addMessage(m) {
	if ($('#chat').length > 0 && m && m.type === "chat") {
		let msg, cm, notify = false;
		while (!!(cm = m.msg.pop())) {
			let area = $('#' + cm.scope);
			if (cm.from.id !== userId && (isClosed() || !area.is(':visible'))) {
				notify = true;
			}
			const actions = ('full' === cm.actions ? 'full' : 'short') + (cm.needModeration ? '-mod' : '');
			msg = OmUtil.tmpl('#chat-msg-template', msgIdPrefix + cm.id)
			const row = msg.find('.user-row')
				.data('userId', cm.from.id)
				.data('actions', actions)
				.mouseenter(function() {
					__hideActions();
					__getActions($(this))
						.data('userId', $(this).data('userId'))
						.data('roomId', $(this).data('roomId'))
						.data('msgId', $(this).data('msgId'))
						.css('top', ($(this).closest('.msg-row')[0].offsetTop + 20) + 'px')
						.show();
				});
			if (cm.needModeration) {
				row.parent().addClass('need-moderation');
				row.data('roomId', cm.scope.substring(9))
					.data('msgId', cm.id);
			}
			area.mouseleave(function() {
				__hideActions();
			});
			msg.find('.from').data('user-id', cm.from.id).html(cm.from.displayName || cm.from.name);
			msg.find('.time').html(cm.time).attr('title', cm.sent);
			if (!area.length) {
				_addTab(cm.scope, cm.scopeName);
				area = $('#' + cm.scope);
			}
			if (m.mode === "accept") {
				$('#chat-msg-id-' + cm.id).remove();
			}
			const btm = area[0].scrollHeight - (area.scrollTop() + area.innerHeight()) < 3; //approximately equal
			if (area.data('lastDate') !== cm.date) {
				area.append(OmUtil.tmpl('#chat-date-template').html(cm.date).mouseenter(function() {
					__hideActions();
				}));
				area.data('lastDate', cm.date);
			}
			area.append(msg);
			msg.find('.user-row')[0].style.cssText = `
				background-image: url(${(!!cm.from.img ? cm.from.img : './profile/' + cm.from.id + '?anticache=' + Date.now())});
				background-position-x: ${Settings.isRtl ? 'right' : 'left'};
			`;

			msg.find('.msg').html(CSSEmoticon.emoticonize(!!cm.message ? cm.message : ""));
			if (btm) {
				_scrollDown(area);
			}
		}
		if (notify) {
			ctrlBlk.addClass('bg-warning');
			if (chatPanel.is(':visible') && !muted) {
				OmUtil.notify(newMsgNotification, 'new_chat_msg', () => {
					// impossible to use Notification API from iFrame
					audio.play()
						.then(function() {
							// Automatic playback started!
						}).catch(function() {
							// Automatic playback failed.
						});
				});
			}
		}
		CSSEmoticon.animate();
	}
}
function _setOpened() {
	__setCssWidth(openedWidth);
	chatPanel.resizable({
		handles: (Settings.isRtl ? 'e' : 'w')
		, minWidth: 165
		, stop: function(_, ui) {
			chatPanel.css({'left': '', 'width': '', 'height': ''});
			openedWidth = ui.size.width + 'px';
			__setCssWidth(openedWidth);
		}
	});
}
function _removeResize() {
	if (chatPanel.resizable('instance') !== undefined) {
		chatPanel.resizable('destroy');
	}
}
function _open(handler) {
	if (isClosed()) {
		ctrlBlk.removeClass('bg-warning');
		let opts;
		if (roomMode) {
			opts = {width: openedWidth};
		} else {
			opts = {height: openedHeight};
			chatPanel.resizable("option", "disabled", false);
		}
		chatPanel.removeClass('closed').animate(opts, 1000, function() {
			__hideActions();
			chatPanel.removeClass('closed');
			chatPanel.css({'height': '', 'width': ''});
			if (typeof(handler) === 'function') {
				handler();
			}
			ctrlBlk.attr('title', ctrlBlk.data('ttl-undock'));
			if (roomMode) {
				_setOpened();
				if (typeof(window.WbArea) === 'object') {
					window.WbArea.resize();
				}
			} else {
				__setCssHeight(openedHeight);
			}
			_setAreaHeight();
		});
	}
}
function _close(handler) {
	if (!isClosed()) {
		let opts;
		if (roomMode) {
			opts = {width: closedSizePx};
		} else {
			opts = {height: closedSizePx};
			chatPanel.resizable("option", "disabled", true);
		}
		chatPanel.animate(opts, 1000, function() {
			chatPanel.addClass('closed').css({'height': '', 'width': ''});
			if (roomMode) {
				__setCssWidth(closedSizePx);
				_removeResize();
				if (typeof(window.WbArea) === 'object') {
					window.WbArea.resize();
				}
			} else {
				__setCssHeight(closedSizePx);
			}
			if (typeof(handler) === 'function') {
				handler();
			}
			ctrlBlk.attr('title', ctrlBlk.data('ttl-dock'));
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
function _editorAppend(_emoticon) {
	editor.html(editor.html() + ' ' + _emoticon + ' ').trigger('change');
}
function _clean() {
	editor.html('').trigger('change');
}
function _setRoomMode(_mode) {
	roomMode = _mode;
	if (inited && !roomMode) {
		// remove all private chats on room exit
		$('li[aria-controls^="chatTab-u"]').remove();
		$('div[id^="chatTab-u"]').remove();
	}
	_reinit({userId: userId, all: allPrefix, room: roomPrefix, sendOnEnter: sendOn === SEND_ENTER});
}
function _scrollDown(area) {
	area.animate({
		scrollTop: area[0].scrollHeight
	}, 300);
}
function _setAreaHeight() {
	$('#chat .messageArea').each(function() {
		_scrollDown($(this));
	});
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
function _typingActivity(uid, active) {
	if (typeof(Room) !== 'object') {
		return;
	}
	const c = Room.getClient(uid);
	if (!c) {
		return;
	}
	const u = c.find('.typing-activity');
	if (!u) {
		return;
	}
	if (active) {
		u.addClass("typing");
	} else {
		u.removeClass("typing");
	}
}

$(function() {
	Wicket.Event.subscribe("/websocket/message", function(_, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = JSON.parse(msg);
			if (m) {
				switch(m.type) {
					case "chat":
						if ('clean' === m.action) {
							$('#' + m.scope).html('');
						} else {
							_addMessage(m);
						}
						break;
					case "typing":
						_typingActivity(m.uid, m.active);
						break;
				}
			}
		} catch (err) {
			//no-op
		}
	});
	function _cancelAskNotification() {
		$(document).off('click', _askNotification)
	}
	function _askNotification() {
		OmUtil.requestNotifyPermission(_cancelAskNotification, _cancelAskNotification);
	}
	$(document).on('click', _askNotification);
});

module.exports = {
	SEND_ENTER: SEND_ENTER
	, SEND_CTRL: SEND_CTRL

	, reinit: _reinit
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
	, clean: _clean
	, reload: () => {
		_load();
		_updateAudioBtn();
		_updateSendBtn();
	}
	, validate: function() {
		return !!editor && editor.text().trim().length > 0;
	}
};
