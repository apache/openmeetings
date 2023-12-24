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
package org.apache.openmeetings.web.app;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.servlet.http.Cookie;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;

class TestOmAuthenticationStrategy extends AbstractWicketTesterTest {
	@Test
	void test() {
		String encKey = randomUUID().toString();
		OmAuthenticationStrategy s = new OmAuthenticationStrategy(encKey, "test");
		s.save(null, null, User.Type.OAUTH, null);
		assertNull(s.load(), "Wasn't saved, should not be loaded");

		s.save("aa", "bb", User.Type.CONTACT, null);
		copyCookies();
		assertNotNull(s.load(), "Should be loaded");

		assertEquals(0, s.decode(null).length);
		assertEquals(4, s.decode("1").length);
		assertEquals(4, s.decode("1-sep-2").length);
		assertEquals(4, s.decode("1-sep-2-sep-user").length);
		assertEquals(4, s.decode("1-sep-2-sep-user-sep-3").length);
		assertEquals(4, s.decode("-sep--sep--sep-").length);
	}

	private void copyCookies() {
		for (Cookie c : tester.getResponse().getCookies()) {
			tester.getRequest().addCookie(c);
		}
	}
}
