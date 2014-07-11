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
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrganisationUserDao implements IDataProviderDao<Organisation_Users> {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UserDao usersDao;
	public final static String[] searchFields = {"user.lastname", "user.firstname", "user.login", "user.adresses.email"};

	public Organisation_Users get(long id) {
		TypedQuery<Organisation_Users> q = em.createNamedQuery("getOrganisationUsersById", Organisation_Users.class);
		q.setParameter("id", id);
		return q.getSingleResult();
	}

	public List<Organisation_Users> get(int start, int count) {
		throw new RuntimeException("Should not be used");
	}

	public List<Organisation_Users> get(String search, int start, int count, String sort) {
		throw new RuntimeException("Should not be used");
	}
	
	public List<Organisation_Users> get(long orgId, String search, int start, int count, String sort) {
		TypedQuery<Organisation_Users> q = em.createQuery(DaoHelper.getSearchQuery("Organisation_Users", "ou", null, search, false, false, "ou.organisation.id = :orgId", sort, searchFields), Organisation_Users.class);
		q.setParameter("orgId", orgId);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	public List<Organisation_Users> get(long orgId, int start, int count) {
		TypedQuery<Organisation_Users> q = em.createNamedQuery("getOrganisationUsersByOrgId", Organisation_Users.class);
		q.setParameter("id", orgId);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public Organisation_Users getByOrganizationAndUser(long orgId, long userId) {
		try {
			List<Organisation_Users> list = em.createNamedQuery("isUserInOrganization", Organisation_Users.class)
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
		return em.createNamedQuery("isUserInOrganization", Organisation_Users.class)
				.setParameter("orgId", orgId).setParameter("userId", userId).getResultList().size() > 0;
	}
	
	public long count() {
		throw new RuntimeException("Should not be used");
	}

	public long count(String search) {
		throw new RuntimeException("Should not be used");
	}
	
	public long count(long orgId, String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Organisation_Users", "ou", search, false, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public long count(long orgId) {
		TypedQuery<Long> q = em.createNamedQuery("countOrganisationUsers", Long.class);
		q.setParameter("id", orgId);
		return q.getSingleResult();
	}

	public void update(List<Organisation_Users> list, Long userId) {
		for (Organisation_Users ou : list) {
			update(ou, userId);
		}
	}
	
	public Organisation_Users update(Organisation_Users entity, Long userId) {
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

	private void updateUser(Organisation_Users entity, boolean delete, Long userId) {
		//entity has been detached need to re-fetch
		User u = usersDao.get(entity.getUser().getId());
		int idx = u.getOrganisation_users().indexOf(entity);
		if (delete && idx > -1) {
			Organisation_Users ou = u.getOrganisation_users().remove(idx);
			em.remove(ou);
		} else if (!delete && idx < 0) {
			u.getOrganisation_users().add(entity);
		}
		usersDao.update(u, userId);
	}
	
	public void delete(Organisation_Users entity, Long userId) {
		if (entity.getId() != null) {
			updateUser(entity, true, userId);
		}
	}
}
