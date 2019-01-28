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

import static org.apache.openmeetings.db.dao.user.UserDao.getNewUserInstance;
import static org.apache.openmeetings.db.util.AuthLevelUtil.hasAdminLevel;
import static org.apache.openmeetings.db.util.AuthLevelUtil.hasGroupAdminLevel;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSendRegisterEmail;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.service.mail.EmailManager;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.common.ComunityUserForm;
import org.apache.openmeetings.web.common.GeneralUserForm;
import org.apache.openmeetings.web.util.DateLabel;
import org.apache.openmeetings.web.util.RestrictiveChoiceProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2MultiChoice;

import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

/**
 * CRUD operations in form for {@link User}
 *
 * @author swagner
 *
 */
public class UserForm extends AdminBaseForm<User> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(UserForm.class, getWebAppRootKey());
	private final WebMarkupContainer mainContainer = new WebMarkupContainer("adminForm");
	private final WebMarkupContainer listContainer;
	private final WebMarkupContainer domain = new WebMarkupContainer("domain");
	private GeneralUserForm generalForm;
	private final RequiredTextField<String> login = new RequiredTextField<>("login");
	private StrongPasswordValidator passValidator;
	private final PasswordTextField password = new PasswordTextField("password", new Model<String>());
	private final MessageDialog warning;
	private final DropDownChoice<Long> domainId = new DropDownChoice<>("domainId");
	private final PasswordDialog adminPass = new PasswordDialog("adminPass");

	public UserForm(String id, WebMarkupContainer listContainer, final User user, MessageDialog warning) {
		super(id, new CompoundPropertyModel<>(user));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
		this.warning = warning;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(mainContainer);
		mainContainer.add(generalForm = new GeneralUserForm("general", getModel(), true));
		mainContainer.add(password.setResetPassword(false).setLabel(new ResourceModel("110")).setRequired(false)
				.add(passValidator = new StrongPasswordValidator(getModelObject())));
		login.setLabel(new ResourceModel("108"));
		mainContainer.add(login.add(minimumLength(getMinLoginLength())));

		mainContainer.add(new DropDownChoice<>("type", Arrays.asList(Type.values())).add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateDomain(target);
			}
		}));
		update(null);
		mainContainer.add(domain.add(domainId).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
		mainContainer.add(new Label("ownerId"));
		mainContainer.add(new DateLabel("inserted"));
		mainContainer.add(new DateLabel("updated"));

		mainContainer.add(new CheckBox("forceTimeZoneCheck"));

		mainContainer.add(new Select2MultiChoice<>("rights", null, new RestrictiveChoiceProvider<Right>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(Right choice) {
				return choice.name();
			}

			@Override
			public String toId(Right choice) {
				return choice.name();
			}

			@Override
			public void query(String term, int page, Response<Right> response) {
				boolean isGroupAdmin = hasGroupAdminLevel(getRights());
				for (Right r : Right.values()) {
					if (Right.GroupAdmin == r) {
						continue;
					}
					if (isGroupAdmin && (Right.Admin == r || Right.Soap == r)) {
						continue;
					}
					if (Strings.isEmpty(term) || r.name().contains(term)) {
						response.add(r);
					}
				}
			}

			@Override
			public Right fromId(String id) {
				return Right.valueOf(id);
			}
		}));
		mainContainer.add(new ComunityUserForm("comunity", getModel()));
		add(adminPass);
		remove(validationBehavior);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		onModelChanged();
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		boolean nd = !getModelObject().isDeleted();
		boolean isNew = getModelObject().getId() == null;
		mainContainer.setEnabled(nd);
		setSaveVisible(nd);
		setDelVisible(nd && !isNew);
		setRestoreVisible(!nd);
		setPurgeVisible(!isNew);
		password.setModelObject(null);
		generalForm.updateModelObject(getModelObject(), true);
		passValidator.setUser(getModelObject());
	}

	@Override
	protected void onRestoreSubmit(AjaxRequestTarget target, Form<?> form) {
		getModelObject().setDeleted(false);
		onSaveSubmit(target, form);
	}

	@Override
	protected void onPurgeSubmit(AjaxRequestTarget target, Form<?> form) {
		if (isAdminPassRequired()) {
			adminPass.setAction((SerializableConsumer<AjaxRequestTarget>)t -> purgeUser(t));
			adminPass.open(target);
		} else {
			purgeUser(target);
		}
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		if (isAdminPassRequired()) {
			adminPass.setAction((SerializableConsumer<AjaxRequestTarget>)t -> saveUser(t, password.getModelObject()));
			adminPass.open(target);
		} else {
			saveUser(target, password.getConvertedInput());
		}
	}

	private static boolean checkLevel(Set<User.Right> rights) {
		return hasAdminLevel(rights) || AuthLevelUtil.hasWebServiceLevel(rights);
	}

	boolean isAdminPassRequired() {
		User u = getModelObject();
		UserDao dao = getBean(UserDao.class);
		User ou = dao.get(u.getId());
		return checkLevel(u.getRights()) || (ou != null && checkLevel(ou.getRights()));
	}

	private void purgeUser(AjaxRequestTarget target) {
		getBean(UserDao.class).purge(getModelObject(), getUserId());
		updateForm(target);
	}

	private void saveUser(AjaxRequestTarget target, String pass) {
		User u = getModelObject();
		final UserDao dao = getBean(UserDao.class);
		final boolean isNew = u.getId() == null;
		boolean sendEmailAtRegister = isSendRegisterEmail();
		if (isNew && sendEmailAtRegister) {
			u.setActivatehash(UUID.randomUUID().toString());
		}
		try {
			u = dao.update(u, pass, getUserId());
		} catch (Exception e) {
			log.error("[onSaveSubmit]: ", e);
		}
		if (isNew && sendEmailAtRegister) {
			String email = u.getAddress().getEmail();
			getBean(EmailManager.class).sendMail(login.getValue(), email, u.getActivatehash(), false, null);
		}
		updateForm(target);
		if (u.getGroupUsers().isEmpty()) {
			warning.open(target);
		}
	}

	private void updateForm(AjaxRequestTarget target) {
		User u = getModelObject();
		final UserDao dao = getBean(UserDao.class);
		setModelObject(dao.get(u.getId()));
		setNewVisible(false);
		target.add(this, listContainer);
		reinitJs(target);
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		UserDao userDao = getBean(UserDao.class);
		setModelObject(getNewUserInstance(userDao.get(getUserId())));
		update(target);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		User user = getModelObject();
		if (user.getId() != null) {
			user = getBean(UserDao.class).get(user.getId());
		} else {
			user = getNewUserInstance(null);
		}
		setModelObject(user);
		update(target);
	}

	private void deleteUser(AjaxRequestTarget target) {
		UserDao userDao = getBean(UserDao.class);
		userDao.delete(getModelObject(), getUserId());
		setModelObject(getNewUserInstance(userDao.get(getUserId())));
		update(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		if (isAdminPassRequired()) {
			adminPass.setAction((SerializableConsumer<AjaxRequestTarget>)t -> deleteUser(t));
			adminPass.open(target);
		} else {
			deleteUser(target);
		}
	}

	public void updateDomain(AjaxRequestTarget target) {
		User u = getModelObject();
		final Map<Long, String> values = new HashMap<>();
		List<Long> ids = new ArrayList<>();
		if (u.getType() == Type.ldap) {
			for (LdapConfig c : getBean(LdapConfigDao.class).getActive()) {
				ids.add(c.getId());
				values.put(c.getId(), c.getName());
			}
		}
		if (u.getType() == Type.oauth) {
			for (OAuthServer s : getBean(OAuth2Dao.class).getActive()) {
				ids.add(s.getId());
				values.put(s.getId(), s.getName());
			}
		}
		domainId.setChoices(ids);
		domainId.setChoiceRenderer(new ChoiceRenderer<Long>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(Long object) {
				return values.get(object);
			}

			@Override
			public String getIdValue(Long object, int index) {
				return "" + object;
			}
		});
		domain.setVisible(u.getType() == Type.ldap || u.getType() == Type.oauth);
		if (target != null) {
			target.add(domain);
		}
	}

	public void update(AjaxRequestTarget target) {
		updateDomain(target);
		if (target != null) {
			target.add(this, listContainer);
			reinitJs(target);
		}
	}

	@Override
	protected void onValidate() {
		User u = getModelObject();
		if(!getBean(UserDao.class).checkLogin(login.getConvertedInput(), u.getType(), u.getDomainId(), u.getId())) {
			error(getString("error.login.inuse"));
		}
		super.onValidate();
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(false);
	}
}
