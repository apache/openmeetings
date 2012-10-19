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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.data.basic.FieldLanguageDaoImpl;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDaoImpl;
import org.apache.openmeetings.data.basic.dao.ServerDaoImpl;
import org.apache.openmeetings.data.user.Organisationmanagement;
import org.apache.openmeetings.data.user.dao.SalutationDaoImpl;
import org.apache.openmeetings.data.user.dao.StateDaoImpl;
import org.apache.openmeetings.data.user.dao.UsersDaoImpl;
import org.apache.openmeetings.persistence.beans.adresses.States;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.user.Salutations;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * CRUD operations in form for {@link Users}
 * 
 * @author swagner
 * 
 */
public class UserForm extends AdminBaseForm<Users> {

	private static final long serialVersionUID = 1L;

	private WebMarkupContainer listContainer;

	private Users user;

	private final List<Salutations> saluationList = Application.getBean(
			SalutationDaoImpl.class).getUserSalutations(
			WebSession.getLanguage());
	private final List<FieldLanguage> languageList = Application.getBean(
			FieldLanguageDaoImpl.class).getLanguages();

	private PasswordTextField passwordField;

	/**
	 * Get id list of {@link Salutations}
	 * 
	 * @return
	 */
	private List<Long> getSalutationsIds() {
		ArrayList<Long> saluationIdList = new ArrayList<Long>(
				saluationList.size());
		for (Salutations saluation : saluationList) {
			saluationIdList.add(saluation.getSalutations_id());
		}
		return saluationIdList;
	}

	/**
	 * Get a name for a given id of {@link Salutations}
	 * 
	 * @param id
	 * @return
	 */
	private String getSaluationLabelById(Long id) {
		for (Salutations saluation : saluationList) {
			if (id.equals(saluation.getSalutations_id())) {
				return saluation.getLabel().getValue();
			}
		}
		throw new RuntimeException("Could not find Salutations for id " + id);
	}

	/**
	 * Id list of {@link FieldLanguage}
	 * 
	 * @return
	 */
	private List<Long> getFieldLanguageIds() {
		ArrayList<Long> languageIdList = new ArrayList<Long>(
				languageList.size());
		for (FieldLanguage language : languageList) {
			languageIdList.add(language.getLanguage_id());
		}
		return languageIdList;
	}

	/**
	 * Get name of {@link FieldLanguage} by its id
	 * 
	 * @param id
	 * @return
	 */
	private String getFieldLanguageLabelById(Long id) {
		for (FieldLanguage language : languageList) {
			if (id.equals(language.getLanguage_id())) {
				return language.getName();
			}
		}
		throw new RuntimeException("Could not find FieldLanguage for id " + id);
	}

	public UserForm(String id, WebMarkupContainer listContainer,
			final Users user) {
		super(id, new CompoundPropertyModel<Users>(user));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
		this.user = user;

		// Add form fields
		addFormFields();

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown",
				Duration.ONE_SECOND);

	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(UsersDaoImpl.class).update(getModelObject(),
				WebSession.getUserId());
		Users userStored = Application.getBean(UsersDaoImpl.class).get(
				getModelObject().getUser_id());
		// TODO: Why the password field is not set via the Model is because its
		// FetchType is Lazy, this extra hook here might be not needed with a
		// different mechanism to protect the password from being read
		// sebawagner, 01.10.2012
		if (passwordField.getConvertedInput() != null
				&& !passwordField.getConvertedInput().isEmpty()) {
			Application.getBean(UsersDaoImpl.class).updatePassword(userStored,
					passwordField.getConvertedInput());
		}
		setModelObject(userStored);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
		target.appendJavaScript("omUserPanelInit();");
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		UsersDaoImpl usersDaoImpl = Application.getBean(UsersDaoImpl.class);
		setModelObject(usersDaoImpl.getNewUserInstance(usersDaoImpl
				.get(WebSession.getUserId())));
		target.add(this);
		target.appendJavaScript("omUserPanelInit();");
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Users user = getModelObject();
		if (user.getUser_id() <= 0) {
			user = Application.getBean(UsersDaoImpl.class).get(
					user.getUser_id());
		} else {
			user = new Users();
		}
		setModelObject(user);
		target.add(this);
		target.appendJavaScript("omUserPanelInit();");
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		UsersDaoImpl usersDaoImpl = Application.getBean(UsersDaoImpl.class);
		usersDaoImpl.delete(this.getModelObject(),
				WebSession.getUserId());
		this.setModelObject(usersDaoImpl.getNewUserInstance(usersDaoImpl
				.get(WebSession.getUserId())));
		target.add(listContainer);
		target.add(this);
		target.appendJavaScript("omUserPanelInit();");
	}

	/**
	 * Add the fields to the form
	 */
	private void addFormFields() {

		RequiredTextField<String> login = new RequiredTextField<String>("login");
		login.add(new StringValidator(4, null));
		// login.setLabel(new Model<String>("testname"));
		add(login);

		passwordField = new PasswordTextField("password");
		add(passwordField);
		passwordField.setRequired(false);

		add(new DropDownChoice<Long>("salutations_id", getSalutationsIds(),
				new IChoiceRenderer<Long>() {
					private static final long serialVersionUID = 1L;

					public Object getDisplayValue(Long id) {
						return getSaluationLabelById(id);
					}

					public String getIdValue(Long id, int index) {
						return "" + id;
					}

				}));

		add(new TextField<String>("firstname"));
		add(new TextField<String>("lastname"));

		add(new DropDownChoice<OmTimeZone>("omTimeZone", Application.getBean(
				OmTimeZoneDaoImpl.class).getOmTimeZones(),
				new ChoiceRenderer<OmTimeZone>("frontEndLabel", "jname")));

		add(new DropDownChoice<Long>("language_id", getFieldLanguageIds(),
				new IChoiceRenderer<Long>() {
					private static final long serialVersionUID = 1L;

					public Object getDisplayValue(Long id) {
						return getFieldLanguageLabelById(id);
					}

					public String getIdValue(Long id, int index) {
						return "" + id;
					}

				}));

		add(DateLabel.forDatePattern("starttime", "dd.MM.yyyy HH:mm:ss"));
		add(DateLabel.forDatePattern("updatetime", "dd.MM.yyyy HH:mm:ss"));

		add(new CheckBox("forceTimeZoneCheck"));
		RequiredTextField<String> email = new RequiredTextField<String>(
				"adresses.email");
		// email.setLabel(new Model<String>("testemail"));
		email.add(EmailAddressValidator.getInstance());
		add(email);
		add(new TextField<String>("adresses.phone"));
		add(new CheckBox("sendSMS"));
		DateTextField age = new DateTextField("age");
		DatePicker datePicker = new DatePicker() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getAdditionalJavaScript() {
				return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
			}
		};
		datePicker.setShowOnFieldClick(true);
		datePicker.setAutoHide(true);
		age.add(datePicker);
		add(age);
		add(new TextField<String>("adresses.street"));
		add(new TextField<String>("adresses.additionalname"));
		add(new TextField<String>("adresses.zip"));
		add(new TextField<String>("adresses.town"));
		add(new DropDownChoice<States>("adresses.states", Application.getBean(
				StateDaoImpl.class).getStates(), new ChoiceRenderer<States>(
				"name", "state_id")));

		final String field159 = WebSession.getString(159);
		final String field160 = WebSession.getString(160);

		add(new DropDownChoice<Integer>("status", Arrays.asList(0, 1),
				new IChoiceRenderer<Integer>() {

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

		add(new DropDownChoice<Long>("level_id", Arrays.asList(1L, 2L, 3L, 4L),
				new IChoiceRenderer<Long>() {

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

		add(new TextArea<String>("adresses.comment"));

		List<Organisation> orgList = Application.getBean(
				Organisationmanagement.class).getOrganisations(3L);
		List<Organisation_Users> orgUsers = new ArrayList<Organisation_Users>(
				orgList.size());
		for (Organisation org : orgList) {
			orgUsers.add(new Organisation_Users(org));
		}
		ListMultipleChoice<Organisation_Users> orgChoiceList = new ListMultipleChoice<Organisation_Users>(
				"organisation_users", orgUsers,
				new ChoiceRenderer<Organisation_Users>("organisation.name",
						"organisation.organisation_id"));
		add(orgChoiceList);

		add(new DropDownChoice<Server>("server", Application.getBean(
				ServerDaoImpl.class).getServerList(),
				new ChoiceRenderer<Server>("name", "id")));

		final String field1160 = WebSession.getString(1160); // 1160 everybody
		final String field1168 = WebSession.getString(1168); // 1168 contact
		final String field1169 = WebSession.getString(1169); // 1169 nobody

		add(new RadioChoice<Long>("community_settings", new IModel<Long>() {
			private static final long serialVersionUID = 1L;

			public Long getObject() {
				if (user.getShowContactData() != null
						&& user.getShowContactData()) {
					return 1L;
				} else if (user.getShowContactDataToContacts() != null
						&& user.getShowContactDataToContacts()) {
					return 2L;
				}
				return 3L;
			}

			public void setObject(Long choice) {
				if (choice.equals(1L)) {
					user.setShowContactData(true);
					user.setShowContactDataToContacts(false);
				} else if (choice.equals(2L)) {
					user.setShowContactData(false);
					user.setShowContactDataToContacts(true);
				} else {
					user.setShowContactData(false);
					user.setShowContactDataToContacts(false);
				}
			}

			public void detach() {
			}
		}, Arrays.asList(1L, 2L, 3L), new IChoiceRenderer<Long>() {
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Long id) {
				if (id.equals(1L)) {
					return field1160;
				} else if (id.equals(2L)) {
					return field1168;
				} else {
					return field1169;
				}
			}

			public String getIdValue(Long id, int index) {
				return "" + id;
			}

		}));

		add(new TextArea<String>("userOffers"));
		add(new TextArea<String>("userSearchs"));
	}
	
}
