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
package org.apache.openmeetings.backup;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_FFMPEG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_IMAGEMAGIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_OFFICE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_SOX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getCryptClassName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TestBackup extends AbstractJUnitDefaults {
	private static final Logger log = LoggerFactory.getLogger(TestBackup.class);
	private String cryptClass = null;

	@Autowired
	private BackupImport backupController;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;

	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		// Crypt class need to be preserved here to avoid overriding by backup import
		cryptClass = getCryptClassName();
	}

	@AfterEach
	public void tearDown() {
		Configuration cfg = cfgDao.get(CONFIG_CRYPT);
		assertNotNull(cfg, "Not null config should be returned");
		cfg.setValue(cryptClass);
		cfgDao.update(cfg, null);
		for (String key : new String[] {CONFIG_PATH_IMAGEMAGIC, CONFIG_PATH_FFMPEG, CONFIG_PATH_OFFICE, CONFIG_PATH_SOX}) {
			Configuration c = cfgDao.get(key);
			assertNotNull(c, "Not null config should be returned");
			c.setValue("");
			cfgDao.update(c, null);
		}
	}

	@Test
	public void importOldVersions() {
		String backupsDir = System.getProperty("backups.dir", ".");
		File backupsHome = new File(backupsDir);

		if (!backupsHome.exists() || !backupsHome.isDirectory()) {
			fail("Invalid directory is specified for backup files: " + backupsDir);
		}
		long groupCount = 0;
		long userCount = 0;
		long roomCount = 0;
		long roomGroupCount = 0;
		long apptCount = 0;
		long meetingMembersCount = 0;
		for (File backup : backupsHome.listFiles()) {
			String name = backup.getName();
			log.debug("Import of backup file : '" + name + "' is started ...");
			try (InputStream is = new FileInputStream(backup)) {
				backupController.performImport(is);
				long newGroupCount = groupDao.count();
				long newUserCount = userDao.count();
				long newRoomCount = roomDao.count();
				long newRoomGroupCount = roomDao.getGroups().size();
				long newApptCount = appointmentDao.get().size();
				long newMeetingMembersCount = meetingMemberDao.getMeetingMembers().size();
				assertTrue(newGroupCount > groupCount, "Zero groups were imported from " + name);
				assertTrue(newUserCount > userCount, "Zero users were imported from " + name);
				assertTrue(newRoomCount > roomCount, "Zero rooms were imported from " + name);
				assertTrue(newRoomGroupCount > roomGroupCount, "Zero room groups were imported from " + name);
				assertTrue(newApptCount > apptCount, "Zero appointments were imported from " + name);
				assertTrue(newMeetingMembersCount > meetingMembersCount, "Zero meeting members were imported from " + name);

				groupCount = newGroupCount;
				userCount = newUserCount;
				roomCount = newRoomCount;
				roomGroupCount = newRoomGroupCount;
				apptCount = newApptCount;
				meetingMembersCount = newMeetingMembersCount;
			} catch (Exception e) {
				throw new RuntimeException("Unexpected exception while importing backup: " + name, e);
			}
			log.debug("... Done.");
		}
	}

	@Test
	public void exportMain() throws Exception {
		BackupExport.main(new String[] {File.createTempFile("gereral", "cfg").getCanonicalPath()});
	}
}
