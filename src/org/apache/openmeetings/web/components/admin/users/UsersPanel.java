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

import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.components.admin.SearchableDataView;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.openmeetings.web.data.OrderByBorder;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

public class UsersPanel extends AdminPanel {

	private static final long serialVersionUID = -4463107742579790120L;

	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.appendJavaScript("omUserPanelInit();");
	}

	private UserForm form;

	@SuppressWarnings("unchecked")
	public UsersPanel(String id) {
		super(id);

		final SearchableDataView<User> dataView = new SearchableDataView<User>("userList"
				, new SearchableDataProvider<User>(UsersDao.class)) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<User> item) {
				final User u = item.getModelObject();
				item.add(new Label("userId", "" + u.getUser_id()));
				item.add(new Label("login", u.getLogin()));
				item.add(new Label("firstName", u.getFirstname()));
				item.add(new Label("lastName", u.getLastname()));
				item.add(AttributeModifier.append("class", "clickable "
						+ ((item.getIndex() % 2 == 1) ? "even" : "odd")));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(u);
						form.hideNewRecord();
						target.add(form);
						//re-initialize height
						target.appendJavaScript("omUserPanelInit();");
					}
				});
			}
		};
		final WebMarkupContainer listContainer = new WebMarkupContainer(
				"listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		DataViewContainer<User> container = new DataViewContainer<User>(listContainer, dataView);
		container.setLinks(new OrderByBorder<User>("orderById", "user_id", container)
				, new OrderByBorder<User>("orderByLogin", "login", container)
				, new OrderByBorder<User>("orderByFirstName", "firstname", container)
				, new OrderByBorder<User>("orderByLastName", "lastname", container));
		add(container.orderLinks);
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});

		UsersDao usersDaoImpl = Application.getBean(UsersDao.class);
		form = new UserForm("form", listContainer,
				usersDaoImpl.getNewUserInstance(usersDaoImpl.get(WebSession
						.getUserId())));
		form.showNewRecord();
		add(form);
	}
}
