/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 */
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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Type;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class KurentoHandlerTest {
	
	@Mock
	private StreamProcessor streamProcessor;
	
	@InjectMocks
	@Spy
	private KurentoHandler kurentoHandler;
	
	@Before
	public void setup() {
		Room room1 = new Room();
		room1.setId(1L);
		room1.setType(Type.CONFERENCE);
		KRoom kroom1 = new KRoom(room1, null, null);
		User user1 = new User();
		user1.setLogin("login1");
		Client cl1 = new Client("sessionId1", 1, user1, "pictureUri1");
		cl1.setRoom(room1);
		StreamDesc sd1 = cl1.addStream(StreamType.WEBCAM, Activity.AUDIO_VIDEO);
		kroom1.join(sd1);
		
		Collection<KRoom> rooms = new ArrayList<>();
		rooms.add(kroom1);
		
		// slightly different syntax for mocking Spy
		Mockito.doReturn(rooms).when(kurentoHandler).getRooms();
		
	}
	
	@Test
	public void getStreamsWithOneKStreamInKRoom() {
		Collection<KStream> resultingStreams = kurentoHandler.getAllStreams();
		assertEquals(resultingStreams.size(), 1);
	}
	

}
