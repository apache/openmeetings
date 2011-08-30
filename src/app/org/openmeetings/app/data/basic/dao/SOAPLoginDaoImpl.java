package org.openmeetings.app.data.basic.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.persistence.beans.basic.SOAPLogin;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SOAPLoginDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(
			SOAPLoginDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ManageCryptStyle manageCryptStyle;

	public String addSOAPLogin(String sessionHash, Long room_id,
			boolean becomemoderator, boolean showAudioVideoTest,
			boolean allowSameURLMultipleTimes, Long recording_id,
			boolean showNickNameDialog, String landingZone,
			boolean allowRecording) {
		try {

			String thistime = "TIME_" + (new Date().getTime());

			String hash = manageCryptStyle.getInstanceOfCrypt()
					.createPassPhrase(thistime);

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

			soapLogin = em.merge(soapLogin);
			Long soapLoginId = soapLogin.getSoapLoginId();

			if (soapLoginId > 0) {
				return hash;
			} else {
				throw new Exception("Could not store SOAPLogin");
			}

		} catch (Exception ex2) {
			log.error("[addSOAPLogin]: ", ex2);
		}
		return null;
	}

	public SOAPLogin getSOAPLoginByHash(String hash) {
		try {
			String hql = "select sl from SOAPLogin as sl "
					+ "WHERE sl.hash LIKE :hash";
			Query query = em.createQuery(hql);
			query.setParameter("hash", hash);
			List<SOAPLogin> sList = query.getResultList();

			if (sList.size() > 1) {
				throw new Exception(
						"there are more then one SOAPLogin with identical hash! "
								+ hash);
			}

			if (sList.size() == 1) {
				return sList.get(0);
			}

		} catch (Exception ex2) {
			log.error("[getSOAPLoginByHash]: ", ex2);
		}
		return null;
	}

	public void updateSOAPLogin(SOAPLogin soapLogin) {
		try {
			if (soapLogin.getSoapLoginId() == 0) {
				em.persist(soapLogin);
			} else {
				if (!em.contains(soapLogin)) {
					em.merge(soapLogin);
				}
			}
		} catch (Exception ex2) {
			log.error("[updateSOAPLogin]: ", ex2);
		}
	}

}
