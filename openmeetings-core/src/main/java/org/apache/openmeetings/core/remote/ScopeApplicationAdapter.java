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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EXT_PROCESS_TTL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_SECURE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_SECURE_PROXY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_VIDEO_CODEC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_HEADER_CSP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_HEADER_XFRAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.EXT_PROCESS_TTL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.HEADER_CSP_SELF;
import static org.apache.openmeetings.util.OpenmeetingsVariables.HEADER_XFRAME_SAMEORIGIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.wicketApplicationName;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.util.IClientUtil;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.SipDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.InitializationContainer;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.Version;
import org.apache.openmeetings.util.message.RoomMessage;
import org.apache.openmeetings.util.message.TextRoomMessage;
import org.apache.wicket.Application;
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
	private static final Logger _log = Red5LoggerFactory.getLogger(ScopeApplicationAdapter.class, webAppRootKey);
	private static final String OWNER_SID_PARAM = "ownerSid";
	private static final String PARENT_SID_PARAM = "parentSid"; //mobile
	private static final String MOBILE_PARAM = "mobileClient";
	private static final String WIDTH_PARAM = "width";
	private static final String HEIGHT_PARAM = "height";
	private static final String SIP_PARAM = "sipClient";
	public static final String HIBERNATE_SCOPE = "hibernate";
	public static final String FLASH_SECURE = "secure";
	public static final String FLASH_NATIVE_SSL = "native";
	public static final String FLASH_PORT = "rtmpPort";
	public static final String FLASH_SSL_PORT = "rtmpsPort";
	public static final String FLASH_VIDEO_CODEC = "videoCodec";
	public static final String FLASH_FPS = "fps";
	private JSONObject flashSettings;

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private RecordingService recordingService;
	@Autowired
	private ConfigurationDao cfgDao;
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
		if (_log.isTraceEnabled()) {
			_log.trace("resultReceived:: {}", arg0);
		}
	}

	@Override
	public boolean appStart(IScope scope) {
		try {
			OmFileHelper.setOmHome(scope.getResource("/").getFile());
			LabelDao.initLanguageMap();

			_log.debug("webAppPath : " + OmFileHelper.getOmHome());

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
				_log.debug("scopeName :: " + scopeName);
			}

			InitializationContainer.initComplete = true;
			// Init properties
			IApplication iapp = (IApplication)Application.get(wicketApplicationName);
			iapp.setXFrameOptions(cfgDao.getConfValue(CONFIG_HEADER_XFRAME, String.class, HEADER_XFRAME_SAMEORIGIN));
			iapp.setContentSecurityPolicy(cfgDao.getConfValue(CONFIG_HEADER_CSP, String.class, HEADER_CSP_SELF));
			EXT_PROCESS_TTL = cfgDao.getConfValue(CONFIG_EXT_PROCESS_TTL, Integer.class, "" + EXT_PROCESS_TTL);
			Version.logOMStarted();
			recordingDao.resetProcessingStatus(); //we are starting so all processing recordings are now errors
			sessionManager.clearCache(); // 'sticky' clients should be cleaned up from DB
		} catch (Exception err) {
			_log.error("[appStart]", err);
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
		_log.debug("roomConnect : ");

		IServiceCapableConnection service = (IServiceCapableConnection) conn;
		String streamId = conn.getClient().getId();

		_log.debug("### Client connected to OpenMeetings, register Client StreamId: {} scope {}", streamId, conn.getScope().getName());

		// Set StreamId in Client
		service.invoke("setId", new Object[] { streamId }, this);

		Map<String, Object> map = conn.getConnectParams();
		String swfURL = map.containsKey("swfUrl") ? (String)map.get("swfUrl") : "";
		String tcUrl = map.containsKey("tcUrl") ? (String)map.get("tcUrl") : "";
		Map<String, Object> connParams = getConnParams(params);
		String uid = (String)connParams.get("uid");
		StreamClient rcm = new StreamClient();
		rcm.setScope(conn.getScope().getName());
		boolean hibernate = HIBERNATE_SCOPE.equals(rcm.getScope());
		if (hibernate && "noclient".equals(uid)) {
			return true;
		}
		String ownerSid = (String)connParams.get(OWNER_SID_PARAM);
		if (Strings.isEmpty(ownerSid)) {
			ownerSid = (String)connParams.get(PARENT_SID_PARAM);
		}
		if (Strings.isEmpty(ownerSid)) {
			_log.warn("No Owner SID is provided, client is rejected");
			return rejectClient();
		}
		if (hibernate) {
			return true; //mobile initial connect
		}
		if (rcm.getRoomId() == null && !hibernate) {
			_log.warn("Bad room specified, client is rejected");
			return rejectClient();
		}
		if (Boolean.TRUE.equals(connParams.get(MOBILE_PARAM))) {
			rcm.setMobile(true);
		}
		if (Boolean.TRUE.equals(connParams.get(SIP_PARAM))) {
			rcm.setSipTransport(true);
		}
		rcm.setUid(Strings.isEmpty(uid) ? UUID.randomUUID().toString() : uid);
		rcm.setOwnerSid(ownerSid);
		rcm.setUserport(conn.getRemotePort());
		rcm.setUserip(conn.getRemoteAddress());
		rcm.setSwfurl(swfURL);
		rcm.setTcUrl(tcUrl);
		IApplication iapp = (IApplication)Application.get(wicketApplicationName);
		Number width = (Number)connParams.get(WIDTH_PARAM);
		Number height = (Number)connParams.get(HEIGHT_PARAM);
		if (width != null && height != null) {
			//this is for external applications like ffmpeg [OPENMEETINGS-1574]
			rcm.setWidth(width.intValue());
			rcm.setHeight(height.intValue());
		}
		if (map.containsKey("screenClient")) {
			rcm.setSharing(true);
		}
		rcm = sessionManager.add(iapp.updateClient(rcm, false), null);
		if (rcm == null) {
			_log.warn("Failed to create Client on room connect");
			return false;
		}
		IClientUtil.init(conn.getClient(), rcm.getId(), rcm.isSharing());

		// Log the User
		conferenceLogDao.add(ConferenceLog.Type.clientConnect,
				rcm.getUserId(), streamId, null, rcm.getUserip(),
				rcm.getScope());
		return true;
	}

	public Map<String, String> screenSharerAction(Map<String, Object> map) {
		Map<String, String> returnMap = new HashMap<>();
		try {
			_log.debug("-----------  screenSharerAction ENTER");
			IConnection current = Red5.getConnectionLocal();

			StreamClient client = sessionManager.get(IClientUtil.getId(current.getClient()));

			if (client != null) {
				boolean changed = false;
				if (Boolean.parseBoolean("" + map.get("stopStreaming")) && client.isSharingStarted()) {
					changed = true;
					client.setSharingStarted(false);
					//Send message to all users
					sendMessageToCurrentScope("stopScreenSharingMessage", client, false);

					returnMap.put("result", "stopSharingOnly");
				}
				if (Boolean.parseBoolean("" + map.get("stopRecording")) && client.isRecordingStarted()) {
					changed = true;
					client.setRecordingStarted(false);

					returnMap.put("result", "stopRecordingOnly");

					recordingService.stopRecordAndSave(current.getScope(), client, null);
				}
				if (Boolean.parseBoolean("" + map.get("stopPublishing")) && client.isPublishStarted()) {
					changed = true;
					client.setPublishStarted(false);
					returnMap.put("result", "stopPublishingOnly");

					//Send message to all users
					sendMessageToCurrentScope("stopPublishingMessage", client, false);
				}

				if (changed) {
					sessionManager.update(client);

					if (!client.isSharingStarted() && !client.isRecordingStarted() && !client.isPublishStarted()) {
						returnMap.put("result", "stopAll");
					}
				}
			}
			_log.debug("-----------  screenSharerAction, return: " + returnMap);
		} catch (Exception err) {
			_log.error("[screenSharerAction]", err);
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
			_log.debug("-----------  setConnectionAsSharingClient");
			IConnection current = Red5.getConnectionLocal();

			StreamClient client = sessionManager.get(IClientUtil.getId(current.getClient()));

			if (client != null) {
				boolean startRecording = Boolean.parseBoolean("" + map.get("startRecording"));
				boolean startStreaming = Boolean.parseBoolean("" + map.get("startStreaming"));
				boolean startPublishing = Boolean.parseBoolean("" + map.get("startPublishing")) && (0 == sessionManager.getPublishingCount(client.getRoomId()));

				boolean alreadyStreaming = client.isSharingStarted();
				if (startStreaming) {
					client.setSharingStarted(true);
				}
				boolean alreadyRecording = client.isRecordingStarted();
				if (startRecording) {
					client.setRecordingStarted(true);
				}
				boolean alreadyPublishing = client.isPublishStarted();
				if (startPublishing) {
					client.setPublishStarted(true);
				}

				client.setWidth(Double.valueOf("" + map.get("screenWidth")).intValue());
				client.setHeight(Double.valueOf("" + map.get("screenHeight")).intValue());
				sessionManager.update(client);

				Map<String, Object> returnMap = new HashMap<>();
				returnMap.put("alreadyPublished", alreadyPublishing);

				_log.debug("screen width,height {},{}", client.getWidth(), client.getHeight());

				if (startStreaming) {
					if (!alreadyStreaming) {
						returnMap.put("modus", "startStreaming");

						_log.debug("start streamPublishStart Is Screen Sharing ");

						//Send message to all users
						sendMessageToCurrentScope("newScreenSharing", client, false);
						WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoomId(), client.getUserId(), RoomMessage.Type.sharingStarted, client.getUid()));
					} else {
						_log.warn("Streaming is already started for the client id={}. Second request is ignored.", client.getId());
					}
				}
				if (startRecording) {
					if (!alreadyRecording) {
						returnMap.put("modus", "startRecording");

						String recordingName = "Recording " + CalendarPatterns.getDateWithTimeByMiliSeconds(new Date());

						recordingService.recordMeetingStream(current, client, recordingName, "", false);
					} else {
						_log.warn("Recording is already started for the client id={}. Second request is ignored.", client.getId());
					}
				}
				if (startPublishing) {
					sendMessageToCurrentScope("startedPublishing", new Object[]{client, "rtmp://" + map.get("publishingHost") + ":1935/"
							+ map.get("publishingApp") + "/" + map.get("publishingId")}, false, true);
					returnMap.put("modus", "startPublishing");
				}
				return returnMap;
			} else {
				_log.error("[setConnectionAsSharingClient] Could not find Screen Sharing Client " + current.getClient().getId());
			}
		} catch (Exception err) {
			_log.error("[setConnectionAsSharingClient]", err);
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
			_log.debug("[roomLeave] {} {} {} {}", client.getId(), room.getClients().size(), room.getContextPath(), room.getName());

			StreamClient rcl = sessionManager.get(IClientUtil.getId(client));

			// The Room Client can be null if the Client left the room by using
			// logicalRoomLeave
			if (rcl != null) {
				_log.debug("currentClient IS NOT NULL");
				roomLeaveByScope(rcl, room);
			}
		} catch (Exception err) {
			_log.error("[roomLeave]", err);
		}
	}

	public void roomLeaveByScope(String uid, Long roomId) {
		StreamClient rcl = sessionManager.getClientByUid(uid, null);
		IScope scope = getRoomScope("" + roomId);
		_log.debug("[roomLeaveByScope] {} {} {} {}", uid, roomId, rcl, scope);
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
			_log.debug("[roomLeaveByScope] currentClient " + client);
			if (client.isSharing() && client.isSharingStarted()) {
				sendSharingStoped(client);
			}
			if (client.isBroadcasting()) {
				sendStreamClosed(client);
			}

			_log.debug("removing Username {} {},  streamid: {}", client.getUsername()
					, client.getConnectedSince(), client.getId());

			// stop and save any recordings
			if (client.isRecordingStarted()) {
				_log.debug("*** roomLeave Current Client is Recording - stop that");
				if (client.getInterviewPodId() != null) {
					//interview, TODO need better check
					_stopInterviewRecording(client, scope);
				} else {
					recordingService.stopRecordAndSave(scope, client, null);
				}
			}
			recordingService.stopRecordingShowForClient(scope, client);

			// Notify all clients of the same currentScope (room) with domain
			// and room except the current disconnected cause it could throw an exception
			_log.debug("currentScope " + scope);

			if (client.isMobile() || client.isSipTransport()) {
				IApplication app = (IApplication)Application.get(wicketApplicationName);
				app.exit(client.getUid());
			}
			sessionManager.remove(client.getId());
		} catch (Exception err) {
			_log.error("[roomLeaveByScope]", err);
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
			_log.debug("-----------  streamPublishStart");
			IConnection current = Red5.getConnectionLocal();
			final StreamClient c = sessionManager.get(IClientUtil.getId(current.getClient()));

			// Notify all the clients that the stream had been started
			String streamName = stream.getPublishedName();
			_log.debug("start streamPublishStart broadcast start: {}, CONN {}", streamName, current);
			c.setBroadCastId(streamName);

			// In case its a screen sharing we start a new Video for that
			if (c.isSharing()) {
				c.setSharingStarted(true);
			} else if (!c.isMobile()) {
				c.setAvsettings("av");
				c.setBroadcasting(true);
				if (c.getWidth() == 0 || c.getHeight() == 0) {
					c.setWidth(320);
					c.setHeight(240);
				}
			}
			sessionManager.update(c);

			_log.debug("newStream SEND: {}", c);

			// Notify all users of the same Scope
			// We need to iterate through the streams to catch if anybody is recording
			new MessageSender(current, "newStream", c, this) {
				@Override
				public boolean filter(IConnection conn) {
					StreamClient rcl = sessionManager.get(IClientUtil.getId(conn.getClient()));

					if (rcl == null) {
						_log.debug("RCL IS NULL newStream SEND");
						return true;
					}

					_log.debug("check send to {}", rcl);

					if (Strings.isEmpty(rcl.getUid())) {
						_log.debug("publicSID IS NULL newStream SEND");
						return true;
					}
					if (rcl.isRecordingStarted()) {
						_log.debug("RCL getIsRecording newStream SEND");
						recordingService.addRecordingByStreamId(current, c, rcl.getRecordingId());
					}
					if (rcl.isSharing()) {
						_log.debug("RCL getisSharing newStream SEND");
						return true;
					}

					if (rcl.getUid().equals(c.getUid())) {
						_log.debug("RCL publicSID is equal newStream SEND");
						return true;
					}
					_log.debug("RCL SEND is equal newStream SEND {} || {}", rcl.getUid(), rcl.getUserport());
					return false;
				}
			}.start();
			JSONObject obj = new JSONObject()
					.put("ownerSid", c.getOwnerSid())
					.put("uid", c.getUid())
					.put("screenShare", c.isSharing())
					.put("streamClientId", c.getId())
					.put("stream", streamName);
			WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c.getUserId(), RoomMessage.Type.newStream, obj.toString()));
		} catch (Exception err) {
			_log.error("[streamPublishStart]", err);
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
		_log.debug("start streamBroadcastClose broadcast close: {}", stream.getPublishedName());
		try {
			IConnection current = Red5.getConnectionLocal();
			StreamClient rcl = sessionManager.get(IClientUtil.getId(current.getClient()));

			if (rcl == null) {

				// In case the client has already left(kicked) this message
				// might be thrown later then the RoomLeave
				// event and the currentClient is already gone
				// The second Use-Case where the currentClient is maybe null is
				// if we remove the client because its a Zombie/Ghost

				return;

			}
			// Notify all the clients that the stream had been started
			_log.debug("streamBroadcastClose : {} ", rcl);
			// this close stream event, stop the recording of this stream
			if (rcl.isRecordingStarted()) {
				_log.debug("***  +++++++ ######## sendClientBroadcastNotifications Any Client is Recording - stop that");
				recordingService.stopRecordingShowForClient(current.getScope(), rcl);
			}
			sendStreamClosed(rcl);
			if (stream.getPublishedName().equals(rcl.getBroadCastId())) {
				rcl.setBroadCastId(null);
				rcl.setBroadcasting(false);
				rcl.setAvsettings("n");
			}
			sessionManager.update(rcl);
			// Notify all clients of the same scope (room)
			sendMessageToCurrentScope("closeStream", rcl, rcl.isMobile());
			if (rcl.isSharing()) {
				sendSharingStoped(rcl);
			}
		} catch (Exception e) {
			_log.error("[streamBroadcastClose]", e);
		}
	}

	private static void sendSharingStoped(StreamClient rcl) {
		JSONObject obj = new JSONObject()
				.put("ownerSid", rcl.getOwnerSid())
				.put("uid", rcl.getUid());
		WebSocketHelper.sendRoom(new TextRoomMessage(rcl.getRoomId(), rcl.getUserId(), RoomMessage.Type.sharingStoped, obj.toString()));
	}

	private static void sendStreamClosed(StreamClient rcl) {
		JSONObject obj = new JSONObject()
				.put("uid", rcl.getUid())
				.put("ownerSid", rcl.getOwnerSid())
				.put("broadcastId", rcl.getBroadCastId());
		WebSocketHelper.sendRoom(new TextRoomMessage(rcl.getRoomId(), rcl.getUserId(), RoomMessage.Type.closeStream, obj.toString()));
	}

	/** TODO need to be implemented in Flex
	public void setNewCursorPosition(Map<String, Object> cursor) {
		try {
			IConnection current = Red5.getConnectionLocal();
			StreamClient c = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			cursor.put("streamPublishName", c.getStreamPublishName());

			sendMessageToCurrentScope("newRed5ScreenCursor", cursor, true, false);
		} catch (Exception err) {
			_log.error("[setNewCursorPosition]", err);
		}
	}
	*/

	public long switchMicMuted(String publicSID, boolean mute) {
		try {
			_log.debug("-----------  switchMicMuted: " + publicSID);

			StreamClient currentClient = sessionManager.getClientByUid(publicSID, null);
			if (currentClient == null) {
				return -1L;
			}

			currentClient.setMicMuted(mute);
			sessionManager.update(currentClient);

			Map<Integer, Object> newMessage = new HashMap<>();
			newMessage.put(0, "updateMuteStatus");
			newMessage.put(1, currentClient);
			sendMessageWithClient(newMessage);
		} catch (Exception err) {
			_log.error("[switchMicMuted]", err);
		}
		return 0L;
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
				StreamClient rcl = sessionManager.get(IClientUtil.getId(conn.getClient()));
				return rcl == null || rcl.isSharing()
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
			_log.warn("[sendMessageToCurrentScope] -> 'Unable to send message using NULL connection' {}, {}", method, msg);
			return;
		}
		sendMessageToCurrentScope(conn.getScope().getName(), method, msg, sendSelf, sendScreen);
	}

	public void sendMessageToCurrentScope(final String scopeName, final String remoteMethodName, final Object newMessage, final boolean sendSelf, final boolean sendScreen) {
		new MessageSender(getRoomScope(scopeName), remoteMethodName, newMessage, this) {
			@Override
			public boolean filter(IConnection conn) {
				IClient client = conn.getClient();
				return (!sendScreen && IClientUtil.isSharing(client))
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
					_log.debug("[MessageSender] -> 'Unable to send message to NULL scope' {}, {}", method, msg);
				} else {
					if (_log.isTraceEnabled()) {
						_log.trace("[MessageSender] -> 'sending message' {}, {}", method, msg);
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
					if (_log.isTraceEnabled()) {
						_log.trace("[MessageSender] -> 'sending message to {} clients, DONE' {}", count, method);
					}
				}
			} catch (Exception err) {
				_log.error(String.format("[MessageSender -> %s, %s]", method, msg), err);
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
			_log.error("[sendMessageWithClient] ", err);
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
			StreamClient currentClient = sessionManager.get(IClientUtil.getId(current.getClient()));

			Map<String, Object> hsm = new HashMap<>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);

			//Sync to all users of current scope
			sendMessageToCurrentScope("sendVarsToMessageWithClient", hsm, sync);

		} catch (Exception err) {
			_log.error("[sendMessageWithClient] ", err);
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
	public int sendMessageById(Object newMessage, final Long id, IScope scope) {
		try {
			_log.debug("### sendMessageById ### {}", id);

			Map<String, Object> hsm = new HashMap<>();
			hsm.put("message", newMessage);

			// broadcast Message to specific user with id inside the same Scope
			for (IConnection conn : scope.getClientConnections()) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						if (id.equals(IClientUtil.getId(conn.getClient()))) {
							((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient", new Object[] { hsm }, this);
						}
					}
				}
			}
		} catch (Exception err) {
			_log.error("[sendMessageWithClient] ", err);
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
			StreamClient currentClient = sessionManager.get(IClientUtil.getId(current.getClient()));

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
			_log.error("[sendMessageWithClient] ", err);
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
					StreamClient rcl = sessionManager.get(IClientUtil.getId(conn.getClient()));

					if (rcl != null && rcl.isRecordingStarted()) {
						return true;
					}
				}
			}
		} catch (Exception err) {
			_log.error("[getInterviewRecordingStatus]", err);
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
			_log.debug("-----------  startInterviewRecording");
			IConnection current = Red5.getConnectionLocal();

			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					StreamClient rcl = sessionManager.get(IClientUtil.getId(conn.getClient()));

					if (rcl != null && rcl.isRecordingStarted()) {
						return false;
					}
				}
			}
			StreamClient rcl = sessionManager.get(IClientUtil.getId(current.getClient()));

			// Also set the Recording Flag to Record all Participants that enter later
			rcl.setRecordingStarted(true);
			sessionManager.update(rcl);

			Map<String, String> interviewStatus = new HashMap<>();
			interviewStatus.put("action", "start");

			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					IClient client = conn.getClient();
					if (IClientUtil.isSharing(client)) {
						// screen sharing clients do not receive events
						continue;
					}

					((IServiceCapableConnection) conn).invoke("interviewStatus", new Object[] { interviewStatus }, this);
					_log.debug("-- startInterviewRecording " + interviewStatus);
				}
			}
			String recordingName = "Interview " + CalendarPatterns.getDateWithTimeByMiliSeconds(new Date());

			recordingService.recordMeetingStream(current, rcl, recordingName, "", true);

			return true;
		} catch (Exception err) {
			_log.debug("[startInterviewRecording]", err);
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes" })
	public boolean sendRemoteCursorEvent(final String streamid, Map messageObj) {
		new MessageSender("sendRemoteCursorEvent", messageObj, this) {

			@Override
			public boolean filter(IConnection conn) {
				IClient client = conn.getClient();
				return !IClientUtil.isSharing(client) || !conn.getClient().getId().equals(streamid);
			}
		}.start();
		return true;
	}

	/**
	 * Stop the recording of the streams and send event to connected users of scope
	 *
	 * @return true if interview was found
	 */
	public boolean stopInterviewRecording() {
		IConnection current = Red5.getConnectionLocal();
		StreamClient currentClient = sessionManager.get(IClientUtil.getId(current.getClient()));
		return _stopInterviewRecording(currentClient, current.getScope());
	}

	/**
	 * Stop the recording of the streams and send event to connected users of scope
	 *
	 * @return true if interview was found
	 */
	private boolean _stopInterviewRecording(StreamClient currentClient, IScope currentScope) {
		try {
			_log.debug("-----------  stopInterviewRecording");
			Long clientRecordingId = currentClient.getRecordingId();

			for (IConnection conn : currentScope.getClientConnections()) {
				Long recordingId = null;
				if (conn != null) {
					StreamClient rcl = sessionManager.get(IClientUtil.getId(conn.getClient()));
					if (rcl != null && rcl.isRecordingStarted()) {
						rcl.setRecordingStarted(false);
						recordingId = rcl.getRecordingId();
						rcl.setRecordingId(null);

						// Reset the Recording Flag to Record all
						// Participants that enter later
						sessionManager.update(rcl);
					}
				}
				if (recordingId != null) {
					clientRecordingId = recordingId;
				}
			}
			if (clientRecordingId == null) {
				_log.debug("stopInterviewRecording:: unable to find recording client");
				return false;
			}

			recordingService.stopRecordAndSave(scope, currentClient, clientRecordingId);

			Map<String, String> interviewStatus = new HashMap<>();
			interviewStatus.put("action", "stop");

			sendMessageToCurrentScope("interviewStatus", interviewStatus, true);
			return true;

		} catch (Exception err) {
			_log.debug("[stopInterviewRecording]", err);
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

	public String getSipNumber(Double roomId) {
		Room r = roomDao.get(roomId.longValue());
		if (r != null && r.getConfno() != null) {
			log.debug("getSipNumber: roomId: {}, sipNumber: {}", new Object[]{roomId, r.getConfno()});
			return r.getConfno();
		}
		return null;
	}

	public List<Long> getActiveRoomIds() {
		Set<Long> ids = new HashSet<>();
		ids.addAll(getVerifiedActiveRoomIds(null));
		for (Server s : serverDao.getActiveServers()) {
			ids.addAll(getVerifiedActiveRoomIds(s));
		}
		return new ArrayList<>(ids);
	}

	public synchronized int updateSipTransport() {
		_log.debug("-----------  updateSipTransport");
		IConnection current = Red5.getConnectionLocal();
		StreamClient client = sessionManager.get(IClientUtil.getId(current.getClient()));
		Long roomId = client.getRoomId();
		Integer count = getSipConferenceMembersNumber(roomId);
		String newNumber = getSipTransportLastname(count);
		_log.debug("getSipConferenceMembersNumber: " + newNumber);
		if (!newNumber.equals(client.getLastname())) {
			client.setLastname(newNumber);
			sessionManager.update(client);
			_log.debug("updateSipTransport: {}, {}, {}, {}, {}", new Object[] { client.getUid(), client.getRoomId(),
					client.getFirstname(), client.getLastname(), client.getAvsettings() });
			sendMessageWithClient(new String[] { "personal", client.getFirstname(), client.getLastname() });
		}
		return count != null && count > 0 ? count - 1 : 0;
	}

	public void setSipTransport(String broadCastId) {
		_log.debug("-----------  setSipTransport");
		IConnection current = Red5.getConnectionLocal();
		IClient client = current.getClient();
		// Notify all clients of the same scope (room)
		StreamClient c = sessionManager.get(IClientUtil.getId(client));
		c.setLastname(getSipTransportLastname(c.getRoomId()));
		c.setBroadCastId(broadCastId);
		sessionManager.update(c);

		sendMessageToCurrentScope("addNewUser", c, false);
	}

	public JSONObject getFlashSettings() {
		return flashSettings;
	}
}
