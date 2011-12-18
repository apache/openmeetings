package org.openmeetings.app.data.logs;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.persistence.beans.logs.ConferenceLogType;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ConferenceLogTypeDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(ConferenceLogTypeDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;

	public Long addConferenceLogType(String eventType) {
		try {
			
			ConferenceLogType confLogType = new ConferenceLogType();
			confLogType.setEventType(eventType);
			confLogType.setInserted(new Date());
			
			confLogType = em.merge(confLogType);
			Long appointment_id = confLogType.getConferenceLogTypeId();

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
					
			TypedQuery<ConferenceLogType> query = em.createQuery(hql, ConferenceLogType.class);
			query.setParameter("eventType",eventType);
			
			//Seems like this does throw an error sometimes cause it does not return a unique Result
			//ConferenceLogType confLogType = (ConferenceLogType) query.getSingleResult();
			List<ConferenceLogType> confLogTypes = query.getResultList();
			ConferenceLogType confLogType = null;
			if (confLogTypes != null && confLogTypes.size() > 0) {
				confLogType = confLogTypes.get(0);
			}
			
			return confLogType;
		} catch (Exception ex2) {
			log.error("[getConferenceLogTypeByEventName]: " + ex2);
		}
		return null;
	}
	
}
