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
package org.apache.openmeetings.web.components;

import static org.apache.openmeetings.utils.UserHelper.getMinPasswdLength;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.basic.FieldLanguageDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.user.EmailManager;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.data.user.dao.SalutationDao;
import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.user.Salutation;
import org.apache.openmeetings.persistence.beans.user.State;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;

public class GeneralUserForm extends Form<User> {
	private static final long serialVersionUID = 5360667099083510234L;
	private Salutation salutation;
	private FieldLanguage lang;
	private PasswordTextField passwordField;
	private RequiredTextField<String> email;

	public GeneralUserForm(String id, IModel<User> model, boolean isAdminForm) {
		super(id, model);

		//TODO should throw exception if non admin User edit somebody else (or make all fields read-only)
		add(passwordField = new PasswordTextField("password", new Model<String>()));
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		passwordField.setRequired(false).add(StringValidator.minimumLength(getMinPasswdLength(cfgDao)));

		SalutationDao salutDao = getBean(SalutationDao.class);
		FieldLanguageDao langDao = getBean(FieldLanguageDao.class);
		salutation = salutDao.get(getModelObject().getSalutations_id(), getLanguage());
		lang = langDao.getFieldLanguageById(getModelObject().getLanguage_id());
		add(new DropDownChoice<Salutation>("salutation"
				, new PropertyModel<Salutation>(this, "salutation")
				, salutDao.getUserSalutations(getLanguage())
				, new ChoiceRenderer<Salutation>("label.value", "salutations_id"))
			.add(new AjaxFormComponentUpdatingBehavior("onchange") {
				private static final long serialVersionUID = -6748844721645465468L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					GeneralUserForm.this.getModelObject().setSalutations_id(salutation.getSalutations_id());
				}
			}));
		add(new TextField<String>("firstname"));
		add(new TextField<String>("lastname"));
		
		add(new DropDownChoice<OmTimeZone>("omTimeZone", getBean(OmTimeZoneDao.class).getOmTimeZones(),
				new ChoiceRenderer<OmTimeZone>("frontEndLabel", "jname")));

		add(new DropDownChoice<FieldLanguage>("language"
				, new PropertyModel<FieldLanguage>(this, "lang")
				, langDao.getLanguages()
				, new ChoiceRenderer<FieldLanguage>("name", "language_id"))
			.add(new AjaxFormComponentUpdatingBehavior("onchange") {
				private static final long serialVersionUID = 2072021284702632856L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					GeneralUserForm.this.getModelObject().setLanguage_id(lang.getLanguage_id());
				}
			}));

		add(email = new RequiredTextField<String>("adresses.email"));
		email.add(RfcCompliantEmailAddressValidator.getInstance());
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
		add(new DropDownChoice<State>("adresses.states", getBean(StateDao.class).getStates()
				, new ChoiceRenderer<State>("name", "state_id")));
		add(new TextArea<String>("adresses.comment"));

		List<Organisation_Users> orgUsers;
		if (isAdminForm) {
			List<Organisation> orgList = getBean(OrganisationManager.class).getOrganisations(3L);
			orgUsers = new ArrayList<Organisation_Users>(orgList.size());
			for (Organisation org : orgList) {
				orgUsers.add(new Organisation_Users(org));
			}
		} else {
			orgUsers = getModelObject().getOrganisation_users();
		}
		ListMultipleChoice<Organisation_Users> orgChoiceList = new ListMultipleChoice<Organisation_Users>(
				"organisation_users", orgUsers,
				new ChoiceRenderer<Organisation_Users>("organisation.name", "organisation.organisation_id"));
		add(orgChoiceList.setEnabled(isAdminForm));
	}

	@Override
	protected void onValidate() {
		if(!getBean(EmailManager.class).checkUserEMail(email.getConvertedInput())) {
			error(WebSession.getString(1000));
		}
		super.onValidate();
	}
	
	public PasswordTextField getPasswordField() {
		return passwordField;
	}
	
	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(false);
	}
}
