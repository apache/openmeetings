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

import static org.apache.commons.lang3.StringUtils.isAlphanumeric;
import static org.apache.openmeetings.db.dto.basic.ServiceResult.NO_PERMISSION;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.webservice.Constants.TNS;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.user.UserSearchResult;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 *
 * The Service contains methods to login and create hash to directly enter
 * conference rooms, recordings or the application in general
 *
 * @author sebawagner
 *
 */
@WebService(serviceName="org.apache.openmeetings.webservice.GroupWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/group")
public class GroupWebService extends BaseWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(GroupWebService.class, webAppRootKey);

	private static GroupDao getDao() {
		return getBean(GroupDao.class);
	}

	/**
	 * add a new group
	 *
	 * @param sid
	 *            The SID from getSession
	 * @param name
	 *            the name of the group
	 * @return the new id of the group or -1 in case an error happened
	 * @throws ServiceException
	 */
	@POST
	@Path("/")
	public ServiceResult add(@QueryParam("sid") @WebParam(name="sid") String sid, @QueryParam("name") @WebParam(name="name") String name) throws ServiceException {
		Sessiondata sd = check(sid);
		Long userId = sd.getUserId();
		if (AuthLevelUtil.hasWebServiceLevel(getRights(userId))) {
			Group o = new Group();
			o.setName(name);
			return new ServiceResult("" + getDao().update(o, userId).getId(), Type.SUCCESS);
		} else {
			log.error("Could not create group");
			return NO_PERMISSION;
		}
	}

	/**
	 * Get the list of all groups
	 *
	 * @param sid
	 *            The SID from getSession
	 * @return list of all groups
	 * @throws ServiceException
	 */
	@GET
	@Path("/")
	public List<Group> get(@QueryParam("sid") @WebParam(name="sid") String sid) throws ServiceException {
		if (AuthLevelUtil.hasWebServiceLevel(getRights(sid))) {
			return getDao().get(0, Integer.MAX_VALUE);
		} else {
			log.error("Insufficient permissions");
			throw ServiceException.NO_PERMISSION;
		}
	}

	/**
	 *
	 * Add user to a certain group
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
	@Path("/{id}/users/{userid}")
	public ServiceResult addUser(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("userid") @WebParam(name="userid") Long userid
			) throws ServiceException
	{
		try {
			Sessiondata sd = check(sid);
			Long authUserId = sd.getUserId();
			if (AuthLevelUtil.hasWebServiceLevel(getRights(authUserId))) {
				if (!getBean(GroupUserDao.class).isUserInGroup(id, userid)) {
					UserDao userDao = getUserDao();
					User u = userDao.get(userid);
					u.getGroupUsers().add(new GroupUser(getDao().get(id), u));
					userDao.update(u, authUserId);
				}
				return new ServiceResult("" + userid, Type.SUCCESS);
			} else {
				return NO_PERMISSION;
			}
		} catch (Exception err) {
			log.error("addUser", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 *
	 * Remove user from a certain group
	 *
	 * @param sid
	 *            The SID from getSession
	 * @param userid
	 *            the user id
	 * @param id
	 *            the group id
	 * @return - id of the user added, or error id in case of the error
	 */
	@DELETE
	@Path("/{id}/users/{userid}")
	public ServiceResult removeUser(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("userid") @WebParam(name="userid") Long userid
			) throws ServiceException
	{
		try {
			Sessiondata sd = check(sid);
			Long authUserId = sd.getUserId();
			if (AuthLevelUtil.hasWebServiceLevel(getRights(authUserId))) {
				if (getBean(GroupUserDao.class).isUserInGroup(id, userid)) {
					UserDao userDao = getUserDao();
					User u = userDao.get(userid);
					for (Iterator<GroupUser> iter = u.getGroupUsers().iterator(); iter.hasNext(); ) {
						GroupUser gu = iter.next();
						if (id.equals(gu.getGroup().getId())) {
							iter.remove();
						}
					}
					userDao.update(u, authUserId);
				}
				return new ServiceResult("" + userid, Type.SUCCESS);
			} else {
				return NO_PERMISSION;
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
			Sessiondata sd = check(sid);
			Long userId = sd.getUserId();
			if (AuthLevelUtil.hasWebServiceLevel(getRights(userId))) {
				RoomDao roomDao = getRoomDao();
				Room r = roomDao.get(roomid);
				if (r != null) {
					if (r.getRoomGroups() == null) {
						r.setRoomGroups(new ArrayList<RoomGroup>());
					}
					boolean found = false;
					for (RoomGroup ro : r.getRoomGroups()) {
						if (ro.getGroup().getId().equals(id)) {
							found = true;
						}
					}
					if (!found) {
						r.getRoomGroups().add(new RoomGroup(getDao().get(id), r));
						roomDao.update(r, userId);
						return new ServiceResult("Success", Type.SUCCESS);
					}
				}
				return new ServiceResult("Not added", Type.ERROR);
			} else {
				return NO_PERMISSION;
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
			SearchResult<User> result = new SearchResult<>();
			result.setObjectName(User.class.getName());
			if (AuthLevelUtil.hasWebServiceLevel(getRights(sid))) {
				GroupUserDao dao = getBean(GroupUserDao.class);
				result.setRecords(dao.count(id));
				result.setResult(new ArrayList<User>());
				String order = isAlphanumeric(orderby) ? orderby : "id";
				for (GroupUser ou : dao.get(id, null, start, max, order + " " + (asc ? "ASC" : "DESC"))) {
					result.getResult().add(ou.getUser());
				}
			} else {
				log.error("Need Administration Account");
				result.setErrorKey(NO_PERMISSION.getMessage());
			}
			return new UserSearchResult(result);
		} catch (Exception err) {
			log.error("getUsers", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Deletes a group
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param id
	 *            the id of the group
	 *
	 * @throws {@link ServiceException} in case of any error
	 */
	@WebMethod
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) throws ServiceException {
		try {
			Sessiondata sd = check(sid);
			Long authUserId = sd.getUserId();

			if (AuthLevelUtil.hasAdminLevel(getRights(authUserId))) {
				GroupDao dao = getDao();
				dao.delete(dao.get(id), authUserId);

				return new ServiceResult("Deleted", Type.SUCCESS);
			} else {
				return NO_PERMISSION;
			}
		} catch (Exception err) {
			log.error("deleteUserById", err);
			throw new ServiceException(err.getMessage());
		}
	}
}
