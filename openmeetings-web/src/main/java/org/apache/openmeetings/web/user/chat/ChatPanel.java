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

import static org.apache.openmeetings.core.util.WebSocketHelper.ID_ROOM_PREFIX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_CHAT;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.user.chat.Chat.getReinit;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

@AuthorizeInstantiation({"Dashboard", "Room"})
public class ChatPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private boolean showDashboardChat = getBean(ConfigurationDao.class).getConfValue(CONFIG_DASHBOARD_SHOW_CHAT, Integer.class, "1") == 1;
	private final Chat chat;

	public ChatPanel(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);

		add(chat = new Chat("chat"));
	}

	public void roomEnter(Room r, AjaxRequestTarget target) {
		if (r.isHidden(RoomElement.Chat)) {
			toggle(target, false);
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("$(function() {");
		if (!showDashboardChat) {
			sb.append("$('#chat').show();");
		}
		sb.append(chat.addRoom(r));
		sb.append(r.isChatOpened() ? "openChat();" : "closeChat();");
		sb.append("});");
		target.appendJavaScript(sb);
	}

	public void roomExit(Room r, IPartialPageRequestHandler handler) {
		if (r.isHidden(RoomElement.Chat)) {
			return;
		}
		handler.appendJavaScript(String.format("if (typeof removeChatTab == 'function') { removeChatTab('%1$s%2$d'); }", ID_ROOM_PREFIX, r.getId()));
		if (!showDashboardChat) {
			StringBuilder sb = new StringBuilder();
			sb.append("$(function() {");
			sb.append("$('#chat').hide();");
			sb.append("});");
			handler.appendJavaScript(sb);
		}
	}

	public void toggle(IPartialPageRequestHandler handler, boolean visible) {
		setVisible(visible);
		if (handler != null) {
			handler.add(this);
			if (visible) {
				handler.appendJavaScript(getReinit());
			}
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		if (!showDashboardChat) {
			StringBuilder sb = new StringBuilder();
			sb.append("$(document).ready(function(){");
			sb.append("$('#ui-id-1').hide();");
			sb.append("$('#chat').hide();");
			sb.append("});");
			response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
		}
	}
}

