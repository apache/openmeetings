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
package org.apache.openmeetings.test.user;

import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.test.AbstractWicketTester;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUserContact extends AbstractWicketTester {
	@Autowired
	private UserDao userDao;
	@Autowired
	private OrganisationDao orgDao;
	Random random = new Random();
	
	@Test
	public void createUserWithOrganisation() throws Exception {
		int rnd = random.nextInt();
		User u = getUser(rnd);
		u.getOrganisation_users().add(new Organisation_Users(orgDao.get(1L)));
		u = userDao.update(u, null);
		assertTrue("Password should be set as expected", userDao.verifyPassword(u.getUser_id(), "pass" + rnd));
		
		User u1 = userDao.get(u.getUser_id());
		assertNotNull("Just created user should not be null", u1);
		assertNotNull("Just created user should have non null org-users", u1.getOrganisation_users());
		assertFalse("Just created user should have not empty org-users", u1.getOrganisation_users().isEmpty());
	}
	
	@Test
	public void createUser() throws Exception {
		int rnd = random.nextInt();
		User u = createUser(rnd);
		assertTrue("Password should be set as expected", userDao.verifyPassword(u.getUser_id(), "pass" + rnd));
	}
	
	@Test
	public void addContactByOwner() throws Exception {
		login(null, null);
		
		List<User> users = userDao.getAllUsers();
		assertNotNull("User list should not be null ", users);
		assertFalse("User list should not be empty ", users.isEmpty());
		
		User contact = createUserContact(random.nextInt(), getUserId());
		String email = contact.getAdresses().getEmail();
		List<User> l = userDao.get(email, false, 0, 9999);
		// check that contact is visible for admin
		assertNotNull("Contact list should not be null for admin ", l);
		assertFalse("Contact list should not be empty for admin ", l.isEmpty());
		
		// check that contact is visible for owner
		l = userDao.get(email, 0, 9999, null, true, getUserId());
		assertTrue("Contact list should not be empty for owner ", !l.isEmpty());		
		//delete contact
		userDao.delete(contact, getUserId());
		l = userDao.get(email, false, 0, 9999);
		assertTrue("Contact list should be empty after deletion", l.isEmpty());

		User u = createUser(random.nextInt());
		User u1 = createUser(random.nextInt());
		contact = createUserContact(random.nextInt(), u.getUser_id());
		email = contact.getAdresses().getEmail();
		// check that contact is not visible for user that is not owner of this contact
		l = userDao.get(email, 0, 9999, null, true, u1.getUser_id());
		assertTrue("Contact list should be empty for another user", l.isEmpty());
		//delete contact
		userDao.delete(contact, u.getUser_id());
		l = userDao.get(email, false, 0, 9999);
		assertTrue("Contact list should be empty after deletion", l.isEmpty());
	}
}
