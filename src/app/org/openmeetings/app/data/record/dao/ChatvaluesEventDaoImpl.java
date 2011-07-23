package org.openmeetings.app.data.record.dao;

import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.openmeetings.app.persistence.beans.recording.ChatvaluesEvent;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class ChatvaluesEventDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(ChatvaluesEventDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private ChatvaluesEventDaoImpl() {
	}

	private static ChatvaluesEventDaoImpl instance = null;

	public static synchronized ChatvaluesEventDaoImpl getInstance() {
		if (instance == null) {
			instance = new ChatvaluesEventDaoImpl();
		}

		return instance;
	}
	
	public List<ChatvaluesEvent> getChatvaluesEventByRoomRecordingId(Long roomrecordingId) {
		try {
			
			String hql = "select c from ChatvaluesEvent as c " +
						"where c.roomRecording.roomrecordingId = :roomrecordingId";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("roomrecordingId", roomrecordingId);
			List<ChatvaluesEvent> ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;
	
		} catch (Exception ex2) {
			log.error("[getChatvaluesEventByRoomRecordingId]: " , ex2);
		}
		return null;
	}
	
	
	public Long addChatvaluesEvent(ChatvaluesEvent chatvaluesEvent) {
		try {
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			chatvaluesEvent = session.merge(chatvaluesEvent);
			Long chatvaluesEventId = chatvaluesEvent.getChatvaluesEventId();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return chatvaluesEventId;
		} catch (Exception ex2) {
			log.error("[addChatvaluesEvent]: " , ex2);
		}
		return null;
	}
	
}
