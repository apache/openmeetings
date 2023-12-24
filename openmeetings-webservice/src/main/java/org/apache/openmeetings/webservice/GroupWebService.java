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

import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

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
import org.apache.openmeetings.webservice.schema.GroupDTOListWrapper;
import org.apache.openmeetings.webservice.schema.ServiceResultWrapper;
import org.apache.openmeetings.webservice.schema.UserSearchResultWrapper;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "GroupService")
@Path("/group")
public class GroupWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(GroupWebService.class);

	@Inject
	private GroupDao groupDao;
	@Inject
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
	@Operation(
			description = "add a new group",
			responses = {
					@ApiResponse(responseCode = "200", description = "ServiceResult with result type, and id of the group added",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult add(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "The name of the group") @QueryParam("name") @WebParam(name="name") String name
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
	@Operation(
			description = "Get the list of all groups",
			responses = {
					@ApiResponse(responseCode = "200", description = "list of users",
							content = @Content(schema = @Schema(implementation = GroupDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<GroupDTO> get(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			) throws ServiceException {
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
	@Operation(
			description = "Add USER to a certain group",
			responses = {
					@ApiResponse(responseCode = "200", description = "ServiceResult with result type, and id of the USER added, or error id in case of the error as text",
						content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult addUser(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the USER id") @PathParam("id") @WebParam(name="id") Long id
			, @Parameter(required = true, description = "the group id") @PathParam("userid") @WebParam(name="userid") Long userid
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
	 * @return {@link ServiceResult} with result type, and id of the USER removed, or error id in case of the error as text
	 * @throws {@link ServiceException} in case of any errors
	 */
	@DELETE
	@Path("/{id}/users/{userid}")
	@Operation(
			description = "Remove USER from a certain group",
			responses = {
					@ApiResponse(responseCode = "200", description = "ServiceResult with result type, and id of the USER removed, or error id in case of the error as text",
						content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult removeUser(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the USER id") @PathParam("id") @WebParam(name="id") Long id
			, @Parameter(required = true, description = "the group id") @PathParam("userid") @WebParam(name="userid") Long userid
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
	@Operation(
			description = "Adds a room to an group",
			responses = {
					@ApiResponse(responseCode = "200", description = "ServiceResult with result type",
						content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult addRoom(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "Id of group that the room is being paired with") @PathParam("id") @WebParam(name="id") Long id
			, @Parameter(required = true, description = "Id of room to be added") @PathParam("roomid") @WebParam(name="roomid") Long roomid
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
	@Operation(
			description = "Search users and return them",
			responses = {
					@ApiResponse(responseCode = "200", description = "users found",
						content = @Content(schema = @Schema(implementation = UserSearchResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public UserSearchResult getUsers(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the group id") @PathParam("id") @WebParam(name="id") long id
			, @Parameter(required = true, description = "first record") @QueryParam("start") @WebParam(name="start") int start
			, @Parameter(required = true, description = "max records") @QueryParam("max") @WebParam(name="max") int max
			, @Parameter(required = true, description = "orderby clause") @QueryParam("orderby") @WebParam(name="orderby") String orderby
			, @Parameter(required = true, description = "asc or desc") @QueryParam("asc") @WebParam(name="asc") boolean asc
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			SearchResult<User> result = new SearchResult<>();
			result.setObjectName(User.class.getName());
			result.setRecords(groupUserDao.count(id));
			result.setResult(new ArrayList<>());
			String order = isAlphanumeric(orderby) ? orderby : "id";
			for (GroupUser ou : groupUserDao.get(id, null, start, max, order == null ? null : new SortParam<>(order, asc))) {
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
	@Operation(
			description = "Deletes a group",
			responses = {
					@ApiResponse(responseCode = "200", description = "ServiceResult with result type",
						content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult delete(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the id of the group") @WebParam(name="id") @PathParam("id") long id
			) throws ServiceException
	{
		return performCall(sid, User.Right.ADMIN, sd -> {
			groupDao.delete(groupDao.get(id), sd.getUserId());

			return new ServiceResult("Deleted", Type.SUCCESS);
		});
	}
}
