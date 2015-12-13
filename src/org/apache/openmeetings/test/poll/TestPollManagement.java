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

import org.apache.openmeetings.data.conference.PollManager;
import org.apache.openmeetings.persistence.beans.poll.PollType;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

public class TestPollManagement extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private PollManager pollManager;
	
	@Test
	public void addPollType() {
		long maxId = 1;
		for (PollType pt : pollManager.getPollTypes()) {
			maxId = Math.max(maxId, pt.getPollTypesId());
		}
		assertNotNull("Poll created is null", pollManager.addPollType(26L, false));
	}
}
