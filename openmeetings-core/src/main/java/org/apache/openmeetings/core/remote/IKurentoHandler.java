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

import java.util.Collection;
import java.util.Map;

import org.apache.openmeetings.core.sip.SipManager;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.room.Room;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public interface IKurentoHandler {

	void init();

	boolean isConnected();

	void sendShareUpdated(StreamDesc sd);

	void destroy();

	void onMessage(IWsClient inClient, JSONObject msg);

	JSONObject getRecordingUser(Long roomId);

	void leaveRoom(Client c);

	void sendClient(String sid, JSONObject msg);

	void sendError(IWsClient c, String msg);

	void remove(IWsClient c);

	MediaPipeline createPipiline(Map<String, String> tags, Continuation<Void> continuation);

	KRoom getRoom(Long roomId);

	Collection<KRoom> getRooms();

	void updateSipCount(Room r, long count);

	JSONObject newKurentoMsg();

	JSONArray getTurnServers(Client c);

	JSONArray getTurnServers(Client c, final boolean test);

	TestStreamProcessor getTestProcessor();

	IStreamProcessor getStreamProcessor();

	SipManager getSipManager();

	RecordingChunkDao getChunkDao();

	int getFlowoutTimeout();

}
