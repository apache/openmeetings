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
package org.apache.openmeetings.data.basic.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.IDataProviderDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.LdapConfig;
import org.apache.openmeetings.utils.DaoHelper;
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
	private static final Logger log = Red5LoggerFactory.getLogger(
			LdapConfigDao.class, OpenmeetingsVariables.webAppRootKey);
	public final static String[] searchFields = {"name", "configFileName", "domain", "comment"};

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UsersDao usersDao;

	public Long addLdapConfig(String name, Boolean addDomainToUserName,
			String configFileName, String domain, Long insertedby,
			Boolean isActive) {
		try {

			LdapConfig ldapConfig = new LdapConfig();
			ldapConfig.setAddDomainToUserName(addDomainToUserName);
			ldapConfig.setConfigFileName(configFileName);
			ldapConfig.setDeleted(false);
			ldapConfig.setDomain(domain);
			ldapConfig.setIsActive(isActive);
			ldapConfig.setName(name);
			ldapConfig.setInserted(new Date());
			if (insertedby != null) {
				log.debug("addLdapConfig :1: " + usersDao.get(insertedby));
				ldapConfig.setInsertedby(usersDao.get(insertedby));
			}

			log.debug("addLdapConfig :2: " + insertedby);

			ldapConfig = em.merge(ldapConfig);
			Long ldapConfigId = ldapConfig.getLdapConfigId();

			if (ldapConfigId > 0) {
				return ldapConfigId;
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
			Long ldapConfigId = ldapConfig.getLdapConfigId();

			if (ldapConfigId > 0) {
				return ldapConfigId;
			} else {
				throw new Exception("Could not store SOAPLogin");
			}

		} catch (Exception ex2) {
			log.error("[addLdapConfig]: ", ex2);
		}
		return null;
	}

	public Long updateLdapConfig(Long ldapConfigId, String name,
			Boolean addDomainToUserName, String configFileName, String domain,
			Long updatedby, Boolean isActive) {
		try {

			LdapConfig ldapConfig = this.get(ldapConfigId);

			if (ldapConfig == null) {
				return -1L;
			}

			ldapConfig.setAddDomainToUserName(addDomainToUserName);
			ldapConfig.setConfigFileName(configFileName);
			ldapConfig.setDeleted(false);
			ldapConfig.setDomain(domain);
			ldapConfig.setIsActive(isActive);
			ldapConfig.setName(name);
			ldapConfig.setUpdated(new Date());
			if (updatedby != null) {
				log.debug("updateLdapConfig :1: " + usersDao.get(updatedby));
				ldapConfig.setUpdatedby(usersDao.get(updatedby));
			}

			log.debug("updateLdapConfig :2: " + updatedby);

			ldapConfig = em.merge(ldapConfig);
			ldapConfigId = ldapConfig.getLdapConfigId();

			return ldapConfigId;

		} catch (Exception ex2) {
			log.error("[updateLdapConfig]: ", ex2);
		}
		return -1L;
	}

	public LdapConfig get(long ldapConfigId) {
		try {

			String hql = "select c from LdapConfig c "
					+ "WHERE c.ldapConfigId = :ldapConfigId "
					+ "AND c.deleted = :deleted";

			TypedQuery<LdapConfig> query = em
					.createQuery(hql, LdapConfig.class);
			query.setParameter("ldapConfigId", ldapConfigId);
			query.setParameter("deleted", false);

			LdapConfig ldapConfig = null;
			try {
				ldapConfig = query.getSingleResult();
			} catch (NoResultException ex) {
			}

			return ldapConfig;

		} catch (Exception ex2) {
			log.error("[getLdapConfigById]: ", ex2);
		}
		return null;
	}

	public List<LdapConfig> getLdapConfigs(int start, int max, String orderby,
			boolean asc) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LdapConfig> cq = cb.createQuery(LdapConfig.class);
			Root<LdapConfig> c = cq.from(LdapConfig.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			cq.where(condition);
			cq.distinct(asc);
			if (asc) {
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<LdapConfig> q = em.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<LdapConfig> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getLdapConfigs]", ex2);
		}
		return null;
	}

	public Long selectMaxFromLdapConfig() {
		try {
			log.debug("selectMaxFromConfigurations ");
			// get all users
			TypedQuery<Long> query = em
					.createQuery(
							"select count(c.ldapConfigId) from LdapConfig c where c.deleted = false",
							Long.class);
			List<Long> ll = query.getResultList();
			log.debug("selectMaxFromLdapConfig" + ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromLdapConfig] ", ex2);
		}
		return null;
	}

	public Long deleteLdapConfigById(Long ldapConfigId) {
		try {

			LdapConfig ldapConfig = this.get(ldapConfigId);

			if (ldapConfig == null) {
				return null;
			}

			ldapConfig = em
					.find(LdapConfig.class, ldapConfig.getLdapConfigId());
			em.remove(ldapConfig);

			return ldapConfigId;

		} catch (Exception ex2) {
			log.error("[deleteLdapConfigById]: ", ex2);
		}
		return null;
	}

	public List<LdapConfig> getActiveLdapConfigs() {
		try {
			log.debug("selectMaxFromConfigurations ");

			String hql = "select c from LdapConfig c "
					+ "where c.deleted = false "
					+ "AND c.isActive = :isActive ";

			// get all users
			TypedQuery<LdapConfig> query = em
					.createQuery(hql, LdapConfig.class);
			query.setParameter("isActive", true);
			List<LdapConfig> ll = query.getResultList();

			return ll;
		} catch (Exception ex2) {
			log.error("[getActiveLdapConfigs] ", ex2);
		}
		return null;
	}

	public List<LdapConfig> getLdapConfigs() {
		try {
			log.debug("selectMaxFromConfigurations ");

			String hql = "select c from LdapConfig c "
					+ "where c.deleted = false ";

			// get all users
			TypedQuery<LdapConfig> query = em
					.createQuery(hql, LdapConfig.class);
			List<LdapConfig> ll = query.getResultList();

			return ll;
		} catch (Exception ex2) {
			log.error("[getActiveLdapConfigs] ", ex2);
		}
		return null;
	}

	public List<LdapConfig> get(int start, int count) {
		TypedQuery<LdapConfig> q = em.createNamedQuery(
				"getNondeletedLdapConfigs", LdapConfig.class);
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
		return selectMaxFromLdapConfig();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("LdapConfig", "lc", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public LdapConfig update(LdapConfig entity, Long userId) {
		try {
			if (entity.getLdapConfigId() <= 0) {
				entity.setInserted(new Date());
				if (userId != null) {
					entity.setInsertedby(usersDao.get(userId));
				}
				entity.setDeleted(false);
				em.persist(entity);
			} else {
				entity.setUpdated(new Date());
				if (userId != null) {
					entity.setUpdatedby(usersDao.get(userId));
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
		if (entity.getLdapConfigId() >= 0) {
			entity.setUpdated(new Date());
			if (userId != null) {
				entity.setUpdatedby(usersDao.get(userId));
			}
			entity.setDeleted(true);
			em.merge(entity);
		}
	}

}
