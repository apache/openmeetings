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
package org.apache.openmeetings.remote;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openmeetings.data.flvrecord.converter.FlvInterviewConverterTask;
import org.apache.openmeetings.data.flvrecord.converter.FlvInterviewReConverterTask;
import org.apache.openmeetings.data.flvrecord.converter.FlvRecorderConverterTask;
import org.apache.openmeetings.data.flvrecord.listener.StreamListener;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingLogDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingMetaDataDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingMetaDeltaDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData.Status;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.util.AuthLevelUtil;
import org.apache.openmeetings.util.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.stream.ClientBroadcastStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FLVRecorderService implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(FLVRecorderService.class, webAppRootKey);

	/**
	 * Stores a reference to all available listeners we need that reference, as the internal references stored with the
	 * red5 stream object might be gone when the user closes the browser. But each listener has an asynchronous
	 * component that needs to be closed no matter how the user leaves the application!
	 */
	private static final Map<Long, StreamListener> streamListeners = new ConcurrentHashMap<Long, StreamListener>();

	// Spring Beans
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private FlvRecordingDao flvRecordingDaoImpl;
	@Autowired
	private UserDao usersDaoImpl;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private FlvRecorderConverterTask flvRecorderConverterTask;
	@Autowired
	private FlvInterviewConverterTask flvInterviewConverterTask;
	@Autowired
	private FlvInterviewReConverterTask flvInterviewReConverterTask;
	@Autowired
	private FlvRecordingLogDao flvRecordingLogDaoImpl;
	@Autowired
	private FlvRecordingDao recordingDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private FlvRecordingMetaDeltaDao metaDeltaDao;
	@Autowired
	private FlvRecordingMetaDataDao metaDataDao;

	public void resultReceived(IPendingServiceCall arg0) {
	}

	public Client checkForRecording() {
		try {

			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug("getCurrentRoomClient -2- " + streamid);

			Client currentClient = sessionManager.getClientByStreamId(streamid, null);

			for (Client rcl : sessionManager.getClientListByRoom(currentClient.getRoom_id())) {
				if (rcl.getIsRecording()) {
					return rcl;
				}
			}

			return null;

		} catch (Exception err) {
			log.error("[checkForRecording]", err);
		}
		return null;
	}
	
	private static String generateFileName(Long flvRecording_id, String streamid) throws Exception {
		String dateString = CalendarPatterns.getTimeForStreamId(new Date());
		return "rec_" + flvRecording_id + "_stream_" + streamid + "_" + dateString;
	}

	public String recordMeetingStream(String roomRecordingName, String comment, Boolean isInterview) {
		try {
			log.debug(":: recordMeetingStream ::");

			IConnection current = Red5.getConnectionLocal();
			Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);
			Long room_id = currentClient.getRoom_id();

			Date now = new Date();

			FlvRecording flvRecording = new FlvRecording();

			flvRecording.setFileHash("");
			flvRecording.setFileName(roomRecordingName);
			flvRecording.setInsertedBy(currentClient.getUser_id());
			flvRecording.setFolder(false);
			flvRecording.setIsImage(false);
			flvRecording.setIsPresentation(false);
			flvRecording.setIsRecording(true);
			flvRecording.setComment(comment);
			flvRecording.setIsInterview(isInterview);

			flvRecording.setRoom_id(room_id);
			flvRecording.setRecordStart(now);

			flvRecording.setWidth(currentClient.getVWidth());
			flvRecording.setHeight(currentClient.getVHeight());

			flvRecording.setOwnerId(currentClient.getUser_id());
			flvRecording.setStatus(FlvRecording.Status.PROCESSING);
			flvRecording = recordingDao.update(flvRecording);
			// Receive flvRecordingId
			Long flvRecordingId = flvRecording.getFlvRecordingId();

			// Update Client and set Flag
			currentClient.setIsRecording(true);
			currentClient.setFlvRecordingId(flvRecordingId);
			sessionManager.updateClientByStreamId(current.getClient().getId(), currentClient, false, null);

			// get all stream and start recording them
			for (Set<IConnection> conset : current.getScope().getConnections()) {
			for (IConnection conn : conset) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);

						// Send every user a notification that the recording did start
						if (!rcl.getIsAVClient()) {
							((IServiceCapableConnection) conn).invoke("startedRecording", new Object[] { currentClient }, this);
						}

						// If its the recording client we need another type of Meta Data
						if (rcl.getIsScreenClient()) {
							if (rcl.getFlvRecordingId() != null && rcl.isScreenPublishStarted()) {
								String streamName_Screen = generateFileName(flvRecordingId, rcl.getStreamPublishName().toString());

								Long flvRecordingMetaDataId = metaDataDao.addFlvRecordingMetaData(
										flvRecordingId, rcl.getFirstname() + " " + rcl.getLastname(), now, false,
										false, true, streamName_Screen, rcl.getInterviewPodId());

								// Start FLV Recording
								recordShow(conn, rcl.getStreamPublishName(), streamName_Screen, flvRecordingMetaDataId, true, isInterview);

								// Add Meta Data
								rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);

								sessionManager.updateClientByStreamId(rcl.getStreamid(), rcl, false, null);
							}
						} else if (rcl.getIsAVClient()
								&& (rcl.getAvsettings().equals("av") || rcl.getAvsettings().equals("a") || rcl.getAvsettings().equals("v"))) {
							// if the user does publish av, a, v
							// But we only record av or a, video only is not interesting
							String streamName = generateFileName(flvRecordingId, String.valueOf(rcl.getBroadCastID()).toString());

							// Add Meta Data
							boolean isAudioOnly = false;
							if (rcl.getAvsettings().equals("a")) {
								isAudioOnly = true;
							}

							boolean isVideoOnly = false;
							if (rcl.getAvsettings().equals("v")) {
								isVideoOnly = true;
							}

							Long flvRecordingMetaDataId = metaDataDao.addFlvRecordingMetaData(flvRecordingId,
									rcl.getFirstname() + " " + rcl.getLastname(), now, isAudioOnly, isVideoOnly, false, streamName,
									rcl.getInterviewPodId());

							rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);

							sessionManager.updateClientByStreamId(rcl.getStreamid(), rcl, false, null);

							// Start FLV recording
							recordShow(conn, String.valueOf(rcl.getBroadCastID()).toString(), streamName, flvRecordingMetaDataId, !isAudioOnly,
									isInterview);
						}
					}
				}
			}
			}
			return roomRecordingName;

		} catch (Exception err) {
			log.error("[recordMeetingStream]", err);
		}
		return null;
	}

	/**
	 * Start recording the published stream for the specified broadcast-Id
	 * 
	 * @param conn
	 * @param broadcastid
	 * @param streamName
	 * @param flvRecordingMetaDataId
	 * @throws Exception
	 */
	private synchronized void recordShow(IConnection conn, String broadcastid, String streamName, Long flvRecordingMetaDataId,
			boolean isScreenData, Boolean isInterview) throws Exception {
		try {
			log.debug("Recording show for: " + conn.getScope().getContextPath());
			log.debug("Name of CLient and Stream to be recorded: " + broadcastid);
			// log.debug("Application.getInstance()"+Application.getInstance());
			log.debug("Scope " + conn);
			log.debug("Scope " + conn.getScope());
			// Get a reference to the current broadcast stream.
			ClientBroadcastStream stream = (ClientBroadcastStream) scopeApplicationAdapter.getBroadcastStream(conn.getScope(), broadcastid);

			if (stream == null) {
				log.debug("Unable to get stream: " + streamName);
				return;
			}
			// Save the stream to disk.
			log.debug("### stream " + stream);
			log.debug("### streamName " + streamName);
			log.debug("### conn.getScope() " + conn.getScope());
			log.debug("### flvRecordingMetaDataId " + flvRecordingMetaDataId);
			log.debug("### isScreenData " + isScreenData);
			log.debug("### isInterview " + isInterview);
			StreamListener streamListener = new StreamListener(!isScreenData, streamName, conn.getScope(),
					flvRecordingMetaDataId, isScreenData, isInterview, metaDataDao, metaDeltaDao);

			streamListeners.put(flvRecordingMetaDataId, streamListener);

			stream.addStreamListener(streamListener);
			// Just for Debug Purpose
			// stream.saveAs(streamName+"_DEBUG", false);
		} catch (Exception e) {
			log.error("Error while saving stream: " + streamName, e);
		}
	}

	/**
	 * Stops recording the publishing stream for the specified IConnection.
	 * 
	 * @param conn
	 */
	public synchronized void stopRecordingShow(IConnection conn, String broadcastId, Long flvRecordingMetaDataId) {
		try {
			if (flvRecordingMetaDataId == null) {
				// this should be fixed, can be useful for debugging, after all this is an error
				// but we don't want the application to completely stop the process
				log.error("flvRecordingMetaDataId is null");
			}

			log.debug("** stopRecordingShow: " + conn);
			log.debug("### Stop recording show for broadcastId: " + broadcastId + " || " + conn.getScope().getContextPath());

			Object streamToClose = scopeApplicationAdapter.getBroadcastStream(conn.getScope(), broadcastId);

			StreamListener listenerAdapter = streamListeners.get(flvRecordingMetaDataId);

			log.debug("Stream Closing :: " + flvRecordingMetaDataId);

			ClientBroadcastStream stream = (ClientBroadcastStream) streamToClose;

			// the stream can be null if the user just closes the browser
			// without canceling the recording before leaving
			if (stream != null) {
				// Iterate through all stream listeners and stop the appropriate
				if (stream.getStreamListeners() != null) {
					for (IStreamListener iStreamListener : stream.getStreamListeners()) {
						stream.removeStreamListener(iStreamListener);
					}
				}
			}

			if (listenerAdapter == null) {
				log.debug("Stream Not Found :: " + flvRecordingMetaDataId);
				log.debug("Available Streams :: " + streamListeners.size());

				for (Long entryKey : streamListeners.keySet()) {
					log.debug("Stored flvRecordingMetaDataId in Map: " + entryKey);
				}

				// Manually call finish on the stream so that there is no endless loop waiting
				// in the FlvRecorderConverter waiting for the stream to finish
				// this would normally happen in the Listener
				FlvRecordingMetaData metaData = metaDataDao.get(flvRecordingMetaDataId);
				if (metaData.getStreamStatus() == Status.STARTED) {
					metaData.setStreamStatus(Status.STOPPED);
					metaDataDao.update(metaData);
				}
				throw new IllegalStateException("Could not find Listener to stop! flvRecordingMetaDataId " + flvRecordingMetaDataId);
			} else {
				FlvRecordingMetaData metaData = metaDataDao.get(flvRecordingMetaDataId);
				metaData.setStreamStatus(Status.STOPPING);
				metaDataDao.update(metaData);
			}

			listenerAdapter.closeStream();
			streamListeners.remove(flvRecordingMetaDataId);

		} catch (Exception err) {
			log.error("[stopRecordingShow]", err);
		}
	}

	public Long stopRecordAndSave(IScope scope, Client currentClient, Long storedFlvRecordingId) {
		try {
			log.debug("stopRecordAndSave " + currentClient.getUsername() + "," + currentClient.getUserip());

			// get all stream and stop recording them
			for (Set<IConnection> conset : scope.getConnections()) {
			for (IConnection conn : conset) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);

						if (rcl == null) {
							continue;
						}
						log.debug("is this users still alive? stop it :" + rcl);

						if (rcl.getIsScreenClient()) {
							if (rcl.getFlvRecordingId() != null && rcl.isScreenPublishStarted()) {
								// Stop FLV Recording
								stopRecordingShow(conn, rcl.getStreamPublishName(), rcl.getFlvRecordingMetaDataId());

								// Update Meta Data
								metaDataDao.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(), new Date());
							}
						} else if (rcl.getIsAVClient()
								&& (rcl.getAvsettings().equals("av") || rcl.getAvsettings().equals("a") || rcl.getAvsettings().equals("v"))) {

							stopRecordingShow(conn, String.valueOf(rcl.getBroadCastID()).toString(), rcl.getFlvRecordingMetaDataId());

							// Update Meta Data
							metaDataDao.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(), new Date());
						}
					}
				}
			}
			}
			// Store to database
			Long flvRecordingId = currentClient.getFlvRecordingId();

			// In the Case of an Interview the stopping client does not mean
			// that its actually the recording client
			if (storedFlvRecordingId != null) {
				flvRecordingId = storedFlvRecordingId;
			}

			if (flvRecordingId != null) {
				flvRecordingDaoImpl.updateFlvRecordingEndTime(flvRecordingId, new Date(), currentClient.getOrganization_id());

				// Reset values
				currentClient.setFlvRecordingId(null);
				currentClient.setIsRecording(false);

				sessionManager.updateClientByStreamId(currentClient.getStreamid(), currentClient, false, null);
				log.debug("flvRecorderConverterTask ", flvRecorderConverterTask);

				FlvRecording flvRecording = flvRecordingDaoImpl.get(flvRecordingId);
				if (flvRecording.getIsInterview() == null || !flvRecording.getIsInterview()) {
					flvRecorderConverterTask.startConversionThread(flvRecordingId);
				} else {
					flvInterviewConverterTask.startConversionThread(flvRecordingId);
				}
			}
		} catch (Exception err) {
			log.error("[-- stopRecordAndSave --]", err);
		}
		return new Long(-1);
	}

	public Client checkLzRecording() {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug("getCurrentRoomClient -2- " + streamid);

			Client currentClient = sessionManager.getClientByStreamId(streamid, null);

			log.debug("getCurrentRoomClient -#########################- " + currentClient.getRoom_id());

			for (Client rcl : sessionManager.getClientListByRoomAll(currentClient.getRoom_id())) {
				if (rcl.getIsRecording()) {
					return rcl;
				}
			}

		} catch (Exception err) {
			log.error("[checkLzRecording]", err);
		}
		return null;
	}

	public void stopRecordingShowForClient(IConnection conn, Client rcl) {
		try {
			// this cannot be handled here, as to stop a stream and to leave a
			// room is not
			// the same type of event.
			// StreamService.addRoomClientEnterEventFunc(rcl, roomrecordingName,
			// rcl.getUserip(), false);
			log.debug("### stopRecordingShowForClient: " + rcl);

			if (rcl.getIsScreenClient()) {

				if (rcl.getFlvRecordingId() != null && rcl.isScreenPublishStarted()) {

					// Stop FLV Recording
					// FIXME: Is there really a need to stop it manually if the
					// user just
					// stops the stream?
					stopRecordingShow(conn, rcl.getStreamPublishName(), rcl.getFlvRecordingMetaDataId());

					// Update Meta Data
					metaDataDao.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(), new Date());
				}

			} else if (rcl.getIsAVClient()
					&& (rcl.getAvsettings().equals("a") || rcl.getAvsettings().equals("v") || rcl.getAvsettings().equals("av"))) {

				// FIXME: Is there really a need to stop it manually if the user
				// just stops the stream?
				stopRecordingShow(conn, String.valueOf(rcl.getBroadCastID()), rcl.getFlvRecordingMetaDataId());

				// Update Meta Data
				metaDataDao.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(), new Date());
			}

		} catch (Exception err) {
			log.error("[stopRecordingShowForClient]", err);
		}
	}

	public void addRecordingByStreamId(IConnection conn, String streamId, Client rcl, Long flvRecordingId) {
		try {
			FlvRecording flvRecording = flvRecordingDaoImpl.get(flvRecordingId);

			Date now = new Date();

			// If its the recording client we need another type of Meta Data
			if (rcl.getIsScreenClient()) {
				if (rcl.getFlvRecordingId() != null && rcl.isScreenPublishStarted()) {
					String streamName_Screen = generateFileName(flvRecordingId, rcl.getStreamPublishName().toString());

					log.debug("##############  ADD SCREEN OF SHARER :: " + rcl.getStreamPublishName());

					Long flvRecordingMetaDataId = metaDataDao.addFlvRecordingMetaData(flvRecordingId, rcl.getFirstname()
							+ " " + rcl.getLastname(), now, false, false, true, streamName_Screen, rcl.getInterviewPodId());

					// Start FLV Recording
					recordShow(conn, rcl.getStreamPublishName(), streamName_Screen, flvRecordingMetaDataId, true,
							flvRecording.getIsInterview());

					// Add Meta Data
					rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);

					sessionManager.updateClientByStreamId(rcl.getStreamid(), rcl, false, null);
				}
			} else if (rcl.getIsAVClient()
					&& (rcl.getAvsettings().equals("av") || rcl.getAvsettings().equals("a") || rcl.getAvsettings().equals("v"))) {
				// if the user does publish av, a, v
				// But we only record av or a, video only is not interesting

				String streamName = generateFileName(flvRecordingId, String.valueOf(rcl.getBroadCastID()).toString());

				// Add Meta Data
				boolean isAudioOnly = false;
				if (rcl.getAvsettings().equals("a")) {
					isAudioOnly = true;
				}
				boolean isVideoOnly = false;
				if (rcl.getAvsettings().equals("v")) {
					isVideoOnly = true;
				}

				Long flvRecordingMetaDataId = metaDataDao.addFlvRecordingMetaData(flvRecordingId, rcl.getFirstname() + " "
						+ rcl.getLastname(), now, isAudioOnly, isVideoOnly, false, streamName, rcl.getInterviewPodId());

				// Start FLV recording
				recordShow(conn, String.valueOf(rcl.getBroadCastID()).toString(), streamName, flvRecordingMetaDataId, false,
						flvRecording.getIsInterview());

				rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);

				sessionManager.updateClientByStreamId(rcl.getStreamid(), rcl, false, null);

			}

		} catch (Exception err) {
			log.error("[addRecordingByStreamId]", err);
		}
	}

	public Long restartConversion(String SID, Long flvRecordingId, Integer leftSideLoud, Integer rightSideLoud, Integer leftSideTime,
			Integer rightSideTime) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (AuthLevelUtil.checkUserLevel(user_level)) {

				log.debug("updateFileOrFolderName " + flvRecordingId);

				FlvRecording flvRecording = flvRecordingDaoImpl.get(flvRecordingId);

				flvRecording.setPreviewImage(null);

				flvRecording.setProgressPostProcessing(0);

				flvRecordingDaoImpl.update(flvRecording);

				if (flvRecording.getIsInterview() == null || !flvRecording.getIsInterview()) {
					flvRecorderConverterTask.startConversionThread(flvRecordingId);
				} else {
					flvInterviewReConverterTask.startConversionThread(flvRecordingId, leftSideLoud, rightSideLoud, leftSideTime,
							rightSideTime);
				}
			}
		} catch (Exception err) {
			log.error("[restartInterviewConversion] ", err);
		}
		return null;
	}

}
