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

import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;

import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.openmeetings.db.entity.room.RoomPoll;
import org.apache.openmeetings.db.entity.room.RoomPollAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PollDao {
	private static final Logger log = LoggerFactory.getLogger(PollDao.class);
	private static final String PARAM_ROOMID = "roomId";

	@PersistenceContext
	private EntityManager em;

	public RoomPoll update(RoomPoll p) {
		if (p.getId() == null) {
			p.setCreated(new Date());
			em.persist(p);
		} else {
			p = em.merge(p);
		}
		return p;
	}

	public boolean close(Long roomId) {
		try {
			log.debug(" :: close :: ");
			Query q = em.createNamedQuery("closePoll");
			q.setParameter(PARAM_ROOMID, roomId);
			q.setParameter("archived", true);
			return q.executeUpdate() > 0;
		} catch (Exception err) {
			log.error("[close]", err);
		}
		return false;
	}

	public boolean delete(RoomPoll p) {
		try {
			log.debug(" :: delete :: ");
			Query q = em.createNamedQuery("deletePoll");
			q.setParameter("id", p.getId());
			return q.executeUpdate() > 0;
		} catch (Exception err) {
			log.error("[delete]", err);
		}
		return false;
	}

	public RoomPoll get(Long id) {
		List<RoomPoll> list = em.createNamedQuery("getPollById", RoomPoll.class).setParameter("id", id).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public RoomPoll getByRoom(Long roomId) {
		log.debug(" :: getPoll :: {}", roomId);
		return only(em.createNamedQuery("getPoll", RoomPoll.class)
				.setParameter(PARAM_ROOMID, roomId).getResultList());
	}

	public List<RoomPoll> get() {
		return em.createNamedQuery("getPollListBackup", RoomPoll.class).getResultList();
	}

	public List<RoomPoll> getArchived(Long roomId) {
		log.debug(" :: getArchived :: {}", roomId);
		return em.createNamedQuery("getArchivedPollList",RoomPoll.class)
				.setParameter(PARAM_ROOMID, roomId).getResultList();
	}

	public boolean hasPoll(Long roomId) {
		log.debug(" :: hasPoll :: {}", roomId);
		return em.createNamedQuery("hasPoll", Long.class)
				.setParameter(PARAM_ROOMID, roomId)
				.setParameter("archived", false)
				.getSingleResult() > 0;
	}

	public boolean notVoted(Long roomId, Long userId) {
		return em.createNamedQuery("notVoted", RoomPollAnswer.class)
				.setParameter(PARAM_ROOMID, roomId)
				.setParameter(PARAM_USER_ID, userId)
				.getResultList().isEmpty();
	}
}
