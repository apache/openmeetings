package org.openmeetings.app.data.conference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.persistence.beans.calendar.Appointment;
import org.openmeetings.app.persistence.beans.calendar.MeetingMember;
import org.openmeetings.app.persistence.beans.invitation.Invitations;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.templates.InvitationTemplate;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.utils.crypt.MD5;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.openmeetings.utils.mail.IcalHandler;
import org.openmeetings.utils.mail.MailHandler;
import org.openmeetings.utils.math.CalendarPatterns;

/**
 * 
 * @author swagner
 *
 */
public class Invitationmanagement {
	
	private static final Logger log = Red5LoggerFactory.getLogger(Invitationmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	private static Invitationmanagement instance;

	private Invitationmanagement() {}

	public static synchronized Invitationmanagement getInstance() {
		if (instance == null) {
			instance = new Invitationmanagement();
		}
		return instance;
	}
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
	
	/**
	 * Sending invitation within plain mail
	 * @param user_level
	 * @param username
	 * @param message
	 * @param baseurl
	 * @param email
	 * @param subject
	 * @param rooms_id
	 * @param conferencedomain
	 * @param isPasswordProtected
	 * @param invitationpass
	 * @param valid
	 * @param validFrom
	 * @param validTo
	 * @param createdBy
	 * @return
	 */
	//---------------------------------------------------------------------------------------------------------
	public Invitations addInvitationLink(Long user_level, String username, String message,
			String baseurl, String email, String subject, Long rooms_id, String conferencedomain,
			Boolean isPasswordProtected, String invitationpass, Integer valid,
			Date validFrom, Date validTo, Long createdBy, String baseUrl, Long language_id,
			Boolean sendMail, Date gmtTimeStart, Date gmtTimeEnd, Long appointmentId){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				
				Invitations invitation = new Invitations();
				invitation.setIsPasswordProtected(isPasswordProtected);
				if (isPasswordProtected){
					invitation.setInvitationpass(ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(invitationpass));
				}
				
				invitation.setInvitationWasUsed(false);
				log.debug(baseUrl);
				invitation.setBaseUrl(baseUrl);
				
				//valid period of Invitation
				if (valid == 1) {
					//endless
					invitation.setIsValidByTime(false);
					invitation.setCanBeUsedOnlyOneTime(false);
				} else if (valid == 2){
					//period
					invitation.setIsValidByTime(true);
					invitation.setCanBeUsedOnlyOneTime(false);
					
					Date gmtTimeStartShifted = new Date(gmtTimeStart.getTime() - ( 5 * 60 * 1000 ) );
					
					invitation.setValidFrom(gmtTimeStartShifted);
					invitation.setValidTo(gmtTimeEnd);	
					
					//invitation.setValidFrom(validFrom);
					//invitation.setValidTo(validTo);		
				} else {
					//one-time
					invitation.setIsValidByTime(false);
					invitation.setCanBeUsedOnlyOneTime(true);
					invitation.setInvitationWasUsed(false);
				}
				
				invitation.setDeleted("false");
				
				Users us = UsersDaoImpl.getInstance().getUser(createdBy);
				String hashRaw = "HASH"+(System.currentTimeMillis());
				invitation.setHash(MD5.do_checksum(hashRaw));
				
				invitation.setInvitedBy(us);
				invitation.setInvitedname(username);
				invitation.setInvitedEMail(email);
				invitation.setRoom(Roommanagement.getInstance().getRoomById(rooms_id));
				invitation.setConferencedomain(conferencedomain);
				invitation.setStarttime(new Date());
				invitation.setAppointmentId(appointmentId);
				
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				invitation = session.merge(invitation);
				session.flush();
				long invitationId = invitation.getInvitations_id();
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				invitation.setInvitations_id(invitationId);
				
				if (invitationId > 0) {
					
					if (sendMail) {
						this.sendInvitionLink(username, message, baseurl, email,
								subject, invitation.getHash(), validFrom,
								validTo, language_id);
					}
					
					return invitation;
				}
				
			}
		} catch (Exception err){
			log.error("addInvitationLink",err);
		}
		return null;
	}
	//----------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * @author becherer
	 * @param ment
	 * @param member
	 */
	//-----------------------------------------------------------------------------------------------
	public void cancelInvitation(Appointment appointment, MeetingMember member, 
			Long canceling_user_id, Long language_id){
		
		log.debug("cancelInvitation");
		
		Users user;
		
		try{
			user= Usermanagement.getInstance().getUserById(canceling_user_id);
		}catch(Exception e){
			log.error("Cancelling user cant be retrieved");
			return;
		}
		
		if(appointment.getRemind() == null ){
			log.error("Appointment " + appointment.getAppointmentName() + " has no ReminderType!");
			return;
		}
		
		log.debug("Remindertype : " + appointment.getRemind().getTypId());
		
		Users us = member.getUserid();
		
		String jNameTimeZone = null;
		if (us != null && us.getOmTimeZone() != null) {
			jNameTimeZone = us.getOmTimeZone().getJname();
		} else {
			Configuration conf = Configurationmanagement.getInstance().getConfKey(3L, "default.timezone");
			if (conf != null) {
				jNameTimeZone = conf.getConf_value();
			}
		}
		
		OmTimeZone omTimeZone = OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jNameTimeZone);
		
		String timeZoneName = omTimeZone.getIcal();
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone(omTimeZone.getIcal()));
		int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
		
		Date starttime = new Date(appointment.getAppointmentStarttime().getTime() + offset);
		Date endtime = new Date(appointment.getAppointmentEndtime().getTime() + offset);
		
//		System.out.println(omTimeZone.getIcal());
//		System.out.println(offset);
//		System.out.println(starttime);
//		System.out.println(endtime);
		
		
		Fieldlanguagesvalues labelid1157 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1157), language_id);
		
		String message = labelid1157.getValue() + appointment.getAppointmentName();
		
		if (appointment.getAppointmentDescription().length() != 0) {
			
			Fieldlanguagesvalues labelid1152 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1152), language_id);
			message += labelid1152.getValue() + appointment.getAppointmentDescription();
			
		}
		
		Fieldlanguagesvalues labelid1153 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1153), language_id);
		Fieldlanguagesvalues labelid1154 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1154), language_id);
		
		message += "<br/>" + labelid1153.getValue() + ' ' 
						+ CalendarPatterns.getDateWithTimeByMiliSeconds(starttime) 
						+ " (" + timeZoneName + ")"
						+ "<br/>";
		
		message += labelid1154.getValue() + ' ' 
						+ CalendarPatterns.getDateWithTimeByMiliSeconds(endtime) 
						+ " (" + timeZoneName + ")"
						+ "<br/>";
		
		String invitorName = user.getFirstname() + " " + user.getLastname() + " [" + user.getAdresses().getEmail() + "]";
		
		Fieldlanguagesvalues labelid1156 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1156), language_id);
		message += labelid1156.getValue() + invitorName + "<br/>";
		
		// checking reminderType
		if(appointment.getRemind().getTypId() == 1){
			log.debug("no remindertype defined -> no cancel of invitation");
		}
		else if(appointment.getRemind().getTypId() == 2){
			log.debug("ReminderType simple mail -> sending simple mail...");
			sendInvitationCancelMail(member.getEmail(), member.getAppointment(), 
					user.getAdresses().getEmail(), 
					labelid1157.getValue() + appointment.getAppointmentName(), 
					message);
		}
		else if(appointment.getRemind().getTypId() == 3){
			try{
				sendInvitationIcalCancelMail(member.getEmail(), member.getFirstname() + " " + member.getLastname(), 
						appointment, canceling_user_id, member.getInvitor(), 
						starttime, endtime, jNameTimeZone, 
						labelid1157.getValue() + appointment.getAppointmentName(), 
						message);
			}catch(Exception e){
				log.error("Error sending IcalCancelMail for User " + member.getEmail() + " : " + e.getMessage());
			}
		}
		
		// Deleting invitation, if exists
		Invitations inv = member.getInvitation();
		
		if(inv != null){
			inv.setDeleted("true");
			updateInvitation(inv);
		}
		
	}
	//-----------------------------------------------------------------------------------------------
	

	/**
	 * @author becherer
	 * @param ment
	 * @param member
	 */
	//-----------------------------------------------------------------------------------------------
	public void updateInvitation(Appointment appointment, MeetingMember member,
				Long canceling_user_id, Long language_id, String invitorName){
		
		log.debug("updateInvitation");
		
		Users user;
		
		try{
			user= Usermanagement.getInstance().getUserById(canceling_user_id);
		}catch(Exception e){
			log.error("Cancelling user cant be retrieved");
			return;
		}
		
		if(appointment.getRemind() == null ){
			log.error("Appointment " + appointment.getAppointmentName() + " has no ReminderType!");
			return;
		}
		
		log.debug("Remindertype : " + appointment.getRemind().getTypId());
		
		Users us = member.getUserid();
			
		String jNameTimeZone = null;
		if (us != null && us.getOmTimeZone() != null) {
			jNameTimeZone = us.getOmTimeZone().getJname();
		} else {
			Configuration conf = Configurationmanagement.getInstance().getConfKey(3L, "default.timezone");
			if (conf != null) {
				jNameTimeZone = conf.getConf_value();
			}
		}
		
		OmTimeZone omTimeZone = OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jNameTimeZone);
		
		String timeZoneName = omTimeZone.getIcal();
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone(timeZoneName));
		int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
		
		Date starttime = new Date(appointment.getAppointmentStarttime().getTime() + offset);
		Date endtime = new Date(appointment.getAppointmentEndtime().getTime() + offset);
		
		
		Fieldlanguagesvalues labelid1155 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1155), language_id);
		
		String message = labelid1155.getValue() + " " + appointment.getAppointmentName();
		
		if (appointment.getAppointmentDescription().length() != 0) {
			
			Fieldlanguagesvalues labelid1152 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1152), language_id);
			message += labelid1152.getValue() + appointment.getAppointmentDescription();
			
		}
		
		Fieldlanguagesvalues labelid1153 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1153), language_id);
		Fieldlanguagesvalues labelid1154 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1154), language_id);
		
		message += "<br/>" + labelid1153.getValue() + ' ' 
						+ CalendarPatterns.getDateWithTimeByMiliSeconds(starttime) 
						+ " (" + timeZoneName + ")"
						+ "<br/>";
		
		message += labelid1154.getValue() + ' ' 
						+ CalendarPatterns.getDateWithTimeByMiliSeconds(endtime) 
						+ " (" + timeZoneName + ")"
						+ "<br/>";
		
		Fieldlanguagesvalues labelid1156 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(1156), language_id);
		message += labelid1156.getValue() + invitorName + "<br/>";
		
		// checking reminderType
		if(appointment.getRemind().getTypId() == 1){
			log.debug("no remindertype defined -> no cancel of invitation");
		}
		else if(appointment.getRemind().getTypId() == 2){
			log.debug("ReminderType simple mail -> sending simple mail...");
			sendInvitationUpdateMail(member.getEmail(), appointment, user.getAdresses().getEmail(),
					labelid1155.getValue() + " " + appointment.getAppointmentName(), message);
		}
		else if(appointment.getRemind().getTypId() == 3){
			try{
				sendInvitationIcalUpdateMail(member.getEmail(), member.getFirstname() + " " + member.getLastname(), 
						appointment, canceling_user_id, member.getInvitor(), language_id,
						starttime, endtime, jNameTimeZone, 
						labelid1155.getValue() + " " + appointment.getAppointmentName(), message);
			}catch(Exception e){
				log.error("Error sending IcalUpdateMail for User " + member.getEmail() + " : " + e.getMessage());
			}
		}
		
	}
	//-----------------------------------------------------------------------------------------------
	
	
	/**
	 * @author o.becherer
	 * @param user_level
	 * @param username
	 * @param message
	 * @param baseurl
	 * @param email
	 * @param subject
	 * @param rooms_id
	 * @param conferencedomain
	 * @param isPasswordProtected
	 * @param invitationpass
	 * @param valid
	 * @param validFrom
	 * @param validTo
	 * @param createdBy
	 * @return
	 */
	//---------------------------------------------------------------------------------------------------------
	public Long addInvitationIcalLink(Long user_level, String username, String message,
			String baseurl, String email, String subject, Long rooms_id, String conferencedomain,
			Boolean isPasswordProtected, String invitationpass, Integer valid,
			Date validFrom, Date validTo, Long createdBy, Long appointMentId, Boolean invitor,
			Long language_id, String jNameTimeZone, Date gmtTimeStart, Date gmtTimeEnd,
			Long appointmentId){
			log.debug("addInvitationIcalLink");
			
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				
				Invitations invitation = new Invitations();
				invitation.setIsPasswordProtected(isPasswordProtected);
				if (isPasswordProtected){
					invitation.setInvitationpass(ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(invitationpass));
				}
				
				invitation.setInvitationWasUsed(false);
				
				//valid period of Invitation
				if (valid == 1) {
					//endless
					invitation.setIsValidByTime(false);
					invitation.setCanBeUsedOnlyOneTime(false);
				} else if (valid == 2){
					//period
					invitation.setIsValidByTime(true);
					invitation.setCanBeUsedOnlyOneTime(false);
					
					//This has to be in the Server's time cause otherwise it is not 
					//in the correct time-zone for the comparison later on if the invitation is still valid
					// and subtract 5 minutes for users to access early
					
//					Calendar cal = Calendar.getInstance();
//					int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
//					
//					log.debug("addAppointment offset :: "+offset);
//					
//					Date appointmentstart = new Date(gmtTimeStart.getTime() + offset);
//					Date appointmentend = new Date(gmtTimeEnd.getTime() + offset);
//					
//					Date gmtTimeStartShifted = new Date(appointmentstart.getTime() - ( 5 * 60 * 1000 ) );
					
					Date gmtTimeStartShifted = new Date(gmtTimeStart.getTime() - ( 5 * 60 * 1000 ) );
					
					invitation.setValidFrom(gmtTimeStartShifted);
					invitation.setValidTo(gmtTimeEnd);	
				} else {
					//one-time
					invitation.setIsValidByTime(false);
					invitation.setCanBeUsedOnlyOneTime(true);
					invitation.setInvitationWasUsed(false);
				}
				
				invitation.setDeleted("false");
				
				Users us = UsersDaoImpl.getInstance().getUser(createdBy);
				String hashRaw = "InvitationHash"+(System.currentTimeMillis());
				log.debug("addInvitationIcalLink : rawHash = " + hashRaw);
				invitation.setHash(MD5.do_checksum(hashRaw));
				
				invitation.setInvitedBy(us);
				invitation.setBaseUrl(baseurl);
				invitation.setInvitedname(username);
				invitation.setInvitedEMail(email);
				invitation.setRoom(Roommanagement.getInstance().getRoomById(rooms_id));
				invitation.setConferencedomain(conferencedomain);
				invitation.setStarttime(new Date());
				invitation.setAppointmentId(appointmentId);
				
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				invitation = session.merge(invitation);
				session.flush();
				long invitationId = invitation.getInvitations_id();
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				if (invitationId > 0) {
					this.sendInvitionIcalLink(username, message, baseurl,
							email, subject, invitation.getHash(),
							appointMentId, createdBy, invitor, language_id, 
							validFrom, validTo, jNameTimeZone);
					return invitationId;
				}
			}
		} catch (Exception err){
			log.error("addInvitationIcalLink",err);
		}
		return null;
	}	

	//---------------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 */
	private String sendInvitionLink(String username, String message, 
			String baseurl, String email, String subject, 
			String invitationsHash, Date dStart, Date dEnd, Long language_id){
		try {
				
			String invitation_link = baseurl+"?invitationHash="+invitationsHash;
			
//			Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
//	        		getConfKey(3,"default_lang_id").getConf_value()).longValue();
			
			String template = InvitationTemplate.getInstance().getRegisterInvitationTemplate(username, message, invitation_link, language_id, dStart, dEnd);
		
			System.out.println(dStart);
			System.out.println(dEnd);
			
			System.out.println(template);
			
			return MailHandler.sendMail(email, subject, template);

		} catch (Exception err){
			log.error("sendInvitationLink",err);
		}
		return null;
	}
	
	
	
	/**
	 * @author o.becherer
	 * @param userName
	 * @param message
	 * @param baseUrl
	 * @param email
	 * @param subject
	 * @param invitationHash
	 * @return
	 */
	//----------------------------------------------------------------------------------------------------
	public String sendInvitationReminderLink(String message, String baseUrl, String email, String subject, String invitationHash){
		log.debug("sendInvitationReminderLink");
		
		try{
			String invitation_link = baseUrl+"?invitationHash="+invitationHash;
			
			message += "<br/>";
			message += "<a href='" + invitation_link + "'>Click here to enter room</a>";
			
			return MailHandler.sendMail(email, subject, message);
		}catch(Exception e){
			log.error("sendInvitationReminderLink",e);
		}
		
		return null;
	}
	//----------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 * @param email
	 * @param point
	 * @param cancelling_person
	 * @return
	 */
	//--------------------------------------------------------------------------------------------------------------
	private String sendInvitationCancelMail(String email, Appointment point, String cancelling_person, 
			String subject, String message){
		log.debug("sendInvitationCancelmail");
		try{
			 return MailHandler.sendMail(email, subject, message);
		}catch(Exception e){
			log.error("sendInvitationCancelmail : " + e.getMessage());
		}
		
		return null;
	}
	//--------------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param email
	 * @param point
	 * @param cancelling_person
	 * @return
	 */
	//--------------------------------------------------------------------------------------------------------------
	private String sendInvitationUpdateMail(String email, Appointment point, 
			String cancelling_person, String subject, String message){
		log.debug("sendInvitationUpdateMail");
		try{
			 return MailHandler.sendMail(email, subject, message);
		}catch(Exception e){
			log.error("sendInvitationUpdateMail : " + e.getMessage());
		}
		
		return null;
	}
	//--------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 * @param email
	 * @param point
	 * @param cancelling_person
	 * @return
	 */
	//--------------------------------------------------------------------------------------------------------------
	private String sendInvitationIcalCancelMail(String email, String userName, Appointment point, 
			Long organizer_userId, Boolean invitor, Date startdate, Date enddate, 
			String jNameTimeZone, String subject, String message) throws Exception{
		log.debug("sendInvitationIcalCancelMail");
		
		
		// Defining Organizer
		Users user = Usermanagement.getInstance().getUserById(organizer_userId);
		
		OmTimeZone omTimeZone = OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jNameTimeZone);
		
		
		IcalHandler handler = new IcalHandler(IcalHandler.ICAL_METHOD_CANCEL);
		
		// refresh appointment
		point = AppointmentLogic.getInstance().getAppointMentById(point.getAppointmentId());
		
		// Transforming Meeting Members
		
		HashMap<String, String> attendeeInDerHashMap = handler.getAttendeeData(email, userName, invitor);
		
		Vector<HashMap<String, String>> atts = new Vector<HashMap<String,String>>();
		atts.add(attendeeInDerHashMap);
		
	
		HashMap<String, String> attendeeList = handler.getAttendeeData(user.getAdresses().getEmail(), user.getLogin(), invitor);
		
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(startdate);
		
		GregorianCalendar end = new GregorianCalendar();
		end.setTime(enddate);
		
		String meetingId = handler.addNewMeeting(start, end, point.getAppointmentName(), atts, 
				subject, attendeeList, point.getIcalId(), jNameTimeZone);
		
		
		log.debug(handler.getICalDataAsString());
		
		MailHandler.sendIcalMessage(email, subject, handler.getIcalAsByteArray(), message);
		
		return null;
	}
	//--------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 * @param email
	 * @param point
	 * @param cancelling_person
	 * @return
	 */
	//--------------------------------------------------------------------------------------------------------------
	private String sendInvitationIcalUpdateMail(String email, String userName, 
			Appointment point, Long organizer_userId, Boolean invitor,
			Long language_id, Date starttime, Date endtime, String jNameTimeZone,
			String subject, String message) throws Exception{
		log.debug("sendInvitationIcalUpdateMail");
		
		
		// Defining Organizer
		Users user = Usermanagement.getInstance().getUserById(organizer_userId);
		
		IcalHandler handler = new IcalHandler(IcalHandler.ICAL_METHOD_REQUEST);
		
		// refresh appointment
		point = AppointmentLogic.getInstance().getAppointMentById(point.getAppointmentId());
		
		// Transforming Meeting Members
		
		HashMap<String, String> attendeeInDerHashMap = handler.getAttendeeData(email, userName, invitor);
		
		Vector<HashMap<String, String>> atts = new Vector<HashMap<String,String>>();
		atts.add(attendeeInDerHashMap);
		
	
		HashMap<String, String> attendeeList = handler.getAttendeeData(user.getAdresses().getEmail(), user.getLogin(), invitor);
		
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(starttime);
		
		GregorianCalendar end = new GregorianCalendar();
		end.setTime(endtime);
		
		String meetingId = handler.addNewMeeting(start, end, point.getAppointmentName(), atts, 
				subject, attendeeList, point.getIcalId(), jNameTimeZone);
		
		log.debug(handler.getICalDataAsString());
		
		MailHandler.sendIcalMessage(email, subject, handler.getIcalAsByteArray(), message);
		
		return null;
	}
	//--------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 * @param username
	 * @param message
	 * @param baseurl
	 * @param email
	 * @param subject
	 * @param invitationsHash
	 * @return
	 */
	private String sendInvitionIcalLink(String username, String message, 
			String baseurl, String email, String subject, String invitationsHash, 
			Long appointMentId, Long organizer_userId, Boolean invitor, 
			Long language_id, Date starttime, Date endtime, String jNametimeZone){
		try {
				
			String invitation_link = baseurl+"?invitationHash="+invitationsHash;
			
//			Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
//	        		getConfKey(3,"default_lang_id").getConf_value()).longValue();
			String template = InvitationTemplate.getInstance().getRegisterInvitationTemplate(username, message, invitation_link, language_id, starttime, endtime);
		
			IcalHandler handler = new IcalHandler(IcalHandler.ICAL_METHOD_REQUEST);
			
			Appointment point = AppointmentLogic.getInstance().getAppointMentById(appointMentId);
			
			// Transforming Meeting Members
			
			HashMap<String, String> attendeeList = handler.getAttendeeData(email, username, invitor);
			
			Vector<HashMap<String, String>> atts = new Vector<HashMap<String,String>>();
			atts.add(attendeeList);
			
			// Defining Organizer
			Users user = Usermanagement.getInstance().getUserById(organizer_userId);
			
			HashMap<String, String> organizerAttendee = handler.getAttendeeData(email, username, invitor);
			if (user != null) {
				organizerAttendee = handler.getAttendeeData(user.getAdresses().getEmail(), user.getLogin(), invitor);
			}			
			
			GregorianCalendar start = new GregorianCalendar();
			start.setTime(starttime); //Must be the calculated date base on the time zone
			
			GregorianCalendar end = new GregorianCalendar();
			end.setTime(endtime); //Must be the calculated date base on the time zone
			
			//Create ICal Message
			String meetingId = handler.addNewMeeting(start, end, point.getAppointmentName(), 
					atts, invitation_link, organizerAttendee, 
					point.getIcalId(), jNametimeZone);
			
			// Writing back meetingUid
			if(point.getIcalId() == null || point.getIcalId().length() < 1){
				point.setIcalId(meetingId);
				
				AppointmentLogic.getInstance().updateAppointMent(point);
			}
			
			log.debug(handler.getICalDataAsString());
			
			MailHandler.sendIcalMessage(email, subject, handler.getIcalAsByteArray(), template);
			
			return "success";
			//return MailHandler.sendMail(email, subject, template);

		} catch (Exception err){
			log.error("sendInvitionIcalLink",err);
		}
		return null;
	}
	
	/**
	 * 
	 * @param user_level
	 * @param username
	 * @param message
	 * @param domain
	 * @param room
	 * @param roomtype
	 * @param baseurl
	 * @param email
	 * @param subject
	 * @param room_id
	 * @return
	 */
	public String sendInvitionLink(Long user_level, String username, String message, String domain, String room, 
			String roomtype, String baseurl, String email, String subject, Long room_id, Date starttime, Date endtime){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				
				String invitation_link = baseurl+"?lzproxied=solo&lzr=swf8&lzt=swf&domain="+domain+"&room="+room+"&roomtype="+roomtype+"&email="+email+"&roomid="+room_id;
				
				Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
		        		getConfKey(3,"default_lang_id").getConf_value()).longValue();
				
				String template = InvitationTemplate.getInstance().getRegisterInvitationTemplate(
									username, message, invitation_link, default_lang_id, 
									starttime, endtime);
			
				return MailHandler.sendMail(email, subject, template);

			}
		} catch (Exception err){
			log.error("sendInvitationLink",err);
		}
		return null;
	}
	
	/**
	 * @author becherer
	 * @param invId
	 * 
	 */
	public Invitations getInvitationbyId(Long invId){
		log.debug("getInvitationbyId");
		
		
		try{
			String hql = "select invi from Invitations invi " +
			"WHERE invi.deleted <> :deleted " +
			"AND invi.invitations_id = :invid";
	
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("invid",invId);
	
			Invitations inv = null;
			try {
				inv = (Invitations) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
		    session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
	
			return inv;
		}catch(Exception e){
			log.error("getInvitationsbyId : " , e);
			return null;
		}
	}
	
	public Invitations getInvitationbyAppointementId(Long invId){
		log.debug("getInvitationbyId");
		
		
		try{
			String hql = "select invi from Invitations invi " +
						"WHERE invi.deleted <> :deleted " +
						"AND invi.invitations_id = :invid";
				
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("invid",invId);
	
			Invitations inv = null;
			try {
				inv = (Invitations) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
		    session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
	
			return inv;
		}catch(Exception e){
			log.error("getInvitationsbyId : " , e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param hashCode
	 * @param hidePass
	 * @return
	 */
	public Object getInvitationByHashCode(String hashCode, boolean hidePass) {
		try {
			String hql = "select c from Invitations as c " +
					"where c.hash LIKE :hashCode " +
					"AND c.deleted = :deleted";
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("hashCode", hashCode);
			query.setParameter("deleted", "false");
			Invitations invitation = null;
			try {
				invitation = (Invitations) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (invitation == null) {
				//already deleted or does not exist
				return new Long(-31);
			} else {
				if (invitation.getCanBeUsedOnlyOneTime()){
					
					//do this only if the user tries to get the Invitation, not while checking the PWD
					if (hidePass) {
						//one-time invitation
						if (invitation.getInvitationWasUsed()){
							//Invitation is of type *only-one-time* and was already used
							return new Long(-32);
						} else {
							//set to true if this is the first time / a normal getInvitation-Query
							invitation.setInvitationWasUsed(true);
							this.updateInvitation(invitation);
							//invitation.setInvitationpass(null);
							return invitation;
						}
					} else {
						return invitation;
					}

				} else if (invitation.getIsValidByTime()){
					Date today = new Date();
					
					Calendar cal = Calendar.getInstance();
					int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
					
					log.debug("addAppointment offset :: "+offset);
					
					Date appointmentstart = new Date(invitation.getValidFrom().getTime() + offset);
					Date appointmentend = new Date(invitation.getValidTo().getTime() + offset);
					
					if (appointmentstart.getTime() <= today.getTime() 
							&& appointmentend.getTime() >= today.getTime()) {
						this.updateInvitation(invitation);
						//invitation.setInvitationpass(null);
						return invitation;
					} else {
						//Invitation is of type *period* and is not valid anymore
						return new Long(-50);
					}
				} else {
					//Invitation is not limited, neither time nor single-usage
					this.updateInvitation(invitation);
					//invitation.setInvitationpass(null);
					return invitation;
				}
			}
			
		} catch (Exception err) {
			log.error("[getInvitationByHashCode]",err);
		}
		return new Long(-1);
	}
	
	/**
	 * 
	 * @param invitation
	 */
	public void updateInvitation(Invitations invitation){
		try {
			invitation.setUpdatetime(new Date());
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (invitation.getInvitations_id() == null) {
				session.persist(invitation);
			    } else {
			    	if (!session.contains(invitation)) {
			    		session.merge(invitation);
			    }
			}
			tx.commit();
			HibernateUtil.closeSession(idf);		
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] ",ex2);
		}
	}
	/**
	 * 
	 * @param hashCode
	 * @param pass
	 * @return
	 */
	public Object checkInvitationPass(String hashCode, String pass){
		try {
			Object obj = this.getInvitationByHashCode(hashCode, false);
			log.debug("checkInvitationPass - obj: "+obj);
			if (obj instanceof Invitations){
				Invitations invitation = (Invitations) obj;
				
//				log.debug("invitationId "+invitation.getInvitations_id());
//				log.debug("pass "+pass);
//				log.debug("getInvitationpass "+invitation.getInvitationpass());
				
				if (ManageCryptStyle.getInstance().getInstanceOfCrypt().verifyPassword(pass, invitation.getInvitationpass())){
					return new Long(1);
				} else {
					return new Long(-34);
				}
			} else {
				return obj;
			}
		} catch (Exception ex2) {
			log.error("[checkInvitationPass] ",ex2);
		}
		return new Long(-1);
	}

	public void updateInvitationByAppointment(Long appointmentId,
			Date appointmentstart, Date appointmentend) {
		try {
			
			Date gmtTimeStartShifted = new Date(appointmentstart.getTime() - ( 5 * 60 * 1000 ) );
			
			String hql = "select a from Invitations a " +					
							"WHERE a.appointmentId = :appointmentId  ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("appointmentId",appointmentId);
			
			List<Invitations> listInvitations = query.getResultList();
			
			
			for (Invitations inv : listInvitations) {
				inv.setValidFrom(gmtTimeStartShifted);
				inv.setValidTo(appointmentend);
				if (inv.getInvitations_id() == null) {
					session.persist(inv);
				    } else {
				    	if (!session.contains(inv)) {
				    		session.merge(inv);
				    }
				}
			}
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (Exception err) {
			
		}
	}
}
