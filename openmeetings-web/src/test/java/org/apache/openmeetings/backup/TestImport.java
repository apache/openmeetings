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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LDAP_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestImport extends AbstractTestImport {
	@Autowired
	private LdapConfigDao ldapDao;
	@Autowired
	private OAuth2Dao oauthDao;
	@Autowired
	private RoomDao roomDao;

	@Test
	public void importVersionNE() throws Exception {
		File configs = new File(getClass().getClassLoader().getResource("org/apache/openmeetings/backup/config/nokey_deleted/configs.xml").toURI());
		BackupVersion ver = BackupImport.getVersion(configs.getParentFile());
		assertEquals(new BackupVersion(), ver);
	}

	@Test
	public void importVersion() throws Exception {
		File version = new File(getClass().getClassLoader().getResource("org/apache/openmeetings/backup/version/version.xml").toURI());
		BackupVersion ver = BackupImport.getVersion(version.getParentFile());
		assertEquals(5, ver.getMajor(), "major");
		assertEquals(0, ver.getMinor(), "minor");
		assertEquals(0, ver.getMicro(), "micro");
	}
	@Test
	public void importGroups() throws Exception {
		long grpCount = groupDao.count();
		File groups = new File(getClass().getClassLoader().getResource("org/apache/openmeetings/backup/group/organizations.xml").toURI());
		backupImport.importGroups(groups.getParentFile());
		assertEquals(grpCount + 1, groupDao.count(), "Group should be added");
	}

	@Test
	public void importLdaps() throws Exception {
		Configuration def = cfgDao.get(CONFIG_DEFAULT_LDAP_ID);
		if (def != null) {
			def.setValueN(null);
			cfgDao.update(def, null);
		}
		long ldapCount = ldapDao.count();
		File ldaps = new File(getClass().getClassLoader().getResource("org/apache/openmeetings/backup/ldap/ldapconfigs.xml").toURI());
		Long id = backupImport.importLdap(ldaps.getParentFile());
		assertEquals(ldapCount + 1, ldapDao.count(), "Ldap should be added");
		LdapConfig ldap = ldapDao.get(id);
		assertEquals("unit_test_ldap", ldap.getName(), "Name should match");
	}

	@Test
	public void importOauths() throws Exception {
		long oauthCount = oauthDao.count();
		File oauths = new File(getClass().getClassLoader().getResource("org/apache/openmeetings/backup/oauth/oauth2servers.xml").toURI());
		backupImport.importOauth(oauths.getParentFile());
		assertEquals(oauthCount + 2, oauthDao.count(), "OAuth should be added");
	}

	@Test
	public void importRooms() throws Exception {
		long roomsCount = roomDao.count();
		File rooms = new File(getClass().getClassLoader().getResource("org/apache/openmeetings/backup/room/rooms.xml").toURI());
		backupImport.importRooms(rooms.getParentFile());
		assertEquals(roomsCount + 1, roomDao.count(), "Room should be added");
	}
}
