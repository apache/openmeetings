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
package org.openmeetings.test.poll;

import org.junit.Test;
import org.openmeetings.app.data.conference.PollManagement;
import org.openmeetings.app.persistence.beans.poll.PollType;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

public class TestPollManagement extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private PollManagement pollManagement;
	
	@Test
	public void addPollType() {
		long maxId = 1;
		for (PollType pt : pollManagement.getPollTypes()) {
			maxId = Math.max(maxId, pt.getPollTypesId());
		}
		assertNotNull("Poll created is null", pollManagement.addPollType(26L, false));
	}
}
