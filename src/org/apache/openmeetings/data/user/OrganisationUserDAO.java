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
package org.apache.openmeetings.data.user;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.OmDAO;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;

public class OrganisationUserDAO implements OmDAO<Organisation_Users> {
	@PersistenceContext
	private EntityManager em;

	public Organisation_Users get(long id) {
		TypedQuery<Organisation_Users> q = em.createNamedQuery("getOrganisationUsersById", Organisation_Users.class);
		q.setParameter("id", id);
		return q.getSingleResult();
	}

	public List<Organisation_Users> get(int start, int count) {
		throw new RuntimeException("Should not be used");
	}

	public List<Organisation_Users> get(long orgId, int start, int count) {
		TypedQuery<Organisation_Users> q = em.createNamedQuery("getOrganisationUsersByOrgId", Organisation_Users.class);
		q.setParameter("id", orgId);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public long count() {
		throw new RuntimeException("Should not be used");
	}

	public long count(long orgId) {
		TypedQuery<Long> q = em.createNamedQuery("countOrganisationUsers", Long.class);
		q.setParameter("id", orgId);
		return q.getSingleResult();
	}

	public void update(Organisation_Users entity) {
		// TODO Auto-generated method stub
		
	}

	public void delete(Organisation_Users entity) {
		// TODO Auto-generated method stub
		
	}

}
