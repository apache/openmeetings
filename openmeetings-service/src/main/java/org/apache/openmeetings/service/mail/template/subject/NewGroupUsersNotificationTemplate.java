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
package org.apache.openmeetings.service.mail.template.subject;

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;

import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.LocaleHelper;
import org.apache.openmeetings.service.mail.template.OmTextLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;

public class NewGroupUsersNotificationTemplate extends SubjectEmailTemplate {
	private static final long serialVersionUID = 1L;
	private long userCount;
	private final Group g;

	private NewGroupUsersNotificationTemplate(User u, Group g, long userCount) {
		super(LocaleHelper.getLocale(u));
		this.g = g;
		this.userCount = userCount;
	}

	public static SubjectEmailTemplate get(User u, Group g, long userCount) {
		ensureApplication(u.getLanguageId());
		return new NewGroupUsersNotificationTemplate(u, g, userCount).create();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final String app = getApplicationName();
		add(new Label("body", getString("template.new.group.users.notification.body", locale
					, g.getName()
					, String.valueOf(userCount)
					, String.valueOf(g.getNotifyInterval())
					, app))
				.setEscapeModelStrings(false));
		add(new Label("footer", getString("template.new.group.users.notification.footer", locale, app))
				.setEscapeModelStrings(false));
	}

	@Override
	Fragment getSubjectFragment() {
		Fragment f = new Fragment(COMP_ID, "subject", this);
		f.add(new OmTextLabel("subject", getString("template.new.group.users.notification.subject", locale, getApplicationName(), g.getName())));
		return f;
	}
}
