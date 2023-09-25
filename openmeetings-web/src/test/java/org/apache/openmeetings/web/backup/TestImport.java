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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LDAP_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.backup.BackupVersion;
import org.apache.openmeetings.db.dao.basic.ChatDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.ExtraMenuDao;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.dao.user.PrivateMessageFolderDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;


class TestImport extends AbstractTestImport {
	public static final String BACKUP_ROOT = "org/apache/openmeetings/backup/";
	@Inject
	private LdapConfigDao ldapDao;
	@Inject
	private OAuth2Dao oauthDao;
	@Inject
	private ChatDao chatDao;
	@Inject
	private RecordingDao recDao;
	@Inject
	private PrivateMessageFolderDao msgFolderDao;
	@Inject
	private ExtraMenuDao menuDao;

	@Test
	void importVersionNE() throws Exception {
		File configs = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "config/skip/configs.xml").toURI());
		BackupVersion ver = BackupImport.getVersion(configs.getParentFile());
		assertEquals(new BackupVersion(), ver);
	}

	@Test
	void importVersion() throws Exception {
		File version = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "version/version.xml").toURI());
		BackupVersion ver = BackupImport.getVersion(version.getParentFile());
		assertEquals(5, ver.getMajor(), "major");
		assertEquals(0, ver.getMinor(), "minor");
		assertEquals(0, ver.getMicro(), "micro");
	}
	@Test
	void importGroups() throws Exception {
		long grpCount = groupDao.count();
		File groups = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "group/organizations.xml").toURI());
		backupImport.importGroups(groups.getParentFile());
		assertEquals(grpCount + 1, groupDao.count(), "Group should be added");
	}

	@Test
	void importLdaps() throws Exception {
		Configuration def = cfgDao.get(CONFIG_DEFAULT_LDAP_ID);
		if (def != null) {
			def.setValueN(null);
			cfgDao.update(def, null);
		}
		long ldapCount = ldapDao.count();
		File ldaps = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "ldap/ldapconfigs.xml").toURI());
		Long id = backupImport.importLdap(ldaps.getParentFile());
		assertEquals(ldapCount + 1, ldapDao.count(), "Ldap should be added");
		LdapConfig ldap = ldapDao.get(id);
		assertEquals("unit_test_ldap", ldap.getName(), "Name should match");
	}

	@Test
	void importOauths() throws Exception {
		long oauthCount = oauthDao.count();
		File oauths = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "oauth/oauth2servers.xml").toURI());
		backupImport.importOauth(oauths.getParentFile());
		assertEquals(oauthCount + 2, oauthDao.count(), "OAuth should be added");
	}

	@Test
	void importChatSkip() throws Exception {
		long chatCount = chatDao.get(0, Integer.MAX_VALUE).size();
		File chats = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "chat/skip/chat_messages.xml").toURI());
		backupImport.importChat(chats.getParentFile());
		assertEquals(chatCount, chatDao.get(0, Integer.MAX_VALUE).size(), "No Chat messages should be added");
	}

	@Test
	void importChat() throws Exception {
		long chatCount = chatDao.get(0, Integer.MAX_VALUE).size();
		File chats = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "chat/chat_messages.xml").toURI());
		backupImport.importChat(chats.getParentFile());
		assertEquals(chatCount + 3, chatDao.get(0, Integer.MAX_VALUE).size(), "Chat messages should be added");
	}

	@Test
	void importExtraMenu() throws Exception {
		long menuCount = menuDao.count();
		File menus = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "menu/extraMenus.xml").toURI());
		backupImport.importExtraMenus(menus.getParentFile());
		assertEquals(menuCount + 3, menuDao.count(), "Extra menus should be added");
	}

	@Test
	void importRecordings() throws Exception {
		long recCount = recDao.get().size();
		File recs = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "file/flvRecordings.xml").toURI());
		backupImport.importRecordings(recs.getParentFile());
		assertEquals(recCount + 4, recDao.get().size(), "Recordings should be added");
	}

	@Test
	void importMsgFolders() throws Exception {
		long fldrCount = msgFolderDao.get(0, Integer.MAX_VALUE).size();
		File fldrs = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "msg/privateMessageFolder.xml").toURI());
		backupImport.importPrivateMsgFolders(fldrs.getParentFile());
		assertEquals(fldrCount + 1, msgFolderDao.get(0, Integer.MAX_VALUE).size(), "Message folders should be added");
	}
}
