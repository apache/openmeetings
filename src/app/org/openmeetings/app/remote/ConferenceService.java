package org.openmeetings.app.remote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.conference.dao.RoomModeratorsDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.persistence.beans.rooms.RoomModerators;
import org.openmeetings.app.persistence.beans.rooms.RoomTypes;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.rooms.Rooms_Organisation;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class ConferenceService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ConferenceService.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private RoomModeratorsDaoImpl roomModeratorsDao;
	@Autowired
	private AuthLevelmanagement authLevelManagement;

	// beans, see chaservice.service.xml
	private ClientListManager clientListManager = null;

	public ClientListManager getClientListManager() {
		return clientListManager;
	}

	public void setClientListManager(ClientListManager clientListManager) {
		this.clientListManager = clientListManager;
	}

	/**
	 * ( get a List of all availible Rooms of this organisation
	 * (non-appointments)
	 * 
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public List<Rooms_Organisation> getRoomsByOrganisationAndType(String SID,
			long organisation_id, long roomtypes_id) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			log.debug("getRoomsByOrganisationAndType");

			if (user_level == null) {
				return null;
			}
			List<Rooms_Organisation> roomOrgsList = roommanagement
					.getRoomsOrganisationByOrganisationIdAndRoomType(
							user_level, organisation_id, roomtypes_id);

			List<Rooms_Organisation> filtered = new ArrayList<Rooms_Organisation>();

			for (Iterator<Rooms_Organisation> iter = roomOrgsList.iterator(); iter
					.hasNext();) {
				Rooms_Organisation orgRoom = iter.next();

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

	public List<Rooms_Organisation> getRoomsByOrganisationWithoutType(
			String SID, long organisation_id) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			log.debug("getRoomsByOrganisationAndType");

			if (user_level == null) {
				return null;
			}
			List<Rooms_Organisation> roomOrgsList = roommanagement
					.getRoomsOrganisationByOrganisationId(user_level,
							organisation_id);

			List<Rooms_Organisation> filtered = new ArrayList<Rooms_Organisation>();

			for (Iterator<Rooms_Organisation> iter = roomOrgsList.iterator(); iter
					.hasNext();) {
				Rooms_Organisation orgRoom = iter.next();

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

	/**
	 * gets all rooms of an organisation TODO:check if the requesting user is
	 * also member of that organisation
	 * 
	 * @param SID
	 * @param organisation_id
	 * @return
	 */
	public SearchResult getRoomsByOrganisation(String SID,
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
	 * @param organisation_id
	 * @return
	 */
	public List<Rooms> getRoomsPublic(String SID, Long roomtypes_id) {
		try {
			log.debug("getRoomsPublic");

			Long users_id = sessionManagement.checkSession(SID);
			Long User_level = userManagement.getUserLevelByID(users_id);
			log.error("getRoomsPublic user_level: " + User_level);

			List<Rooms> roomList = roommanagement.getPublicRooms(User_level,
					roomtypes_id);

			// Filter : no appointed meetings
			List<Rooms> filtered = new ArrayList<Rooms>();

			for (Iterator<Rooms> iter = roomList.iterator(); iter.hasNext();) {
				Rooms rooms = iter.next();

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

	public List<Rooms> getRoomsPublicWithoutType(String SID) {
		try {
			log.debug("getRoomsPublic");

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			log.error("getRoomsPublic user_level: " + user_level);

			List<Rooms> roomList = roommanagement
					.getPublicRoomsWithoutType(user_level);

			// Filter : no appointed meetings
			List<Rooms> filtered = new ArrayList<Rooms>();

			for (Iterator<Rooms> iter = roomList.iterator(); iter.hasNext();) {
				Rooms rooms = iter.next();

				if (!rooms.getAppointment()) {
					rooms.setCurrentusers(this.getRoomClientsListByRoomId(rooms
							.getRooms_id()));
					filtered.add(rooms);
				}
			}

			return filtered;
		} catch (Exception err) {
			log.error("[getRoomsPublicWithoutType]", err);
		}
		return null;
	}

	/**
	 * retrieving ServerTime
	 * 
	 * @return
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
	 * @author o.becherer
	 * 
	 */
	// --------------------------------------------------------------------------------------------
	public Appointment getAppointMentDataForRoom(Long room_id) {
		log.debug("getAppointMentDataForRoom");

		Rooms room = roommanagement.getRoomById(room_id);

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
		log.debug("getAppointMentDataForRoom");

		IConnection current = Red5.getConnectionLocal();
		String streamid = current.getClient().getId();

		log.debug("getCurrentRoomClient -2- " + streamid);

		RoomClient currentClient = this.clientListManager
				.getClientByStreamId(streamid);

		Rooms room = roommanagement.getRoomById(room_id);

		if (room.getAppointment() == false)
			return null;

		try {
			Appointment appointment = appointmentLogic
					.getAppointmentByRoom(room_id);

			Map<String, Object> returnMap = new HashMap<String, Object>();

			returnMap.put("appointment", appointment);

			Users us = userManagement.getUserById(currentClient.getUser_id());

			String jNameTimeZone = null;
			if (us != null && us.getOmTimeZone() != null) {
				jNameTimeZone = us.getOmTimeZone().getJname();
			} else {
				Configuration conf = cfgManagement.getConfKey(3L,
						"default.timezone");
				if (conf != null) {
					jNameTimeZone = conf.getConf_value();
				}
			}

			OmTimeZone omTimeZone = omTimeZoneDaoImpl
					.getOmTimeZone(jNameTimeZone);

			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone(omTimeZone.getIcal()));
			int offset = cal.get(Calendar.ZONE_OFFSET)
					+ cal.get(Calendar.DST_OFFSET);

			Date starttime = new Date(appointment.getAppointmentStarttime()
					.getTime() + offset);
			Date endtime = new Date(appointment.getAppointmentEndtime()
					.getTime() + offset);

			returnMap.put("appointment", appointment);

			returnMap.put("start",
					CalendarPatterns.getDateWithTimeByMiliSeconds(starttime));
			returnMap.put("end",
					CalendarPatterns.getDateWithTimeByMiliSeconds(endtime));
			returnMap.put("timeZone", omTimeZone.getIcal());

			return returnMap;
		} catch (Exception e) {
			log.error("getAppointMentAndTimeZones " + e.getMessage());
			return null;
		}

	}

	/**
	 * 
	 */
	// --------------------------------------------------------------------------------------------
	public List<Rooms> getAppointedMeetings(String SID, Long room_types_id) {
		log.debug("ConferenceService.getAppointedMeetings");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);

		if (authLevelManagement.checkUserLevel(user_level)) {

			List<Appointment> points = appointmentLogic
					.getTodaysAppointmentsForUser(users_id);
			List<Rooms> result = new ArrayList<Rooms>();

			if (points != null) {
				for (int i = 0; i < points.size(); i++) {
					Appointment ment = points.get(i);

					Long rooms_id = ment.getRoom().getRooms_id();
					Rooms rooom = roommanagement.getRoomById(rooms_id);

					if (!rooom.getRoomtype().getRoomtypes_id()
							.equals(room_types_id))
						continue;

					rooom.setCurrentusers(this.getRoomClientsListByRoomId(rooom
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

	public List<Rooms> getAppointedMeetingRoomsWithoutType(String SID) {
		log.debug("ConferenceService.getAppointedMeetings");
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkUserLevel(user_level)) {
				List<Appointment> appointments = appointmentLogic
						.getTodaysAppointmentsForUser(users_id);
				List<Rooms> result = new ArrayList<Rooms>();

				if (appointments != null) {
					for (int i = 0; i < appointments.size(); i++) {
						Appointment ment = appointments.get(i);

						Long rooms_id = ment.getRoom().getRooms_id();
						Rooms rooom = roommanagement.getRoomById(rooms_id);

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
	 * @return
	 */
	public List<RoomTypes> getRoomTypes(String SID) {
		Long users_id = sessionManagement.checkSession(SID);

		log.debug("users_id " + users_id);

		Long user_level = userManagement.getUserLevelByID(users_id);

		return roommanagement.getAllRoomTypes(user_level);
	}

	/**
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public Rooms getRoomById(String SID, long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomById(user_level, rooms_id);
	}

	public Rooms getRoomWithCurrentUsersById(String SID, long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomById(user_level, rooms_id);
	}

	/**
	 * 
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public Rooms getRoomByExternalId(String SID, Long externalUserId,
			String externalUserType, long roomtypes_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomByExternalId(user_level, externalUserId,
				externalUserType, roomtypes_id);
	}

	/**
	 * gets a list of all availible rooms
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public SearchResult getRooms(String SID, int start, int max,
			String orderby, boolean asc, String search) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRooms(user_level, start, max, orderby, asc,
				search);
	}

	public SearchResult getRoomsWithCurrentUsers(String SID, int start,
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
	 * @return
	 */
	public List<Rooms_Organisation> getOrganisationByRoom(String SID,
			long rooms_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getOrganisationsByRoom(user_level, rooms_id);
	}

	/**
	 * 
	 * @param SID
	 * @param name
	 * @param roomtypes_id
	 * @deprecated
	 * @return
	 */
	// public Long addRoomPublic(String SID, String name, long roomtypes_id){
	// Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	// Long User_level = userManagement.getUserLevelByID(users_id);
	// return roommanagement.addRoom(User_level, name,
	// roomtypes_id,"", new Long(4), true,null,
	// 290, 280, 2, 2,
	// 400,
	// true, 296, 2, 592, 660,
	// true, 2, 284, 310, 290, false, false, null);
	// }

	/**
	 * 
	 * @param SID
	 * @param organisation_id
	 * @param name
	 * @param roomtypes_id
	 * @param ispublic
	 * @deprecated
	 * @return
	 */
	// public Long addRoomOrganisation(String SID, long organisation_id, String
	// name, long roomtypes_id, boolean ispublic){
	// Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	// long User_level = userManagement.getUserLevelByID(users_id);
	// Long rooms_id = roommanagement.addRoom(User_level, name,
	// roomtypes_id,"", new Long(4), ispublic, null,
	// 290, 280, 2, 2,
	// 400,
	// true, 296, 2, 592, 660,
	// true, 2, 284, 310, 290, false, false, null);
	// return roommanagement.addRoomToOrganisation(User_level,
	// rooms_id, organisation_id);
	// }

	/**
	 * 
	 * @param SID
	 * @param argObject
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public Long saveOrUpdateRoom(String SID, Object argObject) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			long User_level = userManagement.getUserLevelByID(users_id);
			log.debug("argObject: 1 - " + argObject.getClass().getName());
			LinkedHashMap argObjectMap = (LinkedHashMap) argObject;
			log.debug("argObject: 2 - "
					+ argObjectMap.get("organisations").getClass().getName());
			List organisations = (List) argObjectMap.get("organisations");
			Long rooms_id = Long.valueOf(
					argObjectMap.get("rooms_id").toString()).longValue();
			log.debug("rooms_id " + rooms_id);

			List roomModerators = (List) argObjectMap.get("roomModerators");

			Integer demoTime = null;
			if (argObjectMap.get("demoTime").toString() != null
					&& argObjectMap.get("demoTime").toString().length() > 0) {
				demoTime = Integer.valueOf(
						argObjectMap.get("demoTime").toString()).intValue();
			}

			if (rooms_id == 0) {
				return roommanagement.addRoom(
						User_level,
						argObjectMap.get("name").toString(),
						Long.valueOf(
								argObjectMap.get("roomtypes_id").toString())
								.longValue(),
						argObjectMap.get("comment").toString(),
						Long.valueOf(
								argObjectMap.get("numberOfPartizipants")
										.toString()).longValue(), Boolean
								.valueOf(argObjectMap.get("ispublic")
										.toString()), organisations, Boolean
								.valueOf(argObjectMap.get("appointment")
										.toString()), Boolean
								.valueOf(argObjectMap.get("isDemoRoom")
										.toString()), demoTime, Boolean
								.valueOf(argObjectMap.get("isModeratedRoom")
										.toString()), roomModerators, Boolean
								.valueOf(argObjectMap.get("allowUserQuestions")
										.toString()), Boolean
								.valueOf(argObjectMap.get("isAudioOnly")
										.toString()), Boolean
								.valueOf(argObjectMap.get("isClosed")
										.toString()),
						argObjectMap.get("redirectURL").toString(),
						argObjectMap.get("sipNumber").toString(), argObjectMap
								.get("conferencePin").toString(),
						Long.valueOf(argObjectMap.get("ownerId").toString())
								.longValue(), Boolean.valueOf(argObjectMap.get(
								"waitForRecording").toString()), Boolean
								.valueOf(argObjectMap.get("allowRecording")
										.toString()), Boolean
								.valueOf(argObjectMap.get("hideTopBar")
										.toString()));
			} else if (rooms_id > 0) {
				return roommanagement
						.updateRoom(
								User_level,
								rooms_id,
								Long.valueOf(
										argObjectMap.get("roomtypes_id")
												.toString()).longValue(),
								argObjectMap.get("name").toString(),
								Boolean.valueOf(argObjectMap.get("ispublic")
										.toString()),
								argObjectMap.get("comment").toString(),
								Long.valueOf(
										argObjectMap
												.get("numberOfPartizipants")
												.toString()).longValue(),
								organisations,
								Boolean.valueOf(argObjectMap.get("appointment")
										.toString()),
								Boolean.valueOf(argObjectMap.get("isDemoRoom")
										.toString()),
								demoTime,
								Boolean.valueOf(argObjectMap.get(
										"isModeratedRoom").toString()),
								roomModerators,
								Boolean.valueOf(argObjectMap.get(
										"allowUserQuestions").toString()),
								Boolean.valueOf(argObjectMap.get("isAudioOnly")
										.toString()),
								Boolean.valueOf(argObjectMap.get("isClosed")
										.toString()),
								argObjectMap.get("redirectURL").toString(),
								argObjectMap.get("sipNumber").toString(),
								argObjectMap.get("conferencePin").toString(),
								Long.valueOf(
										argObjectMap.get("ownerId").toString())
										.longValue(),
								Boolean.valueOf(argObjectMap.get(
										"waitForRecording").toString()),
								Boolean.valueOf(argObjectMap.get(
										"allowRecording").toString()), Boolean
										.valueOf(argObjectMap.get("hideTopBar")
												.toString()));
			}

		} catch (Exception e) {
			log.error("saveOrUpdateRoom", e);
		}
		return null;
	}

	public List<RoomModerators> getRoomModeratorsByRoomId(String SID,
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
	 * @return
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
	 * @return
	 */
	public List<RoomClient> getRoomClientsListByRoomId(Long room_id) {
		log.debug("getRoomClientsListByRoomId");
		try {
			// log.error("getRoomClientsListByRoomId: "+room_id);
			LinkedList<RoomClient> clients = new LinkedList<RoomClient>();
			HashMap<String, RoomClient> clientList = this.clientListManager
					.getClientListByRoom(room_id);
			for (Iterator<String> iter = clientList.keySet().iterator(); iter
					.hasNext();) {
				RoomClient rcl = clientList.get(iter.next());
				// log.error("COMPARE: "+rcl.getRoom_id()+" || "+room_id);
				clients.add(rcl);
			}
			return clients;
		} catch (Exception err) {
			log.error("[getRoomClientsListByRoomId]", err);
		}
		return null;
	}

	public HashMap<String, RoomClient> getRoomClientsMapByRoomId(Long room_id) {
		try {
			// log.error("getRoomClientsListByRoomId: "+room_id);
			return this.clientListManager.getClientListByRoom(room_id);
		} catch (Exception err) {
			log.error("[getRoomClientsMapByRoomId]", err);
		}
		return null;
	}

	public SearchResult getRoomClientsMap(String SID, int start, int max,
			String orderby, boolean asc) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkAdminLevel(user_level)) {
				return this.clientListManager.getListByStartAndMax(start, max,
						orderby, asc);
			}
		} catch (Exception err) {
			log.error("[getRoomClientsMap]", err);
		}
		return null;
	}

	public List<Rooms> getRoomsWithCurrentUsersByList(String SID, int start,
			int max, String orderby, boolean asc) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomsWithCurrentUsersByList(user_level, start,
				max, orderby, asc);
	}

	public List<Rooms> getRoomsWithCurrentUsersByListAndType(String SID,
			int start, int max, String orderby, boolean asc,
			String externalRoomType) {
		log.debug("getRooms");

		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return roommanagement.getRoomsWithCurrentUsersByListAndType(user_level,
				start, max, orderby, asc, externalRoomType);
	}

	public Rooms getRoomByOwnerAndType(String SID, Long roomtypesId,
			String roomName) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelManagement.checkUserLevel(user_level)) {
			return roommanagement.getRoomByOwnerAndTypeId(users_id,
					roomtypesId, roomName);
		}
		return null;
	}

}
