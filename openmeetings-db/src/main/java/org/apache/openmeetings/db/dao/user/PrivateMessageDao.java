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
import static org.apache.openmeetings.db.util.DaoHelper.UNSUPPORTED;
import static org.apache.openmeetings.db.util.DaoHelper.getStringParam;
import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PrivateMessageDao implements IDataProviderDao<PrivateMessage> {
	private static final Logger log = LoggerFactory.getLogger(PrivateMessageDao.class);
	private static final String PARAM_FLDRID = "folderId";
	@PersistenceContext
	private EntityManager em;

	public Long addPrivateMessage(String subject, String message,
			User from, User to, User owner,
			boolean isContactRequest, Long userContactId) {
		try {
			PrivateMessage privateMessage = new PrivateMessage();
			privateMessage.setInserted(new Date());
			privateMessage.setSubject(subject);
			privateMessage.setMessage(message);
			privateMessage.setFrom(from);
			privateMessage.setTo(to);
			privateMessage.setOwner(owner);
			privateMessage.setFolderId(INBOX_FOLDER_ID);
			privateMessage.setIsRead(false);
			privateMessage.setIsContactRequest(isContactRequest);
			privateMessage.setUserContactId(userContactId);

			privateMessage = em.merge(privateMessage);
			return privateMessage.getFolderId();
		} catch (Exception e) {
			log.error("[addPrivateMessage]",e);
		}
		return null;
	}

	@Override
	public List<PrivateMessage> get(long first, long count) {
		return setLimits(em.createNamedQuery("getPrivateMessages", PrivateMessage.class)
				, first, count).getResultList();
	}

	@Override
	public PrivateMessage get(Long id) {
		return only(em.createNamedQuery("getPrivateMessageById", PrivateMessage.class)
				.setParameter("id", id).getResultList());
	}

	@Override
	public PrivateMessage update(PrivateMessage entity, Long userId) {
		if (entity.getId() == null) {
			entity.setInserted(new Date());
			em.persist(entity);
		} else {
			entity = em.merge(entity);
		}
		return entity;
	}

	private static String getQuery(boolean isCount, String search, String orderBy, boolean asc) {
		StringBuilder hql = new StringBuilder("SELECT ");
		hql.append(isCount ? "COUNT(" : "").append("m").append(isCount ? ")" : "")
			.append(" FROM PrivateMessage m WHERE m.owner.id = :ownerId ")
			.append(" AND m.folderId = :folderId ");

		if (!Strings.isEmpty(search)) {
			hql.append(" AND ( ")
				.append("lower(m.subject) LIKE :search ")
				.append("OR lower(m.message) LIKE :search ")
				.append("OR lower(m.from.firstname) LIKE :search ")
				.append("OR lower(m.from.lastname) LIKE :search ")
				.append("OR lower(m.from.login) LIKE :search ")
				.append("OR lower(m.from.address.email) LIKE :search ")
				.append(" ) ");
		}

		if (!isCount && !Strings.isEmpty(orderBy)) {
			hql.append(" ORDER BY ").append(orderBy).append(asc ? " ASC" : " DESC");
		}
		return hql.toString();
	}

	private static <T> void setSearch(TypedQuery<T> query, String search) {
		if (!Strings.isEmpty(search)) {
			query.setParameter("search", getStringParam(search));
		}
	}

	public Long count(Long ownerId, Long folderId, String search) {
		TypedQuery<Long> query = em.createQuery(getQuery(true, search, null, true), Long.class);
		query.setParameter("ownerId", ownerId);
		setSearch(query, search);
		query.setParameter(PARAM_FLDRID, folderId);
		return query.getSingleResult();
	}

	public List<PrivateMessage> get(Long ownerId, Long folderId, String search, String orderBy, boolean asc, long start, long max) {
		TypedQuery<PrivateMessage> query = em.createQuery(getQuery(false, search, orderBy, asc), PrivateMessage.class);
		query.setParameter("ownerId", ownerId);
		query.setParameter(PARAM_FLDRID, folderId);
		setSearch(query, search);
		return setLimits(query, start, max).getResultList();
	}

	public int updateReadStatus(Collection<Long> ids, Boolean isRead) {
		Query query = em.createNamedQuery("updatePrivateMessagesReadStatus");
		query.setParameter("isRead", isRead);
		query.setParameter("ids", ids);
		return query.executeUpdate();
	}

	public int moveMailsToFolder(Collection<Long> ids, Long folderId) {
		Query query = em.createNamedQuery("moveMailsToFolder");
		query.setParameter(PARAM_FLDRID, folderId);
		query.setParameter("ids", ids);
		return query.executeUpdate();
	}

	public int delete(Collection<Long> ids) {
		Query query = em.createNamedQuery("deletePrivateMessages");
		query.setParameter("ids", ids);
		return query.executeUpdate();
	}

	public List<PrivateMessage> getByRoom(Long roomId) {
		return em.createNamedQuery("getPrivateMessagesByRoom", PrivateMessage.class)
				.setParameter("roomId", roomId).getResultList();
	}

	@Override
	public List<PrivateMessage> get(String search, long start, long count, SortParam<String> order) {
		throw UNSUPPORTED;
	}

	@Override
	public long count() {
		throw UNSUPPORTED;
	}

	@Override
	public long count(String search) {
		throw UNSUPPORTED;
	}

	@Override
	public void delete(PrivateMessage entity, Long userId) {
		throw UNSUPPORTED;
	}
}
