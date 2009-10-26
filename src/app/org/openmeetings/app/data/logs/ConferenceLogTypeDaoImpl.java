package org.openmeetings.app.data.logs;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.logs.ConferenceLogType;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

public class ConferenceLogTypeDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(ConferenceLogTypeDaoImpl.class, "openmeetings");

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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long appointment_id = (Long)session.save(confLogType);

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return appointment_id;
		} catch (HibernateException ex) {
			log.error("[addConferenceLogType]: ",ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("eventType",eventType);
			
			//Seems like this does throw an error sometimes cause it does not return a unique Result
			//ConferenceLogType confLogType = (ConferenceLogType) query.uniqueResult();
			List<ConferenceLogType> confLogTypes = query.list();
			
			ConferenceLogType confLogType = null;
			if (confLogTypes != null && confLogTypes.size() > 0) {
				confLogType = confLogTypes.get(0);
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return confLogType;
		} catch (HibernateException ex) {
			log.error("[getConferenceLogTypeByEventName]: " + ex);
		} catch (Exception ex2) {
			log.error("[getConferenceLogTypeByEventName]: " + ex2);
		}
		return null;
	}
	
}
