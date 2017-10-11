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
package org.apache.openmeetings.service.quartz;

import static org.apache.openmeetings.util.OpenmeetingsVariables.setInitComplete;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.service.quartz.scheduler.CleanupJob;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJob extends AbstractWicketTester {
	@Autowired
	private CleanupJob cleanJob;

	@Test
	public void testNotInited() {
		try {
			setInitComplete(false);
			cleanJob.cleanExpiredRecordings();
			cleanJob.cleanExpiredResetHash();
			cleanJob.cleanRoomFiles();
			cleanJob.cleanSessions();
			cleanJob.cleanTestSetup();
		} finally {
			setInitComplete(true);
		}
	}
}
