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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

@AuthorizeInstantiation("Dashboard")
public abstract class UserPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public UserPanel(String id) {
		super(id);
	}

	public UserPanel(String id, IModel<?> model) {
		super(id, model);
	}

	public static List<Room> getMyRooms() {
		List<Room> result = new ArrayList<>();
		result.add(getBean(RoomDao.class).getUserRoom(getUserId(), Room.Type.conference, Application.getString("my.room.conference")));
		result.add(getBean(RoomDao.class).getUserRoom(getUserId(), Room.Type.presentation, Application.getString("my.room.presentation")));
		result.addAll(getBean(RoomDao.class).getAppointedRoomsByUser(getUserId()));
		return result;
	}
}
