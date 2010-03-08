package org.openmeetings.axis.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.rooms.RoomsList;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.conference.Invitationmanagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.flvrecord.FlvRecordingDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecording;
import org.openmeetings.app.hibernate.beans.invitation.Invitations;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.rooms.RoomTypes;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.rooms.Rooms_Organisation;
import org.openmeetings.app.remote.ConferenceService;
import org.openmeetings.utils.math.CalendarPatterns;

public class RoomService {
	
	private static final Logger log = Red5LoggerFactory.getLogger(RoomService.class, "openmeetings");
	
	//TODO: Not implemented yet
//	public List<Rooms_Organisation> getRoomsByOrganisationAndType(String SID, long organisation_id, long roomtypes_id) {
//		return ConferenceService.getInstance().getRoomsByOrganisationAndType(SID, organisation_id, roomtypes_id);
//	}
	
	public Rooms[] getRoomsPublic(String SID, Long roomtypes_id) throws AxisFault{
		try {
			List<Rooms> roomList = ConferenceService.getInstance().getRoomsPublic(SID, roomtypes_id);
			//We need to re-marshal the Rooms object cause Axis2 cannot use our objects
			if (roomList!=null && roomList.size()!=0) {
				//roomsListObject.setRoomList(roomList);
				Rooms[] roomItems = new Rooms[roomList.size()];
				int count = 0;
				for (Iterator<Rooms>it = roomList.iterator();it.hasNext();){
					Rooms room = it.next();
					room.setCurrentusers(null);
					roomItems[count] = room;
					count++;
				}
				
				return roomItems;
			}
			log.debug("roomList SIZE: "+roomList.size());
			return null;
		} catch (Exception err) {
			log.error("[getRoomsPublic] ",err);
			throw new AxisFault(err.getMessage());
		}
	}
	
	public List<FlvRecording> getFlvRecordingByExternalRoomTypeByList(String SID, String externalRoomType) throws AxisFault {
		try {
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	    	Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);		
	    	
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				return FlvRecordingDaoImpl.getInstance().getFlvRecordingByExternalRoomType(externalRoomType);
				
			}
			
			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomType] ",err);
			throw new AxisFault(err.getMessage());
		}
	}	
	
	public FlvRecording[] getFlvRecordingByExternalRoomType(String SID, String externalRoomType) throws AxisFault {
		try {
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	    	Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);		
	    	
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				List<FlvRecording> recordingList = FlvRecordingDaoImpl.getInstance().getFlvRecordingByExternalRoomType(externalRoomType);
				
				//We need to re-marshal the Rooms object cause Axis2 cannot use our objects
				if (recordingList!=null && recordingList.size()!=0) {
					//roomsListObject.setRoomList(roomList);
					FlvRecording[] recordingListItems = new FlvRecording[recordingList.size()];
					int count = 0;
					for (Iterator<FlvRecording>it = recordingList.iterator();it.hasNext();){
						FlvRecording flvRecording = it.next();
						recordingListItems[count] = flvRecording;
						count++;
					}
					
					return recordingListItems;
				}
				
				return null;
			}
			
			return null;
		} catch (Exception err) {
			log.error("[getFlvRecordingByExternalRoomType] ",err);
			throw new AxisFault(err.getMessage());
		}
	}
	
//	public RoomsList getRoomsByExternalType(String SID, String externalType) throws AxisFault{
//		try {
//			List<Rooms> roomList = ConferenceService.getInstance().getRoomsByExternalType(SID, externalType);
//			RoomsList roomsListObject = new RoomsList();
//			//We need to re-marshal the Rooms object cause Axis2 cannot use our objects
//			if (roomList!=null && roomList.size()!=0) {
//				//roomsListObject.setRoomList(roomList);
//				Rooms[] roomItems = new Rooms[roomList.size()];
//				int count = 0;
//				for (Iterator<Rooms>it = roomList.iterator();it.hasNext();){
//					Rooms room = it.next();
//					room.setCurrentusers(null);
//					roomItems[count] = room;
//					count++;
//				}
//				
//				roomsListObject.setRoomList(roomItems);
//			}
//			log.debug("roomList SIZE: "+roomList.size());
//			return roomsListObject;
//		} catch (Exception err) {
//			log.error("[getRoomsPublic] ",err);
//			throw new AxisFault(err.getMessage());
//		}
//	}
	
	public RoomTypes[] getRoomTypes(String SID) throws AxisFault {
		try {
			List<RoomTypes> rommTypesList = ConferenceService.getInstance().getRoomTypes(SID);
			RoomTypes[] roomTypesArray = new RoomTypes[rommTypesList.size()];
			
			int count = 0;
			for (Iterator<RoomTypes>it = rommTypesList.iterator();it.hasNext();){
				RoomTypes roomType = it.next();
				roomTypesArray[count] = roomType;
				count++;
			}
			
			return roomTypesArray;
			
		} catch (Exception err) {
			log.error("[getRoomTypes]",err);
			throw new AxisFault(err.getMessage());
		}
	}
	
	public Rooms getRoomById(String SID, long rooms_id){
		return ConferenceService.getInstance().getRoomById(SID, rooms_id);
	}
	
	public Rooms getRoomWithCurrentUsersById(String SID, long rooms_id){
		return ConferenceService.getInstance().getRoomWithCurrentUsersById(SID, rooms_id);
	}
	
	public SearchResult getRooms(String SID, int start, int max, String orderby, boolean asc){
		return ConferenceService.getInstance().getRooms(SID, start, max, orderby, asc);
	}
	
	public SearchResult getRoomsWithCurrentUsers(String SID, int start, int max, String orderby, boolean asc){
		return ConferenceService.getInstance().getRoomsWithCurrentUsers(SID, start, max, orderby, asc);
	}
	
	//TODO: Add functions to get Users of a Room
	
//	public List<Rooms_Organisation> getOrganisationByRoom(String SID,long rooms_id){
//		return ConferenceService.getInstance().getOrganisationByRoom(SID, rooms_id);
//	}
//	
//	public List<RoomClient> getRoomClientsListByRoomId(Long room_id){
//		return ConferenceService.getInstance().getRoomClientsListByRoomId(room_id);
//	}
	
	/**
	 * TODO: Fix Organization Issue
	 * 
	 * @deprecated use addRoomWithModeration instead
	 * 
	 * @param SID
	 * @param name
	 * @param roomtypes_id
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param videoPodWidth
	 * @param videoPodHeight
	 * @param videoPodXPosition
	 * @param videoPodYPosition
	 * @param moderationPanelXPosition
	 * @param showWhiteBoard
	 * @param whiteBoardPanelXPosition
	 * @param whiteBoardPanelYPosition
	 * @param whiteBoardPanelHeight
	 * @param whiteBoardPanelWidth
	 * @param showFilesPanel
	 * @param filesPanelXPosition
	 * @param filesPanelYPosition
	 * @param filesPanelHeight
	 * @param filesPanelWidth
	 * @return
	 */
	public Long addRoom(String SID, String name,
			Long roomtypes_id ,
			String comment, Long numberOfPartizipants,
			Boolean ispublic,
			Integer videoPodWidth,
			Integer videoPodHeight,
			Integer videoPodXPosition,
			Integer videoPodYPosition,
			Integer moderationPanelXPosition,
			Boolean showWhiteBoard,
			Integer whiteBoardPanelXPosition,
			Integer whiteBoardPanelYPosition,
			Integer whiteBoardPanelHeight,
			Integer whiteBoardPanelWidth,
			Boolean showFilesPanel,
			Integer filesPanelXPosition,
			Integer filesPanelYPosition,
			Integer filesPanelHeight,
			Integer filesPanelWidth) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			return Roommanagement.getInstance().addRoom(user_level, name, roomtypes_id, comment, 
							numberOfPartizipants, ispublic, null, 
							false, false, null
							, false, null, true);
		} catch (Exception err) {
			log.error("[addRoom] ",err);
		}
		return new Long (-1);
	}
	
	public Long addRoomWithModeration(String SID, String name,
			Long roomtypes_id ,
			String comment, Long numberOfPartizipants,
			Boolean ispublic,
			Boolean appointment,
			Boolean isDemoRoom,
			Integer demoTime,
			Boolean isModeratedRoom) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			return Roommanagement.getInstance().addRoom(user_level, name, roomtypes_id, comment, 
							numberOfPartizipants, ispublic, null, 
							appointment, isDemoRoom, demoTime, isModeratedRoom, null,
							true);
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ",err);
		}
		return new Long (-1);
	}
	
	/**
	 * this SOAP Method has an additional param to enable or disable the
	 * buttons to apply for moderation
	 * this does only work in combination with the room-type restricted
	 * 
	 * @param SID
	 * @param name
	 * @param roomtypes_id
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param appointment
	 * @param isDemoRoom
	 * @param demoTime
	 * @param isModeratedRoom
	 * @param allowUserQuestions
	 * @return
	 */
	public Long addRoomWithModerationAndQuestions(String SID, String name,
			Long roomtypes_id ,
			String comment, Long numberOfPartizipants,
			Boolean ispublic,
			Boolean appointment,
			Boolean isDemoRoom,
			Integer demoTime,
			Boolean isModeratedRoom,
			Boolean allowUserQuestions) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			return Roommanagement.getInstance().addRoom(user_level, name, roomtypes_id, comment, 
							numberOfPartizipants, ispublic, null, 
							appointment, isDemoRoom, demoTime, isModeratedRoom, null,
							allowUserQuestions);
		} catch (Exception err) {
			log.error("[addRoomWithModerationAndQuestions] ",err);
		}
		return new Long (-1);
	}	

	/**
	 * 
     * @param SID
     * @param name
     * @param roomtypes_id
     * @param comment
     * @param numberOfPartizipants
     * @param ispublic
	 * @param appointment
	 * @param isDemoRoom
	 * @param demoTime
	 * @param isModeratedRoom
	 * @param externalRoomId
	 * @param externalUserType
         * @return
         */
	public Long getRoomIdByExternalId(String SID, String name,
                        Long roomtypes_id ,
                        String comment, Long numberOfPartizipants,
                        Boolean ispublic,
                        Boolean appointment,
                        Boolean isDemoRoom,
                        Integer demoTime,
                        Boolean isModeratedRoom,
						Long externalRoomId, 
						String externalRoomType ) {
                try {
			Rooms room = ConferenceService.getInstance().getRoomByExternalId(SID, externalRoomId, externalRoomType, roomtypes_id);
			Long roomId = null;
			if (room == null) {
				Long users_id = Sessionmanagement.getInstance().checkSession(SID);
                		Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
                        	roomId = Roommanagement.getInstance().addExternalRoom(user_level, name, roomtypes_id, comment,
                                                        numberOfPartizipants, ispublic, null,
                                                        appointment, isDemoRoom, demoTime, isModeratedRoom, null,
							externalRoomId, externalRoomType);
			} else {
				roomId = room.getRooms_id();
			}
			return roomId;
                } catch (Exception err) {
                        log.error("[addRoomWithModeration] ",err);
                }
                return new Long (-1);
        }

	/**
	 * TODO: Fix Organization Issue
	 * 
	 * @deprecated use updateRoomWithModeration
	 * 
	 * @param SID
	 * @param rooms_id
	 * @param name
	 * @param roomtypes_id
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param videoPodWidth
	 * @param videoPodHeight
	 * @param videoPodXPosition
	 * @param videoPodYPosition
	 * @param moderationPanelXPosition
	 * @param showWhiteBoard
	 * @param whiteBoardPanelXPosition
	 * @param whiteBoardPanelYPosition
	 * @param whiteBoardPanelHeight
	 * @param whiteBoardPanelWidth
	 * @param showFilesPanel
	 * @param filesPanelXPosition
	 * @param filesPanelYPosition
	 * @param filesPanelHeight
	 * @param filesPanelWidth
	 * @return
	 */
	public Long updateRoom(String SID, Long rooms_id, String name,
			Long roomtypes_id ,
			String comment, Long numberOfPartizipants,
			Boolean ispublic,
			Integer videoPodWidth,
			Integer videoPodHeight,
			Integer videoPodXPosition,
			Integer videoPodYPosition,
			Integer moderationPanelXPosition,
			Boolean showWhiteBoard,
			Integer whiteBoardPanelXPosition,
			Integer whiteBoardPanelYPosition,
			Integer whiteBoardPanelHeight,
			Integer whiteBoardPanelWidth,
			Boolean showFilesPanel,
			Integer filesPanelXPosition,
			Integer filesPanelYPosition,
			Integer filesPanelHeight,
			Integer filesPanelWidth, Boolean appointment) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			return Roommanagement.getInstance().updateRoom(user_level, rooms_id, roomtypes_id, name, ispublic, 
					comment, numberOfPartizipants, null, appointment, false, null, false,null,true);
		} catch (Exception err) {
			log.error("[addRoom] ",err);
		}
		return new Long (-1);
	}
	
	public Long updateRoomWithModeration(String SID, 
			Long room_id,
			String name,
			Long roomtypes_id ,
			String comment, Long numberOfPartizipants,
			Boolean ispublic,
			Boolean appointment,
			Boolean isDemoRoom,
			Integer demoTime,
			Boolean isModeratedRoom) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			return Roommanagement.getInstance().updateRoom(user_level, room_id, roomtypes_id, 
					name, ispublic, comment, numberOfPartizipants, null, 
					appointment, isDemoRoom, demoTime, isModeratedRoom,null,true);
		} catch (Exception err) {
			log.error("[updateRoomWithModeration] ",err);
		}
		return new Long (-1);
	}
	
	public Long updateRoomWithModerationAndQuestions(String SID, 
			Long room_id,
			String name,
			Long roomtypes_id ,
			String comment, Long numberOfPartizipants,
			Boolean ispublic,
			Boolean appointment,
			Boolean isDemoRoom,
			Integer demoTime,
			Boolean isModeratedRoom,
			Boolean allowUserQuestions) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			return Roommanagement.getInstance().updateRoom(user_level, room_id, roomtypes_id, 
					name, ispublic, comment, numberOfPartizipants, null, 
					appointment, isDemoRoom, demoTime, isModeratedRoom,
					null,allowUserQuestions);
		} catch (Exception err) {
			log.error("[updateRoomWithModerationAndQuestions] ",err);
		}
		return new Long (-1);
	}	
	
	public Long deleteRoom(String SID, long rooms_id){
		return ConferenceService.getInstance().deleteRoom(SID, rooms_id);
	}

	 public Boolean kickUser(String SID_Admin, Long room_id) {
		try {
			Boolean salida = false;

			salida = Usermanagement.getInstance().kickUserByStreamId(SID_Admin,
					room_id);

			if (salida == null)
				salida = false;

			return salida;
		} catch (Exception err) {
			log.error("[kickUser]", err);
		}
		return null;
	}
	 

	public Long addRoomWithModerationAndExternalType(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, String externalRoomType) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			return Roommanagement.getInstance().addExternalRoom(user_level, name,
					roomtypes_id, comment, numberOfPartizipants, ispublic,
					null, appointment, isDemoRoom, demoTime, isModeratedRoom,
					null, null, externalRoomType);
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
		}
		return new Long(-1);
	}
	
	/**
	 * 
	 * Create a Invitation hash and optionally send it by mail
	 * the From to Date is as String as some SOAP libraries do not accept 
	 * Date Objects in SOAP Calls Date is parsed as dd.mm.yyyy, time as hh:mm (don't forget the leading zero's)
	 * 
	 * @param SID a valid Session Token
	 * @param username the username of the User that he will get 
	 * @param room_id the conference room id of the invitation
	 * @param isPasswordProtected if the invitation is password protected
	 * @param invitationpass the password for accessing the conference room via the invitation hash
	 * @param valid the type of validation for the hash 1: endless, 2: from-to period, 3: one-time
	 * @param validFromDate Date in Format of dd.mm.yyyy only of interest if valid is type 2
	 * @param validFromTime time in Format of hh:mm only of interest if valid is type 2
	 * @param validToDate Date in Format of dd.mm.yyyy only of interest if valid is type 2
	 * @param validToTime time in Format of hh:mm only of interest if valid is type 2
	 * @return a HASH value that can be made into a URL with http://$OPENMEETINGS_HOST:$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws AxisFault
	 */
	public String getInvitationHash(
			String SID,
			String username, 
			Long room_id, 
    		Boolean isPasswordProtected, 
    		String invitationpass, 
    		Integer valid, 
    		String validFromDate, 
    		String validFromTime, 
    		String validToDate, 
    		String validToTime) throws AxisFault  {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
			
				Date dFrom = null;
		    	Date dTo = null;
		    	
				if (valid == 2) {
					Integer validFromHour = Integer.valueOf(validFromTime.substring(0, 2)).intValue();
			    	Integer validFromMinute = Integer.valueOf(validFromTime.substring(3, 5)).intValue();
			    	
			    	Integer validToHour = Integer.valueOf(validToTime.substring(0, 2)).intValue();
			    	Integer validToMinute = Integer.valueOf(validToTime.substring(3, 5)).intValue();
			    	
			    	log.info("validFromHour: "+validFromHour);
			    	log.info("validFromMinute: "+validFromMinute);
			    	
			    	
			    	Date fromDate = CalendarPatterns.parseDate(validFromDate); //dd.MM.yyyy
			    	Date toDate = CalendarPatterns.parseDate(validToDate); //dd.MM.yyyy
			    	
			    	Calendar calFrom = Calendar.getInstance();
			    	calFrom.setTime(fromDate);
			    	calFrom.set(calFrom.get(Calendar.YEAR), calFrom.get(Calendar.MONTH), calFrom.get(Calendar.DATE), validFromHour, validFromMinute, 0);
			    	
					Calendar calTo= Calendar.getInstance();
					calTo.setTime(toDate);
			    	calTo.set(calTo.get(Calendar.YEAR), calTo.get(Calendar.MONTH), calTo.get(Calendar.DATE), validToHour, validToMinute, 0);
			    	
			    	dFrom = calFrom.getTime();
			    	dTo = calTo.getTime();
			    	
			    	log.info("validFromDate: "+CalendarPatterns.getDateWithTimeByMiliSeconds(dFrom));
			    	log.info("validToDate: "+CalendarPatterns.getDateWithTimeByMiliSeconds(dTo));
				} 
		    	Invitations invitation =  Invitationmanagement.getInstance().addInvitationLink(user_level, 
		    									username, username, 
		    									username, username, username, room_id, "",
								    			isPasswordProtected, invitationpass, 
								    			valid, dFrom, dTo, users_id, "", 
								    			1L, false);
		    	
		    	if(invitation != null) {
		    		
		    		return invitation.getHash();
		    		
		    	} else {
		    		
		    		return "Sys - Error";
		    		
		    	}
	    	
			} else {
	        	return "Need Admin Privileges to perfom the Action";
	        }		    	
	    	
		} catch (Exception err) {
			log.error("[sendInvitationHash] ", err);
			throw new AxisFault(err.getMessage());
		}
	}
	
	/**
	 * Create a Invitation hash and optionally send it by mail
	 * the From to Date is as String as some SOAP libraries do not accept 
	 * Date Objects in SOAP Calls Date is parsed as dd.mm.yyyy, time as hh:mm (don't forget the leading zero's)
	 * 
	 * @param SID a valid Session Token
	 * @param username the Username of the User that he will get 
	 * @param message the Message in the Email Body send with the invitation if sendMail is true
	 * @param baseurl the baseURL for the Infivations link in the Mail Body if sendMail is true
	 * @param email the Email to send the invitation to if sendMail is true
	 * @param subject the subject of the Email send with the invitation if sendMail is true
	 * @param room_id the conference room id of the invitation
	 * @param conferencedomain the domain of the room (keep empty)
	 * @param isPasswordProtected if the invitation is password protected
	 * @param invitationpass the password for accessing the conference room via the invitation hash
	 * @param valid the type of validation for the hash 1: endless, 2: from-to period, 3: one-time
	 * @param validFromDate Date in Format of dd.mm.yyyy only of interest if valid is type 2
	 * @param validFromTime time in Format of hh:mm only of interest if valid is type 2
	 * @param validToDate Date in Format of dd.mm.yyyy only of interest if valid is type 2
	 * @param validToTime time in Format of hh:mm only of interest if valid is type 2
	 * @param language_id the language id of the EMail that is send with the invitation if sendMail is true
	 * @param sendMail if sendMail is true then the RPC-Call will send the invitation to the email
	 * @return a HASH value that can be made into a URL with http://$OPENMEETINGS_HOST:$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws AxisFault
	 */
	public String sendInvitationHash(
			String SID,
			String username, 
			String message, 
			String baseurl, 
			String email, 
			String subject, 
			Long room_id, 
			String conferencedomain, 
    		Boolean isPasswordProtected, 
    		String invitationpass, 
    		Integer valid, 
    		String validFromDate, 
    		String validFromTime, 
    		String validToDate, 
    		String validToTime,
    		Long language_id, 
    		Boolean sendMail
    		) throws AxisFault  {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				Date dFrom = null;
		    	Date dTo = null;
		    	
		    	if (valid == 2) {
					Integer validFromHour = Integer.valueOf(validFromTime.substring(0, 2)).intValue();
			    	Integer validFromMinute = Integer.valueOf(validFromTime.substring(3, 5)).intValue();
			    	
			    	Integer validToHour = Integer.valueOf(validToTime.substring(0, 2)).intValue();
			    	Integer validToMinute = Integer.valueOf(validToTime.substring(3, 5)).intValue();
			    	
			    	log.info("validFromHour: "+validFromHour);
			    	log.info("validFromMinute: "+validFromMinute);
			    	
			    	
			    	Date fromDate = CalendarPatterns.parseDate(validFromDate); //dd.MM.yyyy
			    	Date toDate = CalendarPatterns.parseDate(validToDate); //dd.MM.yyyy
			    	
			    	Calendar calFrom = Calendar.getInstance();
			    	calFrom.setTime(fromDate);
			    	calFrom.set(calFrom.get(Calendar.YEAR), calFrom.get(Calendar.MONTH), calFrom.get(Calendar.DATE), validFromHour, validFromMinute, 0);
			    	
					Calendar calTo= Calendar.getInstance();
					calTo.setTime(toDate);
			    	calTo.set(calTo.get(Calendar.YEAR), calTo.get(Calendar.MONTH), calTo.get(Calendar.DATE), validToHour, validToMinute, 0);
			    	
			    	dFrom = calFrom.getTime();
			    	dTo = calTo.getTime();
			    	
			    	log.info("validFromDate: "+CalendarPatterns.getDateWithTimeByMiliSeconds(dFrom));
			    	log.info("validToDate: "+CalendarPatterns.getDateWithTimeByMiliSeconds(dTo));
				} 
				
		    	Invitations invitation =  Invitationmanagement.getInstance().addInvitationLink(user_level, username, message, 
								    			baseurl, email, subject, room_id, "",
								    			isPasswordProtected, invitationpass, 
								    			valid, dFrom, dTo, users_id, baseurl, 
								    			language_id, sendMail);
		    	
		    	if(invitation != null) {
		    		
		    		return invitation.getHash();
		    		
		    	} else {
		    		
		    		return "Sys - Error";
		    		
		    	}
		    	
			} else {
	        	return "Need Admin Privileges to perfom the Action";
	        }		    	
	    	
		} catch (Exception err) {
			log.error("[sendInvitationHash] ", err);
			throw new AxisFault(err.getMessage());
		}
	}
	
	/**
	 * Create a Invitation hash and optionally send it by mail
	 * the From to Date is as String as some SOAP libraries do not accept 
	 * Date Objects in SOAP Calls Date is parsed as dd.mm.yyyy, time as hh:mm (don't forget the leading zero's)
	 * 
	 * @param SID a valid Session Token
	 * @param username the Username of the User that he will get 
	 * @param message the Message in the Email Body send with the invitation if sendMail is true
	 * @param baseurl the baseURL for the Infivations link in the Mail Body if sendMail is true
	 * @param email the Email to send the invitation to if sendMail is true
	 * @param subject the subject of the Email send with the invitation if sendMail is true
	 * @param room_id the conference room id of the invitation
	 * @param conferencedomain the domain of the room (keep empty)
	 * @param isPasswordProtected if the invitation is password protected
	 * @param invitationpass the password for accessing the conference room via the invitation hash
	 * @param valid the type of validation for the hash 1: endless, 2: from-to period, 3: one-time
	 * @param fromDate Date as Date Object only of interest if valid is type 2
	 * @param toDate Date as Date Object only of interest if valid is type 2
	 * @param language_id the language id of the EMail that is send with the invitation if sendMail is true
	 * @param sendMail if sendMail is true then the RPC-Call will send the invitation to the email
	 * @return a HASH value that can be made into a URL with http://$OPENMEETINGS_HOST:$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws AxisFault
	 */
	public String sendInvitationHashWithDateObject(
			String SID,
			String username, 
			String message, 
			String baseurl, 
			String email, 
			String subject, 
			Long room_id, 
			String conferencedomain, 
    		Boolean isPasswordProtected, 
    		String invitationpass, 
    		Integer valid, 
    		Date fromDate,
    		Date toDate, 
    		Long language_id, 
    		Boolean sendMail
    		) throws AxisFault  {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
		    	Calendar calFrom = Calendar.getInstance();
		    	calFrom.setTime(fromDate);
				
				Calendar calTo= Calendar.getInstance();
				calTo.setTime(toDate);
		    	
		    	Date dFrom = calFrom.getTime();
		    	Date dTo = calTo.getTime();
		    	
		    	log.info("validFromDate: "+CalendarPatterns.getDateWithTimeByMiliSeconds(dFrom));
		    	log.info("validToDate: "+CalendarPatterns.getDateWithTimeByMiliSeconds(dTo));
		    	
		    	Invitations invitation =  Invitationmanagement.getInstance().addInvitationLink(user_level, username, message, 
								    			baseurl, email, subject, room_id, "",
								    			isPasswordProtected, invitationpass, 
								    			valid, dFrom, dTo, users_id, baseurl, 
								    			language_id, sendMail);
		    	
		    	if(invitation != null) {
		    		
		    		return invitation.getHash();
		    		
		    	} else {
		    		
		    		return "Sys - Error";
		    		
		    	}
	    	
			} else {
	        	return "Need Admin Privileges to perfom the Action";
	        }
			
		} catch (Exception err) {
			log.error("[sendInvitationHash] ", err);
			throw new AxisFault(err.getMessage());
		}
	}
		
}
