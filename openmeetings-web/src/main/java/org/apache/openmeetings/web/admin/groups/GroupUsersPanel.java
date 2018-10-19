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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;

public class GroupUsersPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private long groupId;
	private List<GroupUser> users2add = new ArrayList<>();

	public GroupUsersPanel(String id, long groupId) {
		super(id);
		this.groupId = groupId;
		setOutputMarkupId(true);

		SearchableDataView<GroupUser> dataView = new SearchableDataView<GroupUser>("userList", new GroupUserDataProvider()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<GroupUser> item) {
				final GroupUser grpUser = item.getModelObject();
				item.add(new CheckBox("isModerator").add(new OnChangeAjaxBehavior() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						if (grpUser.getId() != null) {
							update(grpUser);
						}
					}
				}));
				User u = grpUser.getUser();
				Label label = new Label("label", u == null ? "" : GroupForm.formatUser(u));
				if (grpUser.getId() == null) {
					label.add(AttributeModifier.append(ATTR_CLASS, "newItem"));
				}
				item.add(label);
				item.add(new ConfirmableAjaxBorder("deleteUserBtn", getString("80"), getString("833")) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onInitialize() {
						super.onInitialize();
						form.setMultiPart(true);//need to be multipart due to parent form is multipart
					}

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						if (grpUser.getId() == null) {
							users2add.remove(grpUser);
						} else {
							UserDao uDao = getBean(UserDao.class);
							User u = uDao.get(grpUser.getUser().getId());
							u.getGroupUsers().remove(grpUser);
							uDao.update(u, WebSession.getUserId());
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

	public static void update(GroupUser grpUser) {
		UserDao uDao = getBean(UserDao.class);
		User u = uDao.get(grpUser.getUser().getId());
		int idx = u.getGroupUsers().indexOf(grpUser);
		if (idx > -1) {
			u.getGroupUsers().get(idx).setModerator(grpUser.isModerator());
		} else {
			u.getGroupUsers().add(grpUser);
		}
		uDao.update(u, WebSession.getUserId());
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

		@Override
		protected GroupUserDao getDao() {
			return (GroupUserDao)super.getDao();
		}

		@Override
		public long size() {
			return users2add.size() + (search == null ? getDao().count(groupId) : getDao().count(search));
		}

		@Override
		public java.util.Iterator<? extends GroupUser> iterator(long first, long count) {
			List<GroupUser> list = new ArrayList<>();
			list.addAll(users2add);
			list.addAll(search == null && getSort() == null
					? getDao().get(groupId, first, count)
					: getDao().get(groupId, search, first, count, getSortStr()));

			return list.iterator();
		}
	}
}
