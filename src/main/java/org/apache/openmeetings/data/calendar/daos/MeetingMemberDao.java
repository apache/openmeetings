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
package org.apache.openmeetings.data.calendar.daos;

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.user.dao.UserDao;
import org.apache.openmeetings.persistence.beans.calendar.MeetingMember;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MeetingMemberDao {
	private static final Logger log = Red5LoggerFactory.getLogger(MeetingMemberDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private UserDao usersDao;

	public MeetingMember get(Long meetingMemberId) {
		MeetingMember meetingMember = null;
		try {
			meetingMember = em.createQuery("SELECT app FROM MeetingMember app "
					+ "WHERE app.deleted = false AND app.id = :id", MeetingMember.class)
					.setParameter("id", meetingMemberId).getSingleResult();
		} catch (NoResultException ex) {
		}

		return meetingMember;
	}

	public List<MeetingMember> getMeetingMembers() {
		return em.createQuery("select app from MeetingMember app", MeetingMember.class).getResultList();
	}

	public List<MeetingMember> getMeetingMemberByAppointmentId(
			Long appointmentId) {
		try {
			log.debug("getMeetingMemberByAppointmentId: " + appointmentId);

			String hql = "select app from MeetingMember app "
					+ "WHERE app.deleted = false "
					+ "AND app.appointment.id = :id";

			TypedQuery<MeetingMember> query = em.createQuery(hql, MeetingMember.class);
			query.setParameter("id", appointmentId);

			List<MeetingMember> listmeetingMember = query.getResultList();

			return listmeetingMember;
		} catch (Exception ex2) {
			log.error("[getMeetingMemberByAppointmentId]: ", ex2);
		}
		return null;
	}

	/**
	 * Updating MeetingMember
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

	// -------------------------------------------------------------------------------

	public Long updateMeetingMember(Long meetingMemberId, String appointmentStatus,
			Long appointmentId, Long userid) {
		try {

			MeetingMember gm = this.get(meetingMemberId);
			/*
			 * if (gm == null) { log.debug("ALERT Object with ID: "+
			 * MeetingMemberId +" does not exist yet"); return null; }
			 */

			gm.setAppointmentStatus(appointmentStatus);
			gm.setAppointment(appointmentDao.get(appointmentId));
			gm.setDeleted(false);
			gm.setUpdated(new Date());
			gm.setUser(usersDao.get(userid));

			if (gm.getId() == null) {
				em.persist(gm);
			} else {
				if (!em.contains(gm)) {
					gm = em.merge(gm);
				}
			}
			meetingMemberId = gm.getId();
			return meetingMemberId;
		} catch (Exception ex2) {
			log.error("[updateMeetingMember]: ", ex2);
		}
		return null;
	}

	public Long addMeetingMember(String appointmentStatus, Long appointmentId,
			Long userid, TimeZone timeZone, Boolean isConnectedEvent) {
		try {

			MeetingMember gm = new MeetingMember();

			gm.setAppointmentStatus(appointmentStatus);
			gm.setAppointment(appointmentDao.get(appointmentId));
			gm.setUser(userid == null ? null : usersDao.get(userid));

			gm.setInserted(new Date());
			gm.setDeleted(false);
			gm.setConnectedEvent(isConnectedEvent);
			
			gm.setTimeZoneId(timeZone.getID());

			gm = em.merge(gm);
			Long group_member_id = gm.getId();

			return group_member_id;
		} catch (Exception ex2) {
			log.error("[addMeetingMember]: ", ex2);
		}
		return null;
	}

	public Long deleteMeetingMember(Long meetingMemberId) {
		log.debug("MeetingMemnerDAoImpl.deleteMeetingMember : "
				+ meetingMemberId);

		try {

			MeetingMember gm = this.get(meetingMemberId);

			log.debug("ac: " + gm);

			if (gm == null) {
				log.debug("Already deleted / Could not find: "
						+ meetingMemberId);
				return null;
			}
			gm.setUpdated(new Date());
			gm.setDeleted(true);

			if (gm.getId() == null) {
				em.persist(gm);
			} else {
				if (!em.contains(gm)) {
					em.merge(gm);
				}
			}
			return meetingMemberId;
		} catch (Exception ex2) {
			log.error("[deleteMeetingMember]: ", ex2);
		}
		return null;
	}

}
