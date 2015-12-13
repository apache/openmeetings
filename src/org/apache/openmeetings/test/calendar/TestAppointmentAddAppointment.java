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

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.calendar.management.AppointmentLogic;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestAppointmentAddAppointment extends
		AbstractOpenmeetingsSpringTest {

	private static final Logger log = Red5LoggerFactory.getLogger(
			TestAppointmentAddAppointment.class,
			OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private AppointmentLogic appointmentLogic;

	@Test
	public void saveAppointment() {
		log.debug("- 1 MeetingReminderJob.execute");
		log.warn("- 2 MeetingReminderJob.execute");

		try {
			
			//Simulate webapp path
			//ScopeApplicationAdapter.webAppPath = "./WebContent";

			Calendar start = Calendar.getInstance();
			start.setTimeInMillis(start.getTimeInMillis() + 600000);

			Calendar end = Calendar.getInstance();
			end.setTimeInMillis(start.getTimeInMillis() + 600000);

			String appointmentName = "Test 01";
			String appointmentDescription = "Descr";
			Long users_id = 1L;
			String appointmentLocation = "office";
			Boolean isMonthly = false;
			Date appointmentstart = start.getTime();
			Date appointmentend = end.getTime();
			Boolean isDaily = false;
			Long categoryId = 1L;
			Boolean isWeekly = false;
			Long remind = 3L;
			Boolean isYearly = false;
			List<Map<String, String>> mmClient = new LinkedList<Map<String, String>>();
			for (int i = 0; i < 1; i++) {
				mmClient.add(createClientObj("firstname" + i, "lastname" + i,
						"first" + i + ".last" + i + "@webbase-design.de", "Etc/GMT+1"));
			}
			Long language_id = 1L;
			String baseUrl = "http://localhost:5080/openmeetings/";
			Long roomType = 1L;

			Long id = appointmentLogic.saveAppointment(appointmentName,
					users_id, appointmentLocation, appointmentDescription,
					appointmentstart, appointmentend, isDaily, isWeekly,
					isMonthly, isYearly, categoryId, remind, mmClient,
					roomType, baseUrl, language_id, false, "", -1);

			
			Thread.sleep(3000);
			
			appointmentLogic.doScheduledMeetingReminder();
			
			Thread.sleep(3000);
			
			assertTrue("Saved appointment should have valid id: " + id, id != null && id > 0);

		} catch (Exception err) {
			log.error("[saveAppointment]", err);
		}
	}

	private Map<String, String> createClientObj(String firstname,
			String lastname, String email, String jNameTimeZone) {
		Map<String, String> client = new HashMap<String, String>();
		client.put("firstname", firstname);
		client.put("lastname", lastname);
		client.put("email", email);
		client.put("jNameTimeZone", jNameTimeZone);
		return client;
	}

}
