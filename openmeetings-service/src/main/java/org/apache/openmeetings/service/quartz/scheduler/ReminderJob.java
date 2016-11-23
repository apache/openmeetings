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

import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.calendar.AppointmentLogic;
import org.apache.openmeetings.service.mail.template.subject.AbstractSubjectEmailTemplate;
import org.apache.openmeetings.service.mail.template.subject.RecordingExpiringTemplate;
import org.apache.openmeetings.util.InitializationContainer;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ReminderJob extends AbstractJob {
	private static Logger log = Red5LoggerFactory.getLogger(ReminderJob.class, webAppRootKey);
	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MailHandler mailHandler;

	public void remindMeetings() {
		log.debug("ReminderJob.remindMeetings");
		if (!InitializationContainer.initComplete) {
			return;
		}
		try {
			appointmentLogic.doScheduledMeetingReminder();
		} catch (Exception err) {
			log.error("execute", err);
		}
	}

	public void remindExpiringRecordings() {
		log.debug("ReminderJob.remindExpiringRecordings");
		processExpiringRecordings(false, (rec, days) -> {
			if (days > 0) {
				User u = userDao.get(rec.getInsertedBy());
				if (u == null) {
					log.debug("Unable to send expiration email due to recording owner is NULL, {}", rec);
				} else {
					AbstractSubjectEmailTemplate templ = RecordingExpiringTemplate.get(u, rec, days);
					mailHandler.send(u.getAddress().getEmail(), templ.getSubject(), templ.getEmail());
				}
			} else {
				log.debug("Recording is too old to send notification, {} days", days);
			}
			rec.setNotified(true);
			recordingDao.update(rec);
		});
	}
}
