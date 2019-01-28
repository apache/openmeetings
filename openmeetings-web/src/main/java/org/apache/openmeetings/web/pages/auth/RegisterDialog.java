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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSendRegisterEmail;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSendVerificationEmail;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.Captcha;
import org.apache.openmeetings.web.pages.PrivacyPage;
import org.apache.openmeetings.web.util.NonClosableDialog;
import org.apache.openmeetings.web.util.NonClosableMessageDialog;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class RegisterDialog extends NonClosableDialog<String> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(RegisterDialog.class, getWebAppRootKey());
	private DialogButton cancelBtn;
	private DialogButton registerBtn;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final IModel<String> tzModel = Model.of(WebSession.get().getClientTZCode());
	private RegisterForm form;
	private SignInDialog s;
	private Captcha captcha;
	private String firstName;
	private String lastName;
	private String login;
	private String password;
	private String email;
	private String country;
	private Long lang;

	MessageDialog confirmRegistration;
	private boolean sendConfirmation = false;
	private boolean sendEmailAtRegister = false;

	public RegisterDialog(String id) {
		super(id, "");
		add(form = new RegisterForm("form"));
		form.setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("113"));
		cancelBtn = new DialogButton("cancel", getString("lbl.cancel"));
		registerBtn = new DialogButton("register", getString("121")) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isIndicating() {
				return true;
			}
		};
		confirmRegistration = new NonClosableMessageDialog("confirmRegistration", getString("235"), getString("warn.notverified")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				s.open(handler);
			}
		};
		add(new Label("register", getString("121")).setRenderBodyOnly(true), new BookmarkablePageLink<>("link", PrivacyPage.class));
		add(confirmRegistration);
		reset(null);
		super.onInitialize();
	}

	public void setSignInDialog(SignInDialog s) {
		this.s = s;
	}

	public void setClientTimeZone() {
		tzModel.setObject(WebSession.get().getClientTZCode());
	}

	@Override
	public int getWidth() {
		return 400;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(registerBtn, cancelBtn);
	}

	public void reset(IPartialPageRequestHandler handler) {
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
	protected void onOpen(IPartialPageRequestHandler handler) {
		String baseURL = getBaseUrl();
		sendEmailAtRegister = isSendRegisterEmail();
		sendConfirmation = !Strings.isEmpty(baseURL) && isSendVerificationEmail();
		String messageCode = "account.created";
		if (sendConfirmation && sendEmailAtRegister) {
			messageCode = "warn.notverified";
		}
		confirmRegistration.setModelObject(getString(messageCode));
		reset(handler);
		handler.add(form);
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		if (!registerBtn.equals(button)) {
			s.open(handler);
		}
	}

	@Override
	public DialogButton getSubmitButton() {
		return registerBtn;
	}

	@Override
	public Form<Void> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target, DialogButton btn) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
		try {
			Object o = getBean(IUserManager.class).registerUser(login, password, lastName
					, firstName, email, country, lang, tzModel.getObject());
			if (o instanceof String) {
				confirmRegistration.setModelObject(getString((String)o));
			}
		} catch (Exception e) {
			log.error("[registerUser]", e);
		}
		confirmRegistration.open(target);
	}

	@Override
	protected void onDetach() {
		tzModel.detach();
		super.onDetach();
	}

	class RegisterForm extends StatelessForm<Void> {
		private static final long serialVersionUID = 1L;
		private PasswordTextField confirmPassword;
		private PasswordTextField passwordField;
		private RequiredTextField<String> emailField;
		private RequiredTextField<String> loginField;
		private RequiredTextField<String> firstNameField;
		private RequiredTextField<String> lastNameField;

		public RegisterForm(String id) {
			super(id);
			add(feedback.setOutputMarkupId(true));
			add(firstNameField = new RequiredTextField<>("firstName", new PropertyModel<String>(RegisterDialog.this, "firstName")));
			add(lastNameField = new RequiredTextField<>("lastName", new PropertyModel<String>(RegisterDialog.this, "lastName")));
			add(loginField = new RequiredTextField<>("login", new PropertyModel<String>(RegisterDialog.this, "login")));
			add(passwordField = new PasswordTextField("password", new PropertyModel<String>(RegisterDialog.this, "password")));
			add(confirmPassword = new PasswordTextField("confirmPassword", new Model<String>()).setResetPassword(true));
			add(emailField = new RequiredTextField<>("email", new PropertyModel<String>(RegisterDialog.this, "email")));
			add(captcha = new Captcha("captcha"));
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			firstNameField.setLabel(new ResourceModel("117"));
			lastNameField.setLabel(new ResourceModel("136"));
			loginField.add(minimumLength(getMinLoginLength())).setLabel(new ResourceModel("114"));
			passwordField.setResetPassword(true).add(new StrongPasswordValidator(new User()) {
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
			}).setLabel(new ResourceModel("110"));
			confirmPassword.setLabel(new ResourceModel("116"));
			emailField.add(RfcCompliantEmailAddressValidator.getInstance()).setLabel(new ResourceModel("119"));
			add(new AjaxButton("submit") { // FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					RegisterDialog.this.onSubmit(target, registerBtn);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					RegisterDialog.this.onError(target, registerBtn);
				}
			});
		}

		@Override
		protected void onValidate() {
			if (passwordField.getConvertedInput() == null
					|| !passwordField.getConvertedInput().equals(confirmPassword.getConvertedInput())) {
				error(getString("232"));
			}
			if (!getBean(UserDao.class).checkEmail(emailField.getConvertedInput(), User.Type.user, null, null)) {
				error(getString("error.email.inuse"));
			}
			if (!getBean(UserDao.class).checkLogin(loginField.getConvertedInput(), User.Type.user, null, null)) {
				error(getString("error.login.inuse"));
			}
			if (hasErrorMessage()) {
				// add random timeout
				try {
					Thread.sleep((long)(10 * Math.random() * 1000));
				} catch (InterruptedException e) {
					log.error("Unexpected exception while sleeting", e);
				}
			}
		}
	}
}
