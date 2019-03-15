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

import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.directory.api.util.Strings;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.kurento.client.Endpoint;
import org.kurento.client.EventListener;
import org.kurento.client.KurentoClient;
import org.kurento.client.KurentoConnectionListener;
import org.kurento.client.MediaObject;
import org.kurento.client.MediaPipeline;
import org.kurento.client.ObjectCreatedEvent;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.Tag;
import org.kurento.client.Transaction;
import org.kurento.client.WebRtcEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class KurentoHandler {
	private static final Logger log = LoggerFactory.getLogger(KurentoHandler.class);
	public static final String PARAM_ICE = "iceServers";
	public static final String PARAM_CANDIDATE = "candidate";
	private static final String WARN_NO_KURENTO = "Media Server is not accessible";
	public static final String MODE_TEST = "test";
	public static final String TAG_KUID = "kuid";
	public static final String TAG_MODE = "mode";
	public static final String TAG_ROOM = "roomId";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private final ScheduledExecutorService recheckScheduler = Executors.newScheduledThreadPool(1);
	public static final String KURENTO_TYPE = "kurento";
	private long checkTimeout = 120000; //ms
	private long objCheckTimeout = 200; //ms
	private int watchThreadCount = 10;
	private String kurentoWsUrl;
	private String turnUrl;
	private String turnUser;
	private String turnSecret;
	private String turnMode;
	private int turnTtl = 60; //minutes
	private KurentoClient client;
	private boolean connected = false;
	private String kuid;
	private final Map<Long, KRoom> rooms = new ConcurrentHashMap<>();
	private Runnable check;

	@Autowired
	private IClientManager cm;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RecordingChunkDao chunkDao;
	@Autowired
	private TestStreamProcessor testProcessor;
	@Autowired
	private StreamProcessor streamProcessor;

	boolean isConnected() {
		boolean connctd = client != null && !client.isClosed() && connected;
		if (!connctd) {
			log.warn(WARN_NO_KURENTO);
		}
		return connctd;
	}

	public void init() {
		check = () -> {
			try {
				kuid = randomUUID().toString();
				client = KurentoClient.create(kurentoWsUrl, new KConnectionListener(kuid));
				client.getServerManager().addObjectCreatedListener(new KWatchDog());
			} catch (Exception e) {
				log.warn("Fail to create Kurento client, will re-try in {} ms", checkTimeout);
				recheckScheduler.schedule(check, checkTimeout, MILLISECONDS);
			}
		};
		check.run();
	}

	public void destroy() {
		if (client != null) {
			kuid = randomUUID().toString(); // will be changed to prevent double events
			client.destroy();
			for (Entry<Long, KRoom> e : rooms.entrySet()) {
				e.getValue().close(streamProcessor);
			}
			testProcessor.destroy();
			streamProcessor.destroy();
			rooms.clear();
			client = null;
		}
	}

	private static Map<String, String> tagsAsMap(MediaObject pipe) {
		Map<String, String> map = new HashMap<>();
		for (Tag t : pipe.getTags()) {
			map.put(t.getKey(), t.getValue());
		}
		return map;
	}

	Transaction beginTransaction() {
		return client.beginTransaction();
	}

	public void onMessage(IWsClient _c, JSONObject msg) {
		if (!isConnected()) {
			sendError(_c, "Multimedia server is inaccessible");
			return;
		}
		final String cmdId = msg.getString("id");
		if (MODE_TEST.equals(msg.optString(TAG_MODE))) {
			testProcessor.onMessage(_c, cmdId, msg);
		} else {
			final Client c = (Client)_c;

			if (c == null || c.getRoomId() == null) {
				log.warn("Incoming message from invalid user");
				return;
			}
			streamProcessor.onMessage(c, cmdId, msg);
		}
	}

	public JSONObject getRecordingUser(Long roomId) {
		if (!isConnected()) {
			return new JSONObject();
		}
		return getRoom(roomId).getRecordingUser();
	}

	public void leaveRoom(Client c) {
		remove(c);
		WebSocketHelper.sendAll(newKurentoMsg()
				.put("id", "clientLeave")
				.put("uid", c.getUid())
				.toString()
			);
	}

	void sendShareUpdated(StreamDesc sd) {
		sendClient(sd.getSid(), newKurentoMsg()
				.put("id", "shareUpdated")
				.put("stream", sd.toJson())
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

	public void remove(IWsClient c) {
		if (!isConnected() ||c == null) {
			return;
		}
		if (!(c instanceof Client)) {
			testProcessor.remove(c);
			return;
		}
		streamProcessor.remove((Client)c);
	}

	KRoom getRoom(Long roomId) {
		log.debug("Searching for room {}", roomId);
		KRoom room = rooms.get(roomId);

		if (room == null) {
			log.debug("Room {} does not exist. Will create now!", roomId);
			Room r = roomDao.get(roomId);
			Transaction t = beginTransaction();
			MediaPipeline pipe = client.createMediaPipeline(t);
			pipe.addTag(t, TAG_KUID, kuid);
			pipe.addTag(t, TAG_ROOM, String.valueOf(roomId));
			t.commit();
			room = new KRoom(r, pipe, chunkDao);
			rooms.put(roomId, room);
		}
		log.debug("Room {} found!", roomId);
		return room;
	}

	static JSONObject newKurentoMsg() {
		return new JSONObject().put("type", KURENTO_TYPE);
	}

	public static boolean activityAllowed(Client c, Activity a, Room room) {
		boolean r = false;
		switch (a) {
			case AUDIO:
				r = c.hasRight(Right.audio);
				break;
			case VIDEO:
				r = !room.isAudioOnly() && c.hasRight(Right.video);
				break;
			case AUDIO_VIDEO:
				r = !room.isAudioOnly() && c.hasRight(Right.audio) && c.hasRight(Right.video);
				break;
			default:
				break;
		}
		return r;
	}

	public JSONArray getTurnServers() {
		return getTurnServers(false);
	}

	JSONArray getTurnServers(final boolean test) {
		JSONArray arr = new JSONArray();
		if (!Strings.isEmpty(turnUrl)) {
			try {
				JSONObject turn = new JSONObject();
				if ("rest".equalsIgnoreCase(turnMode)) {
					Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
					mac.init(new SecretKeySpec(turnSecret.getBytes(), HMAC_SHA1_ALGORITHM));
					StringBuilder user = new StringBuilder()
							.append((test ? 60 : turnTtl * 60) + System.currentTimeMillis() / 1000L);
					if (!Strings.isEmpty(turnUser)) {
						user.append(':').append(turnUser);
					}
					turn.put("username", user)
						.put("credential", Base64.getEncoder().encodeToString(mac.doFinal(user.toString().getBytes())));
				} else {
					turn.put("username", turnUser)
						.put("credential", turnSecret);
				}
				final String fturnUrl = "turn:" + turnUrl;
				turn.put("url", fturnUrl); // old-school
				turn.put("urls", fturnUrl);
				arr.put(turn);
			} catch (NoSuchAlgorithmException|InvalidKeyException e) {
				log.error("Unexpected error while creating turn", e);
			}
		}
		return arr;
	}

	KurentoClient getClient() {
		return client;
	}

	String getKuid() {
		return kuid;
	}

	public void setCheckTimeout(long checkTimeout) {
		this.checkTimeout = checkTimeout;
	}

	public void setObjCheckTimeout(long objCheckTimeout) {
		this.objCheckTimeout = objCheckTimeout;
	}

	public void setWatchThreadCount(int watchThreadCount) {
		this.watchThreadCount = watchThreadCount;
	}

	public void setKurentoWsUrl(String kurentoWsUrl) {
		this.kurentoWsUrl = kurentoWsUrl;
	}

	public void setTurnUrl(String turnUrl) {
		this.turnUrl = turnUrl;
	}

	public void setTurnUser(String turnUser) {
		this.turnUser = turnUser;
	}

	public void setTurnSecret(String turnSecret) {
		this.turnSecret = turnSecret;
	}

	public void setTurnMode(String turnMode) {
		this.turnMode = turnMode;
	}

	public void setTurnTtl(int turnTtl) {
		this.turnTtl = turnTtl;
	}

	private class KConnectionListener implements KurentoConnectionListener {
		final String lkuid;

		private KConnectionListener(final String lkuid) {
			this.lkuid = lkuid;
		}

		private void notifyRooms() {
			WebSocketHelper.sendServer(new TextRoomMessage(null, new User(), RoomMessage.Type.kurentoStatus, new JSONObject().put("connected", isConnected()).toString()));
		}

		@Override
		public void reconnected(boolean sameServer) {
			log.error("Kurento reconnected ? {}, this shouldn't happen", sameServer);
		}

		@Override
		public void disconnected() {
			if (lkuid.equals(kuid)) {
				log.warn("Disconnected, will re-try in {} ms", checkTimeout);
				connected = false;
				notifyRooms();
				destroy();
				recheckScheduler.schedule(check, checkTimeout, MILLISECONDS);
			}
		}

		@Override
		public void connectionFailed() {
			// this handled seems to be called multiple times
		}

		@Override
		public void connected() {
			log.info("Kurento connected");
			connected = true;
			notifyRooms();
		}
	}

	private class KWatchDog implements EventListener<ObjectCreatedEvent> {
		private ScheduledExecutorService scheduler;

		public KWatchDog() {
			scheduler = Executors.newScheduledThreadPool(watchThreadCount);
		}

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
					Map<String, String> tags = tagsAsMap(pipe);
					if (validTestPipeline(tags)) {
						return;
					}
					if (kuid.equals(tags.get(TAG_KUID))) {
						KRoom r = rooms.get(Long.valueOf(tags.get(TAG_ROOM)));
						if (r.getPipeline().getId().equals(pipe.getId())) {
							return;
						} else if (r != null) {
							rooms.remove(r.getRoomId());
							r.close(streamProcessor);
						}
					}
					log.warn("Invalid MediaPipeline {} detected, will be dropped, tags: {}", pipe.getId(), tags);
					pipe.release();
				}, objCheckTimeout, MILLISECONDS);
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
					KStream stream = streamProcessor.getByUid(tags.get("outUid"));
					if (stream != null && stream.contains(tags.get("uid"))) {
						return;
					}
					log.warn("Invalid Endpoint {} detected, will be dropped, tags: {}", point.getId(), tags);
					point.release();
				}, objCheckTimeout, MILLISECONDS);
			}
		}

		private boolean validTestPipeline(MediaPipeline pipeline) {
			return validTestPipeline(tagsAsMap(pipeline));
		}

		private boolean validTestPipeline(Map<String, String> tags) {
			return kuid.equals(tags.get(TAG_KUID)) && MODE_TEST.equals(tags.get(TAG_MODE)) && MODE_TEST.equals(tags.get(TAG_ROOM));
		}
	}
}
