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
import static org.apache.openmeetings.cli.Admin.RED5_HOME;
import static org.apache.openmeetings.util.OmFileHelper.getOmHome;
import static org.apache.openmeetings.web.pages.install.TestInstall.resetDerbyHome;
import static org.apache.openmeetings.web.pages.install.TestInstall.setDerbyHome;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.openmeetings.AbstractSpringTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAdmin {
	private File tempFolder;

	@Before
	public void setUp() throws IOException {
		AbstractSpringTest.setOmHome();
		tempFolder = Files.createTempDirectory("omtempdb").toFile();
		System.setProperty("user.dir", tempFolder.getCanonicalPath());
		System.setProperty(RED5_HOME, getOmHome().getCanonicalPath());
		setDerbyHome(tempFolder);
	}

	@After
	public void tearDown() throws IOException {
		resetDerbyHome();
		System.getProperties().remove(RED5_HOME);
		deleteDirectory(tempFolder);
	}

	@Test
	public void testNoArgs() {
		try {
			Admin.handle();
			fail();
		} catch (ExitException ee) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testBadArgs() {
		try {
			Admin.handle("aaaa");
			fail();
		} catch (ExitException ee) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testUsage() {
		Admin.handle("-h");
		Assert.assertTrue(true);
	}
}
