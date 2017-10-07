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

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.openmeetings.AbstractJUnitDefaults.adminUsername;
import static org.apache.openmeetings.AbstractJUnitDefaults.email;
import static org.apache.openmeetings.AbstractJUnitDefaults.group;
import static org.apache.openmeetings.AbstractJUnitDefaults.userpass;
import static org.apache.openmeetings.AbstractSpringTest.setOmHome;
import static org.apache.openmeetings.cli.Admin.RED5_HOME;
import static org.apache.openmeetings.web.pages.install.TestInstall.resetDerbyHome;
import static org.apache.openmeetings.web.pages.install.TestInstall.setDerbyHome;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAdmin {
	private File tempFolder;

	@Before
	public void setUp() throws IOException {
		setOmHome();
		tempFolder = Files.createTempDirectory("omtempdb").toFile();
		System.setProperty("user.dir", tempFolder.getCanonicalPath());
		System.setProperty(RED5_HOME, tempFolder.getCanonicalPath());
		setDerbyHome(tempFolder);
	}

	@After
	public void tearDown() throws IOException {
		resetDerbyHome();
		System.getProperties().remove(RED5_HOME);
		deleteDirectory(tempFolder);
	}

	private static void checkError(String... args) throws Exception {
		try {
			new Admin().process(args);
			fail();
		} catch (ExitException ee) {
			Assert.assertTrue(true);
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
		Assert.assertTrue(true);
	}

	@Test
	public void testInstallParamConflict() throws Exception {
		checkError("-i", "-file", "aaa", "-user", "bbb");
		checkError("-i", "-file", "aaa", "-email", "bbb");
		checkError("-i", "-file", "aaa", "-group", "bbb");
	}

	private static Admin getAdmin(Admin _a) {
		if (_a == null) {
			Admin a = new Admin();
			setOmHome();
			return a;
		}
		return _a;
	}

	private static void performInstall(Admin _a, String... args) throws Exception {
		List<String> params = Arrays.asList("-i"
				, "-tz", "Europe/Berlin"
				, "-email", email
				, "-group", group
				, "-user", adminUsername
				, "--password", userpass);
		for (String a : args) {
			params.add(a);
		}
		Admin a = getAdmin(_a);
		a.process(params.toArray(new String[] {}));
	}

	@Test
	public void testInstallBackup() throws Exception {
		Admin a = getAdmin(null);
		performInstall(a);
		//backup
		a.process("-b");
		//backup to file
		a.process("-b", Files.createTempFile("omtempbackup", null).toFile().getCanonicalPath());
	}
}
