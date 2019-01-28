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

import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.util.NonClosableDialog;
import org.apache.openmeetings.web.util.NonClosableMessageDialog;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class ResetPasswordDialog extends NonClosableDialog<String> {
	private static final long serialVersionUID = 1L;
	private DialogButton resetBtn;
	private Form<String> form = new ResetForm("form");
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private PasswordTextField password;
	private final User user;
	MessageDialog confirmReset;

	public ResetPasswordDialog(String id, final User user) {
		super(id, "");
		this.user = user;
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("325"));
		resetBtn = new DialogButton("reset", getString("327"));
		add(form);
		confirmReset = new NonClosableMessageDialog("confirmReset", getString("325"), getString("332")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				setResponsePage(Application.get().getSignInPageClass());
			}
		};
		add(confirmReset);

		super.onInitialize();
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("autoOpen", true);
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(resetBtn);
	}

	@Override
	public DialogButton getSubmitButton() {
		return resetBtn;
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
		try {
			getBean(UserDao.class).resetPassword(user, password.getConvertedInput());
		} catch (Exception e) {
			error(e.getMessage());
		}
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		if (resetBtn.equals(button)) {
			confirmReset.open(handler);
		} else {
			setResponsePage(Application.get().getSignInPageClass());
		}
	}

	private class ResetForm extends Form<String> {
		private static final long serialVersionUID = 1L;
		private TextField<String> login;
		private PasswordTextField confirmPassword;

		private ResetForm(String id) {
			super(id);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(feedback.setOutputMarkupId(true));
			add(login = new TextField<>("login", Model.of(user.getLogin())));
			login.setOutputMarkupId(true);
			add(password = new PasswordTextField("password", new Model<String>()));
			password.setLabel(new ResourceModel("328")).setOutputMarkupId(true);
			password.setRequired(false).add(new StrongPasswordValidator(user));
			add(confirmPassword = new PasswordTextField("confirmPassword", new Model<String>()));
			confirmPassword.setLabel(new ResourceModel("116")).setOutputMarkupId(true);

			add(new AjaxButton("submit") { // FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					ResetPasswordDialog.this.onSubmit(target, resetBtn);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					ResetPasswordDialog.this.onError(target, resetBtn);
				}
			});
		}

		@Override
		protected void onValidate() {
			String pass = password.getConvertedInput();
			if (pass != null && !pass.isEmpty() && !pass.equals(confirmPassword.getConvertedInput())) {
				error(getString("232"));
			}
			super.onValidate();
		}
	}
}
