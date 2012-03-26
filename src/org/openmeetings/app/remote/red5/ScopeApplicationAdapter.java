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
package org.openmeetings.app.remote.red5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.conference.session.RoomClient;
import org.openmeetings.app.conference.whiteboard.BrowserStatus;
import org.openmeetings.app.conference.whiteboard.RoomStatus;
import org.openmeetings.app.conference.whiteboard.WhiteboardManagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.logs.ConferenceLogDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.MeetingMember;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.FLVRecorderService;
import org.openmeetings.app.remote.WhiteBoardService;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IBroadcastStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ScopeApplicationAdapter extends ApplicationAdapter implements
		IPendingServiceCallback {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ScopeApplicationAdapter.class,
			OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ClientListManager clientListManager;
	@Autowired
	private EmoticonsManager emoticonsManager;
	@Autowired
	private WhiteBoardService whiteBoardService;
	@Autowired
	private FLVRecorderService flvRecorderService;
	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private ConferenceLogDaoImpl conferenceLogDao;
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private MeetingMemberDaoImpl meetingMemberDao;

	// This is the Folder where all executables are written
	// for windows platform
	public static String batchFileFir = "webapps" + File.separatorChar + "ROOT"
			+ File.separatorChar + "jod" + File.separatorChar;
	public static String lineSeperator = System.getProperty("line.separator");

	// The Global WebApp Path
	public static String webAppPath = "";
	public static String configDirName = "conf";
	public static String profilesPrefix = "profile_";

	public static String configKeyCryptClassName = null;
	public static Boolean whiteboardDrawStatus = null;
	
	private static long broadCastCounter = 0;
	public static boolean initComplete = false;

	public synchronized void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public synchronized boolean appStart(IScope scope) {
		try {
			// This System out is for testing SLF4J / LOG4J and custom logging n
			// Red5
			// System.out.println("Custom Webapp start UP "+new Date());

			webAppPath = scope.getResource("/").getFile().getAbsolutePath();
			batchFileFir = webAppPath + File.separatorChar + "streams"
					+ File.separatorChar;

			log.debug("webAppPath : " + webAppPath);
			log.debug("batchFileFir : " + batchFileFir);

			// Only load this Class one time
			// Initially this value might by empty, because the DB is empty yet
			Configuration conf = cfgManagement.getConfKey(3, "crypt_ClassName");
			if (conf != null) {
				ScopeApplicationAdapter.configKeyCryptClassName = conf
						.getConf_value();
			}

			// init your handler here

			// The scheduled Jobs did go into the Spring-Managed Beans, see
			// schedulerJobs.service.xml

			// Spring Definition does not work here, its too early, Instance is
			// not set yet
			emoticonsManager.loadEmot(scope);

			for (Iterator<String> subIterate = scope.getScopeNames(); subIterate
					.hasNext();) {

				String scopeName = subIterate.next();

				log.debug("scopeName :: " + scopeName);

			}

			ScopeApplicationAdapter.initComplete = true;

			clientListManager.removeAllClients();

			// OpenXGHttpClient.getInstance().openSIPgUserCreateTest();
			// OpenXGWrapperClient.getInstance().testConnection();
			// OpenXGClient.getInstance().testConnection();
			// ServerSocketMinaProcess serverSocketMinaProcess = new
			// ServerSocketMinaProcess();

			// serverSocketMinaProcess.doInitSocket();

		} catch (Exception err) {
			log.error("[appStart]", err);
		}
		return true;
	}

	@Override
	public boolean roomJoin(IClient client, IScope room) {
		log.debug("roomJoin : ");

		try {

			IConnection conn = Red5.getConnectionLocal();
			IServiceCapableConnection service = (IServiceCapableConnection) conn;
			String streamId = client.getId();

			log.debug("### Client connected to OpenMeetings, register Client StreamId: "
					+ streamId + " scope " + room.getName());

			// Set StreamId in Client
			service.invoke("setId", new Object[] { streamId }, this);

			String swfURL = "";
			if (conn.getConnectParams().get("swfUrl") != null) {
				swfURL = conn.getConnectParams().get("swfUrl").toString();
			}

			RoomClient rcm = this.clientListManager.addClientListItem(streamId,
					room.getName(), conn.getRemotePort(),
					conn.getRemoteAddress(), swfURL);

			// Log the User
			conferenceLogDao.addConferenceLog("ClientConnect",
					rcm.getUser_id(), streamId, null, rcm.getUserip(),
					rcm.getScope(), rcm.getExternalUserId(),
					rcm.getExternalUserType(), rcm.getMail(),
					rcm.getFirstname(), rcm.getLastname());

		} catch (Exception err) {
			log.error("roomJoin", err);
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized Map screenSharerAction(Map map) {
		try {

			IConnection current = Red5.getConnectionLocal();

			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());

			Map returnMap = new HashMap();
			returnMap.put("result", "stopAll");

			log.debug("-----------  ");

			if (currentClient != null) {

				boolean stopStreaming = Boolean.valueOf(map
						.get("stopStreaming").toString());
				boolean stopRecording = Boolean.valueOf(map
						.get("stopRecording").toString());

				if (stopStreaming) {

					log.debug("start streamPublishStart Is Screen Sharing -- Stop ");
					
					//Send message to all users
					syncMessageToCurrentScope("stopRed5ScreenSharing", currentClient, false);

					if (currentClient.isStartRecording()) {

						returnMap.put("result", "stopSharingOnly");

					}

					currentClient.setStartStreaming(false);
					currentClient.setScreenPublishStarted(false);

					this.clientListManager.updateClientByStreamId(
							currentClient.getStreamid(), currentClient);

				}

				if (stopRecording) {

					if (currentClient.isStartStreaming()) {
						returnMap.put("result", "stopRecordingOnly");
					}
					
					//Send message to all users
					syncMessageToCurrentScope("stopRecordingMessage", currentClient, false);

					this.flvRecorderService.stopRecordAndSave(
							current.getScope(), currentClient, null);

					currentClient.setStartRecording(false);
					currentClient.setIsRecording(false);

					this.clientListManager.updateClientByStreamId(
							currentClient.getStreamid(), currentClient);

				}

			}

			return returnMap;

		} catch (Exception err) {
			log.error("[screenSharerAction]", err);
		}
		return null;
	}

	public List<RoomClient> checkRed5ScreenSharing() {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug("checkScreenSharing -2- " + streamid);

			List<RoomClient> screenSharerList = new LinkedList<RoomClient>();

			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);

			HashMap<String, RoomClient> roomList = this.clientListManager
					.getClientListByRoomAll(currentClient.getRoom_id());

			for (Iterator<String> iter = roomList.keySet().iterator(); iter
					.hasNext();) {

				RoomClient rcl = roomList.get(iter.next());

				if (rcl.isStartStreaming()) {
					screenSharerList.add(rcl);
				}

			}

			return screenSharerList;

		} catch (Exception err) {
			log.error("[checkScreenSharing]", err);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized Map setConnectionAsSharingClient(Map map) {
		try {

			IConnection current = Red5.getConnectionLocal();
			// IServiceCapableConnection service = (IServiceCapableConnection)
			// current;

			log.debug("### setConnectionAsSharingClient: ");

			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());

			if (currentClient != null) {

				boolean startRecording = Boolean.valueOf(map.get(
						"startRecording").toString());
				boolean startStreaming = Boolean.valueOf(map.get(
						"startStreaming").toString());

				currentClient.setRoom_id(Long.parseLong(current.getScope()
						.getName()));

				// Set this connection to be a RTMP-Java Client
				currentClient.setIsScreenClient(true);
				currentClient.setUser_id(Long.parseLong(map.get("user_id")
						.toString()));

				if (startStreaming) {
					currentClient.setStartStreaming(true);
				}

				if (startRecording) {
					currentClient.setStartRecording(true);
				}

				currentClient.setOrganization_id(Long.parseLong(map.get(
						"organization_id").toString()));

				this.clientListManager.updateClientByStreamId(current
						.getClient().getId(), currentClient);

				Map returnMap = new HashMap();
				returnMap.put("alreadyPublished", false);

				// if is already started screen sharing, then there is no need
				// to start it again
				if (currentClient.isScreenPublishStarted()) {
					returnMap.put("alreadyPublished", true);
				}

				currentClient.setVX(Integer.parseInt(map.get("screenX")
						.toString()));
				currentClient.setVY(Integer.parseInt(map.get("screenY")
						.toString()));
				currentClient.setVWidth(Integer.parseInt(map.get("screenWidth")
						.toString()));
				currentClient.setVHeight(Integer.parseInt(map.get(
						"screenHeight").toString()));

				log.debug("screen x,y,width,height " + currentClient.getVX()
						+ " " + currentClient.getVY() + " "
						+ currentClient.getVWidth() + " "
						+ currentClient.getVHeight());

				log.debug("publishName :: " + map.get("publishName"));

				currentClient.setStreamPublishName(map.get("publishName")
						.toString());

				RoomClient currentScreenUser = this.clientListManager
						.getClientByPublicSID(currentClient
								.getStreamPublishName(), false);

				currentClient.setFirstname(currentScreenUser.getFirstname());
				currentClient.setLastname(currentScreenUser.getLastname());

				// This is duplicated, but its not sure that in the meantime
				// somebody requests this Client Object Info
				this.clientListManager.updateClientByStreamId(current
						.getClient().getId(), currentClient);

				if (startStreaming) {

					returnMap.put("modus", "startStreaming");

					log.debug("start streamPublishStart Is Screen Sharing ");
					
					//Send message to all users
					syncMessageToCurrentScope("newRed5ScreenSharing", currentClient, false);

				}

				if (startRecording) {

					returnMap.put("modus", "startRecording");

					String recordingName = "Recording "
							+ CalendarPatterns
									.getDateWithTimeByMiliSeconds(new Date());

					this.flvRecorderService.recordMeetingStream(recordingName,
							"", false);

				}

				return returnMap;

			} else {
				throw new Exception("Could not find Screen Sharing Client "
						+ current.getClient().getId());
			}

		} catch (Exception err) {
			log.error("[setConnectionAsSharingClient]", err);
		}
		return null;
	}

    public synchronized List<Integer> listRoomBroadcast() {
        List<Integer> broadcastList = new ArrayList<Integer>();
        IConnection current = Red5.getConnectionLocal();
        String streamid = current.getClient().getId();
        Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
        for (Set<IConnection> conset : conCollection) {
            for (IConnection conn : conset) {
                if (conn != null) {
                    RoomClient rcl = this.clientListManager
                            .getClientByStreamId(conn
                                    .getClient().getId());
                    if (rcl == null) {
                        // continue;
                    } else if (rcl.getIsScreenClient() != null
                            && rcl.getIsScreenClient()) {
                        // continue;
                    } else {
                        if (!streamid.equals(rcl.getStreamid())) {
                            // It is not needed to send back
                            // that event to the actuall
                            // Moderator
                            // as it will be already triggered
                            // in the result of this Function
                            // in the Client
                            broadcastList.add(Long.valueOf(rcl.getBroadCastID()).intValue());
                        }
                    }
                }
            }
        }
        return broadcastList;
    }


	/**
	 * this function is invoked directly after initial connecting
	 * 
	 * @return
	 */
	public synchronized String getPublicSID() {
		IConnection current = Red5.getConnectionLocal();
		RoomClient currentClient = this.clientListManager
				.getClientByStreamId(current.getClient().getId());
		currentClient.setIsAVClient(false);
		clientListManager.updateClientByStreamId(current.getClient().getId(),
				currentClient);
		return currentClient.getPublicSID();
	}

	/**
	 * this function is invoked after a reconnect
	 * 
	 * @param newPublicSID
	 */
	public synchronized Boolean overwritePublicSID(String newPublicSID) {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());
			if (currentClient == null) {
				return false;
			}
			currentClient.setPublicSID(newPublicSID);
			this.clientListManager.updateClientByStreamId(current.getClient()
					.getId(), currentClient);
			return true;
		} catch (Exception err) {
			log.error("[overwritePublicSID]", err);
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

			log.debug("roomLeave " + client.getId() + " "
					+ room.getClients().size() + " " + room.getContextPath()
					+ " " + room.getName());

			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(client.getId());

			// The Room Client can be null if the Client left the room by using
			// logicalRoomLeave
			if (currentClient != null) {
				log.debug("currentClient IS NOT NULL");
				this.roomLeaveByScope(currentClient, room);
			}

		} catch (Exception err) {
			log.error("[roomLeave]", err);
		}
	}

	/**
	 * this means a user has left a room but only logically, he didn't leave the
	 * app he just left the room
	 * 
	 * FIXME: Is this really needed anymore if you re-connect to another scope?
	 * 
	 * Exit Room by Application
	 * 
	 */
	public synchronized void logicalRoomLeave() {
		log.debug("logicalRoomLeave ");
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug(streamid + " is leaving");

			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);

			this.roomLeaveByScope(currentClient, current.getScope());

		} catch (Exception err) {
			log.error("[logicalRoomLeave]", err);
		}
	}

	/**
	 * Removes the Client from the List, stops recording, adds the Room-Leave
	 * event to running recordings, clear Polls and removes Client from any list
	 * 
	 * @param currentClient
	 * @param currentScope
	 */
	public synchronized void roomLeaveByScope(RoomClient currentClient,
			IScope currentScope) {
		try {

			log.debug("currentClient " + currentClient);
			log.debug("currentScope " + currentScope);
			// log.debug("currentClient "+currentClient.getRoom_id());

			Long room_id = currentClient.getRoom_id();

			// Log the User
			conferenceLogDao.addConferenceLog("roomLeave",
					currentClient.getUser_id(), currentClient.getStreamid(),
					room_id, currentClient.getUserip(), "",
					currentClient.getExternalUserId(),
					currentClient.getExternalUserType(),
					currentClient.getMail(), currentClient.getFirstname(),
					currentClient.getLastname());

			// Remove User from Sync List's
			if (room_id != null) {
				this.whiteBoardService.removeUserFromAllLists(currentScope,
						currentClient);
			}

			log.debug("##### roomLeave :. " + currentClient.getStreamid()); // just
																			// a
																			// unique
																			// number
			log.debug("removing USername " + currentClient.getUsername() + " "
					+ currentClient.getConnectedSince() + " streamid: "
					+ currentClient.getStreamid());

			// stop and save any recordings
			if (currentClient.getIsRecording()) {
				log.debug("*** roomLeave Current Client is Recording - stop that");
				// StreamService.stopRecordAndSave(currentScope,
				// currentClient.getRoomRecordingName(), currentClient);

				this.flvRecorderService.stopRecordAndSave(currentScope,
						currentClient, null);

				// set to true and overwrite the default one cause otherwise no
				// notification is send
				currentClient.setIsRecording(true);
			}

			// Notify all clients of the same currentScope (room) with domain
			// and room
			// except the current disconnected cause it could throw an exception

			log.debug("currentScope " + currentScope);

			// Remove User AFTER cause otherwise the currentClient Object is
			// NULL ?!
			// OR before ?!?!

			if (currentScope != null && currentScope.getConnections() != null) {
				// Notify Users of the current Scope
				Collection<Set<IConnection>> conCollection = currentScope
						.getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection cons : conset) {
						if (cons != null) {
							if (cons instanceof IServiceCapableConnection) {

								log.debug("sending roomDisconnect to " + cons
										+ " client id "
										+ cons.getClient().getId());

								RoomClient rcl = this.clientListManager
										.getClientByStreamId(cons.getClient()
												.getId());

								/*
								 * Check if the Client does still exist on the
								 * list
								 */
								if (rcl != null) {

									/*
									 * Do not send back to sender, but actually
									 * all other clients should receive this
									 * message swagner 01.10.2009
									 */
									if (!currentClient.getStreamid().equals(
											rcl.getStreamid())) {

										if (rcl.getIsScreenClient() != null
												&& rcl.getIsScreenClient()) {
											// continue;
										} else {
											// Send to all connected users
											((IServiceCapableConnection) cons)
													.invoke("roomDisconnect",
															new Object[] { currentClient },
															this);
											log.debug("sending roomDisconnect to "
													+ cons);
										}

										// add Notification if another user is
										// recording
										log.debug("###########[roomLeave]");
										if (rcl.getIsRecording()) {
											log.debug("*** roomLeave Any Client is Recording - stop that");
											this.flvRecorderService
													.stopRecordingShowForClient(
															cons, currentClient);
										}

									}
								} else {
									log.debug("For this StreamId: "
											+ cons.getClient().getId()
											+ " There is no Client in the List anymore");
								}
							}
						}
					}
				}
			}

			this.clientListManager.removeClient(currentClient.getStreamid());
		} catch (Exception err) {
			log.error("[roomLeaveByScope]", err);
		}
	}

	/**
	 * This method handles the Event after a stream has been added all connected
	 * Clients in the same room will get a notification
	 * 
	 * @return void
	 * 
	 */
	@Override
	public synchronized void streamPublishStart(IBroadcastStream stream) {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);

			// Notify all the clients that the stream had been started
			log.debug("start streamPublishStart broadcast start: "
					+ stream.getPublishedName() + "CONN " + current);

			// In case its a screen sharing we start a new Video for that
			if (currentClient.getIsScreenClient()) {

				currentClient.setScreenPublishStarted(true);

				this.clientListManager.updateClientByStreamId(current
						.getClient().getId(), currentClient);
			}
			//If its an audio/video client then send the session object with the full 
			//data to everybody
			else if (currentClient.getIsAVClient() != null && currentClient.getIsAVClient()) {
				currentClient = this.clientListManager.getClientByPublicSID(
											currentClient.getPublicSID(), false);
			}
			
			log.debug("newStream SEND");

			// Notify all users of the same Scope
			// We need to iterate through the streams to catch if anybody is recording
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							
							RoomClient rcl = this.clientListManager
									.getClientByStreamId(conn.getClient()
											.getId());
							
							if (rcl == null) {
								log.debug("RCL IS NULL newStream SEND");
								continue;
							}
							
							log.debug("check send to "+rcl);
							
							if (rcl.getPublicSID() == "") {
								log.debug("publicSID IS NULL newStream SEND");
								continue;
							}
							if (rcl.getIsRecording()) {
								log.debug("RCL getIsRecording newStream SEND");
								this.flvRecorderService
										.addRecordingByStreamId(current,
												streamid, currentClient,
												rcl.getFlvRecordingId());
							}
							if (rcl.getIsAVClient() == null || rcl.getIsAVClient()) {
								log.debug("RCL getIsAVClient newStream SEND");
								continue;
							}
							if (rcl.getIsScreenClient() == null || rcl.getIsScreenClient()) {
								log.debug("RCL getIsScreenClient newStream SEND");
								continue;
							}
							
							if (rcl.getPublicSID().equals(currentClient.getPublicSID())) {
								log.debug("RCL publicSID is equal newStream SEND");
								continue;
							}
							
							log.debug("RCL SEND is equal newStream SEND "+rcl.getPublicSID()+" || "+rcl.getUserport());
								
							IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
							iStream.invoke("newStream",
									new Object[] { currentClient },
									this);

						}
					}
				}
			}

		} catch (Exception err) {
			log.error("[streamPublishStart]", err);
		}
	}

	/**
	 * This method handles the Event after a stream has been removed all
	 * connected Clients in the same room will get a notification
	 * 
	 * @return void
	 * 
	 */
	@Override
	public synchronized void streamBroadcastClose(IBroadcastStream stream) {

		// Notify all the clients that the stream had been started
		log.debug("start streamBroadcastClose broadcast close: "
				+ stream.getPublishedName());
		try {
			RoomClient rcl = this.clientListManager.getClientByStreamId(Red5
					.getConnectionLocal().getClient().getId());

			sendClientBroadcastNotifications(stream, "closeStream", rcl);
		} catch (Exception e) {
			log.error("[streamBroadcastClose]", e);
		}
	}

	/**
	 * This method handles the notification room-based
	 * 
	 * @return void
	 * 
	 */
	private synchronized void sendClientBroadcastNotifications(
			IBroadcastStream stream, String clientFunction, RoomClient rc) {
		try {

			// Store the local so that we do not send notification to ourself
			// back
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);

			if (currentClient == null) {

				// In case the client has already left(kicked) this message
				// might be thrown later then the RoomLeave
				// event and the currentClient is already gone
				// The second Use-Case where the currentClient is maybe null is
				// if we remove the client because its a Zombie/Ghost

				return;

			}
			// Notify all the clients that the stream had been started
			log.debug("sendClientBroadcastNotifications: "
					+ stream.getPublishedName());
			log.debug("sendClientBroadcastNotifications : " + currentClient
					+ " " + currentClient.getStreamid());

			// Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							if (conn.equals(current)) {
								// there is a Bug in the current implementation
								// of the appDisconnect
								if (clientFunction.equals("closeStream")) {
									RoomClient rcl = this.clientListManager
											.getClientByStreamId(conn
													.getClient().getId());
									if (clientFunction.equals("closeStream")
											&& rcl.getIsRecording()) {
										log.debug("*** stopRecordingShowForClient Any Client is Recording - stop that");
										// StreamService.stopRecordingShowForClient(conn,
										// currentClient,
										// rcl.getRoomRecordingName(), false);
										this.flvRecorderService
												.stopRecordingShowForClient(
														conn, currentClient);
									}
									// Don't notify current client
									current.ping();
								}
								continue;
							} else {
								RoomClient rcl = this.clientListManager
										.getClientByStreamId(conn.getClient()
												.getId());
								if (rcl != null) {
									if (rcl.getIsScreenClient() != null
											&& rcl.getIsScreenClient()) {
										// continue;
									} else {
										log.debug("is this users still alive? :"
												+ rcl);
										// conn.ping();
										IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
										// log.info("IServiceCapableConnection ID "
										// + iStream.getClient().getId());
										iStream.invoke(clientFunction,
												new Object[] { rc }, this);
									}

									log.debug("sending notification to " + conn
											+ " ID: ");

									// if this close stream event then stop the
									// recording of this stream
									if (clientFunction.equals("closeStream")
											&& rcl.getIsRecording()) {
										log.debug("***  +++++++ ######## sendClientBroadcastNotifications Any Client is Recording - stop that");
										// StreamService.stopRecordingShowForClient(conn,
										// currentClient,
										// rcl.getRoomRecordingName(), false);
										this.flvRecorderService
												.stopRecordingShowForClient(
														conn, currentClient);
									}
								}

							}
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendClientBroadcastNotifications]", err);
		}
	}


	/**
	 * Adds a Moderator by its publicSID
	 * 
	 * @param publicSID
	 * @return
	 */
	public synchronized Long addModerator(String publicSID) {
		try {

			log.debug("*..*addModerator publicSID: " + publicSID);

			// String streamid = current.getClient().getId();

			RoomClient currentClient = this.clientListManager
					.getClientByPublicSID(publicSID, false);

			if (currentClient == null) {
				return -1L;
			}
			Long room_id = currentClient.getRoom_id();

			currentClient.setIsMod(true);
			// Put the mod-flag to true for this client
			this.clientListManager.updateClientByStreamId(
					currentClient.getStreamid(), currentClient);

			List<RoomClient> currentMods = this.clientListManager
					.getCurrentModeratorByRoom(room_id);
			
			//Send message to all users
			syncMessageToCurrentScope("setNewModeratorByList", currentMods, true);

		} catch (Exception err) {
			log.error("[addModerator]", err);
		}
		return -1L;
	}

	@SuppressWarnings("unchecked")
	public void setNewCursorPosition(Object item) {
		try {

			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);

			// log.debug("[setNewCursorPosition]"+item);

			@SuppressWarnings("rawtypes")
			Map cursor = (Map) item;
			cursor.put("streamPublishName",
					currentClient.getStreamPublishName());

			// log.debug("[setNewCursorPosition x]"+cursor.get("cursor_x"));
			// log.debug("[setNewCursorPosition y]"+cursor.get("cursor_y"));
			// log.debug("[setNewCursorPosition publicSID]"+cursor.get("publicSID"));

			// Notify all users of the same Scope
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							if (conn.equals(current)) {
								continue;
							} else {
								RoomClient rcl = this.clientListManager
										.getClientByStreamId(conn.getClient()
												.getId());
								if (rcl == null) {
									// continue;
								} else if (rcl.getIsScreenClient() != null
										&& rcl.getIsScreenClient()) {
									// continue;
								} else {
									// log.debug("is this users still alive? :"+rcl);
									// Check if the Client is in the same room
									// and same domain
									IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
									// log.info("IServiceCapableConnection ID "
									// + iStream.getClient().getId());
									iStream.invoke("newRed5ScreenCursor",
											new Object[] { cursor }, this);
									// log.debug("send Notification to");
								}
							}
						}
					}
				}
			}

		} catch (Exception err) {
			log.error("[setNewCursorPosition]", err);
		}
	}

	public synchronized Long removeModerator(String publicSID) {
		try {

			log.debug("*..*addModerator publicSID: " + publicSID);

			IConnection current = Red5.getConnectionLocal();
			// String streamid = current.getClient().getId();

			RoomClient currentClient = this.clientListManager
					.getClientByPublicSID(publicSID, false);

			if (currentClient == null) {
				return -1L;
			}
			Long room_id = currentClient.getRoom_id();

			currentClient.setIsMod(false);
			// Put the mod-flag to true for this client
			this.clientListManager.updateClientByStreamId(
					currentClient.getStreamid(), currentClient);

			List<RoomClient> currentMods = this.clientListManager
					.getCurrentModeratorByRoom(room_id);

			// Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());
						if (rcl == null) {
							// continue;
						} else if (rcl.getIsScreenClient() != null
								&& rcl.getIsScreenClient()) {
							// continue;
						} else {
							log.debug("Send Flag to Client: "
									+ rcl.getUsername());
							if (conn instanceof IServiceCapableConnection) {
								((IServiceCapableConnection) conn).invoke(
										"setNewModeratorByList",
										new Object[] { currentMods }, this);
								log.debug("sending setNewModeratorByList to "
										+ conn);
							}
						}
					}
				}
			}

		} catch (Exception err) {
			log.error("[addModerator]", err);
		}
		return -1L;
	}

	public synchronized Long setBroadCastingFlag(String publicSID,
			boolean value, Integer interviewPodId) {
		try {

			log.debug("*..*setBroadCastingFlag publicSID: " + publicSID);

			IConnection current = Red5.getConnectionLocal();
			// String streamid = current.getClient().getId();

            RoomClient currentClient = this.clientListManager
					.getClientByPublicSID(publicSID, false);

			if (currentClient == null) {
				return -1L;
			}

			currentClient.setIsBroadcasting(value);
			currentClient.setInterviewPodId(interviewPodId);

            // Put the mod-flag to true for this client
		    this.clientListManager.updateClientByStreamId(
		    		currentClient.getStreamid(), currentClient);
		    
			// Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());
						if (rcl == null) {
							continue;
						} else if (rcl.getIsScreenClient() != null
								&& rcl.getIsScreenClient()) {
							continue;
						} else if (rcl.getIsAVClient() != null
								&& rcl.getIsAVClient()) {
							continue;
						}
						
						log.debug("Send Flag to Client: "
								+ rcl.getUsername());
						if (conn instanceof IServiceCapableConnection) {
							((IServiceCapableConnection) conn).invoke(
									"setNewBroadCastingFlag",
									new Object[] { currentClient }, this);
							log.debug("sending setNewBroadCastingFlag to "
									+ conn);
						}
					}
				}
			}

		} catch (Exception err) {
			log.error("[setBroadCastingFlag]", err);
		}
		return -1L;
	}

	public synchronized Long giveExclusiveAudio(String publicSID) {
		try {

			log.debug("*..*giveExclusiveAudio publicSID: " + publicSID);

			IConnection current = Red5.getConnectionLocal();
			// String streamid = current.getClient().getId();

			RoomClient currentClient = this.clientListManager
					.getClientByPublicSID(publicSID, false);

			if (currentClient == null) {
				return -1L;
			}

			// Put the mod-flag to true for this client
			currentClient.setMicMuted(false);
			this.clientListManager.updateClientByStreamId(
					currentClient.getStreamid(), currentClient);

			// Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());
						if (rcl == null) {
							// continue;
						} else if (rcl.getIsScreenClient() != null
								&& rcl.getIsScreenClient()) {
							// continue;
						} else {
							if (rcl != currentClient) {
								rcl.setMicMuted(true);
								this.clientListManager.updateClientByStreamId(
										rcl.getStreamid(), rcl);
							}
							log.debug("Send Flag to Client: "
									+ rcl.getUsername());
							if (conn instanceof IServiceCapableConnection) {
								((IServiceCapableConnection) conn).invoke(
										"receiveExclusiveAudioFlag",
										new Object[] { currentClient }, this);
								log.debug("sending receiveExclusiveAudioFlag to "
										+ conn);
							}
						}
					}
				}
			}

		} catch (Exception err) {
			log.error("[giveExclusiveAudio]", err);
		}
		return -1L;
	}

	public synchronized Long switchMicMuted(String publicSID, boolean mute) {
		try {
			log.debug("*..*switchMicMuted publicSID: " + publicSID);

			RoomClient currentClient = this.clientListManager
					.getClientByPublicSID(publicSID, false);
			if (currentClient == null) {
				return -1L;
			}

			currentClient.setMicMuted(mute);
			this.clientListManager.updateClientByStreamId(
					currentClient.getStreamid(), currentClient);

			HashMap<Integer, Object> newMessage = new HashMap<Integer, Object>();
			newMessage.put(0, "updateMuteStatus");
			newMessage.put(1, currentClient);
			this.sendMessageWithClient(newMessage);

		} catch (Exception err) {
			log.error("[switchMicMuted]", err);
		}
		return 0L;
	}

    public synchronized Boolean getMicMutedByPublicSID(String publicSID) {
        try {
			RoomClient currentClient = this.clientListManager.getClientByPublicSID(publicSID, false);
			if (currentClient == null) {
				return true;
			}

			//Put the mod-flag to true for this client
            Boolean muted = currentClient.getMicMuted();
            if (null == muted) {
                muted = true;
            }

            return muted;
        } catch (Exception err) {
			log.error("[getMicMutedByPublicSID]",err);
		}
		return true;
    }

	/**
	 * Invoked by a User whenever he want to become moderator this is needed,
	 * cause if the room has no moderator yet there is no-one he can ask to get
	 * the moderation, in case its a Non-Moderated Room he should then get the
	 * Moderation without any confirmation needed
	 * 
	 * @return Long 1 => means get Moderation, 2 => ask Moderator for
	 *         Moderation, 3 => wait for Moderator
	 */
	public synchronized Long applyForModeration(String publicSID) {
		try {

			RoomClient currentClient = this.clientListManager
					.getClientByPublicSID(publicSID, false);

			List<RoomClient> currentModList = this.clientListManager
					.getCurrentModeratorByRoom(currentClient.getRoom_id());

			if (currentModList.size() > 0) {
				return 2L;
			} else {
				// No moderator in this room at the moment
				Rooms room = roommanagement.getRoomById(currentClient
						.getRoom_id());

				if (room.getIsModeratedRoom()) {
					return 3L;
				} else {
					return 1L;
				}
			}

		} catch (Exception err) {
			log.error("[applyForModeration]", err);
		}
		return -1L;
	}

	/**
	 * there will be set an attribute called "broadCastCounter" this is the name
	 * this user will publish his stream
	 * 
	 * @return long broadCastId
	 */
	public synchronized long getBroadCastId() {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);
			currentClient.setBroadCastID(broadCastCounter++);
			this.clientListManager.updateClientByStreamId(streamid,
					currentClient);
			return currentClient.getBroadCastID();
		} catch (Exception err) {
			log.error("[getBroadCastId]", err);
		}
		return -1;
	}

	/**
	 * this must be set _after_ the Video/Audio-Settings have been chosen (see
	 * editrecordstream.lzx) but _before_ anything else happens, it cannot be
	 * applied _after_ the stream has started! avsettings can be: av - video and
	 * audio a - audio only v - video only n - no a/v only static image
	 * furthermore
	 * 
	 * @param avsetting
	 * @param newMessage
	 * @return
	 */
	public synchronized RoomClient setUserAVSettings(String avsettings,
			Object newMessage, Integer vWidth, Integer vHeight, 
			long room_id, String publicSID, Integer interviewPodId) {
		try {

			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);
			currentClient.setAvsettings(avsettings);
			currentClient.setRoom_id(room_id);
			currentClient.setPublicSID(publicSID);
			currentClient.setIsAVClient(true);
			currentClient.setVWidth(vWidth);
			currentClient.setVHeight(vHeight);
			currentClient.setInterviewPodId(interviewPodId);
			// Long room_id = currentClient.getRoom_id();
			this.clientListManager.updateAVClientByStreamId(streamid,
					currentClient);

			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);

			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager
									.getClientByStreamId(conn.getClient()
											.getId());
							if (rcl == null) {
								// continue;
							} else if (rcl.getIsScreenClient() != null
									&& rcl.getIsScreenClient()) {
								// continue;
							} else {
								((IServiceCapableConnection) conn).invoke(
										"sendVarsToMessageWithClient",
										new Object[] { hsm }, this);
							}
						}
					}
				}
			}

			return currentClient;
		} catch (Exception err) {
			log.error("[setUserAVSettings]", err);
		}
		return null;
	}

	/*
	 * checks if the user is allowed to apply for Moderation
	 */
	public synchronized Boolean checkRoomValues(Long room_id) {
		try {

			// appointed meeting or moderated Room?
			Rooms room = roommanagement.getRoomById(room_id);

			// not really - default logic
			if (room.getAppointment() == null || room.getAppointment() == false) {

				if (room.getIsModeratedRoom()) {

					// if this is a Moderated Room then the Room can be only
					// locked off by the Moderator Bit
					List<RoomClient> clientModeratorListRoom = this.clientListManager
							.getCurrentModeratorByRoom(room_id);

					// If there is no Moderator yet and we are asking for it
					// then deny it
					// cause at this moment, the user should wait untill a
					// Moderator enters the Room
					if (clientModeratorListRoom.size() == 0) {
						return false;
					} else {
						return true;
					}

				} else {
					return true;
				}

			} else {

				// FIXME: TODO: For Rooms that are created as Appointment we
				// have to check that too
				// but I don't know yet the Logic behind it - swagner 19.06.2009
				return true;

			}

		} catch (Exception err) {
			log.error("[checkRoomValues]", err);
		}
		return false;
	}

	/**
	 * This function is called once a User enters a Room
	 * 
	 * It contains several different mechanism depending on what roomtype and
	 * what options are available for the room to find out if the current user
	 * will be a moderator of that room or not<br/>
	 * <br/>
	 * Some rules:<br/>
	 * <ul>
	 * <li>If it is a room that was created through the calendar, the user that
	 * organized the room will be moderator, the param Boolean becomeModerator
	 * will be ignored then</li>
	 * <li>In regular rooms you can use the param Boolean becomeModerator to set
	 * any user to become a moderator of the room</li>
	 * </ul>
	 * <br/>
	 * If a new moderator is detected a Push Call to all current users of the
	 * room is invoked "setNewModeratorByList" to notify them of the new
	 * moderator<br/>
	 * <br/>
	 * And the end of the mechanism a push call with the new client-object
	 * and all the informations about the new user is send to every user of the
	 * current conference room<br/>
	 * <br/>
	 * @param room_id
	 * @param colorObj
	 * @return
	 */
	public synchronized RoomStatus setRoomValues(Long room_id,
			Boolean becomeModerator, Boolean isSuperModerator,
			Long organization_id, String colorObj) {
		try {

			// Return Object
			RoomStatus roomStatus = new RoomStatus();

			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);
			currentClient.setRoom_id(room_id);
			currentClient.setIsAVClient(false);
			currentClient.setRoomEnter(new Date());
			currentClient.setOrganization_id(organization_id);

			currentClient.setUsercolor(colorObj);

			// Inject externalUserId if nothing is set yet
			if (currentClient.getExternalUserId() == null) {
				Users us = usersDao.getUser(currentClient.getUser_id());
				if (us != null) {
					currentClient.setExternalUserId(us.getExternalUserId());
					currentClient.setExternalUserType(us.getExternalUserType());
				}
			}

			// This can be set without checking for Moderation Flag
			currentClient.setIsSuperModerator(isSuperModerator);

			this.clientListManager.updateClientByStreamId(streamid,
					currentClient);

            Rooms room = roommanagement.getRoomById(room_id);
            if (room.getShowMicrophoneStatus()) {
            	currentClient.setCanGiveAudio(true);
            }

			// Log the User
			conferenceLogDao.addConferenceLog("roomEnter",
					currentClient.getUser_id(), streamid, room_id,
					currentClient.getUserip(), "",
					currentClient.getExternalUserId(),
					currentClient.getExternalUserType(),
					currentClient.getMail(), currentClient.getFirstname(),
					currentClient.getLastname());

			log.debug("##### setRoomValues : " + currentClient.getUsername()
					+ " " + currentClient.getStreamid()); // just a unique
															// number

			// Check for Moderation LogicalRoom ENTER
			HashMap<String, RoomClient> clientListRoom = this.clientListManager
					.getRoomClients(room_id);

			// appointed meeting or moderated Room? => Check Max Users first
			if (room.getNumberOfPartizipants() != null
					&& clientListRoom.size() > room.getNumberOfPartizipants()) {
				roomStatus.setRoomFull(true);
				return roomStatus;
			}

			// default logic for non regular rooms
			if (room.getAppointment() == null || room.getAppointment() == false) {

				if (room.getIsModeratedRoom()) {

					// if this is a Moderated Room then the Room can be only
					// locked off by the Moderator Bit
					// List<RoomClient> clientModeratorListRoom =
					// this.clientListManager.getCurrentModeratorByRoom(room_id);

					// If there is no Moderator yet we have to check if the
					// current User has the Bit set to true to
					// become one, otherwise he won't get Moderation and has to
					// wait
					if (becomeModerator) {
						currentClient.setIsMod(true);

						// There is a need to send an extra Event here, cause at
						// this moment there could be
						// already somebody in the Room waiting

						// Update the Client List
						this.clientListManager.updateClientByStreamId(streamid,
								currentClient);

						List<RoomClient> modRoomList = this.clientListManager
								.getCurrentModeratorByRoom(currentClient.getRoom_id());
						
						//Sync message to everybody
						syncMessageToCurrentScope("setNewModeratorByList", modRoomList, false);

					} else {
						// The current User is not a Teacher/Admin or whatever
						// Role that should get the
						// Moderation
						currentClient.setIsMod(false);
					}

				} else {

					// If this is a normal Room Moderator rules : first come,
					// first draw ;-)
					log.debug("setRoomValues : Room"
							+ room_id
							+ " not appointed! Moderator rules : first come, first draw ;-)");
					if (clientListRoom.size() == 1) {
						log.debug("Room is empty so set this user to be moderation role");
						currentClient.setIsMod(true);
					} else {
						log.debug("Room is already somebody so set this user not to be moderation role");

						if (becomeModerator) {
							currentClient.setIsMod(true);

							// Update the Client List
							this.clientListManager.updateClientByStreamId(
									streamid, currentClient);

							List<RoomClient> modRoomList = this.clientListManager
									.getCurrentModeratorByRoom(currentClient
											.getRoom_id());

							// There is a need to send an extra Event here,
							// cause at this moment there could be
							// already somebody in the Room waiting -swagner check this comment, 20.01.2012
							
							//Sync message to everybody
							syncMessageToCurrentScope("setNewModeratorByList", modRoomList, false);

						} else {
							// The current User is not a Teacher/Admin or
							// whatever Role that should get the Moderation
							currentClient.setIsMod(false);
						}

					}

				}

				// Update the Client List
				this.clientListManager.updateClientByStreamId(streamid,
						currentClient);

			} else {

				// If this is an Appointment then the Moderator will be set to
				// the Invitor

				Appointment ment = appointmentLogic
						.getAppointmentByRoom(room_id);

				List<MeetingMember> members = meetingMemberDao
						.getMeetingMemberByAppointmentId(ment
								.getAppointmentId());

				Long userIdInRoomClient = currentClient.getUser_id();

				boolean found = false;
				boolean moderator_set = false;

				// Check if current user is set to moderator
				for (int i = 0; i < members.size(); i++) {
					MeetingMember member = members.get(i);

					// only persistent users can schedule a meeting
					// user-id is only set for registered users
					if (member.getUserid() != null) {
						log.debug("checking user " + member.getFirstname()
								+ " for moderator role - ID : "
								+ member.getUserid().getUser_id());

						if (member.getUserid().getUser_id()
								.equals(userIdInRoomClient)) {
							found = true;

							if (member.getInvitor()) {
								log.debug("User "
										+ userIdInRoomClient
										+ " is moderator due to flag in MeetingMember record");
								currentClient.setIsMod(true);

								// Update the Client List
								this.clientListManager.updateClientByStreamId(
										streamid, currentClient);

								List<RoomClient> modRoomList = this.clientListManager
										.getCurrentModeratorByRoom(currentClient
												.getRoom_id());

								// There is a need to send an extra Event here, cause at this moment 
								// there could be already somebody in the Room waiting

								//Sync message to everybody
								syncMessageToCurrentScope("setNewModeratorByList", modRoomList, false);

								moderator_set = true;
								this.clientListManager.updateClientByStreamId(
										streamid, currentClient);
								break;
							} else {
								log.debug("User "
										+ userIdInRoomClient
										+ " is NOT moderator due to flag in MeetingMember record");
								currentClient.setIsMod(false);
								this.clientListManager.updateClientByStreamId(
										streamid, currentClient);
								break;
							}
						} else {
							if (member.getInvitor())
								moderator_set = true;
						}
					} else {
						if (member.getInvitor())
							moderator_set = true;
					}

				}

				if (!found) {
					log.debug("User "
							+ userIdInRoomClient
							+ " could not be found as MeetingMember -> definitely no moderator");
					currentClient.setIsMod(false);
					this.clientListManager.updateClientByStreamId(streamid,
							currentClient);
				} else {
					// if current user is part of the member list, but moderator
					// couldn't be retrieved : first come, first draw!
					if (clientListRoom.size() == 1 && moderator_set == false) {
						log.debug("");
						currentClient.setIsMod(true);

						// Update the Client List
						this.clientListManager.updateClientByStreamId(streamid,
								currentClient);

						List<RoomClient> modRoomList = this.clientListManager
								.getCurrentModeratorByRoom(currentClient
										.getRoom_id());

						// There is a need to send an extra Event here, cause at
						// this moment there could be
						// already somebody in the Room waiting

						//Sync message to everybody
						syncMessageToCurrentScope("setNewModeratorByList", modRoomList, false);
						
						this.clientListManager.updateClientByStreamId(streamid,
								currentClient);
					}
				}

			}
			
			
			//Sync message to everybody
			syncMessageToCurrentScope("addNewUser", currentClient, false);

			//Status object for Shared Browsing
			BrowserStatus browserStatus = (BrowserStatus) current.getScope()
					.getAttribute("browserStatus");

			if (browserStatus == null) {
				browserStatus = new BrowserStatus();
			}

			// RoomStatus roomStatus = new RoomStatus();

			// FIXME: Rework Client Object to DTOs
			roomStatus.setClientMap(clientListRoom);
			roomStatus.setBrowserStatus(browserStatus);

			return roomStatus;
		} catch (Exception err) {
			log.error("[setRoomValues]", err);
		}
		return null;
	}

	/**
	 * This method is invoked when the user has disconnected and reconnects to
	 * the Gateway with the new scope
	 * 
	 * @param SID
	 * @param userId
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param picture_uri
	 * @return
	 */
	public synchronized RoomClient setUsernameReconnect(String SID,
			Long userId, String username, String firstname, String lastname,
			String picture_uri) {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);

			currentClient.setUsername(username);
			currentClient.setUser_id(userId);
			currentClient.setPicture_uri(picture_uri);
			currentClient.setUserObject(userId, username, firstname, lastname);

			// Update Session Data
			sessionManagement.updateUserWithoutSession(SID, userId);

			Users user = userManagement.getUserById(userId);

			if (user != null) {
				currentClient.setExternalUserId(user.getExternalUserId());
				currentClient.setExternalUserType(user.getExternalUserType());
			}

			// only fill this value from User-Record
			// cause invited users have non
			// you cannot set the firstname,lastname from the UserRecord
			Users us = usersDao.getUser(userId);
			if (us != null && us.getPictureuri() != null) {
				// set Picture-URI
				currentClient.setPicture_uri(us.getPictureuri());
			}
			this.clientListManager.updateClientByStreamId(streamid,
					currentClient);
			return currentClient;
		} catch (Exception err) {
			log.error("[setUsername]", err);
		}
		return null;
	}

	/**
	 * this is set initial directly after login/loading language
	 * 
	 * @param userId
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param orgdomain
	 * @return
	 */
	public synchronized RoomClient setUsernameAndSession(String SID,
			Long userId, String username, String firstname, String lastname) {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(streamid);

			currentClient.setUsername(username);
			currentClient.setUser_id(userId);
			currentClient.setUserObject(userId, username, firstname, lastname);

			// Update Session Data
			log.debug("UDPATE SESSION " + SID + ", " + userId);
			sessionManagement.updateUserWithoutSession(SID, userId);

			Users user = userManagement.getUserById(userId);

			if (user != null) {
				currentClient.setExternalUserId(user.getExternalUserId());
				currentClient.setExternalUserType(user.getExternalUserType());
			}

			// only fill this value from User-Record
			// cause invited users have non
			// you cannot set the firstname,lastname from the UserRecord
			Users us = usersDao.getUser(userId);
			if (us != null && us.getPictureuri() != null) {
				// set Picture-URI
				currentClient.setPicture_uri(us.getPictureuri());
			}
			this.clientListManager.updateClientByStreamId(streamid,
					currentClient);
			return currentClient;
		} catch (Exception err) {
			log.error("[setUsername]", err);
		}
		return null;
	}

	/**
	 * used by the Screen-Sharing Servlet to trigger events
	 * 
	 * @param room_id
	 * @param message
	 * @return
	 */
	public synchronized HashMap<String, RoomClient> sendMessageByRoomAndDomain(
			Long room_id, Object message) {
		HashMap<String, RoomClient> roomClientList = new HashMap<String, RoomClient>();
		try {

			log.debug("sendMessageByRoomAndDomain " + room_id);

			IScope globalScope = getContext().getGlobalScope();

			IScope webAppKeyScope = globalScope
					.getScope(OpenmeetingsVariables.webAppRootKey);

			log.debug("webAppKeyScope " + webAppKeyScope);

			IScope scopeHibernate = webAppKeyScope.getScope(room_id.toString());
			// IScope scopeHibernate = webAppKeyScope.getScope("hibernate");

			if (scopeHibernate != null) {

				Collection<Set<IConnection>> conCollection = webAppKeyScope
						.getScope(room_id.toString()).getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							RoomClient rcl = this.clientListManager
									.getClientByStreamId(conn.getClient()
											.getId());
							if (rcl == null) {
								// continue;
							} else if (rcl.getIsScreenClient() != null
									&& rcl.getIsScreenClient()) {
								// continue;
							} else {
								if (conn instanceof IServiceCapableConnection) {
									// RoomClient rcl =
									// this.clientListManager.getClientByStreamId(conn.getClient().getId());
									((IServiceCapableConnection) conn).invoke(
											"newMessageByRoomAndDomain",
											new Object[] { message }, this);

									// log.debug("sending newMessageByRoomAndDomain to "
									// + conn);
								}
							}
						}
					}
				}

			} else {
				log.debug("sendMessageByRoomAndDomain servlet not yet started  - roomID : '"
						+ room_id + "'");
			}

		} catch (Exception err) {
			log.error("[getClientListBYRoomAndDomain]", err);
		}
		return roomClientList;
	}

	public synchronized List<RoomClient> getCurrentModeratorList() {
		try {
			log.debug("*..*getCurrentModerator id: ");

			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();

			// log.debug("Who is this moderator? "+currentMod);

			return this.clientListManager.getCurrentModeratorByRoom(room_id);
		} catch (Exception err) {
			log.error("[getCurrentModerator]", err);
		}
		return null;
	}

	/**
	 * This Function is triggered from the Whiteboard
	 * 
	 * @param whiteboardObj
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void sendVars(ArrayList whiteboardObjParam) {
		//
		try {

			// In previous version this has been always a Map, now its a List
			// so I re-wrapp that class to be a Map again.
			// swagner 13.02.2009
			// log.debug("*..*sendVars1: " + whiteboardObjParam);
			// log.debug("*..*sendVars2: " + whiteboardObjParam.getClass());
			// log.debug("*..*sendVars3: " +
			// whiteboardObjParam.getClass().getName());

			Map whiteboardObj = new HashMap();
			int i = 0;
			for (Iterator iter = whiteboardObjParam.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				// log.debug("obj"+obj);
				whiteboardObj.put(i, obj);
				i++;
			}

			// Map whiteboardObj = (Map) whiteboardObjParam;

			// Check if this User is the Mod:
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());

			if (currentClient == null) {
				return;
			}

			Long room_id = currentClient.getRoom_id();

			String action = whiteboardObj.get(2).toString();

			// log.debug("***** sendVars: " + actionObject.get(0));

			if (action != null && action.equals("whiteboardObj")) {
				// Update Whiteboard Object
				List actionObject = (List) whiteboardObj.get(3);
				WhiteboardManagement.getInstance().updateWhiteboardObject(
						room_id, actionObject);
			} else if (action != null && action.equals("moveMap")) {
				// Update Whiteboard Object
				List actionObject = (List) whiteboardObj.get(3);
				WhiteboardManagement.getInstance().updateWhiteboardObjectPos(
						room_id, actionObject);
			} else {
				// Store event in list
				WhiteboardManagement.getInstance().addWhiteBoardObject(room_id,
						whiteboardObj);
			}

			boolean showDrawStatus = getWhiteboardDrawStatus();

			// Notify all Clients of that Scope (Room)
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {

							if (conn.getClient().getId()
									.equals(current.getClient().getId())) {
								continue;
							}

							RoomClient rcl = this.clientListManager
									.getSyncClientByStreamId(conn.getClient()
											.getId());

							if (rcl == null) {
								continue;
							}

							if (!currentClient.getStreamid().equals(
									rcl.getStreamid())) {
								((IServiceCapableConnection) conn)
										.invoke("sendVarsToWhiteboard",
												new Object[] {
														(showDrawStatus ? currentClient
																: null),
														whiteboardObj }, this);
							}
						}
					}
				}
			}

		} catch (Exception err) {
			log.error("[sendVars]", err);
		}
	}

	/**
	 * This Function is triggered from the Whiteboard
	 * 
	 * @param whiteboardObj
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void sendVarsByWhiteboardId(
			ArrayList whiteboardObjParam, Long whiteboardId) {
		//
		try {

			Map whiteboardObj = new HashMap();
			int i = 0;
			for (Iterator iter = whiteboardObjParam.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				// log.debug("obj"+obj);
				whiteboardObj.put(i, obj);
				i++;
			}

			// Check if this User is the Mod:
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());

			if (currentClient == null) {
				return;
			}

			Long room_id = currentClient.getRoom_id();

			// log.debug("***** sendVars: " + whiteboardObj);

			// Store event in list
			String action = whiteboardObj.get(2).toString();

			if (action.equals("deleteMindMapNodes")) {

				// Simulate Single Delete Events for z-Index
				List actionObject = (List) whiteboardObj.get(3);

				List<List> itemObjects = (List) actionObject.get(3);

				Map whiteboardTempObj = new HashMap();
				whiteboardTempObj.put(2, "delete");

				for (List itemObject : itemObjects) {

					List<Object> tempActionObject = new LinkedList<Object>();
					tempActionObject.add("mindmapnode");
					tempActionObject.add(itemObject.get(0)); // z-Index -8
					tempActionObject.add(null); // simulate -7
					tempActionObject.add(null); // simulate -6
					tempActionObject.add(null); // simulate -5
					tempActionObject.add(null); // simulate -4
					tempActionObject.add(null); // simulate -3
					tempActionObject.add(null); // simulate -2
					tempActionObject.add(itemObject.get(1)); // Object-Name -1

					whiteboardTempObj.put(3, tempActionObject);

					WhiteboardManagement.getInstance().addWhiteBoardObjectById(
							room_id, whiteboardTempObj, whiteboardId);

				}

			} else {

				WhiteboardManagement.getInstance().addWhiteBoardObjectById(
						room_id, whiteboardObj, whiteboardId);

			}

			// This is no longer necessary
			// boolean ismod = currentClient.getIsMod();

			// log.debug("*..*ismod: " + ismod);

			// if (ismod) {

			Map<String, Object> sendObject = new HashMap<String, Object>();
			sendObject.put("id", whiteboardId);
			sendObject.put("param", whiteboardObjParam);

			boolean showDrawStatus = getWhiteboardDrawStatus();

			// Notify all Clients of that Scope (Room)
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							if (conn.getClient().getId()
									.equals(current.getClient().getId())) {
								continue;
							}

							RoomClient rcl = this.clientListManager
									.getSyncClientByStreamId(conn.getClient()
											.getId());

							if (rcl == null) {
								continue;
							}

							if (!currentClient.getStreamid().equals(
									rcl.getStreamid())) {
								((IServiceCapableConnection) conn).invoke(
										"sendVarsToWhiteboardById",
										new Object[] {
												showDrawStatus ? currentClient
														: null, sendObject },
										this);
							}
						}
					}
				}
			}

			// return numberOfUsers;
			// } else {
			// // log.debug("*..*you are not allowed to send: "+ismod);
			// return -1;
			// }
		} catch (Exception err) {
			log.error("[sendVarsByWhiteboardId]", err);
		}
	}

	public synchronized int sendVarsModeratorGeneral(Object vars) {
		log.debug("*..*sendVars: " + vars);
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());
			// Long room_id = currentClient.getRoom_id();

			log.debug("***** id: " + currentClient.getStreamid());

			boolean ismod = currentClient.getIsMod();

			if (ismod) {
				log.debug("CurrentScope :" + current.getScope().getName());
				// Send to all Clients of the same Scope
				Collection<Set<IConnection>> conCollection = current.getScope()
						.getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							if (conn instanceof IServiceCapableConnection) {
								RoomClient rcl = this.clientListManager
										.getClientByStreamId(conn.getClient()
												.getId());
								if (rcl == null) {
									// continue;
								} else if (rcl.getIsScreenClient() != null
										&& rcl.getIsScreenClient()) {
									// continue;
								} else {
									// log.debug("*..*idremote: " +
									// rcl.getStreamid());
									// log.debug("*..*my idstreamid: " +
									// currentClient.getStreamid());
									if (!currentClient.getStreamid().equals(
											rcl.getStreamid())) {
										((IServiceCapableConnection) conn)
												.invoke("sendVarsToModeratorGeneral",
														new Object[] { vars },
														this);
										// log.debug("sending sendVarsToModeratorGeneral to "
										// + conn);
									}
								}
							}
						}
					}
				}
				return 1;
			} else {
				// log.debug("*..*you are not allowed to send: "+ismod);
				return -1;
			}
		} catch (Exception err) {
			log.error("[sendVarsModeratorGeneral]", err);
		}
		return -1;
	}

	public synchronized int sendMessage(Object newMessage) {
		try {
			
			syncMessageToCurrentScope("sendVarsToMessage", newMessage, false);
			
		} catch (Exception err) {
			log.error("[sendMessage]", err);
		}
		return 1;
	}

	/**
	 * send status for shared browsing to all members except self
	 * @param newMessage
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public synchronized int sendBrowserMessageToMembers(Object newMessage) {
		try {
			IConnection current = Red5.getConnectionLocal();

			List newMessageList = (List) newMessage;

			String action = newMessageList.get(0).toString();

			BrowserStatus browserStatus = (BrowserStatus) current.getScope()
					.getAttribute("browserStatus");

			if (browserStatus == null) {
				browserStatus = new BrowserStatus();
			}

			if (action.equals("initBrowser") || action.equals("newBrowserURL")) {
				browserStatus.setBrowserInited(true);
				browserStatus.setCurrentURL(newMessageList.get(1).toString());
			} else if (action.equals("closeBrowserURL")) {
				browserStatus.setBrowserInited(false);
			}

			current.getScope().setAttribute("browserStatus", browserStatus);
			
			syncMessageToCurrentScope("sendVarsToMessage", newMessage, false);

		} catch (Exception err) {
			log.error("[sendMessage]", err);
		}
		return 1;
	}

	/**
	 * wrapper method
	 * @param newMessage
	 * @return
	 */
	public synchronized int sendMessageToMembers(Object newMessage) {
		try {
			
			//Sync to all users of current scope
			syncMessageToCurrentScope("sendVarsToMessage", newMessage, false);
			
		} catch (Exception err) {
			log.error("[sendMessage]", err);
		}
		return 1;
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
	 * @param remoteMethodName
	 * @param newMessage
	 * @param sendSelf 
	 * @return
	 */
	private synchronized int syncMessageToCurrentScope(String remoteMethodName, Object newMessage, boolean sendSelf) {
		try {
			IConnection current = Red5.getConnectionLocal();

			// Send to all Clients of that Scope(Room)
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager
									.getClientByStreamId(conn.getClient().getId());
							
							if (rcl == null) {
								// RoomClient can be null if there are network problems
								continue;
							} else if (rcl.getIsScreenClient() != null && rcl
											.getIsScreenClient()) {
								// screen sharing clients do not receive events
								continue;
							} else if (rcl.getIsAVClient() == null || rcl
									.getIsAVClient()) {
								// AVClients or potential AVClients do not receive events
								continue;
							} else if (current.getClient().getId().equals(
										conn.getClient().getId()) && !sendSelf) {
								//Do not send back to self
								continue;
							}
							
							((IServiceCapableConnection) conn).invoke(
									remoteMethodName, new Object[] { newMessage }, this);
							
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[syncMessageToCurrentScope]", err);
		}
		return 1;
	}

	/**
	 * wrapper method
	 * @param newMessage
	 * @return
	 */
	public synchronized int sendMessageWithClient(Object newMessage) {
		try {
			sendMessageWithClientWithSyncObject(newMessage, false);

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
	 * @return
	 */
	public synchronized int sendMessageWithClientWithSyncObject(Object newMessage, boolean sync) {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());

			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);
			
			//Sync to all users of current scope
			syncMessageToCurrentScope("sendVarsToMessageWithClient", hsm, sync);

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
	 * @return
	 */
	public synchronized int sendMessageById(Object newMessage, String clientId,
			IScope scope) {
		try {
			log.debug("### sendMessageById ###" + clientId);

			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("message", newMessage);

			// broadcast Message to specific user with id inside the same Scope
			Collection<Set<IConnection>> conCollection = scope.getConnections();

			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager
									.getClientByStreamId(conn.getClient()
											.getId());
							if (rcl == null) {
								// continue;
							} else if (rcl.getIsScreenClient() != null
									&& rcl.getIsScreenClient()) {
								// continue;
							} else {
								// log.debug("### sendMessageById 1 ###"+clientId);
								// log.debug("### sendMessageById 2 ###"+conn.getClient().getId());
								if (conn.getClient().getId().equals(clientId)) {
									((IServiceCapableConnection) conn).invoke(
											"sendVarsToMessageWithClient",
											new Object[] { hsm }, this);
									// log.debug("sendingsendVarsToMessageWithClient ByID to "
									// + conn);
								}
							}
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
	 * @return
	 */
	public synchronized int sendMessageWithClientById(Object newMessage,
			String clientId) {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());

			log.debug("### sendMessageWithClientById ###" + clientId);

			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);

			// broadcast Message to specific user with id inside the same Scope
			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());
						if (rcl == null) {
							// continue;
						} else if (rcl.getIsScreenClient() != null
								&& rcl.getIsScreenClient()) {
							// continue;
						} else {
							if (conn instanceof IServiceCapableConnection) {
								// log.debug("### sendMessageWithClientById 1 ###"+clientId);
								// log.debug("### sendMessageWithClientById 2 ###"+conn.getClient().getId());
								if (conn.getClient().getId().equals(clientId)) {
									((IServiceCapableConnection) conn).invoke(
											"sendVarsToMessageWithClient",
											new Object[] { hsm }, this);
									// log.debug("sendingsendVarsToMessageWithClient ByID to "
									// + conn);
								}
							}
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

	public synchronized void sendMessageWithClientByPublicSID(Object message,
			String publicSID) {
		try {
			// ApplicationContext appCtx = getContext().getApplicationContext();
			IScope globalScope = getContext().getGlobalScope();

			IScope webAppKeyScope = globalScope
					.getScope(OpenmeetingsVariables.webAppRootKey);

			// log.debug("webAppKeyScope "+webAppKeyScope);

			// Get Room Id to send it to the correct Scope
			RoomClient currentClient = this.clientListManager
					.getClientByPublicSID(publicSID, false);

			if (currentClient == null) {
				throw new Exception(
						"Could not Find RoomClient on List publicSID: "
								+ publicSID);
			}
			// default Scope Name
			String scopeName = "hibernate";
			if (currentClient.getRoom_id() != null) {
				scopeName = currentClient.getRoom_id().toString();
			}

			IScope scopeHibernate = webAppKeyScope.getScope(scopeName);

			// log.debug("scopeHibernate "+scopeHibernate);

			if (scopeHibernate != null) {
				// Notify the clients of the same scope (room) with user_id

				Collection<Set<IConnection>> conCollection = webAppKeyScope
						.getScope(scopeName).getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							RoomClient rcl = this.clientListManager
									.getClientByStreamId(conn.getClient()
											.getId());
							if (rcl != null) {
								if (rcl.getIsScreenClient() != null
										&& rcl.getIsScreenClient()) {
									// continue;
								} else {
									// log.debug("rcl "+rcl+" rcl.getUser_id(): "+rcl.getPublicSID()+" publicSID: "+publicSID+
									// " IS EQUAL? "+rcl.getPublicSID().equals(publicSID));
									if (rcl.getPublicSID().equals(publicSID)) {
										// log.debug("IS EQUAL ");
										((IServiceCapableConnection) conn)
												.invoke("newMessageByRoomAndDomain",
														new Object[] { message },
														this);
										log.debug("newMessageByRoomAndDomain RPC:newMessageByRoomAndDomain"
												+ message);
									}
								}
							}
						}
					}
				}

			} else {
				// Scope not yet started
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			err.printStackTrace();
		}
	}

	public synchronized void sendMessageWithClientByPublicSIDOrUser(
			Object message, String publicSID, Long user_id) {
		try {
			// ApplicationContext appCtx = getContext().getApplicationContext();

			IScope globalScope = getContext().getGlobalScope();

			IScope webAppKeyScope = globalScope
					.getScope(OpenmeetingsVariables.webAppRootKey);

			// log.debug("webAppKeyScope "+webAppKeyScope);

			// Get Room Id to send it to the correct Scope
			RoomClient currentClient = this.clientListManager
					.getClientByPublicSID(publicSID, false);

			if (currentClient == null) {
				currentClient = this.clientListManager
						.getClientByUserId(user_id);
			}

			Collection<Set<IConnection>> conCollection = null;

			if (currentClient == null) {
				// Must be from a previous session, search for user in current
				// scope
				IConnection current = Red5.getConnectionLocal();
				// Notify all Clients of that Scope (Room)
				conCollection = current.getScope().getConnections();
			} else {
				// default Scope Name
				String scopeName = "hibernate";
				if (currentClient.getRoom_id() != null) {
					scopeName = currentClient.getRoom_id().toString();
				}

				IScope scopeHibernate = webAppKeyScope.getScope(scopeName);

				if (scopeHibernate != null) {
					conCollection = webAppKeyScope.getScope(scopeName)
							.getConnections();
				}
			}

			// log.debug("scopeHibernate "+scopeHibernate);

			// Notify the clients of the same scope (room) with user_id

			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());
						if (rcl != null) {
							if (rcl.getIsScreenClient() != null
									&& rcl.getIsScreenClient()) {
								// continue;
							} else {
								// log.debug("rcl "+rcl+" rcl.getUser_id(): "+rcl.getPublicSID()+" publicSID: "+publicSID+
								// " IS EQUAL? "+rcl.getPublicSID().equals(publicSID));
								if (rcl.getPublicSID().equals(publicSID)) {
									// log.debug("IS EQUAL ");
									((IServiceCapableConnection) conn).invoke(
											"newMessageByRoomAndDomain",
											new Object[] { message }, this);
									log.debug("sendMessageWithClientByPublicSID RPC:newMessageByRoomAndDomain"
											+ message);
								} else if (user_id != 0
										&& rcl.getUser_id() != null
										&& rcl.getUser_id().equals(user_id)) {
									((IServiceCapableConnection) conn).invoke(
											"newMessageByRoomAndDomain",
											new Object[] { message }, this);
									log.debug("sendMessageWithClientByPublicSID RPC:newMessageByRoomAndDomain"
											+ message);
								}
							}
						}
					}
				}
			}

		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			err.printStackTrace();
		}
	}

	public synchronized Boolean getInterviewRecordingStatus() {
		try {

			IConnection current = Red5.getConnectionLocal();

			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {

						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());

						if (rcl.getIsRecording() != null
								&& rcl.getIsRecording()) {
							return true;
						}

					}
				}
			}

			return false;
		} catch (Exception err) {
			log.error("[getInterviewRecordingStatus]", err);
		}

		return null;
	}

	public synchronized Boolean startInterviewRecording() {
		try {

			IConnection current = Red5.getConnectionLocal();

			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {

						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());

						if (rcl.getIsRecording() != null
								&& rcl.getIsRecording()) {
							return false;
						}

					}
				}
			}

			RoomClient current_rcl = this.clientListManager
					.getClientByStreamId(current.getClient().getId());

			// Also set the Recording Flag to Record all Participants that enter
			// later
			current_rcl.setIsRecording(true);
			this.clientListManager.updateClientByStreamId(current.getClient()
					.getId(), current_rcl);

			Map<String, String> interviewStatus = new HashMap<String, String>();
			interviewStatus.put("action", "start");

			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						
						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());
						
						if (rcl == null) {
							continue;
						} else if (rcl.getIsAVClient() == null || rcl.getIsAVClient()) {
							continue;
						} else if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
							continue;
						}

						((IServiceCapableConnection) conn).invoke(
								"interviewStatus",
								new Object[] { interviewStatus }, this);
						log.debug("-- interviewStatus" + interviewStatus);

					}
				}
			}

			String recordingName = "Interview "
					+ CalendarPatterns.getDateWithTimeByMiliSeconds(new Date());

			this.flvRecorderService
					.recordMeetingStream(recordingName, "", true);

			return true;

		} catch (Exception err) {
			log.debug("[startInterviewRecording]", err);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes" })
	public synchronized Boolean sendRemoteCursorEvent(String streamid,
			Map messageObj) {
		try {

			IConnection current = Red5.getConnectionLocal();

			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {

						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());

						if (rcl == null) {
							// continue;
						} else if (rcl.getIsScreenClient() != null
								&& rcl.getIsScreenClient()) {

							if (rcl.getStreamid() != null
									&& rcl.getStreamid().equals(streamid)) {
								((IServiceCapableConnection) conn).invoke(
										"sendRemoteCursorEvent",
										new Object[] { messageObj }, this);
								log.debug("sendRemoteCursorEvent messageObj"
										+ messageObj);
							}

						}

					}
				}
			}
		} catch (Exception err) {
			log.debug("[sendRemoteCursorEvent]", err);
		}
		return null;
	}
	
	//FIXME: legacy code, needs to be removed
	public boolean checkSharerSession() {
		return true;
	}

	/**
	 * Stop the recording of the streams and send event to connected users of scope
	 * 
	 * @return
	 */
	public synchronized Boolean stopInterviewRecording() {
		try {

			IConnection current = Red5.getConnectionLocal();

			boolean found = false;
			Long flvRecordingId = null;

			Collection<Set<IConnection>> conCollection = current.getScope()
					.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {

						RoomClient rcl = this.clientListManager
								.getClientByStreamId(conn.getClient().getId());

						if (rcl.getIsRecording() != null
								&& rcl.getIsRecording()) {

							rcl.setIsRecording(false);

							flvRecordingId = rcl.getFlvRecordingId();

							rcl.setFlvRecordingId(null);

							// Reset the Recording Flag to Record all
							// Participants that enter later
							this.clientListManager.updateClientByStreamId(conn
									.getClient().getId(), rcl);

							found = true;
						}

					}
				}
			}

			if (!found) {
				return false;
			}

			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());

			this.flvRecorderService.stopRecordAndSave(scope, currentClient,
					flvRecordingId);

			Map<String, String> interviewStatus = new HashMap<String, String>();
			interviewStatus.put("action", "stop");

			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						((IServiceCapableConnection) conn).invoke(
								"interviewStatus",
								new Object[] { interviewStatus }, this);
						log.debug("sendMessageWithClientByPublicSID interviewStatus"
								+ interviewStatus);

					}
				}
			}

			return true;

		} catch (Exception err) {
			log.debug("[startInterviewRecording]", err);
		}
		return null;
	}

	/**
	 * Get all ClientList Objects of that room and domain Used in
	 * lz.applyForModeration.lzx
	 * 
	 * @return
	 */
	public synchronized HashMap<String, RoomClient> getClientListScope() {
		HashMap<String, RoomClient> roomClientList = new HashMap<String, RoomClient>();
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager
					.getClientByStreamId(current.getClient().getId());

			return this.clientListManager.getClientListByRoom(currentClient.getRoom_id());

		} catch (Exception err) {
			log.debug("[getClientListScope]", err);
		}
		return roomClientList;
	}

	private boolean getWhiteboardDrawStatus() {
		if (ScopeApplicationAdapter.whiteboardDrawStatus == null) {
			String drawStatus = cfgManagement.getConfValue("show.whiteboard.draw.status", String.class, "0");
			ScopeApplicationAdapter.whiteboardDrawStatus = "1".equals(drawStatus);
		}
		return ScopeApplicationAdapter.whiteboardDrawStatus;
	}
	
	public String getCryptKey() {
		try {

			if (ScopeApplicationAdapter.configKeyCryptClassName == null) {
				Configuration conf = cfgManagement.getConfKey(3,
						"crypt_ClassName");

				if (conf != null) {
					ScopeApplicationAdapter.configKeyCryptClassName = conf
							.getConf_value();
				}
			}

			return ScopeApplicationAdapter.configKeyCryptClassName;
		} catch (Exception err) {
			log.error("[getCryptKey]", err);
		}
		return null;
	}

    public String getExclusiveAudioKeyCode() {
        Configuration conf = cfgManagement.getConfKey(3, "exclusive.audio.keycode");
        if (null != conf) {
            return conf.getConf_value();
        } else {
            return null;
        }
    }

	public synchronized IScope getRoomScope(String room) {
		try {

			IScope globalScope = getContext().getGlobalScope();
			IScope webAppKeyScope = globalScope
					.getScope(OpenmeetingsVariables.webAppRootKey);

			String scopeName = "hibernate";
			// If set then its a NON default Scope
			if (room.length() != 0) {
				scopeName = room;
			}

			IScope scopeHibernate = webAppKeyScope.getScope(scopeName);

			return scopeHibernate;
		} catch (Exception err) {
			log.error("[getRoomScope]", err);
		}
		return null;
	}

    /*
	 * SIP transport methods
	 */

    public synchronized void updateSipTransport() {
        IConnection current = Red5.getConnectionLocal();
        String streamid = current.getClient().getId();
        RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
        log.debug("getSipConferenceMembersNumber: " + roommanagement.getSipConferenceMembersNumber(currentClient.getRoom_id()));
        String newNumber = "("+Integer.toString(roommanagement.getSipConferenceMembersNumber(currentClient.getRoom_id())-1)+")";
        if(!newNumber.equals(currentClient.getLastname())) {
            currentClient.setLastname(newNumber);
            this.clientListManager.updateClientByStreamId(streamid, currentClient);
            log.debug("updateSipTransport: {}, {}, {}, {}", new Object[]{currentClient.getPublicSID(),
                    currentClient.getRoom_id(), currentClient.getFirstname(), currentClient.getLastname()});
            sendMessageWithClient(new String[]{"personal",currentClient.getFirstname(),currentClient.getLastname()});
        }
    }

    /**
     * Perform call to specified phone number and join to conference
     * @param number to call
     */
    public synchronized void joinToConfCall(String number) {
        IConnection current = Red5.getConnectionLocal();
        String streamid = current.getClient().getId();
        RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
        Rooms rooms = roommanagement.getRoomById(currentClient.getRoom_id());
        log.debug("asterisk -rx \"originate Local/" + number + "@rooms extension " + rooms.getSipNumber() + "@rooms\"");
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"asterisk", "-rx", "originate Local/" + number + "@rooms extension " + rooms.getSipNumber() + "@rooms"});
        } catch (IOException e) {
            log.error("Executing asterisk originate error: ", e);
        }
    }

    public synchronized String getSipNumber(Long room_id) {
        Rooms rooms = roommanagement.getRoomById(room_id);
        if(rooms != null) {
            log.debug("getSipNumber: room_id: {}, sipNumber: {}", new Object[]{room_id, rooms.getSipNumber()});
            return rooms.getSipNumber();
        }
        return null;
    }

    public synchronized void setSipTransport(Long room_id, String publicSID, String broadCastId) {
        IConnection current = Red5.getConnectionLocal();
        String streamid = current.getClient().getId();
        Rooms room = roommanagement.getRoomById(room_id);
        // Notify all clients of the same scope (room)
        RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
        currentClient.setRoom_id(room_id);
        currentClient.setRoomEnter(new Date());
        currentClient.setFirstname("SIP Transport");
        currentClient.setLastname("("+Integer.toString(roommanagement.getSipConferenceMembersNumber(room_id)-1)+")");
        currentClient.setBroadCastID(Long.parseLong(broadCastId));
        currentClient.setIsBroadcasting(true);
        currentClient.setPublicSID(publicSID);
        currentClient.setAvsettings("av");
        currentClient.setVWidth(120);
        currentClient.setVHeight(90);
        this.clientListManager.updateClientByStreamId(streamid, currentClient);

        Collection<Set<IConnection>> conCollection = current
                .getScope().getConnections();
        for (Set<IConnection> conset : conCollection) {
            for (IConnection conn : conset) {
                if (conn != null) {
                    RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
                    if (rcl == null) {
                        // continue;
                    } else if (rcl.getIsScreenClient() != null
                            && rcl.getIsScreenClient()) {
                        // continue;
                    } else {
                        if (!streamid.equals(rcl.getStreamid())) {
                            // It is not needed to send back
                            // that event to the actuall
                            // Moderator
                            // as it will be already triggered
                            // in the result of this Function
                            // in the Client
                            if (conn instanceof IServiceCapableConnection) {
                                ((IServiceCapableConnection) conn).invoke("addNewUser", new Object[]{currentClient}, this);
                                ((IServiceCapableConnection) conn).invoke("newStream", new Object[]{currentClient}, this);
                                log.debug("sending setSipTransport to "
                                        + conn);
                            }
                        }
                    }
                }
            }
        }
    }
}
