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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.List;

import org.apache.openmeetings.db.dao.user.AdminUserDao;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.AdminCommonUserForm;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

public class GroupForm extends AdminCommonUserForm<Organisation> {
	private static final long serialVersionUID = -1720731686053912700L;
	private GroupUsersPanel usersPanel;
	private WebMarkupContainer groupList;
	
	public GroupForm(String id, WebMarkupContainer groupList, Organisation organisation) {
		super(id, new CompoundPropertyModel<Organisation>(organisation));
		this.groupList = groupList;
		setOutputMarkupId(true);
		
		add(new RequiredTextField<String>("name").setLabel(Model.of(WebSession.getString(165))));
		usersPanel = new GroupUsersPanel("users", getOrgId());
		add(usersPanel);

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown", Duration.ONE_SECOND);
	}
	
	@Override
	public void updateView(AjaxRequestTarget target) {
		usersPanel.update(getOrgId());
		target.add(this);
		target.appendJavaScript("groupsInit();");
	}

	@Override
	public void submitView(AjaxRequestTarget target, List<User> usersToAdd) {
		// TODO Auto-generated method stub
		AdminUserDao userDao = Application.getBean(AdminUserDao.class);
		Organisation organisation = getModelObject();
		for (User u : usersToAdd) {
			List<Organisation_Users> orgUsers = u.getOrganisation_users();
			boolean found = false;
			for (Organisation_Users ou : orgUsers) {
				if (ou.getOrganisation().getOrganisation_id().equals(organisation.getOrganisation_id())) {
					found = true;
					break;
				}
			}
			if (!found) {
				Organisation_Users orgUser = new Organisation_Users(organisation);
				orgUser.setDeleted(false);
				orgUsers.add(orgUser);
				userDao.update(u, WebSession.getUserId());
			}
		}
		target.add(usersPanel);
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
			org = getBean(OrganisationDao.class).get(org.getOrganisation_id());
		} else {
			org = new Organisation();
		}
		this.setModelObject(org);
		updateView(target);
	}
	
	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(OrganisationDao.class).delete(getModelObject(), getUserId());
		target.add(groupList);
		target.appendJavaScript("groupsInit();");
	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(OrganisationDao.class).update(getModelObject(), getUserId());
		hideNewRecord();
		target.add(groupList);
		target.appendJavaScript("groupsInit();");
	}

	@Override
	protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onNewError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}
}
