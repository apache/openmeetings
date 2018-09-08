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

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
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

	private final Map<String, KStream> participants = new ConcurrentHashMap<>();
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

	public KStream addStream(final KurentoHandler h, StreamDesc sd) {
		log.info("ROOM {}: adding participant {}", roomId, sd.getUid());
		final KStream u = new KStream(h, sd.getSid(), sd.getUid(), this.roomId, this.pipeline);
		participants.put(u.getUid(), u);
		h.usersByUid.put(u.getUid(), u);
		return u;
	}

	public KStream join(final KurentoHandler h, Client c, StreamDesc sd) {
		KStream u = addStream(h, sd);
		broadcast(h, c, sd);
		return u;
	}

	public void leave(final KurentoHandler h, KStream user) {
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
		for (final KStream participant : participants.values()) {
			participant.cancelVideoFrom(name);
			h.sendClient(participant.getSid(), msg);
		}

		if (!unnotifiedParticipants.isEmpty()) {
			log.debug("ROOM {}: The users {} could not be notified that {} left the room", this.roomId,
					unnotifiedParticipants, name);
		}
	}

	private static void broadcast(final KurentoHandler h, Client c, StreamDesc sd) {
		final JSONObject msg = newKurentoMsg();
		msg.put("id", "broadcast");
		msg.put("uid", sd.getUid());
		msg.put("stream", new JSONObject(sd));
		msg.put("client", c.toJson(true));
		log.debug("User {}: has started broadcast", sd.getSid());
		h.sendClient(sd.getSid(), msg);
	}

	public Collection<KStream> getParticipants() {
		return participants.values();
	}

	@PreDestroy
	@Override
	public void close() {
		for (final KStream user : participants.values()) {
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
