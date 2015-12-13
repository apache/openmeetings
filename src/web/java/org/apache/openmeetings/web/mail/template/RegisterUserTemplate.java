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

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;

public class RegisterUserTemplate extends AbstractTemplatePanel {
	private static final long serialVersionUID = 1L;

	public RegisterUserTemplate(String id, String username, String userpass, String email, String verification_url) {
		super(id);
		add(new Label("username", username));
		add(new Label("userpass", userpass));
		add(new Label("email", email));
		WebMarkupContainer verification = new WebMarkupContainer("verification");
		add(verification.add(new Label("verification_url2", verification_url))
			.add(new ExternalLink("verification_url1", verification_url))
			.setVisible(verification_url != null));
	}

	public static String getEmail(String username, String userpass, String email, String verification_url) {
		return renderPanel(new RegisterUserTemplate(TemplatePage.COMP_ID, username, userpass, email, verification_url)).toString();
	}
}
