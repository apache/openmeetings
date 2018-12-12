/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * 'License') +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.db.dto.user;

import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_EMAIL;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_FNAME;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_LNAME;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_LOGIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.wicket.util.string.Strings;
import org.junit.Test;

public class TestOAuthUser {
	@Test
	public void firstLevel() {
		OAuthServer server = new OAuthServer()
				.addMapping(PARAM_LOGIN, "id")
				.addMapping(PARAM_EMAIL, "email")
				.addMapping(PARAM_FNAME, "given_name")
				.addMapping(PARAM_LNAME, "family_name");
		OAuthUser user = new OAuthUser(
				"{'id': '11klahjsfwehf5', 'email': 'alsfkvslvmclqwkdsm@gmail.com', 'verified_email': true, 'name': 'John Doe', 'given_name': 'John', 'family_name': 'Doe', 'link': 'https://plus.google.com/+JohnDoe', 'picture': 'https://lh3.googleusercontent.com/somehash/photo.jpg', 'gender': 'male', 'locale': 'en'}"
				, server
				);
		assertEquals("Login should be correct", "11klahjsfwehf5", user.getLogin());
		assertEquals("Email should be correct", "alsfkvslvmclqwkdsm@gmail.com", user.getEmail());
		assertEquals("Firstname should be correct", "John", user.getUserData().get(PARAM_FNAME));
		assertEquals("Lastname should be correct", "Doe", user.getUserData().get(PARAM_LNAME));
	}

	@Test
	public void secondLevel() {
		OAuthServer server = new OAuthServer()
				.addMapping(PARAM_LOGIN, "uid")
				.addMapping(PARAM_EMAIL, "email")
				.addMapping(PARAM_FNAME, "first_name")
				.addMapping(PARAM_LNAME, "last_name");
		OAuthUser user = new OAuthUser(
				"{'response':[{'uid':4uidhere4,'first_name':'John','last_name':'Doe'}]}"
				, server
				);
		assertEquals("Login should be correct", "4uidhere4", user.getLogin());
		assertTrue("Email should be empty", Strings.isEmpty(user.getEmail()));
		assertEquals("Firstname should be correct", "John", user.getUserData().get(PARAM_FNAME));
		assertEquals("Lastname should be correct", "Doe", user.getUserData().get(PARAM_LNAME));

		server.setIconUrl("https://goo.gl/images/q23g7Y");
		user = new OAuthUser(
				"{'response':[{'uid':4uidhere4,'first_name':'John','last_name':'Doe'}]}"
				, server
				);
		assertEquals("Email should be constructed", "4uidhere4@goo.gl", user.getEmail());
	}

	@Test
	public void secondLevel1() {
		OAuthServer server = new OAuthServer()
				.addMapping(PARAM_LOGIN, "username")
				.addMapping(PARAM_EMAIL, "email")
				.addMapping(PARAM_FNAME, "fname")
				.addMapping(PARAM_LNAME, "lname");
		OAuthUser user = new OAuthUser(
				"{\"hasError\": false, \"result\": {\"username\": \"test\", \"email\": \"aaa@test.com\", \"fname\": \"first\", \"lname\":\"last\"}}"
				, server
				);
		assertEquals("Login should be correct", "test", user.getLogin());
		assertEquals("Email should be correct", "aaa@test.com", user.getEmail());
		assertEquals("Firstname should be correct", "first", user.getUserData().get(PARAM_FNAME));
		assertEquals("Lastname should be correct", "last", user.getUserData().get(PARAM_LNAME));
	}

	@Test
	public void map() {
		Map<String, String> umap = new HashMap<>();
		umap.put("login", "abc");
		umap.put("email", "abc@local");
		OAuthUser user = new OAuthUser(umap);
		assertEquals("Login should be correct", "abc", user.getLogin());
		assertEquals("Email should be correct", "abc@local", user.getEmail());
		assertNull("First should be empty", user.getUserData().get(PARAM_FNAME));
		assertNull("Lastname should be empty", user.getUserData().get(PARAM_LNAME));
	}
}
