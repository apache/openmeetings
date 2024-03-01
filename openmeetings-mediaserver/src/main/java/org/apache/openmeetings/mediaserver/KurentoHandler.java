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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.StreamDesc;
import org.apache.openmeetings.db.entity.basic.IWsClient;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.wicket.util.string.Strings;
import org.kurento.client.CertificateKeyType;
import org.kurento.client.Continuation;
import org.kurento.client.Endpoint;
import org.kurento.client.EventListener;
import org.kurento.client.KurentoClient;
import org.kurento.client.ListenerSubscription;
import org.kurento.client.MediaObject;
import org.kurento.client.MediaPipeline;
import org.kurento.client.ObjectCreatedEvent;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.RtpEndpoint;
import org.kurento.client.Tag;
import org.kurento.client.Transaction;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.client.JsonRpcClientNettyWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named
public class KurentoHandler {
	private static final Logger log = LoggerFactory.getLogger(KurentoHandler.class);
	public static final String PARAM_ICE = "iceServers";
	public static final String PARAM_CANDIDATE = "candidate";
	private static final String WARN_NO_KURENTO = "Media Server is not accessible";
	public static final String MODE_TEST = "test";
	public static final String TAG_KUID = "kuid";
	public static final String TAG_MODE = "mode";
	public static final String TAG_ROOM = "roomId";
	public static final String TAG_STREAM_UID = "streamUid";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private final ScheduledExecutorService kmsRecheckScheduler = Executors.newScheduledThreadPool(1);
	public static final String KURENTO_TYPE = "kurento";
	private static int flowoutTimeout = 5;
	@Value("${kurento.ws.url}")
	private String kurentoWsUrl;
	@Value("${kurento.turn.url}")
	private String turnUrl;
	@Value("${kurento.turn.user}")
	private String turnUser;
	@Value("${kurento.turn.secret}")
	private String turnSecret;
	@Value("${kurento.turn.mode}")
	private String turnMode;
	@Value("${kurento.turn.ttl}")
	private int turnTtl = 60; //minutes
	@Value("${kurento.check.timeout}")
	private long checkTimeout = 120000; //ms
	@Value("${kurento.object.check.timeout}")
	private long objCheckTimeout = 200; //ms
	@Value("${kurento.watch.thread.count}")
	private int watchThreadCount = 10;
	@Value("${kurento.kuid}")
	private String kuid;
	private CertificateKeyType certificateType;
	private KurentoClient client;
	private ListenerSubscription objectCreatedListener;
	private ListenerSubscription objectDestroyedListener;
	private final AtomicBoolean connected = new AtomicBoolean(false);
	private final Map<Long, KRoom> rooms = new ConcurrentHashMap<>();
	private final Set<String> ignoredKuids = new HashSet<>();

	@Inject
	private IClientManager cm;
	@Inject
	private RoomDao roomDao;
	@Inject
	private TestStreamProcessor testProcessor;
	@Inject
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
		log.trace("KurentoHandler::PostConstruct");
		Runnable check = () -> {
			try {
				if (client != null) {
					return;
				}
				log.debug("Reconnecting KMS");
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
				objectCreatedListener = client.getServerManager().addObjectCreatedListener(new KWatchDogCreate());
				objectDestroyedListener = client.getServerManager().addObjectDestroyedListener(event ->
					log.debug("Kurento::ObjectDestroyedEvent objectId {}, tags {}, source {}", event.getObjectId(), event.getTags(), event.getSource())
				);
			} catch (Exception e) {
				connected.set(false);
				clean();
				log.warn("Fail to create Kurento client, will re-try in {} ms", checkTimeout, e);
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
				if (!copy.isClosed()) {
					log.debug("Client will be destroyed ...");
					if (objectCreatedListener != null) {
						copy.getServerManager().removeObjectCreatedListener(objectCreatedListener);
						objectCreatedListener = null;
					}
					if (objectDestroyedListener != null) {
						copy.getServerManager().removeObjectDestroyedListener(objectDestroyedListener);
						objectDestroyedListener = null;
					}
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
		return pipe.getTags().stream()
				.collect(Collectors.toMap(Tag::getKey, Tag::getValue));
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

	MediaPipeline createPipiline(Map<String, String> tags, Continuation<Void> continuation) {
		Transaction t = beginTransaction();
		MediaPipeline pipe = client.createMediaPipeline(t);
		pipe.addTag(t, TAG_KUID, kuid);
		tags.forEach((key, value) -> pipe.addTag(t, key, value));
		t.commit(continuation);
		return pipe;
	}

	KRoom getRoom(Long roomId) {
		return rooms.computeIfAbsent(roomId, k -> {
			log.debug("Room {} does not exist. Will create now!", roomId);
			Room r = roomDao.get(roomId);
			return new KRoom(r);
		});
	}

	public Collection<KRoom> getRooms() {
		return rooms.values();
	}

	public void updateSipCount(Room r, long count) {
		getRoom(r.getId()).updateSipCount(count);
	}

	static JSONObject newKurentoMsg() {
		return new JSONObject().put("type", KURENTO_TYPE);
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

	@Value("${kurento.certificateType}")
	public void setCertificateType(String certificateType) {
		if (certificateType.isEmpty()) {
			return;
		}
		this.certificateType = CertificateKeyType.valueOf(certificateType);
	}

	CertificateKeyType getCertificateType() {
		return certificateType;
	}

	static int getFlowoutTimeout() {
		return flowoutTimeout;
	}

	@Value("${kurento.flowout.timeout}")
	private void setFlowoutTimeout(int timeout) {
		flowoutTimeout = timeout;
	}

	@Value("${kurento.ignored.kuids}")
	private void setIgnoredKuids(String ignoredKuids) {
		if (!Strings.isEmpty(ignoredKuids)) {
			this.ignoredKuids.addAll(List.of(ignoredKuids.split("[, ]")));
		}
	}

	private class KWatchDogCreate implements EventListener<ObjectCreatedEvent> {
		private ScheduledExecutorService scheduler;

		public KWatchDogCreate() {
			scheduler = Executors.newScheduledThreadPool(watchThreadCount);
		}

		private void checkPipeline(String roomOid) {
			scheduler.schedule(() -> {
				if (client == null) {
					log.trace("KWatchDog::checkPipeline Client is NULL");
					return;
				}
				// still alive
				MediaPipeline pipe = client.getById(roomOid, MediaPipeline.class);
				Map<String, String> tags = tagsAsMap(pipe);
				try {
					final String inKuid = tags.get(TAG_KUID);
					if (inKuid != null && ignoredKuids.contains(inKuid)) {
						log.trace("KWatchDog::checkPipeline KUID in ignore list");
						return;
					}
					if (validTestPipeline(tags)) {
						log.trace("KWatchDog::checkPipeline test pipeline detected");
						return;
					}
					if (kuid.equals(inKuid)) {
						String streamUId = tags.get(TAG_STREAM_UID);
						log.trace("KWatchDog::checkPipeline kuid matched, streamId: {}", streamUId);
						KStream stream = streamProcessor.getByUid(streamUId);
						if (stream != null) {
							Long sRoomId = stream.getRoomId();
							Long tRoomId = Long.valueOf(tags.get(TAG_ROOM));
							String pipeId = stream.getPipeline().getId();
							log.trace("KWatchDog::checkPipeline stream found! Room match ? {}, Pipe match ? {}"
									, sRoomId.equals(tRoomId), pipeId.equals(pipe.getId()));
							if (sRoomId.equals(tRoomId) && pipeId.equals(pipe.getId())) {
								return;
							} else {
								stream.release();
							}
						}
					}
				} catch (Exception e) {
					log.warn("Unexpected error while checking MediaPipeline {}, tags: {}", pipe.getId(), tags, e);
				}
				log.warn("Invalid MediaPipeline {} detected, will be dropped, tags: {}", pipe.getId(), tags);
				pipe.release();
			}, objCheckTimeout, MILLISECONDS);
		}

		private Class<? extends Endpoint> getEndpointClass(Endpoint curPoint) {
			Class<? extends Endpoint> clazz = null;
			if (curPoint instanceof WebRtcEndpoint) {
				clazz = WebRtcEndpoint.class;
			} else if (curPoint instanceof RecorderEndpoint) {
				clazz = RecorderEndpoint.class;
			} else if (curPoint instanceof PlayerEndpoint) {
				clazz = PlayerEndpoint.class;
			} else if (curPoint instanceof RtpEndpoint) {
				clazz = RtpEndpoint.class;
			}
			return clazz;
		}

		private void checkEndpoint(String endpointOid, Class<? extends Endpoint> clazz) {
			scheduler.schedule(() -> {
				if (client == null || clazz == null) {
					return;
				}
				// still alive
				Endpoint point = client.getById(endpointOid, clazz);
				Map<String, String> tags = tagsAsMap(point);
				try {
					Map<String, String> pipeTags = tagsAsMap(point.getMediaPipeline());
					final String inKuid = pipeTags.get(TAG_KUID);
					if (ignoredKuids.contains(inKuid)) {
						return;
					}
					if (validTestPipeline(pipeTags)) {
						return;
					}
					KStream stream = streamProcessor.getByUid(tags.get("outUid"));
					log.debug("Kurento::ObjectCreated -> New Endpoint {} detected, tags: {}, kStream: {}", point.getId(), tags, stream);
					if (stream != null && stream.contains(tags.get("uid"))) {
						return;
					}
				} catch (Exception e) {
					log.warn("Unexpected error while checking Endpoint {}, tags: {}", point.getId(), tags, e);
				}
				log.warn("Invalid Endpoint {} detected, will be dropped, tags: {}", point.getId(), tags);
				point.release();
			}, objCheckTimeout, MILLISECONDS);
		}

		@Override
		public void onEvent(ObjectCreatedEvent evt) {
			MediaObject obj = evt.getObject();
			log.debug("Kurento::ObjectCreated -> {}, source {}", obj, evt.getSource());
			if (obj instanceof MediaPipeline) {
				// room created
				final String roid = obj.getId();

				checkPipeline(roid);
			} else if (obj instanceof Endpoint curPoint) {
				// endpoint created
				final String eoid = curPoint.getId();
				final Class<? extends Endpoint> clazz = getEndpointClass(curPoint);

				checkEndpoint(eoid, clazz);
			}
		}

		private boolean validTestPipeline(Map<String, String> tags) {
			return kuid.equals(tags.get(TAG_KUID))
					&& MODE_TEST.equals(tags.get(TAG_MODE))
					&& MODE_TEST.equals(tags.get(TAG_ROOM));
		}
	}
}
