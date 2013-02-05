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
package org.apache.openmeetings.session.store;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.room.Client;

public interface IClientPersistenceStore {

	public enum DEBUG_DETAILS {
		SIZE
	}
	
	/**
	 * called upon start of the session cache
	 */
	public abstract void clearCache();

	/**
	 * 
	 * @param streamId
	 * @param rcl
	 */
	public abstract void put(String streamId, Client rcl);
	
	/**
	 * 
	 * @param server
	 * @param streamId
	 * @return
	 */
	public boolean containsKey(Server server, String streamId);

	/**
	 * by server and publicSID
	 * 
	 * @param server
	 * @param streamId
	 * @return will return null if the client does not exist in the list
	 */
	public abstract Client get(Server server, String streamId);

	/**
	 * 
	 * @param server
	 * @param publicSID
	 * @return will return an empty list if nothing available
	 */
	public abstract List<Client> getClientsByPublicSID(Server server,
			String publicSID);

	/**
	 * Searches for the publicSID across all servers
	 * 
	 * @param publicSID
	 * @return will return a map with the serverId as key and the RoomClients as list in the value
	 */
	public abstract Map<Long, List<Client>> getClientsByPublicSID(
			String publicSID);

	public abstract Collection<Client> getClients();
	
	/**
	 * get all clients by a specific {@link Server}
	 * 
	 * @param server
	 * @return will return an empty map if nothing available
	 */
	public abstract Collection<Client> getClientsByServer(Server server);

	/**
	 * 
	 * @param server
	 * @param userId
	 * @return will return an empty list if nothing available
	 */
	public abstract Collection<Client> getClientsByUserId(Server server, Long userId);

	/**
	 * 
	 * We ignore the server here, cause ONE room can only be on ONE server and often we don't know where.
	 * 
	 * @param roomId
	 * @return will return an empty map if nothing available
	 */
	public abstract List<Client> getClientsByRoomId(Long roomId);

	public abstract void remove(Server server, String streamId);

	public abstract int size();

	public abstract int sizeByServer(Server server);

	public abstract Collection<Client> values();
	
	/**
	 * Get some session statistics
	 * 
	 * @param detailLevel
	 * @return
	 */
	public abstract String getDebugInformation(List<DEBUG_DETAILS> detailLevel);

	/**
	 * returns a list of roomIds (unique) that are currently active on the given server
	 * In case the session is stored in the memory (no-cluster setup) it will always 
	 * return simply all active roomIds
	 * 
	 * @param server
	 * @return
	 */
	public abstract List<Long> getRoomsIdsByServer(Server server);

	/**
	 * if database cache + cluster is enabled, the server object will be loaded 
	 * into the client
	 * 
	 * @return
	 */
	public abstract Collection<Client> getClientsWithServer();

}