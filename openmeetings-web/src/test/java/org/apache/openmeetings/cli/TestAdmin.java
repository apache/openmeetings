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
package org.apache.openmeetings.cli;

import static org.apache.openmeetings.AbstractJUnitDefaults.adminUsername;
import static org.apache.openmeetings.AbstractJUnitDefaults.email;
import static org.apache.openmeetings.AbstractJUnitDefaults.group;
import static org.apache.openmeetings.AbstractJUnitDefaults.userpass;
import static org.apache.openmeetings.AbstractSpringTest.setOmHome;
import static org.apache.openmeetings.cli.Admin.OM_HOME;
import static org.apache.openmeetings.db.util.ApplicationHelper.destroyApplication;
import static org.apache.openmeetings.util.OmFileHelper.getOmHome;
import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_CONTEXT_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setInitComplete;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;
import static org.apache.openmeetings.web.pages.install.TestInstall.resetDerbyHome;
import static org.apache.openmeetings.web.pages.install.TestInstall.setDerbyHome;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TestAdmin {
	@TempDir
	File tempFolder;

	@BeforeEach
	public void setUp() throws IOException {
		setOmHome();
		System.setProperty("user.dir", tempFolder.getCanonicalPath());
		System.setProperty(OM_HOME, getOmHome().getCanonicalPath());
		setDerbyHome(tempFolder);
		System.setProperty("context", UUID.randomUUID().toString());
	}

	@AfterEach
	public void tearDown() {
		resetDerbyHome();
		System.getProperties().remove(OM_HOME);
		WebApplication app = (WebApplication)Application.get(getWicketApplicationName());
		if (app != null) {
			destroyApplication();
			setInitComplete(false);
		}
		System.setProperty("context", DEFAULT_CONTEXT_NAME);
		setWicketApplicationName(DEFAULT_CONTEXT_NAME);
	}

	private static void checkError(String... args) throws Exception {
		try {
			new Admin().process(args);
			fail();
		} catch (ExitException ee) {
			assertTrue(true);
		}
	}

	@Test
	public void testNoArgs() throws Exception {
		checkError();
	}

	@Test
	public void testBadArgs() throws Exception {
		checkError("aaaa");
	}

	@Test
	public void testUsage() throws Exception {
		new Admin().process("-h");
		assertTrue(true);
	}

	@Test
	public void testInstallParamConflict() throws Exception {
		checkError("-v", "-i", "-file", "aaa", "-user", "bbb");
		checkError("-i", "-file", "aaa", "-email", "bbb");
		checkError("-i", "-file", "aaa", "-group", "bbb");
	}

	private static void performInstall(Admin admin, String... args) throws Exception {
		List<String> params = Arrays.asList("-i"
				, "-tz", "Europe/Berlin"
				, "-email", email
				, "-group", group
				, "-user", adminUsername
				, "--password", userpass
				, "--db-name", UUID.randomUUID().toString().replaceAll("-", ""));
		for (String a : args) {
			params.add(a);
		}
		admin.process(params.toArray(new String[] {}));
	}

	@Test
	public void testInstallBackup() throws Exception {
		Admin a = new Admin();
		performInstall(a);
		//backup
		a.process("-b");
		//backup to file
		a.process("-b", Files.createTempFile("omtempbackup", null).toFile().getCanonicalPath());
	}

	@Test
	public void testFilesNoDb() throws Exception {
		//clean-up report
		new Admin().process("-f");
	}
}
