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
package org.apache.openmeetings.test.webservice;

import static org.apache.openmeetings.db.util.ApplicationHelper.getWicketTester;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Test;

public class TestGroupService extends AbstractWebServiceTest {
	protected WicketTester tester;
	public final static String GROUP_SERVICE_URL = BASE_SERVICES_URL + "/group";

	@Override
	public void setUp() throws Exception {
		super.setUp();
		tester = getWicketTester();
		assertNotNull("Web session should not be null", WebSession.get());
	}

	@Test
	public void putTest() {
		ServiceResult r = login();
		Response resp = getClient(GROUP_SERVICE_URL)
				.path("/")
				.query("sid", r.getMessage()).put("");
		assertEquals("Call should NOT be successful", Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), resp.getStatus());
	}

	@Test
	public void addRemoveTest() {
		ServiceResult r = login();
		long groupId = -1;
		{
			Response resp = getClient(GROUP_SERVICE_URL)
					.path("/")
					.query("sid", r.getMessage()).query("name", "Test Group").post("");
			assertNotNull("Valid ServiceResult should be returned", resp);
			assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
			ServiceResult r1 = resp.readEntity(ServiceResult.class);
			assertEquals("OM Call should be successful", r1.getType(), Type.SUCCESS.name());
			groupId = r1.getCode();
		}
		//delete group created
		{
			Response resp = getClient(GROUP_SERVICE_URL)
					.path("/" + groupId)
					.query("sid", r.getMessage()).delete();
			assertNotNull("Valid ServiceResult should be returned", resp);
			assertEquals("Call should be successful", Response.Status.OK.getStatusCode(), resp.getStatus());
			ServiceResult r1 = resp.readEntity(ServiceResult.class);
			assertEquals("OM Call should be successful", r1.getType(), Type.SUCCESS.name());
		}
	}

	@After
	public void tearDown() {
		if (tester != null) {
			//can be null in case exception on initialization
			tester.destroy();
		}
	}
}
