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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.pages.BasePage.ALIGN_LEFT;
import static org.apache.openmeetings.web.pages.BasePage.ALIGN_RIGHT;
import static org.apache.openmeetings.web.util.ProfileImageResourceReference.getUrl;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.web.pages.BasePage;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.sidebar.icon.KickIcon;
import org.apache.openmeetings.web.room.sidebar.icon.RefreshIcon;
import org.apache.openmeetings.web.room.sidebar.icon.UserSpeaksIcon;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;

public class RoomClientPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public RoomClientPanel(String id, ListItem<Client> item, final RoomPanel room) {
		super(id, item.getModel());
		setRenderBodyOnly(true);
		Client c = item.getModelObject();
		final String uid = c.getUid();
		item.setMarkupId(String.format("user%s", c.getUid()));
		item.add(AttributeModifier.append("style", String.format("background-image: url(%s);", getUrl(RequestCycle.get(), c.getUser()))));
		item.add(AttributeModifier.append("data-userid", c.getUserId()));
		add(new RefreshIcon("refresh", uid));
		final String name = c.getUser().getDisplayName();
		add(new Label("name", name));
		add(new UserSpeaksIcon("user-speaks", uid));
		item.add(AttributeModifier.replace(ATTR_TITLE, name));
		WebMarkupContainer actions = new WebMarkupContainer("actions");
		actions.add(new KickIcon("kick", uid));
		actions.add(new WebMarkupContainer("privateChat").setVisible(!room.getRoom().isHidden(RoomElement.Chat) && !getUserId().equals(c.getUserId())));
		actions.setVisible(room.getClient().hasRight(Right.moderator));
		if (c.getUid().equals(room.getClient().getUid())) {
			actions.add(new SelfIconsPanel("icons", uid, false));
			item.add(AttributeModifier.append(ATTR_CLASS, "current"));
		} else {
			actions.add(new ClientIconsPanel("icons", uid));
		}
		add(actions);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Client c = (Client)getDefaultModelObject();
		String status, statusTitle;
		if (c.hasRight(Right.moderator)) {
			status = "status-mod";
			statusTitle = "679";
		} else if (c.hasRight(Right.whiteBoard) || c.hasRight(Right.presenter)) {
			status = "status-wb";
			statusTitle = "678";
		} else {
			status = "status-user";
			statusTitle = "677";
		}
		status = String.format("%s %s", status, ((BasePage)getPage()).isRtl() ? ALIGN_LEFT : ALIGN_RIGHT);
		add(new WebMarkupContainer("status").add(AttributeModifier.append(ATTR_CLASS, status), AttributeModifier.replace(ATTR_TITLE, getString(statusTitle))));
	}
}
