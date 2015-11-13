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

import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.apache.openmeetings.test.selenium.HeavyTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUserGroup extends AbstractJUnitDefaults {
	@Autowired
	private GroupUserDao groupUserDao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private UserDao userDao;
	public static final String GROUP_NAME = "Test Group";
	
	private User getValidUser() {
		for (User u : userDao.getAllBackupUsers()) {
			if (!u.isDeleted() && u.getGroupUsers().size() > 0) {
				return u;
			}
		}
		fail("Unable to find valid user");
		return null;  //unreachable
	}
	
	@Test
	public void getUsersByGroupId() {
		User u = getValidUser();
		Long groupId = u.getGroupUsers().get(0).getGroup().getId();
		List<GroupUser> ul = groupUserDao.get(groupId, 0, 9999);
		assertTrue("Default Group should contain at least 1 user: " + ul.size(), ul.size() > 0);
		
		GroupUser ou = groupUserDao.getByGroupAndUser(groupId, u.getId());
		assertNotNull("Unable to found [group, user] pair - [" + groupId + "," + u.getId() + "]", ou);
	}
	
	@Test
	public void addGroup() {
		Group o = new Group();
		o.setName(GROUP_NAME);
		Long groupId = groupDao.update(o, null).getId(); //inserted by not checked
		assertNotNull("New Group have valid id", groupId);
		
		List<GroupUser> ul = groupUserDao.get(groupId, 0, 9999);
		assertTrue("New Group should contain NO users: " + ul.size(), ul.size() == 0);
	}

	@Test
	@HeavyTests
	public void add10kUsers() throws Exception {
		List<Group> groups = groupDao.get(GROUP_NAME, 0, 1, null);
		Group o = null;
		if (groups == null || groups.isEmpty()) {
			o = new Group();
			o.setName(GROUP_NAME);
			o = groupDao.update(o, null);
		} else {
			o = groups.get(0);
		}
		Random rnd = new Random();
		for (int i = 0; i < 10000; ++i) {
			User u = createUser(rnd.nextInt());
			u.getGroupUsers().add(new GroupUser(o));
			userDao.update(u, null);
		}
	}
}
