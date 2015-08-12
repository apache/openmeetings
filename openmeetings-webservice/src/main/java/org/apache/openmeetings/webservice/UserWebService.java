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

import java.util.Date;

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
import org.apache.openmeetings.core.remote.ConferenceService;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
import org.apache.openmeetings.db.dto.room.RoomOptionsDTO;
import org.apache.openmeetings.db.dto.user.ExternalUserDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.entity.server.RemoteSessionObject;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.State;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.util.OmException;
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
 * @webservice UserService
 * 
 */
@WebService(name = "UserService")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
//@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/user")
public class UserWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(UserWebService.class, webAppRootKey);

	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private IUserManager userManagement;
	@Autowired
	private SOAPLoginDao soapLoginDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private ConferenceService conferenceService;

	/**
	 * @param login - login or email of Openmeetings user with admin or SOAP-rights
	 * @param pass - password
	 *            
	 * @return - {@link ServiceResult} with error code or SID and userId
	 */
	@GET
	@Path("/login")
	public ServiceResult login(@WebParam @QueryParam("user") String login, @WebParam @QueryParam("pass") String pass) {
		try {
			log.debug("Login user");
			User u = userDao.login(login, pass);
			if (u == null) {
				return new ServiceResult(-1L, "Login failed", Type.ERROR);
			}
			
			Sessiondata sd = sessionDao.startsession();
			log.debug("Login user SID : " + sd.getSessionId());
			if (!sessionDao.updateUser(sd.getSessionId(), u.getId(), false, u.getLanguageId())) {
				return new ServiceResult(-35L, "invalid Session-Object", Type.ERROR);
			}
			
			return new ServiceResult(u.getId(), sd.getSessionId(), Type.SUCCESS);
		} catch (OmException oe) {
			return new ServiceResult(oe.getCode() == null ? -1 : oe.getCode(), oe.getMessage(), Type.ERROR);
		} catch (Exception err) {
			log.error("[login]", err);
			return new ServiceResult(-1L, err.getMessage(), Type.ERROR);
		}
	}

	/**
	 * Adds a new User like through the Frontend, but also does activates the
	 * Account To do SSO see the methods to create a hash and use those ones!
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param user
	 *            user object
	 * @param email
	 *            whatever or not to send email, leave empty for auto-send
	 *            
	 * @return - id of the user added or error code
	 * @throws ServiceException
	 */
	@POST
	@Path("/")
	public UserDTO add(
			@WebParam @QueryParam("sid") String sid
			, @WebParam @QueryParam("user") UserDTO user
			, @WebParam @QueryParam("email") Boolean email
			) throws ServiceException
	{
		try {
			Long authUserId = sessionDao.checkSession(sid);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {
				User testUser = userDao.getExternalUser(user.getExternalId(), user.getExternalType());

				if (testUser != null) {
					throw new ServiceException("User does already exist!");
				}

				String jName_timeZone = cfgDao.getConfValue("default.timezone", String.class, "");
				if (user.getAddress() == null) {
					user.setAddress(new Address());
					State s = new State();
					s.setId(1L);
					user.getAddress().setState(s);
				}
				if (user.getLanguageId() == null) {
					user.setLanguageId(1L);
				}
				Long userId = userManagement.registerUser(user.getLogin(), user.getPassword(),
						user.getLastname(), user.getFirstname(), user.getAddress().getEmail(), new Date(), user.getAddress().getStreet(),
						user.getAddress().getAdditionalname(), user.getAddress().getFax(), user.getAddress().getZip(), user.getAddress().getState().getId()
						, user.getAddress().getTown(), user.getLanguageId(),
						"", false, true, // generate SIP Data if the config is enabled
						jName_timeZone, email);

				if (userId == null || userId < 0) {
					throw new ServiceException("Unknown error");
				}

				User u = userDao.get(userId);

				u.getRights().add(Right.Room);
				if (user.getExternalId() == null && user.getExternalType() == null) {
					// activate the User
					u.getRights().add(Right.Login);
					u.getRights().add(Right.Dashboard);
				} else {
					u.setExternalId(user.getExternalId());
					u.setExternalType(user.getExternalType());
				}

				u = userDao.update(u, authUserId);

				return new UserDTO(u);
			} else {
				throw new ServiceException("Insufficient permissins"); //TODO code -26
			}
		} catch (Exception err) {
			log.error("addNewUser", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * 
	 * Delete a certain user by its id
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param id
	 *            the openmeetings user id
	 *            
	 * @return - id of the user deleted, error code otherwise
	 * @throws ServiceException
	 */
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@WebParam @QueryParam("sid") String sid, @WebParam @PathParam("id") long id) throws ServiceException {
		try {
			Long authUserId = sessionDao.checkSession(sid);

			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(authUserId))) {
				userDao.deleteUserID(id);

				return new ServiceResult(id, "Deleted", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("deleteUserById", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * 
	 * Delete a certain user by its external user id
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param externalId
	 *            externalUserId
	 * @param externalType
	 *            externalUserId
	 *            
	 * @return - id of user deleted, or error code
	 * @throws ServiceException
	 */
	@DELETE
	@Path("/{externalType}/{externalId}")
	public ServiceResult deleteExternal(
			@QueryParam("sid") String sid
			, @PathParam("externalType") String externalType
			, @PathParam("externalId") String externalId
			) throws ServiceException
	{
		try {
			Long authUserId = sessionDao.checkSession(sid);

			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(authUserId))) {
				User userExternal = userDao.getExternalUser(externalId, externalType);

				Long userId = userExternal.getId();

				// Setting user deleted
				userDao.deleteUserID(userId);

				return new ServiceResult(userId, "Deleted", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("deleteUserByExternalUserIdAndType", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Description: sets the SessionObject for a certain SID, after setting this
	 * Session-Object you can use the SID + a RoomId to enter any Room. ...
	 * Session-Hashs are deleted 15 minutes after the creation if not used.
	 * 
	 * @param sid
	 *            The SID from getSession
	 * @param user
	 *            user details to set
	 * @param options
	 *            room options to set
	 *            
	 * @return - secure hash or error code
	 * @throws ServiceException
	 */
	@POST
	@Path("/hash")
	public ServiceResult getRoomHash(
			@WebParam @QueryParam("sid") String sid
			, @WebParam @QueryParam("user") ExternalUserDTO user
			, @WebParam @QueryParam("options") RoomOptionsDTO options
			) throws ServiceException
	{
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						user.getLogin(), user.getFirstname(), user.getLastname()
						, user.getProfilePictureUrl(), user.getEmail()
						, user.getExternalId(), user.getExternalType());

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessionDao.updateUserRemoteSession(sid, xmlString);

				//TODO LandingZone are not configurable for now
				String hash = soapLoginDao.addSOAPLogin(sid, options.getRoomId(),
						options.isModerator(), options.isShowAudioVideoTest(), options.isAllowSameURLMultipleTimes(),
						options.getRecordingId(),
						options.isShowNickNameDialog(),
						"room", // LandingZone,
						options.isAllowRecording()
						);

				if (hash != null) {
					return new ServiceResult(0, hash, Type.SUCCESS);
				}
			} else {
				return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("setUserObjectWithAndGenerateRoomHash", err);
			throw new ServiceException(err.getMessage());
		}
		return new ServiceResult(-1L, "Unknown error", Type.ERROR);
	}

	/**
	 * Kick a user by its public SID
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param publicSID
	 *            the publicSID (you can get it from the call to get users in a
	 *            room)
	 * @return - <code>true</code> if user was kicked
	 */
	@POST
	@Path("/kick/{publicSID}")
	public ServiceResult kick(@WebParam @QueryParam("sid") String sid, @PathParam("publicSID") String publicSID) throws ServiceException {
		try {
			Long userId = sessionDao.checkSession(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				Boolean success = userManagement.kickUserByPublicSID(sid, publicSID);
	
				return new ServiceResult(Boolean.TRUE.equals(success) ? 1L : 0L, Boolean.TRUE.equals(success) ? "deleted" : "not deleted", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissins", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("[kickUser]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Returns the count of users currently in the Room with given id
	 * No admin rights are necessary for this call
	 * 
	 * @param SID The SID from UserService.getSession
	 * @param roomId id of the room to get users
	 * @return number of users as int
	 */
	@GET
	@Path("/count/{roomId}")
	public int count(@WebParam @QueryParam("sid") String sid, @PathParam("roomId") Long roomId) {
		Long userId = sessionDao.checkSession(sid);

		if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
			return conferenceService.getRoomClientsListByRoomId(roomId).size();
		}
		return -1;
	}
}
