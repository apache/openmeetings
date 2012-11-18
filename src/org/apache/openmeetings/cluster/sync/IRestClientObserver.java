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
package org.apache.openmeetings.cluster.sync;

import java.util.List;

import org.apache.openmeetings.conference.room.SlaveClientDto;
import org.apache.openmeetings.persistence.beans.basic.Server;

/**
 * Defines the events the {@link RestClient} will broadcast when he performs calls
 * to the {@link Server}
 * 
 * @author sebawagner
 * 
 */
public interface IRestClientObserver {

	/**
	 * performed by the RestClient, whenever a ping is completed and the
	 * response is parsed to a session object ready to be injected in the local
	 * session store
	 * 
	 * @param server
	 * @param slaveClients
	 */
	public void pingComplete(Server server, List<SlaveClientDto> slaveClients);

}
