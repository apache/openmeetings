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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;

public class RegisterUserTemplate extends AbstractTemplatePage {
	private static final long serialVersionUID = 1L;

	private RegisterUserTemplate(String username, String email, String verificationUrl) {
		super(getOmSession().getLocale());
		add(new Label("registrationLbl", getString("506", locale)));
		add(new Label("username", username));
		add(new Label("email", email));
		WebMarkupContainer verification = new WebMarkupContainer("verification");
		add(verification.add(new Label("verification_url2", verificationUrl))
			.add(new ExternalLink("verification_url1", verificationUrl))
			.setVisible(verificationUrl != null));
		add(new Label("groupLbl", getString("511", locale)));
		add(new ExternalLink("url", getBaseUrl(), getApplicationName()));
	}

	public static String getEmail(String username, String email, String verificationUrl) {
		return new RegisterUserTemplate(username, email, verificationUrl).renderEmail();
	}
}
