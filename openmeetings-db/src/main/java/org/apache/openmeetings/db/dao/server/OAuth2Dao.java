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

import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isAllowRegisterOauth;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class OAuth2Dao implements IDataProviderDao<OAuthServer> {
	private static final List<String> searchFields = List.of("name");
	@PersistenceContext
	private EntityManager em;

	@Inject
	private ConfigurationDao cfgDao;

	public List<OAuthServer> getActive() {
		if (!isAllowRegisterOauth()) {
			return List.of();
		}
		return em.createNamedQuery("getEnabledOAuthServers", OAuthServer.class)
				.getResultList();
	}

	@Override
	public OAuthServer get(Long id) {
		return only(em.createNamedQuery("getOAuthServerById", OAuthServer.class)
				.setParameter("id", id).getResultList());
	}

	@Override
	public List<OAuthServer> get(long start, long count) {
		return setLimits(em.createNamedQuery("getAllOAuthServers", OAuthServer.class)
				, start, count).getResultList();
	}

	@Override
	public List<OAuthServer> get(String search, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, OAuthServer.class, false, search, searchFields, true, null, sort, start, count);
	}

	@Override
	public long count() {
		return em.createNamedQuery("countOAuthServers", Long.class)
				.getSingleResult();
	}

	@Override
	public long count(String search) {
		return DaoHelper.count(em, LdapConfig.class, search, searchFields, true, null);
	}

	@Override
	public OAuthServer update(OAuthServer server, Long userId) {
		if (server.getId() == null) {
			em.persist(server);
		} else {
			server = em.merge(server);
		}
		cfgDao.updateCsp();
		return server;
	}

	@Override
	public void delete(OAuthServer server, Long userId) {
		server.setDeleted(true);
		update(server, userId);
	}
}
