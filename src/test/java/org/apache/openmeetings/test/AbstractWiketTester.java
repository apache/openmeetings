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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletContext;

import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class AbstractWiketTester extends AbstractOpenmeetingsSpringTest {

	protected WicketTester tester;
	 	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		Application app = new Application();
        
        tester = new WicketTester(app);
		ServletContext sc = app.getServletContext();
        XmlWebApplicationContext  applicationContext = new XmlWebApplicationContext();
        applicationContext.setConfigLocation("classpath:openmeetings-applicationContext.xml");
        applicationContext.setServletContext(sc);
        applicationContext.refresh();
        sc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
        assertNotNull("Web session should not be null", WebSession.get());
		ScopeApplicationAdapter.initComplete = true;
	}

	public void login(String login, String password) {
		if (login != null && password != null) {
			WebSession.get().signIn(login, password, "");
		} else {
			WebSession.get().signIn(username, userpass, "");
		}
		assertTrue("Web session is not signed in for user: " + (login != null ? login : username), WebSession.get().isSignedIn());
	}
	
	@After
	public void tearDown() {
		tester.destroy();
	}
}
