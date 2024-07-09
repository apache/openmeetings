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
package org.apache.openmeetings.config;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SMTP_SERVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.apache.openmeetings.web.AbstractOmServerTest;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestConfig extends AbstractOmServerTest {
	private static final Logger log = LoggerFactory.getLogger(TestConfig.class);

	@Test
	void getConfigKey() {
		Configuration smtpServer = cfgDao.get(CONFIG_SMTP_SERVER);

		log.error("smtpServer.getUser(): {}", smtpServer.getUser());

		assertNull(smtpServer.getUser());
	}

	@Test
	void getConfigs() {
		try {
			List<Configuration> list = cfgDao.get(4, 6);

			for (Configuration conf : list) {
				log.error("conf.getKey() = {}", conf.getKey());
				log.error("conf.getUser() = {}", conf.getUser());
				if (conf.getUser() != null) {
					log.error("conf.getUser().getLogin() = {}", conf.getUser().getLogin());
				}
			}

			assertEquals(6, list.size());
		} catch (Exception err) {
			log.error("[getConfigs]", err);
		}

	}
}
