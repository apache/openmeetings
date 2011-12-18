package org.openmeetings.app.data.record.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.persistence.beans.recording.RoomRecording;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomRecordingDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RoomRecordingDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private RoomClientDaoImpl roomClientDao;
	
	public RoomRecording getRoomRecordingById(Long roomrecordingId) {
		try {
			log.debug("getRoomRecordingById: "+ roomrecordingId);
			
			String hql = "select r from RoomRecording r " +
					"WHERE r.roomrecordingId = :roomrecordingId ";
			
			TypedQuery<RoomRecording> query = em.createQuery(hql, RoomRecording.class);
			query.setParameter("roomrecordingId",roomrecordingId);
			
			RoomRecording roomRecording = null;
			try {
				roomRecording = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
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
				roomRecording.setEnduser(roomClientDao.getAndAddRoomClientByPublicSID(roomRecording.getEnduser()));
			}
		
			if (roomRecording.getStartedby() != null) {
				roomRecording.setStartedby(roomClientDao.getAndAddRoomClientByPublicSID(roomRecording.getStartedby()));
			}
			
			log.debug("roomRecording.getRoom_setup() ID: "+roomRecording.getRoom_setup().getRooms_id());
			
			log.debug("roomRecording.getEnduser().getRoomClientId(): "+roomRecording.getEnduser().getRoomClientId());
			log.debug("roomRecording.getStartedby().getRoomClientId(): "+roomRecording.getStartedby().getRoomClientId());
			
			roomRecording = em.merge(roomRecording);
			Long roomRecordingId = roomRecording.getRoomrecordingId();
			
			return roomRecordingId;
		} catch (Exception ex2) {
			log.error("[addRoomRecording]: " , ex2);
		}
		return null;
	}
	
	public Long updateRoomRecording(RoomRecording roomRecording) {
		try {
			
			if (roomRecording.getRoomrecordingId() == null) {
				em.persist(roomRecording);
		    } else {
		    	if (!em.contains(roomRecording)) {
		    		em.merge(roomRecording);
			    }
			}
			
		} catch (Exception ex2) {
			log.error("[updateRoomRecording]: " , ex2);
		}
		return null;
	}
	
}
