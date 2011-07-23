package org.openmeetings.app.data.basic.dao;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.persistence.beans.basic.SOAPLogin;
import org.openmeetings.app.persistence.beans.lang.FieldLanguage;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;

public class SOAPLoginDaoImpl {
	
	private static final Logger log = Red5LoggerFactory.getLogger(SOAPLoginDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private SOAPLoginDaoImpl() {
	}

	private static SOAPLoginDaoImpl instance = null;
 
	public static synchronized SOAPLoginDaoImpl getInstance() {
		if (instance == null) {
			instance = new SOAPLoginDaoImpl();
		}
		return instance;
	}
	
	public String addSOAPLogin(String sessionHash, Long room_id, 
				boolean becomemoderator, boolean showAudioVideoTest,
				boolean allowSameURLMultipleTimes, Long recording_id,
				boolean showNickNameDialog, String landingZone, 
				boolean allowRecording) {
		try {
			
			String thistime = "TIME_"+(new Date().getTime());
			
			String hash = ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(thistime);
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();

			SOAPLogin soapLogin = new SOAPLogin();
			soapLogin.setCreated(new Date());
			soapLogin.setUsed(false);
			soapLogin.setRoom_id(room_id);
			soapLogin.setAllowSameURLMultipleTimes(allowSameURLMultipleTimes);
			soapLogin.setHash(hash);
			soapLogin.setRoomRecordingId(recording_id);
			soapLogin.setSessionHash(sessionHash);
			soapLogin.setBecomemoderator(becomemoderator);
			soapLogin.setShowAudioVideoTest(showAudioVideoTest);
			soapLogin.setShowNickNameDialog(showNickNameDialog);
			soapLogin.setLandingZone(landingZone);
			soapLogin.setAllowRecording(allowRecording);

			soapLogin = session.merge(soapLogin);
			Long soapLoginId = soapLogin.getSoapLoginId(); 

			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			if (soapLoginId > 0) {
				return hash;
			} else {
				throw new Exception("Could not store SOAPLogin");
			}
			
		} catch (Exception ex2) {
			log.error("[addSOAPLogin]: ",ex2);
		}
		return null;
	}
	
	
	public SOAPLogin getSOAPLoginByHash(String hash) {
		try {
			String hql = "select sl from SOAPLogin as sl " +
							"WHERE sl.hash LIKE :hash";
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("hash", hash);
			List<SOAPLogin> sList = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			if (sList.size() > 1) {
				throw new Exception ("there are more then one SOAPLogin with identical hash! "+hash);
			}
			
			if (sList.size() == 1) {
				return sList.get(0);
			}
			
			
		} catch (Exception ex2) {
			log.error("[getSOAPLoginByHash]: ",ex2);
		}
		return null;
	}
	
	public void updateSOAPLogin(SOAPLogin soapLogin) {
		try {
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			if (soapLogin.getSoapLoginId() == 0) {
				session.persist(soapLogin);
			    } else {
			    	if (!session.contains(soapLogin)) {
			    		session.merge(soapLogin);
			    }
			}
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[updateSOAPLogin]: ",ex2);
		}
	}
		

}
