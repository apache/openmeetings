/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.openmeetings.app.data.record.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.persistence.beans.recording.ChatvaluesEvent;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ChatvaluesEventDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(ChatvaluesEventDaoImpl.class, OpenmeetingsVariables.webAppRootKey);
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
