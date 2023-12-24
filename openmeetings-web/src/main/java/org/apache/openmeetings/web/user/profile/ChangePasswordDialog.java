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
package org.apache.openmeetings.web.user.profile;

import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.pages.auth.SignInDialog;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import org.apache.wicket.util.string.Strings;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerAjaxButton;
import jakarta.inject.Inject;

public class ChangePasswordDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private final PasswordTextField current = new PasswordTextField("current", Model.of((String)null));
	private final PasswordTextField pass = new PasswordTextField("pass", Model.of((String)null));
	private final PasswordTextField pass2 = new PasswordTextField("pass2", Model.of((String)null));
	private final Form<String> form = new Form<>("form") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onValidate() {
			String p = current.getConvertedInput();
			if (!Strings.isEmpty(p) && !userDao.verifyPassword(getUserId(), p)) {
				error(getString("231"));
				SignInDialog.penalty();
			}
			String p1 = pass.getConvertedInput();
			if (!Strings.isEmpty(p1) && !p1.equals(pass2.getConvertedInput())) {
				error(getString("232"));
			}
			super.onValidate();
		}
	};
	private final NotificationPanel feedback = new NotificationPanel("feedback");

	@Inject
	private UserDao userDao;

	public ChangePasswordDialog(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("327"));

		addButton(new SpinnerAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("327"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				try {
					userDao.update(userDao.get(getUserId()), pass.getModelObject(), getUserId());
					ChangePasswordDialog.this.close(target);
				} catch (Exception e) {
					error(e.getMessage());
					target.add(feedback);
				}
			}
		}); //send
		addButton(OmModalCloseButton.of());
		StrongPasswordValidator passValidator = new StrongPasswordValidator(userDao.get(getUserId()));
		add(form.add(
				current.setLabel(new ResourceModel("current.password")).setRequired(true).setOutputMarkupId(true)
				, pass.setLabel(new ResourceModel("328")).add(passValidator).setOutputMarkupId(true)
				, pass2.setLabel(new ResourceModel("116")).setOutputMarkupId(true)
				, feedback.setOutputMarkupId(true)
				));
		super.onInitialize();
	}

	@Override
	public Modal<String> show(IPartialPageRequestHandler target) {
		target.add(
			current.setModelObject("")
			, pass.setModelObject("")
			, pass2.setModelObject("")
			, feedback
		);
		return super.show(target);
	}
}
