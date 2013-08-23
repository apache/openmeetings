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

import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUserOrganisation extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private OrganisationManager orgManagement;
	@Autowired
	private UsersDao usersDao;
	
	private User getValidUser() {
		for (User u : usersDao.getAllUsers()) {
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
		List<User> ul = orgManagement.getUsersByOrganisationId(orgId, 0, 9999, "login", true);
		assertTrue("Default Organisation should contain at least 1 user: " + ul.size(), ul.size() > 0);
		
		Organisation_Users ou = orgManagement.getOrganisation_UserByUserAndOrganisation(u.getUser_id(), orgId);
		assertNotNull("Unable to found [organisation, user] pair - [" + orgId + "," + u.getUser_id() + "]", ou);
	}
	
	@Test
	public void addOrganisation() {
		Long orgId = orgManagement.addOrganisation("Test Org", 1); //inserted by not checked
		assertNotNull("New Organisation have valid id", orgId);
		
		List<User> ul = orgManagement.getUsersByOrganisationId(orgId, 0, 9999, "login", true);
		assertTrue("New Organisation should contain NO users: " + ul.size(), ul.size() == 0);
		
		boolean found = false;
		List<Organisation> restL = orgManagement.getRestOrganisationsByUserId(3, 1, 0, 9999, "name", true);
		for (Organisation o : restL) {
			if (orgId.equals(o.getOrganisation_id())) {
				found = true;
				break;
			}
		}
		assertTrue("New organisation should not be included into organisation list of any user", found);
	}
}
