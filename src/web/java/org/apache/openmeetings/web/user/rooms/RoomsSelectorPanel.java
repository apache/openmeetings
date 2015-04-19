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

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.openmeetings.web.util.OmUrlFragment.MenuParams;
import org.apache.wicket.markup.html.basic.Label;

public class RoomsSelectorPanel extends UserPanel {
	private static final long serialVersionUID = -3246259803623925341L;

	public RoomsSelectorPanel(String id, MenuParams param) {
		super(id);

		long title, desc;
		RoomDao roomDao = Application.getBean(RoomDao.class);
		switch (param) {
			case myTabButton:
				title = 781L;
				desc = 782L;
				add(new RoomsPanel("rooms", getMyRooms()));
				break;
			case privateTabButton:
				title = 779L;
				desc = 780L;
				add(new RoomsTabbedPanel("rooms")); 
				break;
			case publicTabButton:
			default:
				title = 777L;
				desc = 778L;
				add(new RoomsPanel("rooms", roomDao.getPublicRooms()));
				break;
		}
		add(new Label("title", Application.getString(title)));
		add(new Label("desc", Application.getString(desc)));
	}
}
