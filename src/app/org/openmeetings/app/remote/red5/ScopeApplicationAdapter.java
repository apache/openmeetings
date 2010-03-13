package org.openmeetings.app.remote.red5;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmeetings.app.conference.whiteboard.WhiteboardManagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.calendar.management.MeetingMemberLogic;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.logs.ConferenceLogDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.documents.GenerateSWF;
import org.openmeetings.app.hibernate.beans.basic.Configuration;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.quartz.scheduler.QuartzMeetingReminderJob;
import org.openmeetings.app.quartz.scheduler.QuartzRecordingJob;
import org.openmeetings.app.quartz.scheduler.QuartzSessionClear;
import org.openmeetings.app.remote.ConferenceService;
import org.openmeetings.app.remote.FLVRecorderService;
import org.openmeetings.app.remote.MeetingMemberService;
import org.openmeetings.app.remote.PollService;
import org.openmeetings.app.remote.StreamService;
import org.openmeetings.app.remote.WhiteBoardService;
import org.openmeetings.app.sip.xmlrpc.OpenXGHttpClient;
import org.openmeetings.app.sip.xmlrpc.test.OpenXGClient;
import org.openmeetings.app.sip.xmlrpc.test.OpenXGWrapperClient;
import org.openmeetings.server.beans.ServerFrameBean;
import org.openmeetings.server.beans.ServerFrameCursorStatus;
import org.openmeetings.server.beans.ServerSharingSessionBean;
import org.openmeetings.server.beans.messages.ScreenSharingCursor;
import org.openmeetings.server.beans.messages.ScreenSharingMessage;
import org.openmeetings.server.beans.messages.ScreenSharingNewFrame;
import org.openmeetings.server.cache.ServerSharingSessionList;
import org.openmeetings.server.socket.ServerSocketMinaProcess;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamAwareScopeHandler;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ScopeApplicationAdapter extends ApplicationAdapter implements
	IPendingServiceCallback, IStreamAwareScopeHandler {

	private static final Logger log = Red5LoggerFactory.getLogger(ScopeApplicationAdapter.class, "openmeetings");
	
	//Beans, see red5-web.xml
	private ClientListManager clientListManager = null;
	private EmoticonsManager emoticonsManager = null;
	private WhiteBoardService whiteBoardService = null;
	private FLVRecorderService flvRecorderService = null;

	//This is the Folder where all executables are written
	//for windows platform
	public static String batchFileFir = "webapps"+File.separatorChar
									+"ROOT"+File.separatorChar
									+"jod" + File.separatorChar;
	public static String lineSeperator = System.getProperty("line.separator");
	   
	//The Global WebApp Path
	public static String webAppPath = "";
	public static String webAppRootKey = "openmeetings";//FIXME
	public static String configDirName = "conf";
	
	public static String configKeyCryptClassName = null;
	
	private static long broadCastCounter = 0;
	
	private static ScopeApplicationAdapter instance = null;
	
	public static synchronized ScopeApplicationAdapter getInstance(){
		return instance;
	}
	
	//Beans, see red5-web.xml
	public synchronized ClientListManager getClientListManager() {
		return clientListManager;
	}
	public synchronized void setClientListManager(ClientListManager clientListManager) {
		this.clientListManager = clientListManager;
	}
	public synchronized EmoticonsManager getEmoticonsManager() {
		return emoticonsManager;
	}
	public synchronized void setEmoticonsManager(EmoticonsManager emoticonsManager) {
		this.emoticonsManager = emoticonsManager;
	}
	public synchronized WhiteBoardService getWhiteBoardService() {
		return whiteBoardService;
	}
	public synchronized void setWhiteBoardService(WhiteBoardService whiteBoardService) {
		this.whiteBoardService = whiteBoardService;
	}
	public FLVRecorderService getFlvRecorderService() {
		return flvRecorderService;
	}
	public void setFlvRecorderService(FLVRecorderService flvRecorderService) {
		this.flvRecorderService = flvRecorderService;
	}

	public synchronized void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized boolean appStart(IScope scope) {
		try {
			instance = this;
			
			//This System out is for testing SLF4J / LOG4J and custom logging n Red5
			//System.out.println("Custom Webapp start UP "+new Date());
			
			webAppPath = scope.getResource("/").getFile().getAbsolutePath();
			batchFileFir = webAppPath + File.separatorChar + "streams" + File.separatorChar;
			
			log.debug("webAppPath : "+webAppPath);
			log.debug("batchFileFir : "+batchFileFir);
			//batchFileFir = webAppPath + File.separatorChar + "jod" + File.separatorChar;
			
			//Only load this Class one time
			//Initially this value might by empty, because the DB is empty yet
			Configuration conf = Configurationmanagement.getInstance().getConfKey(3,"crypt_ClassName");
			if (conf != null) {
			    ScopeApplicationAdapter.configKeyCryptClassName = conf.getConf_value();
			}
			
			// init your handler here
			
			//The scheduled Jobs did go into the Spring-Managed Beans, see schedulerJobs.service.xml
			
			QuartzSessionClear quartzSessionClear = new QuartzSessionClear();
			QuartzRecordingJob quartzRecordingJob = new QuartzRecordingJob();
			QuartzMeetingReminderJob reminderJob = new QuartzMeetingReminderJob();
			
			//QuartzZombieJob quartzZombieJob = new QuartzZombieJob();
			addScheduledJob(300000,quartzSessionClear);
			addScheduledJob(3000,quartzRecordingJob);
			addScheduledJob(100000, reminderJob);
			
			//addScheduledJob(2000,quartzZombieJob);
			
			//Spring Definition does not work here, its too early, Instance is not set yet
			EmoticonsManager.getInstance().loadEmot(scope);
			
			for (Iterator<String> subIterate = scope.getScopeNames();subIterate.hasNext();) {
				
				String scopeName = subIterate.next();
				
				log.debug("scopeName :: "+scopeName);
				
			}
			
			//OpenXGHttpClient.getInstance().openSIPgUserCreateTest();
			//OpenXGWrapperClient.getInstance().testConnection();
			//OpenXGClient.getInstance().testConnection();
			//ServerSocketMinaProcess serverSocketMinaProcess = new ServerSocketMinaProcess();
			
			//serverSocketMinaProcess.doInitSocket();
			
		} catch (Exception err) {
			log.error("[appStart]",err);
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
					+ streamId + " scope "+ room.getName() );
			
			//Set StreamId in Client
			service.invoke("setId", new Object[] { streamId },this);

			String swfURL = "";
			if (conn.getConnectParams().get("swfUrl") != null) {
				swfURL = conn.getConnectParams().get("swfUrl").toString();
			}
			
			RoomClient rcm = this.clientListManager.addClientListItem(streamId, room.getName(), conn.getRemotePort(), 
					conn.getRemoteAddress(), swfURL);
			
			//Log the User
			ConferenceLogDaoImpl.getInstance().addConferenceLog("ClientConnect", rcm.getUser_id(), 
					streamId, null, rcm.getUserip(), rcm.getScope(), 
					rcm.getExternalUserId(), rcm.getExternalUserType());

		} catch (Exception err){
			log.error("roomJoin",err);
		}		
		return true;
	}
	
	public synchronized Map screenSharerAction(Map map) {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			IServiceCapableConnection service = (IServiceCapableConnection) current;
			
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			Map returnMap = new HashMap();
			returnMap.put("result", "stopAll");
			
			
			log.debug("-----------  ");
			
			if (currentClient != null) {
				
				boolean stopStreaming = Boolean.valueOf(map.get("stopStreaming").toString());
				boolean stopRecording = Boolean.valueOf(map.get("stopRecording").toString());
				
				
				if (stopStreaming) {
					
					log.debug("start streamPublishStart Is Screen Sharing -- Stop ");
					
					//Notify all users of the same Scope
					Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
					for (Set<IConnection> conset : conCollection) {
						for (IConnection conn : conset) {
							if (conn != null) {
								if (conn instanceof IServiceCapableConnection) {
									if (conn.equals(current)){
										continue;
									} else {
										RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
										//log.debug("is this users still alive? :"+rcl);
										//Check if the Client is in the same room and same domain 
										if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				    						//continue;
				    					} else {
											IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
											//log.info("IServiceCapableConnection ID " + iStream.getClient().getId());
											iStream.invoke("stopRed5ScreenSharing",new Object[] { currentClient }, this);
											log.debug("send Notification to");
										}
									}
								}
							}
						}
					}
					
					if (currentClient.isStartRecording()) {
						
						returnMap.put("result", "stopSharingOnly");
						
					}
					
					currentClient.setStartStreaming(false);
					currentClient.setScreenPublishStarted(false);
					
					this.clientListManager.updateClientByStreamId(currentClient.getStreamid(), currentClient);
					
				}
				
				if (stopRecording) {
					
					if (currentClient.isStartStreaming()) {
						returnMap.put("result", "stopRecordingOnly");
					}
					
					//Notify all users of the same Scope
					Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
					for (Set<IConnection> conset : conCollection) {
						for (IConnection conn : conset) {
							if (conn != null) {
								if (conn instanceof IServiceCapableConnection) {
									if (conn.equals(current)){
										continue;
									} else {
										RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
										//log.debug("is this users still alive? :"+rcl);
										//Check if the Client is in the same room and same domain 
										if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				    						//continue;
				    					} else {
											IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
											//log.info("IServiceCapableConnection ID " + iStream.getClient().getId());
											iStream.invoke("stopRecordingMessage",new Object[] { currentClient }, this);
											log.debug("send Notification to");
										}
									}
								}
							}
						}
					}
					
					this.flvRecorderService.stopRecordAndSave(current.getScope(), currentClient, null);
					
					currentClient.setStartRecording(false);
					currentClient.setIsRecording(false);
					
					this.clientListManager.updateClientByStreamId(currentClient.getStreamid(), currentClient);
					
				}
			
				
			}
			
			return returnMap;
			
		} catch (Exception err){
			log.error("[screenSharerAction]",err);
		}	
		return null;
	}
	
	public List<RoomClient> checkRed5ScreenSharing() {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			
			log.debug("checkScreenSharing -2- "+streamid);
			
			List<RoomClient> screenSharerList = new LinkedList<RoomClient>();
			
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			HashMap<String,RoomClient> roomList = this.clientListManager.getClientListByRoomAll(currentClient.getRoom_id());
			
			for (Iterator<String> iter = roomList.keySet().iterator();iter.hasNext();) {
				
				RoomClient rcl = roomList.get(iter.next());
				
				if (rcl.isStartStreaming()) {
					screenSharerList.add(rcl);
				}
				
			}
			
			return screenSharerList;
			
		} catch (Exception err) {
			log.error("[checkScreenSharing]",err);
		}
		return null;
	}
	
	public synchronized Map setConnectionAsSharingClient(Map map) {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			//IServiceCapableConnection service = (IServiceCapableConnection) current;
			
			log.debug("### setConnectionAsSharingClient: ");
			
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());

			if (currentClient != null) {
				
				boolean startRecording = Boolean.valueOf(map.get("startRecording").toString());
				boolean startStreaming = Boolean.valueOf(map.get("startStreaming").toString());
				
				currentClient.setRoom_id(Long.parseLong(current.getScope().getName()));
				
				//Set this connection to be a RTMP-Java Client
				currentClient.setIsScreenClient(true);
				
				if (startStreaming) {
					currentClient.setStartStreaming(true);
				}
				
				if (startRecording) {
					currentClient.setStartRecording(true);
				}
				
				currentClient.setOrganization_id(Long.parseLong(map.get("organization_id").toString()));
				
				this.clientListManager.updateClientByStreamId(current.getClient().getId(), currentClient);
				
				Map returnMap = new HashMap();
				returnMap.put("alreadyPublished", false);
				
				//if is already started screen sharing, then there is no need to start it again
				if (currentClient.getScreenPublishStarted() != null && currentClient.getScreenPublishStarted()) {
					returnMap.put("alreadyPublished", true);
				}
				
				currentClient.setVX(Integer.parseInt(map.get("screenX").toString()));
				currentClient.setVY(Integer.parseInt(map.get("screenY").toString()));
				currentClient.setVWidth(Integer.parseInt(map.get("screenWidth").toString()));
				currentClient.setVHeight(Integer.parseInt(map.get("screenHeight").toString()));
				
				log.debug("screen x,y,width,height "+currentClient.getVX()+" "+currentClient.getVY()+" "+currentClient.getVWidth()+" "+currentClient.getVHeight());
				
				log.debug("publishName :: "+map.get("publishName"));
				
				currentClient.setStreamPublishName(map.get("publishName").toString());
			
				RoomClient currentScreenUser = this.clientListManager.getClientByPublicSID(currentClient.getStreamPublishName());
				
				currentClient.setFirstname(currentScreenUser.getFirstname());
				currentClient.setLastname(currentScreenUser.getLastname());
				
				//This is duplicated, but its not sure that in the meantime somebody requests this Client Object Info
				this.clientListManager.updateClientByStreamId(current.getClient().getId(), currentClient);
				
				if (startStreaming) {
					
					returnMap.put("modus", "startStreaming");
					
					log.debug("start streamPublishStart Is Screen Sharing ");
					
					//Notify all users of the same Scope
					Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
					for (Set<IConnection> conset : conCollection) {
						for (IConnection conn : conset) {
							if (conn != null) {
								if (conn instanceof IServiceCapableConnection) {
									if (conn.equals(current)){
										continue;
									} else {
										RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
										//log.debug("is this users still alive? :"+rcl);
										//Check if the Client is in the same room and same domain 
										if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				    						//continue;
				    					} else {
											IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
											//log.info("IServiceCapableConnection ID " + iStream.getClient().getId());
											iStream.invoke("newRed5ScreenSharing",new Object[] { currentClient }, this);
											log.debug("send Notification to");
										}
									}
								}
							}
						}
					}
				
				}
				
				if (startRecording) {
					
					returnMap.put("modus", "startRecording");
					
					String recordingName = "Recording "+CalendarPatterns.getDateWithTimeByMiliSeconds(new Date());
					
					this.flvRecorderService.recordMeetingStream( recordingName, "" ,false);
					
				}
				
				return returnMap;
				
			} else {
				throw new Exception("Could not find Screen Sharing Client "+current.getClient().getId());
			}
			
		} catch (Exception err){
			log.error("[setConnectionAsSharingClient]",err);
		}	
		return null;
	}
	
	/**
	 * this function is invoked directly after initial connecting
	 * @return
	 */
	public synchronized String getPublicSID() {
		IConnection current = Red5.getConnectionLocal();
		RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
		return currentClient.getPublicSID();
	}
	
	/**
	 * this function is invoked after a reconnect
	 * @param newPublicSID
	 */
	public synchronized Boolean overwritePublicSID(String newPublicSID) {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			if (currentClient == null) {
				return false;
			}
			currentClient.setPublicSID(newPublicSID);
			this.clientListManager.updateClientByStreamId(current.getClient().getId(), currentClient);
			return true;
		} catch (Exception err) {
			log.error("[overwritePublicSID]",err);
		}
		return null;
	}

	/**
	 * Logic must be before roomDisconnect cause otherwise you cannot throw a message to each one
	 * 
	 */
	@Override
	public void roomLeave(IClient client, IScope room) {
		try {
			
			log.debug("roomLeave " + client.getId() + " "+ room.getClients().size() + " " + room.getContextPath() + " "+ room.getName());
			
			//IConnection current = Red5.getConnectionLocal();
			
			RoomClient currentClient = this.clientListManager.getClientByStreamId(client.getId());
			
//			//In case its a screen sharing we start a new Video for that
//			if (currentClient != null && currentClient.getIsScreenClient() != null && currentClient.getIsScreenClient()) {
//			
//				log.debug("start streamPublishStart Is Screen Sharing -- Stop ");
//				
//				//Notify all users of the same Scope
//				Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
//				for (Set<IConnection> conset : conCollection) {
//					for (IConnection conn : conset) {
//						if (conn != null) {
//							if (conn instanceof IServiceCapableConnection) {
//								if (conn.equals(current)){
//									continue;
//								} else {
//									RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
//									//log.debug("is this users still alive? :"+rcl);
//									//Check if the Client is in the same room and same domain 
//									IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
//									//log.info("IServiceCapableConnection ID " + iStream.getClient().getId());
//									iStream.invoke("stopRed5ScreenSharing",new Object[] { currentClient }, this);
//									log.debug("send Notification to");
//								}
//							}
//						}
//					}
//				}
//				
//			}
			
			//The Room Client can be null if the Client left the room by using logicalRoomLeave
			if (currentClient != null) {
				log.debug("currentClient IS NOT NULL");
				this.roomLeaveByScope(currentClient, room);
			}
			
		} catch (Exception err){
			log.error("[roomLeave]",err);
		}		
	}
	
	/**
	 * this means a user has left a room but only logically, he didn't leave the app he just left the room
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
			
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			this.roomLeaveByScope(currentClient, current.getScope());
			
		} catch (Exception err){
			log.error("[logicalRoomLeave]",err);
		}		
	}	

	/**
	 * Removes the Client from the List, stops recording, adds the Room-Leave event to running recordings,
	 * clear Polls and removes Client from any list
	 * 
	 * @param currentClient
	 * @param currentScope
	 */
	public synchronized void roomLeaveByScope(RoomClient currentClient, IScope currentScope) {
		try {
			
			log.debug("currentClient "+currentClient);
			log.debug("currentScope "+currentScope);
			//log.debug("currentClient "+currentClient.getRoom_id());
			
			Long room_id = currentClient.getRoom_id();
			
			//Log the User
			ConferenceLogDaoImpl.getInstance().addConferenceLog("roomLeave", currentClient.getUser_id(), 
					currentClient.getStreamid(), room_id, currentClient.getUserip(), "", 
					currentClient.getExternalUserId(), currentClient.getExternalUserType());
			
			
			//Remove User from Sync List's
			if (room_id != null) {
				this.whiteBoardService.removeUserFromAllLists(currentScope, currentClient);
			}

			log.debug("##### roomLeave :. " + currentClient.getStreamid()); // just a unique number
			log.debug("removing USername "+currentClient.getUsername()+" "+currentClient.getConnectedSince()+" streamid: "+currentClient.getStreamid());
			
			//stop and save any recordings
			if (currentClient.getIsRecording()) {
				log.debug("*** roomLeave Current Client is Recording - stop that");
				//StreamService.stopRecordAndSave(currentScope, currentClient.getRoomRecordingName(), currentClient);
				
				this.flvRecorderService.stopRecordAndSave(currentScope, currentClient, null);
				
				//set to true and overwrite the default one cause otherwise no notification is send
				currentClient.setIsRecording(true);
			}
			
			//Notify all clients of the same currentScope (room) with domain and room
			//except the current disconnected cause it could throw an exception
			
			log.debug("currentScope "+currentScope);
			
			//Remove User AFTER cause otherwise the currentClient Object is NULL ?!
			//OR before ?!?!
			
			
			if (currentScope != null && currentScope.getConnections() != null) {
				//Notify Users of the current Scope
				Collection<Set<IConnection>> conCollection = currentScope.getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection cons : conset) {
						if (cons != null) {
							if (cons instanceof IServiceCapableConnection) {
						
								log.debug("sending roomDisconnect to " + cons + " client id " + cons.getClient().getId() );
								
								RoomClient rcl = this.clientListManager.getClientByStreamId(cons.getClient().getId());
								
								/*
								 * Check if the Client does still exist on the list
								 */
								if (rcl != null) {
									
									/*
									 * Do not send back to sender, but actually all other clients should receive this message
									 * swagner  01.10.2009
									 */
									if (!currentClient.getStreamid().equals(rcl.getStreamid())){
										
										
										if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				    						//continue;
				    					} else {
											//Send to all connected users	
											((IServiceCapableConnection) cons).invoke("roomDisconnect",new Object[] { currentClient }, this);
											log.debug("sending roomDisconnect to " + cons);
										}
										
										//add Notification if another user is recording
										log.debug("###########[roomLeave]");
										if (rcl.getIsRecording()){
											log.debug("*** roomLeave Any Client is Recording - stop that");
											//StreamService.addRoomClientEnterEventFunc(rcl, rcl.getRoomRecordingName(), rcl.getUserip(), false);
											//StreamService.stopRecordingShowForClient(cons, currentClient, rcl.getRoomRecordingName(), false);
											
											//this.flvRecorderService.stopRecordingShowForClient(cons, currentClient, false);
											
											this.flvRecorderService.stopRecordingShowForClient(cons, currentClient);
										}
										
									}
								} else {
									log.debug("For this StreamId: "+cons.getClient().getId()+" There is no Client in the List anymore");
								}
							}
						}		
					}
				}	
			}
			
			this.clientListManager.removeClient(currentClient.getStreamid());
			
			//If this Room is empty clear the Room Poll List
			HashMap<String,RoomClient> rcpList = this.clientListManager.getClientListByRoom(room_id);
			log.debug("roomLeave rcpList size: "+rcpList.size());
			if (rcpList.size()==0){
				PollService.clearRoomPollList(room_id);
//				log.debug("clearRoomPollList cleared");
			}
			
		} catch (Exception err) {
			log.error("[roomLeaveByScope]",err);
		}
	}
	

	/**
	 * This method handles the Event after a stream has been added all connected
	 * Clients in the same room will get a notification
	 * 
	 * @return void
	 * 
	 */
	public synchronized void streamPublishStart(IBroadcastStream stream) {
		try {

			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
						
			// Notify all the clients that the stream had been started
			log.debug("start streamPublishStart broadcast start: "+ stream.getPublishedName() + "CONN " + current);
			
			//In case its a screen sharing we start a new Video for that
			if (currentClient.getIsScreenClient()) {
				
				currentClient.setScreenPublishStarted(true);
				
				this.clientListManager.updateClientByStreamId(current.getClient().getId(), currentClient);
				
			}

			//Notify all users of the same Scope
			//We need to iterate through the streams to cathc if anybody is recording
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							if (conn.equals(current)){
								RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
								if (rcl.getIsRecording()){
									//StreamService.addRecordingByStreamId(current, streamid, currentClient, rcl.getFlvRecordingId());
									
									this.flvRecorderService.addRecordingByStreamId(current, streamid, currentClient, rcl.getFlvRecordingId());
									
								}
								continue;
							} else {
								RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
								//log.debug("is this users still alive? :"+rcl);
								//Check if the Client is in the same room and same domain 
								
									IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
									//log.info("IServiceCapableConnection ID " + iStream.getClient().getId());
									if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
			    						//continue;
			    					} else {
										iStream.invoke("newStream",new Object[] { currentClient }, this);
									}
									
									if (rcl.getIsRecording()){
										//StreamService.addRecordingByStreamId(current, streamid, currentClient, rcl.getFlvRecordingId());
										this.flvRecorderService.addRecordingByStreamId(current, streamid, currentClient, rcl.getFlvRecordingId());
										
									}
								
							}
						}
					}
				}
			}
			
			
		} catch (Exception err) {
			log.error("[streamPublishStart]",err);
		}
	}

	
	/**
	 * This method handles the Event after a stream has been removed all connected
	 * Clients in the same room will get a notification
	 * 
	 * @return void
	 * 
	 */
	public synchronized void streamBroadcastClose(IBroadcastStream stream) {

		// Notify all the clients that the stream had been started
		log.debug("start streamBroadcastClose broadcast close: "+ stream.getPublishedName());
		try {
			RoomClient rcl = this.clientListManager.getClientByStreamId(Red5.getConnectionLocal().getClient().getId());
			
			sendClientBroadcastNotifications(stream,"closeStream",rcl);
		} catch (Exception e){
			log.error("[streamBroadcastClose]",e);
		}
	}

	/**
	 * This method handles the notification room-based
	 * 
	 * @return void
	 * 
	 */	
	private synchronized void sendClientBroadcastNotifications(IBroadcastStream stream,String clientFunction, RoomClient rc){
		try {

			// Store the local so that we do not send notification to ourself back
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
				
			if (currentClient == null) {
				
				//In case the client has already left(kicked) this message might be thrown later then the RoomLeave
				//event and the currentClient is already gone
				//The second Use-Case where the currentClient is maybe null is if we remove the client because its a Zombie/Ghost
				
				return;
				
			}
			// Notify all the clients that the stream had been started
			log.debug("sendClientBroadcastNotifications: "+ stream.getPublishedName());
			log.debug("sendClientBroadcastNotifications : "+ currentClient+ " " + currentClient.getStreamid());
			
			//Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							if (conn.equals(current)){
								//there is a Bug in the current implementation of the appDisconnect
								if (clientFunction.equals("closeStream")){
									RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
									if (clientFunction.equals("closeStream") && rcl.getIsRecording()){
										log.debug("*** sendClientBroadcastNotifications Any Client is Recording - stop that");
										//StreamService.stopRecordingShowForClient(conn, currentClient, rcl.getRoomRecordingName(), false);
										this.flvRecorderService.stopRecordingShowForClient(conn, currentClient);
									}
									// Don't notify current client
									current.ping();
								}
								continue;
							} else {
								RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
								if (rcl != null){
									if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
			    						//continue;
			    					} else {
										log.debug("is this users still alive? :"+rcl);
										//conn.ping();
										IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
										//log.info("IServiceCapableConnection ID " + iStream.getClient().getId());
										iStream.invoke(clientFunction,new Object[] { rc }, this);
									}
									
									log.debug("sending notification to " + conn+" ID: ");
				
									//if this close stream event then stop the recording of this stream
									if (clientFunction.equals("closeStream") && rcl.getIsRecording()){
										log.debug("***  +++++++ ######## sendClientBroadcastNotifications Any Client is Recording - stop that");
										//StreamService.stopRecordingShowForClient(conn, currentClient, rcl.getRoomRecordingName(), false);
										this.flvRecorderService.stopRecordingShowForClient(conn, currentClient);
									}
								}
								
							}
						}
					}
				}	
			}
		} catch (Exception err) {
			log.error("[sendClientBroadcastNotifications]" , err);
		}
	}
	
	/*
	 * Functions to handle the moderation
	 */
	
//	/**
//	 * This Method will be invoked by each client if he applies for the moderation
//	 * 
//	 * @deprecated
//	 * 
//	 * @param id the StreamId of the Client which should become the Moderator
//	 * @return
//	 */
//
//	public synchronized String setModerator(String id) {
//		
//		String returnVal = "setModerator";
//		try {
//			log.debug("*..*setModerator id: " + id);
//			
//			IConnection current = Red5.getConnectionLocal();
//			//String streamid = current.getClient().getId();
//			
//			RoomClient currentClient = this.clientListManager.getClientByStreamId(id);
//			Long room_id = currentClient.getRoom_id();
//			
//			currentClient.setIsMod(new Boolean(true));
//			//Put the mod-flag to true for this client
//			this.clientListManager.updateClientByStreamId(id, currentClient);
//			
//			//Now set it false for all other clients of this room
//			HashMap<String,RoomClient> clientListRoom = this.clientListManager.getClientListByRoom(room_id);
//			for (Iterator<String> iter=clientListRoom.keySet().iterator();iter.hasNext();) {
//				String streamId = iter.next();
//				RoomClient rcl = clientListRoom.get(streamId);
//				//Check if it is not the same like we have just declared to be moderating this room
//				if( !id.equals(rcl.getStreamid())){
//					log.debug("set to false for client: "+rcl);
//					rcl.setIsMod(new Boolean(false));
//					this.clientListManager.updateClientByStreamId(streamId, rcl);
//				}				
//			}
//	
//			//Notify all clients of the same scope (room)
//			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
//			for (Set<IConnection> conset : conCollection) {
//				for (IConnection conn : conset) {
//					if (conn != null) {
//						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
//						if (conn instanceof IServiceCapableConnection) {
//							((IServiceCapableConnection) conn).invoke("setNewModerator",new Object[] { currentClient }, this);
//							log.debug("sending setNewModerator to " + conn);
//						}
//					}
//				}	
//			}
//		} catch (Exception err){
//			log.error("[setModerator]",err);
//			returnVal = err.toString();
//		}
//		return returnVal;
//	}  
	
	public synchronized Long addModerator(String publicSID) {
		try {
			
			log.debug("*..*addModerator publicSID: " + publicSID);
			
			IConnection current = Red5.getConnectionLocal();
			//String streamid = current.getClient().getId();
			
			RoomClient currentClient = this.clientListManager.getClientByPublicSID(publicSID);
			
			if (currentClient == null) {
				return -1L;
			}
			Long room_id = currentClient.getRoom_id();
			
			currentClient.setIsMod(true);
			//Put the mod-flag to true for this client
			this.clientListManager.updateClientByStreamId(currentClient.getStreamid(), currentClient);
			
			List<RoomClient> currentMods = this.clientListManager.getCurrentModeratorByRoom(room_id);
			
			//Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
						log.debug("Send Flag to Client: "+rcl.getUsername());
						if (conn instanceof IServiceCapableConnection) {
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								((IServiceCapableConnection) conn).invoke("setNewModeratorByList",new Object[] { currentMods }, this);
								log.debug("sending setNewModeratorByList to " + conn);
							}
						}
					}
				}	
			}
			
		} catch (Exception err) {
			log.error("[addModerator]",err);
		}
		return -1L;
	}
	
	public void setNewCursorPosition(Object item) {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			//log.debug("[setNewCursorPosition]"+item);
			
			Map cursor = (Map) item;
			cursor.put("streamPublishName",currentClient.getStreamPublishName());
			
			//log.debug("[setNewCursorPosition x]"+cursor.get("cursor_x"));
			//log.debug("[setNewCursorPosition y]"+cursor.get("cursor_y"));
			//log.debug("[setNewCursorPosition publicSID]"+cursor.get("publicSID"));
			
			//Notify all users of the same Scope
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							if (conn.equals(current)){
								continue;
							} else {
								RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
								if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
		    						//continue;
		    					} else {
									//log.debug("is this users still alive? :"+rcl);
									//Check if the Client is in the same room and same domain 
									IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
									//log.info("IServiceCapableConnection ID " + iStream.getClient().getId());
									iStream.invoke("newRed5ScreenCursor",new Object[] { cursor }, this);
									//log.debug("send Notification to");
								}
							}
						}
					}
				}
			}
			
			
		} catch (Exception err) {
			log.error("[setNewCursorPosition]",err);
		}
	}
	
	public synchronized Long removeModerator(String publicSID) {
		try {
			
			log.debug("*..*addModerator publicSID: " + publicSID);
			
			IConnection current = Red5.getConnectionLocal();
			//String streamid = current.getClient().getId();
			
			RoomClient currentClient = this.clientListManager.getClientByPublicSID(publicSID);
			
			if (currentClient == null) {
				return -1L;
			}
			Long room_id = currentClient.getRoom_id();
			
			currentClient.setIsMod(false);
			//Put the mod-flag to true for this client
			this.clientListManager.updateClientByStreamId(currentClient.getStreamid(), currentClient);
			
			List<RoomClient> currentMods = this.clientListManager.getCurrentModeratorByRoom(room_id);
			
			//Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
						if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
    						//continue;
    					} else {
							log.debug("Send Flag to Client: "+rcl.getUsername());
							if (conn instanceof IServiceCapableConnection) {
								((IServiceCapableConnection) conn).invoke("setNewModeratorByList",new Object[] { currentMods }, this);
								log.debug("sending setNewModeratorByList to " + conn);
							}
						}
					}
				}	
			}
			
		} catch (Exception err) {
			log.error("[addModerator]",err);
		}
		return -1L;
	}

	public synchronized Long setBroadCastingFlag(String publicSID, boolean value, Integer interviewPodId) {
		try {
			
			log.debug("*..*setBroadCastingFlag publicSID: " + publicSID);
			
			IConnection current = Red5.getConnectionLocal();
			//String streamid = current.getClient().getId();
			
			RoomClient currentClient = this.clientListManager.getClientByPublicSID(publicSID);
			
			if (currentClient == null) {
				return -1L;
			}
			
			currentClient.setIsBroadcasting(value);
			currentClient.setInterviewPodId(interviewPodId);
			
			//Put the mod-flag to true for this client
			this.clientListManager.updateClientByStreamId(currentClient.getStreamid(), currentClient);
			
			//Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
						if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
    						//continue;
    					} else {
							log.debug("Send Flag to Client: "+rcl.getUsername());
							if (conn instanceof IServiceCapableConnection) {
								((IServiceCapableConnection) conn).invoke("setNewBroadCastingFlag",new Object[] { currentClient }, this);
								log.debug("sending setNewBroadCastingFlag to " + conn);
							}
						}
					}
				}	
			}
			
		} catch (Exception err) {
			log.error("[setBroadCastingFlag]",err);
		}
		return -1L;
	}
	
	/**
	 * Invoked by a User whenever he want to become moderator
	 * this is needed, cause if the room has no moderator yet there is
	 * no-one he can ask to get the moderation, in case its a Non-Moderated 
	 * Room he should then get the Moderation without any confirmation needed
	 * @return Long 1 => means get Moderation, 2 => ask Moderator for Moderation, 3 => wait for Moderator
	 */
	public synchronized Long applyForModeration(String publicSID) {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			//String streamid = current.getClient().getId();
			
			RoomClient currentClient = this.clientListManager.getClientByPublicSID(publicSID);
			
			List<RoomClient> currentModList = this.clientListManager.getCurrentModeratorByRoom(currentClient.getRoom_id());
			
			if (currentModList.size() > 0) {
				return 2L;
			} else {
				//No moderator in this room at the moment
				Rooms room = Roommanagement.getInstance().getRoomById(currentClient.getRoom_id());
				
				if (room.getIsModeratedRoom()) {
					return 3L;
				} else {
					return 1L;
				}
			}
			
		} catch (Exception err) {
			log.error("[applyForModeration]",err);
		}
		return -1L;
	}
	
	/**
	 * there will be set an attribute called "broadCastCounter"
	 * this is the name this user will publish his stream
	 * @return long broadCastId
	 */
	public synchronized long getBroadCastId(){
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			currentClient.setBroadCastID(broadCastCounter++);
			this.clientListManager.updateClientByStreamId(streamid, currentClient);
			return currentClient.getBroadCastID();
		} catch (Exception err){
			log.error("[getBroadCastId]",err);
		}
		return -1;
	}
	
	
	
	
	
	
	/**
	 * this must be set _after_ the Video/Audio-Settings have been chosen (see editrecordstream.lzx)
	 * but _before_ anything else happens, it cannot be applied _after_ the stream has started!
	 * avsettings can be:
	 * av - video and audio
	 * a - audio only
	 * v - video only
	 * n - no a/v only static image
	 * furthermore 
	 * @param avsetting
	 * @param newMessage
	 * @return
	 */
	public synchronized RoomClient setUserAVSettings(String avsettings, Object newMessage){
		try {

			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			currentClient.setAvsettings(avsettings);
			Long room_id = currentClient.getRoom_id();					
			this.clientListManager.updateClientByStreamId(streamid, currentClient);
			
			HashMap<String,Object> hsm = new HashMap<String,Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);			
			
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient",new Object[] { hsm }, this);
								log.debug("sending setUserObjectNewOneFour to " + conn);
								
								//if somebody is recording in this room add the event
//								if (rcl.getIsRecording()) {
//									StreamService.addRoomClientAVSetEvent(currentClient, rcl.getRoomRecordingName(), conn.getRemoteAddress());
//								}
							}
						}
					}
				}
			}	
			
			return currentClient;
		} catch (Exception err){
			log.error("[setUserAVSettings]",err);
		}
		return null;
	}	
	
	/*
	 * checks if the user is allowed to apply for Moderation
	 * 
	 */
	public synchronized Boolean checkRoomValues(Long room_id){
		try {
			
			// appointed meeting or moderated Room?
			Rooms room = Roommanagement.getInstance().getRoomById(room_id);
			
			// not really - default logic
			if(room.getAppointment() == null || room.getAppointment() == false){
				
				if (room.getIsModeratedRoom()) {
					
					//if this is a Moderated Room then the Room can be only locked off by the Moderator Bit
					List<RoomClient> clientModeratorListRoom = this.clientListManager.getCurrentModeratorByRoom(room_id);
					
					//If there is no Moderator yet and we are asking for it then deny it
					//cause at this moment, the user should wait untill a Moderator enters the Room
					if (clientModeratorListRoom.size() == 0) {
						
						return false;
						
					} else {
						
						return true;
						
					}
					
						
				} else {
					
					return true;
					
				}
				
				
			} else {
				
				//FIXME: TODO: For Rooms that are created as Appointment we have to check that too
				//but I don't know yet the Logic behind it - swagner 19.06.2009
				
				return true;
				
			}
			
			
		} catch (Exception err){
			log.error("[checkRoomValues]",err);
		}
		return false;
	}	
	
	/**
	 * This function is called once a User enters a Room
	 * 
	 * @param room_id
	 * @param colorObj 
	 * @return
	 */
	public synchronized HashMap<String,RoomClient> setRoomValues(Long room_id, 
			Boolean becomeModerator, Boolean isSuperModerator, Long organization_id, 
			String colorObj){
		try {

			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			currentClient.setRoom_id(room_id);
			currentClient.setRoomEnter(new Date());
			currentClient.setOrganization_id(organization_id);
			
			currentClient.setUsercolor(colorObj);
			
			//Inject externalUserId if nothing is set yet
			if (currentClient.getExternalUserId() == null) {
				Users us = UsersDaoImpl.getInstance().getUser(currentClient.getUser_id());
				if (us != null) {
					currentClient.setExternalUserId(us.getExternalUserId());
					currentClient.setExternalUserType(us.getExternalUserType());
				}
			}
			
			//This can be set without checking for Moderation Flag
			currentClient.setIsSuperModerator(isSuperModerator);
			
			this.clientListManager.updateClientByStreamId(streamid, currentClient);
			
			
			//Log the User
			ConferenceLogDaoImpl.getInstance().addConferenceLog("roomEnter", currentClient.getUser_id(), 
					streamid, room_id, currentClient.getUserip(), "", 
					currentClient.getExternalUserId(), currentClient.getExternalUserType());
			
			log.debug("##### setRoomValues : " + currentClient.getUsername()+" "+currentClient.getStreamid()); // just a unique number

			//Check for Moderation
			//LogicalRoom ENTER
			HashMap<String,RoomClient> clientListRoom = this.getRoomClients(room_id);
			
			
			// appointed meeting or moderated Room?
			Rooms room = Roommanagement.getInstance().getRoomById(room_id);
			
			
			// not really - default logic
			if(room.getAppointment() == null || room.getAppointment() == false){
				
				if (room.getIsModeratedRoom()) {
					
					//if this is a Moderated Room then the Room can be only locked off by the Moderator Bit
					//List<RoomClient> clientModeratorListRoom = this.clientListManager.getCurrentModeratorByRoom(room_id);
					
					//If there is no Moderator yet we have to check if the current User has the Bit set to true to 
					//become one, otherwise he won't get Moderation and has to wait
					if (becomeModerator) {
						currentClient.setIsMod(true);
						
						//There is a need to send an extra Event here, cause at this moment there could be 
						//already somebody in the Room waiting
						
						//Update the Client List
						this.clientListManager.updateClientByStreamId(streamid, currentClient);
						
						List<RoomClient> modRoomList = this.clientListManager.getCurrentModeratorByRoom(currentClient.getRoom_id());
						
						//Notify all clients of the same scope (room)
						Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
						for (Set<IConnection> conset : conCollection) {
							for (IConnection conn : conset) {
								if (conn != null) {
									RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
									if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
			    						//continue;
			    					} else {
										if( !streamid.equals(rcl.getStreamid())){
											//It is not needed to send back that event to the actuall Moderator
											//as it will be already triggered in the result of this Function 
											//in the Client
											if (conn instanceof IServiceCapableConnection) {
												((IServiceCapableConnection) conn).invoke("setNewModeratorByList",new Object[] { modRoomList }, this);
												log.debug("sending setNewModeratorByList to " + conn);
											}
										}
									}
								}
							}	
						}
						
					} else {
						//The current User is not a Teacher/Admin or whatever Role that should get the 
						//Moderation 
						currentClient.setIsMod(false);
					}
				
						
				} else {
					
					//If this is a normal Room Moderator rules : first come, first draw ;-)
					log.debug("setRoomValues : Room" + room_id + " not appointed! Moderator rules : first come, first draw ;-)" );
					if (clientListRoom.size()==1){
						log.debug("Room is empty so set this user to be moderation role");
						currentClient.setIsMod(true);
					} else {
						log.debug("Room is already somebody so set this user not to be moderation role");
						
						if (becomeModerator) {
							currentClient.setIsMod(true);
							
							//Update the Client List
							this.clientListManager.updateClientByStreamId(streamid, currentClient);
							
							List<RoomClient> modRoomList = this.clientListManager.getCurrentModeratorByRoom(currentClient.getRoom_id());
							
							//There is a need to send an extra Event here, cause at this moment there could be 
							//already somebody in the Room waiting
							
							//Notify all clients of the same scope (room)
							Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
							for (Set<IConnection> conset : conCollection) {
								for (IConnection conn : conset) {
									if (conn != null) {
										RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
										if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
				    						//continue;
				    					} else {
											if( !streamid.equals(rcl.getStreamid())){
												//It is not needed to send back that event to the actual Moderator
												//as it will be already triggered in the result of this Function 
												//in the Client
												if (conn instanceof IServiceCapableConnection) {
													((IServiceCapableConnection) conn).invoke("setNewModeratorByList",new Object[] { modRoomList }, this);
													log.debug("sending setNewModeratorByList to " + conn);
												}
											}
										}
									}
								}	
							}
							
						} else {
							//The current User is not a Teacher/Admin or whatever Role that should get the 
							//Moderation 
							currentClient.setIsMod(false);
						}
						
					}
					
				}
				
				//Update the Client List
				this.clientListManager.updateClientByStreamId(streamid, currentClient);
				
			} else{
				
				//If this is an Appointment then the Moderator will be set to the Invitor
				
				Appointment ment = AppointmentLogic.getInstance().getAppointmentByRoom(room_id);
				
				List<MeetingMember> members = MeetingMemberDaoImpl.getInstance().getMeetingMemberByAppointmentId(ment.getAppointmentId());
				
				Long userIdInRoomClient = currentClient.getUser_id();
				
				boolean found = false;
				boolean moderator_set = false;
				
				// Check if current user is set to moderator
				for(int i = 0; i< members.size(); i++)
				{
					MeetingMember member = members.get(i);					
					
					// only persistent users can schedule a meeting
					// userid is only set for registered users
					if(member.getUserid() != null )
					{
						log.debug("checking user " + member.getFirstname() + " for moderator role - ID : " + member.getUserid().getUser_id());
						
						if ( member.getUserid().getUser_id().equals(userIdInRoomClient) )
						{					
							found = true;
							
							if(member.getInvitor()){
								log.debug("User " + userIdInRoomClient + " is moderator due to flag in MeetingMember record");
								currentClient.setIsMod(true);
								
								//Update the Client List
								this.clientListManager.updateClientByStreamId(streamid, currentClient);
								
								List<RoomClient> modRoomList = this.clientListManager.getCurrentModeratorByRoom(currentClient.getRoom_id());
								
								//There is a need to send an extra Event here, cause at this moment there could be 
								//already somebody in the Room waiting
								
								//Notify all clients of the same scope (room)
								Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
								for (Set<IConnection> conset : conCollection) {
									for (IConnection conn : conset) {
										if (conn != null) {
											RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
											if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
					    						//continue;
					    					} else {
												if( !streamid.equals(rcl.getStreamid())){
													//It is not needed to send back that event to the actual Moderator
													//as it will be already triggered in the result of this Function 
													//in the Client
													if (conn instanceof IServiceCapableConnection) {
														((IServiceCapableConnection) conn).invoke("setNewModeratorByList",new Object[] { modRoomList }, this);
														log.debug("sending setNewModeratorByList to " + conn);
													}
												}
											}
										}
									}	
								}
								
								moderator_set = true;
								this.clientListManager.updateClientByStreamId(streamid, currentClient);
								break;
							}
							else
							{
								log.debug("User " + userIdInRoomClient + " is NOT moderator due to flag in MeetingMember record");
								currentClient.setIsMod(false);
								this.clientListManager.updateClientByStreamId(streamid, currentClient);
								break;
							}
						}
						else
						{
							if(member.getInvitor())
								moderator_set = true;
						}							
					}
					else
					{
						if(member.getInvitor())
							moderator_set = true;
					}
					
				}
				
				if(!found){
					log.debug("User " + userIdInRoomClient + " could not be found as MeetingMember -> definiteley no moderator");
					currentClient.setIsMod(false);
					this.clientListManager.updateClientByStreamId(streamid, currentClient);
				}
				else{
					// if current user is part of the memberlist, but moderator couldn't be retrieved : first come, first draw!
					if (clientListRoom.size()==1 && moderator_set == false){
						log.debug("");
						currentClient.setIsMod(true);
						
						//Update the Client List
						this.clientListManager.updateClientByStreamId(streamid, currentClient);
						
						List<RoomClient> modRoomList = this.clientListManager.getCurrentModeratorByRoom(currentClient.getRoom_id());
						
						//There is a need to send an extra Event here, cause at this moment there could be 
						//already somebody in the Room waiting
						
						//Notify all clients of the same scope (room)
						Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
						for (Set<IConnection> conset : conCollection) {
							for (IConnection conn : conset) {
								if (conn != null) {
									RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
									if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
			    						//continue;
			    					} else {
										if( !streamid.equals(rcl.getStreamid())){
											//It is not needed to send back that event to the actual Moderator
											//as it will be already triggered in the result of this Function 
											//in the Client
											if (conn instanceof IServiceCapableConnection) {
												((IServiceCapableConnection) conn).invoke("setNewModeratorByList",new Object[] { modRoomList }, this);
												log.debug("sending setNewModeratorByList to " + conn);
											}
										}
									}
								}
							}	
						}
						
						this.clientListManager.updateClientByStreamId(streamid, currentClient);
					}
				}
				
			}
			
			
			//Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							if (conn.equals(current)){
								continue;
							} else {				
								RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
								if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
		    						//continue;
		    					} else {
									//log.debug("*** setAudienceModus Found Client to " + conn);
									//log.debug("*** setAudienceModus Found Client to " + conn.getClient());
									if (conn instanceof IServiceCapableConnection) {
										((IServiceCapableConnection) conn).invoke("addNewUser",new Object[] { currentClient }, this);
										log.debug("sending addNewUser to " + conn);
									}
								}
							}
						}
					}
				}
			}
			
			
			return clientListRoom;
		} catch (Exception err){
			log.error("[setRoomValues]",err);
		}
		return null;
	}	
	
	public synchronized HashMap<String,RoomClient> getRoomClients(Long room_id) {
		try {

			HashMap <String,RoomClient> roomClientList = new HashMap<String,RoomClient>();
			
			//log.debug("roomClientList :: Number of Users :: "+roomClientList.size()+" ::room_id:: "+room_id);

			HashMap<String,RoomClient> clientListRoom = this.clientListManager.getClientListByRoom(room_id);
			for (Iterator<String> iter=clientListRoom.keySet().iterator();iter.hasNext();) {
				String key = (String) iter.next();
				RoomClient rcl = this.clientListManager.getClientByStreamId(key);
				//log.debug("#+#+#+#+##+## logicalRoomEnter ClientList key: "+rcl.getRoom_id()+" "+room_id);
				//log.debug("set to ++ for client: "+rcl.getStreamid());
				//Add user to List
				roomClientList.put(key, rcl);
			}
			
			return roomClientList;
		} catch (Exception err){
			log.error("[getRoomClients]",err);
		}
		return null;
	}
	
	/*
	 * Returns only the Moderators (if there are some)
	 * 
	 * @deprecated I don't see this Function used anywhere
	 * 
	 */
//	public synchronized HashMap<String,RoomClient> getModeratorRoomClients(Long room_id) {
//		try {
//
//			HashMap <String,RoomClient> roomClientList = new HashMap<String,RoomClient>();
//
//			HashMap<String,RoomClient> clientListRoom = this.clientListManager.getClientListByRoom(room_id);
//			for (Iterator<String> iter=clientListRoom.keySet().iterator();iter.hasNext();) {
//				String key = (String) iter.next();
//				RoomClient rcl = this.clientListManager.getClientByStreamId(key);
//				
//				if (rcl.getIsMod()) {
//					log.debug("#+#+#+#+##+## logicalRoomEnter ClientList key: "+rcl.getRoom_id()+" "+room_id);
//					log.debug("set to ++ for client: "+rcl.getStreamid());
//					//Add user to List
//					roomClientList.put(key, rcl);
//				}
//			}
//			
//			return roomClientList;
//		} catch (Exception err){
//			log.error("[getModeratorRoomClients]",err);
//		}
//		return null;
//	}
	

	/**
	 * this is set initial directly after login/loading language
	 * 
	 * @deprecated
	 * 
	 * @param userId
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param orgdomain
	 * @return
	 */
	public synchronized RoomClient setUsername(Long userId, String username, String firstname, String lastname){
		try {
			log.debug("#*#*#*#*#*#*# setUsername userId: "+userId+" username: "+username+" firstname: "+firstname+" lastname: "+lastname);
			IConnection current = Red5.getConnectionLocal();			
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			log.debug("[setUsername] id: "+currentClient.getStreamid());
			
			currentClient.setUsername(username);
			currentClient.setUser_id(userId);
			currentClient.setUserObject(userId, username, firstname, lastname);
			
			//Update Session Data
			log.debug("UDPATE SESSION "+currentClient.getPublicSID()+", "+userId);
			
			Sessionmanagement.getInstance().updateUser(currentClient.getPublicSID(), userId,false,null);
			
			//only fill this value from User-REcord
			//cause invited users have non
			//you cannot set the firstname,lastname from the UserRecord
			Users us = UsersDaoImpl.getInstance().getUser(userId);
			if (us!=null && us.getPictureuri()!=null){
				//set Picture-URI
				log.debug("###### SET PICTURE URI");
				currentClient.setPicture_uri(us.getPictureuri());
			}
			this.clientListManager.updateClientByStreamId(streamid, currentClient);
			return currentClient;
		} catch (Exception err){
			log.error("[setUsername]",err);
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
	public synchronized RoomClient setUsernameAndSession(String SID, Long userId, String username, String firstname, String lastname){
		try {
			log.debug("#*#*#*#*#*#*# setUsername userId: "+userId+" username: "+username+" firstname: "+firstname+" lastname: "+lastname);
			IConnection current = Red5.getConnectionLocal();			
			String streamid = current.getClient().getId();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			log.debug("[setUsername] id: "+currentClient.getStreamid());
			
			currentClient.setUsername(username);
			currentClient.setUser_id(userId);
			currentClient.setUserObject(userId, username, firstname, lastname);
			
			//Update Session Data
			log.debug("UDPATE SESSION "+SID+", "+userId);
			Sessionmanagement.getInstance().updateUserWithoutSession(SID, userId);
			
			Users user = Usermanagement.getInstance().getUserById(userId);
			
			if (user != null) {
				currentClient.setExternalUserId(user.getExternalUserId());
				currentClient.setExternalUserType(user.getExternalUserType());
			}
			
			//only fill this value from User-Record
			//cause invited users have non
			//you cannot set the firstname,lastname from the UserRecord
			Users us = UsersDaoImpl.getInstance().getUser(userId);
			if (us!=null && us.getPictureuri()!=null){
				//set Picture-URI
				log.debug("###### SET PICTURE URI");
				currentClient.setPicture_uri(us.getPictureuri());
			}
			this.clientListManager.updateClientByStreamId(streamid, currentClient);
			return currentClient;
		} catch (Exception err){
			log.error("[setUsername]",err);
		}
		return null;
	}
	
	/**
	 * 
	 * @param colorObj
	 * @param userPos
	 * @deprecated
	 * @return
	 */
	public synchronized int setAudienceModus(String colorObj, int userPos){
		try {
			IConnection current = Red5.getConnectionLocal();
			
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			log.debug("xmlcrm setUserObjectOneFour: "+currentClient.getUsername());
			currentClient.setUsercolor(colorObj);
			currentClient.setUserpos(userPos);
			Long room_id = currentClient.getRoom_id();	
						
			//Notify all clients of the same scope (room)
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							if (conn.equals(current)){
								continue;
							} else {				
								RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
								if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
		    						//continue;
		    					} else {
									//log.debug("*** setAudienceModus Found Client to " + conn);
									//log.debug("*** setAudienceModus Found Client to " + conn.getClient());
									if (conn instanceof IServiceCapableConnection) {
										((IServiceCapableConnection) conn).invoke("setAudienceModusClient",new Object[] { currentClient }, this);
										//log.debug("sending setAudienceModusClient to " + conn);
										//if any user in this room is recording add this client to the list
//										if (rcl.getIsRecording()) {
//											log.debug("currentClient "+currentClient.getPublicSID());
//											//StreamService.addRoomClientEnterEventFunc(currentClient, rcl.getRoomRecordingName(), currentClient.getUserip(), true);
//										}							
									}
								}
							}
						}
					}
				}
			}
			
		} catch (Exception err) {
			log.error("[setUserObjectOne2Four]",err);
		}
		return -1;		
	}
	
	/**
	 * used by the Screen-Sharing Servlet to trigger events
	 * @param room_id
	 * @param message
	 * @return
	 */
	public synchronized HashMap<String,RoomClient> sendMessageByRoomAndDomain(Long room_id, Object message){
		HashMap <String,RoomClient> roomClientList = new HashMap<String,RoomClient>();
		try {
			
			log.debug("sendMessageByRoomAndDomain "+room_id);
			
			IScope globalScope = getContext().getGlobalScope();
			
			IScope webAppKeyScope = globalScope.getScope(ScopeApplicationAdapter.webAppRootKey);
			
			log.debug("webAppKeyScope "+webAppKeyScope);
			
			IScope scopeHibernate = webAppKeyScope.getScope(room_id.toString());
			//IScope scopeHibernate = webAppKeyScope.getScope("hibernate");
			
			
			if (scopeHibernate!=null){
				
				Collection<Set<IConnection>> conCollection = webAppKeyScope.getScope(room_id.toString()).getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								if (conn instanceof IServiceCapableConnection) {
									//RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
									((IServiceCapableConnection) conn).invoke("newMessageByRoomAndDomain",new Object[] { message }, this);
									
									//log.debug("sending newMessageByRoomAndDomain to " + conn);
								}
							}
						}
					}
				}
				
			} else {
				log.debug("sendMessageByRoomAndDomain servlet not yet started  - roomID : '" + room_id + "'");
			}
			
		} catch (Exception err) {
			log.error("[getClientListBYRoomAndDomain]",err);
		}
		return roomClientList;
	}	
	
//	/**
//	 * @deprecated
//	 * 
//	 * @return
//	 */
//	public synchronized RoomClient getCurrentModerator(){
//		try {
//			log.debug("*..*getCurrentModerator id: ");
//			
//			IConnection current = Red5.getConnectionLocal();
//			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
//			Long room_id = currentClient.getRoom_id();		
//			
//			//log.debug("Who is this moderator? "+currentMod);
//			
//			return this.clientListManager.getCurrentModeratorByRoom(room_id);
//		} catch (Exception err){
//			log.error("[getCurrentModerator]",err);
//		}
//		return null;
//	}

	public synchronized List<RoomClient> getCurrentModeratorList(){
		try {
			log.debug("*..*getCurrentModerator id: ");
			
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();		
			
			//log.debug("Who is this moderator? "+currentMod);
			
			return this.clientListManager.getCurrentModeratorByRoom(room_id);
		} catch (Exception err){
			log.error("[getCurrentModerator]",err);
		}
		return null;
	}
	
	/**
	 * This Function is triggered from the Whiteboard
	 * 
	 * @param whiteboardObj
	 * @return
	 */
	public synchronized void sendVars(ArrayList whiteboardObjParam) {
		//
		try {
			
			//In previous version this has been always a Map, now its a List
			//so I re-wrapp that class to be a Map again.
			//swagner 13.02.2009
			//log.debug("*..*sendVars1: " + whiteboardObjParam);
			//log.debug("*..*sendVars2: " + whiteboardObjParam.getClass());
			//log.debug("*..*sendVars3: " + whiteboardObjParam.getClass().getName());
			
			Map whiteboardObj = new HashMap();
			int i = 0;
			for (Iterator iter = whiteboardObjParam.iterator();iter.hasNext();) {
				Object obj = iter.next();
				//log.debug("obj"+obj);
				whiteboardObj.put(i, obj);
				i++;
			}
			
			//Map whiteboardObj = (Map) whiteboardObjParam;
 			
			// Check if this User is the Mod:
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			if (currentClient == null) {
				return;
			}
			
			Long room_id = currentClient.getRoom_id();	
				
			//log.debug("***** sendVars: " + whiteboardObj);
			
			//Store event in list
			WhiteboardManagement.getInstance().addWhiteBoardObject(room_id, whiteboardObj);
			
			int numberOfUsers = 0;
			
			//This is no longer necessary
			//boolean ismod = currentClient.getIsMod();
			
			//log.debug("*..*ismod: " + ismod);
	
			//if (ismod) {
			
			//Notify all Clients of that Scope (Room)
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						continue;
	    					} else {
								//log.debug("*..*idremote: " + rcl.getStreamid());
								//log.debug("*..* sendVars room_id IS EQUAL: " + currentClient.getStreamid() + " asd " + rcl.getStreamid() + " IS eq? " +currentClient.getStreamid().equals(rcl.getStreamid()));
								if (!currentClient.getStreamid().equals(rcl.getStreamid())) {
									((IServiceCapableConnection) conn).invoke("sendVarsToWhiteboard", new Object[] { whiteboardObj },this);
									//log.debug("sending sendVarsToWhiteboard to " + conn + " rcl " + rcl);
									numberOfUsers++;
								}
							}
						}
					}						
				}
			}			
			
			//return numberOfUsers;
			//} else {
			//	// log.debug("*..*you are not allowed to send: "+ismod);
			//	return -1;
			//}
		} catch (Exception err) {
			log.error("[sendVars]",err);
		}
	}
	

	public synchronized int sendVarsModeratorGeneral(Object vars) {
		log.debug("*..*sendVars: " + vars);
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();	
				
			log.debug("***** id: " + currentClient.getStreamid());
			
			boolean ismod = currentClient.getIsMod();
	
			if (ismod) {
				log.debug("CurrentScope :"+current.getScope().getName());
				//Send to all Clients of the same Scope
				Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							if (conn instanceof IServiceCapableConnection) {
								RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
								if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
		    						//continue;
		    					} else {
									//log.debug("*..*idremote: " + rcl.getStreamid());
									//log.debug("*..*my idstreamid: " + currentClient.getStreamid());
									if (!currentClient.getStreamid().equals(rcl.getStreamid())) {
										((IServiceCapableConnection) conn).invoke("sendVarsToModeratorGeneral",	new Object[] { vars }, this);
										//log.debug("sending sendVarsToModeratorGeneral to " + conn);
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
			log.error("[sendVarsModeratorGeneral]",err);
		}
		return -1;
	}

	public synchronized int sendMessage(Object newMessage) {
		try {
			IConnection current = Red5.getConnectionLocal();
			//RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
				
			//Send to all Clients of that Scope(Room)
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							if (rcl == null || (rcl.getIsScreenClient() != null && rcl.getIsScreenClient())) {
	    						//continue;
	    					} else {
								//log.debug("*..*idremote: " + rcl.getStreamid());
								//log.debug("*..*my idstreamid: " + currentClient.getStreamid());
								((IServiceCapableConnection) conn).invoke("sendVarsToMessage",new Object[] { newMessage }, this);
								//log.debug("sending sendVarsToMessage to " + conn);		
							}
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendMessage]",err);
		}
		return 1;
	}


	public synchronized int sendMessageWithClient(Object newMessage) {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();	
				
			
			HashMap<String,Object> hsm = new HashMap<String,Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);
			
			//broadcast to everybody in the Scope (Room)
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								//log.debug("*..*idremote: " + rcl.getStreamid());
								//log.debug("*..*my idstreamid: " + currentClient.getStreamid());
								((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient",new Object[] { hsm }, this);
								//log.debug("sending sendVarsToMessageWithClient to " + conn);
							}
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ",err);
			return -1;
		}
		return 1;
	}
	
	/**
	 * Function is used to send the kick Trigger at the moment
	 * 
	 * @param newMessage
	 * @param clientId
	 * @return
	 */
	public synchronized int sendMessageById(Object newMessage, String clientId, IScope scope) {
		try {
			IConnection current = Red5.getConnectionLocal();
			
			log.debug("### sendMessageById ###"+clientId);
			
			HashMap<String,Object> hsm = new HashMap<String,Object>();
			hsm.put("message", newMessage);
			
			//broadcast Message to specific user with id inside the same Scope
			Collection<Set<IConnection>> conCollection = scope.getConnections();			
			
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								//log.debug("### sendMessageById 1 ###"+clientId);
								//log.debug("### sendMessageById 2 ###"+conn.getClient().getId());
								if (conn.getClient().getId().equals(clientId)){
									((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient",new Object[] { hsm }, this);
									//log.debug("sendingsendVarsToMessageWithClient ByID to " + conn);
								}
							}
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ",err);
			return -1;
		}		
		return 1;
	}

	public synchronized int sendMessageWithClientById(Object newMessage, String clientId) {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			log.debug("### sendMessageWithClientById ###"+clientId);
			
			HashMap<String,Object> hsm = new HashMap<String,Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);
			
			//broadcast Message to specific user with id inside the same Scope
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
						if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
    						//continue;
    					} else {
							if (conn instanceof IServiceCapableConnection) {
								//log.debug("### sendMessageWithClientById 1 ###"+clientId);
								//log.debug("### sendMessageWithClientById 2 ###"+conn.getClient().getId());
								if (conn.getClient().getId().equals(clientId)){
									((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient",new Object[] { hsm }, this);
									//log.debug("sendingsendVarsToMessageWithClient ByID to " + conn);
								}
							}
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ",err);
			return -1;
		}		
		return 1;
	}
	
	/**
	 * update the Session Object after changing the user-record
	 * 
	 * FIXME: This needs to be fixed after the rework of Application Adapter, see Issue 593
	 * 
	 * @param users_id
	 */
	public synchronized void updateUserSessionObject(Long users_id, String pictureuri){
		try {			
//			Users us = UsersDaoImpl.getInstance().getUser(users_id);
//			for (Iterator<String> itList = ClientList.keySet().iterator();itList.hasNext();) {
//				String red5Id  = itList.next();
//				RoomClient rcl = ClientList.get(red5Id);
//				
//				if (rcl.getUser_id().equals(users_id)){
//					log.info("updateUserSessionObject #### FOUND USER rcl1: "+rcl.getUser_id()+ " NEW PIC: "+pictureuri);
//					rcl.setPicture_uri(pictureuri);
//					rcl.setUsername(us.getLogin());
//					rcl.setFirstname(us.getFirstname());
//					rcl.setLastname(us.getLastname());
//					ClientList.put(red5Id, rcl);
//				}
//			}
		} catch (Exception err) {
			log.error("[updateUserSessionObject]",err);
		}
	}
	
	public synchronized IScope getRoomScope(String room) {
		try {
			
			IScope globalScope = getContext().getGlobalScope();
			IScope webAppKeyScope = globalScope.getScope(ScopeApplicationAdapter.webAppRootKey);
			
			String scopeName = "hibernate";
			//If set then its a NON default Scope
			if (room.length() != 0) {
				scopeName = room;
			}
			
			IScope scopeHibernate = webAppKeyScope.getScope(scopeName);
			
			return scopeHibernate;
		} catch (Exception err) {
			log.error("[getRoomScope]",err);
		}
		return null;
	}
	
	public synchronized void sendScreenSharingCursorPos(ServerFrameCursorStatus cursorStatus) {
		try {
			
			//log.debug("sendScreenSharingCursorPos ");
			
			ScreenSharingCursor screenSharingCursor = new ScreenSharingCursor();
			screenSharingCursor.setS(cursorStatus.getPublicSID());
			screenSharingCursor.setX(cursorStatus.getX());
			screenSharingCursor.setY(cursorStatus.getY());
				
			String publicSID = cursorStatus.getPublicSID();
			
			IScope globalScope = getContext().getGlobalScope();
			IScope webAppKeyScope = globalScope.getScope(ScopeApplicationAdapter.webAppRootKey);
			
			//log.debug("webAppKeyScope "+webAppKeyScope);
			
			//Get Room Id to send it to the correct Scope
			RoomClient currentClient = this.clientListManager.getClientByPublicSID(publicSID);
			
			if (currentClient == null) {
				return;
				//throw new Exception("Could not Find RoomClient on List "+publicSID);
			}
			//default Scope Name
			String scopeName = "hibernate";
			if (currentClient.getRoom_id() != null) {
				scopeName = currentClient.getRoom_id().toString();
			}
			
			IScope scopeHibernate = webAppKeyScope.getScope(scopeName);
			
			//log.debug("scopeHibernate "+scopeHibernate);
			
			if (scopeHibernate != null){
				//Notify the clients of the same scope (room) with user_id
				
				Collection<Set<IConnection>> conCollection = webAppKeyScope.getScope(scopeName).getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							//log.debug("rcl "+rcl+" rcl.getUser_id(): "+rcl.getPublicSID()+" publicSID: "+publicSID+ " IS EQUAL? "+rcl.getPublicSID().equals(publicSID));
							
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								//Do not send to self and only to registered viewers
								if (!rcl.getPublicSID().equals(publicSID)){
									
									boolean sharerOnList = false;
									for (int i=0;i<rcl.getSharerSIDs().size();i++) {
										sharerOnList = true;
										break;
									}
									
									if (sharerOnList) {
										//log.debug("IS EQUAL ");
										((IServiceCapableConnection) conn).invoke("newScreenCursorPosition",new Object[] { screenSharingCursor }, this);
										//log.debug("sendMessageWithClientByPublicSID RPC:newMessageByRoomAndDomain"+screenSharingCursor);
									}
								}
							}
						}
					}
				}

			} else {
				//Scope not yet started
				throw new Exception("ScreenSharing Message but no Scope to send");
			}
			
		} catch (Exception err) {
			log.error("[sendScreenSharingMessage] ",err);
			err.printStackTrace();
		}
	}

	public synchronized void sendScreenSharingFrame(ServerFrameBean serverFrameBean) {
		try {
			
			ScreenSharingNewFrame screenSharingNewFrame = new ScreenSharingNewFrame();
			screenSharingNewFrame.setS(serverFrameBean.getPublicSID());
			screenSharingNewFrame.setX(serverFrameBean.getXValue());
			screenSharingNewFrame.setY(serverFrameBean.getYValue());
			screenSharingNewFrame.setH(serverFrameBean.getHeight());
			screenSharingNewFrame.setW(serverFrameBean.getWidth());
				
			String publicSID = serverFrameBean.getPublicSID();
			
			IScope globalScope = getContext().getGlobalScope();
			IScope webAppKeyScope = globalScope.getScope(ScopeApplicationAdapter.webAppRootKey);
			
			//log.debug("webAppKeyScope "+webAppKeyScope);
			
			//Get Room Id to send it to the correct Scope
			RoomClient currentClient = this.clientListManager.getClientByPublicSID(publicSID);
			
			if (currentClient == null) {
				return;
				//throw new Exception("Could not Find RoomClient on List "+publicSID);
			}
			//default Scope Name
			String scopeName = "hibernate";
			if (currentClient.getRoom_id() != null) {
				scopeName = currentClient.getRoom_id().toString();
			}
			
			IScope scopeHibernate = webAppKeyScope.getScope(scopeName);
			
			//log.debug("scopeHibernate "+scopeHibernate);
			
			if (scopeHibernate!=null){
				//Notify the clients of the same scope (room) with user_id
				
				Collection<Set<IConnection>> conCollection = webAppKeyScope.getScope(scopeName).getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							//log.debug("rcl "+rcl+" rcl.getUser_id(): "+rcl.getPublicSID()+" publicSID: "+publicSID+ " IS EQUAL? "+rcl.getPublicSID().equals(publicSID));
							
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								//Do not send to self and only to registered viewers
								if (!rcl.getPublicSID().equals(publicSID)){
									
									boolean sharerOnList = false;
									for (int i=0;i<rcl.getSharerSIDs().size();i++) {
										sharerOnList = true;
										break;
									}
									
									if (sharerOnList) {
										//log.debug("IS EQUAL ");
										((IServiceCapableConnection) conn).invoke("newScreenFrame",new Object[] { screenSharingNewFrame }, this);
										//log.debug("sendMessageWithClientByPublicSID RPC:newMessageByRoomAndDomain"+screenSharingNewFrame);
									}
								}
							}
						}
					}
				}

			} else {
				//Scope not yet started
				throw new Exception("ScreenSharing Message but no Scope to send");
			}
			
		} catch (Exception err) {
			log.error("[sendScreenSharingMessage] ",err);
			err.printStackTrace();
		}
	}
	
	public List<ScreenSharingMessage> checkSharingSession() {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			HashMap<String,RoomClient> roomClientList = this.clientListManager.getClientListByRoom(currentClient.getRoom_id());
			
			List<ScreenSharingMessage> screenSharingMessageList = new LinkedList<ScreenSharingMessage>();
			
			for (Iterator<String> iter = roomClientList.keySet().iterator();iter.hasNext();) {
				
				RoomClient rcl = roomClientList.get(iter.next());
				
				ServerSharingSessionBean serverSharingSessionBean = ServerSharingSessionList.getServerSharingSessionBeanByPublicSID(rcl.getPublicSID());
				
				if (serverSharingSessionBean != null) {
					ScreenSharingMessage screenSharingMessage = new ScreenSharingMessage();
					screenSharingMessage.setS(serverSharingSessionBean.getPublicSID());
					screenSharingMessage.setW(serverSharingSessionBean.getWidth());
					
					screenSharingMessage.setDeleted(serverSharingSessionBean.isDeleted());
					
					screenSharingMessage.setL(new LinkedList<ScreenSharingNewFrame>());
					
					for (ServerFrameBean serverFrameBean : serverSharingSessionBean.getServerFrameBeans()) {
						
						ScreenSharingNewFrame screenSharingNewFrame = new ScreenSharingNewFrame();
						screenSharingNewFrame.setX(serverFrameBean.getXValue());
						screenSharingNewFrame.setY(serverFrameBean.getYValue());
						screenSharingNewFrame.setH(serverFrameBean.getHeight());
						screenSharingNewFrame.setW(serverFrameBean.getWidth());
						
						screenSharingMessage.getL().add(screenSharingNewFrame);
						
					}
					
					screenSharingMessageList.add(screenSharingMessage);
				}
			}
			
			return screenSharingMessageList;
			
		} catch (Exception err) {
			log.error("[checkSharingSession] ",err);
		}
		return null;
	}
	
	public void registerViewerSession(boolean register, String publicSID) {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			if (register) {
				//Add User to Session
				currentClient.getSharerSIDs().add(publicSID);
			} else {
				//Remove User from Session
				currentClient.getSharerSIDs().remove(publicSID);
				
			}
			
			//currentClient.setViewer(register);
			
			this.clientListManager.updateClientByStreamId(current.getClient().getId(), currentClient);
			
		} catch (Exception err) {
			log.error("[checkSharingSession] ",err);
		}
	}
	

	public boolean checkSharerSession() {
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			ServerSharingSessionBean serverSharingSessionBean = ServerSharingSessionList.getServerSharingSessionBeanByPublicSID(currentClient.getPublicSID());
			
			if (serverSharingSessionBean == null) {
				return true;
			} else if (serverSharingSessionBean.isDeleted()) {
				return true;
			} else {
				return false;
			}
			
		} catch (Exception err) {
			log.error("[checkSharingSession] ",err);
		}
		return true;
	}
	
	public synchronized void sendScreenSharingMessage(ServerSharingSessionBean serverSharingSessionBean) {
		try {
			
			log.debug("sendScreenSharingMessage ");
			
			ScreenSharingMessage screenSharingMessage = new ScreenSharingMessage();
			screenSharingMessage.setS(serverSharingSessionBean.getPublicSID());
			screenSharingMessage.setW(serverSharingSessionBean.getWidth());
			
			screenSharingMessage.setDeleted(serverSharingSessionBean.isDeleted());
			
			screenSharingMessage.setL(new LinkedList<ScreenSharingNewFrame>());
			
			for (ServerFrameBean serverFrameBean : serverSharingSessionBean.getServerFrameBeans()) {
				
				ScreenSharingNewFrame screenSharingNewFrame = new ScreenSharingNewFrame();
				screenSharingNewFrame.setX(serverFrameBean.getXValue());
				screenSharingNewFrame.setY(serverFrameBean.getYValue());
				screenSharingNewFrame.setH(serverFrameBean.getHeight());
				screenSharingNewFrame.setW(serverFrameBean.getWidth());
				
				screenSharingMessage.getL().add(screenSharingNewFrame);
				
			}
			
			
			String publicSID = serverSharingSessionBean.getPublicSID();
			
			IScope globalScope = getContext().getGlobalScope();
			IScope webAppKeyScope = globalScope.getScope(ScopeApplicationAdapter.webAppRootKey);
			
			//log.debug("webAppKeyScope "+webAppKeyScope);
			
			//Get Room Id to send it to the correct Scope
			RoomClient currentClient = this.clientListManager.getClientByPublicSID(publicSID);
			
			if (currentClient == null) {
				return;
				//throw new Exception("Could not Find RoomClient on List");
			}
			//default Scope Name
			String scopeName = "hibernate";
			if (currentClient.getRoom_id() != null) {
				scopeName = currentClient.getRoom_id().toString();
			}
			
			IScope scopeHibernate = webAppKeyScope.getScope(scopeName);
			
			//log.debug("scopeHibernate "+scopeHibernate);
			
			if (scopeHibernate!=null){
				//Notify the clients of the same scope (room) with user_id
				
				Collection<Set<IConnection>> conCollection = webAppKeyScope.getScope(scopeName).getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							//log.debug("rcl "+rcl+" rcl.getUser_id(): "+rcl.getPublicSID()+" publicSID: "+publicSID+ " IS EQUAL? "+rcl.getPublicSID().equals(publicSID));
							
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								//Send to self for debugging
								if (!rcl.getPublicSID().equals(publicSID) || true){
									//log.debug("IS EQUAL ");
									((IServiceCapableConnection) conn).invoke("newScreenSharing",new Object[] { screenSharingMessage }, this);
									log.debug("sendMessageWithClientByPublicSID RPC:newMessageByRoomAndDomain"+screenSharingMessage);
								}
							}
						}
					}
				}

			} else {
				//Scope not yet started
				throw new Exception("ScreenSharing Message but no Scope to send");
			}
			
		} catch (Exception err) {
			log.error("[sendScreenSharingMessage] ",err);
			err.printStackTrace();
		}
	}

	public synchronized void sendMessageWithClientByPublicSID(Object message, String publicSID) {
		try {
			//ApplicationContext appCtx = getContext().getApplicationContext();
			
			IScope globalScope = getContext().getGlobalScope();
			
			IScope webAppKeyScope = globalScope.getScope(ScopeApplicationAdapter.webAppRootKey);
			
			//log.debug("webAppKeyScope "+webAppKeyScope);
			
			//Get Room Id to send it to the correct Scope
			RoomClient currentClient = this.clientListManager.getClientByPublicSID(publicSID);
			
			if (currentClient == null) {
				throw new Exception("Could not Find RoomClient on List");
			}
			//default Scope Name
			String scopeName = "hibernate";
			if (currentClient.getRoom_id() != null) {
				scopeName = currentClient.getRoom_id().toString();
			}
			
			IScope scopeHibernate = webAppKeyScope.getScope(scopeName);
			
			//log.debug("scopeHibernate "+scopeHibernate);
			
			if (scopeHibernate!=null){
				//Notify the clients of the same scope (room) with user_id
				
				Collection<Set<IConnection>> conCollection = webAppKeyScope.getScope(scopeName).getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection conn : conset) {
						if (conn != null) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							if (rcl != null) {
								if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
		    						//continue;
		    					} else {
									//log.debug("rcl "+rcl+" rcl.getUser_id(): "+rcl.getPublicSID()+" publicSID: "+publicSID+ " IS EQUAL? "+rcl.getPublicSID().equals(publicSID));
									if (rcl.getPublicSID().equals(publicSID)){
										//log.debug("IS EQUAL ");
										((IServiceCapableConnection) conn).invoke("newMessageByRoomAndDomain",new Object[] { message }, this);
										log.debug("sendMessageWithClientByPublicSID RPC:newMessageByRoomAndDomain"+message);
									}
								}
							}
						}
					}
				}

			} else {
				//Scope not yet started
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ",err);
			err.printStackTrace();
		}		
	}
	
	public synchronized Boolean getInterviewRecordingStatus() {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						
						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
						
						if (rcl.getIsRecording() != null && rcl.getIsRecording()) {
							return true;
						}
						
					}
				}
			}
			
			
			return false;
		} catch (Exception err) {
			log.error("[getInterviewRecordingStatus]",err);
		}
		
		return null;
	}
	
	public synchronized Boolean startInterviewRecording() {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						
						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
						
						if (rcl.getIsRecording() != null && rcl.getIsRecording()) {
							return false;
						}
						
					}
				}
			}
			
			
			
			RoomClient current_rcl = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			//String publicSID = current_rcl.getPublicSID();
			
			//Also set the Recording Flag to Record all Participants that enter later
			current_rcl.setIsRecording(true);
			this.clientListManager.updateClientByStreamId(current.getClient().getId(), current_rcl);
			
			
			Map<String,String> interviewStatus = new HashMap<String,String>();
			interviewStatus.put("action", "start");
			
			//Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			//Collection<Set<IConnection>> conCollection2 = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						
						((IServiceCapableConnection) conn).invoke("interviewStatus",new Object[] { interviewStatus }, this);
						log.debug("-- interviewStatus"+interviewStatus);
						
					}
				}
			}
			
			String recordingName = "Interview "+CalendarPatterns.getDateWithTimeByMiliSeconds(new Date());
			
			this.flvRecorderService.recordMeetingStream( recordingName, "", true);
			
			return true;
			
		} catch (Exception err) {
			log.debug("[startInterviewRecording]",err);
		}
		return null;
	}
	
	public synchronized Boolean stopInterviewRecording() {
		try {
			
			IConnection current = Red5.getConnectionLocal();
			
			boolean found = false;
			Long flvRecordingId = null;
			
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						
						RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
						
						if (rcl.getIsRecording() != null && rcl.getIsRecording()) {
							
							rcl.setIsRecording(false);
							
							flvRecordingId = rcl.getFlvRecordingId();
							
							rcl.setFlvRecordingId(null);
							
							//Reset the Recording Flag to Record all Participants that enter later
							this.clientListManager.updateClientByStreamId(conn.getClient().getId(), rcl);
							
							found = true;
						}
						
					}
				}
			}
			
			if (!found) {
				return false;
			}
			
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			
			
			this.flvRecorderService.stopRecordAndSave(scope, currentClient, flvRecordingId);
			
			Map<String,String> interviewStatus = new HashMap<String,String>();
			interviewStatus.put("action", "stop");
			
			//Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						
						//log.debug("IS EQUAL ");
						((IServiceCapableConnection) conn).invoke("interviewStatus",new Object[] { interviewStatus }, this);
						log.debug("sendMessageWithClientByPublicSID interviewStatus"+interviewStatus);
						
					}
				}
			}
			
			return true;
			
		} catch (Exception err) {
			log.debug("[startInterviewRecording]",err);
		}
		return null;
	}
	
	/**
	 * Get all ClientList Objects of that room and domain
	 * Used in lz.applyForModeration.lzx
	 * @return
	 */
	public synchronized HashMap<String,RoomClient> getClientListScope(){
		HashMap <String,RoomClient> roomClientList = new HashMap<String,RoomClient>();
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			log.debug("xmlcrm getClientListScope: "+currentClient.getUsername());			
			Long room_id = currentClient.getRoom_id();	
							
			return this.clientListManager.getClientListByRoom(room_id);
			
		} catch (Exception err) {
			log.debug("[getClientListScope]",err);
		}
		return roomClientList;
	}
	
	public static synchronized String getCryptKey() {
		try {
			
			if (ScopeApplicationAdapter.configKeyCryptClassName == null) {
				Configuration conf = Configurationmanagement.getInstance().getConfKey(3,"crypt_ClassName");
				if (conf != null) {
				    ScopeApplicationAdapter.configKeyCryptClassName = conf.getConf_value();
				}
			}
			
			return ScopeApplicationAdapter.configKeyCryptClassName;
		} catch (Exception err) {
			log.error("[getCryptKey]",err);
		}
		return null;
	}
	
	
}
