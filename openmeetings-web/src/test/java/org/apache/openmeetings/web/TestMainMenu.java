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

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.util.OmException;
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
import org.apache.openmeetings.web.user.record.RecordingsPanel;
import org.apache.openmeetings.web.user.rooms.RoomsSelectorPanel;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.junit.Assert;
import org.junit.Test;

import com.googlecode.wicket.jquery.ui.widget.menu.Menu;

public class TestMainMenu extends AbstractWicketTester {
	private void checkMenuItem(int idx1, int idx2, Class<? extends BasePanel> clazz) throws OmException {
		testArea(adminUsername, p -> {
			Menu menu = (Menu)p.get(PATH_MENU);
			Assert.assertNotNull(menu);
			tester.getRequest().setParameter("hash", menu.getItemList().get(idx1).getItems().get(idx2).getId());
			tester.executeBehavior((AbstractAjaxBehavior)menu.getBehaviorById(0));

			tester.assertComponent(PATH_CHILD, clazz);
		});
	}

	@Test
	public void testDahboard() throws OmException {
		checkMenuItem(0, 0, OmDashboardPanel.class);
	}

	@Test
	public void testCalendar() throws OmException {
		checkMenuItem(0, 1, CalendarPanel.class);
	}

	@Test
	public void testRoomPublic() throws OmException {
		checkMenuItem(1, 0, RoomsSelectorPanel.class);
	}

	@Test
	public void testRoomGroup() throws OmException {
		checkMenuItem(1, 1, RoomsSelectorPanel.class);
	}

	@Test
	public void testRoomMy() throws OmException {
		checkMenuItem(1, 2, RoomsSelectorPanel.class);
	}

	@Test
	public void testRecordings() throws OmException {
		checkMenuItem(2, 0, RecordingsPanel.class);
	}

	@Test
	public void testAdminUsers() throws OmException {
		checkMenuItem(3, 0, UsersPanel.class);
	}

	@Test
	public void testAdminConnections() throws OmException {
		checkMenuItem(3, 1, ConnectionsPanel.class);
	}

	@Test
	public void testAdminGroups() throws OmException {
		checkMenuItem(3, 2, GroupsPanel.class);
	}

	@Test
	public void testAdminRooms() throws OmException {
		checkMenuItem(3, 3, RoomsPanel.class);
	}

	@Test
	public void testAdminConfigs() throws OmException {
		checkMenuItem(3, 4, ConfigsPanel.class);
	}

	@Test
	public void testAdminLabels() throws OmException {
		checkMenuItem(3, 5, LangPanel.class);
	}

	@Test
	public void testAdminLaps() throws OmException {
		checkMenuItem(3, 6, LdapsPanel.class);
	}

	@Test
	public void testAdminOauth() throws OmException {
		checkMenuItem(3, 7, OAuthPanel.class);
	}

	@Test
	public void testAdminBackup() throws OmException {
		checkMenuItem(3, 8, BackupPanel.class);
	}

	@Test
	public void testAdminEmail() throws OmException {
		checkMenuItem(3, 9, EmailPanel.class);
	}
}
