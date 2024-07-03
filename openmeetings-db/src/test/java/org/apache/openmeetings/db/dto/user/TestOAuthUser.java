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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.wicket.util.string.Strings;
import org.junit.jupiter.api.Test;

class TestOAuthUser {
	@Test
	void firstLevel() {
		OAuthServer server = new OAuthServer()
				.addMapping(PARAM_LOGIN, "id")
				.addMapping(PARAM_EMAIL, "email")
				.addMapping(PARAM_FNAME, "given_name")
				.addMapping(PARAM_LNAME, "family_name");
		OAuthUser user = new OAuthUser(
				"{'id': '11klahjsfwehf5', 'email': 'alsfkvslvmclqwkdsm@gmail.com', 'verified_email': true, 'name': 'John Doe', 'given_name': 'John', 'family_name': 'Doe', 'link': 'https://plus.google.com/+JohnDoe', 'picture': 'https://lh3.googleusercontent.com/somehash/photo.jpg', 'gender': 'male', 'locale': 'en'}"
				, server
				);
		assertEquals("11klahjsfwehf5", user.getLogin(), "Login should be correct");
		assertEquals("alsfkvslvmclqwkdsm@gmail.com", user.getEmail(), "Email should be correct");
		assertEquals("John", user.getUserData().get(PARAM_FNAME), "Firstname should be correct");
		assertEquals("Doe", user.getUserData().get(PARAM_LNAME), "Lastname should be correct");
	}

	@Test
	void secondLevel() {
		OAuthServer server = new OAuthServer()
				.addMapping(PARAM_LOGIN, "uid")
				.addMapping(PARAM_EMAIL, "email")
				.addMapping(PARAM_FNAME, "first_name")
				.addMapping(PARAM_LNAME, "last_name");
		OAuthUser user = new OAuthUser(
				"{'response':[{'uid':4uidhere4,'first_name':'John','last_name':'Doe'}]}"
				, server
				);
		assertEquals("4uidhere4", user.getLogin(), "Login should be correct");
		assertTrue(Strings.isEmpty(user.getEmail()), "Email should be empty");
		assertEquals("John", user.getUserData().get(PARAM_FNAME), "Firstname should be correct");
		assertEquals("Doe", user.getUserData().get(PARAM_LNAME), "Lastname should be correct");

		server.setIconUrl("https://goo.gl/images/q23g7Y");
		user = new OAuthUser(
				"{'response':[{'uid':4uidhere4,'first_name':'John','last_name':'Doe'}]}"
				, server
				);
		assertEquals("4uidhere4@goo.gl", user.getEmail(), "Email should be constructed");
	}

	@Test
	void secondLevel1() {
		OAuthServer server = new OAuthServer()
				.addMapping(PARAM_LOGIN, "username")
				.addMapping(PARAM_EMAIL, "email")
				.addMapping(PARAM_FNAME, "fname")
				.addMapping(PARAM_LNAME, "lname");
		OAuthUser user = new OAuthUser(
				"{\"hasError\": false, \"result\": {\"username\": \"test\", \"email\": \"aaa@test.com\", \"fname\": \"first\", \"lname\":\"last\"}}"
				, server
				);
		assertEquals("test", user.getLogin(), "Login should be correct");
		assertEquals("aaa@test.com", user.getEmail(), "Email should be correct");
		assertEquals("first", user.getUserData().get(PARAM_FNAME), "Firstname should be correct");
		assertEquals("last", user.getUserData().get(PARAM_LNAME), "Lastname should be correct");
	}

	@Test
	void thirdLevel() {
		OAuthServer server = new OAuthServer()
				.addMapping(PARAM_LOGIN, "id")
				.addMapping(PARAM_EMAIL, "email")
				.addMapping(PARAM_FNAME, "display-name");
		OAuthUser user = new OAuthUser("""
			{
				"ocs": {
					"meta": {
						"status": "ok",
						"statuscode": 200,
						"message": "OK"
					},
					"data": {
						"storageLocation": "xxxxx",
						"id": "xxxxx",
						"lastLogin": 1584799957000,
						"backend": "Database",
						"subadmin": [],
						"quota": {
							"free": 183035547648,
							"used": 10244,
							"total": 183035557892,
							"relative": 0,
							"quota": -3
						},
						"email": "xxxxxx@xxx.x",
						"phone": "",
						"address": "",
						"website": "",
						"twitter": "",
						"groups": [
							"xxxxxx"
						],
						"language": "en",
						"locale": "",
						"backendCapabilities": {
							"setDisplayName": true,
							"setPassword": true
						},
						"display-name": "xxxxx"
					}
				}
			}""", server);
		assertEquals("xxxxx", user.getLogin(), "Login should be correct");
	}

	@Test
	void map() {
		OAuthUser user = new OAuthUser(Map.of(
				"login", "abc"
				, "email", "abc@local"));
		assertEquals("abc", user.getLogin(), "Login should be correct");
		assertEquals("abc@local", user.getEmail(), "Email should be correct");
		assertNull(user.getUserData().get(PARAM_FNAME), "First name should be empty");
		assertNull(user.getUserData().get(PARAM_LNAME), "Last name should be empty");
	}
}
