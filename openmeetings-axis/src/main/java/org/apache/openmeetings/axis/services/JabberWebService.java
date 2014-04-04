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

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.AdminUserDao;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomOrganisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.remote.ConferenceService;
import org.apache.openmeetings.util.AuthLevelUtil;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class provides method implementations necessary for OM to Jabber integration.
 * 
 * @author solomax
 * @webservice JabberService
 *
 */
public class JabberWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(
			JabberWebService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private UserManager userManager;
	@Autowired
	private AdminUserDao userDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConferenceService conferenceService;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private RoomDao roomDao;

	/**
	 * Get List&lt;RoomDTO&gt; of all rooms available to the user.
	 * No admin rights are necessary for this call
	 * 
	 * @param SID The SID from UserService.getSession
	 * @return List&lt;RoomDTO&gt; of Rooms
	 */
	public List<RoomDTO> getAvailableRooms(String SID) {
		log.debug("getAvailableRooms enter");
		
		List<Room> rl = new ArrayList<Room>();
		List<Room> al = conferenceService.getAppointedMeetingRoomsWithoutType(SID);
		if (al != null) {
			rl.addAll(al);
		}

		List<Room> pbl = conferenceService.getRoomsPublicWithoutType(SID);
		if (pbl != null) {
			rl.addAll(pbl);
		}

		Long users_id = sessiondataDao.checkSession(SID);
		User u = userManager.getUserById(users_id);
		for (Organisation_Users ou : u.getOrganisation_users()) {
			List<RoomOrganisation> rol = conferenceService.getRoomsByOrganisationWithoutType(SID
					, ou.getOrganisation().getOrganisation_id().longValue());
			if (rol != null) {
				for (RoomOrganisation ro : rol) {
					rl.add(ro.getRoom());
				}
			}
		}
		List<RoomDTO> result = new ArrayList<RoomDTO>();
		for (Room r : rl) {
			r.setCurrentusers(null);
			result.add(new RoomDTO(r));
		}
		return result;
	}

	/**
	 * Returns the count of users currently in the Room with given id
	 * No admin rights are necessary for this call
	 * 
	 * @param SID The SID from UserService.getSession
	 * @param roomId id of the room to get users
	 * @return number of users as int
	 */
	public int getUserCount(String SID, Long roomId) {
		Long users_id = this.sessiondataDao.checkSession(SID);
		Long user_level = this.userManager.getUserLevelByID(users_id);

		if (AuthLevelUtil.checkUserLevel(user_level)) {
			return conferenceService.getRoomClientsListByRoomId(roomId).size();
		}
		return -1;
	}

	/**
	 * Get invitation hash for the room with given id
	 * No admin rights are necessary for this call
	 * 
	 * @param SID The SID from UserService.getSession
	 * @param username The name of invited user, will be displayed in the rooms user list
	 * @param room_id id of the room to get users
	 * @return hash to enter the room
	 */
	public String getInvitationHash(String SID, String username, Long room_id) {
		Long users_id = this.sessiondataDao.checkSession(SID);
		Long user_level = this.userManager.getUserLevelByID(users_id);
		
		if (AuthLevelUtil.checkUserLevel(user_level)) {
			User invitee = userDao.getContact(username, username, username, users_id);
			Invitation invitation = invitationManager.getInvitation(invitee, roomDao.get(room_id),
							false, "", Valid.OneTime
							, userDao.get(users_id), "", 1L,
							null, null, null);
	
			return ((invitation == null) ? null : invitation.getHash());
		} else {
			return "Need Admin Privileges to perfom the Action";
		}
	}
}
