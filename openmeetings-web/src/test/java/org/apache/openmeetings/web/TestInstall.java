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

import static org.apache.openmeetings.AbstractWicketTester.getWicketTester;
import static org.apache.openmeetings.util.OmFileHelper.getOmHome;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import org.apache.openmeetings.AbstractSpringTest;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.install.InstallWizardPage;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestInstall {
	private static final String DERBY_HOME = "derby.system.home";
	protected WicketTester tester;
	protected Random rnd = new Random();

	@Before
	public void setUp() throws IOException {
		AbstractSpringTest.setOmHome();
		setWicketApplicationName("openmeetings");
		System.setProperty(DERBY_HOME, getOmHome().getCanonicalPath());
		tester = getWicketTester();
		assertNotNull("Web session should not be null", WebSession.get());
		Locale[] locales = Locale.getAvailableLocales();
		tester.getSession().setLocale(locales[rnd.nextInt(locales.length)]);
	}

	@After
	public void tearDown() {
		System.getProperties().remove(DERBY_HOME);
	}

	@Test
	public void testInstall() {
		tester.startPage(InstallWizardPage.class);
		tester.assertRenderedPage(InstallWizardPage.class);
	}
}
