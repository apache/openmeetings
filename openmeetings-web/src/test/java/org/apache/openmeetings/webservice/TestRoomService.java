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
import static org.apache.openmeetings.util.OmFileHelper.getDefaultProfilePicture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.dto.room.InvitationDTO;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.room.RoomFileDTO;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.junit.jupiter.api.Test;

class TestRoomService extends AbstractWebServiceTest {
	private static final long CAPACITY = 666L;

	@Test
	void testExternal() {
		ServiceResult sr = login();
		String extId = randomUUID().toString();
		Room.Type type = Room.Type.PRESENTATION;
		String name = "Unit Test Ext Room";
		String comment = "Unit Test Ext Room Comments";
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setCapacity(CAPACITY);
		RoomDTO room = getClient(getRoomUrl()).path(String.format("/%s/%s/%s", type, UNIT_TEST_EXT_TYPE, extId))
				.query("sid", sr.getMessage())
				.query("room", r.toString())
				.get(RoomDTO.class);
		assertNotNull(room, "Valid room should be returned");
		assertEquals(extId, room.getExternalId(), "External ID of room should match");
		assertNotNull(room.getId(), "Room ID should be not empty");

		RoomDTO room1 = getClient(getRoomUrl()).path(String.format("/%s/%s/%s", Room.Type.PRESENTATION, UNIT_TEST_EXT_TYPE, extId))
				.query("sid", sr.getMessage())
				.get(RoomDTO.class);
		assertNotNull(room1, "Valid room should be returned");
		assertEquals(extId, room1.getExternalId(), "External ID of room should match");
		assertEquals(room.getId(), room1.getId(), "Same Room should be returned");
	}

	@Test
	void testCreate1() {
		String extId = randomUUID().toString();
		Room.Type type = Room.Type.PRESENTATION;
		String name = "Unit Test Ext Room1";
		String comment = "Unit Test Ext Room Comments1";
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setCapacity(CAPACITY);
		r.setExternalType(UNIT_TEST_EXT_TYPE);
		r.setExternalId(extId);

		createAndValidate(r);
	}

	@Test
	void testCreate2() {
		Room.Type type = Room.Type.PRESENTATION;
		String name = "Unit Test Ext Room2";
		String comment = "Unit Test Ext Room Comments2";
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setCapacity(CAPACITY);

		createAndValidate(r);
	}

	@Test
	void testCreateWithFiles1() {
		Room.Type type = Room.Type.PRESENTATION;
		String name = "Unit Test Ext Room3";
		String comment = "Unit Test Ext Room Comments3";
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setCapacity(CAPACITY);
		RoomFileDTO rf = new RoomFileDTO();
		rf.setFileId(-666L); //not existent
		r.getFiles().add(rf);

		CallResult<RoomDTO> res = createAndValidate(r);
		assertTrue(res.getObj().getFiles().isEmpty(), "No room files should be added");
	}

	@Test
	void testCreateWithFiles2() throws IOException {
		//lets create real file
		CallResult<FileItemDTO> fileCall = createVerifiedFile(getDefaultProfilePicture(), "img.png", BaseFileItem.Type.IMAGE);

		Room.Type type = Room.Type.PRESENTATION;
		String name = "Unit Test Ext Room4";
		String comment = "Unit Test Ext Room Comments4";
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setCapacity(CAPACITY);

		RoomFileDTO rf = new RoomFileDTO();
		rf.setFileId(fileCall.getObj().getId()); //not existent
		r.getFiles().add(rf);

		CallResult<RoomDTO> res = createAndValidate(fileCall.getSid(), r);
		assertFalse(res.getObj().getFiles().isEmpty(), "Room files should NOT be empty");
	}

	@Test
	void testHash() {
		List<Room> rooms = getBean(RoomDao.class).get(0,  100);
		assertFalse(rooms.isEmpty(), "Room list should not be empty");

		ServiceResult sr = login();
		ServiceResult res = getClient(getRoomUrl())
				.path("/hash")
				.query("sid", sr.getMessage())
				.query("invite", new InvitationDTO()
						.setFirstname("Mark")
						.setLastname("Steven")
						.setEmail("abc@gmail.com")
						.setPassword("Sys@123!")
						.setPasswordProtected(true)
						.setSubject("Health Meeting")
						.setRoomId(rooms.get(0).getId())
						.setMessage("Meeting")
						.setValid(Valid.PERIOD)
						.setValidFrom("2018-03-19 02:25:12")
						.setValidTo("2018-04-20 02:25:12")
						.toString())
				.post("", ServiceResult.class);
		assertEquals(Type.SUCCESS.name(), res.getType(), "Login should be successful");
	}
}
