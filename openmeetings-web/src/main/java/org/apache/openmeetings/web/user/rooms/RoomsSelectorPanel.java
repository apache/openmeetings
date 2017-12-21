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

import static org.apache.openmeetings.web.common.UserPanel.getMyRooms;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.UserBasePanel;
import org.apache.openmeetings.web.util.OmUrlFragment.MenuParams;
import org.apache.wicket.markup.html.basic.Label;

public class RoomsSelectorPanel extends UserBasePanel {
	private static final long serialVersionUID = 1L;
	private String title;
	private String desc;

	public RoomsSelectorPanel(String id, MenuParams param) {
		super(id);

		RoomDao roomDao = Application.getBean(RoomDao.class);
		switch (param) {
			case myTabButton:
				title = "781";
				desc = "782";
				add(new RoomsPanel("rooms", getMyRooms()));
				break;
			case privateTabButton:
				title = "779";
				desc = "780";
				add(new RoomsTabbedPanel("rooms"));
				break;
			case publicTabButton:
			default:
				title = "777";
				desc = "778";
				add(new RoomsPanel("rooms", roomDao.getPublicRooms()));
				break;
		}
	}

	@Override
	protected void onInitialize() {
		add(new Label("title", getString(title)));
		add(new Label("desc", getString(desc)));
		super.onInitialize();
	}
}
