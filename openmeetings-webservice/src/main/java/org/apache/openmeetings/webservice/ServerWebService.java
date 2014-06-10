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
package org.apache.openmeetings.webservice;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import javax.jws.WebService;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.dto.ServiceException;
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
@WebService
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/server")
public class ServerWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(ServerWebService.class, webAppRootKey);

	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ServerDao serversDao;

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
	public Server[] getServers(String SID, int start, int max) throws ServiceException {
		log.debug("getServers enter");
		Long users_id = sessiondataDao.checkSession(SID);

		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(users_id))) {
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
	public int getServerCount(String SID) throws ServiceException {
		log.debug("getServerCount enter");
		Long users_id = sessiondataDao.checkSession(SID);

		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(users_id))) {
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
	 * @param active
	 *            - if the server currently participates in the cluster or not
	 * @param comment
	 *            - comment for the server
	 * @return the id of saved server
	 */
	public long saveServer(String SID, long id, String name, String address,
			int port, String user, String pass, String webapp, String protocol,
			Boolean active, String comment) throws ServiceException {
		log.debug("saveServerCount enter");
		Long users_id = sessiondataDao.checkSession(SID);

		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(users_id))) {
			Server s = serversDao.get(id);
			if (s == null) {
				s = new Server();
			}
			s.setName(name);
			s.setAddress(address);
			s.setPort(port);
			s.setUser(user);
			s.setPass(pass);
			s.setWebapp(webapp);
			s.setProtocol(protocol);
			s.setActive(active);
			s.setComment(comment);
			return serversDao.update(s, users_id).getId();
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
	public boolean deleteServer(String SID, long id) throws ServiceException {
		log.debug("saveServerCount enter");
		Long users_id = sessiondataDao.checkSession(SID);

		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(users_id))) {
			Server s = serversDao.get(id);
			if (s != null) {
				serversDao.delete(s, users_id);
				return true;
			}
		} else {
			log.warn("Insuffisient permissions");
		}
		return false;
	}

}
