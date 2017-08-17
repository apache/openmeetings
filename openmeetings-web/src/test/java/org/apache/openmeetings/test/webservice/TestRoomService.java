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

import java.util.UUID;

import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.entity.room.Room;
import org.junit.Test;

public class TestRoomService extends AbstractWebServiceTest {
	public final static String ROOM_SERVICE_URL = BASE_SERVICES_URL + "/room";

	@Test
	public void testExternal() {
		ServiceResult sr = login();
		String extId = UUID.randomUUID().toString();
		Room.Type type = Room.Type.presentation;
		String name = "Unit Test Ext Room";
		String comment = "Unit Test Ext Room Comments";
		long num = 666L;
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setNumberOfPartizipants(num);
		RoomDTO room = getClient(ROOM_SERVICE_URL).path(String.format("/%s/%s/%s", type, UNIT_TEST_EXT_TYPE, extId))
				.query("sid", sr.getMessage())
				.query("room", r.toString())
				.get(RoomDTO.class);
		assertNotNull("Valid room should be returned", room);
		assertNotNull("Room ID should be not empty", room.getId());

		RoomDTO room1 = getClient(ROOM_SERVICE_URL).path(String.format("/%s/%s/%s", Room.Type.presentation, UNIT_TEST_EXT_TYPE, extId)).query("sid", sr.getMessage())
				.get(RoomDTO.class);
		assertNotNull("Valid room should be returned", room1);
		assertEquals("Same Room should be returned", room.getId(), room1.getId());
	}
}
