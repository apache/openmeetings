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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.DaoHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomDao implements IDataProviderDao<Room> {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomDao.class, webAppRootKey);
	public final static String[] searchFields = {"name"};
	
	@PersistenceContext
	private EntityManager em;
    @Autowired
	private ConfigurationDao cfgDao;
    @Autowired
    private SipDao sipDao;
	@Autowired
	private UserDao usersDao;
	@Autowired
	private TimezoneUtil timezoneUtil;

	public Room get(long id) {
		TypedQuery<Room> q = em.createNamedQuery("getRoomById", Room.class);
		q.setParameter("id", id);
		List<Room> l = q.getResultList();
		return l.isEmpty() ? null : l.get(0);
	}

	public List<Room> get() {
		return em.createNamedQuery("getBackupRooms", Room.class).getResultList();
	}
	
	public List<Room> get(int start, int count) {
		TypedQuery<Room> q = em.createNamedQuery("getNondeletedRooms", Room.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<Room> get(String search, int start, int count, String sort) {
		TypedQuery<Room> q = em.createQuery(DaoHelper.getSearchQuery("Room", "r", search, true, false, sort, searchFields), Room.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countRooms", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Room", "r", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	public List<Room> getPublicRooms() {
		return em.createNamedQuery("getPublicRoomsOrdered", Room.class).getResultList();
	}
	
	public List<Long> getSipRooms(List<Long> ids) {
		TypedQuery<Long> q = em.createNamedQuery("getSipRoomIdsByIds", Long.class);
		q.setParameter("ids", ids);
		return q.getResultList();
	}

	public List<Room> getOrganisationRooms(long orgId) {
		TypedQuery<Room> q = em.createNamedQuery("getOrganisationRooms", Room.class);
		q.setParameter("orgId", orgId);
		return q.getResultList();
	}

	public List<Room> getAppointedRoomsByUser(long userId) {
		//TODO generalize with AppointmentDao
		log.debug("getAppointedRoomsByUser : UserID - " + userId);

		TimeZone timeZone = timezoneUtil.getTimeZone(usersDao.get(userId));

		Calendar startCal = Calendar.getInstance(timeZone);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.HOUR, 0);
		startCal.set(Calendar.SECOND, 1);

		Calendar endCal = Calendar.getInstance(timeZone);
		endCal.set(Calendar.MINUTE, 23);
		endCal.set(Calendar.HOUR, 59);
		endCal.set(Calendar.SECOND, 59);

		return em.createNamedQuery("appointedRoomsInRangeByUser", Room.class)
				.setParameter("userId", userId)
				.setParameter("starttime", startCal.getTime())
				.setParameter("endtime", endCal.getTime())
				.getResultList();
	}
	
	public Long getRoomsCapacityByIds(List<Long> ids) {
		return ids == null || ids.isEmpty() ? 0
			: em.createNamedQuery("getRoomsCapacityByIds", Long.class).setParameter("ids", ids).getSingleResult();
	}
	
	private boolean isSipEnabled() {
		return "yes".equals(cfgDao.getConfValue("red5sip.enable", String.class, "no"));
	}
	
	private String getSipNumber(long roomId) {
        if (isSipEnabled()) {
        	return cfgDao.getConfValue("red5sip.room_prefix", String.class, "400") + roomId;
        }
        return null;
	}
	
	public Room update(Room entity, Long userId) {
		if (entity.getRooms_id() == null) {
			entity.setStarttime(new Date());
			em.persist(entity);
		} else {
			entity.setUpdatetime(new Date());
		}
		if (entity.isSipEnabled() && isSipEnabled()) {
			String sipNumber = getSipNumber(entity.getRooms_id());
			if (!sipNumber.equals(entity.getConfno())) {
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

	public void delete(Room entity, Long userId) {
		entity.setDeleted(true);
		entity.setSipEnabled(false);
		update(entity, userId);
	}
}
