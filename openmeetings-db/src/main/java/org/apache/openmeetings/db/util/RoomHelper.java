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
package org.apache.openmeetings.db.util;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.manager.IStreamClientManager;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class RoomHelper {
	private RoomHelper() {}

	public static JSONObject videoJson(Client c, boolean self, String sid, IStreamClientManager mgr, String uid) {
		StreamClient sc = mgr.get(uid);
		if (sc == null) {
			return new JSONObject();
		}
		JSONObject o = c.toJson(self)
				.put("sid", sid)
				.put("uid", sc.getUid())
				.put("broadcastId", sc.getBroadcastId())
				.put("width", sc.getWidth())
				.put("height", sc.getHeight())
				.put("type", sc.getType());
		return addScreenActivities(o, sc);
	}

	public static JSONObject addScreenActivities(JSONObject o, StreamClient sc) {
		JSONArray a = new JSONArray();
		if (Client.Type.sharing == sc.getType()) {
			if (sc.isSharingStarted()) {
				a.put("sharing");
			}
			if (sc.isRecordingStarted()) {
				a.put("recording");
			}
			if (sc.isPublishStarted()) {
				a.put("publish");
			}
		}
		return o.put("screenActivities", a);
	}
}
