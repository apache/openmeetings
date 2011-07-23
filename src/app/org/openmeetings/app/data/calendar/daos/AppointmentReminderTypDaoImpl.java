package org.openmeetings.app.data.calendar.daos;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class AppointmentReminderTypDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentReminderTypDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private AppointmentReminderTypDaoImpl() {
	}

	private static AppointmentReminderTypDaoImpl instance = null;

	public static synchronized AppointmentReminderTypDaoImpl getInstance() {
		if (instance == null) {
			instance = new AppointmentReminderTypDaoImpl();
		}

		return instance;
	}
	
	public AppointmentReminderTyps getAppointmentReminderTypById(Long typId) {
		try {
			log.debug("AppointmentReminderTypById: "+ typId);
			
			String hql = "select app from AppointmentReminderTyps app " +
					"WHERE app.deleted <> :deleted " +
					"AND app.typId = :typId";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			query.setParameter("typId",typId);
			
			AppointmentReminderTyps appointmentReminderTyps = null;
			try {
				appointmentReminderTyps = (AppointmentReminderTyps) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return appointmentReminderTyps;
		} catch (Exception ex2) {
			log.error("[getAppointmentReminderTypsById]: " + ex2);
		}
		return null;
	}
	
	public Long updateAppointmentReminderTyps(Long typId, String name) {
		try {
			
			
			AppointmentReminderTyps ac = this.getAppointmentReminderTypById(typId);
									
			ac.setName(name);
			ac.setUpdatetime(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			if (ac.getTypId() == null) {
				session.persist(ac);
			    } else {
			    	if (!session.contains(ac)) {
			    		session.merge(ac);
			    }
			}

			tx.commit();
			HibernateUtil.closeSession(idf);
			return typId;
		} catch (Exception ex2) {
			log.error("[updateAppointmentReminderTyps]: ",ex2);
		}
		return null;
	}
	
	public Long addAppointmentReminderTyps( Long user_id, String name, String comment) {
		try {
			
			AppointmentReminderTyps ac = new AppointmentReminderTyps();
			
			ac.setName(name);
			ac.setStarttime(new Date());
			ac.setDeleted("false");
			ac.setUser(UsersDaoImpl.getInstance().getUser(user_id));
			ac.setComment(comment);
			
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			ac = session.merge(ac);
			Long category_id = ac.getTypId();

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return category_id;
		} catch (Exception ex2) {
			log.error("[addAppointmentReminderTyps]: ",ex2);
		}
		return null;
	}
	
	public Long deleteAppointmentReminderTyp(Long typId) {
		try {
			
			AppointmentReminderTyps ac = this.getAppointmentReminderTypById(typId);
			
			log.debug("ac: "+ac);
			
			if (ac == null) {
				log.debug("Already deleted / Could not find: "+typId);
				return typId;
			}
			ac.setUpdatetime(new Date());
			ac.setDeleted("true");
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (ac.getTypId() == null) {
				session.persist(ac);
			    } else {
			    	if (!session.contains(ac)) {
			    		session.merge(ac);
			    }
			}
						
			tx.commit();
			HibernateUtil.closeSession(idf);
			return typId;
		} catch (Exception ex2) {
			log.error("[deleteAppointmentReminderTyp]: " + ex2);
		}
		return null;
	}

	public List<AppointmentReminderTyps> getAppointmentReminderTypList() {
		log.debug("getAppointmenetReminderTypList");
		
		try {
			
			String hql = "select a from AppointmentReminderTyps a " +
					"WHERE a.deleted <> :deleted ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("deleted", "true");
			
				
			List<AppointmentReminderTyps> listAppointmentReminderTyp = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listAppointmentReminderTyp;
		} catch (Exception ex2) {
			log.error("[getAppointmentReminderTypList]: " + ex2);
		}
		return null;
	}
}
