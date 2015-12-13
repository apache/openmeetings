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
package org.apache.openmeetings.data.conference;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.conference.dao.RoomModeratorsDao;
import org.apache.openmeetings.data.conference.dao.SipDao;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.room.RoomOrganisation;
import org.apache.openmeetings.persistence.beans.room.RoomType;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.session.ISessionManager;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author swagner
 * 
 */
@Transactional
public class RoomManager {

	private static final Logger log = Red5LoggerFactory
			.getLogger(RoomManager.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private OrganisationManager organisationManager;
	@Autowired
	private RoomModeratorsDao roomModeratorsDao;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private ISessionManager sessionManager;
    @Autowired
	private ConfigurationDao configurationDao;
    @Autowired
	private RoomDao roomDao;
    @Autowired
	private SipDao sipDao;

	/**
	 * add a new Record to the table roomtypes
	 * 
	 * @param name
	 * @return ID of new created roomtype or null
	 */
	public Long addRoomType(String name, boolean deleted) {
		try {
			RoomType rtype = new RoomType();
			rtype.setName(name);
			rtype.setStarttime(new Date());
			rtype.setDeleted(deleted);
			rtype = em.merge(rtype);
			long returnId = rtype.getRoomtypes_id();
			return returnId;
		} catch (Exception ex2) {
			log.error("[addRoomType] ", ex2);
		}
		return null;
	}

	/**
	 * get all availible RoomTypes
	 * 
	 * @return List of RoomTypes
	 */
	public List<RoomType> getAllRoomTypes() {
		try {
			TypedQuery<RoomType> query = em
					.createNamedQuery("getAllRoomTypes", RoomType.class);
			query.setParameter("deleted", true);
			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getAllRoomTypes] ", ex2);
		}
		return null;
	}

	/**
	 * Get a RoomTypes-Object by its id
	 * 
	 * @param roomtypes_id
	 * @return RoomTypes-Object or NULL
	 */
	public RoomType getRoomTypesById(long roomtypes_id) {
		try {
			TypedQuery<RoomType> query = em
					.createNamedQuery("getRoomTypesById", RoomType.class);
			query.setParameter("roomtypes_id", roomtypes_id);
			query.setParameter("deleted", true);
			List<?> ll = query.getResultList();
			if (ll.size() > 0) {
				return (RoomType) ll.get(0);
			}
		} catch (Exception ex2) {
			log.error("[getRoomTypesById] ", ex2);
		}
		return null;
	}

	/**
	 * get a room object if user level
	 * 
	 * @param user_level
	 * @param rooms_id
	 * @return
	 */
	public Room getRoomById(long user_level, long rooms_id) {
		try {
			if (authLevelUtil.checkUserLevel(user_level)) {
				return roomDao.get(rooms_id);
			} else
				log.error("getRoombyId : Userlevel" + user_level
						+ " not allowed");
		} catch (Exception ex2) {
			log.error("[getRoomById] ", ex2);
		}
		return null;
	}

	public Room getRoomWithCurrentUsersById(long user_level, long rooms_id) {
		try {
			if (authLevelUtil.checkUserLevel(user_level)) {
				Room room = roomDao.get(rooms_id);

				if (room != null) {
					room.setCurrentusers(sessionManager.getClientListByRoom(room.getRooms_id()));

					return room;
				}
			}
		} catch (Exception ex2) {
			log.error("[getRoomWithCurrentUsersById] ", ex2);
		}
		return null;
	}

	/**
	 * Get a Rooms-Object or NULL
	 * 
	 * @param externalRoomId
	 * @return Rooms-Object or NULL
	 */
	public Room getRoomByExternalId(Long externalRoomId,
			String externalRoomType, long roomtypes_id) {
		log.debug("getRoombyExternalId : " + externalRoomId + " - "
				+ externalRoomType + " - " + roomtypes_id);
		try {
			TypedQuery<Room> query = em.createNamedQuery("getRoomByExternalId", Room.class);
			query.setParameter("externalRoomId", externalRoomId);
			query.setParameter("externalRoomType", externalRoomType);
			query.setParameter("roomtypes_id", roomtypes_id);
			query.setParameter("deleted", true);
			List<?> ll = query.getResultList();
			if (ll.size() > 0) {
				return (Room) ll.get(0);
			} else {
				log.error("Could not find room " + externalRoomId);
			}
		} catch (Exception ex2) {
			log.error("[getRoomByExternalId] ", ex2);
		}
		return null;
	}

	public Room getRoomByExternalId(long user_level, Long externalRoomId,
			String externalRoomType, long roomtypes_id) {
		try {
			if (authLevelUtil.checkUserLevel(user_level)) {
				return this.getRoomByExternalId(externalRoomId,
						externalRoomType, roomtypes_id);
			} else
				log.error("getRoombyExternalId : Userlevel" + user_level
						+ " not allowed");
		} catch (Exception ex2) {
			log.error("[getRoomByExternalId] ", ex2);
		}
		return null;
	}

	public SearchResult<Room> getRooms(long user_level, int start, int max,
			String orderby, boolean asc, String search) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				SearchResult<Room> sResult = new SearchResult<Room>();
				sResult.setRecords(this.selectMaxFromRooms(search));
				sResult.setObjectName(Room.class.getName());
				sResult.setResult(this.getRoomsInternatlByHQL(start, max,
						orderby, asc, search));
				return sResult;
			}
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}
	
	public SearchResult<Room> getRoomsWithCurrentUsers(long user_level, int start,
			int max, String orderby, boolean asc) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				SearchResult<Room> sResult = new SearchResult<Room>();
				sResult.setRecords(this.selectMaxFromRooms(""));
				sResult.setObjectName(Room.class.getName());

				List<Room> rooms = this.getRoomsInternatl(start, max, orderby,
						asc);

				for (Room room : rooms) {
					room.setCurrentusers(sessionManager.getClientListByRoom(room.getRooms_id()));
				}

				sResult.setResult(rooms);
				return sResult;
			}
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}

	public List<Room> getRoomsWithCurrentUsersByList(long user_level,
			int start, int max, String orderby, boolean asc) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {

				List<Room> rooms = this.getRoomsInternatl(start, max, orderby,
						asc);

				for (Room room : rooms) {
					room.setCurrentusers(sessionManager.getClientListByRoom(room.getRooms_id()));
				}

				return rooms;

			}
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}

	public List<Room> getRoomsWithCurrentUsersByListAndType(long user_level,
			int start, int max, String orderby, boolean asc,
			String externalRoomType) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {

				List<Room> rooms = this.getRoomsInternatlbyType(start, max,
						orderby, asc, externalRoomType);

				for (Room room : rooms) {
					room.setCurrentusers(sessionManager.getClientListByRoom(room.getRooms_id()));
				}

				return rooms;

			}
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}

	public Long selectMaxFromRooms(String search) {
		try {
			if (search.length() == 0) {
				search = "%";
			} else {
				search = "%" + search + "%";
			}
			// get all users
			TypedQuery<Long> query = em.createNamedQuery("selectMaxFromRooms", Long.class);
			query.setParameter("search", search);
			List<Long> ll = query.getResultList();
			log.debug("Number of records" + ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromRooms] ", ex2);
		}
		return null;
	}

	/**
	 * gets a list of all availible rooms
	 * 
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<Room> getRoomsInternatl(int start, int max, String orderby,
			boolean asc) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Room> cq = cb.createQuery(Room.class);
			Root<Room> c = cq.from(Room.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			cq.where(condition);
			cq.distinct(asc);
			if (asc) {
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<Room> q = em.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<Room> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}

	/**
	 * gets a list of all availible rooms
	 * 
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<Room> getRoomsInternatlByHQL(int start, int max,
			String orderby, boolean asc, String search) {
		try {

			String hql = "select c from Room c "
					+ "where c.deleted <> true AND c.name LIKE :search ";

			if (search.length() == 0) {
				search = "%";
			} else {
				search = "%" + search + "%";
			}
			if (orderby != null) {
				hql += " ORDER BY " + (orderby.startsWith("c.") ? "" : "c.") + orderby;
	
				if (asc) {
					hql += " ASC";
				} else {
					hql += " DESC";
				}
			}
			TypedQuery<Room> query = em.createQuery(hql, Room.class);
			query.setParameter("search", search);
			query.setFirstResult(start);
			query.setMaxResults(max);

			return query.getResultList();

		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}

	public List<Room> getAllRooms() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Room> cq = cb.createQuery(Room.class);
			Root<Room> c = cq.from(Room.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			cq.where(condition);
			TypedQuery<Room> q = em.createQuery(cq);
			List<Room> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getAllRooms]", ex2);
		}
		return null;
	}

	public List<Room> getBackupRooms() {
		try {
			return em.createNamedQuery("getBackupRooms", Room.class)
					.getResultList();
		} catch (Exception e) {
			log.error("[getBackupRooms]", e);
		}
		return null;
	}

	public List<Room> getRoomsInternatlbyType(int start, int max,
			String orderby, boolean asc, String externalRoomType) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Room> cq = cb.createQuery(Room.class);
			Root<Room> c = cq.from(Room.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			Predicate subCondition = cb.equal(c.get("externalRoomType"),
					externalRoomType);
			cq.where(condition, subCondition);
			cq.distinct(asc);
			if (asc) {
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<Room> q = em.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<Room> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}

	public List<RoomOrganisation> getOrganisationsByRoom(long user_level,
			long rooms_id) {
		try {
			if (authLevelUtil.checkUserLevel(user_level)) {
				String hql = "select c from RoomOrganisation as c "
						+ "where c.room.rooms_id = :rooms_id "
						+ "AND c.deleted <> :deleted";
				TypedQuery<RoomOrganisation> q = em.createQuery(hql, RoomOrganisation.class);

				q.setParameter("rooms_id", rooms_id);
				q.setParameter("deleted", true);
				List<RoomOrganisation> ll = q.getResultList();
				return ll;
			}
		} catch (Exception ex2) {
			log.error("[getOrganisationsByRoom] ", ex2);
		}
		return null;
	}

	/**
	 * get all rooms which are availible for public
	 * 
	 * @param user_level
	 * @param roomtypes_id
	 * @return
	 */
	public List<Room> getPublicRooms(long user_level, long roomtypes_id) {
		try {
			if (authLevelUtil.checkUserLevel(user_level)) {
				TypedQuery<Room> q = em.createNamedQuery("getPublicRooms", Room.class);
				q.setParameter("ispublic", true);
				q.setParameter("deleted", false);
				q.setParameter("roomtypes_id", new Long(roomtypes_id));
				List<Room> ll = q.getResultList();
				return ll;
			}
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	public List<Room> getRoomsByIds(List<Integer> roomIds) {
		try {
			if (roomIds == null || roomIds.size() == 0) {
				return new LinkedList<Room>();
			}

			String queryString = "SELECT r from Room r " + "WHERE ";

			queryString += "(";

			int i = 0;
			for (Integer room_id : roomIds) {
				if (i != 0) {
					queryString += " OR ";
				}
				queryString += " r.rooms_id = " + room_id;
				i++;
			}

			queryString += ")";

			TypedQuery<Room> q = em.createQuery(queryString, Room.class);

			List<Room> ll = q.getResultList();

			return ll;

		} catch (Exception ex2) {
			log.error("[getRoomsByIds] ", ex2);
		}
		return null;
	}

	public List<Room> getPublicRoomsWithoutType(long user_level) {
		try {
			if (authLevelUtil.checkUserLevel(user_level)) {
				TypedQuery<Room> q = em.createNamedQuery("getPublicRoomsWithoutType", Room.class);
				q.setParameter("ispublic", true);
				q.setParameter("deleted", true);
				List<Room> ll = q.getResultList();
				return ll;
			}
		} catch (Exception ex2) {
			log.error("[getPublicRoomsWithoutType] ", ex2);
			ex2.printStackTrace();
		}
		return null;
	}

	/**
	 * Get Appointed Meetings
	 */
	// ---------------------------------------------------------------------------------------------
	public List<Room> getAppointedMeetings(Long userid, Long user_level) {
		log.debug("Roommanagement.getAppointedMeetings");

		try {
			if (authLevelUtil.checkUserLevel(user_level)) {
				TypedQuery<Room> q = em.createNamedQuery("getAppointedMeetings", Room.class);
				q.setParameter("appointed", true);
				q.setParameter("deleted", false);
				return q.getResultList();
			}
		} catch (Exception ex2) {
			log.error("[getAppointedMeetings] ", ex2);
		}
		return null;

	}

	// ---------------------------------------------------------------------------------------------

    /**
     * Returns number of SIP conference participants
     * @param rooms_id id of room
     * @return number of participants
     */
    public Integer getSipConferenceMembersNumber(Long rooms_id) {
    	Room r = roomDao.get(rooms_id);
    	return r == null || r.getConfno() == null ? null : sipDao.countUsers(r.getConfno());
    }

	/**
	 * adds a new Record to the table rooms
	 * @param name
	 * @param roomtypes_id
	 * @param ispublic
	 * @param hideActivitiesAndActions TODO
	 * @param hideFilesExplorer TODO
	 * @param hideActionsMenu TODO
	 * @param hideScreenSharing TODO
	 * @param hideWhiteboard TODO
	 * @return id of the newly created room or NULL
	 */
	public Long addRoom(long user_level, String name, long roomtypes_id,
			String comment, Long numberOfPartizipants, boolean ispublic,
			List<Integer> organisations, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom,
			List<Map<String, Object>> roomModerators,
			Boolean allowUserQuestions, Boolean isAudioOnly, Boolean allowFontStyles, Boolean isClosed,
			String redirectURL, String conferencePin,
			Long ownerId, Boolean waitForRecording, Boolean allowRecording,
			Boolean hideTopBar, Boolean hideChat, Boolean hideActivitiesAndActions, Boolean hideFilesExplorer, 
			Boolean hideActionsMenu, Boolean hideScreenSharing, Boolean hideWhiteboard,
			Boolean showMicrophoneStatus, Boolean chatModerated, boolean chatOpened
			, boolean filesOpened, boolean autoVideoSelect, boolean sipEnabled) {

		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {

				Room r = new Room();
				r.setName(name);
				r.setComment(comment);
				r.setStarttime(new Date());
				r.setNumberOfPartizipants(numberOfPartizipants);
				r.setRoomtype(this.getRoomTypesById(roomtypes_id));
				r.setIspublic(ispublic);
				r.setAllowUserQuestions(allowUserQuestions);
				r.setIsAudioOnly(isAudioOnly);
				r.setAllowFontStyles(allowFontStyles);

				r.setAppointment(appointment);

				r.setIsDemoRoom(isDemoRoom);
				r.setDemoTime(demoTime);

				r.setIsModeratedRoom(isModeratedRoom);
				r.setHideTopBar(hideTopBar);

				r.setDeleted(false);

				r.setIsClosed(isClosed);
				r.setRedirectURL(redirectURL);

				r.setOwnerId(ownerId);

				r.setWaitForRecording(waitForRecording);
				r.setAllowRecording(allowRecording);
				
				r.setHideChat(hideChat);
				r.setHideActivitiesAndActions(hideActivitiesAndActions);
				r.setHideActionsMenu(hideActionsMenu);
				r.setHideFilesExplorer(hideFilesExplorer);
				r.setHideScreenSharing(hideScreenSharing);	
				r.setHideWhiteboard(hideWhiteboard);
				r.setShowMicrophoneStatus(showMicrophoneStatus);
				r.setChatModerated(chatModerated);
				r.setChatOpened(chatOpened);
				r.setFilesOpened(filesOpened);
				r.setAutoVideoSelect(autoVideoSelect);
				r.setSipEnabled(sipEnabled);
				r.setPin(conferencePin);
				
				r = roomDao.update(r, ownerId);

				if (organisations != null) {
					Long t = this.updateRoomOrganisations(organisations, r);
					if (t == null)
						return null;
				}

				if (roomModerators != null) {
					roomModeratorsDao.addRoomModeratorByUserList(
							roomModerators, r.getRooms_id());
				}

				return r.getRooms_id();
			}
		} catch (Exception ex2) {
			log.error("[addRoom] ", ex2);
		}
		return null;
	}

	public Long addRoomByMod(long user_level, String name, long roomtypes_id,
			String comment, Long numberOfPartizipants, boolean ispublic,
			Long organisation_id, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom,
			List<Map<String, Object>> roomModerators, Boolean allowUserQuestions) {

		log.debug("addRoom");

		try {
			if (authLevelUtil.checkModLevel(user_level)) {
				Room r = new Room();
				r.setName(name);
				r.setComment(comment);
				r.setStarttime(new Date());
				r.setNumberOfPartizipants(numberOfPartizipants);
				r.setRoomtype(this.getRoomTypesById(roomtypes_id));
				r.setIspublic(ispublic);

				r.setAllowUserQuestions(allowUserQuestions);
				r.setAppointment(appointment);

				r.setIsDemoRoom(isDemoRoom);
				r.setDemoTime(demoTime);

				r.setIsModeratedRoom(isModeratedRoom);

				r.setDeleted(false);
				r = em.merge(r);
				long returnId = r.getRooms_id();

				this.addRoomToOrganisation(3, returnId, organisation_id);

				if (roomModerators != null) {
					roomModeratorsDao.addRoomModeratorByUserList(
							roomModerators, r.getRooms_id());
				}

				return returnId;
			}
		} catch (Exception ex2) {
			log.error("[addRoom] ", ex2);
		}
		return null;
	}

	/**
	 * adds/check a new Record to the table rooms with external fields
	 * 
	 * @param name
	 * @param roomtypes_id
	 * @param ispublic
	 * @return id of (the newly created) room or NULL
	 */
	public Long addExternalRoom(String name, long roomtypes_id, String comment,
			Long numberOfPartizipants, boolean ispublic, List<Integer> organisations,
			Boolean appointment, Boolean isDemoRoom, Integer demoTime,
			Boolean isModeratedRoom, List<Map<String, Object>> roomModerators,
			Long externalRoomId, String externalRoomType,
			Boolean allowUserQuestions, Boolean isAudioOnly, Boolean allowFontStyles, Boolean isClosed,
			String redirectURL, Boolean waitForRecording,
			Boolean allowRecording, Boolean hideTopBar) {

		log.debug("addExternalRoom");

		try {
			Room r = new Room();
			r.setName(name);
			r.setComment(comment);
			r.setStarttime(new Date());
			r.setNumberOfPartizipants(numberOfPartizipants);
			r.setRoomtype(this.getRoomTypesById(roomtypes_id));
			r.setIspublic(ispublic);

			r.setAllowUserQuestions(allowUserQuestions);
			r.setIsAudioOnly(isAudioOnly);
			r.setAllowFontStyles(allowFontStyles);

			r.setAppointment(appointment);

			r.setIsDemoRoom(isDemoRoom);
			r.setDemoTime(demoTime);

			r.setIsModeratedRoom(isModeratedRoom);

			r.setDeleted(false);

			r.setExternalRoomId(externalRoomId);
			r.setExternalRoomType(externalRoomType);

			r.setIsClosed(isClosed);
			r.setRedirectURL(redirectURL);

			r.setWaitForRecording(waitForRecording);
			r.setAllowRecording(allowRecording);

			r.setHideTopBar(hideTopBar);

			r = em.merge(r);

			long returnId = r.getRooms_id();

			if (organisations != null) {
				Long t = this.updateRoomOrganisations(organisations, r);
				if (t == null)
					return null;
			}

			if (roomModerators != null) {
				roomModeratorsDao.addRoomModeratorByUserList(roomModerators,
						r.getRooms_id());
			}

			return returnId;
		} catch (Exception ex2) {
			log.error("[addExternalRoom] ", ex2);
		}
		return null;
	}

	/**
	 * adds a new record to the table rooms_organisation
	 * 
	 * @param rooms_id
	 * @param organisation_id
	 * @return the id of the newly created Rooms_Organisation or NULL
	 */
	public Long addRoomToOrganisation(long user_level, long rooms_id,
			long organisation_id) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				RoomOrganisation rOrganisation = new RoomOrganisation();
				rOrganisation.setRoom(roomDao.get(rooms_id));
				log.debug("addRoomToOrganisation rooms '"
						+ rOrganisation.getRoom().getName() + "'");
				rOrganisation.setStarttime(new Date());
				rOrganisation.setOrganisation(organisationManager
						.getOrganisationById(organisation_id));
				rOrganisation.setDeleted(false);

				rOrganisation = em.merge(rOrganisation);
				long returnId = rOrganisation.getRooms_organisation_id();
				return returnId;
			}
		} catch (Exception ex2) {
			log.error("[addRoomToOrganisation] ", ex2);
		}
		return null;
	}

	public Long addRoomOrganisation(RoomOrganisation rOrganisation) {
		try {

			rOrganisation.setStarttime(new Date());

			rOrganisation = em.merge(rOrganisation);
			long returnId = rOrganisation.getRooms_organisation_id();
			return returnId;

		} catch (Exception ex2) {
			log.error("[addRoomOrganisation] ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param rooms_organisation_id
	 * @return
	 */
	public RoomOrganisation getRoomsOrganisationById(
			long rooms_organisation_id) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RoomOrganisation> cq = cb
					.createQuery(RoomOrganisation.class);
			Root<RoomOrganisation> c = cq.from(RoomOrganisation.class);
			Predicate condition = cb.equal(c.get("rooms_organisation_id"),
					rooms_organisation_id);
			cq.where(condition);
			TypedQuery<RoomOrganisation> q = em.createQuery(cq);
			List<RoomOrganisation> ll = q.getResultList();

			if (ll.size() > 0) {
				return ll.get(0);
			}
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	/**
	 * get List of Rooms_Organisation by organisation and roomtype
	 * 
	 * @param user_level
	 * @param organisation_id
	 * @param roomtypes_id
	 * @return
	 */
	public List<RoomOrganisation> getRoomsOrganisationByOrganisationIdAndRoomType(
			long user_level, long organisation_id, long roomtypes_id) {
		try {
			if (authLevelUtil.checkUserLevel(user_level)) {
				TypedQuery<RoomOrganisation> q = em.
						createNamedQuery("getRoomsOrganisationByOrganisationIdAndRoomType", RoomOrganisation.class);
				q.setParameter("roomtypes_id", roomtypes_id);
				q.setParameter("organisation_id", organisation_id);
				q.setParameter("deleted", true);
				return q.getResultList();
			} else {
				log.error("[notauthentificated] " + user_level);
			}
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	/**
	 * Gets all rooms by an organisation
	 * 
	 * @param organisation_id
	 * @return list of Rooms_Organisation with Rooms as Sub-Objects or null
	 */
	public List<RoomOrganisation> getRoomsOrganisationByOrganisationId(
			long user_level, long organisation_id) {
		try {
			if (authLevelUtil.checkUserLevel(user_level)) {

				TypedQuery<RoomOrganisation> query = em.
						createNamedQuery("getRoomsOrganisationByOrganisationId", RoomOrganisation.class);

				query.setParameter("organisation_id", organisation_id);
				query.setParameter("deleted", true);

				List<RoomOrganisation> ll = query.getResultList();

				return ll;
			} else {
				log.error("[notauthentificated] " + user_level);
			}
		} catch (Exception ex2) {
			log.error("[getPublicRoomsWithoutType] ", ex2);
			ex2.printStackTrace();
		}
		return null;
	}

	public SearchResult<RoomOrganisation> getRoomsOrganisationByOrganisationId(long user_level,
			long organisation_id, int start, int max, String orderby,
			boolean asc) {
		try {
			if (authLevelUtil.checkModLevel(user_level)) {

				SearchResult<RoomOrganisation> sResult = new SearchResult<RoomOrganisation>();
				sResult.setObjectName(RoomOrganisation.class.getName());
				sResult.setRecords(this.selectMaxFromRoomsByOrganisation(
						organisation_id).longValue());
				sResult.setResult(this.getRoomsOrganisationByOrganisationId(
						organisation_id, start, max, orderby, asc));
				return sResult;
			}
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	public Integer selectMaxFromRoomsByOrganisation(long organisation_id) {
		try {
			// get all users
			TypedQuery<RoomOrganisation> q = em.createNamedQuery("selectMaxFromRoomsByOrganisation", RoomOrganisation.class);

			q.setParameter("organisation_id", organisation_id);
			q.setParameter("deleted", true);
			List<RoomOrganisation> ll = q.getResultList();

			return ll.size();
		} catch (Exception ex2) {
			log.error("[selectMaxFromRooms] ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param organisation_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	private List<RoomOrganisation> getRoomsOrganisationByOrganisationId(
			long organisation_id, int start, int max, String orderby,
			boolean asc) {
		try {
			String hql = "select c from RoomOrganisation as c "
					+ "where c.organisation.organisation_id = :organisation_id "
					+ "AND c.deleted <> :deleted";
			if (orderby.startsWith("c.")) {
				hql += "ORDER BY " + orderby;
			} else {
				hql += "ORDER BY " + "c." + orderby;
			}
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			TypedQuery<RoomOrganisation> q = em.createQuery(hql, RoomOrganisation.class);

			q.setParameter("organisation_id", organisation_id);
			q.setParameter("deleted", true);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<RoomOrganisation> ll = q.getResultList();

			return ll;
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	private RoomOrganisation getRoomsOrganisationByOrganisationIdAndRoomId(
			long organisation_id, long rooms_id) {
		try {
			TypedQuery<RoomOrganisation> q = em.
					createNamedQuery("getRoomsOrganisationByOrganisationIdAndRoomId", RoomOrganisation.class);

			q.setParameter("rooms_id", rooms_id);
			q.setParameter("organisation_id", organisation_id);
			q.setParameter("deleted", true);
			List<RoomOrganisation> ll = q.getResultList();

			if (ll.size() > 0) {
				return ll.get(0);
			}
		} catch (Exception ex2) {
			log.error("[getRoomsOrganisationByOrganisationIdAndRoomId] ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param organisation_id
	 * @return
	 */
	public List<RoomOrganisation> getRoomsOrganisationByRoomsId(long rooms_id) {
		try {
			TypedQuery<RoomOrganisation> q = em.createNamedQuery("getRoomsOrganisationByRoomsId", RoomOrganisation.class);
			q.setParameter("rooms_id", rooms_id);
			q.setParameter("deleted", true);
			return q.getResultList();
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	public List<RoomOrganisation> getRoomsOrganisations() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RoomOrganisation> cq = cb
					.createQuery(RoomOrganisation.class);
			cq.from(RoomOrganisation.class);
			TypedQuery<RoomOrganisation> q = em.createQuery(cq);
			List<RoomOrganisation> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param user_id
	 * @param rooms_id
	 * @return
	 */
	private boolean checkUserOrgRoom(long user_id, long rooms_id) {
		try {

			User us = usersDao.get(user_id);
			List<Organisation_Users> s = us.getOrganisation_users();

			for (Iterator<Organisation_Users> it = s.iterator(); it.hasNext();) {
				Organisation_Users orgUsers = it.next();
				long organisation_id = orgUsers.getOrganisation()
						.getOrganisation_id();
				List<RoomOrganisation> ll = this
						.getRoomsOrganisationByOrganisationId(3,
								organisation_id);
				for (Iterator<RoomOrganisation> it2 = ll.iterator(); it2
						.hasNext();) {
					RoomOrganisation roomOrg = it2.next();
					if (roomOrg.getRoom().getRooms_id() == rooms_id) {
						return true;
					}
				}
			}

		} catch (Exception ex2) {
			log.error("[checkUserOrgRoom] ", ex2);
		}
		return false;
	}

	/**
	 * 
	 * @param user_id
	 * @param user_level
	 * @param rooms_id
	 * @param roomtypes_id
	 * @param name
	 * @param ispublic
	 * @param comment
	 * @return
	 */
	public Room updateRoomsSelf(long user_id, long user_level, long rooms_id,
			long roomtypes_id, String name, boolean ispublic, String comment) {
		try {
			if (authLevelUtil.checkModLevel(user_level)) {

				if (this.checkUserOrgRoom(user_id, rooms_id)) {

					Room r = roomDao.get(rooms_id);
					r.setComment(comment);
					r.setIspublic(ispublic);
					r.setName(name);
					r.setRoomtype(this.getRoomTypesById(roomtypes_id));
					r.setUpdatetime(new Date());

					if (r.getRooms_id() == null) {
						em.persist(r);
					} else {
						if (!em.contains(r)) {
							em.merge(r);
						}
					}
				}
			}
		} catch (Exception ex2) {
			log.error("[updateRoom] ", ex2);
		}
		return null;
	}

	/**
	 * Update a Record in the rooms table
	 * @param rooms_id
	 * @param roomtypes_id
	 * @param name
	 * @param ispublic
	 * @param comment
	 * @param hideActivitiesAndActions TODO
	 * @param hideFilesExplorer TODO
	 * @param hideActionsMenu TODO
	 * @param hideScreenSharing TODO
	 * @param hideWhiteboard TODO
	 * @return
	 */
	public Long updateRoom(long user_level, long rooms_id, long roomtypes_id,
			String name, boolean ispublic, String comment,
			Long numberOfPartizipants, List<Integer> organisations,
			Boolean appointment, Boolean isDemoRoom, Integer demoTime,
			Boolean isModeratedRoom, List<Map<String, Object>> roomModerators,
			Boolean allowUserQuestions, Boolean isAudioOnly, Boolean allowFontStyles, Boolean isClosed,
			String redirectURL, String conferencePin,
			Long ownerId, Boolean waitForRecording, Boolean allowRecording,
			Boolean hideTopBar, Boolean hideChat, Boolean hideActivitiesAndActions, 
			Boolean hideFilesExplorer, Boolean hideActionsMenu, Boolean hideScreenSharing, Boolean hideWhiteboard,
			Boolean showMicrophoneStatus, Boolean chatModerated, boolean chatOpened, boolean filesOpened
			, boolean autoVideoSelect, boolean sipEnabled) {
		try {

			log.debug("*** updateRoom numberOfPartizipants: "
					+ numberOfPartizipants);
			if (authLevelUtil.checkAdminLevel(user_level)) {

				return this.updateRoomInternal(rooms_id, roomtypes_id, name,
						ispublic, comment, numberOfPartizipants, organisations,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						roomModerators, allowUserQuestions, isAudioOnly, allowFontStyles,
						isClosed, redirectURL, conferencePin,
						ownerId, waitForRecording, allowRecording, hideTopBar, hideChat, 
						hideActivitiesAndActions, hideFilesExplorer, hideActionsMenu, 
						hideScreenSharing, hideWhiteboard, showMicrophoneStatus, chatModerated
						, chatOpened, filesOpened, autoVideoSelect, sipEnabled);

			}

		} catch (Exception ex2) {
			log.error("[updateRoom] ", ex2);
		}
		return null;
	}

	public Long updateRoomInternal(long rooms_id, long roomtypes_id,
			String name, boolean ispublic, String comment,
			Long numberOfPartizipants, List<Integer> organisations,
			Boolean appointment, Boolean isDemoRoom, Integer demoTime,
			Boolean isModeratedRoom, List<Map<String, Object>> roomModerators,
			Boolean allowUserQuestions, Boolean isAudioOnly, Boolean allowFontStyles, Boolean isClosed,
			String redirectURL, String conferencePin,
			Long ownerId, Boolean waitForRecording, Boolean allowRecording,
			Boolean hideTopBar, Boolean hideChat, Boolean hideActivitiesAndActions, Boolean hideFilesExplorer, 
			Boolean hideActionsMenu, Boolean hideScreenSharing, Boolean hideWhiteboard, 
			Boolean showMicrophoneStatus, Boolean chatModerated, boolean chatOpened, boolean filesOpened
			, boolean autoVideoSelect, boolean sipEnabled) {
		try {
			log.debug("*** updateRoom numberOfPartizipants: "
					+ numberOfPartizipants);
			Room r = roomDao.get(rooms_id);
			r.setComment(comment);

			r.setIspublic(ispublic);
			r.setNumberOfPartizipants(numberOfPartizipants);
			r.setName(name);
			r.setRoomtype(this.getRoomTypesById(roomtypes_id));
			r.setUpdatetime(new Date());
			r.setAllowUserQuestions(allowUserQuestions);
			r.setIsAudioOnly(isAudioOnly);
			r.setAllowFontStyles(allowFontStyles);

			r.setIsDemoRoom(isDemoRoom);
			r.setDemoTime(demoTime);

			r.setAppointment(appointment);

			r.setIsModeratedRoom(isModeratedRoom);
			r.setHideTopBar(hideTopBar);

			r.setIsClosed(isClosed);
			r.setRedirectURL(redirectURL);
			r.setOwnerId(ownerId);
			r.setWaitForRecording(waitForRecording);
			r.setAllowRecording(allowRecording);
			
			r.setHideChat(hideChat);
			r.setHideActivitiesAndActions(hideActivitiesAndActions);
			r.setHideActionsMenu(hideActionsMenu);
			r.setHideFilesExplorer(hideFilesExplorer);
			r.setHideScreenSharing(hideScreenSharing);
			r.setHideWhiteboard(hideWhiteboard);
			r.setShowMicrophoneStatus(showMicrophoneStatus);
			r.setChatModerated(chatModerated);
			r.setChatOpened(chatOpened);
			r.setFilesOpened(filesOpened);
			r.setAutoVideoSelect(autoVideoSelect);
			r.setPin(conferencePin);
			r.setSipEnabled(sipEnabled);
			roomDao.update(r, ownerId);

			if (organisations != null) {
				Long t = this.updateRoomOrganisations(organisations, r);
				if (t == null)
					return null;
			}
			if (roomModerators != null) {
				roomModeratorsDao.updateRoomModeratorByUserList(roomModerators,
						r.getRooms_id());
			}

			return r.getRooms_id();
		} catch (Exception ex2) {
			log.error("[updateRoom] ", ex2);
		}
		return null;
	}

	public Long updateRoomByMod(long user_level, long rooms_id,
			long roomtypes_id, String name, boolean ispublic, String comment,
			Long numberOfPartizipants, Long organisations, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			List<Map<String, Object>> roomModerators, Boolean allowUserQuestions) {
		try {
			log.debug("*** updateRoom numberOfPartizipants: "
					+ numberOfPartizipants);
			if (authLevelUtil.checkModLevel(user_level)) {
				Room r = roomDao.get(rooms_id);
				r.setComment(comment);

				r.setIspublic(ispublic);
				r.setNumberOfPartizipants(numberOfPartizipants);
				r.setName(name);
				r.setRoomtype(this.getRoomTypesById(roomtypes_id));
				r.setUpdatetime(new Date());
				r.setAllowUserQuestions(allowUserQuestions);

				r.setIsDemoRoom(isDemoRoom);
				r.setDemoTime(demoTime);

				r.setAppointment(appointment);

				r.setIsModeratedRoom(isModeratedRoom);

				if (r.getRooms_id() == null) {
					em.persist(r);
				} else {
					if (!em.contains(r)) {
						em.merge(r);
					}
				}

				// FIXME: Organizations will not be changed when you do an
				// update as Moderator

				if (roomModerators != null) {
					roomModeratorsDao.updateRoomModeratorByUserList(
							roomModerators, r.getRooms_id());
				}

				return r.getRooms_id();
			}
		} catch (Exception ex2) {
			log.error("[updateRoom] ", ex2);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private boolean checkRoomAlreadyInOrg(Long orgid, List organisations)
			throws Exception {
		for (Iterator it = organisations.iterator(); it.hasNext();) {
			RoomOrganisation rOrganisation = (RoomOrganisation) it.next();
			if (rOrganisation.getOrganisation().getOrganisation_id()
					.equals(orgid))
				return true;
		}
		return false;
	}

	private boolean checkRoomShouldByDeleted(long orgId, List<Integer> organisations) throws Exception {
		for (Iterator<Integer> it = organisations.iterator(); it.hasNext();) {
			Integer key = it.next();
			Long storedOrgId = key.longValue();
			if (storedOrgId.equals(orgId))
				return true;
		}
		return false;
	}

	private Long updateRoomOrganisations(List<Integer> organisations, Room room)
			throws Exception {
		List<RoomOrganisation> roomOrganisations = this.getOrganisationsByRoom(3,
				room.getRooms_id());

		List<Long> roomsToAdd = new LinkedList<Long>();
		List<Long> roomsToDel = new LinkedList<Long>();

		for (Iterator<Integer> it = organisations.iterator(); it.hasNext();) {
			Integer key = it.next();
			Long orgIdToAdd = key.longValue();
			if (!this.checkRoomAlreadyInOrg(orgIdToAdd, roomOrganisations))
				roomsToAdd.add(orgIdToAdd);
		}

		for (Iterator<RoomOrganisation> it = roomOrganisations.iterator(); it.hasNext();) {
			RoomOrganisation rOrganisation = it.next();
			Long orgIdToDel = rOrganisation.getOrganisation()
					.getOrganisation_id();
			if (!this.checkRoomShouldByDeleted(orgIdToDel, organisations))
				roomsToDel.add(orgIdToDel);
		}

		// log.error("updateRoomOrganisations roomsToAdd: "+roomsToAdd.size());
		// log.error("updateRoomOrganisations roomsToDel: "+roomsToDel.size());

		for (Iterator<Long> it = roomsToAdd.iterator(); it.hasNext();) {
			Long orgIdToAdd = it.next();
			this.addRoomToOrganisation(3, room.getRooms_id(), orgIdToAdd);
		}
		for (Iterator<Long> it = roomsToDel.iterator(); it.hasNext();) {
			Long orgToDel = it.next();
			this.deleteRoomFromOrganisationByRoomAndOrganisation(
					room.getRooms_id(), orgToDel);
		}

		return new Long(1);
	}

	/**
	 * delete all Rooms_Organisations and Room by a given room_id
	 * 
	 * @param rooms_id
	 */
	public Long deleteRoomById(long user_level, long rooms_id) {
		try {
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				this.deleteAllRoomsOrganisationOfRoom(rooms_id);
				roomDao.delete(roomDao.get(rooms_id), -1L);
				return rooms_id;
			}
		} catch (Exception ex2) {
			log.error("[deleteRoomById] ", ex2);
		}
		return null;
	}

	/**
	 * delete all Rooms_Organisation by a rooms_id
	 * 
	 * @param rooms_id
	 */
	@SuppressWarnings("rawtypes")
	public void deleteAllRoomsOrganisationOfRoom(long rooms_id) {
		try {
			List ll = this.getRoomsOrganisationByRoomsId(rooms_id);
			for (Iterator it = ll.iterator(); it.hasNext();) {
				RoomOrganisation rOrg = (RoomOrganisation) it.next();
				this.deleteRoomsOrganisation(rOrg);
			}
		} catch (Exception ex2) {
			log.error("[deleteAllRoomsOrganisationOfRoom] ", ex2);
		}
	}

	/**
	 * Delete all room of a given Organisation
	 * 
	 * @param organisation_id
	 */
	@SuppressWarnings("rawtypes")
	public void deleteAllRoomsOrganisationOfOrganisation(long organisation_id) {
		try {
			List ll = this.getRoomsOrganisationByOrganisationId(3,
					organisation_id);
			for (Iterator it = ll.iterator(); it.hasNext();) {
				RoomOrganisation rOrg = (RoomOrganisation) it.next();
				this.deleteRoomsOrganisation(rOrg);
			}
		} catch (Exception ex2) {
			log.error("[deleteAllRoomsOfOrganisation] ", ex2);
		}
	}

	/**
	 * Delete a Rooms_Organisation by its id
	 * 
	 * @param rooms_organisation_id
	 */
	public Long deleteRoomsOrganisationByID(long rooms_organisation_id) {
		try {
			RoomOrganisation rOrg = this
					.getRoomsOrganisationById(rooms_organisation_id);
			return this.deleteRoomsOrganisation(rOrg);
		} catch (Exception ex2) {
			log.error("[deleteRoomsOrganisationByID] ", ex2);
		}
		return null;
	}

	private Long deleteRoomFromOrganisationByRoomAndOrganisation(long rooms_id,
			long organisation_id) {
		try {
			RoomOrganisation rOrganisation = this
					.getRoomsOrganisationByOrganisationIdAndRoomId(
							organisation_id, rooms_id);
			return this.deleteRoomsOrganisation(rOrganisation);
		} catch (Exception ex2) {
			log.error("[deleteRoomFromOrganisationByRoomAndOrganisation] ", ex2);
		}
		return null;
	}

	/**
	 * delete a Rooms_Organisation-Object
	 * 
	 * @param rOrg
	 */
	public Long deleteRoomsOrganisation(RoomOrganisation rOrg) {
		try {
			rOrg.setDeleted(true);
			rOrg.setUpdatetime(new Date());
			if (rOrg.getRooms_organisation_id() == null) {
				em.persist(rOrg);
			} else {
				if (!em.contains(rOrg)) {
					em.merge(rOrg);
				}
			}
			return rOrg.getRooms_organisation_id();
		} catch (Exception ex2) {
			log.error("[deleteRoomsOrganisation] ", ex2);
		}
		return null;
	}

	// --------------------------------------------------------------------------------------------

	public void closeRoom(Long rooms_id, Boolean status) {
		try {

			Room room = roomDao.get(rooms_id);

			room.setIsClosed(status);

			roomDao.update(room, -1L);

		} catch (Exception e) {
			log.error("Error updateRoomObject : ", e);
		}
	}

	/**
	 * Get a Rooms-Object or NULL
	 * 
	 * @param rooms_id
	 * @return Rooms-Object or NULL
	 */
	public Room getRoomByOwnerAndTypeId(Long ownerId, Long roomtypesId,
			String roomName) {
		try {

			if (roomtypesId == null || roomtypesId == 0) {
				return null;
			}
			log.debug("getRoomByOwnerAndTypeId : " + ownerId + " || "
					+ roomtypesId);
			Room room = null;
			TypedQuery<Room> query = em.createNamedQuery("getRoomByOwnerAndTypeId", Room.class);
			query.setParameter("ownerId", ownerId);
			query.setParameter("roomtypesId", roomtypesId);
			query.setParameter("deleted", true);
			List<Room> ll = query.getResultList();
			if (ll.size() > 0) {
				room = ll.get(0);
			}

			if (room != null) {
				return room;
			} else {
				log.debug("Could not find room " + ownerId + " || "
						+ roomtypesId);
				
				Long rooms_id = this.addRoom(3L, roomName, roomtypesId,
						"My Rooms of ownerId " + ownerId,
						(roomtypesId == 1) ? 25L : 150L, // numberOfPartizipants
						false, // ispublic
						null, // organisations
						false, // appointment
						false, // isDemoRoom
						null, // demoTime
						false, // isModeratedRoom
						null, // roomModerators
						true, // allowUserQuestions
						false, // isAudioOnly
						true, // allowFontStyle
						false, // isClosed
						"", // redirectURL
						"", // conferencePin
						ownerId, null, null, 
						false, // hideTopBar
						false, // hideChat
						false, // hideActivitiesAndActions
						false, // hideFilesExplorer
						false, // hideActionsMenu
						false, // hideScreenSharing 
						false, // hideWhiteboard
						false, //showMicrophoneStatus
						false, // chatModerated
						false, //chatOpened
						false, //filesOpened
						false, //autoVideoSelect
						false //sipEnabled
						);

				if (rooms_id != null) {
					return roomDao.get(rooms_id);
				}
			}
		} catch (Exception ex2) {
			log.error("[getRoomByOwnerAndTypeId] ", ex2);
		}
		return null;
	}

}
