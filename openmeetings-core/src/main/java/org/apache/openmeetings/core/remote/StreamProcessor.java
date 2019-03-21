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

import static org.apache.openmeetings.core.remote.KurentoHandler.PARAM_CANDIDATE;
import static org.apache.openmeetings.core.remote.KurentoHandler.PARAM_ICE;
import static org.apache.openmeetings.core.remote.KurentoHandler.activityAllowed;
import static org.apache.openmeetings.core.remote.KurentoHandler.newKurentoMsg;
import static org.apache.openmeetings.core.remote.KurentoHandler.sendError;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.openmeetings.core.converter.IRecordingConverter;
import org.apache.openmeetings.core.converter.InterviewConverter;
import org.apache.openmeetings.core.converter.RecordingConverter;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.kurento.client.IceCandidate;
import org.kurento.client.internal.server.KurentoServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.github.openjson.JSONObject;

@Component
public class StreamProcessor implements IStreamProcessor {
	private static final Logger log = LoggerFactory.getLogger(StreamProcessor.class);
	private final Map<String, KStream> streamByUid = new ConcurrentHashMap<>();

	@Autowired
	private IClientManager cm;
	@Autowired
	private RecordingDao recDao;
	@Autowired
	private KurentoHandler kHandler;
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private RecordingConverter recordingConverter;
	@Autowired
	private InterviewConverter interviewConverter;

	void onMessage(Client c, final String cmdId, JSONObject msg) {
		final String uid = msg.optString("uid");
		KStream sender;
		StreamDesc sd;
		Optional<StreamDesc> osd;
		log.debug("Incoming message from user with ID '{}': {}", c.getUserId(), msg);
		switch (cmdId) {
			case "devicesAltered":
				if (!msg.getBoolean("audio") && c.hasActivity(Activity.AUDIO)) {
					c.remove(Activity.AUDIO);
				}
				if (!msg.getBoolean("video") && c.hasActivity(Activity.VIDEO)) {
					c.remove(Activity.VIDEO);
				}
				c.getStream(uid).setActivities();
				WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), cm.update(c), RoomMessage.Type.rightUpdated, c.getUid()));
				break;
			case "toggleActivity":
				toggleActivity(c, Activity.valueOf(msg.getString("activity")));
				break;
			case "broadcastStarted":
				handleBroadcastStarted(c, uid, msg);
				break;
			case "onIceCandidate":
				sender = getByUid(uid);
				if (sender != null) {
					JSONObject candidate = msg.getJSONObject(PARAM_CANDIDATE);
					IceCandidate cand = new IceCandidate(
							candidate.getString(PARAM_CANDIDATE)
							, candidate.getString("sdpMid")
							, candidate.getInt("sdpMLineIndex"));
					sender.addCandidate(cand, msg.getString("luid"));
				}
				break;
			case "addListener":
				sender = getByUid(msg.getString("sender"));
				if (sender != null) {
					Client sendClient = cm.getBySid(sender.getSid());
					sd = sendClient.getStream(sender.getUid());
					if (StreamType.SCREEN == sd.getType() && sd.hasActivity(Activity.RECORD) && !sd.hasActivity(Activity.SCREEN)) {
						break;
					}
					sender.addListener(this, c.getSid(), c.getUid(), msg.getString("sdpOffer"));
				}
				break;
			case "wannaShare":
				osd = c.getScreenStream();
				if (screenShareAllowed(c) || (osd.isPresent() && !osd.get().hasActivity(Activity.SCREEN))) {
					startSharing(c, osd, msg, Activity.SCREEN);
				}
				break;
			case "wannaRecord":
				osd = c.getScreenStream();
				if (recordingAllowed(c)) {
					Room r = c.getRoom();
					if (Room.Type.interview == r.getType()) {
						log.warn("This shouldn't be called for interview room");
						break;
					}
					boolean sharing = isSharing(r.getId());
					startSharing(c, osd, msg, Activity.RECORD);
					if (sharing) {
						startRecording(c);
					}
				}
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

	private void handleBroadcastStarted(Client c, final String uid, JSONObject msg) {
		StreamDesc sd = c.getStream(uid);
		KStream sender= getByUid(uid);
		try {
			if (sender == null) {
				KRoom room = kHandler.getRoom(c.getRoomId());
				sender = room.join(sd);
			}
			sender.startBroadcast(this, sd, msg.getString("sdpOffer"));
			if (StreamType.SCREEN == sd.getType() && sd.hasActivity(Activity.RECORD) && !isRecording(c.getRoomId())) {
				startRecording(c);
			}
		} catch (KurentoServerException e) {
			sender.release(this);
			WebSocketHelper.sendClient(c, newKurentoMsg()
					.put("id", "broadcastStopped")
					.put("uid", sd.getUid())
				);
			sendError(c, "Failed to start broadcast: " + e.getMessage());
			log.error("Failed to start broadcast", e);
		}
	}

	private static boolean isBroadcasting(final Client c) {
		return c.hasAnyActivity(Activity.AUDIO, Activity.VIDEO);
	}

	public void toggleActivity(Client c, Activity a) {
		log.info("PARTICIPANT {}: trying to toggle activity {}", c, a);

		if (!activityAllowed(c, a, c.getRoom())) {
			if (a == Activity.AUDIO || a == Activity.AUDIO_VIDEO) {
				c.allow(Room.Right.audio);
			}
			if (!c.getRoom().isAudioOnly() && (a == Activity.VIDEO || a == Activity.AUDIO_VIDEO)) {
				c.allow(Room.Right.video);
			}
		}
		if (activityAllowed(c, a, c.getRoom())) {
			boolean wasBroadcasting = isBroadcasting(c);
			if (a == Activity.AUDIO && !c.isMicEnabled()) {
				return;
			}
			if (a == Activity.VIDEO && !c.isCamEnabled()) {
				return;
			}
			if (a == Activity.AUDIO_VIDEO && !c.isMicEnabled() && !c.isCamEnabled()) {
				return;
			}
			c.toggle(a);
			if (!isBroadcasting(c)) {
				//close
				AtomicBoolean changed = new AtomicBoolean(false);
				c.getStreams().stream()
					.filter(sd -> StreamType.WEBCAM == sd.getType())
					.forEach(sd -> {
						KStream s = getByUid(sd.getUid());
						if (s != null) {
							s.stopBroadcast(this);
						}
						c.removeStream(sd.getUid());
						changed.set(true);
					});
				if (changed.get()) {
					cm.update(c);
					checkStreams(c.getRoomId());
				}
				WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
			} else if (!wasBroadcasting) {
				//join
				StreamDesc sd = c.addStream(StreamType.WEBCAM);
				cm.update(c);
				log.debug("User {}: has started broadcast", sd.getUid());
				kHandler.sendClient(sd.getSid(), newKurentoMsg()
						.put("id", "broadcast")
						.put("stream", sd.toJson())
						.put(PARAM_ICE, kHandler.getTurnServers(false)));
			} else {
				constraintsChanged(c);
				WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
			}
		}
	}

	private void constraintsChanged(Client c) {
		//constraints were changed
		c.getStreams().stream()
			.filter(sd -> StreamType.WEBCAM == sd.getType())
			.findFirst()
			.ifPresent(sd -> {
				sd.setActivities();
				cm.update(c);
			});
	}

	public void rightsUpdated(Client c) {
		Optional<StreamDesc> osd = c.getScreenStream();
		if (osd.isPresent() && !hasRightsToShare(c)) {
			stopSharing(c, osd.get().getUid());
		}
		if (isBroadcasting(c)) {
			constraintsChanged(c);
		} else {
			c.getStreams().stream()
				.filter(sd -> StreamType.WEBCAM == sd.getType())
				.forEach(sd -> {
					KStream stream = streamByUid.get(sd.getUid());
					if (stream != null) {
						KRoom room = kHandler.getRoom(c.getRoomId());
						room.onStopBroadcast(stream, this);
					}
				});
		}
		WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
	}

	private void checkStreams(Long roomId) {
		if (!kHandler.isConnected()) {
			return;
		}
		KRoom room = kHandler.getRoom(roomId);
		if (room.isSharing()) {
			List<StreamDesc> streams = cm.listByRoom(roomId).parallelStream()
					.flatMap(c -> c.getStreams().stream())
					.filter(sd -> StreamType.SCREEN == sd.getType()).collect(Collectors.toList());
			if (streams.isEmpty()) {
				log.info("No more screen streams in the room, stopping sharing");
				room.stopSharing();
				if (Room.Type.interview != room.getType() && room.isRecording()) {
					log.info("No more screen streams in the non-interview room, stopping recording");
					room.stopRecording(this, null);
				}
			}
		}
		if (room.isRecording()) {
			List<StreamDesc> streams = cm.listByRoom(roomId).parallelStream()
					.flatMap(c -> c.getStreams().stream())
					.collect(Collectors.toList());
			if (streams.isEmpty()) {
				log.info("No more streams in the room, stopping recording");
				room.stopRecording(this, null);
			}
		}
	}

	// Sharing
	public boolean hasRightsToShare(Client c) {
		Room r = c.getRoom();
		return r != null && Room.Type.interview != r.getType()
				&& !r.isHidden(RoomElement.ScreenSharing)
				&& r.isAllowRecording()
				&& c.hasRight(Right.share);
	}

	public boolean screenShareAllowed(Client c) {
		if (!kHandler.isConnected()) {
			return false;
		}
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
		Optional<StreamDesc> osd = c.getScreenStream();
		if (osd.isPresent()) {
			stopSharing(c, osd.get().getUid());
		} else {
			room.stopSharing();
		}
		stopRecording(c);
	}

	private void startSharing(Client c, Optional<StreamDesc> osd, JSONObject msg, Activity a) {
		if (kHandler.isConnected() && c.getRoomId() != null) {
			kHandler.getRoom(c.getRoomId()).startSharing(this, cm, c, osd, msg, a);
		}
	}

	private void pauseSharing(Client c, String uid) {
		if (!hasRightsToShare(c)) {
			return;
		}
		if (!isSharing(c.getRoomId())) {
			return;
		}
		if (isRecording(c.getRoomId())) {
			StreamDesc sd = c.getStream(uid);
			sd.removeActivity(Activity.SCREEN);
			cm.update(c);
			KStream sender = getByUid(uid);
			sender.pauseSharing();
			kHandler.sendShareUpdated(sd);
			WebSocketHelper.sendRoomOthers(c.getRoomId(), c.getUid(), newKurentoMsg()
					.put("id", "broadcastStopped")
					.put("uid", sd.getUid())
				);
		} else {
			stopSharing(c, uid);
		}
	}

	private void stopSharing(Client c, String uid) {
		KStream sender = getByUid(uid);
		StreamDesc sd = doStopSharing(c.getSid(), uid);
		if (sender != null && sd != null) {
			sender.stopBroadcast(this);
		}
	}

	StreamDesc doStopSharing(String sid, String uid) {
		return doStopSharing(getBySid(sid), uid);
	}

	private StreamDesc doStopSharing(Client c, String uid) {
		StreamDesc sd = null;
		if (c.getRoomId() != null) {
			sd = c.getStream(uid);
			if (sd != null && StreamType.SCREEN == sd.getType()) {
				c.removeStream(uid);
				cm.update(c);
				checkStreams(c.getRoomId());
				WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.rightUpdated, c.getUid()));
				kHandler.sendShareUpdated(sd
						.removeActivity(Activity.SCREEN)
						.removeActivity(Activity.RECORD));
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
		return r != null && r.isAllowRecording() && c.hasRight(Right.moderator);
	}

	public boolean recordingAllowed(Client c) {
		if (!kHandler.isConnected()) {
			return false;
		}
		Room r = c.getRoom();
		return hasRightsToRecord(c) && !isRecording(r.getId());
	}

	public void startRecording(Client c) {
		if (!kHandler.isConnected() || !hasRightsToRecord(c)) {
			return;
		}
		kHandler.getRoom(c.getRoomId()).startRecording(this, c);
	}

	public void stopRecording(Client c) {
		if (!kHandler.isConnected() || !hasRightsToRecord(c)) {
			return;
		}
		kHandler.getRoom(c.getRoomId()).stopRecording(this, c);
	}

	void startConvertion(Recording rec) {
		IRecordingConverter conv = rec.isInterview() ? interviewConverter : recordingConverter;
		taskExecutor.execute(() -> conv.startConversion(rec));
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
				s.release(this);
			}
		}
		if (c.getRoomId() != null) {
			KRoom room = kHandler.getRoom(c.getRoomId());
			room.leave(this, c);
			checkStreams(c.getRoomId());
		}
	}

	void addStream(KStream stream) {
		streamByUid.put(stream.getUid(), stream);
	}

	Client getBySid(String sid) {
		return cm.getBySid(sid);
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

	@Override
	public void release(AbstractStream stream) {
		final String uid = stream.getUid();
		Client c = cm.getBySid(stream.getSid());
		if (c != null) {
			c.removeStream(uid);
			cm.update(c);
		}
		streamByUid.remove(uid);
	}

	@Override
	public void destroy() {
		streamByUid.clear();
	}
}
