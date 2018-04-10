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
package org.apache.openmeetings.db.dao;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestRoomDao extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestRoomDao.class, getWebAppRootKey());
	@Autowired
	protected RoomDao roomDao;

	@Test
	public void testMicStatusHidden() throws Exception {
		Room r = roomDao.get(1);
		assertTrue("Default interview room should have mic status hidden", r.isHidden(RoomElement.MicrophoneStatus));
		r = roomDao.get(5);
		assertTrue("Default presentation room should have mic status hidden", r.isHidden(RoomElement.MicrophoneStatus));
		r = roomDao.get(6);
		assertFalse("Default Mic room should have mic status visible", r.isHidden(RoomElement.MicrophoneStatus));

		User u = createUser(); //creating new User here
		r = roomDao.getUserRoom(u.getId(), Room.Type.presentation, "bla");
		boolean hidden = r.isHidden(RoomElement.MicrophoneStatus);
		if (!hidden && log.isDebugEnabled()) {
			log.debug("Invalid personal room found -> User: {}, Room: {} ... deleted ? {}", u, r, r.isDeleted());
		}
		Assert.assertEquals("User presentation room should be created", Room.Type.presentation, r.getType());
		assertTrue("User presentation room should have mic status hidden", hidden);
	}

	@Test
	public void testPublicRooms() {
		for (Room.Type type : Room.Type.values()) {
			for (Room r : roomDao.getPublicRooms(type)) {
				assertEquals(String.format("Room type should be %s", type), type, r.getType());
			}
		}
		Set<Room.Type> types = new HashSet<>();
		for (Room r : roomDao.getPublicRooms()) {
			types.add(r.getType());
		}
		assertEquals("All room types should be listed", Room.Type.values().length, types.size());
	}
}
