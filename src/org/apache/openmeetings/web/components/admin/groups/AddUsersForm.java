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
package org.apache.openmeetings.web.components.admin.groups;

import static org.apache.openmeetings.web.components.admin.groups.GroupUsersPanel.getUser;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class AddUsersForm extends Form<Void> {
	private static final long serialVersionUID = -2458265250684437277L;
	private Organisation organisation;
	private String userSearchText;
	private List<User> usersInList = new ArrayList<User>();
	private List<User> usersToAdd = new ArrayList<User>();
	
	public AddUsersForm(String id, final GroupForm groupForm) {
		super(id);
		
		IModel<List<User>> listUsersModel = new PropertyModel<List<User>>(AddUsersForm.this, "usersInList");
		IModel<List<User>> selectedUsersModel = new PropertyModel<List<User>>(AddUsersForm.this, "usersToAdd");
		final ListMultipleChoice<User> users = new ListMultipleChoice<User>("users"
				, selectedUsersModel
				, listUsersModel
				, new IChoiceRenderer<User>() {
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(User object) {
				return getUser(object);
			}

			public String getIdValue(User object, int index) {
				return "" + object.getUser_id();
			}
		});
		
		add(new TextField<String>("searchText", new PropertyModel<String>(AddUsersForm.this, "userSearchText")));
		add(new AjaxButton("search", Model.of(WebSession.getString(182L))) {
			private static final long serialVersionUID = -4752180617634945030L;

			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
				clear();
				usersInList.addAll(Application.getBean(UsersDao.class).get(userSearchText));
				target.add(users);
			}
		});
		add(users.setOutputMarkupId(true));
		add(new AjaxButton("add", Model.of(WebSession.getString(175L))) {
			private static final long serialVersionUID = 5553555064487161840L;

			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
				UsersDao userDao = Application.getBean(UsersDao.class);
				for (User u : usersToAdd) {
					List<Organisation_Users> orgUsers = u.getOrganisation_users();
					boolean found = false;
					for (Organisation_Users ou : orgUsers) {
						if (ou.getOrganisation().getOrganisation_id().equals(organisation.getOrganisation_id())) {
							found = true;
							break;
						}
					}
					if (!found) {
						Organisation_Users orgUser = new Organisation_Users(organisation);
						orgUser.setDeleted(false);
						orgUsers.add(orgUser);
						userDao.update(u, WebSession.getUserId());
					}
				}
				target.appendJavaScript("$('#addUsers').dialog('close');");
				groupForm.updateView(target);
			}
		});
	}

	public void clear() {
		usersToAdd.clear();
		usersInList.clear();
	}
	
	public void setOrganisation(Organisation org) {
		organisation = org;
	}
}
