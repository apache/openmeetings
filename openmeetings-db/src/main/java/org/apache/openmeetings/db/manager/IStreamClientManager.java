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
package org.apache.openmeetings.db.manager;

import java.util.List;
import java.util.Set;

import org.apache.openmeetings.db.entity.basic.IClient;
import org.apache.openmeetings.db.entity.room.StreamClient;

/**
 * Methods to add/get/remove {@link StreamClient}s to the session
 *
 *
 * @author sebawagner
 *
 */
public interface IStreamClientManager {
	StreamClient add(StreamClient c);

	/**
	 * Get all ClientList Objects of that room and domain This Function is
	 * needed cause it is invoked internally AFTER the current user has been
	 * already removed from the ClientList to see if the Room is empty again and
	 * the PollList can be removed
	 *
	 * @param roomId - id of the room
	 * @return - list of all clients in the room
	 */
	List<StreamClient> list(Long roomId);

	/**
	 * Get a client by its UID
	 *
	 * @param uid - uid of the client to get
	 * @return - client with giver uid
	 */
	StreamClient get(String uid);

	/**
	 * Updates {@link StreamClient} in the cache
	 *
	 * @param rcm - client to update
	 */
	IClient update(IClient rcm);

	StreamClient update(StreamClient rcl, boolean forceSize);

	/**
	 * Remove a client from the session store
	 *
	 * @param uid - uid of the client to remove
	 * @return true if client was removed
	 */
	boolean remove(String uid);

	/**
	 * returns number of users performing recording
	 *
	 * @param roomId - id of the room
	 * @return - number of recording clients (MUST be 0 or 1)
	 */
	long getRecordingCount(Long roomId);

	/**
	 * returns a number of current users publishing screensharing
	 *
	 * @param roomId - id of the room
	 * @return - number of publishing clients (MUST be 0 or 1)
	 */
	long getPublishingCount(Long roomId);

	/**
	 * returns a number of users performing screen-sharing
	 *
	 * @param roomId - id of the room
	 * @return - number of sharing clients (MUST be 0 or 1)
	 */
	long getSharingCount(Long roomId);

	/**
	 * returns number of users sending A/V streams to server
	 *
	 * @param roomId - id of the room
	 * @return - number of broadcasting clients
	 */
	long getBroadcastingCount(Long roomId);

	/**
	 * Get a list of rooms with users on particular cluster node.
	 *
	 * @param serverId - id of the server
	 * @return a set, a roomId can be only one time in this list
	 */
	Set<Long> getActiveRoomIds(String serverId);
}
