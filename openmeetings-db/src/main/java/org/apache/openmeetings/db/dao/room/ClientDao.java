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
		em.createNamedQuery("deleteClientAll").executeUpdate();
	}

	public void cleanClientsByServer(Server server) {
		em.createNamedQuery("deleteClientsByServer").
			setParameter("server", server).
			executeUpdate();
	}

	public StreamClient get(Long id) {
		List<StreamClient> list = em.createNamedQuery("getClientById", StreamClient.class)
				.setParameter("id", id)
				.getResultList();
		return list == null || list.isEmpty() ? null : list.get(0);
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
		remove(entity.getId());
	}

	public void remove(Long id) {
		Query q = em.createNamedQuery("deletedClientById");
		q.setParameter("id", id);
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

	public List<StreamClient> getClientsByUidAndServer(Server server, String uid) {
		TypedQuery<StreamClient> q = em.createNamedQuery("getClientsByUidAndServer", StreamClient.class);
		q.setParameter("server", server);
		q.setParameter("uid", uid);
		return q.getResultList();
	}

	public List<StreamClient> getClientsByUid(String uid) {
		TypedQuery<StreamClient> q = em.createNamedQuery("getClientsByUid", StreamClient.class);
		q.setParameter("uid", uid);
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
