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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.user.UserContactDao;
import org.apache.openmeetings.web.user.profile.UserProfilePanel;
import org.apache.openmeetings.web.util.ContactsHelper;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class UserInfoDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer container = new WebMarkupContainer("container");
	private DialogButton cancel;
	private DialogButton message;
	private DialogButton contacts;
	private MessageDialog newMessage;
	private long userId;

	public UserInfoDialog(String id, MessageDialog newMessage) {
		super(id, "");
		this.newMessage = newMessage;
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("1235"));
		cancel = new DialogButton("cancel", getString("lbl.cancel"));
		message = new DialogButton("message", getString("1253"));
		contacts = new DialogButton("contacts", getString("1186"));
		add(container.add(new WebMarkupContainer("body")).setOutputMarkupId(true));
		super.onInitialize();
	}

	public void open(IPartialPageRequestHandler handler, long userId) {
		this.userId = userId;
		contacts.setVisible(userId != getUserId() && getBean(UserContactDao.class).get(userId, getUserId()) == null, handler);
		message.setVisible(userId != getUserId(), handler);
		container.replace(new UserProfilePanel("body", userId));
		handler.add(container);
		open(handler);
	}

	public WebMarkupContainer getContainer() {
		return container;
	}

	@Override
	public int getWidth() {
		return 600;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(contacts, message, cancel);
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		if (message.equals(button)) {
			newMessage.reset(false).open(handler, userId);
		} else if (contacts.equals(button)) {
			ContactsHelper.addUserToContactList(userId);
		}
	}
}
