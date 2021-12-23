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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.openmeetings.AbstractWicketTesterTest;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.room.InvitationManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestInvitation extends AbstractWicketTesterTest {
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private RoomDao roomDao;

	@Test
	void testSendInvitationLink() throws Exception {
		User us = userDao.getByLogin(adminUsername, User.Type.USER, null);

		LocalDateTime from = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0);
		User invitee = userDao.getContact("sebawagner@apache.org", "Testname", "Testlastname", us.getId());
		Invitation i = invitationManager.getInvitation(invitee, roomDao.get(1L),
				false, "", Valid.ONE_TIME
				, us, us.getLanguageId(),
				getDate(from, "GMT"), getDate(from.plusHours(2), "GMT"), null);

		invitationManager.sendInvitationLink(i, MessageType.CREATE, "subject", "message", false, null);
		assertNotNull(i);
	}

	@Test
	void testSendCancel() throws Exception {
		User us = userDao.getByLogin(adminUsername, User.Type.USER, null);

		LocalDateTime from = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0);
		Date start = getDate(from, "GMT");
		Date end = getDate(from.plusHours(2), "GMT");
		Room r = roomDao.get(1L);
		User invitee = userDao.getContact("sebawagner@apache.org", "Testname", "Testlastname", us.getId());
		Appointment app = getAppointment(us, r, start, end);
		appointmentDao.update(app, null, false);
		Invitation i = invitationManager.getInvitation(invitee, r,
				false, "", Valid.ONE_TIME
				, us, us.getLanguageId(),
				start, end, app);

		invitationManager.sendInvitationLink(i, MessageType.CANCEL, "subject", "message", true, "https://localhost:5443/openmeetings");
		assertNotNull(i);
	}
}
