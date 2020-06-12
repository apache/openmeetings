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
package org.apache.openmeetings.db.dao.room;

import static org.apache.openmeetings.db.util.DaoHelper.fillLazy;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.db.util.DaoHelper.single;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SIP_ROOM_PREFIX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.RECENT_ROOMS_COUNT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSipEnabled;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IGroupAdminDataProviderDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.Room.Type;
import org.apache.openmeetings.db.entity.room.RoomFile;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RoomDao implements IGroupAdminDataProviderDao<Room> {
	private static final Logger log = LoggerFactory.getLogger(RoomDao.class);
	private static final String[] searchFields = {"name"};

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private SipDao sipDao;
	@Autowired
	private UserDao userDao;

	@Override
	public Room get(Long id) {
		Room r = null;
		if (id != null && id.longValue() > 0) {
			r = single(fillLazy(em
					, oem -> oem.createNamedQuery("getRoomById", Room.class)
						.setParameter("id", id)
					, "roomModerators", "roomGroups", "roomFiles"));
		} else {
			log.info("[get]: No room id given");
		}
		return r;
	}

	public Room get(String tag) {
		Room r = null;
		if (!Strings.isEmpty(tag)) {
			r = single(fillLazy(em
					, oem -> oem.createNamedQuery("getRoomByTag", Room.class).setParameter("tag", tag)
					, "roomModerators", "roomGroups", "roomFiles"));
		} else {
			log.info("[get]: No room tag given");
		}
		return r;
	}

	public List<Room> get() {
		return fillLazy(em
				, oem -> oem.createNamedQuery("getBackupRooms", Room.class)
				, "roomModerators", "roomGroups", "roomFiles");
	}

	public List<Room> get(List<Long> ids) {
		return em.createNamedQuery("getRoomsByIds", Room.class).setParameter("ids", ids).getResultList();
	}

	@Override
	public List<Room> get(long start, long count) {
		return setLimits(em.createNamedQuery("getNondeletedRooms", Room.class)
				, start, count).getResultList();
	}

	@Override
	public List<Room> get(String search, long start, long count, String sort) {
		return setLimits(em.createQuery(DaoHelper.getSearchQuery("Room", "r", search, true, false, sort, searchFields), Room.class)
				, start, count).getResultList();
	}

	@Override
	public List<Room> adminGet(String search, Long adminId, long start, long count, String order) {
		return setLimits(em.createQuery(DaoHelper.getSearchQuery("RoomGroup rg, IN(rg.room)", "r", null, search, true, true, false
				, "rg.group.id IN (SELECT gu1.group.id FROM GroupUser gu1 WHERE gu1.moderator = true AND gu1.user.id = :adminId)", order, searchFields), Room.class)
					.setParameter("adminId", adminId)
				, start, count).getResultList();
	}

	@Override
	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countRooms", Long.class);
		return q.getSingleResult();
	}

	@Override
	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Room", "r", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	@Override
	public long adminCount(String search, Long adminId) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("RoomGroup rg, IN(rg.room)", "r", null, search, true, true, true
				, "rg.group.id IN (SELECT gu1.group.id FROM GroupUser gu1 WHERE gu1.moderator = true AND gu1.user.id = :adminId)", null, searchFields), Long.class);
		q.setParameter("adminId", adminId);
		return q.getSingleResult();
	}

	public List<Room> getPublicRooms() {
		return em.createNamedQuery("getPublicRoomsOrdered", Room.class).getResultList();
	}

	public List<Room> getPublicRooms(Type type) {
		return type == null ? getPublicRooms()
				: em.createNamedQuery("getPublicRooms", Room.class).setParameter("type", type).getResultList();
	}

	public List<Long> getSipRooms(List<Long> ids) {
		TypedQuery<Long> q = em.createNamedQuery("getSipRoomIdsByIds", Long.class);
		q.setParameter("ids", ids);
		return q.getResultList();
	}

	public List<Room> getGroupRooms(long groupId) {
		TypedQuery<Room> q = em.createNamedQuery("getGroupRooms", Room.class);
		q.setParameter("groupId", groupId);
		return q.getResultList();
	}

	public List<Room> getAppointedRoomsByUser(long userId) {
		log.debug("getAppointedRoomsByUser : UserID - {}", userId);

		TimeZone timeZone = getTimeZone(userDao.get(userId));

		Calendar startCal = Calendar.getInstance(timeZone);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.HOUR, 0);
		startCal.set(Calendar.SECOND, 1);

		Calendar endCal = Calendar.getInstance(timeZone);
		endCal.set(Calendar.MINUTE, 23);
		endCal.set(Calendar.HOUR, 59);
		endCal.set(Calendar.SECOND, 59);

		return em.createNamedQuery("appointedRoomsInRangeByUser", Room.class)
				.setParameter(PARAM_USER_ID, userId)
				.setParameter("start", startCal.getTime())
				.setParameter("end", endCal.getTime())
				.getResultList();
	}

	private String getSipNumber(long roomId) {
		if (isSipEnabled()) {
			return cfgDao.getString(CONFIG_SIP_ROOM_PREFIX, "400") + roomId;
		}
		return null;
	}

	@Override
	public Room update(Room entity, Long userId) {
		if (entity.getId() == null) {
			entity.setInserted(new Date());
			em.persist(entity);
		} else {
			entity.setUpdated(new Date());
		}
		if (entity.isSipEnabled() && isSipEnabled()) {
			String sipNumber = getSipNumber(entity.getId());
			if (sipNumber != null && !sipNumber.equals(entity.getConfno())) {
				entity.setConfno(sipNumber);
			}
			sipDao.update(sipNumber, entity.getPin());
		} else {
			sipDao.delete(entity.getConfno());
			entity.setConfno(null);
			entity.setPin(null);
		}
		entity = em.merge(entity);
		return entity;
	}

	@Override
	public void delete(Room entity, Long userId) {
		entity.setDeleted(true);
		entity.setSipEnabled(false);
		update(entity, userId);
	}

	public Room getUserRoom(Long ownerId, Room.Type type, String name) {
		log.debug("getUserRoom : {} || {}", ownerId, type);
		Room room = null;
		List<Room> ll = em.createNamedQuery("getRoomByOwnerAndTypeId", Room.class).setParameter("ownerId", ownerId).setParameter("type", type).getResultList();
		if (!ll.isEmpty()) {
			room = ll.get(0);
		}

		if (room == null) {
			log.debug("Could not find room {} || {}", ownerId, type);

			room = new Room();
			room.setName(name);
			room.setType(type);
			room.setComment("My Rooms of ownerId " + ownerId);
			room.setCapacity(Room.Type.CONFERENCE == type ? 25L : 120L);
			room.setAllowUserQuestions(true);
			room.setOwnerId(ownerId);
			room.setAllowRecording(true);
			room.hide(RoomElement.MICROPHONE_STATUS);

			room = update(room, ownerId);
			if (room.getId() != null) {
				return room;
			}
			return null;
		} else {
			return room;
		}
	}

	public Room getExternal(Type type, String externalType, String externalId) {
		log.debug("getExternal : {} - {}  - {}", type, externalType, externalId);
		return single(fillLazy(em
				, oem -> oem.createNamedQuery("getExternalRoom", Room.class)
					.setParameter("externalId", externalId)
					.setParameter("externalType", externalType)
					.setParameter("type", type)
				, "roomGroups"));
	}

	public List<Room> getRecent(Long userId) {
		List<Room> result = new ArrayList<>();
		Set<Long> ids = new HashSet<>();
		//(RECENT_ROOMS_COUNT + 1) passes required to preserve the order :(
		for (ConferenceLog l : em.createNamedQuery("getLogRecentRooms", ConferenceLog.class)
				.setParameter("roomEnter", ConferenceLog.Type.ROOM_ENTER)
				.setParameter(PARAM_USER_ID, userId)
				.getResultList())
		{
			if (!ids.contains(l.getRoomId())) {
				Room r = get(l.getRoomId());
				if (r != null && !r.isDeleted()) {
					result.add(r);
					ids.add(r.getId());
				}
			}
			if (ids.size() == RECENT_ROOMS_COUNT) {
				break;
			}
		}
		return result;
	}

	// Methods for backup export
	public List<RoomFile> getFiles() {
		return em.createQuery("SELECT rf FROM RoomFile rf", RoomFile.class)
				.getResultList();
	}

	public List<RoomGroup> getGroups() {
		return em.createNamedQuery("getAllRoomGroups", RoomGroup.class).getResultList();
	}

	public List<Room> getMyRooms(Long userId, String confLbl, String restrLbl) {
		List<Room> result = new ArrayList<>();
		result.add(getUserRoom(userId, Room.Type.CONFERENCE, confLbl));
		result.add(getUserRoom(userId, Room.Type.PRESENTATION, restrLbl));
		result.addAll(getAppointedRoomsByUser(userId));
		return result;
	}
}
