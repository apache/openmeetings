/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 */
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
import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static org.apache.openmeetings.core.remote.KurentoHandler.PARAM_CANDIDATE;
import static org.apache.openmeetings.core.remote.KurentoHandler.PARAM_ICE;
import static org.apache.openmeetings.core.remote.KurentoHandler.getFlowoutTimeout;
import static org.apache.openmeetings.core.remote.KurentoHandler.newKurentoMsg;
import static org.apache.openmeetings.util.OmFileHelper.getRecUri;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingChunk;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;
import org.apache.openmeetings.db.entity.record.RecordingChunk.Type;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.kurento.client.Continuation;
import org.kurento.client.IceCandidate;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.MediaType;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public class KStream extends AbstractStream {
	private static final Logger log = LoggerFactory.getLogger(KStream.class);

	private final KRoom room;
	private final Date connectedSince;
	private final StreamType streamType;
	private MediaProfileSpecType profile;
	private RecorderEndpoint recorder;
	private WebRtcEndpoint outgoingMedia = null;
	private final ConcurrentMap<String, WebRtcEndpoint> listeners = new ConcurrentHashMap<>();
	private Optional<CompletableFuture<Object>> flowoutFuture = Optional.empty();
	private Long chunkId;
	private Type type;

	public KStream(final StreamDesc sd, KRoom room) {
		super(sd.getSid(), sd.getUid());
		this.room = room;
		streamType = sd.getType();
		this.connectedSince = new Date();
		//TODO Min/MaxVideoSendBandwidth
		//TODO Min/Max Audio/Video RecvBandwidth
	}

	public KStream startBroadcast(final StreamProcessor processor, final StreamDesc sd, final String sdpOffer) {
		if (outgoingMedia != null) {
			release(processor, false);
		}
		final boolean hasAudio = sd.hasActivity(Activity.AUDIO);
		final boolean hasVideo = sd.hasActivity(Activity.VIDEO);
		final boolean hasScreen = sd.hasActivity(Activity.SCREEN);
		if ((sdpOffer.indexOf("m=audio") > -1 && !hasAudio)
				|| (sdpOffer.indexOf("m=video") > -1 && !hasVideo && StreamType.SCREEN != streamType))
		{
			log.warn("Broadcast started without enough rights");
			return this;
		}
		if (StreamType.SCREEN == streamType) {
			type = Type.SCREEN;
		} else {
			if (hasAudio && hasVideo) {
				type = Type.AUDIO_VIDEO;
			} else if (hasVideo) {
				type = Type.VIDEO_ONLY;
			} else {
				type = Type.AUDIO_ONLY;
			}
		}
		switch (type) {
			case AUDIO_VIDEO:
				profile = MediaProfileSpecType.WEBM;
				break;
			case AUDIO_ONLY:
				profile = MediaProfileSpecType.WEBM_AUDIO_ONLY;
				break;
			case SCREEN:
			case VIDEO_ONLY:
			default:
				profile = MediaProfileSpecType.WEBM_VIDEO_ONLY;
				break;
		}
		outgoingMedia = createEndpoint(processor, sd.getSid(), sd.getUid());
		outgoingMedia.addMediaSessionTerminatedListener(evt -> log.warn("Media stream terminated {}", sd));
		outgoingMedia.addMediaFlowOutStateChangeListener(evt -> {
			log.info("Media Flow STATE :: {}, type {}, evt {}", evt.getState(), evt.getType(), evt.getMediaType());
			switch (evt.getState()) {
				case NOT_FLOWING:
					log.warn("FlowOut Future is created");
					flowoutFuture = Optional.of(new CompletableFuture<>().completeAsync(() -> {
						log.warn("KStream will be dropped {}", sd);
						if (StreamType.SCREEN == streamType) {
							processor.doStopSharing(sid, uid);
						}
						stopBroadcast();
						return null;
					}, delayedExecutor(getFlowoutTimeout(), TimeUnit.SECONDS)));
					break;
				case FLOWING:
					flowoutFuture.ifPresent(f -> {
						log.warn("FlowOut Future is canceled");
						f.cancel(true);
						flowoutFuture = Optional.empty();
					});
					break;
			}
		});
		outgoingMedia.addMediaFlowInStateChangeListener(evt -> log.warn("Media FlowIn :: {}", evt));
		addListener(processor, sd.getSid(), sd.getUid(), sdpOffer);
		if (room.isRecording()) {
			startRecord(processor);
		}
		Client c = sd.getClient();
		WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.RIGHT_UPDATED, c.getUid()));
		if (hasAudio || hasVideo || hasScreen) {
			WebSocketHelper.sendRoomOthers(room.getRoomId(), c.getUid(), newKurentoMsg()
					.put("id", "newStream")
					.put(PARAM_ICE, processor.getHandler().getTurnServers(c))
					.put("stream", sd.toJson()));
		}
		return this;
	}

	public void addListener(final StreamProcessor processor, String sid, String uid, String sdpOffer) {
		final boolean self = uid.equals(this.uid);
		log.info("USER {}: have started {} in room {}", uid, self ? "broadcasting" : "receiving", room.getRoomId());
		log.trace("USER {}: SdpOffer is {}", uid, sdpOffer);
		if (!self && outgoingMedia == null) {
			log.warn("Trying to add listener too early");
			return;
		}

		final WebRtcEndpoint endpoint = getEndpointForUser(processor, sid, uid);
		final String sdpAnswer = endpoint.processOffer(sdpOffer);

		log.debug("gather candidates");
		endpoint.gatherCandidates(); // this one might throw Exception
		log.trace("USER {}: SdpAnswer is {}", this.uid, sdpAnswer);
		processor.getHandler().sendClient(sid, newKurentoMsg()
				.put("id", "videoResponse")
				.put("uid", this.uid)
				.put("sdpAnswer", sdpAnswer));
	}

	private WebRtcEndpoint getEndpointForUser(final StreamProcessor processor, String sid, String uid) {
		if (uid.equals(this.uid)) {
			log.debug("PARTICIPANT {}: configuring loopback", this.uid);
			return outgoingMedia;
		}

		log.debug("PARTICIPANT {}: receiving video from {}", uid, this.uid);
		WebRtcEndpoint listener = listeners.remove(uid);
		if (listener != null) {
			log.debug("PARTICIPANT {}: re-started video receiving, will drop previous endpoint", uid);
			listener.release();
		}
		log.debug("PARTICIPANT {}: creating new endpoint for {}", uid, this.uid);
		listener = createEndpoint(processor, sid, uid);
		listeners.put(uid, listener);

		log.debug("PARTICIPANT {}: obtained endpoint for {}", uid, this.uid);
		Client cur = processor.getBySid(this.sid);
		if (cur == null) {
			log.warn("Client for endpoint dooesn't exists");
		} else {
			StreamDesc sd = cur.getStream(this.uid);
			if (sd == null) {
				log.warn("Stream for endpoint dooesn't exists");
			} else {
				if (sd.hasActivity(Activity.AUDIO)) {
					outgoingMedia.connect(listener, MediaType.AUDIO);
				}
				if (StreamType.SCREEN == streamType || sd.hasActivity(Activity.VIDEO)) {
					outgoingMedia.connect(listener, MediaType.VIDEO);
				}
			}
		}
		return listener;
	}

	private WebRtcEndpoint createEndpoint(final StreamProcessor processor, String sid, String uid) {
		WebRtcEndpoint endpoint = createWebRtcEndpoint(room.getPipeline());
		endpoint.addTag("outUid", this.uid);
		endpoint.addTag("uid", uid);

		endpoint.addIceCandidateFoundListener(evt -> processor.getHandler().sendClient(sid
				, newKurentoMsg()
					.put("id", "iceCandidate")
					.put("uid", KStream.this.uid)
					.put(PARAM_CANDIDATE, convert(JsonUtils.toJsonObject(evt.getCandidate()))))
				);
		return endpoint;
	}

	public void startRecord(StreamProcessor processor) {
		log.debug("startRecord outMedia OK ? {}", outgoingMedia != null);
		if (outgoingMedia == null) {
			release(processor, true);
			return;
		}
		final String chunkUid = "rec_" + room.getRecordingId() + "_" + randomUUID();
		recorder = createRecorderEndpoint(room.getPipeline(), getRecUri(getRecordingChunk(room.getRoomId(), chunkUid)), profile);
		recorder.addTag("outUid", uid);
		recorder.addTag("uid", uid);

		recorder.addRecordingListener(evt -> chunkId = room.getChunkDao().start(room.getRecordingId(), type, chunkUid, sid));
		recorder.addStoppedListener(evt -> room.getChunkDao().stop(chunkId));
		switch (profile) {
			case WEBM:
				outgoingMedia.connect(recorder, MediaType.AUDIO);
				outgoingMedia.connect(recorder, MediaType.VIDEO);
				break;
			case WEBM_VIDEO_ONLY:
				outgoingMedia.connect(recorder, MediaType.VIDEO);
				break;
			case WEBM_AUDIO_ONLY:
			default:
				outgoingMedia.connect(recorder, MediaType.AUDIO);
				break;
		}
		recorder.record(new Continuation<Void>() {
			@Override
			public void onSuccess(Void result) throws Exception {
				log.info("Recording started successfully");
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.error("Failed to start recording", cause);
			}
		});
	}

	public void stopRecord() {
		releaseRecorder(true);
		chunkId = null;
	}

	public void remove(final Client c) {
		WebRtcEndpoint point = listeners.remove(c.getUid());
		if (point != null) {
			point.release();
		}
	}

	public void stopBroadcast() {
		room.onStopBroadcast(this);
	}

	public void pauseSharing() {
		releaseListeners();
	}

	private void releaseListeners() {
		log.debug("PARTICIPANT {}: Releasing listeners", uid);
		for (Entry<String, WebRtcEndpoint> entry : listeners.entrySet()) {
			final String inUid = entry.getKey();
			log.trace("PARTICIPANT {}: Released incoming EP for {}", uid, inUid);

			final WebRtcEndpoint ep = entry.getValue();
			outgoingMedia.disconnect(ep, new Continuation<Void>() {
				@Override
				public void onSuccess(Void result) throws Exception {
					log.trace("PARTICIPANT {}: Disconnected successfully incoming EP for {}", KStream.this.uid, inUid);
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.warn("PARTICIPANT {}: Could not disconnect incoming EP for {}", KStream.this.uid, inUid);
				}
			});
			ep.release(new Continuation<Void>() {
				@Override
				public void onSuccess(Void result) throws Exception {
					log.trace("PARTICIPANT {}: Released successfully incoming EP for {}", KStream.this.uid, inUid);
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.warn("PARTICIPANT {}: Could not release incoming EP for {}", KStream.this.uid, inUid);
				}
			});
		}
		listeners.clear();
	}

	@Override
	public void release(IStreamProcessor processor, boolean remove) {
		if (outgoingMedia != null) {
			releaseListeners();
			outgoingMedia.release(new Continuation<Void>() {
				@Override
				public void onSuccess(Void result) throws Exception {
					log.trace("PARTICIPANT {}: Released successfully", KStream.this.uid);
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.warn("PARTICIPANT {}: Could not release", KStream.this.uid, cause);
				}
			});
			releaseRecorder(false);
			outgoingMedia = null;
		}
		if (remove) {
			processor.release(this, false);
		}
	}

	private void releaseRecorder(boolean wait) {
		if (recorder != null) {
			if (wait) {
				recorder.stopAndWait();
			} else {
				recorder.stop(new Continuation<Void>() {
					@Override
					public void onSuccess(Void result) throws Exception {
						log.trace("PARTICIPANT {}: Recording stopped", KStream.this.uid);
					}

					@Override
					public void onError(Throwable cause) throws Exception {
						log.warn("PARTICIPANT {}: Could not stop recording", KStream.this.uid, cause);
					}
				});
			}
			outgoingMedia.disconnect(recorder, new Continuation<Void>() {
				@Override
				public void onSuccess(Void result) throws Exception {
					log.trace("PARTICIPANT {}: Recorder disconnected successfully", KStream.this.uid);
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.warn("PARTICIPANT {}: Could not disconnect recorder", KStream.this.uid, cause);
				}
			});
			recorder.release(new Continuation<Void>() {
				@Override
				public void onSuccess(Void result) throws Exception {
					log.trace("PARTICIPANT {}: Recorder released successfully", KStream.this.uid);
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.warn("PARTICIPANT {}: Could not release recorder", KStream.this.uid, cause);
				}
			});
			recorder = null;
		}
	}

	public void addCandidate(IceCandidate candidate, String uid) {
		if (this.uid.equals(uid)) {
			outgoingMedia.addIceCandidate(candidate);
		} else {
			WebRtcEndpoint endpoint = listeners.get(uid);
			log.debug("Add candidate for {}, listener found ? {}", uid, endpoint != null);
			if (endpoint != null) {
				endpoint.addIceCandidate(candidate);
			}
		}
	}

	private static JSONObject convert(com.google.gson.JsonObject o) {
		return new JSONObject(o.toString());
	}

	@Override
	public String getSid() {
		return sid;
	}

	@Override
	public String getUid() {
		return uid;
	}

	public Date getConnectedSince() {
		return connectedSince;
	}

	public KRoom getRoom() {
		return room;
	}

	public StreamType getStreamType() {
		return streamType;
	}

	public MediaProfileSpecType getProfile() {
		return profile;
	}

	public RecorderEndpoint getRecorder() {
		return recorder;
	}

	public WebRtcEndpoint getOutgoingMedia() {
		return outgoingMedia;
	}

	public Long getChunkId() {
		return chunkId;
	}

	public Type getType() {
		return type;
	}

	public boolean contains(String uid) {
		return this.uid.equals(uid) || listeners.containsKey(uid);
	}

	@Override
	public String toString() {
		return "KStream [room=" + room + ", streamType=" + streamType + ", profile=" + profile + ", recorder="
				+ recorder + ", outgoingMedia=" + outgoingMedia + ", listeners=" + listeners + ", flowoutFuture="
				+ flowoutFuture + ", chunkId=" + chunkId + ", type=" + type + ", sid=" + sid + ", uid=" + uid + "]";
	}
}
