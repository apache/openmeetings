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
package org.apache.openmeetings.test.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.webservice.util.DateParamConverter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestCalendarService extends AbstractWebServiceTest {
	public final static String CALENDAR_SERVICE_URL = BASE_SERVICES_URL + "/calendar";
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private RoomDao roomDao;

	private void actualTest(Room r) throws Exception {
		String uuid = UUID.randomUUID().toString();
		User u = getUser(uuid);
		u.getGroupUsers().add(new GroupUser(groupDao.get(1L), u));
		u = createUser(u);
		ServiceResult sr = login(u.getLogin(), getRandomPass(uuid));

		Date start = new Date();
		Appointment a = createAppointment(getAppointment(u, r, start, new Date(start.getTime() + ONE_HOUR)));

		AppointmentDTO app = getClient(CALENDAR_SERVICE_URL).path("/room/" + a.getRoom().getId()).query("sid", sr.getMessage())
				.get(AppointmentDTO.class);
		assertNotNull("Valid DTO should be returned", app);
	}

	@Test
	public void testGetByAppRoom() throws Exception {
		actualTest(null);
	}

	@Test
	public void testGetByPublicRoom() throws Exception {
		actualTest(roomDao.get(5L)); //default public restricted room
	}

	@Test
	public void testDateConverter() throws Exception {
		assertEquals("Null date should be parsed", null, DateParamConverter.get(null));
		assertEquals("Date should be parsed"
				, Date.from(LocalDate.of(2017, 01, 15).atStartOfDay(ZoneId.systemDefault()).toInstant())
				, DateParamConverter.get("2017-01-15"));
	}
}
