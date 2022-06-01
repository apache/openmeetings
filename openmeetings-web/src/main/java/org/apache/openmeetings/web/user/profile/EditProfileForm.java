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

import java.time.Duration;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.CommunityUserForm;
import org.apache.openmeetings.web.common.FormActionsPanel;
import org.apache.openmeetings.web.common.GeneralUserForm;
import org.apache.openmeetings.web.common.UploadableProfileImagePanel;
import org.apache.openmeetings.web.pages.PrivacyPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;

public class EditProfileForm extends Form<User> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(EditProfileForm.class);
	private final PasswordTextField passwd = new PasswordTextField("passwd", new Model<>());
	private final GeneralUserForm userForm;
	private final ChangePasswordDialog chPwdDlg;
	private boolean checkPassword;
	private FormActionsPanel<User> actions;

	@SpringBean
	private UserDao userDao;

	public EditProfileForm(String id, final ChangePasswordDialog chPwdDlg) {
		super(id);
		setModel(new CompoundPropertyModel<>(userDao.get(getUserId())));
		userForm = new GeneralUserForm("general", getModel(), false);
		this.chPwdDlg = chPwdDlg;
		this.checkPassword = User.Type.OAUTH != getModelObject().getType();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(passwd.setLabel(new ResourceModel("current.password")).setRequired(true).setVisible(checkPassword));

		add(actions = new FormActionsPanel<>("buttons", this) {
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
				target.add(EditProfileForm.this);
			}

			@Override
			protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
				refreshUser();
				target.add(EditProfileForm.this);
			}

			@Override
			protected void onPurgeSubmit(AjaxRequestTarget target, Form<?> form) {
				userDao.purge(getModelObject(), getUserId());
				WebSession.get().invalidateNow();
				setResponsePage(Application.get().getSignInPageClass());
			}
		});
		add(new BootstrapAjaxLink<>("changePwd", Model.of(""), Buttons.Type.Outline_Danger, new ResourceModel("327")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				chPwdDlg.show(target);
			}
		}.setVisible(checkPassword));
		add(userForm);
		add(new UploadableProfileImagePanel("img", getUserId()));
		add(new CommunityUserForm("comunity", getModel()));

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ofSeconds(1)));
		add(new BookmarkablePageLink<>("link", PrivacyPage.class));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		actions.setPurgeVisible(true);
	}

	@Override
	protected void onValidate() {
		if (checkPassword) {
			String p = passwd.getConvertedInput();
			if (!Strings.isEmpty(p) && !userDao.verifyPassword(getModelObject().getId(), p)) {
				error(getString("231"));
				// add random timeout
				try {
					Thread.sleep(6 + (long)(10 * Math.random() * 1000));
				} catch (InterruptedException e) {
					log.error("Unexpected exception while sleeping", e);
					Thread.currentThread().interrupt();
				}
			}
		}
		super.onValidate();
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(false);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(OnDomReadyHeaderItem.forScript("$('.profile-edit-form .my-info').off().click(function() {showUserInfo(" + getUserId() + ");});"));
	}
}
