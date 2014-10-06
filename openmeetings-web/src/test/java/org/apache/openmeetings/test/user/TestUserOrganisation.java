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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Random;

import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.OrganisationUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.OrganisationUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.apache.openmeetings.test.selenium.HeavyTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUserOrganisation extends AbstractJUnitDefaults {
	@Autowired
	private OrganisationUserDao orgUserDao;
	@Autowired
	private OrganisationDao orgDao;
	@Autowired
	private UserDao usersDao;
	public static final String ORG_NAME = "Test Org";
	
	private User getValidUser() {
		for (User u : usersDao.getAllBackupUsers()) {
			if (!u.getDeleted() && u.getOrganisationUsers().size() > 0) {
				return u;
			}
		}
		fail("Unable to find valid user");
		return null;  //unreachable
	}
	
	@Test
	public void getUsersByOrganisationId() {
		User u = getValidUser();
		Long orgId = u.getOrganisationUsers().get(0).getOrganisation().getId();
		List<OrganisationUser> ul = orgUserDao.get(orgId, 0, 9999);
		assertTrue("Default Organisation should contain at least 1 user: " + ul.size(), ul.size() > 0);
		
		OrganisationUser ou = orgUserDao.getByOrganizationAndUser(orgId, u.getId());
		assertNotNull("Unable to found [organisation, user] pair - [" + orgId + "," + u.getId() + "]", ou);
	}
	
	@Test
	public void addOrganisation() {
		Organisation o = new Organisation();
		o.setName(ORG_NAME);
		Long orgId = orgDao.update(o, null).getId(); //inserted by not checked
		assertNotNull("New Organisation have valid id", orgId);
		
		List<OrganisationUser> ul = orgUserDao.get(orgId, 0, 9999);
		assertTrue("New Organisation should contain NO users: " + ul.size(), ul.size() == 0);
	}

	@Test
	@HeavyTests
	public void add10kUsers() throws Exception {
		List<Organisation> orgs = orgDao.get(ORG_NAME, 0, 1, null);
		Organisation o = null;
		if (orgs == null || orgs.isEmpty()) {
			o = new Organisation();
			o.setName(ORG_NAME);
			o = orgDao.update(o, null);
		} else {
			o = orgs.get(0);
		}
		Random rnd = new Random();
		for (int i = 0; i < 10000; ++i) {
			User u = createUser(rnd.nextInt());
			u.getOrganisationUsers().add(new OrganisationUser(o));
			usersDao.update(u, null);
		}
	}
}
