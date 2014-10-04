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

import static org.apache.commons.lang3.StringEscapeUtils.unescapeXml;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getUserRooms;
import static org.apache.openmeetings.web.app.Application.isUserInRoom;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Calendar;
import java.util.Date;

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
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.Behavior;
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
import org.apache.wicket.request.resource.ResourceReference;
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
	
	private JSONObject setScope(JSONObject o, ChatMessage m, long curUserId) {
		String scope, scopeName;
		if (m.getToUser() != null) {
			User u = curUserId == m.getToUser().getId() ? m.getFromUser() : m.getToUser();
			scope = ID_USER_PREFIX + u.getId();
			scopeName = String.format("%s %s", u.getFirstname(), u.getLastname());
		} else if (m.getToRoom() != null) {
			scope = ID_ROOM_PREFIX + m.getToRoom().getId();
			scopeName = String.format("%s %s", WebSession.getString(406), m.getToRoom().getId());
		} else {
			scope = ID_ALL;
			scopeName = WebSession.getString(1494);
		}
		return o.put("scope", scope).put("scopeName", scopeName);
	}
	
	private JSONObject getMessage(ChatMessage m) throws JSONException {
		return getMessage(m, getUserId());
	}
	
	private JSONObject getMessage(ChatMessage m, long curUserId) throws JSONException {
		String smsg = m.getMessage();
		smsg = smsg == null ? smsg : " " + smsg.replaceAll("&nbsp;", " ") + " ";
		return new JSONObject()
			.put("type", "chat")
			.put("msg", setScope(new JSONObject(), m, curUserId)
					.put("id", m.getId())
					.put("message", smsg)
					.put("from", m.getFromUser().getFirstname() + " " + m.getFromUser().getLastname())
					.put("sent", getDateFormat().format(m.getSent())));
	}

	public ChatPanel(String id) {
		super(id);
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);

		add(new Behavior() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				ChatDao dao = getBean(ChatDao.class);
				try {				
					StringBuilder sb = new StringBuilder();
					//FIXME limited count should be loaded with "earlier" link
					for (ChatMessage m : dao.getGlobal(0, 30)) {
						sb.append("addChatMessageInternal(").append(getMessage(m).toString()).append(");");
					}
					for(Long roomId : getUserRooms(getUserId())) {
						for (ChatMessage m : dao.getRoom(roomId, 0, 30)) {
							sb.append("addChatMessageInternal(").append(getMessage(m).toString()).append(");");
						}
					}
					Calendar c = WebSession.getCalendar();
					c.add(Calendar.HOUR_OF_DAY, -1);
					for (ChatMessage m : dao.getUserRecent(getUserId(), c.getTime(), 0, 30)) {
						sb.append("addChatMessageInternal(").append(getMessage(m).toString()).append(");");
					}
					if (sb.length() > 0) {
						sb.append("$('.messageArea').emoticonize();");
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

	public void roomEnter(long roomId, AjaxRequestTarget target) {
		Room r = getBean(RoomDao.class).get(roomId);
		if (r.isHideChat()) {
			target.add(setVisible(false));
			return;
		}
		StringBuilder sb = new StringBuilder(String.format("addChatTab('%1$s%2$d', '%3$s %2$d');", ID_ROOM_PREFIX, roomId, WebSession.getString(406)));
		boolean added = false;
		sb.append(r.isChatOpened() ? "openChat();" : "closeChat();");
		for (ChatMessage m : getBean(ChatDao.class).getRoom(roomId, 0, 30)) {
			added = true;
			sb.append("addChatMessageInternal(").append(getMessage(m).toString()).append(");");
		}
		if (added) {
			sb.append("$('.messageArea').emoticonize();");
		}
		target.appendJavaScript(sb);
	}
	
	private ResourceReference newResourceReference() {
		return new JavaScriptResourceReference(ChatPanel.class, "chat.js");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(newResourceReference())));
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
						m.setMessage(unescapeXml(chatMessage.getDefaultModelObjectAsString()));
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
										log.error("It seems like we being hacked!!!!");
										return;
									}
								} else if (scope.startsWith(ID_USER_PREFIX)) {
									User u = getBean(UserDao.class).get(Long.parseLong(scope.substring(ID_USER_PREFIX.length())));
									m.setToUser(u);
								}
							}
						} catch (Exception e) {
							//no-op
						}
						dao.update(m);
						String msg = getMessage(m).toString();
						if (m.getToRoom() != null) {
							RoomPanel.sendRoom(m.getToRoom().getId(), msg);
						} else if (m.getToUser() != null) {
							IWebSocketConnectionRegistry reg = WebSocketSettings.Holder.get(Application.get()).getConnectionRegistry();
							for (Client c : Application.getClients(getUserId())) {
								try {
									reg.getConnection(Application.get(), c.getSessionId(), new PageIdKey(c.getPageId())).sendMessage(msg);
								} catch (Exception e) {
									log.error("Error while sending message to room", e);
								}
							}
							msg = getMessage(m, m.getToUser().getId()).toString();
							for (Client c : Application.getClients(m.getToUser().getId())) {
								try {
									reg.getConnection(Application.get(), c.getSessionId(), new PageIdKey(c.getPageId())).sendMessage(msg);
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
