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
package org.apache.openmeetings.test;

import static org.apache.openmeetings.db.util.ApplicationHelper.getWicketTester;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;

public class AbstractWicketTester extends AbstractJUnitDefaults {

	protected WicketTester tester;
	 	
	@Override
	public void setUp() throws Exception {
		super.setUp();
        tester = getWicketTester();
        assertNotNull("Web session should not be null", WebSession.get());
	}

	public void login(String login, String password) {
		if (login != null && password != null) {
			WebSession.get().signIn(login, password, Type.user, null);
		} else {
			WebSession.get().signIn(username, userpass, Type.user, null);
		}
		assertTrue("Web session is not signed in for user: " + (login != null ? login : username), WebSession.get().isSignedIn());
	}
	
	@After
	public void tearDown() {
		tester.destroy();
	}
}
