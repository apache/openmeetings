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

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.data.basic.FieldLanguageDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.user.State;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class RegisterDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = -8333305491376538792L;
	private DialogButton cancelBtn = new DialogButton(WebSession.getString(122));
	private DialogButton registerBtn = new DialogButton(WebSession.getString(121));
	private Form<String> form;
	private SignInDialog s;
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String email;
    private OmTimeZone tz;
    private State state;
    private FieldLanguage lang;

	public RegisterDialog(String id) {
		super(id, WebSession.getString(113));
		add(form = new RegisterForm("form"));
	}

	public void setSignInDialog(SignInDialog s) {
		this.s = s;
	}
	
	@Override
	public int getWidth() {
		return 400;
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(registerBtn, cancelBtn);
	}
	
	public void onClose(AjaxRequestTarget target, DialogButton button) {
		// TODO Auto-generated method stub
		s.open(target);
	}

	@Override
	protected DialogButton getSubmitButton() {
		return registerBtn;
	}

	@Override
	public Form<String> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {
		// TODO messages
		
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		// TODO Register + validation
		
	}
	
	class RegisterForm extends StatelessForm<String> {
		private static final long serialVersionUID = 1701373326213602431L;
	    private PasswordTextField confirmPassword;

		public RegisterForm(String id) {
			super(id);
			add(new FeedbackPanel("feedback"));
			add(new RequiredTextField<String>("firstName", new PropertyModel<String>(RegisterDialog.this, "firstName")));
			add(new RequiredTextField<String>("lastName", new PropertyModel<String>(RegisterDialog.this, "lastName")));
			add(new RequiredTextField<String>("login", new PropertyModel<String>(RegisterDialog.this, "login")));
			add(new PasswordTextField("password", new PropertyModel<String>(RegisterDialog.this, "password")).setResetPassword(true));
			add(confirmPassword = new PasswordTextField("confirmPassword", new Model<String>()).setResetPassword(true));
			add(new RequiredTextField<String>("email", new PropertyModel<String>(RegisterDialog.this, "email")));
			add(new DropDownChoice<FieldLanguage>("lang"
					, new PropertyModel<FieldLanguage>(RegisterDialog.this, "lang")
					, getBean(FieldLanguageDao.class).getLanguages()
					, new ChoiceRenderer<FieldLanguage>("name", "language_id")));
			add(new DropDownChoice<OmTimeZone>("tz"
					, new PropertyModel<OmTimeZone>(RegisterDialog.this, "tz")
					, getBean(OmTimeZoneDao.class).getOmTimeZones()
					, new ChoiceRenderer<OmTimeZone>("frontEndLabel", "jname")));
			add(new DropDownChoice<State>("state"
					, new PropertyModel<State>(RegisterDialog.this, "tz")
					, getBean(StateDao.class).getStates()
					, new ChoiceRenderer<State>("name", "state_id")));
			
			add(new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = -3612671587183668912L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					RegisterDialog.this.onSubmit(target);
				}
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					// TODO Auto-generated method stub
					RegisterDialog.this.onError(target);
				}
			});
		}
		
		@Override
		protected void onValidate() {
			// TODO Auto-generated method stub
			super.onValidate();
		}
	}
}
