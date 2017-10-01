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
package org.apache.openmeetings.web;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.openmeetings.web.user.dashboard.OmDashboardPanel;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.protocol.ws.util.tester.WebSocketTester;
import org.junit.Assert;
import org.junit.Test;

public class TestMainAreas extends AbstractWicketTester {
	@Test
	public void testSigninIsDisplayed() {
		tester.startPage(MainPage.class);
		tester.assertRenderedPage(SignInPage.class);
	}

	@Test
	public void testDashboard() throws OmException {
		Assert.assertTrue(((WebSession)tester.getSession()).signIn(adminUsername, userpass, User.Type.user, null));;
		MainPage page = tester.startPage(MainPage.class);
		tester.assertRenderedPage(MainPage.class);
		tester.executeBehavior((AbstractAjaxBehavior)tester.getLastRenderedPage().getBehaviors().get(0));
		tester.executeBehavior((AbstractAjaxBehavior)tester.getLastRenderedPage().get("main-container").getBehaviors().get(0));
		WebSocketTester webSocketTester = new WebSocketTester(tester, page);
		webSocketTester.sendMessage("socketConnected");

		//tester.assertComponentOnAjaxResponse("main-container:main:contents:child")
		tester.assertComponent("main-container:main:contents:child", OmDashboardPanel.class); //main-container:main:contents:child:dashboard
		//tester.getLastRenderedPage(). visitChildren(OmDashboardPanel.class, visitor) find(OmDashboardPanel.class)
	}
}
