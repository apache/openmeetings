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

import java.util.stream.Stream;

import jakarta.servlet.http.Cookie;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TestOmAuthenticationStrategy extends AbstractWicketTesterTest {
	@Test
	void testLength() {
		OmAuthenticationStrategy s = new OmAuthenticationStrategy(randomUUID().toString());

		assertEquals(0, s.decode(null).length);
		assertEquals(4, s.decode("1").length);
		assertEquals(4, s.decode("1-sep-2").length);
		assertEquals(4, s.decode("1-sep-2-sep-user").length);
		assertEquals(4, s.decode("1-sep-2-sep-user-sep-3").length);
		assertEquals(4, s.decode("-sep--sep--sep-").length);
	}

	private static Stream<Arguments> provideAuthArgs() {
		return Stream.of(
			Arguments.of("aa", "bb", User.Type.CONTACT, null)
			, Arguments.of("bond", "James Bond", User.Type.USER, null)
			, Arguments.of("jira-admin", "Super-Puper-Pr1vate", User.Type.EXTERNAL, null)
			, Arguments.of("ldap-admin", "An0ther-Pr1vate_Pa$$", User.Type.LDAP, 1L)
			, Arguments.of("oauth", "Pr1vate_Pa$$", User.Type.OAUTH, 100L)
		);
	}

	@ParameterizedTest
	@MethodSource("provideAuthArgs")
	void test(String user, String pass, User.Type type, Long domainId) {
		OmAuthenticationStrategy s = new OmAuthenticationStrategy(randomUUID().toString());
		s.save(null, null, User.Type.OAUTH, null);
		assertNull(s.load(), "Wasn't saved, should not be loaded");

		s.save(user, pass, type, domainId);
		copyCookies();
		String[] data = s.load();
		if (type == User.Type.OAUTH) {
			assertNull(data, "Should NOT be loaded");
		} else {
			assertNotNull(data, "Should be loaded");

			assertEquals(user, data[0], "username");
			assertEquals(pass, data[1], "pass");
			assertEquals(type.toString(), data[2], "type");
			assertEquals(String.valueOf(domainId), data[3], "domainId");
		}
	}

	private void copyCookies() {
		for (Cookie c : tester.getResponse().getCookies()) {
			tester.getRequest().addCookie(c);
		}
	}
}
