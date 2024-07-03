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
package org.apache.openmeetings.mediaserver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kurento.client.MediaPipeline;
import org.kurento.client.Transaction;
import org.mockito.Mock;

import com.github.openjson.JSONObject;

class TestRoomFlowMocked extends BaseMockedTest {
	private static final Long USER_ID = 1L;
	private static final Long ROOM_ID = 5L;
	@Mock
	private UserDao userDao;
	@Mock
	private RoomDao roomDao;
	@Mock
	private RecordingDao recDao;
	@Mock
	private IClientManager cm;
	@Mock
	private IApplication app;

	@BeforeEach
	public void setup() {
		lenient().doReturn(mock(MediaPipeline.class)).when(client).createMediaPipeline(any(Transaction.class));
		User u = new User();
		u.setId(USER_ID);
		u.setFirstname("firstname");
		u.setLastname("lastname");
		lenient().doReturn(u).when(userDao).get(USER_ID);
		doReturn(true).when(handler).isConnected();
		lenient().when(recDao.update(any(Recording.class))).thenAnswer(invocation ->  {
			Object[] args = invocation.getArguments();
			Recording r = (Recording) args[0];
			r.setId(1L);
			return r;
		});
	}

	@Test
	void testNoClient() {
		runWrapped(() -> {
			handler.onMessage(null, getBaseMsg().put("id", "aa"));

			verify(streamProcessor, times(0)).onMessage(any(), any(), any());
		});
	}

	private Client getClient() {
		return new Client("sessionId", 0, userDao.get(USER_ID), "");
	}

	@Test
	void testNoRoom() {
		runWrapped(() -> {
			handler.onMessage(getClient(), getBaseMsg().put("id", "aa"));

			verify(streamProcessor, times(0)).onMessage(any(), any(), any());
		});
	}

	@Test
	void testRecordingAllowed() {
		runWrapped(() -> {
			Client c = getClient();
			assertFalse(streamProcessor.recordingAllowed(c));
			c.setRoom(new Room());
			assertFalse(streamProcessor.recordingAllowed(c));
			c.getRoom().setId(ROOM_ID);
			c.getRoom().setAllowRecording(true);
			assertFalse(streamProcessor.recordingAllowed(c));
			c.allow(Room.Right.MODERATOR);
			doReturn(c.getRoom()).when(roomDao).get(ROOM_ID);
			assertTrue(streamProcessor.recordingAllowed(c));
		});
	}

	private Client getClientWithRoom() {
		Client c = getClient();
		c.setRoom(new Room());
		c.getRoom().setId(ROOM_ID);
		return c;
	}

	@Test
	void testWannaRecord1() throws Exception {
		JSONObject msg = getBaseMsg().put("id", "wannaRecord");
		handler.onMessage(getClientWithRoom(), msg);

		verify(streamProcessor, times(1)).onMessage(any(), any(), any());
	}

	private Client getClientFull() {
		Client c = getClientWithRoom();
		c.getRoom().setAllowRecording(true);
		c.allow(Room.Right.MODERATOR);
		return c;
	}

	@Test
	void testWannaRecord2() throws Exception {
		runWrapped(() -> {
			JSONObject msg = getBaseMsg().put("id", "wannaRecord");
			Client c = getClientFull();
			c.getRoom().setType(Room.Type.INTERVIEW);
			doReturn(c.getRoom()).when(roomDao).get(ROOM_ID);
			handler.onMessage(c, msg);

			verify(streamProcessor, times(1)).onMessage(any(), any(), any());
		});
	}

	@Test
	void testRecordRecord() throws Exception {
		runWrapped(() -> {
			JSONObject msg = getBaseMsg()
					.put("id", "wannaRecord")
					.put("shareType", "shareType")
					.put("fps", "fps")
					;
			Client c = getClientFull();
			doReturn(c.getRoom()).when(roomDao).get(ROOM_ID);
			handler.onMessage(c, msg);
			assertTrue(streamProcessor.isSharing(ROOM_ID));
			handler.onMessage(c, msg);
		});
	}
}
