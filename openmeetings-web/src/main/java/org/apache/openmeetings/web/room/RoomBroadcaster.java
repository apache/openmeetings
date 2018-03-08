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
package org.apache.openmeetings.web.room;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.core.remote.ScopeApplicationAdapter;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.app.StreamClientManager;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RoomBroadcaster {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomBroadcaster.class, getWebAppRootKey());

	private RoomBroadcaster() {}

	public static StreamClient getClient(String publicSid) {
		return getBean(StreamClientManager.class).get(publicSid);
	}

	public static void broadcast(String publicSid, String method, Object obj) {
		StreamClient rc = getClient(publicSid);
		if (rc == null) {
			return;
		}
		broadcast(rc.getRoomId(), method, obj);
	}

	public static void broadcast(Long roomId, String method, Object obj) {
		ScopeApplicationAdapter sa = getBean(ScopeApplicationAdapter.class);
		sa.sendToScope(roomId, method, obj);
	}

	public static void sendUpdatedClient(Client client) {
		String uid = client.getUid();
		StreamClient rcl = getBean(StreamClientManager.class).update(getClient(uid), true);
		log.debug("-----------  sendUpdatedClient ");
		// Notify all clients of the same scope (room)
		getBean(ClientManager.class).update(client);
		broadcast(client.getRoom().getId(), "clientUpdated", rcl);

		if (rcl == null) {
			return;
		}
		getBean(StreamClientManager.class).update(rcl);
	}
}
