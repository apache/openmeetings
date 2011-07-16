package org.openmeetings.app.data.user.dao;

import java.util.Date;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
			
			if (userSipDataId == null) {
				return null;
			}
			
			String hql = "select c from UserSipData as c where c.userSipDataId = :userSipDataId";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("userSipDataId", userSipDataId);
			UserSipData userSipData = null;
			try {
				userSipData = (UserSipData) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userSipData;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			userSipData = session.merge(userSipData);
			Long userSipDataId = userSipData.getUserSipDataId();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userSipDataId;
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (userSipData.getUserSipDataId() == 0) {
				session.persist(userSipData);
			    } else {
			    	if (!session.contains(userSipData)) {
			    		session.merge(userSipData);
			    }
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userSipData.getUserSipDataId();
		} catch (Exception ex2) {
			log.error("[updateUserSipData] ",ex2);
		}
		return null;
	}	
	
}
