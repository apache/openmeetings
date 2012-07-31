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
package org.openmeetings.app.data.basic.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.persistence.beans.basic.Server;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ServerDaoImpl {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ServerDaoImpl.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public List<Server> getServerList(int start, int max) {
		log.debug("getServerList enter");
		TypedQuery<Server> q = em.createNamedQuery("getAllServers",
				Server.class);
		q.setFirstResult(start);
		q.setMaxResults(max);

		return q.getResultList();
	}

	public long getServerCount() {
		log.debug("getServerCount enter");
		TypedQuery<Long> q = em.createNamedQuery("getServerCount", Long.class);

		return q.getSingleResult();
	}

	public Server getServer(long id) {
		Server result = null;
		log.debug("getServer enter, id = " + id);
		TypedQuery<Server> q = em.createNamedQuery("getServerById", Server.class);
		q.setParameter("id", id);
		try {
			result = q.getSingleResult();
		} catch (NoResultException e) {
			// noop
		}
		return result;
	}

	public Server getServerByAddress(String address) {
		log.debug("getServer enter, address = " + address);
		TypedQuery<Server> q = em.createNamedQuery("getServerByAddress", Server.class);
		q.setParameter("address", address);
		List<Server> list = q.getResultList();
		return list.size() > 0 ? list.get(0) : null;
	}

	public Server getServerWithMinimumUsers() {
		Server result = null;
		log.debug("getServerWithMinimumUsers enter");
		TypedQuery<Server> q = em.createNamedQuery("getServersWithNoUsers", Server.class);
		List<Server> l = q.getResultList();
		if (l.isEmpty()) {
			TypedQuery<Object> q1 = em.createNamedQuery("getServerWithMinimumUsers", Object.class);
			List<Object> r = q1.getResultList();
			if (!r.isEmpty()) {
				// get server id from first line
				result = getServer((Long)((Object[])r.get(0))[0]);
			}
		} else {
			result = l.get(0);
		}
		return result;
	}
	
	public Server saveServer(long id, String name, String address) {
		Server s = getServer(id);
		if (s == null) {
			s = new Server();
		}
		s.setName(name);
		s.setAddress(address);

		return em.merge(s);
	}

	public boolean deleteServer(long id) {
		Server s = getServer(id);
		if (s == null) {
			return false;
		}
		s.setDeleted(true);
		s = em.merge(s);

		return true;
	}
}
