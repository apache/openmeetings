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
package org.apache.openmeetings.core.data.calendar.management;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPOINTMENT_REMINDER_MINUTES;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_MINUTES_REMINDER_SEND;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.openmeetings.core.data.conference.RoomManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentCategoryDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentReminderTypDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.room.IInvitationManager;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class AppointmentLogic {
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentLogic.class, webAppRootKey);

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDao;
	@Autowired
	private AppointmentReminderTypDao appointmentReminderTypDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private FieldLanguagesValuesDao langDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private IInvitationManager invitationManager;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private InvitationDao invitationDao;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private RoomTypeDao roomTypeDao;

	public List<Appointment> getTodaysAppointmentsForUser(Long userId) {
		try {
			log.debug("getTodaysAppointmentsForUser");
			List<Appointment> points = appointmentDao.getTodaysAppointmentsbyRangeAndMember(userId);
			log.debug("Count Appointments for Today : " + points.size());
			return points;
		} catch (Exception err) {
			log.error("[getTodaysAppointmentsForUser]", err);
		}
		return null;
	}

	/**
	 * @author o.becherer
	 * @param room_id
	 * @return
	 */
	// --------------------------------------------------------------------------------------------
	public Appointment getAppointmentByRoom(Long room_id) throws Exception {
		log.debug("getAppointmentByRoom");

		Room room = roomDao.get(room_id);

		if (room == null) {
			throw new Exception("Room does not exist in database!");
		}

		if (!room.isAppointment()) {
			throw new Exception("Room " + room.getName() + " isnt part of an appointed meeting");
		}

		return appointmentDao.getAppointmentByRoom(room_id);
	}

	// --------------------------------------------------------------------------------------------

	// next appointment to current date
	public Appointment getNextAppointment() {
		try {
			return appointmentDao.getNextAppointment(new Date());
		} catch (Exception err) {
			log.error("[getNextAppointmentById]", err);
		}
		return null;
	}

	public List<Appointment> searchAppointmentByName(String appointmentName) {
		try {
			return appointmentDao.searchAppointmentsByName(appointmentName);
		} catch (Exception err) {
			log.error("[searchAppointmentByName]", err);
		}
		return null;
	}

	private void sendReminder(User u, Appointment a) throws Exception {
		Invitation i = new Invitation();
		i.setInvitedBy(u);
		i.setInvitee(u);
		i.setAppointment(a);
		sendReminder(u, a, i);
	}
	
	private void sendReminder(User u, Appointment a, Invitation inv) throws Exception {
		if (inv == null) {
			log.error(String.format("Error retrieving Invitation for member %s in Appointment %s"
					, u.getAdresses().getEmail(), a.getTitle()));
			return;
		}

		TimeZone tZone = timezoneUtil.getTimeZone(u.getTimeZoneId());

		long language_id = u.getLanguageId();
		// Get the required labels one time for all meeting members. The
		// Language of the email will be the system default language
		String labelid1158 = langDao.getString(1158L, language_id);
		String labelid1153 = langDao.getString(1153L, language_id);
		String labelid1154 = langDao.getString(1154L, language_id);

		String subject = generateSubject(labelid1158, a, tZone);
		String smsSubject = generateSMSSubject(labelid1158, a);

		String message = generateMessage(labelid1158, a, language_id, labelid1153, labelid1154, tZone);

		invitationManager.sendInvitionLink(inv, MessageType.Create, subject, message, false);

		invitationManager.sendInvitationReminderSMS(u.getAdresses().getPhone(), smsSubject, language_id);
		if (inv.getHash() != null) {
			inv.setUpdated(new Date());
			invitationDao.update(inv);
		}
	}
	
	/**
	 * Sending Reminder in Simple mail format 5 minutes before Meeting begins
	 */
	// ----------------------------------------------------------------------------------------------
	public void doScheduledMeetingReminder() throws Exception {
		// log.debug("doScheduledMeetingReminder");
		String baseUrl = configurationDao.getConfValue(CONFIG_APPLICATION_BASE_URL, String.class, DEFAULT_BASE_URL);
		if (baseUrl == null || baseUrl.length() < 1) {
			log.error("Error retrieving baseUrl for application");
			return;
		}
		Integer minutesReminderSend = configurationDao.getConfValue(CONFIG_APPOINTMENT_REMINDER_MINUTES, Integer.class
				, "" + DEFAULT_MINUTES_REMINDER_SEND);
		if (minutesReminderSend == null) {
			throw new Exception("minutesReminderSend is null!");
		}

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

		for (Appointment a : appointmentDao.getAppointmentsInRange(start, end)) {
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
			appointmentDao.updateAppointment(a);

			List<MeetingMember> members = a.getMeetingMembers();

			sendReminder(a.getOwner(), a);
			if (members == null) {
				log.debug("doScheduledMeetingReminder : no members in meeting!");
				continue;
			}
			//TODO should we add reminder for the owner????

			// Iterate through all MeetingMembers
			for (MeetingMember mm : members) {
				log.debug("doScheduledMeetingReminder : Member " + mm.getUser().getAdresses().getEmail());

				Invitation inv = mm.getInvitation();

				sendReminder(mm.getUser(), a, inv);
			}
		}
	}

	private String generateSubject(String labelid1158, Appointment ment, TimeZone timezone) {
		StringBuilder message = new StringBuilder(labelid1158);
		message.append(" ").append(ment.getTitle()).append(' ')
			.append(CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(ment.getStart(), timezone))
			.append(" - ").append(CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(ment.getEnd(), timezone));

		return message.toString();

	}

	private String generateSMSSubject(String labelid1158, Appointment ment) {
		String subj = configurationDao.getConfValue("sms.subject", String.class, null);
		return subj == null || subj.length() == 0 ? 
				labelid1158 + " " + ment.getTitle() : subj;
	}
	
	/**
	 * Generate a localized message including the time and date of the meeting
	 * event
	 * 
	 * @param labelid1158
	 * @param ment
	 * @param language_id
	 * @param labelid1153
	 * @param jNameTimeZone
	 * @param labelid1154
	 * @return
	 */
	private String generateMessage(String labelid1158, Appointment ment, Long language_id,
			String labelid1153, String labelid1154, TimeZone timezone) {
		StringBuilder message = new StringBuilder(labelid1158);
		message.append(" ").append(ment.getTitle());

		if (ment.getDescription() != null && ment.getDescription().length() > 0) {
			message.append(langDao.getString(1152L, language_id)).append(ment.getDescription());
		}

		message.append("<br/>").append(labelid1153).append(' ')
			.append(CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(ment.getStart(), timezone))
			.append("<br/>").append(labelid1154).append(' ')
			.append(CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(ment.getEnd(), timezone))
			.append("<br/>");

		return message.toString();
	}

	public Appointment getAppointment(String appointmentName,
			String appointmentLocation, String appointmentDescription,
			Calendar appointmentstart, Calendar appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly,
			Boolean isYearly, Long categoryId, Long remind, String[] mmClient,
			Long roomType, Long languageId,
			Boolean isPasswordProtected, String password, long roomId, Long users_id) {
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
		a.setCategory(appointmentCategoryDao.get(categoryId));
		a.setRemind(appointmentReminderTypDao.get(remind));
		if (roomId > 0) {
			a.setRoom(roomDao.get(roomId));
		} else {
			a.setRoom(new Room());
			a.getRoom().setComment(appointmentDescription);
			a.getRoom().setName(appointmentName);
			a.getRoom().setRoomtype(roomTypeDao.get(roomType));
		}
		a.setOwner(userDao.get(users_id));
		a.setPasswordProtected(isPasswordProtected);
		a.setPassword(password);
		a.setMeetingMembers(new ArrayList<MeetingMember>());
		for (String singleClient : mmClient) {
			if (Strings.isEmpty(singleClient)) {
				continue;
			}
			MeetingMember mm = getMeetingMember(users_id, languageId, singleClient);
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
