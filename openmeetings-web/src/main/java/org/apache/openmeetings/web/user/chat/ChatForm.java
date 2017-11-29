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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.isUserInRoom;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;

import java.util.Arrays;
import java.util.Date;

import org.apache.openmeetings.core.remote.MobileService;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;

public class ChatForm extends Form<Void> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(ChatForm.class, getWebAppRootKey());
	private final HiddenField<String> activeTab = new HiddenField<>("activeTab", Model.of(""));

	public ChatForm(String id) {
		super(id);
		final ChatToolbar toolbar = new ChatToolbar("toolbarContainer", this);
		final WysiwygEditor chatMessage = new WysiwygEditor("chatMessage", Model.of(""), toolbar);
		add(toolbar
			, activeTab
			, chatMessage.setOutputMarkupId(true)
			, new AjaxButton("send") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					final String txt = chatMessage.getDefaultModelObjectAsString();
					if (Strings.isEmpty(txt)) {
						return;
					}
					ChatDao dao = getBean(ChatDao.class);
					ChatMessage m = new ChatMessage();
					m.setMessage(txt);
					m.setSent(new Date());
					m.setFromUser(getBean(UserDao.class).get(getUserId()));
					try {
						String scope = getScope();
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
					JSONObject msg = Chat.getMessage(Arrays.asList(m));
					if (m.getToRoom() != null) {
						getBean(MobileService.class).sendChatMessage(getUid(), m, getDateFormat()); //let's send to mobile users
						WebSocketHelper.sendRoom(m, msg);
					} else if (m.getToUser() != null) {
						WebSocketHelper.sendUser(getUserId(), msg.toString());
						msg = Chat.getMessage(m.getToUser().getId(), Arrays.asList(m));
						WebSocketHelper.sendUser(m.getToUser().getId(), msg.toString());
					} else {
						WebSocketHelper.sendAll(msg.toString());
					}
					chatMessage.setDefaultModelObject("");
					target.appendJavaScript("Chat.clean();");
				};
			});
	}

	private String getUid() {
		return findParent(MainPanel.class).getClient().getUid();
	}

	public String getScope() {
		return activeTab.getModelObject();
	}
}
