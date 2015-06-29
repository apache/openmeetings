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
package org.apache.openmeetings.web.user.profile;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.user.UserContactsDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.util.ContactsHelper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class UserInfoDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 6393565468567393270L;
	private WebMarkupContainer container = new WebMarkupContainer("container");
	private DialogButton cancel = new DialogButton("cancel", Application.getString(61));
	private DialogButton message = new DialogButton("message", Application.getString(1253));
	private DialogButton contacts = new DialogButton("contacts", Application.getString(1186));
	private MessageDialog newMessage;
	private long userId;
	
	public UserInfoDialog(String id, MessageDialog newMessage) {
		super(id, Application.getString(1235));
		add(container.add(new WebMarkupContainer("body")).setOutputMarkupId(true));
		this.newMessage = newMessage;
	}

	public void open(AjaxRequestTarget target, long userId) {
		this.userId = userId;
		container.replace(new UserProfilePanel("body", userId));
		target.add(container);
		open(target);
	}
	
	public WebMarkupContainer getContainer() {
		return container;
	}
	
	@Override
	public int getWidth() {
		return 500;
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		return userId != getUserId() && 0 == getBean(UserContactsDao.class).checkUserContacts(userId, getUserId())
				? Arrays.asList(message, cancel) : Arrays.asList(contacts, message, cancel);
	}
	
	public void onClose(AjaxRequestTarget target, DialogButton button) {
		if (message.equals(button)) {
			newMessage.reset(false).open(target, userId);
		} else if (contacts.equals(button)) {
			ContactsHelper.addUserToContactList(userId);
		}
	}
}
