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
import static org.apache.openmeetings.util.OpenmeetingsVariables.isSipEnabled;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setSipEnabled;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;

class TestUserContact extends AbstractWicketTesterTest {

	@Test
	void testGetUser() {
		assertNull(userDao.get(Long.MAX_VALUE), "Null should be returned in case User does not exist");
	}

	@Test
	void createUserWithGroup() throws Exception {
		String uuid = randomUUID().toString();
		User u = getUser(uuid);
		u.addGroup(groupDao.get(1L));
		u = userDao.update(u, null);
		assertTrue(userDao.verifyPassword(u.getId(), createPass()), "Password should be set as expected");

		User u1 = userDao.get(u.getId());
		assertNotNull(u1, "Just created user should not be null");
		assertNotNull(u1.getGroupUsers(), "Just created user should have non null org-users");
		assertFalse(u1.getGroupUsers().isEmpty(), "Just created user should have not empty org-users");
	}

	@Test
	void testCreateUser() throws Exception {
		String uuid = randomUUID().toString();
		User u = createUser(uuid);
		assertTrue(userDao.verifyPassword(u.getId(), createPass()), "Password should be set as expected");
	}

	@Test
	void testCreateUserWithSip() throws Exception {
		boolean sipEnabled = isSipEnabled();
		try {
			setSipEnabled(true);
			String uuid = randomUUID().toString();
			User u = createUser(uuid);
			assertTrue(userDao.verifyPassword(u.getId(), createPass()), "Password should be set as expected");
			assertNotNull(u.getSipUser());
		} finally {
			setSipEnabled(sipEnabled);
		}
	}

	@Test
	void addContactByOwner() throws Exception {
		login(null, null);

		List<User> users = userDao.getAllUsers();
		assertNotNull(users, "User list should not be null ");
		assertFalse(users.isEmpty(), "User list should not be empty ");

		User contact = createUserContact(getUserId());
		String cEmail = contact.getAddress().getEmail();
		List<User> l = userDao.get(cEmail, false, 0, 9999);
		// check that contact is visible for admin
		assertNotNull(l, "Contact list should not be null for admin ");
		assertFalse(l.isEmpty(), "Contact list should not be empty for admin ");

		// check that contact is visible for owner
		l = userDao.get(cEmail, 0, 9999, null, true, getUserId());
		assertFalse(l.isEmpty(), "Contact list should not be empty for owner ");
		//delete contact
		userDao.delete(contact, getUserId());
		l = userDao.get(cEmail, false, 0, 9999);
		assertTrue(l.isEmpty(), "Contact list should be empty after deletion");

		User u = createUser();
		User u1 = createUser();
		contact = createUserContact(u.getId());
		cEmail = contact.getAddress().getEmail();
		// check that contact is not visible for user that is not owner of this contact
		l = userDao.get(cEmail, 0, 9999, null, true, u1.getId());
		assertTrue(l.isEmpty(), "Contact list should be empty for another user");
		//delete contact
		userDao.delete(contact, u.getId());
		l = userDao.get(cEmail, false, 0, 9999);
		assertTrue(l.isEmpty(), "Contact list should be empty after deletion");
	}
}
