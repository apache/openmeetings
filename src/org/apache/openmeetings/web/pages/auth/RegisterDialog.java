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

import static org.apache.openmeetings.utils.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.utils.UserHelper.getMinPasswdLength;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.data.basic.FieldLanguageDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.user.EmailManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.user.State;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.StringValidator;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class RegisterDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = -8333305491376538792L;
	private DialogButton cancelBtn = new DialogButton(WebSession.getString(122));
	private DialogButton registerBtn = new DialogButton(WebSession.getString(121));
	private FeedbackPanel feedback = new FeedbackPanel("feedback");
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
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		//FIXME need to be refactored to be handled by Wicket
		getBean(UserManager.class).registerUser(login, password, lastName
				, firstName, email, null, ""/*street*/, ""/*additionalname*/, ""/*fax*/, ""/*zip*/
				, state.getState_id(), ""/*town*/, lang.getLanguage_id(), ""/*phone*/, false/*sendSMS*/
				, "" + getRequestCycle().urlFor(SwfPage.class, new PageParameters()), false, tz.getJname());
	}
	
	class RegisterForm extends StatelessForm<String> {
		private static final long serialVersionUID = 1701373326213602431L;
	    private PasswordTextField confirmPassword;
		private PasswordTextField passwordField;
		private RequiredTextField<String> emailField;
		private RequiredTextField<String> loginField;

		public RegisterForm(String id) {
			super(id);
			add(feedback.setOutputMarkupId(true));
			ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
			add(new RequiredTextField<String>("firstName", new PropertyModel<String>(RegisterDialog.this, "firstName")));
			add(new RequiredTextField<String>("lastName", new PropertyModel<String>(RegisterDialog.this, "lastName")));
			add(loginField = new RequiredTextField<String>("login", new PropertyModel<String>(RegisterDialog.this, "login")));
			loginField.add(StringValidator.minimumLength(getMinLoginLength(cfgDao)));
			add(passwordField = new PasswordTextField("password", new PropertyModel<String>(RegisterDialog.this, "password")));
			passwordField.setResetPassword(true).add(StringValidator.minimumLength(getMinPasswdLength(cfgDao)));
			add(confirmPassword = new PasswordTextField("confirmPassword", new Model<String>()).setResetPassword(true));
			add(emailField = new RequiredTextField<String>("email", new PropertyModel<String>(RegisterDialog.this, "email")));
			emailField.add(RfcCompliantEmailAddressValidator.getInstance());
			add(new DropDownChoice<FieldLanguage>("lang"
					, new PropertyModel<FieldLanguage>(RegisterDialog.this, "lang")
					, getBean(FieldLanguageDao.class).getLanguages()
					, new ChoiceRenderer<FieldLanguage>("name", "language_id"))
				.setRequired(true).setLabel(Model.of(WebSession.getString(111))));
			add(new DropDownChoice<OmTimeZone>("tz"
					, new PropertyModel<OmTimeZone>(RegisterDialog.this, "tz")
					, getBean(OmTimeZoneDao.class).getOmTimeZones()
					, new ChoiceRenderer<OmTimeZone>("frontEndLabel", "jname"))
				.setRequired(true).setLabel(Model.of(WebSession.getString(1143))));
			add(new DropDownChoice<State>("state"
					, new PropertyModel<State>(RegisterDialog.this, "state")
					, getBean(StateDao.class).getStates()
					, new ChoiceRenderer<State>("name", "state_id"))
				.setRequired(true).setLabel(Model.of(WebSession.getString(120))));
			
			add(new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = -3612671587183668912L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					RegisterDialog.this.onSubmit(target);
				}
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					RegisterDialog.this.onError(target);
				}
			});
		}
		
		@Override
		protected void onValidate() {
			if (passwordField.getConvertedInput() == null
					|| !passwordField.getConvertedInput().equals(confirmPassword.getConvertedInput())) {
				error(WebSession.getString(232));
			}
			if(!getBean(EmailManager.class).checkUserEMail(emailField.getConvertedInput())) {
				error(WebSession.getString(1000));
			}
			if(!getBean(UsersDao.class).checkUserLogin(loginField.getConvertedInput())) {
				error(WebSession.getString(105));
			}
		}
	}
}
