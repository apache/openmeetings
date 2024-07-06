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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;


class TestFileDao extends AbstractOmServerTest {
	@Inject
	protected FileItemDao fileDao;
	@Inject
	protected GroupDao groupDao;

	@Test
	void testExtType() throws Exception {
		Group g = new Group();
		g.setExternal(true);
		g.setName(UNIT_TEST_ARAB_EXT_TYPE);
		g = groupDao.update(g, null);
		User u = getUser();
		u.addGroup(g);
		u = createUser(u);

		FileItem f = new FileItem();
		f.setName("Arab external test");
		f.setType(BaseFileItem.Type.IMAGE);
		f.setInsertedBy(u.getId());
		fileDao.update(f);

		List<FileItem> list1 = fileDao.getExternal(UNIT_TEST_ARAB_EXT_TYPE);
		assertNotNull(list1);
		assertFalse(list1.isEmpty());
	}
}
