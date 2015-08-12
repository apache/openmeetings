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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.room.IInvitationManager;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.room.InvitationDTO;
import org.apache.openmeetings.db.dto.room.RoomCountBean;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RoomService contains methods to manipulate rooms and create invitation hash
 * 
 * @author sebawagner
 * @webservice RoomService
 * 
 */
@WebService
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/room")
public class RoomWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomWebService.class, webAppRootKey);

	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private IUserManager userManager;
	@Autowired
	private UserDao userDao;
	@Autowired
	private InvitationDao invitationDao;
	@Autowired
	private IInvitationManager invitationManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RoomTypeDao roomTypeDao;

	/**
	 * Returns an Object of Type RoomsList which contains a list of
	 * Room-Objects. Every Room-Object contains a Roomtype and all informations
	 * about that Room. The List of current-users in the room is Null if you get
	 * them via SOAP. The Roomtype can be 1 for conference rooms or 2 for
	 * audience rooms.
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomtypesId
	 * @return - list of public rooms
	 * @throws ServiceException
	 */
	@GET
	@Path("/public/{typeid}")
	public List<RoomDTO> getPublic(@QueryParam("sid") @WebParam String sid, @PathParam("typeid") @WebParam Long typeid) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);

			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				return RoomDTO.list(roomDao.getPublicRooms(typeid));
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[getPublic] ", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * returns a conference room object
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @param roomId - the room id
	 * @return - room with the id given
	 */
	@GET
	@Path("/{id}")
	public RoomDTO getRoomById(@QueryParam("sid") @WebParam String sid, @PathParam("id") @WebParam Long id) throws ServiceException {
		Long userId = sessionDao.checkSession(sid);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
			return new RoomDTO(roomDao.get(id));
		} else {
			throw new ServiceException("Insufficient permissins"); //TODO code -26
		}
	}

	/**
	 * Checks if a room with this exteralRoomId + externalRoomType does exist,
	 * if yes it returns the room id if not, it will create the room and then
	 * return the room id of the newly created room
	 * 
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param typeId
	 *            Id of the the room type
	 * @param externalRoomType
	 *            you can specify your system-name or type of room here, for
	 *            example "moodle"
	 * @param externalRoomId
	 *            your external room id may set here
	 * @param room
	 *            details of the room to be created if not found
	 *            
	 * @return - id of the room or error code
	 * @throws ServiceException
	 */
	@GET
	@Path("/{typeId}/{externalType}/{externalId}")
	public RoomDTO getExternal(@WebParam @QueryParam("sid") String sid
			, @PathParam("typeId") @WebParam long typeId
			, @PathParam("externalType") @WebParam String externalType
			, @PathParam("externalId") @WebParam Long externalId
			, @WebParam @QueryParam("room") RoomDTO room) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room r = roomDao.getExternal(typeId, externalType, externalId);
				if (r == null) {
					r = room.get(roomTypeDao);
					r = roomDao.update(r, userId);
					return new RoomDTO(r);
				} else {
					return new RoomDTO(r);
				}
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[getExternal] ", err);
			throw new ServiceException(err.getMessage());
		}
	}
	
	/**
	 * Adds a new Room like through the Frontend
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param room
	 *            room object
	 *            
	 * @return - id of the user added or error code
	 * @throws ServiceException
	 */
	@POST
	@Path("/")
	public RoomDTO add(@WebParam @QueryParam("sid") String sid, @WebParam @QueryParam("room") RoomDTO room) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room r = room.get(roomTypeDao);
				r = roomDao.update(r, userId);
				return new RoomDTO(r);
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("add", err);
			throw new ServiceException(err.getMessage());
		}
	}
	
	/**//*
	 * Adds a new Room like through the Frontend
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param room
	 *            room object
	 *            
	 * @return - id of the user added or error code
	 * @throws ServiceException
	 *//*
	@PUT
	@Path("/{id}")
	public RoomDTO update(@WebParam @QueryParam("sid") String sid, @PathParam("id") @WebParam Long id, @WebParam @QueryParam("room") RoomDTO room) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				if (roomDao.get(id) == null) {
					throw new ServiceException("Room not found");
				}
				Room r = room.get(roomTypeDao);
				r.setId(id);
				r = roomDao.update(r, userId);
				return new RoomDTO(r);
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("update", err);
			throw new ServiceException(err.getMessage());
		}
	}*/
	
	/**
	 * Delete a room by its room id
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 * 
	 * @return - id of the room deleted
	 */
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@WebParam @QueryParam("sid") String sid, @WebParam @PathParam("id") long id) throws ServiceException {
		Long userId = sessionDao.checkSession(sid);
		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
			roomDao.delete(roomDao.get(id), userId);

			return new ServiceResult(id, "Deleted", Type.SUCCESS);
		} else {
			throw new ServiceException("Insufficient permissins"); //TODO code -26
		}
	}

	/**
	 * Method to remotely close rooms. If a room is closed all users
	 * inside the room and all users that try to enter it will be redirected to
	 * the redirectURL that is defined in the Room-Object.
	 * 
	 * Returns positive value if authentication was successful.
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            the room id
	 *            
	 * @return - 1 in case of success, -2 otherwise
	 * @throws ServiceException
	 */
	@GET
	@Path("/close/{id}")
	public ServiceResult close(@WebParam @QueryParam("sid") String sid, @WebParam @PathParam("id") long id) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			log.debug("close " + id);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room room = roomDao.get(id);
				room.setIsClosed(true);

				roomDao.update(room, userId);

				Map<String, String> message = new HashMap<String, String>();
				message.put("message", "roomClosed");
				scopeApplicationAdapter.sendMessageByRoomAndDomain(id, message);
				
				return new ServiceResult(1L, "Closed", Type.SUCCESS);
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[close] ", err);
			throw new ServiceException(err.getMessage());
		}

	}

	/**
	 * Method to remotely open rooms. If a room is closed all users
	 * inside the room and all users that try to enter it will be redirected to
	 * the redirectURL that is defined in the Room-Object.
	 * 
	 * Returns positive value if authentication was successful.
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param roomId
	 *            the room id
	 *            
	 * @return - 1 in case of success, -2 otherwise
	 * @throws ServiceException
	 */
	@GET
	@Path("/open/{id}")
	public ServiceResult open(@WebParam @QueryParam("sid") String sid, @WebParam @PathParam("id") long id) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			log.debug("open " + id);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room room = roomDao.get(id);
				room.setIsClosed(false);
				roomDao.update(room, userId);
				
				return new ServiceResult(1L, "Opened", Type.SUCCESS);
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[open] ", err);
			throw new ServiceException(err.getMessage());
		}

	}

	/**
	 * kick all uses of a certain room
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 *            _Admin
	 * @param roomId
	 *            the room id
	 *            
	 * @return - true if user was kicked, false otherwise
	 */
	@GET
	@Path("/kick/{id}")
	public ServiceResult kick(@WebParam @QueryParam("sid") String sid, @WebParam @PathParam("id") long id) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Boolean result = userManager.kickUserByStreamId(sid, id);
				return new ServiceResult(Boolean.TRUE.equals(result) ? 1L : 0L, "Kicked", Type.SUCCESS);
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[kick]", err);
			throw new ServiceException(err.getMessage());
		}
	}
	
	/**
	 * Returns current users for rooms ids
	 * 
	 * @param SID - The SID of the User. This SID must be marked as Loggedin
	 * @param ids
	 * @return - current users for rooms ids
	 * @throws ServiceException
	 */
	@GET
	@Path("/counters")
	public List<RoomCountBean> counters(@WebParam @QueryParam("sid") String sid, @WebParam @QueryParam("roomId") List<Long> ids) throws ServiceException {
		List<RoomCountBean> roomBeans = new ArrayList<RoomCountBean>();
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				List<Room> rooms = roomDao.get(ids);

				for (Room room : rooms) {
					RoomCountBean rCountBean = new RoomCountBean();
					rCountBean.setRoomId(room.getId());
					rCountBean.setRoomName(room.getName());
					rCountBean.setMaxUser(room.getNumberOfPartizipants().intValue());
					rCountBean.setRoomCount(sessionManager.getClientListByRoom(room.getId()).size());

					roomBeans.add(rCountBean);
				}
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[getRoomCounters]", err);
			throw new ServiceException(err.getMessage());
		}
		return roomBeans;
	}

	@POST
	@Path("/hash")
	private ServiceResult hash(@WebParam @QueryParam("sid") String sid, @WebParam @QueryParam("invite") InvitationDTO invite, @WebParam @QueryParam("sendmail") boolean sendmail) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Invitation i = invite.get(userId, userDao, roomDao);
				i = invitationDao.update(i);
				
				if (i != null) {
					if (sendmail) {
						invitationManager.sendInvitionLink(i, MessageType.Create, invite.getSubject(), invite.getMessage(), false);
					}
					return new ServiceResult(1L, i.getHash(), Type.SUCCESS);
				} else {
					return new ServiceResult(0L, "Sys - Error", Type.ERROR);
				}
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[sendInvitationHash] ", err);
			throw new ServiceException(err.getMessage());
		}
	}
}
