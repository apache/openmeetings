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

import static org.apache.openmeetings.db.util.AuthLevelUtil.hasGroupAdminLevel;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isDisplayNameEditable;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Salutation;
import org.apache.openmeetings.web.common.datetime.AjaxOmDatePicker;
import org.apache.openmeetings.web.util.CountryDropDown;
import org.apache.openmeetings.web.util.RestrictiveChoiceProvider;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.LambdaChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2MultiChoice;

import jakarta.inject.Inject;

public class GeneralUserForm extends Form<User> {
	private static final long serialVersionUID = 1L;
	private final RequiredTextField<String> email = new RequiredTextField<>("address.email");
	private final List<GroupUser> grpUsers = new ArrayList<>();
	private final AjaxOmDatePicker bday = new AjaxOmDatePicker("age");
	private final boolean isAdminForm;

	@Inject
	private GroupDao groupDao;
	@Inject
	private UserDao userDao;

	public GeneralUserForm(String id, IModel<User> model, boolean isAdminForm) {
		super(id, model);
		this.isAdminForm = isAdminForm;
		updateModelObject(getModelObject(), isAdminForm);
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(email);
		email.setLabel(new ResourceModel("119"));
		email.add(RfcCompliantEmailAddressValidator.getInstance());
		add(new DropDownChoice<>("salutation"
				, List.of(Salutation.values())
				, new LambdaChoiceRenderer<>(s -> getString("user.salutation." + s.name()), Salutation::name)));
		add(new TextField<String>("firstname"));
		add(new TextField<String>("lastname"));
		add(new TextField<String>("displayName").setEnabled(isAdminForm || isDisplayNameEditable()));
		add(new DropDownChoice<>("timeZoneId", AVAILABLE_TIMEZONES));
		add(new LanguageDropDown("languageId"));
		add(new TextField<String>("address.phone"));
		add(bday);
		add(new TextField<String>("address.street"));
		add(new TextField<String>("address.additionalname"));
		add(new TextField<String>("address.zip"));
		add(new TextField<String>("address.town"));
		add(new CountryDropDown("address.country"));
		add(new TextArea<String>("address.comment"));
		add(new Select2MultiChoice<>("groupUsers", null, new RestrictiveChoiceProvider<GroupUser>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(GroupUser choice) {
				return choice.getGroup().getName();
			}

			@Override
			public String toId(GroupUser choice) {
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
			public GroupUser fromId(String inId) {
				Long id = Long.parseLong(inId);
				User u = GeneralUserForm.this.getModelObject();
				Group g = groupDao.get(id);
				GroupUser gu = new GroupUser(g, u);
				int idx = grpUsers.indexOf(gu);
				return idx < 0 ? gu : grpUsers.get(idx);
			}
		}).setLabel(new ResourceModel("161")).setRequired(isAdminForm && hasGroupAdminLevel(getRights())).setEnabled(isAdminForm));
	}

	public void updateModelObject(User u, boolean isAdminForm) {
		grpUsers.clear();
		grpUsers.addAll(u.getGroupUsers());
		if (isAdminForm) {
			List<Group> grpList = hasGroupAdminLevel(getRights())
					? groupDao.adminGet(null, getUserId(), 0, Integer.MAX_VALUE, null)
					: groupDao.get(0, Integer.MAX_VALUE);
			for (Group g : grpList) {
				GroupUser gu = new GroupUser(g, u);
				int idx = grpUsers.indexOf(gu);
				if (idx < 0) {
					grpUsers.add(gu);
				}
			}
		}
	}

	@Override
	protected void onValidate() {
		User u = getModelObject();
		if (!userDao.checkEmail(email.getConvertedInput(), u.getType(), u.getDomainId(), u.getId())) {
			error(getString("error.email.inuse"));
		}
		super.onValidate();
	}

	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(false);
	}
}
