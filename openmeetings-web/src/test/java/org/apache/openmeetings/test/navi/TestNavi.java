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
package org.apache.openmeetings.test.navi;

import static org.apache.openmeetings.util.OpenmeetingsVariables.LEVEL_ADMIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.LEVEL_GROUP_ADMIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.LEVEL_USER;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.openmeetings.db.dao.basic.NavigationDao;
import org.apache.openmeetings.db.entity.basic.Naviglobal;
import org.apache.openmeetings.db.entity.basic.Navimain;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestNavi extends AbstractJUnitDefaults {
	@Autowired
	private NavigationDao navimanagement;

	private List<Naviglobal> testGetNavi(int level) {
		List<Naviglobal> ll = navimanagement.getMainMenu(level);

		assertTrue("GlobalNavi size should be greater than zero: " + ll.size(), ll.size() > 0);

		for (Naviglobal navigl : ll) {
			assertNotNull("Naviglobal retrieved should not be null", navigl);
			assertTrue(String.format("Naviglobal retrieved should have level not greater than: %s, has: ", level, navigl.getLevelId()), navigl.getLevelId() <= level);

			assertNotNull("Naviglobal retrieved should have Navimain entries", navigl.getMainnavi());
			for (Navimain navim : navigl.getMainnavi()) {
				assertNotNull("Navimain retrieved should not be null", navim);
				assertTrue(String.format("Navimain retrieved should have level not greater than: %s, has: ", level, navim.getLevelId()), navim.getLevelId() <= level);
			}
		}
		return ll;
	}

	@Test
	public void testGetUserNavi() {
		testGetNavi(LEVEL_USER);
	}

	@Test
	public void testGetGroupAdminNavi() {
		testGetNavi(LEVEL_GROUP_ADMIN);
	}

	@Test
	public void testGetAdminNavi() {
		List<Naviglobal> menu = testGetNavi(LEVEL_ADMIN);
		for (Naviglobal ng : menu) {
			if (ng.getLevelId() == LEVEL_ADMIN) {
				assertTrue("Admin menu should conatin more than 4 items: " + ng.getMainnavi().size(), ng.getMainnavi().size() > 4);
			}
		}
	}
}
