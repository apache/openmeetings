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
package org.apache.openmeetings.conference.room;

import java.util.List;

import org.apache.openmeetings.persistence.beans.basic.Server;

public interface ISharedSessionStore {

	/**
	 * takes a list of clients from another slave of the cluster and sync its
	 * sessions to the masters store, to have correct list of current users
	 * online and user load at the master available that shows the current load
	 * on all slaves of the cluster.
	 * 
	 * There is zero notification when there is session/user removed or added,
	 * those events are handled by the slave, the master just needs the list to
	 * be synced.
	 * 
	 * @param server
	 * @param clients
	 */
	public void syncSlaveClientSession(Server server,
			List<SlaveClientDto> clients);

	/**
	 * Get the current sessions
	 * 
	 * @return
	 */
	public List<SlaveClientDto> getCurrentSlaveSessions();

	/**
	 * We want to make sure that if a server is deleted, also all its sessions
	 * are removed from the memory in the session store
	 * 
	 * @param server
	 */
	public void cleanSessionsOfDeletedOrDeactivatedServer(Server server);

}
