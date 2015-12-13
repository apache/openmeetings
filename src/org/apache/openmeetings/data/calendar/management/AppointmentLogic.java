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
package org.apache.openmeetings.data.calendar.management;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.calendar.daos.MeetingMemberDao;
import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.MeetingMember;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.apache.openmeetings.utils.math.TimezoneUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class AppointmentLogic {

	private static final Logger log = Red5LoggerFactory.getLogger(
			AppointmentLogic.class, "openmeetings");

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private MeetingMemberLogic meetingMemberLogic;
	@Autowired
	private TimezoneUtil timezoneUtil;

	private static int DEFAULT_MINUTES_REMINDER_SEND = 15;

	public List<Appointment> getAppointmentByRange(Long userId, Date starttime,
			Date endtime) {
		try {
			return appointmentDao.getAppointmentsByRange(userId, starttime,
					endtime);
		} catch (Exception err) {
			log.error("[getAppointmentByRange]", err);
		}
		return null;
	}

	public List<Appointment> getTodaysAppointmentsForUser(Long userId) {
		log.debug("getTodaysAppointmentsForUser");

		List<Appointment> points = appointmentDao
				.getTodaysAppointmentsbyRangeAndMember(userId);

		log.debug("Count Appointments for Today : " + points.size());

		return points;

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

		if (room == null)
			throw new Exception("Room does not exist in database!");

		if (!room.getAppointment())
			throw new Exception("Room " + room.getName()
					+ " isnt part of an appointed meeting");

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

	public Long saveAppointment(String appointmentName, Long userId,
			String appointmentLocation, String appointmentDescription,
			Date appointmentstart, Date appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly,
			Boolean isYearly, Long categoryId, Long remind,
			@SuppressWarnings("rawtypes") List mmClient, Long roomType,
			String baseUrl, Long language_id, Boolean isPasswordProtected,
			String password, long roomId) {

		log.debug("Appointmentlogic.saveAppointment");
		
		// TODO:Add this user as the default Moderator of the Room

		Long numberOfParticipants = configurationDao.getConfValue(
				"calendar.conference.rooms.default.size", Long.class, "50");

		try {

			// Adding creator as MeetingMember
			User user = userManager.getUserById(userId);
			
			Long room_id = roomId > 0 ? roomId : roomManager.addRoom(3, // user level
					appointmentName, // name
					roomType, // RoomType
					"", // Comment
					numberOfParticipants, // Number of participants
					true, // public
					null, // organizations
					true, // Appointment
					false, // Demo Room => Meeting Timer
					null, // Meeting Timer time in seconds
					false, // Is Moderated Room
					null, // Moderation List Room
					true, // Allow User Questions
					false, // isAudioOnly
					true, // allowFontStyles
					false, // isClosed
					"", // redirectURL
					"", // conferencePIN
					null, // ownerID
					null, null, 
					false, // hideTopBar
					false, // hideChat
					false, // hideActivitiesAndActions
					false, // hideFilesExplorer
					false, // hideActionsMenu
					false, // hideScreenSharing 
					false, // hideWhiteboard
					false, //showMicrophoneStatus
					false, // chatModerated
					false, // chatOpened
					false, // filesOpened
					false, // autoVideoSelect
					false //sipEnabled
				);

			log.debug("Appointmentlogic.saveAppointment : Room - " + room_id);
			log.debug("Appointmentlogic.saveAppointment : Reminder - " + remind);
	
			Room room = roomDao.get(room_id);

			// Re-factor the given time ignoring the Date is always UTC!
			TimeZone timezone = timezoneUtil.getTimezoneByUser(user);
			
			Long appointmentId = appointmentDao.addAppointment(appointmentName,
					userId, appointmentLocation, appointmentDescription,
					appointmentstart, appointmentend, isDaily, isWeekly,
					isMonthly, isYearly, categoryId, remind, room, language_id,
					isPasswordProtected, password, false, user.getOmTimeZone().getJname());

			String invitorName = user.getFirstname() + " " + user.getLastname()
					+ " [" + user.getAdresses().getEmail() + "]";

			// Add the creator of the meeting calendar event
			meetingMemberLogic.addMeetingMember(user.getFirstname(), user
					.getLastname(), "", "", appointmentId, userId, user
					.getAdresses().getEmail(), user.getPhoneForSMS(), baseUrl, userId, true,
					language_id, isPasswordProtected, password, timezone, user.getOmTimeZone(),
					invitorName);

			// iterate through all members of this meeting and add them to the
			// event and send invitation
			if (mmClient != null) {

				for (int i = 0; i < mmClient.size(); i++) {

					@SuppressWarnings("rawtypes")
					Map clientMember = (Map) mmClient.get(i);

					log.debug("clientMember.get('userId') "
							+ clientMember.get("userId"));
					
					for (Object tString : clientMember.entrySet()) {
						log.debug("tString " + tString);
					}
					log.debug("clientMember.get('meetingMemberId') "
							+ clientMember.get("meetingMemberId"));

					// We need two different timeZones, the internal Java Object
					// TimeZone, and
					// the one for the UI display object to map to, cause the UI
					// only has around 24 timezones
					// and Java around 600++
					Long sendToUserId = 0L;
					TimeZone timezoneMember = null;
					OmTimeZone omTimeZone = null;
					if (clientMember.get("userId") != null) {
						sendToUserId = Long.valueOf(
								clientMember.get("userId").toString())
								.longValue();
					}

					String phone = "";
					// Check if this is an internal user, if yes use the
					// timezone from his profile otherwise get the timezones
					// from the variable jNameTimeZone
					if (sendToUserId > 0) {
						User interalUser = userManager
								.getUserById(sendToUserId);
						phone = interalUser.getPhoneForSMS();
						timezoneMember = timezoneUtil
								.getTimezoneByUser(interalUser);
						omTimeZone = interalUser.getOmTimeZone();
					} else {
						// Get the internal-name of the timezone set in the
						// client object and convert it to a real one
						Object jName = clientMember.get("jNameTimeZone");
						if (jName == null) {
							log.error("jNameTimeZone not set in user object variable");
							jName = "";
						}
						omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(jName
								.toString());
						timezoneMember = timezoneUtil
								.getTimezoneByInternalJName(jName.toString());
					}

					// Not In Remote List available - intern OR extern user
					meetingMemberLogic.addMeetingMember(
							clientMember.get("firstname").toString(),
							clientMember.get("lastname").toString(),
							"0", // memberStatus
							"0", // appointmentStatus
							appointmentId,
							sendToUserId, // sending To: External users have a 0
											// here
							clientMember.get("email").toString(),
							phone,
							baseUrl,
							userId, // meeting_organizer
							new Boolean(false), // invitor
							language_id, //language_id
							isPasswordProtected, // isPasswordProtected
							password, // password
							timezoneMember, omTimeZone, invitorName);

				}
			}

			return appointmentId;
		} catch (Exception err) {
			log.error("[saveAppointment]", err);
		}
		return null;
	}

	/**
	 * 
	 * @param appointmentId
	 * @return
	 */
	// -------------------------------------------------------------------------------------
	public Long deleteAppointment(Long appointmentId, Long users_id,
			Long language_id) {
		log.debug("deleteAppointment : " + appointmentId);

		try {

			Appointment point = getAppointMentById(appointmentId);
			
			if (point == null) {
				log.error("No appointment found for ID " + appointmentId);
				return null;
			}

			if (point.getIsConnectedEvent() != null
					&& point.getIsConnectedEvent()) {
				List<Appointment> appointments = appointmentDao
						.getAppointmentsByRoomId(point.getRoom().getRooms_id());

				for (Appointment appointment : appointments) {

					if (!appointment.getAppointmentId().equals(appointmentId)) {

						appointmentDao.deleteAppointement(appointment
								.getAppointmentId());

					}

				}

			}

			Room room = point.getRoom();

			// Deleting/Notifing Meetingmembers
			List<MeetingMember> members = meetingMemberDao
					.getMeetingMemberByAppointmentId(appointmentId);

			if (members == null)
				log.debug("Appointment " + point.getAppointmentName()
						+ " has no meeting members");

			if (members != null) {
				for (int i = 0; i < members.size(); i++) {
					log.debug("deleting member " + members.get(i).getEmail());
					meetingMemberLogic.deleteMeetingMember(members.get(i)
							.getMeetingMemberId(), users_id, language_id);
				}
			}

			// Deleting Appointment itself
			appointmentDao.deleteAppointement(appointmentId);

			// Deleting Room
			boolean isAppRoom = room.getAppointment();
			if (isAppRoom) {
				roomDao.delete(room, users_id);
			}

			return appointmentId;

		} catch (Exception err) {
			log.error("[deleteAppointment]", err);
		}

		return null;

	}

	// -------------------------------------------------------------------------------------

	/**
	 * Retrieving Appointment by ID
	 */
	// ----------------------------------------------------------------------------------------------
	public Appointment getAppointMentById(Long appointment) {
		log.debug("getAppointMentById");

		return appointmentDao.getAppointmentById(appointment);
	}

	// ----------------------------------------------------------------------------------------------

	/**
	 * Sending Reminder in Simple mail format 5 minutes before Meeting begins
	 */
	// ----------------------------------------------------------------------------------------------
	public void doScheduledMeetingReminder() throws Exception {
		// log.debug("doScheduledMeetingReminder");

		Integer minutesReminderSend = configurationDao.getConfValue(
				"number.minutes.reminder.send", Integer.class, ""
						+ DEFAULT_MINUTES_REMINDER_SEND);
		if (minutesReminderSend == null) {
			throw new Exception("minutesReminderSend is null!");
		}

		if (minutesReminderSend == 0) {
			log.warn("minutesReminderSend is 0, disabling reminder scheduler");
			return;
		}

		long millisecondsToCheck = (minutesReminderSend * 60 * 1000);

		List<Appointment> points = appointmentDao
				.getAppointmentsForAllUsersByTimeRangeStartingNow(
						millisecondsToCheck, false);

		if (points == null || points.size() < 1) {
			log.debug("doScheduledMeetingReminder : no Appointments in range");
			return;
		}

		Long language_id = configurationDao.getConfValue("default_lang_id", Long.class, "1");

		// Get the required labels one time for all meeting members. The
		// Language of the email will be the system default language
		String labelid1158 = fieldManager.getString(1158L, language_id);
		Fieldlanguagesvalues labelid1153 = fieldManager
				.getFieldByIdAndLanguage(1153L, language_id);
		Fieldlanguagesvalues labelid1154 = fieldManager
				.getFieldByIdAndLanguage(1154L, language_id);

		for (int i = 0; i < points.size(); i++) {
			Appointment ment = points.get(i);

			// Prevent email from being send twice, even if the cycle takes
			// very long to send each
			if (ment.getIsReminderEmailSend() != null
					&& ment.getIsReminderEmailSend()) {
				continue;
			}

			// Checking ReminderType - only ReminderType simple mail is
			// concerned!
			if (ment.getRemind().getTypId() == 2
					|| ment.getRemind().getTypId() == 3) {

				// Update Appointment to not send invitation twice
				ment.setIsReminderEmailSend(true);
				appointmentDao.updateAppointment(ment);

				List<MeetingMember> members = meetingMemberDao
						.getMeetingMemberByAppointmentId(ment
								.getAppointmentId());

				if (members == null) {
					log.debug("doScheduledMeetingReminder : no members in meeting!");
					continue;
				}

				// Iterate through all MeetingMembers
				for (MeetingMember mm : members) {

					log.debug("doScheduledMeetingReminder : Member "
							+ mm.getEmail());

					Invitations inv = mm.getInvitation();

					if (inv == null) {
						log.error("Error retrieving Invitation for member "
								+ mm.getEmail() + " in Appointment "
								+ ment.getAppointmentName());
						continue;
					}

					if (inv.getBaseUrl() == null
							|| inv.getBaseUrl().length() < 1) {
						log.error("Error retrieving baseUrl from Invitation ID : "
								+ inv.getInvitations_id());
						continue;
					}

					TimeZone tZone = null;

					if (mm.getOmTimeZone() != null) {
						tZone = timezoneUtil.getTimezoneByOmTimeZoneId(mm
								.getOmTimeZone().getOmtimezoneId());
					} else {
						tZone = TimeZone.getDefault();
					}

					String subject = generateSubject(labelid1158, ment, tZone);
					String smsSubject = generateSMSSubject(labelid1158, ment);

					String message = generateMessage(labelid1158, ment,
							language_id, labelid1153, labelid1154, tZone);

					invitationManager.sendInvitationReminderLink(language_id, message,
							inv.getBaseUrl(), mm.getEmail(), subject,
							inv.getHash());

					invitationManager.sendInvitationReminderSMS(mm.getPhone(), smsSubject, language_id);
					inv.setUpdatetime(new Date());
					invitationManager.updateInvitation(inv);
				}
			}
		}
	}

	private String generateSubject(String labelid1158,
			Appointment ment, TimeZone timezone) {

		String message = labelid1158 + " "
				+ ment.getAppointmentName();

		message += ' ' + CalendarPatterns
				.getDateWithTimeByMiliSecondsAndTimeZone(
						ment.getAppointmentStarttime(), timezone);

		message += " - "
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
						ment.getAppointmentEndtime(), timezone);

		return message;

	}

	private String generateSMSSubject(String labelid1158, Appointment ment) {
		String subj = configurationDao.getConfValue("sms.subject", String.class, null);
		return subj == null || subj.length() == 0 ? 
				labelid1158 + " " + ment.getAppointmentName() : subj;
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
	private String generateMessage(String labelid1158,
			Appointment ment, Long language_id,
			Fieldlanguagesvalues labelid1153, Fieldlanguagesvalues labelid1154,
			TimeZone timezone) {

		String message = labelid1158 + " "
				+ ment.getAppointmentName();

		if (ment.getAppointmentDescription().length() != 0) {

			Fieldlanguagesvalues labelid1152 = fieldManager
					.getFieldByIdAndLanguage(new Long(1152), language_id);
			message += labelid1152.getValue()
					+ ment.getAppointmentDescription();

		}

		message += "<br/>"
				+ labelid1153.getValue()
				+ ' '
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
						ment.getAppointmentStarttime(), timezone) + "<br/>";

		message += labelid1154.getValue()
				+ ' '
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
						ment.getAppointmentEndtime(), timezone) + "<br/>";

		return message;
	}

}
