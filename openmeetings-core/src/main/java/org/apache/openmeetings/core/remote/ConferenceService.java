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
package org.apache.openmeetings.core.remote;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.core.data.conference.RoomManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomModeratorsDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.room.RoomOrganisation;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author sebawagner
 * 
 */
public class ConferenceService {
	private static final Logger log = Red5LoggerFactory.getLogger(ConferenceService.class, webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private IUserManager userManager;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RoomModeratorsDao roomModeratorsDao;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private ISessionManager sessionManager = null;
	@Autowired
	private ServerDao serverDao;
	@Autowired
	private ConfigurationDao cfgDao;

	/**
	 * ( get a List of all available Rooms of this organization
	 * (non-appointments)
	 * 
	 * @param SID
	 * @param organisationId
	 * @return - all available Rooms of this organization
	 */
	public List<RoomOrganisation> getRoomsByOrganisationAndType(String SID, long organisationId, long roomTypeId) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				log.debug("getRoomsByOrganisationAndType");
				List<RoomOrganisation> roomOrgsList = roomManager.getRoomsOrganisationByOrganisationIdAndRoomType(organisationId, roomTypeId);
	
				List<RoomOrganisation> filtered = new ArrayList<RoomOrganisation>();
	
				for (Iterator<RoomOrganisation> iter = roomOrgsList.iterator(); iter
						.hasNext();) {
					RoomOrganisation orgRoom = iter.next();
	
					if (!orgRoom.getRoom().isAppointment()) {
						orgRoom.getRoom().setCurrentusers(
								this.getRoomClientsListByRoomId(orgRoom.getRoom()
										.getId()));
						filtered.add(orgRoom);
					}
				}
				return filtered;
			}
		} catch (Exception err) {
			log.error("[getRoomsByOrganisationAndType]", err);
		}
		return null;
	}

	public List<RoomOrganisation> getRoomsByOrganisationWithoutType(
			String SID, long organisationId) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				log.debug("getRoomsByOrganisationAndType");
				List<RoomOrganisation> roomOrgsList = roomManager.getRoomsOrganisationByOrganisationId(organisationId);
				
				for (RoomOrganisation roomOrg : roomOrgsList) {
					roomOrg.getRoom().setCurrentusers(sessionManager.getClientListByRoom(roomOrg.getRoom().getId()));
				}
	
				return roomOrgsList;
			}
		} catch (Exception err) {
			log.error("[getRoomsByOrganisationAndType]", err);
		}
		return null;
	}

	/**
	 * gets all rooms of an organization TODO:check if the requesting user is
	 * also member of that organization
	 * 
	 * @param SID
	 * @param organisationId
	 * @return - all rooms of an organization
	 */
	public SearchResult<RoomOrganisation> getRoomsByOrganisation(String SID,
			long organisationId, int start, int max, String orderby,
			boolean asc) {

		log.debug("getRoomsByOrganisation");

		Long user_id = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasModLevel(userDao.get(user_id), organisationId)) {
			return roomManager.getRoomsOrganisationsByOrganisationId(organisationId, start, max, orderby, asc);
		}
		return null;
	}

	/**
	 * get a List of all public availible rooms (non-appointments)
	 * 
	 * @param SID
	 * @param roomtypesId
	 * @return - public rooms with given type, null in case of the error
	 */
	public List<Room> getRoomsPublic(String SID, Long roomtypesId) {
		try {
			log.debug("getRoomsPublic");

			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
	
				List<Room> roomList = roomDao.getPublicRooms(roomtypesId);
	
				// Filter : no appointed meetings
				List<Room> filtered = new ArrayList<Room>();
	
				for (Iterator<Room> iter = roomList.iterator(); iter.hasNext();) {
					Room rooms = iter.next();
	
					if (!rooms.isAppointment()) {
						rooms.setCurrentusers(this.getRoomClientsListByRoomId(rooms
								.getId()));
						filtered.add(rooms);
					}
				}
	
				return filtered;
			}
		} catch (Exception err) {
			log.error("[getRoomsByOrganisationAndType]", err);
		}
		return null;
	}

	public List<Room> getRoomsPublicWithoutType(String SID) {
		try {

			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
	
				List<Room> roomList = roomDao.getPublicRooms();
				
				for (Room room : roomList) {
					room.setCurrentusers(sessionManager.getClientListByRoom(room.getId()));
				}
	
				return roomList;
			}
		} catch (Exception err) {
			log.error("[getRoomsPublicWithoutType]", err);
		}
		return null;
	}

	/**
	 * retrieving ServerTime
	 * 
	 * @return - server time
	 */
	// --------------------------------------------------------------------------------------------
	public Date getServerTime() {
		log.debug("getServerTime");

		return new Date(System.currentTimeMillis());

	}

	// --------------------------------------------------------------------------------------------

	/**
	 * 
	 * retrieving Appointment for Room
	 * 
	 * @param roomId
	 * @return - Appointment in case the room is appointment, null otherwise
	 */
	public Appointment getAppointMentDataForRoom(Long roomId) {
		log.debug("getAppointMentDataForRoom");

		Room room = roomDao.get(roomId);

		if (room.isAppointment() == false)
			return null;

		try {
			Appointment ment = appointmentDao.getAppointmentByRoom(roomId);

			return ment;
		} catch (Exception e) {
			log.error("getAppointMentDataForRoom " + e.getMessage());
			return null;
		}

	}

	// --------------------------------------------------------------------------------------------

	/**
	 * 
	 */
	// --------------------------------------------------------------------------------------------
	public List<Room> getAppointedMeetings(String SID, Long roomTypeId) {
		log.debug("ConferenceService.getAppointedMeetings");

		Long userId = sessiondataDao.checkSession(SID);

		if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {

			List<Appointment> points = appointmentDao.getTodaysAppointmentsbyRangeAndMember(userId);
			List<Room> result = new ArrayList<Room>();

			if (points != null) {
				for (int i = 0; i < points.size(); i++) {
					Appointment ment = points.get(i);

					Long roomId = ment.getRoom().getId();
					Room room = roomDao.get(roomId);

					if (!room.getRoomtype().getId()
							.equals(roomTypeId))
						continue;

					room.setCurrentusers(getRoomClientsListByRoomId(room
							.getId()));
					result.add(room);
				}
			}

			log.debug("Found " + result.size() + " rooms");
			return result;

		} else {
			return null;
		}

	}

	// --------------------------------------------------------------------------------------------

	public List<Room> getAppointedMeetingRoomsWithoutType(String SID) {
		log.debug("ConferenceService.getAppointedMeetings");
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				List<Appointment> appointments = appointmentDao.getTodaysAppointmentsbyRangeAndMember(userId);
				List<Room> result = new ArrayList<Room>();

				if (appointments != null) {
					for (int i = 0; i < appointments.size(); i++) {
						Appointment ment = appointments.get(i);

						Long rooms_id = ment.getRoom().getId();
						Room rooom = roomDao.get(rooms_id);

						rooom.setCurrentusers(this
								.getRoomClientsListByRoomId(rooom.getId()));
						result.add(rooom);
					}
				}

				log.debug("Found " + result.size() + " rooms");
				return result;
			}
		} catch (Exception err) {
			log.error("[getAppointedMeetingRoomsWithoutType]", err);
		}
		return null;
	}

	public Room getRoomWithCurrentUsersById(String SID, long roomId) {
		Room room = null;
		Long userId = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
			room = roomDao.get(roomId);
			room.setCurrentusers(sessionManager.getClientListByRoom(room.getId()));
		}
		return room;
	}

	/**
	 * gets a list of all available rooms
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return - list of rooms being searched
	 */
	public SearchResult<Room> getRooms(String SID, int start, int max,
			String orderby, boolean asc, String search) {
		log.debug("getRooms");

		Long userId = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(userId))) {
			return roomManager.getRooms(start, max, orderby, asc, search);
		}
		return null;
	}

	public SearchResult<Room> getRoomsWithCurrentUsers(String SID, int start,
			int max, String orderby, boolean asc) {
		log.debug("getRooms");

		Long userId = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(userId))) {
			return roomManager.getRoomsWithCurrentUsers(start, max, orderby, asc);
		}
		return null;
	}

	public List<RoomModerator> getRoomModeratorsByRoomId(String SID,
			Long roomId) {
		try {

			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {

				return roomModeratorsDao.getByRoomId(roomId);

			}

		} catch (Exception err) {
			log.error("[getRoomModeratorsByRoomId]", err);
		}

		return null;
	}

	/**
	 * return all participants of a room
	 * 
	 * @param roomId
	 * @return - true if room is full, false otherwise
	 */
	public boolean isRoomFull(Long roomId) {
		try {
			Room room = roomDao.get(roomId);
			
			if (room.getNumberOfPartizipants() <= this.sessionManager
					.getClientListByRoom(roomId).size()) {
				return true;
			}
			
			return false;
		} catch (Exception err) {
			log.error("[isRoomFull]", err);
		}
		return true;
	}

	/**
	 * return all participants of a room
	 * 
	 * @param roomId
	 * @return - all participants of a room
	 */
	public List<Client> getRoomClientsListByRoomId(Long roomId) {
		return sessionManager.getClientListByRoom(roomId);
	}

	public List<Room> getRoomsWithCurrentUsersByList(String SID, int start, int max, String orderby, boolean asc) {
		log.debug("getRoomsWithCurrentUsersByList");

		Long userId = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(userId))) {
			return roomManager.getRoomsWithCurrentUsersByList(start, max, orderby, asc);
		}
		return null;
	}

	public List<Room> getRoomsWithCurrentUsersByListAndType(String SID, int start, int max, String orderby, boolean asc, String externalRoomType) {
		log.debug("getRoomsWithCurrentUsersByListAndType");

		Long userId = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(userId))) {
			return roomManager.getRoomsWithCurrentUsersByListAndType(start, max, orderby, asc, externalRoomType);
		}
		return null;
	}

	public Room getRoomByOwnerAndType(String SID, Long roomtypesId, String roomName) {
		Long userId = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
			return roomDao.getUserRoom(userId, roomtypesId, roomName);
		}
		return null;
	}
}
