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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.entity.calendar.Appointment.allowedStart;
import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.CalendarHelper.getZoneDateTime;

import java.util.Date;
import java.util.TimeZone;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.mail.MailHandler;
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
import org.apache.openmeetings.db.manager.IInvitationManager;
import org.apache.openmeetings.service.mail.template.InvitationTemplate;
import org.apache.openmeetings.service.mail.template.subject.CanceledAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.subject.CreatedAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.subject.SubjectEmailTemplate;
import org.apache.openmeetings.service.mail.template.subject.UpdatedAppointmentTemplate;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.util.mail.IcalHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

/**
 *
 * @author swagner
 *
 */
@Component
public class InvitationManager implements IInvitationManager {
	private static final Logger log = LoggerFactory.getLogger(InvitationManager.class);

	@Inject
	private InvitationDao invitationDao;
	@Inject
	private MailHandler mailHandler;

	/**
	 * @author vasya
	 *
	 * @param a - appointment this link is related to
	 * @param mm - attendee being processed
	 * @param type - type of the message
	 * @param ical - should iCal appoinment be attached to message
	 */
	private void sendInvitionLink(Appointment a, MeetingMember mm, MessageType type, boolean ical) {
		User owner = a.getOwner();
		String invitorName = owner.getDisplayName();
		TimeZone tz = getTimeZone(mm.getUser());
		SubjectEmailTemplate t;
		switch (type) {
			case CANCEL:
				t = CanceledAppointmentTemplate.get(mm.getUser(), a, tz, invitorName);
				break;
			case CREATE:
				t = CreatedAppointmentTemplate.get(mm.getUser(), a, tz, invitorName);
				break;
			case UPDATE:
			default:
				t = UpdatedAppointmentTemplate.get(mm.getUser(), a, tz, invitorName);
				break;
		}
		sendInvitationLink(mm.getInvitation(), type, t.getSubject(), t.getEmail(), ical, null);
	}

	@Override
	public void sendInvitationLink(Invitation i, MessageType type, String subject, String message, boolean ical, String baseUrl) {
		final String invitationLink;
		if (type != MessageType.CANCEL) {
			IApplication app = ensureApplication(1L);
			invitationLink = app.getOmInvitationLink(i, baseUrl);
		} else {
			invitationLink = null;
		}
		User owner = i.getInvitedBy();

		String invitorName = owner.getDisplayName();
		String template = InvitationTemplate.getEmail(i.getInvitee(), invitorName, message, invitationLink, i.getRoom() != null);
		String email = i.getInvitee().getAddress().getEmail();
		String replyToEmail = owner.getAddress().getEmail();

		if (ical) {
			boolean isOwner = owner.getId().equals(i.getInvitee().getId());
			Appointment a = i.getAppointment();
			String desc = a.getDescription() == null ? "" : a.getDescription();
			if (invitationLink != null) {
				desc += (desc.isEmpty() ? "" : "\n\n\n") + invitationLink;
			}
			String tzid = getTimeZone(owner).getID();
			IcalHandler handler = new IcalHandler(MessageType.CANCEL == type ? IcalHandler.ICAL_METHOD_CANCEL : IcalHandler.ICAL_METHOD_REQUEST)
					.createVEvent(getZoneDateTime(a.getStart(), tzid), getZoneDateTime(a.getEnd(), tzid), a.getTitle())
					.addOrganizer(replyToEmail, owner.getDisplayName())
					.setUid(a.getIcalId())
					.addAttendee(email, i.getInvitee().getDisplayName(), isOwner)
					.setCreated(getZoneDateTime(a.getInserted(), tzid))
					.setDescription(desc)
					.setModified(getZoneDateTime(a.getUpdated(), tzid))
					.setLocation(a.getLocation())
					.setSequence(0)
					.build();

			log.debug("IcalHandler {}", handler);
			mailHandler.send(new MailMessage(email, replyToEmail, subject, template, handler));
		} else {
			mailHandler.send(email, replyToEmail, subject, template);
		}
	}

	/**
	 * Setting invitation for the appointment, sending emails if needed
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
		if (Reminder.NONE == reminder) {
			log.error("MeetingMember should not have invitation!");
			return;
		}

		log.debug(":::: processInvitation ..... {}", reminder);
		log.debug("Invitation for Appointment : simple email");
		try {
			mm.setInvitation(getInvitation(mm.getInvitation()
					, mm.getUser(), a.getRoom(), a.isPasswordProtected(), a.getPassword()
					, Valid.PERIOD, a.getOwner(), null, a.getStart(), a.getEnd(), a));
			if (sendMail) {
				sendInvitionLink(a, mm, type, Reminder.ICAL == reminder);
			}
		} catch (Exception e) {
			log.error("Unexpected error while setting invitation", e);
		}
	}

	@Override
	public Invitation getInvitation(Invitation inInvitation, User inveetee, Room room
			, boolean isPasswordProtected, String invitationpass, Valid valid
			, User createdBy, Long languageId, Date mmStart, Date mmEnd
			, Appointment appointment) {

		Invitation invitation = inInvitation;
		if (null == inInvitation) {
			invitation = new Invitation();
			invitation.setHash(randomUUID().toString());
		}

		invitation.setPasswordProtected(isPasswordProtected);
		if (isPasswordProtected) {
			invitation.setPassword(CryptProvider.get().hash(invitationpass));
		}

		invitation.setUsed(false);
		invitation.setValid(valid);

		// valid period of Invitation
		switch (valid) {
			case PERIOD:
				invitation.setValidFrom(allowedStart(mmStart));
				invitation.setValidTo(mmEnd);
				break;
			case ENDLESS, ONE_TIME:
			default:
				break;
		}

		invitation.setDeleted(false);

		invitation.setInvitedBy(createdBy);
		invitation.setInvitee(inveetee);
		if (languageId != null && Type.CONTACT == invitation.getInvitee().getType()) {
			invitation.getInvitee().setLanguageId(languageId);
		}
		invitation.setRoom(room);
		invitation.setAppointment(appointment);

		return invitation;
	}

	@Override
	public Invitation getInvitation(User inveetee, Room room, boolean isPasswordProtected, String invitationpass, Valid valid,
			User createdBy, Long languageId, Date mmStart, Date mmEnd, Appointment appointment)
	{
		Invitation i = getInvitation((Invitation)null, inveetee, room, isPasswordProtected, invitationpass, valid, createdBy
				, languageId, mmStart, mmEnd, appointment);
		i = invitationDao.update(i);
		return i;
	}
}
