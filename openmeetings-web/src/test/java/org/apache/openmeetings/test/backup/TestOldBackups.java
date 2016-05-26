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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomGroupDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.After;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestOldBackups extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestOldBackups.class, webAppRootKey);
	
	@Autowired
	private BackupImport backupController;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private RoomGroupDao roomGroupDao;
	private String cryptClass = null;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		// Crypt class need to be preserved here to avoid overriding by backup import
		cryptClass = cfgDao.getCryptKey();
	}
	
	@After
	public void tearDown() {
		List<Configuration> cfgs = cfgDao.get(CONFIG_CRYPT_KEY);
		assertNotNull("Not null list should be returned", cfgs);
		assertEquals("There should be exactly 1 item", 1, cfgs.size());
		Configuration c = cfgs.get(0);
		c.setValue(cryptClass);
		cfgDao.update(c, null);
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
				long newRoomGroupCount = roomGroupDao.get().size();
				long newApptCount = appointmentDao.get().size();
				long newMeetingMembersCount = meetingMemberDao.getMeetingMembers().size();
				assertTrue("Zero groups were imported from " + name, newGroupCount > groupCount);
				assertTrue("Zero users were imported from " + name, newUserCount > userCount);
				assertTrue("Zero rooms were imported from " + name, newRoomCount > roomCount);
				assertTrue("Zero room groups were imported from " + name, newRoomGroupCount > roomGroupCount);
				assertTrue("Zero appointments were imported from " + name, newApptCount > apptCount);
				assertTrue("Zero meeting members were imported from " + name, newMeetingMembersCount > meetingMembersCount);
				
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
