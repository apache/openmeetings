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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomOrganisationDao;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestOldBackups extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestOldBackups.class, webAppRootKey);
	
	@Autowired
	private BackupImport backupController;
	@Autowired
	private OrganisationDao organisationDao;
	@Autowired
	private UserDao usersDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private RoomOrganisationDao roomOrganisationDao;

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
			String name = backup.getName();
			log.debug("Import of backup file : '" + name + "' is started ...");
			InputStream is = null;
			try {
				is = new FileInputStream(backup);
				backupController.performImport(is);
				long newOrgCount = organisationDao.count();
				long newUserCount = usersDao.count();
				long newRoomCount = roomDao.count();
				long newRoomOrgCount = roomOrganisationDao.get().size();
				long newApptCount = appointmentDao.getAppointments().size();
				log.debug("Now DB contains '" + newApptCount + "' appointments");
				long newMeetingMembersCount = meetingMemberDao.getMeetingMembers().size();
				assertTrue("Zero organizations were imported from " + name, newOrgCount > orgCount);
				assertTrue("Zero users were imported from " + name, newUserCount > userCount);
				assertTrue("Zero rooms were imported from " + name, newRoomCount > roomCount);
				assertTrue("Zero room organizations were imported from " + name, newRoomOrgCount > roomOrgCount);
				assertTrue("Zero appointments were imported from " + name + ", apptCount = " + apptCount, newApptCount > apptCount);
				assertTrue("Zero meeting members were imported from " + name, newMeetingMembersCount > meetingMembersCount);
				
				orgCount = newOrgCount;
				userCount = newUserCount;
				roomCount = newRoomCount;
				roomOrgCount = newRoomOrgCount;
				apptCount = newApptCount;
				meetingMembersCount = newMeetingMembersCount;
			} catch (Exception e) {
				throw new RuntimeException("Unexpected exception while importing backup: " + name, e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						throw new RuntimeException("Error while closing ldap config file", e);
					}
				}
			}
			log.debug("... Done.");
		}
	}
}
