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

import org.apache.openmeetings.db.dao.user.AdminUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.test.AbstractWicketTester;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUserContact extends AbstractWicketTester {
	@Autowired
	private AdminUserDao adminUserDao;

	@Autowired
	private UserDao usersDao;
	
	@Test
	public void addContactByOwner() throws Exception {
		login(null, null);
		
		List<User> users = adminUserDao.getAllUsers();
		assertNotNull("Users list should not be null ", users);
		assertFalse("Users list should not be empty ", users.isEmpty());
		
		Random random = new Random();
		User contact = createUserContact(random.nextInt(), getUserId());
		String email = contact.getAdresses().getEmail();
		List<User> l = adminUserDao.get(email);
		// check that contact is visible for admin
		assertNotNull("Contacts list should not be null for admin ", l);
		assertFalse("Contacts list should not be empty for admin ", l.isEmpty());
		
		// check that contact is visible for owner
		l = usersDao.get(contact.getAdresses().getEmail());
		assertTrue("Contacts list should not be empty for admin ", !l.isEmpty());		
		//delete contact
		adminUserDao.delete(contact, getUserId());
		l = adminUserDao.get(email);
		assertTrue("Contacts list should be empty", l.isEmpty());

		User u = createUser(random.nextInt());
		contact = createUserContact(random.nextInt(), u.getUser_id());		
		// check that contact is not visible for user that is not owner of this contact
		l = usersDao.get(contact.getAdresses().getEmail());
		assertTrue("Contacts list should be empty for admin ", !l.isEmpty());
		//delete contact
		adminUserDao.delete(contact, getUserId());
		l = adminUserDao.get(email);
		assertTrue("Contacts list should be empty", l.isEmpty());
	}
}
