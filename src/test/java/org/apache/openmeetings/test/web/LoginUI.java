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
package org.apache.openmeetings.test.web;

import org.apache.openmeetings.test.AbstractWicketTester;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.wicket.util.tester.FormTester;

public class LoginUI extends AbstractWicketTester {

        //@Test Needs to much memory to start this test, and there is nothing useful here inside other then some basic
        //proof of concept (basically the concept failed)
	public void testLoginUi() {
		
		tester.startPage(MainPage.class);
		
		tester.assertRenderedPage(SignInPage.class);
		
		FormTester formTester = tester.newFormTester("signin:signin");
		formTester.setValue("login", username);
		formTester.setValue("pass", userpass);
		
		//How to reference specific buttons in Wicket jQuery UI ?!
		
		formTester.submit();
		
		System.err.println("getLastRenderedPage: "+ tester.getLastRenderedPage().getMarkup().toString());
		
		//will fail
		//tester.assertComponent("dashboard", DashboardPanel.class);
		
	}
	
}

