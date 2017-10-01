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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmException;
import org.junit.Test;

public class TestWebSession extends AbstractWicketTester {
	@Test
	public void testLogin() throws OmException {
		WebSession ws = WebSession.get();
		assertFalse("Should not be signed in", ws.isSignedIn());

		try {
			ws.signIn(adminUsername, "", User.Type.contact, null);
			fail("Exception should be thrown");
		} catch(OmException exc) {
			assertTrue("Expected exception", true);
		}
		assertFalse(ws.signIn(adminUsername, "", User.Type.user, null));
		assertTrue(ws.signIn(adminUsername, userpass, User.Type.user, null));
		try {
			ws.signIn(adminUsername, userpass, User.Type.ldap, null);
			fail("Exception should be thrown");
		} catch(OmException exc) {
			assertTrue("Expected exception", true);
		}
	}
}
