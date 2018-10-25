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
package org.apache.openmeetings.service.mail.template.subject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Consumer;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.string.Strings;
import org.junit.Test;

public class TestSubjTemplate extends AbstractWicketTester {
	private static void checkTemplate(SubjectEmailTemplate t) {
		assertNotNull("Template should be created", t);
		assertFalse("Subject should be not empty", Strings.isEmpty(t.getSubject()));
		assertFalse("Body should be not empty", Strings.isEmpty(t.getEmail()));
	}

	@Test
	public void testTemplateGeneration() {
		Appointment a = getAppointment();
		String[] ids = TimeZone.getAvailableIDs();
		Recording rec = new Recording();
		rec.setRoomId(5L);
		for (User u : userDao.get(0, 100)) {
			TimeZone tz = TimeZone.getTimeZone(ids[rnd.nextInt(ids.length)]);
			checkTemplate(CreatedAppointmentTemplate.get(u, a, tz, u.getLogin()));
			checkTemplate(CanceledAppointmentTemplate.get(u, a, tz, u.getLogin()));
			checkTemplate(UpdatedAppointmentTemplate.get(u, a, tz, u.getLogin()));
			checkTemplate(AppointmentReminderTemplate.get(u, a, tz));
			checkTemplate(RecordingExpiringTemplate.get(u, rec, 1L));
		}
	}

	private static void checkTemplateError(SubjectEmailTemplate t, Consumer<SubjectEmailTemplate> cons) {
		try {
			cons.accept(t);
			fail("RuntimeException is expected");
		} catch (RuntimeException ex) {
			assertTrue("Expected", true);
		}
	}

	@Test
	public void testError() {
		InvitedAppointmentTemplate t = new InvitedAppointmentTemplate(Locale.CHINA, new Appointment(), TimeZone.getDefault(), "TEST") {
			private static final long serialVersionUID = 1L;

			@Override
			String getPrefix() {
				return null;
			}
		};
		checkTemplateError(t, tmp -> tmp.getSubject());
		checkTemplateError(t, tmp -> tmp.getEmail());
	}
}
