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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	private static final Logger log = Red5LoggerFactory.getLogger(SessiondataDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ISessionManager sessionManager;

	private static Sessiondata newInstance() {
		log.debug("startsession :: startsession");

		Sessiondata sd = new Sessiondata();
		sd.setSessionId(UUID.randomUUID().toString());
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
	 * @param SID - sessionId
	 * @return {@link Sessiondata} with sessionId == SID, or null if not found
	 */
	public Sessiondata find(String SID) {
		//MSSql find nothing in case SID is passed as-is without wildcarting '%SID%'
		List<Sessiondata> sessions = em.createNamedQuery("getSessionById", Sessiondata.class)
				.setParameter("sessionId", String.format("%%%s%%", SID)).getResultList();

		Sessiondata sd = null;
		if (sessions != null && sessions.size() > 0) {
			sd = sessions.get(0);
			if (sd == null || sd.getUserId() == null || sd.getUserId().equals(new Long(0))) {
				return null;
			}
		}
		return sd;
	}

	/**
	 *
	 * @param SID
	 * @return
	 */
	public Sessiondata check(String SID) {
		Sessiondata sd = find(SID);
		// Checks if wether the Session or the User Object of that Session is set yet
		if (sd == null) {
			return newInstance();
		}
		return update(sd);
	}

	/**
	 * update the session of a user with a new user id this is needed to see if
	 * the session is loggedin
	 *
	 * @param SID
	 * @param userId
	 */
	public boolean updateUser(String SID, Long userId) {
		try {
			log.debug("updateUser User: " + userId + " || " + SID);

			Sessiondata sd = find(SID);
			if (sd == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: userId: {}", userId);

			sd.setUserId(userId);
			update(sd);
			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: ", ex2);
		}
		return false;
	}

	public boolean updateUserWithoutSession(String SID, Long userId) {
		try {
			log.debug("updateUser User: " + userId + " || " + SID);
			Sessiondata sd = find(SID);
			if (sd == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: userId: {}", userId);

			sd.setUserId(userId);
			update(sd);
			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: ", ex2);
		}
		return false;
	}

	/**
	 *
	 * @param date
	 * @return
	 */
	private List<Sessiondata> getSessionToDelete(Date refreshed) {
		try {
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionToDelete", Sessiondata.class);
			query.setParameter("refreshed", refreshed);
			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getSessionToDelete]: ", ex2);
		}
		return null;
	}

	/**
	 *
	 *
	 */
	public void clearSessionTable(long timeout) {
		try {
			log.debug("****** clearSessionTable: ");
			List<Sessiondata> l = getSessionToDelete(new Date(System.currentTimeMillis() - timeout));
			log.debug("clearSessionTable: " + l.size());
			for (Sessiondata sData : l) {
				sData = em.find(Sessiondata.class, sData.getId());
				em.remove(sData);
			}
		} catch (Exception err) {
			log.error("clearSessionTable", err);
		}
	}

	/**
	 * @param roomId
	 */
	public void clearSessionByRoomId(Long roomId) {
		try {
			for (StreamClient rcl : sessionManager.listByRoom(roomId)) {
				String aux = rcl.getSwfurl();

				//FIXME TODO this need to be refactored !
				int init_pos = aux.indexOf("sid=") + 4;
				int end_pos = init_pos + 32;
				if (end_pos > aux.length()) {
					end_pos = aux.length();
				}
				String SID = aux.substring(init_pos, end_pos);

				Sessiondata sData = check(SID);
				if (sData != null) {
					em.remove(sData);
				}
			}
		} catch (Exception err) {
			log.error("clearSessionByRoomId", err);
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
