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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class GroupUserDao implements IDataProviderDao<GroupUser> {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UserDao userDao;
	public final static String[] searchFields = {"user.lastname", "user.firstname", "user.login", "user.address.email"};

	@Override
	public GroupUser get(long id) {
		return get(Long.valueOf(id));
	}
	
	@Override
	public GroupUser get(Long id) {
		TypedQuery<GroupUser> q = em.createNamedQuery("getGroupUsersById", GroupUser.class);
		q.setParameter("id", id);
		return q.getSingleResult();
	}

	@Override
	public List<GroupUser> get(int start, int count) {
		throw new RuntimeException("Should not be used");
	}

	@Override
	public List<GroupUser> get(String search, int start, int count, String sort) {
		throw new RuntimeException("Should not be used");
	}
	
	public List<GroupUser> get(long groupId, String search, int start, int count, String sort) {
		TypedQuery<GroupUser> q = em.createQuery(DaoHelper.getSearchQuery(GroupUser.class.getSimpleName(), "ou", null, search, false, false, "ou.group.id = :groupId", sort, searchFields), GroupUser.class);
		q.setParameter("groupId", groupId);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	public List<GroupUser> get(long groupId, int start, int count) {
		TypedQuery<GroupUser> q = em.createNamedQuery("getGroupUsersByGroupId", GroupUser.class);
		q.setParameter("id", groupId);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public GroupUser getByGroupAndUser(long groupId, long userId) {
		try {
			List<GroupUser> list = em.createNamedQuery("isUserInGroup", GroupUser.class)
					.setParameter("groupId", groupId).setParameter("userId", userId).getResultList();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (Exception e) {
			//no-op
		}
		return null;
	}
	
	public boolean isUserInGroup(long groupId, long userId) {
		return em.createNamedQuery("isUserInGroup", GroupUser.class)
				.setParameter("groupId", groupId).setParameter("userId", userId).getResultList().size() > 0;
	}
	
	@Override
	public long count() {
		throw new RuntimeException("Should not be used");
	}

	@Override
	public long count(String search) {
		throw new RuntimeException("Should not be used");
	}
	
	public long count(long groupId, String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery(GroupUser.class.getSimpleName(), "ou", search, false, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public long count(long groupId) {
		TypedQuery<Long> q = em.createNamedQuery("countGroupUsers", Long.class);
		q.setParameter("id", groupId);
		return q.getSingleResult();
	}

	public void update(List<GroupUser> list, Long userId) {
		for (GroupUser ou : list) {
			update(ou, userId);
		}
	}
	
	@Override
	public GroupUser update(GroupUser entity, Long userId) {
		if (entity.getId() == null) {
			entity.setInserted(new Date());
			em.persist(entity);
		} else {
			entity.setUpdated(new Date());
			entity = em.merge(entity);
		}
		updateUser(entity, false, userId);
		return entity;
	}

	private void updateUser(GroupUser entity, boolean delete, Long userId) {
		//entity has been detached need to re-fetch
		User u = userDao.get(entity.getUser().getId());
		int idx = u.getGroupUsers().indexOf(entity);
		if (delete && idx > -1) {
			GroupUser ou = u.getGroupUsers().remove(idx);
			em.remove(ou);
		} else if (!delete && idx < 0) {
			u.getGroupUsers().add(entity);
		}
		userDao.update(u, userId);
	}
	
	@Override
	public void delete(GroupUser entity, Long userId) {
		if (entity.getId() != null) {
			updateUser(entity, true, userId);
		}
	}
}
