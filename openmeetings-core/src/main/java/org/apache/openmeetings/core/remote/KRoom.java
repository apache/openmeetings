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

import com.github.openjson.JSONObject;

/**
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class KRoom implements Closeable {
	private final static Logger log = LoggerFactory.getLogger(KRoom.class);

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

	public KUser addUser(final KurentoHandler h, String uid) {
		log.info("ROOM {}: adding participant {}", roomId, uid);
		final KUser u = new KUser(h, uid, this.roomId, this.pipeline);
		participants.put(u.getUid(), u);
		h.usersByUid.put(u.getUid(), u);
		return u;
	}

	public KUser join(final KurentoHandler h, String uid) {
		KUser u = addUser(h, uid);
		broadcast(h, u);
		return u;
	}

	public void leave(final KurentoHandler h, KUser user) {
		log.debug("PARTICIPANT {}: Leaving room {}", user.getUid(), this.roomId);
		this.removeParticipant(h, user.getUid());
		user.release();
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

	private static void broadcast(final KurentoHandler h, KUser user) {
		final JSONObject msg = newKurentoMsg();
		msg.put("id", "broadcast");
		log.debug("User {}: has started broadcast", user.getUid());
		h.sendClient(user.getUid(), msg);
	}

	public Collection<KUser> getParticipants() {
		return participants.values();
	}

	@PreDestroy
	@Override
	public void close() {
		for (final KUser user : participants.values()) {
			user.release();
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
