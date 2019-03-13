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

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.core.JQueryBehavior;

public class RoomsTabbedPanel extends UserPanel {
	private static final long serialVersionUID = 1L;
	@SpringBean
	private RoomDao roomDao;
	@SpringBean
	private UserDao userDao;

	public RoomsTabbedPanel(String id) {
		super(id);
		setRenderBodyOnly(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		User u = userDao.get(getUserId());
		add(new ListView<GroupUser>("orgTabs", u.getGroupUsers()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<GroupUser> item) {
				Group org = item.getModelObject().getGroup();
				item.add(new WebMarkupContainer("link")
					.add(new Label("name", Model.of(org.getName())))
					.add(AttributeModifier.replace("href", "#org" + org.getId())));
			}
		});
		add(new ListView<GroupUser>("orgRooms", u.getGroupUsers()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<GroupUser> item) {
				Group org = item.getModelObject().getGroup();
				item.add(new RoomsPanel("rooms"
					, roomDao.getGroupRooms(org.getId()))
					.setMarkupId("org" + org.getId())).setRenderBodyOnly(true);
			}
		});
		add(new JQueryBehavior("#orgTabs", "tabs"));
	}
}
