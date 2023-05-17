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

import java.util.Locale;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.LocaleHelper;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;

public class InvitationTemplate extends AbstractTemplatePage {
	private static final long serialVersionUID = 1L;

	private InvitationTemplate(Locale locale, String invitorName, String message, String link, boolean room) {
		super(locale);

		add(new Label("titleLbl", getString("500", locale)));
		add(new Label("userLbl", getString("501", locale)));
		add(new Label("user", invitorName));
		add(new Label("messageLbl", getString("502", locale)));
		add(new Label("message", message).setEscapeModelStrings(false));

		add(new WebMarkupContainer("links")
			.add(new Label("comment_for_link1", getString(
						room ? "template.room.invitation.text" : "template.recording.invitation.text"
						, locale)))
			.add(new ExternalLink("invitation_link1", link).add(new Label("clickMe", getString("504", locale))))
			.add(new Label("comment_for_link2", getString("505", locale)))
			.add(new Label("invitation_link2", link))
			.setVisible(link != null)
			);
	}

	public static String getEmail(User invitee, String invitorName, String message, String link, boolean room) {
		ensureApplication(invitee.getLanguageId());
		return new InvitationTemplate(LocaleHelper.getLocale(invitee), invitorName, message, link, room).renderEmail();
	}
}
