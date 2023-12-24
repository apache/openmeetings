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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.openmeetings.web.admin.backup.BackupPanel;
import org.apache.openmeetings.web.admin.configurations.ConfigsPanel;
import org.apache.openmeetings.web.admin.connection.ConnectionsPanel;
import org.apache.openmeetings.web.admin.email.EmailPanel;
import org.apache.openmeetings.web.admin.groups.GroupsPanel;
import org.apache.openmeetings.web.admin.labels.LangPanel;
import org.apache.openmeetings.web.admin.ldaps.LdapsPanel;
import org.apache.openmeetings.web.admin.oauth.OAuthPanel;
import org.apache.openmeetings.web.admin.rooms.RoomsPanel;
import org.apache.openmeetings.web.admin.users.UsersPanel;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.user.calendar.CalendarPanel;
import org.apache.openmeetings.web.user.dashboard.OmDashboardPanel;
import org.apache.openmeetings.web.user.profile.EditProfilePanel;
import org.apache.openmeetings.web.user.profile.InvitationsPanel;
import org.apache.openmeetings.web.user.profile.MessagesContactsPanel;
import org.apache.openmeetings.web.user.profile.UserSearchPanel;
import org.apache.openmeetings.web.user.profile.WidgetsPanel;
import org.apache.openmeetings.web.user.record.RecordingsPanel;
import org.apache.openmeetings.web.user.rooms.RoomsSelectorPanel;
import org.apache.openmeetings.util.OmException;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.junit.jupiter.api.Test;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;

class TestMainMenu extends AbstractWicketTesterTest {
	private void checkMenuItem(int menuIdx, Integer subMenuIdx, Class<? extends BasePanel> clazz) throws OmException {
		testArea(adminUsername, p -> {
			Navbar menu = (Navbar)p.get(PATH_MENU);
			assertNotNull(menu);
			String path = "container:collapse:navLeftListEnclosure:navLeftList:" + menuIdx;
			if (subMenuIdx == null) {
				path += ":component";
			} else {
				path += ":component:dropdown-menu:buttons:" + subMenuIdx + ":button";
			}
			tester.executeBehavior((AbstractAjaxBehavior)menu.get(path).getBehaviorById(0));

			tester.assertComponent(PATH_CHILD, clazz);
		});
	}

	@Test
	void testDahboard() throws OmException {
		checkMenuItem(0, 0, OmDashboardPanel.class);
	}

	@Test
	void testCalendar() throws OmException {
		checkMenuItem(0, 1, CalendarPanel.class);
	}

	@Test
	void testRoomPublic() throws OmException {
		checkMenuItem(1, 0, RoomsSelectorPanel.class);
	}

	@Test
	void testRoomGroup() throws OmException {
		checkMenuItem(1, 1, RoomsSelectorPanel.class);
	}

	@Test
	void testRoomMy() throws OmException {
		checkMenuItem(1, 2, RoomsSelectorPanel.class);
	}

	@Test
	void testRecordings() throws OmException {
		checkMenuItem(2, null, RecordingsPanel.class);
	}

	@Test
	void testSettingsMessages() throws OmException {
		checkMenuItem(3, 0, MessagesContactsPanel.class);
	}

	@Test
	void testSettingsProfile() throws OmException {
		checkMenuItem(3, 1, EditProfilePanel.class);
	}

	@Test
	void testSettingsSearch() throws OmException {
		checkMenuItem(3, 2, UserSearchPanel.class);
	}

	@Test
	void testSettingsInvitations() throws OmException {
		checkMenuItem(3, 3, InvitationsPanel.class);
	}

	@Test
	void testSettingsWidgets() throws OmException {
		checkMenuItem(3, 4, WidgetsPanel.class);
	}

	@Test
	void testAdminUsers() throws OmException {
		checkMenuItem(4, 0, UsersPanel.class);
	}

	@Test
	void testAdminConnections() throws OmException {
		checkMenuItem(4, 1, ConnectionsPanel.class);
	}

	@Test
	void testAdminGroups() throws OmException {
		checkMenuItem(4, 2, GroupsPanel.class);
	}

	@Test
	void testAdminRooms() throws OmException {
		checkMenuItem(4, 3, RoomsPanel.class);
	}

	@Test
	void testAdminConfigs() throws OmException {
		checkMenuItem(4, 4, ConfigsPanel.class);
	}

	@Test
	void testAdminLabels() throws OmException {
		checkMenuItem(4, 5, LangPanel.class);
	}

	@Test
	void testAdminLaps() throws OmException {
		checkMenuItem(4, 6, LdapsPanel.class);
	}

	@Test
	void testAdminOauth() throws OmException {
		checkMenuItem(4, 7, OAuthPanel.class);
	}

	@Test
	void testAdminBackup() throws OmException {
		checkMenuItem(4, 8, BackupPanel.class);
	}

	@Test
	void testAdminEmail() throws OmException {
		checkMenuItem(4, 9, EmailPanel.class);
	}
}
