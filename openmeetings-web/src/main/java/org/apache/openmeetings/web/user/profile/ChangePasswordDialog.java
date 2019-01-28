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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class ChangePasswordDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(ChangePasswordDialog.class, getWebAppRootKey());
	private DialogButton update;
	private DialogButton cancel;
	private final PasswordTextField current = new PasswordTextField("current", Model.of((String)null));
	private final PasswordTextField pass = new PasswordTextField("pass", Model.of((String)null));
	private final PasswordTextField pass2 = new PasswordTextField("pass2", Model.of((String)null));
	private StrongPasswordValidator passValidator;
	private final Form<String> form = new Form<String>("form") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onValidate() {
			String p = current.getConvertedInput();
			if (!Strings.isEmpty(p) && !getBean(UserDao.class).verifyPassword(getUserId(), p)) {
				error(getString("231"));
				// add random timeout
				try {
					Thread.sleep(6 + (long)(10 * Math.random() * 1000));
				} catch (InterruptedException e) {
					log.error("Unexpected exception while sleeping", e);
				}
			}
			String p1 = pass.getConvertedInput();
			if (!Strings.isEmpty(p1) && !p1.equals(pass2.getConvertedInput())) {
				error(getString("232"));
			}
			super.onValidate();
		}
	};
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));

	public ChangePasswordDialog(String id) {
		super(id, "");
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("327"));
		update = new DialogButton("update", new ResourceModel("327")) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isIndicating() {
				return true;
			}
		};
		cancel = new DialogButton("cancel", new ResourceModel("lbl.cancel"));
		passValidator = new StrongPasswordValidator(getBean(UserDao.class).get(getUserId()));
		add(form.add(
				current.setLabel(new ResourceModel("current.password")).setRequired(true)
				, pass.setLabel(new ResourceModel("328")).add(passValidator)
				, pass2.setLabel(new ResourceModel("116"))
				, feedback.setOutputMarkupId(true)
				));
		super.onInitialize();
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(update, cancel);
	}

	@Override
	public DialogButton getSubmitButton() {
		return update;
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
			getBean(UserDao.class).update(getBean(UserDao.class).get(getUserId()), pass.getModelObject(), getUserId());
		} catch (Exception e) {
			error(e.getMessage());
			target.add(feedback);
		}
	}
}
