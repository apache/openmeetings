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
package org.apache.openmeetings.mediaserver;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.mediaserver.KurentoHandler.PARAM_ICE;
import static org.apache.openmeetings.mediaserver.KurentoHandler.newKurentoMsg;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.inject.Inject;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.ScreenStreamDesc;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.StreamDesc;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.FormatHelper;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.wicket.injection.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;

/**
 * Dynamically created object representing a conference room on the MediaServer
 *
 */
public class KRoom {
	private static final Logger log = LoggerFactory.getLogger(KRoom.class);

	@Inject
	private KurentoHandler kHandler;
	@Inject
	private StreamProcessor processor;
	@Inject
	private RecordingDao recDao;
	@Inject
	private IClientManager cm;

	private final Room room;
	private final AtomicBoolean recordingStarted = new AtomicBoolean(false);
	private final AtomicBoolean sharingStarted = new AtomicBoolean(false);
	private Long recordingId = null;
	private long sipCount = 0;
	private JSONObject recordingUser = new JSONObject();
	private JSONObject sharingUser = new JSONObject();

	KRoom(Room r) {
		this.room = r;
		Injector.get().inject(this);
		log.info("ROOM {} has been created", room.getId());
	}

	public Room getRoom() {
		return room;
	}

	public Long getRecordingId() {
		return recordingId;
	}

	public KStream join(final StreamDesc sd) {
		log.info("ROOM {}: join client {}, stream: {}", room.getId(), sd.getClient(), sd.getUid());
		final KStream stream = new KStream(sd, this);
		processor.addStream(stream);
		return stream;
	}

	public void onStopBroadcast(KStream stream) {
		processor.release(stream, true);
		WebSocketHelper.sendAll(newKurentoMsg()
				.put("id", "broadcastStopped")
				.put("uid", stream.getUid())
				.toString()
			);
	}

	public boolean isRecording() {
		return recordingStarted.get();
	}

	public JSONObject getRecordingUser() {
		return new JSONObject(recordingUser.toString());
	}

	public void startRecording(Client c) {
		if (recordingStarted.compareAndSet(false, true)) {
			IApplication app = ensureApplication(c.getUser().getLanguageId());

			log.debug("##REC:: recording in room {} is starting ::", room.getId());
			Room r = c.getRoom();

			Date now = new Date();

			Recording rec = new Recording();

			rec.setHash(randomUUID().toString());
			final FastDateFormat fdf = FormatHelper.getDateTimeFormat(c.getUser());
			rec.setName(app.getOmString(r.isInterview() ? "file.name.interview" : "file.name.recording", c.getUser().getLanguageId())
					+ fdf.format(new Date()));
			User u = c.getUser();
			recordingUser.put("login", u.getLogin());
			recordingUser.put("firstName", u.getFirstname());
			recordingUser.put("lastName", u.getLastname());
			recordingUser.put("started", now.getTime());
			Long ownerId = User.Type.CONTACT == u.getType() ? u.getOwnerId() : u.getId();
			rec.setInsertedBy(ownerId);
			rec.setType(BaseFileItem.Type.RECORDING);
			rec.setInterview(r.isInterview());

			rec.setRoomId(room.getId());
			rec.setRecordStart(now);

			rec.setOwnerId(ownerId);
			rec.setStatus(Recording.Status.RECORDING);
			log.debug("##REC:: recording created by USER: {}", ownerId);

			Optional<ScreenStreamDesc> osd = c.getScreenStream();
			if (osd.isPresent()) {
				osd.get().add(Activity.RECORD);
				cm.update(c);
				rec.setWidth(osd.get().getWidth());
				rec.setHeight(osd.get().getHeight());
			}
			rec = recDao.update(rec);
			// Receive recordingId
			recordingId = rec.getId();
			processor.getByRoom(room.getId()).forEach(KStream::startRecord);

			// Send notification to all users that the recording has been started
			WebSocketHelper.sendRoom(new RoomMessage(room.getId(), u, RoomMessage.Type.RECORDING_TOGGLED));
			log.debug("##REC:: recording in room {} is started {} ::", room.getId(), recordingId);
		}
	}

	public void stopRecording(Client c) {
		if (recordingStarted.compareAndSet(true, false)) {
			log.debug("##REC:: recording in room {} is stopping {} ::", room.getId(), recordingId);
			processor.getByRoom(room.getId()).forEach(KStream::stopRecord);
			Recording rec = recDao.get(recordingId);
			rec.setRecordEnd(new Date());
			rec = recDao.update(rec);
			recordingUser = new JSONObject();
			recordingId = null;

			processor.startConvertion(rec);
			User u;
			if (c == null) {
				u = new User();
			} else {
				u = c.getUser();
				Optional<ScreenStreamDesc> osd = c.getScreenStream();
				if (osd.isPresent()) {
					osd.get().remove(Activity.RECORD);
					cm.update(c);
					kHandler.sendShareUpdated(osd.get());
				}
			}
			// Send notification to all users that the recording has been started
			WebSocketHelper.sendRoom(new RoomMessage(room.getId(), u, RoomMessage.Type.RECORDING_TOGGLED));
			log.debug("##REC:: recording in room {} is stopped ::", room.getId());
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

	public void startSharing(Client c, Optional<ScreenStreamDesc> osd, JSONObject msg, Activity a) {
		ScreenStreamDesc sd;
		if (sharingStarted.compareAndSet(false, true)) {
			sharingUser.put("sid", c.getSid());
			sd = (ScreenStreamDesc)c.addStream(StreamType.SCREEN, a);
			cm.update(c);
			log.debug("Stream.UID {}: sharing has been started, activity: {}", sd.getUid(), a);
			kHandler.sendClient(sd.getSid(), newKurentoMsg()
					.put("id", "broadcast")
					.put("stream", sd.toJson()
							.put("shareType", msg.getString("shareType"))
							.put("fps", msg.getString("fps")))
					.put(PARAM_ICE, kHandler.getTurnServers(c)));
		} else if (osd.isPresent() && !osd.get().has(a)) {
			sd = osd.get();
			sd.add(a);
			cm.update(c);
			kHandler.sendShareUpdated(sd);
			WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.RIGHT_UPDATED, c.getUid()));
			WebSocketHelper.sendRoomOthers(room.getId(), c.getUid(), newKurentoMsg()
					.put("id", "newStream")
					.put(PARAM_ICE, kHandler.getTurnServers(c))
					.put("stream", sd.toJson()));
		}
	}

	public void stopSharing() {
		if (sharingStarted.compareAndSet(true, false)) {
			sharingUser = new JSONObject();
		}
	}

	public void close() {
		processor.getByRoom(room.getId()).forEach(KStream::release);
		log.debug("Room {} closed", room.getId());
	}

	public void updateSipCount(final long count) {
		if (count != sipCount) {
			processor.getByRoom(room.getId()).forEach(stream -> stream.addSipProcessor(count));
			if (sipCount == 0) {
				cm.streamByRoom(room.getId())
					.filter(Client::isSip)
					.findAny()
					.ifPresent(c -> {
						StreamDesc sd = c.addStream(StreamType.WEBCAM, Activity.AUDIO);
						sd.setWidth(120).setHeight(90);
						KStream stream = join(sd);
						stream.startBroadcast(sd, "", () -> {});
						cm.update(c);
					});
			}
			sipCount = count;
		}
	}

	public long getSipCount() {
		return sipCount;
	}
}
