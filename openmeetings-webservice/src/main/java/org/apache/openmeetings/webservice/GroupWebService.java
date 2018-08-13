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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
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
import org.apache.openmeetings.db.dto.user.GroupDTO;
import org.apache.openmeetings.db.dto.user.UserSearchResult;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * The Service contains methods to login and create hash to directly enter
 * conference rooms, recordings or the application in general
 *
 * @author sebawagner
 *
 */
@Service("groupWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.GroupWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/group")
public class GroupWebService extends BaseWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(GroupWebService.class, getWebAppRootKey());

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
	 * @return {@link ServiceResult} with result type, and id of the group added
	 */
	@POST
	@Path("/")
	public ServiceResult add(@QueryParam("sid") @WebParam(name="sid") String sid, @QueryParam("name") @WebParam(name="name") String name) {
		log.debug("add:: name {}", name);
		return performCall(sid, User.Right.Soap, sd -> {
			Group o = new Group();
			o.setName(name);
			return new ServiceResult(String.valueOf(getDao().update(o, sd.getUserId()).getId()), Type.SUCCESS);
		});
	}

	/**
	 * Get the list of all groups
	 *
	 * @param sid
	 *            The SID from getSession
	 * @return list of all groups
	 */
	@GET
	@Path("/")
	public List<GroupDTO> get(@QueryParam("sid") @WebParam(name="sid") String sid) {
		return performCall(sid, User.Right.Soap, sd -> GroupDTO.list(getDao().get(0, Integer.MAX_VALUE)));
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
	 * @return {@link ServiceResult} with result type, and id of the user added, or error id in case of the error as text
	 */
	@POST
	@Path("/{id}/users/{userid}")
	public ServiceResult addUser(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("userid") @WebParam(name="userid") Long userid
			)
	{
		return performCall(sid, User.Right.Soap, sd -> {
			if (!getBean(GroupUserDao.class).isUserInGroup(id, userid)) {
				UserDao userDao = getUserDao();
				User u = userDao.get(userid);
				u.getGroupUsers().add(new GroupUser(getDao().get(id), u));
				userDao.update(u, sd.getUserId());
			}
			return new ServiceResult(String.valueOf(userid), Type.SUCCESS);
		});
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
	 * @return {@link ServiceResult} with result type, and id of the user added, or error id in case of the error as text
	 */
	@DELETE
	@Path("/{id}/users/{userid}")
	public ServiceResult removeUser(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("userid") @WebParam(name="userid") Long userid
			)
	{
		return performCall(sid, User.Right.Soap, sd -> {
			if (getBean(GroupUserDao.class).isUserInGroup(id, userid)) {
				UserDao userDao = getUserDao();
				User u = userDao.get(userid);
				for (Iterator<GroupUser> iter = u.getGroupUsers().iterator(); iter.hasNext(); ) {
					GroupUser gu = iter.next();
					if (id.equals(gu.getGroup().getId())) {
						iter.remove();
					}
				}
				userDao.update(u, sd.getUserId());
			}
			return new ServiceResult(String.valueOf(userid), Type.SUCCESS);
		});
	}

	/**
	 * Adds a room to an group
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param id - Id of group that the room is being paired with
	 * @param roomid - Id of room to be added
	 *
	 * @return {@link ServiceResult} with result type
	 */
	@POST
	@Path("/{id}/rooms/add/{roomid}")
	public ServiceResult addRoom(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("roomid") @WebParam(name="roomid") Long roomid
			)
	{
		return performCall(sid, User.Right.Soap, sd -> {
			RoomDao roomDao = getRoomDao();
			Room r = roomDao.get(roomid);
			if (r != null) {
				if (r.getGroups() == null) {
					r.setGroups(new ArrayList<RoomGroup>());
				}
				boolean found = false;
				for (RoomGroup ro : r.getGroups()) {
					if (ro.getGroup().getId().equals(id)) {
						found = true;
					}
				}
				if (!found) {
					r.getGroups().add(new RoomGroup(getDao().get(id), r));
					roomDao.update(r, sd.getUserId());
					return new ServiceResult("Success", Type.SUCCESS);
				}
			}
			return new ServiceResult("Not added", Type.ERROR);
		});
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
			)
	{
		return performCall(sid, User.Right.Soap, sd -> {
			SearchResult<User> result = new SearchResult<>();
			result.setObjectName(User.class.getName());
			GroupUserDao dao = getBean(GroupUserDao.class);
			result.setRecords(dao.count(id));
			result.setResult(new ArrayList<User>());
			String order = isAlphanumeric(orderby) ? orderby : "id";
			for (GroupUser ou : dao.get(id, null, start, max, order + " " + (asc ? "ASC" : "DESC"))) {
				result.getResult().add(ou.getUser());
			}
			return new UserSearchResult(result);
		});
	}

	/**
	 * Deletes a group
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param id
	 *            the id of the group
	 * @return {@link ServiceResult} with result type
	 */
	@WebMethod
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) {
		return performCall(sid, User.Right.Admin, sd -> {
			GroupDao dao = getDao();
			dao.delete(dao.get(id), sd.getUserId());

			return new ServiceResult("Deleted", Type.SUCCESS);
		});
	}
}
