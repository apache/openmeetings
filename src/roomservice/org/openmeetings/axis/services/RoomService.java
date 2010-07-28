package org.openmeetings.axis.services;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.management.MeetingMemberLogic;
import org.openmeetings.app.data.conference.Invitationmanagement;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.flvrecord.FlvRecordingDaoImpl;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecording;
import org.openmeetings.app.hibernate.beans.invitation.Invitations;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.rooms.RoomTypes;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.rooms.Rooms_Organisation;
import org.openmeetings.app.remote.ConferenceService;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;

public class RoomService {
	
	private static final Logger log = Red5LoggerFactory.getLogger(RoomService.class, ScopeApplicationAdapter.webAppRootKey);
	
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
	
	public FlvRecording[] getFlvRecordingByRoomId(String SID, Long roomId) throws AxisFault {
		try {
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	    	Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);		
	    	
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				List<FlvRecording> recordingList = FlvRecordingDaoImpl.getInstance().getFlvRecordingByRoomId(roomId);
				
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
	
	public RoomCountBean[] getRoomCounters(String SID, Integer roomId1, Integer roomId2, Integer roomId3, Integer roomId4
			, Integer roomId5, Integer roomId6, Integer roomId7, Integer roomId8, Integer roomId9, Integer roomId10) throws AxisFault {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	    	Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);		
	    	
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				LinkedList<Integer> roomIds = new LinkedList<Integer>();
				
				if (roomId1 != null && roomId1 > 0) {
					roomIds.push(roomId1);
				}
				if (roomId2 != null && roomId2 > 0) {
					log.debug("roomId2 :: "+roomId2);
					roomIds.push(roomId2);
				}
				if (roomId3 != null && roomId3 > 0) {
					roomIds.push(roomId3);
				}
				if (roomId4 != null && roomId4 > 0) {
					roomIds.push(roomId4);
				}
				if (roomId5 != null && roomId5 > 0) {
					roomIds.push(roomId5);
				}
				if (roomId6 != null && roomId6 > 0) {
					roomIds.push(roomId6);
				}
				if (roomId7 != null && roomId7 > 0) {
					roomIds.push(roomId7);
				}
				if (roomId8 != null && roomId8 > 0) {
					roomIds.push(roomId8);
				}
				if (roomId9 != null && roomId9 > 0) {
					roomIds.push(roomId9);
				}
				if (roomId10 != null && roomId10 > 0) {
					roomIds.push(roomId10);
				}
				
				List<Rooms> rooms = Roommanagement.getInstance().getRoomsByIds(roomIds);
				
				RoomCountBean[] roomsArray = new RoomCountBean[rooms.size()];
				
				int i = 0;
				for (Rooms room : rooms) {
					
					HashMap<String,RoomClient> map = ClientListManager.getInstance().getClientListByRoom(room.getRooms_id());
					
					//room.setCurrentusers(new LinkedList<RoomClient>());
					
//					for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
//						room.getCurrentusers().add(map.get(iter.next()));
//					}
					
					RoomCountBean rCountBean = new RoomCountBean();
					rCountBean.setRoomId(room.getRooms_id());
					rCountBean.setRoomName(room.getName());
					rCountBean.setMaxUser(room.getNumberOfPartizipants().intValue());
					rCountBean.setRoomCount(map.size());
					
					roomsArray[i] = rCountBean;
					i++;
				}
				
				return roomsArray;
			}
			
		} catch (Exception err) {
			log.error("[getRoomTypes]",err);
			throw new AxisFault(err.getMessage());
		}
		return null;
	}
	
	public Rooms getRoomById(String SID, long rooms_id){
		return ConferenceService.getInstance().getRoomById(SID, rooms_id);
	}
	
	/**
	 * @deprecated this function is intend to be deleted
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	public Rooms getRoomWithCurrentUsersById(String SID, long rooms_id){
		return ConferenceService.getInstance().getRoomWithCurrentUsersById(SID, rooms_id);
	}
	
	public RoomReturn getRoomWithClientObjectsById(String SID, long rooms_id) throws AxisFault {
		try {
    		Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	    	Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);		
	    	
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				Rooms room = Roommanagement.getInstance().getRoomById(user_level,rooms_id);
				
				RoomReturn roomReturn = new RoomReturn();
				
				roomReturn.setCreated(room.getStarttime());
				roomReturn.setCreator(null);
				roomReturn.setName(room.getName());
				roomReturn.setRoom_id(room.getRooms_id());
				
				HashMap<String,RoomClient> map = ClientListManager.getInstance().getClientListByRoom(room.getRooms_id());
				
				RoomUser[] roomUsers = new RoomUser[map.size()];
				
				int i = 0;
				for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
					RoomClient rcl = map.get(iter.next());
					
					RoomUser roomUser = new RoomUser();
					roomUser.setFirstname(rcl.getFirstname());
					roomUser.setLastname(rcl.getLastname());
					roomUser.setBroadcastId(rcl.getBroadCastID());
					roomUser.setPublicSID(rcl.getPublicSID());
					roomUser.setIsBroadCasting(rcl.getIsBroadcasting());
					roomUser.setAvsettings(rcl.getAvsettings());
					
					roomUsers[i] = roomUser;
					
					i++;
					
				}
				
				roomReturn.setRoomUser(roomUsers);

				return roomReturn;
			}
			
			
			return null;
			
		} catch (Exception err) {
			log.error("[getRoomWithClientObjectsById]",err);
			throw new AxisFault(err.getMessage());
		}
		
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
							, false, null, true, false);
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
							true, false);
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
							allowUserQuestions, false);
		} catch (Exception err) {
			log.error("[addRoomWithModerationAndQuestions] ",err);
		}
		return new Long (-1);
	}	
	
	public Long addRoomWithModerationQuestionsAndAudioType(String SID, String name,
			Long roomtypes_id ,
			String comment, Long numberOfPartizipants,
			Boolean ispublic,
			Boolean appointment,
			Boolean isDemoRoom,
			Integer demoTime,
			Boolean isModeratedRoom,
			Boolean allowUserQuestions,
			Boolean isAudioOnly) throws AxisFault {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			return Roommanagement.getInstance().addRoom(user_level, name, roomtypes_id, comment, 
							numberOfPartizipants, ispublic, null, 
							appointment, isDemoRoom, demoTime, isModeratedRoom, null,
							allowUserQuestions, isAudioOnly);
		} catch (Exception err) {
			log.error("[addRoomWithModerationQuestionsAndAudioType] ",err);
			throw new AxisFault(err.getMessage());
		}
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
						String externalRoomType ) throws AxisFault {
            try {
				Rooms room = ConferenceService.getInstance().getRoomByExternalId(SID, externalRoomId, externalRoomType, roomtypes_id);
				Long roomId = null;
				if (room == null) {
					Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	                		Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	                        	roomId = Roommanagement.getInstance().addExternalRoom(user_level, name, roomtypes_id, comment,
	                                                        numberOfPartizipants, ispublic, null,
	                                                        appointment, isDemoRoom, demoTime, isModeratedRoom, null,
	                                                    	externalRoomId, externalRoomType, true, false);
				} else {
					roomId = room.getRooms_id();
				}
				return roomId;
            } catch (Exception err) {
                log.error("[addRoomWithModeration] ",err);
                throw new AxisFault(err.getMessage());
            }
                //return new Long (-1);
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
					comment, numberOfPartizipants, null, appointment, false, null, false,null,true,false);
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
					appointment, isDemoRoom, demoTime, isModeratedRoom,null,true,false);
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
					null,allowUserQuestions,false);
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
					null, null, externalRoomType, true, false);
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
		}
		return new Long(-1);
	}
	
	public Long addRoomWithModerationExternalTypeAndAudioType(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, String externalRoomType,
			Boolean allowUserQuestions, Boolean isAudioOnly) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			return Roommanagement.getInstance().addExternalRoom(user_level, name,
					roomtypes_id, comment, numberOfPartizipants, ispublic,
					null, appointment, isDemoRoom, demoTime, isModeratedRoom,
					null, null, externalRoomType, allowUserQuestions, isAudioOnly);
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
	

	public List<RoomReturn> getRoomsWithCurrentUsersByList(String SID, int start, int max, String orderby, boolean asc) throws AxisFault {
		try {
			List<Rooms> rooms = ConferenceService.getInstance().getRoomsWithCurrentUsersByList(SID, start, max, orderby, asc);
			
			List<RoomReturn> returnObjList = new LinkedList<RoomReturn>();
			
			for (Rooms room : rooms) {
				
				RoomReturn roomReturn = new RoomReturn();
				
				roomReturn.setRoom_id(room.getRooms_id());
				roomReturn.setName(room.getName());
				
				roomReturn.setCreator("SOAP");
				roomReturn.setCreated(room.getStarttime());
				
				RoomUser[] rUser = new RoomUser[room.getCurrentusers().size()];
				
				int i = 0;
				for (RoomClient rcl : room.getCurrentusers()) {
					
					RoomUser ru = new RoomUser();
					ru.setFirstname(rcl.getFirstname());
					ru.setLastname(rcl.getLastname());
					
					rUser[i] = ru;
					
					i++;
				}
				
				roomReturn.setRoomUser(rUser);
				
				returnObjList.add(roomReturn);
					
			}
			
			return returnObjList;
		} catch (Exception err){
			log.error("setUserObjectWithExternalUser",err);
			throw new AxisFault(err.getMessage());
		}	
	}

	public List<RoomReturn> getRoomsWithCurrentUsersByListAndType(String SID, int start, int max, String orderby, boolean asc, String externalRoomType) throws AxisFault {
		try {
			List<Rooms> rooms = ConferenceService.getInstance().getRoomsWithCurrentUsersByListAndType(SID, start, max, orderby, asc, externalRoomType);
			
			List<RoomReturn> returnObjList = new LinkedList<RoomReturn>();
			
			for (Rooms room : rooms) {
				
				RoomReturn roomReturn = new RoomReturn();
				
				roomReturn.setRoom_id(room.getRooms_id());
				roomReturn.setName(room.getName());
				
				roomReturn.setCreator("SOAP");
				roomReturn.setCreated(room.getStarttime());
				
				RoomUser[] rUser = new RoomUser[room.getCurrentusers().size()];
				
				int i = 0;
				for (RoomClient rcl : room.getCurrentusers()) {
					
					RoomUser ru = new RoomUser();
					ru.setFirstname(rcl.getFirstname());
					ru.setLastname(rcl.getLastname());
					
					rUser[i] = ru;
					
					i++;
				}
				
				roomReturn.setRoomUser(rUser);
				
				returnObjList.add(roomReturn);
					
			}
			
			return returnObjList;
		} catch (Exception err){
			log.error("setUserObjectWithExternalUser",err);
			throw new AxisFault(err.getMessage());
		}	
	}

	
	public Long addRoomWithModerationAndExternalTypeAndStartEnd(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, String externalRoomType,
			String validFromDate, 
    		String validFromTime, 
    		String validToDate, 
    		String validToTime,
		    Boolean isPasswordProtected,
		    String password,
		    Long reminderTypeId
			) throws AxisFault {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				Date dFrom = null;
		    	Date dTo = null;
	    	
				Integer validFromHour = Integer.valueOf(validFromTime.substring(0, 2)).intValue();
		    	Integer validFromMinute = Integer.valueOf(validFromTime.substring(3, 5)).intValue();
		    	
		    	Integer validToHour = Integer.valueOf(validToTime.substring(0, 2)).intValue();
		    	Integer validToMinute = Integer.valueOf(validToTime.substring(3, 5)).intValue();
		    	
		    	log.info("validFromHour: "+validFromHour);
		    	log.info("validFromMinute: "+validFromMinute);
		    	
		    	
		    	Date fromDate = CalendarPatterns.parseDateBySeparator(validFromDate); //dd.MM.yyyy
		    	Date toDate = CalendarPatterns.parseDateBySeparator(validToDate); //dd.MM.yyyy
		    	
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
				
		    	Long rooms_id =  Roommanagement.getInstance().addExternalRoom(user_level, name,
						roomtypes_id, comment, numberOfPartizipants, ispublic,
						null, appointment, isDemoRoom, demoTime, isModeratedRoom,
						null, null, externalRoomType, false, false);
		    	
		    	if (rooms_id <= 0) {
		    		return rooms_id;
		    	}
		    	
		    	AppointmentDaoImpl.getInstance().addAppointment("appointmentName", users_id, "appointmentLocation", "appointmentDescription", 
		    			dFrom, dTo, //appointmentstart, appointmentend, 
		    			false, false, false, false, //isDaily, isWeekly, isMonthly, isYearly, 
		    			1L, //categoryId
		    			reminderTypeId, //1=none, 2=simple mail, 3=ICAL
		    			Roommanagement.getInstance().getRoomById(rooms_id), 
		    			1L, //language_id
		    			isPasswordProtected, //isPasswordProtected
		    			password //password
		    			);
		    	
		    	return rooms_id;
	    	
			} else {
				return -2L;
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
			
			throw new AxisFault(err.getMessage());
		}
		//return new Long(-1);
		//return numberOfPartizipants;
	}
	
	public Long addMeetingMemberRemindToRoom(String SID, Long room_id, String firstname, String lastname, String email, String baseUrl, Long language_id) throws AxisFault {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				Appointment appointment = AppointmentDaoImpl.getInstance().getAppointmentByRoom(room_id);
				
				if (appointment == null) {
					return -1L;
				}
				//Not In Remote List available - extern user
				Long memberId = MeetingMemberLogic.getInstance().addMeetingMember(firstname, lastname, 
								"0", "0", appointment.getAppointmentId(), null,  email, baseUrl, 
								null, new Boolean(false), language_id, false, "");
				
				return memberId;
				
			} else {
				return -2L;
			}
		} catch (Exception err) {
			log.error("[addRoomWithModeration] ", err);
			
			throw new AxisFault(err.getMessage());
		}
		
	}	
		
}
