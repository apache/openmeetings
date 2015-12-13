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

import java.util.List;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.data.conference.dao.ClientDao;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.room.Client;
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
		
		List<Server> serverList = serverDao.getActiveServers();

		Server server = null;
		if (serverList.size() > 0) {
			server = serverList.get(0);
		} else {
			server = new Server();
			server.setName("Test Server");
			server.setActive(true);
			serverDao.update(server, null);
		}

		Client cl1 = new Client();
		cl1.setStreamid("1");
		cl1.setServer(null);
		cl1.setUser_id(1L);
		cl1.setRoom_id(1L);
		cl1.setPublicSID("public1");
		clientDao.add(cl1);

		Client cl2 = new Client();
		cl2.setStreamid("2");
		cl2.setServer(null);
		cl2.setRoom_id(1L);
		cl2.setUser_id(2L);
		cl2.setPublicSID("public2");
		clientDao.add(cl2);

		Client cl3 = new Client();
		cl3.setStreamid("3");
		cl3.setServer(server);
		cl3.setRoom_id(3L);
		cl3.setUser_id(3L);
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
		
		long numberOfClients1 = clientDao.countClientsByServerAndStreamId(null, "1");
		assertEquals(1, numberOfClients1);
		
		long numberOfClients3 = clientDao.countClientsByServerAndStreamId(server, "3");
		assertEquals(1, numberOfClients3);
		
		long numberOfClients4 = clientDao.countClientsByServerAndStreamId(null, "3");
		assertEquals(0, numberOfClients4);
		
		List<Client> clTest_Pub_1_list = clientDao.getClientsByPublicSIDAndServer(null, "public1");
		assertEquals(cl1.getId(), clTest_Pub_1_list.get(0).getId());
		
		List<Client> clTest_Pub_3_list = clientDao.getClientsByPublicSIDAndServer(server, "public3");
		assertEquals(cl3.getId(), clTest_Pub_3_list.get(0).getId());
		
		List<Client> clTest_Fail_list = clientDao.getClientsByPublicSIDAndServer(null, "public3");
		assertEquals(0, clTest_Fail_list.size());
		
		List<Client> clTest_PubAll_1_list = clientDao.getClientsByPublicSID("public1");
		assertEquals(cl1.getId(), clTest_PubAll_1_list.get(0).getId());
		
		List<Client> clTest_PubAll_3_list = clientDao.getClientsByPublicSID("public3");
		assertEquals(cl3.getId(), clTest_PubAll_3_list.get(0).getId());
		
		List<Client> clTest_FailAll_list = clientDao.getClientsByPublicSID("public4");
		assertEquals(0, clTest_FailAll_list.size());
		
		List<Client> clientsByServerNull = clientDao.getClientsByServer(null);
		assertEquals(2, clientsByServerNull.size());
		
		List<Client> clientsByServer = clientDao.getClientsByServer(server);
		assertEquals(1, clientsByServer.size());
		
		List<Client> clientsAll = clientDao.getClients();
		assertEquals(3, clientsAll.size());
		
		//by userid
		List<Client> clTest_User_1_list = clientDao.getClientsByUserId(null, 1L);
		assertEquals(cl1.getId(), clTest_User_1_list.get(0).getId());
		
		List<Client> clTest_User_3_list = clientDao.getClientsByUserId(server, 3L);
		assertEquals(cl3.getId(), clTest_User_3_list.get(0).getId());
		
		List<Client> clTest_UserFail_list = clientDao.getClientsByUserId(null, 3L);
		assertEquals(0, clTest_UserFail_list.size());
		
		//by roomid
		List<Client> clTest_Room_1_list = clientDao.getClientsByRoomId(1L);
		assertEquals(2, clTest_Room_1_list.size());
		
		List<Client> clTest_Room_3_list = clientDao.getClientsByRoomId(3L);
		assertEquals(cl3.getId(), clTest_Room_3_list.get(0).getId());
		
		List<Client> clTest_RoomFail_list = clientDao.getClientsByRoomId(2L);
		assertEquals(0, clTest_RoomFail_list.size());
		
		//count all
		int countAll = clientDao.countClients();
		assertEquals(3, countAll);
		
		//count by server
		int clTest_Count_1_list = clientDao.countClientsByServer(null);
		assertEquals(2, clTest_Count_1_list);
		
		int clTest_Count_3_list = clientDao.countClientsByServer(server);
		assertEquals(1, clTest_Count_3_list);
		
		//remove by id
		clientDao.delete(cl1);
		
		int clTest_Count_Delete_list = clientDao.countClientsByServer(null);
		assertEquals(1, clTest_Count_Delete_list);
		
		//remove by server and streamid
		clientDao.removeClientByServerAndStreamId(null, "2");
		
		clTest_Count_Delete_list = clientDao.countClientsByServer(null);
		assertEquals(0, clTest_Count_Delete_list);
		
		clientDao.removeClientByServerAndStreamId(server, "3");
		
		clTest_Count_Delete_list = clientDao.countClientsByServer(server);
		assertEquals(0, clTest_Count_Delete_list);
		
		//delete all
		clientDao.cleanAllClients();

		countAll = clientDao.countClients();
		assertEquals(0, countAll);
	}

}
