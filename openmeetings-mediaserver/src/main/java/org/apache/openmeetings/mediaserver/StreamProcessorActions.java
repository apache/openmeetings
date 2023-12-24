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

import static org.apache.openmeetings.mediaserver.KurentoHandler.PARAM_CANDIDATE;
import static org.apache.openmeetings.mediaserver.KurentoHandler.sendError;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.ScreenStreamDesc;
import org.apache.openmeetings.db.entity.basic.StreamDesc;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.util.logging.TimedApplication;
import org.apache.wicket.util.string.Strings;
import org.kurento.client.IceCandidate;
import org.kurento.client.internal.server.KurentoServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named
public class StreamProcessorActions {
	private static final Logger log = LoggerFactory.getLogger(StreamProcessorActions.class);

	@Inject
	private IClientManager cm;
	@Inject
	private KurentoHandler kHandler;
	@Inject
	private StreamProcessor streamProcessor;

	@TimedApplication
	protected void addIceCandidate(JSONObject msg) {
		final String uid = msg.optString("uid");
		KStream sender;
		JSONObject candidate = msg.getJSONObject(PARAM_CANDIDATE);
		String candStr = candidate.getString(PARAM_CANDIDATE);
		sender = streamProcessor.getByUid(uid);
		if (sender != null) {
			if (!Strings.isEmpty(candStr)) {
				IceCandidate cand = new IceCandidate(
						candStr
						, candidate.getString("sdpMid")
						, candidate.getInt("sdpMLineIndex"));
				sender.addIceCandidate(cand, msg.getString("luid"));
			}
		} else {
			log.warn("addIceCandidate sender is empty, uid: {}, candStr: {} ", uid, candStr);
		}
	}

	@TimedApplication
	protected void addListener(Client c, JSONObject msg) {
		KStream sender = streamProcessor.getByUid(msg.getString("sender"));
		if (sender != null) {
			Client sendClient = cm.getBySid(sender.getSid());
			StreamDesc sd = sendClient.getStream(sender.getUid());
			if (sd == null) {
				return;
			}
			if (sd instanceof ScreenStreamDesc scr && scr.has(Activity.RECORD) && !scr.has(Activity.SCREEN)) {
				return;
			}
			sender.addListener(c.getSid(), c.getUid(), msg.getString("sdpOffer"));
		}
	}

	@TimedApplication
	protected void handleBroadcastRestarted(Client c, final String uid) {
		if (!kHandler.isConnected()) {
			return;
		}
		KStream sender = streamProcessor.getByUid(uid);
		if (sender != null) {
			sender.broadcastRestarted();
		}
	}

	@TimedApplication
	protected void handleBroadcastStarted(Client c, final String uid, JSONObject msg) {
		if (!kHandler.isConnected()) {
			return;
		}
		StreamDesc sd = c.getStream(uid);
		KStream sender = streamProcessor.getByUid(uid);
		try {
			if (sender == null) {
				KRoom room = kHandler.getRoom(c.getRoomId());
				sender = room.join(sd);
			}
			if (msg.has("width")) {
				sd.setWidth(msg.getInt("width")).setHeight(msg.getInt("height"));
				cm.update(c);
			}
			streamProcessor.startBroadcast(sender, sd, msg.getString("sdpOffer"), () -> {
				if (sd instanceof ScreenStreamDesc scr && scr.has(Activity.RECORD) && !streamProcessor.isRecording(c.getRoomId())) {
					streamProcessor.startRecording(c);
				}
			});
		} catch (KurentoServerException e) {
			if (sender != null) {
				sender.release();
			}
			WebSocketHelper.sendClient(c, StreamProcessor.newStoppedMsg(sd));
			sendError(c, "Failed to start broadcast: " + e.getMessage());
			log.error("Failed to start broadcast", e);
		}
	}
}
