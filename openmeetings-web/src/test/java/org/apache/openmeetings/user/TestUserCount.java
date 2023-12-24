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
package org.apache.openmeetings.user;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;

class TestUserCount extends AbstractWicketTesterTest {
	@Test
	void testCountSearchUsers() throws Exception {
		User u = createUser();
		assertEquals(1, userDao.count(u.getFirstname()), "A count of search users should be one");
	}

	@Test
	void testCountFilteredUsers() throws Exception {
		User u = createUser();
		User contact = createUserContact(u.getId());
		assertEquals(1, userDao.count(contact.getFirstname(), true, u.getId()), "A count of filtered user should be one");
	}

	@Test
	void testCountUnfilteredUsers() throws Exception {
		User u = createUser();
		createUserContact(u.getId());
		assertTrue(userDao.count("firstname", false, getUserId()) > 1, "A count of unfiltered should be more then one");
	}

	@Test
	void testCountAllUsers() {
		assertTrue(userDao.count() > 0, "A count of users should be positive");
	}

	@Test
	void testCountProfileSearch() throws Exception {
		String uUid = "usr" + randomUUID().toString();
		Group g1 = new Group().setName("grp" + randomUUID().toString());
		groupDao.update(g1, null);
		Group g2 = new Group().setName("grp" + randomUUID().toString());
		groupDao.update(g2, null);
		User u = getUser(uUid);
		u.addGroup(g1);
		u.addGroup(g2);
		userDao.update(u, null);
		assertEquals(1, userDao.searchCountUserProfile(u.getId(), uUid, null, null), "A count of search users should be one");
	}
}
