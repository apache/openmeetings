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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class TestConfig extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestConfig.class, getWebAppRootKey());

	@Test
	public void getConfigKey() {
		System.err.println("THIS");

		Configuration smtp_server = cfgDao.get(CONFIG_SMTP_SERVER);

		System.err.println("smtp_server " + smtp_server.getUser());

		assertNull(smtp_server.getUser());
	}

	@Test
	public void getConfigs() {

		try {
			List<Configuration> list = cfgDao.get(4, 6);

			for (Configuration conf : list) {
				log.error("conf.getKey() " + conf.getKey());
				log.error("conf.getUser() " + conf.getUser());
				if (conf.getUser() != null) {
					log.error("conf.getUsers() " + conf.getUser().getLogin());
				}
			}

			assertEquals(list.size(), 6);

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}

	}
}
