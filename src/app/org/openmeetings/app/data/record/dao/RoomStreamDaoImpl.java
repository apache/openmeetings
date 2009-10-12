package org.openmeetings.app.data.record.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.recording.RoomStream;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

public class RoomStreamDaoImpl {

	private static final Logger log = Logger.getLogger(RoomStreamDaoImpl.class);

	private RoomStreamDaoImpl() {
	}

	private static RoomStreamDaoImpl instance = null;

	public static synchronized RoomStreamDaoImpl getInstance() {
		if (instance == null) {
			instance = new RoomStreamDaoImpl();
		}

		return instance;
	}

	public List<RoomStream> getRoomStreamsByRoomRecordingId(Long roomrecordingId) {
		try {
			
			String hql = "select c from RoomStream as c " +
						"where c.roomRecording.roomrecordingId = :roomrecordingId";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("roomrecordingId", roomrecordingId);
			List<RoomStream> ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
	
		} catch (HibernateException ex) {
			log.error("[getRoomStreamsByRoomRecordingId]: " , ex);
		} catch (Exception ex2) {
			log.error("[getRoomStreamsByRoomRecordingId]: " , ex2);
		}
		return null;
	}
	
	public Long addRoomStream(RoomStream roomStream) {
		try {
			
			//Fill and remove duplicated RoomClient Objects
			if (roomStream.getRcl() != null) {
				roomStream.setRcl(RoomClientDaoImpl.getInstance().getAndAddRoomClientByPublicSID(roomStream.getRcl()));
			}
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long roomStreamId = (Long) session.save(roomStream);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomStreamId;
		} catch (HibernateException ex) {
			log.error("[addRoomStream]: " , ex);
		} catch (Exception ex2) {
			log.error("[addRoomStream]: " , ex2);
		}
		return null;
	}
}
