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
package org.apache.openmeetings.web.db.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;


class TestRoomDao extends AbstractOmServerTest {
	private static final Logger log = LoggerFactory.getLogger(TestRoomDao.class);
	@Inject
	protected RoomDao roomDao;

	@Test
	void testMicStatusHidden() throws Exception {
		Room r = roomDao.get(1);
		assertNotNull(r, "Room must exist");
		assertTrue(r.isHidden(RoomElement.MICROPHONE_STATUS), "Default interview room should have mic status hidden");
		r = roomDao.get(5);
		assertNotNull(r, "Room must exist");
		assertTrue(r.isHidden(RoomElement.MICROPHONE_STATUS), "Default presentation room should have mic status hidden");
		r = roomDao.get(6);
		assertNotNull(r, "Room must exist");
		assertFalse(r.isHidden(RoomElement.MICROPHONE_STATUS), "Default Mic room should have mic status visible");

		User u = createUser(); //creating new User here
		r = roomDao.getUserRoom(u.getId(), Room.Type.PRESENTATION, "bla");
		assertNotNull(r, "Room must exist");
		boolean hidden = r.isHidden(RoomElement.MICROPHONE_STATUS);
		if (!hidden && log.isDebugEnabled()) {
			log.debug("Invalid personal room found -> User: {}, Room: {} ... deleted ? {}", u, r, r.isDeleted());
		}
		assertEquals(Room.Type.PRESENTATION, r.getType(), "User presentation room should be created");
		assertTrue(hidden, "User presentation room should have mic status hidden");
	}

	@Test
	void testPublicRooms() {
		for (Room.Type type : Room.Type.values()) {
			for (Room r : roomDao.getPublicRooms(type)) {
				assertEquals(type, r.getType(), "Room type should be " + type);
			}
		}
		Set<Room.Type> types = new HashSet<>();
		for (Room r : roomDao.getPublicRooms()) {
			types.add(r.getType());
		}
		assertEquals(Room.Type.values().length, types.size(), "All room types should be listed");
	}
}
