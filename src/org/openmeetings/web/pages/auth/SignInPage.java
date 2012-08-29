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
package org.openmeetings.web.pages.auth;

import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openmeetings.web.app.Application;
import org.openmeetings.web.app.WebSession;
import org.openmeetings.web.pages.BasePage;

public class SignInPage extends BasePage {
	private static final long serialVersionUID = -3843571657066167592L;
	
	public SignInPage(PageParameters p) {
		this();
	}
	
	public SignInPage() {
		add(new SignInForm("signin"));
	}
	
	class SignInForm extends StatelessForm<Void> {
		private static final long serialVersionUID = 4079939497154278822L;
        private String password;
        private String login;

		public SignInForm(String id) {
			super(id);
			
			add(new FeedbackPanel("feedback"));
			add(new RequiredTextField<String>("login", new Model<String>(){
				private static final long serialVersionUID = -1335578251793516071L;
				
				@Override
				public String getObject() {
					return SignInForm.this.login;
				}
				
				@Override
				public void setObject(String object) {
					SignInForm.this.login = object;
				}
			}));
			add(new PasswordTextField("pass", new Model<String>(){
				private static final long serialVersionUID = 4751494320421393717L;

				@Override
				public String getObject() {
					return SignInForm.this.password;
				}
				
				@Override
				public void setObject(String object) {
					SignInForm.this.password = object;
				}
			}).setResetPassword(true));
		}
		
		@Override
		protected void onSubmit() {
			if (WebSession.get().signIn(login, password)) {
				continueToOriginalDestination();
	 			// if we reach this line there was no intercept page, so go to home page
	 			setResponsePage(Application.get().getHomePage());
			}
		}
	}
/*	
	@Override
	protected void configureResponse(WebResponse response) {
		super.configureResponse(response);
        response.setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
	}
*/
}
