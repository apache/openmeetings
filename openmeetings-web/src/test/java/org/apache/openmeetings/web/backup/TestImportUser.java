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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.List;

import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.backup.BackupException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;


class TestImportUser extends AbstractTestImport {
	@Inject
	private LdapConfigDao ldapDao;

	@Test
	void importUserNE() throws Exception {
		File configRoot = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "config/skip/configs.xml").toURI())
				.getParentFile();
		Assertions.assertThrows(BackupException.class, () -> {
			backupImport.importUsers(configRoot);
		});
	}

	@Test
	void importUsers() throws Exception {
		long userCount = userDao.count();
		File userRoot = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "user/users.xml").toURI())
				.getParentFile();
		backupImport.importGroups(userRoot);
		backupImport.importUsers(userRoot);
		assertEquals(userCount + 8, userDao.count(), "Users should be added");
		User ext = userDao.getExternalUser("234", "TheBestCms");
		assertNotNull(ext, "External user should be imported");
	}

	@Test
	void importNoLoginDeleted() throws Exception {
		long userCount = userDao.count();
		File users = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "user/skip/users.xml").toURI());
		backupImport.importUsers(users.getParentFile());
		assertEquals(userCount, userDao.count(), "No records should be added");
	}

	@Test
	void importLdap() throws Exception {
		final String login = "omLdap2294";
		// OPENMEETINGS-2294
		//clean-up
		for (LdapConfig cfg : ldapDao.get(0, Integer.MAX_VALUE)) {
			ldapDao.delete(cfg, null);
		}
		File users = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "user/ldap/users.xml").toURI());
		backupImport.cleanup();
		backupImport.importLdap(users.getParentFile());
		List<LdapConfig> ldapConfigs = ldapDao.get("om_2294_ldap", 0, 100, null);
		assertEquals(1, ldapConfigs.size(), "There should be exactly one config");
		LdapConfig ldap = ldapConfigs.get(0);

		//clean-up
		User u = userDao.getByLogin(login, User.Type.LDAP, ldap.getId());
		if (u != null) {
			userDao.purge(u, null);
		}

		//will create existing user:
		u = getUser();
		u.setLogin(login);
		u.setType(User.Type.LDAP);
		u.setDomainId(ldap.getId());
		userDao.update(u, null);

		backupImport.importUsers(users.getParentFile());
		u = userDao.getByLogin(login, User.Type.LDAP, ldap.getId());
		assertNotNull(u, "User should be imported");
	}

}
