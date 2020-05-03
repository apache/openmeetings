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

import static org.apache.openmeetings.core.remote.KurentoHandler.MODE_TEST;
import static org.apache.openmeetings.core.remote.KurentoHandler.PARAM_CANDIDATE;
import static org.apache.openmeetings.core.remote.KurentoHandler.TAG_MODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.apache.openmeetings.db.entity.basic.WsClient;
import org.junit.Test;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.Transaction;
import org.kurento.client.WebRtcEndpoint;

import com.github.openjson.JSONObject;

public class TestSetupFlowMocked extends BaseMockedTest {
	@Override
	public void setup() {
		super.setup();
		doReturn(true).when(handler).isConnected();
		MSG_BASE.put(TAG_MODE, MODE_TEST);
	}

	@Test
	public void testMsgTestWannaRecord() throws Exception {
		JSONObject msg = new JSONObject(MSG_BASE.toString()).put("id", "wannaRecord");
		WsClient c = new WsClient("sessionId", 0);
		handler.onMessage(c, msg);
	}

	@Test
	public void testMsgTestRecord1() throws Exception {
		doReturn(mock(MediaPipeline.class)).when(client).createMediaPipeline(any(Transaction.class));
		WebRtcEndpoint.Builder builder = mock(WebRtcEndpoint.Builder.class);
		whenNew(WebRtcEndpoint.Builder.class).withArguments(any(MediaPipeline.class)).thenReturn(builder);
		doReturn(mock(WebRtcEndpoint.class)).when(builder).build();

		RecorderEndpoint.Builder recBuilder = mock(RecorderEndpoint.Builder.class);
		whenNew(RecorderEndpoint.Builder.class).withArguments(any(MediaPipeline.class), anyString()).thenReturn(recBuilder);
		doReturn(recBuilder).when(recBuilder).stopOnEndOfStream();
		doReturn(recBuilder).when(recBuilder).withMediaProfile(any(MediaProfileSpecType.class));
		doReturn(mock(RecorderEndpoint.class)).when(recBuilder).build();

		WsClient c = new WsClient("sessionId", 0);
		for (boolean audio : new boolean[] {true, false}) {
			for (boolean video : new boolean[] {true, false}) {
				JSONObject msg = new JSONObject(MSG_BASE.toString())
						.put("id", "record")
						.put("sdpOffer", "")
						.put("audio", audio)
						.put("video", video);
				handler.onMessage(c, msg);
			}
		}
		JSONObject iceMsg = new JSONObject(MSG_BASE.toString())
				.put("id", "iceCandidate")
				.put(PARAM_CANDIDATE, new JSONObject()
						.put(PARAM_CANDIDATE, "candidate")
						.put("sdpMid", "sdpMid")
						.put("sdpMLineIndex", 1));
		handler.onMessage(c, iceMsg);
		PlayerEndpoint.Builder playBuilder = mock(PlayerEndpoint.Builder.class);
		whenNew(PlayerEndpoint.Builder.class).withArguments(any(MediaPipeline.class), anyString()).thenReturn(playBuilder);
		doReturn(mock(PlayerEndpoint.class)).when(playBuilder).build();
		handler.onMessage(c, new JSONObject(MSG_BASE.toString())
				.put("id", "play")
				.put("sdpOffer", "sdpOffer"));
		testProcessor.destroy();
	}

	@Test
	public void testMsgTestIceCandidate() throws Exception {
		JSONObject msg = new JSONObject(MSG_BASE.toString())
				.put("id", "iceCandidate")
				.put(KurentoHandler.PARAM_CANDIDATE, new JSONObject());
		WsClient c = new WsClient("sessionId", 0);
		handler.onMessage(c, msg);
	}

	@Test
	public void testMsgTestWannaPlay() throws Exception {
		JSONObject msg = new JSONObject(MSG_BASE.toString()).put("id", "wannaPlay");
		WsClient c = new WsClient("sessionId", 0);
		handler.onMessage(c, msg);
	}

	@Test
	public void testMsgTestPlay() throws Exception {
		JSONObject msg = new JSONObject(MSG_BASE.toString()).put("id", "play");
		WsClient c = new WsClient("sessionId", 0);
		handler.onMessage(c, msg);
	}
}
