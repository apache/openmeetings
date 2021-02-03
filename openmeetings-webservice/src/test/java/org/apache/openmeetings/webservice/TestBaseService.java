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
package org.apache.openmeetings.webservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.webservice.error.InternalServiceException;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.junit.jupiter.api.Test;

class TestBaseService {
	@Test
	void testCheck() {
		Sessiondata sd = new BaseWebService() {}.check(null);
		assertNotNull(sd, "NOT null Sessiondata should be returned");
		assertNull(sd.getUserId(), "UserId should be null");
	}

	private static void checkRights(Set<Right> rights) {
		assertNotNull(rights, "NOT null Rights should be returned");
		assertTrue(rights.isEmpty(), "Rights should be empty");
	}

	@Test
	void testGetRights1() {
		checkRights(new BaseWebService() {}.getRights(1L));
	}

	@Test
	void testGetRights2() {
		checkRights(new BaseWebService() {}.getRights(""));
	}

	@Test
	void testPerformCall() {
		assertThrows(ServiceException.class, () -> {
			new BaseWebService() {}.performCall("", sd -> true
				, sd -> { throw new InternalServiceException("test"); });
		});
	}
}
