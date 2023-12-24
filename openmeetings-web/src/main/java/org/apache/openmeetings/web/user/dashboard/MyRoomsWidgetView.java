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
package org.apache.openmeetings.web.user.dashboard;

import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.user.rooms.RoomsPanel;
import org.apache.wicket.model.Model;

import org.wicketstuff.dashboard.Widget;
import org.wicketstuff.dashboard.web.WidgetView;

import jakarta.inject.Inject;

public class MyRoomsWidgetView extends WidgetView {
	private static final long serialVersionUID = 1L;

	@Inject
	private RoomDao roomDao;

	public MyRoomsWidgetView(String id, Model<Widget> model) {
		super(id, model);

		add(new RoomsPanel("rooms", roomDao.getMyRooms(getUserId(), Application.getString("my.room.conference"), Application.getString("my.room.presentation"))));
	}
}
