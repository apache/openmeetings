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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.junit.Test;

import com.sun.star.uno.RuntimeException;

public class TestBaseService {
	private static void checkException(Runnable r) {
		try {
			r.run();
			fail("ServiceException expected");
		} catch (ServiceException e) {
			assertTrue("expected", true);
		}
	}

	@Test
	public void testGetBeanExc() {
		checkException(() -> BaseWebService.getBean(UserDao.class));
	}

	@Test
	public void testCheck() {
		Sessiondata sd = BaseWebService.check(null);
		assertNotNull("NOT null Sessiondata should be returned", sd);
		assertNull("UserId should be null", sd.getUserId());
	}

	private static void checkRights(Set<Right> rights) {
		assertNotNull("NOT null Rights should be returned", rights);
		assertTrue("Rights should be empty", rights.isEmpty());
	}

	@Test
	public void testGetRights1() {
		checkRights(BaseWebService.getRights(1L));
	}

	@Test
	public void testGetRights2() {
		checkRights(BaseWebService.getRights(""));
	}

	@Test
	public void testPerformCall() {
		checkException(() -> BaseWebService.performCall("", sd -> true
				, sd -> { throw new RuntimeException("test"); }));
	}
}
