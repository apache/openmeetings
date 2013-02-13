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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.poll.PollType;
import org.apache.openmeetings.persistence.beans.poll.RoomPoll;
import org.apache.openmeetings.persistence.beans.poll.RoomPollAnswers;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PollManager {
	private static final Logger log = Red5LoggerFactory
			.getLogger(PollManager.class);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private RoomDao roomDao;

	public Long addPollType(Long labelId, Boolean isNumeric) {
		log.debug("Adding poll type: " + labelId + ", " + isNumeric);
		PollType pt = new PollType();
		pt.setLabel(fieldManager.getFieldvaluesById(labelId));
		pt.setIsNumericAnswer(isNumeric);

		em.persist(pt);
		
		return pt.getPollTypesId();
	}
	
	public List<PollType> getPollTypes() {
		return em.createNamedQuery("getPollTypes", PollType.class)
				.getResultList();
	}
	
	public PollType getPollType(Long typeId) {
		TypedQuery<PollType> q = em.createNamedQuery("getPollType", PollType.class);
		q.setParameter("pollTypesId", typeId);
		return q.getSingleResult();
	}
	
	public RoomPoll createPoll(Client rc, String pollName, String pollQuestion, Long pollTypeId) {
		RoomPoll roomP = new RoomPoll();
		
		roomP.setCreatedBy(userManager.getUserById(rc.getUser_id()));
		roomP.setCreated(new Date());
		roomP.setPollName(pollName);
		roomP.setPollQuestion(pollQuestion);
		roomP.setPollType(getPollType(pollTypeId));
		roomP.setRoom(roomDao.get(rc.getRoom_id()));
		
		em.persist(roomP);
		return roomP;
	}
	
	public void savePollBackup(RoomPoll rp) {
		em.persist(rp);
	}


	public RoomPoll updatePoll(RoomPoll rp) {
		return em.merge(rp);
	}

	public boolean closePoll(Long room_id){
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

	public boolean deletePoll(Long poll_id){
		try {
			log.debug(" :: deletePoll :: ");
			Query q = em.createNamedQuery("deletePoll");
			q.setParameter("roomPollId", poll_id);
			return q.executeUpdate() > 0;
		} catch (Exception err) {
			log.error("[deletePoll]", err);
		}
		return false;
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
	
	public List<RoomPoll> getPollListBackup() {
		try {
			TypedQuery<RoomPoll> q = em.createNamedQuery("getPollListBackup",RoomPoll.class);
			return q.getResultList();
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return null;
	}
	
	public List<RoomPoll> getArchivedPollList(Long room_id) {
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
			TypedQuery<RoomPollAnswers> q = em.createNamedQuery("hasVoted", RoomPollAnswers.class);
			q.setParameter("room_id", room_id);
			q.setParameter("userid", userid);
			q.setParameter("archived", false);
			q.getSingleResult();
			return true;
		} catch (NoResultException nre) {
			//expected
		} catch (Exception err) {
			log.error("[getPoll]", err);
		}
		return false;
	}
}
