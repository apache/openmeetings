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
package org.apache.openmeetings.test.session;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.apache.openmeetings.core.session.store.HashMapStore;
import org.apache.openmeetings.core.session.store.IClientPersistenceStore;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestHashMapSession extends AbstractJUnitDefaults {
	protected static final Logger log = Red5LoggerFactory.getLogger(TestHashMapSession.class, webAppRootKey);

	@Autowired
	private HashMapStore cache;

	@Test
	public void testHashMapSession() {
		//make sure the cache is empty before starting the test
		cache.clear();

		for (int i = 0; i < 20; i++) {
			Long id = (long)i;

			StreamClient rcm = new StreamClient();
			rcm.setId(id);
			rcm.setConnectedSince(new Date());
			rcm.setScope("scopeName");

			rcm.setUid(UUID.randomUUID().toString());

			rcm.setUserport(0);
			rcm.setUserip("remoteAddress");
			rcm.setSwfurl("swfUrl");
			rcm.setMod(false);

			if (cache.containsKey(id)) {
				log.error("Tried to add an existing Client " + id);
				break;
			}
			cache.remove(id);
		}
		String logString = cache.getDebugInformation(Arrays.asList(IClientPersistenceStore.DEBUG_DETAILS.SIZE));
		log.debug("######## \n\r "+ logString + " \n\r ########");
		assertEquals(0, cache.size());
	}
}
