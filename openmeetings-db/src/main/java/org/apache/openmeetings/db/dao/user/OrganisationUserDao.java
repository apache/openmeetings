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
import org.apache.openmeetings.db.entity.user.OrganisationUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrganisationUserDao implements IDataProviderDao<OrganisationUser> {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UserDao usersDao;
	public final static String[] searchFields = {"user.lastname", "user.firstname", "user.login", "user.adresses.email"};

	public OrganisationUser get(long id) {
		TypedQuery<OrganisationUser> q = em.createNamedQuery("getOrganisationUsersById", OrganisationUser.class);
		q.setParameter("id", id);
		return q.getSingleResult();
	}

	public List<OrganisationUser> get(int start, int count) {
		throw new RuntimeException("Should not be used");
	}

	public List<OrganisationUser> get(String search, int start, int count, String sort) {
		throw new RuntimeException("Should not be used");
	}
	
	public List<OrganisationUser> get(long orgId, String search, int start, int count, String sort) {
		TypedQuery<OrganisationUser> q = em.createQuery(DaoHelper.getSearchQuery(OrganisationUser.class.getSimpleName(), "ou", null, search, false, false, "ou.organisation.id = :orgId", sort, searchFields), OrganisationUser.class);
		q.setParameter("orgId", orgId);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	public List<OrganisationUser> get(long orgId, int start, int count) {
		TypedQuery<OrganisationUser> q = em.createNamedQuery("getOrganisationUsersByOrgId", OrganisationUser.class);
		q.setParameter("id", orgId);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public OrganisationUser getByOrganizationAndUser(long orgId, long userId) {
		try {
			List<OrganisationUser> list = em.createNamedQuery("isUserInOrganization", OrganisationUser.class)
					.setParameter("orgId", orgId).setParameter("userId", userId).getResultList();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (Exception e) {
			//no-op
		}
		return null;
	}
	
	public boolean isUserInOrganization(long orgId, long userId) {
		return em.createNamedQuery("isUserInOrganization", OrganisationUser.class)
				.setParameter("orgId", orgId).setParameter("userId", userId).getResultList().size() > 0;
	}
	
	public long count() {
		throw new RuntimeException("Should not be used");
	}

	public long count(String search) {
		throw new RuntimeException("Should not be used");
	}
	
	public long count(long orgId, String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery(OrganisationUser.class.getSimpleName(), "ou", search, false, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public long count(long orgId) {
		TypedQuery<Long> q = em.createNamedQuery("countOrganisationUsers", Long.class);
		q.setParameter("id", orgId);
		return q.getSingleResult();
	}

	public void update(List<OrganisationUser> list, Long userId) {
		for (OrganisationUser ou : list) {
			update(ou, userId);
		}
	}
	
	public OrganisationUser update(OrganisationUser entity, Long userId) {
		if (entity.getId() == null) {
			entity.setStarttime(new Date());
			em.persist(entity);
		} else {
			entity.setUpdatetime(new Date());
			entity = em.merge(entity);
		}
		updateUser(entity, false, userId);
		return entity;
	}

	private void updateUser(OrganisationUser entity, boolean delete, Long userId) {
		//entity has been detached need to re-fetch
		User u = usersDao.get(entity.getUser().getId());
		int idx = u.getOrganisationUsers().indexOf(entity);
		if (delete && idx > -1) {
			OrganisationUser ou = u.getOrganisationUsers().remove(idx);
			em.remove(ou);
		} else if (!delete && idx < 0) {
			u.getOrganisationUsers().add(entity);
		}
		usersDao.update(u, userId);
	}
	
	public void delete(OrganisationUser entity, Long userId) {
		if (entity.getId() != null) {
			updateUser(entity, true, userId);
		}
	}
}
