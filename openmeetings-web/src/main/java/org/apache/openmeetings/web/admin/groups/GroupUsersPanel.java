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

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;

public class GroupUsersPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private long groupId;
	private List<GroupUser> users2add = new ArrayList<GroupUser>();
	
	public GroupUsersPanel(String id, long groupId) {
		super(id);
		this.groupId = groupId;
		setOutputMarkupId(true);
		
		SearchableDataView<GroupUser> dataView = new SearchableDataView<GroupUser>("userList", new GroupUserDataProvider()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<GroupUser> item) {
				final GroupUser orgUser = item.getModelObject();
				User u = orgUser.getUser();
				item.add(new CheckBox("isModerator").add(new OnChangeAjaxBehavior() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						if (orgUser.getId() != null) {
							getBean(GroupUserDao.class).update(orgUser, WebSession.getUserId());
						}
					}
				}));
				Label label = new Label("label", u == null ? "" : GroupForm.formatUser(u));
				if (orgUser.getId() == null) {
					label.add(AttributeAppender.append("class", "newItem"));
				}
				item.add(label);
				item.add(new ConfirmableAjaxBorder("deleteUserBtn", getString("80"), getString("833")) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						if (orgUser.getId() == null) {
							for (int i = 0; i < users2add.size(); ++i) {
								//FIXME ineffective
								if (users2add.get(i).getUser().getId().equals(orgUser.getUser().getId())) {
									users2add.remove(i);
									break;
								}
							}
						} else {
							getBean(GroupUserDao.class).delete(orgUser, WebSession.getUserId());
						}
						target.add(GroupUsersPanel.this);
					}
				});
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
	
	void update(long groupId) {
		this.groupId = groupId;
		users2add.clear();
	}
	
	List<GroupUser> getUsers2add() {
		return users2add;
	}
	
	private class GroupUserDataProvider extends SearchableDataProvider<GroupUser> {
		private static final long serialVersionUID = 1L;

		GroupUserDataProvider() {
			super(GroupUserDao.class);
		}
		
		protected GroupUserDao getDao() {
			return (GroupUserDao)super.getDao();
		}
		
		public long size() {
			return users2add.size() + (search == null ? getDao().count(groupId) : getDao().count(groupId, search));
		}
		
		public java.util.Iterator<? extends GroupUser> iterator(long first, long count) {
			List<GroupUser> list = new ArrayList<GroupUser>();
			list.addAll(users2add);
			list.addAll(search == null && getSort() == null
					? getDao().get(groupId, (int)first, (int)count)
					: getDao().get(groupId, search, (int)first, (int)count, getSortStr()));
			
			return list.iterator();
		}
	}
}
