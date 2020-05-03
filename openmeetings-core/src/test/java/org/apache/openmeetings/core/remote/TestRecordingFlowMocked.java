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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
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
public class TestRecordingFlowMocked extends BaseMockedTest {
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

	//This variable holds a reference to the current client in the room
	private Client c;

	//This variable hold a reference to the UID of the StreamDesc that will be created
	private String streamDescUID;

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

		PowerMockito.mockStatic(LabelDao.class);
		BDDMockito.given(LabelDao.getLanguage(any(Long.class))).willReturn(new OmLanguage(Locale.ENGLISH));

		// init client object for this test
		c = getClientFull();
		doReturn(c.getRoom()).when(roomDao).get(ROOM_ID);

		// Mock out the methods that do webRTC
		doReturn(null).when(streamProcessor).startBroadcast(any(), any(), any());

	}

	private Client getClient() {
		return new Client("sessionId", 0, userDao.get(USER_ID), "");
	}

	private Client getClientWithRoom() {
		Client c = getClient();
		c.setRoom(new Room());
		c.getRoom().setId(ROOM_ID);
		return c;
	}

	private Client getClientFull() {
		Client c = getClientWithRoom();
		c.getRoom().setAllowRecording(true);
		c.allow(Room.Right.MODERATOR);
		return c;
	}

	@Test
	public void testRecordingFlow() throws Exception {

		// start recording and simulate broadcast starting
		testStartRecordWhenSharingWasNot();

		// stop recording
		testStopRecordingWhenNoSharingStarted();
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
		JSONObject msg = new JSONObject(MSG_BASE.toString())
				.put("id", "wannaRecord")
				.put("width", 640)
				.put("height", 480)
				.put("shareType", "shareType")
				.put("fps", "fps")
				;
		handler.onMessage(c, msg);

		// This should enable the sharing, but for enabling recording it should wait for
		// the event that broadcast was started
		assertFalse(streamProcessor.isRecording(ROOM_ID));
		assertTrue(streamProcessor.isSharing(ROOM_ID));

		// Get current Stream, there should be only 1 KStream created as result of this
		assertTrue(c.getStreams().size() == 1);
		StreamDesc streamDesc = c.getStreams().get(0);

		//save UID for stopping the stream later
		streamDescUID = streamDesc.getUid();

		JSONObject msgBroadcastStarted = new JSONObject(MSG_BASE.toString())
				.put("id", "broadcastStarted")
				.put("type", "kurento")
				.put("uid", streamDescUID)
				.put("sdpOffer", "SDP-OFFER")
				;
		handler.onMessage(c, msgBroadcastStarted);

		// Assert that stream AND recording is true
		assertTrue(streamProcessor.isRecording(ROOM_ID));
		assertTrue(streamProcessor.isSharing(ROOM_ID));

		//verify startBroadcast has been invoked
		verify(streamProcessor).startBroadcast(any(), any(), any());

		// Assert that there is still just 1 stream and has only the activities to Record assigned
		assertTrue(c.getStreams().size() == 1);
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

		JSONObject msg = new JSONObject(MSG_BASE.toString())
				.put("id", "stopRecord")
				.put("type", "kurento")
				.put("uid", streamDescUID)
				;
		handler.onMessage(c, msg);

		// Verify it did also stop the sharing stream
		verify(streamProcessor).pauseSharing(any(), any());
		// Verify all streams gone
		assertTrue(c.getStreams().size() == 0);
	}
}
