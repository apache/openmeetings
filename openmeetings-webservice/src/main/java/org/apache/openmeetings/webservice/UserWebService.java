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
import static org.apache.openmeetings.webservice.Constants.USER_SERVICE_NAME;
import static org.apache.openmeetings.webservice.Constants.USER_SERVICE_PORT_NAME;

import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import org.apache.openmeetings.core.session.SessionManager;
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
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.webservice.cluster.UserService;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * The Service contains methods to login and create hash to directly enter
 * conference rooms, recordings or the application in general
 * 
 * @author sebawagner
 * 
 */
@WebService(serviceName = USER_SERVICE_NAME, targetNamespace = TNS, portName = USER_SERVICE_PORT_NAME)
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/user")
public class UserWebService implements UserService {
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
	private SessionManager sessionManager;

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.webservice.cluster.UserService#login(java.lang.String, java.lang.String)
	 */
	@Override
	@WebMethod
	@GET
	@Path("/login")
	public ServiceResult login(@WebParam(name="user") @QueryParam("user") String user, @WebParam(name="pass") @QueryParam("pass") String pass) {
		try {
			log.debug("Login user");
			User u = userDao.login(user, pass);
			if (u == null) {
				return new ServiceResult(-1L, "Login failed", Type.ERROR);
			}
			
			Sessiondata sd = sessionDao.create(u.getId(), u.getLanguageId());
			log.debug("Login user SID : " + sd.getSessionId());
			return new ServiceResult(u.getId(), sd.getSessionId(), Type.SUCCESS);
		} catch (OmException oe) {
			return new ServiceResult(oe.getCode() == null ? -1 : oe.getCode(), oe.getMessage(), Type.ERROR);
		} catch (Exception err) {
			log.error("[login]", err);
			return new ServiceResult(-1L, err.getMessage(), Type.ERROR);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.webservice.cluster.UserService#get(java.lang.String)
	 */
	@Override
	@WebMethod
	@GET
	@Path("/")
	public List<UserDTO> get(@WebParam(name="sid") @QueryParam("sid") String sid) throws ServiceException {
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(sd.getUserId()))) {
				return UserDTO.list(userDao.getAllUsers());
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
			}
		} catch (Exception err) {
			log.error("addNewUser", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.webservice.cluster.UserService#add(java.lang.String, org.apache.openmeetings.db.dto.user.UserDTO, java.lang.Boolean)
	 */
	@Override
	@WebMethod
	@POST
	@Path("/")
	public UserDTO add(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="user") @QueryParam("user") UserDTO user
			, @WebParam(name="confirm") @QueryParam("confirm") Boolean confirm
			) throws ServiceException
	{
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(sd.getUserId()))) {
				User testUser = userDao.getExternalUser(user.getExternalId(), user.getExternalType());

				if (testUser != null) {
					throw new ServiceException("User does already exist!");
				}

				String jName_timeZone = cfgDao.getConfValue("default.timezone", String.class, "");
				if (user.getAddress() == null) {
					user.setAddress(new Address());
					user.getAddress().setCountry(Locale.getDefault().getCountry());
				}
				if (user.getLanguageId() == null) {
					user.setLanguageId(1L);
				}
				Long userId = userManagement.registerUser(user.getLogin(), user.getPassword(),
						user.getLastname(), user.getFirstname(), user.getAddress().getEmail(), new Date(), user.getAddress().getStreet(),
						user.getAddress().getAdditionalname(), user.getAddress().getFax(), user.getAddress().getZip(), user.getAddress().getCountry()
						, user.getAddress().getTown(), user.getLanguageId(),
						"", false, true, // generate SIP Data if the config is enabled
						jName_timeZone, confirm);

				if (userId == null || userId < 0) {
					throw new ServiceException("Unknown error");
				}

				User u = userDao.get(userId);

				u.getRights().add(Right.Room);
				if (Strings.isEmpty(user.getExternalId()) && Strings.isEmpty(user.getExternalType())) {
					// activate the User
					u.getRights().add(Right.Login);
					u.getRights().add(Right.Dashboard);
				} else {
					u.setType(User.Type.external);
					u.setExternalId(user.getExternalId());
					u.setExternalType(user.getExternalType());
				}

				u = userDao.update(u, sd.getUserId());

				return new UserDTO(u);
			} else {
				throw new ServiceException("Insufficient permissions"); //TODO code -26
			}
		} catch (Exception err) {
			log.error("addNewUser", err);
			throw new ServiceException(err.getMessage());
		}
	}

	//FIXME no update
	
	/* (non-Javadoc)
	 * @see org.apache.openmeetings.webservice.cluster.UserService#delete(java.lang.String, long)
	 */
	@Override
	@WebMethod
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) throws ServiceException {
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(sd.getUserId()))) {
				userDao.delete(userDao.get(id), sd.getUserId());

				return new ServiceResult(id, "Deleted", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissions", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("deleteUserById", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.webservice.cluster.UserService#deleteExternal(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@DELETE
	@Path("/{externaltype}/{externalid}")
	public ServiceResult deleteExternal(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="externaltype") @PathParam("externaltype") String externalType
			, @WebParam(name="externalid") @PathParam("externalid") String externalId
			) throws ServiceException
	{
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(sd.getUserId()))) {
				User user = userDao.getExternalUser(externalId, externalType);

				// Setting user deleted
				userDao.delete(user, sd.getUserId());

				return new ServiceResult(user.getId(), "Deleted", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissions", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("deleteUserByExternalUserIdAndType", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.webservice.cluster.UserService#getRoomHash(java.lang.String, org.apache.openmeetings.db.dto.user.ExternalUserDTO, org.apache.openmeetings.db.dto.room.RoomOptionsDTO)
	 */
	@Override
	@WebMethod
	@POST
	@Path("/hash")
	public ServiceResult getRoomHash(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="user") @FormParam("user") ExternalUserDTO user
			, @WebParam(name="options") @FormParam("options") RoomOptionsDTO options
			) throws ServiceException
	{
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(sd.getUserId()))) {
				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						user.getLogin(), user.getFirstname(), user.getLastname()
						, user.getProfilePictureUrl(), user.getEmail()
						, user.getExternalId(), user.getExternalType());

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				//TODO LandingZone are not configurable for now
				String hash = soapLoginDao.addSOAPLogin(sid, options.getRoomId(),
						options.isModerator(), options.isShowAudioVideoTest(), options.isAllowSameURLMultipleTimes(),
						options.getRecordingId(),
						"room", // LandingZone,
						options.isAllowRecording()
						);

				if (hash != null) {
					if (options.isAllowSameURLMultipleTimes()) {
						sd.setPermanent(true);
					}
					sd.setXml(xmlString);
					sessionDao.update(sd);
					return new ServiceResult(0, hash, Type.SUCCESS);
				}
			} else {
				return new ServiceResult(-26L, "Insufficient permissions", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("getRoomHash", err);
			throw new ServiceException(err.getMessage());
		}
		return new ServiceResult(-1L, "Unknown error", Type.ERROR);
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.webservice.cluster.UserService#kick(java.lang.String, java.lang.String)
	 */
	@Override
	@WebMethod
	@POST
	@Path("/kick/{publicsid}")
	public ServiceResult kick(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="publicsid") @PathParam("publicsid") String publicSID) throws ServiceException {
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(sd.getUserId()))) {
				Boolean success = userManagement.kickUserByPublicSID(sid, publicSID);
	
				return new ServiceResult(Boolean.TRUE.equals(success) ? 1L : 0L, Boolean.TRUE.equals(success) ? "deleted" : "not deleted", Type.SUCCESS);
			} else {
				return new ServiceResult(-26L, "Insufficient permissions", Type.ERROR);
			}
		} catch (Exception err) {
			log.error("[kick]", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.openmeetings.webservice.cluster.UserService#count(java.lang.String, java.lang.Long)
	 */
	@Override
	@WebMethod
	@GET
	@Path("/count/{roomid}")
	public int count(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="roomid") @PathParam("roomid") Long roomId) {
		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
			return sessionManager.getClientListByRoom(roomId).size();
		}
		return -1;
	}
}
