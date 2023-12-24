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

import static org.apache.openmeetings.webservice.Constants.TNS;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.room.InvitationDTO;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomFile;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.db.mapper.RoomMapper;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.service.room.InvitationManager;
import org.apache.openmeetings.webservice.error.InternalServiceException;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.openmeetings.webservice.schema.RoomDTOListWrapper;
import org.apache.openmeetings.webservice.schema.RoomDTOWrapper;
import org.apache.openmeetings.webservice.schema.ServiceResultWrapper;
import org.apache.openmeetings.webservice.schema.UserDTOListWrapper;
import org.apache.wicket.util.string.Strings;
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
 * RoomService contains methods to manipulate rooms and create invitation hash
 *
 * @author sebawagner
 *
 */
@Service("roomWebService")
@WebService(serviceName="org.apache.openmeetings.webservice.RoomWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Tag(name = "RoomService")
@Path("/room")
public class RoomWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(RoomWebService.class);

	@Inject
	private IUserManager userManager;
	@Inject
	private IClientManager clientManager;
	@Inject
	private InvitationDao inviteDao;
	@Inject
	private InvitationManager inviteManager;
	@Inject
	private RoomMapper rMapper;

	/**
	 * Returns an Object of Type RoomsList which contains a list of
	 * ROOM-Objects. Every ROOM-Object contains a Roomtype and all informations
	 * about that ROOM. The List of current-users in the room is Null if you get
	 * them via SOAP. The Roomtype can be 'conference', 'presentation' or 'interview'.
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param type
	 *            Type of public rooms need to be retrieved
	 * @return - list of public rooms
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/public/{type}")
	@Operation(
		description = """
				Returns an Object of Type RoomsList which contains a list of
				 ROOM-Objects. Every ROOM-Object contains a Roomtype and all informations
				  about that ROOM. The List of current-users in the room is Null if you get
				   them via SOAP. The Roomtype can be 'conference', 'presentation' or 'interview'.""",
		responses = {
				@ApiResponse(responseCode = "200", description = "list of public rooms",
						content = @Content(schema = @Schema(implementation = RoomDTOListWrapper.class))),
				@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
		}
	)
	public List<RoomDTO> getPublic(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "Type of public rooms need to be retrieved") @PathParam("type") @WebParam(name="type") String type
			) throws ServiceException
	{
		Room.Type t = Strings.isEmpty(type) ? null : Room.Type.valueOf(type);
		return performCall(sid, User.Right.ROOM, sd -> RoomDTO.list(roomDao.getPublicRooms(t)));
	}

	/**
	 * returns a conference room object
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param id - the room id
	 * @return - room with the id given
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/{id}")
	@Operation(
		description = "Returns a conference room object",
		responses = {
				@ApiResponse(responseCode = "200", description = "room with the id given",
						content = @Content(schema = @Schema(implementation = RoomDTOWrapper.class))),
				@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
		}
	)
	public RoomDTO getRoomById(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @QueryParam("sid") @WebParam(name="sid") String sid
			, @Parameter(required = true, description = "the room id") @PathParam("id") @WebParam(name="id") Long id
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> new RoomDTO(roomDao.get(id)));
	}

	/*
	 * This method is required to set additional fields on room sub-objects
	 * for ex: RoomFile.roomId
	 */
	private Room updateRtoRoom(Room r, Long userId) {
		if (r.getFiles() == null) {
			r.setFiles(new ArrayList<>());
		}
		if (r.getId() == null) {
			List<RoomFile> files = r.getFiles();
			r.setFiles(null);
			r = roomDao.update(r, userId);
			r.setFiles(files);
		}
		for (RoomFile rf : r.getFiles()) {
			rf.setRoomId(r.getId());
		}
		return roomDao.update(r, userId);
	}
	/**
	 * Checks if a room with this exteralId + externalType does exist,
	 * if yes it returns the room id if not, it will create the room and then
	 * return the room id of the newly created room
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param type
	 *            type of the room
	 * @param externalType
	 *            you can specify your system-name or type of room here, for
	 *            example "moodle"
	 * @param externalId
	 *            your external room id may set here
	 * @param room
	 *            details of the room to be created if not found
	 *
	 * @return - id of the room or error code
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/{type}/{externaltype}/{externalid}")
	@Operation(
		description = """
				Checks if a room with this exteralId + externalType does exist,
				 if yes it returns the room id if not, it will create the room and then
				 return the room id of the newly created room""",
		responses = {
				@ApiResponse(responseCode = "200", description = "id of the room or error code",
						content = @Content(schema = @Schema(implementation = RoomDTOWrapper.class))),
				@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
		}
	)
	public RoomDTO getExternal(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "type of the room") @PathParam("type") @WebParam(name="type") String type
			, @Parameter(required = true, description = "you can specify your system-name or type of room here, for example \"moodle\"") @PathParam("externaltype") @WebParam(name="externaltype") String externalType
			, @Parameter(required = true, description = "your external room id may set here") @PathParam("externalid") @WebParam(name="externalid") String externalId
			, @Parameter(required = true, description = "details of the room to be created if not found") @WebParam(name="room") @QueryParam("room") RoomDTO room
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			Room r = roomDao.getExternal(Room.Type.valueOf(type), externalType, externalId);
			if (r == null) {
				if (room == null) {
					return null;
				} else {
					room.setExternalType(externalType);
					room.setExternalId(externalId);
					r = rMapper.get(room);
					r = updateRtoRoom(r, sd.getUserId());
					return new RoomDTO(r);
				}
			} else {
				return new RoomDTO(r);
			}
		});
	}

	/**
	 * Adds a new ROOM like through the Frontend
	 *
	 * @param sid
	 *            The SID from getSession
	 * @param room
	 *            room object
	 *
	 * @return - Room object or throw error
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Path("/")
	@Operation(
			description = "Adds a new ROOM like through the Frontend",
			responses = {
					@ApiResponse(responseCode = "200", description = "Room object or throw error",
							content = @Content(schema = @Schema(implementation = RoomDTOWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public RoomDTO add(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "room object") @WebParam(name="room") @FormParam("room") RoomDTO room
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			Room r = rMapper.get(room);
			r = updateRtoRoom(r, sd.getUserId());
			return new RoomDTO(r);
		});
	}

	/**
	 * Delete a room by its room id
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param id - The id of the room
	 *
	 * @return - id of the room deleted
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@DELETE
	@Path("/{id}")
	@Operation(
			description = "Delete a room by its room id",
			responses = {
					@ApiResponse(responseCode = "200", description = "id of the room deleted",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult delete(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "The id of the room") @WebParam(name="id") @PathParam("id") long id
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			Room r = roomDao.get(id);
			if (r == null) {
				return new ServiceResult("Not found", Type.SUCCESS);
			} else {
				roomDao.delete(r, sd.getUserId());
				return new ServiceResult("Deleted", Type.SUCCESS);
			}
		});
	}

	/**
	 * Method to remotely close rooms. If a room is closed all users
	 * inside the room and all users that try to enter it will be redirected to
	 * the redirectURL that is defined in the ROOM-Object.
	 *
	 * Returns positive value if authentication was successful.
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param id
	 *            the room id
	 *
	 * @return - 1 in case of success, -2 otherwise
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/close/{id}")
	@Operation(
			description = """
					Method to remotely close rooms. If a room is closed all users
					 inside the room and all users that try to enter it will be redirected to
					  the redirectURL that is defined in the ROOM-Object.""",
			responses = {
					@ApiResponse(responseCode = "200", description = "1 in case of success, -2 otherwise",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult close(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the room id") @WebParam(name="id") @PathParam("id") long id
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			Long userId = sd.getUserId();
			Room room = roomDao.get(id);
			room.setClosed(true);

			roomDao.update(room, userId);

			WebSocketHelper.sendRoom(new RoomMessage(room.getId(), userDao.get(userId),  RoomMessage.Type.ROOM_CLOSED));

			return new ServiceResult("Closed", Type.SUCCESS);
		});
	}

	/**
	 * Method to remotely open rooms. If a room is closed all users
	 * inside the room and all users that try to enter it will be redirected to
	 * the redirectURL that is defined in the ROOM-Object.
	 *
	 * Returns positive value if authentication was successful.
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param id
	 *            the room id
	 *
	 * @return - 1 in case of success, -2 otherwise
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/open/{id}")
	@Operation(
			description = """
					Method to remotely open rooms. If a room is closed all users
					 inside the room and all users that try to enter it will be redirected to
					  the redirectURL that is defined in the ROOM-Object.""",
			responses = {
					@ApiResponse(responseCode = "200", description = "1 in case of success, -2 otherwise",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult open(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the room id") @WebParam(name="id") @PathParam("id") long id
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			Room room = roomDao.get(id);
			room.setClosed(false);
			roomDao.update(room, sd.getUserId());

			return new ServiceResult("Opened", Type.SUCCESS);
		});
	}

	/**
	 * kick all uses of a certain room
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin with SOAP privileges
	 * @param id
	 *            the room id
	 *
	 * @return - true if USER was kicked, false otherwise
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/kick/{id}")
	@Operation(
			description = "Kick all uses of a certain room",
			responses = {
					@ApiResponse(responseCode = "200", description = "true if USER was kicked, false otherwise",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult kickAll(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the room id") @WebParam(name="id") @PathParam("id") long id
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			boolean result = userManager.kickUsersByRoomId(id);
			return new ServiceResult(result ? "Kicked" : "Not kicked", Type.SUCCESS);
		});
	}

	/**
	 * kick external USER from given room
	 *
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin with SOAP privileges
	 * @param id
	 *            the room id
	 * @param externalType
	 *            external type of USER to kick
	 * @param externalId
	 *            external id of USER to kick
	 *
	 * @return - 'Kicked' if USER was 'Not kicked' otherwise
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/kick/{id}/{externalType}/{externalId}")
	@Operation(
			description = "kick external USER from given room",
			responses = {
					@ApiResponse(responseCode = "200", description = "'Kicked' if USER was 'Not kicked' otherwise",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult kick(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the room id") @WebParam(name="id") @PathParam("id") long id
			, @Parameter(required = true, description = "external type of USER to kick") @WebParam(name="externalType") @PathParam("externalType") String externalType
			, @Parameter(required = true, description = "external id of USER to kick") @WebParam(name="externalId") @PathParam("externalId") String externalId
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			boolean result = userManager.kickExternal(id, externalType, externalId);
			return new ServiceResult(result ? "Kicked" : "Not kicked", Type.SUCCESS);
		});
	}

	/**
	 * Returns the count of users currently in the ROOM with given id
	 *
	 * @param sid The SID from UserService.getSession
	 * @param roomId id of the room to get users
	 * @return number of users as int
	 */
	@WebMethod
	@GET
	@Path("/count/{roomid}")
	@Operation(
			description = "Returns the count of users currently in the ROOM with given id",
			responses = {
					@ApiResponse(responseCode = "200", description = "number of users as int",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult count(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "roomId id of the room to get users") @WebParam(name="roomid") @PathParam("roomid") Long roomId
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> new ServiceResult(String.valueOf(clientManager.streamByRoom(roomId).count()), Type.SUCCESS));
	}

	/**
	 * Returns list of users currently in the ROOM with given id
	 *
	 * @param sid The SID from UserService.getSession
	 * @param roomId id of the room to get users
	 * @return {@link List} of users in the room
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/users/{roomid}")
	@Operation(
			description = "Returns list of users currently in the ROOM with given id",
			responses = {
					@ApiResponse(responseCode = "200", description = "List of users in the room",
							content = @Content(schema = @Schema(implementation = UserDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<UserDTO> users(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "roomId id of the room to get users") @WebParam(name="roomid") @PathParam("roomid") Long roomId
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP
				, sd -> clientManager.streamByRoom(roomId)
					.map(c -> new UserDTO(c.getUser()))
					.toList()
					);
	}

	/**
	 * Method to get invitation hash with given parameters
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param invite - parameters of the invitation
	 * @param sendmail - flag to determine if email should be sent or not
	 * @return - serviceResult object with the result
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Path("/hash")
	@Operation(
			description = "Method to get invitation hash with given parameters",
			responses = {
					@ApiResponse(responseCode = "200", description = "serviceResult object with the result",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult hash(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "parameters of the invitation") @WebParam(name="invite") @QueryParam("invite") InvitationDTO invite
			, @Parameter(required = true, description = "flag to determine if email should be sent or not") @WebParam(name="sendmail") @QueryParam("sendmail") boolean sendmail
			) throws ServiceException
	{
		log.debug("[hash] invite {}", invite);
		return performCall(sid, User.Right.SOAP, sd -> {
			Invitation i = rMapper.get(invite, sd.getUserId());
			i = inviteDao.update(i);

			if (i != null) {
				if (sendmail) {
					try {
						inviteManager.sendInvitationLink(i, MessageType.CREATE, invite.getSubject(), invite.getMessage(), false, null);
					} catch (Exception e) {
						throw new InternalServiceException(e.getMessage());
					}
				}
				return new ServiceResult(i.getHash(), Type.SUCCESS);
			} else {
				return new ServiceResult("error.unknown", Type.ERROR);
			}
		});
	}
}
