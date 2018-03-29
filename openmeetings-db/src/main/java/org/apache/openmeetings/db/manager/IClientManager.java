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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.IClient;

public interface IClientManager {
	Client get(String uid);
	Client getBySid(String sid);
	String uidBySid(String sid);
	List<Client> listByRoom(Long roomId);
	Collection<Client> listByUser(Long userId);
	Client update(Client c);
	void exit(IClient c);


	/**
	 * Get a list of all rooms with users in the system.
	 *
	 * @return a set, a roomId can be only one time in this list
	 */
	Set<Long> getActiveRoomIds();
}
