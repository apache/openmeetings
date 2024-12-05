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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.withSettings;
import static org.mockito.internal.util.collections.Sets.newMockSafeHashSet;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Set;

import jakarta.inject.Inject;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.label.OmLanguage;
import org.apache.openmeetings.db.util.ApplicationHelper;
import org.apache.wicket.injection.Injector;
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
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.configuration.injection.scanner.MockScanner;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

@ExtendWith(MockitoExtension.class)
public class BaseMockedTest {
	private static final Logger log = LoggerFactory.getLogger(BaseMockedTest.class);
	@Mock
	protected RomManager romManager;
	@Mock
	protected ServerManager kServerManager;
	@Spy
	@InjectMocks
	protected StreamProcessorActions streamProcessorActions;
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

	@BeforeEach
	public void baseSetup() {
		Mockito.reset();
		lenient().doReturn(kServerManager).when(client).getServerManager();
		lenient().doReturn(new TransactionImpl(romManager)).when(client).beginTransaction();
		handler.init();
	}

	void mockWs(MockedStatic<WebSocketHelper> wsHelperMock) {
		wsHelperMock.when(() -> WebSocketHelper.sendClient(any(IWsClient.class), any(JSONObject.class))).thenAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				return null;
			}
		});
	}

	void runWrapped(Runnable task) {
		try (MockedStatic<AbstractStream> streamMock = mockStatic(AbstractStream.class);
				MockedStatic<WebSocketHelper> wsHelperMock = mockStatic(WebSocketHelper.class);
				MockedStatic<LabelDao> labelMock = mockStatic(LabelDao.class);
				MockedStatic<ApplicationHelper> appHelpMock = mockStatic(ApplicationHelper.class);
				MockedStatic<Injector> injectMock = mockStatic(Injector.class)
				)
		{
			Set<Object> mocks = newMockSafeHashSet();
			new MockScanner(this, BaseMockedTest.class).addPreparedMocks(mocks);
			new MockScanner(this, this.getClass()).addPreparedMocks(mocks);
			mockWs(wsHelperMock);
			streamMock.when(() -> AbstractStream.createWebRtcEndpoint(any(MediaPipeline.class), anyBoolean(), any())).thenReturn(mock(WebRtcEndpoint.class));
			streamMock.when(() -> AbstractStream.createRecorderEndpoint(any(MediaPipeline.class), anyString(), any(MediaProfileSpecType.class))).thenReturn(mock(RecorderEndpoint.class));
			streamMock.when(() -> AbstractStream.createPlayerEndpoint(any(MediaPipeline.class), anyString())).thenReturn(mock(PlayerEndpoint.class));

			labelMock.when(() -> LabelDao.getLanguage(any(Long.class))).thenReturn(new OmLanguage(1L, Locale.ENGLISH));
			appHelpMock.when(() -> ApplicationHelper.ensureApplication(any(Long.class))).thenReturn(mock(IApplication.class));
			Injector injector = mock(Injector.class, withSettings().strictness(Strictness.LENIENT));
			doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					Object o = invocation.getArgument(0);
					if (forInjection(o)) {
						inject(o, mocks);
					}
					return null;
				}
			}).when(injector).inject(any());
			injectMock.when(Injector::get).thenReturn(injector);
			task.run();
		}
	}

	protected JSONObject getBaseMsg() {
		return new JSONObject();
	}

	private boolean forInjection(Object o) {
		return o instanceof KRoom || o instanceof KStream || o instanceof KTestStream;
	}

	private void inject(Object o, Set<Object> mocks) {
		for (Field f : o.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Inject.class)) {
				mocks.stream()
						.filter(mock -> f.getType().isAssignableFrom(mock.getClass()))
						.findAny()
						.ifPresent(mock -> {
							try {
								f.setAccessible(true);
								f.set(o, mock);
							} catch (Exception e) {
								log.error("Fail to set mock {} {}", f, mock);
							}
						});
			}
		}
	}
}
