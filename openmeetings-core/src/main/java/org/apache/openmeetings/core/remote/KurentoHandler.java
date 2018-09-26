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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.kurento.client.Endpoint;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaObject;
import org.kurento.client.MediaPipeline;
import org.kurento.client.ObjectCreatedEvent;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.Tag;
import org.kurento.client.WebRtcEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.openjson.JSONObject;

public class KurentoHandler {
	private final static Logger log = LoggerFactory.getLogger(KurentoHandler.class);
	private final static String MODE_TEST = "test";
	private final static String TAG_KUID = "kuid";
	private final static String TAG_MODE = "mode";
	private final static String TAG_ROOM = "roomId";
	public final static String KURENTO_TYPE = "kurento";
	private long checkTimeout = 200; //ms
	private String kurentoWsUrl;
	private KurentoClient client;
	private String kuid;
	private ScheduledExecutorService scheduler;
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
					log.debug("Kurento::ObjectCreated -> {}", evt.getObject());
					if (evt.getObject() instanceof MediaPipeline) {
						// room created
						final String roid = evt.getObject().getId();
						scheduler.schedule(() -> {
							if (client == null) {
								return;
							}
							// still alive
							MediaPipeline pipe = client.getById(roid, MediaPipeline.class);
							try {
								Map<String, String> tags = tagsAsMap(pipe);
								if (validTestPipeline(tags)) {
									return;
								}
								if (kuid.equals(tags.get(TAG_KUID))) {
									KRoom r = rooms.get(Long.valueOf(tags.get(TAG_ROOM)));
									if (r.getPipelineId().equals(pipe.getId())) {
										return;
									} else if (r != null) {
										rooms.remove(r.getRoomId());
										r.close();
									}
								}
							} catch(Exception e) {
								//no-op, connect will be dropped
							}
							log.warn("Invalid MediaPipeline {} detected, will be dropped", pipe.getId());
							pipe.release();
						}, checkTimeout, MILLISECONDS);
					} else if (evt.getObject() instanceof Endpoint) {
						// endpoint created
						Endpoint _point = (Endpoint)evt.getObject();
						final String eoid = _point.getId();
						Class<? extends Endpoint> _clazz = null;
						if (_point instanceof WebRtcEndpoint) {
							_clazz = WebRtcEndpoint.class;
						} else if (_point instanceof RecorderEndpoint) {
							_clazz = RecorderEndpoint.class;
						} else if (_point instanceof PlayerEndpoint) {
							_clazz = PlayerEndpoint.class;
						}
						final Class<? extends Endpoint> clazz = _clazz;
						scheduler.schedule(() -> {
							if (client == null || clazz == null) {
								return;
							}
							// still alive
							Endpoint point = client.getById(eoid, clazz);
							if (validTestPipeline(point.getMediaPipeline())) {
								return;
							}
							Map<String, String> tags = tagsAsMap(point);
							KStream stream = getByUid(tags.get("suid"));
							if (stream != null && stream.contains(tags.get("uid"))) {
								return;
							}
							log.warn("Invalid Endpoint {} detected, will be dropped", point.getId());
							point.release();
						}, checkTimeout, MILLISECONDS);
					}
				}
			});
			kuid = UUID.randomUUID().toString(); //FIXME TODO regenerate on re-connect
			scheduler = Executors.newScheduledThreadPool(10);
		} catch (Exception e) {
			log.error("Fail to create Kurento client", e);
		}
	}

	public void destroy() {
		if (client != null) {
			for (Entry<Long, KRoom> e : rooms.entrySet()) {
				e.getValue().close();
			}
			rooms.clear();
			for (Entry<String, KTestStream> e : testsByUid.entrySet()) {
				e.getValue().release();
			}
			testsByUid.clear();
			usersByUid.clear();
			client.destroy();
		}
	}

	private static Map<String, String> tagsAsMap(MediaObject pipe) {
		Map<String, String> map = new HashMap<>();
		for (Tag t : pipe.getTags()) {
			map.put(t.getKey(), t.getValue());
		}
		return map;
	}

	private boolean validTestPipeline(MediaPipeline pipeline) {
		return validTestPipeline(tagsAsMap(pipeline));
	}

	private boolean validTestPipeline(Map<String, String> tags) {
		return kuid.equals(tags.get(TAG_KUID)) && MODE_TEST.equals(tags.get(TAG_MODE)) && MODE_TEST.equals(tags.get(TAG_MODE));
	}

	private MediaPipeline createTestPipeline() {
		MediaPipeline pipe = client.createMediaPipeline();
		pipe.addTag(TAG_KUID, kuid);
		pipe.addTag(TAG_MODE, MODE_TEST);
		pipe.addTag(TAG_ROOM, MODE_TEST);
		return pipe;
	}

	public void onMessage(IWsClient _c, JSONObject msg) {
		if (client == null) {
			sendError(_c, "Multimedia server is inaccessible");
			return;
		}
		final String cmdId = msg.getString("id");
		if (MODE_TEST.equals(msg.optString(TAG_MODE))) {
			KTestStream user = getTestByUid(_c.getUid());
			switch (cmdId) {
				case "start":
				{
					//TODO FIXME assert null user ???
					user = new KTestStream(_c, msg, createTestPipeline());
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
					user.play(_c, msg, createTestPipeline());
					break;
			}
		} else {
			final Client c = (Client)_c;

			if (c == null || c.getRoomId() == null) {
				log.warn("Incoming message from invalid user");
				return;
			}
			log.debug("Incoming message from user with ID '{}': {}", c.getUserId(), msg);
			KStream stream = getByUid(c.getUid());
			if (stream == null) {
				KRoom room = getRoom(c.getRoomId());
				stream = room.join(this, c);
			}
			//FIXME TODO check client rights here
			switch (cmdId) {
				case "toggleActivity":
					toggleActivity(c, Client.Activity.valueOf(msg.getString("activity")));
					break;
				case "broadcastStarted":
					stream.startBroadcast(this, c, msg.getString("sdpOffer"));
					break;
				case "onIceCandidate":
				{
					JSONObject candidate = msg.getJSONObject("candidate");
					IceCandidate cand = new IceCandidate(
							candidate.getString("candidate")
							, candidate.getString("sdpMid")
							, candidate.getInt("sdpMLineIndex"));
					stream.addCandidate(cand, msg.getString("uid"));
				}
					break;
				case "receiveVideo":
					stream.videoResponse(this, getByUid(msg.getString("sender")), msg.getString("sdpOffer"));
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
				//FIXME TODO update interview buttons
			} else if (!broadcasting) {
				//join
				StreamDesc sd = new StreamDesc(c.getSid(), c.getUid(), StreamDesc.Type.broadcast);
				sd.setWidth(c.getWidth());
				sd.setHeight(c.getHeight());
				cm.update(c.addStream(sd));
				log.debug("User {}: has started broadcast", sd.getUid());
				sendClient(sd.getSid(), newKurentoMsg()
						.put("id", "broadcast")
						.put("uid", sd.getUid())
						.put("stream", new JSONObject(sd))
						.put("client", c.toJson(true)));
				//FIXME TODO update interview buttons
			} else {
				//change constraints
				//FIXME TODO WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
			}
		}
	}

	public void leaveRoom(Client c) {
		remove(c);
		WebSocketHelper.sendAll(newKurentoMsg()
				.put("id", "broadcastStopped")
				.put("uid", c.getUid())
				.toString()
			);
	}

	public void sendClient(String sid, JSONObject msg) {
		WebSocketHelper.sendClient(cm.getBySid(sid), msg);
	}

	public static void sendError(IWsClient c, String msg) {
		WebSocketHelper.sendClient(c, newKurentoMsg()
				.put("id", "error")
				.put("message", msg));
	}

	public void remove(IWsClient _c) {
		if (_c == null) {
			return;
		}
		final String uid = _c.getUid();
		final boolean test = !(_c instanceof Client);
		IKStream u = test ? getTestByUid(uid) : getByUid(uid);
		if (u != null) {
			u.release();
			if (test) {
				testsByUid.remove(uid);
			} else {
				Client c = (Client)_c;
				KRoom room = rooms.get(c.getRoomId());
				if (room != null) {
					room.leave(c);
				}
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
			MediaPipeline pipe = client.createMediaPipeline();
			pipe.addTag(TAG_KUID, kuid);
			pipe.addTag(TAG_ROOM, String.valueOf(roomId));
			room = new KRoom(roomId, pipe);
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
		return newKurentoMsg().put(TAG_MODE, MODE_TEST);
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

	public void setCheckTimeout(long checkTimeout) {
		this.checkTimeout = checkTimeout;
	}
}
