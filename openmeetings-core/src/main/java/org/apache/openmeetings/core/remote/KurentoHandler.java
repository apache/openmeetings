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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.manager.IClientManager;
import org.kurento.client.IceCandidate;
import org.kurento.client.KurentoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.openjson.JSONObject;

@Component
public class KurentoHandler {
	private final static Logger log = LoggerFactory.getLogger(KurentoHandler.class);
	public final static String KURENTO_TYPE = "kurento";
	private KurentoClient client;
	private final Map<Long, KRoom> rooms = new ConcurrentHashMap<>();
	final Map<String, KUser> usersByUid = new ConcurrentHashMap<>();

	@Autowired
	private IClientManager clientManager;

	@PostConstruct
	private void init() {
		try {
			// TODO check connection, reconnect, listeners etc.
			client = KurentoClient.create();
		} catch (Exception e) {
			log.error("Fail to create Kurento client", e);
		}
	}

	@PreDestroy
	private void destroy() {
		if (client != null) {
			destroy();
		}
	}

	public void onMessage(String uid, JSONObject msg) {
		final Client c = clientManager.get(uid);

		if (c != null) {
			log.debug("Incoming message from user with ID '{}': {}", c.getUserId(), msg);
		} else {
			log.debug("Incoming message from new user: {}", msg);
		}
		KUser user = getByUid(uid);
		switch (msg.getString("id")) {
			case "joinRoom":
				joinRoom(c);
				break;
			case "receiveVideoFrom":
				final String senderUid = msg.getString("sender");
				final KUser sender = getByUid(senderUid);
				final String sdpOffer = msg.getString("sdpOffer");
				if (user == null) {
					KRoom room = getRoom(c.getRoomId());
					user = room.addUser(this, uid);
				}
				user.receiveVideoFrom(this, sender, sdpOffer);
				break;
			case "onIceCandidate":
				JSONObject candidate = msg.getJSONObject("candidate");

				if (user == null) {
					KRoom room = getRoom(c.getRoomId());
					user = room.addUser(this, uid);
				}
				IceCandidate cand = new IceCandidate(candidate.getString("candidate"),
						candidate.getString("sdpMid"), candidate.getInt("sdpMLineIndex"));
				user.addCandidate(cand, msg.getString("uid"));
				break;
			case "testStart":
				break;
			case "onTestIceCandidate":
				break;
			default:
				break;
		}
	}

	private void joinRoom(Client c) {
		log.info("PARTICIPANT {}: trying to join room {}", c, c.getRoomId());

		KRoom room = getRoom(c.getRoomId());
		clientManager.update(c.addStream(c.getUid()));
		room.join(this, c.getUid());
	}

	void sendClient(String uid, JSONObject msg) {
		WebSocketHelper.sendClient(clientManager.get(uid), msg);
	}

	/**
	 * Looks for a room in the active room list.
	 *
	 * @param roomId
	 *            the Id of the room
	 * @return the room if it was already created, or a new one if it is the first
	 *         time this room is accessed
	 */
	public KRoom getRoom(Long roomId) {
		log.debug("Searching for room {}", roomId);
		KRoom room = rooms.get(roomId);

		if (room == null) {
			log.debug("Room {} does not exist. Will create now!", roomId);
			room = new KRoom(roomId, client.createMediaPipeline());
			rooms.put(roomId, room);
		}
		log.debug("Room {} found!", roomId);
		return room;
	}

	/**
	 * Removes a room from the list of available rooms.
	 *
	 * @param room
	 *            the room to be removed
	 */
	public void removeRoom(KRoom room) {
		this.rooms.remove(room.getRoomId());
		room.close();
		log.info("Room {} removed and closed", room.getRoomId());
	}

	public KUser getByUid(String uid) {
		return uid == null ? null : usersByUid.get(uid);
	}

	public boolean exists(String name) {
		return usersByUid.keySet().contains(name);
	}

	static JSONObject newKurentoMsg() {
		return new JSONObject().put("type", KURENTO_TYPE);
	}
}
