package org.openmeetings.app.data.record.dao;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.recording.RoomRecording;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class RoomRecordingDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RoomRecordingDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private RoomRecordingDaoImpl() {
	}

	private static RoomRecordingDaoImpl instance = null;

	public static synchronized RoomRecordingDaoImpl getInstance() {
		if (instance == null) {
			instance = new RoomRecordingDaoImpl();
		}

		return instance;
	}
	
	public RoomRecording getRoomRecordingById(Long roomrecordingId) {
		try {
			log.debug("getRoomRecordingById: "+ roomrecordingId);
			
			String hql = "select r from RoomRecording r " +
					"WHERE r.roomrecordingId = :roomrecordingId ";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("roomrecordingId",roomrecordingId);
			
			RoomRecording roomRecording = null;
			try {
				roomRecording = (RoomRecording) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return roomRecording;
		} catch (Exception ex2) {
			log.error("[getRoomRecordingById]: " , ex2);
		}
		return null;
	}
	
	public Long addRoomRecording(RoomRecording roomRecording) {
		try {
			
			//Fill and remove duplicated RoomClient Objects
			if (roomRecording.getEnduser() != null) {
				roomRecording.setEnduser(RoomClientDaoImpl.getInstance().getAndAddRoomClientByPublicSID(roomRecording.getEnduser()));
			}
		
			if (roomRecording.getStartedby() != null) {
				roomRecording.setStartedby(RoomClientDaoImpl.getInstance().getAndAddRoomClientByPublicSID(roomRecording.getStartedby()));
			}
			
			log.debug("roomRecording.getRoom_setup() ID: "+roomRecording.getRoom_setup().getRooms_id());
			
			log.debug("roomRecording.getEnduser().getRoomClientId(): "+roomRecording.getEnduser().getRoomClientId());
			log.debug("roomRecording.getStartedby().getRoomClientId(): "+roomRecording.getStartedby().getRoomClientId());
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			roomRecording = session.merge(roomRecording);
			Long roomRecordingId = roomRecording.getRoomrecordingId();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return roomRecordingId;
		} catch (Exception ex2) {
			log.error("[addRoomRecording]: " , ex2);
		}
		return null;
	}
	
	public Long updateRoomRecording(RoomRecording roomRecording) {
		try {
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (roomRecording.getRoomrecordingId() == null) {
				session.persist(roomRecording);
			    } else {
			    	if (!session.contains(roomRecording)) {
			    		session.merge(roomRecording);
			    }
			}
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
		} catch (Exception ex2) {
			log.error("[updateRoomRecording]: " , ex2);
		}
		return null;
	}
	
}
