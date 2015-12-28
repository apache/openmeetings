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
package org.apache.openmeetings.test.webservice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Random;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.record.RecordingDTO;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RecordingServiceTest extends AbstractJUnitDefaults {
	public final static String RECORD_SERVICE_URL = UserServiceTest.BASE_SERVICES_URL + "/record";
	private final static String UNIT_TEST_GROUP = "om_unit_tests";
	private final Random rnd = new Random();
	@Autowired
	private RecordingDao recordingDao;
	@Autowired
	private UserDao userDao;

	@Test
	public void testExternal() throws Exception {
		User u = getUser(rnd.nextInt());
		u.setExternalType(UNIT_TEST_GROUP);
		u.setExternalId("" + rnd.nextInt());
		userDao.update(u, null);
		Recording r = new Recording();
		r.setCreator(u);
		r.setComment("Created by Unit Tests");
		r = recordingDao.update(r);
		ServiceResult sr = UserServiceTest.login();
		Collection<? extends RecordingDTO> recs = UserServiceTest.getClient(RECORD_SERVICE_URL).path("/" + UNIT_TEST_GROUP).query("sid", sr.getMessage())
				.getCollection(RecordingDTO.class);
		assertNotNull("Valid collection should be returned", recs);
		assertFalse("Collection of the recordings shoould not be empty", recs.isEmpty());
		boolean found = false;
		for (RecordingDTO rdo : recs) {
			if (r.getId().equals(rdo.getId())) {
				found = true;
				break;
			}
		}
		assertTrue("Just created recording was not found by the service", found);
	}
}
