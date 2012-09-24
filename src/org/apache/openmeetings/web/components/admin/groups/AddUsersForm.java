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

import org.apache.openmeetings.data.user.dao.UsersDaoImpl;
import org.apache.openmeetings.persistence.beans.user.Users;
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
	private String userSearchText;
	private List<Users> usersToAdd = new ArrayList<Users>();
	
	public AddUsersForm(String id) {
		super(id);

		IModel<List<Users>> usersModel = new PropertyModel<List<Users>>(AddUsersForm.this, "usersToAdd");
		final ListMultipleChoice<Users> users = new ListMultipleChoice<Users>("users"
				, usersModel
				, usersModel
				, new IChoiceRenderer<Users>() {
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Users object) {
				return getUser(object);
			}

			public String getIdValue(Users object, int index) {
				return "" + object.getUser_id();
			}
		});
		
		add(new TextField<String>("searchText", new PropertyModel<String>(AddUsersForm.this, "userSearchText")));
		add(new AjaxButton("search", Model.of(WebSession.getString(182L))) {
			private static final long serialVersionUID = -4752180617634945030L;

			protected void onAfterSubmit(AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
				usersToAdd.clear();
				usersToAdd.addAll(Application.getBean(UsersDaoImpl.class).get(userSearchText));
				target.add(users);
			}
		});
		add(users.setOutputMarkupId(true));
		add(new AjaxButton("add", Model.of(WebSession.getString(175L))) {
			private static final long serialVersionUID = 5553555064487161840L;

			protected void onAfterSubmit(AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
				for (Users u : usersToAdd) {
					//add them
				}
			}
		});
	}

}
