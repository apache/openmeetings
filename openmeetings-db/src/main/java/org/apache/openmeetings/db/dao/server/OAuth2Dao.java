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
import static org.apache.openmeetings.util.OpenmeetingsVariables.isAllowRegisterOauth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.util.DaoHelper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class OAuth2Dao implements IDataProviderDao<OAuthServer> {
	private static final String[] searchFields = {"name"};
	@PersistenceContext
	private EntityManager em;

	public List<OAuthServer> getActive() {
		if (!isAllowRegisterOauth()) {
			return new ArrayList<>();
		}
		TypedQuery<OAuthServer> query = em.createNamedQuery("getEnabledOAuthServers", OAuthServer.class);
		return query.getResultList();
	}

	@Override
	public OAuthServer get(Long id) {
		List<OAuthServer> list = em.createNamedQuery("getOAuthServerById", OAuthServer.class)
				.setParameter("id", id).getResultList();
		return list.size() == 1 ? list.get(0) : null;
	}

	@Override
	public List<OAuthServer> get(long start, long count) {
		return setLimits(em.createNamedQuery("getAllOAuthServers", OAuthServer.class)
				, start, count).getResultList();
	}

	@Override
	public List<OAuthServer> get(String search, long start, long count, String order) {
		return setLimits(em.createQuery(DaoHelper.getSearchQuery("OAuthServer", "s", search, true, false, null, searchFields), OAuthServer.class)
				, start, count).getResultList();
	}

	@Override
	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countOAuthServers", Long.class);
		return q.getSingleResult();
	}

	@Override
	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("OAuthServer", "s", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	@Override
	public OAuthServer update(OAuthServer server, Long userId) {
		if (server.getId() == null) {
			server.setInserted(new Date());
			em.persist(server);
		} else {
			server.setUpdated(new Date());
			server = em.merge(server);
		}
		return server;
	}

	@Override
	public void delete(OAuthServer server, Long userId) {
		server.setDeleted(true);
		update(server, userId);
	}
}
