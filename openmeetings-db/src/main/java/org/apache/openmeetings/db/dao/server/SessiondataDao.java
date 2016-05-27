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

import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author swagner This Class handles all session management
 * 
 */
@Transactional
public class SessiondataDao {
	private static final Logger log = Red5LoggerFactory.getLogger(SessiondataDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ISessionManager sessionManager;

	/**
	 * creates a new session-object in the database
	 * 
	 * @return
	 */
	public Sessiondata create() {
		log.debug("startsession :: startsession");

		Sessiondata sd = new Sessiondata();
		sd.setSessionId(UUID.randomUUID().toString());
		sd.setCreated(new Date());
		sd.setUserId(null);

		return update(sd);
	}

	public Sessiondata get(String SID) {
		try {
			log.debug("updateUser User SID: " + SID);

			TypedQuery<Sessiondata> q = em.createNamedQuery("getSessionById", Sessiondata.class).setParameter("sessionId", SID);

			List<Sessiondata> fullList = q.getResultList();
			if (fullList.size() == 0) {
				log.error("Could not find session to update: " + SID);
				return null;
			}

			Sessiondata sd = fullList.get(0);

			return sd;
		} catch (Exception ex2) {
			log.error("[getSessionByHash]: ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param SID
	 * @return
	 */
	public Long check(String SID) {
		try {
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class).setParameter("sessionId", SID);
			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sd = null;
			if (sessions != null && sessions.size() > 0) {
				sd = sessions.get(0);
			}

			// Update the Session Object
			if (sd != null) {
				update(sd);
			}

			// Checks if wether the Session or the User Object of that Session
			// is set yet
			if (sd == null || sd.getUserId() == null || sd.getUserId().equals(new Long(0))) {
				return new Long(0);
			} else {
				return sd.getUserId();
			}
		} catch (Exception ex2) {
			log.error("[checkSession]: ", ex2);
		}
		return null;
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

			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class).setParameter("sessionId", SID);

			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sd = null;
			if (sessions != null && sessions.size() > 0) {
				sd = sessions.get(0);
			}

			if (sd == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: {}, userId: {}", sd.getSessionId(), userId);

			sd.setUserId(userId);
			update(sd);
			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: ", ex2);
		}
		return false;
	}

	public boolean updateUser(String SID, Long userId, boolean permanent, long languageId) {
		try {
			log.debug("updateUser User: " + userId + " || " + SID);
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class).setParameter("sessionId", SID);

			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sd = null;
			if (sessions != null && sessions.size() > 0) {
				sd = sessions.get(0);
			}

			if (sd == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: {}, userId: {}", sd.getSessionId(), userId);

			sd.setUserId(userId);
			sd.setPermanent(permanent);
			sd.setLanguageId(languageId);
			update(sd);

			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: ", ex2);
		}
		return false;
	}

	public boolean updateUserGroup(String SID, Long groupId) {
		try {
			log.debug("updateUserGroup User: " + groupId + " || " + SID);
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class).setParameter("sessionId", SID);

			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sd = null;
			if (sessions != null && sessions.size() > 0) {
				sd = sessions.get(0);
			}

			if (sd == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: {}, groupId: {}", sd.getSessionId(), groupId);

			sd.setGroupId(groupId);
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
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class).setParameter("sessionId", SID);

			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sd = null;
			if (sessions != null && sessions.size() > 0) {
				sd = sessions.get(0);
			}

			if (sd == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: {}, userId: {}", sd.getSessionId(), userId);

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
			for (Client rcl : sessionManager.getClientListByRoom(roomId)) {
				String aux = rcl.getSwfurl();

				//FIXME TODO this need to be refactored !
				int init_pos = aux.indexOf("sid=") + 4;
				int end_pos = init_pos + 32;
				if (end_pos > aux.length()) {
					end_pos = aux.length();
				}
				String SID = aux.substring(init_pos, end_pos);

				Sessiondata sData = get(SID);
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
