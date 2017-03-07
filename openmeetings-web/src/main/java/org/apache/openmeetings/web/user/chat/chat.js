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
var Chat = function() {
	var chatTabs
		, tabTemplate = "<li><a href='#{href}'>#{label}</a></li>"
		, msgTemplate = "<div id='chat-msg-id-#{id}'><img class='profile' src='#{imgSrc}'/><span class='from' data-user-id='#{userId}'>#{from}</span><span class='date align-right'>#{sent}</span>#{msg}</div>"
		, acceptTemplate = "<div class='tick om-icon align-right clickable' data-msgid='#{msgid}' data-roomid='#{roomid}' onclick='var e=$(this);chatActivity('accept',e.data(\"roomid\"),e.data(\"msgid\"));e.parent().remove();'></div>"
		, infoTemplate = "<div class='user om-icon align-right clickable' data-user-id='#{userId}' onclick='var e=$(this);showUserInfo(e.data(\"userId\"));'></div>"
		, addTemplate = "<div class='add om-icon align-right clickable' data-user-id='#{userId}' onclick='var e=$(this);addContact(e.data(\"userId\"));'></div>"
		, messageTemplate = "<div class='new-email om-icon align-right clickable' data-user-id='#{userId}' onclick='var e=$(this);privateMessage(e.data(\"userId\"));'></div>"
		, inviteTemplate = "<div class='invite om-icon align-right clickable' data-user-id='#{userId}' onclick='var e=$(this);inviteUser(e.data(\"userId\"));'></div>"
		, clearBlock = "<div class='clear'></div>"
		, closeBlock = "<span class='ui-icon ui-icon-close' role='presentation'></span>"
		, closedHeight = "20px"
		, openedHeight = "345px"
		, allPrefix = "All"
		, roomPrefix = "Room "
		, emoticon = new CSSEmoticon()
		, typingTimer
		, doneTypingInterval = 5000 //time in ms, 5 second for example
		;

	function doneTyping () {
		typingTimer = null;
		chatActivity('typing_stop', $('.room.box').data('room-id'));
	}
	function initToolbar() {
		var emtBtn = $('#emoticons');
		emtBtn.html('');
		emtBtn.append(' ' + emoticon.emoticonize(':)') + ' <b class="caret"></b>');
		var emots = [].concat.apply([], [emoticon.threeCharEmoticons, emoticon.twoCharEmoticons]);
		for (var ei in emoticon.specialEmoticons) {
			emots.push(ei);
		}
		var emotMenuList = $('#emotMenuList');
		emotMenuList.html('');
		var rowSize = 20;
		var row = $('<tr></tr>');
		for (var i = 0; i < emots.length; ++i) {
			row.append('<td><div class="emt" onclick="Chat.emtClick(\'' + emots[i] + '\');">'
				+ emoticon.emoticonize(emots[i]) + '</div></td>');
			if (i != 0 && i % rowSize == 0) {
				emotMenuList.append(row);
				row = $('<tr></tr>');
			}
		}
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
				var panelId = $(this).closest("li").remove().attr("aria-controls");
				$("#" + panelId).remove();
				chatTabs.tabs("refresh");
			});
			$('#chatPanel').resizable({
				handles: "n, w"
				, disabled: isClosed()
				, alsoResize: "#chat, #chat .ui-tabs .ui-tabs-panel.messageArea"
				, minHeight: 195
				, minWidth: 260
				, stop: function(event, ui) {
					$('#chatPanel').css({'top': '', 'left': ''});
					openedHeight = ui.size.height + "px";
				}
			});
			$('#chatMessage').off().on('input propertychange paste', function () {
				var room = $('.room.box');
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
				label = id == "chatTab-all" ? allPrefix : roomPrefix + id.substr(9);
			}
			var li = $(tabTemplate.replace(/#\{href\}/g, "#" + id).replace(/#\{label\}/g, label));
			if (id.indexOf("chatTab-r") != 0) {
				li.append(closeBlock);
			}
			chatTabs.find(".ui-tabs-nav").append(li);
			chatTabs.append("<div class='messageArea' id='" + id + "'></div>");
			chatTabs.tabs("refresh");
			activateTab(id);
		}
		, addMessage: function(m) {
			if ($('#chat').length > 0 && m && m.type == "chat") {
				if (isClosed()) {
					$('#chat .control.block').addClass('ui-state-highlight');
				}
				var msg, cm;
				while (!!(cm = m.msg.pop())) {
					msg = $(msgTemplate.replace(/#\{id\}/g, cm.id)
							.replace(/#\{userId\}/g, cm.from.id)
							.replace(/#\{imgSrc\}/g, !!cm.from.img ? cm.from.img : './profile/' + cm.from.id + '?anticache=' + Date.now())
							.replace(/#\{from\}/g, cm.from.name)
							.replace(/#\{sent\}/g, cm.sent)
							.replace(/#\{msg\}/g, emoticon.emoticonize(!!cm.message ? cm.message : "")));
					var date = msg.children('.date');
					date.after(infoTemplate.replace(/#\{userId\}/g, cm.from.id));
					if ("full" == cm.actions) {
						date.after(addTemplate.replace(/#\{userId\}/g, cm.from.id));
						date.after(messageTemplate.replace(/#\{userId\}/g, cm.from.id));
						date.after(inviteTemplate.replace(/#\{userId\}/g, cm.from.id));
					}
					if (cm.needModeration) {
						msg.append(acceptTemplate.replace(/#\{msgid\}/g, cm.id).replace(/#\{roomid\}/g, cm.scope.substring(9)));
					}
					if (!$('#' + cm.scope).length) {
						this.addTab(cm.scope, cm.scopeName);
					}
					if (m.mode == "accept") {
						$('#chat-msg-id-' + cm.id).remove();
					}
					msg.append(clearBlock);
					$('#' + cm.scope).prepend(msg);
				}
			}
		}
		, open: function() {
			if (isClosed()) {
				$('#chat .control.block .ui-icon').removeClass('ui-icon-caret-1-n').addClass('ui-icon-caret-1-s');
				$('#chat .control.block').removeClass('ui-state-highlight');
				$('#chatPanel, #chat').animate({height: openedHeight}, 1000);
				$('#chatPanel').resizable("option", "disabled", false);
			}
		}
		, close: function() {
			if (!isClosed()) {
				$('#chat .control.block .ui-icon').removeClass('ui-icon-caret-1-s').addClass('ui-icon-caret-1-n');
				$('#chatPanel').animate({height: closedHeight}, 1000);
				$('#chatPanel, #chat').animate({height: closedHeight}, 1000);
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
			var editor = $('#chatMessage .wysiwyg-editor');
			editor.html(editor.html() + ' ' + emoticon + ' ');
		}
	};
}();

$(function() {
	Wicket.Event.subscribe("/websocket/message", function(jqEvent, msg) {
		try {
			var m = jQuery.parseJSON(msg);
			if (m) {
				switch(m.type) {
					case "chat":
						Chat.addMessage(m);
						break;
					case "typing":
						if (typeof typingActivity == "function") {
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
