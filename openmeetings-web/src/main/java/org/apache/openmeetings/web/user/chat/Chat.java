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

import static org.apache.openmeetings.core.util.ChatWebSocketHelper.ID_ALL;
import static org.apache.openmeetings.core.util.ChatWebSocketHelper.ID_ROOM_PREFIX;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_CHAT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isChatSendOnEnter;
import static org.apache.openmeetings.web.util.ProfileImageResourceReference.getUrl;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.core.util.ChatWebSocketHelper;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;
import org.wicketstuff.jquery.ui.settings.JQueryUILibrarySettings;

import jakarta.inject.Inject;

public class Chat extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(Chat.class);
	private static final String PARAM_MSG_ID = "msgId";
	private static final String PARAM_ROOM_ID = "roomId";
	private static final String PARAM_TYPE = "type";
	private boolean showDashboardChat;
	private final AbstractDefaultAjaxBehavior chatActivity = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				String type = getRequest().getRequestParameters().getParameterValue(PARAM_TYPE).toString(null);
				long roomId = getRequest().getRequestParameters().getParameterValue(PARAM_ROOM_ID).toLong();
				if ("accept".equals(type)) {
					long msgId = getRequest().getRequestParameters().getParameterValue(PARAM_MSG_ID).toLong();
					ChatMessage m = chatDao.get(msgId);
					if (m.isNeedModeration() && isModerator(cm, getUserId(), roomId)) {
						m.setNeedModeration(false);
						chatDao.update(m);
						ChatWebSocketHelper.sendRoom(m, getMessage(List.of(m)).put("mode",  "accept"));
					} else {
						log.error("It seems like we are being hacked!!!!");
					}
				} else if (type != null && type.indexOf("typing") > -1) {
					WebSocketHelper.sendRoom(roomId
							, new JSONObject().put(PARAM_TYPE, "typing")
									.put("active", type.indexOf("start") > -1)
									.put("uid", getUid()));
				}
			} catch (Exception e) {
				log.error("Unexpected exception while accepting chat message", e);
			}
		}

		private String getUid() {
			return getClient().getUid();
		}
	};

	@Inject
	private ClientManager cm;
	@Inject
	private ConfigurationDao cfgDao;
	@Inject
	private ChatDao chatDao;
	@Inject
	private UserDao userDao;

	public Chat(String id) {
		super(id);
		showDashboardChat = cfgDao.getBool(CONFIG_DASHBOARD_SHOW_CHAT, true);
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);
	}

	@Override
	protected void onInitialize() {
		add(chatActivity);
		add(new ChatForm("sendForm"));
		super.onInitialize();
	}

	private Client getClient() {
		return findParent(MainPanel.class).getClient();
	}

	public JSONObject getMessage(List<ChatMessage> list) {
		final Client c = getClient();
		final User curUser = c == null ? userDao.get(getUserId()) : c.getUser();
		return getMessage(curUser, list);
	}

	public static JSONObject getMessage(User curUser, List<ChatMessage> list) {
		return ChatWebSocketHelper.getMessage(curUser, list, (o, u) -> o.put("img", getUrl(RequestCycle.get(), u)));
	}

	public CharSequence getReinit() {
		StringBuilder sb = new StringBuilder("Chat.reinit(")
				.append(new JSONObject()
						.put("userId", getUserId())
						.put("all", getString("1494"))
						.put("room", getString("406"))
						.put("sendOnEnter", isChatSendOnEnter()).toString())
				.append("); ");
		return processGlobal(sb);
	}

	public CharSequence processGlobal(StringBuilder sb) {
		if (!showDashboardChat) {
			sb.append(String.format("Chat.removeTab('%s');", ID_ALL));
		}
		return sb;
	}

	public CharSequence addRoom(Room r) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Chat.addTab('%1$s%2$d', '%3$s %2$d');", ID_ROOM_PREFIX, r.getId(), getString("406")));
		List<ChatMessage> list = chatDao.getRoom(r.getId(), 0, 30, !r.isChatModerated() || isModerator(cm, getUserId(), r.getId()));
		if (!list.isEmpty()) {
			sb.append("Chat.addMessage(").append(getMessage(list).toString()).append(");");
		}
		return sb;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(new JavaScriptUrlReferenceHeaderItem("js/chat.js", "om-chat") {
			private static final long serialVersionUID = 1L;

			@Override
			public List<HeaderItem> getDependencies() {
				return List.of(
						new PriorityHeaderItem(JavaScriptHeaderItem.forScript("const bstooltip = jQuery.fn.tooltip;", "preserve-bs-tooltip"))
						, new PriorityHeaderItem(JavaScriptHeaderItem.forReference(JQueryUILibrarySettings.get().getJavaScriptReference()))
						, new PriorityHeaderItem(JavaScriptHeaderItem.forScript("jQuery.fn.tooltip = bstooltip;", "restore-bs-tooltip"))
						);
			}
		}));
		response.render(new PriorityHeaderItem(getNamedFunction("chatActivity", chatActivity, explicit(PARAM_TYPE), explicit(PARAM_ROOM_ID), explicit(PARAM_MSG_ID))));

		if (showDashboardChat) {
			StringBuilder sb = new StringBuilder(getReinit());
			List<ChatMessage> list = new ArrayList<>(chatDao.getGlobal(0, 30));
			list.addAll(chatDao.getUserRecent(getUserId(), Date.from(Instant.now().minus(Duration.ofHours(1L))), 0, 30));
			if (!list.isEmpty()) {
				sb.append("Chat.addMessage(").append(getMessage(list).toString()).append(");");
			}
			response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
		}
	}

	public boolean isShowDashboardChat() {
		return showDashboardChat;
	}
}
