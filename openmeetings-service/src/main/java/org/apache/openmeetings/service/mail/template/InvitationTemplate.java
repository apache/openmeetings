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

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;

public class InvitationTemplate extends AbstractTemplatePanel {
	private static final long serialVersionUID = 1L;

	private InvitationTemplate(Long langId, String invitorName, String message, String link) {
		super(langId);

		add(new Label("titleLbl", getString(500, langId)));
		add(new Label("userLbl", getString(501, langId)));
		add(new Label("user", invitorName));
		add(new Label("messageLbl", getString(502, langId)));
		add(new Label("message", message).setEscapeModelStrings(false));
		
		add(new WebMarkupContainer("links")
			.add(new Label("comment_for_link1", getString(503, langId)))
			.add(new ExternalLink("invitation_link1", link).add(new Label("clickMe", getString(504, langId))))
			.add(new Label("comment_for_link2", getString(505, langId)))
			.add(new Label("invitation_link2", link))
			.setVisible(link != null)
			);
	}
	
	public static String getEmail(Long langId, String invitorName, String message, String link) {
		ensureApplication(langId);
		return renderPanel(new InvitationTemplate(langId, invitorName, message, link)).toString();
	}
}
