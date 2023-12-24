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
package org.apache.openmeetings.web.backup;

import static org.apache.openmeetings.web.backup.TestImport.BACKUP_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;


class TestImportOld extends AbstractTestImport {
	private static final Logger log = LoggerFactory.getLogger(TestImportOld.class);

	@Inject
	private RoomDao roomDao;
	@Inject
	private MeetingMemberDao meetingMemberDao;
	@Inject
	private FileItemDao fileDao;

	@Test
	void importOldVersions() {
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
				backupImport.performImport(is, new AtomicInteger());
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

	/**
	 * Test for https://issues.apache.org/jira/browse/OPENMEETINGS-2423
	 *
	 * @throws Exception
	 */
	@Test
	void importJira2423() throws Exception {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(BACKUP_ROOT + "jira2423/backup_2423.zip")) {
			backupImport.performImport(is, new AtomicInteger());

			Group grp2 = groupDao.get("group2_jira_2423");
			User usr2 = userDao.getByLogin("testUser2_jira_2423", User.Type.USER, null);
			assertTrue(usr2.getGroupUsers().stream().filter(gu -> gu.getGroup().getId().equals(grp2.getId())).findFirst().isPresent(), "User2 should belong to group2");
			roomDao.getMyRooms(usr2.getId(), "bla", "bla1").forEach(r -> {
				assertTrue(r.getComment().contains("user2_jira_2423"));
			});
			FileItem f1 = fileDao.get("820b356c-2c96-4634-90c4-3e490432987f", FileItem.class);
			assertEquals(usr2.getId(), f1.getInsertedBy(), "Inserted by is wrong");
			assertEquals(usr2.getId(), f1.getOwnerId(), "Owner is wrong");
			FileItem f2 = fileDao.get("7af3f90d-2a8d-44fa-9e0f-79fd87511cc6", FileItem.class);
			assertEquals(grp2.getId(), f2.getGroupId(), "Group is wrong");
		}
	}
}
