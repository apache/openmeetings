package org.openmeetings.app.data.calendar.daos;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.calendar.MeetingMember;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

public class MeetingMemberDaoImpl {
	
	private static final Logger log = Logger.getLogger(MeetingMemberDaoImpl.class);

	private MeetingMemberDaoImpl() {
	}

	private static MeetingMemberDaoImpl instance = null;

	public static synchronized MeetingMemberDaoImpl getInstance() {
		if (instance == null) {
			instance = new MeetingMemberDaoImpl();
		}

		return instance;
	}
	
	public MeetingMember getMeetingMemberById(Long meetingMemberId) {
		try {
			log.debug("getMeetingMemberById: "+ meetingMemberId);
			
			String hql = "select app from MeetingMember app " +
					"WHERE app.deleted != :deleted " +
					"AND app.meetingMemberId = :meetingMemberId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setBoolean("deleted", true);
			query.setLong("meetingMemberId",meetingMemberId);
			
			MeetingMember meetingMember = (MeetingMember) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return meetingMember;
		} catch (HibernateException ex) {
			log.error("[getMeetingMemberById]: " + ex);
		} catch (Exception ex2) {
			log.error("[getMeetingMemberById]: " + ex2);
		}
		return null;
	}
	
	public List<MeetingMember> getMeetingMemberByAppointmentId(Long appointmentId) {
		try {
			log.debug("getMeetingMemberByAppointmentId: "+ appointmentId);
			
			String hql = "select app from MeetingMember app " +
					"WHERE app.deleted != :deleted " +
					"AND app.appointment = :appointmentId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setBoolean("deleted", true);
			query.setLong("appointmentId",appointmentId);
			
			List<MeetingMember> listmeetingMember = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listmeetingMember;
		} catch (HibernateException ex) {
			log.error("[getMeetingMemberByAppointmentId]: " + ex);
		} catch (Exception ex2) {
			log.error("[getMeetingMemberByAppointmentId]: " + ex2);
		}
		return null;
	}
	
/*	public List<MeetingMember> getMeetingMemberByRange(Long userId, Date starttime, Date endtime) {
		try {
			
		
			
			String hql = "select a from Appointment a , MeetingMember mm " +					
			"WHERE a.deleted != :deleted  " +
			"AND mm.userid = :userid " +
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
					"a.userId = :userid "+
			")";
			
			//"AND (a.terminstatus != 4 AND a.terminstatus != 5)";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			query.setDate("starttime", starttime);
			query.setDate("endtime", endtime);
			query.setLong("userid",userId);
			
			List<MeetingMember> listAppoints = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listAppoints;
		} catch (HibernateException ex) {
			log.error("[getMeetingMemberByRange]: " + ex);
		} catch (Exception ex2) {
			log.error("[getMeetingMemberByRange]: " + ex2);
		}
		return null;
	}*/
	
	/**
	 * Updating MeetingMember
	 */
	//-------------------------------------------------------------------------------
	public MeetingMember updateMeetingMember(MeetingMember meetingMember) {
		log.debug("");
		if (meetingMember.getMeetingMemberId() > 0) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				session.update(meetingMember);
				tx.commit();
				HibernateUtil.closeSession(idf);
				return meetingMember;
			} catch (HibernateException ex) {
				log.error("[updateMeetingMember] ",ex);
			} catch (Exception ex2) {
				log.error("[updateMeetingMember] ",ex2);
			}
		} else {
			log.error("[updateUser] "+"Error: No MeetingMemberId given");
		}
		return null;
	}
	//-------------------------------------------------------------------------------
	
	public Long updateMeetingMember(Long meetingMemberId, String firstname, String lastname, 
			 String memberStatus, String appointmentStatus, 
			 Long appointmentId, Long userid, String email) {
		try {
			
			
			MeetingMember gm = this.getMeetingMemberById(meetingMemberId);
			/*
			if (gm == null) {
				log.debug("ALERT Object with ID: "+ MeetingMemberId +" does not exist yet");
				return null;
			}*/
									
			gm.setFirstname(firstname);
			gm.setLastname(lastname);
			
			//gm.setLanguageId(Languagemanagement.getInstance().getFieldLanguageById(languageId));
			gm.setMemberStatus(memberStatus);
			gm.setAppointmentStatus(appointmentStatus);
			gm.setAppointment(AppointmentDaoImpl.getInstance().getAppointmentById(appointmentId));	
			gm.setDeleted(false);
			gm.setUpdatetime(new Date());
			gm.setUserid(UsersDaoImpl.getInstance().getUser(userid));
			gm.setEmail(email);
			
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.update(gm);

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return meetingMemberId;
		} catch (HibernateException ex) {
			log.error("[updateMeetingMember]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateMeetingMember]: ",ex2);
		}
		return null;
	}
	
	public Long addMeetingMember(String firstname, String lastname, String memberStatus,
			String appointmentStatus, Long appointmentId, Long userid, String email, Boolean moderator) {
		try {
			
			MeetingMember gm = new MeetingMember();
			
			gm.setFirstname(firstname);
			gm.setLastname(lastname);
			gm.setMemberStatus(memberStatus);
			gm.setAppointmentStatus(appointmentStatus);
			gm.setAppointment(AppointmentDaoImpl.getInstance().getAppointmentById(appointmentId));
			gm.setUserid(UsersDaoImpl.getInstance().getUser(userid));
			gm.setEmail(email);
							
			gm.setStarttime(new Date());
			gm.setDeleted(false);
			gm.setInvitor(moderator);
						
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long group_member_id = (Long)session.save(gm);

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return group_member_id;
		} catch (HibernateException ex) {
			log.error("[addMeetingMember]: ",ex);
		} catch (Exception ex2) {
			log.error("[addMeetingMember]: ",ex2);
		}
		return null;
	}
	
	public Long deleteMeetingMember(Long meetingMemberId) {
		log.debug("MeetingMemnerDAoImpl.deleteMeetingMember : " + meetingMemberId);
		
		try {
			
			MeetingMember gm = this.getMeetingMemberById(meetingMemberId);
			
			log.debug("ac: "+gm);
			
			if (gm == null) {
				log.debug("Already deleted / Could not find: "+meetingMemberId);
				return null;
			}
			gm.setUpdatetime(new Date());
			gm.setDeleted(true);
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(gm);
						
			tx.commit();
			HibernateUtil.closeSession(idf);
			return meetingMemberId;
		} catch (HibernateException ex) {
			log.error("[deleteMeetingMember]: ", ex);
		} catch (Exception ex2) {
			log.error("[deleteMeetingMember]: ", ex2);
		}
		return null;
	}

}
