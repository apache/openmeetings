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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.db.util.DaoHelper.UNSUPPORTED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CALENDAR_ROOM_CAPACITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.manager.IInvitationManager;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AppointmentDao implements IDataProviderDao<Appointment>{
	private static final Logger log = LoggerFactory.getLogger(AppointmentDao.class);
	private static final String PARAM_START = "start";
	private static final String PARAM_CALID = "calId";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private MeetingMemberDao meetingMemberDao;
	@Inject
	private RoomDao roomDao;
	@Inject
	private ConfigurationDao cfgDao;
	@Inject
	private IInvitationManager invitationManager;

	/*
	 * insert, update, delete, select
	 */
	// -----------------------------------------------------------------------------------------------
	@Override
	public Appointment get(Long id) {
		return only(em.createNamedQuery("getAppointmentById", Appointment.class)
				.setParameter("id", id).getResultList());
	}

	public Appointment getAny(Long id) {
		return only(em.createNamedQuery("getAppointmentByIdAny", Appointment.class)
				.setParameter("id", id).getResultList());
	}

	public List<Appointment> get() {
		return em.createNamedQuery("getAppointments", Appointment.class).getResultList();
	}

	@Override
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
		final boolean newApp = a.getId() == null;
		AppointmentDTO a0 = null;
		Set<Long> mmIds = Set.of();
		if (sendmails && !newApp) {
			Appointment prev = get(a.getId());
			if (prev != null) {
				a0 = new AppointmentDTO(prev);
			}
			mmIds = meetingMemberDao.getMeetingMemberIdsByAppointment(a.getId());
		}
		if (newApp) {
			a.setIcalId(randomUUID().toString());
			em.persist(a);
		} else {
			a = em.merge(a);
		}
		if (sendmails) {
			// update meeting members
			boolean sendMail = a0 == null
					|| !StringUtils.equals(a0.getTitle(), a.getTitle())
					|| !StringUtils.equals(a0.getDescription(), a.getDescription())
					|| !StringUtils.equals(a0.getLocation(), a.getLocation())
					|| !a0.getStart().getTime().equals(a.getStart())
					|| !a0.getEnd().getTime().equals(a.getEnd());
			List<MeetingMember> mmList = a.getMeetingMembers();
			if (mmList != null){
				for (MeetingMember mm : mmList) {
					if (mm.getId() == null || !mmIds.contains(mm.getId())) {
						invitationManager.processInvitation(a, mm, MessageType.CREATE);
					} else {
						mmIds.remove(mm.getId());
						invitationManager.processInvitation(a, mm, MessageType.UPDATE, sendMail);
					}
				}
			}
			for (long id : mmIds) {
				invitationManager.processInvitation(a, meetingMemberDao.get(id), MessageType.CANCEL);
			}
			//notify owner
			MeetingMember owner = new MeetingMember();
			owner.setUser(a.getOwner());
			if (newApp) {
				invitationManager.processInvitation(a, owner, MessageType.CREATE);
			} else if (a.isDeleted()) {
				invitationManager.processInvitation(a, owner, MessageType.CANCEL);
			} else if (sendMail) {
				invitationManager.processInvitation(a, owner, MessageType.UPDATE, sendMail);
			}
		}
		return a;
	}

	// ----------------------------------------------------------------------------------------------------------

	@Override
	public void delete(Appointment a, Long userId) {
		if (a == null || a.getId() == null) {
			return;
		}
		a.setDeleted(true);
		a.setMeetingMembers(null);
		if (a.getRoom().isAppointment()) {
			a.getRoom().setDeleted(true);
		}
		update(a, userId);
	}

	public List<Appointment> getInRange(Long userId, Date start, Date end) {
		log.debug("Start {} End {}", start, end);

		TypedQuery<Appointment> query = em.createNamedQuery("appointmentsInRange", Appointment.class);
		query.setParameter(PARAM_START, start);
		query.setParameter("end", end);
		query.setParameter(PARAM_USER_ID, userId);

		List<Appointment> listAppoints = new ArrayList<>(query.getResultList());
		TypedQuery<Appointment> q1 = em.createNamedQuery("joinedAppointmentsInRange", Appointment.class);
		q1.setParameter(PARAM_START, start);
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
		q.setParameter("none", Reminder.NONE);
		q.setParameter(PARAM_START, start.getTime());
		q.setParameter("end", end.getTime());
		return q.getResultList();
	}

	// next appointment to select date
	public Appointment getNext(Long userId, Date start) {
		List<Appointment> list = em.createNamedQuery("getNextAppointment", Appointment.class)
				.setParameter(PARAM_START, start).setParameter(PARAM_USER_ID, userId).getResultList();
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
				.setParameter(PARAM_CALID, calId)
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
				.setParameter(PARAM_CALID, calId)
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
	 */
	public int deletebyCalendar(Long calId) {
		return em.createNamedQuery("deleteAppointmentsbyCalendar", Appointment.class)
				.setParameter(PARAM_CALID, calId)
				.executeUpdate();
	}

	@Override
	public List<Appointment> get(long start, long count) {
		throw UNSUPPORTED;
	}

	@Override
	public List<Appointment> get(String search, long start, long count, SortParam<String> order) {
		throw UNSUPPORTED;
	}

	@Override
	public long count() {
		throw UNSUPPORTED;
	}

	@Override
	public long count(String search) {
		throw UNSUPPORTED;
	}
}
