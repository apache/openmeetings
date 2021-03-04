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
package org.apache.openmeetings.core.remote;

import static org.apache.openmeetings.core.remote.KurentoHandler.MODE_TEST;
import static org.apache.openmeetings.core.remote.KurentoHandler.PARAM_CANDIDATE;
import static org.apache.openmeetings.core.remote.KurentoHandler.PARAM_ICE;
import static org.apache.openmeetings.core.remote.KurentoHandler.TAG_MODE;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.record.Recording;
import org.kurento.client.IceCandidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.openjson.JSONObject;

@Component
class TestStreamProcessor implements IStreamProcessor {
	private final Map<String, KTestStream> streamByUid = new ConcurrentHashMap<>();

	@Autowired
	private IKurentoHandler kHandler;

	void onMessage(IWsClient c, final String cmdId, JSONObject msg) {
		KTestStream user = getByUid1(c.getUid());
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
				user = new KTestStream(c, msg, kHandler);
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

	public KTestStream getByUid1(String uid) {
		return uid == null ? null : streamByUid.get(uid);
	}

	JSONObject newTestKurentoMsg() {
		return kHandler.newKurentoMsg().put(TAG_MODE, MODE_TEST);
	}

	void remove(IWsClient c) {
		AbstractStream s = getByUid1(c.getUid());
		if (s != null) {
			s.release();
		}
	}

	@Override
	public void release(AbstractStream stream, boolean releaseStream) {
		streamByUid.remove(stream.getUid());
	}

	@Override
	public void destroy() {
		for (Entry<String, KTestStream> e : streamByUid.entrySet()) {
			e.getValue().release();
			streamByUid.remove(e.getKey());
		}
		streamByUid.clear();
	}

	@Override
	public void toggleActivity(Client c, Activity a) {
		// TODO Auto-generated method stub
	}

	@Override
	public void rightsUpdated(Client c) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasRightsToShare(Client c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean screenShareAllowed(Client c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSharing(Long roomId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRightsToRecord(Client c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean recordingAllowed(Client c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startRecording(Client c) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stopRecording(Client c) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isRecording(Long roomId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<KStream> getStreams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasStream(String uid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startBroadcast(KStream stream, StreamDesc sd, String sdpOffer, Runnable then) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean startConvertion(Recording rec) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pauseSharing(Client c, String uid) {
		// TODO Auto-generated method stub
	}

	@Override
	public Client getBySid(String sid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addStream(KStream stream) {
		// TODO Auto-generated method stub
	}

	@Override
	public StreamDesc doStopSharing(String sid, String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<KStream> getByRoom(Long roomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KStream getByUid(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onMessage(Client c, String cmdId, JSONObject msg) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(Client c) {
		// TODO Auto-generated method stub
	}

	@Override
	public JSONObject newStoppedMsg(StreamDesc sd) {
		// TODO Auto-generated method stub
		return null;
	}
}
