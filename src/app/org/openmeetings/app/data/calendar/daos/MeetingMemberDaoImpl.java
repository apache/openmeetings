package org.openmeetings.app.data.calendar.daos;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.calendar.MeetingMember;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class MeetingMemberDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(MeetingMemberDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

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
					"WHERE app.deleted <> :deleted " +
					"AND app.meetingMemberId = :meetingMemberId";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", true);
			query.setParameter("meetingMemberId",meetingMemberId);
			
			MeetingMember meetingMember = null;
			try {
				meetingMember = (MeetingMember) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return meetingMember;
		} catch (Exception ex2) {
			log.error("[getMeetingMemberById]: " , ex2);
		}
		return null;
	}
	
	public List<MeetingMember> getMeetingMembers() {
		try {
			String hql = "select app from MeetingMember app";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			
			List<MeetingMember> meetingMembers = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return meetingMembers;
		} catch (Exception ex2) {
			log.error("[getMeetingMembers]: " , ex2);
		}
		return null;
	}
	
	public List<MeetingMember> getMeetingMemberByAppointmentId(Long appointmentId) {
		try {
			log.debug("getMeetingMemberByAppointmentId: "+ appointmentId);
			
			String hql = "select app from MeetingMember app " +
					"WHERE app.deleted <> :deleted " +
					"AND app.appointment.appointmentId = :appointmentId";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", true);
			query.setParameter("appointmentId",appointmentId);
			
			List<MeetingMember> listmeetingMember = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listmeetingMember;
		} catch (Exception ex2) {
			log.error("[getMeetingMemberByAppointmentId]: " , ex2);
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("starttime", starttime);
			query.setParameter("endtime", endtime);
			query.setParameter("userid",userId);
			
			List<MeetingMember> listAppoints = query.getResultList();
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
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				if (meetingMember.getMeetingMemberId() == null) {
					session.persist(meetingMember);
				    } else {
				    	if (!session.contains(meetingMember)) {
				    		meetingMember = session.merge(meetingMember);
				    }
				}
				session.flush();
				tx.commit();
				HibernateUtil.closeSession(idf);
				return meetingMember;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			if (gm.getMeetingMemberId() == null) {
				session.persist(gm);
			    } else {
			    	if (!session.contains(gm)) {
			    		gm = session.merge(gm);
			    }
			}

			session.flush();
			tx.commit();
			meetingMemberId = gm.getMeetingMemberId();
			HibernateUtil.closeSession(idf);
			return meetingMemberId;
		} catch (Exception ex2) {
			log.error("[updateMeetingMember]: ",ex2);
		}
		return null;
	}
	
	public Long addMeetingMember(String firstname, String lastname, String memberStatus,
			String appointmentStatus, Long appointmentId, Long userid, String email, 
			Boolean moderator, String jNameTimeZone, Boolean isConnectedEvent) {
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
			gm.setIsConnectedEvent(isConnectedEvent);
			
			gm.setOmTimeZone(OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jNameTimeZone));
						
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			gm = session.merge(gm);
			session.flush();
			Long group_member_id = gm.getMeetingMemberId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return group_member_id;
		} catch (Exception ex2) {
			log.error("[addMeetingMember]: ",ex2);
		}
		return null;
	}
	
	public Long addMeetingMemberByObject(MeetingMember gm){
		try {
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			gm = session.merge(gm);
			session.flush();
			Long group_member_id = gm.getMeetingMemberId();

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return group_member_id;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (gm.getMeetingMemberId() == null) {
				session.persist(gm);
			    } else {
			    	if (!session.contains(gm)) {
			    		session.merge(gm);
			    }
			}
						
			tx.commit();
			HibernateUtil.closeSession(idf);
			return meetingMemberId;
		} catch (Exception ex2) {
			log.error("[deleteMeetingMember]: ", ex2);
		}
		return null;
	}

}
