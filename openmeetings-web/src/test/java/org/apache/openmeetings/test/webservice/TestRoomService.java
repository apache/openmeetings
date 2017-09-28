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

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static org.apache.openmeetings.util.OmFileHelper.getDefaultProfilePicture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.UUID;

import javax.ws.rs.core.Form;

import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.room.RoomFileDTO;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.room.Room;
import org.junit.Test;

public class TestRoomService extends AbstractWebServiceTest {
	public final static String ROOM_SERVICE_URL = BASE_SERVICES_URL + "/room";

	@Test
	public void testExternal() {
		ServiceResult sr = login();
		String extId = UUID.randomUUID().toString();
		Room.Type type = Room.Type.restricted;
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

		RoomDTO room1 = getClient(ROOM_SERVICE_URL).path(String.format("/%s/%s/%s", Room.Type.restricted, UNIT_TEST_EXT_TYPE, extId))
				.query("sid", sr.getMessage())
				.get(RoomDTO.class);
		assertNotNull("Valid room should be returned", room1);
		assertEquals("Same Room should be returned", room.getId(), room1.getId());
	}

	private CallResult<RoomDTO> createAndValidate(RoomDTO r) {
		return createAndValidate(null, r);
	}

	private CallResult<RoomDTO> createAndValidate(String sid, RoomDTO r) {
		if (sid == null) {
			ServiceResult sr = login();
			sid = sr.getMessage();
		}
		RoomDTO room = getClient(ROOM_SERVICE_URL)
				.query("sid", sid)
				.type(APPLICATION_FORM_URLENCODED)
				.post(new Form().param("room", r.toString()), RoomDTO.class);
		assertNotNull("Valid room should be returned", room);
		assertNotNull("Room ID should be not empty", room.getId());

		RoomDTO room1 = getClient(ROOM_SERVICE_URL).path(String.format("/%s", room.getId()))
				.query("sid", sid)
				.get(RoomDTO.class);
		assertNotNull("Valid room should be returned", room1);
		assertEquals("Room with same ID should be returned", room.getId(), room1.getId());
		assertEquals("Room with same Name should be returned", r.getName(), room1.getName());
		assertEquals("Room with same ExternalType should be returned", r.getExternalType(), room1.getExternalType());
		assertEquals("Room with same ExternalId should be returned", r.getExternalId(), room1.getExternalId());
		//TODO check other fields
		return new CallResult<>(sid, room1);
	}

	@Test
	public void testCreate1() {
		String extId = UUID.randomUUID().toString();
		Room.Type type = Room.Type.restricted;
		String name = "Unit Test Ext Room1";
		String comment = "Unit Test Ext Room Comments1";
		long num = 666L;
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setNumberOfPartizipants(num);
		r.setExternalType(UNIT_TEST_EXT_TYPE);
		r.setExternalId(extId);

		createAndValidate(r);
	}

	@Test
	public void testCreate2() {
		Room.Type type = Room.Type.restricted;
		String name = "Unit Test Ext Room2";
		String comment = "Unit Test Ext Room Comments2";
		long num = 666L;
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setNumberOfPartizipants(num);

		createAndValidate(r);
	}

	@Test
	public void testCreateWithFiles1() {
		Room.Type type = Room.Type.restricted;
		String name = "Unit Test Ext Room3";
		String comment = "Unit Test Ext Room Comments3";
		long num = 666L;
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setNumberOfPartizipants(num);
		RoomFileDTO rf = new RoomFileDTO();
		rf.setFileId(-666L); //not existent
		r.getFiles().add(rf);

		CallResult<RoomDTO> res = createAndValidate(r);
		assertTrue("No room files should be added", res.getObj().getFiles().isEmpty());
	}

	@Test
	public void testCreateWithFiles2() throws IOException {
		//lets create real file
		CallResult<FileItemDTO> fileCall = createVerifiedFile(getDefaultProfilePicture(), "img.jpg", BaseFileItem.Type.Image);

		Room.Type type = Room.Type.restricted;
		String name = "Unit Test Ext Room4";
		String comment = "Unit Test Ext Room Comments4";
		long num = 666L;
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setNumberOfPartizipants(num);

		RoomFileDTO rf = new RoomFileDTO();
		rf.setFileId(fileCall.getObj().getId()); //not existent
		r.getFiles().add(rf);

		CallResult<RoomDTO> res = createAndValidate(fileCall.getSid(), r);
		assertFalse("Room files should NOT be empty", res.getObj().getFiles().isEmpty());
	}
}
