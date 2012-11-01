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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.IDataProviderDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.persistence.beans.rooms.Rooms;
import org.apache.openmeetings.sip.api.impl.asterisk.AsteriskDbSipClient;
import org.apache.openmeetings.sip.api.request.SIPCreateConferenceRequest;
import org.apache.openmeetings.sip.api.result.SipCreateConferenceRequestResult;
import org.apache.openmeetings.utils.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomDAO implements IDataProviderDao<Rooms> {
	public final static String[] searchFields = {"name"};
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private AsteriskDbSipClient asteriskDbSipClient;

	public Rooms get(long id) {
		TypedQuery<Rooms> q = em.createNamedQuery("getRoomById", Rooms.class);
		q.setParameter("id", id);
		List<Rooms> l = q.getResultList();
		return l.isEmpty() ? null : l.get(0);
	}

	public List<Rooms> get(int start, int count) {
		TypedQuery<Rooms> q = em.createNamedQuery("getNondeletedRooms", Rooms.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<Rooms> get(String search, int start, int count, String sort) {
		TypedQuery<Rooms> q = em.createQuery(DaoHelper.getSearchQuery("Rooms", "r", search, true, false, sort, searchFields), Rooms.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countRooms", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Rooms", "r", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	public List<Rooms> getPublicRooms() {
		return em.createNamedQuery("getPublicRoomsOrdered", Rooms.class)
				.getResultList();
	}
	
	public List<Rooms> getOrganisationRooms(long orgId) {
		TypedQuery<Rooms> q = em.createQuery(
				"SELECT DISTINCT c.room FROM Rooms_Organisation c LEFT JOIN FETCH c.room "
				+ "WHERE c.organisation.organisation_id = :orgId "
				+ "AND c.deleted = false AND c.room.deleted = false AND c.room.appointment = false "
				+ "AND c.organisation.deleted = false "
				+ "ORDER BY c.room.name ASC", Rooms.class);
		q.setParameter("orgId", orgId);
		return q.getResultList();
	}
	
	public Rooms update(Rooms entity, long userId) {
		if (entity.getRooms_id() == null) {
	        /* Red5SIP integration *******************************************************************************/
			String sipEnabled = cfgDao.getConfValue("red5sip.enable", String.class, "no");
	        if("yes".equals(sipEnabled)) {
	            if(entity.getSipNumber() != null && !entity.getSipNumber().isEmpty()) {
	                asteriskDbSipClient.createSIPConference(new SIPCreateConferenceRequest(entity.getSipNumber()));
	            } else {
	                SipCreateConferenceRequestResult requestResult = asteriskDbSipClient.createSIPConference(new SIPCreateConferenceRequest());
	                if(!requestResult.hasError()) {
	                	entity.setSipNumber(requestResult.getConferenceNumber());
	                	entity.setConferencePin(requestResult.getConferencePin());
	                }
	            }
	        }
	        /*****************************************************************************************************/
			entity.setStarttime(new Date());
			em.persist(entity);
		} else {
			entity.setUpdatetime(new Date());
			entity = em.merge(entity);
		}
		return entity;
	}

	public void delete(Rooms entity, long userId) {
		entity.setDeleted(true);
		update(entity, userId);
	}

}
