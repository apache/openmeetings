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
package org.apache.openmeetings.axis.services;

import org.apache.axis2.AxisFault;
import org.apache.openmeetings.persistence.beans.room.Room;

/**
 * This class provides method entry points necessary for OM to Jabber integration.
 * 
 * @author solomax
 *
 */
public class JabberWebServiceFacade extends BaseWebService {
	
	/**
	 * Get array of all rooms available to the user.
	 * No admin rights are necessary for this call
	 * 
	 * @param SID The SID from {@link UserWebService.getSession}
	 * @return array of Rooms
	 * @throws AxisFault 
	 */
	public Room[] getAvailableRooms(String SID) throws AxisFault {
		return getBean(JabberWebService.class).getAvailableRooms(SID).toArray(new Room[0]);
	}

	/**
	 * Returns the count of users currently in the Room with given id
	 * No admin rights are necessary for this call
	 * 
	 * @param SID The SID from {@link UserWebService.getSession}
	 * @param roomId id of the room to get users
	 * @return number of users as int
	 * @throws AxisFault 
	 */
	public int getUserCount(String SID, Long roomId) throws AxisFault {
		return getBean(JabberWebService.class).getUserCount(SID, roomId);
	}

	/**
	 * Get invitation hash for the room with given id
	 * No admin rights are necessary for this call
	 * 
	 * @param SID The SID from {@link UserWebService.getSession}
	 * @param username The name of invited user, will be displayed in the rooms user list
	 * @param room_id id of the room to get users
	 * @return hash to enter the room
	 * @throws AxisFault 
	 */
	public String getInvitationHash(String SID, String username, Long room_id) throws AxisFault {
		return getBean(JabberWebService.class)
				.getInvitationHash(SID, username, room_id);
	}
}