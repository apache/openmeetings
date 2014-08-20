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
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.WebSession;
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
import org.apache.wicket.util.time.Duration;

import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.Select2MultiChoice;
import com.vaynberg.wicket.select2.TextChoiceProvider;

/**
 * CRUD operations in form for {@link User}
 * 
 * @author swagner
 * 
 */
public class UserForm extends AdminBaseForm<User> {
	private static final long serialVersionUID = 1L;
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
		generalForm.updateModel(getModelObject());
	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		User u = getModelObject();
		try {
			u = getBean(UserDao.class).update(u, generalForm.getPasswordField().getConvertedInput(), getUserId());
		} catch (Exception e) {
			// FIXME update feedback with the error details
		}
		setModelObject(u);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
		target.appendJavaScript("omUserPanelInit();");
		if (u.getOrganisation_users().isEmpty()) {
			warning.open(target);
		}
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		UserDao usersDaoImpl = getBean(UserDao.class);
		setModelObject(usersDaoImpl.getNewUserInstance(usersDaoImpl.get(getUserId())));
		update(target);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		User user = getModelObject();
		if (user.getId() != null) {
			user = getBean(UserDao.class).get(user.getId());
		} else {
			user = new User();
		}
		setModelObject(user);
		update(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		UserDao usersDaoImpl = getBean(UserDao.class);
		usersDaoImpl.delete(getModelObject(), getUserId());
		setModelObject(usersDaoImpl.getNewUserInstance(usersDaoImpl.get(getUserId())));
		update(target);
	}

	/**
	 * Add the fields to the form
	 */
	private void addFormFields() {
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		login.setLabel(Model.of(WebSession.getString(132)));
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
		add(forDatePattern("starttime", WEB_DATE_PATTERN));
		add(forDatePattern("updatetime", WEB_DATE_PATTERN));

		add(new CheckBox("forceTimeZoneCheck"));

		add(new Select2MultiChoice<Right>("rights", null, new TextChoiceProvider<Right>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getDisplayText(Right choice) {
				return choice.name();
			}

			@Override
			protected Object getId(Right choice) {
				return choice.name();
			}

			@Override
			public void query(String term, int page, Response<Right> response) {
				for (Right r : Right.values()) {
					if (r.name().contains(term)) {
						response.add(r);
					}
				}
			}

			@Override
			public Collection<Right> toChoices(Collection<String> ids) {
				Collection<Right> rights = new ArrayList<User.Right>(ids.size());
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

			public Object getDisplayValue(Long object) {
				return values.get(object);
			}

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
		if(!getBean(UserDao.class).checkUserLogin(login.getConvertedInput(), getModelObject().getId())) {
			error(WebSession.getString(105));
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
