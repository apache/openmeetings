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

import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.openmeetings.db.util.LocaleHelper;
import org.apache.wicket.markup.html.basic.Label;

public class RequestContactConfirmTemplate extends AbstractTemplatePage {
	private static final long serialVersionUID = 1L;

	private RequestContactConfirmTemplate(UserContact contact) {
		super(LocaleHelper.getLocale(contact.getOwner()));
		add(new Label("hi", getString("1192", locale)));
		add(new Label("displayName", contact.getOwner().getDisplayName()));
		add(new Label("addedName", contact.getContact().getDisplayName()));
		add(new Label("confirmed", getString("1198", locale)));
	}

	public static String getEmail(UserContact contact) {
		return new RequestContactConfirmTemplate(contact).renderEmail();
	}
}
