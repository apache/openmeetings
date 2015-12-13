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
package org.apache.openmeetings.test.user;

import static org.junit.Assert.assertTrue;

import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.persistence.beans.user.State;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestStateToLower extends AbstractOpenmeetingsSpringTest {
	
	@Autowired
	private StateDao stateDao;
	
	@Test
	public void doTestQuery() {
		
		State state_alabania = stateDao.getStateByName("Albania");
		
		State state_alabania_lower = stateDao.getStateByName("albaNia");
		
		assertTrue(state_alabania != null);
		assertTrue(state_alabania_lower != null);
		assertTrue(state_alabania.getState_id().equals(3L));
		assertTrue(state_alabania_lower.getState_id().equals(3L));
		assertTrue(state_alabania.getState_id().equals(state_alabania_lower.getState_id()));
		
	}

}
