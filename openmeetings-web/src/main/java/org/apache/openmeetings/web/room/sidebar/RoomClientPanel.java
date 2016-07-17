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
package org.apache.openmeetings.web.room.sidebar;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.sidebar.icon.KickRightIcon;
import org.apache.openmeetings.web.room.sidebar.icon.RefreshIcon;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;

public class RoomClientPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public RoomClientPanel(String id, ListItem<Client> item, final RoomPanel room) {
		super(id, item.getModel());
		setRenderBodyOnly(true);
		Client c = item.getModelObject();
		item.setMarkupId(String.format("user%s", c.getUid()));
		User u = getBean(UserDao.class).get(c.getUserId());
		add(new RefreshIcon("refresh", c, room));
		add(new Label("name", u.getFirstname() + " " + u.getLastname()));
		add(AttributeAppender.append("data-userid", c.getUserId()));
		WebMarkupContainer actions = new WebMarkupContainer("actions");
		actions.add(new RoomRightPanel("rights", c, room));
		actions.add(new KickRightIcon("kick", c, room));
		actions.add(new WebMarkupContainer("privateChat").setVisible(!room.getRoom().isHidden(RoomElement.Chat) && !getUserId().equals(c.getUserId())));
		if (room.getClient() != null) {
			actions.setVisible(room.getClient().hasRight(Right.moderator));
			if (c.getUid().equals(room.getClient().getUid())) {
				item.add(AttributeAppender.append("class", "current"));
			}
		} else {
			actions.setVisible(false);
		}
		add(actions);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		Client c = (Client)getDefaultModelObject();
		String status = null, statusTitle = null;
		if (c.hasRight(Right.moderator)) {
			status = "status-mod";
			statusTitle = "679";
		} else if (c.hasRight(Right.whiteBoard)) {
			status = "status-wb";
			statusTitle = "678";
		} else {
			status = "status-user";
			statusTitle = "677";
		}
		//FIXME TODO add 'typingActivity'
		//FIXME TODO add ability to change 'first/last name'
		add(new WebMarkupContainer("status").add(AttributeAppender.append("class", status), AttributeAppender.replace("title", getString(statusTitle))));
	}
}
