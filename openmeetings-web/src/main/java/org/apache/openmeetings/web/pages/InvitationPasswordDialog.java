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

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.IUpdatable;
import org.apache.openmeetings.web.util.NonClosableDialog;
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

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class InvitationPasswordDialog extends NonClosableDialog<Invitation> {
	private static final long serialVersionUID = 1L;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private DialogButton check;
	private final Form<Void> form = new Form<>("form");
	private final PasswordTextField password = new PasswordTextField("password", Model.of((String)null));
	private final IUpdatable comp;

	public InvitationPasswordDialog(String id, IUpdatable comp) {
		super(id, "");
		this.comp = comp;
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
				InvitationPasswordDialog.this.onSubmit(target, check);
				InvitationPasswordDialog.this.close(target, null);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				InvitationPasswordDialog.this.onError(target, check);
			}
		};
		form.add(ab);
		form.setDefaultButton(ab);
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("230"));
		password.setLabel(new ResourceModel("110"));
		check = new DialogButton("check", getString("537"));
		super.onInitialize();
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		Invitation i = WebSession.get().getInvitation();
		behavior.setOption("autoOpen", i != null && i.isPasswordProtected());
		behavior.setOption("resizable", false);
	}

	@Override
	public boolean isDefaultCloseEventEnabled() {
		return false;
	}

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		password.setModelObject(null);
	}

	@Override
	public DialogButton getSubmitButton() {
		return check;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(check);
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
		comp.update(target);
	}
}
