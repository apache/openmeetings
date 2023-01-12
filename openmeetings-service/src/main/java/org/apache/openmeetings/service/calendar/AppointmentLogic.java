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
package org.apache.openmeetings.service.calendar;

import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAppointmentReminderMinutes;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getBaseUrl;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.openmeetings.core.notifier.NotifierService;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class AppointmentLogic {
	private static final Logger log = LoggerFactory.getLogger(AppointmentLogic.class);

	@Inject
	private AppointmentDao appointmentDao;
	@Inject
	private InvitationDao invitationDao;
	@Inject
	private NotifierService notifierService;

	// --------------------------------------------------------------------------------------------

	private void sendReminder(User u, Appointment a) {
		Invitation i = new Invitation();
		i.setInvitedBy(u);
		i.setInvitee(u);
		i.setAppointment(a);
		i.setRoom(a.getRoom());
		sendReminder(u, a, i);
	}

	private void sendReminder(User u, Appointment a, Invitation inv) {
		notifierService.notify(u, a, inv);
		if (inv.getHash() != null) {
			invitationDao.update(inv);
		}
	}

	/**
	 * Sending Reminder in Simple mail format 5 minutes before Meeting begins
	 */
	// ----------------------------------------------------------------------------------------------
	public void doScheduledMeetingReminder() {
		String baseUrl = getBaseUrl();
		if (baseUrl == null || baseUrl.length() < 1) {
			log.error("Error retrieving baseUrl for application");
			return;
		}
		int minutesReminderSend = getAppointmentReminderMinutes();

		if (minutesReminderSend == 0) {
			log.warn("minutesReminderSend is 0, disabling reminder scheduler");
			return;
		}

		long milliseconds = minutesReminderSend * 60 * 1000L;
		Calendar start = Calendar.getInstance();
		if (milliseconds < 0) {
			start.setTimeInMillis(start.getTimeInMillis() + milliseconds);
		}
		Calendar end = Calendar.getInstance();
		if (milliseconds > 0) {
			end.setTimeInMillis(end.getTimeInMillis() + milliseconds);
		}

		for (Appointment a : appointmentDao.getInRange(start, end)) {
			// Prevent email from being send twice, even if the cycle takes
			// very long to send each
			if (a.isReminderEmailSend()) {
				continue;
			}
			TimeZone ownerZone = getTimeZone(a.getOwner());
			Calendar aNow = Calendar.getInstance(ownerZone);
			Calendar aStart = a.startCalendar(ownerZone);
			aStart.add(Calendar.MINUTE, -minutesReminderSend);
			if (aStart.after(aNow)) {
				// to early to send reminder
				continue;
			}
			// Update Appointment to not send invitation twice
			a.setReminderEmailSend(true);
			appointmentDao.update(a, null, false);

			List<MeetingMember> members = a.getMeetingMembers();

			sendReminder(a.getOwner(), a);
			if (members == null) {
				log.debug("doScheduledMeetingReminder : no members in meeting!");
				continue;
			}
			// Iterate through all MeetingMembers
			for (MeetingMember mm : members) {
				log.debug("doScheduledMeetingReminder : Member {}", mm.getUser().getAddress().getEmail());

				Invitation inv = mm.getInvitation();

				sendReminder(mm.getUser(), a, inv);
			}
		}
	}
}
