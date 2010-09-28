package org.openmeetings.app.data.user.dao;

import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.user.UserSipData;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class UserSipDataDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(UserSipDataDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private static UserSipDataDaoImpl instance = null;

	
	private UserSipDataDaoImpl() {
	}

	public static synchronized UserSipDataDaoImpl getInstance() {
		if (instance == null) {
			instance = new UserSipDataDaoImpl();
		}
		return instance;
	}
	
	public UserSipData getUserSipDataById(Long userSipDataId) {
		try {
			
			String hql = "select c from UserSipData as c where c.userSipDataId = :userSipDataId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("userSipDataId", userSipDataId);
			UserSipData userSipData = (UserSipData) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userSipData;
		} catch (HibernateException ex) {
			log.error("getUserSipDataById",ex);
		} catch (Exception ex2) {
			log.error("getUserSipDataById",ex2);
		}
		return null;
	}

	public Long addUserSipData(UserSipData userSipData) {
		try {
			
			if (userSipData == null) {
				return null;
			}
			
			userSipData.setInserted(new Date());
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long userSipDataId = (Long) session.save(userSipData);
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userSipDataId;
		} catch (HibernateException ex) {
			log.error("[addUserSipData] ",ex);
		} catch (Exception ex2) {
			log.error("[addUserSipData] ",ex2);
		}
		return null;
	}
	
	public Long updateUserSipData(UserSipData userSipData) {
		try {
			
			if (userSipData == null) {
				return null;
			}
			
			userSipData.setUpdated(new Date());
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(userSipData);
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userSipData.getUserSipDataId();
		} catch (HibernateException ex) {
			log.error("[updateUserSipData] ",ex);
		} catch (Exception ex2) {
			log.error("[updateUserSipData] ",ex2);
		}
		return null;
	}	
	
}
