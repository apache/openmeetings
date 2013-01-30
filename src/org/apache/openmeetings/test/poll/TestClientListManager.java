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
package org.apache.openmeetings.test.poll;

import static org.junit.Assert.assertNotNull;

import java.util.Random;

import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestClientListManager extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private ISessionManager sessionManager;
	
	@Test
	public void addClientListItem() {
		Random rnd = new Random();
		assertNotNull("RoomClientId created is null",
				sessionManager.addClientListItem(rnd.nextLong() + "ABCDE"
						+ rnd.nextLong(), "scopeName", 66666, "remoteAddress",
						"swfUrl", false, null));
	}

}
