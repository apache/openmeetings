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
package org.apache.openmeetings.service.quartz.scheduler;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.service.calendar.AppointmentLogic;
import org.apache.openmeetings.util.InitializationContainer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingReminderJob {
	private static Logger log = Red5LoggerFactory.getLogger(MeetingReminderJob.class, webAppRootKey);
	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private RecordingDao recordingDao;
	@Autowired
	private GroupDao groupDao;

	public void remindIt() {
		log.debug("MeetingReminderJob.remindIt");
		if (!InitializationContainer.initComplete) {
			return;
		}
		try {
			appointmentLogic.doScheduledMeetingReminder();
		} catch (Exception err) {
			log.error("execute", err);
		}
	}

	public void remindExpiring() {
		log.debug("MeetingReminderJob.remindExpiring");
		if (!InitializationContainer.initComplete) {
			return;
		}
		for (Group g : groupDao.getLimited()) {
			for (Recording rec : recordingDao.getExpiring(g.getId(), g.getReminderDays())) {
				
			}
		}
	}
}
