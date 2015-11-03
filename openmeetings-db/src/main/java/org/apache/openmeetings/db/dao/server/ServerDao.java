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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.util.DaoHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * CRUD for {@link Server}
 * 
 * @author solomax, sebawagner
 * 
 */
@Transactional
public class ServerDao implements IDataProviderDao<Server> {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ServerDao.class, OpenmeetingsVariables.webAppRootKey);
	public final static String[] searchFields = { "name", "address", "comment" };

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UserDao usersDao;
	
	/**
	 * Get a list of all available servers
	 * 
	 * @return
	 */
	public List<Server> getServerList() {
		log.debug("getServerList enter");
		TypedQuery<Server> q = em.createNamedQuery("getAllServers",
				Server.class);
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#get(int, int)
	 */
	public List<Server> get(int start, int max) {
		TypedQuery<Server> q = em.createNamedQuery("getAllServers", Server.class);
		q.setFirstResult(start);
		q.setMaxResults(max);
		return q.getResultList();
	}

	public List<Server> get(String search, int start, int count, String order) {
		TypedQuery<Server> q = em.createQuery(DaoHelper.getSearchQuery(
				"Server", "s", search, true, false, order, searchFields),
				Server.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	/**
	 * get the list of all servers in the cluster that are ready to receive a
	 * ping (active = true)
	 * 
	 * @return
	 */
	public List<Server> getActiveServers() {
		return em.createNamedQuery("getActiveServers", Server.class)
				.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#count()
	 */
	public long count() {
		log.debug("getServerCount enter");
		TypedQuery<Long> q = em.createNamedQuery("getServerCount", Long.class);

		return q.getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Server",
				"s", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#get(long)
	 */
	public Server get(long id) {
		Server result = null;
		log.debug("getServer enter, id = " + id);
		TypedQuery<Server> q = em.createNamedQuery("getServerById",
				Server.class);
		q.setParameter("id", id);
		try {
			result = q.getSingleResult();
		} catch (NoResultException e) {
			// noop
		}
		return result;
	}

	/**
	 * Get server by its address
	 * 
	 * @param address
	 * @return
	 */
	public Server getServerByAddress(String address) {
		log.debug("getServer enter, address = " + address);
		TypedQuery<Server> q = em.createNamedQuery("getServerByAddress",
				Server.class);
		q.setParameter("address", address);
		List<Server> list = q.getResultList();
		return list.size() > 0 ? list.get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.data.OmDAO#update(org.apache.openmeetings.persistence
	 * .beans.OmEntity, long)
	 */
	public Server update(Server entity, Long userId) {
		entity.setDeleted(false);
		if (entity.getId() > 0) {
			entity.setUpdated(new Date());
			if (userId != null) {
				entity.setUpdatedby(usersDao.get(userId));
			}
			em.merge(entity);
		} else {
			entity.setInserted(new Date());
			if (userId != null) {
				entity.setInsertedby(usersDao.get(userId));
			}
			em.persist(entity);
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.data.OmDAO#delete(org.apache.openmeetings.persistence
	 * .beans.OmEntity, long)
	 */
	public void delete(Server entity, Long userId) {
		if (entity.getId() > 0) {
			entity.setUpdated(new Date());
			if (userId != null) {
				entity.setUpdatedby(usersDao.get(userId));
			}
			entity.setDeleted(true);
			em.merge(entity);
		}
	}

	/**
	 * get {@link Server} by name
	 * 
	 * @param name
	 * @return
	 */
	public List<Server> getServersByName(String name) {
		TypedQuery<Server> q = em.createNamedQuery("getServerByName",
				Server.class);
		q.setParameter("name", name);
		return q.getResultList();
	}

}
