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
package org.apache.openmeetings.test.calendar;

//import groovy.sql.Sql;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.openmeetings.data.calendar.daos.AppointmentReminderTypDao;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentReminderTyps;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDatabaseStructureAppointmentReminderTyp extends AbstractOpenmeetingsSpringTest {

	private static final Logger log = Logger
			.getLogger(TestDatabaseStructureAppointmentReminderTyp.class);

	@Autowired
	private AppointmentReminderTypDao appointmentReminderTypDaoImpl;

	@Test
	public void testAddingGroup() {

		try {

			Calendar cal = Calendar.getInstance();
			cal.set(2008, 9, 2);
			cal.get(Calendar.DAY_OF_MONTH);
			cal.getTime();

			appointmentReminderTypDaoImpl.addAppointmentReminderTyps(1L,
					"test 5 min");
			List<AppointmentReminderTyps> listAppoints = appointmentReminderTypDaoImpl
					.getAppointmentReminderTypList();

			log.debug("Anzahl: " + listAppoints.size());

			for (AppointmentReminderTyps appoints : listAppoints) {
				// log.debug("Termin: "+appoints.getAppointmentName()+" startDate: "+appoints.getAppointmentStarttime()+
				// " endDate: "+appoints.getAppointmentEndtime());
				log.debug("AppointmentReminderTyps: " + appoints.getName());
			}

			// for (Iterator<Appointment> iter =
			// listAppoints.iterator();iter.hasNext();) {
			// log.debug(iter.next());
			// }

		} catch (Exception err) {

			log.error("[testAddingGroup]", err);

		}

	}

}
