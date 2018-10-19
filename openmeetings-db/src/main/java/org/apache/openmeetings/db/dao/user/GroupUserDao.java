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

import static org.apache.openmeetings.db.util.DaoHelper.UNSUPPORTED;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.util.DaoHelper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class GroupUserDao implements IDataProviderDao<GroupUser> {
	private static final String[] searchFields = {"user.lastname", "user.firstname", "user.login", "user.address.email"};
	@PersistenceContext
	private EntityManager em;

	@Override
	public GroupUser get(Long id) {
		TypedQuery<GroupUser> q = em.createNamedQuery("getGroupUsersById", GroupUser.class);
		q.setParameter("id", id);
		return q.getSingleResult();
	}

	@Override
	public List<GroupUser> get(long start, long count) {
		throw UNSUPPORTED;
	}

	@Override
	public List<GroupUser> get(String search, long start, long count, String sort) {
		throw UNSUPPORTED;
	}

	public List<GroupUser> get(long groupId, String search, long start, long count, String sort) {
		return setLimits(
				em.createQuery(DaoHelper.getSearchQuery(GroupUser.class.getSimpleName(), "ou", null, search, false, false, "ou.group.id = :groupId", sort, searchFields), GroupUser.class)
					.setParameter("groupId", groupId)
				, start, count).getResultList();
	}

	public List<GroupUser> get(long groupId, long start, long count) {
		return setLimits(
				em.createNamedQuery("getGroupUsersByGroupId", GroupUser.class).setParameter("id", groupId)
				, start, count).getResultList();
	}

	public GroupUser getByGroupAndUser(Long groupId, Long userId) {
		List<GroupUser> list = em.createNamedQuery("isUserInGroup", GroupUser.class)
				.setParameter("groupId", groupId).setParameter(PARAM_USER_ID, userId).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public boolean isUserInGroup(long groupId, long userId) {
		return !em.createNamedQuery("isUserInGroup", GroupUser.class)
				.setParameter("groupId", groupId).setParameter(PARAM_USER_ID, userId).getResultList().isEmpty();
	}

	@Override
	public long count() {
		throw UNSUPPORTED;
	}

	@Override
	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery(GroupUser.class.getSimpleName(), "ou", search, false, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	public long count(long groupId) {
		TypedQuery<Long> q = em.createNamedQuery("countGroupUsers", Long.class);
		q.setParameter("id", groupId);
		return q.getSingleResult();
	}

	@Override
	public GroupUser update(GroupUser entity, Long userId) {
		throw UNSUPPORTED;
	}

	@Override
	public void delete(GroupUser entity, Long userId) {
		throw UNSUPPORTED;
	}
}
