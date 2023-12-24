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

import static org.apache.openmeetings.db.util.DaoHelper.getRoot;
import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.openmeetings.db.dao.IGroupAdminDataProviderDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class GroupDao implements IGroupAdminDataProviderDao<Group> {
	private static final List<String> searchFields = List.of("name");
	private static final List<String> guSearchFields = List.copyOf(searchFields.stream().map(n -> "group." + n).toList());
	@PersistenceContext
	private EntityManager em;

	@Override
	public Group get(Long id) {
		return only(em.createNamedQuery("getGroupById", Group.class)
				.setParameter("id", id).getResultList());
	}

	public Group get(String name) {
		List<Group> groups = em.createNamedQuery("getGroupByName", Group.class).setParameter("name", name).getResultList();
		return groups == null || groups.isEmpty() ? null : groups.get(0);
	}

	public Group getExternal(String name) {
		List<Group> groups = em.createNamedQuery("getExtGroupByName", Group.class).setParameter("name", name).getResultList();
		Group g = groups == null || groups.isEmpty() ? null : groups.get(0);
		if (g == null) {
			g = update(new Group().setExternal(true).setName(name), null);
		}
		return g;
	}

	@Override
	public List<Group> get(long start, long count) {
		return setLimits(em.createNamedQuery("getNondeletedGroups", Group.class), start, count)
				.getResultList();
	}

	@Override
	public List<Group> get(String search, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, Group.class, false, search, searchFields, true
				, null
				, sort, start, count);
	}

	private Predicate getAdminFilter(Long adminId, CriteriaBuilder builder, CriteriaQuery<?> query) {
		Root<GroupUser> root = getRoot(query, GroupUser.class);
		return builder.and(builder.equal(root.get("user").get("id"), adminId)
						, builder.isTrue(root.get("moderator")));
	}

	@Override
	public List<Group> adminGet(String search, Long adminId, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, GroupUser.class, Group.class
				, (builder, root) -> root.get("group")
				, true, search, guSearchFields, true
				, (b, q) -> getAdminFilter(adminId, b, q)
				, sort, start, count);
	}

	@Override
	public long count() {
		return em.createNamedQuery("countGroups", Long.class).getSingleResult();
	}

	@Override
	public long count(String search) {
		return DaoHelper.count(em, Group.class, search, searchFields, true, null);
	}

	@Override
	public long adminCount(String search, Long adminId) {
		return DaoHelper.count(em, GroupUser.class
				, (builder, root) -> builder.countDistinct(root.get("group"))
				, search, guSearchFields, false
				, (b, q) -> getAdminFilter(adminId, b, q.distinct(true)));
	}

	public List<Group> get(Collection<Long> ids) {
		return em.createNamedQuery("getGroupsByIds", Group.class).setParameter("ids", ids).getResultList();
	}

	public List<Group> getLimited() {
		return em.createNamedQuery("getLimitedGroups", Group.class).getResultList();
	}

	public List<Group> getGroupsForUserNotifications() {
		return em.createNamedQuery("getGroupsForUserNotifications", Group.class).getResultList();
	}

	@Override
	public Group update(Group entity, Long userId) {
		if (entity.getId() == null) {
			if (userId != null) {
				entity.setInsertedby(userId);
			}
			em.persist(entity);
		} else {
			if (userId != null) {
				entity.setUpdatedby(userId);
			}
			entity = em.merge(entity);
		}
		return entity;
	}

	@Override
	public void delete(Group g, Long userId) {
		em.createNamedQuery("deleteGroupUsersByGroup").setParameter("id", g.getId()).executeUpdate();

		g.setDeleted(true);
		if (userId != null) {
			g.setUpdatedby(userId);
		}
		em.merge(g);
	}
}
