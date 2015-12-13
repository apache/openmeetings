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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.util.DaoHelper;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OAuth2Dao implements IDataProviderDao<OAuthServer> {

	public final static String[] searchFields = {"name"};
	@PersistenceContext
	private EntityManager em;
	
	public List<OAuthServer> getEnabledOAuthServers() {
		TypedQuery<OAuthServer> query = em.createNamedQuery("getEnabledOAuthServers", OAuthServer.class);
		return query.getResultList();
	}
	
	public OAuthServer get(long id) {
		TypedQuery<OAuthServer> query = em.createNamedQuery("getOAuthServerById", OAuthServer.class);
		query.setParameter("id", id);
		OAuthServer result = null;
		try {
			result = query.getSingleResult();
		} catch (NoResultException e) {}
		return result;
	}

	public List<OAuthServer> get(int start, int count) {
		TypedQuery<OAuthServer> query = em.createNamedQuery("getAllOAuthServers", OAuthServer.class);
		query.setFirstResult(start);
		query.setMaxResults(count);
		return query.getResultList();
	}

	public List<OAuthServer> get(String search, int start, int count, String order) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("OAuthServer", "s", search, true, false, null, searchFields), Long.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return null;
	}

	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countOAuthServers", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("OAuthServer", "s", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	public OAuthServer update(OAuthServer server, Long userId) {
		if (server.getId() == null) {
			em.persist(server);
		} else {
			server = em.merge(server);
		}
		return server;
	}

	public void delete(OAuthServer server, Long userId) {
		server.setDeleted(true);
		update(server, userId);
	}

}
