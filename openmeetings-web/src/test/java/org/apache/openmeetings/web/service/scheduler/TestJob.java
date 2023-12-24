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
package org.apache.openmeetings.web.service.scheduler;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setInitComplete;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.scheduler.CleanupJob;
import org.apache.openmeetings.service.scheduler.ReminderJob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import jakarta.inject.Inject;


@ExtendWith(MockitoExtension.class)
class TestJob extends AbstractWicketTesterTest {
	@Inject
	private CleanupJob cleanJob;
	@Mock
	private MailHandler mailHandler;
	@Inject
	@InjectMocks
	private ReminderJob reminderJob;

	@Test
	void testNotInited() {
		try {
			setInitComplete(false);
			cleanJob.cleanExpiredRecordings();
			cleanJob.cleanExpiredResetHash();
			cleanJob.cleanSessions();
			cleanJob.cleanTestSetup();

			reminderJob.loadRss();
			reminderJob.remindExpiringRecordings();
			reminderJob.remindMeetings();
			reminderJob.notifyNewGroupUsers();
			assertTrue(true, "All methods are executed, no exception");
		} finally {
			setInitComplete(true);
		}
	}

	private void testRss(boolean enabled, Runnable r) {
		boolean prevRss = cfgDao.getBool(CONFIG_DASHBOARD_SHOW_RSS, false);
		Configuration cfg = cfgDao.get(CONFIG_DASHBOARD_SHOW_RSS);
		try {
			cfg.setValueB(enabled);
			cfgDao.update(cfg, null);
			r.run();
		} finally {
			cfg.setValueB(prevRss);
			cfgDao.update(cfg, null);
		}
	}

	@Test
	void testRssDisabled() {
		testRss(false, () -> reminderJob.loadRss());
	}

	@Test
	void testRssEnabled() {
		testRss(true, () -> reminderJob.loadRss());
	}

	@Test
	void testNotifyNewGroupUsers() throws Exception {
		Group g = new Group();
		g.setName("NotifyNewGroupUsers");
		g.setNotifyInterval(1);
		groupDao.update(g, null);
		User ga = getUser();
		ga.setGroupUsers(List.of(new GroupUser(g, ga)));
		ga.getGroupUsers().get(0).setModerator(true);
		userDao.update(ga, null);
		User u = getUser();
		u.setGroupUsers(List.of(new GroupUser(g, u)));
		userDao.update(u, null);

		// to ensure test will run through all branches
		reminderJob.groupNotifications.clear();
		reminderJob.notifyNewGroupUsers(); // will set initial checkTime
		assertNotNull(reminderJob.groupNotifications.get(g.getId()));

		// will check "too-early" branch
		reminderJob.notifyNewGroupUsers();
		verify(mailHandler, times(0)).send(anyString(), anyString(), anyString());

		// will check mail send
		reminderJob.groupNotifications.put(g.getId(), LocalDateTime.now().minusHours(1).minusMinutes(1));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				return null;
			}
		}).when(mailHandler).send(anyString(), anyString(), anyString());
		reminderJob.notifyNewGroupUsers();
		verify(mailHandler, times(1)).send(anyString(), anyString(), anyString());

		// will check no new users
		reminderJob.groupNotifications.put(g.getId(), LocalDateTime.now().minusHours(1).minusMinutes(1));
		Date newInserted = Date.from(LocalDateTime.now().minusHours(2).atZone(ZoneId.systemDefault()).toInstant());
		ga.getGroupUsers().get(0).setInserted(newInserted);
		userDao.update(ga, null);
		u.getGroupUsers().get(0).setInserted(newInserted);
		userDao.update(u, null);
		reminderJob.notifyNewGroupUsers();
		verify(mailHandler, times(1)).send(anyString(), anyString(), anyString());
	}
}
