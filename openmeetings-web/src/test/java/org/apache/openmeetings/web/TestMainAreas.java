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

import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_BACKUP;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_CALENDAR;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_CONFIG;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_CONNECTION;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_EDIT;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_EMAIL;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_EXTRA;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_GROUP;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_LANG;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_LDAP;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_MESSAGES;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_MY;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_OAUTH2;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_PUBLIC;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_RECORDINGS;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_ROOM;
import static org.apache.openmeetings.web.util.OmUrlFragment.TYPE_USER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.Consumer;

import org.apache.openmeetings.web.admin.backup.BackupPanel;
import org.apache.openmeetings.web.admin.configurations.ConfigsPanel;
import org.apache.openmeetings.web.admin.connection.ConnectionsPanel;
import org.apache.openmeetings.web.admin.email.EmailPanel;
import org.apache.openmeetings.web.admin.extra.ExtraPanel;
import org.apache.openmeetings.web.admin.groups.GroupsPanel;
import org.apache.openmeetings.web.admin.labels.LangPanel;
import org.apache.openmeetings.web.admin.ldaps.LdapsPanel;
import org.apache.openmeetings.web.admin.oauth.OAuthPanel;
import org.apache.openmeetings.web.admin.rooms.RoomsPanel;
import org.apache.openmeetings.web.admin.users.UsersPanel;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.NotInitedPage;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.openmeetings.web.pages.install.InstallWizardPage;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.wb.AbstractWbPanel;
import org.apache.openmeetings.web.user.calendar.CalendarPanel;
import org.apache.openmeetings.web.user.dashboard.OmDashboardPanel;
import org.apache.openmeetings.web.user.profile.EditProfilePanel;
import org.apache.openmeetings.web.user.profile.MessagesContactsPanel;
import org.apache.openmeetings.web.user.record.RecordingsPanel;
import org.apache.openmeetings.web.user.rooms.RoomsSelectorPanel;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.web.util.OmUrlFragment.AreaKeys;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestMainAreas extends AbstractWicketTesterTest {
	private static final Logger log = LoggerFactory.getLogger(TestMainAreas.class);

	@Test
	void testDashboard() throws OmException {
		testArea(regularUsername, p -> {
			tester.assertComponent(PATH_CHILD, OmDashboardPanel.class);
		});
	}

	private void checkArea(AreaKeys area, String type, Class<? extends BasePanel> clazz, String... users) throws OmException {
		checkArea(area, type, clazz, null, users);
	}

	private void checkArea(AreaKeys area, String type, Class<? extends BasePanel> clazz, Consumer<MainPage> consumer, String... users) throws OmException {
		for (String user : users) {
			log.debug("Positive test:: area: {}, type: {} for user: {}", area, type, user);
			testArea(user, p -> {
				tester.getRequest().setParameter(area.zone(), type);
				tester.executeBehavior((AbstractAjaxBehavior)p.getBehaviorById(0));
				tester.assertComponent(PATH_CHILD, clazz);
				if (consumer != null) {
					consumer.accept(p);
				}
			});
		}
	}

	private void checkUnauthArea(AreaKeys area, String type, String... users) throws OmException {
		for (String user : users) {
			log.debug("Positive test:: area: {}, type: {} for user: {}", area, type, user);
			testArea(user, p -> {
				tester.getRequest().setParameter(area.zone(), type);
				AbstractAjaxBehavior authBehavior = (AbstractAjaxBehavior)p.getBehaviorById(0);
				try {
					tester.executeBehavior(authBehavior);
					fail("Not authorized");
				} catch (UnauthorizedInstantiationException e) {
					assertTrue(true, "Exception is expected");
				}
			});
		}
	}

	@Test
	void testCalendar() throws OmException {
		checkArea(AreaKeys.USER, TYPE_CALENDAR, CalendarPanel.class, regularUsername);
	}

	@Test
	void testRecordings() throws OmException {
		checkArea(AreaKeys.USER, TYPE_RECORDINGS, RecordingsPanel.class, regularUsername);
	}

	@Test
	void testRoomsPublic() throws OmException {
		checkArea(AreaKeys.ROOMS, TYPE_PUBLIC, RoomsSelectorPanel.class, regularUsername);
	}

	@Test
	void testRoomsGroup() throws OmException {
		checkArea(AreaKeys.ROOMS, TYPE_GROUP, RoomsSelectorPanel.class, regularUsername);
	}

	@Test
	void testRoomsMy() throws OmException {
		checkArea(AreaKeys.ROOMS, TYPE_MY, RoomsSelectorPanel.class, regularUsername);
	}

	@Test
	void testProfileMessages() throws OmException {
		checkArea(AreaKeys.PROFILE, TYPE_MESSAGES, MessagesContactsPanel.class, regularUsername);
	}

	@Test
	void testProfileEdit() throws OmException {
		checkArea(AreaKeys.PROFILE, TYPE_EDIT, EditProfilePanel.class, regularUsername);
	}

	@Test
	void testNonExistent() throws OmException {
		checkArea(AreaKeys.PROFILE, "bla", OmDashboardPanel.class, regularUsername);
	}

	@Test
	void testAdminUsers() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_USER, UsersPanel.class, adminUsername, groupAdminUsername);
	}

	@Test
	void testAdminUsers1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_USER, regularUsername);
	}

	@Test
	void testAdminConnections() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_CONNECTION, ConnectionsPanel.class, adminUsername);
	}

	@Test
	void testAdminConnections1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_CONNECTION, groupAdminUsername, regularUsername);
	}

	@Test
	void testAdminGroups() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_GROUP, GroupsPanel.class, adminUsername, groupAdminUsername);
	}

	@Test
	void testAdminGroups1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_GROUP, regularUsername);
	}

	@Test
	void testAdminRooms() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_ROOM, RoomsPanel.class, adminUsername, groupAdminUsername);
	}

	@Test
	void testAdminRooms1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_ROOM, regularUsername);
	}

	@Test
	void testAdminConfigs() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_CONFIG, ConfigsPanel.class, adminUsername);
	}

	@Test
	void testAdminConfigs1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_CONFIG, groupAdminUsername, regularUsername);
	}

	@Test
	void testAdminLang() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_LANG, LangPanel.class, adminUsername);
	}

	@Test
	void testAdminLang1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_LANG, groupAdminUsername, regularUsername);
	}

	@Test
	void testAdminLdap() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_LDAP, LdapsPanel.class, adminUsername);
	}

	@Test
	void testAdminLdap1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_LDAP, groupAdminUsername, regularUsername);
	}

	@Test
	void testAdminOauth() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_OAUTH2, OAuthPanel.class, adminUsername);
	}

	@Test
	void testAdminOauth1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_OAUTH2, groupAdminUsername, regularUsername);
	}

	@Test
	void testAdminBackup() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_BACKUP, BackupPanel.class, adminUsername);
	}

	@Test
	void testAdminBackup1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_BACKUP, groupAdminUsername, regularUsername);
	}

	@Test
	void testAdminEmail() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_EMAIL, EmailPanel.class, adminUsername);
	}

	@Test
	void testAdminEmail1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_EMAIL, groupAdminUsername, regularUsername);
	}

	@Test
	void testAdminExtras() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_EXTRA, ExtraPanel.class, adminUsername, groupAdminUsername);
	}

	@Test
	void testAdminExtras1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_EXTRA, regularUsername);
	}

	@Test
	void testAdminBad() throws OmException {
		checkArea(AreaKeys.ADMIN, "BAD", OmDashboardPanel.class, adminUsername);
	}

	private void testRoom(Long id) throws OmException {
		checkArea(AreaKeys.ROOM, String.valueOf(id), RoomPanel.class, p -> {
			RoomPanel rp = (RoomPanel)p.get(PATH_CHILD);
			tester.executeBehavior((AbstractAjaxBehavior)rp.get("roomContainer").getBehaviorById(0)); //room enter
			tester.assertComponent(PATH_CHILD + ":roomContainer:wb-area:whiteboard", AbstractWbPanel.class);
		}, regularUsername);
	}

	@Test
	void testRoom5() throws OmException {
		// public presentation room
		testRoom(5L);
	}

	@Test
	void testRoom1() throws OmException {
		//public interview room
		testRoom(1L);
	}

	@Test
	void testRoomBad() throws OmException {
		checkArea(AreaKeys.ROOM, "BAD", OmDashboardPanel.class, adminUsername);
	}

	@Test
	void testInstallNotAccessible() {
		tester.startPage(InstallWizardPage.class);
		tester.assertRenderedPage(SignInPage.class);
	}

	@Test
	void testUnavailNotAccessible() {
		tester.startPage(NotInitedPage.class);
		tester.assertRenderedPage(SignInPage.class);
	}
}
