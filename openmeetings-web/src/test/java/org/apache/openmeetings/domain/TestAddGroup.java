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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.junit.Assert.assertNotNull;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class TestAddGroup extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestAddGroup.class, getWebAppRootKey());

	@Test
	public void testAddingGroup() {
		Group o = new Group();
		o.setName("default");
		o = groupDao.update(o, null);
		assertNotNull("Id of group created should not be null", o.getId());

		User us = userDao.get(1L);
		assertNotNull("User should exist", us);

		assertNotNull("Group User list should exist", us.getGroupUsers());
		us.getGroupUsers().add(new GroupUser(o, us));
		us = userDao.update(us, null);

		log.error(us.getLastname());
		log.error(us.getAddress().getTown());
	}
}
