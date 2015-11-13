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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.util.DaoHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Insert/Update/Delete {@link LdapConfig}
 * 
 * 
 * @author swagner
 * 
 */
@Transactional
public class LdapConfigDao implements IDataProviderDao<LdapConfig> {
	private static final Logger log = Red5LoggerFactory.getLogger(LdapConfigDao.class, webAppRootKey);
	public final static String[] searchFields = {"name", "configFileName", "domain", "comment"};

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UserDao userDao;

	public Long addLdapConfig(String name, Boolean addDomainToUserName,
			String configFileName, String domain, Long insertedby,
			Boolean isActive) {
		try {

			LdapConfig ldapConfig = new LdapConfig();
			ldapConfig.setAddDomainToUserName(addDomainToUserName);
			ldapConfig.setConfigFileName(configFileName);
			ldapConfig.setDeleted(false);
			ldapConfig.setDomain(domain);
			ldapConfig.setActive(isActive);
			ldapConfig.setName(name);
			ldapConfig.setInserted(new Date());
			if (insertedby != null) {
				log.debug("addLdapConfig :1: " + userDao.get(insertedby));
				ldapConfig.setInsertedby(userDao.get(insertedby));
			}

			log.debug("addLdapConfig :2: " + insertedby);

			ldapConfig = em.merge(ldapConfig);
			Long id = ldapConfig.getId();

			if (id != null) {
				return id;
			} else {
				throw new Exception("Could not store SOAPLogin");
			}

		} catch (Exception ex2) {
			log.error("[addLdapConfig]: ", ex2);
		}
		return null;
	}

	public Long addLdapConfigByObject(LdapConfig ldapConfig) {
		try {

			ldapConfig.setDeleted(false);
			ldapConfig.setInserted(new Date());

			ldapConfig = em.merge(ldapConfig);
			Long id = ldapConfig.getId();

			if (id != null) {
				return id;
			} else {
				throw new Exception("Could not store SOAPLogin");
			}

		} catch (Exception ex2) {
			log.error("[addLdapConfig]: ", ex2);
		}
		return null;
	}

	public Long updateLdapConfig(Long id, String name,
			Boolean addDomainToUserName, String configFileName, String domain,
			Long updatedby, Boolean isActive) {
		try {

			LdapConfig ldapConfig = get(id);

			if (ldapConfig == null) {
				return -1L;
			}

			ldapConfig.setAddDomainToUserName(addDomainToUserName);
			ldapConfig.setConfigFileName(configFileName);
			ldapConfig.setDeleted(false);
			ldapConfig.setDomain(domain);
			ldapConfig.setActive(isActive);
			ldapConfig.setName(name);
			ldapConfig.setUpdated(new Date());
			if (updatedby != null) {
				log.debug("updateLdapConfig :1: " + userDao.get(updatedby));
				ldapConfig.setUpdatedby(userDao.get(updatedby));
			}

			log.debug("updateLdapConfig :2: " + updatedby);

			ldapConfig = em.merge(ldapConfig);
			id = ldapConfig.getId();

			return id;

		} catch (Exception ex2) {
			log.error("[updateLdapConfig]: ", ex2);
		}
		return -1L;
	}

	public LdapConfig get(long id) {
		try {

			TypedQuery<LdapConfig> query = em.createNamedQuery("getLdapConfigById", LdapConfig.class);
			query.setParameter("id", id);

			LdapConfig ldapConfig = null;
			try {
				ldapConfig = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return ldapConfig;

		} catch (Exception ex2) {
			log.error("[get]: ", ex2);
		}
		return null;
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

		List<LdapConfig> result = new ArrayList<LdapConfig>();
		result.add(ldapConfig);
		result.addAll(getActive());
		return result;
	}

	public List<LdapConfig> get(int start, int count) {
		TypedQuery<LdapConfig> q = em.createNamedQuery("getNondeletedLdapConfigs", LdapConfig.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<LdapConfig> get(String search, int start, int count, String sort) {
		TypedQuery<LdapConfig> q = em.createQuery(DaoHelper.getSearchQuery("LdapConfig", "lc", search, true, false, sort, searchFields), LdapConfig.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
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

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("LdapConfig", "lc", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public LdapConfig update(LdapConfig entity, Long userId) {
		try {
			if (entity.getId() == null) {
				entity.setInserted(new Date());
				if (userId != null) {
					entity.setInsertedby(userDao.get(userId));
				}
				entity.setDeleted(false);
				em.persist(entity);
			} else {
				entity.setUpdated(new Date());
				if (userId != null) {
					entity.setUpdatedby(userDao.get(userId));
				}
				entity.setDeleted(false);
				em.merge(entity);
			}
		} catch (PersistenceException ex) {
			log.error("[update LdapConfig]", ex);
		}
		return entity;
	}

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
