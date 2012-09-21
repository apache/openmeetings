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

import org.apache.openmeetings.data.user.dao.OrganisationDAO;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;

public class GroupForm extends AdminBaseForm<Organisation> {
	private static final long serialVersionUID = -1720731686053912700L;
	private GroupUsersPanel usersPanel;
	private WebMarkupContainer groupList;
	
	public GroupForm(String id, WebMarkupContainer groupList, Organisation organisation) {
		super(id, new CompoundPropertyModel<Organisation>(organisation));
		this.groupList = groupList;
		setOutputMarkupId(true);
		
		add(new RequiredTextField<String>("name"));
		usersPanel = new GroupUsersPanel("users", getOrgId());
		add(usersPanel);
	}
	
	void updateView(AjaxRequestTarget target) {
		usersPanel.update(getOrgId());
		target.add(this);
	}
	
	private long getOrgId() {
		return getModelObject().getOrganisation_id() != null ? getModelObject().getOrganisation_id() : 0;
	}
	
	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> f) {
		this.setModelObject(new Organisation());
		updateView(target);
	}
	
	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Organisation org = getModelObject();
		if (org.getOrganisation_id() != null) {
			org = Application.getBean(OrganisationDAO.class).get(org.getOrganisation_id());
		} else {
			org = new Organisation();
		}
		this.setModelObject(org);
		updateView(target);
	}
	
	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(OrganisationDAO.class).delete(getModelObject(), WebSession.getUserId());
		target.add(groupList);
	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(OrganisationDAO.class).update(getModelObject(), WebSession.getUserId());
		target.add(groupList);
	}
}
