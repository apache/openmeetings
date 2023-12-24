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
package org.apache.openmeetings.db.dao.calendar;

import static org.apache.openmeetings.db.util.DaoHelper.only;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class MeetingMemberDao {
	private static final Logger log = LoggerFactory.getLogger(MeetingMemberDao.class);
	@PersistenceContext
	private EntityManager em;

	public MeetingMember get(Long id) {
		return only(em.createNamedQuery("getMeetingMemberById", MeetingMember.class)
				.setParameter("id", id).getResultList());
	}

	public List<MeetingMember> get() {
		return em.createNamedQuery("getMeetingMembers", MeetingMember.class).getResultList();
	}

	public Set<Long> getMeetingMemberIdsByAppointment(Long appointmentId) {
		log.debug("getMeetingMemberIdsByAppointment: {}", appointmentId);

		return new HashSet<>(em.createNamedQuery("getMeetingMemberIdsByAppointment", Long.class)
				.setParameter("id", appointmentId)
				.getResultList());
	}

	/**
	 * Updating MeetingMember
	 *
	 * @param meetingMember - entity to update
	 * @return - updated entity
	 */
	// -------------------------------------------------------------------------------
	public MeetingMember update(MeetingMember meetingMember) {
		if (meetingMember.getId() == null) {
			em.persist(meetingMember);
		} else {
			if (!em.contains(meetingMember)) {
				meetingMember = em.merge(meetingMember);
			}
		}
		return meetingMember;
	}
}
