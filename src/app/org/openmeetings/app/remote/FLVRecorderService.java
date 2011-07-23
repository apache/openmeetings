package org.openmeetings.app.remote;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.flvrecord.FlvRecordingDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingLogDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.data.flvrecord.beans.FLVRecorderObject;
import org.openmeetings.app.data.flvrecord.converter.FlvInterviewConverterTask;
import org.openmeetings.app.data.flvrecord.converter.FlvInterviewReConverterTask;
import org.openmeetings.app.data.flvrecord.converter.FlvRecorderConverterTask;
import org.openmeetings.app.data.flvrecord.listener.ListenerAdapter;
import org.openmeetings.app.data.flvrecord.listener.StreamAudioListener;
import org.openmeetings.app.data.flvrecord.listener.StreamScreenListener;
import org.openmeetings.app.data.flvrecord.listener.StreamTranscodingListener;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecording;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingLog;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.stream.ClientBroadcastStream;
import org.slf4j.Logger;


public class FLVRecorderService implements IPendingServiceCallback {
	
	private static final Logger log = Red5LoggerFactory.getLogger(FLVRecorderService.class, ScopeApplicationAdapter.webAppRootKey);

	//Spring Beans
	private ClientListManager clientListManager = null;
	private FlvRecordingDaoImpl flvRecordingDaoImpl = null;
	private FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDaoImpl = null;
	private UsersDaoImpl usersDaoImpl;
	private Roommanagement roommanagement;
	private FlvRecorderConverterTask flvRecorderConverterTask;
	private FlvInterviewConverterTask flvInterviewConverterTask;
	private FlvInterviewReConverterTask flvInterviewReConverterTask;
	private FlvRecordingLogDaoImpl flvRecordingLogDaoImpl;
	private ScopeApplicationAdapter scopeApplicationAdapter = null;
	
	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
		
	}

	public ClientListManager getClientListManager() {
		return this.clientListManager;
	}
	public void setClientListManager(ClientListManager clientListManager) {
		this.clientListManager = clientListManager;
	}
	
	public FlvRecordingDaoImpl getFlvRecordingDaoImpl() {
		return flvRecordingDaoImpl;
	}
	public void setFlvRecordingDaoImpl(FlvRecordingDaoImpl flvRecordingDaoImpl) {
		this.flvRecordingDaoImpl = flvRecordingDaoImpl;
	}

	public FlvRecordingMetaDataDaoImpl getFlvRecordingMetaDataDaoImpl() {
		return flvRecordingMetaDataDaoImpl;
	}
	public void setFlvRecordingMetaDataDaoImpl(
			FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDaoImpl) {
		this.flvRecordingMetaDataDaoImpl = flvRecordingMetaDataDaoImpl;
	}
	
	public UsersDaoImpl getUsersDaoImpl() {
		return usersDaoImpl;
	}
	public void setUsersDaoImpl(UsersDaoImpl usersDaoImpl) {
		this.usersDaoImpl = usersDaoImpl;
	}
	
	public Roommanagement getRoommanagement() {
		return roommanagement;
	}
	public void setRoommanagement(Roommanagement roommanagement) {
		this.roommanagement = roommanagement;
	}
	
	public FlvRecorderConverterTask getFlvRecorderConverterTask() {
		return flvRecorderConverterTask;
	}
	public void setFlvRecorderConverterTask(
			FlvRecorderConverterTask flvRecorderConverterTask) {
		this.flvRecorderConverterTask = flvRecorderConverterTask;
	}
	
	public FlvInterviewConverterTask getFlvInterviewConverterTask() {
		return flvInterviewConverterTask;
	}
	public void setFlvInterviewConverterTask(
			FlvInterviewConverterTask flvInterviewConverterTask) {
		this.flvInterviewConverterTask = flvInterviewConverterTask;
	}
	
	public FlvInterviewReConverterTask getFlvInterviewReConverterTask() {
		return flvInterviewReConverterTask;
	}
	public void setFlvInterviewReConverterTask(
			FlvInterviewReConverterTask flvInterviewReConverterTask) {
		this.flvInterviewReConverterTask = flvInterviewReConverterTask;
	}

	public FlvRecordingLogDaoImpl getFlvRecordingLogDaoImpl() {
		return flvRecordingLogDaoImpl;
	}
	public void setFlvRecordingLogDaoImpl(
			FlvRecordingLogDaoImpl flvRecordingLogDaoImpl) {
		this.flvRecordingLogDaoImpl = flvRecordingLogDaoImpl;
	}
	
	public ScopeApplicationAdapter getScopeApplicationAdapter() {
		return scopeApplicationAdapter;
	}
	public void setScopeApplicationAdapter(
			ScopeApplicationAdapter scopeApplicationAdapter) {
		this.scopeApplicationAdapter = scopeApplicationAdapter;
	}

	public RoomClient checkForRecording(){
		try {
			
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			
			log.debug("getCurrentRoomClient -2- "+streamid);
			
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			HashMap<String,RoomClient> roomClientList = this.clientListManager.getClientListByRoom(currentClient.getRoom_id());
			
			for (Iterator<String> iter = roomClientList.keySet().iterator();iter.hasNext();) {
				
				RoomClient rcl = roomClientList.get(iter.next());
				
				if (rcl.getIsRecording()) {
					
					return rcl;
					
				}
				
			}
			
			return null;
			
		} catch (Exception err) {
			err.printStackTrace();
			log.error("[checkForRecording]",err);
		}
		return null;
	}
	
	private static String generateFileName(Long flvRecording_id, String streamid) throws Exception{
		String dateString = CalendarPatterns.getTimeForStreamId(new java.util.Date());
		return "rec_"+flvRecording_id+"_stream_"+streamid+"_"+dateString;
		
	}
	
	public String recordMeetingStream(String roomRecordingName, String comment, Boolean isInterview){
		try {
			
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();

			Date now = new Date();
			
			//Receive flvRecordingId
			Long flvRecordingId = this.flvRecordingDaoImpl.addFlvRecording("", roomRecordingName, null, currentClient.getUser_id(),
									room_id, now, null, currentClient.getUser_id(), comment, currentClient.getStreamid(),
									currentClient.getVWidth(),currentClient.getVHeight(), isInterview);
			
			//Update Client and set Flag
			currentClient.setIsRecording(true);
			currentClient.setFlvRecordingId(flvRecordingId);
			this.clientListManager.updateClientByStreamId(current.getClient().getId(), currentClient);
			
			//get all stream and start recording them
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							
							log.debug("is this users still alive? :"+rcl);
							
							//FIXME: Check if this function is really in use at the moment	
							if (rcl.getIsScreenClient() != null && rcl.getIsScreenClient()) {
	    						//continue;
	    					} else {
								((IServiceCapableConnection) conn).invoke("startedRecording",new Object[] { currentClient }, this);
							}
							
							//If its the recording client we need another type of Meta Data
							if (rcl.getIsScreenClient()) {
							
								if (rcl.getFlvRecordingId() != null && rcl.getScreenPublishStarted() != null && rcl.getScreenPublishStarted()) {
									
									String streamName_Screen = generateFileName(flvRecordingId, rcl.getStreamPublishName().toString());
									
									Long flvRecordingMetaDataId = this.flvRecordingMetaDataDaoImpl.addFlvRecordingMetaData(flvRecordingId, 
																			rcl.getFirstname()+" "+rcl.getLastname(), now, 
																						false, false, true, streamName_Screen, rcl.getInterviewPodId());
									
									//Start FLV Recording
									recordShow(conn, rcl.getStreamPublishName(), streamName_Screen, flvRecordingMetaDataId, true, isInterview);
									
									//Add Meta Data
									rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
									
									this.clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
								
								}
								
							} else if 
							//if the user does publish av, a, v
							//But we only record av or a, video only is not interesting
							(rcl.getAvsettings().equals("av") || 
									rcl.getAvsettings().equals("a") || 
									rcl.getAvsettings().equals("v")){	
								
								String streamName = generateFileName(flvRecordingId, String.valueOf(rcl.getBroadCastID()).toString());
								
								//Add Meta Data
								boolean isAudioOnly = false;
								if (rcl.getAvsettings().equals("a")){
									isAudioOnly = true;
								}
								
								boolean isVideoOnly = false;
								if (rcl.getAvsettings().equals("v")){
									isVideoOnly = true;
								}
								
								Long flvRecordingMetaDataId = this.flvRecordingMetaDataDaoImpl.addFlvRecordingMetaData(flvRecordingId, 
																	rcl.getFirstname()+" "+rcl.getLastname(), now, 
																				isAudioOnly, isVideoOnly, false, streamName, rcl.getInterviewPodId());
								
								rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
								
								this.clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
								
								//Start FLV recording
								recordShow(conn, String.valueOf(rcl.getBroadCastID()).toString(), streamName, flvRecordingMetaDataId, false, isInterview);
								
							} 
								
						}
					}
				}		
			}
			
			return roomRecordingName;
			
		} catch (Exception err) {
			log.error("[recordMeetingStream]",err);
		}
		return null;
	}
	
	/**
	 * @deprecated
	 * @param conn
	 * @param broadcastid
	 * @param streamName
	 * @throws Exception
	 */
	private static void _recordShow(IConnection conn, String broadcastid, String streamName) throws Exception {
		log.debug("Recording show for: " + conn.getScope().getContextPath());
		log.debug("Name of CLient and Stream to be recorded: "+broadcastid);		
		//log.debug("Application.getInstance()"+Application.getInstance());
		log.debug("Scope "+conn);
		log.debug("Scope "+conn.getScope());
		// Get a reference to the current broadcast stream.
		ClientBroadcastStream stream = (ClientBroadcastStream) ScopeApplicationAdapter.getInstance()
				.getBroadcastStream(conn.getScope(), broadcastid);
		try {
			// Save the stream to disk.
			stream.saveAs(streamName, false);
		} catch (Exception e) {
			log.error("Error while saving stream: " + streamName, e);
		}
	}
	
	/**
	 * Start recording the published stream for the specified broadcast-Id
	 * @param conn
	 * @param broadcastid
	 * @param streamName
	 * @param flvRecordingMetaDataId
	 * @throws Exception
	 */
	private static void recordShow(IConnection conn, String broadcastid, 
			String streamName, Long flvRecordingMetaDataId, boolean isScreenData, Boolean isInterview) throws Exception {
		try {
			log.debug("Recording show for: " + conn.getScope().getContextPath());
			log.debug("Name of CLient and Stream to be recorded: "+broadcastid);		
			//log.debug("Application.getInstance()"+Application.getInstance());
			log.debug("Scope "+conn);
			log.debug("Scope "+conn.getScope());
			// Get a reference to the current broadcast stream.
			ClientBroadcastStream stream = (ClientBroadcastStream) ScopeApplicationAdapter.getInstance()
					.getBroadcastStream(conn.getScope(), broadcastid);
		
			
			// Save the stream to disk.
			if (isScreenData) {
				stream.addStreamListener(new StreamScreenListener(streamName, conn.getScope(), flvRecordingMetaDataId, isScreenData, isInterview));
			} else {
				   
				log.debug("### stream "+stream);
				log.debug("### streamName "+streamName);
				log.debug("### conn.getScope() "+conn.getScope());
				log.debug("### flvRecordingMetaDataId "+flvRecordingMetaDataId);
				log.debug("### isScreenData "+isScreenData);
				log.debug("### isInterview "+isInterview);
				
				if (isInterview) {
					
					//Additionally record the Video Signal
					stream.addStreamListener(new StreamScreenListener("AV_"+streamName, conn.getScope(), flvRecordingMetaDataId, isScreenData, isInterview));
				}
				
				stream.addStreamListener(new StreamAudioListener(streamName, conn.getScope(), flvRecordingMetaDataId, isScreenData, isInterview));
			}
			//Just for Debug Purpose
			//stream.saveAs(streamName+"_DEBUG", false);
		} catch (Exception e) {
			log.error("Error while saving stream: " + streamName, e);
		}
	}
	
	/**
	 * @deprecated
	 * @param conn
	 * @param broadcastId
	 */
	public void _stopRecordingShow(IConnection conn, String broadcastId) {
		try {
			
			log.debug("** stopRecordingShow: "+conn);
			log.debug("### Stop recording show for broadcastId: "+ broadcastId + " || " + conn.getScope().getContextPath());
			
			Object streamToClose = ScopeApplicationAdapter.getInstance().
											getBroadcastStream(conn.getScope(), broadcastId);
			
			if (streamToClose == null) {
				log.debug("Could not aquire Stream, maybe already closed");
			}
			
			ClientBroadcastStream stream = (ClientBroadcastStream) streamToClose;
			// Stop recording.
			stream.stopRecording();
			
		} catch (Exception err) {
			log.error("[stopRecordingShow]",err);
		}
	}
	
	/**
	 * Stops recording the publishing stream for the specified
	 * IConnection.
	 *
	 * @param conn
	 */
	public void stopRecordingShow(IConnection conn, String broadcastId, Long flvRecordingMetaDataId) {
		try {
			
			log.debug("** stopRecordingShow: "+conn);
			log.debug("### Stop recording show for broadcastId: "+ broadcastId + " || " + conn.getScope().getContextPath());
			
			Object streamToClose = ScopeApplicationAdapter.getInstance().
											getBroadcastStream(conn.getScope(), broadcastId);
			
			if (streamToClose == null) {
				log.debug("Could not aquire Stream, maybe already closed");
			}
			
			ClientBroadcastStream stream = (ClientBroadcastStream) streamToClose;

			if (stream.getStreamListeners() != null) {
				
				for (Iterator<IStreamListener> iter = stream.getStreamListeners().iterator();iter.hasNext();) {
					
					IStreamListener iStreamListener = iter.next();
					
					ListenerAdapter listenerAdapter = (ListenerAdapter) iStreamListener;
					
					log.debug("Stream Closing ?? "+listenerAdapter.getFlvRecordingMetaDataId()+ " " +flvRecordingMetaDataId);
					
					if (listenerAdapter.getFlvRecordingMetaDataId().equals(flvRecordingMetaDataId)) {
						log.debug("Stream Closing :: "+flvRecordingMetaDataId);
						listenerAdapter.closeStream();
					}
					
				}
				
				for (IStreamListener iStreamListener : stream.getStreamListeners()) {
					stream.removeStreamListener(iStreamListener);
				}
			
			}
			
			// Just for Debugging
			//stream.stopRecording();
			
		} catch (Exception err) {
			log.error("[stopRecordingShow]",err);
		}
	}
	
	public Long stopRecordAndSave(IScope scope, RoomClient currentClient, Long storedFlvRecordingId){
		try {
			log.debug("stopRecordAndSave "+currentClient.getUsername()+","+currentClient.getUserip());
			
			//get all stream and stop recording them
			Collection<Set<IConnection>> conCollection = scope.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							
							RoomClient rcl = ClientListManager.getInstance().getClientByStreamId(conn.getClient().getId());
							
							//FIXME: Check if this function is really in use at the moment	
//							if (!rcl.getIsScreenClient()) {
//								((IServiceCapableConnection) conn).invoke("stoppedRecording",new Object[] { currentClient }, this);
//							}
							
							log.debug("is this users still alive? :"+rcl);
							
							if (rcl.getIsScreenClient()) {
								
								if (rcl.getFlvRecordingId() != null && rcl.getScreenPublishStarted() != null && rcl.getScreenPublishStarted()) {
								
									//Stop FLV Recording
									stopRecordingShow(conn, rcl.getStreamPublishName(),rcl.getFlvRecordingMetaDataId());
									
									//Update Meta Data
									this.flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(),new Date());
								}
								
							} else if (rcl.getAvsettings().equals("av") || 
									rcl.getAvsettings().equals("a") || 
									rcl.getAvsettings().equals("v")){	
								
								stopRecordingShow(conn, String.valueOf(rcl.getBroadCastID()).toString(), rcl.getFlvRecordingMetaDataId() );
								
								//Update Meta Data
								this.flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(),new Date());
								
							}
							
						}
					}
				}
			}				
			
			//Store to database
			Long flvRecordingId = currentClient.getFlvRecordingId();
			
			//In the Case of an Interview the stopping client does not mean that its actually the recording client
			if (storedFlvRecordingId != null) {
				flvRecordingId = storedFlvRecordingId;
			}
			
			if (flvRecordingId != null) {
			
				this.flvRecordingDaoImpl.updateFlvRecordingEndTime(flvRecordingId, new Date(), currentClient.getOrganization_id());
				
				//Reset values
				currentClient.setFlvRecordingId(null);
				currentClient.setIsRecording(false);
				
				this.clientListManager.updateClientByStreamId(currentClient.getStreamid(), currentClient);
				
				log.debug("this.flvRecorderConverterTask ",this.flvRecorderConverterTask);
				
				FlvRecording flvRecording = this.flvRecordingDaoImpl.getFlvRecordingById(flvRecordingId);
			
				if (flvRecording.getIsInterview() == null || !flvRecording.getIsInterview()) {
					
					this.flvRecorderConverterTask.startConversionThread(flvRecordingId);
					
				} else {
					
					this.flvInterviewConverterTask.startConversionThread(flvRecordingId);
					
				}
				
			}
			
		} catch (Exception err) {
			
			log.error("[-- stopRecordAndSave --]",err);
		}
		return new Long(-1);
	}
	
	public RoomClient checkLzRecording() {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			
			log.debug("getCurrentRoomClient -2- "+streamid);
			
			RoomClient currentClient = this.clientListManager.getClientByStreamId(streamid);
			
			
			log.debug("getCurrentRoomClient -#########################- "+currentClient.getRoom_id());
			
			HashMap<String,RoomClient> roomList = this.clientListManager.getClientListByRoomAll(currentClient.getRoom_id());
			
			for (Iterator<String> iter = roomList.keySet().iterator();iter.hasNext();) {
				
				RoomClient rcl = roomList.get(iter.next());
				
				if (rcl.getIsRecording()) {
					return rcl;
				}
				
			}
			
			
		} catch (Exception err) {
			log.error("[checkLzRecording]",err);
		}
		return null;
	}
	
	public void stopRecordingShowForClient(IConnection conn, RoomClient rcl) {
		try {
			//this cannot be handled here, as to stop a stream and to leave a room is not
			//the same type of event.
			//StreamService.addRoomClientEnterEventFunc(rcl, roomrecordingName, rcl.getUserip(), false);
			log.error("### stopRecordingShowForClient: "+rcl.getIsRecording()+","+rcl.getUsername()+","+rcl.getUserip());
			
			if (rcl.getIsScreenClient()) {
				
				if (rcl.getFlvRecordingId() != null && rcl.getScreenPublishStarted() != null && rcl.getScreenPublishStarted()) {
				
					//Stop FLV Recording
					//FIXME: Is there really a need to stop it manually if the user just 
					//stops the stream?
					stopRecordingShow(conn, rcl.getStreamPublishName(), rcl.getFlvRecordingMetaDataId());
					
					//Update Meta Data
					this.flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(),new Date());
				}
				
			} else if ((rcl.getAvsettings().equals("a") || rcl.getAvsettings().equals("v") 
					|| rcl.getAvsettings().equals("av"))){
				
				//FIXME: Is there really a need to stop it manually if the user just 
				//stops the stream?
				stopRecordingShow(conn,String.valueOf(rcl.getBroadCastID()).toString(), rcl.getFlvRecordingMetaDataId());
				
				//Update Meta Data
				this.flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(),new Date());
			}
			
		} catch (Exception err) {
			log.error("[stopRecordingShowForClient]",err);
		}
	}
	
	public void addRecordingByStreamId(IConnection conn, String streamId, 
			RoomClient rcl, Long flvRecordingId) {
		try {
			
			FlvRecording flvRecording = this.flvRecordingDaoImpl.getFlvRecordingById(flvRecordingId);
			
			Date now = new Date();
			
			//If its the recording client we need another type of Meta Data
			if (rcl.getIsScreenClient()) {
			
				if (rcl.getFlvRecordingId() != null && rcl.getScreenPublishStarted() != null && rcl.getScreenPublishStarted()) {
					
					String streamName_Screen = generateFileName(flvRecordingId, rcl.getStreamPublishName().toString());
					
					log.debug("##############  ADD SCREEN OF SHARER :: "+rcl.getStreamPublishName());
					
					Long flvRecordingMetaDataId = this.flvRecordingMetaDataDaoImpl.addFlvRecordingMetaData(flvRecordingId, 
															rcl.getFirstname()+" "+rcl.getLastname(), now, 
																		false, false, true, streamName_Screen, rcl.getInterviewPodId());
					
					//Start FLV Recording
					recordShow(conn, rcl.getStreamPublishName(), streamName_Screen, flvRecordingMetaDataId, true, flvRecording.getIsInterview());
					
					//Add Meta Data
					rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
					
					this.clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
				
				}
				
			} else if 
			//if the user does publish av, a, v
			//But we only record av or a, video only is not interesting
			(rcl.getAvsettings().equals("av") || 
					rcl.getAvsettings().equals("a") || 
					rcl.getAvsettings().equals("v")){	
				
				String streamName = generateFileName(flvRecordingId, String.valueOf(rcl.getBroadCastID()).toString());
				
				//Add Meta Data
				boolean isAudioOnly = false;
				if (rcl.getAvsettings().equals("a")){
					isAudioOnly = true;
				}
				boolean isVideoOnly = false;
				if (rcl.getAvsettings().equals("v")){
					isVideoOnly = true;
				}
				
				Long flvRecordingMetaDataId = this.flvRecordingMetaDataDaoImpl.addFlvRecordingMetaData(flvRecordingId, 
													rcl.getFirstname()+" "+rcl.getLastname(), now, 
																isAudioOnly, isVideoOnly, false, streamName, rcl.getInterviewPodId());
				
				//Start FLV recording
				recordShow(conn, String.valueOf(rcl.getBroadCastID()).toString(), streamName, flvRecordingMetaDataId, false, flvRecording.getIsInterview());
				
				rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
				
				this.clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
				
			} 
				
		} catch (Exception err) {
			log.error("[addRecordingByStreamId]",err);
		}	
	}
	
	public FlvRecording getFlvRecordingWithMetaData(String SID, Long flvRecordingId) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){	
	        	
	        	FlvRecording flvRecording = this.flvRecordingDaoImpl.getFlvRecordingById(flvRecordingId);
	        	
	        	flvRecording.setFlvRecordingMetaData(this.flvRecordingMetaDataDaoImpl.getFlvRecordingMetaDataByRecording(flvRecordingId));
	        	
	        	if (flvRecording.getInsertedBy() != null) {
	        		flvRecording.setCreator(this.usersDaoImpl.getUser(flvRecording.getInsertedBy()));
	        	}
	        	
	        	if (flvRecording.getRoom_id() != null) {
	        		flvRecording.setRoom(this.roommanagement.getRoomById(flvRecording.getRoom_id()));
	        	}
	        	
	        	flvRecording.setFlvRecordingLog(this.flvRecordingLogDaoImpl.getFLVRecordingLogByRecordingId(flvRecordingId));
	        	
	        	return flvRecording;
	        	
	        }
		} catch (Exception err){
			log.error("[getFlvRecordingWithMetaData] ",err);
			err.printStackTrace();
		}
		return null;
	}
	
	public List<FlvRecordingLog> getFlvRecordingLog(String SID, Long flvRecordingId) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){	
	        	
	        	return this.flvRecordingLogDaoImpl.getFLVRecordingLogByRecordingId(flvRecordingId);
	        	
	        }
		} catch (Exception err){
			log.error("[getFlvRecordingLog] ",err);
			err.printStackTrace();
		}
		return null;
	}
	
	public Long deleteFLVOrFolder(String SID, Long flvRecordingId) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  
	        
	        System.out.println("deleteFLVOrFolder "+flvRecordingId);
	        
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){	
	        	
	        	log.debug("deleteFLVOrFolder "+flvRecordingId);
	        	
	        	this.flvRecordingDaoImpl.deleteFlvRecording(flvRecordingId);
	        	
	        	return flvRecordingId;
	        }
		} catch (Exception err){
			log.error("[deleteFLVOrFolder] ",err);
		}
		return null;
	}
	

	public List<FlvRecording> getFLVExplorerByParent(String SID, Long parentFileExplorerItemId, Boolean isOwner) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){	
	        	
	        	log.debug("parentFileExplorerItemId "+parentFileExplorerItemId);
	        	
	        	if (parentFileExplorerItemId == 0) {
	        		if (isOwner) {
	        			
	        			return this.flvRecordingDaoImpl.getFlvRecordingByOwner(users_id, parentFileExplorerItemId);
	        			
	        		} else {
	        			
		        		return this.flvRecordingDaoImpl.getFlvRecordingsPublic();
		        		
	        		}
	        	} else {
	        		
	        		return this.flvRecordingDaoImpl.getFlvRecordingByParent(parentFileExplorerItemId);
	        	}
	        	
	        }
		} catch (Exception err){
			log.error("[getFLVExplorerByParent] ",err);
		}
		return null;
	}
	

	public Long addFolder(String SID, Long parentFileExplorerItemId, String fileName, 
							Boolean isOwner, Long organization_id) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){	
	        	
	        	log.debug("addFolder "+parentFileExplorerItemId);
	        	
	        	if (parentFileExplorerItemId == 0 && isOwner) {
	        		
	        		return this.flvRecordingDaoImpl.addFlvFolderRecording("", fileName, 
	        				null, //FileSize
	        				users_id, 
	        				null, null, null, //Long room_id, Date recordStart, Date recordEnd 
	        				users_id, //OwnerID => only set if its directly root in Owner Directory, other Folders and Files
							//maybe are also in a Home directory but just because their parent is
	        				"", parentFileExplorerItemId,
	        				organization_id);
	        		
	        	} else {
	        		
	        		return this.flvRecordingDaoImpl.addFlvFolderRecording("", fileName, 
	        				null, //FileSize
	        				users_id, 
	        				null, null, null, //Long room_id, Date recordStart, Date recordEnd 
	        				null, //OwnerID => only set if its directly root in Owner Directory, other Folders and Files
							//maybe are also in a Home directory but just because their parent is
	        				"", parentFileExplorerItemId,
	        				organization_id);
	        		
	        	}
	        		
	        }
		} catch (Exception err){
			log.error("[addFolder] ",err);
		}
		return null;
	}
	
	public Long moveFile(String SID, Long flvRecordingId, Long newParentFileExplorerItemId, 
			Boolean isOwner, Boolean moveToHome) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  
	        
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){	
	        	
	        	log.debug("moveFile "+flvRecordingId);
	        	
	        	this.flvRecordingDaoImpl.moveFile(flvRecordingId, newParentFileExplorerItemId, isOwner, users_id);
				
	        	FlvRecording flvRecording = this.flvRecordingDaoImpl.getFlvRecordingById(flvRecordingId);
	        	
	        	if (moveToHome) {
	        		//set this file and all subfiles and folders the ownerId
	        		this.setFileToOwnerOrRoomByParent(flvRecording, users_id);
	        		
	        	} else {
	        		//set this file and all subfiles and folders the room_id
	        		this.setFileToOwnerOrRoomByParent(flvRecording, null);
	        		
	        	}
	        }
		} catch (Exception err){
			log.error("[moveFile] ",err);
		}
		return null;
	}
	
	private void setFileToOwnerOrRoomByParent(FlvRecording flvRecording,
			Long usersId) {
		try {
			
			flvRecording.setOwnerId(usersId);
			
			this.flvRecordingDaoImpl.updateFlvRecording(flvRecording);
			
			List<FlvRecording> subFLVItems = this.flvRecordingDaoImpl.getFlvRecordingByParent(flvRecording.getFlvRecordingId());
			
			for (FlvRecording subFLVItem : subFLVItems) {
				this.setFileToOwnerOrRoomByParent(subFLVItem, usersId);
			}
			
		} catch (Exception err){
			log.error("[setFileToOwnerOrRoomByParent] ",err);
		}
	}

	public Long updateFileOrFolderName(String SID, Long flvRecordingId, String fileName) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){	
	        	
	        	log.debug("updateFileOrFolderName "+flvRecordingId);
	        	
	        	this.flvRecordingDaoImpl.updateFileOrFolderName(flvRecordingId, fileName);
	        	
	        	return flvRecordingId;
	        }
		} catch (Exception err){
			log.error("[updateFileOrFolderName] ",err);
		}
		return null;
	}
	
	public FLVRecorderObject getFLVExplorerByRoom(String SID, Long organization_id) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){	
	        	
	        	FLVRecorderObject fileExplorerObject = new FLVRecorderObject();
	        	
	        	//User Home Recordings
	        	List<FlvRecording> homeFlvRecordings = this.flvRecordingDaoImpl.getFlvRecordingRootByOwner(users_id);
	        	long homeFileSize = 0;
	        	
	        	for (FlvRecording homeFlvRecording : homeFlvRecordings) {
	        		homeFileSize += this.getSizeOfDirectoryAndSubs(homeFlvRecording);
	        	}
	        	
	        	fileExplorerObject.setUserHome(homeFlvRecordings);
	        	fileExplorerObject.setUserHomeSize(homeFileSize);
	        	
	        	//Public Recordings by Organization
	        	List<FlvRecording> publicFlvRecordings = this.flvRecordingDaoImpl.getFlvRecordingRootByPublic(organization_id);
	        	long publicFileSize = 0;
	        	
	        	for (FlvRecording publicFlvRecording : publicFlvRecordings) {
	        		publicFileSize += this.getSizeOfDirectoryAndSubs(publicFlvRecording);
	        	}
	        	fileExplorerObject.setRoomHome(publicFlvRecordings);
	        	fileExplorerObject.setRoomHomeSize(publicFileSize);
	        	
	        	return fileExplorerObject;
	        	
	        }
		} catch (Exception err){
			log.error("[getFileExplorerByRoom] ",err);
		}
		return null;
	}

	private long getSizeOfDirectoryAndSubs(FlvRecording baseFlvRecording) {
		try {
			
			long fileSize = 0;
			
			File tFile = new File (ScopeApplicationAdapter.webAppPath + File.separatorChar
					+ "streams" + File.separatorChar + "hibernate" + File.separatorChar
					+ baseFlvRecording.getFileHash());
			if (tFile.exists()) {
				fileSize += tFile.length();
			}
			
			File dFile = new File (ScopeApplicationAdapter.webAppPath + File.separatorChar
					+ "streams" + File.separatorChar + "hibernate" + File.separatorChar
					+ baseFlvRecording.getAlternateDownload());
			if (dFile.exists()) {
				fileSize += dFile.length();
			}
			
			File iFile = new File (ScopeApplicationAdapter.webAppPath + File.separatorChar
					+ "streams" + File.separatorChar + "hibernate" + File.separatorChar
					+ baseFlvRecording.getPreviewImage());
			if (iFile.exists()) {
				fileSize += iFile.length();
			}
			
			List<FlvRecording> flvRecordings = this.flvRecordingDaoImpl.getFlvRecordingByParent(baseFlvRecording.getFlvRecordingId());
			
			for (FlvRecording flvRecording : flvRecordings) {
				fileSize += this.getSizeOfDirectoryAndSubs(flvRecording);
			}
			
			
			return fileSize;
			
		} catch (Exception err){
			log.error("[getSizeOfDirectoryAndSubs] ",err);
		}
		return 0;
	}
	
	public Long restartInterviewConversion(String SID, Long flvRecordingId, Integer leftSideLoud, Integer rightSideLoud,
			Integer leftSideTime, Integer rightSideTime) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){	
	        	
	        	log.debug("updateFileOrFolderName "+flvRecordingId);
	        	
	        	FlvRecording flvRecording = this.flvRecordingDaoImpl.getFlvRecordingById(flvRecordingId);
	        	
	        	if (flvRecording.getIsInterview() == null || !flvRecording.getIsInterview()) {
	        		return -1L;
	        	}
	        	
	        	flvRecording.setPreviewImage(null);
	        	
	        	flvRecording.setProgressPostProcessing(0);
	        	
	        	this.flvRecordingDaoImpl.updateFlvRecording(flvRecording);
	        	
	        	this.flvInterviewReConverterTask.startConversionThread(flvRecordingId, leftSideLoud, rightSideLoud, leftSideTime, rightSideTime);
	        	
	        }
		} catch (Exception err){
			log.error("[restartInterviewConversion] ",err);
		}
		return null;
	}
	
}
