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
var chatTabs, tabTemplate = "<li><a href='#{href}'>#{label}</a></li>"
	, msgTemplate = "<div id='chat-msg-id-#{id}'><img class='profile' src='#{imgSrc}'/><span class='from' data-user-id='#{userId}'>#{from}</span><span class='date align-right'>#{sent}</span>#{msg}</div>"
	, acceptTemplate = "<div class='tick om-icon align-right clickable' data-msgid='#{msgid}' data-roomid='#{roomid}' onclick='var e=$(this);acceptMessage(e.data(\"roomid\"),e.data(\"msgid\"));e.parent().remove();'></div>"
	, infoTemplate = "<div class='user om-icon align-right clickable' data-user-id='#{userId}' onclick='var e=$(this);showUserInfo(e.data(\"userId\"));'></div>"
	, addTemplate = "<div class='add om-icon align-right clickable' data-user-id='#{userId}' onclick='var e=$(this);addContact(e.data(\"userId\"));'></div>"
	, messageTemplate = "<div class='new-email om-icon align-right clickable' data-user-id='#{userId}' onclick='var e=$(this);privateMessage(e.data(\"userId\"));'></div>"
	, inviteTemplate = "<div class='invite om-icon align-right clickable' data-user-id='#{userId}' onclick='var e=$(this);inviteUser(e.data(\"userId\"));'></div>"
	, clearBlock = "<div class='clear'></div>"
	, closeBlock = "<span class='ui-icon ui-icon-close' role='presentation'></span>"
	, closedHeight = "20px", openedHeight = "345px";
$(function() {
	Wicket.Event.subscribe("/websocket/message", function(jqEvent, msg) {
		var m = jQuery.parseJSON(msg);
		if (m) {
			switch(m.type) {
				case "chat":
					addChatMessage(m);
					break;
			}
		}
	});
	reinit();
});
function reinit() {
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
}
function chatClosed() {
	return $('#chatPanel').height() < 24;
}
function openChat() {
	if (chatClosed()) {
		$('#chat .control.block .ui-icon').removeClass('ui-icon-carat-1-n').addClass('ui-icon-carat-1-s');
		$('#chat .control.block').removeClass('ui-state-highlight');
		$('#chatPanel, #chat').animate({height: openedHeight}, 1000);
	}
}
function closeChat() {
	if (!chatClosed()) {
		$('#chat .control.block .ui-icon').removeClass('ui-icon-carat-1-s').addClass('ui-icon-carat-1-n');
		$('#chatPanel').animate({height: closedHeight}, 1000);
		$('#chatPanel, #chat').animate({height: closedHeight}, 1000);
	}
}
function toggleChat() {
	if (chatClosed()) {
		openChat();
	} else {
		closeChat();
	}
}
function activateTab(id) {
	chatTabs.tabs("option", "active", chatTabs.find('a[href="#' + id + '"]').parent().index());
}
function addChatTab(id, label) {
	if (!$("#chatTabs").data("ui-tabs")) {
		reinit();
	}
	if ($('#chat').length < 1 || $('#' + id).length) {
		return;
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
function removeChatTab(id) {
	$('li[aria-controls="' + id + '"]').remove();
	$('#' + id).remove();
	chatTabs.tabs("refresh");
}
function addChatMessage(m) {
	if ($('#chat').length > 0 && m && m.type == "chat") {
		if (chatClosed()) {
			$('#chat .control.block').addClass('ui-state-highlight');
		}
		var msg;
		for (var i = 0; i < m.msg.length; ++i) {
			var cm = m.msg[i];
			msg = $(msgTemplate.replace(/#\{id\}/g, cm.id)
					.replace(/#\{userId\}/g, cm.from.id)
					.replace(/#\{imgSrc\}/g, cm.from.img)
					.replace(/#\{from\}/g, cm.from.name)
					.replace(/#\{sent\}/g, cm.sent)
					.replace(/#\{msg\}/g, cm.message));
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
				addChatTab(cm.scope, cm.scopeName);
			}
			if (m.mode == "accept") {
				$('#chat-msg-id-' + cm.id).remove();
			}
			msg.append(clearBlock);
			$('#' + cm.scope).append(msg);
		}
		if (msg[0]) {
			msg[0].scrollIntoView();
		}
		$('.messageArea').emoticonize();
	}
}
