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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.room.InvitationManager;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;


class TestInvitation extends AbstractWicketTesterTest {
	@Inject
	private InvitationDao inviteDao;
	@Inject
	private InvitationManager invitationManager;
	@Inject
	private RoomDao roomDao;

	private Invitation create(Valid valid) {
		return create(valid, false);
	}

	private Invitation create(Valid valid, boolean createApp) {
		LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0);
		User us = userDao.getByLogin(adminUsername, User.Type.USER, null);

		Date from = getDate(base, "GMT");
		Date to = getDate(base.plusHours(2), "GMT");
		Appointment app = null;
		Room r = roomDao.get(1L);
		if (createApp) {
			app = getAppointment(us, r, from, to);
			appointmentDao.update(app, null, false);
		}
		return invitationManager.getInvitation(
				userDao.getContact("sebawagner@apache.org", "Testname", "Testlastname", us.getId())
				, r
				, false, "", valid
				, us, us.getLanguageId(),
				from, to, app);
	}


	@Test
	void testSendInvitationLink() throws Exception {
		Invitation i = create(Valid.ONE_TIME);

		invitationManager.sendInvitationLink(i, MessageType.CREATE, "subject", "message", false, null);
		assertNotNull(i);
	}

	@Test
	void testSendCancel() throws Exception {
		Invitation i = create(Valid.ONE_TIME, true);

		invitationManager.sendInvitationLink(i, MessageType.CANCEL, "subject", "message", true, "https://localhost:5443/openmeetings");
		assertNotNull(i);
	}

	@Test
	void testGetByHash() {
		final Invitation i1 = create(Valid.ENDLESS);
		final String hash = i1.getHash();

		assertNull(inviteDao.getByHash(hash.substring(0, 2) + '%', false));
		assertNotNull(inviteDao.getByHash(hash, false));
	}
}
