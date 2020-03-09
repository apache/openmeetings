/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Chat = function() {
	const align = Settings.isRtl ? 'align-right' : 'align-left'
		, alignIco = Settings.isRtl ? 'align-left' : 'align-right'
		, msgIdPrefix = 'chat-msg-id-'
		, closedSize = 20
		, closedSizePx = closedSize + "px"
		, emoticon = new CSSEmoticon()
		, doneTypingInterval = 5000 //time in ms, 5 second for example
		, iconOpen = 'fa-angle-up'
		, iconOpenRoom = 'fa-angle-' + (Settings.isRtl ? 'left' : 'right')
		, iconClose = 'fa-angle-down'
		, iconCloseRoom = 'fa-angle-' + (Settings.isRtl ? 'right' : 'left')
		, SEND_ENTER = 'enter', SEND_CTRL = 'ctrl'
		;
	let p, pp, ctrl, icon, tabs, openedHeight = "345px", openedWidth = "300px", allPrefix = "All"
		, roomPrefix = "Room ", typingTimer, audio, roomMode = false, globalWidth = 600
		, editor = $('#chatMessage .wysiwyg-editor'), muted = false, sendOn, DEF_SEND
		, userId, inited = false
		;

	try {
		audio = new Audio('./public/chat_message.mp3');
	} catch (e) {
		//not implemented in IE
		audio = {
			play: function() {}
		};
	}
	function __setCssVar(key, _val) {
		const val = ('' + _val).endsWith('px') ? _val : _val + 'px';
		if (roomMode) {
			if (typeof(Room) == 'object' && typeof(Room.setCssVar) === 'function') {
				Room.setCssVar(key, val);
			}
		} else {
			OmUtil.setCssVar(key, val);
		}
	};
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
		chatActivity('typing_stop', $('.room-block .room-container').data('room-id'));
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
		emtBtn.append(' ' + emoticon.emoticonize(':)'));
		const a = $('#chat .audio');
		const sbtn = $('#chat .send-btn');
		{ //scope
			_load();
			_updateAudioBtn(a);
			_updateSendBtn(sbtn)
		}
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
		icon = $('#chatPopup .control.block i.fas');
		editor = $('#chatMessage .wysiwyg-editor');
		icon.removeClass(function(index, className) {
			return (className.match (/(^|\s)fa-angle-\S+/g) || []).join(' ');
		});
		initToolbar();
		tabs = $("#chatTabs");
		tabs.off().on('shown.bs.tab', function (e) {
			_scrollDown($('#' + $(e.target).attr('aria-controls')));
		});
		tabs.delegate(".btn.close-chat", "click", function() {
			const panelId = $(this).closest("a").attr("aria-controls");
			_removeTab(panelId);
			$('#chatTabs li:last-child a').tab('show');
		});
		if (roomMode) {
			icon.addClass(isClosed() ? iconOpenRoom : iconCloseRoom);
			_removeResize();
		} else {
			ctrl.attr('title', '');
			icon.addClass(iconOpen);
			p.removeClass('room opened').addClass('closed')
				.off('mouseenter mouseleave')
				.resizable({
					handles: 'n, ' + (Settings.isRtl ? 'e' : 'w')
					, disabled: isClosed()
					, minHeight: 195
					, minWidth: 260
					, stop: function(event, ui) {
						p.css({'top': '', 'left': ''});
						openedHeight = ui.size.height + 'px';
						globalWidth = ui.size.width;
						__setCssHeight(openedHeight);
						__setCssWidth(ui.size.width);
					}
				});
			__setCssHeight(closedSize);
		}
		ctrl.off('click').click(Chat.toggle);
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
		const link = $('<a class="nav-link" data-toggle="tab" role="tab">')
			.attr('aria-controls', id)
			.attr('href', '#' + id).text(label)
			, li = $('<li class="nav-item">').append(link);
		if (id.indexOf("chatTab-u") === 0) {
			link.append(OmUtil.tmpl('#chat-close-block'));
		}
		tabs.find('.nav.nav-tabs').append(li);
		tabs.find('.tab-content').append(OmUtil.tmpl('#chat-msg-area-template', id));
		const actions = __hideActions();
		actions.addClass(align);
		actions.find('.user').off('click').click(function() {
			const e = $(this).parent();
			showUserInfo(e.data("userId"));
		});
		actions.find('.add').off('click').click(function() {
			const e = $(this).parent();
			addContact(e.data("userId"));
		});
		actions.find('.new-email').off('click').click(function() {
			const e = $(this).parent();
			privateMessage(e.data("userId"));
		});
		actions.find('.invite').off('click').click(function() {
			const e = $(this).parent();
			inviteUser(e.data("userId"));
		});
		activateTab(id);
	}
	function __hideActions() {
		return $('#chat .tab-content .messageArea .icons').hide();
	}
	function __getActions(row) {
		return row.closest('.messageArea').find('.actions.' + ('full' === row.data('actions') ? 'full' : 'short'));
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
				msg.find('.user-row')
					.data('userId', cm.from.id)
					.data('actions', cm.actions)
					.mouseenter(function() {
						__hideActions();
						__getActions($(this))
							.data('userId', $(this).data('userId'))
							.css('top', ($(this).closest('.msg-row')[0].offsetTop + 20) + 'px')
							.show();
					});
				area.mouseleave(function() {
					__hideActions();
				});
				msg.find('.from').addClass(align).data('user-id', cm.from.id).html(cm.from.name || cm.from.displayName);
				msg.find('.time').addClass(alignIco).html(cm.time).attr('title', cm.sent);
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
				msg.find('.user-row')[0].style.backgroundImage = 'url(' + (!!cm.from.img ? cm.from.img : './profile/' + cm.from.id + '?anticache=' + Date.now()) + ')';

				msg.find('.msg').addClass(align).html(emoticon.emoticonize(!!cm.message ? cm.message : ""));
				if (btm) {
					_scrollDown(area);
				}
			}
			if (notify) {
				ctrl.addClass('bg-warning');
				if (p.is(':visible') && !muted) {
					audio.play()
						.then(function() {
							// Automatic playback started!
						}).catch(function() {
							// Automatic playback failed.
						});
				}
			}
			emoticon.animate();
		}
	}
	function _setOpened() {
		__setCssWidth(openedWidth);
		p.resizable({
			handles: (Settings.isRtl ? 'e' : 'w')
			, minWidth: 120
			, stop: function(event, ui) {
				p.css({'left': '', 'width': '', 'height': ''});
				openedWidth = ui.size.width + 'px';
				__setCssWidth(openedWidth);
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
			ctrl.removeClass('bg-warning');
			let opts;
			if (roomMode) {
				opts = {width: openedWidth};
			} else {
				opts = {height: openedHeight};
				p.resizable("option", "disabled", false);
			}
			p.removeClass('closed').animate(opts, 1000, function() {
				p.removeClass('closed');
				p.css({'height': '', 'width': ''});
				if (typeof(handler) === 'function') {
					handler();
				}
				ctrl.attr('title', ctrl.data('ttl-undock'));
				if (roomMode) {
					_setOpened();
				} else {
					__setCssHeight(openedHeight);
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
			p.animate(opts, 1000, function() {
				p.addClass('closed').css({'height': '', 'width': ''});
				if (roomMode) {
					__setCssWidth(closedSizePx);
					_removeResize();
				} else {
					__setCssHeight(closedSizePx);
				}
				if (typeof(handler) === 'function') {
					handler();
				}
				ctrl.attr('title', ctrl.data('ttl-dock'));
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
			const m = JSON.parse(msg);
			if (m) {
				switch(m.type) {
					case "chat":
						if ('clean' === m.action) {
							$('#' + m.scope).html('');
						} else {
							Chat.addMessage(m);
						}
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
