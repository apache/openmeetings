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

import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.core.session.SessionManager;
import org.apache.openmeetings.db.dto.server.ClientSessionInfo;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.web.app.Application;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class RoomBroadcaster {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomBroadcaster.class, webAppRootKey);

	public static Client getClient(String publicSid) {
		ClientSessionInfo csi = getBean(SessionManager.class).getClientByPublicSIDAnyServer(publicSid);
		return csi == null ? null : csi.getRcl();
	}

	public static void broadcast(String publicSid, String method, Object obj) {
		Client rc = getClient(publicSid);
		if (rc == null) {
			return;
		}
		broadcast(rc.getRoomId(), method, obj);
	}

	public static void broadcast(Long roomId, String method, Object obj) {
		ScopeApplicationAdapter sa = getBean(ScopeApplicationAdapter.class);
		sa.sendToScope(roomId, method, obj);
	}

	public static void sendUpdatedClient(org.apache.openmeetings.db.entity.basic.Client client) {
		org.apache.openmeetings.db.entity.room.Client rcl = Application.get().updateClient(getClient(client.getUid()), true);
		log.debug("-----------  sendUpdatedClient ");

		if (rcl == null) {
			return;
		}

		// Put the mod-flag to true for this client
		getBean(SessionManager.class).updateClientByStreamId(rcl.getStreamid(), rcl, false, null);
		// Notify all clients of the same scope (room)
		broadcast(client.getRoomId(), "clientUpdated", rcl);
	}
}
