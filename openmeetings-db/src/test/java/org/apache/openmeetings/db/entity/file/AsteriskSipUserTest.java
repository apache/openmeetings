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
package org.apache.openmeetings.db.entity.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.apache.openmeetings.util.ImportHelper.getPrivateValue;

import org.apache.openmeetings.db.entity.user.AsteriskSipUser;
import org.junit.jupiter.api.Test;

class AsteriskSipUserTest {
	@Test
	void testGetFileShouldReturnFirstSlideWithPDF() throws Exception {
		AsteriskSipUser u = new AsteriskSipUser();
		u.setPasswordDigest("bond:asterisk:12345");

		String value = getPrivateValue(u, "passwordDigest", String.class);

		assertEquals(
			"SHA-256:c77920ac5aa487108de1ff6c198dda60cec2bed48655ee209eb46de20ab1e111"
			// leave it here for future upgrade "SHA-512-256:34ffbf14183bdbc03a595ab16ff53141f10475d1c810a8baff43bc1645df31e9"
			, value);
	}
}
