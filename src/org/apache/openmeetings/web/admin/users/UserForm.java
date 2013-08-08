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

import static org.apache.openmeetings.OpenmeetingsVariables.WEB_DATE_PATTERN;
import static org.apache.openmeetings.utils.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.wicket.datetime.markup.html.basic.DateLabel.forDatePattern;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.util.Arrays;

import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ComunityUserForm;
import org.apache.openmeetings.web.common.GeneralUserForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

/**
 * CRUD operations in form for {@link User}
 * 
 * @author swagner
 * 
 */
public class UserForm extends AdminBaseForm<User> {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer listContainer;
	private GeneralUserForm generalForm;
	private RequiredTextField<String> login;
	private MessageDialog warning;

	public UserForm(String id, WebMarkupContainer listContainer, final User user, MessageDialog warning) {
		super(id, new CompoundPropertyModel<User>(user));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
		this.warning = warning;
		// Add form fields
		addFormFields();

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown", Duration.ONE_SECOND);
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		User u = getModelObject();
		try {
			u = getBean(UsersDao.class).update(u, generalForm.getPasswordField().getConvertedInput(), getUserId());
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
		UsersDao usersDaoImpl = getBean(UsersDao.class);
		setModelObject(usersDaoImpl.getNewUserInstance(usersDaoImpl.get(getUserId())));
		target.add(this);
		target.appendJavaScript("omUserPanelInit();");
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		User user = getModelObject();
		if (user.getUser_id() != null) {
			user = getBean(UsersDao.class).get(user.getUser_id());
		} else {
			user = new User();
		}
		setModelObject(user);
		target.add(this);
		target.appendJavaScript("omUserPanelInit();");
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		UsersDao usersDaoImpl = getBean(UsersDao.class);
		usersDaoImpl.delete(this.getModelObject(), getUserId());
		this.setModelObject(usersDaoImpl.getNewUserInstance(usersDaoImpl.get(getUserId())));
		target.add(listContainer);
		target.add(this);
		target.appendJavaScript("omUserPanelInit();");
	}

	/**
	 * Add the fields to the form
	 */
	private void addFormFields() {
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		login = new RequiredTextField<String>("login");
		login.setLabel(Model.of(WebSession.getString(132)));
		add(login.add(minimumLength(getMinLoginLength(cfgDao))));

		add(generalForm = new GeneralUserForm("general", getModel(), true));

		add(forDatePattern("starttime", WEB_DATE_PATTERN));
		add(forDatePattern("updatetime", WEB_DATE_PATTERN));

		add(new CheckBox("forceTimeZoneCheck"));

		final String field159 = WebSession.getString(159);
		final String field160 = WebSession.getString(160);

		add(new DropDownChoice<Integer>("status", Arrays.asList(0, 1), new IChoiceRenderer<Integer>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Integer id) {
				if (id.equals(0)) {
					return field159;
				} else if (id.equals(1)) {
					return field160;
				}
				return null;
			}

			public String getIdValue(Integer id, int index) {
				return "" + id;
			}

		}));

		final String field166 = WebSession.getString(166);
		final String field167 = WebSession.getString(167);
		final String field168 = WebSession.getString(168);
		final String field1311 = WebSession.getString(1311);

		add(new DropDownChoice<Long>("level_id", Arrays.asList(1L, 2L, 3L, 4L), new IChoiceRenderer<Long>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Long id) {
				if (id.equals(1L)) {
					return field166;
				} else if (id.equals(2L)) {
					return field167;
				} else if (id.equals(3L)) {
					return field168;
				} else if (id.equals(4L)) {
					return field1311;
				}
				return null;
			}

			public String getIdValue(Long id, int index) {
				return "" + id;
			}

		}));
		add(new ComunityUserForm("comunity", getModel()));
	}

	@Override
	protected void onValidate() {
		if(!getBean(UsersDao.class).checkUserLogin(login.getConvertedInput(), getModelObject().getUser_id())) {
			error(WebSession.getString(105));
		}
	}

	@Override
	protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onNewError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}
}
