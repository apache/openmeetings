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
import static org.apache.openmeetings.core.remote.KurentoHandler.TAG_MODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.WsClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kurento.client.KurentoClient;
import org.kurento.client.KurentoConnectionListener;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.ServerManager;
import org.kurento.client.Transaction;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.client.internal.TransactionImpl;
import org.kurento.client.internal.client.RomManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.openjson.JSONObject;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KurentoClient.class, WebSocketHelper.class, IKStream.class})
public class TestKurentoHandlerMocked {
	@Mock
	private RomManager romManager;
	@Mock
	private ServerManager kServerManager;
	@Mock
	private KurentoClient client;
	@InjectMocks
	private KurentoHandler handler;
	private final static JSONObject TEST_BASE = new JSONObject().put(TAG_MODE, MODE_TEST);

	@Before
	public void setup() {
		mockStatic(KurentoClient.class);
		mockStatic(WebSocketHelper.class);
		when(client.getServerManager()).thenReturn(kServerManager);
		when(KurentoClient.create(nullable(String.class), any(KurentoConnectionListener.class))).thenReturn(client);
		when(client.beginTransaction()).thenReturn(new TransactionImpl(romManager));
		handler.init();
	}

	@Test
	public void testMsgTestWannaRecord() throws Exception {
		JSONObject msg = new JSONObject(TEST_BASE.toString()).put("id", "wannaRecord");
		WsClient c = new WsClient("sessionId", 0);
		handler.onMessage(c, msg);
	}

	@Test
	public void testMsgTestRecord1() throws Exception {
		when(client.createMediaPipeline(any(Transaction.class))).thenReturn(mock(MediaPipeline.class));
		WebRtcEndpoint.Builder builder = mock(WebRtcEndpoint.Builder.class);
		whenNew(WebRtcEndpoint.Builder.class).withArguments(any(MediaPipeline.class)).thenReturn(builder);
		when(builder.build()).thenReturn(mock(WebRtcEndpoint.class));

		RecorderEndpoint.Builder recBuilder = mock(RecorderEndpoint.Builder.class);
		whenNew(RecorderEndpoint.Builder.class).withArguments(any(MediaPipeline.class), anyString()).thenReturn(recBuilder);
		when(recBuilder.stopOnEndOfStream()).thenReturn(recBuilder);
		when(recBuilder.withMediaProfile(any(MediaProfileSpecType.class))).thenReturn(recBuilder);
		when(recBuilder.build()).thenReturn(mock(RecorderEndpoint.class));

		WsClient c = new WsClient("sessionId", 0);
		for (boolean audio : new boolean[] {true, false}) {
			for (boolean video : new boolean[] {true, false}) {
				JSONObject msg = new JSONObject(TEST_BASE.toString())
						.put("id", "record")
						.put("sdpOffer", "")
						.put("audio", audio)
						.put("video", video);
				handler.onMessage(c, msg);
			}
		}
	}
}
