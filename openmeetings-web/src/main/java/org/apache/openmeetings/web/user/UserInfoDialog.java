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

import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.db.dao.user.UserContactDao;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.user.profile.UserProfilePanel;
import org.apache.openmeetings.web.util.ContactsHelper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class UserInfoDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer container = new WebMarkupContainer("container");
	private MessageDialog newMessage;
	private long userId;
	private BootstrapAjaxLink<String> message;
	private BootstrapAjaxLink<String> contacts;

	@Inject
	private UserContactDao contactDao;

	public UserInfoDialog(String id, MessageDialog newMessage) {
		super(id);
		this.newMessage = newMessage;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("1235"));
		addButton(message = new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, Model.of(""), Buttons.Type.Outline_Primary, new ResourceModel("1253")) {
			private static final long serialVersionUID = 1L;

			public void onClick(AjaxRequestTarget target) {
				newMessage.reset(false).show(target, userId);
				UserInfoDialog.this.close(target);
			}
		});
		addButton(contacts = new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, Model.of(""), Buttons.Type.Outline_Info, new ResourceModel("1186")) {
			private static final long serialVersionUID = 1L;

			public void onClick(AjaxRequestTarget target) {
				ContactsHelper.addUserToContactList(userId);
				UserInfoDialog.this.close(target);
			}
		});
		contacts.setOutputMarkupId(true);
		addButton(OmModalCloseButton.of());
		add(container.add(new WebMarkupContainer("body")).setOutputMarkupId(true));
		super.onInitialize();
	}

	public void show(IPartialPageRequestHandler handler, long userId) {
		this.userId = userId;
		contacts.setVisible(userId != getUserId() && contactDao.get(userId, getUserId()) == null);
		message.setVisible(userId != getUserId());
		container.replace(new UserProfilePanel("body", userId));
		handler.add(container, contacts);
		super.show(handler);
	}

	public WebMarkupContainer getContainer() {
		return container;
	}
}
