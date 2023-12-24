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

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.calendar.AppointmentLogic;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;


class TestAppointmentAddAppointment extends AbstractWicketTesterTest {
	private static final Logger log = LoggerFactory.getLogger(TestAppointmentAddAppointment.class);

	@Inject
	private AppointmentLogic appointmentLogic;
	@Inject
	private MeetingMemberDao meetingMemberDao;

	private static void setTime(Appointment a) {
		a.setStart(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		a.setEnd(Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant()));
	}

	public MeetingMember getMeetingMember(Long userId, Long langId, String str) {
		String[] params = str.split(",");

		try {
			return meetingMemberDao.get(Long.valueOf(params[0]));
		} catch (Exception e) {
			//no-op
		}
		MeetingMember mm = new MeetingMember();
		try {
			mm.setUser(userDao.get(Long.valueOf(params[4])));
		} catch (Exception e) {
			//no-op
		}
		if (mm.getUser() == null) {
			mm.setUser(userDao.getContact(params[3], params[1], params[2], langId, params[5], userId));
		}

		return mm;
	}

	@Test
	void saveAppointment() throws Exception {
		log.debug("- saveAppointment");

		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(start.getTimeInMillis() + 600000);

		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(start.getTimeInMillis() + 600000);

		String appointmentName = "Test 01";
		String appointmentDescription = "Descr";
		Long userId = 1L;
		String appointmentLocation = "office";
		Boolean isMonthly = false;
		Boolean isDaily = false;
		Boolean isWeekly = false;
		String remind = Appointment.Reminder.ICAL.name();
		Boolean isYearly = false;
		String[] mmClient = new String[1];
		for (int i = 0; i < 1; i++) {
			mmClient[0] = createClientObj("firstname" + i, "lastname" + i,
					"first" + i + ".last" + i + "@webbase-design.de", "Etc/GMT+1");
		}
		Long languageId = 1L;
		Long roomType = 1L;

		Appointment a = new Appointment();
		a.setTitle(appointmentName);
		a.setLocation(appointmentLocation);
		a.setDescription(appointmentDescription);
		a.setStart(start.getTime());
		a.setEnd(end.getTime());
		a.setIsDaily(isDaily);
		a.setIsWeekly(isWeekly);
		a.setIsMonthly(isMonthly);
		a.setIsYearly(isYearly);
		a.setReminder(Reminder.valueOf(remind));
		a.setRoom(new Room());
		a.getRoom().setComment(appointmentDescription);
		a.getRoom().setName(appointmentName);
		a.getRoom().setType(Room.Type.get(roomType));
		a.getRoom().setAppointment(true);
		a.setOwner(userDao.get(userId));
		a.setPasswordProtected(false);
		a.setPassword("");
		a.setMeetingMembers(new ArrayList<MeetingMember>());
		for (String singleClient : mmClient) {
			if (Strings.isEmpty(singleClient)) {
				continue;
			}
			MeetingMember mm = getMeetingMember(userId, languageId, singleClient);
			mm.setAppointment(a);
			a.getMeetingMembers().add(mm);
		}
		a = appointmentDao.update(a, userId);

		Thread.sleep(3000);

		appointmentLogic.doScheduledMeetingReminder();

		Thread.sleep(3000);

		assertNotNull(a.getId(), "Saved appointment should have valid id");
	}

	@Test
	void testCreate() {
		Appointment a = new Appointment();
		a.setTitle("Test title");
		setTime(a);
		a.setReminder(Reminder.ICAL);
		a.setMeetingMembers(new ArrayList<>());
		User owner = userDao.get(1L);
		a.setOwner(owner);
		a.setRoom(new Room());
		a.getRoom().setAppointment(true);
		a.getRoom().setType(Room.Type.CONFERENCE);
		for (int i = 0; i < 3; ++i) {
			MeetingMember mm = new MeetingMember();
			mm.setUser(getContact(randomUUID().toString(), owner.getId()));
			a.getMeetingMembers().add(mm);
		}
		a = appointmentDao.update(a, owner.getId());
		assertNotNull(a.getId(), "Saved appointment should have valid id");
		assertEquals(3, a.getMeetingMembers().size(), "Saved appointment should have corect count of guests");
		for (MeetingMember mm : a.getMeetingMembers()) {
			assertNotNull(mm.getId(), "Saved guest should have valid id");
			assertNotNull(mm.getInvitation(), "Saved guest should have valid invitation");
			assertNotNull(mm.getInvitation().getId(), "Saved guest should have invitation with ID");
		}

		WebSession ws = WebSession.get();
		Appointment a1 = appointmentDao.get(a.getId());
		ws.checkHashes(StringValue.valueOf(""), StringValue.valueOf(a1.getMeetingMembers().get(0).getInvitation().getHash()));
		assertTrue(ws.isSignedIn(), "Login via secure hash should be successful");
	}

	private static String createClientObj(String firstname, String lastname, String email, String jNameTimeZone) {
		StringBuilder sb = new StringBuilder();
		sb.append(",") //memberId
			.append(firstname).append(",")
			.append(lastname).append(",")
			.append(email).append(",")
			.append(",") //userId
			.append(jNameTimeZone);
		return sb.toString();
	}
}
