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
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomModeratorDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.util.AuthLevelUtil;
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
	private SessiondataDao sessionDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RoomModeratorDao roomModeratorDao;
	@Autowired
	private ISessionManager sessionManager = null;

	public List<RoomGroup> getRoomsByGroupWithoutType(String sid, long groupId) {
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
				log.debug("getRoomsByGroupWithoutType");
				List<RoomGroup> roomGroupsList = roomManager.getRoomGroupByGroupId(groupId);
				
				for (RoomGroup roomGroup : roomGroupsList) {
					roomGroup.getRoom().setCurrentusers(sessionManager.getClientListByRoom(roomGroup.getRoom().getId()));
				}
	
				return roomGroupsList;
			}
		} catch (Exception err) {
			log.error("[getRoomsByGroupWithoutType]", err);
		}
		return null;
	}

	/**
	 * get a List of all public availible rooms (non-appointments)
	 * 
	 * @param sid
	 * @param typeId
	 * @return - public rooms with given type, null in case of the error
	 */
	public List<Room> getRoomsPublic(String sid, Long typeId) {
		try {
			log.debug("getRoomsPublic");

			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
	
				List<Room> roomList = roomDao.getPublicRooms(Room.Type.get(typeId));
	
				// Filter : no appointed meetings
				List<Room> filtered = new ArrayList<Room>();
	
				for (Iterator<Room> iter = roomList.iterator(); iter.hasNext();) {
					Room rooms = iter.next();
	
					if (!rooms.isAppointment()) {
						rooms.setCurrentusers(getRoomClientsListByRoomId(rooms.getId()));
						filtered.add(rooms);
					}
				}
	
				return filtered;
			}
		} catch (Exception err) {
			log.error("[getRoomsPublic]", err);
		}
		return null;
	}

	public List<Room> getRoomsPublicWithoutType(String sid) {
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
	
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

	public List<Room> getAppointedMeetingRoomsWithoutType(String sid) {
		log.debug("ConferenceService.getAppointedMeetings");
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
				List<Appointment> appointments = appointmentDao.getForToday(sd.getUserId());
				List<Room> result = new ArrayList<Room>();

				if (appointments != null) {
					for (int i = 0; i < appointments.size(); i++) {
						Appointment ment = appointments.get(i);

						Long roomId = ment.getRoom().getId();
						Room rooom = roomDao.get(roomId);

						rooom.setCurrentusers(getRoomClientsListByRoomId(rooom.getId()));
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

	public Room getRoomWithCurrentUsersById(String sid, long roomId) {
		Room room = null;
		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
			room = roomDao.get(roomId);
			room.setCurrentusers(sessionManager.getClientListByRoom(room.getId()));
		}
		return room;
	}

	/**
	 * gets a list of all available rooms
	 * 
	 * @param sid
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return - list of rooms being searched
	 */
	public SearchResult<Room> getRooms(String sid, int start, int max, String orderby, boolean asc, String search) {
		log.debug("getRooms");

		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(sd.getUserId()))) {
			return roomManager.getRooms(start, max, orderby, asc, search);
		}
		return null;
	}

	public SearchResult<Room> getRoomsWithCurrentUsers(String sid, int start, int max, String orderby, boolean asc) {
		log.debug("getRooms");

		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(sd.getUserId()))) {
			return roomManager.getRoomsWithCurrentUsers(start, max, orderby, asc);
		}
		return null;
	}

	public List<RoomModerator> getRoomModeratorsByRoomId(String sid, Long roomId) {
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
				return roomModeratorDao.getByRoomId(roomId);
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
	 * @return - all participants of a room
	 */
	public List<Client> getRoomClientsListByRoomId(Long roomId) {
		return sessionManager.getClientListByRoom(roomId);
	}

	public List<Room> getRoomsWithCurrentUsersByList(String sid, int start, int max, String orderby, boolean asc) {
		log.debug("getRoomsWithCurrentUsersByList");

		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(sd.getUserId()))) {
			return roomManager.getRoomsWithCurrentUsersByList(start, max, orderby, asc);
		}
		return null;
	}

	public List<Room> getRoomsWithCurrentUsersByListAndType(String sid, int start, int max, String orderby, boolean asc, String externalType) {
		log.debug("getRoomsWithCurrentUsersByListAndType");

		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(sd.getUserId()))) {
			return roomManager.getRoomsWithCurrentUsersByListAndType(start, max, orderby, asc, externalType);
		}
		return null;
	}

	public Room getRoomByOwnerAndType(String sid, Long typeId, String roomName) {
		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
			return roomDao.getUserRoom(sd.getUserId(), Room.Type.get(typeId), roomName);
		}
		return null;
	}
}
