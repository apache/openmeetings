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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.Locale;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.label.OmLanguage;
import org.apache.openmeetings.db.util.ApplicationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.ServerManager;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.client.internal.TransactionImpl;
import org.kurento.client.internal.client.RomManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.github.openjson.JSONObject;

@ExtendWith(MockitoExtension.class)
public class BaseMockedTest {
	@Mock
	protected RomManager romManager;
	@Mock
	protected ServerManager kServerManager;
	@Mock
	protected KurentoClient client;
	@Spy
	@InjectMocks
	protected StreamProcessor streamProcessor;
	@Spy
	@InjectMocks
	protected TestStreamProcessor testProcessor;
	@Spy
	@InjectMocks
	protected KurentoHandler handler;

	protected final static JSONObject MSG_BASE = new JSONObject();

	@BeforeEach
	public void setup() {
		lenient().doReturn(kServerManager).when(client).getServerManager();
		lenient().doReturn(new TransactionImpl(romManager)).when(client).beginTransaction();
		handler.init();
	}

	void runWrapped(Runnable task) {
		try (MockedStatic<AbstractStream> streamMock = mockStatic(AbstractStream.class);
				MockedStatic<WebSocketHelper> wsHelperMock = mockStatic(WebSocketHelper.class);
				MockedStatic<LabelDao> labelMock = mockStatic(LabelDao.class);
				MockedStatic<ApplicationHelper> appHelpMock = mockStatic(ApplicationHelper.class);
				)
		{
			wsHelperMock.when(() -> WebSocketHelper.sendClient(any(IWsClient.class), any(JSONObject.class))).thenAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					return null;
				}
			});
			streamMock.when(() -> AbstractStream.createWebRtcEndpoint(any(MediaPipeline.class), anyBoolean())).thenReturn(mock(WebRtcEndpoint.class));
			streamMock.when(() -> AbstractStream.createRecorderEndpoint(any(MediaPipeline.class), anyString(), any(MediaProfileSpecType.class))).thenReturn(mock(RecorderEndpoint.class));
			streamMock.when(() -> AbstractStream.createPlayerEndpoint(any(MediaPipeline.class), anyString())).thenReturn(mock(PlayerEndpoint.class));

			labelMock.when(() -> LabelDao.getLanguage(any(Long.class))).thenReturn(new OmLanguage(Locale.ENGLISH));
			appHelpMock.when(() -> ApplicationHelper.ensureApplication(any(Long.class))).thenReturn(mock(IApplication.class));
			task.run();
		}
	}
}
