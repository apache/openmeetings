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

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.util.tester.WicketTester;

public class InvitationTemplate extends AbstractTemplatePanel {
	private static final long serialVersionUID = 1L;

	public InvitationTemplate(String id, String user, String message, String link, boolean isCanceled) {
		super(id);
		add(new Label("user", user));
		add(new Label("message", message).setEscapeModelStrings(false));
		Label commentForLink1 = new Label("comment_for_link1", WebSession.getString(503));
		commentForLink1.setVisible(!isCanceled);
		add(commentForLink1);
		ExternalLink externalLink1 = new ExternalLink("invitation_link1", link);
		externalLink1.setVisible(!isCanceled);
		add(externalLink1);
		Label commentForLink2 = new Label("comment_for_link2", WebSession.getString(505));
		commentForLink2.setVisible(!isCanceled);
		add(commentForLink2);
		Label externalLink2 = new Label("invitation_link2", link);
		externalLink2.setEscapeModelStrings(false).setVisible(!isCanceled); 
		add(externalLink2);
	}
	
	public static String getEmail(String user, String message, String link) {
		return getEmail(-1, user, message, link, false);
	}
	
	public static String getEmail(long langId, String user, String message, String link, boolean isCanceled) {
		WicketTester tester = null;
		try {
			tester = ensureApplication(langId);
			return renderPanel(new InvitationTemplate(TemplatePage.COMP_ID, user, message, link, isCanceled)).toString();
		} finally {
			Application.destroy(tester);
		}
	}
}
