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
package org.apache.openmeetings.core.service;

import static org.apache.openmeetings.core.converter.BaseConverter.printMetaInfo;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openmeetings.core.data.record.converter.InterviewConverterTask;
import org.apache.openmeetings.core.data.record.converter.RecordingConverterTask;
import org.apache.openmeetings.core.data.record.listener.StreamListener;
import org.apache.openmeetings.core.remote.ScopeApplicationAdapter;
import org.apache.openmeetings.core.util.IClientUtil;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.record.RecordingMetaDataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IClient;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.db.entity.record.RecordingMetaData.Status;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.manager.IStreamClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.stream.ClientBroadcastStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.openjson.JSONObject;

@Service
public class RecordingService {
	private static final Logger log = Red5LoggerFactory.getLogger(RecordingService.class, getWebAppRootKey());

	/**
	 * Stores a reference to all available listeners we need that reference, as the internal references stored with the
	 * red5 stream object might be gone when the user closes the browser. But each listener has an asynchronous
	 * component that needs to be closed no matter how the user leaves the application!
	 */
	private static final Map<Long, StreamListener> streamListeners = new ConcurrentHashMap<>();

	@Autowired
	private IStreamClientManager streamClientManager;
	@Autowired
	private IClientManager clientManager;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RecordingConverterTask recordingConverter;
	@Autowired
	private InterviewConverterTask interviewConverter;
	@Autowired
	private RecordingDao recordingDao;
	@Autowired
	private ScopeApplicationAdapter scopeAdapter;
	@Autowired
	private RecordingMetaDataDao metaDataDao;

	private static String generateFileName(Long recordingId, String streamid) {
		String dateString = CalendarPatterns.getTimeForStreamId(new Date());
		return "rec_" + recordingId + "_stream_" + streamid + "_" + dateString;
	}

	public void startRecording(final IScope scope, IClient client) {
		try {
			log.debug("##REC:: recordMeetingStream ::");
			boolean isInterview = Room.Type.interview == client.getRoomType();
			Long roomId = client.getRoomId();

			Date now = new Date();

			Recording recording = new Recording();

			recording.setHash(UUID.randomUUID().toString());
			recording.setName(String.format("%s %s", isInterview ? "Interview" : "Recording", CalendarPatterns.getDateWithTimeByMiliSeconds(new Date())));
			Long ownerId = client.getUserId();
			User u = userDao.get(ownerId);
			if (u != null && User.Type.contact == u.getType()) {
				ownerId = u.getOwnerId();
				u = userDao.get(ownerId);
			}
			recording.setInsertedBy(ownerId);
			recording.setType(Type.Recording);
			recording.setInterview(isInterview);

			recording.setRoomId(roomId);
			recording.setRecordStart(now);

			if (!isInterview) {
				recording.setWidth(client.getWidth());
				recording.setHeight(client.getHeight());
			}

			recording.setOwnerId(ownerId);
			recording.setStatus(Recording.Status.RECORDING);
			recording = recordingDao.update(recording);
			// Receive recordingId
			Long recordingId = recording.getId();
			IClientUtil.setRecordingId(scope, recordingId);
			log.debug("##REC:: recording created by USER: {}", ownerId);

			// Update Client and set Flag
			client.setRecordingStarted(true);
			if (!(client instanceof Client)) {
				Client c = clientManager.getBySid(client.getSid());
				c.setRecordingId(recordingId);
				c.setRecordingStarted(true);
			}
			streamClientManager.update(client);

			// get all stream and start recording them
			for (IConnection conn : scope.getClientConnections()) {
				if (conn != null && conn instanceof IServiceCapableConnection) {
					startStreamRecord(conn, recordingId, isInterview);
				}
			}
			// Send notification to all users that the recording has been started
			WebSocketHelper.sendRoom(new TextRoomMessage(roomId, u, RoomMessage.Type.recordingStarted
					, new JSONObject().put("uid", client.getUid()).put("sid", client.getSid()).toString()));
		} catch (Exception err) {
			log.error("[startRecording]", err);
		}
	}

	public void stopRecording(IScope scope, IClient client) {
		try {
			Long recordingId = IClientUtil.getRecordingId(scope);
			IClientUtil.setRecordingId(scope, null);
			log.debug("stopRecordAndSave {}, {}, ID: {}", client.getLogin(), client.getRemoteAddress(), recordingId);
			if (recordingId == null) {
				log.error("Unable to find recordingId on recording stop");
				return;
			}
			Client recClient = null;
			for (Client c : clientManager.listByRoom(client.getRoomId())) {
				if (c.getRecordingId() != null) {
					recClient = c;
					break;
				}
			}
			IClient stopClient;
			if (recClient == null) {
				log.warn("Unable to find Recording client");
				stopClient = client;
			} else {
				stopClient = recClient;
				// Store to database
				recClient.setRecordingId(null);
				recClient.setRecordingStarted(false);
				streamClientManager.update(recClient);
			}
			WebSocketHelper.sendRoom(new TextRoomMessage(stopClient.getRoomId(), stopClient, RoomMessage.Type.recordingStoped, stopClient.getSid()));
			// get all stream and stop recording them
			for (IConnection conn : scope.getClientConnections()) {
				if (conn != null && conn instanceof IServiceCapableConnection) {
					StreamClient rcl = streamClientManager.get(IClientUtil.getId(conn.getClient()));
					stopStreamRecord(scope, rcl);
				}
			}
			recordingDao.updateEndTime(recordingId, new Date());

			// Reset values
			log.debug("recordingConverterTask {}", recordingConverter);

			Recording recording = recordingDao.get(recordingId);
			if (recording.isInterview()) {
				interviewConverter.startConversionThread(recordingId);
			} else {
				recordingConverter.startConversionThread(recordingId);
			}
		} catch (Exception err) {
			log.error("[-- stopRecording --]", err);
		}
	}

	/**
	 * Start recording the published stream for the specified broadcast-Id
	 *
	 * @param conn
	 * @param broadcastid
	 * @param streamName
	 * @param metaId
	 * @param isScreenSharing
	 * @param isInterview
	 */
	private void addListener(IConnection conn, String broadcastid, String streamName, Long metaId, boolean isScreenSharing, boolean isInterview) {
		log.debug("Recording show for: {}", conn.getScope().getContextPath());
		log.debug("Name of CLient and Stream to be recorded: {}", broadcastid);
		log.debug("Scope " + conn);
		log.debug("Scope " + conn.getScope());
		// Get a reference to the current broadcast stream.
		ClientBroadcastStream stream = (ClientBroadcastStream) scopeAdapter.getBroadcastStream(conn.getScope(), broadcastid);

		if (stream == null) {
			log.debug("Unable to get stream: {}", streamName);
			return;
		}
		// Save the stream to disk.
		log.debug("### stream: [{}, name: {}, scope: {}, metaId: {}, sharing ? {}, interview ? {}]"
				, stream, streamName, conn.getScope(), metaId, isScreenSharing, isInterview);
		StreamListener streamListener = new StreamListener(!isScreenSharing, streamName, conn.getScope(), metaId, isScreenSharing, isInterview);

		streamListeners.put(metaId, streamListener);
		stream.addStreamListener(streamListener);
	}

	/**
	 * Stops recording the publishing stream for the specified IConnection.
	 *
	 * @param scope
	 * @param broadcastId
	 * @param metaId
	 */
	private void removeListener(IScope scope, String broadcastId, Long metaId) {
		try {
			log.debug("** removeListener: scope: {}, broadcastId: {} || {}", scope, broadcastId, scope.getContextPath());

			IBroadcastStream stream = scopeAdapter.getBroadcastStream(scope, broadcastId);

			// the stream can be null if the user just closes the browser
			// without canceling the recording before leaving
			// Iterate through all stream listeners and stop the appropriate
			if (stream != null && stream.getStreamListeners() != null) {
				for (IStreamListener iStreamListener : stream.getStreamListeners()) {
					stream.removeStreamListener(iStreamListener);
				}
			}

			if (metaId == null) {
				// this should be fixed, can be useful for debugging, after all this is an error
				// but we don't want the application to completely stop the process
				log.error("recordingMetaDataId is null");
				return;
			}

			StreamListener listenerAdapter = streamListeners.get(metaId);
			log.debug("Stream Closing :: " + metaId);

			RecordingMetaData metaData = metaDataDao.get(metaId);
			printMetaInfo(metaData, "Stopping the stream");
			// Manually call finish on the stream so that there is no endless loop waiting in the RecordingConverter waiting for the stream to finish
			// this would normally happen in the Listener
			Status s = metaData.getStreamStatus();
			switch (s) {
				case NONE:
					log.debug("Stream was not started, no need to stop :: stream with id {}", metaId);
					break;
				case STARTED:
					metaData.setStreamStatus(listenerAdapter == null ? Status.STOPPED : Status.STOPPING);
					metaDataDao.update(metaData);
					break;
				default:
					//no-op
					break;
			}
			if (listenerAdapter == null) {
				log.debug("Stream Not Found :: {}", metaId);
				log.debug("Available Streams :: {}", streamListeners.size());

				for (Long entryKey : streamListeners.keySet()) {
					log.debug("Stored recordingMetaDataId in Map: {}", entryKey);
				}
				throw new IllegalStateException("Could not find Listener to stop! metaId " + metaId);
			}

			listenerAdapter.closeStream();
			streamListeners.remove(metaId);

		} catch (Exception err) {
			log.error("[removeListener]", err);
		}
	}

	public void stopStreamRecord(IScope scope, StreamClient rcl) {
		if (rcl == null || rcl.getMetaId() == null || rcl.getBroadcastId() == null) {
			return;
		}
		log.debug("is this users still alive? stop it : {}", rcl);

		removeListener(scope, rcl.getBroadcastId(), rcl.getMetaId());

		// Update Meta Data
		metaDataDao.updateEndDate(rcl.getMetaId(), new Date());

		// Remove Meta Data
		rcl.setMetaId(null);
		streamClientManager.update(rcl);
	}

	public void startStreamRecord(IConnection conn) {
		Long recordingId = IClientUtil.getRecordingId(conn.getScope());
		Recording rec = recordingDao.get(recordingId);
		startStreamRecord(conn, recordingId, rec.isInterview());
	}

	private void startStreamRecord(IConnection conn, Long recordingId, boolean isInterview) {
		Date now = new Date();

		StreamClient rcl = streamClientManager.get(IClientUtil.getId(conn.getClient()));
		String broadcastId = rcl.getBroadcastId();
		if (rcl.getMetaId() != null && streamListeners.get(rcl.getMetaId()) != null) {
			log.debug("startStreamRecord[{}]:: existing metaId: {}", broadcastId, rcl.getMetaId());
			return;
		}

		// If its the recording client we need another type of Meta Data
		if (broadcastId != null) {
			boolean audioOnly = "a".equals(rcl.getAvsettings());
			boolean videoOnly = "v".equals(rcl.getAvsettings());
			if (Client.Type.sharing == rcl.getType()) {
				if (rcl.isSharingStarted() || rcl.isRecordingStarted()) {
					String streamName = generateFileName(recordingId, broadcastId);

					Long metaId = metaDataDao.add(recordingId, now, false, false, true, streamName, rcl.getSid());

					// Start FLV Recording
					addListener(conn, rcl.getBroadcastId(), streamName, metaId, true, isInterview);

					// Add Meta Data
					rcl.setMetaId(metaId);
					streamClientManager.update(rcl);
				}
			} else if ("av".equals(rcl.getAvsettings()) || audioOnly || videoOnly) {
				// if the user does publish av, a, v
				// But we only record av or a, video only is not interesting
				String streamName = generateFileName(recordingId, broadcastId);

				Long metaId = metaDataDao.add(recordingId, now, audioOnly, videoOnly, false, streamName, rcl.getSid());

				// Start FLV recording
				addListener(conn, broadcastId, streamName, metaId, false, isInterview);

				// Add Meta Data
				rcl.setMetaId(metaId);
				streamClientManager.update(rcl);
			}
		}
		log.debug("startStreamRecord[{}]:: resulting metaId: {}", broadcastId, rcl.getMetaId());
	}
}
