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

import static org.apache.openmeetings.web.AbstractOmServerTest.adminUsername;
import static org.apache.openmeetings.web.AbstractOmServerTest.userpass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.user.GroupDTO;
import org.apache.openmeetings.db.entity.room.Room;
import org.junit.jupiter.api.Test;

class TestGroupService extends AbstractWebServiceTest {
	public static final String GROUP_SERVICE_MOUNT = "group";

	@Test
	void list() {
		ServiceResult r = login();
		Collection<? extends GroupDTO> groups = getClient(getGroupUrl())
				.path("/")
				.query("sid", r.getMessage()).getCollection(GroupDTO.class);
		assertNotNull(groups, "Collection should be not null");
		assertFalse(groups.isEmpty(), "Collection should be not empty");
	}

	@Test
	void putTest() {
		ServiceResult r = login();
		Response resp = getClient(getGroupUrl())
				.path("/")
				.query("sid", r.getMessage()).put("");
		assertEquals(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), resp.getStatus(), "Call should NOT be successful");
	}

	private static Long createGroup(String sid, String name) {
		Response resp = getClient(getGroupUrl())
				.path("/")
				.query("sid", sid).query("name", name).post("");
		assertNotNull(resp, "Valid ServiceResult should be returned");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should be successful");
		ServiceResult r1 = resp.readEntity(ServiceResult.class);
		assertEquals(Type.SUCCESS.name(), r1.getType(), "OM Call should be successful");
		return Long.valueOf(r1.getMessage());
	}

	@Test
	void addRemoveTest() {
		ServiceResult r = login(adminUsername, userpass);
		Long groupId = createGroup(r.getMessage(), "Test Group");
		//delete group created
		{
			Response resp = getClient(getGroupUrl())
					.path("/" + groupId)
					.query("sid", r.getMessage()).delete();
			assertNotNull(resp, "Valid ServiceResult should be returned");
			assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should be successful");
			ServiceResult r1 = resp.readEntity(ServiceResult.class);
			assertEquals(Type.SUCCESS.name(), r1.getType(), "OM Call should be successful");
		}
	}

	@Test
	void addRoomErrTest() {
		ServiceResult sr = login();
		RoomDTO rdto = new RoomDTO();
		rdto.setName("Group WS Room");
		String msg = sr.getMessage();
		assertThrows(BadRequestException.class, () -> create(msg, rdto));
	}

	@Test
	void addRoomTest() {
		//create new group
		ServiceResult sr = login();
		Long groupId = createGroup(sr.getMessage(), "Group WS");
		RoomDTO rdto = new RoomDTO();
		rdto.setType(Room.Type.PRESENTATION);
		rdto.setName("Group WS Room");
		CallResult<RoomDTO> room = createAndValidate(sr.getMessage(), rdto);

		Response resp = getClient(getGroupUrl())
				.path(String.format("/%s/rooms/add/%s", groupId, room.getObj().getId()))
				.query("sid", sr.getMessage()).query("name", "Test Group").post("");
		assertNotNull(resp, "Valid ServiceResult should be returned");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus(), "Call should be successful");
		ServiceResult sr1 = resp.readEntity(ServiceResult.class);
		assertEquals(Type.SUCCESS.name(), sr1.getType(), "OM Call should be successful");
	}

	protected static String getGroupUrl() {
		return getServiceUrl(GROUP_SERVICE_MOUNT);
	}
}
