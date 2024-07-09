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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.StreamDesc;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.openjson.JSONObject;

class TestRecordingFlowMocked extends BaseMockedTest {
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

	//This variable holds a reference to the current client in the room
	private Client c;

	//This variable hold a reference to the UID of the StreamDesc that will be created
	private String streamDescUID;

	@BeforeEach
	public void setup() {
		User u = new User();
		u.setId(USER_ID);
		u.setFirstname("firstname");
		u.setLastname("lastname");
		doReturn(u).when(userDao).get(USER_ID);
		doReturn(true).when(handler).isConnected();
		when(recDao.update(any(Recording.class))).thenAnswer(invocation ->  {
			Object[] args = invocation.getArguments();
			Recording r = (Recording) args[0];
			r.setId(1L);
			return r;
		});

		// init client object for this test
		c = getClientFull();
		doReturn(c.getRoom()).when(roomDao).get(ROOM_ID);

		// Mock out the methods that do webRTC
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgument(3, Runnable.class).run();
				return null;
			}
		}).when(streamProcessor).startBroadcast(any(), any(), any(), any());

	}

	private Client getClient() {
		return new Client("sessionId", 0, userDao.get(USER_ID), "");
	}

	private Client getClientWithRoom() {
		Client client = getClient();
		client.setRoom(new Room());
		client.getRoom().setId(ROOM_ID);
		return client;
	}

	private Client getClientFull() {
		Client client = getClientWithRoom();
		client.getRoom().setAllowRecording(true);
		client.allow(Room.Right.MODERATOR);
		return client;
	}

	@Test
	void testRecordingFlow() {
		runWrapped(() -> {
			try {
				// start recording and simulate broadcast starting
				testStartRecordWhenSharingWasNot();

				// stop recording
				testStopRecordingWhenNoSharingStarted();
			} catch (Exception e) {
				fail("Unexpected exception", e);
			}
		});
	}

	/**
	 * Start the recording.
	 *
	 * This should enable the sharing, but for enabling recording it should wait for
	 * the event that broadcast was started..
	 *
	 * Then simulate that the broadcast has started. And check both sharing and recording is true.
	 *
	 * But the permissions on the stream should still be recording only. Not sharing.
	 *
	 * @throws Exception
	 */
	private void testStartRecordWhenSharingWasNot() throws Exception {
		JSONObject msg = getBaseMsg()
				.put("id", "wannaRecord")
				.put("shareType", "shareType")
				.put("fps", "fps")
				;
		handler.onMessage(c, msg);

		// This should enable the sharing, but for enabling recording it should wait for
		// the event that broadcast was started
		assertFalse(streamProcessor.isRecording(ROOM_ID));
		assertTrue(streamProcessor.isSharing(ROOM_ID));

		// Get current Stream, there should be only 1 KStream created as result of this
		assertEquals(1, c.getStreams().size());
		StreamDesc streamDesc = c.getStreams().get(0);

		//save UID for stopping the stream later
		streamDescUID = streamDesc.getUid();

		JSONObject msgBroadcastStarted = getBaseMsg()
				.put("id", "broadcastStarted")
				.put("type", "kurento")
				.put("uid", streamDescUID)
				.put("sdpOffer", "SDP-OFFER")
				.put("width", 640)
				.put("height", 480)
				;
		handler.onMessage(c, msgBroadcastStarted);

		// Assert that stream AND recording is true
		assertTrue(streamProcessor.isRecording(ROOM_ID));
		assertTrue(streamProcessor.isSharing(ROOM_ID));

		//verify startBroadcast has been invoked
		verify(streamProcessor).startBroadcast(any(), any(), any(), any());

		// Assert that there is still just 1 stream and has only the activities to Record assigned
		assertEquals(1, c.getStreams().size());
		streamDesc = c.getStreams().get(0);
		assertEquals(1, streamDesc.getActivities().size());
		assertEquals(Activity.RECORD, streamDesc.getActivities().get(0));
	}

	/**
	 * This should stop the recording. AND stop the sharing, as the sharing wasn't initially started.
	 *
	 * @throws Exception
	 */
	private void testStopRecordingWhenNoSharingStarted() throws Exception {

		// Mock out the methods that would produce the Recording
		Recording rec = new Recording();
		doReturn(rec).when(recDao).get(Long.valueOf(1L));

		// Mock out the method that would start recording
		doReturn(true).when(streamProcessor).startConvertion(any(Recording.class));

		// Needed for stopping, needs to stop by sid
		doReturn(c).when(streamProcessor).getBySid(c.getSid());

		JSONObject msg = getBaseMsg()
				.put("id", "stopRecord")
				.put("type", "kurento")
				.put("uid", streamDescUID)
				;
		handler.onMessage(c, msg);

		// Verify it did also stop the sharing stream
		verify(streamProcessor).pauseSharing(any(), any());
		// Verify all streams gone
		assertTrue(c.getStreams().isEmpty());
	}
}
