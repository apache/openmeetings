package org.openmeetings.app.data.record.dao;

import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.recording.RecordingClient;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("roomRecordingId",roomRecordingId);
			
			List<RecordingClient> recordingClients = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return recordingClients;
			
		} catch (HibernateException ex) {
			log.error("[getRecordingClientByroomRecordingId]: " , ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long recordingClientId = (Long) session.save(recordingClient);
			
			session.flush();
			session.clear();
			session.refresh(recordingClient);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return recordingClientId;
		} catch (HibernateException ex) {
			log.error("[addRecordingClient]: " , ex);
		} catch (Exception ex2) {
			log.error("[addRecordingClient]: " , ex2);
		}
		return null;
	}
}
