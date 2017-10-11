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
package org.apache.openmeetings.invitiation;

import static org.apache.openmeetings.util.CalendarHelper.getDate;

import java.time.LocalDateTime;
import java.util.TimeZone;

import org.apache.directory.api.util.Strings;
import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.mail.template.subject.AppointmentReminderTemplate;
import org.apache.openmeetings.service.mail.template.subject.CanceledAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.subject.CreatedAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.subject.SubjectEmailTemplate;
import org.apache.openmeetings.service.mail.template.subject.UpdatedAppointmentTemplate;
import org.apache.openmeetings.service.room.InvitationManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestInvitation extends AbstractWicketTester {
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private RoomDao roomDao;

	@Test
	public void testSendInvitationLink() throws Exception {
		User us = userDao.getByLogin(adminUsername, User.Type.user, null);

		LocalDateTime from = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0);
		User invitee = userDao.getContact("sebawagner@apache.org", "Testname", "Testlastname", us.getId());
		Invitation i = invitationManager.getInvitation(invitee, roomDao.get(1L),
				false, "", Valid.OneTime
				, us, us.getLanguageId(),
				getDate(from, "GMT"), getDate(from.plusHours(2), "GMT"), null);

		invitationManager.sendInvitationLink(i, MessageType.Create, "subject", "message", false);
	}

	private static void checkTemplate(SubjectEmailTemplate t) {
		Assert.assertNotNull("Template should be created", t);
		Assert.assertFalse("Subject should be not empty", Strings.isEmpty(t.getSubject()));
		Assert.assertFalse("Boby should be not empty", Strings.isEmpty(t.getEmail()));
	}

	@Test
	public void testTemplateGeneration() {
		Appointment a = getAppointment();
		String[] ids = TimeZone.getAvailableIDs();
		for (User u : userDao.get(0, 100)) {
			TimeZone tz = TimeZone.getTimeZone(ids[rnd.nextInt(ids.length)]);
			checkTemplate(CreatedAppointmentTemplate.get(u, a, tz, u.getLogin()));
			checkTemplate(CanceledAppointmentTemplate.get(u, a, tz, u.getLogin()));
			checkTemplate(UpdatedAppointmentTemplate.get(u, a, tz, u.getLogin()));
			checkTemplate(AppointmentReminderTemplate.get(u, a, tz));
		}
	}
}
