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
package org.apache.openmeetings.web.components.admin.user;

import java.util.Iterator;

import org.apache.openmeetings.data.user.dao.UsersDaoImpl;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class UsersPanel extends AdminPanel {
	private static final long serialVersionUID = -4463107742579790120L;
	@SuppressWarnings("unused")
	private String selectedText = "Click on the table to change the user";
	private Label selected = null;
	private UserForm form = null;
	
	public UsersPanel(String id) {
		super(id);
		DataView<Users> dataView = new DataView<Users>("userList", new IDataProvider<Users>(){
			private static final long serialVersionUID = -6822789354860988626L;

			public void detach() {
				//empty
			}

			public Iterator<? extends Users> iterator(long first, long count) {
				return Application.getBean(UsersDaoImpl.class).getNondeletedUsers((int)first, (int)count).iterator();
			}

			public long size() {
				return Application.getBean(UsersDaoImpl.class).selectMaxFromUsers();
			}

			public IModel<Users> model(Users object) {
				return new CompoundPropertyModel<Users>(object);
			}
			
		}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<Users> item) {
				final Users u = item.getModelObject();
				item.add(new Label("userId", "" + u.getUser_id()));
				item.add(new Label("login", u.getLogin()));
				final String fName = u.getFirstname();
				item.add(new Label("firstName", fName));
				final String lName = u.getLastname();
				item.add(new Label("lastName", lName));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(u);
						target.add(form);
					}
				});
			}
		};
		selected = new Label("selected", new PropertyModel<String>(this, "selectedText"));
		selected.setOutputMarkupId(true);
		add(selected);
		dataView.setItemsPerPage(8); //FIXME need to be parametrized
		add(dataView);
		add(new AjaxPagingNavigator("navigator", dataView));
		
		Users user = new Users();
		form = new UserForm("form", user);
        add(form);
	}
}
