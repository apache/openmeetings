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
package org.apache.openmeetings.db.dao.basic;

import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;

import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openmeetings.db.entity.basic.ChatMessage;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ChatDao {
	@PersistenceContext
	private EntityManager em;

	public ChatMessage get(long id) {
		return em.createNamedQuery("getChatMessageById", ChatMessage.class)
				.setParameter("id", id)
				.getSingleResult();
	}

	//for export
	public List<ChatMessage> get(long start, long count) {
		return setLimits(em.createNamedQuery("getChatMessages", ChatMessage.class)
				, start, count).getResultList();
	}

	public List<ChatMessage> getGlobal(long start, long count) {
		return setLimits(em.createNamedQuery("getGlobalChatMessages", ChatMessage.class)
				, start, count).getResultList();
	}

	public List<ChatMessage> getRoom(long roomId, long start, long count, boolean all) {
		return setLimits(em.createNamedQuery("getChatMessagesByRoom", ChatMessage.class)
					.setParameter("roomId", roomId)
					.setParameter("all", all)
				, start, count).getResultList();
	}

	public List<ChatMessage> getUser(long userId, long start, long count) {
		return setLimits(em.createNamedQuery("getChatMessagesByUser", ChatMessage.class)
					.setParameter(PARAM_USER_ID, userId)
				, start, count).getResultList();
	}

	public List<ChatMessage> getUserRecent(long userId, Date date, long start, long count) {
		return setLimits(em.createNamedQuery("getChatMessagesByUserTime", ChatMessage.class)
					.setParameter(PARAM_USER_ID, userId)
					.setParameter("status", ChatMessage.Status.CLOSED)
					.setParameter("date", date)
				, start, count).getResultList();
	}

	public void closeMessages(long userId) {
		em.createNamedQuery("chatCloseMessagesByUser")
			.setParameter(PARAM_USER_ID, userId)
			.setParameter("status", ChatMessage.Status.CLOSED)
			.executeUpdate();
	}

	public ChatMessage update(ChatMessage entity) {
		return update(entity, null);
	}

	public ChatMessage update(ChatMessage entity, Date sent) {
		entity.setSent(sent == null ? new Date() : sent);
		if (entity.getId() == null) {
			em.persist(entity);
		}
		return entity;
	}

	/**
	 * @param entity - unused
	 * @param userId - unused
	 */
	public void delete(ChatMessage entity, long userId) {
		// no-op
	}

	public void deleteGlobal() {
		em.createNamedQuery("deleteChatGlobal").executeUpdate();
	}

	public void deleteRoom(Long roomId) {
		em.createNamedQuery("deleteChatRoom").setParameter("roomId", roomId).executeUpdate();
	}

	public void deleteUser(Long userId) {
		em.createNamedQuery("deleteChatUser").setParameter(PARAM_USER_ID, userId).executeUpdate();
	}
}
