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

import static org.apache.openmeetings.db.dao.user.UserDao.getNewUserInstance;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableGroupAdminDataProvider;
import org.apache.openmeetings.web.room.IconTextModal;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.utilities.ColorBehavior;
import jakarta.inject.Inject;

@AuthorizeInstantiation({"ADMIN", "GROUP_ADMIN"})
public class UsersPanel extends AdminBasePanel {
	private static final long serialVersionUID = 1L;
	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	private final PasswordDialog adminPass = new PasswordDialog("adminPass");
	private UserForm form;

	@Inject
	private UserDao userDao;

	public UsersPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		final SearchableDataView<User> dataView = new SearchableDataView<>("userList", new SearchableGroupAdminDataProvider<>(UserDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<User> item) {
				User u = item.getModelObject();
				final Long userId = u.getId();
				item.add(new Label("id"));
				item.add(new Label("login"));
				item.add(new Label("firstname"));
				item.add(new Label("lastname"));
				item.add(AjaxEventBehavior.onEvent(EVT_CLICK, target -> {
					form.setModelObject(userDao.get(userId));
					form.setNewRecordVisible(false);
					form.update(target);
				}));
				StringBuilder cl = getRowClass(u.getId(), form.getModelObject().getId());
				if (u.isDeleted()) {
					cl.append(" deleted");
				}
				item.add(AttributeModifier.append(ATTR_CLASS, cl));
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
		DataViewContainer<User> container = new DataViewContainer<>(listContainer, dataView, navigator);
		container.addLink(new OmOrderByBorder<>("orderById", "id", container))
			.addLink(new OmOrderByBorder<>("orderByLogin", "login", container))
			.addLink(new OmOrderByBorder<>("orderByFirstName", "firstname", container))
			.addLink(new OmOrderByBorder<>("orderByLastName", "lastname", container));
		add(container.getLinks());
		add(navigator, adminPass);
		final Modal<String> warning = new IconTextModal("warning")
				.withLabel(new ResourceModel("warn.nogroup"))
				.withErrorIcon(ColorBehavior.Color.Warning)
				.header(new ResourceModel("797"))
				.addButton(OmModalCloseButton.of("54"));

		form = new UserForm("form", listContainer, getNewUserInstance(userDao.get(getUserId())), adminPass, warning);
		add(form, warning);
		super.onInitialize();
	}
}
