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
package org.apache.openmeetings.quartz.scheduler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.cluster.sync.IRestClientObserver;
import org.apache.openmeetings.cluster.sync.RestClient;
import org.apache.openmeetings.conference.room.ISharedSessionStore;
import org.apache.openmeetings.conference.room.SlaveClientDto;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.documents.beans.UploadCompleteMessage;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ClusterSlaveJob implements IRestClientObserver {

	private static Logger log = Red5LoggerFactory.getLogger(
			ClusterSlaveJob.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ServerDao serverDao;
	@Autowired
	private ISharedSessionStore clientListManager;


	/**
	 * We store the list of RestClients in the memory, so that we can simply
	 * call ping to get the load, without the need to get a new session Hash and
	 * to login again. <br/>
	 * There can be only one RestClient per server, so we use the primary key of
	 * the server to store the RestClient.
	 */
	private static Map<Long, RestClient> restClientsSessionStore = new HashMap<Long, RestClient>();

	/**
	 * Synchronized, cause nobody should manipulate the object while another
	 * process requests it, the scheduler could run several times and request
	 * the same object, add or remove it.<br/>
	 * If there is no object yet, create one.
	 * 
	 * @param server
	 */
	private synchronized RestClient getRestClient(Server server) {
		RestClient restClient = restClientsSessionStore.get(server.getId());

		// check if any values of the server have been changed,
		// if yes, we need a new RestClient to make sure it will re-login to the
		// changed server details
		if (restClient != null && restClient.hasServerDetailsChanged(server)) {
			log.debug("Server details changed, get new rest client");
			restClient = null;
		}

		if (restClient == null) {
			restClient = new RestClient(this, server);
			restClientsSessionStore.put(server.getId(), restClient);
		}
		return restClient;
	}

	public void doIt() {
		try {

			for (Server server : serverDao.getActiveServers()) {

				RestClient rClient = getRestClient(server);
				
				//If the ping is still running, we don't ping the client in this session
				if (rClient.getPingRunning()) {
					log.warn("The ping for the server " + server
							+ " takes longer then the ping interval!");
					continue;
				}
				
				log.debug("ClusterSlaveJob. Ping server: " + server);

				rClient.ping();

			}

		} catch (Exception e) {
			log.error("Unexpected exception while doRoundTrip cluster.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.cluster.sync.IRestClientObserverEvent#pingComplete
	 * (org.apache.openmeetings.persistence.beans.basic.Server, java.util.List)
	 */
	public void pingComplete(Server server, List<SlaveClientDto> slaveClients) {

		log.debug("-- pingComplete -- For server: " + server+ " Size: "+slaveClients.size());

		clientListManager.syncSlaveClientSession(server, slaveClients);

		server.setLastPing(Calendar.getInstance());
		serverDao.update(server, -1L);

	}
	
	/**
	 * Gets the current {@link RestClient} from the session store and then
	 * performs a kickUser on that. It is not possible that there is no
	 * {@link RestClient}, because if you want to kick a user from a slave, the
	 * master <i>must</i> already have loaded the sessions from the slave, so
	 * there logically <i>must</i> by a {@link RestClient} available that has an 
	 * open connection to that slave / {@link Server}
	 * 
	 * @param serverId
	 * @param publicSID
	 */
	public void kickSlaveUser(Server server, String publicSID) throws Exception {
		
		RestClient rClient = getRestClient(server);
		
		if (rClient == null) {
			throw new Exception("No RestClient found for server " + server);
		}
		
		rClient.kickUser(publicSID);
		
	}
	
	/**
	 * Gets the current {@link RestClient} from the session store and then
	 * performs a kickUser on that. It is not possible that there is no
	 * {@link RestClient}, because if you want to kick a user from a slave, the
	 * master <i>must</i> already have loaded the sessions from the slave, so
	 * there logically <i>must</i> by a {@link RestClient} available that has an 
	 * open connection to that slave / {@link Server}
	 * 
	 * @param server
	 * @param publicSID
	 * @param uploadCompleteMessage
	 * @throws Exception
	 */
	public void syncMessageToClientOnSlave(Server server, String publicSID, UploadCompleteMessage uploadCompleteMessage) throws Exception {
		RestClient rClient = getRestClient(server);
		
		if (rClient == null) {
			throw new Exception("No RestClient found for server " + server);
		}
		
		rClient.syncMessage(publicSID, uploadCompleteMessage);
	}

}
