package org.openmeetings.app.data.calendar.daos;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.calendar.management.MeetingMemberLogic;
import org.openmeetings.app.data.conference.Invitationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.openmeetings.app.hibernate.beans.lang.FieldLanguage;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

public class AppointmentDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentDaoImpl.class, "openmeetings");

	private AppointmentDaoImpl() {
	}

	private static AppointmentDaoImpl instance = null;

	public static synchronized AppointmentDaoImpl getInstance() {
		if (instance == null) {
			instance = new AppointmentDaoImpl();
		}

		return instance;
	}
	
	/*
	 * insert, update, delete, select
	 */
	
	
	
	/**
	 * @author o.becherer
	 * Retrievment of Appointment for room
	 */
	//-----------------------------------------------------------------------------------------------
	public Appointment getAppointmentByRoom(Long room_id) throws Exception{
		log.debug("AppointMentDaoImpl.getAppointmentByRoom");
		
		String hql = "select a from Appointment a " +
		"WHERE a.deleted != :deleted " +
		"AND a.room.rooms_id = :room_id ";
		
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery(hql);
		query.setString("deleted", "true");
		query.setLong("room_id",room_id);
		
		
		Appointment appoint = (Appointment) query.uniqueResult();
		tx.commit();
		HibernateUtil.closeSession(idf);
		
		return appoint;

	}
	//-----------------------------------------------------------------------------------------------
	
	public Appointment getAppointmentById(Long appointmentId) {
		try {
			
			String hql = "select a from Appointment a " +
					"WHERE a.deleted != :deleted " +
					"AND a.appointmentId = :appointmentId ";
					
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			query.setLong("appointmentId",appointmentId);
			
			
			Appointment appoint = (Appointment) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return appoint;
		} catch (HibernateException ex) {
			log.error("[getAppointmentById]: " , ex);
		} catch (Exception ex2) {
			log.error("[getAppointmentById]: " , ex2);
		}
		return null;
	}
	
	public List<Appointment> getAppointments() {
		try {
			
			String hql = "select a from Appointment a " +
					"WHERE a.deleted != :deleted ";
					
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			
			List<Appointment> appointList = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			for (Appointment appointment : appointList) {
			
				appointment.setMeetingMember(MeetingMemberDaoImpl.getInstance().getMeetingMemberByAppointmentId(appointment.getAppointmentId()));
			
			}
			
			return appointList;
			
		} catch (HibernateException ex) {
			log.error("[getAppointmentById]: " , ex);
		} catch (Exception ex2) {
			log.error("[getAppointmentById]: " , ex2);
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param appointmentName
	 * @param userId
	 * @param appointmentLocation
	 * @param appointmentDescription
	 * @param appointmentstart
	 * @param appointmentend
	 * @param isDaily
	 * @param isWeekly
	 * @param isMonthly
	 * @param isYearly
	 * @param categoryId
	 * @param remind
	 * @param room
	 * @return
	 */
	//----------------------------------------------------------------------------------------------------------------------------
	public Long addAppointment(String appointmentName, Long userId, String appointmentLocation,String appointmentDescription, 
			Date appointmentstart, Date appointmentend, 
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly, Boolean isYearly, 
			Long categoryId, Long remind, Rooms room, Long language_id, 
			Boolean isPasswordProtected, String password) {
		try {
			
			Appointment ap = new Appointment();
			
			ap.setAppointmentName(appointmentName);
			ap.setAppointmentLocation(appointmentLocation);
			ap.setAppointmentStarttime(appointmentstart);
		 	ap.setAppointmentEndtime(appointmentend);
			ap.setAppointmentDescription(appointmentDescription);
			ap.setRemind(AppointmentReminderTypDaoImpl.getInstance().getAppointmentReminderTypById(remind));
			ap.setStarttime(new Date());
			ap.setDeleted("false");
			ap.setIsDaily(isDaily);
			ap.setIsWeekly(isWeekly);
			ap.setIsMonthly(isMonthly);
			ap.setIsYearly(isYearly);
			ap.setLanguage_id(language_id);
			ap.setIsPasswordProtected(isPasswordProtected);
			ap.setPassword(password);
			ap.setUserId(UsersDaoImpl.getInstance().getUser(userId));
			ap.setAppointmentCategory(AppointmentCategoryDaoImpl.getInstance().getAppointmentCategoryById(categoryId));
			ap.setRoom(room);
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long appointment_id = (Long)session.save(ap);

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return appointment_id;
		} catch (HibernateException ex) {
			log.error("[addAppointment]: ",ex);
		} catch (Exception ex2) {
			log.error("[addAppointment]: ",ex2);
		}
		return null;
	}
	//----------------------------------------------------------------------------------------------------------------------------
	
	public Long updateAppointment(Appointment appointment) {
		if (appointment.getAppointmentId() > 0) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				session.update(appointment);
				tx.commit();
				HibernateUtil.closeSession(idf);
				return appointment.getAppointmentId();
			} catch (HibernateException ex) {
				log.error("[updateAppointment] ",ex);
			} catch (Exception ex2) {
				log.error("[updateAppointment] ",ex2);
			}
		} else {
			log.error("[updateAppointment] "+"Error: No AppointmentId given");
		}
		return null;
	}
	
	/**
	 * 
	 * @param appointmentId
	 * @param appointmentName
	 * @param appointmentDescription
	 * @param appointmentstart
	 * @param appointmentend
	 * @param isDaily
	 * @param isWeekly
	 * @param isMonthly
	 * @param isYearly
	 * @param categoryId
	 * @param remind
	 * @param mmClient
	 * @param users_id
	 * @return
	 */
	//----------------------------------------------------------------------------------------------------------
	public Long updateAppointment(Long appointmentId, String appointmentName, String appointmentDescription, 
			Date appointmentstart, Date appointmentend,
			Boolean isDaily, Boolean isWeekly, Boolean isMonthly, Boolean isYearly, 
			Long categoryId, Long remind, List mmClient, Long users_id, String baseUrl,
			Long language_id, Boolean isPasswordProtected, String password) {
		
			log.debug("AppointmentDAOImpl.updateAppointment");
		try {
			
			
			Appointment ap = this.getAppointmentById(appointmentId);
									
			ap.setAppointmentName(appointmentName);
			ap.setAppointmentStarttime(appointmentstart);
		 	ap.setAppointmentEndtime(appointmentend);
			ap.setAppointmentDescription(appointmentDescription);			
			ap.setUpdatetime(new Date());
			ap.setRemind(AppointmentReminderTypDaoImpl.getInstance().getAppointmentReminderTypById(remind));
			ap.setIsDaily(isDaily);
			ap.setIsWeekly(isWeekly);
			ap.setIsMonthly(isMonthly);
			ap.setIsYearly(isYearly);
			ap.setLanguage_id(language_id);
			ap.setIsPasswordProtected(isPasswordProtected);
			ap.setPassword(password);
			//ap.setUserId(UsersDaoImpl.getInstance().getUser(userId));
			ap.setAppointmentCategory(AppointmentCategoryDaoImpl.getInstance().getAppointmentCategoryById(categoryId));
						
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.update(ap);

			tx.commit();
		    HibernateUtil.closeSession(idf);
		    
		    
		    List<MeetingMember> meetingsRemoteMembers = MeetingMemberDaoImpl.getInstance().getMeetingMemberByAppointmentId(ap.getAppointmentId());
		    
		    
		    //to remove
		    for (MeetingMember memberRemote : meetingsRemoteMembers) {
		    	
	    		boolean found = false;
		    	
	    		if(mmClient != null){
	    			for (int i = 0; i < mmClient.size(); i++) {
	    				Map clientMemeber = (Map) mmClient.get(i);
	    				Long meetingMemberId = Long.valueOf(clientMemeber.get("meetingMemberId").toString()).longValue();
		    	
	    				if (memberRemote.getMeetingMemberId().equals(meetingMemberId)) {
	    					log.debug("AppointMentDAOImpl.updateAppointment  - member " + meetingMemberId + " is to be removed!");
	    					// Notifying Member for Update
	    					found = true;
	    				}
		    		
	    			}
	    		}
	    		
		    	if (!found) {
		    		
					//Not in List in client delete it
		    		MeetingMemberLogic.getInstance().deleteMeetingMember(memberRemote.getMeetingMemberId(), users_id);
		    		//MeetingMemberDaoImpl.getInstance().deleteMeetingMember(memberRemote.getMeetingMemberId());
		    	}
		    	else{
		    		// Notify member of changes
		    		Invitationmanagement.getInstance().updateInvitation(ap, memberRemote, users_id, language_id);
		    		
		    	}
		    }
		    
		    //add items
		    if(mmClient !=null){
		    	
			    for (int i = 0; i < mmClient.size(); i++) {
		    		
			    	Map clientMember = (Map)mmClient.get(i);
			    	
		    		Long meetingMemberId = Long.valueOf(clientMember.get("meetingMemberId").toString()).longValue();
		    	
		    		boolean found = false;
		    		
		    		for (MeetingMember memberRemote : meetingsRemoteMembers) {
		    			if (memberRemote.getMeetingMemberId().equals(meetingMemberId)) {
			    			found = true;
			    		}
		    		}
		    		
		    		if (!found) {
		    			
						// Not In Remote List available - extern user
						MeetingMemberLogic.getInstance().addMeetingMember(
								clientMember.get("firstname").toString(),
								clientMember.get("lastname").toString(), 
								"0", //member - Status
								"0", //appointement - Status
								appointmentId, 
								null, //UserId
								clientMember.get("email").toString(), //Email to send to
								baseUrl, //URL to send to
								users_id, //organizer
								new Boolean(false), //invitor
								language_id, 
								isPasswordProtected, 
								password);

		    		}
		   		
		    	}
		    }
		    
		    return appointmentId;
		} catch (HibernateException ex) {
			log.error("[updateAppointment]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateAppointment]: ",ex2);
		}
		return null;
		
	}
	
	
	public Long updateAppointmentByTime(Long appointmentId, 
			Date appointmentstart, Date appointmentend, Long users_id, String baseUrl, 
			Long language_id) {
		
			log.debug("AppointmentDAOImpl.updateAppointment");
		try {
			
			
			Appointment ap = this.getAppointmentById(appointmentId);
									
			ap.setAppointmentStarttime(appointmentstart);
		 	ap.setAppointmentEndtime(appointmentend);
			ap.setUpdatetime(new Date());
						
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.update(ap);

			tx.commit();
		    HibernateUtil.closeSession(idf);
		    
		    
		    List<MeetingMember> meetingsRemoteMembers = MeetingMemberDaoImpl.getInstance().getMeetingMemberByAppointmentId(ap.getAppointmentId());
		    
		    
		    //Send notification of updated Event
		    for (MeetingMember memberRemote : meetingsRemoteMembers) {
		    	
	    		// Notify member of changes
	    		Invitationmanagement.getInstance().updateInvitation(ap, memberRemote, users_id, language_id);
		    		
		    }
		    
		    return appointmentId;
		} catch (HibernateException ex) {
			log.error("[updateAppointmentByTime]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateAppointmentByTime]: ",ex2);
		}
		return null;
		
	}
	
	//----------------------------------------------------------------------------------------------------------
	
	public Long deleteAppointement(Long appointmentId) {
		log.debug("deleteAppointMent");
		try {
			
			Appointment app = this.getAppointmentById(appointmentId);
			app.setUpdatetime(new Date());
			app.setDeleted("true");
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(app);
						
			tx.commit();
			HibernateUtil.closeSession(idf);
			return appointmentId;
		} catch (HibernateException ex) {
			log.error("[deleteAppointement]: " + ex);
		} catch (Exception ex2) {
			log.error("[deleteAppointement]: " + ex2);
		}
		return null;
	}
	
	public List<Appointment> getAppointmentsByRange(Long userId, Date starttime, Date endtime) {
		try {
			
		/*	String hql = "select a from Appointment a " +
					"WHERE a.deleted != :deleted "+
					"AND a.userId = :userId "+
			"AND a.starttime BETWEEN :starttime AND :endtime";
			*/
			
			
			starttime.setHours(0);
			
			endtime.setHours(23);
			endtime.setMinutes(59);
			
			String hql = "select a from Appointment a " +					
			"WHERE a.deleted != :deleted  " +
			"AND "+
			"( " +
					"(a.appointmentStarttime BETWEEN :starttime AND :endtime) "+
				"OR " +
					"(a.appointmentEndtime BETWEEN :starttime AND :endtime) "+
				"OR " +
					"(a.appointmentStarttime < :starttime AND a.appointmentEndtime > :endtime) " +
			") "+
			"AND " +
			"( " +
					"a.userId = :userId "+
			")";
			
			//"AND (a.terminstatus != 4 AND a.terminstatus != 5)";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			query.setDate("starttime", starttime);
			query.setDate("endtime", endtime);
			query.setLong("userId",userId);
			
			List<Appointment> listAppoints = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			for (Appointment appointment : listAppoints) {
				log.debug(""+appointment);
				
				appointment.setMeetingMember(MeetingMemberDaoImpl.getInstance().getMeetingMemberByAppointmentId(appointment.getAppointmentId()));	
				
			}
			
			return listAppoints;
		} catch (HibernateException ex) {
			log.error("[getAppointmentsByRange]: " + ex);
		} catch (Exception ex2) {
			log.error("[getAppointmentsByRange]: " + ex2);
		}
		return null;
	}
	
	public List<Appointment> getAppointmentsByCat(Long categoryId) {
		try {
			
			String hql = "select a from Appointments a " +
					"WHERE a.deleted != :deleted " +
					"AND a.appointmentCategory.categoryId = :categoryId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			query.setLong("categoryId", categoryId);
			
			List<Appointment> listAppoints = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listAppoints;
		} catch (HibernateException ex) {
			log.error("[getAppointements]: " + ex);
		} catch (Exception ex2) {
			log.error("[getAppointements]: " + ex2);
		}
		return null;
	}
	
	public List<Appointment> getAppointmentsByCritAndCat(Long cat_id) {
		try {
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Appointment.class, "openmeetings");
			crit.add(Restrictions.eq("deleted", "false"));
			Criteria subcrit = crit.createCriteria("appointmentCategory");
			subcrit.add(Restrictions.eq("categoryId", cat_id));
			List<Appointment> listAppoints = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listAppoints;
		} catch (HibernateException ex) {
			log.error("[getAppointements]: " + ex);
		} catch (Exception ex2) {
			log.error("[getAppointements]: " + ex2);
		}
		return null;
	}
	
	//next appointment to select date
	public Appointment getNextAppointment(Date appointmentStarttime) {
		try {
						
			String hql = "select a from Appointment a " +
					"WHERE a.deleted != :deleted " +					
					"AND a.appointmentStarttime > :appointmentStarttime ";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");			
			query.setDate("appointmentStarttime", appointmentStarttime);
			
			Appointment appoint = (Appointment) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return appoint;
		} catch (HibernateException ex) {
			log.error("[getNextAppointmentById]: " + ex);
		} catch (Exception ex2) {
			log.error("[getNextAppointmentById]: " + ex2);
		}
		return null;
	}
	
	public List<Appointment> searchAppointmentsByName(String name) {
		try {
			
			String hql = "select a from Appointment a " +
					"WHERE a.deleted != :deleted " +
					"AND a.appointmentName LIKE :appointmentName";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			query.setString("appointmentName", name );
			
			List<Appointment> listAppoints = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listAppoints;
		} catch (HibernateException ex) {
			log.error("[searchAppointmentsByName]: " + ex);
		} catch (Exception ex2) {
			log.error("[searchAppointmentsByName]: " + ex2);
		}
		return null;
	}
	
	
	/**
	 * @author becherer
	 * @param userId
	 * @return
	 */
	public List<Appointment> getTodaysAppoitmentsbyRangeAndMember( Long userId){
		log.debug("getAppoitmentbyRangeAndMember : UserID - " + userId);	
		
		String hql = "SELECT app from MeetingMember mm " + 
		"JOIN mm.appointment as app " + 
		"WHERE mm.userid= :userId " + 
		"AND mm.deleted!= :mm_deleted " + 
		"AND app.deleted!= :app_deleted "+
		"AND  " +
		"app.appointmentStarttime between :starttime " + 
		"AND " + 
		" :endtime";
		
		
		
		
		Date startDate = new Date();
		startDate.setHours(0);
		startDate.setMinutes(0);
		startDate.setSeconds(1);
		
		Date endDate = new Date();
		endDate.setHours(23);
		endDate.setMinutes(59);
		endDate.setSeconds(59);
		
		Timestamp startStamp = new Timestamp(startDate.getTime());
		Timestamp stopStamp = new Timestamp(endDate.getTime());
		
		
		System.out.println("StartTime : " + startDate);
		System.out.println("EndTime : " + endDate);
		
		
		try{
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		
		session.flush();
		Query query = session.createQuery(hql);
		
		query.setBoolean("mm_deleted", true);
		query.setString("app_deleted", "true");
		query.setLong("userId", userId);
		
		
		query.setTimestamp("starttime", startStamp);
		query.setTimestamp("endtime", stopStamp);
		
		
		List<Appointment> listAppoints = query.list();
		tx.commit();
		HibernateUtil.closeSession(idf);
		
		return listAppoints;
		}catch(Exception e){
			log.error("Error in getTodaysAppoitmentsbyRangeAndMember : " + e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * @author becherer
	 * @param userId
	 * @return
	 */
	//---------------------------------------------------------------------------------------------
	public List<Appointment> getTodaysAppoitmentsForAllUsers( ){
		try{
			
		    //log.debug("getTodaysAppoitmentsForAllUsers");	
			
			String hql = "SELECT app from MeetingMember mm " + 
						"JOIN mm.appointment as app " + 
						"WHERE mm.deleted!= :mm_deleted " + 
						"AND app.deleted!= :app_deleted "+
						"AND  " +
						"app.appointmentStarttime between :starttime " + 
						"AND " + 
						" :endtime";
			
			
			Date startDate = new Date();
			startDate.setHours(0);
			startDate.setMinutes(0);
			startDate.setSeconds(1);
			
			Date endDate = new Date();
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);
			
			Timestamp startStamp = new Timestamp(startDate.getTime());
			Timestamp stopStamp = new Timestamp(endDate.getTime());
			
			
			//System.out.println("StartTime : " + startDate);
			//System.out.println("EndTime : " + endDate);
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			
			query.setBoolean("mm_deleted", true);
			query.setString("app_deleted", "true");
			
			query.setTimestamp("starttime", startStamp);
			query.setTimestamp("endtime", stopStamp);
			
			
			List<Appointment> listAppoints = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listAppoints;
		}catch(Exception e){
			log.error("Error in getTodaysAppoitmentsForAllUsers : " , e);
			return null;
		}
	}
	//---------------------------------------------------------------------------------------------
	
	
}


