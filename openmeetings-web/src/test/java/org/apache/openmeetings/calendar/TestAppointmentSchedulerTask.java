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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_REMINDER_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setBaseUrl;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.service.calendar.AppointmentLogic;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;


class TestAppointmentSchedulerTask extends AbstractOmServerTest {
	private static final Logger log = LoggerFactory.getLogger(TestAppointmentSchedulerTask.class);

	@Inject
	private AppointmentLogic appointmentLogic;

	@Test
	void noBaseUrl() {
		final String origBaseUrl = getBaseUrl();
		try {
			for (String url : new String[] {null, ""}) {
				setBaseUrl(url);
				doIt();
			}
		} finally {
			setBaseUrl(origBaseUrl);
		}
	}

	@Test
	void turnedOff() {
		final Configuration origCfg = cfgDao.get(CONFIG_APPOINTMENT_REMINDER_MINUTES);
		try {
			Configuration cfg = cfgDao.get(origCfg.getId());
			cfg.setValueN(0L);
			cfgDao.update(cfg, null);
			doIt();
		} finally {
			cfgDao.update(origCfg, null);
		}
	}

	@Test
	void doIt() {
		log.debug("- 1 MeetingReminderJob.execute");
		try {
			appointmentLogic.doScheduledMeetingReminder();

			assertTrue(true);
		} catch (Exception err){
			log.error("execute",err);
		}
	}

}
