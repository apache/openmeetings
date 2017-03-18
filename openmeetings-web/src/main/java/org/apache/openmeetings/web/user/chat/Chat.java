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
import static org.apache.openmeetings.core.util.WebSocketHelper.ID_USER_PREFIX;
import static org.apache.openmeetings.db.util.AuthLevelUtil.hasAdminLevel;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getUserRooms;
import static org.apache.openmeetings.web.app.Application.isUserInRoom;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;
import static org.apache.openmeetings.web.user.chat.EmotionsResources.EMOTIONS_CSS_REFERENCE;
import static org.apache.openmeetings.web.user.chat.EmotionsResources.EMOTIONS_JS_REFERENCE;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.openmeetings.web.util.ProfileImageResourceReference.getUrl;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.core.remote.MobileService;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;

public class Chat extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(Chat.class, webAppRootKey);
	private static final String PARAM_MSG_ID = "msgid";
	private static final String PARAM_ROOM_ID = "roomid";
	private static final String PARAM_TYPE = "type";
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
							, new JSONObject().put("type", "typing")
									.put("active", type.indexOf("start") > -1)
									.put("uid", getUid())
									.toString());
				}
			} catch (Exception e) {
				log.error("Unexpected exception while accepting chat message", e);
			}
		}
	};

	private String getUid() {
		return findParent(MainPanel.class).getClient().getUid(); //TODO HACK
	}

	public JSONObject getMessage(List<ChatMessage> list) {
		return getMessage(getUserId(), list);
	}

	public JSONObject getMessage(Long userId, List<ChatMessage> list) {
		return WebSocketHelper.getMessage(userId, list, getDateFormat(), (o, u) -> o.put("img", getUrl(getRequestCycle(), u)));
	}

	public Chat(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);

		add(chatActivity);
		add(new ChatForm("sendForm"));
	}

	public static CharSequence getReinit() {
		StringBuilder sb = new StringBuilder("Chat.reinit(");
		sb.append('\'').append(Application.getString(1494)).append('\'')
				.append(',').append('\'').append(Application.getString(406)).append('\'');
		return sb.append("); ");
	}

	public CharSequence addRoom(Room r) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Chat.addTab('%1$s%2$d', '%3$s %2$d');", ID_ROOM_PREFIX, r.getId(), Application.getString(406)));
		List<ChatMessage> list = getBean(ChatDao.class).getRoom(r.getId(), 0, 30, !r.isChatModerated() || isModerator(getUserId(), r.getId()));
		if (list.size() > 0) {
			sb.append("Chat.addMessage(").append(getMessage(list).toString()).append(");");
		}
		return sb;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(EMOTIONS_JS_REFERENCE)));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Chat.class, "chat.js"))));
		response.render(CssHeaderItem.forReference(EMOTIONS_CSS_REFERENCE));
		response.render(CssHeaderItem.forUrl("css/chat.css"));
		response.render(new PriorityHeaderItem(getNamedFunction("chatActivity", chatActivity, explicit(PARAM_TYPE), explicit(PARAM_ROOM_ID), explicit(PARAM_MSG_ID))));

		ChatDao dao = getBean(ChatDao.class);
		//FIXME limited count should be loaded with "earlier" link
		List<ChatMessage> list = new ArrayList<>(dao.getGlobal(0, 30));
		for(Long roomId : getUserRooms(getUserId())) {
			Room r = getBean(RoomDao.class).get(roomId);
			list.addAll(dao.getRoom(roomId, 0, 30, !r.isChatModerated() || isModerator(getUserId(), roomId)));
		}
		list.addAll(dao.getUserRecent(getUserId(), Date.from(Instant.now().minus(Duration.ofHours(1L))), 0, 30));
		StringBuilder sb = new StringBuilder(getReinit());
		if (list.size() > 0) {
			sb.append("Chat.addMessage(").append(getMessage(list).toString()).append(");");
		}
		response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
	}

	private class ChatForm extends Form<Void> {
		private static final long serialVersionUID = 1L;
		private final ChatToolbar toolbar = new ChatToolbar("toolbarContainer");
		private final WysiwygEditor chatMessage = new WysiwygEditor("chatMessage", Model.of(""), toolbar);
		private final HiddenField<String> activeTab = new HiddenField<>("activeTab", Model.of(""));

		ChatForm(String id) {
			super(id);
			add(toolbar
				, activeTab
				, chatMessage.setOutputMarkupId(true)
				, new AjaxButton("send") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						ChatDao dao = getBean(ChatDao.class);
						ChatMessage m = new ChatMessage();
						m.setMessage(chatMessage.getDefaultModelObjectAsString());
						m.setSent(new Date());
						m.setFromUser(getBean(UserDao.class).get(getUserId()));
						try {
							String scope = activeTab.getModelObject();
							if (scope != null) {
								if (ID_ALL.equals(scope)) {
									//we done
								} else if (scope.startsWith(ID_ROOM_PREFIX)) {
									Room r = getBean(RoomDao.class).get(Long.parseLong(scope.substring(ID_ROOM_PREFIX.length())));
									if (isUserInRoom(r.getId(), getUserId())) {
										m.setToRoom(r);
									} else {
										log.error("It seems like we are being hacked!!!!");
										return;
									}
									m.setNeedModeration(r.isChatModerated() && !isModerator(m.getFromUser().getId(), r.getId()));
								} else if (scope.startsWith(ID_USER_PREFIX)) {
									User u = getBean(UserDao.class).get(Long.parseLong(scope.substring(ID_USER_PREFIX.length())));
									m.setToUser(u);
								}
							}
						} catch (Exception e) {
							//no-op
						}
						dao.update(m);
						JSONObject msg = getMessage(Arrays.asList(m));
						if (m.getToRoom() != null) {
							getBean(MobileService.class).sendChatMessage(getUid(), m, getDateFormat()); //let's send to mobile users
							WebSocketHelper.sendRoom(m, msg);
						} else if (m.getToUser() != null) {
							WebSocketHelper.sendUser(getUserId(), msg.toString());
							msg = getMessage(m.getToUser().getId(), Arrays.asList(m));
							WebSocketHelper.sendUser(m.getToUser().getId(), msg.toString());
						} else {
							WebSocketHelper.sendAll(msg.toString());
						}
						chatMessage.setDefaultModelObject("");
						target.add(chatMessage);
					};
				});
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			ConfirmableAjaxBorder delBtn = new ConfirmableAjaxBorder("ajax-cancel-button", getString("80"), getString("832"), this) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onError(AjaxRequestTarget target) {
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					ChatDao dao = getBean(ChatDao.class);
					String scope = activeTab.getModelObject();
					boolean clean = false;
					try {
						if (scope == null || ID_ALL.equals(scope)) {
							scope = ID_ALL;
							dao.deleteGlobal();
							clean = true;
						} else if (scope.startsWith(ID_ROOM_PREFIX)) {
							Room r = getBean(RoomDao.class).get(Long.parseLong(scope.substring(ID_ROOM_PREFIX.length())));
							if (r != null) {
								dao.deleteRoom(r.getId());
								clean = true;
							}
						} else if (scope.startsWith(ID_USER_PREFIX)) {
							User u = getBean(UserDao.class).get(Long.parseLong(scope.substring(ID_USER_PREFIX.length())));
							if (u != null) {
								dao.deleteUser(u.getId());
								clean = true;
							}
						}
					} catch (Exception e) {
						//no-op
					}
					if (clean) {
						target.appendJavaScript("$('#" + scope + "').html('')");
					}
				}
			};
			add(delBtn.setVisible(hasAdminLevel(getRights())));
		}
	}
}
