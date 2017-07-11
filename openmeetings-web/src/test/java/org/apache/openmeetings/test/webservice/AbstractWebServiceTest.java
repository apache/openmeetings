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
import static org.apache.openmeetings.util.OmFileHelper.getOmHome;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.webservice.util.AppointmentMessageBodyReader;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

public class AbstractWebServiceTest extends AbstractJUnitDefaults {
	private static Tomcat tomcat;
	public final static String CONTEXT = "/openmeetings";
	public final static String BASE_SERVICES_URL = "http://localhost:8080" + CONTEXT + "/services";
	public final static String USER_SERVICE_URL = BASE_SERVICES_URL + "/user";
	public final static String INFO_SERVICE_URL = BASE_SERVICES_URL + "/info";
	public final static String UNIT_TEST_EXT_TYPE = "om_unit_tests";
	public final static long TIMEOUT = 5 * 60 * 1000;
	protected WicketTester tester;

	public static WebClient getClient(String url) {
		WebClient c = WebClient.create(url, Arrays.asList(new AppointmentMessageBodyReader()))
				.accept("application/json").type("application/json");
		HTTPClientPolicy p = WebClient.getConfig(c).getHttpConduit().getClient();
		p.setConnectionTimeout(TIMEOUT);
		p.setReceiveTimeout(TIMEOUT);
		return c;
	}

	public static ServiceResult login() {
		return login(username, userpass);
	}

	public static ServiceResult loginNoCheck(String user, String pass) {
		ServiceResult sr = getClient(USER_SERVICE_URL).path("/login").query("user", user).query("pass", pass)
				.get(ServiceResult.class);
		return sr;
	}

	public static ServiceResult login(String user, String pass) {
		ServiceResult sr = loginNoCheck(user, pass);
		assertEquals("Login should be successful", Type.SUCCESS.name(), sr.getType());
		return sr;
	}

	@BeforeClass
	public static void initialize() throws Exception {
		tomcat = new Tomcat();
		tomcat.setPort(8080);
		File wd = Files.createTempDirectory("om" + UUID.randomUUID().toString()).toFile();
		tomcat.setBaseDir(wd.getCanonicalPath());
		tomcat.getHost().setAppBase(wd.getCanonicalPath());
		tomcat.getHost().setAutoDeploy(true);
		tomcat.getHost().setDeployOnStartup(true);
		tomcat.addWebapp(CONTEXT, getOmHome().getAbsolutePath());
		tomcat.start();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		tester = getWicketTester();
		assertNotNull("Web session should not be null", WebSession.get());
	}

	@After
	public void tearDown() {
		if (tester != null) {
			//can be null in case exception on initialization
			tester.destroy();
		}
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

	public void webCreateUser(User u) {
		ServiceResult r = login();
		UserDTO dto = new UserDTO(u);
		dto.setPassword(createPass());
		UserDTO user = getClient(USER_SERVICE_URL)
				.path("/")
				.query("sid", r.getMessage())
				.query("user", dto.toString())
				.query("confirm", false).post(null, UserDTO.class);
		Assert.assertNotNull(user.getId());
		u.setId(user.getId());
	}
}
