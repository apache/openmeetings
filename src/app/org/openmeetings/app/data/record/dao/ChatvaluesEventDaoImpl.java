package org.openmeetings.app.data.record.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.persistence.beans.recording.ChatvaluesEvent;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ChatvaluesEventDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(ChatvaluesEventDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;

	public List<ChatvaluesEvent> getChatvaluesEventByRoomRecordingId(Long roomrecordingId) {
		try {
			
			String hql = "select c from ChatvaluesEvent as c " +
						"where c.roomRecording.roomrecordingId = :roomrecordingId";
			
			TypedQuery<ChatvaluesEvent> query = em.createQuery(hql, ChatvaluesEvent.class);
			query.setParameter("roomrecordingId", roomrecordingId);
			List<ChatvaluesEvent> ll = query.getResultList();
			
			return ll;
	
		} catch (Exception ex2) {
			log.error("[getChatvaluesEventByRoomRecordingId]: " , ex2);
		}
		return null;
	}
	
	
	public Long addChatvaluesEvent(ChatvaluesEvent chatvaluesEvent) {
		try {
			
			chatvaluesEvent = em.merge(chatvaluesEvent);
			Long chatvaluesEventId = chatvaluesEvent.getChatvaluesEventId();
			
			return chatvaluesEventId;
		} catch (Exception ex2) {
			log.error("[addChatvaluesEvent]: " , ex2);
		}
		return null;
	}
	
}
