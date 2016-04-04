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
package org.apache.openmeetings.core.session.store;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.server.Server;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * Stores the session in the memory.
 * Is NOT designed to be clustered across multiple servers.
 * 
 * <ul>
 * <li>client by streamid</li>
 * <li>client by publicSID</li>
 * <li>client by userId</li>
 * <li>clients by roomId</li>
 * <li>roomIds by server</li>
 * </ul>
 * 
 * @author sebawagner
 * 
 */
public class HashMapStore implements IClientPersistenceStore {
	protected static final Logger log = Red5LoggerFactory.getLogger(HashMapStore.class, webAppRootKey);
	
	private Map<String, Client> clientsByStreamId = new ConcurrentHashMap<>();
	
	@Override
	public void clear() {
		clientsByStreamId = new ConcurrentHashMap<>();
	}
	
	@Override
	public void put(String streamId, Client rcl) {
		clientsByStreamId.put(rcl.getStreamid(), rcl);
	}

	@Override
	public boolean containsKey(Server server, String streamId) {
		return clientsByStreamId.containsKey(streamId);
	}

	@Override
	public Client get(Server server, String streamId) {
		return clientsByStreamId.get(streamId);
	}
	
	@Override
	public List<Client> getClientsByPublicSID(Server server, String publicSID) {
		List<Client> clientList = new ArrayList<>();
		for (Map.Entry<String, Client> e: clientsByStreamId.entrySet()) {
			Client cl = e.getValue();
			if (cl.getPublicSID().equals(publicSID)) {
				clientList.add(cl);
			}
		}
		return clientList;
	}
	
	@Override
	public Map<Long,List<Client>> getClientsByPublicSID(String publicSID) {
		Map<Long,List<Client>> clientMapList = new HashMap<>();
		List<Client> clientList = new ArrayList<>();
		for (Map.Entry<String, Client> e: clientsByStreamId.entrySet()) {
			Client cl = e.getValue();
			if (cl.getPublicSID().equals(publicSID)) {
				clientList.add(cl);
			}
		}
		clientMapList.put(null, clientList);
		return clientMapList;
	}
	
	@Override
	public Collection<Client> getClients() {
		return clientsByStreamId.values();
	}
	
	@Override
	public Collection<Client> getClientsWithServer() {
		//there is no server object to be loaded, memory cache means
		//there is no cluster enabled
		return getClients();
	}
	
	@Override
	public Collection<Client> getClientsByServer(Server server) {
		return clientsByStreamId.values();
	}
	
	@Override
	public List<Client> getClientsByUserId(Server server, Long userId) {
		List<Client> clientList = new ArrayList<>();
		for (Map.Entry<String, Client> e: clientsByStreamId.entrySet()) {
			Client cl = e.getValue();
			if (cl.getUserId().equals(userId)) {
				clientList.add(cl);
			}
		}
		return clientList;
	}
	
	@Override
	public  List<Client> getClientsByRoomId(Long roomId) {
		List<Client> clientList = new ArrayList<>();
		for (Map.Entry<String, Client> e: clientsByStreamId.entrySet()) {
			Client cl = e.getValue();
			if (cl.getRoomId() != null && cl.getRoomId().equals(roomId)) {
				clientList.add(cl);
			}
		}
		return clientList;
	}

	@Override
	public void remove(Server server, String streamId) {
		clientsByStreamId.remove(streamId);
	}

	@Override
	public int size() {
		return clientsByStreamId.size();
	}
	
	@Override
	public int sizeByServer(Server server) {
		return clientsByStreamId.size();
	}

	@Override
	public Collection<Client> values() {
		return clientsByStreamId.values();
	}
	
	public int getTotalNumberOfSessions() {
		return clientsByStreamId.size();
	}
	
	/**
	 * Print some session statistics to the debug out
	 * 
	 * @param detailLevel
	 */
	public void printDebugInformation(List<DEBUG_DETAILS> detailLevel) {
		log.debug("Session Statistics Start ################## ");
		log.debug(getDebugInformation(detailLevel));
		log.debug("Session Statistics End ################## ");
	}

	@Override
	public String getDebugInformation(List<DEBUG_DETAILS> detailLevel) {
		StringBuilder statistics = new StringBuilder();

		if (detailLevel.contains(DEBUG_DETAILS.SIZE)) {
			addNewLine(statistics, "Number of sessions Total " + getTotalNumberOfSessions());
		}

		return statistics.toString();
	}

	private static void addNewLine(StringBuilder strBuilder, String message) {
		strBuilder.append(message + "\n\r");
	}

	@Override
	public List<Long> getRoomsIdsByServer(Server server) {
		Set<Long> rooms = new HashSet<>();
		for (Map.Entry<String, Client> e: clientsByStreamId.entrySet()) {
			Client cl = e.getValue();
			Long roomId = cl.getRoomId();
			if (roomId != null && roomId.longValue() > 0 && !rooms.contains(roomId)) {
				rooms.add(roomId);
			}
		}
		return new ArrayList<Long>(rooms);
	}

}
