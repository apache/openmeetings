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
package org.apache.openmeetings.core.remote;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.label.OmLanguage;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.junit.Test;
import org.kurento.client.MediaPipeline;
import org.kurento.client.Transaction;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.github.openjson.JSONObject;

@PrepareForTest(LabelDao.class)
public class TestRoomFlowMocked extends BaseMockedTest {
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

	@Override
	public void setup() {
		super.setup();
		doReturn(mock(MediaPipeline.class)).when(client).createMediaPipeline(any(Transaction.class));
		User u = new User();
		u.setId(USER_ID);
		u.setFirstname("firstname");
		u.setLastname("lastname");
		doReturn(u).when(userDao).get(USER_ID);
		doReturn(true).when(handler).isConnected();
		when(recDao.update(any(Recording.class))).thenAnswer((invocation) ->  {
			Object[] args = invocation.getArguments();
			Recording r = (Recording) args[0];
			r.setId(1L);
			return r;
		});
	}

	@Test
	public void testNoClient() {
		handler.onMessage(null, MSG_BASE.put("id", "aa"));
	}

	private Client getClient() {
		return new Client("sessionId", 0, userDao.get(USER_ID), "");
	}

	@Test
	public void testNoRoom() {
		handler.onMessage(getClient(), MSG_BASE.put("id", "aa"));
	}

	@Test
	public void testRecordingAllowed() {
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
	}

	private Client getClientWithRoom() {
		Client c = getClient();
		c.setRoom(new Room());
		c.getRoom().setId(ROOM_ID);
		return c;
	}

	@Test
	public void testWannaRecord1() throws Exception {
		JSONObject msg = new JSONObject(MSG_BASE.toString()).put("id", "wannaRecord");
		handler.onMessage(getClientWithRoom(), msg);
	}

	private Client getClientFull() {
		Client c = getClientWithRoom();
		c.getRoom().setAllowRecording(true);
		c.allow(Room.Right.MODERATOR);
		return c;
	}

	@Test
	public void testWannaRecord2() throws Exception {
		JSONObject msg = new JSONObject(MSG_BASE.toString()).put("id", "wannaRecord");
		Client c = getClientFull();
		c.getRoom().setType(Room.Type.INTERVIEW);
		doReturn(c.getRoom()).when(roomDao).get(ROOM_ID);
		handler.onMessage(c, msg);
	}

	@Test
	public void testRecordRecord() throws Exception {
		PowerMockito.mockStatic(LabelDao.class);
		BDDMockito.given(LabelDao.getLanguage(any(Long.class))).willReturn(new OmLanguage(Locale.ENGLISH));
		JSONObject msg = new JSONObject(MSG_BASE.toString())
				.put("id", "wannaRecord")
				.put("width", 640)
				.put("height", 480)
				.put("shareType", "shareType")
				.put("fps", "fps")
				;
		Client c = getClientFull();
		doReturn(c.getRoom()).when(roomDao).get(ROOM_ID);
		handler.onMessage(c, msg);
		assertTrue(streamProcessor.isSharing(ROOM_ID));
		handler.onMessage(c, msg);
	}
}
