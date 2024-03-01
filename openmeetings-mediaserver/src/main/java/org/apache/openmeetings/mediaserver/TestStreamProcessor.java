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
package org.apache.openmeetings.mediaserver;

import static org.apache.openmeetings.mediaserver.KurentoHandler.MODE_TEST;
import static org.apache.openmeetings.mediaserver.KurentoHandler.PARAM_CANDIDATE;
import static org.apache.openmeetings.mediaserver.KurentoHandler.PARAM_ICE;
import static org.apache.openmeetings.mediaserver.KurentoHandler.TAG_MODE;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.kurento.client.IceCandidate;

import com.github.openjson.JSONObject;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named
class TestStreamProcessor {
	private final Map<String, KTestStream> streamByUid = new ConcurrentHashMap<>();

	@Inject
	private KurentoHandler kHandler;

	void onMessage(IWsClient c, final String cmdId, JSONObject msg) {
		KTestStream user = getByUid(c.getUid());
		switch (cmdId) {
			case "wannaRecord":
				WebSocketHelper.sendClient(c, newTestKurentoMsg()
						.put("id", "canRecord")
						.put(PARAM_ICE, kHandler.getTurnServers(null, true))
						);
				break;
			case "record":
				if (user != null) {
					user.release();
				}
				user = new KTestStream(c, msg);
				streamByUid.put(c.getUid(), user);
				break;
			case "iceCandidate":
				JSONObject candidate = msg.getJSONObject(PARAM_CANDIDATE);
				if (user != null) {
					IceCandidate cand = new IceCandidate(candidate.getString(PARAM_CANDIDATE),
							candidate.getString("sdpMid"), candidate.getInt("sdpMLineIndex"));
					user.addCandidate(cand);
				}
				break;
			case "wannaPlay":
				WebSocketHelper.sendClient(c, newTestKurentoMsg()
						.put("id", "canPlay")
						.put(PARAM_ICE, kHandler.getTurnServers(null, true))
						);
				break;
			case "play":
				if (user != null) {
					user.play(c, msg);
				}
				break;
			default:
				//no-op
				break;
		}
	}

	private KTestStream getByUid(String uid) {
		return uid == null ? null : streamByUid.get(uid);
	}

	static JSONObject newTestKurentoMsg() {
		return KurentoHandler.newKurentoMsg().put(TAG_MODE, MODE_TEST);
	}

	void remove(IWsClient c) {
		AbstractStream s = getByUid(c.getUid());
		if (s != null) {
			s.release();
		}
	}

	public void release(AbstractStream stream) {
		streamByUid.remove(stream.getUid());
	}

	public void destroy() {
		for (Entry<String, KTestStream> e : streamByUid.entrySet()) {
			e.getValue().release();
			streamByUid.remove(e.getKey());
		}
		streamByUid.clear();
	}
}
