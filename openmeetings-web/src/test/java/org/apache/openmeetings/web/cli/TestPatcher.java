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
package org.apache.openmeetings.web.cli;

import static org.apache.openmeetings.web.AbstractOmServerTest.setOmHome;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.TimeZone;

import org.apache.openjpa.lib.util.Files;
import org.apache.openmeetings.cli.ConnectionPropertiesPatcher;
import org.apache.openmeetings.web.IsolatedTest;
import org.apache.openmeetings.util.ConnectionProperties;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.ConnectionProperties.DbType;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.util.string.StringValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@IsolatedTest
class TestPatcher {
	private static final String HOST = "myhost";
	private static final String PORT = "6666";
	private static final String DB = "mydb";
	private static final String USER = "myuser";
	private static final String PASS = "mypass";

	@BeforeAll
	public static void setUp() {
		setOmHome();
	}

	@Test
	void test() throws Exception {
		for (DbType dbType : DbType.values()) {
			ConnectionProperties props = ConnectionPropertiesPatcher.patch(dbType, HOST, PORT, DB, USER, PASS);
			assertEquals(dbType, props.getDbType(), "DB type should match");
			if (DbType.MYSQL == dbType) {
				Url url = Url.parse(props.getURL());
				PageParameters pp = new PageParametersEncoder().decodePageParameters(url);
				StringValue tz = pp.get("serverTimezone");
				assertEquals(TimeZone.getDefault().getID(), tz.toString(), "serverTimezone parameter is mandatory for MySql");
			}
		}
	}

	@AfterAll
	public static void cleanup() throws IOException {
		Files.copy(OmFileHelper.getPersistence(DbType.H2), OmFileHelper.getPersistence());
	}
}
