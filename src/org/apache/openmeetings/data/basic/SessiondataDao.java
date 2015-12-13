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
package org.apache.openmeetings.data.basic;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author swagner This Class handles all session management
 * 
 *         TODO: Delete all inactive session by a scheduler
 * 
 */
@Transactional
public class SessiondataDao {

	private static final Logger log = Red5LoggerFactory.getLogger(
			SessiondataDao.class, OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ManageCryptStyle manageCryptStyle;
	@Autowired
	private ISessionManager sessionManager;

	/**
	 * creates a new session-object in the database
	 * 
	 * @return
	 */
	public Sessiondata startsession() {
		try {

			log.debug("startsession :: startsession");

			long thistime = new Date().getTime();
			Sessiondata sessiondata = new Sessiondata();
			sessiondata.setSession_id(manageCryptStyle.getInstanceOfCrypt()
					.createPassPhrase(String.valueOf(thistime).toString()));
			sessiondata.setRefresh_time(new Date());
			sessiondata.setStarttermin_time(new Date());
			sessiondata.setUser_id(null);

			sessiondata = em.merge(sessiondata);

			return sessiondata;
		} catch (Exception ex2) {
			log.error("[startsession]: ", ex2);
		}

		return null;
	}

	public Sessiondata getSessionByHash(String SID) {
		try {
			log.debug("updateUser User SID: " + SID);

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Sessiondata> cq = cb.createQuery(Sessiondata.class);
			Root<Sessiondata> c = cq.from(Sessiondata.class);
			Predicate condition = cb.equal(c.get("session_id"), SID);
			cq.where(condition);

			TypedQuery<Sessiondata> q = em.createQuery(cq);

			List<Sessiondata> fullList = q.getResultList();
			if (fullList.size() == 0) {
				log.error("Could not find session to update: " + SID);
				return null;
			}

			Sessiondata sd = fullList.get(0);

			return sd;
		} catch (Exception ex2) {
			log.error("[updateUser]: ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param SID
	 * @return
	 */
	public Long checkSession(String SID) {
		try {
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class);
			query.setParameter("session_id", SID);
			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}

			// Update the Session Object
			if (sessiondata != null)
				updatesession(SID);

			// Checks if wether the Session or the User Object of that Session
			// is set yet
			if (sessiondata == null || sessiondata.getUser_id() == null
					|| sessiondata.getUser_id().equals(new Long(0))) {
				return new Long(0);
			} else {
				return sessiondata.getUser_id();
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
	 * @param USER_ID
	 */
	public Boolean updateUser(String SID, long USER_ID) {
		try {
			log.debug("updateUser User: " + USER_ID + " || " + SID);

			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class);
			query.setParameter("session_id", SID);

			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}

			if (sessiondata == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: " + sessiondata.getSession_id()
					+ " userId: " + USER_ID);

			sessiondata.setRefresh_time(new Date());
			sessiondata.setUser_id(USER_ID);
			if (sessiondata.getId() == null) {
				em.persist(sessiondata);
			} else {
				if (!em.contains(sessiondata)) {
					em.merge(sessiondata);
				}
			}
			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: ", ex2);
		}
		return null;
	}

	public Boolean updateUser(String SID, long USER_ID, boolean storePermanent,
			Long language_id) {
		try {
			log.debug("updateUser User: " + USER_ID + " || " + SID);
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class);
			query.setParameter("session_id", SID);

			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}

			if (sessiondata == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: " + sessiondata.getSession_id()
					+ " userId: " + USER_ID);

			sessiondata.setRefresh_time(new Date());
			sessiondata.setUser_id(USER_ID);
			if (storePermanent) {
				sessiondata.setStorePermanent(storePermanent);
			}
			sessiondata.setLanguage_id(language_id);
			if (sessiondata.getId() == null) {
				em.persist(sessiondata);
			} else {
				if (!em.contains(sessiondata)) {
					em.merge(sessiondata);
				}
			}

			// log.debug("session updated User: "+USER_ID);
			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: ", ex2);
		}
		return null;
	}

	public Boolean updateUserOrg(String SID, Long organization_id) {
		try {
			log.debug("updateUserOrg User: " + organization_id + " || " + SID);
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class);
			query.setParameter("session_id", SID);

			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}

			if (sessiondata == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: " + sessiondata.getSession_id()
					+ " organisation_id: " + organization_id);

			sessiondata.setRefresh_time(new Date());
			sessiondata.setOrganization_id(organization_id);
			if (sessiondata.getId() == null) {
				em.persist(sessiondata);
			} else {
				if (!em.contains(sessiondata)) {
					em.merge(sessiondata);
				}
			}
			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: ", ex2);
		}
		return null;
	}

	public Boolean updateUserWithoutSession(String SID, Long USER_ID) {
		try {
			log.debug("updateUser User: " + USER_ID + " || " + SID);
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionById", Sessiondata.class);
			query.setParameter("session_id", SID);

			List<Sessiondata> sessions = query.getResultList();

			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}

			if (sessiondata == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: " + sessiondata.getSession_id()
					+ " userId: " + USER_ID);

			sessiondata.setRefresh_time(new Date());
			sessiondata.setUser_id(USER_ID);
			if (sessiondata.getId() == null) {
				em.persist(sessiondata);
			} else {
				if (!em.contains(sessiondata)) {
					em.merge(sessiondata);
				}
			}
			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: ", ex2);
		}
		return null;
	}

	public Boolean updateUserRemoteSession(String SID, String sessionXml) {
		try {
			log.debug("updateUser User SID: " + SID);

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Sessiondata> cq = cb.createQuery(Sessiondata.class);
			Root<Sessiondata> c = cq.from(Sessiondata.class);
			Predicate condition = cb.equal(c.get("session_id"), SID);
			cq.where(condition);

			TypedQuery<Sessiondata> q = em.createQuery(cq);
			List<Sessiondata> fullList = q.getResultList();

			if (fullList.size() == 0) {
				log.error("Could not find session to update: " + SID);
				return false;
			} else {
				// log.error("Found session to update: "+SID);
			}
			Sessiondata sd = fullList.get(0);
			sd.setRefresh_time(new Date());
			sd.setSessionXml(sessionXml);

			if (sd.getId() == null) {
				em.persist(sd);
			} else {
				if (!em.contains(sd)) {
					em.merge(sd);
				}
			}
			return true;
		} catch (Exception ex2) {
			log.error("[updateUserRemoteSession]: ", ex2);
		}
		return null;
	}

	/**
	 * update the session every time a user makes a request
	 * 
	 * @param SID
	 */
	private void updatesession(String SID) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Sessiondata> cq = cb.createQuery(Sessiondata.class);
			Root<Sessiondata> c = cq.from(Sessiondata.class);
			Predicate condition = cb.equal(c.get("session_id"), SID);
			cq.where(condition);

			TypedQuery<Sessiondata> q = em.createQuery(cq);

			List<Sessiondata> fullList = q.getResultList();
			if (fullList.size() == 0) {
				log.error("Found NO session to updateSession: ");

			} else {
				// log.debug("Found session to updateSession: ");
				Sessiondata sd = fullList.iterator().next();
				// log.debug("Found session to updateSession sd "+sd.getUser_id()+" "+sd.getSession_id());
				sd.setRefresh_time(new Date());

				if (sd.getId() == null) {
					em.persist(sd);
				} else {
					if (!em.contains(sd)) {
						em.merge(sd);
					}
				}
			}

		} catch (Exception ex2) {
			log.error("[updatesession]: ", ex2);
		}
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	private List<Sessiondata> getSessionToDelete(Date refresh_time) {
		try {
			TypedQuery<Sessiondata> query = em.createNamedQuery("getSessionToDelete", Sessiondata.class);
			query.setParameter("refresh_time", refresh_time);
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
	public void clearSessionTable() {
		try {
			log.debug("****** clearSessionTable: ");
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTimeInMillis(rightNow.getTimeInMillis() - 1800000);
			List<Sessiondata> l = this.getSessionToDelete(rightNow.getTime());
			log.debug("clearSessionTable: " + l.size());
			for (Iterator<Sessiondata> it = l.iterator(); it.hasNext();) {
				Sessiondata sData = it.next();
				sData = em.find(Sessiondata.class, sData.getId());
				em.remove(sData);
			}
		} catch (Exception err) {
			log.error("clearSessionTable", err);
		}
	}

	/**
	 * @param room_id
	 */
	public void clearSessionByRoomId(Long room_id) {
		try {
			for (Client rcl : sessionManager.getClientListByRoom(room_id)) {
				String aux = rcl.getSwfurl();

				int init_pos = aux.indexOf("sid=") + 4;
				int end_pos = init_pos + 32;
				if (end_pos > aux.length())
					end_pos = aux.length();
				String SID = aux.substring(init_pos, end_pos);

				Sessiondata sData = this.getSessionByHash(SID);

				sData = em.find(Sessiondata.class, sData.getId());
				em.remove(sData);
			}

		} catch (Exception err) {
			log.error("clearSessionByRoomId", err);
		}
	}
}
