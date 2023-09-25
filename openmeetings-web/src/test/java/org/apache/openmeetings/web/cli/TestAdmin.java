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
package org.apache.openmeetings.web.cli;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.openmeetings.web.AbstractOmServerTest.adminUsername;
import static org.apache.openmeetings.web.AbstractOmServerTest.email;
import static org.apache.openmeetings.web.AbstractOmServerTest.group;
import static org.apache.openmeetings.web.AbstractOmServerTest.setOmHome;
import static org.apache.openmeetings.web.AbstractOmServerTest.userpass;
import static org.apache.openmeetings.cli.Admin.OM_HOME;
import static org.apache.openmeetings.db.util.ApplicationHelper.destroyApplication;
import static org.apache.openmeetings.web.pages.install.TestInstall.resetH2Home;
import static org.apache.openmeetings.web.pages.install.TestInstall.setH2Home;
import static org.apache.openmeetings.util.OmFileHelper.getOmHome;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_CONTEXT_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setInitComplete;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.openmeetings.cli.Admin;
import org.apache.openmeetings.cli.ExitException;
import org.apache.openmeetings.web.IsolatedTest;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IsolatedTest
class TestAdmin {
	private File tempFolder;

	@BeforeEach
	public void setUp() throws Exception {
		setOmHome();
		tempFolder = Files.createTempDirectory("omtempdb").toFile();
		System.setProperty("user.dir", tempFolder.getCanonicalPath());
		System.setProperty(OM_HOME, getOmHome().getCanonicalPath());
		setH2Home(tempFolder);
		System.setProperty("context", UUID.randomUUID().toString());
	}

	@AfterEach
	public void tearDown() throws Exception {
		resetH2Home();
		System.getProperties().remove(OM_HOME);
		WebApplication app = (WebApplication)Application.get(getWicketApplicationName());
		if (app != null) {
			destroyApplication();
			setInitComplete(false);
		}
		System.setProperty("context", DEFAULT_CONTEXT_NAME);
		setWicketApplicationName(DEFAULT_CONTEXT_NAME);
		deleteQuietly(tempFolder);
	}

	private static void checkError(String... args) throws Exception {
		Admin a = new Admin();
		try {
			a.process(args);
			fail();
		} catch (ExitException ee) {
			assertTrue(true);
		}
	}

	@Test
	void testNoArgs() throws Exception {
		checkError();
	}

	@Test
	void testBadArgs() throws Exception {
		checkError("aaaa");
	}

	@Test
	void testUsage() throws Exception {
		new Admin().process("-h");
		assertTrue(true);
	}

	@Test
	void testInstallParamConflict() throws Exception {
		checkError("-v", "-i", "-file", "aaa", "-user", "bbb");
		checkError("-i", "-file", "aaa", "-email", "bbb");
		checkError("-i", "-file", "aaa", "-group", "bbb");
	}

	private static void performInstall(Admin admin, String... args) throws Exception {
		List<String> params = new ArrayList<>(List.of("-i"
				, "-tz", "Europe/Berlin"
				, "-email", email
				, "-group", group
				, "-user", adminUsername
				, "--password", userpass));
		for (String a : args) {
			params.add(a);
		}
		admin.process(params.toArray(new String[] {}));
	}

	@Test
	void testInstallBackup() throws Exception {
		String tempDB = Files.createTempFile("omtempdb", null).toFile().getCanonicalPath();
		Admin a = new Admin();
		performInstall(a, "--db-name", tempDB);
		//backup
		a.process("-b");
		//backup to file
		File backup = Files.createTempFile("omtempbackup", null).toFile();
		a.process("-b", "-file", backup.getCanonicalPath());
		assertTrue(backup.exists(), "backup Should be created");
		assertTrue(Files.size(backup.toPath()) > 0, "backup shouldn't be empty");
	}

	@Test
	void testFilesNoDb() throws Exception {
		//clean-up report
		new Admin().process("-f");
		assertTrue(true, "No exception is thrown");
	}
}
