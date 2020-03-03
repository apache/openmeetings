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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestImportUser extends AbstractTestImport {
	@Test
	public void importUserNE() throws Exception {
		Assertions.assertThrows(BackupException.class, () -> {
			File configs = new File(getClass().getClassLoader().getResource("org/apache/openmeetings/backup/config/skip/configs.xml").toURI());
			backupImport.importUsers(configs.getParentFile());
		});
	}

	@Test
	public void importUsers() throws Exception {
		long userCount = userDao.count();
		File configs = new File(getClass().getClassLoader().getResource("org/apache/openmeetings/backup/user/users.xml").toURI());
		backupImport.importUsers(configs.getParentFile());
		assertEquals(userCount + 7, userDao.count(), "Users should be added");
	}

	@Test
	public void importNoLoginDeleted() throws Exception {
		long userCount = userDao.count();
		File configs = new File(getClass().getClassLoader().getResource("org/apache/openmeetings/backup/user/skip/users.xml").toURI());
		backupImport.importUsers(configs.getParentFile());
		assertEquals(userCount, userDao.count(), "No records should be added");
	}
}
