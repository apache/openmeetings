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
package org.apache.openmeetings.webservice;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.web.AbstractOmServerTest.getUser;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.record.RecordingDTO;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;

class TestRecordingService extends AbstractWebServiceTest {
	public static final String RECORD_SERVICE_MOUNT = "record";

	private User getExternalUser() throws Exception {
		String uuid = randomUUID().toString();
		User u = getUser(uuid);
		u.addGroup(getBean(GroupDao.class).getExternal(UNIT_TEST_EXT_TYPE));
		u.setExternalId(uuid);
		webCreateUser(u);
		return u;
	}

	@Test
	void testExternal() throws Exception {
		User u = getExternalUser();
		Recording r = new Recording();
		r.setInsertedBy(u.getId());
		r.setComment("Created by Unit Tests");
		r.setRoomId(5L);
		r = getBean(RecordingDao.class).update(r);
		ServiceResult sr = login();
		Collection<? extends RecordingDTO> recs = getClient(getRecordUrl()).path("/" + UNIT_TEST_EXT_TYPE).query("sid", sr.getMessage())
				.getCollection(RecordingDTO.class);
		assertNotNull(recs, "Valid collection should be returned");
		assertFalse(recs.isEmpty(), "Collection of the recordings should not be empty");
		boolean found = false;
		for (RecordingDTO rdo : recs) {
			if (r.getId().equals(rdo.getId())) {
				found = true;
				break;
			}
		}
		assertTrue(found, "Just created recording was not found by the service");
	}

	protected static String getRecordUrl() {
		return getServiceUrl(RECORD_SERVICE_MOUNT);
	}
}
