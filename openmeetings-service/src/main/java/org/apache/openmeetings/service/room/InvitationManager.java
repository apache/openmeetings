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
package org.apache.openmeetings.service.room;

import static org.apache.openmeetings.util.CalendarHelper.getZoneId;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.wicketApplicationName;

import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.core.mail.SMSHandler;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.IInvitationManager;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.entity.basic.MailMessage;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.service.mail.template.AbstractAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.CanceledAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.CreatedAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.InvitationTemplate;
import org.apache.openmeetings.service.mail.template.UpdatedAppointmentTemplate;
import org.apache.openmeetings.util.CalendarHelper;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.util.mail.IcalHandler;
import org.apache.wicket.Application;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

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
	private MailHandler mailHandler;
	@Autowired
	private SMSHandler smsHandler;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private ConfigurationDao configDao;

	/**
	 * @author vasya
	 * 
	 * @param mm
	 * @param a
	 * @param message
	 * @param subject
	 * @throws Exception 
	 */
	private void sendInvitionLink(Appointment a, MeetingMember mm, MessageType type, boolean ical) throws Exception	{
		User owner = a.getOwner();
		String invitorName = owner.getFirstname() + " " + owner.getLastname();
		Long langId = mm.getUser().getLanguageId();
		TimeZone tz = timezoneUtil.getTimeZone(mm.getUser());
		AbstractAppointmentTemplate t = null;
		switch (type) {
			case Cancel:
				t = CanceledAppointmentTemplate.get(langId, a, tz, invitorName);
				break;
			case Create:
				t = CreatedAppointmentTemplate.get(langId, a, tz, invitorName);
				break;
			case Update:
			default:
				t = UpdatedAppointmentTemplate.get(langId, a, tz, invitorName);
				break;
			
		}
		sendInvitationLink(mm.getInvitation(), type, t.getSubject(), t.getEmail(), ical);
	}
	
	@Override
	public void sendInvitationLink(Invitation i, MessageType type, String subject, String message, boolean ical) throws Exception {
		String invitation_link = type == MessageType.Cancel ? null : ((IApplication)Application.get(wicketApplicationName)).getOmInvitationLink(configDao.getBaseUrl(), i); //TODO check for exceptions
		User owner = i.getInvitedBy();
		
		String invitorName = owner.getFirstname() + " " + owner.getLastname();
		String template = InvitationTemplate.getEmail(i.getInvitee().getLanguageId(), invitorName, message, invitation_link);
		String email = i.getInvitee().getAddress().getEmail();
		String replyToEmail = owner.getAddress().getEmail();
		
		if (ical) {
			String username = i.getInvitee().getLogin();
			boolean isOwner = owner.getId().equals(i.getInvitee().getId());
			IcalHandler handler = new IcalHandler(MessageType.Cancel == type ? IcalHandler.ICAL_METHOD_CANCEL : IcalHandler.ICAL_METHOD_REQUEST);

			Map<String, String> attendeeList = handler.getAttendeeData(email, username, isOwner);

			Vector<Map<String, String>> atts = new Vector<Map<String, String>>();
			atts.add(attendeeList);

			// Defining Organizer

			Map<String, String> organizerAttendee = handler.getAttendeeData(email, username, isOwner);
			organizerAttendee = handler.getAttendeeData(replyToEmail, owner.getLogin(), isOwner);

			Appointment a = i.getAppointment();
			// Create ICal Message
			String meetingId = handler.addNewMeeting(a.getStart(), a.getEnd(), a.getTitle(), atts, invitation_link,
					organizerAttendee, a.getIcalId(), timezoneUtil.getTimeZone(owner).getID());

			// Writing back meetingUid
			if (Strings.isEmpty(a.getIcalId())) {
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
	@Override
	public boolean sendInvitationReminderSMS(String phone, String subject, long languageId) {
		if (!Strings.isEmpty(phone)) {
			log.debug("sendInvitationReminderSMS to " + phone + ": " + subject);
			try {
				return smsHandler.sendSMS(phone, subject, languageId);
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
	@Override
	public Object getInvitationByHashCode(String hashCode, boolean hidePass) {
		try {
			log.debug("Invitation was requested by hashcode: " + hashCode);
			Invitation i = invitationDao.getInvitationByHashCode(hashCode, hidePass);

			if (i == null) {
				// already deleted or does not exist
				return new Long(-31);
			} else {
				switch (i.getValid()) {
					case OneTime:
						// do this only if the user tries to get the Invitation, not
						// while checking the PWD
						if (hidePass) {
							// one-time invitation
							if (i.isUsed()) {
								// Invitation is of type *only-one-time* and was
								// already used
								return new Long(-32);
							} else {
								// set to true if this is the first time / a normal
								// getInvitation-Query
								i.setUsed(true);
								invitationDao.update(i);
								// invitation.setInvitationpass(null);
								i.setAllowEntry(true);
							}
						} else {
							i.setAllowEntry(true);
						}
						break;
					case Period:
						LocalDateTime now = ZonedDateTime.now(getZoneId(i.getInvitee().getTimeZoneId())).toLocalDateTime();
						LocalDateTime from = CalendarHelper.getDateTime(i.getValidFrom(), i.getInvitee().getTimeZoneId());
						LocalDateTime to = CalendarHelper.getDateTime(i.getValidTo(), i.getInvitee().getTimeZoneId());
						if (now.isAfter(from) && now.isBefore(to)) {
							invitationDao.update(i);
							// invitation.setInvitationpass(null);
							i.setAllowEntry(true);
						} else {

							// Invitation is of type *period* and is not valid
							// anymore, this is an extra hook to display the time
							// correctly
							// in the method where it shows that the hash code does
							// not work anymore
							i.setAllowEntry(false);
						}
						break;
					case Endless:
					default:
						invitationDao.update(i);

						i.setAllowEntry(true);
						// invitation.setInvitationpass(null);
						break;
				}
				return i;
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
	@Override
	public Object checkInvitationPass(String hashCode, String pass) {
		try {
			Object obj = getInvitationByHashCode(hashCode, false);
			log.debug("checkInvitationPass - obj: " + obj);
			if (obj instanceof Invitation) {
				Invitation invitation = (Invitation) obj;

				if (CryptProvider.get().verify(pass, invitation.getPassword())) {
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
	
	/**
	 * @author vasya
	 * 
	 * @param member
	 * @param a
	 */
	@Override
	public void processInvitation(Appointment a, MeetingMember member, MessageType type) {
		processInvitation(a, member, type, true);
	}

	@Override
	public void processInvitation(Appointment a, MeetingMember mm, MessageType type, boolean sendMail) {
		Reminder reminder = a.getReminder();
		if (reminder == null) {
			log.error("Appointment doesn't have reminder set!");
			return;
		}
		if (Reminder.none == reminder) {
			log.error("MeetingMember should not have invitation!");
			return;
		}

		log.debug(":::: processInvitation ..... " + reminder);
		log.debug("Invitation for Appointment : simple email");
		try {
			mm.setInvitation(getInvitation(mm.getInvitation()
					, mm.getUser(), a.getRoom(), a.isPasswordProtected(), a.getPassword()
					, Valid.Period, a.getOwner(), null, a.getStart(), a.getEnd(), a));
			if (sendMail) {
				sendInvitionLink(a, mm, type, Reminder.ical == reminder);
			}
		} catch (Exception e) {
			log.error("Unexpected error while setting invitation", e);
		}
	}

	@Override
	public Invitation getInvitation(Invitation _invitation, User inveetee, Room room
			, boolean isPasswordProtected, String invitationpass, Valid valid,
			User createdBy, Long languageId, Date gmtTimeStart, Date gmtTimeEnd
			, Appointment appointment) {
		
		Invitation invitation = _invitation;
		if (null == _invitation) {
			invitation = new Invitation();
			invitation.setHash(UUID.randomUUID().toString());
		}

		invitation.setPasswordProtected(isPasswordProtected);
		if (isPasswordProtected) {
			invitation.setPassword(CryptProvider.get().hash(invitationpass));
		}

		invitation.setUsed(false);
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
		if (languageId != null && Type.contact == invitation.getInvitee().getType()) {
			invitation.getInvitee().setLanguageId(languageId);
		}
		invitation.setRoom(room);
		invitation.setInserted(new Date());
		invitation.setAppointment(appointment);

		return invitation;
	}

	@Override
	public Invitation getInvitation(User inveetee, Room room, boolean isPasswordProtected, String invitationpass, Valid valid,
			User createdBy, Long languageId, Date gmtTimeStart, Date gmtTimeEnd, Appointment appointment)
	{
		Invitation i = getInvitation((Invitation)null, inveetee, room, isPasswordProtected, invitationpass, valid, createdBy
				, languageId, gmtTimeStart, gmtTimeEnd, appointment);
		i = invitationDao.update(i);
		return i;
	}
}
