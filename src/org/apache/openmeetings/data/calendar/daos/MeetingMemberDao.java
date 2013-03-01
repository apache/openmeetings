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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.calendar.MeetingMember;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MeetingMemberDao {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MeetingMemberDao.class, OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private UsersDao usersDao;

	public MeetingMember getMeetingMemberById(Long meetingMemberId) {
		try {
			log.debug("getMeetingMemberById: " + meetingMemberId);

			String hql = "select app from MeetingMember app "
					+ "WHERE app.deleted <> :deleted "
					+ "AND app.meetingMemberId = :meetingMemberId";

			TypedQuery<MeetingMember> query = em.createQuery(hql, MeetingMember.class);
			query.setParameter("deleted", true);
			query.setParameter("meetingMemberId", meetingMemberId);

			MeetingMember meetingMember = null;
			try {
				meetingMember = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return meetingMember;
		} catch (Exception ex2) {
			log.error("[getMeetingMemberById]: ", ex2);
		}
		return null;
	}

	public List<MeetingMember> getMeetingMembers() {
		try {
			String hql = "select app from MeetingMember app";
			
			TypedQuery<MeetingMember> query = em.createQuery(hql, MeetingMember.class);

			List<MeetingMember> meetingMembers = query.getResultList();

			return meetingMembers;
		} catch (Exception ex2) {
			log.error("[getMeetingMembers]: ", ex2);
		}
		return null;
	}

	public List<MeetingMember> getMeetingMemberByAppointmentId(
			Long appointmentId) {
		try {
			log.debug("getMeetingMemberByAppointmentId: " + appointmentId);

			String hql = "select app from MeetingMember app "
					+ "WHERE app.deleted <> :deleted "
					+ "AND app.appointment.appointmentId = :appointmentId";

			TypedQuery<MeetingMember> query = em.createQuery(hql, MeetingMember.class);
			query.setParameter("deleted", true);
			query.setParameter("appointmentId", appointmentId);

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
	public MeetingMember updateMeetingMember(MeetingMember meetingMember) {
		log.debug("");
		if (meetingMember.getMeetingMemberId() > 0) {
			try {
				if (meetingMember.getMeetingMemberId() == null) {
					em.persist(meetingMember);
				} else {
					if (!em.contains(meetingMember)) {
						meetingMember = em.merge(meetingMember);
					}
				}
				return meetingMember;
			} catch (Exception ex2) {
				log.error("[updateMeetingMember] ", ex2);
			}
		} else {
			log.error("[updateUser] " + "Error: No MeetingMemberId given");
		}
		return null;
	}

	// -------------------------------------------------------------------------------

	public Long updateMeetingMember(Long meetingMemberId, String firstname,
			String lastname, String memberStatus, String appointmentStatus,
			Long appointmentId, Long userid, String email, String phone) {
		try {

			MeetingMember gm = this.getMeetingMemberById(meetingMemberId);
			/*
			 * if (gm == null) { log.debug("ALERT Object with ID: "+
			 * MeetingMemberId +" does not exist yet"); return null; }
			 */

			gm.setFirstname(firstname);
			gm.setLastname(lastname);

			// gm.setLanguageId(Languagemanagement.getInstance().getFieldLanguageById(languageId));
			gm.setMemberStatus(memberStatus);
			gm.setAppointmentStatus(appointmentStatus);
			gm.setAppointment(appointmentDao.getAppointmentById(appointmentId));
			gm.setDeleted(false);
			gm.setUpdatetime(new Date());
			gm.setUserid(usersDao.get(userid));
			gm.setEmail(email);
			gm.setPhone(phone);

			if (gm.getMeetingMemberId() == null) {
				em.persist(gm);
			} else {
				if (!em.contains(gm)) {
					gm = em.merge(gm);
				}
			}
			meetingMemberId = gm.getMeetingMemberId();
			return meetingMemberId;
		} catch (Exception ex2) {
			log.error("[updateMeetingMember]: ", ex2);
		}
		return null;
	}

	public Long addMeetingMember(String firstname, String lastname,
			String memberStatus, String appointmentStatus, Long appointmentId,
			Long userid, String email, String phone, Boolean moderator, OmTimeZone omTimeZone,
			Boolean isConnectedEvent) {
		try {

			MeetingMember gm = new MeetingMember();

			gm.setFirstname(firstname);
			gm.setLastname(lastname);
			gm.setMemberStatus(memberStatus);
			gm.setAppointmentStatus(appointmentStatus);
			gm.setAppointment(appointmentDao.getAppointmentById(appointmentId));
			gm.setUserid(userid == null ? null : usersDao.get(userid));
			gm.setEmail(email);
			gm.setPhone(phone);

			gm.setStarttime(new Date());
			gm.setDeleted(false);
			gm.setInvitor(moderator);
			gm.setIsConnectedEvent(isConnectedEvent);
			
			gm.setOmTimeZone(omTimeZone);

			gm = em.merge(gm);
			Long group_member_id = gm.getMeetingMemberId();

			return group_member_id;
		} catch (Exception ex2) {
			log.error("[addMeetingMember]: ", ex2);
		}
		return null;
	}

	public Long addMeetingMemberByObject(MeetingMember gm) {
		try {
			gm = em.merge(gm);
			Long group_member_id = gm.getMeetingMemberId();

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

			MeetingMember gm = this.getMeetingMemberById(meetingMemberId);

			log.debug("ac: " + gm);

			if (gm == null) {
				log.debug("Already deleted / Could not find: "
						+ meetingMemberId);
				return null;
			}
			gm.setUpdatetime(new Date());
			gm.setDeleted(true);

			if (gm.getMeetingMemberId() == null) {
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
