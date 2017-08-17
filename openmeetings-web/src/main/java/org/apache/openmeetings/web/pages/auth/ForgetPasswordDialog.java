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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.directory.api.util.Strings;
import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.UserHelper;
import org.apache.openmeetings.service.mail.template.ResetPasswordTemplate;
import org.apache.openmeetings.web.app.Application;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
	private static final Logger log = Red5LoggerFactory.getLogger(ForgetPasswordDialog.class, webAppRootKey);
	private static final long serialVersionUID = 1L;
	private final DialogButton send = new DialogButton("send", Application.getString("317"));
	private final DialogButton cancel = new DialogButton("cancel", Application.getString("122"));
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final IValidator<String> emailValidator = RfcCompliantEmailAddressValidator.getInstance();
	private final RequiredTextField<String> name = new RequiredTextField<>("name", Model.of((String)null));
	private final RadioGroup<Type> rg = new RadioGroup<>("type", Model.of(Type.email));
	private final Label label = new Label("label", Model.of(Application.getString("315")));
	private final Captcha captcha = new Captcha("captcha");
	private Form<String> form;
	private SignInDialog s;
	final MessageDialog confirmDialog;

	enum Type {
		email
		, login
	}

	public ForgetPasswordDialog(String id) {
		super(id, Application.getString("312"));
		add(form = new Form<String>("form") {
			private static final long serialVersionUID = 1L;

			{
				add(feedback.setOutputMarkupId(true));
				add(label.setOutputMarkupId(true));
				add(name.setOutputMarkupId(true));
				add(captcha);
				add(rg.add(new Radio<>("email", Model.of(Type.email)).setOutputMarkupId(true))
						.add(new Radio<>("login", Model.of(Type.login)).setOutputMarkupId(true))
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
						ForgetPasswordDialog.this.onSubmit(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						ForgetPasswordDialog.this.onError(target);
					}
				});
				updateLabel(null);
			}

			@Override
			protected void onValidate() {
				String n = name.getConvertedInput();
				if (n != null) {
					IValidatable<String> val = new Validatable<>(n);
					Type type = rg.getModelObject();
					if (type == Type.email) {
						emailValidator.validate(val);
						if (!val.isValid()) {
							error(getString("234"));
						}
					}
					if (type == Type.login && n.length() < UserHelper.getMinLoginLength(getBean(ConfigurationDao.class))) {
						error(getString("104"));
					}
				}
			}
		});
		confirmDialog = new NonClosableMessageDialog("confirmDialog", Application.getString("312"), Application.getString("321")){
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				s.open(handler);
			}
		};
		add(confirmDialog);
	}

	private void updateLabel(IPartialPageRequestHandler handler) {
		String lbl = Application.getString(rg.getModelObject() == Type.email ? "315" : "316");
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
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		String nm = name.getModelObject();
		Type type = rg.getModelObject();
		resetUser(type == Type.email ? nm : "", type == Type.login ? nm : ""
			, getBean(ConfigurationDao.class).getBaseUrl() + getRequestCycle().urlFor(ResetPage.class, new PageParameters()).toString().substring(2));
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
	private static boolean resetUser(String email, String username, String appLink) {
		try {
			UserDao userDao = getBean(UserDao.class);
			log.debug("resetUser " + email);

			// check if Mail given
			if (!Strings.isEmpty(email)) {
				User us = userDao.getByEmail(email);
				if (us != null) {
					sendHashByUser(us, appLink, userDao);
					return true;
				}
			} else if (!Strings.isEmpty(username)) {
				User us = userDao.getByLogin(username, User.Type.user, null);
				if (us != null) {
					sendHashByUser(us, appLink, userDao);
					return true;
				}
			}
		} catch (Exception e) {
			log.error("[resetUser]", e);
		}
		return false;
	}

	private static void sendHashByUser(User us, String appLink, UserDao userDao) {
		log.debug("User: " + us.getLogin());
		us.setResethash(UUID.randomUUID().toString());
		us.setResetDate(new Date());
		userDao.update(us, null);
		String reset_link = appLink + "?hash=" + us.getResethash();

		String email = us.getAddress().getEmail();

		String template = ResetPasswordTemplate.getEmail(reset_link);

		getBean(MailHandler.class).send(email, Application.getString("517"), template);
	}

}
