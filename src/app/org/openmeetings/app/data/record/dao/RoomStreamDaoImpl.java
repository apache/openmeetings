package org.openmeetings.app.data.record.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.persistence.beans.recording.RoomStream;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoomStreamDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(RoomStreamDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private RoomClientDaoImpl roomClientDao;

	public List<RoomStream> getRoomStreamsByRoomRecordingId(Long roomrecordingId) {
		try {
			
			String hql = "select c from RoomStream as c " +
						"where c.roomRecording.roomrecordingId = :roomrecordingId";
			
			TypedQuery<RoomStream> query = em.createQuery(hql, RoomStream.class);
			query.setParameter("roomrecordingId", roomrecordingId);
			List<RoomStream> ll = query.getResultList();
			
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
				roomStream.setRcl(roomClientDao.getAndAddRoomClientByPublicSID(roomStream.getRcl()));
			}
			
			roomStream = em.merge(roomStream);
			Long roomStreamId = roomStream.getRoomStreamId();
			
			return roomStreamId;
		} catch (Exception ex2) {
			log.error("[addRoomStream]: " , ex2);
		}
		return null;
	}
}
