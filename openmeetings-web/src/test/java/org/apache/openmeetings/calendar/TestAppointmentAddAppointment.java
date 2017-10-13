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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
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
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestAppointmentAddAppointment extends AbstractWicketTester {
	private static final Logger log = Red5LoggerFactory.getLogger(TestAppointmentAddAppointment.class, getWebAppRootKey());

	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
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
	public void saveAppointment() throws Exception {
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
		String remind = Appointment.Reminder.ical.name();
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

		assertNotNull("Saved appointment should have valid id: " + a.getId(), a.getId());
	}

	@Test
	public void testCreate() {
		Appointment a = new Appointment();
		a.setTitle("Test title");
		setTime(a);
		a.setReminder(Reminder.ical);
		a.setMeetingMembers(new ArrayList<>());
		User owner = userDao.get(1L);
		a.setOwner(owner);
		a.setRoom(new Room());
		a.getRoom().setAppointment(true);
		a.getRoom().setType(Room.Type.conference);
		for (int i = 0; i < 3; ++i) {
			MeetingMember mm = new MeetingMember();
			mm.setUser(getContact(UUID.randomUUID().toString(), owner.getId()));
			a.getMeetingMembers().add(mm);
		}
		a = appointmentDao.update(a, owner.getId());
		assertNotNull("Saved appointment should have valid id: " + a.getId(), a.getId());
		assertEquals("Saved appointment should have corect count of guests: ", 3, a.getMeetingMembers().size());
		for (MeetingMember mm : a.getMeetingMembers()) {
			assertNotNull("Saved guest should have valid id: ", mm.getId());
			assertNotNull("Saved guest should have valid invitation: ", mm.getInvitation());
			assertNotNull("Saved guest should have invitation with ID: ", mm.getInvitation().getId());
		}

		WebSession ws = WebSession.get();
		Appointment a1 = appointmentDao.get(a.getId());
		ws.checkHashes(StringValue.valueOf(""), StringValue.valueOf(a1.getMeetingMembers().get(0).getInvitation().getHash()));
		assertTrue("Login via secure hash should be successful", ws.isSignedIn());
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
