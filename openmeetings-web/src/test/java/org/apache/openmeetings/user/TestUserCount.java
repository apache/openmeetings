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

import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.junit.Assert.assertTrue;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.Test;

public class TestUserCount extends AbstractWicketTester {
	@Test
	public void testCountSearchUsers() throws Exception {
		User u = createUser();
		assertTrue("Account of search users should be one", userDao.count(u.getFirstname()) == 1);
	}

	@Test
	public void testCountFilteredUsers() throws Exception {
		User u = createUser();
		User contact = createUserContact(u.getId());
		assertTrue("Account of filtered user should be one", userDao.count(contact.getFirstname(), true, u.getId()) == 1);
	}

	@Test
	public void testCountUnfilteredUsers() throws Exception {
		User u = createUser();
		createUserContact(u.getId());
		assertTrue("Account of unfiltered should be more then one", userDao.count("firstname", false, getUserId()) > 1);
	}

	@Test
	public void testCountAllUsers() {
		assertTrue("Account of users should be positive", userDao.count() > 0);
	}
}
