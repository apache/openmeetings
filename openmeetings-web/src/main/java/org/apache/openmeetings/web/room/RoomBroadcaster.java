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

import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.server.ClientSessionInfo;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.core.session.SessionManager;
import org.red5.server.api.IConnection;

public class RoomBroadcaster {
	public static Client getClient(String publicSid) {
		ClientSessionInfo csi = getBean(SessionManager.class).getClientByPublicSIDAnyServer(publicSid);
		return csi == null ? null : csi.getRcl();
	}
	
	public static void broadcast(String publicSid, String method, Object obj) {
		Client rc = getClient(publicSid);
		final Long roomId = rc.getRoomId();
		final SessionManager sessionMgr = getBean(SessionManager.class);
		final UserDao userDao = getBean(UserDao.class);
		ScopeApplicationAdapter sa = getBean(ScopeApplicationAdapter.class);
		sa.new MessageSender(sa.getRoomScope("" + roomId), method, obj) {
			public boolean filter(IConnection conn) {
				Client rcl = sessionMgr.getClientByStreamId(conn.getClient().getId(), null);
				return rcl.isScreenClient()
						|| rcl.getRoomId() == null || !rcl.getRoomId().equals(roomId) || userDao.get(rcl.getUserId()) == null;
			}
		}.start();
	}
}
