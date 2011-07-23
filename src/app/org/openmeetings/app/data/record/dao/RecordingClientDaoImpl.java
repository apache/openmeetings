package org.openmeetings.app.data.record.dao;

import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.recording.RecordingClient;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class RecordingClientDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RecordingClientDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private RecordingClientDaoImpl() {
	}

	private static RecordingClientDaoImpl instance = null;

	public static synchronized RecordingClientDaoImpl getInstance() {
		if (instance == null) {
			instance = new RecordingClientDaoImpl();
		}

		return instance;
	}
	
	public List<RecordingClient> getRecordingClientByroomRecordingId(Long roomRecordingId) {
		try {
			log.debug("getdRecordingClientByRoomRecordingId: "+ roomRecordingId);
			
			String hql = "select r from RecordingClient r " +
					"WHERE r.roomRecordingId = :roomRecordingId ";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("roomRecordingId",roomRecordingId);
			
			List<RecordingClient> recordingClients = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return recordingClients;
			
		} catch (Exception ex2) {
			log.error("[getRecordingClientByroomRecordingId]: " , ex2);
		}
		return null;
	}
	
	public Long addRecordingClient(RecordingClient recordingClient) {
		try {
			
			//Fill and remove duplicated RoomClient Objects
			if (recordingClient.getRcl() != null) {
				recordingClient.setRcl(RoomClientDaoImpl.getInstance().getAndAddRoomClientByPublicSID(recordingClient.getRcl()));
			}
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			recordingClient = session.merge(recordingClient);
			Long recordingClientId = recordingClient.getRecordingclient_id();
			
			session.flush();
			session.refresh(recordingClient);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return recordingClientId;
		} catch (Exception ex2) {
			log.error("[addRecordingClient]: " , ex2);
		}
		return null;
	}
}
