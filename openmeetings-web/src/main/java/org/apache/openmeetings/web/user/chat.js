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
	, msgTemplate = "<div id='chat-msg-id-#{id}'><span class='from'>#{from}</span><span class='date'>#{sent}</span>#{msg}</div>"
	, acceptTemplate = "<span class='tick om-icon align-right clickable' data-msgid='#{msgid}' data-roomid='#{roomid}' onclick='var e=$(this);acceptMessage(e.data(\"roomid\"),e.data(\"msgid\"));e.parent().remove();'></span>"
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
});
function openChat() {
	if ($('#chatPanel').height() < 24) {
		$('#chat .control.block .ui-icon').removeClass('ui-icon-carat-1-n').addClass('ui-icon-carat-1-s');
		$('#chatPanel, #chat').animate({height: openedHeight}, 1000);
	}
}
function closeChat() {
	var chat = $('#chatPanel');
	if ($('#chatPanel').height() > 24) {
		$('#chat .control.block .ui-icon').removeClass('ui-icon-carat-1-s').addClass('ui-icon-carat-1-n');
		chat.animate({height: closedHeight}, 1000);
		$('#chatPanel, #chat').animate({height: closedHeight}, 1000);
	}
}
function toggleChat() {
	if ($('#chatPanel').height() < 24) {
		openChat();
	} else {
		closeChat();
	}
}
function activateTab(id) {
	chatTabs.tabs("option", "active", chatTabs.find('a[href="#' + id + '"]').parent().index());
}
function addChatTab(id, label) {
	if ($('#chat').length < 1 || $('#' + id).length > 0) {
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
function addChatMessage(m) {
	if ($('#chat').length > 0 && m && m.type == "chat") {
		var msg;
		for (var i = 0; i < m.msg.length; ++i) {
			var cm = m.msg[i];
			//needModeration
			msg = $(msgTemplate.replace(/#\{id\}/g, cm.id).replace(/#\{from\}/g, cm.from).replace(/#\{sent\}/g, cm.sent).replace(/#\{msg\}/g, cm.message));
			if (cm.needModeration) {
				msg.append(acceptTemplate.replace(/#\{msgid\}/g, cm.id).replace(/#\{roomid\}/g, cm.scope.substring(9)));
			}
			if (!$('#' + cm.scope).length) {
				addChatTab(cm.scope, cm.scopeName);
			}
			if (m.mode == "accept") {
				$('#chat-msg-id-' + cm.id).remove();
			}
			$('#' + cm.scope).append(msg);
		}
		if (msg[0]) {
			msg[0].scrollIntoView();
		}
		$('.messageArea').emoticonize();
	}
}
