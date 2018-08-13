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

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static org.apache.openmeetings.AbstractWicketTester.getWicketTester;
import static org.apache.openmeetings.util.OmFileHelper.getOmHome;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.webservice.util.AppointmentMessageBodyReader;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

public class AbstractWebServiceTest extends AbstractJUnitDefaults {
	private static Tomcat tomcat;
	private static final String HOST = "localhost";
	private static final String CONTEXT = "/openmeetings";
	private static int port = 8080;
	private static final String USER_SERVICE_MOUNT = "user";
	private static final String ROOM_SERVICE_MOUNT = "room";
	private static final String FILE_SERVICE_MOUNT = "file";
	public static final String UNIT_TEST_EXT_TYPE = "om_unit_tests";
	public static final long TIMEOUT = 5 * 60 * 1000;
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
		return login(soapUsername, userpass);
	}

	public static ServiceResult loginNoCheck(String user, String pass) {
		ServiceResult sr = getClient(getUserUrl()).path("/login").query("user", user).query("pass", pass)
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
		Connector connector = new Connector("HTTP/1.1");
		connector.setAttribute("address", InetAddress.getByName(HOST).getHostAddress());
		connector.setPort(0);
		tomcat.getService().addConnector(connector);
		tomcat.setConnector(connector);
		File wd = Files.createTempDirectory("om" + UUID.randomUUID().toString()).toFile();
		tomcat.setBaseDir(wd.getCanonicalPath());
		tomcat.getHost().setAppBase(wd.getCanonicalPath());
		tomcat.getHost().setAutoDeploy(false);
		tomcat.getHost().setDeployOnStartup(false);
		tomcat.addWebapp(CONTEXT, getOmHome().getAbsolutePath());
		tomcat.getConnector(); // to init the connector
		tomcat.start();
		port = tomcat.getConnector().getLocalPort();
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

	protected static CallResult<RoomDTO> createAndValidate(RoomDTO r) {
		return createAndValidate(null, r);
	}

	protected static CallResult<RoomDTO> createAndValidate(String sid, RoomDTO r) {
		if (sid == null) {
			ServiceResult sr = login();
			sid = sr.getMessage();
		}
		RoomDTO room = getClient(getRoomUrl())
				.query("sid", sid)
				.type(APPLICATION_FORM_URLENCODED)
				.post(new Form().param("room", r.toString()), RoomDTO.class);
		assertNotNull("Valid room should be returned", room);
		assertNotNull("Room ID should be not empty", room.getId());

		RoomDTO room1 = getClient(getRoomUrl()).path(String.format("/%s", room.getId()))
				.query("sid", sid)
				.get(RoomDTO.class);
		assertNotNull("Valid room should be returned", room1);
		assertEquals("Room with same ID should be returned", room.getId(), room1.getId());
		assertEquals("Room with same Name should be returned", r.getName(), room1.getName());
		assertEquals("Room with same ExternalType should be returned", r.getExternalType(), room1.getExternalType());
		assertEquals("Room with same ExternalId should be returned", r.getExternalId(), room1.getExternalId());
		//TODO check other fields
		return new CallResult<>(sid, room1);
	}

	public void webCreateUser(User u) {
		ServiceResult r = login();
		UserDTO dto = new UserDTO(u);
		dto.setPassword(createPass());
		UserDTO user = getClient(getUserUrl())
				.path("/")
				.query("sid", r.getMessage())
				.type(APPLICATION_FORM_URLENCODED)
				.post(new Form().param("user", dto.toString()).param("confirm", "" + false), UserDTO.class);
		Assert.assertNotNull(user.getId());
		u.setId(user.getId());
	}

	public CallResult<FileItemDTO> createVerifiedFile(File fsFile, String name, BaseFileItem.Type type) throws IOException {
		ServiceResult r = login();

		FileItemDTO f1 = null;
		try (InputStream is = new FileInputStream(fsFile)) {
			FileItemDTO file = new FileItemDTO()
					.setName(name)
					.setHash(UUID.randomUUID().toString())
					.setType(type);
			List<Attachment> atts = new ArrayList<>();
			atts.add(new Attachment("file", MediaType.APPLICATION_JSON, file));
			atts.add(new Attachment("stream", MediaType.APPLICATION_OCTET_STREAM, is));
			f1 = getClient(getFileUrl())
					.path("/")
					.query("sid", r.getMessage())
					.type(MediaType.MULTIPART_FORM_DATA_TYPE).postCollection(atts, Attachment.class, FileItemDTO.class);
			assertNotNull("Valid FileItem should be returned", f1);
			assertNotNull("Valid FileItem should be returned", f1.getId());
		}
		return new CallResult<>(r.getMessage(), f1);
	}

	protected static String getServiceUrl(String mount) {
		return String.format("http://%s:%s%s/services/%s", HOST, port, CONTEXT, mount);
	}

	protected static String getUserUrl() {
		return getServiceUrl(USER_SERVICE_MOUNT);
	}

	protected static String getRoomUrl() {
		return getServiceUrl(ROOM_SERVICE_MOUNT);
	}

	protected static String getFileUrl() {
		return getServiceUrl(FILE_SERVICE_MOUNT);
	}

	public static class CallResult<T> {
		private final String sid;
		private final T obj;

		public CallResult(String sid, T obj) {
			this.sid = sid;
			this.obj = obj;
		}

		public String getSid() {
			return sid;
		}

		public T getObj() {
			return obj;
		}
	}
}
