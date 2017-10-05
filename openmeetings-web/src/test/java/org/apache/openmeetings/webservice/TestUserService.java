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

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

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
import org.junit.Assert;
import org.junit.Test;

public class TestUserService extends AbstractWebServiceTest {
	@Test
	public void invalidLoginTest() {
		ServiceResult r = loginNoCheck("invalid-user", "bad pass");
		assertNotNull("Valid ServiceResult should be returned", r);
		assertEquals("Login should NOT be successful", Type.ERROR.name(), r.getType());
	}

	@Test
	public void loginTest() {
		ServiceResult r = login();
		assertNotNull("Valid ServiceResult should be returned", r);
	}

	private static ServiceResult getHash(String sid) {
		ExternalUserDTO user = new ExternalUserDTO()
				.setExternalId("1")
				.setExternalType(UNIT_TEST_EXT_TYPE)
				.setEmail("user1@junit.openmeetings.apache.org")
				.setFirstname("First Name 1")
				.setLastname("Last Name 1")
				.setLogin("login1");
		RoomOptionsDTO options = new RoomOptionsDTO()
				.setRoomId(5L)
				.setModerator(true);
		Response resp = getClient(USER_SERVICE_URL)
				.path("/hash")
				.query("sid", sid)
				.form(new Form().param("user", user.toString()).param("options", options.toString()));
		assertNotNull("Valid ServiceResult should be returned", resp);
		assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
		return resp.readEntity(ServiceResult.class);
	}

	@Test
	public void hashTestNoAuth() {
		ServiceResult r = getHash("aa");
		assertEquals("OM Call should NOT be successful", Type.ERROR.name(), r.getType());
	}

	@Test
	public void hashTest() throws OmException {
		ServiceResult r = login();
		ServiceResult r1 = getHash(r.getMessage());
		assertEquals("OM Call should be successful", Type.SUCCESS.name(), r1.getType());

		WebSession ws = WebSession.get();
		assertTrue(ws.signIn(adminUsername, userpass, User.Type.user, null));
		Long userId0 = WebSession.getUserId();
		ws.checkHashes(StringValue.valueOf(r1.getMessage()), StringValue.valueOf(""));
		assertTrue("Login via secure hash should be successful", ws.isSignedIn());
		Long userId1 = WebSession.getUserId();
		Assert.assertNotEquals(userId0, userId1);
	}

	@Test
	public void addUserTest() {
		String[] tzList = TimeZone.getAvailableIDs();
		String tz = TimeZone.getTimeZone(tzList[rnd.nextInt(tzList.length)]).getID();
		ServiceResult r = login();
		UserDTO u = new UserDTO();
		String uuid = UUID.randomUUID().toString();
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
		UserDTO user = getClient(USER_SERVICE_URL)
				.path("/")
				.query("sid", r.getMessage())
				.type(APPLICATION_FORM_URLENCODED)
				.post(new Form().param("user", u.toString()).param("confirm", "" + false), UserDTO.class);
		assertNotNull("Valid UserDTO should be returned", user);
		assertNotNull("Id should not be NULL", user.getId());
		assertEquals("OM Call should be successful", u.getLogin(), user.getLogin());
		assertEquals("OM Call should be successful", tz, user.getTimeZoneId());
	}
}
