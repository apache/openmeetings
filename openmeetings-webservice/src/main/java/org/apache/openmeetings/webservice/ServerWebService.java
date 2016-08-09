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
import static org.apache.openmeetings.webservice.Constants.TNS;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.server.ServerDTO;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class provides method implementations necessary for OM to manage servers
 * participating in cluster.
 * 
 * @author solomax, sebawagner
 * 
 */
@WebService(serviceName="org.apache.openmeetings.webservice.ServerWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/server")
public class ServerWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(ServerWebService.class, webAppRootKey);

	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ServerDao serverDao;

	/**
	 * Method to retrieve the list of the servers participating in cluster
	 * 
	 * @param sid
	 *            - session id to identify the user making request
	 * @param start
	 *            - server index to start with
	 * @param max
	 *            - Maximum server count
	 * @return The list of servers participating in cluster
	 */
	@WebMethod
	@GET
	@Path("/{start}/{max}")
	public List<ServerDTO> getServers(@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("start") @WebParam(name="start") int start
			, @PathParam("max") @WebParam(name="max") int max
			) throws ServiceException
	{
		log.debug("getServers enter");
		Sessiondata sd = sessionDao.check(sid);

		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(sd.getUserId()))) {
			return ServerDTO.list(serverDao.get(start, max));
		} else {
			log.warn("Insuffisient permissions");
			throw new ServiceException("Insufficient permissions"); //TODO code -26
		}
	}

	/**
	 * Method to retrieve the total count of the servers participating in
	 * cluster
	 * 
	 * @param sid
	 *            - session id to identify the user making request
	 * @return total count of the servers participating in cluster
	 */
	@WebMethod
	@GET
	@Path("/count")
	public long count(@QueryParam("sid") @WebParam(name="sid") String sid) throws ServiceException {
		log.debug("getServerCount enter");
		Sessiondata sd = sessionDao.check(sid);

		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(sd.getUserId()))) {
			return serverDao.count();
		} else {
			throw new ServiceException("Insufficient permissions"); //TODO code -26
		}
	}

	/**
	 * Method to add/update server
	 * 
	 * @param sid
	 *            - session id to identify the user making request
	 * @param server
	 *            - server to add/update
	 * @return the id of saved server
	 */
	@WebMethod
	@POST
	@Path("/")
	public ServerDTO add(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="server") @QueryParam("server") ServerDTO server) throws ServiceException {
		log.debug("saveServerCount enter");
		Sessiondata sd = sessionDao.check(sid);
		Long userId = sd.getUserId();
		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
			Server s = server.get();
			return new ServerDTO(serverDao.update(s, userId));
		} else {
			log.warn("Insuffisient permissions");
			throw new ServiceException("Insufficient permissions"); //TODO code -26
		}
	}

	/**
	 * Method to delete server
	 * 
	 * @param sid
	 *            - session id to identify the user making request
	 * @param id
	 *            - the id of the server to delete
	 * @return true if the server was deleted, false otherwise
	 */
	@WebMethod
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) throws ServiceException {
		log.debug("saveServerCount enter");
		Sessiondata sd = sessionDao.check(sid);
		Long userId = sd.getUserId();

		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
			Server s = serverDao.get(id);
			if (s != null) {
				serverDao.delete(s, userId);
				return new ServiceResult(id, "Deleted", Type.SUCCESS);
			}
			return new ServiceResult(0L, "Not found", Type.SUCCESS);
		} else {
			log.warn("Insuffisient permissions");
			throw new ServiceException("Insufficient permissions"); //TODO code -26
		}
	}
}
