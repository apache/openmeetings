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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.junit.Test;

public class TestOAuthUser {
	@Test
	public void firstLevel() {
		OAuthServer server = new OAuthServer();
		server.setLoginParamName("id");
		server.setEmailParamName("email");
		server.setFirstnameParamName("given_name");
		server.setLastnameParamName("family_name");
		OAuthUser user = new OAuthUser(
				"{'id': '11klahjsfwehf5', 'email': 'alsfkvslvmclqwkdsm@gmail.com', 'verified_email': true, 'name': 'John Doe', 'given_name': 'John', 'family_name': 'Doe', 'link': 'https://plus.google.com/+JohnDoe', 'picture': 'https://lh3.googleusercontent.com/somehash/photo.jpg', 'gender': 'male', 'locale': 'en'}"
				, server
				);
		assertEquals("UID should be correct", "11klahjsfwehf5", user.getUid());
		assertEquals("Email should be correct", "alsfkvslvmclqwkdsm@gmail.com", user.getEmail());
		assertEquals("Firstname should be correct", "John", user.getFirstName());
		assertEquals("Lastname should be correct", "Doe", user.getLastName());
	}

	@Test
	public void secondLevel() {
		OAuthServer server = new OAuthServer();
		server.setLoginParamName("uid");
		server.setEmailParamName("email");
		server.setFirstnameParamName("first_name");
		server.setLastnameParamName("last_name");
		OAuthUser user = new OAuthUser(
				"{'response':[{'uid':4uidhere4,'first_name':'John','last_name':'Doe'}]}"
				, server
				);
		assertEquals("UID should be correct", "4uidhere4", user.getUid());
		assertNull("Email should be empty", user.getEmail());
		assertEquals("Firstname should be correct", "John", user.getFirstName());
		assertEquals("Lastname should be correct", "Doe", user.getLastName());

		server.setIconUrl("https://goo.gl/images/q23g7Y");
		user = new OAuthUser(
				"{'response':[{'uid':4uidhere4,'first_name':'John','last_name':'Doe'}]}"
				, server
				);
		assertEquals("Email should be constructed", "4uidhere4@goo.gl", user.getEmail());
	}
	/*
{'id': '11klahjsfwehf5', 'email': 'alsfkvslvmclqwkdsm@gmail.com', 'verified_email': true, 'name': 'John Doe', 'given_name': 'John', 'family_name': 'Doe', 'link': 'https://plus.google.com/+JohnDoe', 'picture': 'https://lh3.googleusercontent.com/somehash/photo.jpg', 'gender': 'male', 'locale': 'en'}
*/
/*
{'response':[{'uid':4uidhere4,'first_name':'John','last_name':'Doe'}]}
*/
}
