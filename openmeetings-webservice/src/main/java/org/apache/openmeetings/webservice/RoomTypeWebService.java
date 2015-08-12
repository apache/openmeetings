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

import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.RoomType;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * The Service contains methods to work with room types
 * 
 * @author solomax
 * @webservice RoomTypeService
 * 
 */
@WebService(name = "RoomTypeService")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/roomtype")
public class RoomTypeWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomTypeWebService.class, webAppRootKey);

	@Autowired
	private UserDao userDao;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private RoomTypeDao roomTypeDao;

	/**
	 * List of available room types
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @return - List of available room types
	 * @throws ServiceException
	 */
	public List<RoomType> getRoomTypes(String SID) throws ServiceException {
		log.debug("getRoomTypes");
		Long userId = sessionDao.checkSession(SID);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
			return roomTypeDao.get();
		}
		return null;
	}

}
