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
package org.apache.openmeetings.calendar;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestDatabaseStructureGetUserStart extends AbstractJUnitDefaults {
	private static final Logger log = LoggerFactory.getLogger(TestDatabaseStructureGetUserStart.class);

	@Test
	void testAddingGroup() {
		try {
			userDao.get(1L);
		} catch (Exception err) {
			log.error("[testAddingGroup]", err);
		}
	}
}
