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

import static org.apache.openmeetings.db.util.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.WEB_DATE_PATTERN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.wicket.datetime.markup.html.basic.DateLabel.forDatePattern;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.service.mail.EmailManager;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.ComunityUserForm;
import org.apache.openmeetings.web.common.GeneralUserForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.select2.ChoiceProvider;
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
	private static final Logger log = Red5LoggerFactory.getLogger(UserForm.class, webAppRootKey);
	private final WebMarkupContainer listContainer;
	private final WebMarkupContainer domain = new WebMarkupContainer("domain");
	private GeneralUserForm generalForm;
	private final RequiredTextField<String> login = new RequiredTextField<String>("login");
	private final MessageDialog warning;
	private final DropDownChoice<Long> domainId = new DropDownChoice<Long>("domainId");

	public UserForm(String id, WebMarkupContainer listContainer, final User user, MessageDialog warning) {
		super(id, new CompoundPropertyModel<User>(user));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
		this.warning = warning;
		// Add form fields
		addFormFields();

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ONE_SECOND));
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		generalForm.updateModelObject(getModelObject(), true);
	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		User u = getModelObject();
		try {
			boolean isNew = (u.getId() == null);
			u = getBean(UserDao.class).update(u, generalForm.getPasswordField().getConvertedInput(), getUserId());
			boolean sendEmailAtRegister = (1 == getBean(ConfigurationDao.class).getConfValue("sendEmailAtRegister", Integer.class, "0"));
			if (isNew && sendEmailAtRegister) {
				String link = getBean(ConfigurationDao.class).getBaseUrl();
				String sendMail = getBean(EmailManager.class).sendMail(login.getValue(),
						generalForm.getPasswordField().getConvertedInput(), generalForm.getEmail(), link, false, null);
				if (!sendMail.equals("success")) {
					throw new Exception("Mail for new user is not sent");
				}
			}
		} catch (Exception e) {
			// FIXME update feedback with the error details
			log.error("[onSaveSubmit]: ", e);
		}
		setModelObject(u);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
		target.appendJavaScript("omUserPanelInit();");
		if (u.getGroupUsers().isEmpty()) {
			warning.open(target);
		}
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		UserDao userDao = getBean(UserDao.class);
		setModelObject(userDao.getNewUserInstance(userDao.get(getUserId())));
		update(target);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		User user = getModelObject();
		if (user.getId() != null) {
			user = getBean(UserDao.class).get(user.getId());
		} else {
			user = getBean(UserDao.class).getNewUserInstance(null);
		}
		setModelObject(user);
		update(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		UserDao userDao = getBean(UserDao.class);
		userDao.delete(getModelObject(), getUserId());
		setModelObject(userDao.getNewUserInstance(userDao.get(getUserId())));
		update(target);
	}

	/**
	 * Add the fields to the form
	 */
	private void addFormFields() {
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		login.setLabel(Model.of(Application.getString(132)));
		add(login.add(minimumLength(getMinLoginLength(cfgDao))));

		add(generalForm = new GeneralUserForm("general", getModel(), true));

		add(new DropDownChoice<Type>("type", Arrays.asList(Type.values())).add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateDomain(target);
			}
		}));
		update(null);
		add(domain.add(domainId).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));
		add(new Label("ownerId"));
		add(forDatePattern("inserted", WEB_DATE_PATTERN));
		add(forDatePattern("updated", WEB_DATE_PATTERN));

		add(new CheckBox("forceTimeZoneCheck"));

		add(new Select2MultiChoice<Right>("rights", null, new ChoiceProvider<Right>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(Right choice) {
				return choice.name();
			}

			@Override
			public String getIdValue(Right choice) {
				return choice.name();
			}

			@Override
			public void query(String term, int page, Response<Right> response) {
				for (Right r : Right.values()) {
					if (Strings.isEmpty(term) || r.name().contains(term)) {
						response.add(r);
					}
				}
			}

			@Override
			public Collection<Right> toChoices(Collection<String> ids) {
				Collection<Right> rights = new ArrayList<>(ids.size());
				for (String id : ids) {
					rights.add(Right.valueOf(id));
				}
				return rights;
			}
		}));
		add(new ComunityUserForm("comunity", getModel()));
	}

	public void updateDomain(AjaxRequestTarget target) {
		User u = getModelObject();
		final Map<Long, String> values = new Hashtable<Long, String>();
		List<Long> ids = new ArrayList<Long>();
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
			target.appendJavaScript("omUserPanelInit();");
		}
	}
	
	@Override
	protected void onValidate() {
		User u = getModelObject();
		if(!getBean(UserDao.class).checkLogin(login.getConvertedInput(), u.getType(), u.getDomainId(), u.getId())) {
			error(Application.getString(105));
		}
	}

	@Override
	protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onNewError(AjaxRequestTarget target, Form<?> form) {
		//ignore validation errors
		onNewSubmit(target, form);
	}

	@Override
	protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
		//ignore validation errors
		onDeleteSubmit(target, form);
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(false);
	}
}
