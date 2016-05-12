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
import static org.apache.openmeetings.webservice.Constants.TNS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.room.IInvitationManager;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
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
 * 
 */
@WebService(serviceName="org.apache.openmeetings.webservice.RoomWebService", targetNamespace = TNS)
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
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

	/**
	 * Returns an Object of Type RoomsList which contains a list of
	 * Room-Objects. Every Room-Object contains a Roomtype and all informations
	 * about that Room. The List of current-users in the room is Null if you get
	 * them via SOAP. The Roomtype can be 1 for conference rooms or 2 for
	 * audience rooms.
	 * 
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param type
	 * @return - list of public rooms
	 * @throws ServiceException
	 */
	@WebMethod
	@GET
	@Path("/public/{type}")
	public List<RoomDTO> getPublic(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("type") @WebParam(name="type") String type) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);

			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				return RoomDTO.list(roomDao.getPublicRooms(Room.Type.valueOf(type)));
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param id - the room id
	 * @return - room with the id given
	 */
	@WebMethod
	@GET
	@Path("/{id}")
	public RoomDTO getRoomById(@QueryParam("sid") @WebParam(name="sid") String sid, @PathParam("id") @WebParam(name="id") Long id) throws ServiceException {
		Long userId = sessionDao.checkSession(sid);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
			return new RoomDTO(roomDao.get(id));
		} else {
			throw new ServiceException("Insufficient permissions"); //TODO code -26
		}
	}

	/**
	 * Checks if a room with this exteralRoomId + externalRoomType does exist,
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
	 * @throws ServiceException
	 */
	@WebMethod
	@GET
	@Path("/{type}/{externaltype}/{externaliId}")
	public RoomDTO getExternal(@WebParam(name="sid") @QueryParam("sid") String sid
			, @PathParam("type") @WebParam(name="type") String type
			, @PathParam("externaltype") @WebParam(name="externaltype") String externalType
			, @PathParam("externalid") @WebParam(name="externalid") Long externalId
			, @WebParam(name="room") @QueryParam("room") RoomDTO room) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room r = roomDao.getExternal(Room.Type.valueOf(type), externalType, externalId);
				if (r == null) {
					r = room.get();
					r = roomDao.update(r, userId);
					return new RoomDTO(r);
				} else {
					return new RoomDTO(r);
				}
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	@WebMethod
	@POST
	@Path("/")
	public RoomDTO add(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="room") @FormParam("room") RoomDTO room) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room r = room.get();
				r = roomDao.update(r, userId);
				return new RoomDTO(r);
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			log.error("add", e);
			throw new ServiceException(e.getMessage());
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
	@WebMethod
	@PUT
	@Path("/{id}")
	public RoomDTO update(@WebParam(name="sid") @QueryParam("sid") String sid, @PathParam("id") @WebParam(name="id") Long id, @WebParam(name="room") @QueryParam("room") RoomDTO room) throws ServiceException {
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
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param id - The id of the room
	 * 
	 * @return - id of the room deleted
	 */
	@WebMethod
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) throws ServiceException {
		Long userId = sessionDao.checkSession(sid);
		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
			Room r = roomDao.get(id);
			if (r != null) {
				roomDao.delete(r, userId);
				return new ServiceResult(id, "Deleted", Type.SUCCESS);
			} else {
				return new ServiceResult(0, "Not found", Type.SUCCESS);
			}
		} else {
			throw new ServiceException("Insufficient permissions"); //TODO code -26
		}
	}

	/**
	 * Method to remotely close rooms. If a room is closed all users
	 * inside the room and all users that try to enter it will be redirected to
	 * the redirectURL that is defined in the Room-Object.
	 * 
	 * Returns positive value if authentication was successful.
	 * 
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param id
	 *            the room id
	 *            
	 * @return - 1 in case of success, -2 otherwise
	 * @throws ServiceException
	 */
	@WebMethod
	@GET
	@Path("/close/{id}")
	public ServiceResult close(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			log.debug("close " + id);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room room = roomDao.get(id);
				room.setClosed(true);

				roomDao.update(room, userId);

				Map<String, String> message = new HashMap<String, String>();
				message.put("message", "roomClosed");
				scopeApplicationAdapter.sendMessageByRoomAndDomain(id, message);
				
				return new ServiceResult(1L, "Closed", Type.SUCCESS);
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param id
	 *            the room id
	 *            
	 * @return - 1 in case of success, -2 otherwise
	 * @throws ServiceException
	 */
	@WebMethod
	@GET
	@Path("/open/{id}")
	public ServiceResult open(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			log.debug("open " + id);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Room room = roomDao.get(id);
				room.setClosed(false);
				roomDao.update(room, userId);
				
				return new ServiceResult(1L, "Opened", Type.SUCCESS);
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @param sid
	 *            The SID of the User. This SID must be marked as Loggedin
	 *            _Admin
	 * @param id
	 *            the room id
	 *            
	 * @return - true if user was kicked, false otherwise
	 */
	@WebMethod
	@GET
	@Path("/kick/{id}")
	public ServiceResult kick(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				boolean result = userManager.kickUserByStreamId(sid, id);
				return new ServiceResult(result ? 1L : 0L, "Kicked", Type.SUCCESS);
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
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
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param ids - id of the room you need counters for
	 * @return - current users for rooms ids
	 * @throws ServiceException
	 */
	@WebMethod
	@GET
	@Path("/counters")
	public List<RoomCountBean> counters(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @QueryParam("id") List<Long> ids) throws ServiceException {
		List<RoomCountBean> roomBeans = new ArrayList<RoomCountBean>();
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				List<Room> rooms = roomDao.get(ids);

				for (Room room : rooms) {
					RoomCountBean rCountBean = new RoomCountBean();
					rCountBean.setRoomId(room.getId());
					rCountBean.setRoomName(room.getName());
					rCountBean.setMaxUser(room.getNumberOfPartizipants());
					rCountBean.setRoomCount(sessionManager.getClientListByRoom(room.getId()).size());

					roomBeans.add(rCountBean);
				}
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[counters]", err);
			throw new ServiceException(err.getMessage());
		}
		return roomBeans;
	}

	/**
	 * Method to get invitation hash with given parameters
	 * 
	 * @param sid - The SID of the User. This SID must be marked as Loggedin
	 * @param invite - parameters of the invitation
	 * @param sendmail - flag to determine if email should be sent or not
	 * @return - serviceResult object with the result
	 * @throws ServiceException - in case of any exception
	 */
	@WebMethod
	@POST
	@Path("/hash")
	private ServiceResult hash(@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="invite") @QueryParam("invite") InvitationDTO invite
			, @WebParam(name="sendmail") @QueryParam("sendmail") boolean sendmail
			) throws ServiceException
	{
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Invitation i = invite.get(userId, userDao, roomDao);
				i = invitationDao.update(i);
				
				if (i != null) {
					if (sendmail) {
						invitationManager.sendInvitationLink(i, MessageType.Create, invite.getSubject(), invite.getMessage(), false);
					}
					return new ServiceResult(1L, i.getHash(), Type.SUCCESS);
				} else {
					return new ServiceResult(0L, "Sys - Error", Type.ERROR);
				}
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
			}
		} catch (ServiceException err) {
			throw err;
		} catch (Exception err) {
			log.error("[hash] ", err);
			throw new ServiceException(err.getMessage());
		}
	}
}
