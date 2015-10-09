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

import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
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

/**
 * Modify/ CRUD operations for {@link Group} and
 * {@link GroupUser}
 * 
 * @author swagner
 * 
 */
public class GroupsPanel extends AdminPanel {
	private static final long serialVersionUID = 1L;
	private GroupForm form;
	
	@Override
	public void onMenuPanelLoad(IPartialPageRequestHandler handler) {
		super.onMenuPanelLoad(handler);
		handler.appendJavaScript("groupsInit();");
	}

	public GroupsPanel(String id) {
		super(id);
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        
		//Adding the Group Form
		form = new GroupForm("form", listContainer, new Group());
        add(form);

        //List view
        SearchableDataView<Group> dataView = new SearchableDataView<Group>("groupList", new SearchableDataProvider<Group>(GroupDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Group> item) {
				final Group o = item.getModelObject();
				item.add(new Label("id", "" + o.getId()));
				item.add(new Label("name", "" + o.getName()));
				item.add(new AjaxEventBehavior("click") {
					private static final long serialVersionUID = 1L;

					protected void onEvent(AjaxRequestTarget target) {
						form.hideNewRecord();
						form.setModelObject(o);
						form.updateView(target);
						target.add(listContainer);
						target.appendJavaScript("groupsInit();");
					}
				});
				item.add(AttributeModifier.append("class", "clickable ui-widget-content"
						+ (o.getId().equals(form.getModelObject().getId()) ? " ui-state-active" : "")));
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
		DataViewContainer<Group> container = new DataViewContainer<Group>(listContainer, dataView, navigator);
		container.addLink(new OmOrderByBorder<Group>("orderById", "id", container))
			.addLink(new OmOrderByBorder<Group>("orderByName", "name", container));
		add(container.getLinks());
		add(navigator);
	}
}
