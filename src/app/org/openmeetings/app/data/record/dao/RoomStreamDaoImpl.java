package org.openmeetings.app.data.record.dao;

import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.hibernate.beans.recording.RoomStream;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class RoomStreamDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RoomStreamDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("roomrecordingId", roomrecordingId);
			List<RoomStream> ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
	
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
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			roomStream = session.merge(roomStream);
			Long roomStreamId = roomStream.getRoomStreamId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return roomStreamId;
		} catch (Exception ex2) {
			log.error("[addRoomStream]: " , ex2);
		}
		return null;
	}
}
