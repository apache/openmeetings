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
package org.apache.openmeetings.web.pages;

import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.IUpdatable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

public class InvitationPasswordDialog extends Modal<Invitation> {
	private static final long serialVersionUID = 1L;
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final Form<Void> form = new Form<>("form");
	private final PasswordTextField password = new PasswordTextField("password", Model.of((String)null));
	private final IUpdatable comp;

	public InvitationPasswordDialog(String id, IUpdatable comp) {
		super(id);
		this.comp = comp;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("230"));
		setCloseOnEscapeKey(false);
		setBackdrop(Backdrop.STATIC);

		password.add(new IValidator<String>(){
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> validatable) {
				if (!CryptProvider.get().verify(validatable.getValue(), WebSession.get().getInvitation().getPassword())) {
					validatable.error(new ValidationError(getString("error.bad.password")));
				}
			}
		});
		add(form.add(password, feedback.setOutputMarkupId(true)));
		AjaxButton ab = new AjaxButton("submit", form) { //FAKE button so "submit-on-enter" works as expected
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				InvitationPasswordDialog.this.onSubmit(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				InvitationPasswordDialog.this.onError(target);
			}
		};
		form.add(ab);
		form.setDefaultButton(ab);
		password.setLabel(new ResourceModel("110"));
		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("537"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				InvitationPasswordDialog.this.onError(target);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				InvitationPasswordDialog.this.onSubmit(target);
			}
		}); //check
		super.onInitialize();
		Invitation i = WebSession.get().getInvitation();
		show(i != null && i.isPasswordProtected());
	}

	@Override
	public Modal<Invitation> show(IPartialPageRequestHandler handler) {
		password.setModelObject(null);
		return super.show(handler);
	}

	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	protected void onSubmit(AjaxRequestTarget target) {
		comp.update(target);
		close(target);
	}
}
