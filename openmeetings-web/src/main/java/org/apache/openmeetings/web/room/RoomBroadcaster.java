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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.update;

import org.apache.openmeetings.core.remote.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.web.app.Application;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RoomBroadcaster {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomBroadcaster.class, webAppRootKey);

	public static StreamClient getClient(String publicSid) {
		return getBean(ISessionManager.class).get(publicSid);
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
		StreamClient rcl = Application.get().updateClient(getClient(uid), true);
		log.debug("-----------  sendUpdatedClient ");
		// Notify all clients of the same scope (room)
		update(client);
		broadcast(client.getRoomId(), "clientUpdated", rcl);

		if (rcl == null) {
			return;
		}

		// Put the mod-flag to true for this client
		getBean(ISessionManager.class).update(rcl);
	}
}
