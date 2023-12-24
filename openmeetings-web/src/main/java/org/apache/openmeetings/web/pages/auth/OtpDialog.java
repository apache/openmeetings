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

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.web.app.OtpManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;

import org.apache.wicket.util.string.Strings;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerAjaxButton;
import jakarta.inject.Inject;

public class OtpDialog extends Modal<User> {
	private static final long serialVersionUID = 1L;
	private static final String TYPE_OTP = "type-otp";
	private static final String TYPE_FALLBACK = "type-fallback";
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final OtpForm form = new OtpForm("form");
	private final RadioGroup<String> radioGroup = new RadioGroup<>("type", Model.of(TYPE_OTP));

	@Inject
	private OtpManager otpManager;
	@Inject
	private UserDao userDao;

	public OtpDialog(String id, IModel<User> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("otp.label"));
		setUseCloseHandler(true);

		addButton(new SpinnerAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("54"), form, Buttons.Type.Outline_Primary)); // OK
		addButton(OmModalCloseButton.of());

		add(form);
		super.onInitialize();
	}

	@Override
	public Modal<User> show(IPartialPageRequestHandler handler) {
		form.reset(handler);
		return super.show(handler);
	}

	private SignInDialog getSignin() {
		return (SignInDialog)getPage().get("signin");
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler) {
		getSignin().show(handler);
	}

	private class OtpForm extends Form<String> {
		private static final long serialVersionUID = 1L;
		private final WebMarkupContainer otpBlock = new WebMarkupContainer("otp-block");
		private final WebMarkupContainer fallbackBlock = new WebMarkupContainer("fallback-block");
		private final RequiredTextField<String> otpField = new RequiredTextField<>("otp", Model.of("")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String[] getInputTypes() {
				return new String[]{"number"};
			}
		};
		private final RequiredTextField<String> fallbackField = new RequiredTextField<>("fallback", Model.of(""));

		public OtpForm(String id) {
			super(id);
		}

		@Override
		protected void onInitialize() {
			super.onInitialize();
			add(feedback.setOutputMarkupId(true));
			add(otpBlock.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
			otpBlock.add(otpField.setLabel(new ResourceModel("otp.label")));
			add(fallbackBlock.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
			fallbackBlock.add(fallbackField.setLabel(new ResourceModel("otp.fallback.label")));
			add(radioGroup.add(new Radio<>(TYPE_OTP, Model.of(TYPE_OTP)))
					.add(new Radio<>(TYPE_FALLBACK, Model.of(TYPE_FALLBACK)))
					.setOutputMarkupId(true));
			radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					updateForm(target);
				}
			});
			add(new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					OtpForm.this.onSubmit(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					OtpForm.this.onError(target);
				}
			});
		}

		@Override
		protected void onError() {
			RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(this::onError);
		}

		private void onError(AjaxRequestTarget target) {
			SignInDialog.penalty();
			target.add(feedback);
		}

		@Override
		protected void onSubmit() {
			RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(this::onSubmit);
		}

		@Override
		protected void onValidate() {
			final User u = OtpDialog.this.getModelObject();
			String otp = otpField.getConvertedInput();
			try {
				Integer.valueOf(otp);
				if (!otpManager.verify(u.getOtpSecret(), otp)) {
					error(getString("otp.invalid"));
				}
			} catch (NumberFormatException e) {
				// fallback
				String fallback = fallbackField.getConvertedInput();
				final String[] codes = Optional.of(u.getOtpRecoveryCodes())
						.map(str -> str.split(" "))
						.orElse(new String[]{});
				if (Stream.of(codes).anyMatch(str -> str.equalsIgnoreCase(fallback))) {
					// match found
					// let's remove recovery code
					u.setOtpRecoveryCodes(Stream.of(codes)
							.filter(str -> !Strings.isEmpty(str))
							.filter(str -> !str.equalsIgnoreCase(fallback))
							.collect(Collectors.joining(" "))
							);
					userDao.update(u, u.getId());
					return;
				}
				error(getString("otp.fallback.invalid"));
			}
		}

		private void onSubmit(AjaxRequestTarget target) {
			final User u = OtpDialog.this.getModelObject();
			WebSession ws = WebSession.get();
			ws.signIn(u);
			getSignin().finalStep(true, Type.USER, target);
		}

		private void updateForm(IPartialPageRequestHandler handler) {
			final boolean isOtp = TYPE_OTP.equals(radioGroup.getModelObject());
			otpBlock.setVisible(isOtp);
			fallbackBlock.setVisible(!isOtp);
			handler.add(otpBlock, fallbackBlock);
		}

		private void reset(IPartialPageRequestHandler handler) {
			radioGroup.setModelObject(TYPE_OTP);
			otpField.setModelObject("");
			fallbackField.setModelObject("");
			handler.add(radioGroup);
			updateForm(handler);
		}
	}
}
