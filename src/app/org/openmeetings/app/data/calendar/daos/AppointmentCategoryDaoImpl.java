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
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class AppointmentCategoryDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(Configurationmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	private AppointmentCategoryDaoImpl() {
	}

	private static AppointmentCategoryDaoImpl instance = null;

	public static synchronized AppointmentCategoryDaoImpl getInstance() {
		if (instance == null) {
			instance = new AppointmentCategoryDaoImpl();
		}

		return instance;
	}
	
	public AppointmentCategory getAppointmentCategoryById(Long categoryId) {
		try {
			log.debug("getAppointmentCategoryById: "+ categoryId);
			
			String hql = "select app from AppointmentCategory app " +
					"WHERE app.deleted != :deleted " +
					"AND app.categoryId = :categoryId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
			query.setLong("categoryId",categoryId);
			
			AppointmentCategory appointCategory = (AppointmentCategory) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return appointCategory;
		} catch (HibernateException ex) {
			log.error("[getAppointmentCategoryById]: " + ex);
		} catch (Exception ex2) {
			log.error("[getAppointmentCategoryById]: " + ex2);
		}
		return null;
	}
	
	public Long updateAppointmentCategory(Long categoryId, String name) {
		try {
			
			
			AppointmentCategory ac = this.getAppointmentCategoryById(categoryId);
									
			ac.setName(name);
			ac.setUpdatetime(new Date());
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.update(ac);

			tx.commit();
			HibernateUtil.closeSession(idf);
			return categoryId;
		} catch (HibernateException ex) {
			log.error("[updateAppointmentCategory]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateAppointmentCategory]: ",ex2);
		}
		return null;
	}
	
	public Long addAppointmentCategory( Long user_id, String name, String comment) {
		try {
			
			AppointmentCategory ac = new AppointmentCategory();
			
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
			log.error("[addAppointmentCategory]: ",ex);
		} catch (Exception ex2) {
			log.error("[addAppointmentCategory]: ",ex2);
		}
		return null;
	}
	
	public Long deleteAppointmentCategory(Long categoryId) {
		try {
			
			AppointmentCategory ac = this.getAppointmentCategoryById(categoryId);
			
			log.debug("ac: "+ac);
			
			if (ac == null) {
				log.debug("Already deleted / Could not find: "+categoryId);
				return categoryId;
			}
			ac.setUpdatetime(new Date());
			ac.setDeleted("true");
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(ac);
						
			tx.commit();
			HibernateUtil.closeSession(idf);
			return categoryId;
		} catch (HibernateException ex) {
			log.error("[deleteAppointmentCategory]: " + ex);
		} catch (Exception ex2) {
			log.error("[deleteAppointmentCategory]: " + ex2);
		}
		return null;
	}

	public List<AppointmentCategory> getAppointmentCategoryList() {
		try {
			
			String hql = "select a from AppointmentCategory a " +
					"WHERE a.deleted != :deleted ";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("deleted", "true");
				
			List<AppointmentCategory> listAppointmentCategory = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return listAppointmentCategory;
		} catch (HibernateException ex) {
			log.error("[AppointmentCategory]: " + ex);
		} catch (Exception ex2) {
			log.error("[AppointmentCategory]: " + ex2);
		}
		return null;
	}
}
