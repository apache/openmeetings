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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.IInvitationManager.MessageType;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AppointmentDao {
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDaoImpl;
	@Autowired
	private AppointmentReminderTypDao appointmentReminderTypDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private UserDao usersDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private IInvitationManager invitationManager;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private InvitationDao invitationDao;

	/*
	 * insert, update, delete, select
	 */

	/**
	 * @author o.becherer Retrievment of Appointment for room
	 */
	// -----------------------------------------------------------------------------------------------
	public Appointment getAppointmentByRoom(Long room_id) throws Exception {
		log.debug("AppointMentDaoImpl.getAppointmentByRoom");

		TypedQuery<Appointment> query = em.createNamedQuery("getAppointmentByRoomId", Appointment.class);
		query.setParameter("room_id", room_id);

		List<Appointment> appoint = query.getResultList();

		if (appoint.size() > 0) {
			return appoint.get(0);
		}

		return null;
	}

	// -----------------------------------------------------------------------------------------------

	public Appointment get(Long id) {
		TypedQuery<Appointment> query = em.createNamedQuery("getAppointmentById", Appointment.class);
		query.setParameter("id", id);

		Appointment appoint = null;
		try {
			appoint = query.getSingleResult();
		} catch (NoResultException ex) {
		}
		return appoint;
	}

	public Appointment getAppointmentByIdBackup(Long appointmentId) {
		String hql = "select a from Appointment a WHERE a.id = :id ";

		TypedQuery<Appointment> query = em.createQuery(hql, Appointment.class);
		query.setParameter("id", appointmentId);

		Appointment appoint = null;
		try {
			appoint = query.getSingleResult();
		} catch (NoResultException ex) {
		}

		return appoint;
	}

	public List<Appointment> getAppointments() {
		return em.createQuery(
				"SELECT a FROM Appointment a LEFT JOIN FETCH a.meetingMembers WHERE a.deleted = false ORDER BY a.id"
				, Appointment.class).getResultList();
	}

	public Long addAppointmentObj(Appointment ap) {
		try {

			ap.setInserted(new Date());

			ap = em.merge(ap);

			return ap.getId();
		} catch (Exception ex2) {
			log.error("[addAppointmentObj]: ", ex2);
		}
		return null;
	}

	public Appointment update(Appointment a, String baseUrl, Long userId) {
		Room r = a.getRoom();
		if (r.getRooms_id() == null) {
			r.setName(a.getTitle());
			r.setNumberOfPartizipants(cfgDao.getConfValue("calendar.conference.rooms.default.size", Long.class, "50"));
		}
		roomDao.update(r, userId);
		Set<Long> mmIds = a.getId() == null ? new HashSet<Long>()
				: meetingMemberDao.getMeetingMemberIdsByAppointment(a.getId());
		// update meeting members
		//TODO update email need to be sent on meeting members list update
		Appointment a0 = a.getId() == null ? null : get(a.getId());
		boolean sendMail = a0 == null || !a0.getTitle().equals(a.getTitle()) ||
				!(a0.getDescription() != null ? a0.getDescription().equals(a.getDescription()) : true) ||
				!(a0.getLocation() != null ? a0.getLocation().equals(a.getLocation()) : true) ||
				!a0.getStart().equals(a.getStart()) ||
				!a0.getEnd().equals(a.getEnd());
		List<MeetingMember> mmList = a.getMeetingMembers();
		if (mmList != null){
			for (MeetingMember mm : mmList) {
				if (mm.getId() == null || !mmIds.contains(mm.getId())) {
					invitationManager.processInvitation(a, mm, MessageType.Create, baseUrl);
				} else {
					mmIds.remove(mm.getId());
					invitationManager.processInvitation(a, mm, MessageType.Update, baseUrl, sendMail);
				}
			}
		}
		for (long id : mmIds) {
			invitationManager.processInvitation(a, meetingMemberDao.get(id), MessageType.Cancel, baseUrl);
		}
		//notify owner
		MeetingMember owner = new MeetingMember();
		owner.setUser(a.getOwner());
		if (a.getId() == null) {
			invitationManager.processInvitation(a, owner, MessageType.Create, baseUrl);
		} else if (a.isDeleted()) {
			invitationManager.processInvitation(a, owner, MessageType.Cancel, baseUrl);
		} else if (sendMail) {
			invitationManager.processInvitation(a, owner, MessageType.Update, baseUrl, sendMail);
		}
		if (a.getId() == null) {
			a.setInserted(new Date());
			em.persist(a);
		} else {
			a.setUpdated(new Date());
			a =	em.merge(a);
		}
		return a;
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------

	public Long updateAppointment(Appointment appointment) {
		if (appointment.getId() != null) {
			appointment = em.merge(appointment);
			return appointment.getId();
		} else {
			log.error("[updateAppointment] " + "Error: No AppointmentId given");
		}
		return null;
	}

	public List<Appointment> getAppointmentsByRoomId(Long roomId) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.room.rooms_id = :roomId ";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);
			query.setParameter("roomId", roomId);
			List<Appointment> ll = query.getResultList();

			return ll;
		} catch (Exception e) {
			log.error("[getAppointmentsByRoomId]", e);
		}
		return null;
	}

	// ----------------------------------------------------------------------------------------------------------

	public void delete(Appointment a, String baseUrl, Long userId) {
		a.setUpdated(new Date());
		a.setDeleted(true);
		a.setMeetingMembers(null);
		if (Boolean.TRUE.equals(a.getRoom().getAppointment())) {
			a.getRoom().setDeleted(true);
		}
		update(a, baseUrl, userId);
	}
	
	public List<Appointment> getAppointmentsByRange(Long userId, Date start, Date end) {
		Calendar calstart = Calendar.getInstance();
		calstart.setTime(start);
		calstart.set(Calendar.HOUR, 0);

		Calendar calend = Calendar.getInstance();
		calend.setTime(end);
		calend.set(Calendar.HOUR, 23);
		calend.set(Calendar.MINUTE, 59);
		
		log.debug("Start " + calstart.getTime() + " End " + calend.getTime());

		TypedQuery<Appointment> query = em.createNamedQuery("appointmentsInRange", Appointment.class);
		query.setParameter("starttime", calstart.getTime());
		query.setParameter("endtime", calend.getTime());
		query.setParameter("userId", userId);
		
		List<Appointment> listAppoints = new ArrayList<Appointment>(query.getResultList()); 
		TypedQuery<Appointment> q1 = em.createNamedQuery("joinedAppointmentsInRange", Appointment.class);
		q1.setParameter("starttime", calstart.getTime());
		q1.setParameter("endtime", calend.getTime());
		q1.setParameter("userId", userId);
		for (Appointment a : q1.getResultList()) {
			a.setConnectedEvent(true); //TODO need to be reviewed
			listAppoints.add(a);
		}

		return listAppoints;
	}

	public List<Appointment> getAppointmentsInRange(Calendar start, Calendar end) {
		TypedQuery<Appointment> q = em.createNamedQuery("appointmentsInRangeRemind", Appointment.class);
		q.setParameter("starttime", start.getTime());
		q.setParameter("endtime", end.getTime());
		return q.getResultList();
	}
	
	public List<Appointment> getAppointmentsByCat(Long categoryId) {
		try {

			String hql = "select a from Appointments a "
					+ "WHERE a.deleted false "
					+ "AND a.appointmentCategory.categoryId = :categoryId";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);
			query.setParameter("categoryId", categoryId);

			List<Appointment> listAppoints = query.getResultList();
			return listAppoints;
		} catch (Exception ex2) {
			log.error("[getAppointements]: ", ex2);
		}
		return null;
	}

	// next appointment to select date
	public Appointment getNextAppointment(Date appointmentStarttime) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.deleted false "
					+ "AND a.start > :appointmentStarttime ";

			TypedQuery<Appointment> query = em.createQuery(hql, Appointment.class);
			query.setParameter("appointmentStarttime", appointmentStarttime);

			Appointment appoint = null;
			try {
				appoint = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return appoint;
		} catch (Exception ex2) {
			log.error("[getNextAppointmentById]: ", ex2);
		}
		return null;
	}

	public List<Appointment> searchAppointmentsByName(String name) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.deleted false "
					+ "AND a.title LIKE :appointmentName";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);
			query.setParameter("appointmentName", name);

			List<Appointment> listAppoints = query.getResultList();

			return listAppoints;
		} catch (Exception ex2) {
			log.error("[searchAppointmentsByName]: ", ex2);
		}
		return null;
	}

	/**
	 * @author becherer
	 * @param userId
	 * @return
	 */
	public List<Appointment> getTodaysAppointmentsbyRangeAndMember(Long userId) {
		log.debug("getAppoitmentbyRangeAndMember : UserID - " + userId);

		TimeZone timeZone = timezoneUtil.getTimeZone(usersDao.get(userId));

		Calendar startCal = Calendar.getInstance(timeZone);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.HOUR, 0);
		startCal.set(Calendar.SECOND, 1);

		Calendar endCal = Calendar.getInstance(timeZone);
		endCal.set(Calendar.MINUTE, 23);
		endCal.set(Calendar.HOUR, 59);
		endCal.set(Calendar.SECOND, 59);

		TypedQuery<Appointment> query = em.createNamedQuery("appointmentsInRangeByUser", Appointment.class);

		query.setParameter("userId", userId);

		query.setParameter("starttime", startCal.getTime());
		query.setParameter("endtime", endCal.getTime());

		List<Appointment> listAppoints = query.getResultList();
		return listAppoints;
	}

	// ---------------------------------------------------------------------------------------------

	public Appointment getAppointmentByRoomId(Long user_id, Long rooms_id) {
		try {

			String hql = "select a from Appointment a "
					+ "WHERE a.deleted <> :deleted "
					+ "AND a.owner.user_id = :user_id "
					+ "AND a.room.rooms_id = :rooms_id ";

			TypedQuery<Appointment> query = em.createQuery(hql,
					Appointment.class);

			query.setParameter("deleted", true);
			query.setParameter("user_id", user_id);
			query.setParameter("rooms_id", rooms_id);

			List<Appointment> listAppoints = query.getResultList();

			if (listAppoints.size() > 0) {
				return listAppoints.get(0);
			}

			return null;

		} catch (Exception e) {
			log.error("[getAppointmentByRoomId]", e);
			return null;
		}
	}

}
