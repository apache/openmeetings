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

import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.util.DaoHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	private static final Logger log = Red5LoggerFactory.getLogger(LdapConfigDao.class, getWebAppRootKey());
	private static final String[] searchFields = {"name", "configFileName", "domain", "comment"};

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UserDao userDao;

	@Override
	public LdapConfig get(Long id) {
		List<LdapConfig> list = em.createNamedQuery("getLdapConfigById", LdapConfig.class)
				.setParameter("id", id).getResultList();
		return list.size() == 1 ? list.get(0) : null;
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
	public List<LdapConfig> get(String search, long start, long count, String sort) {
		return setLimits(em.createQuery(DaoHelper.getSearchQuery("LdapConfig", "lc", search, true, false, sort, searchFields), LdapConfig.class)
				, start, count).getResultList();
	}

	@Override
	public long count() {
		try {
			TypedQuery<Long> query = em.createNamedQuery("countNondeletedLdapConfigs", Long.class);
			List<Long> ll = query.getResultList();
			log.debug("selectMaxFromLdapConfig" + ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromLdapConfig] ", ex2);
		}
		return 0L;
	}

	@Override
	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("LdapConfig", "lc", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	@Override
	public LdapConfig update(LdapConfig entity, Long userId) {
		try {
			entity.setDeleted(false);
			if (entity.getId() == null) {
				entity.setInserted(new Date());
				if (userId != null) {
					entity.setInsertedby(userDao.get(userId));
				}
				em.persist(entity);
			} else {
				entity.setUpdated(new Date());
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
			entity.setUpdated(new Date());
			if (userId != null) {
				entity.setUpdatedby(userDao.get(userId));
			}
			entity.setDeleted(true);
			em.merge(entity);
		}
	}
}
