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

import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.OrganisationUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
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
			if (!u.getDeleted() && u.getOrganisation_users().size() > 0) {
				return u;
			}
		}
		fail("Unable to find valid user");
		return null;  //unreachable
	}
	
	@Test
	public void getUsersByOrganisationId() {
		User u = getValidUser();
		Long orgId = u.getOrganisation_users().get(0).getOrganisation().getOrganisation_id();
		List<Organisation_Users> ul = orgUserDao.get(orgId, 0, 9999);
		assertTrue("Default Organisation should contain at least 1 user: " + ul.size(), ul.size() > 0);
		
		Organisation_Users ou = orgUserDao.getByOrganizationAndUser(orgId, u.getUser_id());
		assertNotNull("Unable to found [organisation, user] pair - [" + orgId + "," + u.getUser_id() + "]", ou);
	}
	
	@Test
	public void addOrganisation() {
		Organisation o = new Organisation();
		o.setName(ORG_NAME);
		Long orgId = orgDao.update(o, null).getOrganisation_id(); //inserted by not checked
		assertNotNull("New Organisation have valid id", orgId);
		
		List<Organisation_Users> ul = orgUserDao.get(orgId, 0, 9999);
		assertTrue("New Organisation should contain NO users: " + ul.size(), ul.size() == 0);
	}
}
