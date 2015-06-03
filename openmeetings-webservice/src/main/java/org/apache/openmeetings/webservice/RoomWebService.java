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
package org.apache.openmeetings.webservice;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.core.data.conference.RoomManager;
import org.apache.openmeetings.core.remote.ConferenceService;
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.calendar.AppointmentCategoryDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentReminderTypDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.dao.room.IInvitationManager;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.record.RecordingDTO;
import org.apache.openmeetings.db.dto.room.RoomCountBean;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.room.RoomReturn;
import org.apache.openmeetings.db.dto.room.RoomSearchResult;
import org.apache.openmeetings.db.dto.room.RoomUser;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomType;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RoomService contains methods to manipulate rooms and create invitation hash
 * 
 * @author sebawagner
 * @webservice RoomService
 * 
 */
@WebService
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/room")
public class RoomWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomWebService.class, webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDao;
	@Autowired
	private AppointmentReminderTypDao appointmentReminderTypDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private IUserManager userManager;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private FlvRecordingDao flvRecordingDao;
	@Autowired
	private InvitationDao invitationDao;
	@Autowired
	private IInvitationManager invitationManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private ConferenceService conferenceService;
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RoomTypeDao roomTypeDao;
	@Autowired
	private TimezoneUtil timezoneUtil;

	/**
	 * Returns an Object of Type RoomsList which contains a list of
	 * Room-Objects. Every Room-Object contains a Roomtype and all informations
	 * about that Room. The List of current-users in the room is Null if you get
	 * them via SOAP. The Roomtype can be 1 for conference rooms or 2 for
	 * audience rooms.
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomtypesId
	 * @return - list of public rooms
	 * @throws ServiceException
	 */
	public List<RoomDTO> getRoomsPublic(String SID, Long roomtypesId) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RoomDTO.list(roomDao.getPublicRooms(roomtypesId));
			}
			return null;
		} catch (Exception err) {
			log.error("[getRoomsPublic] ", err);
			throw new ServiceException(err.getMessage());
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
	 * @throws ServiceException
	 */
	public boolean deleteFlvRecording(String SID, Long flvRecordingId)
			throws ServiceException {
		try {

			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return flvRecordingDao.delete(flvRecordingId);
			}

		} catch (Exception err) {
			log.error("[deleteFlvRecording] ", err);
			throw new ServiceException(err.getMessage());
		}

		return false;
	}

	/**
	 * Gets a list of flv recordings
	 * 
	 * @param SID The SID of the User. This SID must be marked as Loggedin
	 * @param externalUserId the externalUserId
	 * @param externalUsertype the externalUserType
	 *            
	 * @return - list of flv recordings
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getFlvRecordingByExternalUserId(String SID,
			String externalUserId, String externalUserType) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(flvRecordingDao.getFlvRecordingByExternalUserId(externalUserId, externalUserType));
			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalUserId] ", err);
			throw new ServiceException(err.getMessage());
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
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getFlvRecordingByExternalRoomTypeAndCreator(
			String SID, String externalRoomType, Long insertedBy)
			throws ServiceException {
		try {

			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(flvRecordingDao.getFlvRecordingByExternalRoomTypeAndCreator(externalRoomType, insertedBy));
			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomTypeAndCreator] ", err);
			throw new ServiceException(err.getMessage());
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
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getFlvRecordingByExternalRoomTypeByList(
			String SID, String externalRoomType) throws ServiceException {
		try {

			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(flvRecordingDao.getFlvRecordingByExternalRoomType(externalRoomType));

			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomTypeByList] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Gets a list of recordings
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as logged-in
	 * @param externalType
	 *            externalType specified when creating room or user
	 * @return - list of flv recordings
	 * @throws AxisFault
	 */
	public List<RecordingDTO> getRecordingsByExternalType(String SID, String externalType) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(flvRecordingDao.getRecordingsByExternalType(externalType));
			}

			return null;
		} catch (Exception err) {
			log.error("[getRecordingsByExternalType] ", err);
			throw new ServiceException(err.getMessage());
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
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getFlvRecordingByExternalRoomType(String SID,
			String externalRoomType) throws ServiceException {
		try {

			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(flvRecordingDao.getFlvRecordingByExternalRoomType(externalRoomType));
			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomType] ", err);
			throw new ServiceException(err.getMessage());
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
	 * @throws ServiceException
	 */
	public List<RecordingDTO> getFlvRecordingByRoomId(String SID, Long roomId)
			throws ServiceException {
		try {

			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return RecordingDTO.list(flvRecordingDao.getFlvRecordingByRoomId(roomId));
			}

			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomType] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * List of available room types
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @return - List of available room types
	 * @throws ServiceException
	 */
	public List<RoomType> getRoomTypes(String SID) throws ServiceException {
		try {
			return conferenceService.getRoomTypes(SID);
		} catch (Exception err) {
			log.error("[getRoomTypes]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Returns current users for rooms ids
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 * @return - current users for rooms ids
	 * @throws ServiceException
	 */
	public List<RoomCountBean> getRoomCounters(String SID, Integer[] roomId) throws ServiceException {
		List<RoomCountBean> roomBeans = new ArrayList<RoomCountBean>();
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				List<Integer> roomIds = new ArrayList<Integer>();

				if (roomId != null) {
					for (Integer id : roomId) {
						if (id != null && id > 0) {
							log.debug("roomId :: " + id);
							roomIds.add(id);
						}
					}
				}

				List<Room> rooms = roomManager.getRoomsByIds(roomIds);

				for (Room room : rooms) {
					RoomCountBean rCountBean = new RoomCountBean();
					rCountBean.setRoomId(room.getId());
					rCountBean.setRoomName(room.getName());
					rCountBean.setMaxUser(room.getNumberOfPartizipants().intValue());
					rCountBean.setRoomCount(sessionManager.getClientListByRoom(room.getId()).size());

					roomBeans.add(rCountBean);
				}

			} else {
				log.error("Not authorized");
			}
		} catch (Exception err) {
			log.error("[getRoomTypes]", err);
			throw new ServiceException(err.getMessage());
		}
		return roomBeans;
	}

	/**
	 * returns a conference room object
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @param roomId - the room id
	 * @return - room with the id given
	 */
	public Room getRoomById(String SID, long roomId) {
		return conferenceService.getRoomById(SID, roomId);
	}

	/**
	 * Returns a object of type RoomReturn
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 * @return - object of type RoomReturn
	 * @throws ServiceException
	 */
	public RoomReturn getRoomWithClientObjectsById(String SID, long roomId) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				Room room = roomDao.get(roomId);

				RoomReturn roomReturn = new RoomReturn();

				roomReturn.setCreated(room.getInserted());
				roomReturn.setCreator(null);
				roomReturn.setName(room.getName());
				roomReturn.setRoomId(room.getId());

				List<Client> map = sessionManager
						.getClientListByRoom(room.getId());

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
			throw new ServiceException(err.getMessage());
		}

	}

	/**
	 * Returns a List of Objects of Rooms You can use "name" as value for
	 * orderby or roomId
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @param start - The id you want to start
	 * @param max - The maximum you want to get
	 * @param orderby - The column it will be ordered
	 * @param asc - Asc or Desc sort ordering
	 *            
	 * @return - List of Objects of Rooms
	 */
	public RoomSearchResult getRooms(String SID, int start, int max, String orderby, boolean asc) {
		return new RoomSearchResult(conferenceService.getRooms(SID, start, max, orderby, asc, ""));
	}

	/**
	 * Returns a List of Objects of Rooms You can use "name" as value for
	 * orderby or rooms_id. It also fills the attribute currentUsers in the
	 * Room-Object
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @param start - The id you want to start
	 * @param max - The maximum you want to get
	 * @param orderby - The column it will be ordered
	 * @param asc - Asc or Desc sort ordering
	 *            
	 * @return - List of Objects of Rooms
	 */
	public RoomSearchResult getRoomsWithCurrentUsers(String SID, int start, int max, String orderby, boolean asc) {
		return new RoomSearchResult(conferenceService.getRoomsWithCurrentUsers(SID, start, max, orderby, asc));
	}

	// TODO: Add functions to get Users of a Room

	/**
	 * Create a conference room
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            Name of the Room
	 * @param roomtypesId
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
			Long roomtypesId, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.addRoom(name, roomtypesId, comment,
						numberOfPartizipants, ispublic, null, appointment,
						isDemoRoom, demoTime, isModeratedRoom, null, true,
						false, true, false //isClosed
						, "", "", null, null,
						true,  // allowRecording
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
	 * @param roomtypesId
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
			Long roomtypesId, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.addRoom(name, roomtypesId, comment,
						numberOfPartizipants, ispublic, null, appointment,
						isDemoRoom, demoTime, isModeratedRoom, null,
						allowUserQuestions, false, true, false //isClosed
						, "", "", null,
						null, 
						true,  // allowRecording
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
	 * @param roomtypesId
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
	 * @throws ServiceException
	 */
	public Long addRoomWithModerationQuestionsAndAudioType(String SID,
			String name, Long roomtypesId, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions, Boolean isAudioOnly) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.addRoom(name, roomtypesId, comment,
						numberOfPartizipants, ispublic, null, appointment,
						isDemoRoom, demoTime, isModeratedRoom, null,
						allowUserQuestions, isAudioOnly, true, false //isClosed
						, "", "",
						null, null, 
						true,  // allowRecording
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
			return -1L;
		} catch (Exception err) {
			log.error("[addRoomWithModerationQuestionsAndAudioType] ", err);
			throw new ServiceException(err.getMessage());
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
	 * @param roomtypesId
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
	 * @throws ServiceException
	 */
	public Long addRoomWithModerationQuestionsAudioTypeAndHideOptions(
			String SID, String name, Long roomtypesId, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions, Boolean isAudioOnly,
			Boolean hideTopBar, Boolean hideChat,
			Boolean hideActivitiesAndActions, Boolean hideFilesExplorer,
			Boolean hideActionsMenu, Boolean hideScreenSharing,
			Boolean hideWhiteboard) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.addRoom(name, roomtypesId, comment,
						numberOfPartizipants, ispublic, null, appointment,
						isDemoRoom, demoTime, isModeratedRoom, null,
						allowUserQuestions, isAudioOnly, true, false //isClosed
						, "", "",
						null, null,
						true,  // allowRecording
						hideTopBar, hideChat,
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
			throw new ServiceException(err.getMessage());
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
	 * @param roomtypesId
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
	 * @throws ServiceException
	 */
	public Long getRoomIdByExternalId(String SID, String name,
			Long roomtypesId, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, Long externalRoomId,
			String externalRoomType) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room room = conferenceService.getRoomByExternalId(SID,
						externalRoomId, externalRoomType, roomtypesId);
				Long roomId = null;
				if (room == null) {
					roomId = roomManager.addExternalRoom(name, roomtypesId,
							comment, numberOfPartizipants, ispublic, null,
							appointment, isDemoRoom, demoTime, isModeratedRoom,
							null, externalRoomId, externalRoomType, true,
							false, true, false, "", false, true, false);
				} else {
					roomId = room.getId();
				}
				return roomId;
			}

			return -26L;
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
			throw new ServiceException(err.getMessage());
		}
		// return new Long (-1);
	}

	/**
	 * Updates a conference room by its room id
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            the room id to update
	 * @param name
	 *            new name of the room
	 * @param roomtypesId
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
	public Long updateRoomWithModeration(String SID, Long roomId, String name,
			Long roomtypesId, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.updateRoomInternal(roomId, roomtypesId,
						name, ispublic, comment, numberOfPartizipants, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, true, false, true, false, "", "", null, null,
						true,  // allowRecording
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
	 * @param roomId
	 *            the room id to update
	 * @param name
	 *            new name of the room
	 * @param roomtypesId
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
	public Long updateRoomWithModerationAndQuestions(String SID, Long roomId,
			String name, Long roomtypesId, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.updateRoomInternal(roomId, roomtypesId,
						name, ispublic, comment, numberOfPartizipants, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, allowUserQuestions, false, true, false, "", "",
						null, null, 
						true,  // allowRecording 
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
			log.error("[updateRoomWithModerationAndQuestions] ", err);
		}
		return new Long(-1);
	}

	/**
	 * update room with options for user questions, audio only and hide
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            the room id to update
	 * @param name
	 *            new name of the room
	 * @param roomtypesId
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
			String SID, Long roomId, String name, Long roomtypesId,
			String comment, Long numberOfPartizipants, Boolean ispublic,
			Boolean appointment, Boolean isDemoRoom, Integer demoTime,
			Boolean isModeratedRoom, Boolean allowUserQuestions,
			Boolean isAudioOnly, Boolean hideTopBar, Boolean hideChat,
			Boolean hideActivitiesAndActions, Boolean hideFilesExplorer,
			Boolean hideActionsMenu, Boolean hideScreenSharing,
			Boolean hideWhiteboard) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.updateRoomInternal(roomId, roomtypesId,
						name, ispublic, comment, numberOfPartizipants, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, allowUserQuestions, isAudioOnly, true, false, "", "",
						null, null,
						true,  // allowRecording
						hideTopBar, hideChat,
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
			log.error("[updateRoomWithModerationQuestionsAudioTypeAndHideOptions] ", err);
		}
		return new Long(-1);
	}

	/**
	 * Delete a room by its room id
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 * 
	 * @return - id of the room deleted
	 */
	public Long deleteRoom(String SID, long roomId) {
		return conferenceService.deleteRoom(SID, roomId);
	}

	/**
	 * kick all uses of a certain room
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 *            _Admin
	 * @param roomId
	 *            the room id
	 *            
	 * @return - true if user was kicked, false otherwise
	 */
	public Boolean kickUser(String SID_Admin, Long roomId) {
		try {
			Boolean salida = false;

			salida = userManager.kickUserByStreamId(SID_Admin, roomId);

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
	 * @param roomtypesId
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
			Long roomtypesId, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, String externalRoomType) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.addExternalRoom(name, roomtypesId,
						comment, numberOfPartizipants, ispublic, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, null, externalRoomType, true, false, true, false, "",
						false, true, false);
			}
		} catch (Exception err) {
			log.error("[addRoomWithModerationAndExternalType] ", err);
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
	 * @param roomtypesId
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
			String name, Long roomtypesId, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, Boolean allowUserQuestions,
			Boolean isAudioOnly) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.addExternalRoom(name, roomtypesId,
						comment, numberOfPartizipants, ispublic, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, null, externalRoomType, allowUserQuestions,
						isAudioOnly, true, false, "", false, true, false);
			}
		} catch (Exception err) {
			log.error("[addRoomWithModerationExternalTypeAndAudioType] ", err);
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
	 * @param roomtypesId
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
			Long roomtypesId, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, String externalRoomType,
			Boolean allowUserQuestions, Boolean isAudioOnly,
			Boolean waitForRecording, boolean allowRecording) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.addExternalRoom(name, roomtypesId,
						comment, numberOfPartizipants, ispublic, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, null, externalRoomType, allowUserQuestions,
						isAudioOnly, true, false, "", waitForRecording,
						allowRecording, false);
			} else {
				return -26L;
			}
		} catch (Exception err) {
			log.error("[addRoomWithModerationAndRecordingFlags] ", err);
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
	 * @param roomtypesId
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
			String name, Long roomtypesId, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, Boolean allowUserQuestions,
			Boolean isAudioOnly, Boolean waitForRecording,
			boolean allowRecording, Boolean hideTopBar) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				return roomManager.addExternalRoom(name, roomtypesId,
						comment, numberOfPartizipants, ispublic, null,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, null, externalRoomType, allowUserQuestions,
						isAudioOnly, true, false, "", waitForRecording,
						allowRecording, hideTopBar);
			}
		} catch (Exception err) {
			log.error("[addRoomWithModerationExternalTypeAndTopBarOption] ", err);
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
	 * @param roomId
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
	 * @throws ServiceException
	 */
	public String getInvitationHash(String SID, String username, Long roomId,
			Boolean isPasswordProtected, String invitationpass, Integer valid,
			String validFromDate, String validFromTime, String validToDate,
			String validToTime) throws ServiceException {
		return getInvitationHashFullName(SID, username, username, username, roomId, isPasswordProtected, invitationpass, valid,
				validFromDate, validFromTime, validToDate, validToTime);
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
	 * @param firstname
	 *            the first name of the User that he will get
	 * @param lastname
	 *            the last name of the User that he will get
	 * @param roomId
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
	 * @throws ServiceException
	 */
	public String getInvitationHashFullName(String SID, String username, String firstname, String lastname, Long roomId,
			Boolean isPasswordProtected, String invitationpass, Integer valid,
			String validFromDate, String validFromTime, String validToDate,
			String validToTime) throws ServiceException {
		return sendInvitationHash(SID, username, firstname, lastname, null, null,
				roomId, isPasswordProtected, invitationpass, valid, validFromDate,
				validFromTime, validToDate, validToTime, 1L, false);
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
	 * @param email
	 *            the Email to send the invitation to if sendMail is true
	 * @param subject
	 *            the subject of the Email send with the invitation if sendMail
	 *            is true
	 * @param roomId
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
	 * @param languageId
	 *            the language id of the EMail that is send with the invitation
	 *            if sendMail is true
	 * @param sendMail
	 *            if sendMail is true then the RPC-Call will send the invitation
	 *            to the email
	 * @return a HASH value that can be made into a URL with
	 *         http://$OPENMEETINGS_HOST
	 *         :$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws ServiceException
	 */
	public String sendInvitationHash(String SID, String username, String message, String email, String subject,
			Long roomId, Boolean isPasswordProtected, String invitationpass, Integer valid, String validFromDate,
			String validFromTime, String validToDate, String validToTime, Long languageId, Boolean sendMail) throws ServiceException {
		return sendInvitationHash(SID, email, username, username, message, subject,
				roomId, isPasswordProtected, invitationpass, valid, validFromDate,
				validFromTime, validToDate, validToTime, languageId, sendMail);
	}

	private String sendInvitationHash(String SID, String email, String firstname, String lastname, String message, String subject,
			Long roomId, Boolean isPasswordProtected, String invitationpass, Integer valid, String validFromDate,
			String validFromTime, String validToDate, String validToTime, Long languageId, Boolean sendMail) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				Date dFrom = new Date();
				Date dTo = new Date();

				if (valid == 2) {
					Integer validFromHour = Integer.valueOf(validFromTime.substring(0, 2));
					Integer validFromMinute = Integer.valueOf(validFromTime.substring(3, 5));

					Integer validToHour = Integer.valueOf(validToTime.substring(0, 2));
					Integer validToMinute = Integer.valueOf(validToTime.substring(3, 5));

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

					log.info("validFromDate: " + CalendarPatterns.getDateWithTimeByMiliSeconds(dFrom));
					log.info("validToDate: " + CalendarPatterns.getDateWithTimeByMiliSeconds(dTo));
				}

				User invitee = userDao.getContact(email, firstname, lastname, userId);
				Invitation invitation = invitationManager.getInvitation(invitee, roomDao.get(roomId),
								isPasswordProtected, invitationpass, Valid.fromInt(valid)
								, userDao.get(userId), languageId,
								dFrom, dTo, null);

				if (invitation != null) {
					if (sendMail) {
						invitationManager.sendInvitionLink(invitation, MessageType.Create, subject, message, false);
					}

					return invitation.getHash();
				} else {
					return "Sys - Error";
				}
			} else {
				return "Need Admin Privileges to perfom the Action";
			}

		} catch (Exception err) {
			log.error("[sendInvitationHash] ", err);
			throw new ServiceException(err.getMessage());
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
	 * @param email
	 *            the Email to send the invitation to if sendMail is true
	 * @param subject
	 *            the subject of the Email send with the invitation if sendMail
	 *            is true
	 * @param roomId
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
	 * @param languageId
	 *            the language id of the EMail that is send with the invitation
	 *            if sendMail is true
	 * @param sendMail
	 *            if sendMail is true then the RPC-Call will send the invitation
	 *            to the email
	 * @return a HASH value that can be made into a URL with
	 *         http://$OPENMEETINGS_HOST
	 *         :$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws ServiceException
	 */
	public String sendInvitationHashWithDateObject(String SID, String username,
			String message, String email, String subject,
			Long roomId, String conferencedomain, Boolean isPasswordProtected,
			String invitationpass, Integer valid, Date fromDate, Date toDate,
			Long languageId, Boolean sendMail) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

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

				User invitee = userDao.getContact(email, userId);
				Invitation invitation = invitationManager.getInvitation(invitee, roomDao.get(roomId),
								isPasswordProtected, invitationpass, Valid.fromInt(valid)
								, userDao.get(userId), languageId, dFrom, dTo, null);

				if (invitation != null) {
					if (sendMail) {
						invitationManager.sendInvitionLink(invitation, MessageType.Create, subject, message, false);
					}

					return invitation.getHash();
				} else {
					return "Sys - Error";
				}
			} else {
				return "Need Admin Privileges to perfom the Action";
			}
		} catch (Exception err) {
			log.error("[sendInvitationHash] ", err);
			throw new ServiceException(err.getMessage());
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
	 * @throws ServiceException
	 */
	public List<RoomReturn> getRoomsWithCurrentUsersByList(String SID, int start, int max, String orderby, boolean asc) throws ServiceException {
		try {
			List<Room> rooms = conferenceService.getRoomsWithCurrentUsersByList(SID, start, max, orderby, asc);

			List<RoomReturn> returnObjList = new LinkedList<RoomReturn>();
			if (rooms != null) {
				for (Room room : rooms) {
					RoomReturn roomReturn = new RoomReturn();
	
					roomReturn.setRoomId(room.getId());
					roomReturn.setName(room.getName());
	
					roomReturn.setCreator("SOAP");
					roomReturn.setCreated(room.getInserted());
	
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
			}
			return returnObjList;
		} catch (Exception err) {
			log.error("setUserObjectWithExternalUser", err);
			throw new ServiceException(err.getMessage());
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
	 * @throws ServiceException
	 */
	public List<RoomReturn> getRoomsWithCurrentUsersByListAndType(String SID,
			int start, int max, String orderby, boolean asc,
			String externalRoomType) throws ServiceException {
		try {
			List<Room> rooms = conferenceService.getRoomsWithCurrentUsersByListAndType(SID, start, max, orderby, asc, externalRoomType);

			List<RoomReturn> returnObjList = new LinkedList<RoomReturn>();
			if (rooms != null) {
				for (Room room : rooms) {
					RoomReturn roomReturn = new RoomReturn();
	
					roomReturn.setRoomId(room.getId());
					roomReturn.setName(room.getName());
	
					roomReturn.setCreator("SOAP");
					roomReturn.setCreated(room.getInserted());
	
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
			}
			return returnObjList;
		} catch (Exception err) {
			log.error("setUserObjectWithExternalUser", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Adds a conference room that is only available for a period of time
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param name
	 *            new name of the room
	 * @param roomtypesId
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
	 * @throws ServiceException
	 */
	public Long addRoomWithModerationAndExternalTypeAndStartEnd(String SID,
			String name, Long roomtypesId, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, String validFromDate,
			String validFromTime, String validToDate, String validToTime,
			Boolean isPasswordProtected, String password, Long reminderTypeId,
			String redirectURL) throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				int validFromHour = Integer.valueOf(validFromTime.substring(0, 2)).intValue();
				int validFromMinute = Integer.valueOf(validFromTime.substring(3, 5)).intValue();

				int validToHour = Integer.valueOf(validToTime.substring(0, 2)).intValue();
				int validToMinute = Integer.valueOf(validToTime.substring(3, 5)).intValue();

				log.info("validFromHour: " + validFromHour);
				log.info("validFromMinute: " + validFromMinute);

				Date fromDate = CalendarPatterns.parseDateBySeparator(validFromDate); // dd.MM.yyyy
				Date toDate = CalendarPatterns.parseDateBySeparator(validToDate); // dd.MM.yyyy

				if (fromDate == null || toDate == null) {
					throw new ServiceException("Invalid dates are passed");
				}
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

				Date dFrom = calFrom.getTime();
				Date dTo = calTo.getTime();

				log.info("validFromDate: " + CalendarPatterns.getDateWithTimeByMiliSeconds(dFrom));
				log.info("validToDate: " + CalendarPatterns.getDateWithTimeByMiliSeconds(dTo));

				Long roomId = roomManager.addExternalRoom(name,
						roomtypesId, comment, numberOfPartizipants, ispublic,
						null, appointment, isDemoRoom, demoTime,
						isModeratedRoom, null, null, externalRoomType, false, // allowUserQuestions
						false, // isAudioOnly
						true,  // allowFontStyles
						false, // isClosed
						redirectURL, false, true, false);

				if (roomId <= 0) {
					return roomId;
				}

				Appointment a = new Appointment();
				a.setTitle("appointmentName");
				a.setOwner(userDao.get(userId));
				a.setLocation("appointmentLocation");
				a.setDescription("appointmentDescription");
				a.setStart(dFrom);
				a.setEnd(dTo);
				a.setCategory(appointmentCategoryDao.get(1L));
				a.setRemind(appointmentReminderTypDao.get(reminderTypeId));
				a.setRoom(roomDao.get(roomId));
				a.setPasswordProtected(isPasswordProtected);
				a.setPassword(password);
				a.setLanguageId(1L); //TODO check
				appointmentDao.update(a, userId); //FIXME verify !!!

				return roomId;

			} else {
				return -2L;
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);

			throw new ServiceException(err.getMessage());
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
	 * @param roomId
	 *            The Room Id the meeting member is going to be added
	 * @param firstname
	 *            The first name of the meeting member
	 * @param lastname
	 *            The last name of the meeting member
	 * @param email
	 *            The email of the Meeting member
	 * @param languageId
	 *            The ID of the language, for the email that is send to the
	 *            meeting member
	 *            
	 * @return - id of the member in case of success, error code otherwise
	 * @throws ServiceException
	 */
	public Long addMeetingMemberRemindToRoom(String SID, Long roomId,
			String firstname, String lastname, String email, Long languageId) throws ServiceException {
		return addExternalMeetingMemberRemindToRoom(SID, roomId, firstname, lastname, email, languageId, null, null);
	}

	/**
	 * Add a meeting member to a certain room. This is the same as adding an
	 * external user to a event in the calendar. with a certain time zone
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            The Room Id the meeting member is going to be added
	 * @param firstname
	 *            The first name of the meeting member
	 * @param lastname
	 *            The last name of the meeting member
	 * @param email
	 *            The email of the Meeting member
	 * @param languageId
	 *            The ID of the language, for the email that is send to the
	 *            meeting member
	 * @param jNameTimeZone
	 *            name of the timezone
	 * @param invitorName
	 *            name of invitation creators
	 *            
	 * @return - id of the member in case of success, error code otherwise
	 * @throws ServiceException
	 */
	public Long addExternalMeetingMemberRemindToRoom(String SID, Long roomId,
			String firstname, String lastname, String email, Long languageId, String jNameTimeZone, String invitorName)
			throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Appointment a = appointmentDao.getAppointmentByRoom(roomId);

				if (email == null || a == null) {
					return -1L;
				}
				for (MeetingMember mm : a.getMeetingMembers()) {
					if (email.equals(mm.getUser().getAddress().getEmail())) {
						return mm.getId();
					}
				}
				MeetingMember mm = new MeetingMember();
				mm.setAppointment(a);
				mm.setUser(userDao.getContact(email, firstname, lastname, languageId, jNameTimeZone, userId));
				a.getMeetingMembers().add(mm);
				appointmentDao.update(a, userId);

				return mm.getId(); //FIXME check to return ID
			} else {
				return -2L;
			}
		} catch (Exception err) {
			log.error("[addExternalMeetingMemberRemindToRoom] ", err);

			throw new ServiceException(err.getMessage());
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
	 * @param roomId
	 *            the room id
	 * @param status
	 *            false = close, true = open
	 *            
	 * @return - 1 in case of success, -2 otherwise
	 * @throws ServiceException
	 */
	public int closeRoom(String SID, Long roomId, Boolean status)
			throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);

			log.debug("closeRoom 1 " + roomId);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				log.debug("closeRoom 2 " + status);

				roomManager.closeRoom(roomId, status);

				if (status) {
					Map<String, String> message = new HashMap<String, String>();
					message.put("message", "roomClosed");
					scopeApplicationAdapter.sendMessageByRoomAndDomain(roomId,
							message);
				}
				return 1;

			} else {
				return -2;
			}
		} catch (Exception err) {
			log.error("[closeRoom] ", err);

			throw new ServiceException(err.getMessage());
		}

	}

	/**
	 * Method to update arbitrary room parameter.
	 * 
	 * @param SID The SID of the User. This SID must be marked as logged in
	 * @param roomId the room id
	 * @param paramName the name of parameter to be updated, please NOTE rooms_id is not updatable as well as fields of type {@link Date} and {@link List}
	 * @param paramValue the value to be set, please use "type id" to set room type 
	 * @return 1 in case of success, -2 if permissions are insufficient
	 * @throws AxisFault if any error occurred
	 */
	public int modifyRoomParameter(String SID, Long roomId, String paramName, String paramValue) throws ServiceException {
		try {
			if ("rooms_id".equals(paramName)) {
				throw new ServiceException("Non modifiable parameter");
			}
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				log.debug(String.format("modifyRoomParameter[%s]: %s = %s", roomId, paramName, paramValue));
				Room r = roomDao.get(roomId);
				BeanWrapper rw = new BeanWrapperImpl(r);
				Class<?> valueClass = rw.getPropertyType(paramName);
				Object val = null;
				//don't like this code
				if (valueClass == null) {
					//do nothing
				} else if (valueClass.isAssignableFrom(String.class)) {
					val = paramValue;
				} else if (valueClass.isAssignableFrom(Boolean.class) || valueClass.isAssignableFrom(boolean.class)) {
					val = Boolean.parseBoolean(paramValue);
				} else if (valueClass.isAssignableFrom(RoomType.class)) {
					val = roomTypeDao.get(Long.parseLong(paramValue));
				} else if (valueClass.isAssignableFrom(Long.class)) {
					val = Long.parseLong(paramValue);
				} else if (valueClass.isAssignableFrom(Integer.class)) {
					val = Integer.parseInt(paramValue);
				}
				rw.setPropertyValue(paramName, val);
				roomDao.update(r, userId);
			} else {
				return -2;
			}
			return 1;
		} catch (Exception err) {
			log.error("[modifyRoomParameter] ", err);

			throw new ServiceException(err.getMessage());
		}
	}
	
	/**
	 * Adds a room to an organization
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @param roomId - Id of room to be added
	 * @param organisationId - Id of organisation that the room is being paired with
	 * 
	 * @return Id of the relation created, null or -1 in case of the error
	 */
	public Long addRoomToOrg(String SID, Long roomId, Long organisationId) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				if (null == roomManager.getRoomsOrganisationByOrganisationIdAndRoomId(organisationId, roomId)) {
					return roomManager.addRoomToOrganisation(roomId, organisationId);
				}
			}
		} catch (Exception err) {
			log.error("[addRoomToOrg]", err);
		}
		return new Long(-1);
	}
}
