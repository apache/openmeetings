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

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.data.conference.dao.ClientDao;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.rooms.Client;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDbSession extends AbstractOpenmeetingsSpringTest {

	protected static final Logger log = Red5LoggerFactory.getLogger(
			TestDbSession.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ServerDao serverDao;

	@Autowired
	private ClientDao clientDao;

	@Test
	public void testDbSessionFunctions() {

		clientDao.cleanAllClients();

		Server server = serverDao.get(1);
		if (server == null) {
			server = new Server();
			server.setName("Server 1");
			serverDao.update(server, 0);
		}

		Client cl1 = new Client();
		cl1.setStreamid("1");
		cl1.setServer(null);
		cl1.setRoom_id(1L);
		cl1.setPublicSID("public1");
		clientDao.add(cl1);

		Client cl2 = new Client();
		cl2.setStreamid("2");
		cl2.setServer(null);
		cl2.setRoom_id(2L);
		cl2.setPublicSID("public2");
		clientDao.add(cl2);

		Client cl3 = new Client();
		cl3.setStreamid("3");
		cl3.setServer(server);
		cl3.setRoom_id(3L);
		cl3.setPublicSID("public3");
		clientDao.add(cl3);

		Client clTest = clientDao.getClientByServerAndStreamId(null, "1");

		log.debug("cl1 " + cl1);
		log.debug("clTest " + clTest);

		assertEquals(clTest.getId(), cl1.getId());

		Client clTest3 = clientDao.getClientByServerAndStreamId(server, "3");

		log.debug("cl3 " + cl3);
		log.debug("clTest3 " + clTest3);

		assertEquals(clTest3.getId(), cl3.getId());

		Client clTest_NOT_3 = clientDao.getClientByServerAndStreamId(null, "3");

		log.debug("clTest_NOT_3 " + clTest_NOT_3);
		assertEquals(null, clTest_NOT_3);

	}

}
