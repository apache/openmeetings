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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_REMINDER_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_MINUTES_REMINDER_SEND;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.openmeetings.core.notifier.NotifierService;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentLogic {
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentLogic.class, webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private InvitationDao invitationDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
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
		// log.debug("doScheduledMeetingReminder");
		String baseUrl = cfgDao.getString(CONFIG_APPLICATION_BASE_URL, DEFAULT_BASE_URL);
		if (baseUrl == null || baseUrl.length() < 1) {
			log.error("Error retrieving baseUrl for application");
			return;
		}
		int minutesReminderSend = cfgDao.getInt(CONFIG_APPOINTMENT_REMINDER_MINUTES, DEFAULT_MINUTES_REMINDER_SEND);

		if (minutesReminderSend == 0) {
			log.warn("minutesReminderSend is 0, disabling reminder scheduler");
			return;
		}

		long milliseconds = (minutesReminderSend * 60 * 1000L);
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
			TimeZone ownerZone = timezoneUtil.getTimeZone(a.getOwner().getTimeZoneId());
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
			//TODO should we add reminder for the owner????

			// Iterate through all MeetingMembers
			for (MeetingMember mm : members) {
				log.debug("doScheduledMeetingReminder : Member " + mm.getUser().getAddress().getEmail());

				Invitation inv = mm.getInvitation();

				sendReminder(mm.getUser(), a, inv);
			}
		}
	}

	public Appointment getAppointment(String appointmentName,
			String appointmentLocation, String appointmentDescription,
			Calendar appointmentstart, Calendar appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly,
			Boolean isYearly, String remind, String[] mmClient,
			Long roomType, Long languageId,
			boolean isPasswordProtected, String password, long roomId, Long userId) {
		Appointment a = new Appointment();
		a.setTitle(appointmentName);
		a.setLocation(appointmentLocation);
		a.setDescription(appointmentDescription);
		a.setStart(appointmentstart.getTime());
		a.setEnd(appointmentend.getTime());
		a.setIsDaily(isDaily);
		a.setIsWeekly(isWeekly);
		a.setIsMonthly(isMonthly);
		a.setIsYearly(isYearly);
		a.setReminder(Reminder.valueOf(remind));
		if (roomId > 0) {
			a.setRoom(roomDao.get(roomId));
		} else {
			a.setRoom(new Room());
			a.getRoom().setComment(appointmentDescription);
			a.getRoom().setName(appointmentName);
			a.getRoom().setType(Room.Type.get(roomType));
			a.getRoom().setAppointment(true);
		}
		a.setOwner(userDao.get(userId));
		a.setPasswordProtected(isPasswordProtected);
		a.setPassword(password);
		a.setMeetingMembers(new ArrayList<MeetingMember>());
		for (String singleClient : mmClient) {
			if (Strings.isEmpty(singleClient)) {
				continue;
			}
			MeetingMember mm = getMeetingMember(userId, languageId, singleClient);
			mm.setAppointment(a);
			a.getMeetingMembers().add(mm);
		}
		return a;
	}

	public MeetingMember getMeetingMember(Long userId, Long langId, String str) {
		String[] params = str.split(",");

		try {
			return meetingMemberDao.get(Long.valueOf(params[0]));
		} catch (Exception e) {
			//no-op
		}
		MeetingMember mm = new MeetingMember();
		try {
			mm.setUser(userDao.get(Long.valueOf(params[4])));
		} catch (Exception e) {
			//no-op
		}
		if (mm.getUser() == null) {
			mm.setUser(userDao.getContact(params[3], params[1], params[2], langId, params[5], userId));
		}

		return mm;
	}
}
