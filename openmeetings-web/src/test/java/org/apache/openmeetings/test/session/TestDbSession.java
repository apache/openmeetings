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

import java.util.List;

import org.apache.openmeetings.db.dao.room.ClientDao;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDbSession extends AbstractJUnitDefaults {
	protected static final Logger log = Red5LoggerFactory.getLogger(TestDbSession.class, webAppRootKey);

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

		StreamClient cl1 = new StreamClient();
		cl1.setServer(null);
		cl1.setUserId(1L);
		cl1.setScope("1");
		cl1.setUid("public1");
		clientDao.add(cl1);

		StreamClient cl2 = new StreamClient();
		cl2.setServer(null);
		cl2.setScope("1");
		cl2.setUserId(2L);
		cl2.setUid("public2");
		clientDao.add(cl2);

		StreamClient cl3 = new StreamClient();
		cl3.setServer(server);
		cl3.setScope("3");
		cl3.setUserId(3L);
		cl3.setUid("public3");
		clientDao.add(cl3);

		List<StreamClient> clTest_Pub_1_list = clientDao.getClientsByUidAndServer(null, "public1");
		assertEquals(cl1.getId(), clTest_Pub_1_list.get(0).getId());

		List<StreamClient> clTest_Pub_3_list = clientDao.getClientsByUidAndServer(server, "public3");
		assertEquals(cl3.getId(), clTest_Pub_3_list.get(0).getId());

		List<StreamClient> clTest_Fail_list = clientDao.getClientsByUidAndServer(null, "public3");
		assertEquals(0, clTest_Fail_list.size());

		List<StreamClient> clTest_PubAll_1_list = clientDao.getClientsByUid("public1");
		assertEquals(cl1.getId(), clTest_PubAll_1_list.get(0).getId());

		List<StreamClient> clTest_PubAll_3_list = clientDao.getClientsByUid("public3");
		assertEquals(cl3.getId(), clTest_PubAll_3_list.get(0).getId());

		List<StreamClient> clTest_FailAll_list = clientDao.getClientsByUid("public4");
		assertEquals(0, clTest_FailAll_list.size());

		List<StreamClient> clientsByServerNull = clientDao.getClientsByServer(null);
		assertEquals(2, clientsByServerNull.size());

		List<StreamClient> clientsByServer = clientDao.getClientsByServer(server);
		assertEquals(1, clientsByServer.size());

		List<StreamClient> clientsAll = clientDao.getClients();
		assertEquals(3, clientsAll.size());

		//by userid
		List<StreamClient> clTest_User_1_list = clientDao.getClientsByUserId(null, 1L);
		assertEquals(cl1.getId(), clTest_User_1_list.get(0).getId());

		List<StreamClient> clTest_User_3_list = clientDao.getClientsByUserId(server, 3L);
		assertEquals(cl3.getId(), clTest_User_3_list.get(0).getId());

		List<StreamClient> clTest_UserFail_list = clientDao.getClientsByUserId(null, 3L);
		assertEquals(0, clTest_UserFail_list.size());

		//by roomid
		List<StreamClient> clTest_Room_1_list = clientDao.getClientsByRoomId(1L);
		assertEquals(2, clTest_Room_1_list.size());

		List<StreamClient> clTest_Room_3_list = clientDao.getClientsByRoomId(3L);
		assertEquals(cl3.getId(), clTest_Room_3_list.get(0).getId());

		List<StreamClient> clTest_RoomFail_list = clientDao.getClientsByRoomId(2L);
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

		clTest_Count_Delete_list = clientDao.countClientsByServer(server);
		assertEquals(0, clTest_Count_Delete_list);

		//delete all
		clientDao.cleanAllClients();

		countAll = clientDao.countClients();
		assertEquals(0, countAll);
	}
}
