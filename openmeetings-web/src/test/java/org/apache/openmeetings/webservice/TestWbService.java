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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.BiConsumer;

import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.entity.room.Room;
import org.junit.jupiter.api.Test;

class TestWbService extends AbstractWebServiceTest {
	private static final long CAPACITY = 666L;
	private static final String WB_SERVICE_MOUNT = "wb";

	protected static String getWbUrl() {
		return getServiceUrl(WB_SERVICE_MOUNT);
	}

	private static RoomDTO createTestRoom(final String sid) {
		String extId = randomUUID().toString();
		Room.Type type = Room.Type.PRESENTATION;
		String name = "Unit Test Ext Room1";
		String comment = "Unit Test Ext Room Comments1";
		RoomDTO r = new RoomDTO();
		r.setType(type);
		r.setName(name);
		r.setComment(comment);
		r.setCapacity(CAPACITY);
		r.setExternalType(UNIT_TEST_EXT_TYPE);
		r.setExternalId(extId);

		CallResult<RoomDTO> res = createAndValidate(sid, r);
		return res.getObj();
	}

	private void doTest(BiConsumer<RoomDTO, String> consumer) {
		ServiceResult sr = login();
		final String sid = sr.getMessage();
		RoomDTO room = createTestRoom(sid);

		consumer.accept(room, sid);
	}

	@Test
	void testReset() {
		doTest((room, sid) -> {
			ServiceResult res = getClient(getWbUrl())
					.path("/resetwb/" + room.getId())
					.query("sid", sid)
					.get(ServiceResult.class);
			assertEquals(ServiceResult.Type.SUCCESS.name(), res.getType());
		});
	}

	@Test
	void testCleanAll() {
		doTest((room, sid) -> {
			ServiceResult res = getClient(getWbUrl())
					.path("/cleanwb/" + room.getId() + "/" + 0)
					.query("sid", sid)
					.get(ServiceResult.class);
			assertEquals(ServiceResult.Type.SUCCESS.name(), res.getType());
		});
	}

	@Test
	void testCleanSlide() {
		doTest((room, sid) -> {
			ServiceResult res = getClient(getWbUrl())
					.path("/cleanslide/" + room.getId() + "/" + 0 + "/" + 0)
					.query("sid", sid)
					.get(ServiceResult.class);
			assertEquals(ServiceResult.Type.SUCCESS.name(), res.getType());
		});
	}
}
