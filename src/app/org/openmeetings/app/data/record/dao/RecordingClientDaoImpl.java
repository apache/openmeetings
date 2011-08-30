package org.openmeetings.app.data.record.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.persistence.beans.recording.RecordingClient;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RecordingClientDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RecordingClientDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private RoomClientDaoImpl roomClientDao;
	
	public List<RecordingClient> getRecordingClientByroomRecordingId(Long roomRecordingId) {
		try {
			log.debug("getdRecordingClientByRoomRecordingId: "+ roomRecordingId);
			
			String hql = "select r from RecordingClient r " +
					"WHERE r.roomRecordingId = :roomRecordingId ";
			
			Query query = em.createQuery(hql);
			query.setParameter("roomRecordingId",roomRecordingId);
			
			List<RecordingClient> recordingClients = query.getResultList();
			
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
				recordingClient.setRcl(roomClientDao.getAndAddRoomClientByPublicSID(recordingClient.getRcl()));
			}
			
			recordingClient = em.merge(recordingClient);
			Long recordingClientId = recordingClient.getRecordingclient_id();
			
			return recordingClientId;
		} catch (Exception ex2) {
			log.error("[addRecordingClient]: " , ex2);
		}
		return null;
	}
}
