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

import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_SRC;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.OtpManager;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.pages.auth.SignInDialog;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import org.apache.wicket.util.string.Strings;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerAjaxButton;
import jakarta.inject.Inject;

public class ToggleOtpDialog extends Modal<User> {
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final PasswordTextField current = new PasswordTextField("current", Model.of((String)null));
	private final WebMarkupContainer qr = new WebMarkupContainer("qr");
	private final TextArea<String> codesArea = new TextArea<>("codes", Model.of(""));
	private String secret;
	private String[] codes;

	@Inject
	private OtpManager otpManager;
	@Inject
	private UserDao userDao;

	public ToggleOtpDialog(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("otp.enable"));
		setUseCloseHandler(true);

		final Form<String> form = new Form<>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate() {
				final User u = ToggleOtpDialog.this.getModelObject();
				String p = current.getConvertedInput();
				if (!Strings.isEmpty(p) && !userDao.verifyPassword(u.getId(), p)) {
					error(getString("231"));
					SignInDialog.penalty();
				}
				super.onValidate();
			}
			};
		add(form.add(current.setLabel(new ResourceModel("current.password")).setOutputMarkupId(true))
				.add(qr.setOutputMarkupId(true))
				.add(codesArea.setOutputMarkupId(true))
				.add(feedback.setOutputMarkupId(true)));

		addButton(new SpinnerAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("otp.enable"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				final User u = ToggleOtpDialog.this.getModelObject();
				u.setOtpSecret(secret);
				u.setOtpRecoveryCodes(String.join(" ", codes));
				EditProfileForm editForm = (EditProfileForm)findParent(EditProfilePanel.class).get("form");
				editForm.updateOtpButton(true, target);
				userDao.update(u, u.getId());
				ToggleOtpDialog.this.close(target);
			}
		});
		addButton(OmModalCloseButton.of());
		super.onInitialize();
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler) {
		secret = null;
		current.setModelObject(null);
		qr.add(AttributeModifier.remove(PARAM_SRC));
		codesArea.setModelObject("");
		handler.add(current, qr, codesArea);
	}

	@Override
	public Modal<User> show(IPartialPageRequestHandler target) {
		secret = otpManager.generateSecret();
		codes = otpManager.getRecoveryCodes();
		final User u = getModelObject();
		current.setModelObject(null);
		qr.add(AttributeModifier.replace(PARAM_SRC, otpManager.getQr(u.getAddress().getEmail(), secret)));
		codesArea.setModelObject(String.join("\n", codes));
		target.add(current, qr, codesArea);
		return super.show(target);
	}
}
