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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.db.dao.user.AdminUserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

public class UsersPanel extends AdminPanel {
	private static final long serialVersionUID = -4463107742579790120L;
	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	private final MessageDialog warning = new MessageDialog("warning", WebSession.getString(797), WebSession.getString(343), DialogButtons.OK, DialogIcon.WARN) {
		private static final long serialVersionUID = 1L;

		public void onClose(AjaxRequestTarget target, DialogButton button) {
		}
	};

	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.appendJavaScript("omUserPanelInit();");
	}

	private UserForm form;

	@SuppressWarnings("unchecked")
	public UsersPanel(String id) {
		super(id);

		final SearchableDataView<User> dataView = new SearchableDataView<User>("userList"
				, new SearchableDataProvider<User>(AdminUserDao.class)) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<User> item) {
				final User u = item.getModelObject();
				item.add(new Label("userId", "" + u.getUser_id()));
				item.add(new Label("login", u.getLogin()));
				item.add(new Label("firstName", u.getFirstname()));
				item.add(new Label("lastName", u.getLastname()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(u);
						form.hideNewRecord();
						target.add(form, listContainer);
						//re-initialize height
						target.appendJavaScript("omUserPanelInit();");
					}
				});
				item.add(AttributeModifier.append("class", "clickable "
						+ (item.getIndex() % 2 == 1 ? "even" : "odd")
						+ (u.getUser_id().equals(form.getModelObject().getUser_id()) ? " selected" : "")));
			}
		};
		add(listContainer.add(dataView).setOutputMarkupId(true));
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		};
		DataViewContainer<User> container = new DataViewContainer<User>(listContainer, dataView, navigator);
		container.setLinks(new OmOrderByBorder<User>("orderById", "user_id", container)
				, new OmOrderByBorder<User>("orderByLogin", "login", container)
				, new OmOrderByBorder<User>("orderByFirstName", "firstname", container)
				, new OmOrderByBorder<User>("orderByLastName", "lastname", container));
		add(container.orderLinks);
		add(navigator);

		AdminUserDao usersDaoImpl = getBean(AdminUserDao.class);
		form = new UserForm("form", listContainer, usersDaoImpl.getNewUserInstance(usersDaoImpl.get(getUserId())), warning);
		form.showNewRecord();
		add(form, warning);
	}
}
