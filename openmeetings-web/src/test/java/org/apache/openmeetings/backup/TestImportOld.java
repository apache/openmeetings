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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TestImportOld extends AbstractTestImport {
	private static final Logger log = LoggerFactory.getLogger(TestImportOld.class);

	@Autowired
	private RoomDao roomDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;

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
			log.debug("Import of backup file : '{}' is started ...", name);
			try (InputStream is = new FileInputStream(backup)) {
				backupImport.performImport(is, new ProgressHolder());
				long newGroupCount = groupDao.count();
				long newUserCount = userDao.count();
				long newRoomCount = roomDao.count();
				long newRoomGroupCount = roomDao.getGroups().size();
				long newApptCount = appointmentDao.get().size();
				long newMeetingMembersCount = meetingMemberDao.get().size();
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
}
