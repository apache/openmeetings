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
package org.apache.openmeetings.data.conference.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.room.Client;
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
	
	private static List<Long> EMPTY_LIST = new ArrayList<Long>(0);
	
	public void cleanAllClients() {
		em.createNamedQuery("deleteAll").executeUpdate();
	}

	public void cleanClientsByServer(Server server) {
		em.createNamedQuery("deleteClientsByServer").
			setParameter("server", server).
			executeUpdate();
	}
	
	public Client add(Client entity) {
		em.persist(entity);
		return entity;
	}
	
	public Client update(Client entity) {
		em.merge(entity);
		return entity;
	}
	
	public void delete(Client entity) {
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
	public Client getClientByServerAndStreamId(Server server, String streamId) {
		TypedQuery<Client> q = em.createNamedQuery("getClientByServerAndStreamId", Client.class);
		q.setParameter("streamid", streamId);
		q.setParameter("server", server);
		List<Client> ll = q.getResultList();
		if (ll.size() == 1) {
			return ll.get(0);
		} else if (ll.size() == 0) {
			return null;
		}
		throw new RuntimeException("more then one client was found streamId "+ streamId + " server "+server);
	}

	public List<Client> getClientsByPublicSIDAndServer(Server server, String publicSID) {
		TypedQuery<Client> q = em.createNamedQuery("getClientsByPublicSIDAndServer", Client.class);
		q.setParameter("server", server);
		q.setParameter("publicSID", publicSID);
		return q.getResultList();
	}

	public List<Client> getClientsByPublicSID(String publicSID) {
		TypedQuery<Client> q = em.createNamedQuery("getClientsByPublicSID", Client.class);
		q.setParameter("publicSID", publicSID);
		return q.getResultList();
	}

	public List<Client> getClientsByServer(Server server) {
		TypedQuery<Client> q = em.createNamedQuery("getClientsByServer", Client.class);
		q.setParameter("server", server);	
		return q.getResultList();
	}

	public List<Client> getClients() {
		return em.createNamedQuery("getClients", Client.class).getResultList();
	}
	
	public List<Client> getClientsWithServer() {
		return em.createNamedQuery("getClientsWithServer", Client.class).getResultList();
	}

	public List<Client> getClientsByUserId(Server server, Long userId) {
		TypedQuery<Client> q = em.createNamedQuery("getClientsByUserId", Client.class);
		q.setParameter("server", server);
		q.setParameter("user_id", userId);	
		return q.getResultList();
	}

	public List<Client> getClientsByRoomId(Long roomId) {
		TypedQuery<Client> q = em.createNamedQuery("getClientsByRoomId", Client.class);
		q.setParameter("room_id", roomId);	
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
