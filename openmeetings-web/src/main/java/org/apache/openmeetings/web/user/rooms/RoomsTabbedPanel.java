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
package org.apache.openmeetings.web.user.rooms;

import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.stream.Stream;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;


import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import jakarta.inject.Inject;

public class RoomsTabbedPanel extends UserPanel {
	private static final long serialVersionUID = 1L;

	@Inject
	private RoomDao roomDao;
	@Inject
	private UserDao userDao;

	public RoomsTabbedPanel(String id) {
		super(id);
		setRenderBodyOnly(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		User u = userDao.get(getUserId());
		Stream<Group> groups = u.getGroupUsers().stream()
				.map(GroupUser::getGroup)
				.filter(g -> !roomDao.getGroupRooms(g.getId()).isEmpty());
		add(new AjaxBootstrapTabbedPanel<>("orgTabs", groups.map(g -> new AbstractTab(Model.of(g.getName())) {
			private static final long serialVersionUID = 1L;

			@Override
			public WebMarkupContainer getPanel(String panelId) {
				return new RoomsPanel(panelId, roomDao.getGroupRooms(g.getId()));
			}
		}).toList()));
	}
}
