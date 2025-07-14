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
		testArea(REGULAR_USERNAME, p -> {
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
		checkArea(AreaKeys.USER, TYPE_CALENDAR, CalendarPanel.class, REGULAR_USERNAME);
	}

	@Test
	void testRecordings() throws OmException {
		checkArea(AreaKeys.USER, TYPE_RECORDINGS, RecordingsPanel.class, REGULAR_USERNAME);
	}

	@Test
	void testRoomsPublic() throws OmException {
		checkArea(AreaKeys.ROOMS, TYPE_PUBLIC, RoomsSelectorPanel.class, REGULAR_USERNAME);
	}

	@Test
	void testRoomsGroup() throws OmException {
		checkArea(AreaKeys.ROOMS, TYPE_GROUP, RoomsSelectorPanel.class, REGULAR_USERNAME);
	}

	@Test
	void testRoomsMy() throws OmException {
		checkArea(AreaKeys.ROOMS, TYPE_MY, RoomsSelectorPanel.class, REGULAR_USERNAME);
	}

	@Test
	void testProfileMessages() throws OmException {
		checkArea(AreaKeys.PROFILE, TYPE_MESSAGES, MessagesContactsPanel.class, REGULAR_USERNAME);
	}

	@Test
	void testProfileEdit() throws OmException {
		checkArea(AreaKeys.PROFILE, TYPE_EDIT, EditProfilePanel.class, REGULAR_USERNAME);
	}

	@Test
	void testNonExistent() throws OmException {
		checkArea(AreaKeys.PROFILE, "bla", OmDashboardPanel.class, REGULAR_USERNAME);
	}

	@Test
	void testAdminUsers() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_USER, UsersPanel.class, ADMIN_USERNAME, GROUP_ADMIN_USERNAME);
	}

	@Test
	void testAdminUsers1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_USER, REGULAR_USERNAME);
	}

	@Test
	void testAdminConnections() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_CONNECTION, ConnectionsPanel.class, ADMIN_USERNAME);
	}

	@Test
	void testAdminConnections1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_CONNECTION, GROUP_ADMIN_USERNAME, REGULAR_USERNAME);
	}

	@Test
	void testAdminGroups() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_GROUP, GroupsPanel.class, ADMIN_USERNAME, GROUP_ADMIN_USERNAME);
	}

	@Test
	void testAdminGroups1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_GROUP, REGULAR_USERNAME);
	}

	@Test
	void testAdminRooms() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_ROOM, RoomsPanel.class, ADMIN_USERNAME, GROUP_ADMIN_USERNAME);
	}

	@Test
	void testAdminRooms1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_ROOM, REGULAR_USERNAME);
	}

	@Test
	void testAdminConfigs() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_CONFIG, ConfigsPanel.class, ADMIN_USERNAME);
	}

	@Test
	void testAdminConfigs1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_CONFIG, GROUP_ADMIN_USERNAME, REGULAR_USERNAME);
	}

	@Test
	void testAdminLang() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_LANG, LangPanel.class, ADMIN_USERNAME);
	}

	@Test
	void testAdminLang1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_LANG, GROUP_ADMIN_USERNAME, REGULAR_USERNAME);
	}

	@Test
	void testAdminLdap() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_LDAP, LdapsPanel.class, ADMIN_USERNAME);
	}

	@Test
	void testAdminLdap1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_LDAP, GROUP_ADMIN_USERNAME, REGULAR_USERNAME);
	}

	@Test
	void testAdminOauth() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_OAUTH2, OAuthPanel.class, ADMIN_USERNAME);
	}

	@Test
	void testAdminOauth1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_OAUTH2, GROUP_ADMIN_USERNAME, REGULAR_USERNAME);
	}

	@Test
	void testAdminBackup() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_BACKUP, BackupPanel.class, ADMIN_USERNAME);
	}

	@Test
	void testAdminBackup1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_BACKUP, GROUP_ADMIN_USERNAME, REGULAR_USERNAME);
	}

	@Test
	void testAdminEmail() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_EMAIL, EmailPanel.class, ADMIN_USERNAME);
	}

	@Test
	void testAdminEmail1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_EMAIL, GROUP_ADMIN_USERNAME, REGULAR_USERNAME);
	}

	@Test
	void testAdminExtras() throws OmException {
		checkArea(AreaKeys.ADMIN, TYPE_EXTRA, ExtraPanel.class, ADMIN_USERNAME, GROUP_ADMIN_USERNAME);
	}

	@Test
	void testAdminExtras1() throws OmException {
		checkUnauthArea(AreaKeys.ADMIN, TYPE_EXTRA, REGULAR_USERNAME);
	}

	@Test
	void testAdminBad() throws OmException {
		checkArea(AreaKeys.ADMIN, "BAD", OmDashboardPanel.class, ADMIN_USERNAME);
	}

	private void testRoom(Long id) throws OmException {
		checkArea(AreaKeys.ROOM, String.valueOf(id), RoomPanel.class, p -> {
			RoomPanel rp = (RoomPanel)p.get(PATH_CHILD);
			tester.executeBehavior((AbstractAjaxBehavior)rp.get("roomContainer").getBehaviorById(0)); //room enter
			tester.assertComponent(PATH_CHILD + ":roomContainer:wb-area:whiteboard", AbstractWbPanel.class);
		}, REGULAR_USERNAME);
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
		checkArea(AreaKeys.ROOM, "BAD", OmDashboardPanel.class, ADMIN_USERNAME);
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
