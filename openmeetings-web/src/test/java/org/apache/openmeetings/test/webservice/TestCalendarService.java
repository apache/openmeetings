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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

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
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.webservice.util.AppointmentParamConverter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class TestCalendarService extends AbstractWebServiceTest {
	public final static String CALENDAR_SERVICE_URL = BASE_SERVICES_URL + "/calendar";
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private MeetingMemberDao mmDao;
	@Autowired
	private InvitationDao invitationDao;
	@Autowired
	private UserDao userDao;

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

	private static JSONObject createAppointment() {
		return new JSONObject()
			.put("title", "test")
			.put("start", "2025-01-20T20:30:03+0300")
			.put("end", "2025-01-20T21:30:03+0300")
			.put("description", "Русский Тест")
			.put("reminder", "email")
			.put("room", new JSONObject()
					.put("name", "test24")
					.put("comment", "appointment test room")
					.put("type", "conference")
					.put("numberOfPartizipants", 15)
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
					.put("waitForRecording", false)
					.put("audioOnly", true)
					.put("topBarHidden", false)
					.put("chatHidden", false)
					.put("activitiesHidden", false)
					.put("filesExplorerHidden", false)
					.put("actionsMenuHidden", false)
					.put("screenSharingHidden", false)
					.put("whiteboardHidden", false))
			.put("languageId", 9)
			.put("passwordProtected", false)
			.put("connectedEvent", false)
			.put("reminderEmailSend", false);
	}

	private String loginNewUser() throws Exception {
		String uuid = UUID.randomUUID().toString();
		User u = getUser(uuid);
		u.getGroupUsers().add(new GroupUser(groupDao.get(1L), u));
		u = createUser(u);
		ServiceResult sr = login(u.getLogin(), getRandomPass(uuid));
		return sr.getMessage();
	}

	@Test
	public void testCreate() throws Exception {
		JSONObject o = createAppointment();

		String sid = loginNewUser();

		Response resp = getClient(CALENDAR_SERVICE_URL)
				.path("/")
				.query("sid", sid)
				.form(new Form().param("appointment", o.toString()));

		assertNotNull("Valid AppointmentDTO should be returned", resp);
		assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
		AppointmentDTO dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull("Valid DTO should be returned", dto);
		assertNotNull("DTO id should be valid", dto.getId());
	}

	@Test
	public void testDelete() {
		ServiceResult sr = login();
		Response resp = getClient(CALENDAR_SERVICE_URL)
				.path("/" + Long.MAX_VALUE) //non-existent ID
				.query("sid", sr.getMessage())
				.delete();

		assertNotEquals("Call should NOT be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
	}

	@Test
	public void testCreateWithOmMm() throws Exception {
		JSONObject o = createAppointment()
				.put("meetingMembers", new JSONArray()
						.put(new JSONObject().put("user", new JSONObject()
								.put("id", 1))));

		String uuid = UUID.randomUUID().toString();
		User u = getUser(uuid);
		u.getGroupUsers().add(new GroupUser(groupDao.get(1L), u));
		u = createUser(u);
		ServiceResult sr = login(u.getLogin(), getRandomPass(uuid));

		Response resp = getClient(CALENDAR_SERVICE_URL)
				.path("/")
				.query("sid", sr.getMessage())
				.form(new Form().param("appointment", o.toString()));

		assertNotNull("Valid AppointmentDTO should be returned", resp);
		assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
		AppointmentDTO dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull("Valid DTO should be returned", dto);
		assertNotNull("DTO id should be valid", dto.getId());
	}

	private static AppointmentDTO createEventWithGuests(String sid) throws Exception {
		JSONObject o = createAppointment()
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

		Response resp = getClient(CALENDAR_SERVICE_URL)
				.path("/")
				.query("sid", sid)
				.form(new Form().param("appointment", o.toString()));

		assertNotNull("Valid AppointmentDTO should be returned", resp);
		assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
		AppointmentDTO dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull("Valid DTO should be returned", dto);
		assertNotNull("DTO id should be valid", dto.getId());
		assertEquals("DTO should have 2 attendees", 2, dto.getMeetingMembers().size());
		for (MeetingMemberDTO mm : dto.getMeetingMembers()) {
			assertNotNull("Email should be valid", mm.getUser().getAddress().getEmail());
		}

		return dto;
	}

	@Test
	public void testCreateWithGuests() throws Exception {
		String sid = loginNewUser();
		AppointmentDTO dto = createEventWithGuests(sid);

		//try to change MM list
		JSONObject o1 = AppointmentParamConverter.json(dto)
				.put("meetingMembers", new JSONArray()
						.put(new JSONObject().put("user", new JSONObject()
								.put("id", 1))));

		Response resp = getClient(CALENDAR_SERVICE_URL)
				.path("/")
				.query("sid", sid)
				.form(new Form().param("appointment", o1.toString()));

		assertNotNull("Valid AppointmentDTO should be returned", resp);
		assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
		dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull("Valid DTO should be returned", dto);
		assertNotNull("DTO id should be valid", dto.getId());
		assertEquals("DTO should have 1 attendees", 1, dto.getMeetingMembers().size());
	}

	@Test
	public void testCreateWithGuestsCleanOne() throws Exception {
		String sid = loginNewUser();
		AppointmentDTO dto = createEventWithGuests(sid);
		List<MeetingMemberDTO> initialList = new ArrayList<>(dto.getMeetingMembers());
		MeetingMember mm = mmDao.get(initialList.get(initialList.size() - 1).getId());
		Long mmId = mm.getId(), mmUserId = mm.getUser().getId();
		String hash = mm.getInvitation().getHash();
		dto.getMeetingMembers().remove(initialList.size() - 1);

		//try to change MM list
		JSONObject o = AppointmentParamConverter.json(dto);
		Response resp = getClient(CALENDAR_SERVICE_URL)
				.path("/")
				.query("sid", sid)
				.form(new Form().param("appointment", o.toString()));

		assertNotNull("Valid AppointmentDTO should be returned", resp);
		assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
		dto = resp.readEntity(AppointmentDTO.class);
		assertNotNull("Valid DTO should be returned", dto);
		assertNotNull("DTO id should be valid", dto.getId());
		assertEquals("DTO should have 1 attendees", 1, dto.getMeetingMembers().size());

		assertNull("Meeting member should deleted", mmDao.get(mmId));
		assertNull("Invitation should deleted", invitationDao.getByHash(hash, true, false));
		User uc = userDao.get(mmUserId);
		assertNotNull("Meeting member user should not be deleted", uc);
		assertFalse("Meeting member user should not be deleted", uc.isDeleted());
	}
}
