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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.util.DaoHelper;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class GroupDao implements IDataProviderDao<Group> {
	public final static String[] searchFields = {"name"};
	@PersistenceContext
	private EntityManager em;

	public Group get(long id) {
		TypedQuery<Group> query = em.createNamedQuery("getGroupById", Group.class);
		query.setParameter("id", id);
		Group o = null;
		try {
			o = query.getSingleResult();
		} catch (NoResultException e) {
			// o = null;
		}
		return o;
	}

	public Group get(String name) {
		List<Group> groups = em.createNamedQuery("getGroupByName", Group.class).setParameter("name", name).getResultList();
		return groups == null || groups.isEmpty() ? null : groups.get(0);
	}

	public List<Group> get(int start, int count) {
		TypedQuery<Group> q = em.createNamedQuery("getNondeletedGroups", Group.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<Group> get(String search, int start, int count, String sort) {
		TypedQuery<Group> q = em.createQuery(DaoHelper.getSearchQuery("Group", "o", search, true, false, sort, searchFields), Group.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countGroups", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Group", "o", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public List<Group> get(Collection<Long> ids) {
		return em.createNamedQuery("getGroupsByIds", Group.class).setParameter("ids", ids).getResultList();
	}

	public Group update(Group entity, Long userId) {
		if (entity.getId() == null) {
			if (userId != null) {
				entity.setInsertedby(userId);
			}
			entity.setInserted(new Date());
			em.persist(entity);
		} else {
			if (userId != null) {
				entity.setUpdatedby(userId);
			}
			entity.setUpdated(new Date());
			em.merge(entity);
		}
		return entity;
	}

	public void delete(Group entity, Long userId) {
		em.createNamedQuery("deleteUsersFromGroup")
			.setParameter("id", entity.getId())
			.executeUpdate();

		entity.setDeleted(true);
		if (userId != null) {
			entity.setUpdatedby(userId);
		}

		em.merge(entity);
	}
}
