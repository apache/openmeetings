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
package org.apache.openmeetings.calendar;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDatabaseStructureAppointment extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestDatabaseStructureAppointment.class, getWebAppRootKey());

	@Autowired
	private AppointmentDao appointmentDao;

	@Test
	public void testAddingGroup() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(2008, 9, 2);
			cal.get(Calendar.DAY_OF_MONTH);
			cal.getTime();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse("2008-17-08");
			Date date2 = format.parse("2008-18-08");

			List<Appointment> listAppoints = appointmentDao.getInRange(1L, date, date2);
			// List<Appointment> listAppoints = appointmentDao.searchAppointmentsByName("%");
			// appointmentDao.getNextAppointmentById(1L);
			// appointmentDao.addAppointment("mezo", 1L, "Pforzheim", "zweiter", Calendar.getInstance().getTime(),
			// 		date, null, true, null, null, 1L,1L);
			// appointmentDao.addAppointment("testap", "erster Test",Calendar.getInstance().getTime(),
			// 		Calendar.getInstance().getTime(), true, false, false, false, new Long(1), 1L);
			log.debug("Anzahl: " + listAppoints.size());

			for (Appointment appoints : listAppoints) {
				log.debug("Termin: " + appoints.getTitle() + " startDate: " + appoints.getStart() + " endDate: " + appoints.getEnd());
				log.debug("MeetingMembers: " + appoints.getMeetingMembers().size());
			}

			for (Iterator<Appointment> iter = listAppoints.iterator(); iter.hasNext();) {
				log.debug("" + iter.next());
			}
		} catch (Exception err) {
			log.error("[testAddingGroup]", err);
		}
	}
}
