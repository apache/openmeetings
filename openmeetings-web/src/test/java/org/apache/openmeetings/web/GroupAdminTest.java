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
package org.apache.openmeetings.web;

import static java.util.UUID.randomUUID;

import static org.apache.openmeetings.db.util.AuthLevelUtil.hasAdminLevel;
import static org.apache.openmeetings.db.util.AuthLevelUtil.hasGroupAdminLevel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.RoomManager;
import org.apache.openmeetings.web.admin.users.UserForm;
import org.apache.openmeetings.web.admin.users.UsersPanel;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.util.OmSelect2MultiChoice;
import org.apache.openmeetings.web.util.OmUrlFragment.AreaKeys;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.protocol.ws.tester.WebSocketTester;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

public class GroupAdminTest extends AbstractWicketTesterTest {
	@Inject
	private RoomDao roomDao;
	@Inject
	private RoomManager roomManager;

	@Test
	void groupAdminCanRemoveVictimsUnrelatedMembershipThroughUserForm() throws Exception {
		String token = randomUUID().toString();
		Group groupA = groupDao.update(new Group().setName("i007-A-" + token), null);
		Group groupB = groupDao.update(new Group().setName("i007-B-" + token), null);

		User attacker = createUser("i007-attacker-" + token);
		attacker.setGroupUsers(new ArrayList<>());
		GroupUser attackerMembership = new GroupUser(groupA, attacker);
		attackerMembership.setModerator(true);
		attacker.getGroupUsers().add(attackerMembership);
		attacker = userDao.update(attacker, null);

		User victim = createUser("i007-victim-" + token);
		victim.setGroupUsers(new ArrayList<>());
		victim.addGroup(groupA);
		victim.addGroup(groupB);
		victim = userDao.update(victim, null);

		Room room = new Room();
		room.setName("i007-B-only-" + token);
		room.setType(Room.Type.CONFERENCE);
		room.setIspublic(false);
		room.setOwnerId(userDao.get(1L).getId());
		room.addGroup(groupB);
		room = roomDao.update(room, userDao.get(1L).getId());

		User attackerReloaded = userDao.get(attacker.getId());
		User victimBefore = userDao.get(victim.getId());
		Room roomBefore = roomDao.get(room.getId());
		assertNotNull(attackerReloaded);
		assertNotNull(victimBefore);
		assertNotNull(roomBefore);
		assertTrue(hasGroupAdminLevel(attackerReloaded.getRights()) || attackerReloaded.getGroupUsers().stream().anyMatch(GroupUser::isModerator));
		assertFalse(hasAdminLevel(attackerReloaded.getRights()), "attacker must not be global ADMIN");
		assertTrue(groupIds(victimBefore).containsAll(Set.of(groupA.getId(), groupB.getId())));
		assertTrue(roomManager.isRoomAllowedToUser(roomBefore, victimBefore), "victim initially reaches B-only room");

		login(attacker.getLogin(), createPass());
		MainPage page = tester.startPage(MainPage.class);
		tester.assertRenderedPage(MainPage.class);
		tester.executeBehavior((AbstractAjaxBehavior)page.getBehaviorById(1));
		tester.executeBehavior((AbstractAjaxBehavior)page.get("main-container").getBehaviorById(0));
		WebSocketTester webSocketTester = new WebSocketTester(tester, page);
		webSocketTester.sendMessage(org.apache.openmeetings.web.common.OmWebSocketPanel.CONNECTED_MSG);
		tester.getRequest().setParameter(AreaKeys.ADMIN.zone(), "user");
		tester.executeBehavior((AbstractAjaxBehavior)page.getBehaviorById(0));
		tester.assertComponent(PATH_CHILD, UsersPanel.class);
		UsersPanel usersPanel = (UsersPanel)page.get(PATH_CHILD);

		Item<?> victimItem = null;
		for (int i = 0; i < 20; ++i) {
			Component candidate = usersPanel.get("listContainer:userList:" + i);
			if (candidate != null && victim.getId().equals(candidate.getDefaultModelObject() instanceof User u ? u.getId() : null)) {
				victimItem = (Item<?>)candidate;
				break;
			}
		}
		assertNotNull(victimItem, "victim must be present in GROUP_ADMIN-scoped list");
		tester.executeBehavior((AbstractAjaxBehavior)victimItem.getBehaviorById(0));

		UserForm form = (UserForm)usersPanel.get("form");
		@SuppressWarnings("unchecked")
		OmSelect2MultiChoice<GroupUser> memberships = (OmSelect2MultiChoice<GroupUser>)form.get("adminForm:general:groupUsers");
		Collection<GroupUser> loadedMemberships = memberships.getModelObject();
		assertNotNull(loadedMemberships);
		assertTrue(groupIds(loadedMemberships).containsAll(Set.of(groupA.getId(), groupB.getId())), "loaded form contains A and unrelated B");

		memberships.setModelObject(loadedMemberships.stream().filter(gu -> groupA.getId().equals(gu.getGroup().getId())).toList());
		tester.executeBehavior((AbstractAjaxBehavior)form.get("buttons:btn-save").getBehaviorById(0));

		User victimAfter = userDao.get(victim.getId());
		Room roomAfter = roomDao.get(room.getId());
		Set<Long> afterGroups = groupIds(victimAfter);
		boolean accessAfter = roomManager.isRoomAllowedToUser(roomAfter, victimAfter);
		assertTrue(afterGroups.contains(groupA.getId()));
		assertFalse(afterGroups.contains(groupB.getId()), "B membership must be deleted by persisted form collection");
		assertFalse(accessAfter, "victim must lose B-only room access after B membership deletion");
	}

	private static Set<Long> groupIds(User user) {
		return groupIds(user.getGroupUsers());
	}

	private static Set<Long> groupIds(Collection<GroupUser> memberships) {
		return memberships.stream().map(gu -> gu.getGroup().getId()).collect(Collectors.toSet());
	}
}
