package org.openmeetings.app.data.user.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.persistence.beans.user.UserSipData;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserSipDataDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(UserSipDataDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public UserSipData getUserSipDataById(Long userSipDataId) {
		try {
			
			if (userSipDataId == null) {
				return null;
			}
			
			String hql = "select c from UserSipData as c where c.userSipDataId = :userSipDataId";
			
			TypedQuery<UserSipData> query = em.createQuery(hql, UserSipData.class);
			query.setParameter("userSipDataId", userSipDataId);
			UserSipData userSipData = null;
			try {
				userSipData = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
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
			
			userSipData = em.merge(userSipData);
			Long userSipDataId = userSipData.getUserSipDataId();
			
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
			
			if (userSipData.getUserSipDataId() == 0) {
				em.persist(userSipData);
		    } else {
		    	if (!em.contains(userSipData)) {
		    		em.merge(userSipData);
			    }
			}
			
			return userSipData.getUserSipDataId();
		} catch (Exception ex2) {
			log.error("[updateUserSipData] ",ex2);
		}
		return null;
	}	
	
}
