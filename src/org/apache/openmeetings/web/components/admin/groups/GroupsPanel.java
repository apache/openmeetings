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

import java.util.Iterator;

import org.apache.openmeetings.data.user.Organisationmanagement;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class GroupsPanel extends AdminPanel {

	private static final long serialVersionUID = -1L;
	@SuppressWarnings("unused")
	private String selectedText = "Click on the table to change the room";
	private GroupForm form;
	
	public GroupsPanel(String id) {
		super(id);
		DataView<Organisation> dataView = new DataView<Organisation>("groupList", new IDataProvider<Organisation>(){
			private static final long serialVersionUID = -1L;

			public void detach() {
				//empty
			}

			public Iterator<? extends Organisation> iterator(long first, long count) {
				return Application.getBean(Organisationmanagement.class).getNondeletedOrganisation((int)first, (int)count).iterator();
			}

			public long size() {
				return Application.getBean(Organisationmanagement.class).selectMaxFromOrganisations();
			}

			public IModel<Organisation> model(Organisation object) {
				return new CompoundPropertyModel<Organisation>(object);
			}
			
		}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(final Item<Organisation> item) {
				final Organisation organisation = item.getModelObject();
				item.add(new Label("organisation_id", "" + organisation.getOrganisation_id()));
				item.add(new Label("name", "" + organisation.getName()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(organisation);
						target.add(form);
					}
				});
				item.add(AttributeModifier.replace("class", (item.getIndex() % 2 == 1) ? "even" : "odd"));
			}
		};
		dataView.setItemsPerPage(8); //FIXME need to be parametrized
		add(dataView);
		add(new AjaxPagingNavigator("navigator", dataView));
		
		Organisation organisation = new Organisation();
		form = new GroupForm("form", organisation);
        add(form);
		
	}
}
