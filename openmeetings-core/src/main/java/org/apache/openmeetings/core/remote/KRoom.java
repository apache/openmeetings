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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.util.CalendarPatterns;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

public class KRoom {
	private static final Logger log = LoggerFactory.getLogger(KRoom.class);

	private final Map<String, KStream> streams = new ConcurrentHashMap<>();
	private final MediaPipeline pipeline;
	private final Long roomId;
	private final AtomicBoolean recordingStarted = new AtomicBoolean(false);
	private Long recordingId = null;
	private JSONObject recordingUser = new JSONObject();

	public KRoom(Long roomId, MediaPipeline pipeline) {
		this.roomId = roomId;
		this.pipeline = pipeline;
		log.info("ROOM {} has been created", roomId);
	}

	public Long getRoomId() {
		return roomId;
	}

	public String getPipelineId() {
		return pipeline.getId();
	}

	public KStream join(final StreamDesc sd) {
		log.info("ROOM {}: join client {}, stream: {}", roomId, sd.getClient().getUser().getLogin(), sd.getUid());
		final KStream stream = new KStream(sd, this.pipeline);
		streams.put(stream.getUid(), stream);
		return stream;
	}

	public Collection<KStream> getParticipants() {
		return streams.values();
	}

	public void leave(final KurentoHandler h, final Client c) {
		for (Map.Entry<String, KStream> e : streams.entrySet()) {
			e.getValue().remove(c);
		}
		KStream stream = streams.remove(c.getUid());
		if (stream != null) {
			stream.release(h);
		}
	}

	public boolean isRecording() {
		return recordingStarted.get();
	}

	public JSONObject getRecordingUser() {
		return new JSONObject(recordingUser.toString());
	}

	public void startRecording(Client c, RecordingDao recDao, RecordingChunkDao chunkDao) throws IOException {
		if (recordingStarted.compareAndSet(false, true)) {
			log.debug("##REC:: recording in room is started ::");
			Room r = c.getRoom();
			boolean interview = Room.Type.interview == r.getType();

			Date now = new Date();

			Recording rec = new Recording();

			rec.setHash(randomUUID().toString());
			rec.setName(String.format("%s %s", interview ? "Interview" : "Recording", CalendarPatterns.getDateWithTimeByMiliSeconds(new Date())));
			User u = c.getUser();
			recordingUser.put("login", u.getLogin());
			recordingUser.put("firstName", u.getFirstname());
			recordingUser.put("lastName", u.getLastname());
			recordingUser.put("started", now.getTime());
			Long ownerId = User.Type.contact == u.getType() ? u.getOwnerId() : u.getId();
			rec.setInsertedBy(ownerId);
			rec.setType(BaseFileItem.Type.Recording);
			rec.setInterview(interview);

			rec.setRoomId(roomId);
			rec.setRecordStart(now);

			rec.setOwnerId(ownerId);
			rec.setStatus(Recording.Status.RECORDING);
			rec = recDao.update(rec);
			// Receive recordingId
			recordingId = rec.getId();
			log.debug("##REC:: recording created by USER: {}", ownerId);

			for (final KStream stream : streams.values()) {
				stream.startRecord(recordingId, chunkDao);
			}

			// Send notification to all users that the recording has been started
			WebSocketHelper.sendRoom(new RoomMessage(roomId, u, RoomMessage.Type.recordingToggled));
		}
	}

	public void stopRecording(Client c, RecordingDao recDao) {
		if (recordingStarted.compareAndSet(true, false)) {
			for (final KStream stream : streams.values()) {
				stream.stopRecord();
			}
			Recording rec = recDao.get(recordingId);
			rec.setRecordEnd(new Date());
			rec = recDao.update(rec);

			// Send notification to all users that the recording has been started
			WebSocketHelper.sendRoom(new RoomMessage(roomId, c.getUser(), RoomMessage.Type.recordingToggled));
		}
	}

	public void close(final KurentoHandler h) {
		for (final KStream stream : streams.values()) {
			stream.release(h);
		}
		streams.clear();
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
