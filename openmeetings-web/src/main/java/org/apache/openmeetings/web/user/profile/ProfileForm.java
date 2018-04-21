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
import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ComunityUserForm;
import org.apache.openmeetings.web.common.FormActionsPanel;
import org.apache.openmeetings.web.common.GeneralUserForm;
import org.apache.openmeetings.web.common.UploadableProfileImagePanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.wicket.jquery.ui.form.button.ButtonBehavior;

public class ProfileForm extends Form<User> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ProfileForm.class);
	private final PasswordTextField passwd = new PasswordTextField("passwd", new Model<String>());
	private final GeneralUserForm userForm;
	private final ChangePasswordDialog chPwdDlg;
	private FormActionsPanel<User> actions;

	@SpringBean
	private UserDao userDao;

	public ProfileForm(String id, final ChangePasswordDialog chPwdDlg) {
		super(id);
		setModel(new CompoundPropertyModel<>(userDao.get(getUserId())));
		userForm = new GeneralUserForm("general", getModel(), false);
		this.chPwdDlg = chPwdDlg;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(passwd.setLabel(Model.of(getString("current.password"))).setRequired(true));

		add(actions = new FormActionsPanel<User>("buttons", this) {
			private static final long serialVersionUID = 1L;

			private void refreshUser() {
				User u = getModelObject();
				if (u.getId() != null) {
					u = userDao.get(u.getId());
				} else {
					u = new User();
				}
				setModelObject(u);
			}

			@Override
			protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					userDao.update(getModelObject(), null, getUserId());
				} catch (Exception e) {
					error(e.getMessage());
				}
				refreshUser();
				target.add(ProfileForm.this);
			}

			@Override
			protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
				refreshUser();
				target.add(ProfileForm.this);
			}

			@Override
			protected void onPurgeSubmit(AjaxRequestTarget target, Form<?> form) {
				userDao.purge(getModelObject(), getUserId());
				WebSession.get().invalidateNow();
				setResponsePage(Application.get().getSignInPageClass());
			}
		});
		add(new WebMarkupContainer("changePwd").add(new ButtonBehavior("#changePwd"), new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				chPwdDlg.open(target);
			}
		}));
		add(userForm);
		add(new UploadableProfileImagePanel("img", getUserId()));
		add(new ComunityUserForm("comunity", getModel()));

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ONE_SECOND));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		actions.setPurgeVisible(true);
	}

	@Override
	protected void onValidate() {
		String p = passwd.getConvertedInput();
		if (!Strings.isEmpty(p) && !userDao.verifyPassword(getModelObject().getId(), p)) {
			error(getString("231"));
			// add random timeout
			try {
				Thread.sleep(6 + (long)(10 * Math.random() * 1000));
			} catch (InterruptedException e) {
				log.error("Unexpected exception while sleeping", e);
			}
		}
		super.onValidate();
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(false);
	}
}
