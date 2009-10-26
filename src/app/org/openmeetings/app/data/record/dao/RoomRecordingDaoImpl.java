package org.openmeetings.app.data.record.dao;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.recording.RoomRecording;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

public class RoomRecordingDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RoomRecordingDaoImpl.class, "openmeetings");

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
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("roomrecordingId",roomrecordingId);
			
			RoomRecording roomRecording = (RoomRecording) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomRecording;
		} catch (HibernateException ex) {
			log.error("[getRoomRecordingById]: " , ex);
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
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long roomRecordingId = (Long) session.save(roomRecording);
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomRecordingId;
		} catch (HibernateException ex) {
			log.error("[addRoomRecording]: " , ex);
		} catch (Exception ex2) {
			log.error("[addRoomRecording]: " , ex2);
		}
		return null;
	}
	
	public Long updateRoomRecording(RoomRecording roomRecording) {
		try {
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(roomRecording);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
		} catch (HibernateException ex) {
			log.error("[updateRoomRecording]: " , ex);
		} catch (Exception ex2) {
			log.error("[updateRoomRecording]: " , ex2);
		}
		return null;
	}
	
}
