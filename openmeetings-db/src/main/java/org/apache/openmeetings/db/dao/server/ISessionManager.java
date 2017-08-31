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

import java.util.Collection;
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
public interface ISessionManager {
	StreamClient add(StreamClient c);

	/**
	 * loads the server into the client (only if database cache is used)
	 *
	 * @return
	 */
	Collection<StreamClient> list();

	/**
	 * Get a client by its UID
	 *
	 * @param uid
	 * @return
	 */
	StreamClient get(String uid);

	/**
	 * Updates {@link StreamClient} in the cache
	 *
	 * @param rcm
	 * @return updated client
	 */
	void update(IClient rcm);

	/**
	 * Remove a client from the session store
	 *
	 * @param uid
	 * @return true if client was removed
	 */
	boolean remove(String uid);

	/**
	 * Get all ClientList Objects of that room and domain This Function is
	 * needed cause it is invoked internally AFTER the current user has been
	 * already removed from the ClientList to see if the Room is empty again and
	 * the PollList can be removed
	 * @param roomId
	 * @return
	 */
	List<StreamClient> listByRoom(Long roomId);

	/**
	 * returns number of users performing recording
	 *
	 * @param roomId
	 * @return
	 */
	long getRecordingCount(Long roomId);

	/**
	 * returns a number of current users publishing screensharing
	 *
	 * @param roomId
	 * @return
	 */
	long getPublishingCount(Long roomId);

	/**
	 * returns a number of users performing screen-sharing
	 *
	 * @param roomId
	 * @return MUST return 0 or 1
	 */
	long getSharingCount(Long roomId);

	/**
	 * returns number of users sending A/V streams to server
	 *
	 * @param roomId
	 * @return
	 */
	long getBroadcastingCount(Long roomId);

	/**
	 * Get a list of all rooms with users in the system.
	 *
	 * @return a set, a roomId can be only one time in this list
	 */
	Set<Long> getActiveRoomIds();

	/**
	 * Get a list of rooms with users on particular cluster node.
	 *
	 * @param server
	 * @return a set, a roomId can be only one time in this list
	 */
	Set<Long> getActiveRoomIds(String serverId);
}
