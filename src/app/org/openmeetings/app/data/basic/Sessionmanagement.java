package org.openmeetings.app.data.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

import org.openmeetings.app.hibernate.beans.basic.Sessiondata;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.utils.crypt.ManageCryptStyle;
//import org.slf4j.Logger;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.red5.logging.Red5LoggerFactory;
//import org.slf4j.Logger;
//import org.red5.logging.Red5LoggerFactory;
//import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

//import org.slf4j.Logger;
//import org.red5.logging.Red5LoggerFactory;

/**
 * 
 * @author swagner
 * This Class handles all session management
 * 
 * TODO: Delete all inactive session by a scheduler
 *
 */ 
public class Sessionmanagement {
 
	private static final Logger log = Red5LoggerFactory.getLogger(Sessionmanagement.class, "openmeetings");

	//private static final Logger log = new Logger(Sessionmanagement.class, "openmeetings");
	
	//private static final Logger log = Red5Red5LoggerFactory.getLogger(Sessionmanagement.class, "openmeetings");
	//private static final Logger log = Red5LoggerFactory.getLogger(Sessionmanagement.class, "openmeetings");

	private static Sessionmanagement instance;

	private Sessionmanagement() {
		//log.setLevel(Level.DEBUG);
	}

	public static synchronized Sessionmanagement getInstance() {
		if (instance == null) {
			instance = new Sessionmanagement();
		}
		return instance;
	}

	/**
	 * creates a new session-object in the database
	 * @return
	 */
	public Sessiondata startsession() {
		//log.error("startsession User: || ");
		try {
			
			System.out.println("startsession :: startsession");
			log.debug("startsession :: startsession");
			
			long thistime = new Date().getTime();
			Sessiondata sessiondata = new Sessiondata();
			sessiondata.setSession_id(ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(String.valueOf(thistime).toString()));
			sessiondata.setRefresh_time(new Date());
			sessiondata.setStarttermin_time(new Date());
			sessiondata.setUser_id(null);
		
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.flush();
			session.save(sessiondata);
			session.flush();
			session.refresh(sessiondata);
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return sessiondata;
		} catch (HibernateException ex) {
			log.error("[startsession]: " ,ex);
		} catch (Exception ex2) {
			log.error("[startsession]: " ,ex2);
		}

		return null;
	}
	
	public Sessiondata getSessionByHash(String SID) {
		try {
			//log.debug("updateUser User: "+USER_ID+" || "+SID);
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.flush();
			Criteria crit = session.createCriteria(Sessiondata.class, "openmeetings");
			crit.add(Restrictions.eq("session_id", SID));

			List fullList = crit.list();
			if (fullList.size() == 0){
				log.error("Could not find session to update: "+SID);
				return null;
			} else {
				//log.error("Found session to update: "+SID);
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			Sessiondata sd = (Sessiondata) fullList.get(0);
			
			return sd;
		} catch (HibernateException ex) {
			log.error("[updateUser]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updateUser]: " ,ex2);
		}
		return null;
	}	

	/**
	 * check if a given sessionID is loged in
	 * @param SID
	 * @return the User_id or 0 if not logged in
	 */
//	public Long checkSession(String SID) {
//		try {
//			//log.debug("checkSession User: || "+SID);
//			Object idf = HibernateUtil.createSession();
//			Session session = HibernateUtil.getSession();
//			Transaction tx = session.beginTransaction();
//			
//			//session.flush();
//			Criteria crit = session.createCriteria(Sessiondata.class, "openmeetings");
//			crit.add(Restrictions.eq("session_id", SID));
//
//			List<Sessiondata> sessions = crit.list();
//			Sessiondata sessiondata = null;
//			if (sessions != null && sessions.size() > 0) {
//				sessiondata = sessions.get(0);
//			}
//			
//			if (sessiondata!=null) session.refresh(sessiondata);
//			//session.flush();
//			tx.commit();
//			HibernateUtil.closeSession(idf);
//			//if (sessiondata!=null) log.debug("checkSession USER_ID: "+sessiondata.getUser_id());
//				
//			if (sessiondata!=null) updatesession(SID);
//			
//			//Checks if wether the Session or the User Object of that Session is set yet
//			if (sessiondata==null || sessions.size() >0 || sessiondata.equals(null) ||
//					sessiondata.getUser_id()==null || sessiondata.getUser_id().equals(null) 
//					|| sessiondata.getUser_id().equals(new Long(0)) ) {
//				return new Long(0);
//			} else {
//				return sessiondata.getUser_id();
//			}			
//		} catch (HibernateException ex) {
//			log.error("[checkSession]: " ,ex);
//		} catch (Exception ex2) {
//			log.error("[checkSession]: " ,ex2);
//		}
//		return null;
//	}
	
	public Long checkSession(String SID) {
		try {
			
			String hql = "select c from Sessiondata as c " +
					"where c.session_id LIKE :session_id";
			
			//log.debug("checkSession User: || "+SID);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			//session.flush();
			Query query = session.createQuery(hql);
			query.setString("session_id", SID);
			
			List<Sessiondata> sessions = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}
			
			if (sessiondata!=null)  {
				log.debug("checkSession USER_ID: "+sessiondata.getUser_id());
			} else {
				log.debug("Session IS NULL SID: "+SID);
			}
				
			//Update the Session Object
			if (sessiondata!=null) updatesession(SID);
			
			//Checks if wether the Session or the User Object of that Session is set yet
			if (sessiondata==null || sessiondata.getUser_id()==null || sessiondata.getUser_id().equals(new Long(0)) ) {
				return new Long(0);
			} else {
				return sessiondata.getUser_id();
			}			
		} catch (HibernateException ex) {
			log.error("[checkSession]: " ,ex);
		} catch (Exception ex2) {
			log.error("[checkSession]: " ,ex2);
		}
		return null;
	}

	/**
	 * update the session of a user with a new user id
	 * this is needed to see if the session is loggedin
	 * @param SID
	 * @param USER_ID
	 */
	public Boolean updateUser(String SID, long USER_ID) {
		try {
			log.debug("updateUser User: "+USER_ID+" || "+SID);
			
			String hql = "select c from Sessiondata as c " +
							"where c.session_id LIKE :session_id";
	
			//log.debug("checkSession User: || "+SID);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			//session.flush();
			Query query = session.createQuery(hql);
			query.setString("session_id", SID);

			List<Sessiondata> sessions = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}
			
			if (sessiondata == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: "+sessiondata.getSession_id()+ " userId: "+USER_ID);
			
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();
			sessiondata.setRefresh_time(new Date());
			//session.refresh(sd);
			sessiondata.setUser_id(USER_ID);
			//session.flush();
			session.update(sessiondata);
			//session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
		} catch (HibernateException ex) {
			log.error("[updateUser]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updateUser]: " ,ex2);
		}
		return null;
	}
	
	public Boolean updateUser(String SID, long USER_ID, boolean storePermanent, Long language_id) {
		try {
			log.debug("updateUser User: "+USER_ID+" || "+SID);
			
			String hql = "select c from Sessiondata as c " +
							"where c.session_id LIKE :session_id";
	
			//log.debug("checkSession User: || "+SID);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			//session.flush();
			Query query = session.createQuery(hql);
			query.setString("session_id", SID);

			List<Sessiondata> sessions = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}
			
			if (sessiondata == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: "+sessiondata.getSession_id()+ " userId: "+USER_ID);
			
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();
			sessiondata.setRefresh_time(new Date());
			//session.refresh(sd);
			sessiondata.setUser_id(USER_ID);
			sessiondata.setStorePermanent(storePermanent);
			sessiondata.setLanguage_id(language_id);
			//session.flush();
			session.update(sessiondata);
			//session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
		} catch (HibernateException ex) {
			log.error("[updateUser]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updateUser]: " ,ex2);
		}
		return null;
	}
	
	public Boolean updateUserOrg(String SID, Long organization_id) {
		try {
			log.debug("updateUserOrg User: "+organization_id+" || "+SID);
			
			String hql = "select c from Sessiondata as c " +
							"where c.session_id LIKE :session_id";
	
			//log.debug("checkSession User: || "+SID);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			//session.flush();
			Query query = session.createQuery(hql);
			query.setString("session_id", SID);

			List<Sessiondata> sessions = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}
			
			if (sessiondata == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: "+sessiondata.getSession_id()+ " organisation_id: "+organization_id);
			
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();
			sessiondata.setRefresh_time(new Date());
			//session.refresh(sd);
			sessiondata.setOrganization_id(organization_id);
			//session.flush();
			session.update(sessiondata);
			//session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
		} catch (HibernateException ex) {
			log.error("[updateUser]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updateUser]: " ,ex2);
		}
		return null;
	}
	
	
	public Boolean updateUserWithoutSession(String SID, long USER_ID) {
		try {
			log.debug("updateUser User: "+USER_ID+" || "+SID);
			
			String hql = "select c from Sessiondata as c " +
							"where c.session_id LIKE :session_id";
	
			//log.debug("checkSession User: || "+SID);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			//session.flush();
			Query query = session.createQuery(hql);
			query.setString("session_id", SID);

			List<Sessiondata> sessions = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}
			
			if (sessiondata == null) {
				log.error("Could not find session to Update");
				return false;
			}
			log.debug("Found session to update: "+sessiondata.getSession_id()+ " userId: "+USER_ID);
			
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();
			sessiondata.setRefresh_time(new Date());
			//session.refresh(sd);
			sessiondata.setUser_id(USER_ID);
			//session.flush();
			session.update(sessiondata);
			//session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
		} catch (HibernateException ex) {
			log.error("[updateUser]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updateUser]: " ,ex2);
		}
		return null;
	}
	
	public Boolean updateUserRemoteSession(String SID, String sessionXml) {
		try {
			//log.debug("updateUser User: "+USER_ID+" || "+SID);
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.flush();
			Criteria crit = session.createCriteria(Sessiondata.class, "openmeetings");
			crit.add(Restrictions.eq("session_id", SID));

			List fullList = crit.list();
			if (fullList.size() == 0){
				log.error("Could not find session to update: "+SID);
				return false;
			} else {
				//log.error("Found session to update: "+SID);
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			Sessiondata sd = (Sessiondata) fullList.get(0);
			//log.debug("Found session to update: "+sd.getSession_id()+ " userId: "+USER_ID);
			
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();
			sd.setRefresh_time(new Date());
			session.refresh(sd);
			sd.setSessionXml(sessionXml);
			session.flush();
			session.update(sd);
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
		} catch (HibernateException ex) {
			log.error("[updateUserRemoteSession]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updateUserRemoteSession]: " ,ex2);
		}
		return null;
	}

	/**
	 * update the session every time a user makes a request
	 * @param SID
	 */
	private void updatesession(String SID) {
		try {
			//log.debug("****** updatesession: "+SID);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Sessiondata.class, "openmeetings");
			crit.add(Restrictions.eq("session_id", SID));
			List fullList = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);			
			if (fullList.size() == 0) {
				log.error("Found NO session to updateSession: ");

			} else {
				//log.debug("Found session to updateSession: ");
				Sessiondata sd = (Sessiondata) fullList.iterator().next();
				//log.debug("Found session to updateSession sd "+sd.getUser_id()+" "+sd.getSession_id());
				sd.setRefresh_time(new Date());
				
				Object idf2 = HibernateUtil.createSession();
				Session session2 = HibernateUtil.getSession();
				Transaction tx2 = session2.beginTransaction();				
				session2.update(sd);
				session2.flush();
				tx2.commit();
				HibernateUtil.closeSession(idf2);	
			}
			
		} catch (HibernateException ex) {
			log.error("[updatesession]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updatesession]: " ,ex2);
		}
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	private List getSessionToDelete(Date date){
		try {
			//log.debug("****** sessionToDelete: "+date);
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Sessiondata.class, "openmeetings");
			crit.add(Restrictions.lt("refresh_time", date));
			List fullList = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return fullList;
		} catch (HibernateException ex) {
			log.error("[getSessionToDelete]: " ,ex);
		} catch (Exception ex2) {
			log.error("[getSessionToDelete]: " ,ex2);
		}
		return null;
	}
	
	/**
	 * 
	 *
	 */
	public void clearSessionTable(){
		try {
			//log.debug("****** clearSessionTable: ");
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTimeInMillis(rightNow.getTimeInMillis()-1800000);
		    List l = this.getSessionToDelete(rightNow.getTime());
		    //log.debug("clearSessionTable: "+l.size());
		    for (Iterator it = l.iterator();it.hasNext();){
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Sessiondata sData = (Sessiondata) it.next();
				session.delete(sData);
				tx.commit();
				HibernateUtil.closeSession(idf);
		    }
		    
		} catch (HibernateException ex) {
			log.error("clearSessionTable",ex);
		} catch (Exception err) {
			log.error("clearSessionTable",err);
		}
	}

	/**
	 * @param room_id
	 */
	public void clearSessionByRoomId(Long room_id) {
		try {

			HashMap<String, RoomClient> MyUserList = ClientListManager
					.getInstance().getClientListByRoom(room_id);

			for (Iterator<String> iter = MyUserList.keySet().iterator(); iter
					.hasNext();) {
				String key = (String) iter.next();

				RoomClient rcl = MyUserList.get(key);

				String aux = rcl.getSwfurl();

				int init_pos = aux.indexOf("sid=") + 4;
				int end_pos = init_pos + 32;
				if (end_pos > aux.length())
					end_pos = aux.length();
				String SID = aux.substring(init_pos, end_pos);
				
				Sessiondata sData = this.getSessionByHash(SID);

				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				session.delete(sData);
				HibernateUtil.closeSession(idf);
			}

		} catch (HibernateException ex) {
			log.error("clearSessionByRoomId", ex);
		} catch (Exception err) {
			log.error("clearSessionByRoomId", err);
		}
	}
}
