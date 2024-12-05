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
import static org.apache.openmeetings.web.AbstractOmServerTest.createPass;
import static org.apache.openmeetings.web.AbstractOmServerTest.ensureSchema;
import static org.apache.openmeetings.web.AbstractOmServerTest.soapUsername;
import static org.apache.openmeetings.web.AbstractOmServerTest.userpass;
import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.file.FileItemDTO;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.webservice.util.AppointmentMessageBodyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;

import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;

@Tag("webservice")
public abstract class AbstractWebServiceTest {
	private static final String HOST = "localhost";
	private static final String CONTEXT = "/openmeetings";
	private static int port = 8080;
	private static final String USER_SERVICE_MOUNT = "user";
	private static final String ROOM_SERVICE_MOUNT = "room";
	private static final String FILE_SERVICE_MOUNT = "file";
	public static final String UNIT_TEST_EXT_TYPE = "om_unit_tests";
	public static final long TIMEOUT = 5 * 60 * 1000;

	@RegisterExtension
	public static final CreateTomcatExtension tomcatExt = new CreateTomcatExtension(HOST, CONTEXT);

	protected static <T> T getBean(Class<T> clazz) {
		return ensureApplication().getBean(clazz);
	}

	public static WebClient getClient(String url) {
		WebClient c = WebClient.create(url, List.of(new AppointmentMessageBodyReader()))
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
		return getClient(getUserUrl()).path("/login").query("user", user).query("pass", pass)
				.get(ServiceResult.class);
	}

	public static ServiceResult login(String user, String pass) {
		ServiceResult sr = loginNoCheck(user, pass);
		assertEquals(Type.SUCCESS.name(), sr.getType(), "Login should be successful");
		return sr;
	}

	@BeforeAll
	public static void initialize() throws Exception {
		port = tomcatExt.getPort();
	}

	@BeforeEach
	public void setUp() throws Exception {
		ensureSchema(getBean(UserDao.class), getBean(ImportInitvalues.class));
	}

	protected static CallResult<RoomDTO> createAndValidate(RoomDTO r) {
		return createAndValidate(null, r);
	}

	protected static RoomDTO create(String sid, RoomDTO r) {
		return getClient(getRoomUrl())
				.query("sid", sid)
				.type(MediaType.APPLICATION_FORM_URLENCODED)
				.post(new Form().param("room", r.toString()), RoomDTO.class);
	}

	protected static CallResult<RoomDTO> createAndValidate(String sid, RoomDTO r) {
		if (sid == null) {
			ServiceResult sr = login();
			sid = sr.getMessage();
		}
		RoomDTO room = create(sid, r);
		assertNotNull(room, "Valid room should be returned");
		assertNotNull(room.getId(), "Room ID should be not empty");

		RoomDTO room1 = getClient(getRoomUrl()).path("/" + room.getId())
				.query("sid", sid)
				.get(RoomDTO.class);
		assertNotNull(room1, "Valid room should be returned");
		assertEquals(room.getId(), room1.getId(), "Room with same ID should be returned");
		assertEquals(r.getName(), room1.getName(), "Room with same Name should be returned");
		assertEquals(r.getExternalType(), room1.getExternalType(), "Room with same ExternalType should be returned");
		assertEquals(r.getExternalId(), room1.getExternalId(), "Room with same ExternalId should be returned");
		return new CallResult<>(sid, room1);
	}

	public void webCreateUser(User u) {
		ServiceResult r = login();
		UserDTO dto = new UserDTO(u);
		dto.setPassword(createPass());
		UserDTO user = getClient(getUserUrl())
				.path("/")
				.query("sid", r.getMessage())
				.type(MediaType.APPLICATION_FORM_URLENCODED)
				.post(new Form().param("user", dto.toString()).param("confirm", "" + false), UserDTO.class);
		assertNotNull(user.getId());
		u.setId(user.getId());
	}

	public CallResult<FileItemDTO> createVerifiedFile(File fsFile, String name, BaseFileItem.Type type) throws IOException {
		ServiceResult r = login();

		FileItemDTO f1 = null;
		try (InputStream is = new FileInputStream(fsFile)) {
			FileItemDTO file = new FileItemDTO()
					.setName(name)
					.setHash(randomUUID().toString())
					.setType(type);
			List<Attachment> atts = new ArrayList<>();
			atts.add(new Attachment("file", MediaType.APPLICATION_JSON, file));
			atts.add(new Attachment("stream", MediaType.APPLICATION_OCTET_STREAM, is));
			f1 = getClient(getFileUrl())
					.path("/")
					.query("sid", r.getMessage())
					.type(MediaType.MULTIPART_FORM_DATA_TYPE).postCollection(atts, Attachment.class, FileItemDTO.class);
			assertNotNull(f1, "Valid FileItem should be returned");
			assertNotNull(f1.getId(), "Valid FileItem should be returned");
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
