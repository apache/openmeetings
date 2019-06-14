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
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static org.apache.openmeetings.AbstractJUnitDefaults.adminUsername;
import static org.apache.openmeetings.AbstractJUnitDefaults.createPass;
import static org.apache.openmeetings.AbstractJUnitDefaults.rnd;
import static org.apache.openmeetings.AbstractJUnitDefaults.userpass;
import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.room.RoomOptionsDTO;
import org.apache.openmeetings.db.dto.user.ExternalUserDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.util.string.StringValue;
import org.junit.jupiter.api.Test;

public class TestUserService extends AbstractWebServiceTest {
	private static final String DUMMY_PICTURE_URL = "https://openmeetings.apache.org/images/logo.png";

	@Test
	public void invalidLoginTest() {
		ServiceResult r = loginNoCheck("invalid-user", "bad pass");
		assertNotNull(r, "Valid ServiceResult should be returned");
		assertEquals(Type.ERROR.name(), r.getType(), "Login should NOT be successful");
	}

	@Test
	public void loginTest() {
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
	public void hashTestNoAuth() {
		getHash("aa", true);
	}

	@Test
	public void hashTest() throws OmException {
		ServiceResult r = login();
		ServiceResult r1 = getHash(r.getMessage(), false);
		assertEquals(Type.SUCCESS.name(), r1.getType(), "OM Call should be successful");

		ensureApplication(-1L); // to ensure WebSession is attached
		WebSession ws = WebSession.get();
		assertTrue(ws.signIn(adminUsername, userpass, User.Type.user, null));
		Long userId0 = WebSession.getUserId();
		ws.checkHashes(StringValue.valueOf(r1.getMessage()), StringValue.valueOf(""));
		assertTrue(ws.isSignedIn(), "Login via secure hash should be successful");
		Long userId1 = WebSession.getUserId();
		assertNotEquals(userId0, userId1);
		User u = getBean(UserDao.class).get(userId1);
		assertNotNull(u, "User should be created successfuly");
		assertEquals(DUMMY_PICTURE_URL, u.getPictureUri(), "Picture URL should be preserved");
	}

	@Test
	public void addUserTest() {
		String[] tzList = TimeZone.getAvailableIDs();
		String tz = TimeZone.getTimeZone(tzList[rnd.nextInt(tzList.length)]).getID();
		ServiceResult r = login();
		UserDTO u = new UserDTO();
		String uuid = randomUUID().toString();
		u.setLogin("test" + uuid);
		u.setPassword(createPass());
		u.setFirstname("testF" + uuid);
		u.setLastname("testL" + uuid);
		u.setAddress(new Address());
		u.getAddress().setEmail(uuid + "@local");
		u.getAddress().setCountry(Locale.getDefault().getCountry());
		u.setTimeZoneId(tz);
		u.setExternalId(uuid);
		u.setExternalType(UNIT_TEST_EXT_TYPE);
		UserDTO user = getClient(getUserUrl())
				.path("/")
				.query("sid", r.getMessage())
				.type(APPLICATION_FORM_URLENCODED)
				.post(new Form().param("user", u.toString()).param("confirm", "" + false), UserDTO.class);
		assertNotNull(user, "Valid UserDTO should be returned");
		assertNotNull(user.getId(), "Id should not be NULL");
		assertEquals(u.getLogin(), user.getLogin(), "Login should match");
		assertEquals(User.Type.external, user.getType(), "Type should match");
		assertEquals(tz, user.getTimeZoneId(), "Timezone should match");
	}

	@Test
	public void list() {
		ServiceResult r = login();
		Collection<? extends UserDTO> users = getClient(getUserUrl())
				.path("/")
				.query("sid", r.getMessage()).getCollection(UserDTO.class);
		assertNotNull(users, "Collection should be not null");
		assertFalse(users.isEmpty(), "Collection should be not empty");
	}
}
