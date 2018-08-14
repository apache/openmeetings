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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import javax.ws.rs.core.Response;

import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.user.GroupDTO;
import org.junit.Test;

public class TestGroupService extends AbstractWebServiceTest {
	public static final String GROUP_SERVICE_MOUNT = "group";

	@Test
	public void list() {
		ServiceResult r = login();
		Collection<? extends GroupDTO> groups = getClient(getGroupUrl())
				.path("/")
				.query("sid", r.getMessage()).getCollection(GroupDTO.class);
		assertNotNull("Collection should be not null", groups);
		assertFalse("Collection should be not empty", groups.isEmpty());
	}

	@Test
	public void putTest() {
		ServiceResult r = login();
		Response resp = getClient(getGroupUrl())
				.path("/")
				.query("sid", r.getMessage()).put("");
		assertEquals("Call should NOT be successful", Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), resp.getStatus());
	}

	private static Long createGroup(String sid, String name) {
		Response resp = getClient(getGroupUrl())
				.path("/")
				.query("sid", sid).query("name", name).post("");
		assertNotNull("Valid ServiceResult should be returned", resp);
		assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
		ServiceResult r1 = resp.readEntity(ServiceResult.class);
		assertEquals("OM Call should be successful", r1.getType(), Type.SUCCESS.name());
		return Long.valueOf(r1.getMessage());
	}

	@Test
	public void addRemoveTest() {
		ServiceResult r = login(adminUsername, userpass);
		Long groupId = createGroup(r.getMessage(), "Test Group");
		//delete group created
		{
			Response resp = getClient(getGroupUrl())
					.path("/" + groupId)
					.query("sid", r.getMessage()).delete();
			assertNotNull("Valid ServiceResult should be returned", resp);
			assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
			ServiceResult r1 = resp.readEntity(ServiceResult.class);
			assertEquals("OM Call should be successful", r1.getType(), Type.SUCCESS.name());
		}
	}

	@Test
	public void addRoomTest() {
		//create new group
		ServiceResult sr = login();
		Long groupId = createGroup(sr.getMessage(), "Group WS");
		RoomDTO rdto = new RoomDTO();
		rdto.setName("Group WS Room");
		CallResult<RoomDTO> room = createAndValidate(sr.getMessage(), rdto);

		Response resp = getClient(getGroupUrl())
				.path(String.format("/%s/rooms/add/%s", groupId, room.getObj().getId()))
				.query("sid", sr.getMessage()).query("name", "Test Group").post("");
		assertNotNull("Valid ServiceResult should be returned", resp);
		assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
		ServiceResult sr1 = resp.readEntity(ServiceResult.class);
		assertEquals("OM Call should be successful", sr1.getType(), Type.SUCCESS.name());
	}

	protected static String getGroupUrl() {
		return getServiceUrl(GROUP_SERVICE_MOUNT);
	}
}
