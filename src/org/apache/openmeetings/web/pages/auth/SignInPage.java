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
package org.apache.openmeetings.web.pages.auth;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.BasePage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.googlecode.wicket.jquery.ui.JQueryAbstractBehavior;

public class SignInPage extends BasePage {
	private static final long serialVersionUID = -3843571657066167592L;
	
	public SignInPage(PageParameters p) {
		this();
	}
	
	public SignInPage() {
		add(new JQueryAbstractBehavior("name") {
			private static final long serialVersionUID = -8915882511038880055L;

			@Override
			protected String $() {
				return "";
			}
		});
		add(new SignInForm("signin"));
	}
	
	class SignInForm extends StatelessForm<Void> {
		private static final long serialVersionUID = 4079939497154278822L;
        private String password;
        private String login;
        private String area = "";
        private boolean rememberMe = false;

		public SignInForm(String id) {
			super(id);
			
			if (WebSession.get().isSignedIn()) {
				alreadyLoggedIn();
			} else {
				IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();
				// get username and password from persistence store
				String[] data = strategy.load();

				if ((data != null) && (data.length > 1)) {
					// try to sign in the user
					if (WebSession.get().signIn(data[0], data[1])) {
						login = data[0];
						password = data[1];

						alreadyLoggedIn();
					} else {
						// the loaded credentials are wrong. erase them.
						strategy.remove();
					}
				}
			}
			add(new FeedbackPanel("feedback"));
			add(new RequiredTextField<String>("login", new PropertyModel<String>(this, "login")));
			add(new PasswordTextField("pass", new PropertyModel<String>(this, "password")).setResetPassword(true));
			add(new CheckBox("rememberMe", new PropertyModel<Boolean>(this, "rememberMe")).setOutputMarkupId(true));
			add(new HiddenField<String>("area", new PropertyModel<String>(this, "area"))
					.setMarkupId("area")
					.setOutputMarkupId(true));
		}

		private void alreadyLoggedIn() {
			// logon successful. Continue to the original destination
			continueToOriginalDestination();
			// Ups, no original destination. Go to the home page
			throw new RestartResponseException(Application.get().getHomePage());
		}
		
		@Override
		protected void onSubmit() {
			IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();
			if (WebSession.get().signIn(login, password)) {
				WebSession.get().setArea(area);
	 			setResponsePage(Application.get().getHomePage());
				if (rememberMe) {
					strategy.save(login, password);
				} else {
					strategy.remove();
				}
			} else {
				strategy.remove();
			}
		}
	}
}
