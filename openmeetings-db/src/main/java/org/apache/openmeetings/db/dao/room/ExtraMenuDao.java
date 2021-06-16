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
package org.apache.openmeetings.db.dao.room;

import static org.apache.openmeetings.db.util.DaoHelper.getSearchQuery;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.db.util.DaoHelper.single;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.db.dao.IGroupAdminDataProviderDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.ExtraMenu;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ExtraMenuDao implements IGroupAdminDataProviderDao<ExtraMenu> {
	public final static String[] searchFields = {"name", "link", "description"};

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UserDao userDao;

	@Override
	public ExtraMenu get(Long id) {
		return single(em.createNamedQuery("getExtraMenuById", ExtraMenu.class)
				.getResultList());
	}

	@Override
	public ExtraMenu get(long id) {
		return get(Long.valueOf(id));
	}

	@Override
	public List<ExtraMenu> get(long start, long count) {
		return setLimits(em.createNamedQuery("getExtraMenus", ExtraMenu.class), start, count).getResultList();
	}

	@Override
	public List<ExtraMenu> get(String search, long start, long count, String order) {
		return setLimits(
				em.createQuery(getSearchQuery("ExtraMenu", "m", search, false, false, order, searchFields)
						, ExtraMenu.class)
				, start, count).getResultList();
	}

	public List<ExtraMenu> getByGroups(List<Long> groups) {
		return em.createNamedQuery("getExtraMenuByGroups", ExtraMenu.class)
				.setParameter("ids", groups)
				.getResultList();
	}

	@Override
	public long count() {
		return em.createNamedQuery("countExtraMenus", Long.class).getSingleResult();
	}

	@Override
	public long count(String search) {
		return em.createQuery(getSearchQuery("ExtraMenu", "m", search, false, true, null, searchFields), Long.class)
				.getSingleResult();
	}

	@Override
	public ExtraMenu update(ExtraMenu entity, Long userId) {
		if (entity.getId() == null) {
			em.persist(entity);
		} else {
			entity = em.merge(entity);
		}
		return entity;
	}

	@Override
	public void delete(ExtraMenu entity, Long userId) {
		em.remove(entity);
	}

	@Override
	public List<ExtraMenu> adminGet(String search, Long adminId, long start, long count, String order) {
		final String additionalWhere = getGroupFilter(adminId);
		return setLimits(em.createQuery(getSearchQuery("ExtraMenu", "m", null, search, false, false, additionalWhere, order, searchFields), ExtraMenu.class)
				, start, count).getResultList();
	}

	@Override
	public long adminCount(String search, Long adminId) {
		final String additionalWhere = getGroupFilter(adminId);
		return em.createQuery(getSearchQuery("ExtraMenu", "m", null, search, false, true, additionalWhere, null, searchFields), Long.class)
				.getSingleResult();
	}

	private String getGroupFilter(Long adminId) {
		return userDao.get(adminId).getGroupUsers().stream()
				.filter(GroupUser::isModerator)
				.map(GroupUser::getGroup)
				.map(Group::getId)
				.map(String::valueOf)
				.collect(Collectors.joining(", ", "m.groups IN (", ")"));
	}
}
