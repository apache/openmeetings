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
package org.apache.openmeetings.web.backup;

import static org.apache.openmeetings.web.backup.TestImport.BACKUP_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.calendar.OmCalendarDao;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;


class TestImportCalendar extends AbstractTestImport {
	@Inject
	private OmCalendarDao calendarDao;
	@Inject
	private MeetingMemberDao meetingMemberDao;

	@Test
	void importCalendars() throws Exception {
		long calCount = calendarDao.get().size();
		File cals = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "calendar/calendars.xml").toURI());
		backupImport.importCalendars(cals.getParentFile());
		assertEquals(calCount + 1, calendarDao.get().size(), "Calendars should be added");
	}

	@Test
	void importAppointmentsSkip() throws Exception {
		long appCount = appointmentDao.get().size();
		File apps = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "calendar/skip/appointements.xml").toURI());
		backupImport.importAppointments(apps.getParentFile());
		assertEquals(appCount, appointmentDao.get().size(), "No Appointments should be added");
	}

	@Test
	void importAppointments() throws Exception {
		long appCount = appointmentDao.get().size();
		File apps = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "calendar/appointements.xml").toURI());
		backupImport.importAppointments(apps.getParentFile());
		assertEquals(appCount + 2, appointmentDao.get().size(), "Appointments should be added");

		long mmCount = meetingMemberDao.get().size();
		backupImport.importMeetingMembers(apps.getParentFile());
		assertEquals(mmCount + 1, meetingMemberDao.get().size(), "Meeting members should be added");
	}
}
