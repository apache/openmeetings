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
package org.apache.openmeetings.web.db.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.dao.user.PrivateMessageFolderDao;
import org.apache.openmeetings.db.entity.user.PrivateMessageFolder;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestMessageDao extends AbstractOmServerTest {
	@Autowired
	private PrivateMessageFolderDao msgFolderDao;

	@Test
	void testMessageFolder() throws Exception {
		User u1 = createUser();
		String uiFolderName = "u1Folder-" + u1.getLogin();
		Long fldId = msgFolderDao.addPrivateMessageFolder(uiFolderName, u1.getId());

		User u2 = createUser();
		List<PrivateMessageFolder> folders = msgFolderDao.getByUser(u2.getId());
		assertFalse(folders.stream().anyMatch(fld -> fldId.equals(fld.getId()))
				, "Folders of first user shouldn't be visible to second one");
	}
}
