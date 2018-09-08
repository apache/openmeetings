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

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.KurentoClient;
import org.kurento.client.ObjectCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.openjson.JSONObject;

public class KurentoHandler {
	private final static Logger log = LoggerFactory.getLogger(KurentoHandler.class);
	public final static String KURENTO_TYPE = "kurento";
	private String kurentoWsUrl;
	private KurentoClient client;
	private final Map<Long, KRoom> rooms = new ConcurrentHashMap<>();
	final Map<String, KStream> usersByUid = new ConcurrentHashMap<>();
	final Map<String, KTestStream> testsByUid = new ConcurrentHashMap<>();

	@Autowired
	private IClientManager cm;

	public void setKurentoWsUrl(String kurentoWsUrl) {
		this.kurentoWsUrl = kurentoWsUrl;
	}

	public void init() {
		try {
			// TODO check connection, reconnect, listeners etc.
			client = KurentoClient.create(kurentoWsUrl);
			client.getServerManager().addObjectCreatedListener(new EventListener<ObjectCreatedEvent>() {
				@Override
				public void onEvent(ObjectCreatedEvent evt) {
					log.warn("Kurento::ObjectCreated -> {}", evt);
				}
			});
		} catch (Exception e) {
			log.error("Fail to create Kurento client", e);
		}
	}

	public void destroy() {
		if (client != null) {
			client.destroy();
		}
	}

	public void onMessage(IWsClient _c, JSONObject msg) {
		if (client == null) {
			sendError(_c, "Multimedia server is inaccessible");
			return;
		}
		final String cmdId = msg.getString("id");
		if ("test".equals(msg.optString("mode"))) {
			KTestStream user = getTestByUid(_c.getUid());
			switch (cmdId) {
				case "start":
				{
					//TODO FIXME assert null user ???
					user = new KTestStream(_c, msg, client.createMediaPipeline());
					testsByUid.put(_c.getUid(), user);
				}
					break;
				case "iceCandidate":
				{
					JSONObject candidate = msg.getJSONObject("candidate");

					if (user != null) {
						IceCandidate cand = new IceCandidate(candidate.getString("candidate"),
								candidate.getString("sdpMid"), candidate.getInt("sdpMLineIndex"));
						user.addCandidate(cand);
					}
				}
					break;
				case "play":
					user.play(_c, msg, client.createMediaPipeline());
					break;
			}
		} else {
			final Client c = (Client)_c;

			if (c != null) {
				log.debug("Incoming message from user with ID '{}': {}", c.getUserId(), msg);
			} else {
				log.debug("Incoming message from new user: {}", msg);
			}
			KStream user = getByUid(_c.getUid());
			switch (cmdId) {
				case "toggleActivity":
					toggleActivity(c, Client.Activity.valueOf(msg.getString("activity")));
					break;
				case "receiveVideoFrom":
					final String senderUid = msg.getString("sender");
					final KStream sender = getByUid(senderUid);
					final String sdpOffer = msg.getString("sdpOffer");
					if (user == null) {
						return;
					}
					user.receiveVideoFrom(this, c, sender, sdpOffer);
					break;
				case "onIceCandidate":
				{
					JSONObject candidate = msg.getJSONObject("candidate");
					if (user == null) {
						return;
					}
					IceCandidate cand = new IceCandidate(candidate.getString("candidate"),
							candidate.getString("sdpMid"), candidate.getInt("sdpMLineIndex"));
					user.addCandidate(cand, msg.getString("uid"));
				}
					break;
			}
		}
	}

	private static boolean isBroadcasting(final Client c) {
		return c.hasAnyActivity(Client.Activity.broadcastA, Client.Activity.broadcastV);
	}

	public void toggleActivity(Client c, Client.Activity a) {
		log.info("PARTICIPANT {}: trying to toggle activity {}", c, c.getRoomId());

		if (!activityAllowed(c, a, c.getRoom())) {
			if (a == Client.Activity.broadcastA || a == Client.Activity.broadcastAV) {
				c.allow(Room.Right.audio);
			}
			if (!c.getRoom().isAudioOnly() && (a == Client.Activity.broadcastV || a == Client.Activity.broadcastAV)) {
				c.allow(Room.Right.video);
			}
		}
		if (activityAllowed(c, a, c.getRoom())) {
			boolean broadcasting = isBroadcasting(c);
			if (a == Client.Activity.broadcastA && !c.isMicEnabled()) {
				return;
			}
			if (a == Client.Activity.broadcastV && !c.isCamEnabled()) {
				return;
			}
			if (a == Client.Activity.broadcastAV && !c.isMicEnabled() && !c.isCamEnabled()) {
				return;
			}
			c.toggle(a);
			if (!isBroadcasting(c)) {
				//close
				leaveRoom(cm.update(c.removeStream(c.getUid())));
				WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
				WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.closeStream, c.getUid()));
			} else if (!broadcasting) {
				//join
				KRoom room = getRoom(c.getRoomId());
				StreamDesc sd = new StreamDesc(c.getSid(), c.getUid(), StreamDesc.Type.broadcast);
				sd.setWidth(c.getWidth());
				sd.setHeight(c.getHeight());
				cm.update(c.addStream(sd));
				room.join(this, c, sd);
			} else {
				//change constraints
				//FIXME TODO WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
			}
		}
	}

	public void leaveRoom(Client c) {
		remove(c);
		WebSocketHelper.sendClient(c, newKurentoMsg()
				.put("id", "broadcastStopped")
				.put("uid", c.getUid()));
	}

	public void sendClient(String sid, JSONObject msg) {
		WebSocketHelper.sendClient(cm.getBySid(sid), msg);
	}

	public static void sendError(IWsClient c, String msg) {
		WebSocketHelper.sendClient(c, newKurentoMsg()
				.put("id", "error")
				.put("message", msg));
	}

	public void remove(IWsClient c) {
		if (c == null) {
			return;
		}
		final String uid = c.getUid();
		final boolean test = !(c instanceof Client);
		IKStream u = test ? getTestByUid(uid) : getByUid(uid);
		if (u != null) {
			u.release();
			if (test) {
				testsByUid.remove(uid);
			} else {
				usersByUid.remove(uid);
			}
		}
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

	private KStream getByUid(String uid) {
		return uid == null ? null : usersByUid.get(uid);
	}

	private KTestStream getTestByUid(String uid) {
		return uid == null ? null : testsByUid.get(uid);
	}

	static JSONObject newKurentoMsg() {
		return new JSONObject().put("type", KURENTO_TYPE);
	}

	static JSONObject newTestKurentoMsg() {
		return newKurentoMsg().put("mode", "test");
	}

	public static boolean activityAllowed(Client c, Client.Activity a, Room room) {
		boolean r = false;
		switch (a) {
			case broadcastA:
				r = c.hasRight(Right.audio);
				break;
			case broadcastV:
				r = !room.isAudioOnly() && c.hasRight(Right.video);
				break;
			case broadcastAV:
				r = !room.isAudioOnly() && c.hasRight(Right.audio) && c.hasRight(Right.video);
				break;
			default:
				break;
		}
		return r;
	}
}
