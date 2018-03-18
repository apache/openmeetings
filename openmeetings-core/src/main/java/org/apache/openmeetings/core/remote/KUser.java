/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.openmeetings.core.remote;

import static org.apache.openmeetings.core.remote.KurentoHandler.newKurentoMsg;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kurento.client.ConnectionStateChangedEvent;
import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaFlowOutStateChangeEvent;
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
public class KUser implements Closeable {
	private static final Logger log = LoggerFactory.getLogger(KUser.class);

	private final String uid;
	private final MediaPipeline pipeline;
	private final Long roomId;
	private final WebRtcEndpoint outgoingMedia;
	private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

	public KUser(final KurentoHandler h, final String uid, Long roomId, MediaPipeline pipeline) {
		this.pipeline = pipeline;
		this.uid = uid;
		this.roomId = roomId;
		//TODO Min/MaxVideoSendBandwidth
		//TODO Min/Max Audio/Video RecvBandwidth
		outgoingMedia = new WebRtcEndpoint.Builder(pipeline).build();

		outgoingMedia.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {
			@Override
			public void onEvent(IceCandidateFoundEvent event) {
				JSONObject response = newKurentoMsg();
				response.put("id", "iceCandidate");
				response.put("uid", uid);
				response.put("candidate", convert(JsonUtils.toJsonObject(event.getCandidate())));
				h.sendClient(uid, response);
			}
		});
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
			}
		});
	}

	public WebRtcEndpoint getOutgoingWebRtcPeer() {
		return outgoingMedia;
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
		return this.roomId;
	}

	public void receiveVideoFrom(final KurentoHandler h, KUser sender, String sdpOffer) {
		log.info("USER {}: connecting with {} in room {}", this.uid, sender.getUid(), this.roomId);

		log.trace("USER {}: SdpOffer for {} is {}", this.uid, sender.getUid(), sdpOffer);

		final String sdpAnswer = this.getEndpointForUser(h, sender).processOffer(sdpOffer);
		final JSONObject scParams = newKurentoMsg();
		scParams.put("id", "videoResponse");
		scParams.put("uid", sender.getUid());
		scParams.put("sdpAnswer", sdpAnswer);

		log.trace("USER {}: SdpAnswer for {} is {}", this.uid, sender.getUid(), sdpAnswer);
		h.sendClient(uid, scParams);
		log.debug("gather candidates");
		this.getEndpointForUser(h, sender).gatherCandidates();
	}

	private WebRtcEndpoint getEndpointForUser(final KurentoHandler h, final KUser sender) {
		if (sender.getUid().equals(uid)) {
			log.debug("PARTICIPANT {}: configuring loopback", this.uid);
			return outgoingMedia;
		}

		log.debug("PARTICIPANT {}: receiving video from {}", this.uid, sender.getUid());

		WebRtcEndpoint incoming = incomingMedia.get(sender.getUid());
		if (incoming == null) {
			log.debug("PARTICIPANT {}: creating new endpoint for {}", this.uid, sender.getUid());
			incoming = new WebRtcEndpoint.Builder(pipeline).build();

			incoming.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

				@Override
				public void onEvent(IceCandidateFoundEvent event) {
					JSONObject response = newKurentoMsg();
					response.put("id", "iceCandidate");
					response.put("uid", sender.getUid());
					response.put("candidate", convert(JsonUtils.toJsonObject(event.getCandidate())));
					h.sendClient(uid, response);
				}
			});

			incomingMedia.put(sender.getUid(), incoming);
		}

		log.debug("PARTICIPANT {}: obtained endpoint for {}", this.uid, sender.getUid());
		sender.getOutgoingWebRtcPeer().connect(incoming);

		return incoming;
	}

	public void cancelVideoFrom(final KUser sender) {
		this.cancelVideoFrom(sender.getUid());
	}

	public void cancelVideoFrom(final String senderName) {
		log.debug("PARTICIPANT {}: canceling video reception from {}", this.uid, senderName);
		final WebRtcEndpoint incoming = incomingMedia.remove(senderName);

		log.debug("PARTICIPANT {}: removing endpoint for {}", this.uid, senderName);
		incoming.release(new Continuation<Void>() {
			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("PARTICIPANT {}: Released successfully incoming EP for {}", KUser.this.uid, senderName);
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("PARTICIPANT {}: Could not release incoming EP for {}", KUser.this.uid, senderName);
			}
		});
	}

	@Override
	public void close() {
		log.debug("PARTICIPANT {}: Releasing resources", this.uid);
		for (final String remoteParticipantName : incomingMedia.keySet()) {

			log.trace("PARTICIPANT {}: Released incoming EP for {}", this.uid, remoteParticipantName);

			final WebRtcEndpoint ep = this.incomingMedia.get(remoteParticipantName);
			ep.release(new Continuation<Void>() {
				@Override
				public void onSuccess(Void result) throws Exception {
					log.trace("PARTICIPANT {}: Released successfully incoming EP for {}", KUser.this.uid,
							remoteParticipantName);
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.warn("PARTICIPANT {}: Could not release incoming EP for {}", KUser.this.uid,
							remoteParticipantName);
				}
			});
		}

		outgoingMedia.release(new Continuation<Void>() {

			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("PARTICIPANT {}: Released outgoing EP", KUser.this.uid);
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("USER {}: Could not release outgoing EP", KUser.this.uid);
			}
		});
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
		if (obj == null || !(obj instanceof KUser)) {
			return false;
		}
		KUser other = (KUser) obj;
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
