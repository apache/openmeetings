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

import org.apache.openmeetings.data.user.dao.OrganisationUserDAO;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.ConfirmCallListener;
import org.apache.openmeetings.web.components.admin.OmDataView;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.data.OmDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

public class GroupUsersPanel extends Panel {
	private static final long serialVersionUID = -1813488722913433227L;
	private long organisationId;
	
	public static String getUser(Users u) {
		return u.getLogin() + " [" + u.getFirstname() + ", " + u.getLastname() + "]";
	}
	
	public GroupUsersPanel(String id, long orgId) {
		super(id);
		this.organisationId = orgId;
		setOutputMarkupId(true);
		
		OmDataView<Organisation_Users> dataView = new OmDataView<Organisation_Users>("userList", new OmDataProvider<Organisation_Users>(OrganisationUserDAO.class){
			private static final long serialVersionUID = 1L;

			public long size() {
				return search == null
						? Application.getBean(OrganisationUserDAO.class).count(organisationId)
						: Application.getBean(OrganisationUserDAO.class).count(organisationId, search);
			}
			
			public java.util.Iterator<? extends Organisation_Users> iterator(long first, long count) {
				return (search == null && getSort() == null
						? Application.getBean(OrganisationUserDAO.class).get(organisationId, (int)first, (int)count)
						: Application.getBean(OrganisationUserDAO.class).get(organisationId, search, (int)first, (int)count, getSortStr())).iterator();
			}
		}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<Organisation_Users> item) {
				final Organisation_Users orgUser = item.getModelObject();
				Users u = orgUser.getUser();
				if (u != null) {
					item.add(new Label("label", Model.of(getUser(u))));
				} else {
					item.add(new Label("label", Model.of("")));
				}
				item.add(new WebMarkupContainer("deleteUserBtn").add(new AjaxEventBehavior("onclick"){
					private static final long serialVersionUID = 1L;

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);
						attributes.getAjaxCallListeners().add(new ConfirmCallListener(833L));
					}
					
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						Application.getBean(OrganisationUserDAO.class).delete(orgUser, WebSession.getUserId());
						target.add(GroupUsersPanel.this);
					}
				})); 
				item.add(AttributeModifier.append("class", ((item.getIndex() % 2 == 1) ? "even" : "odd")));
			}
		};
		add(dataView).setOutputMarkupId(true);
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(GroupUsersPanel.this);
			}
		});
	}
	
	void update(long orgId) {
		organisationId = orgId;
	}
}
