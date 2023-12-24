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
package org.apache.openmeetings.web.service.mail.template.subject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Consumer;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.mail.template.subject.SubjectEmailTemplate;
import org.apache.openmeetings.service.mail.template.subject.CreatedAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.subject.CanceledAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.subject.UpdatedAppointmentTemplate;
import org.apache.openmeetings.service.mail.template.subject.AppointmentReminderTemplate;
import org.apache.openmeetings.service.mail.template.subject.RecordingExpiringTemplate;
import org.apache.openmeetings.service.mail.template.subject.NewGroupUsersNotificationTemplate;
import org.apache.openmeetings.service.mail.template.subject.InvitedAppointmentTemplate;
import org.apache.wicket.util.string.Strings;
import org.junit.jupiter.api.Test;

class TestSubjTemplate extends AbstractWicketTesterTest {
	private static void checkTemplate(SubjectEmailTemplate t) {
		assertNotNull(t, "Template should be created");
		assertFalse(Strings.isEmpty(t.getSubject()), "Subject should be not empty");
		assertFalse(Strings.isEmpty(t.getEmail()), "Body should be not empty");
	}

	@Test
	void testTemplateGeneration() {
		Appointment a = getAppointment();
		String[] ids = TimeZone.getAvailableIDs();
		Recording rec = new Recording();
		rec.setRoomId(5L);
		List<User> users = new ArrayList<>(userDao.get(0, 100));
		User en = new User();
		en.setLanguageId(1L); // ltr
		User ar = new User();
		ar.setLanguageId(14L); // rtl, arabic
		users.add(en);
		users.add(ar);
		Group g = new Group();
		g.setName("Template test");
		for (User u : users) {
			TimeZone tz = TimeZone.getTimeZone(ids[rnd.nextInt(ids.length)]);
			checkTemplate(CreatedAppointmentTemplate.get(u, a, tz, u.getLogin()));
			checkTemplate(CanceledAppointmentTemplate.get(u, a, tz, u.getLogin()));
			checkTemplate(UpdatedAppointmentTemplate.get(u, a, tz, u.getLogin()));
			checkTemplate(AppointmentReminderTemplate.get(u, a, tz));
			checkTemplate(RecordingExpiringTemplate.get(u, rec, 1L));
			checkTemplate(NewGroupUsersNotificationTemplate.get(u, g, 18L));
		}
	}

	private static void checkTemplateError(SubjectEmailTemplate t, Consumer<SubjectEmailTemplate> cons) {
		try {
			cons.accept(t);
			fail("RuntimeException is expected");
		} catch (RuntimeException ex) {
			assertTrue(true, "Expected");
		}
	}

	@Test
	void testError() {
		InvitedAppointmentTemplate t = new InvitedAppointmentTemplate(Locale.CHINA, new Appointment(), TimeZone.getDefault(), "TEST") {
			private static final long serialVersionUID = 1L;

			@Override
			public String getPrefix() {
				return null;
			}
		};
		checkTemplateError(t, tmp -> tmp.getSubject());
		checkTemplateError(t, tmp -> tmp.getEmail());
	}
}
