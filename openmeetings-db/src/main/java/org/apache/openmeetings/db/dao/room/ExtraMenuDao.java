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

import static org.apache.openmeetings.db.util.DaoHelper.getRoot;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.db.util.DaoHelper.single;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.openmeetings.db.dao.IGroupAdminDataProviderDao;
import org.apache.openmeetings.db.entity.room.ExtraMenu;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ExtraMenuDao implements IGroupAdminDataProviderDao<ExtraMenu> {
	private static final List<String> searchFields = List.of("name", "link", "description");

	@PersistenceContext
	private EntityManager em;

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
	public List<ExtraMenu> get(String search, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, ExtraMenu.class, false, search, searchFields, false, null, sort, start, count);
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
		return DaoHelper.count(em, ExtraMenu.class, search, searchFields, false, null);
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
	public List<ExtraMenu> adminGet(String search, Long adminId, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, ExtraMenu.class, true, search, searchFields, false
				, (builder, query) -> getGroupFilter(adminId, builder, query)
				, sort, start, count);
	}

	@Override
	public long adminCount(String search, Long adminId) {
		return DaoHelper.count(em, ExtraMenu.class, search, searchFields, false
				, (builder, query) -> getGroupFilter(adminId, builder, query));
	}

	private Predicate getGroupFilter(Long adminId, CriteriaBuilder builder, CriteriaQuery<?> query) {
		Root<ExtraMenu> root = getRoot(query, ExtraMenu.class);
		return builder.in(root.get("groups")).value(DaoHelper.groupAdminQuery(adminId, builder, query));
	}
}
