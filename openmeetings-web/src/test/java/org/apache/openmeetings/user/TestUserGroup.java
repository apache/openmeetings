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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;


class TestUserGroup extends AbstractOmServerTest {
	public static final String GROUP_NAME = "Test Group";
	@Inject
	private GroupUserDao groupUserDao;

	private User getValidUser() {
		for (User u : userDao.getAllBackupUsers()) {
			if (!u.isDeleted() && u.getGroupUsers().size() > 0) {
				return u;
			}
		}
		fail("Unable to find valid user");
		return null; //unreachable
	}

	@Test
	void getUsersByGroupId() {
		User u = getValidUser();
		Long groupId = u.getGroupUsers().get(0).getGroup().getId();
		List<GroupUser> ul = groupUserDao.get(groupId, 0, 9999);
		assertTrue(ul.size() > 0, "Default Group should contain at least 1 user: " + ul.size());

		GroupUser ou = groupUserDao.getByGroupAndUser(groupId, u.getId());
		assertNotNull(ou, "Unable to find [group, user] pair - [" + groupId + "," + u.getId() + "]");
	}

	@Test
	void addGroup() {
		Group g = new Group();
		g.setName(GROUP_NAME);
		Long groupId = groupDao.update(g, null).getId();
		assertNotNull(groupId, "New Group have valid id");

		List<GroupUser> ul = groupUserDao.get(groupId, 0, 9999);
		assertEquals(0, ul.size(), "New Group should contain NO users: " + ul.size());
	}

	@Test
	void addUserWithoutGroup() throws Exception {
		String uuid = randomUUID().toString();
		User u = getUser(uuid);
		u = userDao.update(u, null);
		assertNotNull(u.getId(), "User successfully created");
		checkEmptyGroup("dao.get()", userDao.get(u.getId()));
		try {
			checkEmptyGroup("dao.login()", userDao.login(u.getAddress().getEmail(), createPass()));
			fail("User with no Group is unable to login");
		} catch (OmException e) {
			assertEquals("error.nogroup", e.getKey(), "Expected Om Exception");
		}
		checkEmptyGroup("dao.getByLogin(user)", userDao.getByLogin(u.getLogin(), u.getType(), u.getDomainId()));
	}

	private Long addGroupUser(User u, String grpNameUq, boolean moderator) {
		Group g = new Group();
		g.setName(grpNameUq + randomUUID().toString());
		g = groupDao.update(g, null);
		GroupUser gu = new GroupUser(g, u);
		gu.setModerator(moderator);
		u.getGroupUsers().add(gu);
		return g.getId();
	}

	@Test
	void groupAdmin() throws Exception {
		String uuid = randomUUID().toString();
		User u = getUser(uuid);
		u.setGroupUsers(new ArrayList<>());

		final String uniquePart = randomUUID().toString().replace('-', '_');
		Long idG1 = addGroupUser(u, uniquePart, true);
		Long idG2 = addGroupUser(u, uniquePart, true);
		Long idG3 = addGroupUser(u, uniquePart, false);

		u = userDao.update(u, null);

		assertEquals(2, groupDao.adminCount(uniquePart, u.getId()), "Count: There should be exatly 2 groups");

		List<Group> groups = groupDao.adminGet(uniquePart, u.getId(), 0, 10, null);
		assertEquals(2, groups.size(), "List: There should be exatly 2 groups");
		Set<Long> groupIds = groups.stream().map(Group::getId).collect(Collectors.toSet());
		assertTrue(groupIds.contains(idG1), "First group should be found");
		assertTrue(groupIds.contains(idG2), "Second group should be found");
		assertFalse(groupIds.contains(idG3), "Third group should NOT be found");
	}

	@Test
	void addLdapUserWithoutGroup() throws Exception {
		User u1 = getUser();
		u1.setType(User.Type.LDAP);
		u1.setDomainId(1L);
		u1 = userDao.update(u1, null);
		checkEmptyGroup("dao.getByLogin(ldap)", userDao.getByLogin(u1.getLogin(), u1.getType(), u1.getDomainId()));
	}

	private static void checkEmptyGroup(String prefix, User u) {
		assertNotNull(u, prefix + ":: Created user should be available");
		assertNotNull(u.getGroupUsers(), prefix + ":: List<GroupUser> for newly created user should not be null");
		assertTrue(u.getGroupUsers().isEmpty(), prefix + ":: List<GroupUser> for newly created user should be empty");
	}

	@Test
	@Tag("heavy-test")
	void add10kUsers() throws Exception {
		final int amount = 10_000;
		List<Group> groups = groupDao.get(GROUP_NAME, 0, 1, null);
		Group g = null;
		if (groups == null || groups.isEmpty()) {
			g = new Group();
			g.setName(GROUP_NAME);
			g = groupDao.update(g, null);
		} else {
			g = groups.get(0);
		}
		long count = userDao.count();
		for (int i = 0; i < amount; ++i) {
			User u = createUser();
			u.addGroup(g);
			userDao.update(u, null);
		}
		long newCount = userDao.count();
		assertEquals(count + amount, newCount, "All users should be added");
	}
}
