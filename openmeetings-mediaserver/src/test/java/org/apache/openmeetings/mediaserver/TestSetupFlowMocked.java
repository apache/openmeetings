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

import static org.apache.openmeetings.mediaserver.KurentoHandler.MODE_TEST;
import static org.apache.openmeetings.mediaserver.KurentoHandler.PARAM_CANDIDATE;
import static org.apache.openmeetings.mediaserver.KurentoHandler.TAG_MODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.openmeetings.db.entity.basic.WsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kurento.client.MediaPipeline;
import org.kurento.client.Transaction;

import com.github.openjson.JSONObject;

class TestSetupFlowMocked extends BaseMockedTest {
	@Override
	protected JSONObject getBaseMsg() {
		return super.getBaseMsg().put(TAG_MODE, MODE_TEST);
	}

	@BeforeEach
	public void setup() {
		doReturn(true).when(handler).isConnected();
	}

	@Test
	void testMsgTestWannaRecord() throws Exception {
		runWrapped(() -> {
			JSONObject msg = getBaseMsg().put("id", "wannaRecord");
			WsClient c = new WsClient("sessionId", 0);
			handler.onMessage(c, msg);

			verify(testProcessor, times(1)).onMessage(any(), any(), any());
		});
	}

	@Test
	void testMsgTestRecord1() throws Exception {
		runWrapped(() -> {
			doReturn(mock(MediaPipeline.class)).when(client).createMediaPipeline(any(Transaction.class));

			WsClient c = new WsClient("sessionId", 0);
			for (boolean audio : new boolean[] {true, false}) {
				for (boolean video : new boolean[] {true, false}) {
					JSONObject msg = getBaseMsg()
							.put("id", "record")
							.put("sdpOffer", "")
							.put("audio", audio)
							.put("video", video);
					handler.onMessage(c, msg);
				}
			}
			JSONObject iceMsg = getBaseMsg()
					.put("id", "iceCandidate")
					.put(PARAM_CANDIDATE, new JSONObject()
							.put(PARAM_CANDIDATE, "candidate")
							.put("sdpMid", "sdpMid")
							.put("sdpMLineIndex", 1));
			handler.onMessage(c, iceMsg);
			handler.onMessage(c, getBaseMsg()
					.put("id", "play")
					.put("sdpOffer", "sdpOffer"));
			testProcessor.destroy();

			verify(testProcessor, times(6)).onMessage(any(), any(), any());
		});
	}

	@Test
	void testMsgTestIceCandidate() throws Exception {
		runWrapped(() -> {
			JSONObject msg = getBaseMsg()
					.put("id", "iceCandidate")
					.put(KurentoHandler.PARAM_CANDIDATE, new JSONObject());
			WsClient c = new WsClient("sessionId", 0);
			handler.onMessage(c, msg);

			verify(testProcessor, times(1)).onMessage(any(), any(), any());
		});
	}

	@Test
	void testMsgTestWannaPlay() throws Exception {
		runWrapped(() -> {
			JSONObject msg = getBaseMsg().put("id", "wannaPlay");
			WsClient c = new WsClient("sessionId", 0);
			handler.onMessage(c, msg);

			verify(testProcessor, times(1)).onMessage(any(), any(), any());
		});
	}

	@Test
	void testMsgTestPlay() throws Exception {
		runWrapped(() -> {
			JSONObject msg = getBaseMsg().put("id", "play");
			WsClient c = new WsClient("sessionId", 0);
			handler.onMessage(c, msg);

			verify(testProcessor, times(1)).onMessage(any(), any(), any());
		});
	}
}
