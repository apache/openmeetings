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

import java.util.ArrayList;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomOrganisationDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.OrganisationUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.user.UserSearchResult;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomOrganisation;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.OrganisationUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * The Service contains methods to login and create hash to directly enter
 * conference rooms, recordings or the application in general
 * 
 * @author sebawagner
 * @webservice GroupService
 * 
 */
@WebService(name = "GroupService")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/group")
public class GroupWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(GroupWebService.class, webAppRootKey);

	@Autowired
	private OrganisationDao orgDao;
	@Autowired
	private OrganisationUserDao orgUserDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private RoomOrganisationDao roomOrgDao;

	/**
	 * add a new organisation
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param name
	 *            the name of the org
	 * @return the new id of the org or -1 in case an error happened
	 * @throws ServiceException
	 */
	@POST
	@Path("/")
	public ServiceResult add(@QueryParam("sid") @WebParam String sid, @QueryParam("name") @WebParam String name) throws ServiceException {
		Long userId = sessionDao.checkSession(sid);
		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
			Organisation o = new Organisation();
			o.setName(name);
			return new ServiceResult(orgDao.update(o, userId).getId(), "Success", Type.SUCCESS);
		} else {
			log.error("Could not create organization");
			return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
		}
	}
	
	/**
	 * 
	 * Add a user to a certain organization
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param userId
	 *            the user id
	 * @param id
	 *            the organization id
	 * @return - id of the user added, or error id in case of the error
	 */
	@POST
	@Path("/{id}/users/add/{userId}")
	public ServiceResult addUser(
			@QueryParam("sid") @WebParam String sid
			, @PathParam("id") @WebParam Long id
			, @PathParam("userId") @WebParam Long userId
			) throws ServiceException
	{
		try {
			Long authUserId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {
				if (!orgUserDao.isUserInOrganization(id, userId)) {
					User u = userDao.get(userId);
					u.getOrganisationUsers().add(new OrganisationUser(orgDao.get(id)));
					userDao.update(u, authUserId);
				}
				return new ServiceResult(userId, "Success", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("addUserToOrganisation", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Adds a room to an organization
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @param roomId - Id of room to be added
	 * @param organisationId - Id of organisation that the room is being paired with
	 * 
	 * @return Id of the relation created, null or -1 in case of the error
	 */
	@POST
	@Path("/{id}/rooms/add/{roomId}")
	public ServiceResult addRoom(
			@QueryParam("sid") @WebParam String sid
			, @PathParam("id") @WebParam Long id
			, @PathParam("roomId") @WebParam Long roomId
			) throws ServiceException
	{
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room r = roomDao.get(roomId);
				if (r != null) {
					if (r.getRoomOrganisations() == null) {
						r.setRoomOrganisations(new ArrayList<RoomOrganisation>());
					}
					boolean found = false;
					for (RoomOrganisation ro : r.getRoomOrganisations()) {
						if (ro.getOrganisation().getId().equals(id)) {
							found = true;
						}
					}
					if (!found) {
						r.getRoomOrganisations().add(new RoomOrganisation(orgDao.get(id), r));
						roomDao.update(r, userId);
						return new ServiceResult(1L, "Success", Type.SUCCESS);
					}
				}
				return new ServiceResult(0L, "Not added", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("[addRoomToOrg]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Search users and return them
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param organisationId
	 *            the organization id
	 * @param start
	 *            first record
	 * @param max
	 *            max records
	 * @param orderby
	 *            orderby clause
	 * @param asc
	 *            asc or desc
	 * @return - users found
	 */
	@GET
	@Path("/users/{id}")
	public UserSearchResult getUsers(
			@QueryParam("sid") @WebParam String sid
			, @PathParam("id") @WebParam long id
			, @QueryParam("start") @WebParam int start
			, @QueryParam("max") @WebParam int max
			, @QueryParam("orderby") @WebParam String orderby
			, @QueryParam("asc") @WebParam boolean asc
			) throws ServiceException
	{
		try {
			Long userId = sessionDao.checkSession(sid);
			SearchResult<User> result = new SearchResult<User>();
			result.setObjectName(User.class.getName());
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				result.setRecords(orgUserDao.count(id));
				result.setResult(new ArrayList<User>());
				for (OrganisationUser ou : orgUserDao.get(id, null, start, max, orderby + " " + (asc ? "ASC" : "DESC"))) {
					result.getResult().add(ou.getUser());
				}
			} else {
				log.error("Need Administration Account");
				result.setErrorId(-26L);
			}
			return new UserSearchResult(result);
		} catch (Exception err) {
			log.error("getUsersByOrganisation", err);
			throw new ServiceException(err.getMessage());
		}
	}
}
