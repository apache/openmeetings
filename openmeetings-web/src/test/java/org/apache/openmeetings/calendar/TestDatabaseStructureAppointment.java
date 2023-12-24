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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.util.CalendarHelper;
import org.junit.jupiter.api.Test;

class TestDatabaseStructureAppointment extends AbstractOmServerTest {
	private static String getTzId() {
		return TimeZone.getDefault().getID();
	}

	private static Date getDate(int hour, int minute) {
		return CalendarHelper.getDate(LocalDateTime.of(2008, 8, 17, 12, 28), getTzId());
	}

	private void createAppointment(int startHour, int startMinute, int endHour, int endMinute) {
		Appointment a = getAppointment(getDate(startHour, startMinute), getDate(endHour, endMinute));
		appointmentDao.update(a, a.getOwner().getId());
	}

	@Test
	void testAddingGroup() throws Exception {
		Date date = CalendarHelper.getDate(LocalDate.of(2008, 8, 17), getTzId());
		Date date2 = CalendarHelper.getDate(LocalDate.of(2008, 8, 18), getTzId());

		createAppointment(12, 28, 23, 15);
		createAppointment(1, 1, 3, 52);

		List<Appointment> listAppoints = appointmentDao.getInRange(1L, date, date2);
		assertEquals(2, listAppoints.size(), "Exactly 2 appointments should retrieved");
	}
}
