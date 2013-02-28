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
package org.apache.openmeetings.test.backup;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.calendar.daos.MeetingMemberDao;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.file.dao.FileExplorerItemDao;
import org.apache.openmeetings.data.user.dao.OrganisationDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.servlet.outputhandler.BackupImportController;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestOldBackups extends AbstractOpenmeetingsSpringTest {
	private static final Logger log = Red5LoggerFactory.getLogger(
			TestOldBackups.class, OpenmeetingsVariables.webAppRootKey);
	
	@Autowired
	private BackupImportController backupController;
	@Autowired
	private OrganisationDao organisationDao;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private FileExplorerItemDao fileExplorerItemDao;

	@Test
	public void importOldVersions() {
		String backupsDir = System.getProperty("backups.dir", ".");
		File backupsHome = new File(backupsDir);
		
		if (!backupsHome.exists() || !backupsHome.isDirectory()) {
			fail("Invalid directory is specified for backup files: " + backupsDir);
		}
		long orgCount = 0;
		long userCount = 0;
		long roomCount = 0;
		long roomOrgCount = 0;
		long apptCount = 0;
		long meetingMembersCount = 0;
		for (File backup : backupsHome.listFiles()) {
			log.debug("Import of backup file : '" + backup.getName() + "' is started ...");
			try {
				backupController.performImport(new FileInputStream(backup));
				long newOrgCount = organisationDao.count();
				long newUserCount = usersDao.count();
				long newRoomCount = roomDao.count();
				long newRoomOrgCount = roomManager.getRoomsOrganisations().size();
				long newApptCount = appointmentDao.getAppointments().size();
				long newMeetingMembersCount = meetingMemberDao.getMeetingMembers().size();
				assertTrue("Zero organizations were imported", newOrgCount > orgCount);
				assertTrue("Zero users were imported", newUserCount > userCount);
				assertTrue("Zero rooms were imported", newRoomCount > roomCount);
				assertTrue("Zero room organizations were imported", newRoomOrgCount > roomOrgCount);
				assertTrue("Zero appointments were imported", newApptCount > apptCount);
				assertTrue("Zero meeting members were imported", newMeetingMembersCount > meetingMembersCount);
				
				orgCount = newOrgCount;
				userCount = newUserCount;
				roomCount = newRoomCount;
				roomOrgCount = newRoomOrgCount;
				apptCount = newApptCount;
				meetingMembersCount = newMeetingMembersCount;
			} catch (Exception e) {
				throw new RuntimeException("Unexpected exception while importing backup: " + backup.getName(), e);
			}
			log.debug("... Done.");
		}
	}
}
