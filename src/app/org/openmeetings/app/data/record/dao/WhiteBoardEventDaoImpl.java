package org.openmeetings.app.data.record.dao;

import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmeetings.app.hibernate.beans.recording.WhiteBoardEvent;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
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
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("startTime", startTime);
			query.setLong("endTime", endTime);
			query.setLong("roomrecordingId", roomrecordingId);
			List<WhiteBoardEvent> ll = query.list();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
			
		} catch (HibernateException ex) {
			log.error("[getWhiteboardEventsInRange]: " , ex);
		} catch (Exception ex2) {
			log.error("[getWhiteboardEventsInRange]: " , ex2);
		}
		return null;
	}
	
	public Long addWhiteBoardEvent(WhiteBoardEvent whiteBoardEvent) {
		try {
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Long whiteBoardEventId = (Long) session.save(whiteBoardEvent);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return whiteBoardEventId;
		} catch (HibernateException ex) {
			log.error("[addWhiteBoardEvent]: " , ex);
		} catch (Exception ex2) {
			log.error("[addWhiteBoardEvent]: " , ex2);
		}
		return null;
	}
	
}
