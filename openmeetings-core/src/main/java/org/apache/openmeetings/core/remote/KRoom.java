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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

/**
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class KRoom implements Closeable {
	private final Logger log = LoggerFactory.getLogger(KRoom.class);

	private final Map<String, KUser> participants = new ConcurrentHashMap<>();
	private final MediaPipeline pipeline;
	private final Long roomId;

	public Long getRoomId() {
		return roomId;
	}

	public KRoom(Long roomId, MediaPipeline pipeline) {
		this.roomId = roomId;
		this.pipeline = pipeline;
		log.info("ROOM {} has been created", roomId);
	}

	@PreDestroy
	private void shutdown() {
		this.close();
	}

	public KUser join(final KurentoHandler h, String uid) {
		log.info("ROOM {}: adding participant {}", roomId, uid);
		final KUser participant = new KUser(h, uid, this.roomId, this.pipeline);
		joinRoom(h, participant);
		participants.put(participant.getUid(), participant);
		sendParticipantNames(h, participant);
		return participant;
	}

	public void leave(final KurentoHandler h, KUser user) {
		log.debug("PARTICIPANT {}: Leaving room {}", user.getUid(), this.roomId);
		this.removeParticipant(h, user.getUid());
		user.close();
	}

	private Collection<String> joinRoom(final KurentoHandler h, KUser newParticipant) {
		final JSONObject msg = newKurentoMsg();
		msg.put("id", "newParticipantArrived");
		msg.put("name", newParticipant.getUid());

		final List<String> participantsList = new ArrayList<>(participants.values().size());
		log.debug("ROOM {}: notifying other participants of new participant {}", roomId, newParticipant.getUid());

		for (final KUser participant : participants.values()) {
			h.sendClient(participant.getUid(), msg);
			participantsList.add(participant.getUid());
		}

		return participantsList;
	}

	private void removeParticipant(final KurentoHandler h, String name) {
		participants.remove(name);

		log.debug("ROOM {}: notifying all users that {} is leaving the room", this.roomId, name);

		final List<String> unnotifiedParticipants = new ArrayList<>();
		final JSONObject msg = newKurentoMsg();
		msg.put("id", "participantLeft");
		msg.put("name", name);
		for (final KUser participant : participants.values()) {
			participant.cancelVideoFrom(name);
			h.sendClient(participant.getUid(), msg);
		}

		if (!unnotifiedParticipants.isEmpty()) {
			log.debug("ROOM {}: The users {} could not be notified that {} left the room", this.roomId,
					unnotifiedParticipants, name);
		}

	}

	public void sendParticipantNames(final KurentoHandler h, KUser user) {
		final JSONArray participantsArray = new JSONArray();
		for (final KUser participant : this.getParticipants()) {
			if (!participant.equals(user)) {
				participantsArray.put(participant.getUid());
			}
		}

		final JSONObject msg = newKurentoMsg();
		msg.put("id", "existingParticipants");
		msg.put("data", participantsArray);
		log.debug("PARTICIPANT {}: sending a list of {} participants", user.getUid(), participantsArray.length());
		h.sendClient(user.getUid(), msg);
	}

	public Collection<KUser> getParticipants() {
		return participants.values();
	}

	public KUser getParticipant(String name) {
		return participants.get(name);
	}

	@Override
	public void close() {
		for (final KUser user : participants.values()) {
			user.close();
		}

		participants.clear();

		pipeline.release(new Continuation<Void>() {

			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("ROOM {}: Released Pipeline", KRoom.this.roomId);
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("PARTICIPANT {}: Could not release Pipeline", KRoom.this.roomId);
			}
		});

		log.debug("Room {} closed", this.roomId);
	}

}
