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

import java.io.Closeable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.apache.openmeetings.db.entity.basic.Client;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KRoom implements Closeable {
	private final static Logger log = LoggerFactory.getLogger(KRoom.class);

	private final Map<String, KStream> participants = new ConcurrentHashMap<>();
	private final MediaPipeline pipeline;
	private final Long roomId;

	public Long getRoomId() {
		return roomId;
	}

	public String getPipelineId() {
		return pipeline.getId();
	}

	public KRoom(Long roomId, MediaPipeline pipeline) {
		this.roomId = roomId;
		this.pipeline = pipeline;
		log.info("ROOM {} has been created", roomId);
	}

	public KStream startBroadcast(final KurentoHandler h, Client c) {
		log.info("ROOM {}: adding participant {}", roomId, c.getUid());
		final KStream u = new KStream(h, c, this.pipeline);
		participants.put(u.getUid(), u);
		h.usersByUid.put(u.getUid(), u);
		return u;
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
