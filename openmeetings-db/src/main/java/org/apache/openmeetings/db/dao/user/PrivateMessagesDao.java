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
package org.apache.openmeetings.db.dao.user;

import static org.apache.openmeetings.db.entity.user.PrivateMessage.INBOX_FOLDER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.User;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrivateMessagesDao implements IDataProviderDao<PrivateMessage> {
	private static final Logger log = Red5LoggerFactory.getLogger(PrivateMessagesDao.class, webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public Long addPrivateMessage(String subject, String message, Long parentMessageId, 
			User from, User to, User owner, Boolean bookedRoom, Room room,
			Boolean isContactRequest, Long userContactId) {
		try {
			PrivateMessage privateMessage = new PrivateMessage();
			privateMessage.setInserted(new Date());
			privateMessage.setSubject(subject);
			privateMessage.setMessage(message);
			privateMessage.setFrom(from);
			privateMessage.setTo(to);
			privateMessage.setOwner(owner);
			privateMessage.setBookedRoom(Boolean.TRUE.equals(bookedRoom));
			privateMessage.setRoom(room);
			privateMessage.setParentMessage(parentMessageId);
			privateMessage.setFolderId(INBOX_FOLDER_ID);
			privateMessage.setIsRead(false);
			privateMessage.setIsContactRequest(isContactRequest);
			privateMessage.setUserContactId(userContactId);
			
			privateMessage = em.merge(privateMessage);
			Long privateMessageId = privateMessage.getFolderId();
			
			return privateMessageId;			
		} catch (Exception e) {
			log.error("[addPrivateMessage]",e);
		}
		return null;
	}
	
	public List<PrivateMessage> get(int first, int count) {
		return em.createNamedQuery("getPrivateMessages", PrivateMessage.class)
				.setFirstResult(first).setMaxResults(count)
				.getResultList();
	}
	
	public PrivateMessage get(long id) {
		TypedQuery<PrivateMessage> query = em.createNamedQuery("getPrivateMessageById", PrivateMessage.class); 
		query.setParameter("id", id);
		PrivateMessage privateMessage = null;
		try {
			privateMessage = query.getSingleResult();
	    } catch (NoResultException ex) {
	    }
		return privateMessage;
	}
	
	public PrivateMessage update(PrivateMessage entity, Long userId) {
		if (entity.getId() < 1) {
			entity.setInserted(new Date());
			em.persist(entity);
	    } else {
    		entity = em.merge(entity);
		}
		return entity;
	}
	
	private String getQuery(boolean isCount, String search, String orderBy, boolean asc) {
		StringBuilder hql = new StringBuilder("SELECT ");
		hql.append(isCount ? "COUNT(" : "").append("m").append(isCount ? ")" : "")
			.append(" FROM PrivateMessage m WHERE m.owner.user_id = :ownerId ")
			.append(" AND m.folderId = :folderId ");
		
		if (!StringUtils.isEmpty(search)) {
			hql.append(" AND ( ")
				.append("lower(m.subject) LIKE :search ")
				.append("OR lower(m.message) LIKE :search ")
				.append("OR lower(m.from.firstname) LIKE :search ")
				.append("OR lower(m.from.lastname) LIKE :search ")
				.append("OR lower(m.from.login) LIKE :search ")
				.append("OR lower(m.from.adresses.email) LIKE :search ")
				.append(" ) ");
		}
		
		if (!isCount && !StringUtils.isEmpty(orderBy)) {
			hql.append(" ORDER BY ").append(orderBy).append(asc ? " ASC" : " DESC");
		}
		return hql.toString();
	}
	
	public Long count(Long ownerId, Long folderId, String search) {
		TypedQuery<Long> query = em.createQuery(getQuery(true, search, null, true), Long.class); 
		query.setParameter("ownerId", ownerId);
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", StringUtils.lowerCase("%" + search + "%"));
		}
		query.setParameter("folderId", folderId);
		return query.getSingleResult();
	}
	
	public List<PrivateMessage> get(Long ownerId, Long folderId, String search, String orderBy, boolean asc, int start, int max) {
		TypedQuery<PrivateMessage> query = em.createQuery(getQuery(false, search, orderBy, asc), PrivateMessage.class); 
		query.setParameter("ownerId", ownerId);
		query.setParameter("folderId", folderId);
		if (!StringUtils.isEmpty(search)) {
			query.setParameter("search", StringUtils.lowerCase("%" + search + "%"));
		}
		query.setFirstResult(start);
		query.setMaxResults(max);
		return query.getResultList();
	}
	
	public int updateReadStatus(Collection<Long> ids, Boolean isRead) {
		Query query = em.createNamedQuery("updatePrivateMessagesReadStatus"); 
		query.setParameter("isRead", isRead);
		query.setParameter("ids", ids);
		return query.executeUpdate();
	}

	public Integer moveMailsToFolder(Collection<Long> ids, Long folderId) {
		Query query = em.createNamedQuery("moveMailsToFolder"); 
		query.setParameter("folderId", folderId);
		query.setParameter("ids", ids);
		return query.executeUpdate();
	}
	
	public int delete(Collection<Long> ids) {
		Query query = em.createNamedQuery("deletePrivateMessages"); 
		query.setParameter("ids", ids);
		return query.executeUpdate();
	}
	
	public List<PrivateMessage> getPrivateMessagesByRoom(Long roomId) {
		TypedQuery<PrivateMessage> query = em.createNamedQuery("getPrivateMessagesByRoom", PrivateMessage.class); 
		query.setParameter("roomId", roomId);
		return query.getResultList();
	}

	public List<PrivateMessage> get(String search, int start, int count, String order) {
		// TODO Auto-generated method stub
		return null;
	}

	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long count(String search) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(PrivateMessage entity, Long userId) {
		// TODO Auto-generated method stub
		
	}
}
