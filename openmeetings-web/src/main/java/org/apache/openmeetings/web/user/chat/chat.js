/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Chat = function() {
	const align = Settings.isRtl ? 'align-right' : 'align-left'
		, alignIco = Settings.isRtl ? 'align-left' : 'align-right'
		, tabTemplate = "<li><a href='#{href}'>#{label}</a></li>"
		, msgTemplate = "<div class='clear msg-row' id='chat-msg-id-#{id}'><img class='profile " + align + "' src='#{imgSrc}'/><span class='from " + align + "' data-user-id='#{userId}'>#{from}</span><span class='" + align + "'>#{msg}</span><span class='date " + alignIco + "'>#{sent}</span></div>"
		, acceptTemplate = "<div class='tick om-icon " + alignIco + " clickable' data-msgid='#{msgid}' data-roomid='#{roomid}' onclick='const e=$(this);chatActivity('accept',e.data(\"roomid\"),e.data(\"msgid\"));e.parent().remove();'></div>"
		, infoTemplate = "<div class='user om-icon " + alignIco + " clickable' data-user-id='#{userId}' onclick='const e=$(this);showUserInfo(e.data(\"userId\"));'></div>"
		, addTemplate = "<div class='add om-icon " + alignIco + " clickable' data-user-id='#{userId}' onclick='const e=$(this);addContact(e.data(\"userId\"));'></div>"
		, messageTemplate = "<div class='new-email om-icon " + alignIco + " clickable' data-user-id='#{userId}' onclick='const e=$(this);privateMessage(e.data(\"userId\"));'></div>"
		, inviteTemplate = "<div class='invite om-icon " + alignIco + " clickable' data-user-id='#{userId}' onclick='const e=$(this);inviteUser(e.data(\"userId\"));'></div>"
		, closeBlock = "<span class='ui-icon ui-icon-close' role='presentation'></span>"
		, closedSize = 20
		, closedSizePx = closedSize + "px"
		, emoticon = new CSSEmoticon()
		, doneTypingInterval = 5000 //time in ms, 5 second for example
		, iconOpen = 'ui-icon-caret-1-n'
		, iconOpenRoom = 'ui-icon-caret-1-' + (Settings.isRtl ? 'e' : 'w')
		, iconClose = 'ui-icon-caret-1-s'
		, iconCloseRoom = 'ui-icon-caret-1-' + (Settings.isRtl ? 'w' : 'e')
		;
	let p, pp, ctrl, icon, tabs, openedHeight = "345px", openedWidth = "300px", allPrefix = "All"
		, roomPrefix = "Room ", typingTimer, audio, roomMode = false, globalWidth = 600
		, editor = $('#chatMessage .wysiwyg-editor')
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
			s.chat = {};
		}
		return s;
	}
	function _updateBtn(s, a) {
		const muted = s.chat.muted === true;
		a.removeClass('sound' + (muted ? '' : '-mute')).addClass('sound' + (muted ? '-mute' : ''))
				.attr('title', a.data(muted ? 'sound-enabled' : 'sound-muted'));
	}
	function doneTyping () {
		typingTimer = null;
		chatActivity('typing_stop', $('.room.box').data('room-id'));
	}
	function initToolbar() {
		const emtBtn = $('#emoticons');
		emtBtn.html('');
		emtBtn.append(' ' + emoticon.emoticonize(':)') + ' <b class="caret"></b>');
		const emots = [].concat.apply([], [emoticon.threeCharEmoticons, emoticon.twoCharEmoticons]);
		for (let ei in emoticon.specialEmoticons) {
			emots.push(ei);
		}
		const rowSize = 20, emotMenuList = $('#emotMenuList');
		emotMenuList.html('');
		let row = $('<tr></tr>');
		for (let i = 0; i < emots.length; ++i) {
			row.append('<td><div class="emt" onclick="Chat.emtClick(\'' + emots[i] + '\');">'
				+ emoticon.emoticonize(emots[i]) + '</div></td>');
			if (i !== 0 && i % rowSize === 0) {
				emotMenuList.append(row);
				row = $('<tr></tr>');
			}
		}
		const a = $('#chat .audio');
		_updateBtn(_load(), a);
		$('#chat .chat-btn').hover(function(){ $(this).addClass('ui-state-hover') }, function(){ $(this).removeClass('ui-state-hover') });
		a.off().click(function() {
			const s = _load();
			s.chat.muted = !s.chat.muted;
			_updateBtn(s, a);
			Settings.save(s);
		});
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
	function _reinit(_allPrefix, _roomPrefix) {
		allPrefix = _allPrefix;
		roomPrefix = _roomPrefix;
		p = $('#chatPanel');
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
				$('#activeChatTab').val(ui.newPanel[0].id);
			}
		});
		// close icon: removing the tab on click
		tabs.delegate("span.ui-icon-close", "click", function() {
			const panelId = $(this).closest("li").remove().attr("aria-controls");
			$("#" + panelId).remove();
			tabs.tabs("refresh");
		});
		if (roomMode) {
			icon.addClass(isClosed ? iconOpenRoom : iconCloseRoom);
			p.addClass('room').hover(_open, _close);
			pp.width(closedSize);
			_removeResize();
		} else {
			ctrl.attr('title', '');
			icon.addClass(isClosed ? iconOpen : iconClose);
			ctrl.height(closedSize).width(globalWidth).off('click').click(Chat.toggle);
			pp.width(globalWidth).height(closedSize);
			p.removeClass('room')
				.off('mouseenter mouseleave')
				.resizable({
					handles: 'n, ' + (Settings.isRtl ? 'w' : 'e')
					, disabled: isClosed()
					, alsoResize: "#chatPopup, #chat .ui-tabs .ui-tabs-panel.messageArea"
					, minHeight: 195
					, minWidth: 260
					, stop: function(event, ui) {
						p.css({'top': '', 'left': ''});
						openedHeight = ui.size.height + "px";
						globalWidth = ui.size.width;
					}
				});
		}
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
			_reinit();
		}
		if ($('#chat').length < 1 || $('#' + id).length) {
			return;
		}
		if (!label) {
			label = id === "chatTab-all" ? allPrefix : roomPrefix + id.substr(9);
		}
		const li = $(tabTemplate.replace(/#\{href\}/g, "#" + id).replace(/#\{label\}/g, label));
		if (id.indexOf("chatTab-r") !== 0) {
			li.append(closeBlock);
		}
		tabs.find(".ui-tabs-nav").append(li);
		tabs.append("<div class='messageArea' id='" + id + "'></div>");
		tabs.tabs("refresh");
		activateTab(id);
	}
	function _addMessage(m) {
		if ($('#chat').length > 0 && m && m.type === "chat") {
			if (isClosed()) {
				ctrl.addClass('ui-state-highlight');
				const s = _load();
				if (p.is(':visible') && s.chat.muted !== true) {
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
			let msg, cm;
			while (!!(cm = m.msg.pop())) {
				let area = $('#' + cm.scope);
				msg = $(msgTemplate.replace(/#\{id\}/g, cm.id)
						.replace(/#\{userId\}/g, cm.from.id)
						.replace(/#\{imgSrc\}/g, !!cm.from.img ? cm.from.img : './profile/' + cm.from.id + '?anticache=' + Date.now())
						.replace(/#\{from\}/g, cm.from.name)
						.replace(/#\{sent\}/g, cm.sent)
						.replace(/#\{msg\}/g, emoticon.emoticonize(!!cm.message ? cm.message : "")));
				const date = msg.children('.date');
				date.after(infoTemplate.replace(/#\{userId\}/g, cm.from.id));
				if ("full" === cm.actions) {
					date.after(addTemplate.replace(/#\{userId\}/g, cm.from.id));
					date.after(messageTemplate.replace(/#\{userId\}/g, cm.from.id));
					date.after(inviteTemplate.replace(/#\{userId\}/g, cm.from.id));
				}
				if (cm.needModeration) {
					msg.append(acceptTemplate.replace(/#\{msgid\}/g, cm.id).replace(/#\{roomid\}/g, cm.scope.substring(9)));
				}
				if (!area.length) {
					_addTab(cm.scope, cm.scopeName);
					area = $('#' + cm.scope);
				}
				if (m.mode === "accept") {
					$('#chat-msg-id-' + cm.id).remove();
				}
				const btm = area.scrollTop() + area.innerHeight() >= area[0].scrollHeight;
				area.append(msg);
				if (btm) {
					area.animate({
						scrollTop: area[0].scrollHeight
					}, 300);
				}
			}
		}
	}
	function _setOpened() {
		p.addClass('opened').off('mouseenter mouseleave');
		p.resizable({
			handles: (Settings.isRtl ? 'e' : 'w')
			, alsoResize: '#chatPopup'
			, stop: function(event, ui) {
				p.css({'left': ''});
				openedWidth = ui.size.width + 'px';
			}
		});
		Room.setSize();
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
				$('#chat .ui-tabs .ui-tabs-panel.messageArea').height(p.height() - closedSize - $('#chat .ui-tabs-nav').height() - $('#chat form').height() - 5);
				$('#chat .messageArea').each(function() {
					$(this).scrollTop($(this)[0].scrollHeight);
				});
				if (typeof(handler) === 'function') {
					handler();
				}
				if (roomMode) {
					ctrl.off('click').click(function() {
						if (p.hasClass('opened')) {
							ctrl.attr('title', ctrl.data('ttl-dock'));
							_close(Room.setSize);
							p.removeClass('opened').hover(_open, _close);
							_removeResize();
						} else {
							ctrl.attr('title', ctrl.data('ttl-undock'));
							_setOpened();
						}
					}).attr('title', ctrl.data('ttl-dock'));
				}
			});
		}
	}
	function _close(handler) {
		if (!isClosed()) {
			icon.removeClass(roomMode ? iconCloseRoom : iconClose).addClass(roomMode ? iconOpenRoom : iconOpen);
			let opts;
			if (roomMode) {
				opts = {width: closedSizePx};
				ctrl.off('click');
			} else {
				opts = {height: closedSizePx};
				p.resizable("option", "disabled", true);
			}
			pp.animate(opts, 1000, function() {
				p.addClass('closed');
				if (roomMode) {
					ctrl.height(p.height());
				}
				if (typeof(handler) === 'function') {
					handler();
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
	function _emtClick(emoticon) {
		editor.html(editor.html() + ' ' + emoticon + ' ').trigger('change');
	}
	function _clean() {
		editor.html('').trigger('change');
	}
	function _setRoomMode(_mode) {
		roomMode = _mode;
		_reinit(allPrefix, roomPrefix);
	}
	function _setHeight(h) {
		pp.height(h);
		if (isClosed()) {
			ctrl.height(h);
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
		, emtClick: _emtClick
		, setRoomMode: _setRoomMode
		, setHeight: _setHeight
		, clean: _clean
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
