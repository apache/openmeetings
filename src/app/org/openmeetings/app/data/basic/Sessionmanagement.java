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
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.slf4j.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.red5.logging.Red5LoggerFactory;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

/**
 * 
 * @author swagner
 * This Class handles all session management
 * 
 * TODO: Delete all inactive session by a scheduler
 *
 */ 
public class Sessionmanagement {
 
	private static final Logger log = Red5LoggerFactory.getLogger(Sessionmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	//private static final Logger log = new Logger(Sessionmanagement.class, ScopeApplicationAdapter.webAppRootKey);
	
	//private static final Logger log = Red5Red5LoggerFactory.getLogger(Sessionmanagement.class, ScopeApplicationAdapter.webAppRootKey);
	//private static final Logger log = Red5LoggerFactory.getLogger(Sessionmanagement.class, ScopeApplicationAdapter.webAppRootKey);

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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			session.flush();
			sessiondata = session.merge(sessiondata);
			session.flush();
			session.refresh(sessiondata);
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return sessiondata;
		} catch (Exception ex2) {
			log.error("[startsession]: " ,ex2);
		}

		return null;
	}
	
	public Sessiondata getSessionByHash(String SID) {
		try {
			log.debug("updateUser User SID: "+SID);
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			session.flush();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Sessiondata> cq = cb.createQuery(Sessiondata.class);
			Root<Sessiondata> c = cq.from(Sessiondata.class);
			Predicate condition = cb.equal(c.get("session_id"), SID);
			cq.where(condition);

			TypedQuery<Sessiondata> q = session.createQuery(cq);

			List<Sessiondata> fullList = q.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			if (fullList.size() == 0){
				log.error("Could not find session to update: "+SID);
				return null;
			} else {
				//log.error("Found session to update: "+SID);
			}
			
			Sessiondata sd = (Sessiondata) fullList.get(0);
			
			return sd;
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
//			EntityManager session = HibernateUtil.getSession();
//			EntityTransaction tx = session.getTransaction();
//			
//			//session.flush();
//			CriteriaBuilder crit = session.getCriteriaBuilder();
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			//session.flush();
			Query query = session.createQuery(hql);
			query.setParameter("session_id", SID);
			
			List<Sessiondata> sessions = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			Sessiondata sessiondata = null;
			if (sessions != null && sessions.size() > 0) {
				sessiondata = sessions.get(0);
			}
			
//			if (sessiondata!=null)  {
//				log.debug("checkSession USER_ID: "+sessiondata.getUser_id());
//			} else {
//				log.debug("Session IS NULL SID: "+SID);
//			}
				
			//Update the Session Object
			if (sessiondata!=null) updatesession(SID);
			
			//Checks if wether the Session or the User Object of that Session is set yet
			if (sessiondata==null || sessiondata.getUser_id()==null || sessiondata.getUser_id().equals(new Long(0)) ) {
				return new Long(0);
			} else {
				return sessiondata.getUser_id();
			}			
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			//session.flush();
			Query query = session.createQuery(hql);
			query.setParameter("session_id", SID);

			List<Sessiondata> sessions = query.getResultList();
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
			tx = session.getTransaction();
			tx.begin();
			sessiondata.setRefresh_time(new Date());
			//session.refresh(sd);
			sessiondata.setUser_id(USER_ID);
			if (sessiondata.getId() == null) {
				session.persist(sessiondata);
			    } else {
			    	if (!session.contains(sessiondata)) {
			    		session.merge(sessiondata);
			    }
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			session.flush();
			Query query = session.createQuery(hql);
			query.setParameter("session_id", SID);

			List<Sessiondata> sessions = query.getResultList();
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
			tx = session.getTransaction();
			tx.begin();
			sessiondata.setRefresh_time(new Date());
			//session.refresh(sd);
			sessiondata.setUser_id(USER_ID);
			if (storePermanent) {
				sessiondata.setStorePermanent(storePermanent);
			}
			sessiondata.setLanguage_id(language_id);
			if (sessiondata.getId() == null) {
				session.persist(sessiondata);
			    } else {
			    	if (!session.contains(sessiondata)) {
			    		session.merge(sessiondata);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			session.flush();
			Query query = session.createQuery(hql);
			query.setParameter("session_id", SID);

			List<Sessiondata> sessions = query.getResultList();
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
			tx = session.getTransaction();
			tx.begin();
			sessiondata.setRefresh_time(new Date());
			//session.refresh(sd);
			sessiondata.setOrganization_id(organization_id);
			if (sessiondata.getId() == null) {
				session.persist(sessiondata);
			    } else {
			    	if (!session.contains(sessiondata)) {
			    		session.merge(sessiondata);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: " ,ex2);
		}
		return null;
	}
	
	
	public Boolean updateUserWithoutSession(String SID, Long USER_ID) {
		try {
			log.debug("updateUser User: "+USER_ID+" || "+SID);
			
			String hql = "select c from Sessiondata as c " +
							"where c.session_id LIKE :session_id";
	
			//log.debug("checkSession User: || "+SID);
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			session.flush();
			Query query = session.createQuery(hql);
			query.setParameter("session_id", SID);

			List<Sessiondata> sessions = query.getResultList();
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
			tx = session.getTransaction();
			tx.begin();
			sessiondata.setRefresh_time(new Date());
			//session.refresh(sd);
			sessiondata.setUser_id(USER_ID);
			if (sessiondata.getId() == null) {
				session.persist(sessiondata);
			    } else {
			    	if (!session.contains(sessiondata)) {
			    		session.merge(sessiondata);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
		} catch (Exception ex2) {
			log.error("[updateUser]: " ,ex2);
		}
		return null;
	}
	
	public Boolean updateUserRemoteSession(String SID, String sessionXml) {
		try {
			log.debug("updateUser User SID: "+SID);
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			session.flush();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Sessiondata> cq = cb.createQuery(Sessiondata.class);
			Root<Sessiondata> c = cq.from(Sessiondata.class);
			Predicate condition = cb.equal(c.get("session_id"), SID);
			cq.where(condition);

			TypedQuery<Sessiondata> q = session.createQuery(cq);
			List<Sessiondata> fullList = q.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (fullList.size() == 0){
				log.error("Could not find session to update: "+SID);
				return false;
			} else {
				//log.error("Found session to update: "+SID);
			}
			Sessiondata sd = (Sessiondata) fullList.get(0);
			//log.debug("Found session to update: "+sd.getSession_id()+ " userId: "+USER_ID);
			
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.getTransaction();
			tx.begin();
			sd.setRefresh_time(new Date());
			session.refresh(sd);
			sd.setSessionXml(sessionXml);
			session.flush();
			if (sd.getId() == null) {
				session.persist(sd);
			    } else {
			    	if (!session.contains(sd)) {
			    		session.merge(sd);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			//log.debug("session updated User: "+USER_ID);
			return true;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Sessiondata> cq = cb.createQuery(Sessiondata.class);
			Root<Sessiondata> c = cq.from(Sessiondata.class);
			Predicate condition = cb.equal(c.get("session_id"), SID);
			cq.where(condition);

			TypedQuery<Sessiondata> q = session.createQuery(cq);

			List<Sessiondata> fullList = q.getResultList();
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
				EntityManager session2 = HibernateUtil.getSession();
				EntityTransaction tx2 = session2.getTransaction();
				tx2.begin();
				if (sd.getId() == null) {
					session2.persist(sd);
				    } else {
				    	if (!session2.contains(sd)) {
				    		session2.merge(sd);
				    }
				}
				session2.flush();
				tx2.commit();
				HibernateUtil.closeSession(idf2);	
			}
			
		} catch (Exception ex2) {
			log.error("[updatesession]: " ,ex2);
		}
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	private List<Sessiondata> getSessionToDelete(Date refresh_time){
		try {
			
			String hql = "Select c from Sessiondata c " +
							"WHERE c.refresh_time < :refresh_time " +
							"AND ( " +
							"c.storePermanent IS NULL " +
							"OR " +
							"c.storePermanent = false " +
							")";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("refresh_time", refresh_time);
			List<Sessiondata> fullList = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			log.debug("Sessions To Delete :: "+fullList.size());
			
			return fullList;			
			
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
			log.debug("****** clearSessionTable: ");
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTimeInMillis(rightNow.getTimeInMillis()-1800000);
		    List l = this.getSessionToDelete(rightNow.getTime());
		    log.debug("clearSessionTable: "+l.size());
            Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
		    for (Iterator it = l.iterator();it.hasNext();){
				Sessiondata sData = (Sessiondata) it.next();
				sData = session.find(Sessiondata.class, sData.getId());
				session.remove(sData);
		    }
            tx.commit();
			HibernateUtil.closeSession(idf);
		    
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
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				sData = session.find(Sessiondata.class, sData.getId());
				session.remove(sData);
				HibernateUtil.closeSession(idf);
			}

		} catch (Exception err) {
			log.error("clearSessionByRoomId", err);
		}
	}
}
