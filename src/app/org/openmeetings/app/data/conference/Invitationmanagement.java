package org.openmeetings.app.data.conference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.templates.InvitationTemplate;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.calendar.management.MeetingMemberLogic;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.openmeetings.app.hibernate.beans.invitation.Invitations;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.utils.crypt.MD5;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.openmeetings.utils.mail.IcalHandler;
import org.openmeetings.utils.mail.MailHandler;

/**
 * 
 * @author swagner
 *
 */
public class Invitationmanagement {
	
	private static final Logger log = Red5LoggerFactory.getLogger(Invitationmanagement.class, "openmeetings");

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
	public Long addInvitationLink(Long user_level, String username, String message,
			String baseurl, String email, String subject, Long rooms_id, String conferencedomain,
			Boolean isPasswordProtected, String invitationpass, Integer valid,
			Date validFrom, Date validTo, Long createdBy, String baseUrl
			){
			String validFromString = "";
			String validToString = "";
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
					invitation.setValidFrom(validFrom);
					invitation.setValidTo(validTo);		
					validFromString = dateFormat.format(validFrom);
					validToString = dateFormat.format(validTo);
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
				
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				long invitationId = (Long) session.save(invitation);
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				if (invitationId>0){
					this.sendInvitionLink(username, message, baseurl, email, subject, invitation.getHash(), validFromString, validToString);
					return invitationId;
				}
				
			}
		} catch (HibernateException ex) {
			log.error("[addInvitationLink] ",ex);
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
	public void cancelInvitation(Appointment ment, MeetingMember member,Long canceling_user_id){
		
		log.debug("cancelInvitation");
		
		Users user;
		
		try{
			user= Usermanagement.getInstance().getUserById(canceling_user_id);
		}catch(Exception e){
			log.error("Cancelling user cant be retrieved");
			return;
		}
		
		if(ment.getRemind() == null ){
			log.error("Appointment " + ment.getAppointmentName() + " has no ReminderType!");
			return;
		}
		
		log.debug("Remindertype : " + ment.getRemind().getTypId());
		
		// checking reminderType
		if(ment.getRemind().getTypId() == 1){
			log.debug("no remindertype defined -> no cancel of invitation");
		}
		else if(ment.getRemind().getTypId() == 2){
			log.debug("ReminderType simple mail -> sending simple mail...");
			sendInvitationCancelMail(member.getEmail(), member.getAppointment(), user.getAdresses().getEmail());
		}
		else if(ment.getRemind().getTypId() == 3){
			try{
				sendInvitationIcalCancelMail(member.getEmail(), member.getFirstname() + " " + member.getLastname(), ment, canceling_user_id, member.getInvitor());
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
	public void updateInvitation(Appointment ment, MeetingMember member,Long canceling_user_id){
		
		log.debug("updateInvitation");
		
		Users user;
		
		try{
			user= Usermanagement.getInstance().getUserById(canceling_user_id);
		}catch(Exception e){
			log.error("Cancelling user cant be retrieved");
			return;
		}
		
		if(ment.getRemind() == null ){
			log.error("Appointment " + ment.getAppointmentName() + " has no ReminderType!");
			return;
		}
		
		log.debug("Remindertype : " + ment.getRemind().getTypId());
		
		// checking reminderType
		if(ment.getRemind().getTypId() == 1){
			log.debug("no remindertype defined -> no cancel of invitation");
		}
		else if(ment.getRemind().getTypId() == 2){
			log.debug("ReminderType simple mail -> sending simple mail...");
			sendInvitationUpdateMail(member.getEmail(), ment, user.getAdresses().getEmail());
		}
		else if(ment.getRemind().getTypId() == 3){
			try{
				sendInvitationIcalUpdateMail(member.getEmail(), member.getFirstname() + " " + member.getLastname(), ment, canceling_user_id, member.getInvitor());
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
			Date validFrom, Date validTo, Long createdBy, Long appointMentId, Boolean invitor
			){
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
					invitation.setValidFrom(validFrom);
					invitation.setValidTo(validTo);					
				} else {
					//one-time
					invitation.setIsValidByTime(false);
					invitation.setCanBeUsedOnlyOneTime(true);
					invitation.setInvitationWasUsed(false);
				}
				
				invitation.setDeleted("false");
				
				Users us = UsersDaoImpl.getInstance().getUser(createdBy);
				String hashRaw = us.getLogin()+us.getUser_id()+(System.currentTimeMillis());
				log.debug("addInvitationIcalLink : rawHash = " + hashRaw);
				invitation.setHash(MD5.do_checksum(hashRaw));
				
				invitation.setInvitedBy(us);
				invitation.setInvitedname(username);
				invitation.setInvitedEMail(email);
				invitation.setRoom(Roommanagement.getInstance().getRoomById(rooms_id));
				invitation.setConferencedomain(conferencedomain);
				invitation.setStarttime(new Date());
				
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				long invitationId = (Long) session.save(invitation);
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				if (invitationId>0){
					 this.sendInvitionIcalLink(username, message, baseurl, email, subject, invitation.getHash(), appointMentId, createdBy, invitor);
					 return invitationId;
				}
			}
		} catch (HibernateException ex) {
			log.error("[addInvitationLink] "+ex);
		} catch (Exception err){
			log.error("addInvitationLink",err);
		}
		return null;
	}	

	//---------------------------------------------------------------------------------------------------------
	
	
	/**
	 * 
	 */
	private String sendInvitionLink(String username, String message, 
			String baseurl, String email, String subject, String invitationsHash, String dStart, String dEnd){
		try {
				
			String invitation_link = baseurl+"?lzproxied=solo&invitationHash="+invitationsHash;
			
			Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
	        		getConfKey(3,"default_lang_id").getConf_value()).longValue();
			
			String template = InvitationTemplate.getInstance().getRegisterInvitationTemplate(username, message, invitation_link, default_lang_id, dStart, dEnd);
		
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
	public String sendInvitationReminderLink(String userName, String message, String baseUrl, String email, String subject, String invitationHash){
		log.debug("sendInvitationReminderLink");
		
		try{
			String invitation_link = baseUrl+"?lzproxied=solo&lzr=swf8&lzt=swf&invitationHash="+invitationHash;
			
			Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().getConfKey(3,"default_lang_id").getConf_value()).longValue();
			
			//String template = InvitationTemplate.getInstance().getReminderInvitationTemplate(userName, message, invitation_link, default_lang_id);
			//TODO Velocity Template cant be found when running from QuartzJob
			
			String template = "";
			template +="<html>";
			template += "<body>";
			template +="<b>OpenMeetings Meeting Reminder</b><br><br>";
			template +=message + "<br><br>";
			template += "<a href='" + invitation_link + "'>Click here to enter room</a>";
			
			
			return MailHandler.sendMail(email, subject, template);
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
	private String sendInvitationCancelMail(String email, Appointment point, String cancelling_person){
		log.debug("sendInvitationCancelmail");
		
		String subject = "Cancelled OpenMeetings Appointment " + point.getAppointmentName();
		
		String message = "<html><body>Your Appointment " + point.getAppointmentName() + " has been cancelled by " + cancelling_person;
		message += "<br><br>";
		message += "Appointment : " + point.getAppointmentName() + "<br>";
		message += "Start Time : " + point.getAppointmentStarttime() + "<br>";
		message += "End Time : " + point.getAppointmentEndtime() + "<br>";
		message += "</body></html>";
		
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
	private String sendInvitationUpdateMail(String email, Appointment point, String cancelling_person){
		log.debug("sendInvitationUpdateMail");
		
		String subject = "Update of OpenMeetings Appointment " + point.getAppointmentName();
		
		String message = "<html><body>Your Appointment " + point.getAppointmentName() + " has been updated by " + cancelling_person;
		message += "<br><br>";
		message += "<b>Appointment : " + point.getAppointmentName() + "</b><br>";
		message += "Descrition : " + point.getAppointmentDescription() + "</br>";
		message += "Start Time : " + point.getAppointmentStarttime() + "<br>";
		message += "End Time : " + point.getAppointmentEndtime() + "<br>";
		message += "</body></html>";
		
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
	private String sendInvitationIcalCancelMail(String email, String userName, Appointment point, Long organizer_userId, Boolean invitor) throws Exception{
		log.debug("sendInvitationIcalCancelMail");
		
		
		// Defining Organizer
		Users user = Usermanagement.getInstance().getUserById(organizer_userId);
		
		String subject = "Cancelled OpenMeetings Appointment " + point.getAppointmentName();
		
		String message = "<html><body>Your Appointment " + point.getAppointmentName() + " has been cancelled by " + user.getAdresses().getEmail();
		message += "<br><br>";
		message += "Appointment : " + point.getAppointmentName() + "<br>";
		message += "Start Time : " + point.getAppointmentStarttime() + "<br>";
		message += "End Time : " + point.getAppointmentEndtime() + "<br>";
		message += "</body></html>";
		
		IcalHandler handler = new IcalHandler(IcalHandler.ICAL_METHOD_CANCEL);
		
		// refresh appointment
		point = AppointmentLogic.getInstance().getAppointMentById(point.getAppointmentId());
		
		// Transforming Meeting Members
		
		HashMap<String, String> dusselInDerHashMap = handler.getAttendeeData(email, userName, invitor);
		
		Vector<HashMap<String, String>> atts = new Vector<HashMap<String,String>>();
		atts.add(dusselInDerHashMap);
		
	
		HashMap<String, String> oberDussel = handler.getAttendeeData(user.getAdresses().getEmail(), user.getLogin(), invitor);
		
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(point.getAppointmentStarttime());
		
		GregorianCalendar end = new GregorianCalendar();
		end.setTime(point.getAppointmentEndtime());
		
		String meetingId = handler.addNewMeeting(start, end, point.getAppointmentName(), atts, "Canceled OpenMeetings Appointment : " + point.getAppointmentName(), oberDussel, point.getIcalId());
		
		
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
	private String sendInvitationIcalUpdateMail(String email, String userName, Appointment point, Long organizer_userId, Boolean invitor) throws Exception{
		log.debug("sendInvitationIcalUpdateMail");
		
		
		// Defining Organizer
		Users user = Usermanagement.getInstance().getUserById(organizer_userId);
		
		String subject = "Update of OpenMeetings Appointment " + point.getAppointmentName();
		
		String message = "<html><body>Your Appointment " + point.getAppointmentName() + " has been updated by " + user.getAdresses().getEmail();
		message += "<br><br>";
		message += "<b>Appointment : " + point.getAppointmentName() + "</b><br>";
		message += "Description : " + point.getAppointmentDescription() + "<br>";
		message += "Start Time : " + point.getAppointmentStarttime() + "<br>";
		message += "End Time : " + point.getAppointmentEndtime() + "<br>";
		message += "</body></html>";
		
		IcalHandler handler = new IcalHandler(IcalHandler.ICAL_METHOD_REQUEST);
		
		// refresh appointment
		point = AppointmentLogic.getInstance().getAppointMentById(point.getAppointmentId());
		
		// Transforming Meeting Members
		
		HashMap<String, String> dusselInDerHashMap = handler.getAttendeeData(email, userName, invitor);
		
		Vector<HashMap<String, String>> atts = new Vector<HashMap<String,String>>();
		atts.add(dusselInDerHashMap);
		
	
		HashMap<String, String> oberDussel = handler.getAttendeeData(user.getAdresses().getEmail(), user.getLogin(), invitor);
		
		GregorianCalendar start = new GregorianCalendar();
		start.setTime(point.getAppointmentStarttime());
		
		GregorianCalendar end = new GregorianCalendar();
		end.setTime(point.getAppointmentEndtime());
		
		String meetingId = handler.addNewMeeting(start, end, point.getAppointmentName(), atts, "Update of OpenMeetings Appointment : " + point.getAppointmentName(), oberDussel, point.getIcalId());
		
		
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
			String baseurl, String email, String subject, String invitationsHash, Long appointMentId, Long organizer_userId, Boolean invitor){
		try {
				
			String invitation_link = baseurl+"?lzproxied=solo&lzr=swf8&lzt=swf&invitationHash="+invitationsHash;
			
			Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
	        		getConfKey(3,"default_lang_id").getConf_value()).longValue();
			String template = InvitationTemplate.getInstance().getRegisterInvitationTemplate(username, message, invitation_link, default_lang_id, "", "");
		
			IcalHandler handler = new IcalHandler(IcalHandler.ICAL_METHOD_REQUEST);
			
			Appointment point = AppointmentLogic.getInstance().getAppointMentById(appointMentId);
			
			// Transforming Meeting Members
			
			HashMap<String, String> dusselInDerHashMap = handler.getAttendeeData(email, username, invitor);
			
			Vector<HashMap<String, String>> atts = new Vector<HashMap<String,String>>();
			atts.add(dusselInDerHashMap);
			
			// Defining Organizer
			Users user = Usermanagement.getInstance().getUserById(organizer_userId);
			
			HashMap<String, String> oberDussel = handler.getAttendeeData(user.getAdresses().getEmail(), user.getLogin(), invitor);
			
			GregorianCalendar start = new GregorianCalendar();
			start.setTime(point.getAppointmentStarttime());
			
			GregorianCalendar end = new GregorianCalendar();
			end.setTime(point.getAppointmentEndtime());
			
			String meetingId = handler.addNewMeeting(start, end, point.getAppointmentName(), atts, invitation_link, oberDussel, point.getIcalId());
			
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
			String roomtype, String baseurl, String email, String subject, Long room_id){
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)){
				
				String invitation_link = baseurl+"?lzproxied=solo&lzr=swf8&lzt=swf&domain="+domain+"&room="+room+"&roomtype="+roomtype+"&email="+email+"&roomid="+room_id;
				
				Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
		        		getConfKey(3,"default_lang_id").getConf_value()).longValue();
				
				String template = InvitationTemplate.getInstance().getRegisterInvitationTemplate(username, message, invitation_link, default_lang_id, "", "");
			
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
			"WHERE invi.deleted != :deleted " +
			"AND invi.invitations_id = :invid";
	
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setBoolean("deleted", true);
			query.setLong("invid",invId);
	
			Invitations inv = (Invitations) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
	
			return inv;
		}catch(Exception e){
			log.error("getInvitationsbyId : " + e.getMessage());
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
					"where c.hash = :hashCode " +
					"AND c.deleted = :deleted";
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("hashCode", hashCode);
			query.setString("deleted", "false");
			Invitations invitation = (Invitations) query.uniqueResult();
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
							if (hidePass) invitation.setInvitationpass(null);
							return invitation;
						}
					} else {
						return invitation;
					}

				} else if (invitation.getIsValidByTime()){
					Date today = new Date();
					if (invitation.getValidFrom().getTime() <= today.getTime() 
							&& invitation.getValidTo().getTime() >= today.getTime()) {
						this.updateInvitation(invitation);
						if (hidePass) invitation.setInvitationpass(null);
						return invitation;
					} else {
						//Invitation is of type *period* and is not valid anymore
						return new Long(-33);
					}
				} else {
					//Invitation is not limited, neither time nor single-usage
					this.updateInvitation(invitation);
					if (hidePass) invitation.setInvitationpass(null);
					return invitation;
				}
			}
			
		} catch (HibernateException ex) {
			log.error("[getInvitationByHashCode] "+ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(invitation);
			tx.commit();
			HibernateUtil.closeSession(idf);		
		} catch (HibernateException ex) {
			log.error("[selectMaxFromUsers] "+ex);
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] "+ex2);
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
			if (obj instanceof Invitations){
				Invitations invitation = (Invitations) obj;
				if (ManageCryptStyle.getInstance().getInstanceOfCrypt().verifyPassword(pass, invitation.getInvitationpass())){
					return new Long(1);
				} else {
					return new Long(-34);
				}
			} else {
				return obj;
			}
		} catch (Exception ex2) {
			log.error("[checkInvitationPass] "+ex2);
		}
		return new Long(-1);
	}
}
