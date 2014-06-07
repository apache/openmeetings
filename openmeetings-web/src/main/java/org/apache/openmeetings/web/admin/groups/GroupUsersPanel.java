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

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.user.OrganisationUserDao;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ConfirmCallListener;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;

public class GroupUsersPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private long organisationId;
	private List<Organisation_Users> users2add = new ArrayList<Organisation_Users>();
	
	public GroupUsersPanel(String id, long orgId) {
		super(id);
		this.organisationId = orgId;
		setOutputMarkupId(true);
		
		SearchableDataView<Organisation_Users> dataView = new SearchableDataView<Organisation_Users>("userList", new OrgUserDataProvider()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Organisation_Users> item) {
				final Organisation_Users orgUser = item.getModelObject();
				User u = orgUser.getUser();
				item.add(new CheckBox("isModerator").add(new OnChangeAjaxBehavior() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						if (orgUser.getOrganisation_users_id() != null) {
							Application.getBean(OrganisationUserDao.class).update(orgUser, WebSession.getUserId());
						}
					}
				}));
				Label label = new Label("label", u == null ? "" : GroupForm.formatUser(u));
				if (orgUser.getOrganisation_users_id() == null) {
					label.add(AttributeAppender.append("class", "newItem"));
				}
				item.add(label);
				item.add(new WebMarkupContainer("deleteUserBtn").add(new AjaxEventBehavior("onclick"){
					private static final long serialVersionUID = 1L;

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);
						attributes.getAjaxCallListeners().add(new ConfirmCallListener(833L));
					}
					
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						if (orgUser.getOrganisation_users_id() == null) {
							for (int i = 0; i < users2add.size(); ++i) {
								//FIXME ineffective
								if (users2add.get(i).getUser().getUser_id().equals(orgUser.getUser().getUser_id())) {
									users2add.remove(i);
									break;
								}
							}
						} else {
							Application.getBean(OrganisationUserDao.class).delete(orgUser, WebSession.getUserId());
						}
						target.add(GroupUsersPanel.this);
					}
				}));
			}
		};
		add(dataView).setOutputMarkupId(true);
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(GroupUsersPanel.this);
			}
		});
	}
	
	void update(long orgId) {
		organisationId = orgId;
		users2add.clear();
	}
	
	List<Organisation_Users> getUsers2add() {
		return users2add;
	}
	
	private class OrgUserDataProvider extends SearchableDataProvider<Organisation_Users> {
		private static final long serialVersionUID = 1L;

		OrgUserDataProvider() {
			super(OrganisationUserDao.class);
		}
		
		protected OrganisationUserDao getDao() {
			return (OrganisationUserDao)super.getDao();
		}
		
		public long size() {
			return users2add.size() + (search == null ? getDao().count(organisationId) : getDao().count(organisationId, search));
		}
		
		public java.util.Iterator<? extends Organisation_Users> iterator(long first, long count) {
			List<Organisation_Users> list = new ArrayList<Organisation_Users>();
			list.addAll(users2add);
			list.addAll(search == null && getSort() == null
					? getDao().get(organisationId, (int)first, (int)count)
					: getDao().get(organisationId, search, (int)first, (int)count, getSortStr()));
			
			return list.iterator();
		}
	}
}
