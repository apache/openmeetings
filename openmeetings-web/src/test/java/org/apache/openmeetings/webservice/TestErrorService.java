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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.AbstractJUnitDefaults.rnd;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import javax.ws.rs.core.Response;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.junit.Test;


public class TestErrorService extends AbstractWebServiceTest {
	public static final String ERROR_SERVICE_MOUNT = "error";

	@Test
	public void getTestBadKey() {
		Locale[] locales = Locale.getAvailableLocales();
		ServiceResult sr = getClient(getErrorUrl())
				.path(String.format("/%s/%s", randomUUID().toString(), LabelDao.getLanguage(locales[rnd.nextInt(locales.length)], 1L)))
				.get(ServiceResult.class);
		assertNotNull("Valid Result should be returned", sr);
		assertEquals("SUCCESS result should be returned", ServiceResult.Type.SUCCESS.name(), sr.getType());
	}

	@Test
	public void getTest() {
		Locale[] locales = Locale.getAvailableLocales();
		ServiceResult sr = getClient(getErrorUrl())
				.path(String.format("/%s/%s", "error.unknown", LabelDao.getLanguage(locales[rnd.nextInt(locales.length)], 1L)))
				.get(ServiceResult.class);
		assertNotNull("Valid Result should be returned", sr);
		assertEquals("SUCCESS result should be returned", ServiceResult.Type.SUCCESS.name(), sr.getType());
	}

	@Test
	public void reportTest() {
		// null report
		Response resp = getClient(getErrorUrl())
				.path("/report")
				.post("");
		assertNotNull("Not null Response should be returned", resp);
		assertEquals("SUCCESS result should be returned", Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());

		// report with message
		resp = getClient(getErrorUrl())
				.path("/report")
				.query("message", "Dummy test")
				.post("");
		assertNotNull("Not null Response should be returned", resp);
		assertEquals("SUCCESS result should be returned", Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());

		// report with invalid sid and message
		resp = getClient(getErrorUrl())
				.path("/report")
				.query("message", "Dummy test")
				.query("sid", "n/a")
				.post("");
		assertNotNull("Not null Response should be returned", resp);
		assertEquals("SUCCESS result should be returned", Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());

		// report with valid sid and no message
		ServiceResult r = login();
		resp = getClient(getErrorUrl())
				.path("/report")
				.query("sid", r.getMessage())
				.post("");
		assertNotNull("Not null Response should be returned", resp);
		assertEquals("SUCCESS result should be returned", Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());

		// report with valid sid and message
		resp = getClient(getErrorUrl())
				.path("/report")
				.query("sid", r.getMessage())
				.query("message", "Dummy test")
				.post("");
		assertNotNull("Not null Response should be returned", resp);
		assertEquals("SUCCESS result should be returned", Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus());
	}

	protected static String getErrorUrl() {
		return getServiceUrl(ERROR_SERVICE_MOUNT);
	}
}
