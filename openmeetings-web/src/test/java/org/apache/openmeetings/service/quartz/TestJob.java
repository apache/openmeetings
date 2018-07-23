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
package org.apache.openmeetings.service.quartz;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setInitComplete;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.service.quartz.scheduler.CleanupJob;
import org.apache.openmeetings.service.quartz.scheduler.ReminderJob;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJob extends AbstractWicketTester {
	@Autowired
	private CleanupJob cleanJob;
	@Autowired
	private ReminderJob reminderJob;

	@Test
	public void testNotInited() {
		try {
			setInitComplete(false);
			cleanJob.cleanExpiredRecordings();
			cleanJob.cleanExpiredResetHash();
			cleanJob.cleanSessions();
			cleanJob.cleanTestSetup();

			reminderJob.loadRss();
			reminderJob.remindExpiringRecordings();
			reminderJob.remindMeetings();
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
	public void testRssDisabled() {
		testRss(false, () -> reminderJob.loadRss());
	}

	@Test
	public void testRssEnabled() {
		testRss(true, () -> reminderJob.loadRss());
	}
}
