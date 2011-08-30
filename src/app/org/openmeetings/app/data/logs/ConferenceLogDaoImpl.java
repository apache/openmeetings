package org.openmeetings.app.data.logs;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openmeetings.app.persistence.beans.logs.ConferenceLog;
import org.openmeetings.app.persistence.beans.logs.ConferenceLogType;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ConferenceLogDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(ConferenceLogDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ConferenceLogTypeDaoImpl conferenceLogTypeDao;

	public Long addConferenceLog(String eventType, Long userId, String streamid, 
			Long room_id, String userip, String scopeName, 
			Long externalUserId, String externalUserType, String email,
			String firstname, String lastname) {
		try {
			
			ConferenceLogType confLogType = conferenceLogTypeDao.getConferenceLogTypeByEventName(eventType);
			if (confLogType == null) {
				conferenceLogTypeDao.addConferenceLogType(eventType);
				confLogType = conferenceLogTypeDao.getConferenceLogTypeByEventName(eventType);
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
			
			confLog = em.merge(confLog);
			Long confLogId = confLog.getConferenceLogId();

			return confLogId;
		} catch (Exception ex2) {
			log.error("[addConferenceLog]: ",ex2);
		}
		return null;
	}
	
	
}
