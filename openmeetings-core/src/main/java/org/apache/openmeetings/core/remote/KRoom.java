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
import static org.apache.openmeetings.core.remote.KurentoHandler.PARAM_ICE;
import static org.apache.openmeetings.core.remote.KurentoHandler.newKurentoMsg;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
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
	final MediaPipeline pipeline;
	final Long roomId;
	final AtomicBoolean recordingStarted = new AtomicBoolean(false);
	final AtomicBoolean sharingStarted = new AtomicBoolean(false);
	Long recordingId = null;
	final RecordingChunkDao chunkDao;
	private JSONObject recordingUser = new JSONObject();

	public KRoom(Long roomId, MediaPipeline pipeline, RecordingChunkDao chunkDao) {
		this.roomId = roomId;
		this.pipeline = pipeline;
		this.chunkDao = chunkDao;
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
		final KStream stream = new KStream(sd, this);
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
		for (StreamDesc sd : c.getStreams()) {
			if (StreamType.SCREEN == sd.getType()) {

			}
			KStream stream = streams.remove(sd.getUid());
			if (stream != null) {
				stream.release(h);
			}
		}
	}

	public boolean isRecording() {
		return recordingStarted.get();
	}

	public JSONObject getRecordingUser() {
		return new JSONObject(recordingUser.toString());
	}

	public void startRecording(Client c, RecordingDao recDao) {
		if (recordingStarted.compareAndSet(false, true)) {
			log.debug("##REC:: recording in room {} is starting ::", roomId);
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
				StreamDesc sd = c.getStream(stream.getUid());
				if (StreamType.SCREEN == sd.getType()) {
					sd.addActivity(Activity.RECORD);
				}
				stream.startRecord();
			}

			// Send notification to all users that the recording has been started
			WebSocketHelper.sendRoom(new RoomMessage(roomId, u, RoomMessage.Type.recordingToggled));
			log.debug("##REC:: recording in room {} is started {} ::", roomId, recordingId);
		}
	}

	public void stopRecording(KurentoHandler h, Client c, RecordingDao recDao) {
		if (recordingStarted.compareAndSet(true, false)) {
			log.debug("##REC:: recording in room {} is stopping {} ::", roomId, recordingId);
			for (final KStream stream : streams.values()) {
				stream.stopRecord();
			}
			Recording rec = recDao.get(recordingId);
			rec.setRecordEnd(new Date());
			rec = recDao.update(rec);
			recordingUser = new JSONObject();
			recordingId = null;

			h.startConvertion(rec);
			// Send notification to all users that the recording has been started
			User u = c == null ? new User() : c.getUser();
			WebSocketHelper.sendRoom(new RoomMessage(roomId, u, RoomMessage.Type.recordingToggled));
			log.debug("##REC:: recording in room {} is stopped ::", roomId);
		}
	}

	public boolean isSharing() {
		return sharingStarted.get();
	}

	public void startSharing(KurentoHandler h, IClientManager cm, Client c, JSONObject msg, Activity...activities) {
		if (sharingStarted.compareAndSet(false, true)) {
			StreamDesc sd = c.addStream(StreamType.SCREEN, activities);
			sd.setWidth(msg.getInt("width")).setHeight(msg.getInt("height"));
			cm.update(c);
			log.debug("User {}: has started broadcast", sd.getUid());
			h.sendClient(sd.getSid(), newKurentoMsg()
					.put("id", "broadcast")
					.put("stream", sd.toJson()
							.put("shareType", msg.getString("shareType"))
							.put("fps", msg.getString("fps")))
					.put(PARAM_ICE, h.getTurnServers()));
		}
	}

	public void stopSharing() {
		if (sharingStarted.compareAndSet(true, false)) {
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
