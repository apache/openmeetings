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
import static org.apache.openmeetings.core.util.ChatWebSocketHelper.ID_USER_PREFIX;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;

import java.util.Date;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import org.apache.openmeetings.core.util.ChatWebSocketHelper;
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
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;

import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;
import org.wicketstuff.jquery.ui.plugins.wysiwyg.WysiwygEditor;

import jakarta.inject.Inject;

public class ChatForm extends Form<Void> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ChatForm.class);
	private final HiddenField<String> activeTab = new HiddenField<>("activeTab", Model.of(""));

	@Inject
	private ClientManager cm;
	@Inject
	private ChatDao chatDao;
	@Inject
	private UserDao userDao;
	@Inject
	private RoomDao roomDao;

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
					final ChatMessage m = new ChatMessage();
					m.setMessage(txt);
					m.setSent(new Date());
					m.setFromUser(userDao.get(getUserId()));
					m.setFromName(getClient().getUser().getDisplayName());
					if (!process(
							() -> getChat().isShowDashboardChat()
							, r -> {
								if (cm.isInRoom(r.getId(), getUserId())) {
									m.setToRoom(r);
								} else {
									log.error("It seems like we are being hacked!!!!");
									return false;
								}
								m.setNeedModeration(r.isChatModerated() && !isModerator(cm, m.getFromUser().getId(), r.getId()));
								return true;
							}, u -> {
								m.setToUser(u);
								return true;
							}))
					{
						return;
					}
					chatDao.update(m);
					JSONObject msg = getChat().getMessage(List.of(m));
					if (m.getToRoom() != null) {
						ChatWebSocketHelper.sendRoom(m, msg);
					} else if (m.getToUser() != null) {
						ChatWebSocketHelper.sendUser(getUserId(), m, msg);
						msg = Chat.getMessage(m.getToUser(), List.of(m));
						ChatWebSocketHelper.sendUser(m.getToUser().getId(), m, msg);
					} else {
						ChatWebSocketHelper.sendAll(m, msg);
					}
					chatMessage.setDefaultModelObject("");
					target.appendJavaScript("Chat.clean();");
				}
			});
	}

	private Client getClient() {
		return findParent(MainPanel.class).getClient();
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
				Room r = roomDao.get(Long.parseLong(scope.substring(ID_ROOM_PREFIX.length())));
				if (r != null) {
					return processRoom.test(r);
				}
			} else if (scope.startsWith(ID_USER_PREFIX)) {
				User u = userDao.get(Long.parseLong(scope.substring(ID_USER_PREFIX.length())));
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
