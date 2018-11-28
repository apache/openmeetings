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
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;

import java.util.Arrays;
import java.util.Date;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import org.apache.openmeetings.core.remote.MobileService;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
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
			, activeTab.add(new AjaxFormSubmitBehavior(this, "change") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					toolbar.update(target);
				}
			})
			, chatMessage.setOutputMarkupId(true)
			, new AjaxButton("send") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
					super.updateAjaxAttributes(attributes);
					attributes.getAjaxCallListeners().add(new IAjaxCallListener() {
						@Override
						public CharSequence getPrecondition(Component component) {
							return "return Chat.validate();";
						}
					});
				}

				private Chat getChat() {
					return findParent(Chat.class);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					final String txt = chatMessage.getDefaultModelObjectAsString();
					if (Strings.isEmpty(txt)) {
						return;
					}
					ChatDao dao = getBean(ChatDao.class);
					final ChatMessage m = new ChatMessage();
					m.setMessage(txt);
					m.setSent(new Date());
					m.setFromUser(getBean(UserDao.class).get(getUserId()));
					m.setFromName(getClient().getUser().getDisplayName());
					if (!process(
							() -> getChat().isShowDashboardChat()
							, r -> {
								if (getBean(ClientManager.class).isInRoom(r.getId(), getUserId())) {
									m.setToRoom(r);
								} else {
									log.error("It seems like we are being hacked!!!!");
									return false;
								}
								m.setNeedModeration(r.isChatModerated() && !isModerator(m.getFromUser().getId(), r.getId()));
								return true;
							}, u -> {
								m.setToUser(u);
								return true;
							}))
					{
						return;
					};
					dao.update(m);
					JSONObject msg = getChat().getMessage(Arrays.asList(m));
					if (m.getToRoom() != null) {
						getBean(MobileService.class).sendChatMessage(getUid(), m, getDateFormat()); //let's send to mobile users
						WebSocketHelper.sendRoom(m, msg);
					} else if (m.getToUser() != null) {
						WebSocketHelper.sendUser(getUserId(), msg.toString());
						msg = Chat.getMessage(m.getToUser(), Arrays.asList(m));
						WebSocketHelper.sendUser(m.getToUser().getId(), msg.toString());
					} else {
						WebSocketHelper.sendAll(msg.toString());
					}
					chatMessage.setDefaultModelObject("");
					target.appendJavaScript("Chat.clean();");
				};
			});
	}

	private Client getClient() {
		return findParent(MainPanel.class).getClient();
	}

	private String getUid() {
		return getClient().getUid();
	}

	public String getScope() {
		return activeTab.getModelObject();
	}

	boolean process(BooleanSupplier processAll, Predicate<Room> processRoom, Predicate<User> processUser) {
		try {
			final String scope = getScope();
			if (Strings.isEmpty(scope) || ID_ALL.equals(scope)) {
				return processAll.getAsBoolean();
			} else if (scope.startsWith(ID_ROOM_PREFIX)) {
				Room r = getBean(RoomDao.class).get(Long.parseLong(scope.substring(ID_ROOM_PREFIX.length())));
				if (r != null) {
					return processRoom.test(r);
				}
			} else if (scope.startsWith(ID_USER_PREFIX)) {
				User u = getBean(UserDao.class).get(Long.parseLong(scope.substring(ID_USER_PREFIX.length())));
				if (u != null) {
					return processUser.test(u);
				}
			}
		} catch (Exception e) {
			//no-op
		}
		return false;
	}
}
