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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.UUID;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.record.RecordingDTO;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestRecordingService extends AbstractWebServiceTest {
	public static final String RECORD_SERVICE_MOUNT = "record";
	@Autowired
	private RecordingDao recordingDao;

	private User getExternalUser() throws Exception {
		String uuid = UUID.randomUUID().toString();
		User u = getUser(uuid);
		u.setExternalType(UNIT_TEST_EXT_TYPE);
		u.setExternalId(uuid);
		webCreateUser(u);
		return u;
	}

	@Test
	public void testExternal() throws Exception {
		User u = getExternalUser();
		Recording r = new Recording();
		r.setInsertedBy(u.getId());
		r.setComment("Created by Unit Tests");
		r.setRoomId(5L);
		r = recordingDao.update(r);
		ServiceResult sr = login();
		Collection<? extends RecordingDTO> recs = getClient(getRecordUrl()).path("/" + UNIT_TEST_EXT_TYPE).query("sid", sr.getMessage())
				.getCollection(RecordingDTO.class);
		assertNotNull("Valid collection should be returned", recs);
		assertFalse("Collection of the recordings should not be empty", recs.isEmpty());
		boolean found = false;
		for (RecordingDTO rdo : recs) {
			if (r.getId().equals(rdo.getId())) {
				//TODO check room, user
				found = true;
				break;
			}
		}
		assertTrue("Just created recording was not found by the service", found);
	}

	protected static String getRecordUrl() {
		return getServiceUrl(RECORD_SERVICE_MOUNT);
	}
}
