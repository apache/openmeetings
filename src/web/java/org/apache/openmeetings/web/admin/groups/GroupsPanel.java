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

import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.web.admin.AddUsersDialog;
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
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
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
	private AddUsersDialog addUsersDialog;
	private GroupForm form;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		super.onMenuPanelLoad(target);
		target.appendJavaScript("groupsInit();");
	}

	@SuppressWarnings("unchecked")
	public GroupsPanel(String id) {
		super(id);
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        final AjaxButton addUsersBtn = new AjaxButton("addUsersBtn") {
			private static final long serialVersionUID = 1L;
        	@Override
        	protected void onSubmit(AjaxRequestTarget target, Form<?> form1) {
        		if (form.getModelObject().getOrganisation_id() != null) {
        			addUsersDialog.open(target);
        		}
        	}
        };
        
		//Adding the Group Form
		form = new GroupForm("form", listContainer, new Organisation()){
			private static final long serialVersionUID = 1L;

			private AjaxButton processAddClass() {
				boolean orgSaved = getModelObject().getOrganisation_id() != null;
				addUsersBtn.add(AttributeModifier.replace("class", orgSaved ? "formNewButton" : "formNewButton disabled"));
				addUsersBtn.setEnabled(orgSaved);
				return addUsersBtn;
			}
			
			@Override
			protected void onModelChanged() {
				super.onModelChanged();
				processAddClass();
			}
			
			@Override
			public void updateView(AjaxRequestTarget target) {
				super.updateView(target);
				target.add(processAddClass());
			}
		};
        add(form.add(addUsersBtn.setOutputMarkupId(true)));
        addUsersDialog = new AddUsersDialog("addUsers",WebSession.getString(180), form, true);
		add(addUsersDialog.setOutputMarkupId(true));

        //List view
        SearchableDataView<Organisation> dataView = new SearchableDataView<Organisation>("groupList", new SearchableDataProvider<Organisation>(OrganisationDao.class)) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<Organisation> item) {
				final Organisation o = item.getModelObject();
				item.add(new Label("organisation_id", "" + o.getOrganisation_id()));
				item.add(new Label("name", "" + o.getName()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.hideNewRecord();
						form.setModelObject(o);
						form.updateView(target);
						target.add(listContainer);
						target.appendJavaScript("groupsInit();");
					}
				});
				item.add(AttributeModifier.append("class", "clickable ui-widget-content"
						+ (o.getOrganisation_id().equals(form.getModelObject().getOrganisation_id()) ? " selected" : "")));
			}
		};

		//Paging
		add(listContainer.add(dataView).setOutputMarkupId(true));
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		};
		DataViewContainer<Organisation> container = new DataViewContainer<Organisation>(listContainer, dataView, navigator);
		container.setLinks(new OmOrderByBorder<Organisation>("orderById", "organisation_id", container)
				, new OmOrderByBorder<Organisation>("orderByName", "name", container));
		add(container.orderLinks);
		add(navigator);
	}
}
