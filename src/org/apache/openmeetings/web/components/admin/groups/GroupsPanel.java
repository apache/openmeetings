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

import static org.apache.openmeetings.web.components.admin.groups.GroupUsersPanel.getUser;

import org.apache.openmeetings.data.user.dao.OrganisationDAO;
import org.apache.openmeetings.data.user.dao.UsersDaoImpl;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.data.OmDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class GroupsPanel extends AdminPanel {
	private static final long serialVersionUID = -5170400556006464830L;
	private String userSearchText;
	
	public GroupsPanel(String id) {
		super(id);
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
		final GroupForm form = new GroupForm("form", listContainer, new Organisation());
        add(form);
		
		DataView<Organisation> dataView = new DataView<Organisation>("groupList", new OmDataProvider<Organisation>(OrganisationDAO.class)) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<Organisation> item) {
				final Organisation organisation = item.getModelObject();
				item.add(new Label("organisation_id", "" + organisation.getOrganisation_id()));
				item.add(new Label("name", "" + organisation.getName()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(organisation);
						form.updateView(target);
						target.add(form);
					}
				});
				item.add(AttributeModifier.append("class", "clickable " + ((item.getIndex() % 2 == 1) ? "even" : "odd")));
			}
		};

		add(listContainer.add(dataView).setOutputMarkupId(true));
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});
		
		add(new Form<Void>("addUsers"){
			private static final long serialVersionUID = 3726465576292784604L;

			{
				add(new TextField<String>("searchText", new PropertyModel<String>(GroupsPanel.this, "userSearchText")));
				add(new AjaxButton("search", Model.of(WebSession.getString(182L))) {
					private static final long serialVersionUID = -4752180617634945030L;

					protected void onAfterSubmit(AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
						
					}
				});
				add(new ListMultipleChoice<Users>("users", Application.getBean(UsersDaoImpl.class).getAllUsers(), new IChoiceRenderer<Users>() {
					private static final long serialVersionUID = 1L;

					public Object getDisplayValue(Users object) {
						return getUser(object);
					}

					public String getIdValue(Users object, int index) {
						return "" + object.getUser_id();
					}
				}));
				add(new AjaxButton("add", Model.of(WebSession.getString(175L))) {
					private static final long serialVersionUID = 5553555064487161840L;

					protected void onAfterSubmit(AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
						
					}
				});
			}
		});
	}
}
