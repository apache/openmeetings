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

import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.badge.BootstrapBadge;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.utilities.BackgroundColorBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import jakarta.inject.Inject;

public class GroupUsersPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private long groupId;
	private List<GroupUser> users2add = new ArrayList<>();

	@Inject
	private UserDao userDao;

	public GroupUsersPanel(String id, long groupId) {
		super(id);
		this.groupId = groupId;
		setOutputMarkupId(true);

		SearchableDataView<GroupUser> dataView = new SearchableDataView<>("userList", new GroupUserDataProvider()) {
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
				item.add(label);
				BootstrapAjaxLink<String> del = new BootstrapAjaxLink<>("deleteUserBtn", Buttons.Type.Outline_Danger) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						if (grpUser.getId() == null) {
							users2add.remove(grpUser);
						} else {
							User u = userDao.get(grpUser.getUser().getId());
							u.getGroupUsers().remove(grpUser);
							userDao.update(u, WebSession.getUserId());
						}
						target.add(GroupUsersPanel.this);
					}
				};
				del.setIconType(FontAwesome6IconType.xmark_s)
						.add(newOkCancelDangerConfirm(this, getString("833")));
				item.add(del);
				item.add(new BootstrapBadge("new", new ResourceModel("lbl.new"), BackgroundColorBehavior.Color.Warning).setVisible((grpUser.getId() == null)));
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

	public void update(GroupUser grpUser) {
		User u = userDao.get(grpUser.getUser().getId());
		int idx = u.getGroupUsers().indexOf(grpUser);
		if (idx > -1) {
			u.getGroupUsers().get(idx).setModerator(grpUser.isModerator());
		} else {
			u.getGroupUsers().add(grpUser);
		}
		userDao.update(u, WebSession.getUserId());
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
					: getDao().get(groupId, search, first, count, getSort()));

			return list.iterator();
		}
	}
}
