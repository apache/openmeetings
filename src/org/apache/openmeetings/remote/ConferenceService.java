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
package org.apache.openmeetings.remote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.cluster.beans.ServerDTO;
import org.apache.openmeetings.data.basic.AuthLevelmanagement;
import org.apache.openmeetings.data.basic.Sessionmanagement;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.calendar.management.AppointmentLogic;
import org.apache.openmeetings.data.conference.Roommanagement;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.conference.dao.RoomModeratorsDao;
import org.apache.openmeetings.data.user.Usermanagement;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.persistence.beans.room.RoomModerator;
import org.apache.openmeetings.persistence.beans.room.RoomType;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.room.RoomOrganisation;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.apache.openmeetings.utils.math.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author sebawagner
 * 
 */
public class ConferenceService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ConferenceService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RoomModeratorsDao roomModeratorsDao;
	@Autowired
	private AuthLevelmanagement authLevelManagement;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private ISessionManager sessionManager = null;
	@Autowired
	private ServerDao serverDao;

	/**
	 * ( get a List of all available Rooms of this organization
	 * (non-appointments)
	 * 
	 * @param SID
	 * @param organisation_id
	 * @return - all available Rooms of this organization
	 */
	public List<RoomOrganisation> getRoomsByOrganisationAndType(String SID,
			long organisation_id, long roomtypes_id) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			log.debug("getRoomsByOrganisationAndType");

			if (user_level == null) {
				return null;
			}
			List<RoomOrganisation> roomOrgsList = roommanagement
					.getRoomsOrganisationByOrganisationIdAndRoomType(
							user_level, organisation_id, roomtypes_id);

			List<RoomOrganisation> filtered = new ArrayList<RoomOrganisation>();

			for (Iterator<RoomOrganisation> iter = roomOrgsList.iterator(); iter
					.hasNext();) {
				RoomOrganisation orgRoom = iter.next();

				if (!orgRoom.getRoom().getAppointment()) {
					orgRoom.getRoom().setCurrentusers(
							this.getRoomClientsListByRoomId(orgRoom.getRoom()
									.getRooms_id()));
					filtered.add(orgRoom);
				}
			}
			return filtered;
		} catch (Exception err) {
			log.error("[getRoomsByOrganisationAndType]", err);
		}
		return null;
	}

	public List<RoomOrganisation> getRoomsByOrganisationWithoutType(
			String SID, long organisation_id) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			log.debug("getRoomsByOrganisationAndType");

			if (user_level == null) {
				return null;
			}
			List<RoomOrganisation> roomOrgsList = roommanagement
					.getRoomsOrganisationByOrganisationId(user_level,
							organisation_id);
			
			for (RoomOrganisation roomOrg : roomOrgsList) {
				roomOrg.getRoom().setCurrentusers(sessionManager.getClientListByRoom(roomOrg.getRoom().getRooms_id()));
			}

			return roomOrgsList;
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
	 * @param organisation_id
	 * @return - all rooms of an organization
	 */
	public SearchResult<RoomOrganisation> getRoomsByOrganisation(String SID,
			long organisation_id, int start, int max, String orderby,
			boolean asc) {

		log.debug("getRoomsByOrganisation");

		Long user_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByIdAndOrg(user_id,
				organisation_id);

		return roommanagement.getRoomsOrganisationByOrganisationId(user_level,
				organisation_id, start, max, orderby, asc);
	}

	/**
	 * get a List of all public availible rooms (non-appointments)
	 * 
	 * @param SID
	 * @param roomtypes_id
	 * @return - public rooms with given type, null in case of the error
	 */
	public List<Room> getRoomsPublic(String SID, Long roomtypes_id) {
		try {
			log.debug("getRoomsPublic");

			Long users_id = sessionManagement.checkSession(SID);
			Long User_level = userManagement.getUserLevelByID(users_id);
			log.error("getRoomsPublic user_level: " + User_level);

			List<Room> roomList = roommanagement.getPublicRooms(User_level,
					roomtypes_id);

			// Filter : no appointed meetings
			List<Room> filtered = new ArrayList<Room>();

			for (Iterator<Room> iter = roomList.iterator(); iter.hasNext();) {
				Room rooms = iter.next();

				if (!rooms.getAppointment()) {
					rooms.setCurrentusers(this.getRoomClientsListByRoomId(rooms
							.getRooms_id()));
					filtered.add(rooms);
				}
			}

			return filtered;
		} catch (Exception err) {
			log.error("[getRoomsByOrganisationAndType]", err);
		}
		return null;
	}

	public List<Room> getRoomsPublicWithoutType(String SID) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			log.debug("getRoomsPublic user_level: " + user_level);

			List<Room> roomList = roomDao.getPublicRooms();
			
			for (Room room : roomList) {
				room.setCurrentusers(sessionManager.getClientListByRoom(room.getRooms_id()));
			}

			return roomList;
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
	 * @param room_id
	 * @return - Appointment in case the room is appointment, null otherwise
	 */
	public Appointment getAppointMentDataForRoom(Long room_id) {
		log.debug("getAppointMentDataForRoom");

		Room room = roomDao.get(room_id);

		if (room.getAppointment() == false)
			return null;

		try {
			Appointment ment = appointmentLogic.getAppointmentByRoom(room_id);

			return ment;
		} catch (Exception e) {
			log.error("getAppointMentDataForRoom " + e.getMessage());
			return null;
		}

	}

	// --------------------------------------------------------------------------------------------

	public Map<String, Object> getAppointMentAndTimeZones(Long room_id) {
		try {
			log.debug("getAppointMentDataForRoom");
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
	
			log.debug("getCurrentRoomClient -2- " + streamid);
	
			Client currentClient = this.sessionManager
					.getClientByStreamId(streamid, null);
	
			Room room = roomDao.get(room_id);
	
			if (room.getAppointment() == false) {
				throw new IllegalStateException("Room has no appointment");
			}
		
			Appointment appointment = appointmentLogic
					.getAppointmentByRoom(room_id);

			Map<String, Object> returnMap = new HashMap<String, Object>();

			returnMap.put("appointment", appointment);

			User us = userManagement.getUserById(currentClient.getUser_id());
			TimeZone timezone = timezoneUtil.getTimezoneByUser(us);

			returnMap.put("appointment", appointment);

			returnMap.put(
					"start",
					CalendarPatterns.getDateWithTimeByMiliSeconds(
							appointment.getAppointmentStarttime(), timezone));
			returnMap.put(
					"end",
					CalendarPatterns.getDateWithTimeByMiliSeconds(
							appointment.getAppointmentEndtime(), timezone));
			returnMap.put("timeZone", timezone.getDisplayName());

			return returnMap;
		} catch (Exception e) {
			log.error("getAppointMentAndTimeZones " , e );
			return null;
		}

	}

	/**
	 * 
	 */
	// --------------------------------------------------------------------------------------------
	public List<Room> getAppointedMeetings(String SID, Long room_types_id) {
		log.debug("ConferenceService.getAppointedMeetings");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);

		if (authLevelManagement.checkUserLevel(user_level)) {

			List<Appointment> points = appointmentLogic
					.getTodaysAppointmentsForUser(users_id);
			List<Room> result = new ArrayList<Room>();

			if (points != null) {
				for (int i = 0; i < points.size(); i++) {
					Appointment ment = points.get(i);

					Long rooms_id = ment.getRoom().getRooms_id();
					Room rooom = roomDao.get(rooms_id);

					if (!rooom.getRoomtype().getRoomtypes_id()
							.equals(room_types_id))
						continue;

					rooom.setCurrentusers(getRoomClientsListByRoomId(rooom
							.getRooms_id()));
					result.add(rooom);
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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {
				List<Appointment> appointments = appointmentLogic
						.getTodaysAppointmentsForUser(users_id);
				List<Room> result = new ArrayList<Room>();

				if (appointments != null) {
					for (int i = 0; i < appointments.size(); i++) {
						Appointment ment = appointments.get(i);

						Long rooms_id = ment.getRoom().getRooms_id();
						Room rooom = roomDao.get(rooms_id);

						rooom.setCurrentusers(this
								.getRoomClientsListByRoomId(rooom.getRooms_id()));
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

	/**
	 * 
	 * @param SID
	 * @return - all room types available
	 */
	public List<RoomType> getRoomTypes(String SID) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkUserLevel(user_level)) {
			return roommanagement.getAllRoomTypes();
		}
		return null;
	}

	/**
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return - room with the id given
	 */
	public Room getRoomById(String SID, long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomById(user_level, rooms_id);
	}

	public Room getRoomWithCurrentUsersById(String SID, long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		Room room = roommanagement.getRoomById(user_level, rooms_id);
		room.setCurrentusers(sessionManager.getClientListByRoom(room.getRooms_id()));
		return room;
	}

	/**
	 * 
	 * @param SID
	 * @param externalUserId
	 * @param externalUserType
	 * @param roomtypes_id
	 * @return - room with the given external id
	 */
	public Room getRoomByExternalId(String SID, Long externalUserId,
			String externalUserType, long roomtypes_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomByExternalId(user_level, externalUserId,
				externalUserType, roomtypes_id);
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

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRooms(user_level, start, max, orderby, asc,
				search);
	}

	public SearchResult<Room> getRoomsWithCurrentUsers(String SID, int start,
			int max, String orderby, boolean asc) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomsWithCurrentUsers(user_level, start, max,
				orderby, asc);
	}

	/**
	 * get all Organisations of a room
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return - all Organisations of a room
	 */
	public List<RoomOrganisation> getOrganisationByRoom(String SID,
			long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getOrganisationsByRoom(user_level, rooms_id);
	}

	/**
	 * 
	 * @param SID
	 * @param argObject
	 * @return - id of the room being saved, null in case of the error
	 */
	public Long saveOrUpdateRoom(String SID, Object argObject) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			long User_level = userManagement.getUserLevelByID(users_id);
			log.debug("argObject: 1 - " + argObject.getClass().getName());
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> argObjectMap = (LinkedHashMap<String, Object>) argObject;
			log.debug("argObject: 2 - "
					+ argObjectMap.get("organisations").getClass().getName());
			@SuppressWarnings("unchecked")
			List<Integer> organisations = (List<Integer>) argObjectMap
					.get("organisations");
			Long rooms_id = Long.valueOf(
					argObjectMap.get("rooms_id").toString()).longValue();
			log.debug("rooms_id " + rooms_id);

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> roomModerators = (List<Map<String, Object>>) argObjectMap
					.get("roomModerators");

			Integer demoTime = null;
			if (argObjectMap.get("demoTime").toString() != null
					&& argObjectMap.get("demoTime").toString().length() > 0) {
				demoTime = Integer.valueOf(
						argObjectMap.get("demoTime").toString()).intValue();
			}

			long roomId = -1;
			if (rooms_id == 0) {
				roomId = roommanagement.addRoom(
						User_level
						, argObjectMap.get("name").toString()
						, Long.valueOf(argObjectMap.get("roomtypes_id").toString()).longValue()
						, argObjectMap.get("comment").toString()
						, Long.valueOf(argObjectMap.get("numberOfPartizipants").toString()).longValue()
						, Boolean.valueOf(argObjectMap.get("ispublic").toString())
						, organisations
						, Boolean.valueOf(argObjectMap.get("appointment").toString())
						, Boolean.valueOf(argObjectMap.get("isDemoRoom").toString())
						, demoTime
						, Boolean.valueOf(argObjectMap.get("isModeratedRoom").toString())
						, roomModerators
						, Boolean.valueOf(argObjectMap.get("allowUserQuestions").toString())
						, Boolean.valueOf(argObjectMap.get("isAudioOnly").toString())
						, Boolean.valueOf(argObjectMap.get("allowFontStyles").toString())
						, Boolean.valueOf(argObjectMap.get("isClosed").toString())
						, argObjectMap.get("redirectURL").toString()
						, argObjectMap.get("conferencePin").toString()
						, Long.valueOf(argObjectMap.get("ownerId").toString()).longValue()
						, Boolean.valueOf(argObjectMap.get("waitForRecording").toString())
						, Boolean.valueOf(argObjectMap.get("allowRecording").toString())
						, Boolean.valueOf(argObjectMap.get("hideTopBar").toString())
						, Boolean.valueOf(argObjectMap.get("hideChat").toString())
						, Boolean.valueOf(argObjectMap.get("hideActivitiesAndActions").toString())
						, Boolean.valueOf(argObjectMap.get("hideFilesExplorer").toString())
						, Boolean.valueOf(argObjectMap.get("hideActionsMenu").toString())
						, Boolean.valueOf(argObjectMap.get("hideScreenSharing").toString())
						, Boolean.valueOf(argObjectMap.get("hideWhiteboard").toString())
						, Boolean.valueOf(argObjectMap.get("showMicrophoneStatus").toString())
						, Boolean.valueOf(argObjectMap.get("chatModerated").toString())
						, Boolean.valueOf(argObjectMap.get("chatOpened").toString())
						, Boolean.valueOf(argObjectMap.get("filesOpened").toString())
						, Boolean.valueOf(argObjectMap.get("autoVideoSelect").toString())
						, Boolean.valueOf(argObjectMap.get("sipEnabled").toString())
						);
			} else if (rooms_id > 0) {
				roomId = roommanagement.updateRoom(
						User_level
						, rooms_id
						, Long.valueOf(argObjectMap.get("roomtypes_id").toString()).longValue()
						, argObjectMap.get("name").toString()
						, Boolean.valueOf(argObjectMap.get("ispublic").toString())
						, argObjectMap.get("comment").toString()
						, Long.valueOf(argObjectMap.get("numberOfPartizipants").toString()).longValue()
						, organisations
						, Boolean.valueOf(argObjectMap.get("appointment").toString())
						, Boolean.valueOf(argObjectMap.get("isDemoRoom").toString())
						, demoTime
						, Boolean.valueOf(argObjectMap.get("isModeratedRoom").toString())
						, roomModerators
						, Boolean.valueOf(argObjectMap.get("allowUserQuestions").toString())
						, Boolean.valueOf(argObjectMap.get("isAudioOnly").toString())
						, Boolean.valueOf(argObjectMap.get("allowFontStyles").toString())
						, Boolean.valueOf(argObjectMap.get("isClosed").toString())
						, argObjectMap.get("redirectURL").toString()
						, argObjectMap.get("conferencePin").toString()
						, Long.valueOf(argObjectMap.get("ownerId").toString()).longValue()
						, Boolean.valueOf(argObjectMap.get("waitForRecording").toString())
						, Boolean.valueOf(argObjectMap.get("allowRecording").toString())
						, Boolean.valueOf(argObjectMap.get("hideTopBar").toString())
						, Boolean.valueOf(argObjectMap.get("hideChat").toString())
						, Boolean.valueOf(argObjectMap.get("hideActivitiesAndActions").toString())
						, Boolean.valueOf(argObjectMap.get("hideFilesExplorer").toString())
						, Boolean.valueOf(argObjectMap.get("hideActionsMenu").toString())
						, Boolean.valueOf(argObjectMap.get("hideScreenSharing").toString())
						, Boolean.valueOf(argObjectMap.get("hideWhiteboard").toString())
						, Boolean.valueOf(argObjectMap.get("showMicrophoneStatus").toString())
						, Boolean.valueOf(argObjectMap.get("chatModerated").toString())
						, Boolean.valueOf(argObjectMap.get("chatOpened").toString())
						, Boolean.valueOf(argObjectMap.get("filesOpened").toString())
						, Boolean.valueOf(argObjectMap.get("autoVideoSelect").toString())
						, Boolean.valueOf(argObjectMap.get("sipEnabled").toString())
						);
			}
			
			return roomId;

		} catch (Exception e) {
			log.error("saveOrUpdateRoom", e);
		}
		return null;
	}

	public List<RoomModerator> getRoomModeratorsByRoomId(String SID,
			Long roomId) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {

				return roomModeratorsDao.getRoomModeratorByRoomId(roomId);

			}

		} catch (Exception err) {
			log.error("[getRoomModeratorsByRoomId]", err);
			err.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return - id of the room being deleted
	 */
	public Long deleteRoom(String SID, long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.deleteRoomById(user_level, rooms_id);
	}
	
	/**
	 * return all participants of a room
	 * 
	 * @param room_id
	 * @return - true if room is full, false otherwise
	 */
	public boolean isRoomFull(Long room_id) {
		try {
			Room room = roomDao.get(room_id);
			
			if (room.getNumberOfPartizipants() <= this.sessionManager
					.getClientListByRoom(room_id).size()) {
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
	 * @param room_id
	 * @return - all participants of a room
	 */
	public List<Client> getRoomClientsListByRoomId(Long room_id) {
		return sessionManager.getClientListByRoom(room_id);
	}

	/**
	 * invoked in the admin interface to show the connections currently open
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return - list of the connections currently open
	 */
	public SearchResult<Client> getRoomClientsMap(String SID, int start, int max,
			String orderby, boolean asc) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkAdminLevel(user_level)) {
				return this.sessionManager.getListByStartAndMax(start, max,
						orderby, asc);
			}
		} catch (Exception err) {
			log.error("[getRoomClientsMap]", err);
		}
		return null;
	}
	
	/**
	 * Get some statistics about the current sessions handled by this instance
	 * 
	 * @param SID
	 * @return - session statistics as String
	 */
	public String getSessionStatistics(String SID) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkAdminLevel(user_level)) {
				return this.sessionManager.getSessionStatistics();
			}
		} catch (Exception err) {
			log.error("[getRoomClientsMap]", err);
		}
		return null;
	}

	public List<Room> getRoomsWithCurrentUsersByList(String SID, int start,
			int max, String orderby, boolean asc) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomsWithCurrentUsersByList(user_level, start,
				max, orderby, asc);
	}

	public List<Room> getRoomsWithCurrentUsersByListAndType(String SID,
			int start, int max, String orderby, boolean asc,
			String externalRoomType) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomsWithCurrentUsersByListAndType(user_level,
				start, max, orderby, asc, externalRoomType);
	}

	public Room getRoomByOwnerAndType(String SID, Long roomtypesId,
			String roomName) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkUserLevel(user_level)) {
			return roommanagement.getRoomByOwnerAndTypeId(users_id,
					roomtypesId, roomName);
		}
		return null;
	}

	/**
	 * Gives a {@link Server} entity, in case there is a cluster configured
	 * 
	 * @param SID
	 * @param roomId
	 * @return null means the user should stay on the master, otherwise a
	 *         {@link Server} entity is returned
	 */
	public ServerDTO getServerForSession(String SID, long roomId) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkUserLevel(user_level)) {
			List<Server> serverList = serverDao.getActiveServers();
			
			//if there is no cluster set up, just redirect to the current one
			if (serverList.size() == 0) {
				return null;
			}
			
			//if the room is already opened on a server, redirect the user to that one,
			//we do that in two loop's because there is no query involved here,
			//only the first user that enters the conference room needs to be adjusted 
			//to that server that has the less maxUser count in its rooms currently.
			//But if the room is already opened, then the maxUser is no more relevant,
			//the user will be just redirected to the same server
			
			//check if the user is on master hosted, (serverId == null)
			for (Long activeRoomId : sessionManager.getActiveRoomIdsByServer(null)) {
				if (activeRoomId != null && activeRoomId.equals(roomId)) {
					return null;
				}
			}
			
			for (Server server : serverList) {
				for (Long activeRoomId : sessionManager.getActiveRoomIdsByServer(server)) {
					if (activeRoomId.equals(roomId)) {
						return new ServerDTO(server);
					}
				}
			}
			
			//the room is not opened on any server yet, its the first user, get the maxUser 
			//per room
			
			//TODO / FIXME: Get room's maxUser in a single query instead a query for each room
			final Map<Server,List<Room>> serverRoomMap = new HashMap<Server,List<Room>>();
			
			//Locally handled sessions are serverId = null
			List<Room> localRoomList = new ArrayList<Room>();
			for (Long activeRoomId : sessionManager.getActiveRoomIdsByServer(null)) {
				//FIXME / TODO: This is the single query to get the room by its id
				localRoomList.add(roomDao.get(activeRoomId));
			}
			serverRoomMap.put(null, localRoomList);
			
			//Slave/Server rooms
			for (Server server : serverList) {
				List<Room> roomList = new ArrayList<Room>();
				for (Long activeRoomId : sessionManager.getActiveRoomIdsByServer(server)) {
					//FIXME / TODO: This is the single query to get the room by its id
					roomList.add(roomDao.get(activeRoomId));
				}
				serverRoomMap.put(server, roomList);
			}
			
			//calc server with lowest max users
			List<Server> list = new LinkedList<Server>();
			list.addAll(serverRoomMap.keySet());
			Collections.sort(list, new Comparator<Server>() {
		          public int compare(Server s1, Server s2) {
		        	  int maxUsersInRoomS1 = 0;
		        	  log.debug("serverRoomMap.get(s1) SIZE "+serverRoomMap.get(s1).size());
		        	  for (Room room : serverRoomMap.get(s1)) {
		        		  log.debug("s1 room: "+room);
		        		  maxUsersInRoomS1 += room.getNumberOfPartizipants();
		        	  }
		        	  int maxUsersInRoomS2 = 0;
		        	  log.debug("serverRoomMap.get(s2) SIZE "+serverRoomMap.get(s2).size());
		        	  for (Room room : serverRoomMap.get(s2)) {
		        		  log.debug("s2 room: "+room);
		        		  maxUsersInRoomS2 += room.getNumberOfPartizipants();
		        	  }
		        	  
		              return maxUsersInRoomS1 - maxUsersInRoomS2;
		          }
		     });
			
			LinkedHashMap<Server, List<Room>> serverRoomMapOrdered = new LinkedHashMap<Server, List<Room>>();
			for (Server server : list) {
				serverRoomMapOrdered.put(server, serverRoomMap.get(server));
			}

			if (log.isDebugEnabled()) {
				log.debug("Resulting order: ");
				for (Entry<Server, List<Room>> entry : serverRoomMapOrdered
						.entrySet()) {
					int maxUsersInRoom = 0;
					for (Room room : entry.getValue()) {
						maxUsersInRoom += room.getNumberOfPartizipants();
					}
					
					String roomids = "";
					for (Room r : entry.getValue()) { 
						roomids += " " + r.getRooms_id(); 
					}
					
					log.debug("entry " + entry.getKey() + " Number of rooms "
							+ entry.getValue().size()+ " RoomIds: " + roomids + " max(Sum): " + maxUsersInRoom);
				}

			}

			log.debug("Resulting Server");
			
			Server s = serverRoomMapOrdered.entrySet().iterator().next().getKey();
			
			if (s == null) {
				return null;
			}
			
			//Somehow this object here cannot be serialized cause its abused by OpenJPA
			//so get a fresh copy from the entity manager and return that
			return new ServerDTO(s);
		}

		log.error("Could not get server for cluster session");
		//Empty server object
		return null;
	}
	
}
