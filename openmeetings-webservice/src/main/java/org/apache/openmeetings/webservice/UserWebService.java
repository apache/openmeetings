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
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.ServiceResult;
import org.apache.openmeetings.db.dto.basic.ServiceResult.Type;
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
	private ConfigurationDao configurationDao;
	@Autowired
	private IUserManager userManagement;
	@Autowired
	private SOAPLoginDao soapLoginDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SessiondataDao sessionDao;

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
	public Long add(
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

				String jName_timeZone = configurationDao.getConfValue("default.timezone", String.class, "");
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
					return userId;
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

				userDao.update(u, authUserId);

				return userId;

			} else {
				return new Long(-26);
			}
		} catch (ServiceException err) {
			throw err;
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
			Long userId = sessionDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessionDao.updateUserRemoteSession(SID, xmlString);

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
			Long userId = sessionDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessionDao.updateUserRemoteSession(SID, xmlString);

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
			Long userId = sessionDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessionDao.updateUserRemoteSession(SID, xmlString);

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
			Long userId = sessionDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessionDao.updateUserRemoteSession(SID, xmlString);

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
			Long userId = sessionDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug(remoteSessionObject.toString());
				log.debug("showNickNameDialogAsInt" + showNickNameDialogAsInt);

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessionDao.updateUserRemoteSession(SID, xmlString);

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
			Long userId = sessionDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(userId))) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, "", "", "", externalUserId,
						externalUserType);

				log.debug(remoteSessionObject.toString());

				String xmlString = remoteSessionObject.toXml();

				log.debug("xmlString " + xmlString);

				sessionDao.updateUserRemoteSession(SID, xmlString);

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
			Boolean success = userManagement.kickUserByPublicSID(SID, publicSID);

			if (success == null) {
				success = false;
			}

			return success;
		} catch (Exception err) {
			log.error("[kickUser]", err);
		}
		return null;
	}
}
