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

import static org.junit.Assert.assertNotNull;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;

public class UserServiceTest extends AbstractJUnitDefaults {
	public final static String BASE_SERVICES_URL = "http://localhost:5080/openmeetings/services";
	public final static String USER_SERVICE_URL = BASE_SERVICES_URL + "/user";
	
	protected static WebClient getClient(String url) {
		return WebClient.create(url).accept("application/json").type("application/json");
	}
	
	@Test
	public void loginTest() {
		ServiceResult r = getClient(USER_SERVICE_URL).path("/login").query("user", username).query("pass", userpass).get(ServiceResult.class);
		assertNotNull(r);
	}
}
