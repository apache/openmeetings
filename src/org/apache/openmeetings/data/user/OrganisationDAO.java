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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.data.OmDAO;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrganisationDAO implements OmDAO<Organisation> {
	@PersistenceContext
	private EntityManager em;

	public Organisation get(long id) {
		TypedQuery<Organisation> query = em.createNamedQuery("getOrganisationById", Organisation.class);
		query.setParameter("organisation_id", id);
		query.setParameter("deleted", false);
		Organisation o = null;
		try {
			o = query.getSingleResult();
		} catch (NoResultException e) {
			// o = null;
		}
		return o;
	}

	public List<Organisation> get(int start, int count) {
		TypedQuery<Organisation> q = em.createNamedQuery("getNondeletedOrganisations", Organisation.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countOrganisations", Long.class);
		return q.getSingleResult();
	}

	public void update(Organisation entity) {
		// TODO Auto-generated method stub
		
	}

	public void delete(Organisation entity) {
		// TODO Auto-generated method stub
		
	}
}
