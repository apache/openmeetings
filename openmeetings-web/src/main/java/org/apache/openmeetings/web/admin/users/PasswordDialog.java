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
package org.apache.openmeetings.web.admin.users;

import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class PasswordDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final Form<String> form = new Form<>("form");
	private final PasswordTextField pass = new PasswordTextField("password", Model.of(""));
	private SerializableConsumer<AjaxRequestTarget> action = null;

	@Inject
	private UserDao userDao;

	public PasswordDialog(String id) {
		super(id);
	}

	public UserForm getUserForm() {
		return (UserForm)findParent(UsersPanel.class).get("form");
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("537"));

		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("54"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				final UserForm uf = getUserForm();
				if (uf.isAdminPassRequired()) {
					if (userDao.verifyPassword(getUserId(), pass.getConvertedInput())) {
						if (action != null) {
							action.accept(target);
						}
						PasswordDialog.this.close(target);
					} else {
						form.error(getString("error.bad.password"));
						target.add(feedback);
					}
				} else {
					PasswordDialog.this.close(target);
				}
			}
		}); // OK
		addButton(OmModalCloseButton.of());
		add(form.add(feedback.setOutputMarkupId(true), pass.setRequired(false).setLabel(new ResourceModel("110")).setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true)));
		super.onInitialize();
	}

	@Override
	public Modal<String> show(IPartialPageRequestHandler handler) {
		handler.add(pass.setModelObject(""));
		return super.show(handler);
	}

	public void setAction(SerializableConsumer<AjaxRequestTarget> action) {
		this.action = action;
	}
}
