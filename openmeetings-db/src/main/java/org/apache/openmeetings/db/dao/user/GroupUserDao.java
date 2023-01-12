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
import static org.apache.openmeetings.db.util.DaoHelper.getRoot;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.db.util.DaoHelper.single;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;

import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class GroupUserDao implements IDataProviderDao<GroupUser> {
	private static final List<String> searchFields = List.of("user.lastname", "user.firstname", "user.login", "user.address.email");
	private static final String PARAM_GROUPID = "groupId";
	@PersistenceContext
	private EntityManager em;

	@Override
	public GroupUser get(Long id) {
		return em.createNamedQuery("getGroupUsersById", GroupUser.class)
				.setParameter("id", id)
				.getSingleResult();
	}

	@Override
	public List<GroupUser> get(long start, long count) {
		throw UNSUPPORTED;
	}

	@Override
	public List<GroupUser> get(String search, long start, long count, SortParam<String> sort) {
		throw UNSUPPORTED;
	}

	private Predicate getGroupFilter(Long groupId, CriteriaBuilder builder, CriteriaQuery<?> query) {
		Root<GroupUser> root = getRoot(query, GroupUser.class);
		return builder.equal(root.get("group").get("id"), groupId);
	}

	public List<GroupUser> get(long groupId, String search, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, GroupUser.class, false, search, searchFields, false
				, (builder, query) -> getGroupFilter(groupId, builder, query)
				, sort, start, count);
	}

	public List<GroupUser> get(long groupId, long start, long count) {
		return setLimits(
				em.createNamedQuery("getGroupUsersByGroupId", GroupUser.class).setParameter("id", groupId)
				, start, count).getResultList();
	}

	public GroupUser getByGroupAndUser(Long groupId, Long userId) {
		return single(em.createNamedQuery("isUserInGroup", GroupUser.class)
				.setParameter(PARAM_GROUPID, groupId)
				.setParameter(PARAM_USER_ID, userId)
				.getResultList());
	}

	public boolean isUserInGroup(long groupId, long userId) {
		return !em.createNamedQuery("isUserInGroup", GroupUser.class)
				.setParameter(PARAM_GROUPID, groupId).setParameter(PARAM_USER_ID, userId)
				.getResultList()
				.isEmpty();
	}

	@Override
	public long count() {
		throw UNSUPPORTED;
	}

	@Override
	public long count(String search) {
		return DaoHelper.count(em, GroupUser.class, search, searchFields, false, null);
	}

	public long count(long groupId) {
		return em.createNamedQuery("countGroupUsers", Long.class)
				.setParameter("id", groupId)
				.getSingleResult();
	}

	@Override
	public GroupUser update(GroupUser entity, Long userId) {
		throw UNSUPPORTED;
	}

	@Override
	public void delete(GroupUser entity, Long userId) {
		throw UNSUPPORTED;
	}

	public long getGroupUserCountAddedAfter(Long id, Date date) {
		return em.createNamedQuery("getGroupUserCountAddedAfter", Long.class)
				.setParameter("id", id)
				.setParameter("inserted", date)
				.getSingleResult();
	}

	public List<User> getGroupModerators(Long id) {
		return em.createNamedQuery("getGroupModerators", User.class)
				.setParameter("id", id)
				.getResultList();
	}
}
