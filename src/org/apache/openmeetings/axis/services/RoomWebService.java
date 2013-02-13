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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.calendar.management.MeetingMemberLogic;
import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.room.RoomType;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.remote.ConferenceService;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RoomService contains methods to manipulate rooms and create invitation hash
 * 
 * @author sebawagner
 * @webservice RoomService
 * 
 */
public class RoomWebService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			RoomWebService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private FlvRecordingDao flvRecordingDao;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private ConferenceService conferenceService;
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private MeetingMemberLogic meetingMemberLogic;
	@Autowired
	private RoomDao roomDao;

	/**
	 * Returns an Object of Type RoomsList which contains a list of
	 * Room-Objects. Every Room-Object contains a Roomtype and all informations
	 * about that Room. The List of current-users in the room is Null if you get
	 * them via SOAP. The Roomtype can be 1 for conference rooms or 2 for
	 * audience rooms.
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomtypes_id
	 * @return - list of public rooms
	 * @throws AxisFault
	 */
	public Room[] getRoomsPublic(String SID, Long roomtypes_id)
			throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long User_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(User_level)) {

				List<Room> roomList = roomManager.getPublicRooms(
						User_level, roomtypes_id);
				// We need to re-marshal the Rooms object cause Axis2 cannot use
				// our objects
				if (roomList != null && roomList.size() != 0) {
					// roomsListObject.setRoomList(roomList);
					Room[] roomItems = new Room[roomList.size()];
					int count = 0;
					for (Iterator<Room> it = roomList.iterator(); it.hasNext();) {
						Room room = it.next();
						room.setCurrentusers(null);
						roomItems[count] = room;
						count++;
					}

					return roomItems;
				}
				log.debug("roomList SIZE: " + roomList.size());

			}
			return null;
		} catch (Exception err) {
			log.error("[getRoomsPublic] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Deletes a flv recording
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param flvRecordingId
	 *            the id of the recording
	 *            
	 * @return - true if recording was deleted
	 * @throws AxisFault
	 */
	public boolean deleteFlvRecording(String SID, Long flvRecordingId)
			throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return flvRecordingDao.deleteFlvRecording(flvRecordingId);
			}

		} catch (Exception err) {
			log.error("[deleteFlvRecording] ", err);
			throw new AxisFault(err.getMessage());
		}

		return false;
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalUserId
	 *            the externalUserId
	 *            
	 * @return - list of flv recordings
	 * @throws AxisFault
	 */
	public FLVRecordingReturn[] getFlvRecordingByExternalUserId(String SID,
			String externalUserId) throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				List<FlvRecording> recordingList = flvRecordingDao
						.getFlvRecordingByExternalUserId(externalUserId);

				// We need to re-marshal the Rooms object cause Axis2 cannot use
				// our objects
				if (recordingList != null && recordingList.size() != 0) {
					// roomsListObject.setRoomList(roomList);
					FLVRecordingReturn[] recordingListItems = new FLVRecordingReturn[recordingList
							.size()];
					int count = 0;
					for (Iterator<FlvRecording> it = recordingList.iterator(); it
							.hasNext();) {
						FlvRecording flvRecording = it.next();
						recordingListItems[count] = FLVRecordingReturn
								.initObject(flvRecording);
						count++;
					}

					return recordingListItems;
				}

				return null;
			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomType] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalRoomType
	 *            externalRoomType specified when creating the room
	 * @param insertedBy
	 *            the userId that created the recording
	 * @return - list of flv recordings
	 * @throws AxisFault
	 */
	public FLVRecordingReturn[] getFlvRecordingByExternalRoomTypeAndCreator(
			String SID, String externalRoomType, Long insertedBy)
			throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				List<FlvRecording> recordingList = flvRecordingDao
						.getFlvRecordingByExternalRoomTypeAndCreator(
								externalRoomType, insertedBy);

				// We need to re-marshal the Rooms object cause Axis2 cannot use
				// our objects
				if (recordingList != null && recordingList.size() != 0) {
					// roomsListObject.setRoomList(roomList);
					FLVRecordingReturn[] recordingListItems = new FLVRecordingReturn[recordingList
							.size()];
					int count = 0;
					for (Iterator<FlvRecording> it = recordingList.iterator(); it
							.hasNext();) {
						FlvRecording flvRecording = it.next();
						recordingListItems[count] = FLVRecordingReturn
								.initObject(flvRecording);
						count++;
					}

					return recordingListItems;
				}

				return null;
			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomType] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalRoomType
	 *            externalRoomType specified when creating the room
	 * @return - list of flv recordings
	 * @throws AxisFault
	 */
	public List<FlvRecording> getFlvRecordingByExternalRoomTypeByList(
			String SID, String externalRoomType) throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return flvRecordingDao
						.getFlvRecordingByExternalRoomType(externalRoomType);

			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomType] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param externalRoomType
	 *            externalRoomType specified when creating the room
	 * @return - list of flv recordings
	 * @throws AxisFault
	 */
	public FlvRecording[] getFlvRecordingByExternalRoomType(String SID,
			String externalRoomType) throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				List<FlvRecording> recordingList = flvRecordingDao
						.getFlvRecordingByExternalRoomType(externalRoomType);

				// We need to re-marshal the Rooms object cause Axis2 cannot use
				// our objects
				if (recordingList != null && recordingList.size() != 0) {
					// roomsListObject.setRoomList(roomList);
					FlvRecording[] recordingListItems = new FlvRecording[recordingList
							.size()];
					int count = 0;
					for (Iterator<FlvRecording> it = recordingList.iterator(); it
							.hasNext();) {
						FlvRecording flvRecording = it.next();
						recordingListItems[count] = flvRecording;
						count++;
					}

					return recordingListItems;
				}

				return null;
			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomType] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Get list of recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            the room id
	 * @return - list of recordings
	 * @throws AxisFault
	 */
	public FlvRecording[] getFlvRecordingByRoomId(String SID, Long roomId)
			throws AxisFault {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				List<FlvRecording> recordingList = flvRecordingDao
						.getFlvRecordingByRoomId(roomId);

				// We need to re-marshal the Rooms object cause Axis2 cannot use
				// our objects
				if (recordingList != null && recordingList.size() != 0) {
					// roomsListObject.setRoomList(roomList);
					FlvRecording[] recordingListItems = new FlvRecording[recordingList
							.size()];
					int count = 0;
					for (Iterator<FlvRecording> it = recordingList.iterator(); it
							.hasNext();) {
						FlvRecording flvRecording = it.next();
						recordingListItems[count] = flvRecording;
						count++;
					}

					return recordingListItems;
				}

				return null;
			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomType] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * List of available room types
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @return - List of available room types
	 * @throws AxisFault
	 */
	public RoomType[] getRoomTypes(String SID) throws AxisFault {
		try {
			List<RoomType> rommTypesList = conferenceService.getRoomTypes(SID);
			RoomType[] roomTypesArray = new RoomType[rommTypesList.size()];

			int count = 0;
			for (Iterator<RoomType> it = rommTypesList.iterator(); it
					.hasNext();) {
				RoomType roomType = it.next();
				roomTypesArray[count] = roomType;
				count++;
			}

			return roomTypesArray;

		} catch (Exception err) {
			log.error("[getRoomTypes]", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Returns current users for rooms ids
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId1
	 * @param roomId2
	 * @param roomId3
	 * @param roomId4
	 * @param roomId5
	 * @param roomId6
	 * @param roomId7
	 * @param roomId8
	 * @param roomId9
	 * @param roomId10
	 * @return - current users for rooms ids
	 * @throws AxisFault
	 */
	public RoomCountBean[] getRoomCounters(String SID, Integer roomId1,
			Integer roomId2, Integer roomId3, Integer roomId4, Integer roomId5,
			Integer roomId6, Integer roomId7, Integer roomId8, Integer roomId9,
			Integer roomId10) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				LinkedList<Integer> roomIds = new LinkedList<Integer>();

				if (roomId1 != null && roomId1 > 0) {
					roomIds.push(roomId1);
				}
				if (roomId2 != null && roomId2 > 0) {
					log.debug("roomId2 :: " + roomId2);
					roomIds.push(roomId2);
				}
				if (roomId3 != null && roomId3 > 0) {
					roomIds.push(roomId3);
				}
				if (roomId4 != null && roomId4 > 0) {
					roomIds.push(roomId4);
				}
				if (roomId5 != null && roomId5 > 0) {
					roomIds.push(roomId5);
				}
				if (roomId6 != null && roomId6 > 0) {
					roomIds.push(roomId6);
				}
				if (roomId7 != null && roomId7 > 0) {
					roomIds.push(roomId7);
				}
				if (roomId8 != null && roomId8 > 0) {
					roomIds.push(roomId8);
				}
				if (roomId9 != null && roomId9 > 0) {
					roomIds.push(roomId9);
				}
				if (roomId10 != null && roomId10 > 0) {
					roomIds.push(roomId10);
				}

				List<Room> rooms = roomManager.getRoomsByIds(roomIds);

				RoomCountBean[] roomsArray = new RoomCountBean[rooms.size()];

				int i = 0;
				for (Room room : rooms) {
					RoomCountBean rCountBean = new RoomCountBean();
					rCountBean.setRoomId(room.getRooms_id());
					rCountBean.setRoomName(room.getName());
					rCountBean.setMaxUser(room.getNumberOfPartizipants()
							.intValue());
					rCountBean.setRoomCount(sessionManager
							.getClientListByRoom(room.getRooms_id()).size());

					roomsArray[i] = rCountBean;
					i++;
				}

				return roomsArray;
			}

		} catch (Exception err) {
			log.error("[getRoomTypes]", err);
			throw new AxisFault(err.getMessage());
		}
		return null;
	}

	/**
	 * returns a conference room object
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param rooms_id
	 *            the room id
	 * @return - room with the id given
	 */
	public Room getRoomById(String SID, long rooms_id) {
		return conferenceService.getRoomById(SID, rooms_id);
	}

	/**
	 * @deprecated this function is intend to be deleted
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param rooms_id
	 * @return - room with the id given
	 */
	@Deprecated
	public Room getRoomWithCurrentUsersById(String SID, long rooms_id) {
		return conferenceService.getRoomWithCurrentUsersById(SID, rooms_id);
	}

	/**
	 * Returns a object of type RoomReturn
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param rooms_id
	 * @return - object of type RoomReturn
	 * @throws AxisFault
	 */
	public RoomReturn getRoomWithClientObjectsById(String SID, long rooms_id)
			throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				Room room = roomManager.getRoomById(user_level, rooms_id);

				RoomReturn roomReturn = new RoomReturn();

				roomReturn.setCreated(room.getStarttime());
				roomReturn.setCreator(null);
				roomReturn.setName(room.getName());
				roomReturn.setRoom_id(room.getRooms_id());

				List<Client> map = sessionManager
						.getClientListByRoom(room.getRooms_id());

				RoomUser[] roomUsers = new RoomUser[map.size()];

				int i = 0;
				for (Client rcl : map) {
					RoomUser roomUser = new RoomUser();
					roomUser.setFirstname(rcl.getFirstname());
					roomUser.setLastname(rcl.getLastname());
					roomUser.setBroadcastId(rcl.getBroadCastID());
					roomUser.setPublicSID(rcl.getPublicSID());
					roomUser.setIsBroadCasting(rcl.getIsBroadcasting());
					roomUser.setAvsettings(rcl.getAvsettings());

					roomUsers[i++] = roomUser;
				}

				roomReturn.setRoomUser(roomUsers);

				return roomReturn;
			}

			return null;

		} catch (Exception err) {
			log.error("[getRoomWithClientObjectsById]", err);
			throw new AxisFault(err.getMessage());
		}

	}

	/**
	 * Returns a List of Objects of Rooms You can use "name" as value for
	 * orderby or rooms_id
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param start
	 *            The id you want to start
	 * @param max
	 *            The maximum you want to get
	 * @param orderby
	 *            The column it will be ordered
	 * @param asc
	 *            Asc or Desc sort ordering
	 *            
	 * @return - List of Objects of Rooms
	 */
	public SearchResult<Room> getRooms(String SID, int start, int max,
			String orderby, boolean asc) {
		return conferenceService.getRooms(SID, start, max, orderby, asc, "");
	}

	/**
	 * Returns a List of Objects of Rooms You can use "name" as value for
	 * orderby or rooms_id. It also fills the attribute currentUsers in the
	 * Room-Object
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param start
	 *            The id you want to start
	 * @param max
	 *            The maximum you want to get
	 * @param orderby
	 *            The column it will be ordered
	 * @param asc
	 *            Asc or Desc sort ordering
	 *            
	 * @return - List of Objects of Rooms
	 */
	public SearchResult<Room> getRoomsWithCurrentUsers(String SID, int start,
			int max, String orderby, boolean asc) {
		return conferenceService.getRoomsWithCurrentUsers(SID, start, max,
				orderby, asc);
	}

	// TODO: Add functions to get Users of a Room

	/**
	 * TODO: Fix Organization Issue
	 * 
	 * deprecated use addRoomWithModeration instead
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 * @param roomtypes_id
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param videoPodWidth
	 * @param videoPodHeight
	 * @param videoPodXPosition
	 * @param videoPodYPosition
	 * @param moderationPanelXPosition
	 * @param showWhiteBoard
	 * @param whiteBoardPanelXPosition
	 * @param whiteBoardPanelYPosition
	 * @param whiteBoardPanelHeight
	 * @param whiteBoardPanelWidth
	 * @param showFilesPanel
	 * @param filesPanelXPosition
	 * @param filesPanelYPosition
	 * @param filesPanelHeight
	 * @param filesPanelWidth
	 * @return - id of the room or error code
	 */
	@Deprecated
	public Long addRoom(String SID, String name, Long roomtypes_id,
			String comment, Long numberOfPartizipants, Boolean ispublic,
			Integer videoPodWidth, Integer videoPodHeight,
			Integer videoPodXPosition, Integer videoPodYPosition,
			Integer moderationPanelXPosition, Boolean showWhiteBoard,
			Integer whiteBoardPanelXPosition, Integer whiteBoardPanelYPosition,
			Integer whiteBoardPanelHeight, Integer whiteBoardPanelWidth,
			Boolean showFilesPanel, Integer filesPanelXPosition,
			Integer filesPanelYPosition, Integer filesPanelHeight,
			Integer filesPanelWidth) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.addRoom(3L, name, roomtypes_id, comment,
						numberOfPartizipants, ispublic, null, false, false,
						null, false, null, true, false, false, true, "", "",
						null, null, null, false, // hideTopBar
						false, // hideChat
						false, // hideActivitiesAndActions
						false, // hideFilesExplorer
						false, // hideActionsMenu
						false, // hideScreenSharing
						false, // hideWhiteboard
						false, // showMicrophoneStatus
						false, // chatModerated
						false, // chatOpened
						false, // filesOpened
						false, // autoVideoSelect
						false //sipEnabled
						);
			}
		} catch (Exception err) {
			log.error("[addRoom] ", err);
		}
		return new Long(-1);
	}

	/**
	 * Create a conference room
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            Name of the Room
	 * @param roomtypes_id
	 *            Type of that room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            any comment
	 * @param numberOfPartizipants
	 *            the maximum users allowed in this room
	 * @param ispublic
	 *            If this room is public (use true if you don't deal with
	 *            different Organizations)
	 * @param appointment
	 *            is it a Calendar Room (use false by default)
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time (use false by default)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait untill a Moderator arrives. Use the
	 *            becomeModerator param in setUserObjectAndGenerateRoomHash to
	 *            set a user as default Moderator
	 *            
	 * @return - id of the room or error code
	 */
	public Long addRoomWithModeration(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.addRoom(3L, name, roomtypes_id, comment,
						numberOfPartizipants, ispublic, null, appointment,
						isDemoRoom, demoTime, isModeratedRoom, null, true,
						false, false, true, "", "", null, null, null, false, // hideTopBar
						false, // hideChat
						false, // hideActivitiesAndActions
						false, // hideFilesExplorer
						false, // hideActionsMenu
						false, // hideScreenSharing
						false, // hideWhiteboard
						false, // showMicrophoneStatus
						false, // chatModerated
						false, // chatOpened
						false, // filesOpened
						false, // autoVideoSelect
						false //sipEnabled
						);
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
		}
		return new Long(-1);
	}

	/**
	 * this SOAP Method has an additional param to enable or disable the buttons
	 * to apply for moderation this does only work in combination with the
	 * room-type restricted
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            Name of the Room
	 * @param roomtypes_id
	 *            Type of that room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            any comment
	 * @param numberOfPartizipants
	 *            the maximum users allowed in this room
	 * @param ispublic
	 *            If this room is public (use true if you don't deal with
	 *            different Organizations)
	 * @param appointment
	 *            is it a Calendar Room (use false by default)
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time (use false by default)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait untill a Moderator arrives. Use the
	 *            becomeModerator param in setUserObjectAndGenerateRoomHash to
	 *            set a user as default Moderator
	 * @param allowUserQuestions
	 *            enable or disable the button to allow users to apply for
	 *            moderation
	 *            
	 * @return - id of the room or error code
	 */
	public Long addRoomWithModerationAndQuestions(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.addRoom(3L, name, roomtypes_id, comment,
						numberOfPartizipants, ispublic, null, appointment,
						isDemoRoom, demoTime, isModeratedRoom, null,
						allowUserQuestions, false, true, false, "", "", null,
						null, null, false, // hideTopBar
						false, // hideChat
						false, // hideActivitiesAndActions
						false, // hideFilesExplorer
						false, // hideActionsMenu
						false, // hideScreenSharing
						false, // hideWhiteboard
						false, // showMicrophoneStatus
						false, // chatModerated
						false, // chatOpened
						false, // filesOpened
						false, // autoVideoSelect
						false //sipEnabled
						);
			}
		} catch (Exception err) {
			log.error("[addRoomWithModerationAndQuestions] ", err);
		}
		return new Long(-1);
	}

	/**
	 * 
	 * adds a new room with options for user questions and audio only
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            Name of the Room
	 * @param roomtypes_id
	 *            Type of that room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            any comment
	 * @param numberOfPartizipants
	 *            the maximum users allowed in this room
	 * @param ispublic
	 *            If this room is public (use true if you don't deal with
	 *            different Organizations)
	 * @param appointment
	 *            is it a Calendar Room (use false by default)
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time (use false by default)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator param in setUserObjectAndGenerateRoomHash to
	 *            set a user as default Moderator
	 * @param allowUserQuestions
	 *            enable or disable the button to allow users to apply for
	 *            moderation
	 * @param isAudioOnly
	 *            enable or disable the video / or audio-only
	 * 
	 * @return - id of the room or error code
	 * @throws AxisFault
	 */
	public Long addRoomWithModerationQuestionsAndAudioType(String SID,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions, Boolean isAudioOnly) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.addRoom(3L, name, roomtypes_id, comment,
						numberOfPartizipants, ispublic, null, appointment,
						isDemoRoom, demoTime, isModeratedRoom, null,
						allowUserQuestions, isAudioOnly, true, false, "", "",
						null, null, null, false, // hideTopBar
						false, // hideChat
						false, // hideActivitiesAndActions
						false, // hideFilesExplorer
						false, // hideActionsMenu
						false, // hideScreenSharing
						false, // hideWhiteboard
						false, // showMicrophoneStatus
						false, // chatModerated
						false, // chatOpened
						false, // filesOpened
						false, // autoVideoSelect
						false //sipEnabled
						);
			}
			return -1L;
		} catch (Exception err) {
			log.error("[addRoomWithModerationQuestionsAndAudioType] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * 
	 * adds a new room with options for user questions, audio only and hide
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            Name of the Room
	 * @param roomtypes_id
	 *            Type of that room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            any comment
	 * @param numberOfPartizipants
	 *            the maximum users allowed in this room
	 * @param ispublic
	 *            If this room is public (use true if you don't deal with
	 *            different Organizations)
	 * @param appointment
	 *            is it a Calendar Room (use false by default)
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time (use false by default)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator param in setUserObjectAndGenerateRoomHash to
	 *            set a user as default Moderator
	 * @param allowUserQuestions
	 *            enable or disable the button to allow users to apply for
	 *            moderation
	 * @param isAudioOnly
	 *            enable or disable the video / or audio-only
	 * @param hideTopBar
	 *            hide or show TopBar
	 * @param hideChat
	 *            hide or show Chat
	 * @param hideActivitiesAndActions
	 *            hide or show Activities And Actions
	 * @param hideFilesExplorer
	 *            hide or show Files Explorer
	 * @param hideActionsMenu
	 *            hide or show Actions Menu
	 * @param hideScreenSharing
	 *            hide or show Screen Sharing
	 * @param hideWhiteboard
	 *            hide or show Whiteboard. If whitboard is hidden, video pods
	 *            and scrollbar appear instead.
	 *            
	 * @return - id of the room or error code
	 * @throws AxisFault
	 */
	public Long addRoomWithModerationQuestionsAudioTypeAndHideOptions(
			String SID, String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions, Boolean isAudioOnly,
			Boolean hideTopBar, Boolean hideChat,
			Boolean hideActivitiesAndActions, Boolean hideFilesExplorer,
			Boolean hideActionsMenu, Boolean hideScreenSharing,
			Boolean hideWhiteboard) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.addRoom(3L, name, roomtypes_id, comment,
						numberOfPartizipants, ispublic, null, appointment,
						isDemoRoom, demoTime, isModeratedRoom, null,
						allowUserQuestions, isAudioOnly, true, false, "", "",
						null, null, null, hideTopBar, hideChat,
						hideActivitiesAndActions, hideFilesExplorer,
						hideActionsMenu, hideScreenSharing, hideWhiteboard,
						false, false, 
						false, // chatOpened
						false, // filesOpened
						false, // autoVideoSelect
						false //sipEnabled
						);
			}
			return -1L;
		} catch (Exception err) {
			log.error(
					"[addRoomWithModerationQuestionsAudioTypeAndHideOptions] ",
					err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Checks if a room with this exteralRoomId + externalRoomType does exist,
	 * if yes it returns the room id if not, it will create the room and then
	 * return the room id of the newly created room
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            Name of the room
	 * @param roomtypes_id
	 *            Type of that room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            any comment
	 * @param numberOfPartizipants
	 *            the maximum users allowed in this room
	 * @param ispublic
	 *            If this room is public (use true if you don't deal with
	 *            different Organizations)
	 * @param appointment
	 *            is it a Calendar Room? (use false if not sure what that means)
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait untill a Moderator arrives. Use the
	 *            becomeModerator param in setUserObjectAndGenerateRoomHash to
	 *            set a user as default Moderator
	 * @param externalRoomId
	 *            your external room id may set here
	 * @param externalRoomType
	 *            you can specify your system-name or type of room here, for
	 *            example "moodle"
	 *            
	 * @return - id of the room or error code
	 * @throws AxisFault
	 */
	public Long getRoomIdByExternalId(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, Long externalRoomId,
			String externalRoomType) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				Room room = conferenceService.getRoomByExternalId(SID,
						externalRoomId, externalRoomType, roomtypes_id);
				Long roomId = null;
				if (room == null) {
					roomId = roomManager.addExternalRoom(name, roomtypes_id,
							comment, numberOfPartizipants, ispublic, null,
							appointment, isDemoRoom, demoTime, isModeratedRoom,
							null, externalRoomId, externalRoomType, true,
							false, true, false, "", false, true, false);
				} else {
					roomId = room.getRooms_id();
				}
				return roomId;
			}

			return -26L;
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
			throw new AxisFault(err.getMessage());
		}
		// return new Long (-1);
	}

	/**
	 * TODO: Fix Organization Issue deprecated use updateRoomWithModeration
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param rooms_id
	 * @param name
	 * @param roomtypes_id
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param videoPodWidth
	 * @param videoPodHeight
	 * @param videoPodXPosition
	 * @param videoPodYPosition
	 * @param moderationPanelXPosition
	 * @param showWhiteBoard
	 * @param whiteBoardPanelXPosition
	 * @param whiteBoardPanelYPosition
	 * @param whiteBoardPanelHeight
	 * @param whiteBoardPanelWidth
	 * @param showFilesPanel
	 * @param filesPanelXPosition
	 * @param filesPanelYPosition
	 * @param filesPanelHeight
	 * @param filesPanelWidth
	 * 
	 * @return - id of the room updated or error code
	 */
	@Deprecated
	public Long updateRoom(String SID, Long rooms_id, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Integer videoPodWidth, Integer videoPodHeight,
			Integer videoPodXPosition, Integer videoPodYPosition,
			Integer moderationPanelXPosition, Boolean showWhiteBoard,
			Integer whiteBoardPanelXPosition, Integer whiteBoardPanelYPosition,
			Integer whiteBoardPanelHeight, Integer whiteBoardPanelWidth,
			Boolean showFilesPanel, Integer filesPanelXPosition,
			Integer filesPanelYPosition, Integer filesPanelHeight,
			Integer filesPanelWidth, Boolean appointment) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.updateRoomInternal(rooms_id,
						roomtypes_id, name, ispublic, comment,
						numberOfPartizipants, null, appointment, false, null,
						false, null, true, false, true, false, "", "", null,
						null, null, false, // hideTopBar
						false, // hideChat
						false, // hideActivitiesAndActions
						false, // hideFilesExplorer
						false, // hideActionsMenu
						false, // hideScreenSharing
						false, // hideWhiteboard
						false, // showMicrophoneStatus
						false, // chatModerated
						false, // chatOpened
						false, // filesOpened
						false, // autoVideoSelect
						false //sipEnabled
						);
			}
		} catch (Exception err) {
			log.error("[addRoom] ", err);
		}
		return new Long(-1);
	}

	/**
	 * Updates a conference room by its room id
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param room_id
	 *            the room id to update
	 * @param name
	 *            new name of the room
	 * @param roomtypes_id
	 *            new type of room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            new comment
	 * @param numberOfPartizipants
	 *            new numberOfParticipants
	 * @param ispublic
	 *            is public
	 * @param appointment
	 *            if the room is an appointment
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator parameter in setUserObjectAndGenerateRoomHash
	 *            to set a user as default Moderator
	 *            
	 * @return - id of the room updated or error code
	 */
	public Long updateRoomWithModeration(String SID, Long room_id, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.updateRoomInternal(room_id, roomtypes_id,
						name, ispublic, comment, numberOfPartizipants, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, true, false, true, false, "", "", null, null, null,
						false, // hideTopBar
						false, // hideChat
						false, // hideActivitiesAndActions
						false, // hideFilesExplorer
						false, // hideActionsMenu
						false, // hideScreenSharing
						false, // hideWhiteboard
						false, // showMicrophoneStatus
						false, // chatModerated
						false, // chatOpened
						false, // filesOpened
						false, // autoVideoSelect
						false //sipEnabled
						);
			}
		} catch (Exception err) {
			log.error("[updateRoomWithModeration] ", err);
		}
		return new Long(-1);
	}

	/**
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param room_id
	 *            the room id to update
	 * @param name
	 *            new name of the room
	 * @param roomtypes_id
	 *            new type of room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            new comment
	 * @param numberOfPartizipants
	 *            new numberOfParticipants
	 * @param ispublic
	 *            is public
	 * @param appointment
	 *            if the room is an appointment
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator parameter in setUserObjectAndGenerateRoomHash
	 *            to set a user as default Moderator
	 * @param allowUserQuestions
	 *            enable or disable the button to allow users to apply for
	 *            moderation
	 *            
	 * @return - id of the room updated or error code
	 */
	public Long updateRoomWithModerationAndQuestions(String SID, Long room_id,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.updateRoomInternal(room_id, roomtypes_id,
						name, ispublic, comment, numberOfPartizipants, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, allowUserQuestions, false, true, false, "", "",
						null, null, null, false, // hideTopBar
						false, // hideChat
						false, // hideActivitiesAndActions
						false, // hideFilesExplorer
						false, // hideActionsMenu
						false, // hideScreenSharing
						false, // hideWhiteboard
						false, // showMicrophoneStatus
						false, // chatModerated
						false, // chatOpened
						false, // filesOpened
						false, // autoVideoSelect
						false //sipEnabled
						);
			}
		} catch (Exception err) {
			log.error("[updateRoomWithModerationAndQuestions] ", err);
		}
		return new Long(-1);
	}

	/**
	 * update room with options for user questions, audio only and hide
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param room_id
	 *            the room id to update
	 * @param name
	 *            new name of the room
	 * @param roomtypes_id
	 *            new type of room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            new comment
	 * @param numberOfPartizipants
	 *            number of participants
	 * @param ispublic
	 *            is public
	 * @param appointment
	 *            if the room is an appointment (use false if not sure what that
	 *            means)
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator parameter in setUserObjectAndGenerateRoomHash
	 *            to set a user as default Moderator
	 * @param allowUserQuestions
	 *            enable or disable the button to allow users to apply for
	 *            moderation
	 * @param isAudioOnly
	 *            enable or disable the video / or audio-only
	 * @param hideTopBar
	 *            hide or show TopBar
	 * @param hideChat
	 *            hide or show Chat
	 * @param hideActivitiesAndActions
	 *            hide or show Activities And Actions
	 * @param hideFilesExplorer
	 *            hide or show Files Explorer
	 * @param hideActionsMenu
	 *            hide or show Actions Menu
	 * @param hideScreenSharing
	 *            hide or show Screen Sharing
	 * @param hideWhiteboard
	 *            hide or show Whiteboard. If whitboard is hidden, video pods
	 *            and scrollbar appear instead.
	 *            
	 * @return - id of the room updated or error code
	 */
	public Long updateRoomWithModerationQuestionsAudioTypeAndHideOptions(
			String SID, Long room_id, String name, Long roomtypes_id,
			String comment, Long numberOfPartizipants, Boolean ispublic,
			Boolean appointment, Boolean isDemoRoom, Integer demoTime,
			Boolean isModeratedRoom, Boolean allowUserQuestions,
			Boolean isAudioOnly, Boolean hideTopBar, Boolean hideChat,
			Boolean hideActivitiesAndActions, Boolean hideFilesExplorer,
			Boolean hideActionsMenu, Boolean hideScreenSharing,
			Boolean hideWhiteboard) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.updateRoomInternal(room_id, roomtypes_id,
						name, ispublic, comment, numberOfPartizipants, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, allowUserQuestions, true, true, false, "", "",
						null, null, null, hideTopBar, hideChat,
						hideActivitiesAndActions, hideFilesExplorer,
						hideActionsMenu, hideScreenSharing, hideWhiteboard,
						false, // showMicrophoneStatus
						false, // chatModerated
						false, // chatOpened
						false, // filesOpened
						false, // autoVideoSelect
						false //sipEnabled
						);
			}
		} catch (Exception err) {
			log.error("[updateRoomWithModerationAndQuestions] ", err);
		}
		return new Long(-1);
	}

	/**
	 * Delete a room by its room id
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param rooms_id
	 * 
	 * @return - id of the room deleted
	 */
	public Long deleteRoom(String SID, long rooms_id) {
		return conferenceService.deleteRoom(SID, rooms_id);
	}

	/**
	 * kick all uses of a certain room
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 *            _Admin
	 * @param room_id
	 *            the room id
	 *            
	 * @return - true if user was kicked, false otherwise
	 */
	public Boolean kickUser(String SID_Admin, Long room_id) {
		try {
			Boolean salida = false;

			salida = userManager.kickUserByStreamId(SID_Admin, room_id);

			if (salida == null)
				salida = false;

			return salida;
		} catch (Exception err) {
			log.error("[kickUser]", err);
		}
		return null;
	}

	/**
	 * Add a new conference room with option to set the external room type, the
	 * external room type should be set if multiple applications use the same
	 * OpenMeetings instance
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            new name of the room
	 * @param roomtypes_id
	 *            new type of room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            new comment
	 * @param numberOfPartizipants
	 *            new numberOfParticipants
	 * @param ispublic
	 *            is public
	 * @param appointment
	 *            if the room is an appointment
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator parameter in setUserObjectAndGenerateRoomHash
	 *            to set a user as default Moderator
	 * @param externalRoomType
	 *            the external room type (can be used to identify different
	 *            external systems using same OpenMeetings instance)
	 *            
	 * @return - id of the room added or error code
	 */
	public Long addRoomWithModerationAndExternalType(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, String externalRoomType) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.addExternalRoom(name, roomtypes_id,
						comment, numberOfPartizipants, ispublic, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, null, externalRoomType, true, false, true, false, "",
						false, true, false);
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
		}
		return new Long(-1);
	}

	/**
	 * Adds a new room with options for audio only
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            new name of the room
	 * @param roomtypes_id
	 *            new type of room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            new comment
	 * @param numberOfPartizipants
	 *            new numberOfParticipants
	 * @param ispublic
	 *            is public
	 * @param appointment
	 *            if the room is an appointment
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator parameter in setUserObjectAndGenerateRoomHash
	 *            to set a user as default Moderator
	 * @param externalRoomType
	 *            the external room type (can be used to identify different
	 *            external systems using same OpenMeetings instance)
	 * @param allowUserQuestions
	 *            enable or disable the button to allow users to apply for
	 *            moderation
	 * @param isAudioOnly
	 *            enable or disable the video / or audio-only
	 *            
	 * @return - id of the room added or error code
	 */
	public Long addRoomWithModerationExternalTypeAndAudioType(String SID,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, Boolean allowUserQuestions,
			Boolean isAudioOnly) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.addExternalRoom(name, roomtypes_id,
						comment, numberOfPartizipants, ispublic, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, null, externalRoomType, allowUserQuestions,
						isAudioOnly, true, false, "", false, true, false);
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
		}
		return new Long(-1);
	}

	/**
	 * Adds a new room with options for recording
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            new name of the room
	 * @param roomtypes_id
	 *            new type of room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            new comment
	 * @param numberOfPartizipants
	 *            new numberOfParticipants
	 * @param ispublic
	 *            is public
	 * @param appointment
	 *            if the room is an appointment
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator parameter in setUserObjectAndGenerateRoomHash
	 *            to set a user as default Moderator
	 * @param externalRoomType
	 *            the external room type (can be used to identify different
	 *            external systems using same OpenMeetings instance)
	 * @param allowUserQuestions
	 *            enable or disable the button to allow users to apply for
	 *            moderation
	 * @param isAudioOnly
	 *            enable or disable the video / or audio-only
	 * @param waitForRecording
	 *            if the users in the room will get a notification that they
	 *            should start recording before they do a conference
	 * @param allowRecording
	 *            if the recording option is available or not
	 *            
	 * @return - id of the room added or error code
	 */
	public Long addRoomWithModerationAndRecordingFlags(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, String externalRoomType,
			Boolean allowUserQuestions, Boolean isAudioOnly,
			Boolean waitForRecording, Boolean allowRecording) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.addExternalRoom(name, roomtypes_id,
						comment, numberOfPartizipants, ispublic, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, null, externalRoomType, allowUserQuestions,
						isAudioOnly, true, false, "", waitForRecording,
						allowRecording, false);
			} else {
				return -26L;
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
		}
		return new Long(-1);
	}

	/**
	 * Add a conference room with options to disable the top menu bar in the
	 * conference room
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            new name of the room
	 * @param roomtypes_id
	 *            new type of room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            new comment
	 * @param numberOfPartizipants
	 *            new numberOfParticipants
	 * @param ispublic
	 *            is public
	 * @param appointment
	 *            if the room is an appointment
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator parameter in setUserObjectAndGenerateRoomHash
	 *            to set a user as default Moderator
	 * @param externalRoomType
	 *            the external room type (can be used to identify different
	 *            external systems using same OpenMeetings instance)
	 * @param allowUserQuestions
	 *            enable or disable the button to allow users to apply for
	 *            moderation
	 * @param isAudioOnly
	 *            enable or disable the video / or audio-only
	 * @param waitForRecording
	 *            if the users in the room will get a notification that they
	 *            should start recording before they do a conference
	 * @param allowRecording
	 *            if the recording option is available or not
	 * @param hideTopBar
	 *            if the top bar in the conference room is visible or not
	 *            
	 * @return - id of the room added or error code
	 */
	public Long addRoomWithModerationExternalTypeAndTopBarOption(String SID,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, Boolean allowUserQuestions,
			Boolean isAudioOnly, Boolean waitForRecording,
			Boolean allowRecording, Boolean hideTopBar) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return roomManager.addExternalRoom(name, roomtypes_id,
						comment, numberOfPartizipants, ispublic, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, null, externalRoomType, allowUserQuestions,
						isAudioOnly, true, false, "", waitForRecording,
						allowRecording, hideTopBar);
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
		}
		return new Long(-1);
	}

	/**
	 * 
	 * Create a Invitation hash and optionally send it by mail the From to Date
	 * is as String as some SOAP libraries do not accept Date Objects in SOAP
	 * Calls Date is parsed as dd.mm.yyyy, time as hh:mm (don't forget the
	 * leading zero's)
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin a
	 *            valid Session Token
	 * @param username
	 *            the username of the User that he will get
	 * @param room_id
	 *            the conference room id of the invitation
	 * @param isPasswordProtected
	 *            if the invitation is password protected
	 * @param invitationpass
	 *            the password for accessing the conference room via the
	 *            invitation hash
	 * @param valid
	 *            the type of validation for the hash 1: endless, 2: from-to
	 *            period, 3: one-time
	 * @param validFromDate
	 *            Date in Format of dd.mm.yyyy only of interest if valid is type
	 *            2
	 * @param validFromTime
	 *            time in Format of hh:mm only of interest if valid is type 2
	 * @param validToDate
	 *            Date in Format of dd.mm.yyyy only of interest if valid is type
	 *            2
	 * @param validToTime
	 *            time in Format of hh:mm only of interest if valid is type 2
	 * @return a HASH value that can be made into a URL with
	 *         http://$OPENMEETINGS_HOST
	 *         :$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws AxisFault
	 */
	public String getInvitationHash(String SID, String username, Long room_id,
			Boolean isPasswordProtected, String invitationpass, Integer valid,
			String validFromDate, String validFromTime, String validToDate,
			String validToTime) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				Date dFrom = null;
				Date dTo = null;

				if (valid == 2) {
					Integer validFromHour = Integer.valueOf(
							validFromTime.substring(0, 2)).intValue();
					Integer validFromMinute = Integer.valueOf(
							validFromTime.substring(3, 5)).intValue();

					Integer validToHour = Integer.valueOf(
							validToTime.substring(0, 2)).intValue();
					Integer validToMinute = Integer.valueOf(
							validToTime.substring(3, 5)).intValue();

					log.info("validFromHour: " + validFromHour);
					log.info("validFromMinute: " + validFromMinute);

					Date fromDate = CalendarPatterns.parseDate(validFromDate); // dd.MM.yyyy
					Date toDate = CalendarPatterns.parseDate(validToDate); // dd.MM.yyyy

					Calendar calFrom = Calendar.getInstance();
					calFrom.setTime(fromDate);
					calFrom.set(calFrom.get(Calendar.YEAR),
							calFrom.get(Calendar.MONTH),
							calFrom.get(Calendar.DATE), validFromHour,
							validFromMinute, 0);

					Calendar calTo = Calendar.getInstance();
					calTo.setTime(toDate);
					calTo.set(calTo.get(Calendar.YEAR),
							calTo.get(Calendar.MONTH),
							calTo.get(Calendar.DATE), validToHour,
							validToMinute, 0);

					dFrom = calFrom.getTime();
					dTo = calTo.getTime();

					log.info("validFromDate: "
							+ CalendarPatterns
									.getDateWithTimeByMiliSeconds(dFrom));
					log.info("validToDate: "
							+ CalendarPatterns
									.getDateWithTimeByMiliSeconds(dTo));
				}
				Invitations invitation = invitationManager
						.addInvitationLink(user_level, username, username,
								username, username, username, room_id, "",
								isPasswordProtected, invitationpass, valid,
								dFrom, dTo, users_id, "", 1L, false, dFrom,
								dTo, null, username, userManager.getUserById(users_id).getOmTimeZone());

				if (invitation != null) {

					return invitation.getHash();

				} else {

					return "Sys - Error";

				}

			} else {
				return "Need Admin Privileges to perfom the Action";
			}

		} catch (Exception err) {
			log.error("[sendInvitationHash] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Create a Invitation hash and optionally send it by mail the From to Date
	 * is as String as some SOAP libraries do not accept Date Objects in SOAP
	 * Calls Date is parsed as dd.mm.yyyy, time as hh:mm (don't forget the
	 * leading zero's)
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin a
	 *            valid Session Token
	 * @param username
	 *            the Username of the User that he will get
	 * @param message
	 *            the Message in the Email Body send with the invitation if
	 *            sendMail is true
	 * @param baseurl
	 *            the baseURL for the Infivations link in the Mail Body if
	 *            sendMail is true
	 * @param email
	 *            the Email to send the invitation to if sendMail is true
	 * @param subject
	 *            the subject of the Email send with the invitation if sendMail
	 *            is true
	 * @param room_id
	 *            the conference room id of the invitation
	 * @param conferencedomain
	 *            the domain of the room (keep empty)
	 * @param isPasswordProtected
	 *            if the invitation is password protected
	 * @param invitationpass
	 *            the password for accessing the conference room via the
	 *            invitation hash
	 * @param valid
	 *            the type of validation for the hash 1: endless, 2: from-to
	 *            period, 3: one-time
	 * @param validFromDate
	 *            Date in Format of dd.mm.yyyy only of interest if valid is type
	 *            2
	 * @param validFromTime
	 *            time in Format of hh:mm only of interest if valid is type 2
	 * @param validToDate
	 *            Date in Format of dd.mm.yyyy only of interest if valid is type
	 *            2
	 * @param validToTime
	 *            time in Format of hh:mm only of interest if valid is type 2
	 * @param language_id
	 *            the language id of the EMail that is send with the invitation
	 *            if sendMail is true
	 * @param sendMail
	 *            if sendMail is true then the RPC-Call will send the invitation
	 *            to the email
	 * @return a HASH value that can be made into a URL with
	 *         http://$OPENMEETINGS_HOST
	 *         :$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws AxisFault
	 */
	public String sendInvitationHash(String SID, String username,
			String message, String baseurl, String email, String subject,
			Long room_id, String conferencedomain, Boolean isPasswordProtected,
			String invitationpass, Integer valid, String validFromDate,
			String validFromTime, String validToDate, String validToTime,
			Long language_id, Boolean sendMail) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				Date dFrom = null;
				Date dTo = null;

				if (valid == 2) {
					Integer validFromHour = Integer.valueOf(
							validFromTime.substring(0, 2)).intValue();
					Integer validFromMinute = Integer.valueOf(
							validFromTime.substring(3, 5)).intValue();

					Integer validToHour = Integer.valueOf(
							validToTime.substring(0, 2)).intValue();
					Integer validToMinute = Integer.valueOf(
							validToTime.substring(3, 5)).intValue();

					log.info("validFromHour: " + validFromHour);
					log.info("validFromMinute: " + validFromMinute);

					Date fromDate = CalendarPatterns.parseDate(validFromDate); // dd.MM.yyyy
					Date toDate = CalendarPatterns.parseDate(validToDate); // dd.MM.yyyy

					Calendar calFrom = Calendar.getInstance();
					calFrom.setTime(fromDate);
					calFrom.set(calFrom.get(Calendar.YEAR),
							calFrom.get(Calendar.MONTH),
							calFrom.get(Calendar.DATE), validFromHour,
							validFromMinute, 0);

					Calendar calTo = Calendar.getInstance();
					calTo.setTime(toDate);
					calTo.set(calTo.get(Calendar.YEAR),
							calTo.get(Calendar.MONTH),
							calTo.get(Calendar.DATE), validToHour,
							validToMinute, 0);

					dFrom = calFrom.getTime();
					dTo = calTo.getTime();

					log.info("validFromDate: "
							+ CalendarPatterns
									.getDateWithTimeByMiliSeconds(dFrom));
					log.info("validToDate: "
							+ CalendarPatterns
									.getDateWithTimeByMiliSeconds(dTo));
				}

				Invitations invitation = invitationManager
						.addInvitationLink(user_level, username, message,
								baseurl, email, subject, room_id, "",
								isPasswordProtected, invitationpass, valid,
								dFrom, dTo, users_id, baseurl, language_id,
								sendMail, dFrom, dTo, null, username, userManager.getUserById(users_id).getOmTimeZone());

				if (invitation != null) {

					return invitation.getHash();

				} else {

					return "Sys - Error";

				}

			} else {
				return "Need Admin Privileges to perfom the Action";
			}

		} catch (Exception err) {
			log.error("[sendInvitationHash] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Create a Invitation hash and optionally send it by mail the From to Date
	 * is as String as some SOAP libraries do not accept Date Objects in SOAP
	 * Calls Date is parsed as dd.mm.yyyy, time as hh:mm (don't forget the
	 * leading zero's)
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin a
	 *            valid Session Token
	 * @param username
	 *            the Username of the User that he will get
	 * @param message
	 *            the Message in the Email Body send with the invitation if
	 *            sendMail is true
	 * @param baseurl
	 *            the baseURL for the Infivations link in the Mail Body if
	 *            sendMail is true
	 * @param email
	 *            the Email to send the invitation to if sendMail is true
	 * @param subject
	 *            the subject of the Email send with the invitation if sendMail
	 *            is true
	 * @param room_id
	 *            the conference room id of the invitation
	 * @param conferencedomain
	 *            the domain of the room (keep empty)
	 * @param isPasswordProtected
	 *            if the invitation is password protected
	 * @param invitationpass
	 *            the password for accessing the conference room via the
	 *            invitation hash
	 * @param valid
	 *            the type of validation for the hash 1: endless, 2: from-to
	 *            period, 3: one-time
	 * @param fromDate
	 *            Date as Date Object only of interest if valid is type 2
	 * @param toDate
	 *            Date as Date Object only of interest if valid is type 2
	 * @param language_id
	 *            the language id of the EMail that is send with the invitation
	 *            if sendMail is true
	 * @param sendMail
	 *            if sendMail is true then the RPC-Call will send the invitation
	 *            to the email
	 * @return a HASH value that can be made into a URL with
	 *         http://$OPENMEETINGS_HOST
	 *         :$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws AxisFault
	 */
	public String sendInvitationHashWithDateObject(String SID, String username,
			String message, String baseurl, String email, String subject,
			Long room_id, String conferencedomain, Boolean isPasswordProtected,
			String invitationpass, Integer valid, Date fromDate, Date toDate,
			Long language_id, Boolean sendMail) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				Calendar calFrom = Calendar.getInstance();
				calFrom.setTime(fromDate);

				Calendar calTo = Calendar.getInstance();
				calTo.setTime(toDate);

				Date dFrom = calFrom.getTime();
				Date dTo = calTo.getTime();

				log.info("validFromDate: "
						+ CalendarPatterns.getDateWithTimeByMiliSeconds(dFrom));
				log.info("validToDate: "
						+ CalendarPatterns.getDateWithTimeByMiliSeconds(dTo));

				Invitations invitation = invitationManager
						.addInvitationLink(user_level, username, message,
								baseurl, email, subject, room_id, "",
								isPasswordProtected, invitationpass, valid,
								dFrom, dTo, users_id, baseurl, language_id,
								sendMail, dFrom, dTo, null, username, userManager.getUserById(users_id).getOmTimeZone());

				if (invitation != null) {

					return invitation.getHash();

				} else {

					return "Sys - Error";

				}

			} else {
				return "Need Admin Privileges to perfom the Action";
			}

		} catch (Exception err) {
			log.error("[sendInvitationHash] ", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Return a RoomReturn Object with information of the current users of a
	 * conference room
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param start
	 *            The id you want to start
	 * @param max
	 *            The maximum you want to get
	 * @param orderby
	 *            The column it will be ordered
	 * @param asc
	 *            Asc or Desc sort ordering
	 *            
	 * @return - RoomReturn Objects with information of the current users
	 * @throws AxisFault
	 */
	public List<RoomReturn> getRoomsWithCurrentUsersByList(String SID,
			int start, int max, String orderby, boolean asc) throws AxisFault {
		try {
			List<Room> rooms = conferenceService
					.getRoomsWithCurrentUsersByList(SID, start, max, orderby,
							asc);

			List<RoomReturn> returnObjList = new LinkedList<RoomReturn>();

			for (Room room : rooms) {

				RoomReturn roomReturn = new RoomReturn();

				roomReturn.setRoom_id(room.getRooms_id());
				roomReturn.setName(room.getName());

				roomReturn.setCreator("SOAP");
				roomReturn.setCreated(room.getStarttime());

				RoomUser[] rUser = new RoomUser[room.getCurrentusers().size()];

				int i = 0;
				for (Client rcl : room.getCurrentusers()) {

					RoomUser ru = new RoomUser();
					ru.setFirstname(rcl.getFirstname());
					ru.setLastname(rcl.getLastname());

					rUser[i] = ru;

					i++;
				}

				roomReturn.setRoomUser(rUser);

				returnObjList.add(roomReturn);

			}

			return returnObjList;
		} catch (Exception err) {
			log.error("setUserObjectWithExternalUser", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Return a RoomReturn Object with information of the current users of a
	 * conference room with option to search for special external room types
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param start
	 *            The id you want to start
	 * @param max
	 *            The maximum you want to get
	 * @param orderby
	 *            The column it will be ordered
	 * @param asc
	 *            Asc or Desc sort ordering
	 * @param externalRoomType
	 *            the external room type
	 *            
	 * @return - list of room return objects
	 * @throws AxisFault
	 */
	public List<RoomReturn> getRoomsWithCurrentUsersByListAndType(String SID,
			int start, int max, String orderby, boolean asc,
			String externalRoomType) throws AxisFault {
		try {
			List<Room> rooms = conferenceService
					.getRoomsWithCurrentUsersByListAndType(SID, start, max,
							orderby, asc, externalRoomType);

			List<RoomReturn> returnObjList = new LinkedList<RoomReturn>();

			for (Room room : rooms) {

				RoomReturn roomReturn = new RoomReturn();

				roomReturn.setRoom_id(room.getRooms_id());
				roomReturn.setName(room.getName());

				roomReturn.setCreator("SOAP");
				roomReturn.setCreated(room.getStarttime());

				RoomUser[] rUser = new RoomUser[room.getCurrentusers().size()];

				int i = 0;
				for (Client rcl : room.getCurrentusers()) {

					RoomUser ru = new RoomUser();
					ru.setFirstname(rcl.getFirstname());
					ru.setLastname(rcl.getLastname());

					rUser[i] = ru;

					i++;
				}

				roomReturn.setRoomUser(rUser);

				returnObjList.add(roomReturn);

			}

			return returnObjList;
		} catch (Exception err) {
			log.error("setUserObjectWithExternalUser", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * Adds a conference room that is only available for a period of time
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            new name of the room
	 * @param roomtypes_id
	 *            new type of room (1 = Conference, 2 = Audience, 3 =
	 *            Restricted, 4 = Interview)
	 * @param comment
	 *            new comment
	 * @param numberOfPartizipants
	 *            new numberOfParticipants
	 * @param ispublic
	 *            is public
	 * @param appointment
	 *            if the room is an appointment
	 * @param isDemoRoom
	 *            is it a Demo Room with limited time? (use false if not sure
	 *            what that means)
	 * @param demoTime
	 *            time in seconds after the user will be logged out (only
	 *            enabled if isDemoRoom is true)
	 * @param isModeratedRoom
	 *            Users have to wait until a Moderator arrives. Use the
	 *            becomeModerator parameter in setUserObjectAndGenerateRoomHash
	 *            to set a user as default Moderator
	 * @param externalRoomType
	 *            the external room type (can be used to identify different
	 *            external systems using same OpenMeetings instance)
	 * @param validFromDate
	 *            valid from as Date format: dd.MM.yyyy
	 * @param validFromTime
	 *            valid to as time format: mm:hh
	 * @param validToDate
	 *            valid to Date format: dd.MM.yyyy
	 * @param validToTime
	 *            valid to time format: mm:hh
	 * @param isPasswordProtected
	 *            If the links send via EMail to invited people is password
	 *            protected
	 * @param password
	 *            Password for Invitations send via Mail
	 * @param reminderTypeId
	 *            1=none, 2=simple mail, 3=ICAL
	 * @param redirectURL
	 *            URL Users will be lead to if the Conference Time is elapsed
	 *            
	 * @return - id of the room in case of success, error code otherwise
	 * @throws AxisFault
	 */
	public Long addRoomWithModerationAndExternalTypeAndStartEnd(String SID,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, String validFromDate,
			String validFromTime, String validToDate, String validToTime,
			Boolean isPasswordProtected, String password, Long reminderTypeId,
			String redirectURL) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				Date dFrom = null;
				Date dTo = null;

				Integer validFromHour = Integer.valueOf(
						validFromTime.substring(0, 2)).intValue();
				Integer validFromMinute = Integer.valueOf(
						validFromTime.substring(3, 5)).intValue();

				Integer validToHour = Integer.valueOf(
						validToTime.substring(0, 2)).intValue();
				Integer validToMinute = Integer.valueOf(
						validToTime.substring(3, 5)).intValue();

				log.info("validFromHour: " + validFromHour);
				log.info("validFromMinute: " + validFromMinute);

				Date fromDate = CalendarPatterns
						.parseDateBySeparator(validFromDate); // dd.MM.yyyy
				Date toDate = CalendarPatterns
						.parseDateBySeparator(validToDate); // dd.MM.yyyy

				Calendar calFrom = Calendar.getInstance();
				calFrom.setTime(fromDate);
				calFrom.set(calFrom.get(Calendar.YEAR),
						calFrom.get(Calendar.MONTH),
						calFrom.get(Calendar.DATE), validFromHour,
						validFromMinute, 0);

				Calendar calTo = Calendar.getInstance();
				calTo.setTime(toDate);
				calTo.set(calTo.get(Calendar.YEAR), calTo.get(Calendar.MONTH),
						calTo.get(Calendar.DATE), validToHour, validToMinute, 0);

				dFrom = calFrom.getTime();
				dTo = calTo.getTime();

				log.info("validFromDate: "
						+ CalendarPatterns.getDateWithTimeByMiliSeconds(dFrom));
				log.info("validToDate: "
						+ CalendarPatterns.getDateWithTimeByMiliSeconds(dTo));

				Long rooms_id = roomManager.addExternalRoom(name,
						roomtypes_id, comment, numberOfPartizipants, ispublic,
						null, appointment, isDemoRoom, demoTime,
						isModeratedRoom, null, null, externalRoomType, false, // allowUserQuestions
						false, // isAudioOnly
						true,  // allowFontStyles
						false, // isClosed
						redirectURL, false, true, false);

				if (rooms_id <= 0) {
					return rooms_id;
				}

				User us = userManager.getUserById(users_id);

				appointmentDao.addAppointment("appointmentName", users_id,
						"appointmentLocation", "appointmentDescription", dFrom,
						dTo, // appointmentstart, appointmentend,
						false, false, false, false, // isDaily, isWeekly,
													// isMonthly, isYearly,
						1L, // categoryId
						reminderTypeId, // 1=none, 2=simple mail, 3=ICAL
						roomDao.get(rooms_id), 1L, // language_id
						isPasswordProtected, // isPasswordProtected
						password, // password
						false, us.getOmTimeZone().getJname());

				return rooms_id;

			} else {
				return -2L;
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);

			throw new AxisFault(err.getMessage());
		}
		// return new Long(-1);
		// return numberOfPartizipants;
	}

	/**
	 * Add a meeting member to a certain room. This is the same as adding an
	 * external user to a event in the calendar.
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param room_id
	 *            The Room Id the meeting member is going to be added
	 * @param firstname
	 *            The first name of the meeting member
	 * @param lastname
	 *            The last name of the meeting member
	 * @param email
	 *            The email of the Meeting member
	 * @param baseUrl
	 *            The baseUrl, this is important to send the correct link in the
	 *            invitation to the meeting member
	 * @param language_id
	 *            The ID of the language, for the email that is send to the
	 *            meeting member
	 *            
	 * @return - id of the member in case of success, error code otherwise
	 * @throws AxisFault
	 */
	public Long addMeetingMemberRemindToRoom(String SID, Long room_id,
			String firstname, String lastname, String email, String baseUrl,
			Long language_id) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				Appointment appointment = appointmentDao
						.getAppointmentByRoom(room_id);

				if (appointment == null) {
					return -1L;
				}
				// Not In Remote List available - extern user
				Long memberId = meetingMemberLogic.addMeetingMember(firstname,
						lastname, "0", "0", appointment.getAppointmentId(),
						null, email, null, baseUrl, null, new Boolean(false),
						language_id, false, "", null, null, "");

				return memberId;

			} else {
				return -2L;
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);

			throw new AxisFault(err.getMessage());
		}

	}

	/**
	 * Add a meeting member to a certain room. This is the same as adding an
	 * external user to a event in the calendar. with a certain time zone
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param room_id
	 *            The Room Id the meeting member is going to be added
	 * @param firstname
	 *            The first name of the meeting member
	 * @param lastname
	 *            The last name of the meeting member
	 * @param email
	 *            The email of the Meeting member
	 * @param baseUrl
	 *            The baseUrl, this is important to send the correct link in the
	 *            invitation to the meeting member
	 * @param language_id
	 *            The ID of the language, for the email that is send to the
	 *            meeting member
	 * @param jNameTimeZone
	 *            name of the timezone
	 * @param invitorName
	 *            name of invitation creators
	 *            
	 * @return - id of the member in case of success, error code otherwise
	 * @throws AxisFault
	 */
	public Long addExternalMeetingMemberRemindToRoom(String SID, Long room_id,
			String firstname, String lastname, String email, String baseUrl,
			Long language_id, String jNameTimeZone, String invitorName)
			throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				Appointment appointment = appointmentDao
						.getAppointmentByRoom(room_id);

				if (appointment == null) {
					return -1L;
				}

				// Not In Remote List available - extern user
				Long memberId = meetingMemberLogic.addMeetingMember(firstname,
						lastname, "0", "0", appointment.getAppointmentId(),
						null, email, "", baseUrl, null, new Boolean(false),
						language_id, false, "", null, null, invitorName);

				return memberId;

			} else {
				return -2L;
			}
		} catch (Exception err) {
			log.error("[addExternalMeetingMemberRemindToRoom] ", err);

			throw new AxisFault(err.getMessage());
		}

	}

	/**
	 * Method to remotely close or open rooms. If a room is closed all users
	 * inside the room and all users that try to enter it will be redirected to
	 * the redirectURL that is defined in the Room-Object.
	 * 
	 * Returns positive value if authentication was successful.
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param room_id
	 *            the room id
	 * @param status
	 *            false = close, true = open
	 *            
	 * @return - 1 in case of success, -2 otherwise
	 * @throws AxisFault
	 */
	public int closeRoom(String SID, Long room_id, Boolean status)
			throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			log.debug("closeRoom 1 " + room_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				log.debug("closeRoom 2 " + status);

				roomManager.closeRoom(room_id, status);

				if (status) {
					Map<String, String> message = new HashMap<String, String>();
					message.put("message", "roomClosed");
					scopeApplicationAdapter.sendMessageByRoomAndDomain(room_id,
							message);
				}
				return 1;

			} else {
				return -2;
			}
		} catch (Exception err) {
			log.error("[closeRoom] ", err);

			throw new AxisFault(err.getMessage());
		}

	}

	/**
	 * Method to update arbitrary room parameter.
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @param room_id the room id
	 * @param paramName
	 * @param paramValue
	 * @return 1 in case of success, -2 if permissions are insufficient
	 * @throws AxisFault if any error occurred
	 */
	public int modifyRoomParameter(String SID, Long room_id, String paramName, String paramValue)
			throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				log.debug("closeRoom 1 " + room_id);
				Room r = roomDao.get(room_id);
				PropertyUtils.setSimpleProperty(r, paramName, paramValue);
				roomDao.update(r, users_id);
			} else {
				return -2;
			}
			return 1;
		} catch (Exception err) {
			log.error("[modifyRoomParameter] ", err);

			throw new AxisFault(err.getMessage());
		}
	}
	
}
