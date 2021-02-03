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
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
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
import org.apache.openmeetings.webservice.error.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private static final Logger log = LoggerFactory.getLogger(GroupWebService.class);

	@Autowired
	private GroupDao groupDao;
	@Autowired
	private GroupUserDao groupUserDao;

	/**
	 * add a new group
	 *
	 * @param sid
	 *            The SID from getSession
	 * @param name
	 *            the name of the group
	 * @return {@link ServiceResult} with result type, and id of the group added
	 * @throws {@link ServiceException} in case of any errors
	 */
	@POST
	@Path("/")
	public ServiceResult add(@QueryParam("sid") @WebParam(name="sid") String sid
			, @QueryParam("name") @WebParam(name="name") String name
			) throws ServiceException
	{
		log.debug("add:: name {}", name);
		return performCall(sid, User.Right.SOAP, sd -> {
			Group o = new Group();
			o.setName(name);
			return new ServiceResult(String.valueOf(groupDao.update(o, sd.getUserId()).getId()), Type.SUCCESS);
		});
	}

	/**
	 * Get the list of all groups
	 *
	 * @param sid
	 *            The SID from getSession
	 * @return list of all groups
	 * @throws {@link ServiceException} in case of any errors
	 */
	@GET
	@Path("/")
	public List<GroupDTO> get(@QueryParam("sid") @WebParam(name="sid") String sid) throws ServiceException {
		return performCall(sid, User.Right.SOAP, sd -> GroupDTO.list(groupDao.get(0, Integer.MAX_VALUE)));
	}

	/**
	 *
	 * Add USER to a certain group
	 *
	 * @param sid
	 *            The SID from getSession
	 * @param userid
	 *            the USER id
	 * @param id
	 *            the group id
	 * @return {@link ServiceResult} with result type, and id of the USER added, or error id in case of the error as text
	 * @throws {@link ServiceException} in case of any errors
	 */
	@POST
	@Path("/{id}/users/{userid}")
	public ServiceResult addUser(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("userid") @WebParam(name="userid") Long userid
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			if (!groupUserDao.isUserInGroup(id, userid)) {
				User u = userDao.get(userid);
				u.addGroup(groupDao.get(id));
				userDao.update(u, sd.getUserId());
			}
			return new ServiceResult(String.valueOf(userid), Type.SUCCESS);
		});
	}

	/**
	 *
	 * Remove USER from a certain group
	 *
	 * @param sid
	 *            The SID from getSession
	 * @param userid
	 *            the USER id
	 * @param id
	 *            the group id
	 * @return {@link ServiceResult} with result type, and id of the USER added, or error id in case of the error as text
	 * @throws {@link ServiceException} in case of any errors
	 */
	@DELETE
	@Path("/{id}/users/{userid}")
	public ServiceResult removeUser(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("userid") @WebParam(name="userid") Long userid
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			if (groupUserDao.isUserInGroup(id, userid)) {
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@POST
	@Path("/{id}/rooms/add/{roomid}")
	public ServiceResult addRoom(
			@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
			, @PathParam("roomid") @WebParam(name="roomid") Long roomid
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			Room r = roomDao.get(roomid);
			if (r != null) {
				if (r.getGroups() == null) {
					r.setGroups(new ArrayList<>());
				}
				boolean found = false;
				for (RoomGroup ro : r.getGroups()) {
					if (ro.getGroup().getId().equals(id)) {
						found = true;
					}
				}
				if (!found) {
					r.addGroup(groupDao.get(id));
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
	 * @throws {@link ServiceException} in case of any errors
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
		return performCall(sid, User.Right.SOAP, sd -> {
			SearchResult<User> result = new SearchResult<>();
			result.setObjectName(User.class.getName());
			result.setRecords(groupUserDao.count(id));
			result.setResult(new ArrayList<>());
			String order = isAlphanumeric(orderby) ? orderby : "id";
			for (GroupUser ou : groupUserDao.get(id, null, start, max, order + " " + (asc ? "ASC" : "DESC"))) {
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
			) throws ServiceException
	{
		return performCall(sid, User.Right.ADMIN, sd -> {
			groupDao.delete(groupDao.get(id), sd.getUserId());

			return new ServiceResult("Deleted", Type.SUCCESS);
		});
	}
}
