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
package org.apache.openmeetings.userdata;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmException;
import org.junit.jupiter.api.Test;

class TestLogin extends AbstractOmServerTest {
	@Test
	void testTestLogin() throws OmException {
		User us = userDao.login(adminUsername, userpass);
		assertNotNull(us, "User is unable to login");
	}

	private User prepareUser() throws Exception {
		User u = getUser(randomUUID().toString());
		u.setLogin(" AB" + u.getLogin() + " ");
		u.getAddress().setEmail(" CD_" + u.getAddress().getEmail() + " ");
		u.updatePassword(userpass);
		u.addGroup(groupDao.get(group));
		return userDao.update(u, null);
	}

	@Test
	void testMixedCaseLogin() throws Exception {
		final String login = prepareUser().getLogin();

		User us = userDao.login(login.toUpperCase(Locale.ROOT), userpass);
		assertNotNull(us, "Uppercase User is unable to login");
	}

	@Test
	void testMixedCaseEmail() throws Exception {
		final String email = prepareUser().getAddress().getEmail();

		User us = userDao.login(email.toUpperCase(Locale.ROOT), userpass);
		assertNotNull(us, "Uppercase Email is unable to login");
	}
}
