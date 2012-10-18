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
package org.apache.openmeetings.data.user.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.OmDAO;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.utils.DaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrganisationUserDAO implements OmDAO<Organisation_Users> {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UsersDaoImpl usersDao;
	public final static String[] searchFields = {"user.lastname", "user.firstname", "user.login", "user.adresses.email"};

	public Organisation_Users get(long id) {
		TypedQuery<Organisation_Users> q = em.createNamedQuery("getOrganisationUsersById", Organisation_Users.class);
		q.setParameter("id", id);
		return q.getSingleResult();
	}

	public List<Organisation_Users> get(int start, int count) {
		throw new RuntimeException("Should not be used");
	}

	public List<Organisation_Users> get(String search, int start, int count) {
		throw new RuntimeException("Should not be used");
	}
	
	public List<Organisation_Users> get(long orgId, String search, int start, int count) {
		TypedQuery<Organisation_Users> q = em.createQuery(DaoHelper.getSearchQuery("Organisation_Users", "ou", search, false, false, searchFields), Organisation_Users.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	public List<Organisation_Users> get(long orgId, int start, int count) {
		TypedQuery<Organisation_Users> q = em.createNamedQuery("getOrganisationUsersByOrgId", Organisation_Users.class);
		q.setParameter("id", orgId);
		q.setFirstResult(start);
		q.setMaxResults(count);

		// This refresh is necessary because after saving the user entity the
		// user_id is somehow not
		// filled into the Organisation_Users, this might be fixed by
		// implementing another
		// JOIN or mapping strategy
		List<Organisation_Users> orgUserList = q.getResultList();
		for (Organisation_Users ou : orgUserList) {
			em.refresh(ou);
		}
		return orgUserList;
	}

	public long count() {
		throw new RuntimeException("Should not be used");
	}

	public long count(String search) {
		throw new RuntimeException("Should not be used");
	}
	
	public long count(long orgId, String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Organisation_Users", "ou", search, false, true, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public long count(long orgId) {
		TypedQuery<Long> q = em.createNamedQuery("countOrganisationUsers", Long.class);
		q.setParameter("id", orgId);
		return q.getSingleResult();
	}

	public Organisation_Users update(Organisation_Users entity, long userId) {
		//if (entity.getOrganisation_users_id())// TODO Auto-generated method stub
		return entity;
	}

	public void delete(Organisation_Users entity, long userId) {
		if (entity.getOrganisation_users_id() != null) {
			Users u = usersDao.get(entity.getUser_id());
			int idx = u.getOrganisation_users().indexOf(entity);
			//entity has been detached need to re-fetch
			Organisation_Users ou = u.getOrganisation_users().remove(idx);
			em.remove(ou);
			usersDao.update(u, userId);
		}
	}

	public void add() {
		
	}
}
