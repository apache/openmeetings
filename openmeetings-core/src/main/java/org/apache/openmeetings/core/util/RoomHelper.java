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
package org.apache.openmeetings.core.util;

import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.StreamClient;

import com.github.openjson.JSONObject;

public class RoomHelper {

	public static JSONObject videoJson(Client c, boolean self, String sid, ISessionManager mgr, boolean share) {
		JSONObject json = c.toJson(self).put("sid", sid);
		if (share) {
			StreamClient sc = mgr.getClientByPublicSID(c.getUid(), null); //TODO check server

			json.put("screenShare", true)
				.put("uid", sc.getPublicSID()) // unique screen-sharing ID
				.put("broadcastId", sc.getBroadCastID())
				.put("width", sc.getVWidth())
				.put("height", sc.getVHeight());
		}
		return json;
	}
}
