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

import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class OmCalendarDao {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private AppointmentDao appointmentDao;

	public List<OmCalendar> get() {
		return em.createNamedQuery("getCalendars", OmCalendar.class).getResultList();
	}

	/**
	 * Returns the Calendar Specified by the Calendar ID.
	 *
	 * @param calId Calendar ID of the Calendar to return.
	 * @return Returns the Calendar if found, else returns null
	 */
	public OmCalendar get(Long calId) {
		List<OmCalendar> list = em.createNamedQuery("getCalendarbyId", OmCalendar.class)
				.setParameter("calId", calId).getResultList();
		return list.size() == 1 ? list.get(0) : null;
	}

	/**
	 * Return all the Calendars that belong to the User.
	 *
	 * @param userId User ID to whom the calendars belong.
	 * @return List of Calendars
	 */
	public List<OmCalendar> getByUser(Long userId) {
		return em.createNamedQuery("getCalendarbyUser", OmCalendar.class)
				.setParameter(PARAM_USER_ID, userId)
				.getResultList();
	}

	/**
	 * Returns all the Google Calendars associated with the user.
	 *
	 * @param userId User ID of the owner of the Calendar
	 * @return List of Google Calendars.
	 */
	public List<OmCalendar> getGoogleCalendars(Long userId) {
		return em.createNamedQuery("getGoogleCalendars", OmCalendar.class)
				.setParameter(PARAM_USER_ID, userId)
				.getResultList();
	}

	/**
	 * Creates or Updates the given calendar.
	 *
	 * @param c Calendar to Update
	 * @return Updated Calendar
	 */
	public OmCalendar update(OmCalendar c) {
		if (c.getId() == null) {
			c.setInserted(new Date());
			em.persist(c);
		} else {
			c.setUpdated(new Date());
			c = em.merge(c);
		}
		return c;
	}

	/**
	 * Deletes the Calendar on the Database, along with all the Appointments associated with it.
	 *
	 * @param c Calendar to Delete
	 */
	public void delete(OmCalendar c) {
		c.setDeleted(true);

		//Delete all appointments in calendar.
		appointmentDao.deletebyCalendar(c.getId());

		//Cascades the Appointment Updates as well.
		update(c);
	}
}
