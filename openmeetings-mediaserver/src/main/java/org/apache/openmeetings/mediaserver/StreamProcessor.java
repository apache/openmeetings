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

import static org.apache.openmeetings.mediaserver.KurentoHandler.PARAM_ICE;
import static org.apache.openmeetings.mediaserver.KurentoHandler.newKurentoMsg;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isRecordingsEnabled;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.openmeetings.core.converter.IRecordingConverter;
import org.apache.openmeetings.core.converter.InterviewConverter;
import org.apache.openmeetings.core.converter.RecordingConverter;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;
import org.apache.openmeetings.db.entity.basic.ScreenStreamDesc;
import org.apache.openmeetings.db.entity.basic.StreamDesc;
import org.apache.openmeetings.db.entity.basic.WebcamStreamDesc;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.logging.TimedApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.task.TaskExecutor;

import com.github.openjson.JSONObject;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named
public class StreamProcessor {
	private static final Logger log = LoggerFactory.getLogger(StreamProcessor.class);
	/**
	 * Holds references to the current streams available on the server instance
	 */
	private final Map<String, KStream> streamByUid = new ConcurrentHashMap<>();

	@Inject
	private IClientManager cm;
	@Inject
	private RecordingDao recDao;
	@Inject
	private KurentoHandler kHandler;
	@Inject
	private TaskExecutor taskExecutor;
	@Inject
	private RecordingConverter recordingConverter;
	@Inject
	private InterviewConverter interviewConverter;
	@Inject
	private StreamProcessorActions streamProcessorActions;

	@TimedApplication
	void onMessage(Client c, final String cmdId, JSONObject msg) {
		final String uid = msg.optString("uid");
		log.debug("Incoming message from user with ID '{}': {}", c.getUserId(), msg);
		switch (cmdId) {
			case "devicesAltered":
				onDeviceAltered(c, uid, msg);
				break;
			case "toggleActivity":
				onToggleActivity(c, Activity.valueOf(msg.getString("activity")));
				break;
			case "broadcastStarted":
				streamProcessorActions.handleBroadcastStarted(c, uid, msg);
				break;
			case "broadcastRestarted":
				streamProcessorActions.handleBroadcastRestarted(c, uid);
				break;
			case "onIceCandidate":
				streamProcessorActions.addIceCandidate(msg);
				break;
			case "addListener":
				streamProcessorActions.addListener(c, msg);
				break;
			case "wannaShare": {
				Optional<ScreenStreamDesc> osd = c.getScreenStream();
				if (screenShareAllowed(c) || (osd.isPresent() && !osd.get().has(Activity.SCREEN))) {
					startSharing(c, osd, msg, Activity.SCREEN);
				}
			}
				break;
			case "wannaRecord":
				onWannaRecord(c, msg);
				break;
			case "pauseSharing":
				pauseSharing(c, uid);
				break;
			case "stopRecord":
				stopRecording(c);
				break;
			case "errorSharing":
				errorSharing(c);
				break;
			default:
				// no-op
				break;
		}
	}

	private void onDeviceAltered(Client c, String uid, JSONObject msg) {
		StreamDesc sd = c.getStream(uid);
		if (sd != null) {
			if (!msg.getBoolean("audio") && sd.has(Activity.AUDIO)) {
				sd.remove(Activity.AUDIO);
			}
			if (!msg.getBoolean("video") && sd.has(Activity.VIDEO)) {
				sd.remove(Activity.VIDEO);
			}
			WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), cm.update(c), RoomMessage.Type.RIGHT_UPDATED, c.getUid()));
		}
	}

	private void onWannaRecord(Client c, JSONObject msg) {
		Optional<ScreenStreamDesc> osd = c.getScreenStream();
		if (recordingAllowed(c)) {
			Room r = c.getRoom();
			if (r.isInterview()) {
				log.warn("This shouldn't be called for interview room");
				return;
			}
			boolean sharing = isSharing(r.getId());
			startSharing(c, osd, msg, Activity.RECORD);
			if (sharing) {
				startRecording(c);
			}
		}
	}

	/**
	 *  Method to start broadcasting.  Externalised for mocking purpose to be able to
	 *  prevent calling webRTC methods.
	 *
	 * @param stream Stream to start
	 * @param sd StreamDesc to start
	 * @param sdpOffer the sdpOffer
	 * @param then steps need to be done after broadcast is started
	 * @return the current KStream
	 */
	void startBroadcast(KStream stream, StreamDesc sd, String sdpOffer, Runnable then) {
		stream.startBroadcast(sd, sdpOffer, then);
	}

	@TimedApplication
	public void onToggleActivity(Client c, Activity a) {
		log.info("PARTICIPANT {}: trying to toggle activity {}", c, a);
		if (!kHandler.isConnected()) {
			return;
		}

		if (c.isAllowed(a)) {
			if (a == Activity.AUDIO && !c.isMicEnabled()) {
				return;
			}
			if (a == Activity.VIDEO && !c.isCamEnabled()) {
				return;
			}
			if (a == Activity.AUDIO_VIDEO && !c.isMicEnabled() && !c.isCamEnabled()) {
				return;
			}
			Optional<WebcamStreamDesc> cam = c.getCamStreams().findFirst();
			if (cam.isPresent()) {
				WebcamStreamDesc camStr = cam.get();
				camStr.toggle(a);
				if (!camStr.isBroadcasting()) {
					KStream s = getByUid(camStr.getUid());
					if (s != null) {
						s.stopBroadcast();
					}
					c.removeStream(camStr.getUid());
				}
				cm.update(c);
				WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.RIGHT_UPDATED, c.getUid()));
			} else {
				StreamDesc sd = c.addStream(StreamType.WEBCAM, a);
				log.debug("User {}: has started broadcast", sd.getUid());
				kHandler.sendClient(sd.getSid(), newKurentoMsg()
						.put("id", "broadcast")
						.put("stream", sd.toJson(true))
						.put(PARAM_ICE, kHandler.getTurnServers(c, false)));
			}
		}
	}

	public void rightsUpdated(Client c) {
		Optional<ScreenStreamDesc> osd = c.getScreenStream();
		if (osd.isPresent() && !hasRightsToShare(c)) {
			stopSharing(c, osd.get().getUid());
		}
		c.getCamStreams()
			.filter(sd -> !sd.isBroadcasting())
			.map(sd -> streamByUid.get(sd.getUid()))
			.forEach(stream -> {
				KRoom room = kHandler.getRoom(c.getRoomId());
				room.onStopBroadcast(stream);
			});
		WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.RIGHT_UPDATED, c.getUid()));
	}

	private void checkStreams(Long roomId) {
		if (!kHandler.isConnected()) {
			return;
		}
		KRoom kRoom = kHandler.getRoom(roomId);
		if (kRoom.isSharing() && cm.streamByRoom(roomId)
					.flatMap(c -> c.getStreams().stream())
					.filter(sd -> StreamType.SCREEN == sd.getType())
					.findAny()
					.isEmpty())
		{
			log.info("No more screen streams in the room, stopping sharing");
			kRoom.stopSharing();
			if (!kRoom.getRoom().isInterview() && kRoom.isRecording()) {
				log.info("No more screen streams in the non-interview room, stopping recording");
				kRoom.stopRecording(null);
			}
		}
		if (kRoom.isRecording() && cm.streamByRoom(roomId)
				.flatMap(c -> c.getStreams().stream())
				.findAny()
				.isEmpty())
		{
			log.info("No more streams in the room, stopping recording");
			kRoom.stopRecording(null);
		}
	}

	// Sharing
	public boolean hasRightsToShare(Client c) {
		if (!kHandler.isConnected()) {
			return false;
		}
		Room r = c.getRoom();
		return r != null && !r.isInterview()
				&& !r.isHidden(RoomElement.SCREEN_SHARING)
				&& c.hasRight(Right.SHARE);
	}

	public boolean screenShareAllowed(Client c) {
		Room r = c.getRoom();
		return hasRightsToShare(c) && !isSharing(r.getId());
	}

	private void errorSharing(Client c) {
		if (!kHandler.isConnected()) {
			return;
		}
		KRoom room = kHandler.getRoom(c.getRoomId());
		if (!room.isSharing() || !c.getSid().equals(room.getSharingUser().getString("sid"))) {
			return;
		}
		Optional<ScreenStreamDesc> osd = c.getScreenStream();
		if (osd.isPresent()) {
			stopSharing(c, osd.get().getUid());
		} else {
			room.stopSharing();
		}
		stopRecording(c);
	}

	private void startSharing(Client c, Optional<ScreenStreamDesc> osd, JSONObject msg, Activity a) {
		if (kHandler.isConnected() && c.getRoomId() != null) {
			kHandler.getRoom(c.getRoomId()).startSharing(c, osd, msg, a);
		}
	}

	/**
	 * Execute Pausing of sharing.
	 *
	 * Invoked and overwritten by Mock, hance package private.
	 *
	 * @param c client
	 * @param uid the uid
	 */
	void pauseSharing(Client c, String uid) {
		if (!hasRightsToShare(c)) {
			return;
		}
		if (!isSharing(c.getRoomId())) {
			return;
		}
		if (isRecording(c.getRoomId())) {
			ScreenStreamDesc sd = (ScreenStreamDesc)c.getStream(uid);
			sd.remove(Activity.SCREEN);
			cm.update(c);
			KStream sender = getByUid(uid);
			sender.pauseSharing();
			kHandler.sendShareUpdated(sd);
			WebSocketHelper.sendRoomOthers(c.getRoomId(), c.getUid(), newStoppedMsg(sd));
		} else {
			stopSharing(c, uid);
		}
	}

	private void stopSharing(Client c, String uid) {
		KStream sender = getByUid(uid);
		StreamDesc sd = doStopSharing(c.getSid(), uid);
		if (sender != null && sd != null) {
			sender.stopBroadcast();
		} else {
			log.warn("Could not stop broadcast - could be a KStream leak and lead to ghost KStream, client: {}, uid: {} ", c, uid);
		}
	}

	StreamDesc doStopSharing(String sid, String uid) {
		return doStopSharing(getBySid(sid), uid);
	}

	private StreamDesc doStopSharing(Client c, String uid) {
		StreamDesc sd = null;
		if (c.getRoomId() != null) {
			sd = c.getStream(uid);
			if (sd instanceof ScreenStreamDesc scr) {
				c.removeStream(uid);
				cm.update(c);
				checkStreams(c.getRoomId());
				WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.RIGHT_UPDATED, c.getUid()));
				kHandler.sendShareUpdated(scr.remove(Activity.SCREEN).remove(Activity.RECORD));
			}
		}
		return sd;
	}

	public boolean isSharing(Long roomId) {
		if (!kHandler.isConnected()) {
			return false;
		}
		return kHandler.getRoom(roomId).isSharing();
	}

	// Recording

	public boolean hasRightsToRecord(Client c) {
		Room r = c.getRoom();
		return isRecordingsEnabled() && r != null && r.isAllowRecording() && c.hasRight(Right.MODERATOR);
	}

	public boolean recordingAllowed(Client c) {
		if (!kHandler.isConnected() || !isRecordingsEnabled()) {
			return false;
		}
		Room r = c.getRoom();
		return hasRightsToRecord(c) && !isRecording(r.getId());
	}

	public void startRecording(Client c) {
		if (!kHandler.isConnected() || !hasRightsToRecord(c)) {
			return;
		}
		kHandler.getRoom(c.getRoomId()).startRecording(c);
	}

	public void stopRecording(Client c) {
		if (!kHandler.isConnected() || !hasRightsToRecord(c)) {
			return;
		}
		kHandler.getRoom(c.getRoomId()).stopRecording(c);

		// In case this user wasn't shareing his screen we also need to close that one
		c.getScreenStream().ifPresent(sd -> {
			if (!sd.has(Activity.SCREEN)) {
				pauseSharing(c, sd.getUid());
			}
		});
	}

	/**
	 * Used for mocking. Requires a return value in order to be mocked.
	 *
	 * @param rec
	 * @return
	 */
	boolean startConvertion(Recording rec) {
		IRecordingConverter conv = rec.isInterview() ? interviewConverter : recordingConverter;
		taskExecutor.execute(() -> conv.startConversion(rec));
		return true;
	}

	public boolean isRecording(Long roomId) {
		if (!kHandler.isConnected()) {
			return false;
		}
		return kHandler.getRoom(roomId).isRecording();
	}

	void remove(Client c) {
		for (StreamDesc sd : c.getStreams()) {
			AbstractStream s = getByUid(sd.getUid());
			if (s != null) {
				s.release();
				WebSocketHelper.sendRoomOthers(c.getRoomId(), c.getUid(), newStoppedMsg(sd));
			}
		}
		if (c.getRoomId() != null) {
			getByRoom(c.getRoomId()).forEach(stream -> stream.remove(c)); // listeners of existing streams should be cleaned-up
			checkStreams(c.getRoomId());
		}
	}

	void addStream(KStream stream) {
		streamByUid.put(stream.getUid(), stream);
	}

	public Collection<KStream> getStreams() {
		return streamByUid.values();
	}

	Stream<KStream> getByRoom(Long roomId) {
		return streamByUid.values().stream()
				.filter(stream -> stream.getRoomId().equals(roomId));
	}

	Client getBySid(String sid) {
		return cm.getBySid(sid);
	}

	public boolean hasStream(String uid) {
		return streamByUid.get(uid) != null;
	}

	KStream getByUid(String uid) {
		return uid == null ? null : streamByUid.get(uid);
	}

	KurentoHandler getHandler() {
		return kHandler;
	}

	IClientManager getClientManager() {
		return cm;
	}

	RecordingDao getRecordingDao() {
		return recDao;
	}

	public void release(AbstractStream stream, boolean releaseStream) {
		final String uid = stream.getUid();
		if (releaseStream) {
			stream.release();
		}
		Client c = cm.getBySid(stream.getSid());
		if (c != null) {
			StreamDesc sd = c.getStream(uid);
			if (sd != null) {
				c.removeStream(uid);
				cm.update(c);
				WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.RIGHT_UPDATED, c.getUid()));
			}
		}
		log.trace("StreamProcessor::release uid: {}", uid);
		streamByUid.remove(uid);
	}

	public void destroy() {
		log.trace("StreamProcessor::destroy !!!!");
		for (Map.Entry<String, KStream> e : streamByUid.entrySet()) {
			release(e.getValue(), true);
		}
	}

	protected static JSONObject newStoppedMsg(StreamDesc sd) {
		return newKurentoMsg()
				.put("id", "broadcastStopped")
				.put("uid", sd.getUid());
	}
}
