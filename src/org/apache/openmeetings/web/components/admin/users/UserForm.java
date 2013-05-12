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
package org.apache.openmeetings.web.components.admin.users;

import static org.apache.openmeetings.utils.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Arrays;

import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.ComunityUserForm;
import org.apache.openmeetings.web.components.GeneralUserForm;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.StringValidator;

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

	public UserForm(String id, WebMarkupContainer listContainer, final User user) {
		super(id, new CompoundPropertyModel<User>(user));
		setOutputMarkupId(true);
		this.listContainer = listContainer;

		// Add form fields
		addFormFields();

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown", Duration.ONE_SECOND);

	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		User u = getModelObject();
		// TODO: Why the password field is not set via the Model is because its
		// FetchType is Lazy, this extra hook here might be not needed with a
		// different mechanism to protect the password from being read
		// sebawagner, 01.10.2012
		try {
			String pass = generalForm.getPasswordField().getConvertedInput();
			if (pass != null && !pass.isEmpty()) {
				u.updatePassword(getBean(ManageCryptStyle.class), getBean(ConfigurationDao.class), pass);
			}
			getBean(UsersDao.class).update(u, getUserId());
		} catch (Exception e) {
			// FIXME update feedback with the error details
		}
		setModelObject(u);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
		target.appendJavaScript("omUserPanelInit();");
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
		// login.setLabel(new Model<String>("testname"));
		add(login.add(StringValidator.minimumLength(getMinLoginLength(cfgDao))));

		add(generalForm = new GeneralUserForm("general", getModel(), true));

		add(DateLabel.forDatePattern("starttime", "dd.MM.yyyy HH:mm:ss"));
		add(DateLabel.forDatePattern("updatetime", "dd.MM.yyyy HH:mm:ss"));

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
		if(getBean(UsersDao.class).checkUserLogin(login.getConvertedInput())) {
			error(WebSession.getString(105));
		}
	}
}
