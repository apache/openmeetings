package org.openmeetings.app.data.logs;

import java.util.Date;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.hibernate.beans.logs.ConferenceLog;
import org.openmeetings.app.hibernate.beans.logs.ConferenceLogType;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class ConferenceLogDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(ConferenceLogDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private ConferenceLogDaoImpl() {
	}

	private static ConferenceLogDaoImpl instance = null;

	public static synchronized ConferenceLogDaoImpl getInstance() {
		if (instance == null) {
			instance = new ConferenceLogDaoImpl();
		}
		return instance;
	}
	

	public Long addConferenceLog(String eventType, Long userId, String streamid, 
			Long room_id, String userip, String scopeName, 
			Long externalUserId, String externalUserType, String email,
			String firstname, String lastname) {
		try {
			
			ConferenceLogType confLogType = ConferenceLogTypeDaoImpl.getInstance().getConferenceLogTypeByEventName(eventType);
			if (confLogType == null) {
				ConferenceLogTypeDaoImpl.getInstance().addConferenceLogType(eventType);
				confLogType = ConferenceLogTypeDaoImpl.getInstance().getConferenceLogTypeByEventName(eventType);
			}
			
			ConferenceLog confLog = new ConferenceLog();
			confLog.setConferenceLogType(confLogType);
			confLog.setInserted(new Date());
			confLog.setUserId(userId);
			confLog.setStreamid(streamid);
			confLog.setScopeName(scopeName);
			confLog.setRoom_id(room_id);
			confLog.setUserip(userip);
			confLog.setExternalUserId(externalUserId);
			confLog.setExternalUserType(externalUserType);
			confLog.setFirstname(firstname);
			confLog.setLastname(lastname);
			confLog.setEmail(email);
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			confLog = session.merge(confLog);
			session.flush();
			Long confLogId = confLog.getConferenceLogId();

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return confLogId;
		} catch (Exception ex2) {
			log.error("[addConferenceLog]: ",ex2);
		}
		return null;
	}
	
	
}
