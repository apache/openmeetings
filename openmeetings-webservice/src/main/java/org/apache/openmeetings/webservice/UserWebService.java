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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultGroup;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultTimezone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isAllowRegisterSoap;
import static org.apache.openmeetings.webservice.Constants.TNS;
import static org.apache.openmeetings.webservice.Constants.USER_SERVICE_NAME;
import static org.apache.openmeetings.webservice.Constants.USER_SERVICE_PORT_NAME;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

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
import org.apache.openmeetings.core.util.StrongPasswordValidator;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
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
import org.apache.openmeetings.db.mapper.UserMapper;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.webservice.error.InternalServiceException;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.apache.openmeetings.webservice.schema.ServiceResultWrapper;
import org.apache.openmeetings.webservice.schema.UserDTOListWrapper;
import org.apache.openmeetings.webservice.schema.UserDTOWrapper;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.Validatable;
import org.apache.wicket.validation.ValidationError;
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
@Service("userWebService")
@WebService(serviceName = USER_SERVICE_NAME, targetNamespace = TNS, portName = USER_SERVICE_PORT_NAME)
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
@Produces({MediaType.APPLICATION_JSON})
@Tag(name = "UserService")
@Path("/user")
public class UserWebService extends BaseWebService {
	private static final Logger log = LoggerFactory.getLogger(UserWebService.class);

	@Inject
	private IUserManager userManager;
	@Inject
	private SOAPLoginDao soapDao;
	@Inject
	private GroupDao groupDao;
	@Inject
	private UserMapper uMapper;

	/**
	 * @param user - login or email of Openmeetings user with admin or SOAP-rights
	 * @param pass - password
	 *
	 * @return - {@link ServiceResult} with error code or SID and userId
	 */
	@WebMethod
	@GET
	@Path("/login")
	@Operation(
			description = "Login and create sessionId required for sub-sequent calls",
			responses = {
					@ApiResponse(responseCode = "200", description = "ServiceResult with error code or SID and userId",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error of server error")
			}
		)
	public ServiceResult login(
			@Parameter(required = true, description = "login or email of Openmeetings user with admin or SOAP-rights") @WebParam(name="user") @QueryParam("user") String user
			, @Parameter(required = true, description = "password") @WebParam(name="pass") @QueryParam("pass") String pass) {
		try {
			log.debug("Login user");
			User u = userDao.login(user, pass);
			if (u == null) {
				return new ServiceResult("error.bad.credentials", Type.ERROR);
			}

			Sessiondata sd = sessionDao.create(u.getId(), u.getLanguageId());
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@GET
	@Path("/")
	@Operation(
			description = "Lists all users in the system!",
			responses = {
					@ApiResponse(responseCode = "200", description = "list of users",
							content = @Content(schema = @Schema(implementation = UserDTOListWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public List<UserDTO> get(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			) throws ServiceException {
		return performCall(sid, User.Right.SOAP, sd -> UserDTO.list(userDao.getAllUsers()));
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Path("/")
	@Operation(
			description = """
					Adds a new User like through the Frontend, but also does activates the
					 Account To do SSO see the methods to create a hash and use those ones!""",
			responses = {
					@ApiResponse(responseCode = "200", description = "list of users",
						content = @Content(schema = @Schema(implementation = UserDTOWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public UserDTO add(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "user object") @WebParam(name="user") @FormParam("user") UserDTO user
			, @Parameter(required = true, description = "whatever or not to send email, leave empty for auto-send") @WebParam(name="confirm") @FormParam("confirm") Boolean confirm
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			if (!isAllowRegisterSoap()) {
				throw new InternalServiceException("Soap register is denied in Settings");
			}
			User testUser = userDao.getExternalUser(user.getExternalId(), user.getExternalType());

			if (testUser != null) {
				throw new InternalServiceException("User does already exist!");
			}

			String tz = user.getTimeZoneId();
			if (Strings.isEmpty(tz)) {
				tz = getDefaultTimezone();
			}
			if (user.getAddress() == null) {
				user.setAddress(new Address());
				user.getAddress().setCountry(Locale.getDefault().getCountry());
			}
			if (user.getLanguageId() == null) {
				user.setLanguageId(1L);
			}
			User jsonUser = uMapper.get(user);
			IValidator<String> passValidator = new StrongPasswordValidator(false, jsonUser);
			Validatable<String> passVal = new Validatable<>(user.getPassword());
			passValidator.validate(passVal);
			if (!passVal.isValid()) {
				StringBuilder sb = new StringBuilder();
				for (IValidationError err : passVal.getErrors()) {
					sb.append(((ValidationError)err).getMessage()).append(System.lineSeparator());
				}
				log.debug("addNewUser::weak password '{}', msg: {}", user.getPassword(), sb);
				throw new InternalServiceException(sb.toString());
			}
			Object ouser;
			try {
				jsonUser.addGroup(groupDao.get(getDefaultGroup()));
				ouser = userManager.registerUser(jsonUser, user.getPassword(), null);
			} catch (NoSuchAlgorithmException | OmException e) {
				throw new InternalServiceException("Unexpected error while creating user");
			}

			if (ouser == null) {
				throw new InternalServiceException(UNKNOWN.getMessage());
			} else if (ouser instanceof String str) {
				throw new InternalServiceException(str);
			}

			User u = (User)ouser;

			u.getRights().add(Right.ROOM);
			if (Strings.isEmpty(user.getExternalId()) && Strings.isEmpty(user.getExternalType())) {
				// activate the User
				u.getRights().add(Right.LOGIN);
				u.getRights().add(Right.DASHBOARD);
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@DELETE
	@Path("/{id}")
	@Operation(
			description = "Delete a certain user by its id",
			responses = {
					@ApiResponse(responseCode = "200", description = "id of the user deleted, error code otherwise",
						content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult delete(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "the openmeetings user id") @WebParam(name="id") @PathParam("id") long id
			) throws ServiceException
	{
		return performCall(sid, User.Right.ADMIN, sd -> {
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
	 *            externalType
	 *
	 * @return - id of user deleted, or error code
	 * @throws {@link ServiceException} in case of any errors
	 */
	@DELETE
	@Path("/{externaltype}/{externalid}")
	@Operation(
			description = "Delete a certain user by its external user id",
			responses = {
					@ApiResponse(responseCode = "200", description = "id of user deleted, or error code",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult deleteExternal(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "externalUserId") @WebParam(name="externaltype") @PathParam("externaltype") String externalType
			, @Parameter(required = true, description = "externalType") @WebParam(name="externalid") @PathParam("externalid") String externalId
			) throws ServiceException
	{
		return performCall(sid, User.Right.ADMIN, sd -> {
			User user = userDao.getExternalUser(externalId, externalType);

			// Setting user deleted
			userDao.delete(user, sd.getUserId());

			return new ServiceResult("Deleted", Type.SUCCESS);
		});
	}

	/**
	 * Sets the SessionObject for a certain SID, after setting this
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
	 * @throws {@link ServiceException} in case of any errors
	 */
	@WebMethod
	@POST
	@Path("/hash")
	@Operation(
			description = """
					Sets the SessionObject for a certain SID, after setting this
					 Session-Object you can use the SID + a RoomId to enter any Room. ...
					  Session-Hashs are deleted 15 minutes after the creation if not used.""",
			responses = {
					@ApiResponse(responseCode = "200", description = "secure hash or error code",
							content = @Content(schema = @Schema(implementation = ServiceResultWrapper.class))),
					@ApiResponse(responseCode = "500", description = "Error in case of invalid credentials or server error")
			}
		)
	public ServiceResult getRoomHash(
			@Parameter(required = true, description = "The SID of the User. This SID must be marked as Loggedin") @WebParam(name="sid") @QueryParam("sid") String sid
			, @Parameter(required = true, description = "user details to set") @WebParam(name="user") @FormParam("user") ExternalUserDTO user
			, @Parameter(required = true, description = "room options to set") @WebParam(name="options") @FormParam("options") RoomOptionsDTO options
			) throws ServiceException
	{
		return performCall(sid, User.Right.SOAP, sd -> {
			if (Strings.isEmpty(user.getExternalId()) || Strings.isEmpty(user.getExternalType())) {
				return new ServiceResult("externalId and/or externalType are not set", Type.ERROR);
			}
			RemoteSessionObject remoteSessionObject = new RemoteSessionObject(user);

			log.debug(remoteSessionObject.toString());

			String xmlString = remoteSessionObject.toString();

			log.debug("jsonString {}", xmlString);

			String hash = soapDao.addSOAPLogin(sid, options);

			if (hash != null) {
				if (options.isAllowSameURLMultipleTimes()) {
					sd.setPermanent(true);
				}
				sd.setXml(xmlString);
				sd = sessionDao.update(sd);
				return new ServiceResult(hash, Type.SUCCESS);
			}
			return UNKNOWN;
		});
	}
}
