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
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.OrganisationUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.user.UserSearchResult;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
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
@WebService(serviceName="org.apache.openmeetings.webservice.GroupWebService")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/group")
public class GroupWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(GroupWebService.class, webAppRootKey);

	@Autowired
	private OrganisationDao groupDao;
	@Autowired
	private OrganisationUserDao groupUserDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private SessiondataDao sessionDao;

	/**
	 * add a new group
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
	public ServiceResult add(@QueryParam("sid") @WebParam(name="sid") String sid, @QueryParam("name") @WebParam(name="name") String name) throws ServiceException {
		Long userId = sessionDao.checkSession(sid);
		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
			Organisation o = new Organisation();
			o.setName(name);
			return new ServiceResult(groupDao.update(o, userId).getId(), "Success", Type.SUCCESS);
		} else {
			log.error("Could not create group");
			return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
		}
	}
	
	/**
	 * 
	 * Add a user to a certain group
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param userid
	 *            the user id
	 * @param id
	 *            the group id
	 * @return - id of the user added, or error id in case of the error
	 */
	@POST
	@Path("/{id}/users/add/{userid}")
	public ServiceResult addUser(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("userid") @WebParam(name="userid") Long userid
			) throws ServiceException
	{
		try {
			Long authUserId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {
				if (!groupUserDao.isUserInOrganization(id, userid)) {
					User u = userDao.get(userid);
					u.getOrganisation_users().add(new Organisation_Users(groupDao.get(id)));
					userDao.update(u, authUserId);
				}
				return new ServiceResult(userid, "Success", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("addUser", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Adds a room to an group
	 * 
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param id - Id of group that the room is being paired with
	 * @param roomid - Id of room to be added
	 * 
	 * @return Id of the relation created, null or -1 in case of the error
	 */
	@POST
	@Path("/{id}/rooms/add/{roomId}")
	public ServiceResult addRoom(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("roomid") @WebParam(name="roomid") Long roomid
			) throws ServiceException
	{
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room r = roomDao.get(roomid);
				if (r != null) {
					if (r.getRoomGroups() == null) {
						r.setRoomGroups(new ArrayList<RoomGroup>());
					}
					boolean found = false;
					for (RoomGroup ro : r.getRoomGroups()) {
						if (ro.getOrganisation().getId().equals(id)) {
							found = true;
						}
					}
					if (!found) {
						r.getRoomGroups().add(new RoomGroup(groupDao.get(id), r));
						roomDao.update(r, userId);
						return new ServiceResult(1L, "Success", Type.SUCCESS);
					}
				}
				return new ServiceResult(0L, "Not added", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("[addRoom]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Search users and return them
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param id
	 *            the group id
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
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") long id
			, @QueryParam("start") @WebParam(name="start") int start
			, @QueryParam("max") @WebParam(name="max") int max
			, @QueryParam("orderby") @WebParam(name="orderby") String orderby
			, @QueryParam("asc") @WebParam(name="asc") boolean asc
			) throws ServiceException
	{
		try {
			Long userId = sessionDao.checkSession(sid);
			SearchResult<User> result = new SearchResult<User>();
			result.setObjectName(User.class.getName());
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				result.setRecords(groupUserDao.count(id));
				result.setResult(new ArrayList<User>());
				for (Organisation_Users ou : groupUserDao.get(id, null, start, max, orderby + " " + (asc ? "ASC" : "DESC"))) {
					result.getResult().add(ou.getUser());
				}
			} else {
				log.error("Need Administration Account");
				result.setErrorId(-26L);
			}
			return new UserSearchResult(result);
		} catch (Exception err) {
			log.error("getUsers", err);
			throw new ServiceException(err.getMessage());
		}
	}
}
