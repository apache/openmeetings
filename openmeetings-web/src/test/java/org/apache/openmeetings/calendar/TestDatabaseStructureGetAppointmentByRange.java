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
package org.apache.openmeetings.calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestDatabaseStructureGetAppointmentByRange extends AbstractOmServerTest {
	private static final Logger log = LoggerFactory.getLogger(TestDatabaseStructureGetAppointmentByRange.class);

	@Test
	void test() {
		log.debug("Test started");
		Long userId = 1L;

		Calendar now = Calendar.getInstance();
		Calendar rangeStart = Calendar.getInstance();
		rangeStart.setTime(now.getTime());
		rangeStart.add(Calendar.DATE, -1);
		Calendar rangeEnd = Calendar.getInstance();
		rangeEnd.add(Calendar.DATE, 1);
		rangeEnd.setTime(now.getTime());

		Calendar a1End = Calendar.getInstance();
		a1End.setTime(now.getTime());
		a1End.add(Calendar.HOUR_OF_DAY, 1);
		Appointment a1 = getAppointment(now.getTime(), a1End.getTime());
		a1.setTitle("AppointmentByRange_a1");

		Appointment a2 = getAppointment(now.getTime(), a1End.getTime());
		a2.setTitle("AppointmentByRange_a2");
		a2.setMeetingMembers(new ArrayList<MeetingMember>());
		MeetingMember mm1 = new MeetingMember();
		mm1.setUser(createUserContact(userId));
		mm1.setAppointment(a2);
		a2.getMeetingMembers().add(mm1);

		Appointment a3 = getAppointment(now.getTime(), a1End.getTime());
		a3.setTitle("AppointmentByRange_a3");
		a3.setMeetingMembers(new ArrayList<MeetingMember>());
		MeetingMember mm2 = new MeetingMember();
		mm2.setUser(createUserContact(userId));
		mm2.setAppointment(a3);
		a3.getMeetingMembers().add(mm2);
		MeetingMember mm3 = new MeetingMember();
		mm3.setUser(createUserContact(userId));
		mm3.setAppointment(a3);
		a3.getMeetingMembers().add(mm3);

		a1 = appointmentDao.update(a1, userId);
		a2 = appointmentDao.update(a2, userId);
		a3 = appointmentDao.update(a3, userId);

		int a1found = 0, a2found = 0, a3found = 0;
		for (Appointment a : appointmentDao.getInRange(userId, rangeStart.getTime(), rangeEnd.getTime())) {
			int mmCount = a.getMeetingMembers() == null ? 0 : a.getMeetingMembers().size();
			if (a.getId().equals(a1.getId())) {
				assertEquals(0, mmCount, "Inapropriate MeetingMembers count");
				a1found++;
			}
			if (a.getId().equals(a2.getId())) {
				assertEquals(1, mmCount, "Inapropriate MeetingMembers count");
				a2found++;
			}
			if (a.getId().equals(a3.getId())) {
				assertEquals(2, mmCount, "Inapropriate MeetingMembers count");
				a3found++;
			}
		}
		assertEquals(1, a1found, "Inappropriate count of appointments without members found");
		assertEquals(1, a2found, "Inappropriate count of appointments with 1 member found");
		assertEquals(1, a3found, "Inappropriate count of appointments with 2 members found");
	}
}
