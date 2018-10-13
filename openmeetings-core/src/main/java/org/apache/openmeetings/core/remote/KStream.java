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
import static org.apache.openmeetings.core.remote.KurentoHandler.newKurentoMsg;
import static org.apache.openmeetings.util.OmFileHelper.getRecUri;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingChunk;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;
import org.apache.openmeetings.db.entity.record.RecordingChunk.Type;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.MediaType;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.RecordingEvent;
import org.kurento.client.StoppedEvent;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public class KStream implements IKStream {
	private static final Logger log = LoggerFactory.getLogger(KStream.class);

	private final String sid;
	private final String uid;
	private final MediaPipeline pipeline;
	private MediaProfileSpecType profile;
	private RecorderEndpoint recorder;
	private final Long roomId;
	private WebRtcEndpoint outgoingMedia = null;
	private final ConcurrentMap<String, WebRtcEndpoint> listeners = new ConcurrentHashMap<>();
	private Long recordingId;
	private Long chunkId;
	private Type type;
	private RecordingChunkDao chunkDao;

	public KStream(final StreamDesc sd, MediaPipeline pipeline) {
		this.pipeline = pipeline;
		this.sid = sd.getSid();
		this.uid = sd.getUid();
		this.roomId = sd.getClient().getRoomId();
		//TODO Min/MaxVideoSendBandwidth
		//TODO Min/Max Audio/Video RecvBandwidth
	}

	public KStream startBroadcast(final KurentoHandler h, final StreamDesc sd, final String sdpOffer) {
		if (outgoingMedia != null) {
			release(h);
		}
		final boolean hasAudio = sd.hasActivity(Activity.broadcastA);
		final boolean hasVideo = sd.hasActivity(Activity.broadcastV);
		if ((sdpOffer.indexOf("m=audio") > -1 && !hasAudio)
				|| (sdpOffer.indexOf("m=video") > -1 && !hasVideo))
		{
			log.warn("Broadcast started without enough rights");
			return this;
		}
		if (StreamType.screen == sd.getType()) {
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
		outgoingMedia = createEndpoint(h, sd.getSid(), sd.getUid());
		h.streamsByUid.put(uid, this);
		addListener(h, sd.getSid(), sd.getUid(), sdpOffer);
		Client c = sd.getClient();
		WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
		WebSocketHelper.sendRoomOthers(roomId, c.getUid(), newKurentoMsg()
				.put("id", "newStream")
				.put("iceServers", h.getTurnServers())
				.put("stream", sd.toJson()));
		return this;
	}

	public void addListener(final KurentoHandler h, String sid, String uid, String sdpOffer) {
		final boolean self = uid.equals(this.uid);
		log.info("USER {}: have started {} in room {}", uid, self ? "broadcasting" : "receiving", roomId);
		log.trace("USER {}: SdpOffer is {}", uid, sdpOffer);
		if (!self && outgoingMedia == null) {
			log.warn("Trying to add listener too early");
			return;
		}

		final WebRtcEndpoint endpoint = getEndpointForUser(h, sid, uid);
		final String sdpAnswer = endpoint.processOffer(sdpOffer);

		log.trace("USER {}: SdpAnswer is {}", this.uid, sdpAnswer);
		h.sendClient(sid, newKurentoMsg()
				.put("id", "videoResponse")
				.put("uid", this.uid)
				.put("sdpAnswer", sdpAnswer));
		log.debug("gather candidates");
		endpoint.gatherCandidates();
	}

	private WebRtcEndpoint getEndpointForUser(final KurentoHandler h, String sid, String uid) {
		if (uid.equals(this.uid)) {
			log.debug("PARTICIPANT {}: configuring loopback", this.uid);
			return outgoingMedia;
		}

		log.debug("PARTICIPANT {}: receiving video from {}", uid, this.uid);

		WebRtcEndpoint listener = listeners.get(uid);
		if (listener == null) {
			log.debug("PARTICIPANT {}: creating new endpoint for {}", uid, this.uid);
			listener = createEndpoint(h, sid, uid);
			listeners.put(uid, listener);
		}

		log.debug("PARTICIPANT {}: obtained endpoint for {}", uid, this.uid);
		Client cur = h.getBySid(this.sid);
		if (cur.hasActivity(Activity.broadcastA)) {
			outgoingMedia.connect(listener, MediaType.AUDIO);
		}
		if (cur.hasActivity(Activity.broadcastV)) {
			outgoingMedia.connect(listener, MediaType.VIDEO);
		}
		return listener;
	}

	private WebRtcEndpoint createEndpoint(final KurentoHandler h, String sid, String uid) {
		WebRtcEndpoint endpoint = new WebRtcEndpoint.Builder(pipeline).build();
		endpoint.addTag("outUid", this.uid);
		endpoint.addTag("uid", uid);

		endpoint.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
			@Override
			public void onEvent(IceCandidateFoundEvent event) {
				h.sendClient(sid, newKurentoMsg()
						.put("id", "iceCandidate")
						.put("uid", KStream.this.uid)
						.put("candidate", convert(JsonUtils.toJsonObject(event.getCandidate()))));
					}
		});
		return endpoint;
	}

	public void startRecord(Long recId, final RecordingChunkDao chunkDao) throws IOException {
		this.chunkDao = chunkDao;
		final String chunkUid = randomUUID().toString();
		recordingId = recId;
		recorder = new RecorderEndpoint.Builder(pipeline, getRecUri(getRecordingChunk(roomId, chunkUid)))
				.stopOnEndOfStream()
				.withMediaProfile(profile).build();
		recorder.addTag("outUid", uid);
		recorder.addTag("uid", uid);

		recorder.addRecordingListener(new EventListener<RecordingEvent>() {
			@Override
			public void onEvent(RecordingEvent event) {
				chunkId = chunkDao.start(recordingId, type, chunkUid, sid);
			}
		});
		recorder.addStoppedListener(new EventListener<StoppedEvent>() {
			@Override
			public void onEvent(StoppedEvent event) {
				chunkDao.stop(chunkId);
			}
		});
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
		recorder.stopAndWait();
		releaseRecorder();
		recordingId = null;
		chunkId = null;
		chunkDao = null;
	}

	public void remove(final Client c) {
		WebRtcEndpoint point = listeners.remove(c.getUid());
		if (point != null) {
			point.release();
		}
	}

	public void stopBroadcast(final KurentoHandler h) {
		release(h);
		WebSocketHelper.sendAll(newKurentoMsg()
				.put("id", "broadcastStopped")
				.put("uid", uid)
				.toString()
			);
	}

	@Override
	public void release(KurentoHandler h) {
		if (outgoingMedia != null) {
			log.debug("PARTICIPANT {}: Releasing resources", uid);
			for (Entry<String, WebRtcEndpoint> entry : listeners.entrySet()) {
				final String inUid = entry.getKey();
				log.trace("PARTICIPANT {}: Released incoming EP for {}", uid, inUid);

				final WebRtcEndpoint ep = entry.getValue();
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
			outgoingMedia.release();
			outgoingMedia = null;
		}
		releaseRecorder();
		h.streamsByUid.remove(uid);
	}

	private void releaseRecorder() {
		if (recorder != null) {
			recorder.release();
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

	public String getSid() {
		return sid;
	}

	public String getUid() {
		return uid;
	}

	public Long getRoomId() {
		return roomId;
	}

	public boolean contains(String uid) {
		return this.uid.equals(uid) || listeners.containsKey(uid);
	}
}
