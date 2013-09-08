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

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.utils.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.utils.UserHelper.getMinPasswdLength;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.openmeetings.data.basic.FieldLanguageDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.AdminUserDao;
import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.user.State;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.ActivatePage;
import org.apache.openmeetings.web.pages.MainPage;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

public class RegisterDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = -8333305491376538792L;
	private static final Logger log = Red5LoggerFactory.getLogger(MainPage.class, webAppRootKey);
	private DialogButton cancelBtn = new DialogButton(WebSession.getString(122));
	private DialogButton registerBtn = new DialogButton(WebSession.getString(121));
	private FeedbackPanel feedback = new FeedbackPanel("feedback");
	private final IModel<String> tzModel = Model.of(WebSession.get().getTimeZoneByBrowserLocale(0));
	private final DropDownChoice<String> tzDropDown = new DropDownChoice<String>("tz"
			, tzModel
			, WebSession.getAvailableTimezones());
	private Form<String> form;
	private SignInDialog s;
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String email;
    private State state;
    private FieldLanguage lang;

    final MessageDialog confirmRegistration;
    private boolean sendConfirmation = false;
    private boolean sendEmailAtRegister = false;
    
	public RegisterDialog(String id) {
		super(id, WebSession.getString(113));
		add(form = new RegisterForm("form"));
		lang = WebSession.get().getLanguageByBrowserLocale();
		state = WebSession.get().getCountryByBrowserLocale();
		tzDropDown.setOutputMarkupId(true);
		String baseURL = WebSession.get().getBaseUrl();
		
		sendEmailAtRegister = 1 == getBean(ConfigurationDao.class).getConfValue("sendEmailAtRegister", Integer.class, "0");
		sendConfirmation = baseURL != null
				&& !baseURL.isEmpty()
				&& 1 == getBean(ConfigurationDao.class).getConfValue("sendEmailWithVerficationCode", Integer.class, "0");

		confirmRegistration = new MessageDialog("confirmRegistration", WebSession.getString(235), WebSession.getString(674), DialogButtons.OK, DialogIcon.INFO){
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(JQueryBehavior behavior) {
				super.onConfigure(behavior);
		        behavior.setOption("dialogClass", Options.asString("no-close"));
				behavior.setOption("closeOnEscape", false);
			}
			
			public void onOpen(AjaxRequestTarget target) {
				this.setTitle(Model.of(sendConfirmation && sendEmailAtRegister ? WebSession.getString(674) : WebSession.getString(236)));
			}
			
			public void onClose(AjaxRequestTarget target, DialogButton button) {
				s.open(target);
			}
		};
		add(confirmRegistration);
}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
        behavior.setOption("dialogClass", Options.asString("no-close"));
		behavior.setOption("closeOnEscape", false);
	}
	
	public void setSignInDialog(SignInDialog s) {
		this.s = s;
	}

	public void setBrowserTZOffset(AjaxRequestTarget target, int browserTZOffset) {
		tzModel.setObject(WebSession.get().getTimeZoneByBrowserLocale(browserTZOffset));
		target.add(tzDropDown);
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
		if (button.equals(registerBtn)){
			confirmRegistration.open(target);
		} else {
			s.open(target);
		}
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
		String hash = getBean(ManageCryptStyle.class).getInstanceOfCrypt()
				.createPassPhrase(login + CalendarPatterns.getDateWithTimeByMiliSeconds(new Date()));

		String redirectPage = getRequestCycle().urlFor(ActivatePage.class, new PageParameters().add("u", hash)).toString().substring(2);
		String baseURL = WebSession.get().getBaseUrl() + redirectPage;
		try {
			getBean(UserManager.class).registerUserInit(3, 1, 0, 1,
					login, password, lastName,
					firstName, email, new Date(), "" /*street*/,
					"" /*additionalname*/, "" /*fax*/, "" /*zip*/,
					state.getState_id(),
					"" /*town*/, lang.getLanguage_id(), true /*sendWelcomeMessage*/,
					Arrays.asList(getBean(ConfigurationDao.class).getConfValue("default_domain_id", Long.class, null)),
					"" /*phone*/, false, baseURL,
					sendConfirmation,
					TimeZone.getTimeZone(tzModel.getObject()),
					false /*forceTimeZoneCheck*/,
					"" /*userOffers*/, "" /*userSearchs*/,
					false /*showContactData*/,
					true /*showContactDataToContacts*/, hash);
			
		} catch (Exception e) {
			log.error("[registerUser]", e);
		}

	}
	
	class RegisterForm extends StatelessForm<String> {
		private static final long serialVersionUID = 1701373326213602431L;
	    private PasswordTextField confirmPassword;
		private PasswordTextField passwordField;
		private RequiredTextField<String> emailField;
		private RequiredTextField<String> loginField;
		private RequiredTextField<String> firstNameField;
		private RequiredTextField<String> lastNameField;

		public RegisterForm(String id) {
			super(id);
			add(feedback.setOutputMarkupId(true));
			ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
			add(firstNameField = new RequiredTextField<String>("firstName", new PropertyModel<String>(RegisterDialog.this, "firstName")));
			firstNameField.setLabel(Model.of(WebSession.getString(117)));
			add(lastNameField = new RequiredTextField<String>("lastName", new PropertyModel<String>(RegisterDialog.this, "lastName")));
			lastNameField.setLabel(Model.of(WebSession.getString(118)));
			add(loginField = new RequiredTextField<String>("login", new PropertyModel<String>(RegisterDialog.this, "login")));
			loginField.setLabel(Model.of(WebSession.getString(114)));
			loginField.add(minimumLength(getMinLoginLength(cfgDao)));
			add(passwordField = new PasswordTextField("password", new PropertyModel<String>(RegisterDialog.this, "password")));
			passwordField.setLabel(Model.of(WebSession.getString(115)));
			passwordField.setResetPassword(true).add(minimumLength(getMinPasswdLength(cfgDao)));
			add(confirmPassword = new PasswordTextField("confirmPassword", new Model<String>()).setResetPassword(true));
			confirmPassword.setLabel(Model.of(WebSession.getString(116)));
			add(emailField = new RequiredTextField<String>("email", new PropertyModel<String>(RegisterDialog.this, "email")));
			emailField.setLabel(Model.of(WebSession.getString(119)));
			emailField.add(RfcCompliantEmailAddressValidator.getInstance());
			add(new DropDownChoice<FieldLanguage>("lang"
					, new PropertyModel<FieldLanguage>(RegisterDialog.this, "lang")
					, getBean(FieldLanguageDao.class).getLanguages()
					, new ChoiceRenderer<FieldLanguage>("name", "language_id"))
				.setRequired(true).setLabel(Model.of(WebSession.getString(111))));
			add(tzDropDown.setRequired(true).setLabel(Model.of(WebSession.getString(1143))));
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
			if(!getBean(AdminUserDao.class).checkUserEMail(emailField.getConvertedInput(), null)) {
				error(WebSession.getString(1000));
			}
			if(!getBean(AdminUserDao.class).checkUserLogin(loginField.getConvertedInput(), null)) {
				error(WebSession.getString(105));
			}
		}
	}
}
