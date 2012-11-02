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
package org.apache.openmeetings.data.basic.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.IDataProviderDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.utils.DaoHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * CRUD for {@link Server}
 * 
 * @author solomax, swagner
 * 
 */
@Transactional
public class ServerDao implements IDataProviderDao<Server> {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ServerDao.class, OpenmeetingsVariables.webAppRootKey);
	public final static String[] searchFields = {"name", "address", "comment"};

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private UsersDao usersDao;

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
		log.debug("getServerList enter");
		TypedQuery<Server> q = em.createNamedQuery("getAllServers",
				Server.class);
		q.setFirstResult(start);
		q.setMaxResults(max);

		return q.getResultList();
	}
	
	public List<Server> get(String search, int start, int count, String order) {
		TypedQuery<Server> q = em.createQuery(DaoHelper.getSearchQuery("Server", "s", search, true, false, order, searchFields), Server.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
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
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Server", "s", search, true, true, null, searchFields), Long.class);
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
		TypedQuery<Server> q = em.createNamedQuery("getServerById", Server.class);
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
		TypedQuery<Server> q = em.createNamedQuery("getServerByAddress", Server.class);
		q.setParameter("address", address);
		List<Server> list = q.getResultList();
		return list.size() > 0 ? list.get(0) : null;
	}

	/**
	 * This method is necessary to automatically assign user to the server with minimum load.
	 * 
	 * First of all we are trying to find servers referenced by 0 users.
	 * If all servers are referenced by at least 1 user we are searching the first server referenced by minimum users.
	 * 
	 * @return Server object referenced by the minimum user accounts. 
	 */
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
				result = get((Long)((Object[])r.get(0))[0]);
			}
		} else {
			result = l.get(0);
		}
		return result;
	}
	
	/**
	 * @deprecated user standard mechanism of
	 *             {@link IDataProviderDao#update(org.apache.openmeetings.persistence.beans.OmEntity, long)}
	 * @param id
	 * @param name
	 * @param address
	 * @return
	 */
	@Deprecated
	public Server saveServer(long id, String name, String address, String comment, long userId) {
		Server s = get(id);
		if (s == null) {
			s = new Server();
			s.setInserted(new Date());
			s.setInsertedby(usersDao.get(userId));
		} else {
			s.setUpdated(new Date());
			s.setUpdatedby(usersDao.get(userId));
		}
		s.setName(name);
		s.setAddress(address);
		s.setComment(comment);

		return em.merge(s);
	}

	/**
	 * @deprecated use standard mechanism of
	 *             {@link IDataProviderDao#delete(org.apache.openmeetings.persistence.beans.OmEntity, long)}
	 * @param id
	 * @return
	 */
	@Deprecated
	public boolean delete(long id) {
		Server s = get(id);
		if (s == null) {
			return false;
		}
		s.setDeleted(true);
		s = em.merge(s);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.data.OmDAO#update(org.apache.openmeetings.persistence
	 * .beans.OmEntity, long)
	 */
	public Server update(Server entity, long userId) {
		try {
			if (entity.getId() <= 0) {
				entity.setInserted(new Date());
				entity.setInsertedby(usersDao.get(userId));
				entity.setDeleted(false);
				em.persist(entity);
			} else {
				entity.setUpdated(new Date());
				entity.setUpdatedby(usersDao.get(userId));
				entity.setDeleted(false);
				em.merge(entity);
			}
		} catch (PersistenceException ex) {
			log.error("[update LdapConfig]", ex);
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
	public void delete(Server entity, long userId) {
		if (entity.getId() >= 0) {
			entity.setUpdated(new Date());
			entity.setUpdatedby(usersDao.get(userId));
			entity.setDeleted(true);
			em.merge(entity);
		}
	}

	/**
	 * Update the last ping of any server.
	 * 
	 * @param name
	 * @param userId
	 * @return
	 */
	public long updateLastPing(String name, long userId) {

		TypedQuery<Server> q = em.createNamedQuery("getServerByName",
				Server.class);
		q.setParameter("name", name);
		List<Server> l = q.getResultList();
		if (l.size() == 0) {
			log.debug("No server found with name: " + name);
			return -55L;
		} else if (l.size() == 1) {
			Server server = l.get(0);
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			server.setLastPing(cal);
			update(server, userId);
		} else {
			log.debug("Multiple server found with name: " + name);
			return -56L;
		}

		return -1L;
	}

}
