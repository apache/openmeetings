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
package org.apache.openmeetings.core.sip;

import static org.apache.openmeetings.test.Utils.getTestCoordinates;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.entity.room.Room;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.ManagerConnectionState;
import org.asteriskjava.manager.action.ManagerAction;
import org.asteriskjava.manager.internal.ManagerWriterImpl;
import org.asteriskjava.util.SocketConnectionFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSipMocked {
	private static final Logger log = LoggerFactory.getLogger(TestSipMocked.class);
	@Mock
	private ManagerConnection con;
	@Mock
	private ManagerConnectionFactory factory;
	@Mock
	private SocketConnectionFacade sock;
	@InjectMocks
	private ManagerWriterImpl writerImpl;

	@BeforeEach
	void setup(TestInfo testInfo) {
		log.info("Test started: {} ---", getTestCoordinates(testInfo));
		MockitoAnnotations.openMocks(this);
		Mockito.reset();
		doReturn(ManagerConnectionState.CONNECTED).when(con).getState();
	}

	@AfterEach
	void tearDown(TestInfo testInfo) {
		log.info(" --- test finished: {}", getTestCoordinates(testInfo));
	}

	@Test
	void testCallExternalNumber() throws Exception {
		Room r = new Room();
		r.setConfno("test-conf-no");

		SipManager sip = spy(new SipManager());

		ArgumentCaptor<ManagerAction> actionCaptor = ArgumentCaptor.forClass(ManagerAction.class);
		when(sip.exec(actionCaptor.capture())).thenReturn(null); // we need argument

		sip.callExternalNumber("12345\r\nAction: Command\r\nActionID: injected\r\nCommand: sip show peers", r);
		List<String> actionStrings = new ArrayList<>();
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				actionStrings.add(invocation.getArgument(0));
				return null;
			}}).when(sock).write(anyString());
		writerImpl.sendAction(actionCaptor.getValue(), "some-id");
		assertEquals(1, actionStrings.size(), "Exctly one message should be written");
		assertFalse(actionStrings.get(0).contains("injected"), "External number should be sanitized");
	}
}
