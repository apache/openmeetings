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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.apache.openmeetings.data.calendar.management.AppointmentLogic;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
public class TestAppointmentAddAppointment extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestAppointmentAddAppointment.class, webAppRootKey);

	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private AppointmentDao appointmentDao;

	@Test
	public void saveAppointment() throws Exception {
		log.debug("- 1 MeetingReminderJob.execute");
		log.warn("- 2 MeetingReminderJob.execute");

		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(start.getTimeInMillis() + 600000);

		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(start.getTimeInMillis() + 600000);

		String appointmentName = "Test 01";
		String appointmentDescription = "Descr";
		Long users_id = 1L;
		String appointmentLocation = "office";
		Boolean isMonthly = false;
		Boolean isDaily = false;
		Long categoryId = 1L;
		Boolean isWeekly = false;
		Long remind = 3L;
		Boolean isYearly = false;
		String[] mmClient = new String[1];
		for (int i = 0; i < 1; i++) {
			mmClient[0] = createClientObj("firstname" + i, "lastname" + i,
					"first" + i + ".last" + i + "@webbase-design.de", "Etc/GMT+1");
		}
		Long language_id = 1L;
		String baseUrl = "http://localhost:5080/openmeetings/";
		Long roomType = 1L;

		Appointment a = appointmentLogic.getAppointment(appointmentName,
				appointmentLocation, appointmentDescription,
				start, end, isDaily, isWeekly,
				isMonthly, isYearly, categoryId, remind, mmClient,
				roomType, baseUrl, language_id, false, "", -1, users_id);
		a = appointmentDao.update(a, baseUrl, users_id);
		
		Thread.sleep(3000);
		
		appointmentLogic.doScheduledMeetingReminder();
		
		Thread.sleep(3000);
		
		assertTrue("Saved appointment should have valid id: " + a.getId(), a.getId() != null && a.getId() > 0);
	}

	private String createClientObj(String firstname, String lastname, String email, String jNameTimeZone) {
		StringBuilder sb = new StringBuilder();
		sb.append(",") //memberId
			.append(firstname).append(",")
			.append(lastname).append(",")
			.append(email).append(",")
			.append(",") //userId
			.append(jNameTimeZone);
		return sb.toString();
	}

}
