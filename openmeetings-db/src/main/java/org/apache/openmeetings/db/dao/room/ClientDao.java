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
package org.apache.openmeetings.db.dao.room;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.room.StreamClient;
import org.apache.openmeetings.db.entity.server.Server;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence of client objects to database is only available if so configured!
 *
 * @author sebawagner
 *
 */
@Transactional
public class ClientDao {

	@PersistenceContext
	private EntityManager em;

	private static List<Long> EMPTY_LIST = new ArrayList<>(0);

	public void cleanAllClients() {
		em.createNamedQuery("deleteAll").executeUpdate();
	}

	public void cleanClientsByServer(Server server) {
		em.createNamedQuery("deleteClientsByServer").
			setParameter("server", server).
			executeUpdate();
	}

	public StreamClient add(StreamClient entity) {
		em.persist(entity);
		return entity;
	}

	public StreamClient update(StreamClient entity) {
		em.merge(entity);
		return entity;
	}

	public void delete(StreamClient entity) {
		Query q = em.createNamedQuery("deletedById");
		q.setParameter("id", entity.getId());
		q.executeUpdate();
	}

	public void removeClientByServerAndStreamId(Server server, String streamId) {
		Query q = em.createNamedQuery("deletedByServerAndStreamId");
		q.setParameter("server", server);
		q.setParameter("streamid", streamId);
		q.executeUpdate();
	}

	public int countClients() {
		return em.createNamedQuery("countClients", Long.class).getSingleResult().intValue();
	}

	public int countClientsByServer(Server server) {
		TypedQuery<Long> q = em.createNamedQuery("countClientsByServer", Long.class);
		q.setParameter("server", server);
		return q.getSingleResult().intValue();
	}

	public long countClientsByServerAndStreamId(Server server, String streamId) {
		TypedQuery<Long> q = em.createNamedQuery("countClientsByServerAndStreamId", Long.class);
		q.setParameter("streamid", streamId);
		q.setParameter("server", server);
		return q.getSingleResult();
	}

	/**
	 * Query.getSingleResult would throw an error if result is null,
	 * see: http://stackoverflow.com/questions/2002993/jpa-getsingleresult-or-null
	 *
	 * @param server
	 * @param streamId
	 * @return
	 */
	public StreamClient getClientByServerAndStreamId(Server server, String streamId) {
		TypedQuery<StreamClient> q = em.createNamedQuery("getClientByServerAndStreamId", StreamClient.class);
		q.setParameter("streamid", streamId);
		q.setParameter("server", server);
		List<StreamClient> ll = q.getResultList();
		if (ll.size() == 1) {
			return ll.get(0);
		} else if (ll.size() == 0) {
			return null;
		}
		throw new RuntimeException("more then one client was found streamId "+ streamId + " server "+server);
	}

	public List<StreamClient> getClientsByPublicSIDAndServer(Server server, String publicSID) {
		TypedQuery<StreamClient> q = em.createNamedQuery("getClientsByPublicSIDAndServer", StreamClient.class);
		q.setParameter("server", server);
		q.setParameter("publicSID", publicSID);
		return q.getResultList();
	}

	public List<StreamClient> getClientsByPublicSID(String publicSID) {
		TypedQuery<StreamClient> q = em.createNamedQuery("getClientsByPublicSID", StreamClient.class);
		q.setParameter("publicSID", publicSID);
		return q.getResultList();
	}

	public List<StreamClient> getClientsByServer(Server server) {
		TypedQuery<StreamClient> q = em.createNamedQuery("getClientsByServer", StreamClient.class);
		q.setParameter("server", server);
		return q.getResultList();
	}

	public List<StreamClient> getClients() {
		return em.createNamedQuery("getClients", StreamClient.class).getResultList();
	}

	public List<StreamClient> getClientsWithServer() {
		return em.createNamedQuery("getClientsWithServer", StreamClient.class).getResultList();
	}

	public List<StreamClient> getClientsByUserId(Server server, Long userId) {
		TypedQuery<StreamClient> q = em.createNamedQuery("getClientsByUserId", StreamClient.class);
		q.setParameter("server", server);
		q.setParameter("userId", userId);
		return q.getResultList();
	}

	public List<StreamClient> getClientsByRoomId(Long roomId) {
		TypedQuery<StreamClient> q = em.createNamedQuery("getClientsByRoomId", StreamClient.class);
		q.setParameter("roomId", roomId);
		return q.getResultList();
	}

	/**
	 * returns a list of servers or an empty list in case no roomIds are found
	 *
	 * @param server
	 * @return
	 */
	public List<Long> getRoomsIdsByServer(Server server) {
		Query q = em.createNamedQuery("getRoomsIdsByServer");
		q.setParameter("server", server);
		@SuppressWarnings("unchecked")
		List<Long> resultList = q.getResultList();
		//if the result list contains only a value null, it means it
		//was empty and no roomid's have been found
		if (resultList.size() == 1 && resultList.get(0) == null) {
			return EMPTY_LIST;
		}
		return resultList;
	}

}
