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
package org.apache.openmeetings.db.dao.server;

import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Insert/Update/Delete {@link LdapConfig}
 *
 *
 * @author swagner
 *
 */
@Repository
@Transactional
public class LdapConfigDao implements IDataProviderDao<LdapConfig> {
	private static final Logger log = LoggerFactory.getLogger(LdapConfigDao.class);
	private static final List<String> searchFields = List.of("name", "configFileName", "domain", "comment");

	@PersistenceContext
	private EntityManager em;

	@Inject
	private UserDao userDao;

	@Override
	public LdapConfig get(Long id) {
		return only(em.createNamedQuery("getLdapConfigById", LdapConfig.class)
				.setParameter("id", id).getResultList());
	}

	public List<LdapConfig> getActive() {
		log.debug("getActiveLdapConfigs");

		// get all users
		TypedQuery<LdapConfig> query =  em.createNamedQuery("getActiveLdapConfigs", LdapConfig.class);
		query.setParameter("isActive", true);

		return query.getResultList();
	}

	public List<LdapConfig> get() {
		//Add localhost Domain
		LdapConfig ldapConfig = new LdapConfig();

		ldapConfig.setName("local DB [internal]");
		ldapConfig.setId(-1L);

		List<LdapConfig> result = new ArrayList<>();
		result.add(ldapConfig);
		result.addAll(getActive());
		return result;
	}

	@Override
	public List<LdapConfig> get(long start, long count) {
		return setLimits(em.createNamedQuery("getNondeletedLdapConfigs", LdapConfig.class)
				, start, count).getResultList();
	}

	@Override
	public List<LdapConfig> get(String search, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, LdapConfig.class, false, search, searchFields, true, null, sort, start, count);
	}

	@Override
	public long count() {
		try {
			TypedQuery<Long> query = em.createNamedQuery("countNondeletedLdapConfigs", Long.class);
			List<Long> ll = query.getResultList();
			log.debug("selectMaxFromLdapConfig {}", ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromLdapConfig] ", ex2);
		}
		return 0L;
	}

	@Override
	public long count(String search) {
		return DaoHelper.count(em, LdapConfig.class, search, searchFields, true, null);
	}

	@Override
	public LdapConfig update(LdapConfig entity, Long userId) {
		try {
			entity.setDeleted(false);
			if (entity.getId() == null) {
				if (userId != null) {
					entity.setInsertedby(userDao.get(userId));
				}
				em.persist(entity);
			} else {
				if (userId != null) {
					entity.setUpdatedby(userDao.get(userId));
				}
				entity = em.merge(entity);
			}
		} catch (PersistenceException ex) {
			log.error("[update LdapConfig]", ex);
		}
		return entity;
	}

	@Override
	public void delete(LdapConfig entity, Long userId) {
		if (entity.getId() != null) {
			if (userId != null) {
				entity.setUpdatedby(userDao.get(userId));
			}
			entity.setDeleted(true);
			em.merge(entity);
		}
	}
}
