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
import org.kurento.client.MediaFlowOutStateChangeEvent;
import org.kurento.client.MediaFlowState;
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
	private WebRtcEndpoint outgoingMedia;
	private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

	//FIXME TODO multiple streams from client
	public KStream(final KurentoHandler h, final Client c, MediaPipeline pipeline) {
		this.pipeline = pipeline;
		this.sid = c.getSid();
		this.uid = c.getUid();
		this.roomId = c.getRoomId();
		//TODO Min/MaxVideoSendBandwidth
		//TODO Min/Max Audio/Video RecvBandwidth
	}

	private void initOutMedia(final KurentoHandler h) {
		outgoingMedia = createEndpoint(h, this);
		//TODO add logic here
		outgoingMedia.addMediaFlowOutStateChangeListener(new EventListener<MediaFlowOutStateChangeEvent>() {
			@Override
			public void onEvent(MediaFlowOutStateChangeEvent event) {
				log.warn("MediaFlowOutStateChange {}", event.getState());
				if (MediaFlowState.NOT_FLOWING == event.getState()) {
					outgoingMedia.release();
				}
			}
		});
	}

	public KStream startBroadcast(final KurentoHandler h, final Client c, final String sdpOffer) {
		if (outgoingMedia != null) {
			outgoingMedia.release();
		}
		initOutMedia(h);
		videoResponse(h, this, sdpOffer);
		WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
		WebSocketHelper.sendRoomOthers(roomId, uid, newKurentoMsg()
				.put("id", "newStream")
				.put("client", c.toJson(false).put("type", "room"))); // FIXME TODO add multi-stream support
		return this;
	}

	public String getSid() {
		return sid;
	}

	public String getUid() {
		return uid;
	}

	/**
	 * The room to which the user is currently attending.
	 *
	 * @return The room
	 */
	public Long getRoomId() {
		return roomId;
	}

	public boolean contains(String uid) {
		return this.uid.equals(uid) || incomingMedia.containsKey(uid);
	}

	public void videoResponse(final KurentoHandler h, KStream sender, String sdpOffer) {
		final boolean self = sender.getUid().equals(uid);
		log.info("USER {}: have started {} in room {}", uid, self ? "broadcasting" : "receiving", roomId);
		log.trace("USER {}: SdpOffer is {}", sender.getUid(), sdpOffer);

		final WebRtcEndpoint endpoint = getEndpointForUser(h, sender);
		final String sdpAnswer = endpoint.processOffer(sdpOffer);

		log.trace("USER {}: SdpAnswer is {}", uid, sdpAnswer);
		h.sendClient(sid, newKurentoMsg()
				.put("id", "videoResponse")
				.put("uid", sender.getUid())
				.put("sdpAnswer", sdpAnswer));
		log.debug("gather candidates");
		endpoint.gatherCandidates();
	}

	private WebRtcEndpoint getEndpointForUser(final KurentoHandler h, final KStream sender) {
		if (sender.getUid().equals(uid)) {
			log.debug("PARTICIPANT {}: configuring loopback", uid);
			return outgoingMedia;
		}

		log.debug("PARTICIPANT {}: receiving video from {}", uid, sender.getUid());

		WebRtcEndpoint incoming = incomingMedia.get(sender.getUid());
		if (incoming == null) {
			log.debug("PARTICIPANT {}: creating new endpoint for {}", uid, sender.getUid());
			incoming = createEndpoint(h, sender);
			incomingMedia.put(sender.getUid(), incoming);
		}

		log.debug("PARTICIPANT {}: obtained endpoint for {}", uid, sender.getUid());
		Client c = h.getBySid(sender.getSid());
		if (c.hasActivity(Activity.broadcastA)) {
			sender.outgoingMedia.connect(incoming, MediaType.AUDIO);
		}
		if (c.hasActivity(Activity.broadcastV)) {
			sender.outgoingMedia.connect(incoming, MediaType.VIDEO);
		}
		return incoming;
	}

	private WebRtcEndpoint createEndpoint(final KurentoHandler h, final KStream sender) {
		WebRtcEndpoint endpoint = new WebRtcEndpoint.Builder(pipeline).build();
		endpoint.addTag("suid", uid);
		endpoint.addTag("uid", sender.getUid());

		endpoint.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
			@Override
			public void onEvent(IceCandidateFoundEvent event) {
				h.sendClient(sid, newKurentoMsg()
						.put("id", "iceCandidate")
						.put("uid", sender.getUid())
						.put("candidate", convert(JsonUtils.toJsonObject(event.getCandidate()))));
					}
		});
		return endpoint;
	}

	public void remove(final Client c) {
		WebRtcEndpoint point = incomingMedia.remove(c.getUid());
		if (point != null) {
			point.release();
		}
	}

	public void stopBroadcast() {
		outgoingMedia.release();
		WebSocketHelper.sendAll(newKurentoMsg()
				.put("id", "broadcastStopped")
				.put("uid", uid)
				.toString()
			);
	}

	@Override
	public void release() {
		log.debug("PARTICIPANT {}: Releasing resources", uid);
		for (final String inUid : incomingMedia.keySet()) {
			log.trace("PARTICIPANT {}: Released incoming EP for {}", uid, inUid);

			final WebRtcEndpoint ep = incomingMedia.get(inUid);
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
		outgoingMedia.release();
	}

	public void addCandidate(IceCandidate candidate, String name) {
		if (this.uid.compareTo(name) == 0) {
			outgoingMedia.addIceCandidate(candidate);
		} else {
			WebRtcEndpoint webRtc = incomingMedia.get(name);
			if (webRtc != null) {
				webRtc.addIceCandidate(candidate);
			}
		}
	}

	private static JSONObject convert(com.google.gson.JsonObject o) {
		return new JSONObject(o.toString());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof KStream)) {
			return false;
		}
		KStream other = (KStream) obj;
		boolean eq = uid.equals(other.uid);
		eq &= roomId.equals(other.roomId);
		return eq;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + uid.hashCode();
		result = 31 * result + roomId.hashCode();
		return result;
	}
}
