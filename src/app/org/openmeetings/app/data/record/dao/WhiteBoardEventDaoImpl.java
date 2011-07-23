package org.openmeetings.app.data.record.dao;

import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.recording.WhiteBoardEvent;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class WhiteBoardEventDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(WhiteBoardEventDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private WhiteBoardEventDaoImpl() {
	}

	private static WhiteBoardEventDaoImpl instance = null;

	public static synchronized WhiteBoardEventDaoImpl getInstance() {
		if (instance == null) {
			instance = new WhiteBoardEventDaoImpl();
		}

		return instance;
	}
	
	public List<WhiteBoardEvent> getWhiteboardEventsInRange(long startTime, long endTime, long roomrecordingId) {
		try {
			
			String hql = "select wbe from WhiteBoardEvent as wbe " +
					"where wbe.starttime between :startTime and :endTime " +
					"AND wbe.roomRecording.roomrecordingId = :roomrecordingId";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("startTime", startTime);
			query.setParameter("endTime", endTime);
			query.setParameter("roomrecordingId", roomrecordingId);
			List<WhiteBoardEvent> ll = query.getResultList();
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return ll;
			
		} catch (Exception ex2) {
			log.error("[getWhiteboardEventsInRange]: " , ex2);
		}
		return null;
	}
	
	public Long addWhiteBoardEvent(WhiteBoardEvent whiteBoardEvent) {
		try {
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			whiteBoardEvent = session.merge(whiteBoardEvent);
			Long whiteBoardEventId = whiteBoardEvent.getWhiteBoardEventId();
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return whiteBoardEventId;
		} catch (Exception ex2) {
			log.error("[addWhiteBoardEvent]: " , ex2);
		}
		return null;
	}
	
}
