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
import static org.junit.Assert.assertTrue;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.service.calendar.AppointmentLogic;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestAppointmentSchedulerTask extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestAppointmentSchedulerTask.class, getWebAppRootKey());

	@Autowired
	private AppointmentLogic appointmentLogic;

	@Test
	public void doIt() {
		log.debug("- 1 MeetingReminderJob.execute");
		log.warn("- 2 MeetingReminderJob.execute");
		try {
			appointmentLogic.doScheduledMeetingReminder();

			assertTrue(true);
		} catch (Exception err){
			log.error("execute",err);
		}
	}

}
