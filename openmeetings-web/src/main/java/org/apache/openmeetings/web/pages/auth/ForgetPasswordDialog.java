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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.web.app.Application.urlForPage;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLoginLength;

import java.util.Date;

import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.mail.template.ResetPasswordTemplate;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.Captcha;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.pages.ResetPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.Validatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class ForgetPasswordDialog extends Modal<String> {
	private static final Logger log = LoggerFactory.getLogger(ForgetPasswordDialog.class);
	private static final long serialVersionUID = 1L;
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final IValidator<String> emailValidator = RfcCompliantEmailAddressValidator.getInstance();
	private final RequiredTextField<String> name = new RequiredTextField<>("name", Model.of((String)null));
	private final RadioGroup<Type> rg = new RadioGroup<>("type", Model.of(Type.EMAIL));
	private final WebMarkupContainer label = new WebMarkupContainer("label");
	private final Captcha captcha = new Captcha("captcha");
	private ForgetPasswordForm form = new ForgetPasswordForm("form");
	private boolean wasReset = false;

	@Inject
	private UserDao userDao;
	@Inject
	private MailHandler mailHandler;

	enum Type {
		EMAIL
		, LOGIN
	}

	public ForgetPasswordDialog(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("312"));
		setUseCloseHandler(true);

		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("317"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;
		}); // Send
		addButton(OmModalCloseButton.of());

		add(form);
		super.onInitialize();
	}

	private void updateLabel(IPartialPageRequestHandler handler) {
		IModel<String> lbl = new ResourceModel(rg.getModelObject() == Type.EMAIL ? "315" : "316");
		name.setLabel(lbl);
		name.add(AttributeModifier.replace("type", rg.getModelObject() == Type.EMAIL ? "email" : "text"));
		name.add(AttributeModifier.replace("title", lbl));
		name.add(AttributeModifier.replace("placeholder", lbl));
		label.add(AttributeModifier.replace("class", rg.getModelObject() == Type.EMAIL ? "fa fa-at" : "fa fa-user"));
		if (handler != null) {
			handler.add(name, label);
		}
	}

	@Override
	public Modal<String> show(IPartialPageRequestHandler handler) {
		name.setModelObject(null);
		rg.setModelObject(Type.EMAIL);
		captcha.refresh(handler);
		handler.add(rg);
		updateLabel(handler);
		wasReset = false;
		return super.show(handler);
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler) {
		if (wasReset) {
			@SuppressWarnings("unchecked")
			Modal<String> forgetInfo = (Modal<String>)getPage().get("forgetInfo");
			forgetInfo.show(handler);
		} else {
			SignInDialog signin = (SignInDialog)getPage().get("signin");
			signin.show(handler);
		}
	}

	private class ForgetPasswordForm extends Form<String> {
		private static final long serialVersionUID = 1L;

		public ForgetPasswordForm(String id) {
			super(id);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(feedback.setOutputMarkupId(true));
			add(label.setOutputMarkupId(true));
			add(name.setOutputMarkupId(true));
			add(captcha);
			add(rg.add(new Radio<>("email", Model.of(Type.EMAIL)))
					.add(new Radio<>("login", Model.of(Type.LOGIN)))
					.setOutputMarkupId(true));
			rg.add(new AjaxFormChoiceComponentUpdatingBehavior() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					updateLabel(target);
				}
			});
			add(new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					ForgetPasswordForm.this.onSubmit(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					ForgetPasswordForm.this.onError(target);
				}
			});
			updateLabel(null);
		}

		@Override
		protected void onValidate() {
			String n = name.getConvertedInput();
			if (n != null) {
				IValidatable<String> val = new Validatable<>(n);
				Type type = rg.getConvertedInput();
				if (type == Type.EMAIL) {
					emailValidator.validate(val);
					if (!val.isValid()) {
						error(getString("234"));
					}
				}
				if (type == Type.LOGIN && n.length() < getMinLoginLength()) {
					error(getString("104"));
				}
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
			String nm = name.getModelObject();
			Type type = rg.getModelObject();
			resetUser(type == Type.EMAIL ? nm : "", type == Type.LOGIN ? nm : "");
			wasReset = true;
			ForgetPasswordDialog.this.close(target);
		}

		/**
		 * reset a username by a given mail oder login by sending a mail to the
		 * registered EMail-Address
		 *
		 * @param email - email of the user
		 * @param username - username of the user
		 * @return <code>true</code> in case reset was successful, <code>false</code> otherwise
		 */
		private boolean resetUser(String email, String username) {
			try {
				log.debug("resetUser {}", email);

				// check if Mail given
				if (!Strings.isEmpty(email)) {
					User us = userDao.getByEmail(email);
					if (us != null) {
						sendHashByUser(us);
						return true;
					}
				} else if (!Strings.isEmpty(username)) {
					User us = userDao.getByLogin(username, User.Type.USER, null);
					if (us != null) {
						sendHashByUser(us);
						return true;
					}
				}
			} catch (Exception e) {
				log.error("[resetUser]", e);
			}
			return false;
		}

		private void sendHashByUser(User us) {
			log.debug("User: {}", us.getLogin());
			us.setResethash(randomUUID().toString());
			us.setResetDate(new Date());
			userDao.update(us, null);
			String resetLink = urlForPage(ResetPage.class
					, new PageParameters().add("hash", us.getResethash())
					, getBaseUrl());

			String email = us.getAddress().getEmail();

			String template = ResetPasswordTemplate.getEmail(resetLink);

			mailHandler.send(email, Application.getString("517"), template); // Application should be used here to fill placeholder
		}
	}
}
