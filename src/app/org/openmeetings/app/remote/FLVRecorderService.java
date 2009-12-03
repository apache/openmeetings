package org.openmeetings.app.remote;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.openmeetings.app.data.flvrecord.FlvRecordingDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.data.record.dao.ChatvaluesEventDaoImpl;
import org.openmeetings.app.data.record.dao.RecordingClientDaoImpl;
import org.openmeetings.app.data.record.dao.RecordingDaoImpl;
import org.openmeetings.app.data.record.dao.RoomRecordingDaoImpl;
import org.openmeetings.app.data.record.dao.RoomStreamDaoImpl;
import org.openmeetings.app.data.record.dao.WhiteBoardEventDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.recording.ChatvaluesEvent;
import org.openmeetings.app.hibernate.beans.recording.RecordingClient;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.recording.RoomRecording;
import org.openmeetings.app.hibernate.beans.recording.RoomStream;
import org.openmeetings.app.hibernate.beans.recording.WhiteBoardEvent;
import org.openmeetings.app.hibernate.beans.user.Users;
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
import org.red5.server.stream.ClientBroadcastStream;
import org.slf4j.Logger;


public class FLVRecorderService implements IPendingServiceCallback {
	
	private static final Logger log = Red5LoggerFactory.getLogger(FLVRecorderService.class, "openmeetings");

	//Spring Beans
	private ClientListManager clientListManager = null;
	private FlvRecordingDaoImpl flvRecordingDaoImpl = null;
	private FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDaoImpl = null;
	
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
	
	private static String generateFileName(String streamid) throws Exception{
		String dateString = CalendarPatterns.getTimeForStreamId(new java.util.Date());
		return streamid+"_"+dateString;
		
	}
	
	public String recordMeetingStream(String roomRecordingName, String comment){
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();

			Date now = new Date();
			
			//Receive flvRecordingId
			Long flvRecordingId = this.flvRecordingDaoImpl.addFlvRecording("", roomRecordingName, null, currentClient.getUser_id(),
									room_id, now, null, currentClient.getUser_id(), comment, currentClient.getStreamid());
			
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
							if (!rcl.getIsScreenClient()) {
								((IServiceCapableConnection) conn).invoke("startedRecording",new Object[] { currentClient }, this);
							}
							
							//If its the recording client we need another type of Meta Data
							if (rcl.getIsScreenClient()) {
							
								if (rcl.getFlvRecordingId() != null && rcl.getScreenPublishStarted() != null && rcl.getScreenPublishStarted()) {
									
									String streamName_Screen = generateFileName(rcl.getStreamPublishName().toString());
									
									//Start FLV Recording
									recordShow(conn, rcl.getStreamPublishName(), streamName_Screen);
									
									Long flvRecordingMetaDataId = this.flvRecordingMetaDataDaoImpl.addFlvRecordingMetaData(flvRecordingId, 
																			rcl.getFirstname()+" "+rcl.getLastname(), null, now, 
																						false, false, true, streamName_Screen);
									
									//Add Meta Data
									rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
									
									this.clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
								
								}
								
							} else if 
							//if the user does publish av, a, v
							//But we only record av or a, video only is not interesting
							(rcl.getAvsettings().equals("av") || 
									rcl.getAvsettings().equals("a")){	
								
								String streamName = generateFileName(String.valueOf(rcl.getBroadCastID()).toString());
								
								//Start FLV recording
								recordShow(conn, String.valueOf(rcl.getBroadCastID()).toString(), streamName);
								
								//Add Meta Data
								boolean isAudioOnly = false;
								if (rcl.getAvsettings().equals("a")){
									isAudioOnly = true;
								}
								
								Long flvRecordingMetaDataId = this.flvRecordingMetaDataDaoImpl.addFlvRecordingMetaData(flvRecordingId, 
																	rcl.getFirstname()+" "+rcl.getLastname(), null, now, 
																				isAudioOnly, false, false, streamName);
								
								rcl.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
								
								this.clientListManager.updateClientByStreamId(rcl.getStreamid(), rcl);
								
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
	 * Start recording the publishing stream for the specified
	 *
	 * @param conn
	 */
	private static void recordShow(IConnection conn, String broadcastid, String streamName) throws Exception {
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
	 * Stops recording the publishing stream for the specified
	 * IConnection.
	 *
	 * @param conn
	 */
	public static void stopRecordingShow(IConnection conn, String broadcastId) throws Exception {
		log.debug("** stopRecordingShow: "+conn);
		log.debug("### Stop recording show for broadcastId: "+ broadcastId + " || " + conn.getScope().getContextPath());
		ClientBroadcastStream stream = (ClientBroadcastStream) ScopeApplicationAdapter.getInstance().
												getBroadcastStream(conn.getScope(), broadcastId);
		// Stop recording.
		stream.stopRecording();
	}
	
	public Long stopRecordAndSave(IScope scope, String roomrecordingName, RoomClient currentClient){
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
									stopRecordingShow(conn, rcl.getStreamPublishName());
									
									//Update Meta Data
									this.flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(),new Date());
								}
								
							} else if (rcl.getAvsettings().equals("av") || 
									rcl.getAvsettings().equals("a")){	
								
								stopRecordingShow(conn, String.valueOf(rcl.getBroadCastID()).toString() );
								
								//Update Meta Data
								this.flvRecordingMetaDataDaoImpl.updateFlvRecordingMetaDataEndDate(rcl.getFlvRecordingMetaDataId(),new Date());
								
							}
							
						}
					}
				}
			}				
			
			//Store to database
			Long flvRecordingId = currentClient.getFlvRecordingId();
			
			this.flvRecordingDaoImpl.updateFlvRecordingEndTime(flvRecordingId, new Date());
			
			//Reset values
			currentClient.setFlvRecordingId(null);
			
			this.clientListManager.updateClientByStreamId(currentClient.getStreamid(), currentClient);
			
		} catch (Exception err) {
			log.error("[stopRecordAndSave]",err);
		}
		return new Long(-1);
	}
	
}
