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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.JQueryBehavior;

public class RoomsTabbedPanel extends UserPanel {
	private static final long serialVersionUID = 3642004664480074881L;

	public RoomsTabbedPanel(String id) {
		super(id);
		
		User u = getBean(UserDao.class).get(getUserId());
		add(new ListView<Organisation_Users>("orgTabs", u.getOrganisation_users()) {
			private static final long serialVersionUID = -145637079945252731L;

			@Override
			protected void populateItem(ListItem<Organisation_Users> item) {
				Organisation org = item.getModelObject().getOrganisation();
				item.add(new WebMarkupContainer("link")
					.add(new Label("name", Model.of(org.getName())))
					.add(new AttributeModifier("href", "#org" + org.getOrganisation_id())));
			}
		});
		add(new ListView<Organisation_Users>("orgRooms", u.getOrganisation_users()) {
			private static final long serialVersionUID = 9039932126334250798L;

			@Override
			protected void populateItem(ListItem<Organisation_Users> item) {
				Organisation org = item.getModelObject().getOrganisation();
				item.add(new RoomsPanel("rooms"
					, getBean(RoomDao.class).getOrganisationRooms(org.getOrganisation_id()))
					.setMarkupId("org" + org.getOrganisation_id())).setRenderBodyOnly(true);
			}
		});
		add(new JQueryBehavior("#orgTabs", "tabs"));
	}
}
