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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import org.kurento.client.MediaObject;
import org.kurento.client.MediaPipeline;
import org.kurento.client.ObjectCreatedEvent;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.Tag;
import org.kurento.client.Transaction;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.client.JsonRpcClientNettyWebSocket;
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
	private final ScheduledExecutorService kmsRecheckScheduler = Executors.newScheduledThreadPool(1);
	public static final String KURENTO_TYPE = "kurento";
	private static int FLOWOUT_TIMEOUT_SEC = 5;
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
	private final AtomicBoolean connected = new AtomicBoolean(false);
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
		boolean connctd = connected.get() && client != null && !client.isClosed();
		if (!connctd) {
			log.warn(WARN_NO_KURENTO);
		}
		return connctd;
	}

	@PostConstruct
	public void init() {
		check = () -> {
			try {
				if (client != null) {
					return;
				}
				log.debug("Reconnecting KMS");
				kuid = randomUUID().toString();
				client = KurentoClient.createFromJsonRpcClient(new JsonRpcClientNettyWebSocket(kurentoWsUrl) {
						{
							setTryReconnectingMaxTime(0);
						}

						private void notifyRooms(boolean connected) {
							WebSocketHelper.sendServer(new TextRoomMessage(null, new User(), RoomMessage.Type.KURENTO_STATUS, new JSONObject().put("connected", connected).toString()));
						}

						private void onDisconnect() {
							log.info("!!! Kurento disconnected");
							connected.set(false);
							notifyRooms(false);
							clean();
						}

						@Override
						protected void handleReconnectDisconnection(final int statusCode, final String closeReason) {
							if (!isClosedByUser()) {
								log.debug("{}JsonRpcWsClient disconnected from {} because {}.", label, uri, closeReason);
								onDisconnect();
							} else {
								super.handleReconnectDisconnection(statusCode, closeReason);
								onDisconnect();
							}
						}

						@Override
						protected void fireConnected() {
							log.info("!!! Kurento connected");
							connected.set(true);
							notifyRooms(true);
						}
					});
				client.getServerManager().addObjectCreatedListener(new KWatchDogCreate());
				client.getServerManager().addObjectDestroyedListener(event ->
					log.debug("Kurento::ObjectDestroyedEvent objectId {}, tags {}, source {}", event.getObjectId(), event.getTags(), event.getSource())
				);
			} catch (Exception e) {
				connected.set(false);
				clean();
				log.warn("Fail to create Kurento client, will re-try in {} ms", checkTimeout);
			}
		};
		kmsRecheckScheduler.scheduleAtFixedRate(check, 0L, checkTimeout, MILLISECONDS);
	}

	@PreDestroy
	public void destroy() {
		clean();
		kmsRecheckScheduler.shutdownNow();
	}

	private void clean() {
		if (client != null) {
			try {
				KurentoClient copy = client;
				client = null;
				if (copy != null && !copy.isClosed()) {
					log.debug("Client will destroyed ...");
					copy.destroy();
					log.debug(".... Client is destroyed");
				}
				testProcessor.destroy();
				streamProcessor.destroy();
				for (Entry<Long, KRoom> e : rooms.entrySet()) {
					e.getValue().close();
				}
				rooms.clear();
			} catch (Exception e) {
				log.error("Unexpected error while clean-up", e);
			}
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

	public void onMessage(IWsClient inClient, JSONObject msg) {
		if (!isConnected()) {
			sendError(inClient, "Multimedia server is inaccessible");
			return;
		}
		final String cmdId = msg.getString("id");
		if (MODE_TEST.equals(msg.optString(TAG_MODE))) {
			testProcessor.onMessage(inClient, cmdId, msg);
		} else {
			final Client c = (Client)inClient;

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
		if (!isConnected() || c == null) {
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
			room = new KRoom(streamProcessor, r, pipe, chunkDao);
			rooms.put(roomId, room);
		}
		log.debug("Room {} found!", roomId);
		return room;
	}

	public Collection<KRoom> getRooms() {
		return rooms.values();
	}

	static JSONObject newKurentoMsg() {
		return new JSONObject().put("type", KURENTO_TYPE);
	}

	public static boolean activityAllowed(Client c, Activity a, Room room) {
		boolean r = false;
		switch (a) {
			case AUDIO:
				r = c.hasRight(Right.AUDIO);
				break;
			case VIDEO:
				r = !room.isAudioOnly() && c.hasRight(Right.VIDEO);
				break;
			case AUDIO_VIDEO:
				r = !room.isAudioOnly() && c.hasRight(Right.AUDIO) && c.hasRight(Right.VIDEO);
				break;
			default:
				break;
		}
		return r;
	}

	public JSONArray getTurnServers(Client c) {
		return getTurnServers(c, false);
	}

	JSONArray getTurnServers(Client c, final boolean test) {
		JSONArray arr = new JSONArray();
		if (!Strings.isEmpty(turnUrl)) {
			try {
				JSONObject turn = new JSONObject();
				if ("rest".equalsIgnoreCase(turnMode)) {
					Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
					mac.init(new SecretKeySpec(turnSecret.getBytes(), HMAC_SHA1_ALGORITHM));
					StringBuilder user = new StringBuilder()
							.append((test ? 60 : turnTtl * 60) + System.currentTimeMillis() / 1000L);
					final String uid = c == null ? null : c.getUid();
					if (!Strings.isEmpty(uid)) {
						user.append(':').append(uid);
					} else if (!Strings.isEmpty(turnUser)) {
						user.append(':').append(turnUser);
					}
					turn.put("username", user)
						.put("credential", Base64.getEncoder().encodeToString(mac.doFinal(user.toString().getBytes())));
				} else {
					turn.put("username", turnUser)
						.put("credential", turnSecret);
				}

				JSONArray urls = new JSONArray();
				final String[] turnUrls = turnUrl.split(",");
				for (String url : turnUrls) {
					if (url.startsWith("stun:") || url.startsWith("stuns:") || url.startsWith("turn:") || url.startsWith("turns:")) {
						urls.put(url);
					} else {
						urls.put("turn:" + url);
					}
				}
				turn.put("urls", urls);

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

	public void setFlowoutTimeout(int timeout) {
		FLOWOUT_TIMEOUT_SEC = timeout;
	}

	static int getFlowoutTimeout() {
		return FLOWOUT_TIMEOUT_SEC;
	}

	private class KWatchDogCreate implements EventListener<ObjectCreatedEvent> {
		private ScheduledExecutorService scheduler;

		public KWatchDogCreate() {
			scheduler = Executors.newScheduledThreadPool(watchThreadCount);
		}

		@Override
		public void onEvent(ObjectCreatedEvent evt) {
			log.debug("Kurento::ObjectCreated -> {}, source {}", evt.getObject(), evt.getSource());
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
							r.close();
						}
					}
					log.warn("Invalid MediaPipeline {} detected, will be dropped, tags: {}", pipe.getId(), tags);
					pipe.release();
				}, objCheckTimeout, MILLISECONDS);
			} else if (evt.getObject() instanceof Endpoint) {
				// endpoint created
				Endpoint curPoint = (Endpoint)evt.getObject();
				final String eoid = curPoint.getId();
				Class<? extends Endpoint> clazz = null;
				if (curPoint instanceof WebRtcEndpoint) {
					clazz = WebRtcEndpoint.class;
				} else if (curPoint instanceof RecorderEndpoint) {
					clazz = RecorderEndpoint.class;
				} else if (curPoint instanceof PlayerEndpoint) {
					clazz = PlayerEndpoint.class;
				}
				final Class<? extends Endpoint> fClazz = clazz;
				scheduler.schedule(() -> {
					if (client == null || fClazz == null) {
						return;
					}
					// still alive
					Endpoint point = client.getById(eoid, fClazz);
					if (validTestPipeline(point.getMediaPipeline())) {
						return;
					}
					Map<String, String> tags = tagsAsMap(point);
					KStream stream = streamProcessor.getByUid(tags.get("outUid"));
					log.debug("New Endpoint {} detected, tags: {}, kStream: {}", point.getId(), tags, stream);
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
