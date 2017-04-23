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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_SECURE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_SECURE_PROXY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_CODEC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.SipDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.InitializationContainer;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.Version;
import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.message.TextRoomMessage;
import org.apache.wicket.Application;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IBroadcastStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.openjson.JSONObject;

public class ScopeApplicationAdapter extends MultiThreadedApplicationAdapter implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(ScopeApplicationAdapter.class, webAppRootKey);
	private static final String SECURITY_CODE_PARAM = "securityCode";
	private static final String WIDTH_PARAM = "width";
	private static final String HEIGHT_PARAM = "height";
	public static final String FLASH_SECURE = "secure";
	public static final String FLASH_NATIVE_SSL = "native";
	public static final String FLASH_PORT = "rtmpPort";
	public static final String FLASH_SSL_PORT = "rtmpsPort";
	public static final String FLASH_VIDEO_CODEC = "videoCodec";
	public static final String FLASH_FPS = "fps";
	private static AtomicLong broadCastCounter = new AtomicLong(0);
	private JSONObject flashSettings;

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private RecordingService recordingService;
	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConferenceLogDao conferenceLogDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private SipDao sipDao;
	@Autowired
	private RecordingDao recordingDao;
	@Autowired
	private ServerDao serverDao;

	@Override
	public void resultReceived(IPendingServiceCall arg0) {
		if (log.isTraceEnabled()) {
			log.trace("resultReceived:: {}", arg0);
		}
	}

	@Override
	public boolean appStart(IScope scope) {
		try {
			OmFileHelper.setOmHome(scope.getResource("/").getFile());
			LabelDao.initLanguageMap();

			log.debug("webAppPath : " + OmFileHelper.getOmHome());

			// Only load this Class one time Initially this value might by empty, because the DB is empty yet
			cfgDao.getCryptKey();

			// init your handler here
			Properties props = new Properties();
			try (InputStream is = new FileInputStream(new File(new File(OmFileHelper.getRootDir(), "conf"), "red5.properties"))) {
				props.load(is);
			}
			flashSettings = new JSONObject()
					.put(FLASH_SECURE, "yes".equals(cfgDao.getConfValue(CONFIG_FLASH_SECURE, String.class, "no")))
					.put(FLASH_NATIVE_SSL, "best".equals(cfgDao.getConfValue(CONFIG_FLASH_SECURE_PROXY, String.class, "none")))
					.put(FLASH_PORT, props.getProperty("rtmp.port"))
					.put(FLASH_SSL_PORT, props.getProperty("rtmps.port"))
					.put(FLASH_VIDEO_CODEC, cfgDao.getConfValue(CONFIG_FLASH_VIDEO_CODEC, String.class, "h263"))
					.put(FLASH_FPS, cfgDao.getConfValue(OpenmeetingsVariables.CONFIG_FLASH_VIDEO_FPS, Integer.class, "30"))
					;

			for (String scopeName : scope.getScopeNames()) {
				log.debug("scopeName :: " + scopeName);
			}

			InitializationContainer.initComplete = true;
			Version.logOMStarted();
			recordingDao.resetProcessingStatus(); //we are starting so all processing recordings are now errors
			sessionManager.clearCache(); // 'sticky' clients should be cleaned up from DB
		} catch (Exception err) {
			log.error("[appStart]", err);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> getConnParams(Object[] params) {
		if (params != null && params.length > 0) {
			return (Map<String, Object>)params[0];
		}
		return new HashMap<>();
	}

	@Override
	public boolean roomConnect(IConnection conn, Object[] params) {
		log.debug("roomConnect : ");

		IServiceCapableConnection service = (IServiceCapableConnection) conn;
		String streamId = conn.getClient().getId();

		log.debug("### Client connected to OpenMeetings, register Client StreamId: " + streamId + " scope " + conn.getScope().getName());

		// Set StreamId in Client
		service.invoke("setId", new Object[] { streamId }, this);

		Map<String, Object> map = conn.getConnectParams();
		String swfURL = map.containsKey("swfUrl") ? (String)map.get("swfUrl") : "";
		String tcUrl = map.containsKey("tcUrl") ? (String)map.get("tcUrl") : "";
		Map<String, Object> connParams = getConnParams(params);
		String uid = (String)connParams.get("uid");
		if ("noclient".equals(uid)) {
			return true;
		}
		String securityCode = (String)connParams.get(SECURITY_CODE_PARAM);
		String parentSid = (String)map.get("parentSid");
		if (parentSid == null) {
			parentSid = (String)connParams.get("parentSid");
		}
		StringValue scn = StringValue.valueOf(conn.getScope().getName());
		long roomId = scn.toLong(Long.MIN_VALUE);
		StreamClient rcm = new StreamClient();
		IApplication iapp = (IApplication)Application.get(OpenmeetingsVariables.wicketApplicationName);
		if (!Strings.isEmpty(securityCode)) {
			//this is for external applications like ffmpeg [OPENMEETINGS-1574]
			if (roomId < 0) {
				log.warn("Trying to enter invalid scope using security code, client is rejected:: " + roomId);
				return rejectClient();
			}
			String _uid = null;
			for (org.apache.openmeetings.db.entity.basic.Client wcl : iapp.getOmRoomClients(roomId)) {
				if (wcl.getSid().equals(securityCode)) {
					_uid = wcl.getUid();
					break;
				}
			}
			if (_uid == null) {
				log.warn("Client is not found by security id, client is rejected");
				return rejectClient();
			}
			StreamClient parent = sessionManager.getClientByPublicSID(_uid, null);
			if (parent == null || !parent.getScope().equals(scn.toString())) {
				log.warn("Security code is invalid, client is rejected");
				return rejectClient();
			}
			rcm.setUsername(parent.getUsername());
			rcm.setFirstname(parent.getFirstname());
			rcm.setLastname(parent.getLastname());
			rcm.setUserId(parent.getUserId());
			rcm.setPublicSID(UUID.randomUUID().toString());
			rcm.setSecurityCode(_uid);
			Number width = (Number)connParams.get(WIDTH_PARAM);
			Number height = (Number)connParams.get(HEIGHT_PARAM);
			if (width != null && height != null) {
				rcm.setVWidth(width.intValue());
				rcm.setVHeight(height.intValue());
			}
		}
		if (Strings.isEmpty(uid) && Strings.isEmpty(securityCode) && Strings.isEmpty(parentSid)) {
			log.warn("No UIDs are provided, client is rejected");
			return rejectClient();
		}

		if (map.containsKey("screenClient")) {
			org.apache.openmeetings.db.entity.basic.Client parent = iapp.getOmClient(uid);
			if (parent == null) {
				log.warn("Bad parent for screen-sharing client, client is rejected");
				return rejectClient();
			}
			SessionVariablesUtil.setIsScreenClient(conn.getClient());
			rcm.setUserId(parent.getUserId());
			rcm.setScreenClient(true);
			rcm.setPublicSID(UUID.randomUUID().toString());
			rcm.setStreamPublishName(uid);
		}
		rcm.setStreamid(conn.getClient().getId());
		rcm.setScope(scn.toString());
		boolean notHibernate = !"hibernate".equals(scn.toString());
		if (Long.MIN_VALUE != roomId) {
			rcm.setRoomId(roomId);
		} else if (notHibernate) {
			log.warn("Bad room specified, client is rejected");
			return rejectClient();
		}
		if (connParams.containsKey("mobileClient")) {
			Sessiondata sd = sessiondataDao.check(parentSid);
			if (sd.getUserId() == null && notHibernate) {
				log.warn("Attempt of unauthorized room enter, client is rejected");
				return rejectClient();
			}
			rcm.setMobile(true);
			rcm.setUserId(sd.getUserId());
			if (rcm.getUserId() != null) {
				User u = userDao.get(rcm.getUserId());
				if (u == null) {
					log.error("Attempt of unauthorized room enter: USER not found, client is rejected");
					return rejectClient();
				}
				rcm.setUsername(u.getLogin());
				rcm.setFirstname(u.getFirstname());
				rcm.setLastname(u.getLastname());
				rcm.setEmail(u.getAddress() == null ? null : u.getAddress().getEmail());
			}
			rcm.setSecurityCode(sd.getSessionId());
			rcm.setPublicSID(UUID.randomUUID().toString());
		}
		rcm.setUserport(conn.getRemotePort());
		rcm.setUserip(conn.getRemoteAddress());
		rcm.setSwfurl(swfURL);
		rcm.setTcUrl(tcUrl);
		if (!Strings.isEmpty(uid)) {
			rcm.setPublicSID(uid);
		}
		rcm = sessionManager.add(iapp.updateClient(rcm, false), null);
		if (rcm == null) {
			log.warn("Failed to create Client on room connect");
			return false;
		}

		SessionVariablesUtil.initClient(conn.getClient(), rcm.getPublicSID());
		//TODO add similar code for other connections, merge with above block
		if (map.containsKey("screenClient")) {
			//TODO add check for room rights
			User u = null;
			Long userId = rcm.getUserId();
			SessionVariablesUtil.setUserId(conn.getClient(), userId);
			if (userId != null) {
				long _uid = userId.longValue();
				u = userDao.get(_uid < 0 ? -_uid : _uid);
			}
			if (u != null) {
				rcm.setUsername(u.getLogin());
				rcm.setFirstname(u.getFirstname());
				rcm.setLastname(u.getLastname());
			}
			log.debug("publishName :: " + rcm.getStreamPublishName());
			sessionManager.updateClientByStreamId(streamId, rcm, false, null);
		}

		// Log the User
		conferenceLogDao.add(ConferenceLog.Type.clientConnect,
				rcm.getUserId(), streamId, null, rcm.getUserip(),
				rcm.getScope());
		return true;
	}

	public Map<String, String> screenSharerAction(Map<String, Object> map) {
		Map<String, String> returnMap = new HashMap<>();
		try {
			log.debug("-----------  screenSharerAction ENTER");
			IConnection current = Red5.getConnectionLocal();

			StreamClient client = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			if (client != null) {
				boolean changed = false;
				if (Boolean.parseBoolean("" + map.get("stopStreaming")) && client.isStartStreaming()) {
					changed = true;
					client.setStartStreaming(false);
					//Send message to all users
					sendMessageToCurrentScope("stopScreenSharingMessage", client, false);
					WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoomId(), client.getUserId(), RoomMessage.Type.sharingStoped, client.getStreamPublishName()));

					returnMap.put("result", "stopSharingOnly");
				}
				if (Boolean.parseBoolean("" + map.get("stopRecording")) && client.getIsRecording()) {
					changed = true;
					client.setStartRecording(false);
					client.setIsRecording(false);

					returnMap.put("result", "stopRecordingOnly");

					recordingService.stopRecordAndSave(current.getScope(), client, null);
				}
				if (Boolean.parseBoolean("" + map.get("stopPublishing")) && client.isScreenPublishStarted()) {
					changed = true;
					client.setScreenPublishStarted(false);
					returnMap.put("result", "stopPublishingOnly");

					//Send message to all users
					sendMessageToCurrentScope("stopPublishingMessage", client, false);
				}

				if (changed) {
					sessionManager.updateClientByStreamId(client.getStreamid(), client, false, null);

					if (!client.isStartStreaming() && !client.isStartRecording() && !client.isStreamPublishStarted()) {
						returnMap.put("result", "stopAll");
					}
				}
			}
			log.debug("-----------  screenSharerAction, return: " + returnMap);
		} catch (Exception err) {
			log.error("[screenSharerAction]", err);
		}
		return returnMap;
	}

	/**
	 *
	 * @param map
	 * @return returns key,value Map with multiple return values or null in case of exception
	 *
	 */
	public Map<String, Object> setConnectionAsSharingClient(Map<String, Object> map) {
		try {
			log.debug("-----------  setConnectionAsSharingClient");
			IConnection current = Red5.getConnectionLocal();

			StreamClient client = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			if (client != null) {
				boolean startRecording = Boolean.parseBoolean("" + map.get("startRecording"));
				boolean startStreaming = Boolean.parseBoolean("" + map.get("startStreaming"));
				boolean startPublishing = Boolean.parseBoolean("" + map.get("startPublishing")) && (0 == sessionManager.getPublishingCount(client.getRoomId()));

				boolean alreadyStreaming = client.isStartStreaming();
				if (startStreaming) {
					client.setStartStreaming(true);
				}
				boolean alreadyRecording = client.isStartRecording();
				if (startRecording) {
					client.setStartRecording(true);
				}
				if (startPublishing) {
					client.setStreamPublishStarted(true);
				}

				client.setVX(Double.valueOf("" + map.get("screenX")).intValue());
				client.setVY(Double.valueOf("" + map.get("screenY")).intValue());
				client.setVWidth(Double.valueOf("" + map.get("screenWidth")).intValue());
				client.setVHeight(Double.valueOf("" + map.get("screenHeight")).intValue());
				client.setStreamPublishName("" + map.get("publishName"));
				sessionManager.updateClientByStreamId(current.getClient().getId(), client, false, null);

				Map<String, Object> returnMap = new HashMap<>();
				returnMap.put("alreadyPublished", false);

				// if is already started screen sharing, then there is no need to start it again
				if (client.isScreenPublishStarted()) {
					returnMap.put("alreadyPublished", true);
				}

				log.debug("screen x,y,width,height {},{},{},{}", client.getVX(), client.getVY(), client.getVWidth(), client.getVHeight());

				if (startStreaming) {
					if (!alreadyStreaming) {
						returnMap.put("modus", "startStreaming");

						log.debug("start streamPublishStart Is Screen Sharing ");

						//Send message to all users
						sendMessageToCurrentScope("newScreenSharing", client, false);
						WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoomId(), client.getUserId(), RoomMessage.Type.sharingStarted, client.getStreamPublishName()));
					} else {
						log.warn("Streaming is already started for the client id=" + client.getId() + ". Second request is ignored.");
					}
				}
				if (startRecording) {
					if (!alreadyRecording) {
						returnMap.put("modus", "startRecording");

						String recordingName = "Recording " + CalendarPatterns.getDateWithTimeByMiliSeconds(new Date());

						recordingService.recordMeetingStream(current, client, recordingName, "", false);
					} else {
						log.warn("Recording is already started for the client id=" + client.getId() + ". Second request is ignored.");
					}
				}
				if (startPublishing) {
					sendMessageToCurrentScope("startedPublishing", new Object[]{client, "rtmp://" + map.get("publishingHost") + ":1935/"
							+ map.get("publishingApp") + "/" + map.get("publishingId")}, false, true);
					returnMap.put("modus", "startPublishing");
				}
				return returnMap;
			} else {
				log.error("[setConnectionAsSharingClient] Could not find Screen Sharing Client " + current.getClient().getId());
			}
		} catch (Exception err) {
			log.error("[setConnectionAsSharingClient]", err);
		}
		return null;
	}

	/**
	 * Logic must be before roomDisconnect cause otherwise you cannot throw a
	 * message to each one
	 *
	 */
	@Override
	public void roomLeave(IClient client, IScope room) {
		try {
			log.debug("[roomLeave] {} {} {} {}", client.getId(), room.getClients().size(), room.getContextPath(), room.getName());

			StreamClient rcl = sessionManager.getClientByStreamId(client.getId(), null);

			// The Room Client can be null if the Client left the room by using
			// logicalRoomLeave
			if (rcl != null) {
				log.debug("currentClient IS NOT NULL");
				roomLeaveByScope(rcl, room);
			}
		} catch (Exception err) {
			log.error("[roomLeave]", err);
		}
	}

	public void roomLeaveByScope(String uid, Long roomId) {
		StreamClient rcl = sessionManager.getClientByPublicSID(uid, null);
		IScope scope = getRoomScope("" + roomId);
		log.debug("[roomLeaveByScope] {} {} {} {}", uid, roomId, rcl, scope);
		if (rcl != null && scope != null) {
			roomLeaveByScope(rcl, scope);
		}
	}

	/**
	 * Removes the Client from the List, stops recording, adds the Room-Leave
	 * event to running recordings, clear Polls and removes Client from any list
	 *
	 * This function is kind of private/protected as the client won't be able
	 * to call it with proper values.
	 *
	 * @param client
	 * @param scope
	 */
	public void roomLeaveByScope(StreamClient client, IScope scope) {
		try {
			log.debug("[roomLeaveByScope] currentClient " + client);
			if (client.isScreenClient() && client.isStartStreaming()) {
				//TODO check others/find better way
				WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoomId(), client.getUserId(), RoomMessage.Type.sharingStoped, client.getStreamPublishName()));
			} 
			if (client.getIsBroadcasting()) {
				WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoomId(), client.getUserId(), RoomMessage.Type.closeStream, client.getPublicSID()));
			}

			log.debug("removing Username " + client.getUsername() + " "
					+ client.getConnectedSince() + " streamid: "
					+ client.getStreamid());

			// stop and save any recordings
			if (client.getIsRecording()) {
				log.debug("*** roomLeave Current Client is Recording - stop that");
				if (client.getInterviewPodId() != null) {
					//interview, TODO need better check
					_stopInterviewRecording(client, scope);
				} else {
					recordingService.stopRecordAndSave(scope, client, null);

					// set to true and overwrite the default one cause otherwise no
					// notification is send
					client.setIsRecording(true);
				}
			}
			recordingService.stopRecordingShowForClient(scope, client);

			// Notify all clients of the same currentScope (room) with domain
			// and room except the current disconnected cause it could throw an exception
			log.debug("currentScope " + scope);

			new MessageSender(scope, "roomDisconnect", client, this) {
				@Override
				public boolean filter(IConnection conn) {
					StreamClient rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
					if (rcl == null) {
						return true;
					}
					boolean isScreen = rcl.isScreenClient();
					if (isScreen && client.getPublicSID().equals(rcl.getStreamPublishName())) {
						//going to terminate screen sharing started by this client
						((IServiceCapableConnection) conn).invoke("stopStream", new Object[] { }, callback);
					}
					return isScreen;
				}
			}.start();

			if (client.isMobile()) {
				IApplication app = (IApplication)Application.get(OpenmeetingsVariables.wicketApplicationName);
				app.exit(client.getPublicSID());
			}
			sessionManager.removeClient(client.getStreamid(), null);
		} catch (Exception err) {
			log.error("[roomLeaveByScope]", err);
		}
	}

	/**
	 * This method handles the Event after a stream has been added all connected
	 * Clients in the same room will get a notification
	 *
	 */
	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamPublishStart(org.red5.server.api.stream.IBroadcastStream)
	 */
	@Override
	public void streamPublishStart(IBroadcastStream stream) {
		try {
			log.debug("-----------  streamPublishStart");
			IConnection current = Red5.getConnectionLocal();
			final String streamid = current.getClient().getId();
			final StreamClient c = sessionManager.getClientByStreamId(streamid, null);

			//We make a second object the has the reference to the object
			//that we will use to send to all participents
			StreamClient clientObjectSendToSync = c;

			// Notify all the clients that the stream had been started
			log.debug("start streamPublishStart broadcast start: " + stream.getPublishedName() + " CONN " + current);

			// In case its a screen sharing we start a new Video for that
			if (c.isScreenClient()) {
				c.setScreenPublishStarted(true);
				sessionManager.updateClientByStreamId(streamid, c, false, null);
			}
			if (!c.isMobile() && !Strings.isEmpty(c.getSecurityCode())) {
				c.setBroadCastID(Long.parseLong(stream.getPublishedName()));
				c.setAvsettings("av");
				c.setIsBroadcasting(true);
				if (c.getVWidth() == 0 || c.getVHeight() == 0) {
					c.setVWidth(320);
					c.setVHeight(240);
				}
				sessionManager.updateClientByStreamId(streamid, c, false, null);
			}

			log.debug("newStream SEND: " + c);

			// Notify all users of the same Scope
			// We need to iterate through the streams to catch if anybody is recording
			new MessageSender(current, "newStream", clientObjectSendToSync, this) {
				@Override
				public boolean filter(IConnection conn) {
					StreamClient rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);

					if (rcl == null) {
						log.debug("RCL IS NULL newStream SEND");
						return true;
					}

					log.debug("check send to "+rcl);

					if (Strings.isEmpty(rcl.getPublicSID())) {
						log.debug("publicSID IS NULL newStream SEND");
						return true;
					}
					if (rcl.getIsRecording()) {
						log.debug("RCL getIsRecording newStream SEND");
						recordingService.addRecordingByStreamId(current, c, rcl.getRecordingId());
					}
					if (rcl.isScreenClient()) {
						log.debug("RCL getIsScreenClient newStream SEND");
						return true;
					}

					if (rcl.getPublicSID().equals(c.getPublicSID())) {
						log.debug("RCL publicSID is equal newStream SEND");
						return true;
					}
					log.debug("RCL SEND is equal newStream SEND "+rcl.getPublicSID()+" || "+rcl.getUserport());
					return false;
				}
			}.start();
			JSONObject obj = new JSONObject().put("uid", c.getPublicSID()).put("screenShare", c.isScreenClient());
			WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c.getUserId(), RoomMessage.Type.newStream, obj.toString()));
		} catch (Exception err) {
			log.error("[streamPublishStart]", err);
		}
	}

	/**
	 * This method handles the Event after a stream has been removed all
	 * connected Clients in the same room will get a notification
	 *
	 */
	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamBroadcastClose(org.red5.server.api.stream.IBroadcastStream)
	 */
	@Override
	public void streamBroadcastClose(IBroadcastStream stream) {
		// Notify all the clients that the stream had been closed
		log.debug("start streamBroadcastClose broadcast close: " + stream.getPublishedName());
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamId = current.getClient().getId();
			StreamClient rcl = sessionManager.getClientByStreamId(streamId, null);

			if (rcl == null) {

				// In case the client has already left(kicked) this message
				// might be thrown later then the RoomLeave
				// event and the currentClient is already gone
				// The second Use-Case where the currentClient is maybe null is
				// if we remove the client because its a Zombie/Ghost

				return;

			}
			// Notify all the clients that the stream had been started
			log.debug("streamBroadcastClose : " + rcl + " " + rcl.getStreamid());
			// this close stream event, stop the recording of this stream
			if (rcl.getIsRecording()) {
				log.debug("***  +++++++ ######## sendClientBroadcastNotifications Any Client is Recording - stop that");
				recordingService.stopRecordingShowForClient(current.getScope(), rcl);
			}
			if (stream.getPublishedName().equals("" + rcl.getBroadCastID())) {
				rcl.setBroadCastID(-1);
				rcl.setIsBroadcasting(false);
				rcl.setAvsettings("n");
			}
			sessionManager.updateClientByStreamId(streamId, rcl, false, null);
			// Notify all clients of the same scope (room)
			sendMessageToCurrentScope("closeStream", rcl, rcl.isMobile());
			if (rcl.isScreenClient()) {
				WebSocketHelper.sendRoom(new TextRoomMessage(rcl.getRoomId(), rcl.getUserId(), RoomMessage.Type.closeStream, rcl.getPublicSID()));
			}
		} catch (Exception e) {
			log.error("[streamBroadcastClose]", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void setNewCursorPosition(Object item) {
		try {
			IConnection current = Red5.getConnectionLocal();
			StreamClient c = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			@SuppressWarnings("rawtypes")
			Map cursor = (Map) item;
			cursor.put("streamPublishName", c.getStreamPublishName());

			sendMessageToCurrentScope("newRed5ScreenCursor", cursor, true, false);
		} catch (Exception err) {
			log.error("[setNewCursorPosition]", err);
		}
	}

	public long switchMicMuted(String publicSID, boolean mute) {
		try {
			log.debug("-----------  switchMicMuted: " + publicSID);

			StreamClient currentClient = sessionManager.getClientByPublicSID(publicSID, null);
			if (currentClient == null) {
				return -1L;
			}

			currentClient.setMicMuted(mute);
			sessionManager.updateClientByStreamId(currentClient.getStreamid(), currentClient, false, null);

			Map<Integer, Object> newMessage = new HashMap<>();
			newMessage.put(0, "updateMuteStatus");
			newMessage.put(1, currentClient);
			sendMessageWithClient(newMessage);
		} catch (Exception err) {
			log.error("[switchMicMuted]", err);
		}
		return 0L;
	}

	public static long nextBroadCastId() {
		return broadCastCounter.getAndIncrement();
	}

	public int sendMessage(Object newMessage) {
		sendMessageToCurrentScope("sendVarsToMessage", newMessage, false);
		return 1;
	}

	public int sendMessageAll(Object newMessage) {
		sendMessageToCurrentScope("sendVarsToMessage", newMessage, true);
		return 1;
	}

	/**
	 * wrapper method
	 * @param newMessage
	 */
	public void sendMessageToMembers(List<?> newMessage) {
		//Sync to all users of current scope
		sendMessageToCurrentScope("sendVarsToMessage", newMessage, false);
	}

	/**
	 * General sync mechanism for all messages that are send from within the
	 * scope of the current client, but:
	 * <ul>
	 * <li>optionally do not send to self (see param: sendSelf)</li>
	 * <li>do not send to clients that are screen sharing clients</li>
	 * <li>do not send to clients that are audio/video clients (or potentially ones)</li>
	 * <li>do not send to connections where no RoomClient is registered</li>
	 * </ul>
	 *
	 * @param remoteMethodName The method to be called
	 * @param newMessage parameters
	 * @param sendSelf send to the current client as well
	 */
	public void sendMessageToCurrentScope(String remoteMethodName, Object newMessage, boolean sendSelf) {
		sendMessageToCurrentScope(remoteMethodName, newMessage, sendSelf, false);
	}

	public void sendMessageToCurrentScope(String scopeName, String remoteMethodName, Object newMessage, boolean sendSelf) {
		sendMessageToCurrentScope(scopeName, remoteMethodName, newMessage, sendSelf, false);
	}

	public void sendToScope(final Long roomId, String method, Object obj) {
		new MessageSender(getRoomScope("" + roomId), method, obj, this) {
			@Override
			public boolean filter(IConnection conn) {
				StreamClient rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
				return rcl == null || rcl.isScreenClient()
						|| rcl.getRoomId() == null || !rcl.getRoomId().equals(roomId) || userDao.get(rcl.getUserId()) == null;
			}
		}.start();
	}

	/**
	 * Only temporary for load test, with return argument for the client to have a result
	 *
	 * @param remoteMethodName
	 * @param newMessage
	 * @param sendSelf
	 * @return true
	 */
	@Deprecated
	public boolean loadTestSyncMessage(String remoteMethodName, Object newMessage, boolean sendSelf) {
		sendMessageToCurrentScope(remoteMethodName, newMessage, sendSelf, false);
		return true;
	}

	/**
	 * General sync mechanism for all messages that are send from within the
	 * scope of the current client, but:
	 * <ul>
	 * <li>optionally do not send to self (see param: sendSelf)</li>
	 * <li>send to clients that are screen sharing clients based on parameter</li>
	 * <li>do not send to clients that are audio/video clients (or potentially ones)</li>
	 * <li>do not send to connections where no RoomClient is registered</li>
	 * </ul>
	 *
	 * @param method The method to be called
	 * @param msg parameters
	 * @param sendSelf send to the current client as well
	 * @param sendScreen send to the current client as well
	 */
	public void sendMessageToCurrentScope(final String method, final Object msg, final boolean sendSelf, final boolean sendScreen) {
		IConnection conn = Red5.getConnectionLocal();
		if (conn == null) {
			log.warn("[sendMessageToCurrentScope] -> 'Unable to send message using NULL connection' {}, {}", method, msg);
			return;
		}
		sendMessageToCurrentScope(conn.getScope().getName(), method, msg, sendSelf, sendScreen);
	}

	public void sendMessageToCurrentScope(final String scopeName, final String remoteMethodName, final Object newMessage, final boolean sendSelf, final boolean sendScreen) {
		new MessageSender(getRoomScope(scopeName), remoteMethodName, newMessage, this) {
			@Override
			public boolean filter(IConnection conn) {
				IClient client = conn.getClient();
				return (!sendScreen && SessionVariablesUtil.isScreenClient(client))
						|| (!sendSelf && current != null && client.getId().equals(current.getClient().getId()));
			}
		}.start();
	}

	public static abstract class MessageSender extends Thread {
		final IScope scope;
		final IConnection current;
		final String method;
		final Object msg;
		final IPendingServiceCallback callback;

		public MessageSender(final String remoteMethodName, final Object newMessage, IPendingServiceCallback callback) {
			this((IScope)null, remoteMethodName, newMessage, callback);
		}

		public MessageSender(IScope _scope, String method, Object msg, IPendingServiceCallback callback) {
			this(Red5.getConnectionLocal(), _scope, method, msg, callback);
		}

		public MessageSender(IConnection current, String method, Object msg, IPendingServiceCallback callback) {
			this(current, null, method, msg, callback);
		}

		public MessageSender(IConnection current, IScope _scope, String method, Object msg, IPendingServiceCallback callback) {
			this.current = current;
			scope = _scope == null && current != null ? current.getScope() : _scope;
			this.method = method;
			this.msg = msg;
			this.callback = callback;
		}

		public abstract boolean filter(IConnection conn);

		@Override
		public void run() {
			try {
				if (scope == null) {
					log.debug("[MessageSender] -> 'Unable to send message to NULL scope' {}, {}", method, msg);
				} else {
					if (log.isTraceEnabled()) {
						log.trace("[MessageSender] -> 'sending message' {}, {}", method, msg);
					}
					// Send to all Clients of that Scope(Room)
					int count = 0;
					for (IConnection conn : scope.getClientConnections()) {
						if (conn != null && conn instanceof IServiceCapableConnection) {
							if (filter(conn)) {
								continue;
							}
							((IServiceCapableConnection) conn).invoke(method, new Object[] { msg }, callback);
							count++;
						}
					}
					if (log.isTraceEnabled()) {
						log.trace("[MessageSender] -> 'sending message to {} clients, DONE' {}", count, method);
					}
				}
			} catch (Exception err) {
				log.error(String.format("[MessageSender -> %s, %s]", method, msg), err);
			}
		}
	}

	/**
	 * wrapper method
	 * @param newMessage
	 * @return 1 in case of success, -1 otherwise
	 */
	public int sendMessageWithClient(Object newMessage) {
		try {
			sendMessageWithClientWithSyncObject(newMessage, true);

		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}

	/**
	 * wrapper method
	 * @param newMessage
	 * @param sync
	 * @return 1 in case of success, -1 otherwise
	 */
	public int sendMessageWithClientWithSyncObject(Object newMessage, boolean sync) {
		try {
			IConnection current = Red5.getConnectionLocal();
			StreamClient currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			Map<String, Object> hsm = new HashMap<>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);

			//Sync to all users of current scope
			sendMessageToCurrentScope("sendVarsToMessageWithClient", hsm, sync);

		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}

	/**
	 * Function is used to send the kick Trigger at the moment,
	 * it sends a general message to a specific clientId
	 *
	 * @param newMessage
	 * @param clientId
	 * @return 1 in case of success, -1 otherwise
	 */
	public int sendMessageById(Object newMessage, String clientId, IScope scope) {
		try {
			log.debug("### sendMessageById ###" + clientId);

			Map<String, Object> hsm = new HashMap<>();
			hsm.put("message", newMessage);

			// broadcast Message to specific user with id inside the same Scope
			for (IConnection conn : scope.getClientConnections()) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						if (conn.getClient().getId().equals(clientId)) {
							((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient", new Object[] { hsm }, this);
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}

	/**
	 * Sends a message to a user in the same room by its clientId
	 *
	 * @param newMessage
	 * @param clientId
	 * @return 1 in case of no exceptions, -1 otherwise
	 */
	public int sendMessageWithClientById(Object newMessage, String clientId) {
		try {
			IConnection current = Red5.getConnectionLocal();
			StreamClient currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			Map<String, Object> hsm = new HashMap<>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);

			// broadcast Message to specific user with id inside the same Scope
			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn.getClient().getId().equals(clientId)) {
					((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient", new Object[] { hsm }, this);
				}
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}

	/**
	 * @deprecated this method should be reworked to use a single SQL query in
	 *             the cache to get any client in the current room that is
	 *             recording instead of iterating through connections!
	 * @return true in case there is recording session, false otherwise, null if any exception happend
	 */
	@Deprecated
	public boolean getInterviewRecordingStatus() {
		try {
			IConnection current = Red5.getConnectionLocal();

			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					StreamClient rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);

					if (rcl.getIsRecording()) {
						return true;
					}
				}
			}
		} catch (Exception err) {
			log.error("[getInterviewRecordingStatus]", err);
		}
		return false;
	}

	/**
	 * @deprecated @see {@link ScopeApplicationAdapter#getInterviewRecordingStatus()}
	 * @return - false if there were existing recording, true if recording was started successfully, null if any exception happens
	 */
	@Deprecated
	public boolean startInterviewRecording() {
		try {
			log.debug("-----------  startInterviewRecording");
			IConnection current = Red5.getConnectionLocal();

			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					StreamClient rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);

					if (rcl != null && rcl.getIsRecording()) {
						return false;
					}
				}
			}
			StreamClient current_rcl = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			// Also set the Recording Flag to Record all Participants that enter
			// later
			current_rcl.setIsRecording(true);
			sessionManager.updateClientByStreamId(current.getClient().getId(), current_rcl, false, null);

			Map<String, String> interviewStatus = new HashMap<>();
			interviewStatus.put("action", "start");

			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					IClient client = conn.getClient();
					if (SessionVariablesUtil.isScreenClient(client)) {
						// screen sharing clients do not receive events
						continue;
					}

					((IServiceCapableConnection) conn).invoke("interviewStatus", new Object[] { interviewStatus }, this);
					log.debug("-- startInterviewRecording " + interviewStatus);
				}
			}
			String recordingName = "Interview " + CalendarPatterns.getDateWithTimeByMiliSeconds(new Date());

			recordingService.recordMeetingStream(current, current_rcl, recordingName, "", true);

			return true;
		} catch (Exception err) {
			log.debug("[startInterviewRecording]", err);
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes" })
	public boolean sendRemoteCursorEvent(final String streamid, Map messageObj) {
		new MessageSender("sendRemoteCursorEvent", messageObj, this) {

			@Override
			public boolean filter(IConnection conn) {
				IClient client = conn.getClient();
				return !SessionVariablesUtil.isScreenClient(client) || !conn.getClient().getId().equals(streamid);
			}
		}.start();
		return true;
	}

	private Long checkRecordingClient(IConnection conn) {
		Long recordingId = null;
		if (conn != null) {
			StreamClient rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
			if (rcl != null && rcl.getIsRecording()) {
				rcl.setIsRecording(false);
				recordingId = rcl.getRecordingId();
				rcl.setRecordingId(null);

				// Reset the Recording Flag to Record all
				// Participants that enter later
				sessionManager.updateClientByStreamId(conn.getClient().getId(), rcl, false, null);
			}
		}
		return recordingId;
	}

	/**
	 * Stop the recording of the streams and send event to connected users of scope
	 *
	 * @return true if interview was found
	 */
	public boolean stopInterviewRecording() {
		IConnection current = Red5.getConnectionLocal();
		StreamClient currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);
		return _stopInterviewRecording(currentClient, current.getScope());
	}

	/**
	 * Stop the recording of the streams and send event to connected users of scope
	 *
	 * @return true if interview was found
	 */
	private boolean _stopInterviewRecording(StreamClient currentClient, IScope currentScope) {
		try {
			log.debug("-----------  stopInterviewRecording");
			Long clientRecordingId = currentClient.getRecordingId();

			for (IConnection conn : currentScope.getClientConnections()) {
				Long recordingId = checkRecordingClient(conn);
				if (recordingId != null) {
					clientRecordingId = recordingId;
				}
			}
			if (clientRecordingId == null) {
				log.debug("stopInterviewRecording:: unable to find recording client");
				return false;
			}

			recordingService.stopRecordAndSave(scope, currentClient, clientRecordingId);

			Map<String, String> interviewStatus = new HashMap<>();
			interviewStatus.put("action", "stop");

			sendMessageToCurrentScope("interviewStatus", interviewStatus, true);
			return true;

		} catch (Exception err) {
			log.debug("[stopInterviewRecording]", err);
		}
		return false;
	}

	public IScope getRoomScope(String room) {
		if (Strings.isEmpty(room)) {
			return null;
		} else {
			IScope globalScope = getContext().getGlobalScope();
			IScope webAppKeyScope = globalScope.getScope(OpenmeetingsVariables.webAppRootKey);

			return webAppKeyScope.getScope(room);
		}
	}

	/*
	 * SIP transport methods
	 */

	private List<Long> getVerifiedActiveRoomIds(Server s) {
		List<Long> result = new ArrayList<>(sessionManager.getActiveRoomIdsByServer(s));
		//verify
		for (Iterator<Long> i = result.iterator(); i.hasNext();) {
			Long id = i.next();
			List<StreamClient> rcs = sessionManager.getClientListByRoom(id);
			if (rcs.size() == 0 || (rcs.size() == 1 && rcs.get(0).isSipTransport())) {
				i.remove();
			}
		}
		return result.isEmpty() ? result : roomDao.getSipRooms(result);
	}

	public List<Long> getActiveRoomIds() {
		List<Long> result = getVerifiedActiveRoomIds(null);
		for (Server s : serverDao.getActiveServers()) {
			result.addAll(getVerifiedActiveRoomIds(s));
		}
		return result.isEmpty() ? result : roomDao.getSipRooms(result);
	}

	/**
	 * Returns number of SIP conference participants
	 * @param roomId id of room
	 * @return number of participants
	 */
	public Integer getSipConferenceMembersNumber(Long roomId) {
		Room r = roomDao.get(roomId);
		return r == null || r.getConfno() == null ? null : sipDao.countUsers(r.getConfno());
	}

	private String getSipTransportLastname(Long roomId) {
		return getSipTransportLastname(getSipConferenceMembersNumber(roomId));
	}

	private static String getSipTransportLastname(Integer c) {
		return (c != null && c > 0) ? "(" + (c - 1) + ")" : "";
	}

	public synchronized int updateSipTransport() {
		log.debug("-----------  updateSipTransport");
		IConnection current = Red5.getConnectionLocal();
		String streamid = current.getClient().getId();
		StreamClient client = sessionManager.getClientByStreamId(streamid, null);
		Long roomId = client.getRoomId();
		Integer count = getSipConferenceMembersNumber(roomId);
		String newNumber = getSipTransportLastname(count);
		log.debug("getSipConferenceMembersNumber: " + newNumber);
		if (!newNumber.equals(client.getLastname())) {
			client.setLastname(newNumber);
			sessionManager.updateClientByStreamId(streamid, client, false, null);
			log.debug("updateSipTransport: {}, {}, {}, {}, {}", new Object[] { client.getPublicSID(), client.getRoomId(),
					client.getFirstname(), client.getLastname(), client.getAvsettings() });
			sendMessageWithClient(new String[] { "personal", client.getFirstname(), client.getLastname() });
		}
		return count != null && count > 0 ? count - 1 : 0;
	}

	public void setSipTransport(Long roomId, String publicSID, String broadCastId) {
		log.debug("-----------  setSipTransport");
		IConnection current = Red5.getConnectionLocal();
		IClient c = current.getClient();
		String streamid = c.getId();
		// Notify all clients of the same scope (room)
		StreamClient currentClient = sessionManager.getClientByStreamId(streamid, null);
		currentClient.setSipTransport(true);
		currentClient.setRoomId(roomId);
		currentClient.setRoomEnter(new Date());
		currentClient.setFirstname("SIP Transport");
		currentClient.setLastname(getSipTransportLastname(roomId));
		currentClient.setBroadCastID(Long.parseLong(broadCastId));
		currentClient.setIsBroadcasting(true);
		currentClient.setPublicSID(publicSID);
		currentClient.setVWidth(120);
		currentClient.setVHeight(90);
		currentClient.setPicture_uri("phone.png");
		sessionManager.updateClientByStreamId(streamid, currentClient, false, null);
		SessionVariablesUtil.initClient(c, publicSID);

		sendMessageToCurrentScope("addNewUser", currentClient, false);
	}

	public JSONObject getFlashSettings() {
		return flashSettings;
	}
}
