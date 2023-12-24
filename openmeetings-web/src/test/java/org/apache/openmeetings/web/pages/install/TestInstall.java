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

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.openmeetings.web.AbstractOmServerTest.adminUsername;
import static org.apache.openmeetings.web.AbstractOmServerTest.email;
import static org.apache.openmeetings.web.AbstractOmServerTest.group;
import static org.apache.openmeetings.web.AbstractOmServerTest.setOmHome;
import static org.apache.openmeetings.web.AbstractOmServerTest.userpass;
import static org.apache.openmeetings.web.AbstractWicketTesterTest.checkErrors;
import static org.apache.openmeetings.web.AbstractWicketTesterTest.countErrors;
import static org.apache.openmeetings.web.AbstractWicketTesterTest.getWicketTester;
import static org.apache.openmeetings.cli.ConnectionPropertiesPatcher.DEFAULT_DB_NAME;
import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_APP_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.web.IsolatedTest;
import org.apache.openmeetings.cli.ConnectionPropertiesPatcher;
import org.apache.openmeetings.util.crypt.SCryptImplementation;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.util.ConnectionProperties.DbType;
import org.apache.wicket.ajax.AjaxClientInfoBehavior;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.extensions.wizard.WizardButton;
import org.apache.wicket.protocol.ws.WebSocketAwareResourceIsolationRequestCycleListener;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycleListenerCollection;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IsolatedTest
public class TestInstall {
	private static final Logger log = LoggerFactory.getLogger(TestInstall.class);
	private static final String WIZARD_PATH = "wizard";
	private static final String FORM_PATH = WIZARD_PATH + ":form";
	private File tempFolder;
	protected WicketTester tester;
	protected Random rnd = new Random();

	private static String getH2Home(File dir) throws Exception {
		File f = new File(dir.getCanonicalFile(), DEFAULT_DB_NAME);
		String path = f.toURI().toString();
		if (System.getProperty("os.name").startsWith("Win")) {
			path = "file:" + path.substring(6);
		}
		log.warn("Gonna create DB at {}", path);
		return path;
	}

	public static void setH2Home(File f) throws Exception {
		ConnectionPropertiesPatcher.patch(DbType.H2, null, null, getH2Home(f), null, null);
	}

	public static void resetH2Home() throws Exception {
		ConnectionPropertiesPatcher.patch(DbType.H2, null, null, null, null, null);
	}

	@BeforeEach
	public void setUp() throws Exception {
		log.info("Going to perform setup for TestInstall");
		setOmHome();
		setWicketApplicationName(DEFAULT_APP_NAME);
		tempFolder = Files.createTempDirectory("omtempdb").toFile();
		setH2Home(tempFolder);
		tester = getWicketTester((Application)ensureApplication(-1L));
		RequestCycleListenerCollection listeners = tester.getApplication().getRequestCycleListeners();
		for (Iterator<IRequestCycleListener> iter = listeners.iterator(); iter.hasNext();) {
			IRequestCycleListener l = iter.next();
			if (l instanceof WebSocketAwareResourceIsolationRequestCycleListener) {
				listeners.remove(l);
				break;
			}
		}
		assertNotNull(WebSession.get(), "Web session should not be null");
		Locale[] locales = Locale.getAvailableLocales();
		tester.getSession().setLocale(locales[rnd.nextInt(locales.length)]);
		log.info("Setup complete");
	}

	@AfterEach
	public void tearDown() throws Exception {
		log.info("Going to perform clean-up for TestInstall");
		AbstractWicketTesterTest.destroy(tester);
		log.info("WicketTester is destroyed");
		resetH2Home();
		deleteQuietly(tempFolder);
		log.info("Clean-up complete");
	}

	@Test
	void testInstall() {
		InstallWizardPage page = tester.startPage(InstallWizardPage.class);
		tester.assertRenderedPage(InstallWizardPage.class);
		InstallWizard wiz = (InstallWizard)page.get(WIZARD_PATH);
		assertFalse(wiz.isEnabled(), "Wizard should be disabled");
		AjaxClientInfoBehavior clientInfo = page.getBehaviors(AjaxClientInfoBehavior.class).get(0);
		tester.executeBehavior(clientInfo);
		assertTrue(wiz.isEnabled(), "Wizard should be enabled");
		assertNotNull(wiz.getWizardModel().getActiveStep(), "Model should NOT be null");

		WizardButton prev = getWizardButton("previous");
		//check enabled, add check for other buttons on other steps
		assertFalse(prev.isEnabled(), "Prev button should be disabled");
		WizardButton next = getWizardButton("next");
		AbstractAjaxBehavior finish = (AbstractAjaxBehavior)getWizardButton("finish").getBehaviorById(0);
		FormTester wizardTester = tester.newFormTester(FORM_PATH);
		wizardTester.submit(next);
		wizardTester = tester.newFormTester(FORM_PATH);
		wizardTester.select("view:form:dbType", 1);
		checkErrors(tester, 0);
		wizardTester.submit(next); //user step
		checkErrors(tester, 0);
		wizardTester = tester.newFormTester(FORM_PATH);
		wizardTester.setValue("view:username", adminUsername);
		wizardTester.setValue("view:password", userpass);
		wizardTester.setValue("view:email", email);
		String[] tzIds = TimeZone.getAvailableIDs();
		wizardTester.select("view:timeZone", rnd.nextInt(tzIds.length));
		wizardTester.setValue("view:group", group);
		wizardTester.submit(next); //cfg+smtp step
		checkErrors(tester, 0);
		wizardTester = tester.newFormTester(FORM_PATH);
		wizardTester.setValue("view:smtpPort", "25");
		wizardTester.select("view:defaultLangId", 0);
		wizardTester.submit(next); //converters step
		checkErrors(tester, 0);
		wizardTester = tester.newFormTester(FORM_PATH);
		wizardTester.setValue("view:docDpi", "150");
		wizardTester.setValue("view:docQuality", "90");
		wizardTester.submit(next); //crypt step
		// not checking errors
		if (countErrors(tester) > 0) {
			tester.cleanupFeedbackMessages();
			wizardTester = tester.newFormTester(FORM_PATH);
			wizardTester.setValue("view:docDpi", "150");
			wizardTester.setValue("view:docQuality", "90");
			wizardTester.submit(next); //skip errors
		}
		wizardTester = tester.newFormTester(FORM_PATH);
		wizardTester.setValue("view:cryptClassName", SCryptImplementation.class.getName());
		wizardTester.submit(next); //install step
		checkErrors(tester, 0);
		tester.executeBehavior(finish);
		checkErrors(tester, 0);
	}

	private WizardButton getWizardButton(String name) {
		return (WizardButton)tester.getComponentFromLastRenderedPage("wizard:form:buttons:" + name);
	}
}
