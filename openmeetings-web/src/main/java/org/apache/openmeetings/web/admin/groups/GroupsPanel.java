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
package org.apache.openmeetings.web.admin.groups;

import static org.apache.openmeetings.db.util.AuthLevelUtil.hasGroupAdminLevel;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultGroup;

import java.util.Iterator;

import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableGroupAdminDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

/**
 * Modify/ CRUD operations for {@link Group} and
 * {@link GroupUser}
 *
 * @author swagner
 *
 */
@AuthorizeInstantiation({"ADMIN", "GROUP_ADMIN"})
public class GroupsPanel extends AdminBasePanel {
	private static final long serialVersionUID = 1L;
	private GroupForm form;

	public GroupsPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");

		//List view
		SearchableDataView<Group> dataView = new SearchableDataView<>("groupList", new SearchableGroupAdminDataProvider<>(GroupDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Group> item) {
				final Group g = item.getModelObject();
				item.add(new Label("id"));
				item.add(new WebMarkupContainer("default").setVisible(g.getId().equals(getDefaultGroup())));
				Label name = new Label("name");
				if (g.isExternal()) {
					name.add(AttributeModifier.append("class", "external"));
				}
				item.add(name);
				item.add(AjaxEventBehavior.onEvent(EVT_CLICK, target -> {
					form.setNewRecordVisible(false);
					form.setModelObject(g);
					form.updateView(target);
					target.add(listContainer);
				}));
				item.add(AttributeModifier.append(ATTR_CLASS, getRowClass(g.getId(), form.getModelObject().getId())));
			}
		};

		final boolean isGroupAdmin = hasGroupAdminLevel(getRights());
		Iterator<? extends Group> iter = dataView.getDataProvider().iterator(0, 1);
		Group g = iter.hasNext() ? iter.next() : new Group();
		//Adding the Group Form
		form = new GroupForm("form", listContainer, isGroupAdmin ? g : new Group());
		add(form);

		//Paging
		add(listContainer.add(dataView).setOutputMarkupId(true));
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		};
		DataViewContainer<Group> container = new DataViewContainer<>(listContainer, dataView, navigator);
		container.addLink(new OmOrderByBorder<>("orderById", "id", container))
			.addLink(new OmOrderByBorder<>("orderByName", "name", container));
		add(container.getLinks());
		add(navigator);
	}
}
