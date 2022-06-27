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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.util.collections.Sets.newMockSafeHashSet;

import java.util.Set;
import java.util.function.Consumer;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.internal.configuration.injection.scanner.MockScanner;

import com.github.openjson.JSONObject;

class TestNotConnectedMocked extends BaseMockedTest {
	private void wrapWs(Consumer<MockedStatic<WebSocketHelper>> task) {
		try (MockedStatic<WebSocketHelper> wsHelperMock = mockStatic(WebSocketHelper.class)) {
			Set<Object> mocks = newMockSafeHashSet();
			new MockScanner(this, TestNotConnectedMocked.class).addPreparedMocks(mocks);
			new MockScanner(this, this.getClass()).addPreparedMocks(mocks);
			mockWs(wsHelperMock);
			task.accept(wsHelperMock);
		}
	}

	@Test
	void testNotConnected() {
		wrapWs(wsHelperMock -> {
			handler.onMessage(null, getBaseMsg());
			wsHelperMock.verify(
					() -> WebSocketHelper.sendClient(isNull(), any(JSONObject.class))
					, times(1));
		});
	}

	@Test
	void testRecordingAllowed() {
		assertFalse(streamProcessor.recordingAllowed(null));
	}

	@Test
	void testStartRecording() {
		streamProcessor.startRecording(null);
		verify(handler, times(0)).getRoom(any());
	}

	@Test
	void testStopRecording() {
		streamProcessor.stopRecording(null);
		verify(handler, times(0)).getRoom(any());
	}

	@Test
	void testIsRecording() {
		assertFalse(streamProcessor.isRecording(null));
	}

	@Test
	void testGetRecordingUser() {
		assertEquals(new JSONObject().toString(), handler.getRecordingUser(null).toString());
	}
}
