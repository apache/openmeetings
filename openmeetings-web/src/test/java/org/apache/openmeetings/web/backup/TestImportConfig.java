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

import static org.apache.openmeetings.web.backup.TestImport.BACKUP_ROOT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_FRONTEND;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_OAUTH;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REGISTER_SOAP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REPLY_TO_ORGANIZER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_SERVER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.apache.openmeetings.db.entity.basic.Configuration;
import org.junit.jupiter.api.Test;

class TestImportConfig extends AbstractTestImport {

	@Test
	void importConfigsNoKeyDeleted() throws Exception {
		long configsCount = cfgDao.count();
		File configs = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "config/skip/configs.xml").toURI());
		backupImport.importConfigs(configs.getParentFile());
		assertEquals(configsCount, cfgDao.count(), "No records should be added");
	}

	@Test
	void importConfigsBool() throws Exception {
		cfgDao.get(CONFIG_REGISTER_FRONTEND, CONFIG_REGISTER_SOAP, CONFIG_REGISTER_OAUTH, CONFIG_REPLY_TO_ORGANIZER).forEach(cfg -> {
			cfg.setValueB(cfg.getKey().equals(CONFIG_REPLY_TO_ORGANIZER));
			cfgDao.update(cfg, null);
		});
		Configuration cfgNE2 = cfgDao.forceGet("test.non.existent.cfg.2");
		if (cfgNE2 == null) {
			cfgNE2 = new Configuration();
			cfgNE2.setKey("test.non.existent.cfg.2");
			cfgNE2.setType(Configuration.Type.BOOL);
		}
		cfgDao.update(cfgNE2, null, true);
		File configs = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "config/bool/configs.xml").toURI());
		backupImport.importConfigs(configs.getParentFile());
		cfgDao.get(CONFIG_REGISTER_FRONTEND, CONFIG_REGISTER_SOAP, CONFIG_REGISTER_OAUTH, CONFIG_REPLY_TO_ORGANIZER).forEach(cfg -> {
			boolean exp = !cfg.getKey().equals(CONFIG_REPLY_TO_ORGANIZER);
			assertEquals(exp, cfg.getValueB(), "Cfg " + cfg.getKey() + " should have value " + exp);
		});
	}

	@Test
	void importConfigs() throws Exception {
		File configs = new File(getClass().getClassLoader().getResource(BACKUP_ROOT + "config/configs.xml").toURI());
		backupImport.importConfigs(configs.getParentFile());
		assertEquals("ml.abracadabra.com", cfgDao.get(CONFIG_SMTP_SERVER).getValue(), "SMTP server should be updated");
	}
}
