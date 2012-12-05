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

import org.apache.openmeetings.data.user.dao.OrganisationDao;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
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

/**
 * Modify/ CRUD operations for {@link Organisation} and
 * {@link Organisation_Users}
 * 
 * @author swag
 * 
 */
public class GroupsPanel extends AdminPanel {
	private static final long serialVersionUID = -5170400556006464830L;
	private AddUsersForm addUsersForm;
	private GroupForm form;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.appendJavaScript("groupsInit();");
	}

	@SuppressWarnings("unchecked")
	public GroupsPanel(String id) {
		super(id);
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        final WebMarkupContainer addUsersBtn = new WebMarkupContainer("addUsersBtn");
		
		//Adding the Group Form
		form = new GroupForm("form", listContainer, new Organisation()){
			private static final long serialVersionUID = -3042797340375988889L;

			@Override
			protected void onModelChanged() {
				super.onModelChanged();
				if (getModelObject().getOrganisation_id() == null) {
					addUsersBtn.add(AttributeModifier.replace("class", "formNewButton disabled")
							, AttributeModifier.replace("onclick", ""));
				} else {
					addUsersBtn.add(AttributeModifier.replace("class", "formNewButton")
							, AttributeModifier.replace("onclick", "addUsers();"));
				}
				addUsersForm.setOrganisation(getModelObject());
			}
			
			@Override
			void updateView(AjaxRequestTarget target) {
				super.updateView(target);
				target.add(addUsersBtn);
			}
		};
        add(form.add(addUsersBtn.setOutputMarkupId(true)));
        addUsersForm = new AddUsersForm("addUsers", form);
		add(addUsersForm);

        //List view
        SearchableDataView<Organisation> dataView = new SearchableDataView<Organisation>("groupList", new SearchableDataProvider<Organisation>(OrganisationDao.class)) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<Organisation> item) {
				final Organisation organisation = item.getModelObject();
				item.add(new Label("organisation_id", "" + organisation.getOrganisation_id()));
				item.add(new Label("name", "" + organisation.getName()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.hideNewRecord();
						form.setModelObject(organisation);
						form.updateView(target);
						target.appendJavaScript("groupsInit();");
					}
				});
				item.add(AttributeModifier.append("class", "clickable " + ((item.getIndex() % 2 == 1) ? "even" : "odd")));
			}
		};

		//Paging
		add(listContainer.add(dataView).setOutputMarkupId(true));
		DataViewContainer<Organisation> container = new DataViewContainer<Organisation>(listContainer, dataView);
		container.setLinks(new OrderByBorder<Organisation>("orderById", "organisation_id", container)
				, new OrderByBorder<Organisation>("orderByName", "name", container));
		add(container.orderLinks);
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});
	}
}
