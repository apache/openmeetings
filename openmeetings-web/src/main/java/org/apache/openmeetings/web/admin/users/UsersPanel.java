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

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

public class UsersPanel extends AdminPanel {
	private static final long serialVersionUID = 1L;
	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	private final MessageDialog warning = new MessageDialog("warning", Application.getString(797), Application.getString(343), DialogButtons.OK, DialogIcon.WARN) {
		private static final long serialVersionUID = 1L;

		@Override
		public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		}
	};

	@Override
	public BasePanel onMenuPanelLoad(IPartialPageRequestHandler handler) {
		super.onMenuPanelLoad(handler);
		handler.appendJavaScript("omUserPanelInit();");
		return this;
	}

	private UserForm form;

	public UsersPanel(String id) {
		super(id);

		final SearchableDataView<User> dataView = new SearchableDataView<User>("userList", new SearchableDataProvider<User>(UserDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<User> item) {
				User u = item.getModelObject();
				final Long userId = u.getId();
				item.add(new Label("id"));
				item.add(new Label("login"));
				item.add(new Label("firstname"));
				item.add(new Label("lastname"));
				item.add(new AjaxEventBehavior("click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(getBean(UserDao.class).get(userId));
						form.hideNewRecord();
						form.update(target);
					}
				});
				item.add(AttributeModifier.append("class", getRowClass(u.getId(), form.getModelObject().getId())));
			}
		};
		add(listContainer.add(dataView).setOutputMarkupId(true));
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		};
		DataViewContainer<User> container = new DataViewContainer<User>(listContainer, dataView, navigator);
		container.addLink(new OmOrderByBorder<User>("orderById", "id", container))
			.addLink(new OmOrderByBorder<User>("orderByLogin", "login", container))
			.addLink(new OmOrderByBorder<User>("orderByFirstName", "firstname", container))
			.addLink(new OmOrderByBorder<User>("orderByLastName", "lastname", container));
		add(container.getLinks());
		add(navigator);

		UserDao userDao = getBean(UserDao.class);
		form = new UserForm("form", listContainer, userDao.getNewUserInstance(userDao.get(getUserId())), warning);
		form.showNewRecord();
		add(form, warning);
	}
}
