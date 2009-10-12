package org.openmeetings.app.remote;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.calendar.management.MeetingMemberLogic;
import org.openmeetings.app.data.user.Usermanagement;

public class MeetingMemberService {
	
	private static final Logger log = Logger.getLogger(MeetingMemberService.class);
	
	private static MeetingMemberService instance = null;

	public static synchronized MeetingMemberService getInstance() {
		if (instance == null) {
			instance = new MeetingMemberService();
		}

		return instance;
	}

	
	public Long addMeetingMember(String SID, String firstname, String lastname, String memberStatus,
			String appointmentStatus, Long appointmentId, Long userid, String email, String baseUrl){
			
			log.debug("addMeetingMember baseUrl = " + baseUrl);
			
			
		try{
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
					        	
	        Long id = MeetingMemberLogic.getInstance().addMeetingMember( firstname,  lastname,  memberStatus,
	    			 appointmentStatus,  appointmentId,  userid,  email, baseUrl, users_id, false);
	        
	        log.debug("addMeetingmember : newId : " + id);
	        return id;
	        }
		} catch (Exception err) {
			log.error("[addMeetingMember]",err);
		}
		return null;
	
	}
	
	public Long updateMeetingMember(String SID,Long meetingMemberId, String firstname, String lastname, String memberStatus,
			String appointmentStatus, Long appointmentId, Long userid, String email){
		
		try{
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
					        	
	        return	 MeetingMemberLogic.getInstance().updateMeetingMember(meetingMemberId, 
	        		firstname, lastname, memberStatus, appointmentStatus, appointmentId, userid, email);
	        }
		} catch (Exception err) {
			log.error("[updateMeetingMember]",err);
		}
		return null;
	
	}
	
	public Long deleteMeetingMember(String SID,Long meetingMemberId){
		log.debug("MeetingMemberService.deleteMeetingmember");
		
		try{
			
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
	        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
	        if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
					        	
	        return	 MeetingMemberLogic.getInstance().deleteMeetingMember(meetingMemberId, users_id);
	        }
		} catch (Exception err) {
			log.error("[deleteMeetingMember]",err);
		}
		return null;
	
	}
}
