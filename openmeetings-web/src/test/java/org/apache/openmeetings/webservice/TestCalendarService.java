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
package org.apache.openmeetings.webservice;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.web.AbstractOmServerTest.ONE_HOUR;
import static org.apache.openmeetings.web.AbstractOmServerTest.createPass;
import static org.apache.openmeetings.web.AbstractOmServerTest.createUser;
import static org.apache.openmeetings.web.AbstractOmServerTest.getAppointment;
import static org.apache.openmeetings.web.AbstractOmServerTest.getUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.dto.calendar.MeetingMemberDTO;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.webservice.util.AppointmentParamConverter;
import org.junit.jupiter.api.Test;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

class TestCalendarService extends AbstractWebServiceTest {
	public static final String CALENDAR_SERVICE_MOUNT = "calendar";

	private void actualTest(Room r) throws Exception {
		String uuid = randomUUID().toString();
		User u = getUser(uuid);
		u.addGroup(getBean(GroupDao.class).get(1L));
		webCreateUser(u);
		ServiceResult sr = login(u.getLogin(), createPass());
		u = getBean(UserDao.class).get(u.getId());

		Date start = new Date();
		Appointment a = AbstractOmServerTest.createAppointment(getBean(AppointmentDao.class), getAppointment(u, r, start, new Date(start.getTime() + ONE_HOUR)));

		AppointmentDTO app = getClient(getCalendarUrl()).path("/room/" + a.getRoom().getId()).query("sid", sr.getMessage())
				.get(AppointmentDTO.class);
		assertNotNull(app, "Valid DTO should be returned");
	}

	@Test
	void testGetByAppRoom() throws Exception {
		actualTest(null);
	}

	@Test
	void testGetByPublicRoom() throws Exception {
		actualTest(getBean(RoomDao.class).get(5L)); //default public presentation room
	}

	private static JSONObject createAppointment(String title) {
		return new JSONObject()
			.put("title", title)
			.put("start", "2025-01-20T20:30:03+0300")
			.put("end", "2025-01-20T21:30:03+0300")
			.put("description", "Русский Тест")
			.put("reminder", "EMAIL")
			.put("room", new JSONObject()
					.put("name", "test24")
					.put("comment", "appointment test room")
					.put("type", "CONFERENCE")
					.put("capacity", 15)
					.put("appointment", true)
					.put("isPublic", false)
					.put("demo", false)
					.put("closed", false)
					.put("externalId", 10)
					.put("externalType", "HuntingLabCMS")
					.put("redirectUrl", "")
					.put("moderated", true)
					.put("allowUserQuestions", true)
					.put("allowRecording", false)
					.put("waitRecording", false)
					.put("audioOnly", true))
			.put("languageId", 9)
			.put("passwordProtected", false)
			.put("connectedEvent", false)
			.put("reminderEmailSend", false);
	}

	private String loginNewUser() throws Exception {
		String uuid = randomUUID().toString();
		User u = getUser(uuid);
		u.addGroup(getBean(GroupDao.class).get(1L));
		webCreateUser(u);
		ServiceResult sr = login(u.getLogin(), createPass());
		return sr.getMessage();
	}

	private String createApp(String title) throws Exception {
		JSONObject o = createAppointment(title);

		String sid = loginNewUser();

		Response resp = getClient(getCalendarUrl())
				.path("/")
				.query("sid", sid)
				.form(new Form().param("appointment", o.toString()));

		assertNotNull(resp, "Valid AppointmentDTO should be returned");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should be successful");
		AppointmentDTO dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull(dto, "Valid DTO should be returned");
		assertNotNull(dto.getId(), "DTO id should be valid");

		return sid;
	}

	@Test
	void testCreate() throws Exception {
		createApp("test");
	}

	@Test
	void testDelete() {
		ServiceResult sr = login();
		Response resp = getClient(getCalendarUrl())
				.path("/" + Long.MAX_VALUE) //non-existent ID
				.query("sid", sr.getMessage())
				.delete();

		assertNotEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should NOT be successful");
	}

	@Test
	void testCreateWithOmMm() throws Exception {
		JSONObject o = createAppointment("test")
				.put("meetingMembers", new JSONArray()
						.put(new JSONObject().put("user", new JSONObject()
								.put("id", 1))));

		String uuid = randomUUID().toString();
		User u = getUser(uuid);
		u.addGroup(getBean(GroupDao.class).get(1L));
		u = createUser(getBean(UserDao.class), u);
		ServiceResult sr = login(u.getLogin(), createPass());

		Response resp = getClient(getCalendarUrl())
				.path("/")
				.query("sid", sr.getMessage())
				.form(new Form().param("appointment", o.toString()));

		assertNotNull(resp, "Valid AppointmentDTO should be returned");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should be successful");
		AppointmentDTO dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull(dto, "Valid DTO should be returned");
		assertNotNull(dto.getId(), "DTO id should be valid");
	}

	private static AppointmentDTO createEventWithGuests(String sid) throws Exception {
		JSONObject o = createAppointment("test")
				.put("meetingMembers", new JSONArray()
						.put(new JSONObject().put("user", new JSONObject()
								.put("firstname", "John 1")
								.put("lastname", "Doe")
								.put("address", new JSONObject().put("email", "john1@doe.email"))
								))
						.put(new JSONObject().put("user", new JSONObject()
								.put("firstname", "John 2")
								.put("lastname", "Doe")
								.put("address", new JSONObject().put("email", "john2@doe.email"))
								))
						);

		Response resp = getClient(getCalendarUrl())
				.path("/")
				.query("sid", sid)
				.form(new Form().param("appointment", o.toString()));

		assertNotNull(resp, "Valid AppointmentDTO should be returned");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should be successful");
		AppointmentDTO dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull(dto, "Valid DTO should be returned");
		assertNotNull(dto.getId(), "DTO id should be valid");
		assertEquals(2, dto.getMeetingMembers().size(), "DTO should have 2 attendees");
		for (MeetingMemberDTO mm : dto.getMeetingMembers()) {
			assertNotNull("Email should be valid", mm.getUser().getAddress().getEmail());
		}

		return dto;
	}

	@Test
	void testCreateWithGuests() throws Exception {
		String sid = loginNewUser();
		AppointmentDTO dto = createEventWithGuests(sid);

		//try to change MM list
		JSONObject o1 = AppointmentParamConverter.json(dto)
				.put("meetingMembers", new JSONArray()
						.put(new JSONObject().put("user", new JSONObject()
								.put("id", 1))));

		Response resp = getClient(getCalendarUrl())
				.path("/")
				.query("sid", sid)
				.form(new Form().param("appointment", o1.toString()));

		assertNotNull(resp, "Valid AppointmentDTO should be returned");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should be successful");
		dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull(dto, "Valid DTO should be returned");
		assertNotNull(dto.getId(), "DTO id should be valid");
		assertEquals(1, dto.getMeetingMembers().size(), "DTO should have 1 attendees");
	}

	@Test
	void testCreateWithGuestsCleanOne() throws Exception {
		String sid = loginNewUser();
		AppointmentDTO dto = createEventWithGuests(sid);
		List<MeetingMemberDTO> initialList = new ArrayList<>(dto.getMeetingMembers());
		MeetingMemberDao mmDao = getBean(MeetingMemberDao.class);
		MeetingMember mm = mmDao.get(initialList.get(initialList.size() - 1).getId());
		Long mmId = mm.getId(), mmUserId = mm.getUser().getId();
		String hash = mm.getInvitation().getHash();
		dto.getMeetingMembers().remove(initialList.size() - 1);

		//try to change MM list
		JSONObject o = AppointmentParamConverter.json(dto);
		Response resp = getClient(getCalendarUrl())
				.path("/")
				.query("sid", sid)
				.form(new Form().param("appointment", o.toString()));

		assertNotNull(resp, "Valid AppointmentDTO should be returned");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should be successful");
		dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull(dto, "Valid DTO should be returned");
		assertNotNull(dto.getId(), "DTO id should be valid");
		assertEquals(1, dto.getMeetingMembers().size(), "DTO should have 1 attendees");

		assertNull(mmDao.get(mmId), "Meeting member should deleted");
		assertNull(getBean(InvitationDao.class).getByHash(hash, true), "Invitation should deleted");
		User uc = getBean(UserDao.class).get(mmUserId);
		assertNotNull(uc, "Meeting member user should not be deleted");
		assertFalse(uc.isDeleted(), "Meeting member user should not be deleted");
	}

	@Test
	void testGetByTitle() throws Exception {
		String title = "title" + randomUUID().toString();
		String sid = createApp(title);
		@SuppressWarnings("unchecked")
		List<AppointmentDTO> list = (List<AppointmentDTO>)getClient(getCalendarUrl())
			.path("/title/" + title)
			.query("sid", sid)
			.getCollection(AppointmentDTO.class);

		assertEquals(1, list.size(), "List of one item should be returned");
		assertEquals(title, list.get(0).getTitle(), "Title should match");

		title = randomUUID().toString();
		@SuppressWarnings("unchecked")
		List<AppointmentDTO> list1 = (List<AppointmentDTO>)getClient(getCalendarUrl())
			.path("/title/" + title)
			.query("sid", sid)
			.getCollection(AppointmentDTO.class);
		assertEquals(0, list1.size(), "None items should be returned");
	}

	protected static String getCalendarUrl() {
		return getServiceUrl(CALENDAR_SERVICE_MOUNT);
	}
}
