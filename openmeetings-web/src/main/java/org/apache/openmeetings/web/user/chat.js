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
$(function() {
	Wicket.Event.subscribe("/websocket/message", function(jqEvent, msg) {
		var m = jQuery.parseJSON(msg);
		if (m) {
			switch(m.type) {
				case "chat":
					addChatMessage(m);
					break;
				case "room":
					if (typeof(roomMessage) == "function") {
						roomMessage(m);
					}
					break;
			}
		}
	});
});
function toggleChat() {
	var chat = $('#chat');
	$('#chat #controlBlock #control')
		.removeClass('ui-icon-carat-1-' + (chat.height() < 20 ? 'n' : 's'))
		.addClass('ui-icon-carat-1-' + (chat.height() < 20 ? 's' : 'n'));
	chat.animate({ height: chat.height() < 20 ? "320px" : "16px" }, 1000);
}
function addChatMessageInternal(m) {
	if (m && m.type == "chat") {
		var msg = $('<div><span class="from">' + m.msg.from + '</span><span class="date">'
				+ m.msg.sent + '</span>' + m.msg.message + '</div>');
		$('#messageArea').append(msg);
		msg[0].scrollIntoView();
	}
}
function addChatMessage(m) {
	if (m && m.type == "chat") {
		addChatMessageInternal(m);
		$('#messageArea').emoticonize();
	}
}
