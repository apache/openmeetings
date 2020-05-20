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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
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
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.CalendarPatterns;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

/**
 * Bean object dynamically created representing a conference room on the MediaServer
 *
 */
public class KRoom {

	private static final Logger log = LoggerFactory.getLogger(KRoom.class);

	/**
	 * Not injected by annotation but by constructor.
	 */
	private final StreamProcessor processor;
	private final MediaPipeline pipeline;
	private final Long roomId;
	private final Room.Type type;
	private final AtomicBoolean recordingStarted = new AtomicBoolean(false);
	private final AtomicBoolean sharingStarted = new AtomicBoolean(false);
	private Long recordingId = null;
	private final RecordingChunkDao chunkDao;
	private JSONObject recordingUser = new JSONObject();
	private JSONObject sharingUser = new JSONObject();

	public KRoom(StreamProcessor processor, Room r, MediaPipeline pipeline, RecordingChunkDao chunkDao) {
		this.processor = processor;
		this.roomId = r.getId();
		this.type = r.getType();
		this.pipeline = pipeline;
		this.chunkDao = chunkDao;
		log.info("ROOM {} has been created", roomId);
	}

	public Long getRoomId() {
		return roomId;
	}

	public Room.Type getType() {
		return type;
	}

	public Long getRecordingId() {
		return recordingId;
	}

	public MediaPipeline getPipeline() {
		return pipeline;
	}

	public RecordingChunkDao getChunkDao() {
		return chunkDao;
	}

	public KStream join(final StreamDesc sd) {
		log.info("ROOM {}: join client {}, stream: {}", roomId, sd.getClient(), sd.getUid());
		final KStream stream = new KStream(sd, this);
		processor.addStream(stream);
		return stream;
	}

	public Collection<KStream> getParticipants() {
		return processor.getByRoom(this.getRoomId());
	}

	public void onStopBroadcast(KStream stream) {
		processor.release(stream, true);
		WebSocketHelper.sendAll(newKurentoMsg()
				.put("id", "broadcastStopped")
				.put("uid", stream.getUid())
				.toString()
			);
		//FIXME TODO check close on stop sharing
		//FIXME TODO permission can be removed, some listener might be required
	}

	public boolean isRecording() {
		return recordingStarted.get();
	}

	public JSONObject getRecordingUser() {
		return new JSONObject(recordingUser.toString());
	}

	public void startRecording(Client c) {
		if (recordingStarted.compareAndSet(false, true)) {
			log.debug("##REC:: recording in room {} is starting ::", roomId);
			Room r = c.getRoom();
			boolean interview = Room.Type.INTERVIEW == r.getType();

			Date now = new Date();

			Recording rec = new Recording();

			rec.setHash(randomUUID().toString());
			rec.setName(String.format("%s %s", interview ? "Interview" : "Recording", CalendarPatterns.getDateWithTimeByMiliSeconds(new Date())));
			User u = c.getUser();
			recordingUser.put("login", u.getLogin());
			recordingUser.put("firstName", u.getFirstname());
			recordingUser.put("lastName", u.getLastname());
			recordingUser.put("started", now.getTime());
			Long ownerId = User.Type.CONTACT == u.getType() ? u.getOwnerId() : u.getId();
			rec.setInsertedBy(ownerId);
			rec.setType(BaseFileItem.Type.RECORDING);
			rec.setInterview(interview);

			rec.setRoomId(roomId);
			rec.setRecordStart(now);

			rec.setOwnerId(ownerId);
			rec.setStatus(Recording.Status.RECORDING);
			log.debug("##REC:: recording created by USER: {}", ownerId);

			Optional<StreamDesc> osd = c.getScreenStream();
			if (osd.isPresent()) {
				osd.get().addActivity(Activity.RECORD);
				processor.getClientManager().update(c);
				rec.setWidth(osd.get().getWidth());
				rec.setHeight(osd.get().getHeight());
			}
			rec = processor.getRecordingDao().update(rec);
			// Receive recordingId
			recordingId = rec.getId();
			processor.getByRoom(this.getRoomId()).forEach(
					stream -> stream.startRecord(processor)
			);

			// Send notification to all users that the recording has been started
			WebSocketHelper.sendRoom(new RoomMessage(roomId, u, RoomMessage.Type.RECORDING_TOGGLED));
			log.debug("##REC:: recording in room {} is started {} ::", roomId, recordingId);
		}
	}

	public void stopRecording(Client c) {
		if (recordingStarted.compareAndSet(true, false)) {
			log.debug("##REC:: recording in room {} is stopping {} ::", roomId, recordingId);
			processor.getByRoom(this.getRoomId()).forEach(KStream::stopRecord);
			Recording rec = processor.getRecordingDao().get(recordingId);
			rec.setRecordEnd(new Date());
			rec = processor.getRecordingDao().update(rec);
			recordingUser = new JSONObject();
			recordingId = null;

			processor.startConvertion(rec);
			User u;
			if (c == null) {
				u = new User();
			} else {
				u = c.getUser();
				Optional<StreamDesc> osd = c.getScreenStream();
				if (osd.isPresent()) {
					osd.get().removeActivity(Activity.RECORD);
					processor.getClientManager().update(c);
					processor.getHandler().sendShareUpdated(osd.get());
				}
			}
			// Send notification to all users that the recording has been started
			WebSocketHelper.sendRoom(new RoomMessage(roomId, u, RoomMessage.Type.RECORDING_TOGGLED));
			log.debug("##REC:: recording in room {} is stopped ::", roomId);
		}
	}

	/**
	 * This method will return true, even if the sharing is not enabled. But just recording.
	 * Cause in order to record you need to have a Screensharing enabled. Doesn't mean that other
	 * users see that screenshare yet (permissions have not been granted).
	 *
	 * @return
	 */
	public boolean isSharing() {
		return sharingStarted.get();
	}

	public JSONObject getSharingUser() {
		return new JSONObject(sharingUser.toString());
	}

	public void startSharing(StreamProcessor processor, IClientManager cm, Client c, Optional<StreamDesc> osd, JSONObject msg, Activity a) {
		StreamDesc sd;
		KurentoHandler h = processor.getHandler();
		if (sharingStarted.compareAndSet(false, true)) {
			sharingUser.put("sid", c.getSid());
			sd = c.addStream(StreamType.SCREEN, a);
			sd.setWidth(msg.getInt("width")).setHeight(msg.getInt("height"));
			cm.update(c);
			log.debug("Stream.UID {}: sharing has been started, activity: {}", sd.getUid(), a);
			h.sendClient(sd.getSid(), newKurentoMsg()
					.put("id", "broadcast")
					.put("stream", sd.toJson()
							.put("shareType", msg.getString("shareType"))
							.put("fps", msg.getString("fps")))
					.put(PARAM_ICE, h.getTurnServers(c)));
		} else if (osd.isPresent() && !osd.get().hasActivity(a)) {
			sd = osd.get();
			sd.addActivity(a);
			cm.update(c);
			h.sendShareUpdated(sd);
			WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.RIGHT_UPDATED, c.getUid()));
			WebSocketHelper.sendRoomOthers(roomId, c.getUid(), newKurentoMsg()
					.put("id", "newStream")
					.put(PARAM_ICE, processor.getHandler().getTurnServers(c))
					.put("stream", sd.toJson()));
		}
	}

	public void stopSharing() {
		if (sharingStarted.compareAndSet(true, false)) {
			sharingUser = new JSONObject();
		}
	}

	public void close() {
		processor.getByRoom(this.getRoomId()).forEach(
				stream -> stream.release(processor)
		);
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
