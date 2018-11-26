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

import static org.apache.openmeetings.core.util.WebSocketHelper.ID_ALL;
import static org.apache.openmeetings.core.util.WebSocketHelper.ID_ROOM_PREFIX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_CHAT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.openmeetings.web.util.ProfileImageResourceReference.getUrl;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;

public class Chat extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(Chat.class, getWebAppRootKey());
	private static final String PARAM_MSG_ID = "msgid";
	private static final String PARAM_ROOM_ID = "roomid";
	private static final String PARAM_TYPE = "type";
	private boolean showDashboardChat = getBean(ConfigurationDao.class).getBool(CONFIG_DASHBOARD_SHOW_CHAT, true);
	private final AbstractDefaultAjaxBehavior chatActivity = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				String type = getRequest().getRequestParameters().getParameterValue(PARAM_TYPE).toString(null);
				long roomId = getRequest().getRequestParameters().getParameterValue(PARAM_ROOM_ID).toLong();
				if ("accept".equals(type)) {
					long msgId = getRequest().getRequestParameters().getParameterValue(PARAM_MSG_ID).toLong();
					ChatDao dao = getBean(ChatDao.class);
					ChatMessage m = dao.get(msgId);
					if (m.isNeedModeration() && isModerator(getUserId(), roomId)) {
						m.setNeedModeration(false);
						dao.update(m);
						WebSocketHelper.sendRoom(m, getMessage(Arrays.asList(m)).put("mode",  "accept"));
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

	public Chat(String id) {
		super(id);
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
		final User curUser = c == null ? getBean(UserDao.class).get(getUserId()) : c.getUser();
		return getMessage(curUser, list);
	}

	public static JSONObject getMessage(User curUser, List<ChatMessage> list) {
		return WebSocketHelper.getMessage(curUser, list, (o, u) -> o.put("img", getUrl(RequestCycle.get(), u)));
	}

	public CharSequence getReinit() {
		StringBuilder sb = new StringBuilder("Chat.reinit(")
				.append(new JSONObject()
						.put("userId", getUserId())
						.put("all", getString("1494"))
						.put("room", getString("406"))
						.put("sendOnEnter", OpenmeetingsVariables.getChatSenndOnEnter()).toString())
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
		List<ChatMessage> list = getBean(ChatDao.class).getRoom(r.getId(), 0, 30, !r.isChatModerated() || isModerator(getUserId(), r.getId()));
		if (!list.isEmpty()) {
			sb.append("Chat.addMessage(").append(getMessage(list).toString()).append(");");
		}
		return sb;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Chat.class, "chat.js"))));
		response.render(new PriorityHeaderItem(getNamedFunction("chatActivity", chatActivity, explicit(PARAM_TYPE), explicit(PARAM_ROOM_ID), explicit(PARAM_MSG_ID))));

		if (showDashboardChat) {
			ChatDao dao = getBean(ChatDao.class);
			StringBuilder sb = new StringBuilder(getReinit());
			List<ChatMessage> list = new ArrayList<>(dao.getGlobal(0, 30));
			list.addAll(dao.getUserRecent(getUserId(), Date.from(Instant.now().minus(Duration.ofHours(1L))), 0, 30));
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
