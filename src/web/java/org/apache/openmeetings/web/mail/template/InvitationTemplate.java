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
package org.apache.openmeetings.web.mail.template;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.util.tester.WicketTester;

public class InvitationTemplate extends AbstractTemplatePanel {
	private static final long serialVersionUID = 1L;

	public InvitationTemplate(String id, String user, String message, String link) {
		super(id);
		add(new Label("user", user));
		add(new Label("message", message));
		add(new ExternalLink("invitation_link1", link));
		add(new Label("invitation_link2", link).setEscapeModelStrings(false));
	}
	
	public static String getEmail(String user, String message, String link) {
		return getEmail(-1, user, message, link);
	}
	
	public static String getEmail(long langId, String user, String message, String link) {
		WicketTester tester = null;
		try {
			tester = ensureApplication(langId);
			return renderPanel(new InvitationTemplate(TemplatePage.COMP_ID, user, message, link)).toString();
		} finally {
			if (tester != null) {
				tester.destroy();
			}
		}
	}
}
