/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Chat = function() {
	const isRtl = "rtl" === $('html').attr('dir')
		, align = isRtl ? 'align-right' : 'align-left'
		, alignIco = isRtl ? 'align-left' : 'align-right'
		, tabTemplate = "<li><a href='#{href}'>#{label}</a></li>"
		, msgTemplate = "<div class='clear msg-row' id='chat-msg-id-#{id}'><img class='profile " + align + "' src='#{imgSrc}'/><span class='from " + align + "' data-user-id='#{userId}'>#{from}</span><span class='" + align + "'>#{msg}</span><span class='date " + alignIco + "'>#{sent}</span></div>"
		, acceptTemplate = "<div class='tick om-icon " + alignIco + " clickable' data-msgid='#{msgid}' data-roomid='#{roomid}' onclick='const e=$(this);chatActivity('accept',e.data(\"roomid\"),e.data(\"msgid\"));e.parent().remove();'></div>"
		, infoTemplate = "<div class='user om-icon " + alignIco + " clickable' data-user-id='#{userId}' onclick='const e=$(this);showUserInfo(e.data(\"userId\"));'></div>"
		, addTemplate = "<div class='add om-icon " + alignIco + " clickable' data-user-id='#{userId}' onclick='const e=$(this);addContact(e.data(\"userId\"));'></div>"
		, messageTemplate = "<div class='new-email om-icon " + alignIco + " clickable' data-user-id='#{userId}' onclick='const e=$(this);privateMessage(e.data(\"userId\"));'></div>"
		, inviteTemplate = "<div class='invite om-icon " + alignIco + " clickable' data-user-id='#{userId}' onclick='const e=$(this);inviteUser(e.data(\"userId\"));'></div>"
		, closeBlock = "<span class='ui-icon ui-icon-close' role='presentation'></span>"
		, closedHeight = "20px"
		, emoticon = new CSSEmoticon()
		, doneTypingInterval = 5000 //time in ms, 5 second for example
		;
	let chatTabs, openedHeight = "345px", allPrefix = "All"
		, roomPrefix = "Room ", typingTimer, audio, s;

	try {
		audio = new Audio('./public/chat_message.mp3');
	} catch (e) {
		//not implemented in IE
		audio = {
			play: function() {}
		};
	}
	function _load() {
		s = {};
		try {
			s = JSON.parse(localStorage.getItem('openmeetings')) || s;
		} catch (e) {}
		if (!s.chat) {
			s.chat = {};
		}
	}
	function _save() {
		const _s = JSON.stringify(s);
		localStorage.setItem('openmeetings', _s);
	}
	function _updateBtn(a) {
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
		_load();
		const a = $('#chat .audio');
		_updateBtn(a);
		$('#chat .chat-btn').hover(function(){ $(this).addClass('ui-state-hover') }, function(){ $(this).removeClass('ui-state-hover') });
		a.click(function() {
			s.chat.muted = !s.chat.muted;
			_updateBtn(a);
			_save();
		});
	}
	function isClosed() {
		return $('#chatPanel').height() < 24;
	}
	function activateTab(id) {
		chatTabs.tabs("option", "active", chatTabs.find('a[href="#' + id + '"]').parent().index());
	}
	function isInited() {
		return !!$("#chatTabs").data("ui-tabs");
	}
	return {
		reinit: function(_allPrefix, _roomPrefix) {
			allPrefix = _allPrefix;
			roomPrefix = _roomPrefix;
			initToolbar();
			chatTabs = $("#chatTabs").tabs({
				activate: function(event, ui) {
					$('#activeChatTab').val(ui.newPanel[0].id);
				}
			});
			// close icon: removing the tab on click
			chatTabs.delegate("span.ui-icon-close", "click", function() {
				const panelId = $(this).closest("li").remove().attr("aria-controls");
				$("#" + panelId).remove();
				chatTabs.tabs("refresh");
			});
			$('#chatPanel').resizable({
				handles: "n, w"
				, disabled: isClosed()
				, alsoResize: "#chatPopup, #chat .ui-tabs .ui-tabs-panel.messageArea"
				, minHeight: 195
				, minWidth: 260
				, stop: function(event, ui) {
					$('#chatPanel').css({'top': '', 'left': ''});
					openedHeight = ui.size.height + "px";
				}
			});
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
		, removeTab: function(id) {
			$('li[aria-controls="' + id + '"]').remove();
			$('#' + id).remove();
			if (isInited()) {
				chatTabs.tabs("refresh");
			}
		}
		, addTab: function(id, label) {
			if (!isInited()) {
				this.reinit();
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
			chatTabs.find(".ui-tabs-nav").append(li);
			chatTabs.append("<div class='messageArea' id='" + id + "'></div>");
			chatTabs.tabs("refresh");
			activateTab(id);
		}
		, addMessage: function(m) {
			if ($('#chat').length > 0 && m && m.type === "chat") {
				if (isClosed()) {
					$('#chatPopup .control.block').addClass('ui-state-highlight');
					if ($('#chatPanel').is(':visible') && s.chat.muted !== true) {
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
						this.addTab(cm.scope, cm.scopeName);
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
		, open: function() {
			if (isClosed()) {
				$('#chatPopup .control.block .ui-icon').removeClass('ui-icon-caret-1-n').addClass('ui-icon-caret-1-s');
				$('#chatPopup .control.block').removeClass('ui-state-highlight');
				$('#chatPanel, #chatPopup').animate({height: openedHeight}, 1000);
				$('#chatPanel').resizable("option", "disabled", false);
				$('#chat .messageArea').each(function() {
					$(this).scrollTop($(this)[0].scrollHeight);
				});
			}
		}
		, close: function() {
			if (!isClosed()) {
				$('#chatPopup .control.block .ui-icon').removeClass('ui-icon-caret-1-s').addClass('ui-icon-caret-1-n');
				$('#chatPanel, #chatPopup').animate({height: closedHeight}, 1000);
				$('#chatPanel').resizable("option", "disabled", true);
			}
		}
		, toggle: function() {
			if (isClosed()) {
				this.open();
			} else {
				this.close();
			}
		}
		, emtClick: function(emoticon) {
			const editor = $('#chatMessage .wysiwyg-editor');
			editor.html(editor.html() + ' ' + emoticon + ' ');
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
						if (typeof typingActivity === "function") {
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
