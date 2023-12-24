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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSendRegisterEmail;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSendVerificationEmail;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.Captcha;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.pages.PrivacyPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;

import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerAjaxButton;
import jakarta.inject.Inject;

public class RegisterDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(RegisterDialog.class);
	private static final String FLD_EMAIL = "email";
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final IModel<String> tzModel = Model.of(WebSession.get().getClientTZCode());
	private final RegisterForm form = new RegisterForm("form");
	private Captcha captcha;
	private String firstName;
	private String lastName;
	private String login;
	private String password;
	private String email;
	private String country;
	private Long lang;
	private boolean wasRegistered = false;

	@Inject
	private IUserManager userManager;
	@Inject
	private UserDao userDao;

	public RegisterDialog(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("113"));
		setUseCloseHandler(true);

		addButton(new SpinnerAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("121"), form, Buttons.Type.Outline_Primary)); // register
		addButton(OmModalCloseButton.of());
		add(form);
		add(new Label("register", getString("121")).setRenderBodyOnly(true), new BookmarkablePageLink<>("link", PrivacyPage.class));
		reset(null);
		super.onInitialize();
	}

	public void setClientTimeZone() {
		tzModel.setObject(WebSession.get().getClientTZCode());
	}

	public void reset(IPartialPageRequestHandler handler) {
		wasRegistered = false;
		firstName = null;
		lastName = null;
		login = null;
		password = null;
		form.confirmPassword.setModelObject(null);
		email = null;
		lang = WebSession.get().getLanguageByLocale();
		country = WebSession.get().getLocale().getCountry();
		captcha.refresh(handler);
	}

	@Override
	public Modal<String> show(IPartialPageRequestHandler handler) {
		String baseURL = getBaseUrl();
		boolean sendEmailAtRegister = isSendRegisterEmail();
		boolean sendConfirmation = !Strings.isEmpty(baseURL) && isSendVerificationEmail();
		String messageCode = "account.created";
		if (sendConfirmation && sendEmailAtRegister) {
			messageCode = "warn.notverified";
		}
		Modal<String> registerInfo = getRegisterInfo();
		registerInfo.setModelObject(getString(messageCode));
		handler.add(registerInfo.get("content"));
		reset(handler);
		handler.add(form);
		return super.show(handler);
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler) {
		if (!wasRegistered) {
			SignInDialog signin = (SignInDialog)getPage().get("signin");
			signin.show(handler);
		}
	}

	@Override
	protected void onDetach() {
		tzModel.detach();
		super.onDetach();
	}

	@SuppressWarnings("unchecked")
	private Modal<String> getRegisterInfo() {
		return (Modal<String>)getPage().get("registerInfo");
	}

	class RegisterForm extends StatelessForm<Void> {
		private static final long serialVersionUID = 1L;
		private PasswordTextField confirmPassword;
		private PasswordTextField passwordField;
		private RequiredTextField<String> emailField;
		private RequiredTextField<String> loginField;

		public RegisterForm(String id) {
			super(id);
			setOutputMarkupId(true);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(feedback.setOutputMarkupId(true));
			RequiredTextField<String> firstNameField = new RequiredTextField<>("firstName", new PropertyModel<>(RegisterDialog.this, "firstName"));
			RequiredTextField<String> lastNameField = new RequiredTextField<>("lastName", new PropertyModel<>(RegisterDialog.this, "lastName"));
			add(firstNameField.setLabel(new ResourceModel("117")));
			add(lastNameField.setLabel(new ResourceModel("136")));
			loginField = new RequiredTextField<>("login", new PropertyModel<>(RegisterDialog.this, "login"));
			add(loginField.add(minimumLength(getMinLoginLength())).setLabel(new ResourceModel("114")));
			passwordField = new PasswordTextField("password", new PropertyModel<>(RegisterDialog.this, "password"));
			add(passwordField.setResetPassword(true).add(new StrongPasswordValidator(new User()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void validate(IValidatable<String> pass) {
					User u = new User();
					u.setLogin(loginField.getRawInput());
					u.setAddress(new Address());
					u.getAddress().setEmail(emailField.getRawInput());
					setUser(u);
					super.validate(pass);
				}
			}).setLabel(new ResourceModel("110")));
			confirmPassword = new PasswordTextField("confirmPassword", new Model<>()).setResetPassword(true);
			add(confirmPassword.setLabel(new ResourceModel("116")));
			emailField = new RequiredTextField<>(FLD_EMAIL, new PropertyModel<>(RegisterDialog.this, FLD_EMAIL)) {
				private static final long serialVersionUID = 1L;

				@Override
				protected String[] getInputTypes() {
					return new String[] {FLD_EMAIL};
				}
			};
			add(emailField.add(RfcCompliantEmailAddressValidator.getInstance()).setLabel(new ResourceModel("119")));
			add(captcha = new Captcha("captcha"));
			loginField.add(minimumLength(getMinLoginLength())).setLabel(new ResourceModel("114"));
			AjaxButton ab = new AjaxButton("submit") { // FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = 1L;
			};
			add(ab);
			setDefaultButton(ab);
		}

		@Override
		protected void onValidate() {
			if (passwordField.getConvertedInput() == null
					|| !passwordField.getConvertedInput().equals(confirmPassword.getConvertedInput())) {
				error(getString("232"));
			}
			if (!userDao.checkEmail(emailField.getConvertedInput(), User.Type.USER, null, null)) {
				error(getString("error.email.inuse"));
			}
			if (!userDao.checkLogin(loginField.getConvertedInput(), User.Type.USER, null, null)) {
				error(getString("error.login.inuse"));
			}
			if (hasErrorMessage()) {
				SignInDialog.penalty();
			}
		}

		@Override
		protected void onError() {
			RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(this::onError);
		}

		private void onError(AjaxRequestTarget target) {
			target.add(feedback);
		}

		@Override
		protected void onSubmit() {
			RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(this::onSubmit);
		}

		private void onSubmit(AjaxRequestTarget target) {
			Modal<String> registerInfo = getRegisterInfo();
			wasRegistered = true;
			try {
				Object o = userManager.registerUser(login, password, lastName
						, firstName, email, country, lang, tzModel.getObject());
				if (o instanceof String str) {
					registerInfo.setModelObject(getString(str));
					target.add(registerInfo.get("content"));
				}
			} catch (Exception e) {
				log.error("[registerUser]", e);
			}
			RegisterDialog.this.close(target);
			registerInfo.show(target);
		}
	}
}
