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
package org.apache.openmeetings.data.conference;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.user.rooms.RoomEnterBehavior.getRoomUrlFragment;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.db.dao.calendar.IInvitationManager;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.entity.basic.MailMessage;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.mail.MailHandler;
import org.apache.openmeetings.mail.SMSHandler;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.crypt.MD5;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
import org.apache.openmeetings.util.mail.IcalHandler;
import org.apache.openmeetings.web.mail.template.InvitationTemplate;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class InvitationManager implements IInvitationManager {
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationManager.class, webAppRootKey);

	@Autowired
	private InvitationDao invitationDao;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private SMSHandler smsHandler;
	@Autowired
	private TimezoneUtil timezoneUtil;

	/**
	 * Sending invitation within plain mail
	 * 
	 * @param user_level
	 * @param username
	 * @param message
	 * @param baseurl
	 * @param email
	 * @param subject
	 * @param rooms_id
	 * @param conferencedomain
	 * @param isPasswordProtected
	 * @param invitationpass
	 * @param valid
	 * @param validFrom
	 * @param validTo
	 * @param createdBy
	 * @return
	 */
	// ---------------------------------------------------------------------------------------------------------
	public Invitation getInvitation(User inveetee, Room room
			, boolean isPasswordProtected, String invitationpass, Valid valid,
			User createdBy, String baseUrl, Long language_id, Date gmtTimeStart, Date gmtTimeEnd
			, Appointment appointment)
	{
		Invitation i = getInvitation(null, inveetee, room, isPasswordProtected, invitationpass, valid, createdBy
				, baseUrl, language_id, gmtTimeStart, gmtTimeEnd, appointment);
		i = invitationDao.update(i);
		return i;
	}
	
	public Invitation getInvitation(Invitation _invitation, User inveetee, Room room
			, boolean isPasswordProtected, String invitationpass, Valid valid,
			User createdBy, String baseUrl, Long language_id, Date gmtTimeStart, Date gmtTimeEnd
			, Appointment appointment) {
		
		Invitation invitation = _invitation;
		if (null == _invitation) {
			invitation = new Invitation();
			String hashRaw = "HASH" + (System.currentTimeMillis());
			try {
				invitation.setHash(MD5.do_checksum(hashRaw));
			} catch (NoSuchAlgorithmException e) {
				log.error("Unexpected error while creating invitation", e);
				throw new RuntimeException(e);
			}
		}

		invitation.setPasswordProtected(isPasswordProtected);
		if (isPasswordProtected) {
			invitation.setPassword(ManageCryptStyle.getInstanceOfCrypt().createPassPhrase(invitationpass));
		}

		invitation.setUsed(false);
		log.debug(baseUrl);
		if (baseUrl != null) {
			invitation.setBaseUrl(baseUrl);
		}
		invitation.setValid(valid);
		
		// valid period of Invitation
		switch (valid) {
			case Period:
				invitation.setValidFrom(new Date(gmtTimeStart.getTime() - (5 * 60 * 1000)));
				invitation.setValidTo(gmtTimeEnd);
				break;
			case Endless:
			case OneTime:
			default:
				break;
		}

		invitation.setDeleted(false);

		invitation.setInvitedBy(createdBy);
		invitation.setInvitee(inveetee);
		if (language_id != null && Type.contact == invitation.getInvitee().getType()) {
			invitation.getInvitee().setLanguage_id(language_id);
		}
		invitation.setRoom(room);
		invitation.setInserted(new Date());
		invitation.setAppointment(appointment);

		return invitation;
	}
	
	/**
	 * @author vasya
	 * 
	 * @param member
	 * @param a
	 */
	public void processInvitation(Appointment a, MeetingMember member, MessageType type, String baseUrl) {
		processInvitation(a, member, type, baseUrl, true);
	}
	
	public void processInvitation(Appointment a, MeetingMember mm, MessageType type, String baseUrl, boolean sendMail) {
		if (a.getRemind() == null) {
			log.error("Appointment doesn't have reminder set!");
			return;
		}
		long remindType = a.getRemind().getTypId();
		if (remindType < 2) {
			log.error("MeetingMember should not have invitation!");
			return;
		}

		log.debug(":::: processInvitation ..... " + remindType);

		// appointment.getRemind().getTypId() == 1 will not receive emails
		if (remindType > 1) {
			log.debug("Invitation for Appointment : simple email");

			try {
				mm.setInvitation(getInvitation(mm.getInvitation()
						, mm.getUser(), a.getRoom(), a.isPasswordProtected(), a.getPassword()
						, Valid.Period, a.getOwner(), baseUrl, null, a.getStart(), a.getEnd(), a));
				if (sendMail) {
					sendInvitionLink(a, mm, type, remindType > 2);
				}
			} catch (Exception e) {
				log.error("Unexpected error while setting invitation", e);
			}
		}
	}

	private String formatSubject(Long langId, Appointment a, TimeZone tz) {
		String message = fieldManager.getString(1151L, langId) + " " + a.getTitle();

		message += " "
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz);

		message += " - "
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz);

		return message;
	}

	private String formatMessage(Long langId, Appointment a, TimeZone tz, String invitorName) {
		String message = fieldManager.getString(1151L, langId) + " " + a.getTitle();

		if (a.getDescription() != null &&  a.getDescription().length() != 0) {
			message += fieldManager.getString(1152L, langId) + a.getDescription();
		}

		message += "<br/>"
				+ fieldManager.getString(1153L, langId)
				+ ' '
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz)
				+ "<br/>";

		message += fieldManager.getString(1154L, langId)
				+ ' '
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz) + "<br/>";

		message += fieldManager.getString(1156L, langId) + invitorName + "<br/>";

		return message;
	}

	private String formatCancelSubject(Long langId, Appointment a, TimeZone tz) {
		String message = fieldManager.getString(1157L, langId) + a.getTitle();

		message += " "
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz)
				+ " - "
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz);

		return message;
	}

	private String formatCancelMessage(Long langId, Appointment a, TimeZone tz, String invitorName) {
		try {
			String message = fieldManager.getString(1157L, langId) + a.getTitle();

			if (a.getDescription() != null && a.getDescription().length() != 0) {
				message += fieldManager.getString(1152L, langId) + a.getDescription();
			}

			message += "<br/>"
					+ fieldManager.getString(1153L, langId)
					+ ' '
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz)
					+ "<br/>";

			message += fieldManager.getString(1154L, langId)
					+ ' '
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz)
					+ "<br/>";

			message += fieldManager.getString(1156L, langId) + invitorName + "<br/>";

			return message;
		} catch (Exception err) {
			log.error("Could not format cancel message", err);
			return "Error formatCancelMessage";
		}
	}

	private String formatUpdateSubject(Long langId, Appointment a, TimeZone tz) {
		String message = fieldManager.getString(1155L, langId) + " " + a.getTitle();

		message += " "
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz)
				+ " - "
				+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz);

		return message;
	}

	private String formatUpdateMessage(Long langId, Appointment a, TimeZone tz, String invitorName) {
		try {
			String message = fieldManager.getString(1155L, langId) + " " + a.getTitle();

			if (a.getDescription().length() != 0) {
				message += fieldManager.getString(1152L, langId) + a.getDescription();
			}

			message += "<br/>"
					+ fieldManager.getString(1153L, langId)
					+ ' '
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz)
					+ "<br/>";

			message += fieldManager.getString(1154L, langId)
					+ ' '
					+ CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz)
					+ "<br/>";

			message += fieldManager.getString(1156L, langId) + invitorName + "<br/>";

			return message;
		} catch (Exception err) {
			log.error("Could not format update message", err);
			return "Error formatUpdateMessage";
		}
	}

	/**
	 * @author vasya
	 * 
     * @param mm
     * @param a
     * @param message
     * @param baseurl
     * @param subject
	 * @throws Exception 
	 */
	private void sendInvitionLink(Appointment a, MeetingMember mm, MessageType type, boolean ical) throws Exception	{
		User owner = a.getOwner();
		String invitorName = owner.getFirstname() + " " + owner.getLastname();
		Long langId = mm.getUser().getLanguage_id();
		TimeZone tz = timezoneUtil.getTimeZone(mm.getUser());
		String subject = null;
		String message = null;
		switch (type) {
			case Cancel:
				subject = formatCancelSubject(langId, a, tz);
				message = formatCancelMessage(langId, a, tz, invitorName);
				break;
			case Create:
				subject = formatSubject(langId, a, tz);
				message = formatMessage(langId, a, tz, invitorName);
				break;
			case Update:
			default:
				subject = formatUpdateSubject(langId, a, tz);
				message = formatUpdateMessage(langId, a, tz, invitorName);
				break;
			
		}
		sendInvitionLink(mm.getInvitation(), type, subject, message, ical);
	}
	
	public void sendInvitionLink(Invitation i, MessageType type, String subject, String message, boolean ical) throws Exception {
		String invitation_link = i.getBaseUrl();
		if (i.getInvitee().getType() == Type.contact) {
			invitation_link += "?invitationHash=" + i.getHash();

            if (i.getInvitee().getLanguage_id() > 0) {
                invitation_link += "&language=" + i.getInvitee().getLanguage_id().toString();
            }
		} else {
			invitation_link = getRoomUrlFragment(i.getRoom().getRooms_id()).getLink(i.getBaseUrl());
		}
		User owner = i.getInvitedBy();
		
		String invitorName = owner.getFirstname() + " " + owner.getLastname();
		boolean isCanceled = (type==MessageType.Cancel); 
		String template = InvitationTemplate.getEmail(i.getInvitee().getLanguage_id(), invitorName, message, invitation_link, isCanceled);
		String email = i.getInvitee().getAdresses().getEmail();
		String replyToEmail = owner.getAdresses().getEmail();
		
		if (ical) {
			String username = i.getInvitee().getLogin();
			boolean isOwner = owner.getUser_id() == i.getInvitee().getUser_id();
			IcalHandler handler = new IcalHandler(MessageType.Cancel == type ? IcalHandler.ICAL_METHOD_CANCEL : IcalHandler.ICAL_METHOD_REQUEST);

			HashMap<String, String> attendeeList = handler.getAttendeeData(email, username, isOwner);

			Vector<HashMap<String, String>> atts = new Vector<HashMap<String, String>>();
			atts.add(attendeeList);

			// Defining Organizer

			HashMap<String, String> organizerAttendee = handler.getAttendeeData(email, username, isOwner);
			organizerAttendee = handler.getAttendeeData(replyToEmail, owner.getLogin(), isOwner);

			Appointment a = i.getAppointment();
			// Create ICal Message
			//FIXME should be checked to generate valid time
			String meetingId = handler.addNewMeeting(a.getStart(), a.getEnd(),
					a.getTitle(), atts, invitation_link,
					organizerAttendee, a.getIcalId(), timezoneUtil.getTimeZone(owner));

			// Writing back meetingUid
			if (a.getIcalId() == null || a.getIcalId().length() < 1) {
				a.setIcalId(meetingId);
				// TODO should it be saved ???
			}

			log.debug(handler.getICalDataAsString());
			mailHandler.send(new MailMessage(email, replyToEmail, subject, template, handler.getIcalAsByteArray()));
		} else {
			mailHandler.send(email, replyToEmail, subject, template);
		}
	}

	/**
	 * This method sends invitation reminder SMS
	 * @param phone user's phone
	 * @param subject 
	 * @return
	 */
	public boolean sendInvitationReminderSMS(String phone, String subject, long language_id) {
		if (phone != null && phone.length() > 0) {
			log.debug("sendInvitationReminderSMS to " + phone + ": " + subject);
			try {
				return smsHandler.sendSMS(phone, subject, language_id);
			} catch (Exception e) {
				log.error("sendInvitationReminderSMS", e);
			}
		}
		return false;
	}

	/**
	 * 
	 * @param hashCode
	 * @param hidePass
	 * @return
	 */
	public Object getInvitationByHashCode(String hashCode, boolean hidePass) {
		try {
			Invitation invitation = invitationDao.getInvitationByHashCode(hashCode, hidePass);

			if (invitation == null) {
				// already deleted or does not exist
				return new Long(-31);
			} else {
				switch (invitation.getValid()) {
					case OneTime:
						// do this only if the user tries to get the Invitation, not
						// while checking the PWD
						if (hidePass) {
							// one-time invitation
							if (invitation.isUsed()) {
								// Invitation is of type *only-one-time* and was
								// already used
								return new Long(-32);
							} else {
								// set to true if this is the first time / a normal
								// getInvitation-Query
								invitation.setUsed(true);
								invitationDao.update(invitation);
								// invitation.setInvitationpass(null);
								invitation.setAllowEntry(true);
							}
						} else {
							invitation.setAllowEntry(true);
						}
						break;
					case Period:
						TimeZone tz = timezoneUtil.getTimeZone(invitation.getInvitee());
						Calendar now = Calendar.getInstance(tz);
						Calendar start = Calendar.getInstance(tz);
						start.setTime(invitation.getValidFrom());

						Calendar end = Calendar.getInstance(tz);
						end.setTime(invitation.getValidTo());
						if (now.after(start) && now.before(end)) {
							invitationDao.update(invitation);
							// invitation.setInvitationpass(null);
							invitation.setAllowEntry(true);
						} else {

							// Invitation is of type *period* and is not valid
							// anymore, this is an extra hook to display the time
							// correctly
							// in the method where it shows that the hash code does
							// not work anymore
							invitation.setAllowEntry(false);
						}
						break;
					case Endless:
					default:
						invitationDao.update(invitation);

						invitation.setAllowEntry(true);
						// invitation.setInvitationpass(null);
						break;
				}
				return invitation;
			}

		} catch (Exception err) {
			log.error("[getInvitationByHashCode]", err);
		}
		return new Long(-1);
	}

	/**
	 * 
	 * @param hashCode
	 * @param pass
	 * @return
	 */
	public Object checkInvitationPass(String hashCode, String pass) {
		try {
			Object obj = this.getInvitationByHashCode(hashCode, false);
			log.debug("checkInvitationPass - obj: " + obj);
			if (obj instanceof Invitation) {
				Invitation invitation = (Invitation) obj;

				// log.debug("invitationId "+invitation.getInvitations_id());
				// log.debug("pass "+pass);
				// log.debug("getInvitationpass "+invitation.getInvitationpass());

				if (ManageCryptStyle.getInstanceOfCrypt().verifyPassword(pass, invitation.getPassword())) {
					return new Long(1);
				} else {
					return new Long(-34);
				}
			} else {
				return obj;
			}
		} catch (Exception ex2) {
			log.error("[checkInvitationPass] ", ex2);
		}
		return new Long(-1);
	}

	
}
