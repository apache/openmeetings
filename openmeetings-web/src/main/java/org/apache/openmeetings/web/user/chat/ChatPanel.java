/*
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
package org.apache.openmeetings.web.user.chat;

import static org.apache.openmeetings.core.util.ChatWebSocketHelper.ID_ROOM_PREFIX;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;

@AuthorizeInstantiation({"DASHBOARD", "ROOM"})
public class ChatPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final Chat chat;

	public ChatPanel(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);

		add(chat = new Chat("chat"));
	}

	public void roomEnter(Room r, IPartialPageRequestHandler handler) {
		if (r.isHidden(RoomElement.CHAT)) {
			toggle(handler, false);
			return;
		}
		StringBuilder sb = new StringBuilder("$(function() {");
		if (!chat.isShowDashboardChat()) {
			sb.append("$('#chatPanel').show();");
		}
		sb.append("Chat.setRoomMode(true);")
			.append(chat.addRoom(r))
			.append("Chat.").append(r.isChatOpened() ? "setOpened" : "close").append("();");
		chat.processGlobal(sb);
		sb.append("});");
		handler.appendJavaScript(sb);
	}

	public void roomExit(Room r, IPartialPageRequestHandler handler) {
		if (r.isHidden(RoomElement.CHAT)) {
			return;
		}
		handler.appendJavaScript(String.format("if (typeof(Chat) === 'object') { Chat.removeTab('%1$s%2$d'); }", ID_ROOM_PREFIX, r.getId()));
		StringBuilder sb = new StringBuilder("$(function() {")
				.append("Chat.setRoomMode(false);");
		if (!chat.isShowDashboardChat()) {
			sb.append("$('#chatPanel').hide();");
		}
		sb.append("});");
		handler.appendJavaScript(sb);
	}

	public void toggle(IPartialPageRequestHandler handler, boolean visible) {
		setVisible(visible);
		if (handler != null) {
			handler.add(this);
			if (visible) {
				handler.appendJavaScript(chat.getReinit());
			}
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		if (!chat.isShowDashboardChat()) {
			response.render(OnDomReadyHeaderItem.forScript("$('#chatPanel').hide();"));
		}
	}
}

