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
import java.util.stream.Collectors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
import org.apache.openmeetings.db.manager.IWhiteboardManager;
import org.apache.openmeetings.db.mapper.RoomMapper;
import org.apache.openmeetings.db.util.ws.RoomMessage;
import org.apache.openmeetings.service.room.InvitationManager;
import org.apache.openmeetings.webservice.error.InternalServiceException;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Path("/room")
public class RoomWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(RoomWebService.class);

	@Autowired
	private IUserManager userManager;
	@Autowired
	private IClientManager clientManager;
	@Autowired
	private IWhiteboardManager wbManager;
	@Autowired
	private InvitationDao inviteDao;
	@Autowired
	private InvitationManager inviteManager;
	@Autowired
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
	public List<RoomDTO> getPublic(@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("type") @WebParam(name="type") String type
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
	public RoomDTO getRoomById(@QueryParam("sid") @WebParam(name="sid") String sid
			, @PathParam("id") @WebParam(name="id") Long id
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
	public RoomDTO getExternal(@WebParam(name="sid") @QueryParam("sid") String sid
			, @PathParam("type") @WebParam(name="type") String type
			, @PathParam("externaltype") @WebParam(name="externaltype") String externalType
			, @PathParam("externalid") @WebParam(name="externalid") String externalId
			, @WebParam(name="room") @QueryParam("room") RoomDTO room
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
	 * @return - id of the USER added or error code
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Path("/")
	public RoomDTO add(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="room") @FormParam("room") RoomDTO room
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
	public ServiceResult delete(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
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
	public ServiceResult close(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
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
	public ServiceResult open(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
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
	public ServiceResult kickAll(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
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
	 * @return - true if USER was kicked, false otherwise
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/kick/{id}/{externalType}/{externalId}")
	public ServiceResult kick(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
			, @WebParam(name="externalType") @PathParam("externalType") String externalType
			, @WebParam(name="externalId") @PathParam("externalId") String externalId
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
	public ServiceResult count(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="roomid") @PathParam("roomid") Long roomId
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
	public List<UserDTO> users(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="roomid") @PathParam("roomid") Long roomId
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			return clientManager.streamByRoom(roomId)
					.map(c -> new UserDTO(c.getUser()))
					.collect(Collectors.toList());
		});
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
	public ServiceResult hash(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="invite") @QueryParam("invite") InvitationDTO invite
			, @WebParam(name="sendmail") @QueryParam("sendmail") boolean sendmail
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

	/**
	 * @deprecated please use {@link WbWebService#resetWb(String, long)} method instead
	 *
	 * Method to clean room white board (all objects will be purged)
	 *
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param id - id of the room to clean
	 * @return - serviceResult object with the result
	 * @throws {@link ServiceException} in case of any errors
	 */
	@Deprecated(since = "5.0.0-M3")
	@WebMethod
	@GET
	@Path("/cleanwb/{id}")
	public ServiceResult cleanWb(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="id") @PathParam("id") long id
			) throws ServiceException
	{
		log.debug("[cleanwb] room id {}", id);
		return performCall(sid, User.Right.SOAP, sd -> {
			wbManager.reset(id, sd.getUserId());
			return new ServiceResult("", Type.SUCCESS);
		});
	}
}
