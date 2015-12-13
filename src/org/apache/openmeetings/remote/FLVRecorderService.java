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

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingLogDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDataDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDeltaDao;
import org.apache.openmeetings.data.flvrecord.beans.FLVRecorderObject;
import org.apache.openmeetings.data.flvrecord.converter.FlvInterviewConverterTask;
import org.apache.openmeetings.data.flvrecord.converter.FlvInterviewReConverterTask;
import org.apache.openmeetings.data.flvrecord.converter.FlvRecorderConverterTask;
import org.apache.openmeetings.data.flvrecord.listener.BaseStreamListener;
import org.apache.openmeetings.data.flvrecord.listener.StreamAudioListener;
import org.apache.openmeetings.data.flvrecord.listener.StreamVideoListener;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingLog;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.math.CalendarPatterns;
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

	private static final Logger log = Red5LoggerFactory.getLogger(
			FLVRecorderService.class, OpenmeetingsVariables.webAppRootKey);
	
	/**
	 * Stores a reference to all available listeners
	 * we need that reference, as the internal references stored 
	 * with the red5 stream object might be gone when the user 
	 * closes the browser.
	 * But each listener has an asynchronous component that needs to be closed 
	 * no matter how the user leaves the application!
	 */
	private static final Map<Long,BaseStreamListener> streamListeners = new HashMap<Long,BaseStreamListener>();

	// Spring Beans
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private FlvRecordingDao flvRecordingDaoImpl;
	@Autowired
	private FlvRecordingMetaDataDao flvRecordingMetaDataDao;
	@Autowired
	private UsersDao usersDaoImpl;
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
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private FlvRecordingMetaDeltaDao flvRecordingMetaDeltaDao;
	@Autowired
	private FlvRecordingMetaDataDao flvRecordingMetaDataDaoImpl = null;

	public void resultReceived(IPendingServiceCall arg0) {
	}

	public Client checkForRecording() {
		try {

			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug("getCurrentRoomClient -2- " + streamid);

			Client currentClient = this.sessionManager
					.getClientByStreamId(streamid, null);

			for (Client rcl : sessionManager.getClientListByRoom(currentClient.getRoom_id())) {
				if (rcl.getIsRecording()) {
					return rcl;
				}
			}

			return null;

		} catch (Exception err) {
			err.printStackTrace();
			log.error("[checkForRecording]", err);
		}
		return null;
	}

	private static String generateFileName(Long flvRecording_id, String streamid)
			throws Exception {
		String dateString = CalendarPatterns
				.getTimeForStreamId(new java.util.Date());
		return "rec_" + flvRecording_id + "_stream_" + streamid + "_"
				+ dateString;

	}

	public String recordMeetingStream(String roomRecordingName, String comment,
			Boolean isInterview) {
		try {
			
			log.debug(":: recordMeetingStream ::");

			IConnection current = Red5.getConnectionLocal();
			Client currentClient = this.sessionManager
					.getClientByStreamId(current.getClient().getId(), null);
			Long room_id = currentClient.getRoom_id();

			Date now = new Date();

			// Receive flvRecordingId
			Long flvRecordingId = this.flvRecordingDaoImpl.addFlvRecording("",
					roomRecordingName, null, currentClient.getUser_id(),
					room_id, now, null, currentClient.getUser_id(), comment,
					currentClient.getStreamid(), currentClient.getVWidth(),
					currentClient.getVHeight(), isInterview);

			// Update Client and set Flag
			currentClient.setIsRecording(true);
			currentClient.setFlvRecordingId(flvRecordingId);
			this.sessionManager.updateClientByStreamId(current.getClient()
					.getId(), currentClient, false, null);

			// get all stream and start recording them
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							Client rcl = this.sessionManager
									.getClientByStreamId(conn.getClient()
											.getId(), null);

							// Send every user a notification that the recording did start
							if (!rcl.getIsAVClient()) {
								((IServiceCapableConnection) conn).invoke(
										"startedRecording",
										new Object[] { currentClient }, this);
							}

							// If its the recording client we need another type
							// of Meta Data
							if (rcl.getIsScreenClient()) {

								if (rcl.getFlvRecordingId() != null
										&& rcl.isScreenPublishStarted()) {

									String streamName_Screen = generateFileName(
											flvRecordingId, rcl
													.getStreamPublishName()
													.toString());

									Long flvRecordingMetaDataId = this.flvRecordingMetaDataDao
											.addFlvRecordingMetaData(
													flvRecordingId,
													rcl.getFirstname() + " "
															+ rcl.getLastname(),
													now, false, false, true,
													streamName_Screen,
													rcl.getInterviewPodId());

									// Start FLV Recording
									recordShow(conn,
											rcl.getStreamPublishName(),
											streamName_Screen,
											flvRecordingMetaDataId, true,
											isInterview);

									// Add Meta Data
									rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);

									this.sessionManager
											.updateClientByStreamId(
													rcl.getStreamid(), rcl, false, null);

								}

							} else if
							// if the user does publish av, a, v
							// But we only record av or a, video only is not
							// interesting
							(rcl.getIsAVClient() &&
									(rcl.getAvsettings().equals("av")
									|| rcl.getAvsettings().equals("a")
									|| rcl.getAvsettings().equals("v"))) {

								String streamName = generateFileName(
										flvRecordingId,
										String.valueOf(rcl.getBroadCastID())
												.toString());

								// Add Meta Data
								boolean isAudioOnly = false;
								if (rcl.getAvsettings().equals("a")) {
									isAudioOnly = true;
								}

								boolean isVideoOnly = false;
								if (rcl.getAvsettings().equals("v")) {
									isVideoOnly = true;
								}

								Long flvRecordingMetaDataId = this.flvRecordingMetaDataDao
										.addFlvRecordingMetaData(
												flvRecordingId,
												rcl.getFirstname() + " "
														+ rcl.getLastname(),
												now, isAudioOnly, isVideoOnly,
												false, streamName,
												rcl.getInterviewPodId());

								rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);

								this.sessionManager.updateClientByStreamId(
										rcl.getStreamid(), rcl, false, null);

								// Start FLV recording
								recordShow(conn,
										String.valueOf(rcl.getBroadCastID())
												.toString(), streamName,
										flvRecordingMetaDataId, false,
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
	private synchronized void recordShow(IConnection conn, String broadcastid,
			String streamName, Long flvRecordingMetaDataId,
			boolean isScreenData, Boolean isInterview) throws Exception {
		try {
			log.debug("Recording show for: " + conn.getScope().getContextPath());
			log.debug("Name of CLient and Stream to be recorded: "
					+ broadcastid);
			// log.debug("Application.getInstance()"+Application.getInstance());
			log.debug("Scope " + conn);
			log.debug("Scope " + conn.getScope());
			// Get a reference to the current broadcast stream.
			ClientBroadcastStream stream = (ClientBroadcastStream) scopeApplicationAdapter
					.getBroadcastStream(conn.getScope(), broadcastid);

			if (stream == null) {
				log.debug("Unable to get stream: " + streamName);
				return;
			}
			// Save the stream to disk.
			if (isScreenData) {
				
				StreamVideoListener streamScreenListener = new StreamVideoListener(streamName,
																		conn.getScope(), flvRecordingMetaDataId, isScreenData,
																		isInterview, flvRecordingMetaDataDao);
				
				streamListeners.put(flvRecordingMetaDataId, streamScreenListener);
				
				stream.addStreamListener(streamScreenListener);
			} else {

				log.debug("### stream " + stream);
				log.debug("### streamName " + streamName);
				log.debug("### conn.getScope() " + conn.getScope());
				log.debug("### flvRecordingMetaDataId "
						+ flvRecordingMetaDataId);
				log.debug("### isScreenData " + isScreenData);
				log.debug("### isInterview " + isInterview);

				StreamAudioListener streamAudioListener = new StreamAudioListener(streamName,
																	conn.getScope(), flvRecordingMetaDataId, isScreenData,
																	isInterview, flvRecordingMetaDeltaDao, flvRecordingMetaDataDao);

				streamListeners.put(flvRecordingMetaDataId, streamAudioListener);

				stream.addStreamListener(streamAudioListener);
			}
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
	public synchronized void stopRecordingShow(IConnection conn, String broadcastId,
			Long flvRecordingMetaDataId) {
		try {
			
			if (flvRecordingMetaDataId == null) {
				//this should be fixed, can be useful for debugging, after all this is an error
				//but we don't want the application to completely stop the process
				log.error("flvRecordingMetaDataId is null");
			}

			log.debug("** stopRecordingShow: " + conn);
			log.debug("### Stop recording show for broadcastId: " + broadcastId
					+ " || " + conn.getScope().getContextPath());

			Object streamToClose = scopeApplicationAdapter.getBroadcastStream(
					conn.getScope(), broadcastId);

			BaseStreamListener listenerAdapter = streamListeners.get(flvRecordingMetaDataId);
			
			log.debug("Stream Closing :: " + flvRecordingMetaDataId);
			
			ClientBroadcastStream stream = (ClientBroadcastStream) streamToClose;

			//the stream can be null if the user just closes the browser 
			//without canceling the recording before leaving
			if (stream != null) {
				//Iterate through all stream listeners and stop the appropriate
				if (stream.getStreamListeners() != null) {
	
					for (IStreamListener iStreamListener : stream
							.getStreamListeners()) {
						stream.removeStreamListener(iStreamListener);
					}
				}
			}
			
			if (listenerAdapter == null) {
				
				log.debug("Stream Not Found :: " + flvRecordingMetaDataId);
				log.debug("Available Streams :: "+streamListeners.size());
				
				for (Long entryKey : streamListeners.keySet()) {
					log.debug("Stored flvRecordingMetaDataId in Map: "+ entryKey);
				}
				
				//Manually call finish on the stream so that there is no endless loop waiting  
				//in the FlvRecorderConverter waiting for the stream to finish
				//this would normally happen in the Listener
				FlvRecordingMetaData flvRecordingMetaData = flvRecordingMetaDataDaoImpl.
							getFlvRecordingMetaDataById(flvRecordingMetaDataId);
				flvRecordingMetaData.setStreamReaderThreadComplete(true);
				flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaData(flvRecordingMetaData);
				
				throw new IllegalStateException("Could not find Listener to stop! flvRecordingMetaDataId "+flvRecordingMetaDataId);
			}
			
			listenerAdapter.closeStream();
			streamListeners.remove(flvRecordingMetaDataId);
			

		} catch (Exception err) {
			log.error("[stopRecordingShow]", err);
		}
	}

	public Long stopRecordAndSave(IScope scope, Client currentClient,
			Long storedFlvRecordingId) {
		try {
			log.debug("stopRecordAndSave " + currentClient.getUsername() + ","
					+ currentClient.getUserip());

			// get all stream and stop recording them
			Collection<Set<IConnection>> conCollection = scope.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {

							Client rcl = sessionManager.getClientByStreamId(
											conn.getClient().getId(), null);

							// FIXME: Check if this function is really in use at
							// the moment
							// if (!rcl.getIsScreenClient()) {
							// ((IServiceCapableConnection)
							// conn).invoke("stoppedRecording",new Object[] {
							// currentClient }, this);
							// }

							log.debug("is this users still alive? stop it :" + rcl);

							if (rcl.getIsScreenClient()) {

								if (rcl.getFlvRecordingId() != null
										&& rcl.isScreenPublishStarted()) {

									// Stop FLV Recording
									stopRecordingShow(conn,
											rcl.getStreamPublishName(),
											rcl.getFlvRecordingMetaDataId());

									// Update Meta Data
									this.flvRecordingMetaDataDao
											.updateFlvRecordingMetaDataEndDate(
													rcl.getFlvRecordingMetaDataId(),
													new Date());
								}

							} else if (rcl.getIsAVClient()
									&& (rcl.getAvsettings().equals("av")
									|| rcl.getAvsettings().equals("a")
									|| rcl.getAvsettings().equals("v"))) {

								stopRecordingShow(conn,
										String.valueOf(rcl.getBroadCastID())
												.toString(),
										rcl.getFlvRecordingMetaDataId());

								// Update Meta Data
								this.flvRecordingMetaDataDao
										.updateFlvRecordingMetaDataEndDate(
												rcl.getFlvRecordingMetaDataId(),
												new Date());

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

				this.flvRecordingDaoImpl.updateFlvRecordingEndTime(
						flvRecordingId, new Date(),
						currentClient.getOrganization_id());

				// Reset values
				currentClient.setFlvRecordingId(null);
				currentClient.setIsRecording(false);

				this.sessionManager.updateClientByStreamId(
						currentClient.getStreamid(), currentClient, false, null);

				log.debug("this.flvRecorderConverterTask ",
						this.flvRecorderConverterTask);

				FlvRecording flvRecording = this.flvRecordingDaoImpl
						.getFlvRecordingById(flvRecordingId);

				if (flvRecording.getIsInterview() == null
						|| !flvRecording.getIsInterview()) {

					this.flvRecorderConverterTask
							.startConversionThread(flvRecordingId);

				} else {

					this.flvInterviewConverterTask
							.startConversionThread(flvRecordingId);

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

			Client currentClient = this.sessionManager
					.getClientByStreamId(streamid, null);

			log.debug("getCurrentRoomClient -#########################- "
					+ currentClient.getRoom_id());

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

				if (rcl.getFlvRecordingId() != null
						&& rcl.isScreenPublishStarted()) {

					// Stop FLV Recording
					// FIXME: Is there really a need to stop it manually if the
					// user just
					// stops the stream?
					stopRecordingShow(conn, rcl.getStreamPublishName(),
							rcl.getFlvRecordingMetaDataId());

					// Update Meta Data
					this.flvRecordingMetaDataDao
							.updateFlvRecordingMetaDataEndDate(
									rcl.getFlvRecordingMetaDataId(), new Date());
				}

			} else if (rcl.getIsAVClient() &&
					(rcl.getAvsettings().equals("a")
					|| rcl.getAvsettings().equals("v") 
					|| rcl.getAvsettings().equals("av"))) {

				// FIXME: Is there really a need to stop it manually if the user
				// just stops the stream?
				stopRecordingShow(conn, String.valueOf(rcl.getBroadCastID()),
						rcl.getFlvRecordingMetaDataId());

				// Update Meta Data
				this.flvRecordingMetaDataDao
						.updateFlvRecordingMetaDataEndDate(
								rcl.getFlvRecordingMetaDataId(), new Date());
			}

		} catch (Exception err) {
			log.error("[stopRecordingShowForClient]", err);
		}
	}

	public void addRecordingByStreamId(IConnection conn, String streamId,
			Client rcl, Long flvRecordingId) {
		try {

			FlvRecording flvRecording = this.flvRecordingDaoImpl
					.getFlvRecordingById(flvRecordingId);

			Date now = new Date();

			// If its the recording client we need another type of Meta Data
			if (rcl.getIsScreenClient()) {

				if (rcl.getFlvRecordingId() != null
						&& rcl.isScreenPublishStarted()) {

					String streamName_Screen = generateFileName(flvRecordingId,
							rcl.getStreamPublishName().toString());

					log.debug("##############  ADD SCREEN OF SHARER :: "
							+ rcl.getStreamPublishName());

					Long flvRecordingMetaDataId = this.flvRecordingMetaDataDao
							.addFlvRecordingMetaData(
									flvRecordingId,
									rcl.getFirstname() + " "
											+ rcl.getLastname(), now, false,
									false, true, streamName_Screen,
									rcl.getInterviewPodId());

					// Start FLV Recording
					recordShow(conn, rcl.getStreamPublishName(),
							streamName_Screen, flvRecordingMetaDataId, true,
							flvRecording.getIsInterview());

					// Add Meta Data
					rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);

					this.sessionManager.updateClientByStreamId(
							rcl.getStreamid(), rcl, false, null);

				}

			} else if
			// if the user does publish av, a, v
			// But we only record av or a, video only is not interesting
			(rcl.getIsAVClient() && 
					(rcl.getAvsettings().equals("av")
					|| rcl.getAvsettings().equals("a")
					|| rcl.getAvsettings().equals("v"))) {

				String streamName = generateFileName(flvRecordingId, String
						.valueOf(rcl.getBroadCastID()).toString());

				// Add Meta Data
				boolean isAudioOnly = false;
				if (rcl.getAvsettings().equals("a")) {
					isAudioOnly = true;
				}
				boolean isVideoOnly = false;
				if (rcl.getAvsettings().equals("v")) {
					isVideoOnly = true;
				}

				Long flvRecordingMetaDataId = this.flvRecordingMetaDataDao
						.addFlvRecordingMetaData(flvRecordingId,
								rcl.getFirstname() + " " + rcl.getLastname(),
								now, isAudioOnly, isVideoOnly, false,
								streamName, rcl.getInterviewPodId());

				// Start FLV recording
				recordShow(conn, String.valueOf(rcl.getBroadCastID())
						.toString(), streamName, flvRecordingMetaDataId, false,
						flvRecording.getIsInterview());

				rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);

				this.sessionManager.updateClientByStreamId(
						rcl.getStreamid(), rcl, false, null);

			}

		} catch (Exception err) {
			log.error("[addRecordingByStreamId]", err);
		}
	}

	public FlvRecording getFlvRecordingWithMetaData(String SID,
			Long flvRecordingId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				FlvRecording flvRecording = this.flvRecordingDaoImpl
						.getFlvRecordingById(flvRecordingId);

				flvRecording
						.setFlvRecordingMetaData(this.flvRecordingMetaDataDao
								.getFlvRecordingMetaDataByRecording(flvRecordingId));

				if (flvRecording.getInsertedBy() != null) {
					flvRecording.setCreator(this.usersDaoImpl
							.get(flvRecording.getInsertedBy()));
				}

				if (flvRecording.getRoom_id() != null) {
					flvRecording.setRoom(roomDao.get(flvRecording.getRoom_id()));
				}

				flvRecording.setFlvRecordingLog(this.flvRecordingLogDaoImpl
						.getFLVRecordingLogByRecordingId(flvRecordingId));

				return flvRecording;

			}
		} catch (Exception err) {
			log.error("[getFlvRecordingWithMetaData] ", err);
			err.printStackTrace();
		}
		return null;
	}

	public List<FlvRecordingLog> getFlvRecordingLog(String SID,
			Long flvRecordingId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				return this.flvRecordingLogDaoImpl
						.getFLVRecordingLogByRecordingId(flvRecordingId);

			}
		} catch (Exception err) {
			log.error("[getFlvRecordingLog] ", err);
			err.printStackTrace();
		}
		return null;
	}

	public Long deleteFLVOrFolder(String SID, Long flvRecordingId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			System.out.println("deleteFLVOrFolder " + flvRecordingId);

			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("deleteFLVOrFolder " + flvRecordingId);

				this.flvRecordingDaoImpl.deleteFlvRecording(flvRecordingId);

				return flvRecordingId;
			}
		} catch (Exception err) {
			log.error("[deleteFLVOrFolder] ", err);
		}
		return null;
	}

	public List<FlvRecording> getFLVExplorerByParent(String SID,
			Long parentFileExplorerItemId, Boolean isOwner) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("parentFileExplorerItemId "
						+ parentFileExplorerItemId);

				if (parentFileExplorerItemId == 0) {
					if (isOwner) {

						return this.flvRecordingDaoImpl.getFlvRecordingByOwner(
								users_id, parentFileExplorerItemId);

					} else {

						return this.flvRecordingDaoImpl
								.getFlvRecordingsPublic();

					}
				} else {

					return this.flvRecordingDaoImpl
							.getFlvRecordingByParent(parentFileExplorerItemId);
				}

			}
		} catch (Exception err) {
			log.error("[getFLVExplorerByParent] ", err);
		}
		return null;
	}

	public Long addFolder(String SID, Long parentFileExplorerItemId,
			String fileName, Boolean isOwner, Long organization_id) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("addFolder " + parentFileExplorerItemId);

				if (parentFileExplorerItemId == 0 && isOwner) {

					return this.flvRecordingDaoImpl.addFlvFolderRecording("",
							fileName, null, // FileSize
							users_id, null, null, null, // Long room_id, Date
														// recordStart, Date
														// recordEnd
							users_id, // OwnerID => only set if its directly
										// root in Owner Directory, other
										// Folders and Files
							// maybe are also in a Home directory but just
							// because their parent is
							"", parentFileExplorerItemId, organization_id);

				} else {

					return this.flvRecordingDaoImpl.addFlvFolderRecording("",
							fileName, null, // FileSize
							users_id, null, null, null, // Long room_id, Date
														// recordStart, Date
														// recordEnd
							null, // OwnerID => only set if its directly root in
									// Owner Directory, other Folders and Files
							// maybe are also in a Home directory but just
							// because their parent is
							"", parentFileExplorerItemId, organization_id);

				}

			}
		} catch (Exception err) {
			log.error("[addFolder] ", err);
		}
		return null;
	}

	public Long moveFile(String SID, Long flvRecordingId,
			Long newParentFileExplorerItemId, Boolean isOwner,
			Boolean moveToHome) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("moveFile " + flvRecordingId);

				this.flvRecordingDaoImpl.moveFile(flvRecordingId,
						newParentFileExplorerItemId, isOwner, users_id);

				FlvRecording flvRecording = this.flvRecordingDaoImpl
						.getFlvRecordingById(flvRecordingId);

				if (moveToHome) {
					// set this file and all subfiles and folders the ownerId
					this.setFileToOwnerOrRoomByParent(flvRecording, users_id);

				} else {
					// set this file and all subfiles and folders the room_id
					this.setFileToOwnerOrRoomByParent(flvRecording, null);

				}
			}
		} catch (Exception err) {
			log.error("[moveFile] ", err);
		}
		return null;
	}

	private void setFileToOwnerOrRoomByParent(FlvRecording flvRecording,
			Long usersId) {
		try {

			flvRecording.setOwnerId(usersId);

			this.flvRecordingDaoImpl.updateFlvRecording(flvRecording);

			List<FlvRecording> subFLVItems = this.flvRecordingDaoImpl
					.getFlvRecordingByParent(flvRecording.getFlvRecordingId());

			for (FlvRecording subFLVItem : subFLVItems) {
				this.setFileToOwnerOrRoomByParent(subFLVItem, usersId);
			}

		} catch (Exception err) {
			log.error("[setFileToOwnerOrRoomByParent] ", err);
		}
	}

	public Long updateFileOrFolderName(String SID, Long flvRecordingId,
			String fileName) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("updateFileOrFolderName " + flvRecordingId);

				this.flvRecordingDaoImpl.updateFileOrFolderName(flvRecordingId,
						fileName);

				return flvRecordingId;
			}
		} catch (Exception err) {
			log.error("[updateFileOrFolderName] ", err);
		}
		return null;
	}

	public FLVRecorderObject getFLVExplorerByRoom(String SID,
			Long organization_id) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				FLVRecorderObject fileExplorerObject = new FLVRecorderObject();

				// User Home Recordings
				List<FlvRecording> homeFlvRecordings = this.flvRecordingDaoImpl
						.getFlvRecordingRootByOwner(users_id);
				long homeFileSize = 0;

				for (FlvRecording homeFlvRecording : homeFlvRecordings) {
					homeFileSize += this
							.getSizeOfDirectoryAndSubs(homeFlvRecording);
				}

				fileExplorerObject.setUserHome(homeFlvRecordings);
				fileExplorerObject.setUserHomeSize(homeFileSize);

				// Public Recordings by Organization
				List<FlvRecording> publicFlvRecordings = this.flvRecordingDaoImpl
						.getFlvRecordingRootByPublic(organization_id);
				long publicFileSize = 0;

				for (FlvRecording publicFlvRecording : publicFlvRecordings) {
					publicFileSize += this
							.getSizeOfDirectoryAndSubs(publicFlvRecording);
				}
				fileExplorerObject.setRoomHome(publicFlvRecordings);
				fileExplorerObject.setRoomHomeSize(publicFileSize);

				return fileExplorerObject;

			}
		} catch (Exception err) {
			log.error("[getFileExplorerByRoom] ", err);
		}
		return null;
	}

	private long getSizeOfDirectoryAndSubs(FlvRecording rec) {
		try {
			long fileSize = 0;

			if (rec.getFileHash() != null) {
				File tFile = new File(OmFileHelper.getStreamsHibernateDir(), rec.getFileHash());
				if (tFile.exists()) {
					fileSize += tFile.length();
				}
			}

			if (rec.getAlternateDownload() != null) {
				File dFile = new File(OmFileHelper.getStreamsHibernateDir(), rec.getAlternateDownload());
				if (dFile.exists()) {
					fileSize += dFile.length();
				}
			}
			if (rec.getPreviewImage() != null) {
				File iFile = new File(OmFileHelper.getStreamsHibernateDir(), rec.getPreviewImage());
				if (iFile.exists()) {
					fileSize += iFile.length();
				}
			}

			List<FlvRecording> flvRecordings = flvRecordingDaoImpl.getFlvRecordingByParent(
				rec.getFlvRecordingId());

			for (FlvRecording flvRecording : flvRecordings) {
				fileSize += getSizeOfDirectoryAndSubs(flvRecording);
			}

			return fileSize;

		} catch (Exception err) {
			log.error("[getSizeOfDirectoryAndSubs] ", err);
		}
		return 0;
	}

	public Long restartConversion(String SID, Long flvRecordingId,
			Integer leftSideLoud, Integer rightSideLoud, Integer leftSideTime,
			Integer rightSideTime) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				log.debug("updateFileOrFolderName " + flvRecordingId);

				FlvRecording flvRecording = this.flvRecordingDaoImpl
						.getFlvRecordingById(flvRecordingId);

				flvRecording.setPreviewImage(null);

				flvRecording.setProgressPostProcessing(0);

				this.flvRecordingDaoImpl.updateFlvRecording(flvRecording);

				if (flvRecording.getIsInterview() == null
						|| !flvRecording.getIsInterview()) {
					flvRecorderConverterTask.startConversionThread(flvRecordingId);
				} else {
					flvInterviewReConverterTask.startConversionThread(
						flvRecordingId, leftSideLoud, rightSideLoud,
						leftSideTime, rightSideTime);
				}
			}
		} catch (Exception err) {
			log.error("[restartInterviewConversion] ", err);
		}
		return null;
	}

}
