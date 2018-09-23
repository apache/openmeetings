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
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.kurento.client.ConnectionStateChangedEvent;
import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaFlowOutStateChangeEvent;
import org.kurento.client.MediaFlowState;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

/**
 *
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class KStream implements IKStream {
	private static final Logger log = LoggerFactory.getLogger(KStream.class);

	private final String sid;
	private final String uid;
	private final MediaPipeline pipeline;
	private final Long roomId;
	private final WebRtcEndpoint outgoingMedia;
	private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

	//FIXME TODO multiple streams from client
	public KStream(final KurentoHandler h, final Client c, MediaPipeline pipeline) {
		this.pipeline = pipeline;
		this.sid = c.getSid();
		this.uid = c.getUid();
		this.roomId = c.getRoomId();
		//TODO Min/MaxVideoSendBandwidth
		//TODO Min/Max Audio/Video RecvBandwidth
		outgoingMedia = createEndpoint(h, c);
		//TODO add logic here
		outgoingMedia.addConnectionStateChangedListener(new EventListener<ConnectionStateChangedEvent>() {
			@Override
			public void onEvent(ConnectionStateChangedEvent event) {
				log.warn("StateChanged {} -> {}", event.getOldState(), event.getNewState());
			}
		});
		outgoingMedia.addMediaFlowOutStateChangeListener(new EventListener<MediaFlowOutStateChangeEvent>() {
			@Override
			public void onEvent(MediaFlowOutStateChangeEvent event) {
				log.warn("MediaFlowOutStateChange {}", event.getState());
				if (MediaFlowState.FLOWING == event.getState()) {
					JSONObject msg = newKurentoMsg();
					msg.put("id", "newStream");
					msg.put("client", c.toJson(false));
					WebSocketHelper.sendRoomOthers(roomId, uid, msg);
				}
			}
		});
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

	public void videoResponse(final KurentoHandler h, Client c, String sdpOffer) {
		final boolean self = c.getUid().equals(uid);
		log.info("USER {}: have started {} in room {}", uid, self ? "broadcasting" : "receiving", roomId);
		log.trace("USER {}: SdpOffer is {}", c.getUid(), sdpOffer);

		final WebRtcEndpoint endpoint = getEndpointForUser(h, c);
		final String sdpAnswer = endpoint.processOffer(sdpOffer);

		log.trace("USER {}: SdpAnswer is {}", uid, sdpAnswer);
		h.sendClient(c.getSid(), newKurentoMsg()
				.put("id", "videoResponse")
				.put("uid", uid)
				.put("sdpAnswer", sdpAnswer));
		log.debug("gather candidates");
		endpoint.gatherCandidates();
		if (self) {
			WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
		}
	}

	private WebRtcEndpoint getEndpointForUser(final KurentoHandler h, final Client c) {
		if (c.getUid().equals(uid)) {
			log.debug("PARTICIPANT {}: configuring loopback", uid);
			return outgoingMedia;
		}

		log.debug("PARTICIPANT {}: receiving video from {}", uid, c.getUid());

		WebRtcEndpoint incoming = incomingMedia.get(c.getUid());
		if (incoming == null) {
			log.debug("PARTICIPANT {}: creating new endpoint for {}", uid, c.getUid());
			incoming = createEndpoint(h, c);
			incomingMedia.put(c.getUid(), incoming);
		}

		log.debug("PARTICIPANT {}: obtained endpoint for {}", uid, c.getUid());
		outgoingMedia.connect(incoming);

		return incoming;
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
		outgoingMedia.release(new Continuation<Void>() {
			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("PARTICIPANT {}: Released outgoing EP", KStream.this.uid);
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("USER {}: Could not release outgoing EP", KStream.this.uid);
			}
		});
	}

	private WebRtcEndpoint createEndpoint(final KurentoHandler h, final Client c) {
		WebRtcEndpoint endpoint = new WebRtcEndpoint.Builder(pipeline).build();
		endpoint.addTag("suid", uid);
		endpoint.addTag("uid", c.getUid());

		endpoint.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
			@Override
			public void onEvent(IceCandidateFoundEvent event) {
				JSONObject response = newKurentoMsg();
				response.put("id", "iceCandidate");
				response.put("uid", uid);
				response.put("candidate", convert(JsonUtils.toJsonObject(event.getCandidate())));
				h.sendClient(c.getSid(), response);
			}
		});
		return endpoint;
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
