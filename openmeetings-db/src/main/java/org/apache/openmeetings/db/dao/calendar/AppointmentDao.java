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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CALENDAR_ROOM_CAPACITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.IInvitationManager;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Room;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AppointmentDao {
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentDao.class, getWebAppRootKey());
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private IInvitationManager invitationManager;

	/*
	 * insert, update, delete, select
	 */
	// -----------------------------------------------------------------------------------------------
	public Appointment get(Long id) {
		List<Appointment> list = em.createNamedQuery("getAppointmentById", Appointment.class)
				.setParameter("id", id).getResultList();
		return list.size() == 1 ? list.get(0) : null;
	}

	public Appointment getAny(Long id) {
		List<Appointment> list = em.createNamedQuery("getAppointmentByIdAny", Appointment.class)
				.setParameter("id", id).getResultList();
		return list.size() == 1 ? list.get(0) : null;
	}

	public List<Appointment> get() {
		return em.createNamedQuery("getAppointments", Appointment.class).getResultList();
	}

	public Appointment update(Appointment a, Long userId) {
		return update(a, userId, true);
	}

	public Appointment update(Appointment a, Long userId, boolean sendmails) {
		Room r = a.getRoom();
		if (r.getId() == null) {
			r.setName(a.getTitle());
			r.setCapacity(cfgDao.getLong(CONFIG_CALENDAR_ROOM_CAPACITY, 50L));
		}
		a.setRoom(roomDao.update(r, userId));
		if (sendmails) {
			Set<Long> mmIds = a.getId() == null ? new HashSet<>()
					: meetingMemberDao.getMeetingMemberIdsByAppointment(a.getId());
			// update meeting members
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
						invitationManager.processInvitation(a, mm, MessageType.Create);
					} else {
						mmIds.remove(mm.getId());
						invitationManager.processInvitation(a, mm, MessageType.Update, sendMail);
					}
				}
			}
			for (long id : mmIds) {
				invitationManager.processInvitation(a, meetingMemberDao.get(id), MessageType.Cancel);
			}
			//notify owner
			MeetingMember owner = new MeetingMember();
			owner.setUser(a.getOwner());
			if (a.getId() == null) {
				invitationManager.processInvitation(a, owner, MessageType.Create);
			} else if (a.isDeleted()) {
				invitationManager.processInvitation(a, owner, MessageType.Cancel);
			} else if (sendMail) {
				invitationManager.processInvitation(a, owner, MessageType.Update, sendMail);
			}
		}
		if (a.getId() == null) {
			a.setInserted(new Date());
			em.persist(a);
		} else {
			a.setUpdated(new Date());
			a = em.merge(a);
		}
		return a;
	}

	// ----------------------------------------------------------------------------------------------------------

	public void delete(Appointment a, Long userId) {
		if (a == null || a.getId() == null) {
			return;
		}
		a.setUpdated(new Date());
		a.setDeleted(true);
		a.setMeetingMembers(null);
		if (a.getRoom().isAppointment()) {
			a.getRoom().setDeleted(true);
		}
		update(a, userId);
	}

	public List<Appointment> getInRange(Long userId, Date start, Date end) {
		log.debug("Start " + start + " End " + end);

		TypedQuery<Appointment> query = em.createNamedQuery("appointmentsInRange", Appointment.class);
		query.setParameter("start", start);
		query.setParameter("end", end);
		query.setParameter(PARAM_USER_ID, userId);

		List<Appointment> listAppoints = new ArrayList<>(query.getResultList());
		TypedQuery<Appointment> q1 = em.createNamedQuery("joinedAppointmentsInRange", Appointment.class);
		q1.setParameter("start", start);
		q1.setParameter("end", end);
		q1.setParameter(PARAM_USER_ID, userId);
		for (Appointment a : q1.getResultList()) {
			a.setConnectedEvent(true);
			listAppoints.add(a);
		}

		return listAppoints;
	}

	public List<Appointment> getInRange(Calendar start, Calendar end) {
		TypedQuery<Appointment> q = em.createNamedQuery("appointmentsInRangeRemind", Appointment.class);
		q.setParameter("none", Reminder.none);
		q.setParameter("start", start.getTime());
		q.setParameter("end", end.getTime());
		return q.getResultList();
	}

	// next appointment to select date
	public Appointment getNext(Long userId, Date start) {
		List<Appointment> list = em.createNamedQuery("getNextAppointment", Appointment.class)
				.setParameter("start", start).setParameter(PARAM_USER_ID, userId).getResultList();
		return list == null || list.isEmpty() ? null : list.get(0);
	}

	public List<Appointment> searchByTitle(Long userId, String title) {
		return em.createNamedQuery("getAppointmentsByTitle", Appointment.class)
				.setParameter("title", title).setParameter(PARAM_USER_ID, userId).getResultList();
	}

	// ---------------------------------------------------------------------------------------------
	public Appointment getByRoom(Long userId, Long roomId) {
		try {
			List<Appointment> list = em.createNamedQuery("getAppointmentByOwnerRoomId", Appointment.class)
					.setParameter(PARAM_USER_ID, userId)
					.setParameter("roomId", roomId)
					.getResultList();

			return list.isEmpty() ? null : list.get(0);
		} catch (Exception e) {
			log.error("[getByRoom]", e);
			return null;
		}
	}

	public Appointment getByRoom(Long roomId) {
		List<Appointment> list = em.createNamedQuery("getAppointmentByRoomId", Appointment.class)
				.setParameter("roomId", roomId)
				.getResultList();

		Appointment a = list.isEmpty() ? null : list.get(0);
		if (a != null && !a.getRoom().isAppointment()) {
			throw new RuntimeException("Room " + a.getRoom().getName() + " isnt part of an appointed meeting");
		}

		return a;
	}

	//Calendar Related Methods

	/**
	 * Returns the Appointment HREF's belonging to the Calendar Id specified.
	 *
	 * @param calId Calendar to which the Appointments are related to.
	 * @return <code>List</code> of Appointment HREF's
	 */
	public List<String> getHrefsbyCalendar(Long calId) {
		return em.createNamedQuery("getHrefsforAppointmentsinCalendar", String.class)
				.setParameter("calId", calId)
				.getResultList();
	}

	/**
	 * Returns the Appointments related to the Calendar ID specified.
	 *
	 * @param calId Calendar ID of the calendar, to which the appointment is associated
	 * @return <code>List</code> of <code>Appointment</code>
	 */
	public List<Appointment> getbyCalendar(Long calId) {
		return em.createNamedQuery("getAppointmentsbyCalendar", Appointment.class)
				.setParameter("calId", calId)
				.getResultList();
	}

	/**
	 * Bulk Deletes the Appointments related the the calId.
	 * Note: Does not automatically, commit, but gets cascaded in the function which calls it.
	 * If there is a need to commit during this function, use <code>em.flush()</code> and <code>em.clear()</code>
	 *
	 * @param calId Calendar Id of the Calendar Id to which the Appointments belong to.
	 * @return Returns <code>-1</code> if the there was an error executing the query,
	 * otherwise returns the number of updated rows.
	 * as described here {@link Query#executeUpdate()}
	 *
	 * @param calId - id of the calendar
	 * @return - number of deleted items
	 */
	public int deletebyCalendar(Long calId) {
		return em.createNamedQuery("deleteAppointmentsbyCalendar", Appointment.class)
				.setParameter("calId", calId)
				.executeUpdate();
	}
}
