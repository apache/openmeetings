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
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.urlForPage;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.mail.template.ResetPasswordTemplate;
import org.apache.openmeetings.web.common.Captcha;
import org.apache.openmeetings.web.pages.ResetPage;
import org.apache.openmeetings.web.util.NonClosableMessageDialog;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.Validatable;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class ForgetPasswordDialog extends AbstractFormDialog<String> {
	private static final Logger log = Red5LoggerFactory.getLogger(ForgetPasswordDialog.class, getWebAppRootKey());
	private static final long serialVersionUID = 1L;
	private DialogButton send;
	private DialogButton cancel;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final IValidator<String> emailValidator = RfcCompliantEmailAddressValidator.getInstance();
	private final RequiredTextField<String> name = new RequiredTextField<>("name", Model.of((String)null));
	private final RadioGroup<Type> rg = new RadioGroup<>("type", Model.of(Type.email));
	private final Label label = new Label("label", Model.of(""));
	private final Captcha captcha = new Captcha("captcha");
	private Form<String> form = new Form<String>("form") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(feedback.setOutputMarkupId(true));
			add(label.setDefaultModelObject(getString("315")).setOutputMarkupId(true));
			add(name.setOutputMarkupId(true));
			add(captcha);
			add(rg.add(new Radio<>("email", Model.of(Type.email)))
					.add(new Radio<>("login", Model.of(Type.login)))
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
					ForgetPasswordDialog.this.onSubmit(target, send);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					ForgetPasswordDialog.this.onError(target, send);
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
				if (type == Type.email) {
					emailValidator.validate(val);
					if (!val.isValid()) {
						error(getString("234"));
					}
				}
				if (type == Type.login && n.length() < getMinLoginLength()) {
					error(getString("104"));
				}
			}
		}
	};
	private SignInDialog s;
	MessageDialog confirmDialog;

	enum Type {
		email
		, login
	}

	public ForgetPasswordDialog(String id) {
		super(id, "");
	}

	@Override
	protected void onInitialize() {
		setTitle(new ResourceModel("312"));
		send = new DialogButton("send", getString("317"));
		cancel = new DialogButton("cancel", getString("lbl.cancel"));
		add(form);
		confirmDialog = new NonClosableMessageDialog("confirmDialog", getString("312"), getString("321")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				s.open(handler);
			}
		};
		add(confirmDialog);
		super.onInitialize();
	}

	private void updateLabel(IPartialPageRequestHandler handler) {
		String lbl = getString(rg.getModelObject() == Type.email ? "315" : "316");
		name.setLabel(Model.of(lbl));
		label.setDefaultModelObject(lbl);
		if (handler != null) {
			handler.add(name, label);
		}
	}

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		super.onOpen(handler);
		name.setModelObject(null);
		rg.setModelObject(Type.email);
		captcha.refresh(handler);
		handler.add(rg);
		updateLabel(handler);
	}

	@Override
	public boolean isDefaultCloseEventEnabled()	{
		return true;
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		if (send.equals(button)){
			confirmDialog.open(handler);
		} else {
			s.open(handler);
		}
	}

	public void setSignInDialog(SignInDialog s) {
		this.s = s;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(send, cancel);
	}

	@Override
	public DialogButton getSubmitButton() {
		return send;
	}

	@Override
	public Form<?> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target, DialogButton btn) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
		String nm = name.getModelObject();
		Type type = rg.getModelObject();
		resetUser(type == Type.email ? nm : "", type == Type.login ? nm : "");
	}

	/**
	 * reset a username by a given mail oder login by sending a mail to the
	 * registered EMail-Address
	 *
	 * @param email
	 * @param username
	 * @param appLink
	 * @return <code>true</code> in case reset was successful, <code>false</code> otherwise
	 */
	private boolean resetUser(String email, String username) {
		try {
			UserDao userDao = getBean(UserDao.class);
			log.debug("resetUser " + email);

			// check if Mail given
			if (!Strings.isEmpty(email)) {
				User us = userDao.getByEmail(email);
				if (us != null) {
					sendHashByUser(us, userDao);
					return true;
				}
			} else if (!Strings.isEmpty(username)) {
				User us = userDao.getByLogin(username, User.Type.user, null);
				if (us != null) {
					sendHashByUser(us, userDao);
					return true;
				}
			}
		} catch (Exception e) {
			log.error("[resetUser]", e);
		}
		return false;
	}

	private void sendHashByUser(User us, UserDao userDao) {
		log.debug("User: " + us.getLogin());
		us.setResethash(UUID.randomUUID().toString());
		us.setResetDate(new Date());
		userDao.update(us, null);
		String resetLink = urlForPage(ResetPage.class
				, new PageParameters().add("hash", us.getResethash())
				, getBaseUrl());

		String email = us.getAddress().getEmail();

		String template = ResetPasswordTemplate.getEmail(resetLink);

		getBean(MailHandler.class).send(email, getString("517"), template);
	}
}
