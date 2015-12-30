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

import static org.apache.openmeetings.util.OmFileHelper.getOmHome;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AbstractWebServiceTest extends AbstractJUnitDefaults {
	private static Tomcat tomcat;
	public final static String CONTEXT = "/openmeetings";
	public final static String BASE_SERVICES_URL = "http://localhost:8080" + CONTEXT + "/services";
	public final static String USER_SERVICE_URL = BASE_SERVICES_URL + "/user";
	protected static final Random rnd = new Random();

	public static WebClient getClient(String url) {
		return WebClient.create(url).accept("application/json").type("application/json");
	}

	public static ServiceResult login() {
		ServiceResult sr = getClient(USER_SERVICE_URL).path("/login").query("user", username).query("pass", userpass)
				.get(ServiceResult.class);
		assertEquals("Login should be successful", sr.getType(), Type.SUCCESS.name());
		return sr;
	}

	@BeforeClass
	public static void initialize() throws Exception {
		tomcat = new Tomcat();
		tomcat.setPort(8080);
		File wd = Files.createTempDirectory("om" + rnd.nextInt()).toFile();
		tomcat.setBaseDir(wd.getCanonicalPath());
		tomcat.getHost().setAppBase(wd.getCanonicalPath());
		tomcat.getHost().setAutoDeploy(true);
		tomcat.getHost().setDeployOnStartup(true);
		tomcat.addWebapp(CONTEXT, getOmHome().getAbsolutePath());
		tomcat.start();
	}

	@AfterClass
	public static void destroy() throws Exception {
		if (tomcat.getServer() != null && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
			if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
				tomcat.stop();
			}
			tomcat.destroy();
		}
	}
}
