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
package org.apache.openmeetings.web.backup;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_FFMPEG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_IMAGEMAGIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_OFFICE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_SOX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getCryptClassName;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.openmeetings.backup.BackupImport;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.web.AbstractOmServerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import jakarta.inject.Inject;


public class AbstractTestImport extends AbstractOmServerTest {
	private String cryptClass = null;
	@Inject
	protected BackupImport backupImport;

	@BeforeEach
	public void setUp() throws Exception {
		// Crypt class need to be preserved here to avoid overriding by backup import
		cryptClass = getCryptClassName();
	}

	@AfterEach
	public void tearDown() {
		Configuration cfg = cfgDao.get(CONFIG_CRYPT);
		assertNotNull(cfg, "Not null config should be returned");
		cfg.setValue(cryptClass);
		cfgDao.update(cfg, null);
		for (String key : new String[] {CONFIG_PATH_IMAGEMAGIC, CONFIG_PATH_FFMPEG, CONFIG_PATH_OFFICE, CONFIG_PATH_SOX}) {
			Configuration c = cfgDao.get(key);
			assertNotNull(c, "Not null config should be returned");
			c.setValue("");
			cfgDao.update(c, null);
		}
	}
}
