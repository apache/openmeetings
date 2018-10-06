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

import static org.apache.openmeetings.core.remote.KurentoHandler.newKurentoMsg;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaType;
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
	private final Long roomId;
	private WebRtcEndpoint outgoingMedia = null;
	private final ConcurrentMap<String, WebRtcEndpoint> listeners = new ConcurrentHashMap<>();

	//FIXME TODO multiple streams from client
	public KStream(final Client c, MediaPipeline pipeline) {
		this.pipeline = pipeline;
		this.sid = c.getSid();
		this.uid = c.getUid();
		this.roomId = c.getRoomId();
		//TODO Min/MaxVideoSendBandwidth
		//TODO Min/Max Audio/Video RecvBandwidth
	}

	public KStream startBroadcast(final KurentoHandler h, final Client c, final String sdpOffer) {
		if (outgoingMedia != null) {
			release();
		}
		if ((sdpOffer.indexOf("m=audio") > -1 && !c.hasActivity(Activity.broadcastA))
				|| (sdpOffer.indexOf("m=video") > -1 && !c.hasActivity(Activity.broadcastV)))
		{
			log.warn("Broadcast started without enough rights");
			return this;
		}
		outgoingMedia = createEndpoint(h, h.getBySid(sid));
		addListener(h, c, sdpOffer);
		WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
		WebSocketHelper.sendRoomOthers(roomId, uid, newKurentoMsg()
				.put("id", "newStream")
				.put("iceServers", h.getTurnServers())
				.put("client", c.toJson(false).put("type", "room"))); // FIXME TODO add multi-stream support
		return this;
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

	public void addListener(final KurentoHandler h, Client c, String sdpOffer) {
		final boolean self = c.getUid().equals(uid);
		log.info("USER {}: have started {} in room {}", uid, self ? "broadcasting" : "receiving", roomId);
		log.trace("USER {}: SdpOffer is {}", c.getUid(), sdpOffer);
		if (!self && outgoingMedia == null) {
			log.warn("Trying to add listener too early");
			return;
		}

		final WebRtcEndpoint endpoint = getEndpointForUser(h, c);
		final String sdpAnswer = endpoint.processOffer(sdpOffer);

		log.trace("USER {}: SdpAnswer is {}", uid, sdpAnswer);
		h.sendClient(c.getSid(), newKurentoMsg()
				.put("id", "videoResponse")
				.put("uid", uid)
				.put("sdpAnswer", sdpAnswer));
		log.debug("gather candidates");
		endpoint.gatherCandidates();
	}

	private WebRtcEndpoint getEndpointForUser(final KurentoHandler h, final Client c) {
		if (c.getUid().equals(uid)) {
			log.debug("PARTICIPANT {}: configuring loopback", uid);
			return outgoingMedia;
		}

		log.debug("PARTICIPANT {}: receiving video from {}", c.getUid(), uid);

		WebRtcEndpoint listener = listeners.get(c.getUid());
		if (listener == null) {
			log.debug("PARTICIPANT {}: creating new endpoint for {}", c.getUid(), uid);
			listener = createEndpoint(h, c);
			listeners.put(c.getUid(), listener);
		}

		log.debug("PARTICIPANT {}: obtained endpoint for {}", c.getUid(), uid);
		Client cur = h.getBySid(sid);
		if (cur.hasActivity(Activity.broadcastA)) {
			outgoingMedia.connect(listener, MediaType.AUDIO);
		}
		if (cur.hasActivity(Activity.broadcastV)) {
			outgoingMedia.connect(listener, MediaType.VIDEO);
		}
		return listener;
	}

	private WebRtcEndpoint createEndpoint(final KurentoHandler h, final Client c) {
		WebRtcEndpoint endpoint = new WebRtcEndpoint.Builder(pipeline).build();
		endpoint.addTag("outUid", uid);
		endpoint.addTag("uid", c.getUid());

		endpoint.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
			@Override
			public void onEvent(IceCandidateFoundEvent event) {
				h.sendClient(c.getSid(), newKurentoMsg()
						.put("id", "iceCandidate")
						.put("uid", uid)
						.put("candidate", convert(JsonUtils.toJsonObject(event.getCandidate()))));
					}
		});
		return endpoint;
	}

	public void remove(final Client c) {
		WebRtcEndpoint point = listeners.remove(c.getUid());
		if (point != null) {
			point.release();
		}
	}

	public void stopBroadcast() {
		release();
		WebSocketHelper.sendAll(newKurentoMsg()
				.put("id", "broadcastStopped")
				.put("uid", uid)
				.toString()
			);
	}

	@Override
	public void release() {
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
	}

	public void addCandidate(IceCandidate candidate, String uid) {
		if (this.uid.equals(uid)) {
			outgoingMedia.addIceCandidate(candidate);
		} else {
			WebRtcEndpoint webRtc = listeners.get(uid);
			if (webRtc != null) {
				webRtc.addIceCandidate(candidate);
			}
		}
	}

	private static JSONObject convert(com.google.gson.JsonObject o) {
		return new JSONObject(o.toString());
	}
}
