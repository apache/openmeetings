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
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kurento.client.KurentoClient;
import org.kurento.client.KurentoConnectionListener;
import org.kurento.client.ServerManager;
import org.kurento.client.internal.TransactionImpl;
import org.kurento.client.internal.client.RomManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.openjson.JSONObject;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KurentoClient.class, WebSocketHelper.class, AbstractStream.class})
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

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockStatic(KurentoClient.class);
		mockStatic(WebSocketHelper.class);
		doReturn(kServerManager).when(client).getServerManager();
		when(KurentoClient.create(nullable(String.class), any(KurentoConnectionListener.class))).thenReturn(client);
		doReturn(new TransactionImpl(romManager)).when(client).beginTransaction();
		handler.init();
	}
}
