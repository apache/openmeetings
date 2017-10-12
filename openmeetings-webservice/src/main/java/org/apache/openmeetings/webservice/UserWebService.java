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

import static org.apache.openmeetings.db.dto.basic.ServiceResult.UNKNOWN;
import static org.apache.openmeetings.db.util.UserHelper.getMinPasswdLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_TIMEZONE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
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
import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
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
import org.apache.openmeetings.service.user.UserManager;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.Validatable;
import org.apache.wicket.validation.ValidationError;
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
@Service("userWebService")
@WebService(serviceName = USER_SERVICE_NAME, targetNamespace = TNS, portName = USER_SERVICE_PORT_NAME)
@Features(features = "org.apache.cxf.feature.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Path("/user")
public class UserWebService extends BaseWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(UserWebService.class, getWebAppRootKey());

	/**
	 * @param user - login or email of Openmeetings user with admin or SOAP-rights
	 * @param pass - password
	 *
	 * @return - {@link ServiceResult} with error code or SID and userId
	 */
	@WebMethod
	@GET
	@Path("/login")
	public ServiceResult login(@WebParam(name="user") @QueryParam("user") String user, @WebParam(name="pass") @QueryParam("pass") String pass) {
		try {
			log.debug("Login user");
			User u = getUserDao().login(user, pass);
			if (u == null) {
				return new ServiceResult("error.bad.credentials", Type.ERROR);
			}

			Sessiondata sd = getSessionDao().create(u.getId(), u.getLanguageId());
			log.debug("Login user: {}", u.getId());
			return new ServiceResult(sd.getSessionId(), Type.SUCCESS);
		} catch (OmException oe) {
			return oe.getKey() == null ? UNKNOWN : new ServiceResult(oe.getKey(), Type.ERROR);
		} catch (Exception err) {
			log.error("[login]", err);
			return UNKNOWN;
		}
	}

	/**
	 * Lists all users in the system!
	 *
	 * @param sid
	 *            The SID from getSession
	 *
	 * @return - list of users
	 */
	@WebMethod
	@GET
	@Path("/")
	public List<UserDTO> get(@WebParam(name="sid") @QueryParam("sid") String sid) {
		return performCall(sid, User.Right.Soap, sd -> UserDTO.list(getUserDao().getAllUsers()));
	}

	/**
	 * Adds a new User like through the Frontend, but also does activates the
	 * Account To do SSO see the methods to create a hash and use those ones!
	 *
	 * @param sid
	 *            The SID from getSession
	 * @param user
	 *            user object
	 * @param confirm
	 *            whatever or not to send email, leave empty for auto-send
	 *
	 * @return - id of the user added or error code
	 */
	@WebMethod
	@POST
	@Path("/")
	public UserDTO add(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="user") @FormParam("user") UserDTO user
			, @WebParam(name="confirm") @FormParam("confirm") Boolean confirm
			)
	{
		return performCall(sid, User.Right.Soap, sd -> {
			UserDao userDao = getUserDao();
			User testUser = userDao.getExternalUser(user.getExternalId(), user.getExternalType());

			if (testUser != null) {
				throw new ServiceException("User does already exist!");
			}

			ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
			String tz = user.getTimeZoneId();
			if (Strings.isEmpty(tz)) {
				tz = cfgDao.getString(CONFIG_DEFAULT_TIMEZONE, "");
			}
			if (user.getAddress() == null) {
				user.setAddress(new Address());
				user.getAddress().setCountry(Locale.getDefault().getCountry());
			}
			if (user.getLanguageId() == null) {
				user.setLanguageId(1L);
			}
			IValidator<String> passValidator = new StrongPasswordValidator(true, getMinPasswdLength(cfgDao), user.get(userDao));
			Validatable<String> passVal = new Validatable<>(user.getPassword());
			passValidator.validate(passVal);
			if (!passVal.isValid()) {
				StringBuilder sb = new StringBuilder();
				for (IValidationError err : passVal.getErrors()) {
					sb.append(((ValidationError)err).getMessage()).append(System.lineSeparator());
				}
				log.debug("addNewUser::weak password '{}', msg: {}", user.getPassword(), sb);
				throw new ServiceException(sb.toString());
			}
			Object _user = getBean(UserManager.class).registerUser(user.getLogin(), user.getPassword(),
					user.getLastname(), user.getFirstname(), user.getAddress().getEmail(), new Date(), user.getAddress().getStreet(),
					user.getAddress().getAdditionalname(), user.getAddress().getFax(), user.getAddress().getZip(), user.getAddress().getCountry()
					, user.getAddress().getTown(), user.getLanguageId(),
					"", false, true, // generate SIP Data if the config is enabled
					tz, confirm);

			if (_user == null) {
				throw new ServiceException(UNKNOWN.getMessage());
			} else if (_user instanceof String) {
				throw new ServiceException((String)_user);
			}

			User u = (User)_user;

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
		});
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
	 */
	@WebMethod
	@DELETE
	@Path("/{id}")
	public ServiceResult delete(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="id") @PathParam("id") long id) {
		return performCall(sid, User.Right.Admin, sd -> {
			UserDao userDao = getUserDao();
			userDao.delete(userDao.get(id), sd.getUserId());

			return new ServiceResult("Deleted", Type.SUCCESS);
		});
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
	 */
	@DELETE
	@Path("/{externaltype}/{externalid}")
	public ServiceResult deleteExternal(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="externaltype") @PathParam("externaltype") String externalType
			, @WebParam(name="externalid") @PathParam("externalid") String externalId
			)
	{
		return performCall(sid, User.Right.Admin, sd -> {
			UserDao userDao = getUserDao();
			User user = userDao.getExternalUser(externalId, externalType);

			// Setting user deleted
			userDao.delete(user, sd.getUserId());

			return new ServiceResult("Deleted", Type.SUCCESS);
		});
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
	 */
	@WebMethod
	@POST
	@Path("/hash")
	public ServiceResult getRoomHash(
			@WebParam(name="sid") @QueryParam("sid") String sid
			, @WebParam(name="user") @FormParam("user") ExternalUserDTO user
			, @WebParam(name="options") @FormParam("options") RoomOptionsDTO options
			)
	{
		return performCall(sid, User.Right.Soap, sd -> {
			RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
					user.getLogin(), user.getFirstname(), user.getLastname()
					, user.getProfilePictureUrl(), user.getEmail()
					, user.getExternalId(), user.getExternalType());

			log.debug(remoteSessionObject.toString());

			String xmlString = remoteSessionObject.toXml();

			log.debug("xmlString " + xmlString);

			String hash = getBean(SOAPLoginDao.class).addSOAPLogin(sid, options.getRoomId(),
					options.isModerator(), options.isShowAudioVideoTest(), options.isAllowSameURLMultipleTimes(),
					options.getRecordingId(),
					options.isAllowRecording()
					);

			if (hash != null) {
				if (options.isAllowSameURLMultipleTimes()) {
					sd.setPermanent(true);
				}
				sd.setXml(xmlString);
				getSessionDao().update(sd);
				return new ServiceResult(hash, Type.SUCCESS);
			}
			return UNKNOWN;
		});
	}

	/**
	 * Kick a user by its public SID
	 *
	 * @param sid
	 *            The SID from getSession
	 * @param uid the uid of the client
	 * @return - <code>true</code> if user was kicked
	 */
	@WebMethod
	@POST
	@Path("/kick/{uid}")
	public ServiceResult kick(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="uid") @PathParam("uid") String uid) {
		return performCall(sid, User.Right.Soap, sd -> {
			boolean success = getBean(IUserManager.class).kickById(uid);

			return new ServiceResult(Boolean.TRUE.equals(success) ? "kicked" : "not kicked", Type.SUCCESS);
		});
	}

	/**
	 * Returns the count of users currently in the Room with given id
	 * No admin rights are necessary for this call
	 *
	 * @param sid The SID from UserService.getSession
	 * @param roomId id of the room to get users
	 * @return number of users as int
	 */
	@WebMethod
	@GET
	@Path("/count/{roomid}")
	public ServiceResult count(@WebParam(name="sid") @QueryParam("sid") String sid, @WebParam(name="roomid") @PathParam("roomid") Long roomId) {
		return performCall(sid, User.Right.Soap, sd -> new ServiceResult(String.valueOf(getApp().getOmRoomClients(roomId).size()), Type.SUCCESS));
	}
}
