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
package org.apache.openmeetings.web.pages.install;

import static com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog.SUBMIT;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.openmeetings.AbstractJUnitDefaults.adminUsername;
import static org.apache.openmeetings.AbstractJUnitDefaults.email;
import static org.apache.openmeetings.AbstractJUnitDefaults.group;
import static org.apache.openmeetings.AbstractJUnitDefaults.userpass;
import static org.apache.openmeetings.AbstractWicketTester.checkErrors;
import static org.apache.openmeetings.AbstractWicketTester.countErrors;
import static org.apache.openmeetings.AbstractWicketTester.getButtonBehavior;
import static org.apache.openmeetings.AbstractWicketTester.getWicketTester;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_APP_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.red5.logging.Red5LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import org.apache.openmeetings.AbstractSpringTest;
import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.util.crypt.SCryptImplementation;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.ui.widget.dialog.ButtonAjaxBehavior;

public class TestInstall {
	private static final Logger log = getLogger(TestInstall.class, getWebAppRootKey());
	private static final String DERBY_HOME = "derby.system.home";
	private static final String WIZARD_PATH = "wizard";
	private File tempFolder;
	protected WicketTester tester;
	protected Random rnd = new Random();

	public static void setDerbyHome(File f) throws IOException {
		System.setProperty(DERBY_HOME, f.getCanonicalPath());
	}

	public static void resetDerbyHome() {
		System.getProperties().remove(DERBY_HOME);
	}

	@Before
	public void setUp() throws IOException {
		AbstractSpringTest.setOmHome();
		setWicketApplicationName(DEFAULT_APP_NAME);
		tempFolder = Files.createTempDirectory("omtempdb").toFile();
		setDerbyHome(tempFolder);
		tester = getWicketTester();
		assertNotNull("Web session should not be null", WebSession.get());
		Locale[] locales = Locale.getAvailableLocales();
		tester.getSession().setLocale(locales[rnd.nextInt(locales.length)]);
		log.debug("Setup complete");
	}

	@After
	public void tearDown() throws IOException {
		AbstractWicketTester.destroy(tester);
		resetDerbyHome();
		deleteDirectory(tempFolder);
	}

	@Test
	public void testInstall() {
		InstallWizardPage page = tester.startPage(InstallWizardPage.class);
		tester.assertRenderedPage(InstallWizardPage.class);
		InstallWizard wiz = (InstallWizard)page.get(WIZARD_PATH);
		assertNull("Model should be null", wiz.getWizardModel().getActiveStep());
		tester.executeBehavior((AbstractAjaxBehavior)page.getBehaviorById(0)); //welcome step
		assertNotNull("Model should NOT be null", wiz.getWizardModel().getActiveStep());

		ButtonAjaxBehavior prev = getButtonBehavior(tester, WIZARD_PATH, "PREV");
		//check enabled, add check for other buttons on other steps
		assertFalse("Prev button should be disabled", prev.getButton().isEnabled());
		ButtonAjaxBehavior next = getButtonBehavior(tester, WIZARD_PATH, "NEXT");
		ButtonAjaxBehavior finish = getButtonBehavior(tester, WIZARD_PATH, SUBMIT);
		tester.executeBehavior(next); //DB step
		FormTester wizardTester = tester.newFormTester("wizard:form");
		wizardTester.select("view:form:dbType", 1);
		checkErrors(tester, 0);
		tester.executeBehavior(next); //user step
		checkErrors(tester, 0);
		wizardTester.setValue("view:username", adminUsername);
		wizardTester.setValue("view:password", userpass);
		wizardTester.setValue("view:email", email);
		String[] tzIds = TimeZone.getAvailableIDs();
		wizardTester.select("view:timeZone", rnd.nextInt(tzIds.length));
		wizardTester.setValue("view:group", group);
		tester.executeBehavior(next); //cfg+smtp step
		checkErrors(tester, 0);
		wizardTester.setValue("view:smtpPort", "25");
		wizardTester.select("view:defaultLangId", 0);
		tester.executeBehavior(next); //converters step
		checkErrors(tester, 0);
		wizardTester.setValue("view:docDpi", "150");
		wizardTester.setValue("view:docQuality", "90");
		tester.executeBehavior(next); //crypt step
		// not checking errors
		if (countErrors(tester) > 0) {
			tester.cleanupFeedbackMessages();
			wizardTester.setValue("view:docDpi", "150");
			wizardTester.setValue("view:docQuality", "90");
			tester.executeBehavior(next); //skip errors
		}
		wizardTester.setValue("view:cryptClassName", SCryptImplementation.class.getName());
		tester.executeBehavior(next); //install step
		checkErrors(tester, 0);
		tester.executeBehavior(finish);
		checkErrors(tester, 0);
	}
}
