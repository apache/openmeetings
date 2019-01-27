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

import static org.apache.openmeetings.util.OmFileHelper.HIBERNATE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EXT_PROCESS_TTL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_HEADER_CSP;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_HEADER_XFRAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.HEADER_CSP_SELF;
import static org.apache.openmeetings.util.OpenmeetingsVariables.HEADER_XFRAME_SAMEORIGIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getExtProcessTtl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWicketApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setExtProcessTtl;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setInitComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.service.RecordingService;
import org.apache.openmeetings.core.util.IClientUtil;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.SipDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.room.CheckDto;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.manager.IStreamClientManager;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.db.util.ws.TextRoomMessage;
import org.apache.openmeetings.util.NullStringer;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.Version;
import org.apache.wicket.Application;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.scope.ScopeType;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IBroadcastStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.openjson.JSONObject;

@Service("web.handler")
public class ScopeApplicationAdapter extends MultiThreadedApplicationAdapter implements IPendingServiceCallback {
	private static final Logger _log = Red5LoggerFactory.getLogger(ScopeApplicationAdapter.class, getWebAppRootKey());
	private static final String SID_PARAM = "sid";
	private static final String PARENT_SID_PARAM = "parentSid"; //mobile
	private static final String MOBILE_PARAM = "mobileClient";
	private static final String ROOM_PARAM = "roomClient";
	private static final String WIDTH_PARAM = "width";
	private static final String HEIGHT_PARAM = "height";

	@Autowired
	private IStreamClientManager streamClientManager;
	@Autowired
	private IClientManager clientManager;
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

	public static IApplication getApp() {
		return (IApplication)Application.get(getWicketApplicationName());
	}

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

			// Init all global config properties
			cfgDao.reinit();

			for (String scopeName : scope.getScopeNames()) {
				_log.debug("scopeName :: " + scopeName);
			}

			setInitComplete(true);
			// Init properties
			IApplication iapp = getApp();
			iapp.setXFrameOptions(cfgDao.getString(CONFIG_HEADER_XFRAME, HEADER_XFRAME_SAMEORIGIN));
			iapp.setContentSecurityPolicy(cfgDao.getString(CONFIG_HEADER_CSP, HEADER_CSP_SELF));
			iapp.updateJpaAddresses(cfgDao);
			setExtProcessTtl(cfgDao.getInt(CONFIG_EXT_PROCESS_TTL, getExtProcessTtl()));
			Version.logOMStarted();
			recordingDao.resetProcessingStatus(); //we are starting so all processing recordings are now errors
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
	public boolean appConnect(IConnection conn, Object[] params) {
		if (conn != null && conn.getScope() != null && conn.getScope().getType() == ScopeType.APPLICATION) {
			return false;
		}
		return super.appConnect(conn, params);
	}

	@Override
	public void appDisconnect(IConnection conn) {
		StreamClient c = streamClientManager.get(IClientUtil.getId(conn.getClient()));
		if (c != null && Client.Type.sip == c.getType()) {
			clientManager.exit(c);
		}
		super.appDisconnect(conn);
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
		boolean hibernate = HIBERNATE.equals(rcm.getScope());
		if (hibernate && "noclient".equals(uid)) {
			return true;
		}
		String sid = (String)connParams.get(SID_PARAM);
		if (Strings.isEmpty(sid)) {
			sid = (String)connParams.get(PARENT_SID_PARAM);
		}
		if (Strings.isEmpty(sid)) {
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
			rcm.setType(Client.Type.mobile);
		} else if (Boolean.TRUE.equals(connParams.get(ROOM_PARAM))) {
			rcm.setType(Client.Type.room);
		}
		rcm.setUid(Strings.isEmpty(uid) ? UUID.randomUUID().toString() : uid);
		rcm.setSid(sid);
		if (sipDao.getUid() != null && sipDao.getUid().equals(rcm.getSid())) {
			rcm.setType(Client.Type.sip);
		}
		rcm.setUserport(conn.getRemotePort());
		rcm.setRemoteAddress(conn.getRemoteAddress());
		rcm.setSwfurl(swfURL);
		rcm.setTcUrl(tcUrl);
		Number width = (Number)connParams.get(WIDTH_PARAM);
		Number height = (Number)connParams.get(HEIGHT_PARAM);
		if (width != null && height != null) {
			//this is for external applications like ffmpeg [OPENMEETINGS-1574]
			rcm.setWidth(width.intValue());
			rcm.setHeight(height.intValue());
		}
		if (map.containsKey("screenClient")) {
			rcm.setType(Client.Type.sharing);
		}
		rcm = streamClientManager.add(streamClientManager.update(rcm, false));
		if (rcm == null) {
			_log.warn("Failed to create Client on room connect");
			return false;
		}
		IClientUtil.init(conn.getClient(), rcm.getUid(), Client.Type.sharing == rcm.getType());

		service.invoke("setUid", new Object[] { rcm.getUid() }, this);

		// Log the User
		conferenceLogDao.add(ConferenceLog.Type.clientConnect,
				rcm.getUserId(), streamId, null, rcm.getRemoteAddress(),
				rcm.getScope());
		return true;
	}

	@Override
	public IScope getChildScope(String name) {
		IScope sc = null;
		try {
			sc = super.getChildScope(name);
		} catch (Exception e) {
			//no-op, scope doesn't exist while testing
		}
		return sc;
	}

	public IScope getChildScope(Long roomId) {
		return getChildScope(String.valueOf(roomId));
	}

	public Map<String, String> screenSharerAction(Map<String, Object> map) {
		Map<String, String> returnMap = new HashMap<>();
		try {
			_log.debug("-----------  screenSharerAction ENTER");
			IConnection current = Red5.getConnectionLocal();

			StreamClient client = streamClientManager.get(IClientUtil.getId(current.getClient()));

			if (client != null) {
				boolean changed = false;
				if (Boolean.parseBoolean("" + map.get("stopStreaming")) && client.isSharingStarted()) {
					changed = true;
					client.setSharingStarted(false);
					//Send message to all users
					sendMessageToCurrentScope("stopScreenSharingMessage", client, false);

					returnMap.put("result", "stopSharingOnly");
					sendStreamClosed(client);
				}
				if (Boolean.parseBoolean("" + map.get("stopRecording")) && client.isRecordingStarted()) {
					changed = true;
					client.setRecordingStarted(false);

					returnMap.put("result", "stopRecordingOnly");

					recordingService.stopRecording(current.getScope(), client);
				}
				if (Boolean.parseBoolean("" + map.get("stopPublishing")) && client.isPublishStarted()) {
					changed = true;
					client.setPublishStarted(false);
					returnMap.put("result", "stopPublishingOnly");

					//Send message to all users
					sendMessageToCurrentScope("stopPublishingMessage", client, false);
				}

				if (changed) {
					streamClientManager.update(client);

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
	 * @param map - {@link Map} with all statuses
	 * @return returns key,value Map with multiple return values or null in case of exception
	 *
	 */
	public Map<String, Object> setConnectionAsSharingClient(Map<String, Object> map) {
		try {
			_log.debug("-----------  setConnectionAsSharingClient");
			IConnection current = Red5.getConnectionLocal();

			StreamClient client = streamClientManager.get(IClientUtil.getId(current.getClient()));

			if (client != null) {
				boolean startRecording = Boolean.parseBoolean("" + map.get("startRecording")) && (0 == streamClientManager.getRecordingCount(client.getRoomId()));
				boolean startStreaming = Boolean.parseBoolean("" + map.get("startStreaming")) && (0 == streamClientManager.getSharingCount(client.getRoomId()));
				boolean startPublishing = Boolean.parseBoolean("" + map.get("startPublishing"));

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
				streamClientManager.update(client);

				Map<String, Object> returnMap = new HashMap<>();
				returnMap.put("alreadyPublished", alreadyPublishing);

				_log.debug("screen width,height {},{}", client.getWidth(), client.getHeight());

				if (startStreaming) {
					if (!alreadyStreaming) {
						returnMap.put("modus", "startStreaming");

						_log.debug("start streamPublishStart Is Screen Sharing ");

						//Send message to all users
						sendMessageToCurrentScope("newScreenSharing", client, false);
						WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoomId(), client, RoomMessage.Type.sharingStarted, client.getUid()));
					} else {
						_log.warn("Streaming is already started for the client id={}. Second request is ignored.", client.getId());
					}
				}
				if (startRecording) {
					if (!alreadyRecording) {
						returnMap.put("modus", "startRecording");
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

			StreamClient rcl = streamClientManager.get(IClientUtil.getId(client));

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

	public void dropSharing(org.apache.openmeetings.db.entity.basic.IClient c, Long roomId) {
		IScope scope = getChildScope(roomId);
		//Elvis has left the building
		new MessageSender(scope, "stopStream", new Object(), this) {
			@Override
			public boolean filter(IConnection conn) {
				StreamClient rcl = streamClientManager.get(IClientUtil.getId(conn.getClient()));
				return rcl == null
						|| Client.Type.sharing != rcl.getType()
						|| !c.getSid().equals(rcl.getSid());
			}
		}.start();
	}

	public void roomLeaveByScope(org.apache.openmeetings.db.entity.basic.IClient c, Long roomId) {
		StreamClient rcl = streamClientManager.get(c.getUid());
		IScope scope = getChildScope(roomId);
		_log.debug("[roomLeaveByScope] {} {} {} {}", c.getUid(), roomId, rcl, scope);
		if (rcl != null && scope != null) {
			roomLeaveByScope(rcl, scope);
		}
		dropSharing(c, roomId);
	}

	/**
	 * Removes the Client from the List, stops recording, adds the Room-Leave
	 * event to running recordings, clear Polls and removes Client from any list
	 *
	 * This function is kind of private/protected as the client won't be able
	 * to call it with proper values.
	 *
	 * @param client - client who leave
	 * @param scope - scope being leaved
	 */
	public void roomLeaveByScope(StreamClient client, IScope scope) {
		try {
			_log.debug("[roomLeaveByScope] currentClient {}", client);
			if (Client.Type.sharing == client.getType() && client.isSharingStarted()) {
				sendSharingStoped(client);
			}
			if (client.isBroadcasting()) {
				sendStreamClosed(client);
			}

			_log.debug("removing Username {} {},  streamid: {}", client.getLogin()
					, client.getConnectedSince(), client.getId());

			// stop and save any recordings
			if (Client.Type.sharing == client.getType() && client.isRecordingStarted()) {
				_log.debug("*** roomLeave Current Client is Recording - stop that");
				recordingService.stopRecording(scope, client);
			}
			recordingService.stopStreamRecord(scope, client);

			// Notify all clients of the same currentScope (room) with domain
			// and room except the current disconnected cause it could throw an exception
			_log.debug("currentScope " + scope);

			if (Client.Type.mobile == client.getType() || Client.Type.sip == client.getType()) {
				clientManager.exit(client);
			}
			streamClientManager.remove(client.getUid());
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
			final StreamClient c = streamClientManager.get(IClientUtil.getId(current.getClient()));

			// Notify all the clients that the stream had been started
			String streamName = stream.getPublishedName();
			_log.debug("start streamPublishStart broadcast start: {}, CONN {}", streamName, current);
			c.setBroadcastId(streamName);

			if (Client.Type.mobile == c.getType()) {
				Client cl = clientManager.getBySid(c.getSid());
				cl.setActivities(c.getAvsettings()).addStream(c.getUid());
				clientManager.update(cl);
			} else if (Client.Type.sharing != c.getType()) {
				if (Strings.isEmpty(c.getAvsettings()) || "n".equals(c.getAvsettings())) {
					c.setAvsettings("av");
				}
				c.setBroadcasting(true);
				if (c.getWidth() == 0 || c.getHeight() == 0) {
					c.setWidth(320);
					c.setHeight(240);
				}
			}
			if (Client.Type.sip == c.getType()) {
				Client cl = clientManager.getBySid(c.getSid());
				String newNumber = getSipTransportLastname(c.getRoomId());
				cl.getUser().setLastname(newNumber);
				c.setLastname(newNumber);
				c.setLastname(getSipTransportLastname(c.getRoomId()));
			}
			streamClientManager.update(c);
			if (Client.Type.sharing == c.getType() && c.isRecordingStarted()) {
				recordingService.startRecording(current.getScope(), c);
			}

			_log.debug("newStream SEND: {}", c);

			// Notify all users of the same Scope
			// We need to iterate through the streams to catch if anybody is recording
			new MessageSender(current, "newStream", c, this) {
				@Override
				public boolean filter(IConnection conn) {
					StreamClient rcl = streamClientManager.get(IClientUtil.getId(conn.getClient()));

					if (rcl == null || Strings.isEmpty(rcl.getUid())) {
						_log.debug("Invalid client");
						return true;
					}

					_log.debug("check send to {}", rcl);
					if (IClientUtil.getRecordingId(current.getScope()) != null) {
						_log.debug("RCL getIsRecording newStream SEND");
						recordingService.startStreamRecord(current);
					}
					if (Client.Type.sharing == rcl.getType() || rcl.getUid().equals(c.getUid())) {
						_log.debug("Going to skip this client");
						return true;
					}
					_log.debug("RCL SEND is equal newStream SEND {} || {}", rcl.getUid(), rcl.getUserport());
					return false;
				}
			}.start();
			JSONObject obj = new JSONObject()
					.put("sid", c.getSid())
					.put("uid", c.getUid())
					.put("type", c.getType())
					.put("streamId", current.getClient().getId())
					.put("streamName", streamName);
			WebSocketHelper.sendRoom(new TextRoomMessage(c.getRoomId(), c, RoomMessage.Type.newStream, obj.toString(new NullStringer())));
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
			StreamClient rcl = streamClientManager.get(IClientUtil.getId(current.getClient()));

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
			_log.debug("***  +++++++ ######## sendClientBroadcastNotifications Any Client is Recording - stop that");
			recordingService.stopStreamRecord(current.getScope(), rcl);

			sendStreamClosed(rcl);
			if (stream.getPublishedName().equals(rcl.getBroadcastId())) {
				rcl.setBroadcastId(null);
				rcl.setBroadcasting(false);
				rcl.setAvsettings("n");
			}
			streamClientManager.update(rcl);
			Room r = roomDao.get(rcl.getRoomId());
			if ((Client.Type.sharing == rcl.getType() && rcl.isRecordingStarted())
					|| (r != null && Room.Type.interview == r.getType() && streamClientManager.getBroadcastingCount(rcl.getRoomId()) == 0))
			{
				_log.debug("*** Screen sharing client stoped recording, or last broadcasting user stoped in interview room");
				recordingService.stopRecording(scope, rcl);
			}
			// Notify all clients of the same scope (room)
			sendMessageToCurrentScope("closeStream", rcl, Client.Type.mobile == rcl.getType());
			if (Client.Type.sharing == rcl.getType()) {
				sendSharingStoped(rcl);
			}
			if (Client.Type.mobile == rcl.getType()) {
				Client cl = clientManager.getBySid(rcl.getSid());
				cl.removeStream(rcl.getUid());
				clientManager.update(cl);
			}
		} catch (Exception e) {
			_log.error("[streamBroadcastClose]", e);
		}
	}

	public void setNewCursorPosition(Double x, Double y) {
		try {
			IConnection current = Red5.getConnectionLocal();
			StreamClient c = streamClientManager.get(IClientUtil.getId(current.getClient()));

			sendMessageToCurrentScope("newScreenCursor", new Object[] {c.getUid(), x, y}, true, false);
		} catch (Exception err) {
			_log.error("[setNewCursorPosition]", err);
		}
	}

	private static void sendSharingStoped(StreamClient rcl) {
		JSONObject obj = new JSONObject()
				.put("sid", rcl.getSid())
				.put("uid", rcl.getUid());
		WebSocketHelper.sendRoom(new TextRoomMessage(rcl.getRoomId(), rcl, RoomMessage.Type.sharingStoped, obj.toString()));
	}

	private static void sendStreamClosed(StreamClient rcl) {
		JSONObject obj = new JSONObject()
				.put("uid", rcl.getUid())
				.put("sid", rcl.getSid());
		WebSocketHelper.sendRoom(new TextRoomMessage(rcl.getRoomId(), rcl, RoomMessage.Type.closeStream, obj.toString()));
	}

	public long switchMicMuted(String publicSID, boolean mute) {
		try {
			_log.debug("-----------  switchMicMuted: " + publicSID);

			StreamClient currentClient = streamClientManager.get(publicSID);
			if (currentClient == null) {
				return -1L;
			}

			currentClient.setMicMuted(mute);
			streamClientManager.update(currentClient);

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
	 *
	 * @param newMessage - message being sent
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
		new MessageSender(getChildScope(roomId), method, obj, this) {
			@Override
			public boolean filter(IConnection conn) {
				StreamClient rcl = streamClientManager.get(IClientUtil.getId(conn.getClient()));
				return rcl == null || Client.Type.sharing == rcl.getType()
						|| rcl.getRoomId() == null || !rcl.getRoomId().equals(roomId) || userDao.get(rcl.getUserId()) == null;
			}
		}.start();
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
		new MessageSender(getChildScope(scopeName), remoteMethodName, newMessage, this) {
			@Override
			public boolean filter(IConnection conn) {
				IClient client = conn.getClient();
				return (!sendScreen && IClientUtil.isSharing(client))
						|| (!sendSelf && current != null && client.getId().equals(current.getClient().getId()));
			}
		}.start();
	}

	public abstract static class MessageSender extends Thread {
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
	 *
	 * @param newMessage - message being sent
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
	 *
	 * @param newMessage - message being sent
	 * @param sync - send self
	 * @return 1 in case of success, -1 otherwise
	 */
	public int sendMessageWithClientWithSyncObject(Object newMessage, boolean sync) {
		try {
			IConnection current = Red5.getConnectionLocal();
			StreamClient currentClient = streamClientManager.get(IClientUtil.getId(current.getClient()));

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
	 * @param newMessage - message being sent
	 * @param uid - uid of the receiver
	 * @param scope - scope of message
	 * @return 1 in case of success, -1 otherwise
	 */
	public int sendMessageById(Object newMessage, final String uid, IScope scope) {
		try {
			_log.debug("### sendMessageById ### {}", uid);

			Map<String, Object> hsm = new HashMap<>();
			hsm.put("message", newMessage);

			_sendMessageToClient(uid, hsm, scope);
		} catch (Exception err) {
			_log.error("[sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}

	/**
	 * Sends a message to a user in the same room by its clientId
	 *
	 * @param uid - uid of the recepient
	 * @param newMessage - message being sent
	 * @return 1 in case of no exceptions, -1 otherwise
	 */
	public int sendMessageToClient(final String uid, Object newMessage) {
		try {
			IConnection current = Red5.getConnectionLocal();

			_sendMessageToClient(uid, newMessage, current.getScope());
		} catch (Exception err) {
			_log.error("[sendMessageToClient] ", err);
			return -1;
		}
		return 1;
	}

	private void _sendMessageToClient(final String uid, Object msg, IScope scope) {
		new MessageSender(scope, "sendVarsToMessageWithClient", msg, this) {

			@Override
			public boolean filter(IConnection conn) {
				IClient client = conn.getClient();
				return uid == null || !IClientUtil.getId(client).equals(uid);
			}
		}.start();
	}

	public boolean sendRemoteCursorEvent(final String uid, Map<String, Object> messageObj) {
		new MessageSender("sendRemoteCursorEvent", messageObj, this) {

			@Override
			public boolean filter(IConnection conn) {
				IClient client = conn.getClient();
				return uid == null || !IClientUtil.isSharing(client) || !IClientUtil.getId(client).equals(uid);
			}
		}.start();
		return true;
	}

	/**
	 * Starts recording in interview room
	 *
	 * @param c - client who initiated recording
	 */
	public void startInterviewRecording(Client c) {
		_log.debug("-----------  startInterviewRecording");

		if (c == null || streamClientManager.getRecordingCount(c.getRoom().getId()) > 0) {
			return;
		}

		recordingService.startRecording(getChildScope(c.getRoomId()), c);
	}

	/**
	 * Stop the recording of the streams and send event to connected users of scope
	 *
	 * @param c - client who stopped recording
	 */
	public void stopInterviewRecording(org.apache.openmeetings.db.entity.basic.IClient c) {
		_log.debug("-----------  stopInterviewRecording");
		recordingService.stopRecording(getChildScope(c.getRoomId()), c);
	}

	public void micActivity(boolean active) {
		IConnection current = Red5.getConnectionLocal();
		StreamClient client = streamClientManager.get(IClientUtil.getId(current.getClient()));
		if (client != null && client.getRoomId() != null) {
			final String uid = clientManager.uidBySid(client.getSid());
			if (uid != null) {
				WebSocketHelper.sendRoom(client.getRoomId(), new JSONObject()
						.put("type", "mic")
						.put("id", "activity")
						.put("uid", uid)
						.put("active", active));
			}
		}
	}

	/*
	 * SIP transport methods
	 */
	public List<String> listRoomBroadcast() {
		List<String> ids = new ArrayList<>();
		IConnection current = Red5.getConnectionLocal();
		StreamClient client = streamClientManager.get(IClientUtil.getId(current.getClient()));
		for (Client c: clientManager.listByRoom(client.getRoomId()) ) {
			for (String uid : c.getStreams()) {
				StreamClient rc = streamClientManager.get(uid);
				ids.add(rc.getBroadcastId());
			}
		}
		return ids;
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
		return new ArrayList<>(clientManager.getActiveRoomIds());
	}

	public synchronized int updateSipTransport() {
		_log.debug("-----------  updateSipTransport");
		IConnection current = Red5.getConnectionLocal();
		StreamClient client = streamClientManager.get(IClientUtil.getId(current.getClient()));
		Long roomId = client.getRoomId();
		Integer count = getSipConferenceMembersNumber(roomId);
		String newNumber = getSipTransportLastname(count);
		_log.debug("getSipConferenceMembersNumber: " + newNumber);
		if (!newNumber.equals(client.getLastname())) {
			Client cl = clientManager.getBySid(client.getSid());
			cl.getUser().setLastname(newNumber);
			client.setLastname(newNumber);
			streamClientManager.update(client);
			_log.debug("updateSipTransport: {}, {}, {}, {}, {}", new Object[] { client.getUid(), client.getRoomId(),
					client.getFirstname(), client.getLastname(), client.getAvsettings() });
			WebSocketHelper.sendRoom(new TextRoomMessage(client.getRoomId(), client, RoomMessage.Type.rightUpdated, client.getUid()));
			sendMessageWithClient(new String[] { "personal", client.getFirstname(), client.getLastname() });
		}
		return count != null && count > 0 ? count - 1 : 0;
	}

	public CheckDto check() {
		IConnection current = Red5.getConnectionLocal();
		StreamClient c = streamClientManager.get(IClientUtil.getId(current.getClient()));
		Client cl = clientManager.getBySid(c.getSid());
		return new CheckDto(cl);
	}

	public void resize(Double width, Double height) {
		if (width == null || height == null) {
			return;
		}
		IConnection current = Red5.getConnectionLocal();
		StreamClient c = streamClientManager.get(IClientUtil.getId(current.getClient()));
		if (c == null) {
			return;
		}
		streamClientManager.update(c.setWidth(width.intValue()).setHeight(height.intValue()));
	}
}
