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
import static org.apache.openmeetings.web.AbstractOmServerTest.rnd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;

import jakarta.ws.rs.core.Response;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.junit.jupiter.api.Test;

class TestErrorService extends AbstractWebServiceTest {
	public static final String ERROR_SERVICE_MOUNT = "error";

	@Test
	void getTestBadKey() {
		Locale[] locales = Locale.getAvailableLocales();
		ServiceResult sr = getClient(getErrorUrl())
				.path(String.format("/%s/%s", randomUUID().toString(), LabelDao.getLanguage(locales[rnd.nextInt(locales.length)], 1L)))
				.get(ServiceResult.class);
		assertNotNull(sr, "Valid Result should be returned");
		assertEquals(ServiceResult.Type.SUCCESS.name(), sr.getType(), "SUCCESS result should be returned");
	}

	@Test
	void getTest() {
		Locale[] locales = Locale.getAvailableLocales();
		ServiceResult sr = getClient(getErrorUrl())
				.path(String.format("/%s/%s", "error.unknown", LabelDao.getLanguage(locales[rnd.nextInt(locales.length)], 1L)))
				.get(ServiceResult.class);
		assertNotNull(sr, "Valid Result should be returned");
		assertEquals(ServiceResult.Type.SUCCESS.name(), sr.getType(), "SUCCESS result should be returned");
	}

	@Test
	void reportTest() {
		// null report
		Response resp = getClient(getErrorUrl())
				.path("/report")
				.post("");
		assertNotNull(resp, "Not null Response should be returned");
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus(), "SUCCESS result should be returned");

		// report with message
		resp = getClient(getErrorUrl())
				.path("/report")
				.query("message", "Dummy test")
				.post("");
		assertNotNull(resp, "Not null Response should be returned");
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus(), "SUCCESS result should be returned");

		// report with invalid sid and message
		resp = getClient(getErrorUrl())
				.path("/report")
				.query("message", "Dummy test")
				.query("sid", "n/a")
				.post("");
		assertNotNull(resp, "Not null Response should be returned");
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus(), "SUCCESS result should be returned");

		// report with valid sid and no message
		ServiceResult r = login();
		resp = getClient(getErrorUrl())
				.path("/report")
				.query("sid", r.getMessage())
				.post("");
		assertNotNull(resp, "Not null Response should be returned");
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus(), "SUCCESS result should be returned");

		// report with valid sid and message
		resp = getClient(getErrorUrl())
				.path("/report")
				.query("sid", r.getMessage())
				.query("message", "Dummy test")
				.post("");
		assertNotNull(resp, "Not null Response should be returned");
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), resp.getStatus(), "SUCCESS result should be returned");
	}

	protected static String getErrorUrl() {
		return getServiceUrl(ERROR_SERVICE_MOUNT);
	}
}
