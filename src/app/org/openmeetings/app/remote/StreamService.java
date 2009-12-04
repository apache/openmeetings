package org.openmeetings.app.remote;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.record.dao.ChatvaluesEventDaoImpl;
import org.openmeetings.app.data.record.dao.RecordingClientDaoImpl;
import org.openmeetings.app.data.record.dao.RecordingConversionJobDaoImpl;
import org.openmeetings.app.data.record.dao.RecordingDaoImpl;
import org.openmeetings.app.data.record.dao.RoomRecordingDaoImpl;
import org.openmeetings.app.data.record.dao.RoomStreamDaoImpl;
import org.openmeetings.app.data.record.dao.WhiteBoardEventDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.domain.Organisation_Users;
import org.openmeetings.app.hibernate.beans.recording.ChatvaluesEvent;
import org.openmeetings.app.hibernate.beans.recording.Recording;
import org.openmeetings.app.hibernate.beans.recording.RecordingClient;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.recording.RoomRecording;
import org.openmeetings.app.hibernate.beans.recording.RoomStream;
import org.openmeetings.app.hibernate.beans.recording.WhiteBoardEvent;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.rooms.Rooms_Organisation;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.stream.ClientBroadcastStream;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * 
 * @author sebastianwagner
 *
 */
public class StreamService implements IPendingServiceCallback {
	
	public static String fileNameXML = "recording_";
	public static String folderForRecordings = "recorded";
	
	//Beans, see red5-web.xml
	private ClientListManager clientListManager = null;
	
	private static final Logger log = Red5LoggerFactory.getLogger(StreamService.class, "openmeetings");
	
	private static LinkedHashMap<String,RoomRecording> roomRecordingList = new LinkedHashMap<String,RoomRecording>();
	
	public ClientListManager getClientListManager() {
		return clientListManager;
	}
	public void setClientListManager(ClientListManager clientListManager) {
		this.clientListManager = clientListManager;
	}

	/**
	 * this function starts recording a Conference (Meeting or Event)
	 * 
	 * the temp-value contains an attribute called *streamlist*
	 * the streamlist all room-streaming events are inside
	 * avset means this is an streaming event where only the audio-video settings have been altered
	 * 
	 * Meaning of the boolean Values in the *streamlist* :
	 * streamstart = true && avset = false => an initial user (a user which has been already in the room when you started the recorder-session) 
	 * streamstart = true && avset = true => new user did start streaming
	 * streamstart = false && avset =  true => new user did has chosen his av/settings, if he has chosen *n* 
	 * 									(rcl.avsettings == "n") then he will not send any streamstart event cause he
	 * 									does not stream audio/video => show the picture of the user
	 * streamstart = false && avset = false => user did finished streaming
	 * 									if the user has rcl.avsettings != "n" stop playing the stream
	 * 
	 * 
	 * Meaning of the boolean Values in the *roomclients* :
	 * roomenter =  true => a new user OR initial user
	 * roomenter = false => a user has left the Conference 
	 * 
	 * @param conferenceType
	 * @param initwhiteboardvars
	 * @param roomRecordingsTableString
	 * @param comment
	 * @return
	 */
	public String recordMeetingStream(String conferenceType, Object initwhiteboardvars, 
			String roomRecordingsTableString, String comment){
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();

			String recordingName = generateFileName(Long.valueOf(currentClient.getBroadCastID()).toString());
			currentClient.setIsRecording(true);
			currentClient.setRoomRecordingName(recordingName);			
			this.clientListManager.updateClientByStreamId(current.getClient().getId(), currentClient);
			
			Date now = new Date();
			
			RoomRecording roomRecording = new RoomRecording();
			roomRecording.setConferenceType(conferenceType);
			roomRecording.setRoom_setup(Roommanagement.getInstance().getRoomById(currentClient.getRoom_id()));
			roomRecording.setRoomRecordingsTableString(roomRecordingsTableString);
			roomRecording.setStarttime(now);
			roomRecording.setComment(comment);
			
			//get all stream and start recording them
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							log.error("is this users still alive? :"+rcl);
							//Check if the Client is in the same room and same domain 
							if(room_id.equals(rcl.getRoom_id()) && room_id!=null){
								
								((IServiceCapableConnection) conn).invoke("startedRecording",new Object[] { currentClient }, this);
								
								String remoteAdress = conn.getRemoteAddress();
								Date startDate = new Date();
								
								//add streamings to record File
								if (!conferenceType.equals("audience") || rcl.getIsMod()){
									
									RoomStream roomStream = new RoomStream();
									
									String streamName = generateFileName(Long.valueOf(rcl.getBroadCastID()).toString());
									
									//if the user does publish av, a, v
									if (!rcl.getAvsettings().equals("n")){	
										recordShow(conn, rcl.getBroadCastID(), streamName);
									} 
									
									roomStream.setStreamName(streamName);
									//stream starting
									roomStream.setStreamstart(true);
									//is not only a avset-event
									roomStream.setAvset(false);
									roomStream.setRemoteAdress(remoteAdress);
									roomStream.setStartdate(startDate);
									roomStream.setStarttime(0L);
									roomStream.setRcl(rcl);
									
									if (roomRecording.getRoomStreams() == null) {
										roomRecording.setRoomStreams(new LinkedList<RoomStream>());
									}
									roomRecording.getRoomStreams().add(roomStream);
								} 
		
								//add room Clients enter/leave Events to record File
								RecordingClient roomClient = new RecordingClient();
								roomClient.setRemoteAdress(remoteAdress);
								roomClient.setRoomenter(true);
								roomClient.setStartdate(startDate);
								roomClient.setStarttime(0L);
								roomClient.setRcl(rcl);
								if (roomRecording.getRoomClients() == null) {
									roomRecording.setRoomClients(new LinkedList<RecordingClient>());
								}
								roomRecording.getRoomClients().add(roomClient);
							}
						}
					}
				}		
			}
			
			roomRecording.setInitwhiteboardvars(initwhiteboardvars);
			XStream xStream_temp = new XStream(new XppDriver());
			xStream_temp.setMode(XStream.NO_REFERENCES);
			roomRecording.setInitwhiteboardvarsInXml(xStream_temp.toXML(initwhiteboardvars));
			
			roomRecording.setRecordingName(recordingName);
			roomRecording.setStarttime(new java.util.Date());
			
			roomRecording.setStartedby(currentClient);
			
			//add Room Client enter/leave events
			//moved inside function - loop
			
			//add Stream-Events
			//moved inside function - loop
			
			//add Whiteboard-Events
			roomRecording.setWhiteboard(new LinkedList<WhiteBoardEvent>());
			
			//add Chat-Events
			roomRecording.setChatvalues(new LinkedList<ChatvaluesEvent>());
			
			roomRecordingList.put(recordingName, roomRecording);
			
			return recordingName;
		} catch (Exception err) {
			log.error("[recordMeetingStream]",err);
		}
		return null;
	}
	
	public Long _stopRecordMeetingStream(String roomrecordingName){
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());

			RoomRecording roomRecording = roomRecordingList.get(roomrecordingName);
			Long room_id = currentClient.getRoom_id();	

			String conferenceType = roomRecording.getConferenceType();
			
			//get all stream and stop recording them
			//Todo: Check that nobody does Recording at the same time Issue 253
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							log.debug("is this users still alive? :"+rcl);
							//Check if the Client is in the same room and same domain 
							if(room_id.equals(rcl.getRoom_id()) && room_id!=null){
								((IServiceCapableConnection) conn).invoke("stopedRecording",new Object[] { currentClient }, this);
							}
						}
					}		
				}
			}
			
			return _stopRecordAndSave(current.getScope(), roomrecordingName, currentClient);
		} catch (Exception err) {
			log.error("[stopRecordAndSave]",err);
		}
		return new Long(-1);
	}
	
	public static Long _stopRecordAndSave(IScope scope, String roomrecordingName, RoomClient currentClient){
		try {
			log.debug("stopRecordAndSave "+currentClient.getUsername()+","+currentClient.getUserip());
			RoomRecording roomRecording = roomRecordingList.get(roomrecordingName);
			
			Long room_id = currentClient.getRoom_id();
			currentClient.setIsRecording(false);
			currentClient.setRoomRecordingName("");
			ClientListManager.getInstance().updateClientByStreamId(currentClient.getStreamid(), currentClient);
			
			
			String conferenceType = roomRecording.getConferenceType();
			
			//get all stream and stop recording them
			//Todo: Check that nobody does Recording at the same time Issue 253
			Collection<Set<IConnection>> conCollection = scope.getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							
							RoomClient rcl = ClientListManager.getInstance().getClientByStreamId(conn.getClient().getId());
							log.debug("is this users still alive? :"+rcl);
							//Check if the Client is in the same room and same domain 
							if(room_id.equals(rcl.getRoom_id()) && room_id!=null){
								if (!conferenceType.equals("audience") || rcl.getIsMod()){
									//stop the recorded flv and add the event to the notifications
									log.debug("*** sendClientBroadcastNotifications Any Client is Recording - stop that");
									StreamService._addRoomClientEnterEventFunc(rcl, roomrecordingName, rcl.getUserip(), false);
									_stopRecordingShowForClient(conn, rcl, roomrecordingName, true);
								}
							}
							
						}
					}
				}
			}				
			
			
			String newRecordFileName = roomRecording.getRoomRecordingsTableString();
			String comment = roomRecording.getComment();
			
			Date starttime = roomRecording.getStarttime();
			Date endtime =  new java.util.Date();
			Long duration = endtime.getTime() - starttime.getTime();
			roomRecording.setEndtime(endtime);
			
			roomRecording.setEnduser(currentClient);
			
			roomRecording.setRecordname(newRecordFileName);
			
			RoomClient startedClient = roomRecording.getStartedby();
			Long recordedby = startedClient.getUser_id();
			Users us = null;
			if (recordedby!=null && recordedby>0){
				us = UsersDaoImpl.getInstance().getUser(recordedby);
			}
			
			
//			//TODO: Replace Persistence Logic with Database-DAO
//			XStream xStream = new XStream(new XppDriver());
//			xStream.setMode(XStream.NO_REFERENCES);
//			String xmlString = xStream.toXML(roomRecording);
//			
//			//log.error(xmlString);
//			
//			//make persistent
//			Long recording_id = RecordingDaoImpl.getInstance().addRecording(newRecordFileName, duration, "", currentClient.getRoom_id(), us, comment, null);
//			
//			//save XML to Disk
//			IScope scope = Red5.getConnectionLocal().getScope().getParent();
//			String current_dir = scope.getResource("upload/").getFile().getAbsolutePath();
//			//System.out.println(current_dir  + File.separatorChar + this.folderForRecordings);
//			File f = new File(current_dir  + File.separatorChar + folderForRecordings);
//			if (!f.exists()){
//				f.mkdir();
//			}
//			String fileName = f.getAbsolutePath() + File.separatorChar + fileNameXML+recording_id+".xml";
//			//System.out.println("fileName"+fileName);
//			PrintWriter pw = new PrintWriter(new FileWriter(fileName));
//		    pw.println(xmlString);
//		    pw.flush();
//		    pw.close();
//		    
//		    List<LinkedHashMap<String,Object>> roomClients = (List<LinkedHashMap<String,Object>>)roomRecording.get("roomclients");
//		    for (Iterator<LinkedHashMap<String,Object>> iter = roomClients.iterator();iter.hasNext();){
//		    	LinkedHashMap<String,Object> roomClient = iter.next();
//		    	
//		    	log.debug("###### NEW RRR1: "+roomClient.get("roomenter"));
//				log.debug("###### NEW RRR2: "+roomClient.get("startdate"));
//				log.debug("###### NEW RRR3: "+roomClient.get("starttime"));
//				RoomClient rcl = (RoomClient) roomClient.get("rcl");
//				log.debug("###### NEW RRR4: "+rcl.getBroadCastID());
//				log.debug("###### NEW RRR5: "+rcl.getConnectedSince());
//				log.debug("###### NEW RRR6: "+rcl.getPublicSID());
//				log.debug("###### NEW RRR7: "+rcl.getUserip());   
//				
//		    }
		    
			//remove recording from Temp - List
			roomRecordingList.remove(roomrecordingName);
			
			
			//Store to database
			
			//First Store the RoomRecording
			Long roomRecordingId = RoomRecordingDaoImpl.getInstance().addRoomRecording(roomRecording);
			RoomRecording roomRecordingRemote = RoomRecordingDaoImpl.getInstance().getRoomRecordingById(roomRecordingId);
			
			//Store all RoomRecordingClients
			for (Iterator<RecordingClient> iter = roomRecording.getRoomClients().iterator();iter.hasNext();) {
				RecordingClient recordingClient = iter.next();
				recordingClient.setRoomRecordingId(roomRecordingId);
				RecordingClientDaoImpl.getInstance().addRecordingClient(recordingClient);
			}
			
			//Store all Whiteboard Events
			for (WhiteBoardEvent whiteBoardEvent : roomRecording.getWhiteboard()) {
				whiteBoardEvent.setRoomRecording(roomRecordingRemote);
				WhiteBoardEventDaoImpl.getInstance().addWhiteBoardEvent(whiteBoardEvent);
			}
			
			//Store all Chatbox Events
			for (ChatvaluesEvent chatvaluesEvent : roomRecording.getChatvalues()) {
				chatvaluesEvent.setRoomRecording(roomRecordingRemote);
				ChatvaluesEventDaoImpl.getInstance().addChatvaluesEvent(chatvaluesEvent);
			}
			
			//Store all Stream Events
			for (RoomStream roomStream : roomRecording.getRoomStreams()) {
				roomStream.setRoomRecording(roomRecordingRemote);
				RoomStreamDaoImpl.getInstance().addRoomStream(roomStream);
			}
			
			//roomRecording.getRoomStreams()
			
			//make persistent
			Long recording_id_remote = RecordingDaoImpl.getInstance().addRecording(newRecordFileName, duration, "", currentClient.getRoom_id(), us, comment, roomRecordingRemote);
			
			
			return recording_id_remote;
		} catch (Exception err) {
			log.error("[stopRecordAndSave]",err);
		}
		return new Long(-1);
	}	

	/**
	 * Start recording the publishing stream for the specified
	 *
	 * @param conn
	 */
	private static void recordShow(IConnection conn, long broadcastid, String streamName) throws Exception {
		log.debug("Recording show for: " + conn.getScope().getContextPath());
		log.debug("Name of CLient and Stream to be recorded: "+broadcastid);		
		//log.debug("Application.getInstance()"+Application.getInstance());
		log.debug("Scope "+conn);
		log.debug("Scope "+conn.getScope());
		// Get a reference to the current broadcast stream.
		ClientBroadcastStream stream = (ClientBroadcastStream) ScopeApplicationAdapter.getInstance()
				.getBroadcastStream(conn.getScope(), Long.valueOf(broadcastid).toString());
		try {
			// Save the stream to disk.
			stream.saveAs(streamName, false);
		} catch (Exception e) {
			log.error("Error while saving stream: " + streamName, e);
		}
	}
	
	/**
	 * if doStopStream = false this use is already away, no connection can be stoped
	 * @param conn
	 * @param rcl
	 * @param roomrecordingName
	 * @param doStopStream
	 */
	public static void _stopRecordingShowForClient(IConnection conn, RoomClient rcl, 
			String roomrecordingName, boolean doStopStream) {
		try {
			//this cannot be handled here, as to stop a stream and to leave a room is not
			//the same type of event.
			//StreamService.addRoomClientEnterEventFunc(rcl, roomrecordingName, rcl.getUserip(), false);
			log.error("### stopRecordingShowForClient: "+rcl.getIsRecording()+","+rcl.getUsername()+","+rcl.getUserip());
			
			RoomRecording roomRecording = roomRecordingList.get(roomrecordingName);
			Date recordingsStartTime = roomRecording.getStarttime();
			Date currentDate = new Date();
			
			RoomStream roomStream = new RoomStream();
			roomStream.setStreamName("");
			roomStream.setStreamstart(false);
			roomStream.setAvset(false);
			roomStream.setRemoteAdress(conn.getRemoteAddress());
			roomStream.setStartdate(currentDate);
			roomStream.setStarttime(currentDate.getTime()-recordingsStartTime.getTime());
			roomStream.setRcl(rcl);
			
			roomRecording.getRoomStreams().add(roomStream);
			roomRecordingList.put(roomrecordingName, roomRecording);

			if ((rcl.getAvsettings().equals("a") || rcl.getAvsettings().equals("v") 
					|| rcl.getAvsettings().equals("av")) && doStopStream){
				stopRecordingShow(conn,rcl.getBroadCastID());
			}
			
		} catch (Exception err) {
			log.error("[stopRecordingShowForClient]",err);
		}
	}

	/**
	 * Stops recording the publishing stream for the specified
	 * IConnection.
	 *
	 * @param conn
	 */
	public static void stopRecordingShow(IConnection conn, long broadcastId) throws Exception {
		log.debug("** stopRecordingShow: "+conn);
		log.debug("### Stop recording show for broadcastId: "+ broadcastId + " || " + conn.getScope().getContextPath());
		ClientBroadcastStream stream = (ClientBroadcastStream) ScopeApplicationAdapter.getInstance().
												getBroadcastStream(conn.getScope(), Long.valueOf(broadcastId).toString());
		// Stop recording.
		stream.stopRecording();
	}
	
	public static String generateFileName(String streamid) throws Exception{
		String dateString = CalendarPatterns.getTimeForStreamId(new java.util.Date());
		return streamid+"_"+dateString;
		
	}
	
	public List<Recording> getAllRecordingsForUser(String SID){
		try {
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  

	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
	        	
	        	String whereClause = "";
	        	
	        	int i = 0;
	        	List<Rooms> rooms = Roommanagement.getInstance().getPublicRooms(user_level);
	        	for (Iterator<Rooms> iter = rooms.iterator();iter.hasNext();){
	        		Rooms room = iter.next();
	        		if (i==0) whereClause += " (";
	        		else whereClause += " OR";
	        		whereClause += " c.rooms.rooms_id = "+room.getRooms_id()+" ";
	        		i++;
	        	}
				
				Users us = UsersDaoImpl.getInstance().getUser(users_id);
				
				for (Iterator<Organisation_Users> iter = us.getOrganisation_users().iterator();iter.hasNext();) {
					Organisation_Users orgUser = iter.next();
					Long organisation_id = orgUser.getOrganisation().getOrganisation_id();
					
					List<Rooms_Organisation> rOrgList = Roommanagement.getInstance().getRoomsOrganisationByOrganisationId(3, organisation_id);
					for (Iterator<Rooms_Organisation> iterOrgList = rOrgList.iterator();iterOrgList.hasNext();){
						Rooms_Organisation rOrg = iterOrgList.next();
		        		if (i==0) whereClause += " (";
		        		else whereClause += " OR";						
						whereClause += " c.rooms.rooms_id = "+rOrg.getRoom().getRooms_id()+" ";
						i++;
					}
					
				}
				if (whereClause.length()!=0) whereClause += ") AND ";
				List<Recording> rList = RecordingDaoImpl.getInstance().getRecordingsByWhereClause(whereClause);
				
				for (Iterator<Recording> iter = rList.iterator();iter.hasNext();) {
					Recording rec = iter.next();
					rec.setStarttimeAsString(CalendarPatterns.getDateWithTimeByMiliSeconds(rec.getStarttime()));
				}
				
				return rList;
	        }

			
			
		} catch (Exception err) {
			log.error("[getAllRecordings]",err);
		}		
		return null;
	}
	
	public Recording getRecordingById(String SID, Long recording_id) {
		try {
			
	        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);  

	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
	        	Recording rec = RecordingDaoImpl.getInstance().getRecordingById(recording_id);
	        	
	        	rec.getRoomRecording().setRoomClients(
	        			RecordingClientDaoImpl.getInstance().getRecordingClientByroomRecordingId(
	        					rec.getRoomRecording().getRoomrecordingId()));
	        	
	        	rec.getRoomRecording().setChatvalues(
	        			ChatvaluesEventDaoImpl.getInstance().getChatvaluesEventByRoomRecordingId(
	        						rec.getRoomRecording().getRoomrecordingId()));
	        	
	        	rec.getRoomRecording().setRoomStreams(
	        			RoomStreamDaoImpl.getInstance().getRoomStreamsByRoomRecordingId(
	        					rec.getRoomRecording().getRoomrecordingId()));
	        	
				//Set Conversion Job Record
	        	rec.setRecordingConversionJob(RecordingConversionJobDaoImpl.getInstance().
	        			getRecordingConversionJobsByRecording(rec.getRecording_id()));
	        	
				return rec;
	        }
	        
		} catch (Exception err) {
			log.error("[getRecordingById]",err);
		}		
		return null;
	}
	
	public static void _addRecordingByStreamId(IConnection conn, String streamId, 
			RoomClient rcl, String roomrecordingName) {
		try {
			RoomRecording roomRecording = roomRecordingList.get(roomrecordingName);
			String conferenceType = roomRecording.getConferenceType();
			
			//log.debug("addRecordingByStreamId "+conferenceType+" MOD: "+rcl.getIsMod());
			
			if (!conferenceType.equals("audience") || rcl.getIsMod()){
				
				//log.debug("AV-Settings: "+rcl.getAvsettings());
				
				Date recordingsStartTime = roomRecording.getStarttime();
				Date currentDate = new Date();
				
				
				
				String streamName = generateFileName(Long.valueOf(rcl.getBroadCastID()).toString());
				String remoteAdress = conn.getRemoteAddress();
				
				//if the user does publish av, a, v
				if (!rcl.getAvsettings().equals("n")){
					//log.debug("Record Show");
					recordShow(conn, rcl.getBroadCastID(), streamName);
				}
				
				RoomStream roomStream = new RoomStream();
				roomStream.setStreamName(streamName);
				//this is a recording event
				roomStream.setStreamstart(true);
				//this is not an av event
				roomStream.setAvset(true);
				roomStream.setRemoteAdress(remoteAdress);
				roomStream.setStartdate(new java.util.Date());
				roomStream.setStarttime(currentDate.getTime()-recordingsStartTime.getTime());
				roomStream.setRcl(rcl);
				
				roomRecording.getRoomStreams().add(roomStream);
				roomRecordingList.put(roomrecordingName, roomRecording);
			}
		} catch (Exception err) {
			log.error("[addRecordingByStreamId]",err);
		}	
	}
	
	public static void addWhiteBoardEvent(String roomrecordingName,Object vars) {
		try {
			
			log.debug("addWhiteBoardEvent roomrecordingName: "+roomrecordingName);
			RoomRecording roomRecording = roomRecordingList.get(roomrecordingName);
			
			Date recordingsStartTime = roomRecording.getStarttime();
			Date currentDate = new Date();
			
			XStream xStream = new XStream(new XppDriver());
			xStream.setMode(XStream.NO_REFERENCES);
			String action = xStream.toXML(vars);
			
			WhiteBoardEvent whiteBoardEvent = new WhiteBoardEvent();
			whiteBoardEvent.setStarttime(currentDate.getTime()-recordingsStartTime.getTime());
			whiteBoardEvent.setAction(action);
			
			roomRecording.getWhiteboard().add(whiteBoardEvent);
			
			roomRecordingList.put(roomrecordingName, roomRecording);
			
		} catch (Exception err) {
			log.error("[addWhiteBoardEvent]",err);
		}	
	}
	
	public static void addChatEvent(String roomrecordingName,Object vars) {
		try {
			
			log.debug("addChatEvent roomrecordingName: "+roomrecordingName);
			RoomRecording roomRecording = roomRecordingList.get(roomrecordingName);
			
			Date recordingsStartTime = roomRecording.getStarttime();
			Date currentDate = new Date();
			
			XStream xStream = new XStream(new XppDriver());
			xStream.setMode(XStream.NO_REFERENCES);
			String action = xStream.toXML(vars);
			
			ChatvaluesEvent chatvaluesEvent = new ChatvaluesEvent();
			chatvaluesEvent.setStarttime(currentDate.getTime()-recordingsStartTime.getTime());
			chatvaluesEvent.setAction(action);
			
			roomRecording.getChatvalues().add(chatvaluesEvent);
			
			roomRecordingList.put(roomrecordingName, roomRecording);
			
		} catch (Exception err) {
			log.error("[addChatEvent]",err);
		}	
	}
	
	
	public static void _addRoomClientAVSetEvent(RoomClient rcl, String roomrecordingName, 
			String remoteAdress) {
		try {
			RoomRecording roomRecording = roomRecordingList.get(roomrecordingName);
			Date recordingsStartTime = roomRecording.getStarttime();
			Date currentDate = new Date();
			List<RoomStream> roomStreams = roomRecording.getRoomStreams();
			
			RoomStream roomStream = new RoomStream();
			roomStream.setStreamName("");
			roomStream.setStreamstart(false);
			roomStream.setAvset(true);
			roomStream.setRemoteAdress(remoteAdress);
			roomStream.setStartdate(currentDate);
			roomStream.setStarttime(currentDate.getTime()-recordingsStartTime.getTime());
			roomStream.setRcl(rcl);
			
			roomRecording.getRoomStreams().add(roomStream);
			roomRecordingList.put(roomrecordingName, roomRecording);			
		} catch (Exception err) {
			log.error("[addRoomClientAVSetEvent]",err);
		}	
	}	
	
	public static void _addRoomClientEnterEventFunc(RoomClient rcl, String roomrecordingName, 
				String remoteAdress, boolean enter) {
		try {
			RoomRecording roomRecording = roomRecordingList.get(roomrecordingName);
			Date recordingsStartTime = roomRecording.getStarttime();
			Date currentDate = new Date();
			
			RecordingClient roomClient = new RecordingClient();						
			roomClient.setRemoteAdress(remoteAdress);
			roomClient.setRoomenter(enter);
			roomClient.setStartdate(currentDate);
			roomClient.setStarttime(currentDate.getTime()-recordingsStartTime.getTime());
//			
//			if (!enter) {
//				log.debug("###### NEW USER1: "+rcl.getBroadCastID());
//				log.debug("###### NEW USER2: "+rcl.getConnectedSince());
//				log.debug("###### NEW USER3: "+rcl.getPublicSID());
//				log.debug("###### NEW USER4: "+rcl.getUserip());
//			}
			roomClient.setRcl(rcl);
			
			roomRecording.getRoomClients().add(roomClient);
			roomRecordingList.put(roomrecordingName, roomRecording);			
		} catch (Exception err) {
			log.error("[addRoomClientEnterEvent]",err);
		}	
	}	

	public Long _clientCancelRecording(String roomrecordingName){
		try {
			
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();
			currentClient.setIsRecording(false);
			currentClient.setRoomRecordingName("");
			this.clientListManager.updateClientByStreamId(current.getClient().getId(), currentClient);
			
			RoomRecording roomRecording = roomRecordingList.get(roomrecordingName);
			String conferenceType = roomRecording.getConferenceType();
			
			//get all stream and stop recording them
			//Todo: Check that nobody does Recording at the same time Issue 253
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection conn : conset) {
					if (conn != null) {
						if (conn instanceof IServiceCapableConnection) {
							RoomClient rcl = this.clientListManager.getClientByStreamId(conn.getClient().getId());
							log.debug("is this users still alive? :"+rcl);
							//Check if the Client is in the same room and same domain 
							if(room_id.equals(rcl.getRoom_id()) && room_id!=null){
								((IServiceCapableConnection) conn).invoke("stopedRecording",new Object[] { currentClient }, this);
								if (!conferenceType.equals("audience") || rcl.getIsMod()){
									//if the user does publish av, a, v
									if (!rcl.getAvsettings().equals("n")){
										stopRecordingShow(conn,rcl.getBroadCastID());
									}
								}
							}
						}
					}
				}
			}
			
			roomRecordingList.remove(roomrecordingName);
			
			return new Long(1);
		} catch (Exception err) {
			log.error("[cancelRecording]",err);
		}
		return new Long(-1);
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	public RoomClient checkForRecording(){
		try {
			IConnection current = Red5.getConnectionLocal();
			RoomClient currentClient = this.clientListManager.getClientByStreamId(current.getClient().getId());
			Long room_id = currentClient.getRoom_id();
			
			//Check if any client in the same room is recording at the moment
			
			Collection<Set<IConnection>> conCollection = current.getScope().getConnections();
			for (Set<IConnection> conset : conCollection) {
				for (IConnection cons : conset) {
					if (cons != null) {
						if (cons instanceof IServiceCapableConnection) {
							if (!cons.equals(current)){
								log.debug("sending roomDisconnect to " + cons);
								RoomClient rcl = this.clientListManager.getClientByStreamId(cons.getClient().getId());
								//Check if the Client is in the same room and same domain except its the current one
								if(room_id.equals(rcl.getRoom_id()) && room_id!=null){					
									if (rcl.getIsRecording()){
										return rcl;
									}
								}
							}
						}
					}		
				}
			}
			
		} catch (Exception err) {
			log.error("[cancelRecording]",err);
		}
		return null;
	}

	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
		log.debug("resultReceived"+arg0);
	}
	
	public Long deleteRecordedFile(String SID, Long recording_id){
		try {
	    	Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	    	Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	    	if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
	    		Recording rec = RecordingDaoImpl.getInstance().getRecordingById(recording_id);
	    		if (rec!=null) {
	    			rec.setDeleted("true");
	    			RecordingDaoImpl.getInstance().updateRecording(rec);
	    			return new Long(recording_id);
	    		}
	    	}
		} catch (Exception err) {
			log.error("[deleteRecordedFile]",err);
		}
		return new Long(-1);
	}
	
}
