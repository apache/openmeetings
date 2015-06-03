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
import java.util.Date;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.feature.Features;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.basic.ErrorDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.OrganisationUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ErrorResult;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.dto.user.UserDTO;
import org.apache.openmeetings.db.dto.user.UserSearchResult;
import org.apache.openmeetings.db.entity.basic.ErrorType;
import org.apache.openmeetings.db.entity.basic.ErrorValue;
import org.apache.openmeetings.db.entity.server.RemoteSessionObject;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.OrganisationUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.webservice.error.ServiceException;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.istack.NotNull;

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
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/user")
public class UserWebService {
	private static final Logger log = Red5LoggerFactory.getLogger(UserWebService.class, webAppRootKey);

	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private IUserManager userManagement;
	@Autowired
	private ErrorDao errorDao;
	@Autowired
	private OrganisationDao orgDao;
	@Autowired
	private OrganisationUserDao orgUserDao;
	@Autowired
	private SOAPLoginDao soapLoginDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private LabelDao labelDao;

	/**
	 * load this session id before doing anything else Returns an Object of Type
	 * Sessiondata, this contains a sessionId, use that sessionId in all Methods
	 * 
	 * @return - creates new session
	 */
	@GET
	@Path("/getSession")
	public Sessiondata getSession() {
		log.debug("SPRING LOADED getSession -- ");
		return sessionDao.startsession();
	}

	/**
	 * Auth function, use the SID you get by getSession, return positive means
	 * logged-in, if negative its an ErrorCode, you have to invoke the Method
	 * getErrorByCode to get the Text-Description of that ErrorCode
	 * 
	 * @param SID - The SID from getSession
	 * @param username - Username from OpenMeetings, the user has to have Admin-rights
	 * @param userpass - Userpass from OpenMeetings
	 *            
	 * @return - id of the logged in user, -1 in case of the error
	 */
	@GET
	@Path("/login")
	public Long login(@WebParam String SID, @WebParam String username, @WebParam String userpass) {
		try {
			log.debug("Login user SID : " + SID);
			User u = userDao.login(username, userpass);
			if (u == null) {
				return -1L;
			}
			
			boolean bool = sessiondataDao.updateUser(SID, u.getId(), false, u.getLanguageId());
			if (!bool) {
				// invalid Session-Object
				return -35L;
			}
			
			return u.getId();
		} catch (OmException oe) {
			if (oe.getCode() != null) {
				return oe.getCode();
			}
		} catch (Exception err) {
			log.error("[login]", err);
		}
		return -1L;
	}

	/**
	 * loads an Error-Object. If a Method returns a negative Result, its an
	 * Error-id, it needs a languageId to specify in which language you want to
	 * display/read the error-message. English has the Language-ID one, for
	 * different one see the list of languages
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param errorid
	 *            the error id (negative Value here!)
	 * @param langId
	 *            The id of the language
	 *            
	 * @return - error with the code given
	 */
	@GET
	@Path("/getErrorByCode")
	public ErrorResult getErrorByCode(@WebParam String SID, @WebParam long errorid, @WebParam long langId) {
		try {
			if (errorid < 0) {
				ErrorValue eValues = errorDao.get(-1 * errorid);
				if (eValues != null) {
					ErrorType eType = errorDao.getErrorType(eValues.getTypeId());
					log.debug("eValues.getLabelId() = " + eValues.getLabelId());
					log.debug("eValues.getErrorType() = " + eType);
					String eValue = labelDao.getString(eValues.getLabelId(), langId);
					String tValue = labelDao.getString(eType.getLabelId(), langId);
					if (eValue != null) {
						return new ErrorResult(errorid, eValue, tValue);
					}
				}
			} else {
				return new ErrorResult(errorid, "Error ... please check your input", "Error");
			}
		} catch (Exception err) {
			log.error("[getErrorByCode] ", err);
		}
		return null;
	}

	/**
	 * Adds a new User like through the Frontend, but also does activates the
	 * Account To do SSO see the methods to create a hash and use those ones!
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param userDTO
	 *            user object
	 *            
	 * @return - id of the user added or error code
	 * @throws ServiceException
	 */
	@GET
	@Path("/addUser")
	public Long addUser(@WebParam String SID, @WebParam @NotNull UserDTO userDto)
			throws ServiceException {
		try {
			Long authUserId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {

				String jName_timeZone = configurationDao.getConfValue("default.timezone", String.class, "");

				Long userId = userManagement.registerUser(userDto.getLogin(), userDto.getPassword(),
						userDto.getLastname(), userDto.getFirstname(), userDto.getAddress() != null ? userDto.getAddress().getEmail() : "", new Date(), userDto.getAddress().getStreet(), //FIXME NPE signature
						userDto.getAddress().getAdditionalname(), userDto.getAddress().getFax(), userDto.getAddress().getZip(), userDto.getAddress().getState().getId() //FIXME NPE signature
						, userDto.getAddress().getTown(), userDto.getLanguageId(), //FIXME NPE signature
						"", false, true, // generate SIP Data if the config is enabled
						jName_timeZone);

				if (userId == null || userId < 0) {
					return userId;
				}

				User user = userDao.get(userId);

				// activate the User
				user.getRights().add(Right.Dashboard);
				user.getRights().add(Right.Login);
				user.getRights().add(Right.Room);
				user.setUpdated(new Date());

				userDao.update(user, authUserId);

				return userId;

			} else {
				return new Long(-26);
			}
		} catch (Exception err) {
			log.error("addNewUser", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Adds a new User like through the Frontend, but also does activates the
	 * Account
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param username
	 *            any username
	 * @param userpass
	 *            any userpass
	 * @param lastname
	 *            any lastname
	 * @param firstname
	 *            any firstname
	 * @param email
	 *            any email
	 * @param additionalname
	 *            any additionalname
	 * @param street
	 *            any street
	 * @param zip
	 *            any zip
	 * @param fax
	 *            any fax
	 * @param stateId
	 *            a valid stateId
	 * @param town
	 *            any town
	 * @param languageId
	 *            the languageId
	 * @param jNameTimeZone
	 *            the name of the timezone for the user
	 *            
	 * @return - id of the user added or the error code
	 * @throws ServiceException
	 */
	public Long addNewUserWithTimeZone(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long stateId, String town, long languageId, String jNameTimeZone) throws ServiceException {
		try {
			Long authUserId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {

				Long userId = userManagement.registerUser(username, userpass,
						lastname, firstname, email, new Date(), street,
						additionalname, fax, zip, stateId, town, languageId,
						"", false, true, // generate
											// SIP
											// Data
											// if
											// the
											// config
											// is
											// enabled
						jNameTimeZone); 

				if (userId == null || userId < 0) {
					return userId;
				}

				User user = userDao.get(userId);

				// activate the User
				user.getRights().add(Right.Login);
				user.setUpdated(new Date());

				userDao.update(user, authUserId);

				return userId;

			} else {
				return new Long(-26);
			}
		} catch (Exception err) {
			log.error("addNewUserWithTimeZone", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * Adds a new User like through the Frontend, but also does activates the
	 * Account, sends NO email (no matter what you configured) and sets the
	 * users external user id and type
	 * 
	 * Use the methods to create a hash for SSO, creating users is not required
	 * for SSO
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param username
	 *            any username
	 * @param userpass
	 *            any userpass
	 * @param lastname
	 *            any lastname
	 * @param firstname
	 *            any firstname
	 * @param email
	 *            any email
	 * @param additionalname
	 *            any additionalname
	 * @param street
	 *            any street
	 * @param zip
	 *            any zip
	 * @param fax
	 *            any fax
	 * @param stateId
	 *            a valid stateId
	 * @param town
	 *            any town
	 * @param languageId
	 *            the languageId
	 * @param jNameTimeZone
	 *            the name of the timezone for the user
	 * @param externalUserId
	 *            externalUserId
	 * @param externalUserType
	 *            externalUserType
	 *            
	 * @return - id of user added or error code
	 * @throws ServiceException
	 */
	public Long addNewUserWithExternalType(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long stateId, String town, long languageId,
			String jNameTimeZone, String externalUserId, String externalUserType)
			throws ServiceException {
		try {
			Long authUserId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(authUserId))) {

				User testUser = userDao.getExternalUser(externalUserId, externalUserType);

				if (testUser != null) {
					throw new Exception("User does already exist!");
				}

				// This will send no email to the users
				Long userId = userManagement.registerUserNoEmail(username,
						userpass, lastname, firstname, email, new Date(),
						street, additionalname, fax, zip, stateId, town,
						languageId, "", false, true, // generate SIP Data if the config is enabled
						jNameTimeZone);

				if (userId == null || userId < 0) {
					return userId;
				}

				User user = userDao.get(userId);

				// activate the User
				user.getRights().add(Right.Login);
				user.setUpdated(new Date());
				user.setExternalId(externalUserId);
				user.setExternalType(externalUserType);

				userDao.update(user, authUserId);

				return userId;

			} else {
				return new Long(-26);
			}

		} catch (Exception err) {
			log.error("addNewUserWithExternalType", err);
			throw new ServiceException(err.getMessage());
		}
	}

	/**
	 * 
	 * Delete a certain user by its id
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param userId
	 *            the openmeetings user id
	 *            
	 * @return - id of the user deleted, error code otherwise
	 * @throws ServiceException
	 */
	public Long deleteUserById(String SID, Long userId) throws ServiceException {
		try {
			Long authUserId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(authUserId))) {

				// Setting user deleted
				userDao.deleteUserID(userId);

				return userId;

			} else {
				return new Long(-26);
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
	 * @param SID
	 *            The SID from getSession
	 * @param externalId
	 *            externalUserId
	 * @param externalType
	 *            externalUserId
	 *            
	 * @return - id of user deleted, or error code
	 * @throws ServiceException
	 */
	public Long deleteUserByExternalUserIdAndType(String SID,
			String externalId, String externalType) throws ServiceException {
		try {
			Long authUserId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(authUserId))) {

				User userExternal = userDao.getExternalUser(externalId, externalType);

				Long userId = userExternal.getId();

				// Setting user deleted
				userDao.deleteUserID(userId);

				return userId;

			} else {
				return new Long(-26);
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
	 * @param SID
	 *            The SID from getSession
	 * @param username
	 *            any username
	 * @param firstname
	 *            any firstname
	 * @param lastname
	 *            any lastname
	 * @param profilePictureUrl
	 *            any profilePictureUrl
	 * @param email
	 *            any email
	 * @param externalUserId
	 *            if you have any external user Id you may set it here
	 * @param externalUserType
	 *            you can specify your system-name here, for example "moodle"
	 * @param roomId
	 *            the room id the user should be logged in
	 * @param becomeModeratorAsInt
	 *            0 means no Moderator, 1 means Moderator
	 * @param showAudioVideoTestAsInt
	 *            0 means don't show Audio/Video Test, 1 means show Audio/Video
	 *            Test Application before the user is logged into the room
	 *            
	 * @return - secure hash or error code
	 * @throws ServiceException
	 */
	public String setUserObjectAndGenerateRoomHash(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, String externalUserId, String externalUserType,
			Long roomId, int becomeModeratorAsInt, int showAudioVideoTestAsInt)
			throws ServiceException {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				boolean becomeModerator = false;
				if (becomeModeratorAsInt != 0) {
					becomeModerator = true;
				}

				boolean showAudioVideoTest = false;
				if (showAudioVideoTestAsInt != 0) {
					showAudioVideoTest = true;
				}

				String hash = soapLoginDao.addSOAPLogin(SID, roomId,
						becomeModerator, showAudioVideoTest, false, // allowSameURLMultipleTimes
						null, // recordingId
						false, // showNickNameDialogAsInt
						"room", // LandingZone,
						true // allowRecording
						);

				if (hash != null) {
					return hash;
				}

			} else {
				return "" + new Long(-26);
			}
		} catch (Exception err) {
			log.error("setUserObjectWithAndGenerateRoomHash", err);
			throw new ServiceException(err.getMessage());
		}
		return "" + new Long(-1);
	}

	/**
	 * 
	 * Description: sets the SessionObject for a certain SID, after setting this
	 * Session-Object you can use the SID + a RoomId to enter any Room.
	 * 
	 * ++ the user can press f5 to reload the page / use the link several times,
	 * the SOAP Gateway does remember the IP of the user and the will only the
	 * first user that enters the room allow to re-enter. ... Session-Hashs are
	 * deleted 15 minutes after the creation if not used.
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param username
	 *            any username
	 * @param firstname
	 *            any firstname
	 * @param lastname
	 *            any lastname
	 * @param profilePictureUrl
	 *            any profilePictureUrl
	 * @param email
	 *            any email any email
	 * @param externalUserId
	 *            if you have any external user Id you may set it here
	 * @param externalUserType
	 *            you can specify your system-name here, for example "moodle"
	 * @param roomId
	 *            the room id the user should be logged in
	 * @param becomeModeratorAsInt
	 *            0 means no Moderator, 1 means Moderator
	 * @param showAudioVideoTestAsInt
	 *            0 means don't show Audio/Video Test, 1 means show Audio/Video
	 *            Test Application before the user is logged into the room
	 *            
	 * @return - secure hash or error code
	 */
	public String setUserObjectAndGenerateRoomHashByURL(String SID,
			String username, String firstname, String lastname,
			String profilePictureUrl, String email, String externalUserId,
			String externalUserType, Long roomId, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt) throws ServiceException {

		log.debug("UserService.setUserObjectAndGenerateRoomHashByURL");
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				boolean becomeModerator = false;
				if (becomeModeratorAsInt != 0) {
					becomeModerator = true;
				}

				boolean showAudioVideoTest = false;
				if (showAudioVideoTestAsInt != 0) {
					showAudioVideoTest = true;
				}

				String hash = soapLoginDao.addSOAPLogin(SID, roomId,
						becomeModerator, showAudioVideoTest, true, // allowSameURLMultipleTimes
						null, // recordingId
						false, // showNickNameDialogAsInt
						"room", // LandingZone,
						true // allowRecording
						);

				if (hash != null) {
					return hash;
				}

			} else {
				return "" + new Long(-26);
			}
		} catch (Exception err) {
			log.error("setUserObjectAndGenerateRoomHashByURL", err);
			throw new ServiceException(err.getMessage());
		}
		return "" + new Long(-1);
	}

	/**
	 * 
	 * Description: sets the SessionObject for a certain SID, after setting this
	 * Session-Object you can use the SID + a RoomId to enter any Room.
	 * 
	 * ++ the user can press f5 to reload the page / use the link several times,
	 * the SOAP Gateway does remember the IP of the user and the will only the
	 * first user that enters the room allow to re-enter. ... Session-Hashs are
	 * deleted 15 minutes after the creation if not used.
	 * 
	 * ++ sets the flag if the user can do recording in the conference room
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param username
	 *            any username
	 * @param firstname
	 *            any firstname
	 * @param lastname
	 *            any lastname
	 * @param profilePictureUrl
	 *            any profilePictureUrl
	 * @param email
	 *            any email
	 * @param externalUserId
	 *            if you have any external user Id you may set it here
	 * @param externalUserType
	 *            you can specify your system-name here, for example "moodle"
	 * @param roomId
	 *            the room id the user should be logged in
	 * @param becomeModeratorAsInt
	 *            0 means no Moderator, 1 means Moderator
	 * @param showAudioVideoTestAsInt
	 *            0 means don't show Audio/Video Test, 1 means show Audio/Video
	 *            Test Application before the user is logged into the room
	 * @param allowRecording
	 *            0 means don't allow Recording, 1 means allow Recording
	 *            
	 * @return - secure hash or error code
	 */
	public String setUserObjectAndGenerateRoomHashByURLAndRecFlag(String SID,
			String username, String firstname, String lastname,
			String profilePictureUrl, String email, String externalUserId,
			String externalUserType, Long roomId, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int allowRecording) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				boolean becomeModerator = false;
				if (becomeModeratorAsInt != 0) {
					becomeModerator = true;
				}

				boolean showAudioVideoTest = false;
				if (showAudioVideoTestAsInt != 0) {
					showAudioVideoTest = true;
				}

				boolean allowRecordingBool = false;
				if (allowRecording != 0) {
					allowRecordingBool = true;
				}

				String hash = soapLoginDao.addSOAPLogin(SID, roomId,
						becomeModerator, showAudioVideoTest, true, // allowSameURLMultipleTimes
						null, // recordingId
						false, // showNickNameDialogAsInt
						"room", // LandingZone,
						allowRecordingBool // allowRecording
						);

				if (hash != null) {
					return hash;
				}

			} else {
				return "" + new Long(-26);
			}
		} catch (Exception err) {
			log.error("setUserObjectWithAndGenerateRoomHash", err);
		}
		return "" + new Long(-1);
	}

	/**
	 * 
	 * Description: sets the SessionObject for a certain SID, after setting this
	 * Session-Object you can use the SID and directly login into the dashboard
	 * 
	 * ++ the user can press f5 to reload the page / use the link several times,
	 * the SOAP Gateway does remember the IP of the user and the will only the
	 * first user that enters the room allow to re-enter. ... Session-Hashs are
	 * deleted 15 minutes after the creation if not used.
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param username
	 *            any username
	 * @param firstname
	 *            any firstname
	 * @param lastname
	 *            any lastname
	 * @param profilePictureUrl
	 *            any absolute profilePictureUrl
	 * @param email
	 *            any email
	 * @param externalUserId
	 *            if you have any external user Id you may set it here
	 * @param externalUserType
	 *            you can specify your system-name here, for example "moodle"
	 *            
	 * @return - secure hash or error code
	 */
	public String setUserObjectMainLandingZone(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, String externalUserId, String externalUserType) {
		log.debug("UserService.setUserObjectMainLandingZone");

		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				String hash = soapLoginDao.addSOAPLogin(SID, null, false, true,
						true, // allowSameURLMultipleTimes
						null, // recordingId
						false, // showNickNameDialogAsInt
						"dashboard", // LandingZone,
						true // allowRecording
						);

				if (hash != null) {
					return hash;
				}

			} else {
				return "" + -26L;
			}
		} catch (Exception err) {
			log.error("setUserObjectWithAndGenerateRoomHash", err);
		}
		return "" + -1L;
	}

	/**
	 * 
	 * Description: sets the SessionObject for a certain SID, after setting this
	 * Session-Object you can use the SID + a RoomId to enter any Room.
	 * 
	 * ++ the user can press f5 to reload the page / use the link several times,
	 * the SOAP Gateway does remember the IP of the user and the will only the
	 * first user that enters the room allow to re-enter. ... Session-Hashs are
	 * deleted 15 minutes after the creation if not used.
	 * 
	 * ++ Additionally you can set a param showNickNameDialogAsInt, the effect
	 * if that param is 1 is, that the user gets a popup where he can enter his
	 * nickname right before he enters the conference room. All nicknames and
	 * emails users enter are logged in the conferencelog table.
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param username
	 *            any username
	 * @param firstname
	 *            any firstname
	 * @param lastname
	 *            any lastname
	 * @param profilePictureUrl
	 *            any profilePictureUrl
	 * @param email
	 *            any email
	 * @param externalUserId
	 *            if you have any external user Id you may set it here
	 * @param externalUserType
	 *            you can specify your system-name here, for example "moodle"
	 * @param roomId
	 *            the room id the user should be logged in
	 * @param becomeModeratorAsInt
	 *            0 means no Moderator, 1 means Moderator
	 * @param showAudioVideoTestAsInt
	 *            0 means don't show Audio/Video Test, 1 means show Audio/Video
	 *            Test Application before the user is logged into the room
	 * @param showNickNameDialogAsInt
	 *            0 means do not show the popup to enter a nichname, 1 means
	 *            that there is a popup to enter the nickname for the conference
	 *            
	 * @return - secure hash, or error code 
	 */
	public String setUserAndNickName(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, String externalUserId, String externalUserType,
			Long roomId, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int showNickNameDialogAsInt) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());
				log.debug("showNickNameDialogAsInt" + showNickNameDialogAsInt);

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				boolean becomeModerator = false;
				if (becomeModeratorAsInt != 0) {
					becomeModerator = true;
				}

				boolean showAudioVideoTest = false;
				if (showAudioVideoTestAsInt != 0) {
					showAudioVideoTest = true;
				}

				boolean showNickNameDialog = false;
				if (showNickNameDialogAsInt != 0) {
					showNickNameDialog = true;
				}

				String hash = soapLoginDao.addSOAPLogin(SID, roomId,
						becomeModerator, showAudioVideoTest, true, null,
						showNickNameDialog, "room", // LandingZone,
						true // allowRecording
						);

				if (hash != null) {
					return hash;
				}

			} else {
				return "" + new Long(-26);
			}
		} catch (Exception err) {
			log.error("setUserObjectWithAndGenerateRoomHash", err);
		}
		return "" + new Long(-1);
	}

	/**
	 * Use this method to access a Recording instead of Room
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param username
	 *            any username
	 * @param firstname
	 *            any firstname
	 * @param lastname
	 *            any lastname
	 * @param externalUserId
	 *            if you have any external user Id you may set it here
	 * @param externalUserType
	 *            you can specify your system-name here, for example "moodle"
	 * @param recordingId
	 *            the id of the recording, get a List of all Recordings with
	 *            RoomService::getFlvRecordingByExternalRoomType
	 *            
	 * @return - hash of the recording, or error id
	 */
	public String setUserObjectAndGenerateRecordingHashByURL(String SID,
			String username, String firstname, String lastname,
			String externalUserId, String externalUserType, Long recordingId) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, "", "", "", externalUserId,
						externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				String hash = soapLoginDao.addSOAPLogin(SID, null, false,
						false, true, // allowSameURLMultipleTimes
						recordingId, // recordingId
						false, // showNickNameDialogAsInt
						"room", // LandingZone,
						true // allowRecording
						);

				if (hash != null) {
					return hash;
				}

			} else {
				return "" + new Long(-26);
			}
		} catch (Exception err) {
			log.error("setUserObjectWithAndGenerateRoomHash", err);
		}
		return "" + new Long(-1);
	}

	/**
	 * 
	 * Add a user to a certain organization
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param userId
	 *            the user id
	 * @param organisationId
	 *            the organization id
	 * @param insertedby
	 *            user id of the operating user
	 * @return - id of the user added, or error id in case of the error
	 */
	public Long addUserToOrganisation(String SID, Long userId,
			Long organisationId, Long insertedby) {
		try {
			Long authUserId = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(authUserId))) {
				if (!orgUserDao.isUserInOrganization(organisationId, userId)) {
					User u = userDao.get(userId);
					u.getOrganisationUsers().add(new OrganisationUser(orgDao.get(organisationId)));
					userDao.update(u, authUserId);
				}
				return userId;
			} else {
				return new Long(-26);
			}
		} catch (Exception err) {
			log.error("addUserToOrganisation", err);
		}
		return new Long(-1);
	}

	/**
	 * Search users and return them
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param organisationId
	 *            the organization id
	 * @param start
	 *            first record
	 * @param max
	 *            max records
	 * @param orderby
	 *            orderby clause
	 * @param asc
	 *            asc or desc
	 * @return - users found
	 */
	public UserSearchResult getUsersByOrganisation(String SID,
			long organisationId, int start, int max, String orderby,
			boolean asc) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			SearchResult<User> result = new SearchResult<User>();
			result.setObjectName(User.class.getName());
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
				result.setRecords(orgUserDao.count(organisationId));
				result.setResult(new ArrayList<User>());
				for (OrganisationUser ou : orgUserDao.get(organisationId, null, start, max, orderby + " " + (asc ? "ASC" : "DESC"))) {
					result.getResult().add(ou.getUser());
				}
			} else {
				log.error("Need Administration Account");
				result.setErrorId(-26L);
			}
			return new UserSearchResult(result);
		} catch (Exception err) {
			log.error("getUsersByOrganisation", err);
		}
		return null;
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
	public Boolean kickUserByPublicSID(String SID, String publicSID) {
		try {
			Boolean success = false;

			success = userManagement.kickUserByPublicSID(SID, publicSID);

			if (success == null)
				success = false;

			return success;
		} catch (Exception err) {
			log.error("[kickUser]", err);
		}
		return null;
	}
	
	/**
	 * add a new organisation
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param name
	 *            the name of the org
	 * @return the new id of the org or -1 in case an error happened
	 * @throws ServiceException
	 */
	public Long addOrganisation(String SID, String name) throws ServiceException {
		Long userId = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {
			Organisation o = new Organisation();
			o.setName(name);
			return orgDao.update(o, userId).getId();
		}
		log.error("Could not create organization");
		return -1L;
	}

}
