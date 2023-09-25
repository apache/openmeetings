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
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_GROUP;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_MY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isMyRoomsEnabled;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.UserBasePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

import jakarta.inject.Inject;


public class RoomsSelectorPanel extends UserBasePanel {
	private static final long serialVersionUID = 1L;
	private static final String PANEL_ID = "rooms";
	private final String type;

	@Inject
	private RoomDao roomDao;

	public RoomsSelectorPanel(String id, String type) {
		super(id);
		this.type = type;
	}

	@Override
	protected void onInitialize() {
		String title;
		String desc;
		if (TYPE_GROUP.equals(type)) {
			title = "779";
			desc = "780";
			add(new RoomsTabbedPanel(PANEL_ID));
		} else if (isMyRoomsEnabled() && TYPE_MY.equals(type)) {
			title = "781";
			desc = "782";
			add(new RoomsPanel(PANEL_ID, roomDao.getMyRooms(getUserId(), Application.getString("my.room.conference"), Application.getString("my.room.presentation"))));
		} else {
			title = "777";
			desc = "778";
			add(new RoomsPanel(PANEL_ID, roomDao.getPublicRooms()));
		}
		add(new Label("title", new ResourceModel(title)));
		add(new Label("desc", new ResourceModel(desc)).setRenderBodyOnly(true));
		add(new WebMarkupContainer("desc-info").add(AttributeModifier.append(ATTR_TITLE, getString(desc))));
		super.onInitialize();
	}
}
