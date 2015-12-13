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

import java.util.TimeZone;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.calendar.daos.MeetingMemberDao;
import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.MeetingMember;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingMemberLogic {

	private static final Logger log = Red5LoggerFactory.getLogger(
			MeetingMemberLogic.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private UserManager userManager;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private MeetingMemberDao meetingMemberDao;

	/**
	 * This can be either an internal or an external user, internal users have a
	 * user id != null && > 0
	 * 
	 * jNameInternalTimeZone is needed for the mapping of the timezones
	 * available
	 * 
	 * @author obecherer,seba.wagner
	 * @param firstname
	 * @param lastname
	 * @param memberStatus
	 * @param appointmentStatus
	 * @param appointmentId
	 * @param userid
	 * @param email
	 * @param baseUrl
	 * @param meeting_organizer
	 * @param invitor
	 * @param language_id
	 * @param isPasswordProtected
	 * @param password
	 * @param timezone
	 * @param jNameInternalTimeZone
	 * @param invitorName
	 *            can be different from the current firstname/lastname of course
	 * @return
	 */
	public Long addMeetingMember(String firstname, String lastname,
			String memberStatus, String appointmentStatus, Long appointmentId,
			Long userid, String email, String phone, String baseUrl, Long meeting_organizer,
			Boolean invitor, Long language_id, Boolean isPasswordProtected,
			String password, TimeZone timezone, OmTimeZone omTimeZone,
			String invitorName) {

		try {
			Long memberId = meetingMemberDao.addMeetingMember(firstname,
					lastname, memberStatus, appointmentStatus, appointmentId,
					userid, email, phone, invitor, omTimeZone, false);

			// DefaultInvitation
			Appointment point = appointmentLogic
					.getAppointMentById(appointmentId);

			MeetingMember member = getMemberById(memberId);
			Boolean isInvitor = member.getInvitor();

			Long invitationId = null;

			if (point.getRemind() == null) {
				log.error("Appointment has no assigned ReminderType!");
				return null;
			}

			log.debug(":::: addMeetingMember ..... "
					+ point.getRemind().getTypId());

			String subject = formatSubject(language_id, point, timezone);

			String message = formatMessage(language_id, point, timezone,
					invitorName);

			// point.getRemind().getTypId() == 1 will not receive emails

			if (point.getRemind().getTypId() == 2) {
				log.debug("Invitation for Appointment : simple email");

				Invitations invitation = invitationManager
						.addInvitationLink(
								new Long(2), // userlevel
								firstname + " " + lastname, // username
								message,
								baseUrl, // baseURl
								email, // email
								subject, // subject
								point.getRoom().getRooms_id(), // room_id
								"public",
								isPasswordProtected, // passwordprotected
								password, // invitationpass
								2, // valid type
								point.getAppointmentStarttime(), // valid from
								point.getAppointmentEndtime(), // valid to
								meeting_organizer, // created by
								baseUrl,
								language_id,
								true, // really send mail sendMail
								point.getAppointmentStarttime(),
								point.getAppointmentEndtime(),
								point.getAppointmentId(),
								invitorName,
								omTimeZone);

				invitationId = invitation.getInvitations_id();

			} else if (point.getRemind().getTypId() == 3) {
				log.debug("Reminder for Appointment : iCal mail");

				System.out.println("### SENDING iCAL EMAIL");

				invitationId = invitationManager
						.addInvitationIcalLink(
								new Long(2), // userlevel
								firstname + " " + lastname, // username
								message,
								baseUrl, // baseURl
								email, // email
								subject, // subject
								point.getRoom().getRooms_id(), // room_id
								"public",
								isPasswordProtected, // passwordprotected
								password, // invitationpass
								2, // valid
								point.getAppointmentStarttime(), // valid from
								point.getAppointmentEndtime(), // valid to
								meeting_organizer, // created by
								point.getAppointmentId(), isInvitor,
								language_id, timezone,
								point.getAppointmentId(),
								invitorName);

			}

			// Setting InvitationId within MeetingMember

			if (invitationId != null) {
				Invitations invi = invitationManager
						.getInvitationbyId(invitationId);

				member.setInvitation(invi);

				updateMeetingMember(member);

			}

			return memberId;

		} catch (Exception err) {
			log.error("[addMeetingMember]", err);
		}
		return null;
	}

	private String formatSubject(Long language_id, Appointment point,
			TimeZone timezone) {
		try {
			String message = fieldManager.getString(1151L, language_id) + " "
					+ point.getAppointmentName();

			message += " "
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
							point.getAppointmentStarttime(), timezone);

			message += " - "
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
							point.getAppointmentEndtime(), timezone);

			return message;
		} catch (Exception err) {
			log.error("Could not format Email message");
			return "Error formatSubject";
		}

	}

	private String formatMessage(Long language_id, Appointment point,
			TimeZone timezone, String invitorName) {
		try {
			String message = fieldManager.getString(1151L, language_id) + " "
					+ point.getAppointmentName();

			if (point.getAppointmentDescription().length() != 0) {
				message += fieldManager.getString(1152L, language_id)
						+ point.getAppointmentDescription();
			}

			message += "<br/>"
					+ fieldManager.getString(1153L, language_id)
					+ ' '
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
							point.getAppointmentStarttime(), timezone)
					+ "<br/>";

			message += fieldManager.getString(1154L, language_id)
					+ ' '
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(
							point.getAppointmentEndtime(), timezone) + "<br/>";

			message += fieldManager.getString(1156L, language_id) + invitorName + "<br/>";

			return message;
		} catch (Exception err) {
			log.error("Could not format Email message");
			return "Error formatMessage";
		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------

	/**
	 * 
	 */
	// ------------------------------------------------------------------------------------------------------------------------------
	public Long updateMeetingMember(Long meetingMemberId, String firstname,
			String lastname, String memberStatus, String appointmentStatus,
			Long appointmentId, Long userid, String email, String phone) {

		log.debug("MeetingMemberLogic.updateMeetingMember");

		MeetingMember member = meetingMemberDao
				.getMeetingMemberById(meetingMemberId);

		if (member == null) {
			log.error("Couldnt retrieve Member for ID " + meetingMemberId);
			return null;
		}

		try {
			return meetingMemberDao.updateMeetingMember(meetingMemberId,
					firstname, lastname, memberStatus, appointmentStatus,
					appointmentId, userid, email, phone);
		} catch (Exception err) {
			log.error("[updateMeetingMember]", err);
		}
		return null;
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	/**
	 * @author becherer
	 * @param member
	 * @return
	 */
	// --------------------------------------------------------------------------------------------
	public Long updateMeetingMember(MeetingMember member) {
		log.debug("updateMeetingMember");

		return meetingMemberDao.updateMeetingMember(member)
				.getMeetingMemberId();
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * @author becherer
	 * @param memberId
	 */
	// --------------------------------------------------------------------------------------------
	public MeetingMember getMemberById(Long memberId) {
		log.debug("getMemberById");

		return meetingMemberDao.getMeetingMemberById(memberId);
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param meetingMemberId
	 * @param users_id
	 * @return
	 */
	// --------------------------------------------------------------------------------------------
	public Long deleteMeetingMember(Long meetingMemberId, Long users_id,
			Long language_id) {
		log.debug("meetingMemberLogic.deleteMeetingMember : " + meetingMemberId);

		try {

			MeetingMember member = meetingMemberDao
					.getMeetingMemberById(meetingMemberId);

			if (member == null) {
				log.error("could not find meeting member!");
				return null;
			}

			Appointment point = member.getAppointment();
			point = appointmentLogic.getAppointMentById(point
					.getAppointmentId());

			if (point == null) {
				log.error("could not retrieve appointment!");
				return null;
			}

			User user = userManager.getUserById(users_id);

			if (user == null) {
				log.error("could not retrieve user!");
				return null;
			}

			log.debug("before sending cancelMail");

			// cancel invitation
			invitationManager.cancelInvitation(point, member, users_id,
					language_id);

			log.debug("after sending cancelmail");

			Long returnValue = meetingMemberDao
					.deleteMeetingMember(meetingMemberId);

			return returnValue;

		} catch (Exception err) {
			log.error("[deleteMeetingMember]", err);
		}
		return null;
	}
	// --------------------------------------------------------------------------------------------
}
