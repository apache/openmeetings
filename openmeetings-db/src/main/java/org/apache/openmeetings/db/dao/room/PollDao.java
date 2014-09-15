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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.label.FieldValueDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.PollType;
import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PollDao {
	private static final Logger log = Red5LoggerFactory.getLogger(PollDao.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private FieldValueDao fieldValDao;
	@Autowired
	private FieldLanguagesValuesDao labelDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomDao roomDao;

	public Long addPollType(Long labelId, Boolean isNumeric) {
		log.debug("Adding poll type: " + labelId + ", " + isNumeric);
		PollType pt = new PollType();
		pt.setLabel(fieldValDao.get(labelId));
		pt.setNumeric(isNumeric);

		em.persist(pt);
		
		return pt.getId();
	}
	
	public List<PollType> getTypes(long langId) {
		List<PollType> l = em.createNamedQuery("getPollTypes", PollType.class).getResultList();
		for (PollType t : l) {
			t.getLabel().setFieldlanguagesvalue(labelDao.get(t.getLabel().getId(), langId));
		}
		return l;
	}
	
	public PollType getType(Long typeId) {
		TypedQuery<PollType> q = em.createNamedQuery("getPollType", PollType.class);
		q.setParameter("pollTypesId", typeId);
		return q.getSingleResult();
	}
	
	public RoomPoll update(RoomPoll p) {
		if (p.getId() == null) {
			p.setCreated(new Date());
			em.persist(p);
		} else {
			p =	em.merge(p);
		}
		return p;
	}

	public boolean close(Long room_id){
		try {
			log.debug(" :: closePoll :: ");
			Query q = em.createNamedQuery("closePoll");
			q.setParameter("rooms_id", room_id);
			q.setParameter("archived", true);
			return q.executeUpdate() > 0;
		} catch (Exception err) {
			log.error("[closePoll]", err);
		}
		return false;
	}

	public boolean delete(RoomPoll p){
		try {
			log.debug(" :: delete :: ");
			Query q = em.createNamedQuery("deletePoll");
			q.setParameter("id", p.getId());
			return q.executeUpdate() > 0;
		} catch (Exception err) {
			log.error("[deletePoll]", err);
		}
		return false;
	}

	public RoomPoll get(Long id) {
		List<RoomPoll> list = em.createNamedQuery("getPollById", RoomPoll.class).setParameter("id", id).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}
	
	public RoomPoll getPoll(Long room_id) {
		try {
			log.debug(" :: getPoll :: " + room_id);
			TypedQuery<RoomPoll> q = em.createNamedQuery("getPoll", RoomPoll.class);
			q.setParameter("room_id", room_id);
			q.setParameter("archived", false);
			return q.getSingleResult();
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return null;
	}
	
	public List<RoomPoll> get() {
		try {
			TypedQuery<RoomPoll> q = em.createNamedQuery("getPollListBackup", RoomPoll.class);
			return q.getResultList();
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return null;
	}
	
	public List<RoomPoll> getArchived(Long room_id) {
		try {
			log.debug(" :: getPoll :: " + room_id);
			TypedQuery<RoomPoll> q = em.createNamedQuery("getArchivedPollList",RoomPoll.class);
			q.setParameter("room_id", room_id);
			q.setParameter("archived", true);
			return q.getResultList();
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return null;
	}
	
	public boolean hasPoll(Long room_id) {
		try {
			log.debug(" :: hasPoll :: " + room_id);
			TypedQuery<Long> q = em.createNamedQuery("hasPoll", Long.class);
			q.setParameter("room_id", room_id);
			q.setParameter("archived", false);
			return q.getSingleResult() > 0;
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return false;
	}
	
	public boolean hasVoted(Long room_id, Long userid) {
		try {
			log.debug(" :: hasVoted :: " + room_id + ", " + userid);
			TypedQuery<RoomPollAnswer> q = em.createNamedQuery("hasVoted", RoomPollAnswer.class);
			q.setParameter("room_id", room_id);
			q.setParameter("userid", userid);
			q.getSingleResult();
			return true;
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[hasVoted]", err);
		}
		return false;
	}
}
