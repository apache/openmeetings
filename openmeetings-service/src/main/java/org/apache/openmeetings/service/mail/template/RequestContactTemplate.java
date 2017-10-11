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
package org.apache.openmeetings.service.mail.template;

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.LocaleHelper;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;

public class RequestContactTemplate extends AbstractTemplatePanel {
	private static final long serialVersionUID = 1L;

	private RequestContactTemplate(User userToAdd, User user) {
		super(LocaleHelper.getLocale(userToAdd));
		add(new Label("hi", getString("1192", locale)));
		add(new Label("addedFirstName", userToAdd.getFirstname()));
		add(new Label("addedLastName", userToAdd.getLastname()));
		add(new Label("firstName", user.getFirstname()));
		add(new Label("lastName", user.getLastname()));
		add(new Label("likeToAdd", getString("1193", locale)));
		add(new Label("check", getString("1194", locale)));
		add(new ExternalLink("link", ensureApplication().getOmContactsLink()).add(new Label("contactList", getString("1196", locale))));
	}

	public static String getEmail(User userToAdd, User user) {
		return ComponentRenderer.renderComponent(new RequestContactTemplate(userToAdd, user)).toString();
	}
}
