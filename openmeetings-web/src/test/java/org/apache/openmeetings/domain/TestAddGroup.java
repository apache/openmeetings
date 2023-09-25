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
package org.apache.openmeetings.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestAddGroup extends AbstractOmServerTest {
	private static final Logger log = LoggerFactory.getLogger(TestAddGroup.class);

	@Test
	void testAddingGroup() {
		Group o = new Group();
		o.setName("default");
		o = groupDao.update(o, null);
		assertNotNull(o.getId(), "Id of group created should not be null");

		User us = userDao.get(1L);
		assertNotNull(us, "User should exist");

		assertNotNull(us.getGroupUsers(), "Group User list should exist");
		us.addGroup(o);
		us = userDao.update(us, null);

		log.error(us.getLastname());
		log.error(us.getAddress().getTown());
	}
}
