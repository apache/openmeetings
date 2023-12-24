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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Calendar;
import java.util.Date;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;


class TestGetAppointment extends AbstractOmServerTest {
	private static final Logger log = LoggerFactory.getLogger(TestGetAppointment.class);

	@Inject
	private RoomDao roomDao;

	@Test
	void getAppoinment() {
		log.debug("getAppoinment enter");
		Long userId = 1L;

		Calendar now = Calendar.getInstance();
		Calendar a1End = Calendar.getInstance();
		a1End.setTime(now.getTime());
		a1End.add(Calendar.HOUR_OF_DAY, 1);
		Appointment a1 = getAppointment(now.getTime(), a1End.getTime());
		a1.setTitle("GetAppointment");

		a1 = appointmentDao.update(a1, userId);

		Appointment a = appointmentDao.get(a1.getId());
		assertNotNull(a, "Failed to get Appointment By id");
		assertEquals(0, a.getMeetingMembers() == null ? 0 : a.getMeetingMembers().size(), "Inapropriate MeetingMembers count");
	}

	@Test
	void testGetByRoom() {
		Date start = new Date();
		Appointment a = createAppointment(getAppointment(userDao.get(1L), roomDao.get(5L), start, new Date(start.getTime() + ONE_HOUR)));
		Appointment a1 = appointmentDao.getByRoom(1L, 5L);
		assertNotNull(a1, "Created appointment should be found");
		assertEquals(a.getId(), a1.getId());
	}
}
