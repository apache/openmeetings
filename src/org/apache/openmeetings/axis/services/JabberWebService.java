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

import java.util.List;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.room.RoomOrganisation;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.remote.ConferenceService;
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
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private UserManager userManager;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConferenceService conferenceService;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private ConfigurationDao configurationDao;

	/**
	 * Get List&lt;Rooms&gt; of all rooms available to the user.
	 * No admin rights are necessary for this call
	 * 
	 * @param SID The SID from UserService.getSession
	 * @return List&lt;Rooms&gt; of Rooms
	 */
	public List<Room> getAvailableRooms(String SID) {
		log.debug("getAvailableRooms enter");

		List<Room> result = this.conferenceService
				.getAppointedMeetingRoomsWithoutType(SID);

		List<Room> pbl = this.conferenceService.getRoomsPublicWithoutType(SID);
		if (pbl != null) {
			result.addAll(pbl);
		}

		Long users_id = this.sessiondataDao.checkSession(SID);
		User u = this.userManager.getUserById(users_id);
		for (Organisation_Users ou : u.getOrganisation_users()) {
			List<RoomOrganisation> rol = this.conferenceService
					.getRoomsByOrganisationWithoutType(SID, ou
							.getOrganisation().getOrganisation_id().longValue());
			if (rol != null) {
				for (RoomOrganisation ro : rol) {
					result.add(ro.getRoom());
				}
			}
		}
		for (Room r : result) {
			r.setCurrentusers(null);
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

		if (this.authLevelUtil.checkUserLevel(user_level)) {
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
		Invitations invitation = this.invitationManager.addInvitationLink(
				user_level, username, username, username, username, username,
				room_id, "", Boolean.valueOf(false), null, Integer.valueOf(3),
				null, null, users_id, "", Long.valueOf(1L),
				Boolean.valueOf(false), null, null, null, username
				, omTimeZoneDaoImpl.getOmTimeZone(configurationDao.getConfValue("default.timezone", String.class, "Europe/Berlin")));

		return ((invitation == null) ? null : invitation.getHash());
	}
}
