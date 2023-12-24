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
package org.apache.openmeetings.db.dao.server;

import static java.util.UUID.randomUUID;

import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author swagner This Class handles all session management
 *
 */
@Repository
@Transactional
public class SessiondataDao {
	private static final Logger log = LoggerFactory.getLogger(SessiondataDao.class);
	@PersistenceContext
	private EntityManager em;

	private static Sessiondata newInstance() {
		log.debug("startsession :: startsession");

		Sessiondata sd = new Sessiondata();
		sd.setSessionId(randomUUID().toString());
		sd.setCreated(new Date());
		sd.setUserId(null);

		return sd;
	}

	/**
	 * creates a new session-object in the database
	 *
	 * @param userId the id of the user to be set on this session
	 * @param languageId language id to be set on this session
	 * @return newly create {@link Sessiondata}
	 */
	public Sessiondata create(Long userId, long languageId) {
		return create(userId, null, languageId);
	}

	/**
	 * creates a new session-object in the database
	 *
	 * @param userId the id of the user to be set on this session
	 * @param roomId the id of the room to be set on this session
	 * @param languageId language id to be set on this session
	 * @return newly create {@link Sessiondata}
	 */
	public Sessiondata create(Long userId, Long roomId, long languageId) {
		log.debug("create :: create");
		Sessiondata sd = newInstance();
		sd.setUserId(userId);
		sd.setRoomId(roomId);
		sd.setLanguageId(languageId);
		return update(sd);
	}

	/**
	 * Serches {@link Sessiondata} object by sessionId
	 *
	 * @param sid - sessionId
	 * @return {@link Sessiondata} with sessionId == SID, or null if not found
	 */
	public Sessiondata find(String sid) {
		if (sid == null) {
			return null;
		}
		//MSSql find nothing in case SID is passed as-is without wildcarting '%SID%'
		List<Sessiondata> sessions = em.createNamedQuery("getSessionById", Sessiondata.class)
				.setParameter("sessionId", String.format("%%%s%%", sid)).getResultList();

		if (sessions.isEmpty()) {
			return null;
		}
		Sessiondata sd = sessions.get(0);
		if (sd == null || sd.getUserId() == null || sd.getUserId().equals(Long.valueOf(0)) || !sid.equals(sd.getSessionId())) {
			return null;
		}
		return sd;
	}

	/**
	 *
	 * @param sid - sid of {@link Sessiondata} to check
	 * @return - {@link Sessiondata} for given sid or new {@link Sessiondata}
	 */
	public Sessiondata check(String sid) {
		Sessiondata sd = find(sid);
		// Checks if wether the Session or the User Object of that Session is set yet
		if (sd == null) {
			return newInstance();
		}
		return update(sd);
	}

	/**
	 *
	 * @param refreshed - date to compare session update time with
	 * @return - the list of all expired session data
	 */
	private List<Sessiondata> getSessionToDelete(Date refreshed) {
		return em.createNamedQuery("getSessionToDelete", Sessiondata.class)
				.setParameter("refreshed", refreshed)
				.getResultList();
	}

	/**
	 *
	 * @param timeout - timeout in millis to check expired sessions
	 */
	public void clearSessionTable(long timeout) {
		try {
			log.trace("****** clearSessionTable: ");
			List<Sessiondata> l = getSessionToDelete(new Date(System.currentTimeMillis() - timeout));
			if (!l.isEmpty()) {
				log.debug("clearSessionTable: {}", l.size());
				for (Sessiondata sData : l) {
					sData = em.find(Sessiondata.class, sData.getId());
					em.remove(sData);
				}
			}
		} catch (Exception err) {
			log.error("clearSessionTable", err);
		}
	}

	public Sessiondata update(Sessiondata sd) {
		sd.setRefreshed(new Date());

		if (sd.getId() == null) {
			em.persist(sd);
		} else {
			sd = em.merge(sd);
		}
		return sd;
	}
}
