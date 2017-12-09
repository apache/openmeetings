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

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.mail.MailHandler;
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
import org.apache.openmeetings.service.mail.template.InvitationTemplate;
import org.apache.openmeetings.service.mail.template.subject.CanceledAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.subject.CreatedAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.subject.SubjectEmailTemplate;
import org.apache.openmeetings.service.mail.template.subject.UpdatedAppointmentTemplate;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.util.mail.IcalHandler;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author swagner
 *
 */
@Component
public class InvitationManager implements IInvitationManager {
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationManager.class, getWebAppRootKey());

	@Autowired
	private InvitationDao invitationDao;
	@Autowired
	private MailHandler mailHandler;

	/**
	 * @author vasya
	 *
	 * @param a - appointment this link is related to
	 * @param mm - attendee being processed
	 * @param type - type of the message
	 * @param ical - should iCal appoinment be attached to message
	 * @throws Exception in case of error happens during sending
	 */
	private void sendInvitionLink(Appointment a, MeetingMember mm, MessageType type, boolean ical) throws Exception	{
		User owner = a.getOwner();
		String invitorName = owner.getFirstname() + " " + owner.getLastname();
		TimeZone tz = getTimeZone(mm.getUser());
		SubjectEmailTemplate t;
		switch (type) {
			case Cancel:
				t = CanceledAppointmentTemplate.get(mm.getUser(), a, tz, invitorName);
				break;
			case Create:
				t = CreatedAppointmentTemplate.get(mm.getUser(), a, tz, invitorName);
				break;
			case Update:
			default:
				t = UpdatedAppointmentTemplate.get(mm.getUser(), a, tz, invitorName);
				break;
		}
		sendInvitationLink(mm.getInvitation(), type, t.getSubject(), t.getEmail(), ical);
	}

	@Override
	public void sendInvitationLink(Invitation i, MessageType type, String subject, String message, boolean ical) throws Exception {
		String invitationLink = null;
		if (type != MessageType.Cancel) {
			IApplication app = ensureApplication(1L);
			invitationLink = app.getOmInvitationLink(i);
		}
		User owner = i.getInvitedBy();

		String invitorName = owner.getFirstname() + " " + owner.getLastname();
		String template = InvitationTemplate.getEmail(i.getInvitee(), invitorName, message, invitationLink);
		String email = i.getInvitee().getAddress().getEmail();
		String replyToEmail = owner.getAddress().getEmail();

		if (ical) {
			String username = i.getInvitee().getLogin();
			boolean isOwner = owner.getId().equals(i.getInvitee().getId());
			IcalHandler handler = new IcalHandler(MessageType.Cancel == type ? IcalHandler.ICAL_METHOD_CANCEL : IcalHandler.ICAL_METHOD_REQUEST);

			Map<String, String> attendeeList = handler.getAttendeeData(email, username, isOwner);

			List<Map<String, String>> atts = new ArrayList<>();
			atts.add(attendeeList);

			// Defining Organizer

			Map<String, String> organizerAttendee = handler.getAttendeeData(replyToEmail, owner.getLogin(), isOwner);

			Appointment a = i.getAppointment();
			// Create ICal Message
			String meetingId = handler.addNewMeeting(a.getStart(), a.getEnd(), a.getTitle(), atts, invitationLink,
					organizerAttendee, a.getIcalId(), getTimeZone(owner).getID());

			// Writing back meetingUid
			if (Strings.isEmpty(a.getIcalId())) {
				a.setIcalId(meetingId);
			}

			log.debug(handler.getICalDataAsString());
			mailHandler.send(new MailMessage(email, replyToEmail, subject, template, handler.getIcalAsByteArray()));
		} else {
			mailHandler.send(email, replyToEmail, subject, template);
		}
	}

	/**
	 * @author vasya
	 *
	 * @param a - appointment this link is related to
	 * @param member - attendee being processed
	 * @param type - type of the message
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
