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

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.session.store.HashMapStore;
import org.apache.openmeetings.session.store.IClientPersistenceStore;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestHashMapSession extends AbstractOpenmeetingsSpringTest {
	
	protected static final Logger log = Red5LoggerFactory.getLogger(
			TestHashMapSession.class, OpenmeetingsVariables.webAppRootKey);
	
	@Autowired
	private HashMapStore cache;
	
	@Autowired
	private ManageCryptStyle manageCryptStyle;
	
	@Test
	public void testHashMapSession() {
		
		for (int i=0;i<20;i++) {
			
			String streamId = ""+i;
			
			Client rcm = new Client();
			rcm.setConnectedSince(new Date());
			rcm.setStreamid(streamId);
			rcm.setScope("scopeName");
			long random = System.currentTimeMillis() + new BigInteger(256, new Random()).longValue();
			
			rcm.setPublicSID(manageCryptStyle.getInstanceOfCrypt()
					.createPassPhrase(String.valueOf(random).toString()));

			rcm.setUserport(0);
			rcm.setUserip("remoteAddress");
			rcm.setSwfurl("swfUrl");
			rcm.setIsMod(new Boolean(false));
			rcm.setCanDraw(new Boolean(false));
			rcm.setIsAVClient(false);

			if (cache.containsKey(null, streamId)) {
				log.error("Tried to add an existing Client " + streamId);
				break;
			}

			cache.put(rcm.getStreamid(), rcm);
			
			cache.remove(null, streamId);
		
		}
		
		String logString = cache.getDebugInformation(Arrays.asList(IClientPersistenceStore.DEBUG_DETAILS.SIZE));
		
		
		log.debug("######## \n\r "+ logString + " \n\r ########");
		
		assertEquals(0, cache.size());
		
	}

}
