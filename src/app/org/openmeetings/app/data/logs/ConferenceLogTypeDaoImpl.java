package org.openmeetings.app.data.logs;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.logs.ConferenceLogType;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class ConferenceLogTypeDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(ConferenceLogTypeDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private ConferenceLogTypeDaoImpl() {
	}

	private static ConferenceLogTypeDaoImpl instance = null;

	public static synchronized ConferenceLogTypeDaoImpl getInstance() {
		if (instance == null) {
			instance = new ConferenceLogTypeDaoImpl();
		}
		return instance;
	}
	

	public Long addConferenceLogType(String eventType) {
		try {
			
			ConferenceLogType confLogType = new ConferenceLogType();
			confLogType.setEventType(eventType);
			confLogType.setInserted(new Date());
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			confLogType = session.merge(confLogType);
			session.flush();
			Long appointment_id = confLogType.getConferenceLogTypeId();

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return appointment_id;
		} catch (Exception ex2) {
			log.error("[addConferenceLogType]: ",ex2);
		}
		return null;
	}
	
	public ConferenceLogType getConferenceLogTypeByEventName(String eventType) {
		try {
			
			String hql = "select a from ConferenceLogType a " +
					"WHERE a.eventType = :eventType ";
					
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("eventType",eventType);
			
			//Seems like this does throw an error sometimes cause it does not return a unique Result
			//ConferenceLogType confLogType = (ConferenceLogType) query.getSingleResult();
			List<ConferenceLogType> confLogTypes = query.getResultList();
			ConferenceLogType confLogType = null;
			if (confLogTypes != null && confLogTypes.size() > 0) {
				confLogType = confLogTypes.get(0);
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return confLogType;
		} catch (Exception ex2) {
			log.error("[getConferenceLogTypeByEventName]: " + ex2);
		}
		return null;
	}
	
}
