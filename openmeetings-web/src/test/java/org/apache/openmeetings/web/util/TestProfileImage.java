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
package org.apache.openmeetings.web.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ApplicationHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.web.IsolatedTest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@IsolatedTest
class TestProfileImage {
	@BeforeAll
	static void setup() {
		OpenmeetingsVariables.setWicketApplicationName("TestProfileImage");
		ApplicationHelper.ensureRequestCycle(ApplicationHelper.ensureApplication());
	}

	@AfterAll
	static void teardown() {
		ApplicationHelper.destroyApplication();
	}

	private static List<String> invalidURLs() {
		return List.of("../../../abracadabra"
			, "/etc/hosts"
			, "file:///etc/hosts"
			, "ftp://username:password@ftp.domain.ext/folder/abracadabra"
			, "smb://video/abracadabra"
			, "\\\\video/abracadabra"
			, "javascript:alert(document.cookie)"
			, "emailto:jane@doe");
	}

	@ParameterizedTest
	@MethodSource("invalidURLs")
	void testSanitize(String url) {
		User u = new User();
		u.setId(666L);
		u.setInserted(new Date());
		u.setPictureUri(url);
		assertFalse(ProfileImageResourceReference.sanitize(u).isAbsolute()
				, "Invalid URL should be treated as relative");
	}

	@ParameterizedTest
	@MethodSource("invalidURLs")
	void testProfilePicture(String url) {
		User u = new User();
		u.setId(666L);
		u.setInserted(new Date());
		u.setPictureUri(url);
		assertTrue(ProfileImageResourceReference.getUrl(RequestCycle.get(), u).startsWith("./profile/666")
				, "There should be standard picture URL");
	}
}
