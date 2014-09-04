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

import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.OrganisationUserDao;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.admin.AdminUserChoiceProvider;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import org.wicketstuff.select2.Select2Choice;

public class GroupForm extends AdminBaseForm<Organisation> {
	private static final long serialVersionUID = 1L;
	private GroupUsersPanel usersPanel;
	private WebMarkupContainer groupList;
	private Select2Choice<User> userToadd = null;
	
	static String formatUser(User choice) {
		return String.format("%s [%s %s]", choice.getLogin(), choice.getFirstname(), choice.getLastname());
	}
	
	public GroupForm(String id, WebMarkupContainer groupList, Organisation organisation) {
		super(id, new CompoundPropertyModel<Organisation>(organisation));
		this.groupList = groupList;
		setOutputMarkupId(true);
		
		add(new RequiredTextField<String>("name").setLabel(Model.of(WebSession.getString(165))));
		usersPanel = new GroupUsersPanel("users", getOrgId());
		add(usersPanel);

		add(userToadd = new Select2Choice<User>("user2add", Model.of((User)null), new AdminUserChoiceProvider() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getDisplayText(User choice) {
				return formatUser(choice);
			}
		}));
		userToadd.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Organisation o = GroupForm.this.getModelObject();
				User u = userToadd.getModelObject();
				boolean found = false;
				if (o.getId() != null) {
					found = null != getBean(OrganisationUserDao.class).getByOrganizationAndUser(o.getId(), u.getId());
				}
				if (!found && u != null) {
					for (Organisation_Users ou : usersPanel.getUsers2add()) {
						if (ou.getUser().getId().equals(u.getId())) {
							found = true;
							break;
						}
					}
					if (!found) {
						Organisation_Users ou = new Organisation_Users(o);
						ou.setUser(u);
						usersPanel.getUsers2add().add(ou);

						userToadd.setModelObject(null);
						target.add(usersPanel, userToadd);
					}
				}
			}
		});
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ONE_SECOND));
	}
	
	public void updateView(AjaxRequestTarget target) {
		userToadd.setModelObject(null);
		usersPanel.update(getOrgId());
		target.add(this, groupList);
		target.appendJavaScript("groupsInit();");
	}

	private long getOrgId() {
		return getModelObject().getId() != null ? getModelObject().getId() : 0;
	}
	
	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> f) {
		setModelObject(new Organisation());
		updateView(target);
	}
	
	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Organisation org = getModelObject();
		if (org.getId() != null) {
			org = getBean(OrganisationDao.class).get(org.getId());
		} else {
			org = new Organisation();
		}
		setModelObject(org);
		updateView(target);
	}
	
	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(OrganisationDao.class).delete(getModelObject(), getUserId());
		setModelObject(new Organisation());
		updateView(target);
	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Organisation o = getModelObject();
		o = getBean(OrganisationDao.class).update(o, getUserId());
		setModelObject(o);
		getBean(OrganisationUserDao.class).update(usersPanel.getUsers2add(), getUserId());
		hideNewRecord();
		updateView(target);
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
