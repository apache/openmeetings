
package org.openmeetings.app.data.calendar.management;


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.conference.Invitationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.basic.Configuration;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.openmeetings.app.hibernate.beans.invitation.Invitations;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class MeetingMemberLogic {
	
	private static final Logger log = Red5LoggerFactory.getLogger(MeetingMemberLogic.class, ScopeApplicationAdapter.webAppRootKey);
	private static MeetingMemberLogic instance = null;

	public static synchronized MeetingMemberLogic getInstance() {
		if (instance == null) {
			instance = new MeetingMemberLogic();
		}

		return instance;
	}
	
	/**
	 * @author becherer
	 * @param firstname
	 * @param lastname
	 * @param memberStatus
	 * @param appointmentStatus
	 * @param appointmentId
	 * @param userid
	 * @param email
	 * @param baseUrl
	 * @param meeting_organizer
	 * @return
	 */
	//------------------------------------------------------------------------------------------------------------------------------
	public Long addMeetingMember(String firstname, String lastname, String memberStatus,
			String appointmentStatus, Long appointmentId, Long userid, String email, 
			String baseUrl, Long meeting_organizer, Boolean invitor, 
			Long language_id, Boolean isPasswordProtected, String password){
		
		try{
			Long memberId =  MeetingMemberDaoImpl.getInstance().addMeetingMember(firstname,  lastname,  memberStatus,
				 appointmentStatus,  appointmentId,  userid,  email, invitor);
		
			
			// DefaultInvitation
			Appointment point = AppointmentLogic.getInstance().getAppointMentById(appointmentId);
			
			MeetingMember member = getMemberById(memberId);
			
			Long invitationId = null;
			
			if(point.getRemind() == null){
				log.error("Appointment has no assigned ReminderType!");
				return null;
			}
			
			log.debug(":::: addMeetingMember ..... "+point.getRemind().getTypId());
			
			Users us = Usermanagement.getInstance().getUserById(userid);
			
			String jNameTimeZone = null;
			if (us != null && us.getOmTimeZone() != null) {
				jNameTimeZone = us.getOmTimeZone().getJname();
			} else {
				Configuration conf = Configurationmanagement.getInstance().getConfKey(3L, "default.timezone");
				if (conf != null) {
					jNameTimeZone = conf.getConf_value();
				}
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone(jNameTimeZone));
			int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
			
			Date starttime = new Date(point.getAppointmentStarttime().getTime() + offset);
			Date endtime = new Date(point.getAppointmentEndtime().getTime() + offset);
			
			if(point.getRemind().getTypId() == 1){
				log.debug("no reminder required");
			} else if(point.getRemind().getTypId() == 2){
				log.debug("Reminder for Appointment : simple email");
				
				Invitations invitation = Invitationmanagement.getInstance().addInvitationLink(
							new Long(2), //userlevel
							firstname + " " + lastname, //username
							"Invitation to an openMeetings Event : " + point.getAppointmentName() + ", " + point.getAppointmentDescription() + ", Start : " + point.getAppointmentStarttime() + ", End : " + point.getAppointmentEndtime(), //message
							baseUrl, // baseURl
							email, //email
							"Invitation to an openmeetings Event : " + point.getAppointmentName(), //subject
							point.getRoom().getRooms_id(), // room_id
							"public",
							isPasswordProtected, // passwordprotected
							password, // invitationpass
							2, // valid
							starttime, // valid from
							endtime, // valid to
							meeting_organizer, // created by
							baseUrl, language_id, 
							true
						);
				
				invitationId = invitation.getInvitations_id();
				
			} else if(point.getRemind().getTypId() == 3){
				log.debug("Reminder for Appointment : iCal mail");
				
				invitationId = Invitationmanagement.getInstance().addInvitationIcalLink(new Long(2), //userlevel
							firstname + " " + lastname, //username
							"Invitation to an openMeetings Event : " + point.getAppointmentName() + ", " + point.getAppointmentDescription() + ", Start : " + point.getAppointmentStarttime() + ", End : " + point.getAppointmentEndtime(), //message
							baseUrl, // baseURl
							email, //email
							"Invitation to an openmeetings Event : " + point.getAppointmentName(), //subject
							point.getRoom().getRooms_id(), // room_id
							"public",
							isPasswordProtected, // passwordprotected
							password, // invitationpass
							2, // valid
							starttime, // valid from
							endtime, // valid to
							meeting_organizer, // created by
							point.getAppointmentId(),
							member.getInvitor(), language_id,
							jNameTimeZone
						);
				
			}
			
			// Setting InvitationId within MeetingMember
			
			if(invitationId != null){
				Invitations invi = Invitationmanagement.getInstance().getInvitationbyId(invitationId);
			
				member.setInvitation(invi);
				
				updateMeetingMember(member);
				
			}
			
			return memberId;
		
		}catch(Exception err){
			log.error("[addMeetingMember]",err);
		}
		return null;
	}
	//------------------------------------------------------------------------------------------------------------------------------
	
	
	
	/**
	 * 
	 */
	//------------------------------------------------------------------------------------------------------------------------------
	public Long updateMeetingMember(Long meetingMemberId, String firstname, String lastname, 
			 String memberStatus, String appointmentStatus, 
			 Long appointmentId, Long userid, String email ){
		
		log.debug("MeetingMemberLogic.updateMeetingMember");
		
		MeetingMember member = MeetingMemberDaoImpl.getInstance().getMeetingMemberById(meetingMemberId);
		
		if(member == null){
			log.error("Couldnt retrieve Member for ID " + meetingMemberId);
			return null;
		}
		
		
		
		try {
			return MeetingMemberDaoImpl.getInstance().updateMeetingMember(meetingMemberId,
					firstname, lastname, memberStatus, appointmentStatus, appointmentId, userid, email);
		} catch (Exception err) {
			log.error("[updateMeetingMember]",err);
		}
		return null;
	}
	//------------------------------------------------------------------------------------------------------------------------------
	
	
	
	
	
	/**
	 * @author becherer
	 * @param member
	 * @return
	 */
	//--------------------------------------------------------------------------------------------
	public Long updateMeetingMember(MeetingMember member){
		log.debug("updateMeetingMember");
		
		return MeetingMemberDaoImpl.getInstance().updateMeetingMember(member).getMeetingMemberId();
	}
	//--------------------------------------------------------------------------------------------
	
	/**
	 * @author becherer
	 * @param memberId
	 */
	//--------------------------------------------------------------------------------------------
	public MeetingMember getMemberById(Long memberId){
		log.debug("getMemberById");
		
		return MeetingMemberDaoImpl.getInstance().getMeetingMemberById(memberId);
	}
	//--------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 * @param meetingMemberId
	 * @param users_id
	 * @return
	 */
	//--------------------------------------------------------------------------------------------
	public Long deleteMeetingMember(Long meetingMemberId , Long users_id){
		log.debug("meetingMemberLogic.deleteMeetingMember : " + meetingMemberId);
		
		try {
			
			MeetingMember member = MeetingMemberDaoImpl.getInstance().getMeetingMemberById(meetingMemberId);
			
			if(member == null){
				log.error("could not find meeting member!");
				return null;
			}
			
			Appointment point = member.getAppointment();
			point = AppointmentLogic.getInstance().getAppointMentById(point.getAppointmentId());
			
			if(point == null){
				log.error("could not retrieve appointment!");
				return null;
			}
			
			Users user = Usermanagement.getInstance().getUserById(users_id);
			
			if(user == null){
				log.error("could not retrieve user!");
				return null;
			}
			
			log.debug("before sending cancelMail");
			
			// cancel invitation
			Invitationmanagement.getInstance().cancelInvitation(point, member, users_id);
			
			log.debug("after sending cancelmail");
			
			
			Long returnValue =  MeetingMemberDaoImpl.getInstance().deleteMeetingMember(meetingMemberId);
			
			return returnValue;
			
		} catch (Exception err) {
			log.error("[deleteMeetingMember]",err);
		}
		return null;
	}
	//--------------------------------------------------------------------------------------------

	/*	public List<Appointment> getAppointmentByRange(Long userId ,Date starttime, Date endtime){
		try {	
			return AppointmentDaoImpl.getInstance().getAppointmentsByRange(userId, starttime, endtime);
		}catch(Exception err){
			log.error("[getAppointmentByRange]",err);
		}
		return null;
	}
	//next appointment to current date
	public Appointment getNextAppointment(){
		try{
		return AppointmentDaoImpl.getInstance().getNextAppointment(new Date());
		}catch(Exception err){
			log.error("[getNextAppointmentById]",err);
		}	
		return null;
	}
	
	public List<Appointment> searchAppointmentByName(String appointmentName){
		try{
		return AppointmentDaoImpl.getInstance().searchAppointmentsByName(appointmentName) ;
		}catch(Exception err){
			log.error("[searchAppointmentByName]",err);	
		}
		return null;
	}
	
	
	
	public Long deleteAppointment(Long appointmentId){
		try{
		AppointmentDaoImpl.getInstance().deleteAppointement(appointmentId);
		return appointmentId;
		}catch(Exception err){
		log.error("[deleteAppointment]",err);	
		}
		return null;
		
	}
	
	public Long updateAppointment(Long appointmentId, String appointmentName,Long userId, String appointmentDescription, 
			Date appointmentstart, Date appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly, Boolean isYearly, Long categoryId ){
		
		try {
			return AppointmentDaoImpl.getInstance().updateAppointment(appointmentId,
					appointmentName, userId, appointmentDescription, appointmentstart,
					appointmentend, isDaily, isWeekly, isMonthly, isYearly,
					categoryId);
		} catch (Exception err) {
			log.error("[updateAppointment]",err);
		}
		return null;
	}
	*/
}
