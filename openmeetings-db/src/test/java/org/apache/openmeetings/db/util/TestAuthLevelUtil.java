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
package org.apache.openmeetings.db.util;

import static org.apache.openmeetings.db.util.AuthLevelUtil.getRoomRight;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.Assert;
import org.junit.Test;

public class TestAuthLevelUtil {
	@Test
	public void testNull() {
		assertTrue("Result should be empty", getRoomRight(null, null, null, 0).isEmpty());
	}

	@Test
	public void testAdmin() {
		User u = new User();
		u.setRights(new HashSet<>(Arrays.asList(User.Right.Admin)));
		Set<Room.Right> rights = getRoomRight(u, new Room(), null, 0);
		assertEquals("Result should NOT be empty", 1, rights.size());
		Assert.assertTrue("Result should be super moderator", rights.contains(Room.Right.superModerator));
	}

	@Test
	public void testAppointmentOwner() {
		User u = new User();
		u.setId(666L);
		Room r = new Room();
		r.setAppointment(true);
		Appointment a = new Appointment();
		a.setOwner(new User());
		a.getOwner().setId(666L);
		Set<Room.Right> rights = getRoomRight(u, r, a, 0);
		assertEquals("Result should NOT be empty", 1, rights.size());
		Assert.assertTrue("Result should be super moderator", rights.contains(Room.Right.superModerator));
	}

	@Test
	public void testConference() {
		User u = new User();
		Room r = new Room();
		Set<Room.Right> rights = getRoomRight(u, r, null, 0);
		assertEquals("Result should NOT be empty", 2, rights.size());
		Assert.assertTrue("Result should contains audio", rights.contains(Room.Right.audio));
		Assert.assertTrue("Result should contains video", rights.contains(Room.Right.video));
	}
}
