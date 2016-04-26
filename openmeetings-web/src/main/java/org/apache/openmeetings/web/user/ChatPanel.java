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
package org.apache.openmeetings.web.user;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getRoomUsers;
import static org.apache.openmeetings.web.app.Application.getUserRooms;
import static org.apache.openmeetings.web.app.Application.isUserInRoom;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.openmeetings.web.util.ProfileImageResourceReference.getUrl;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.plugins.emoticons.EmoticonsBehavior;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;

@AuthorizeInstantiation({"Dashboard", "Room"})
public class ChatPanel extends BasePanel {
	private static final Logger log = Red5LoggerFactory.getLogger(ChatPanel.class, webAppRootKey);
	private static final long serialVersionUID = 1L;
	private static final String ID_TAB_PREFIX = "chatTab-";
	private static final String ID_USER_PREFIX = ID_TAB_PREFIX + "u";
	public static final String ID_ROOM_PREFIX = ID_TAB_PREFIX + "r";
	private static final String ID_ALL = ID_TAB_PREFIX + "all";
	private static final String PARAM_MSG_ID = "msgid";
	private static final String PARAM_ROOM_ID = "roomid";
	private final AbstractDefaultAjaxBehavior acceptMessage = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				long msgId = getRequest().getRequestParameters().getParameterValue(PARAM_MSG_ID).toLong(); 
				long roomId = getRequest().getRequestParameters().getParameterValue(PARAM_ROOM_ID).toLong();
				ChatDao dao = getBean(ChatDao.class);
				ChatMessage m = dao.get(msgId);
				if (m.isNeedModeration() && isModerator(getUserId(), roomId)) {
					m.setNeedModeration(false);
					dao.update(m);
					sendRoom(m, getMessage(Arrays.asList(m)).put("mode",  "accept").toString());
				} else {
					log.error("It seems like we are being hacked!!!!");
				}
			} catch (Exception e) {
				log.error("Unexpected exception while accepting chat message", e);
			}
		}
	};
	
	private static JSONObject setScope(JSONObject o, ChatMessage m, long curUserId) {
		String scope, scopeName;
		if (m.getToUser() != null) {
			User u = curUserId == m.getToUser().getId() ? m.getFromUser() : m.getToUser();
			scope = ID_USER_PREFIX + u.getId();
			scopeName = String.format("%s %s", u.getFirstname(), u.getLastname());
		} else if (m.getToRoom() != null) {
			scope = ID_ROOM_PREFIX + m.getToRoom().getId();
			scopeName = String.format("%s %s", Application.getString(406), m.getToRoom().getId());
			o.put("needModeration", m.isNeedModeration());
		} else {
			scope = ID_ALL;
			scopeName = Application.getString(1494);
		}
		return o.put("scope", scope).put("scopeName", scopeName);
	}
	
	public JSONObject getMessage(List<ChatMessage> list) throws JSONException {
		return getMessage(getUserId(), list);
	}
	
	private JSONObject getMessage(long curUserId, List<ChatMessage> list) throws JSONException {
		JSONArray arr = new JSONArray();
		for (ChatMessage m : list) {
			String smsg = m.getMessage();
			smsg = smsg == null ? smsg : " " + smsg.replaceAll("&nbsp;", " ") + " ";
			arr.put(setScope(new JSONObject(), m, curUserId)
				.put("id", m.getId())
				.put("message", smsg)
				.put("from", new JSONObject()
						.put("id", m.getFromUser().getId())
						.put("name", m.getFromUser().getFirstname() + " " + m.getFromUser().getLastname())
						.put("img", getUrl(getRequestCycle(), m.getFromUser().getId()))

					)
				.put("actions", curUserId == m.getFromUser().getId() ? "short" : "full")
				.put("sent", getDateFormat().format(m.getSent())));
		}
		return new JSONObject()
			.put("type", "chat")
			.put("msg", arr);
	}

	public ChatPanel(String id) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);

		add(acceptMessage, new Behavior() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				ChatDao dao = getBean(ChatDao.class);
				try {				
					//FIXME limited count should be loaded with "earlier" link
					List<ChatMessage> list = new ArrayList<ChatMessage>(dao.getGlobal(0, 30));
					for(Long roomId : getUserRooms(getUserId())) {
						Room r = getBean(RoomDao.class).get(roomId);
						list.addAll(dao.getRoom(roomId, 0, 30, !r.isChatModerated() || isModerator(getUserId(), roomId)));
					}
					Calendar c = WebSession.getCalendar();
					c.add(Calendar.HOUR_OF_DAY, -1);
					list.addAll(dao.getUserRecent(getUserId(), c.getTime(), 0, 30));
					if (list.size() > 0) {
						StringBuilder sb = new StringBuilder();
						sb.append("addChatMessage(").append(getMessage(list).toString()).append(");");
						response.render(OnDomReadyHeaderItem.forScript(sb.toString()));
					}
				} catch (JSONException e) {
					
				}
				super.renderHead(component, response);
			}
		});
		add(new EmoticonsBehavior(".messageArea"));
		add(new ChatForm("sendForm"));
	}

	public void roomEnter(Room r, AjaxRequestTarget target) {
		if (r.isChatHidden()) {
			target.add(setVisible(false));
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("$(function() {");
		sb.append(String.format("addChatTab('%1$s%2$d', '%3$s %2$d');", ID_ROOM_PREFIX, r.getId(), Application.getString(406)));
		sb.append(r.isChatOpened() ? "openChat();" : "closeChat();");
		List<ChatMessage> list = getBean(ChatDao.class).getRoom(r.getId(), 0, 30, !r.isChatModerated() || isModerator(getUserId(), r.getId()));
		if (list.size() > 0) {
			sb.append("addChatMessage(").append(getMessage(list).toString()).append(");");
		}
		sb.append("});");
		target.appendJavaScript(sb);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ChatPanel.class, "chat.js"))));
		response.render(CssHeaderItem.forUrl("css/chat.css"));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("acceptMessage", acceptMessage, explicit(PARAM_ROOM_ID), explicit(PARAM_MSG_ID)), "acceptMessage")));
	}
	
	private static void sendRoom(ChatMessage m, String msg) {
		IWebSocketConnectionRegistry reg = WebSocketSettings.Holder.get(Application.get()).getConnectionRegistry();
		for (Client c : getRoomUsers(m.getToRoom().getId())) {
			try {
				if (!m.isNeedModeration() || (m.isNeedModeration() && c.hasRight(Client.Right.moderator))) {
					IWebSocketConnection con = reg.getConnection(Application.get(), c.getSessionId(), new PageIdKey(c.getPageId()));
					if (con != null) {
						con.sendMessage(msg);
					}
				}
			} catch (Exception e) {
				log.error("Error while sending message to room", e);
			}
		}
	}
	
	private class ChatForm extends Form<Void> {
		private static final long serialVersionUID = 1L;
		private final ChatToolbar toolbar = new ChatToolbar("toolbarContainer");
		private final WysiwygEditor chatMessage = new WysiwygEditor("chatMessage", Model.of(""), toolbar);
		private final HiddenField<String> activeTab = new HiddenField<String>("activeTab", Model.of(""));
		
		ChatForm(String id) {
			super(id);
			add(toolbar
				, activeTab
				, chatMessage.setOutputMarkupId(true)
				, new AjaxButton("send") {
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
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
						String msg = getMessage(Arrays.asList(m)).toString();
						if (m.getToRoom() != null) {
							sendRoom(m, msg);
						} else if (m.getToUser() != null) {
							IWebSocketConnectionRegistry reg = WebSocketSettings.Holder.get(Application.get()).getConnectionRegistry();
							for (Client c : Application.getClients(getUserId())) {
								try {
									IWebSocketConnection con = reg.getConnection(Application.get(), c.getSessionId(), new PageIdKey(c.getPageId()));
									if (con != null) {
										con.sendMessage(msg);
									}
								} catch (Exception e) {
									log.error("Error while sending message to room", e);
								}
							}
							msg = getMessage(m.getToUser().getId(), Arrays.asList(m)).toString();
							for (Client c : Application.getClients(m.getToUser().getId())) {
								try {
									IWebSocketConnection con = reg.getConnection(Application.get(), c.getSessionId(), new PageIdKey(c.getPageId()));
									if (con != null) {
										con.sendMessage(msg);
									}
								} catch (Exception e) {
									log.error("Error while sending message to room", e);
								}
							}
						} else {
							IWebSocketConnectionRegistry reg = WebSocketSettings.Holder.get(getApplication()).getConnectionRegistry();
							for (IWebSocketConnection c : reg.getConnections(getApplication())) {
								try {
									c.sendMessage(msg);
								} catch(Exception e) {
									log.error("Error while sending message", e);
								}
							}
						}
						chatMessage.setDefaultModelObject("");
						target.add(chatMessage);
					};
				});
		}
	}
}
