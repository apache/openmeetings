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
package org.apache.openmeetings.web.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation("USER")
public abstract class UserPanel extends BasePanel {
	private static final long serialVersionUID = 8607846885689465360L;

	public UserPanel(String id) {
		super(id);
	}
	
	//FIXME need to be handled somehow differently
	public static List<Room> getMyRooms() {
		List<Room> result = new ArrayList<Room>();
		Room r1 = new Room();
		r1.setIspublic(true);
		r1.setName(WebSession.getString(1306L));
		//FIXME need to be DAO !!!!
		r1.setRoomtype(Application.getBean(RoomManager.class).getRoomTypesById(1L));
		result.add(r1);
		Room r2 = new Room();
		r2.setIspublic(true); //TODO weird
		r2.setName(WebSession.getString(1307L));
		//FIXME need to be DAO !!!!
		r2.setRoomtype(Application.getBean(RoomManager.class).getRoomTypesById(3L));
		result.add(r2);
		result.addAll(Application.getBean(RoomDao.class).getAppointedRoomsByUser(WebSession.getUserId()));
		return result;
	}
}
