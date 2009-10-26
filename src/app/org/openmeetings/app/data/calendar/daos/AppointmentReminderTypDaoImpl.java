package org.openmeetings.app.data.calendar.daos;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.calendar.Appointment;
import org.openmeetings.app.hibernate.beans.calendar.AppointmentCategory;
import org.openmeetings.app.hibernate.beans.calendar.AppointmentReminderTyps;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

public class AppointmentReminderTypDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentReminderTypDaoImpl.class, "openmeetings");

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
					"WHERE app.deleted != :deleted " +
					"AND app.typId = :typId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			query.setLong("typId",typId);
			
			AppointmentReminderTyps appointmentReminderTyps = (AppointmentReminderTyps) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return appointmentReminderTyps;
		} catch (HibernateException ex) {
			log.error("[getAppointmentReminderTypsById]: " + ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.update(ac);

			tx.commit();
			HibernateUtil.closeSession(idf);
			return typId;
		} catch (HibernateException ex) {
			log.error("[updateAppointmentReminderTyps]: ",ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long category_id = (Long)session.save(ac);

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return category_id;
		} catch (HibernateException ex) {
			log.error("[addAppointmentReminderTyps]: ",ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(ac);
						
			tx.commit();
			HibernateUtil.closeSession(idf);
			return typId;
		} catch (HibernateException ex) {
			log.error("[deleteAppointmentReminderTyp]: " + ex);
		} catch (Exception ex2) {
			log.error("[deleteAppointmentReminderTyp]: " + ex2);
		}
		return null;
	}

	public List<AppointmentReminderTyps> getAppointmentReminderTypList() {
		log.debug("getAppointmenetReminderTypList");
		
		try {
			
			String hql = "select a from AppointmentReminderTyps a " +
					"WHERE a.deleted != :deleted ";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			
				
			List<AppointmentReminderTyps> listAppointmentReminderTyp = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listAppointmentReminderTyp;
		} catch (HibernateException ex) {
			log.error("[getAppointmentReminderTypList]: " + ex);
		} catch (Exception ex2) {
			log.error("[getAppointmentReminderTypList]: " + ex2);
		}
		return null;
	}
}
