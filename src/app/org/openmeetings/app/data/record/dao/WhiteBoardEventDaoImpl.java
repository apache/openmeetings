package org.openmeetings.app.data.record.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.persistence.beans.recording.WhiteBoardEvent;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class WhiteBoardEventDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(WhiteBoardEventDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public List<WhiteBoardEvent> getWhiteboardEventsInRange(long startTime, long endTime, long roomrecordingId) {
		try {
			
			String hql = "select wbe from WhiteBoardEvent as wbe " +
					"where wbe.starttime between :startTime and :endTime " +
					"AND wbe.roomRecording.roomrecordingId = :roomrecordingId";
			
			TypedQuery<WhiteBoardEvent> query = em.createQuery(hql, WhiteBoardEvent.class);
			query.setParameter("startTime", startTime);
			query.setParameter("endTime", endTime);
			query.setParameter("roomrecordingId", roomrecordingId);
			List<WhiteBoardEvent> ll = query.getResultList();
			
			return ll;
			
		} catch (Exception ex2) {
			log.error("[getWhiteboardEventsInRange]: " , ex2);
		}
		return null;
	}
	
	public Long addWhiteBoardEvent(WhiteBoardEvent whiteBoardEvent) {
		try {
			
			whiteBoardEvent = em.merge(whiteBoardEvent);
			Long whiteBoardEventId = whiteBoardEvent.getWhiteBoardEventId();
			
			return whiteBoardEventId;
		} catch (Exception ex2) {
			log.error("[addWhiteBoardEvent]: " , ex2);
		}
		return null;
	}
	
}
