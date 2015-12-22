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

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.room.RoomOptionsDTO;
import org.apache.openmeetings.db.dto.user.ExternalUserDTO;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;

public class UserServiceTest extends AbstractJUnitDefaults {
	public final static String BASE_SERVICES_URL = "http://localhost:5080/openmeetings/services";
	public final static String USER_SERVICE_URL = BASE_SERVICES_URL + "/user";
	
	protected static WebClient getClient(String url) {
		return WebClient.create(url).accept("application/json").type("application/json");
	}
	
	@Test
	public void loginTest() {
		ServiceResult r = getClient(USER_SERVICE_URL).path("/login").query("user", username).query("pass", userpass).get(ServiceResult.class);
		assertNotNull("Valid ServiceResult should be returned", r);
		assertEquals("Login should be successful", r.getType(), Type.SUCCESS.name());
	}

	@Test
	public void hashTest() {
		ServiceResult r = getClient(USER_SERVICE_URL).path("/login").query("user", username).query("pass", userpass).get(ServiceResult.class);
		assertEquals("Login should be successful", r.getType(), Type.SUCCESS.name());
		ExternalUserDTO user = new ExternalUserDTO();
		user.setExternalId("1");
		user.setExternalType("OmJunitTests");
		user.setEmail("user1@junit.openmeetings.apache.org");
		user.setFirstname("First Name 1");
		user.setLastname("Last Name 1");
		user.setLogin("login1");
		RoomOptionsDTO options = new RoomOptionsDTO();
		options.setRoomId(5L);
		options.setModerator(true);
		Response resp = getClient(USER_SERVICE_URL)
				.path("/hash")
				.query("sid", r.getMessage())
				.form(new Form().param("user", user.toString()).param("options", options.toString()));
		assertNotNull("Valid ServiceResult should be returned", resp);
		assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
		ServiceResult r1 = resp.readEntity(ServiceResult.class);
		assertEquals("OM Call should be successful", r1.getType(), Type.SUCCESS.name());
	}
}
