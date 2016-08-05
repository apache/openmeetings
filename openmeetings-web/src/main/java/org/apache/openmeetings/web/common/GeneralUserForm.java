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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.db.util.UserHelper.getMinPasswdLength;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Salutation;
import org.apache.openmeetings.util.CalendarHelper;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.CountryDropDown;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.threeten.bp.LocalDate;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2MultiChoice;

import com.googlecode.wicket.kendo.ui.form.datetime.local.AjaxDatePicker;
import com.googlecode.wicket.kendo.ui.resource.KendoCultureResourceReference;

public class GeneralUserForm extends Form<User> {
	private static final long serialVersionUID = 1L;
	private LocalDate age;
	private final PasswordTextField passwordField;
	private final RequiredTextField<String> email;
	private final List<GroupUser> grpUsers = new ArrayList<>();

	public GeneralUserForm(String id, IModel<User> model, boolean isAdminForm) {
		super(id, model);

		//TODO should throw exception if non admin User edit somebody else (or make all fields read-only)
		add(passwordField = new PasswordTextField("password", new Model<String>()));
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		passwordField.setRequired(false).add(minimumLength(getMinPasswdLength(cfgDao)));

		updateModelObject(getModelObject(), isAdminForm);
		add(new DropDownChoice<Salutation>("salutation"
				, Arrays.asList(Salutation.values())
				, new ChoiceRenderer<Salutation>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object getDisplayValue(Salutation object) {
						return getString("user.salutation." + object.name());
					}

					@Override
					public String getIdValue(Salutation object, int index) {
						return object.name();
					}
				}));
		add(new TextField<String>("firstname"));
		add(new TextField<String>("lastname"));
		
		add(new DropDownChoice<String>("timeZoneId", AVAILABLE_TIMEZONES));

		add(new LanguageDropDown("languageId"));

		add(email = new RequiredTextField<String>("address.email"));
		email.setLabel(Model.of(Application.getString(137)));
		email.add(RfcCompliantEmailAddressValidator.getInstance());
		add(new TextField<String>("address.phone"));
		add(new CheckBox("sendSMS"));
		add(new AjaxDatePicker("age", new PropertyModel<LocalDate>(this, "age"), WebSession.get().getLocale()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onValueChanged(IPartialPageRequestHandler target) {
				User u = GeneralUserForm.this.getModelObject();
				u.setAge(CalendarHelper.getDate(age, u.getTimeZoneId()));
			}
		});
		add(new TextField<String>("address.street"));
		add(new TextField<String>("address.additionalname"));
		add(new TextField<String>("address.zip"));
		add(new TextField<String>("address.town"));
		add(new CountryDropDown("address.country"));
		add(new TextArea<String>("address.comment"));

		add(new Select2MultiChoice<GroupUser>("groupUsers", null, new ChoiceProvider<GroupUser>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(GroupUser choice) {
				return choice.getGroup().getName();
			}

			@Override
			public String getIdValue(GroupUser choice) {
				Long id = choice.getGroup().getId();
				return id == null ? null : "" + id;
			}

			@Override
			public void query(String term, int page, Response<GroupUser> response) {
				for (GroupUser ou : grpUsers) {
					if (Strings.isEmpty(term) || ou.getGroup().getName().contains(term)) {
						response.add(ou);
					}
				}
			}

			@Override
			public Collection<GroupUser> toChoices(Collection<String> _ids) {
				List<Long> ids = new ArrayList<Long>();
				for (String id : _ids) {
					ids.add(Long.parseLong(id));
				}
				List<GroupUser> list = new ArrayList<GroupUser>();
				User u = GeneralUserForm.this.getModelObject();
				for (Group g : getBean(GroupDao.class).get(ids)) {
					GroupUser gu = new GroupUser(g, u);
					int idx = grpUsers.indexOf(gu);
					list.add(idx < 0 ? gu : grpUsers.get(idx));
				}
				return list;
			}
		}).setEnabled(isAdminForm));
	}

	public void updateModelObject(User u, boolean isAdminForm) {
		grpUsers.clear();
		grpUsers.addAll(u.getGroupUsers());
		if (isAdminForm) {
			List<Group> grpList = getBean(GroupDao.class).get(0, Integer.MAX_VALUE);
			for (Group g : grpList) {
				GroupUser gu = new GroupUser(g, u);
				int idx = grpUsers.indexOf(gu);
				if (idx < 0) {
					grpUsers.add(gu);
				}
			}
		}
		age = CalendarHelper.getDate(u.getAge(), u.getTimeZoneId());
	}
	
	@Override
	protected void onValidate() {
		User u = getModelObject();
		if(!getBean(UserDao.class).checkEmail(email.getConvertedInput(), u.getType(), u.getDomainId(), u.getId())) {
			error(Application.getString(1000));
		}
		super.onValidate();
	}
	
	public PasswordTextField getPasswordField() {
		return passwordField;
	}
	
	public String getEmail() {
		return email.getValue();
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(false);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new KendoCultureResourceReference(WebSession.get().getLocale())));
	}
}
