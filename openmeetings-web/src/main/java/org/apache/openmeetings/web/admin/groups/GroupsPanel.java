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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;

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
@AuthorizeInstantiation({"Admin", "GroupAdmin"})
public class GroupsPanel extends AdminBasePanel {
	private static final long serialVersionUID = 1L;
	private GroupForm form;

	public GroupsPanel(String id) {
		super(id);
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");

		//Adding the Group Form
		form = new GroupForm("form", listContainer, new Group());
		add(form);

		//List view
		SearchableDataView<Group> dataView = new SearchableDataView<Group>("groupList", new SearchableGroupAdminDataProvider<>(GroupDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Group> item) {
				final Group g = item.getModelObject();
				item.add(new Label("id"));
				item.add(new Label("name"));
				item.add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						form.setNewVisible(false);
						form.setModelObject(g);
						form.updateView(target);
						target.add(listContainer);
						reinitJs(target);
					}
				});
				item.add(AttributeModifier.append(ATTR_CLASS, getRowClass(g.getId(), form.getModelObject().getId())));
			}
		};

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
