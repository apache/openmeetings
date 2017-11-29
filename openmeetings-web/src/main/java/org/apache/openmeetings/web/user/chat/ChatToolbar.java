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
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getRights;

import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.googlecode.wicket.jquery.core.IJQueryWidget.JQueryWidget;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.IWysiwygToolbar;

/**
 * Provides a custom implementation for com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.IWysiwygToolbar suitable
 * for chat}
 */
public class ChatToolbar extends Panel implements IWysiwygToolbar {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer toolbar;
	private final ChatForm chatForm;

	/**
	 * Constructor
	 *
	 * @param id
	 *            the markup-id
	 */
	public ChatToolbar(String id, ChatForm form) {
		this(id, form, null);
	}

	/**
	 * Constructor
	 *
	 * @param id
	 *            the markup-id
	 * @param model
	 *            the {@link org.apache.wicket.model.IModel}
	 */
	public ChatToolbar(String id, ChatForm form, IModel<String> model) {
		super(id, model);
		this.chatForm = form;
		add(toolbar = new WebMarkupContainer("toolbar"));
	}

	@Override
	public void attachToEditor(Component editor) {
		toolbar.add(AttributeModifier.replace("data-target", JQueryWidget.getSelector(editor)));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		ConfirmableAjaxBorder delBtn = new ConfirmableAjaxBorder("ajax-cancel-button", getString("80"), getString("832"), chatForm) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				ChatDao dao = getBean(ChatDao.class);
				String scope = chatForm.getScope();
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
		toolbar.add(delBtn.setVisible(hasAdminLevel(getRights())));
		toolbar.add(new WebMarkupContainer("save").setVisible(hasAdminLevel(getRights())));
	}
}
