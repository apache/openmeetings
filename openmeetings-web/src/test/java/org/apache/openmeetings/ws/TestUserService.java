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
package org.apache.openmeetings.ws;

import static java.util.UUID.randomUUID;
import static jakarta.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static org.apache.openmeetings.web.AbstractOmServerTest.ADMIN_USERNAME;
import static org.apache.openmeetings.web.AbstractOmServerTest.createPass;
import static org.apache.openmeetings.web.AbstractOmServerTest.RND;
import static org.apache.openmeetings.web.AbstractOmServerTest.USER_PASS;
import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.room.RoomOptionsDTO;
import org.apache.openmeetings.db.dto.user.ExternalUserDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.util.OmException;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestUserService extends AbstractWebServiceTest {
	private static final String DUMMY_PICTURE_URL = "https://openmeetings.apache.org/images/logo.png";

	@Test
	void invalidLoginTest() {
		ServiceResult r = loginNoCheck("invalid-user", "bad pass");
		assertNotNull(r, "Valid ServiceResult should be returned");
		assertEquals(Type.ERROR.name(), r.getType(), "Login should NOT be successful");
	}

	@Test
	void loginTest() {
		ServiceResult r = login();
		assertNotNull(r, "Valid ServiceResult should be returned");
	}

	private static ServiceResult getHash(String sid, boolean expectError) {
		ExternalUserDTO user = new ExternalUserDTO()
				.setExternalId("1")
				.setExternalType(UNIT_TEST_EXT_TYPE)
				.setEmail("user1@junit.openmeetings.apache.org")
				.setFirstname("First Name 1")
				.setLastname("Last Name 1")
				.setProfilePictureUrl(DUMMY_PICTURE_URL)
				.setLogin("login1");
		RoomOptionsDTO options = new RoomOptionsDTO()
				.setRoomId(5L)
				.setModerator(true);
		try (Response resp = getClient(getUserUrl())
				.path("/hash")
				.query("sid", sid)
				.form(new Form().param("user", user.toString()).param("options", options.toString())))
		{
			assertNotNull(resp, "Valid ServiceResult should be returned");
			if (expectError) {
				assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), resp.getStatus(), "Call should NOT be successful");
				return null;
			} else {
				assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should be successful");
				return resp.readEntity(ServiceResult.class);
			}
		}
	}

	@Test
	void hashTestNoAuth() {
		getHash("aa", true);
	}

	private Long getAndcheckHash(Long adminId) {
		ServiceResult r = login();
		String sid = r.getMessage();
		ServiceResult r1 = getHash(sid, false);
		assertEquals(Type.SUCCESS.name(), r1.getType(), "OM Call should be successful");
		WebSession ws = WebSession.get();
		ws.checkHashes(StringValue.valueOf(r1.getMessage()), StringValue.valueOf(""));
		assertTrue(ws.isSignedIn(), "Login via secure hash should be successful");
		Long userId = WebSession.getUserId();
		assertNotEquals(adminId, userId);
		User u = getBean(UserDao.class).get(userId);
		assertNotNull(u, "User should be created successfuly");
		assertEquals(DUMMY_PICTURE_URL, u.getPictureUri(), "Picture URL should be preserved");
		return userId;
	}

	@Test
	void hashTest() throws OmException {
		ensureApplication(-1L); // to ensure WebSession is attached
		WebSession ws = WebSession.get();
		assertTrue(ws.signIn(ADMIN_USERNAME, USER_PASS, User.Type.USER, null));
		Long userId0 = WebSession.getUserId();
		Long userId1 = getAndcheckHash(userId0);
		Long userId2 = getAndcheckHash(userId0);
		assertEquals(userId1, userId2, "User should be the same");
	}

	private static record AddUserResult<T>(UserDTO initial, T created) {}

	private <T> AddUserResult<T> sendAddUser(String uuid, String email, String extId, int expCode, Class<T> expected) {
		String[] tzList = TimeZone.getAvailableIDs();
		String tz = TimeZone.getTimeZone(tzList[RND.nextInt(tzList.length)]).getID();
		ServiceResult r = login();
		UserDTO u = new UserDTO();
		u.setType(null); // check auto-set
		u.setLogin("test" + uuid);
		u.setPassword(createPass());
		u.setFirstname("testF" + uuid);
		u.setLastname("testL" + uuid);
		u.setAddress(new Address());
		u.getAddress().setEmail(email);
		u.getAddress().setCountry(Locale.getDefault().getCountry());
		u.setTimeZoneId(tz);
		if (!Strings.isEmpty(extId)) {
			u.setExternalId(extId);
			u.setExternalType(UNIT_TEST_EXT_TYPE);
		}
		Response resp = getClient(getUserUrl())
				.path("/")
				.query("sid", r.getMessage())
				.type(APPLICATION_FORM_URLENCODED)
				.post(new Form().param("user", u.toString()).param("confirm", "" + false));

		assertEquals(expCode, resp.getStatus(), "Not allowed error");
		assertTrue(resp.hasEntity());
		return new AddUserResult<T>(u, resp.readEntity(expected));
	}

	private UserDTO doAddUser(String uuid, String extId) {
		AddUserResult<UserDTO> result = sendAddUser(uuid, uuid + "@local", extId, 200, UserDTO.class);
		assertNotNull(result.created, "Valid UserDTO should be returned");
		assertNotNull(result.created.getId(), "Id should not be NULL");
		assertEquals(result.initial.getLogin(), result.created.getLogin(), "Login should match");
		assertEquals(result.initial.getTimeZoneId(), result.created.getTimeZoneId(), "Timezone should match");
		return result.created;
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "  ", "aaaaa", "aaa\rbbb@eeeee", "gggg\n@hhhh"})
	void addUserBadEmailTest(String email) {
		String uuid = randomUUID().toString();
		AddUserResult<ServiceResult> result = sendAddUser(uuid, email, uuid, 500, ServiceResult.class);
		assertEquals(ServiceResult.Type.ERROR.name(), result.created.getType());
	}

	@Test
	void addUserTest() {
		String uuid = randomUUID().toString();
		UserDTO user = doAddUser(uuid, uuid);
		assertEquals(User.Type.EXTERNAL, user.getType(), "Type should match");
	}

	@Test
	void addIntUserTest() {
		String uuid = randomUUID().toString();
		UserDTO user = doAddUser(uuid, null);
		assertEquals(User.Type.USER, user.getType(), "Type should match");
	}

	@Test
	void list() {
		ServiceResult r = login();
		Collection<? extends UserDTO> users = getClient(getUserUrl())
				.path("/")
				.query("sid", r.getMessage())
				.getCollection(UserDTO.class);
		assertNotNull(users, "Collection should be not null");
		assertFalse(users.isEmpty(), "Collection should be not empty");
	}

	@Test
	void listNoAuth() {
		Response r = getClient(getUserUrl())
				.path("/")
				.get();
		assertEquals(500, r.getStatus(), "Not allowed error");
		assertTrue(r.hasEntity());
		ServiceResult result = r.readEntity(ServiceResult.class);
		assertEquals(ServiceResult.Type.ERROR.name(), result.getType());
	}
}
