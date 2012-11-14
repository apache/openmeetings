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
package org.apache.openmeetings.axis.services;

import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.conference.room.ISharedSessionStore;
import org.apache.openmeetings.conference.room.SlaveClientDto;
import org.apache.openmeetings.data.basic.AuthLevelmanagement;
import org.apache.openmeetings.data.basic.Sessionmanagement;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.data.user.Usermanagement;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class provides method implementations necessary for OM to manage servers
 * participating in cluster.
 * 
 * @author solomax, sebawagner
 * @webservice ServerService
 * 
 */
public class ServerWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ServerWebService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private AuthLevelmanagement authLevelManagement;
	@Autowired
	private ServerDao serversDao;
	@Autowired
	private ISharedSessionStore clientListManager;

	/**
	 * Method to retrieve the list of the servers participating in cluster
	 * 
	 * @param SID
	 *            - session id to identify the user making request
	 * @param start
	 *            - server index to start with
	 * @param max
	 *            - Maximum server count
	 * @return The list of servers participating in cluster
	 */
	public Server[] getServers(String SID, int start, int max) throws AxisFault {
		log.debug("getServers enter");
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);

		if (authLevelManagement.checkWebServiceLevel(user_level)) {
			return serversDao.get(start, max).toArray(new Server[0]);
		} else {
			log.warn("Insuffisient permissions");
			return null;
		}
	}

	/**
	 * Method to retrieve the total count of the servers participating in
	 * cluster
	 * 
	 * @param SID
	 *            - session id to identify the user making request
	 * @return total count of the servers participating in cluster
	 */
	public int getServerCount(String SID) throws AxisFault {
		log.debug("getServerCount enter");
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);

		if (authLevelManagement.checkWebServiceLevel(user_level)) {
			return (int) serversDao.count();
		} else {
			log.warn("Insuffisient permissions");
			return -1;
		}
	}

	/**
	 * Method to add/update server
	 * 
	 * @param SID
	 *            - session id to identify the user making request
	 * @param id
	 *            - the id of the server to save
	 * @param name
	 *            - the name of the server to save
	 * @param address
	 *            - the address(DNS name or IP) of the server to save
	 * @param port
	 *            - the http port of the slave
	 * @param user
	 *            - REST user to access the slave
	 * @param pass
	 *            - REST pass to access the slave
	 * @param webapp
	 *            - webapp name of the OpenMeetings instance
	 * @param protocol
	 *            - protocol to access the OpenMeetings instance
	 * @param comment
	 *            - comment for the server
	 * @return the id of saved server
	 */
	public long saveServer(String SID, long id, String name, String address,
			int port, String user, String pass, String webapp, String protocol,
			String comment) throws AxisFault {
		log.debug("saveServerCount enter");
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);

		if (authLevelManagement.checkWebServiceLevel(user_level)) {
			return serversDao.saveServer(id, name, address, port, user, pass,
					webapp, protocol, comment, users_id)
					.getId();
		} else {
			log.warn("Insuffisient permissions");
			return -1;
		}
	}

	/**
	 * Method to delete server
	 * 
	 * @param SID
	 *            - session id to identify the user making request
	 * @param id
	 *            - the id of the server to delete
	 * @return true if the server was deleted, false otherwise
	 */
	public boolean deleteServer(String SID, long id) throws AxisFault {
		log.debug("saveServerCount enter");
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);

		if (authLevelManagement.checkWebServiceLevel(user_level)) {
			return serversDao.delete(id);
		} else {
			log.warn("Insuffisient permissions");
			return false;
		}
	}

	/**
	 * Load a ping from the slave, see <a
	 * href="http://incubator.apache.org/openmeetings/ClusteringManual.html"
	 * target
	 * ="_BLANK">http://incubator.apache.org/openmeetings/ClusteringManual.
	 * html</a>
	 * 
	 * @param SID
	 *            - session id to identify the user making request, WebService
	 *            Level required
	 * @return List of
	 * @throws AxisFault
	 */
	public List<SlaveClientDto> ping(String SID)
			throws AxisFault {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);

		if (authLevelManagement.checkWebServiceLevel(user_level)) {
			
			//sync the sessions to the masters session store
			return clientListManager.getCurrentSlaveSessions();
			
		} else {
			log.warn("Insuffisient permissions");
			throw new AxisFault("Insuffisient permissions");
		}
	}

	// //sync the sessions to the masters session store
	// clientListManager.syncSlaveClientSession(server, clients);
	//
	// //Update last ping
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(new Date());
	// server.setLastPing(cal);
	// return serversDao.update(server, users_id).getId();

}
