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
package org.apache.openmeetings.axis.services;

import java.util.Date;

import org.apache.axis2.AxisFault;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.ErrorDao;
import org.apache.openmeetings.data.basic.dao.SOAPLoginDao;
import org.apache.openmeetings.data.beans.basic.ErrorResult;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.ErrorValue;
import org.apache.openmeetings.persistence.beans.basic.RemoteSessionObject;
import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.remote.MainService;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * The Service contains methods to login and create hash to directly enter
 * conference rooms, recordings or the application in general
 * 
 * @author sebawagner
 * @webservice UserService
 * 
 */
public class UserWebService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UserWebService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserManager userManagement;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private ErrorDao errorManagement;
	@Autowired
	private OrganisationManager organisationManager;
	@Autowired
	private SOAPLoginDao soapLoginDao;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private MainService mainService;
	@Autowired
	private AuthLevelUtil authLevelUtil;

	/**
	 * load this session id before doing anything else Returns an Object of Type
	 * Sessiondata, this contains a sessionId, use that sessionId in all Methods
	 * 
	 * @return - creates new session
	 */
	public Sessiondata getSession() {
		log.debug("SPRING LOADED getSession -- ");
		return mainService.getsessiondata();
	}

	/**
	 * uth function, use the SID you get by getSession, return positive means
	 * logged-in, if negative its an ErrorCode, you have to invoke the Method
	 * getErrorByCode to get the Text-Description of that ErrorCode
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param username
	 *            Username from OpenMeetings, the user has to have Admin-rights
	 * @param userpass
	 *            Userpass from OpenMeetings
	 *            
	 * @return - id of the logged in user, -1 in case of the error
	 */
	public Long loginUser(String SID, String username, String userpass)
			throws AxisFault {
		try {
			Object obj = userManagement.loginUser(SID, username, userpass,
					null, null, false);
			if (obj == null) {
				return new Long(-1);
			}
			String objName = obj.getClass().getName();
			if (objName.equals("java.lang.Long")) {
				return (Long) obj;
			} else {
				return new Long(1);
			}
		} catch (Exception err) {
			log.error("[loginUser]", err);
		}
		return new Long(-1);
	}

	/**
	 * loads an Error-Object. If a Method returns a negative Result, its an
	 * Error-id, it needs a language_id to specify in which language you want to
	 * display/read the error-message. English has the Language-ID one, for
	 * different one see the list of languages
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param errorid
	 *            the error id (negative Value here!)
	 * @param language_id
	 *            The id of the language
	 *            
	 * @return - error with the code given
	 */
	public ErrorResult getErrorByCode(String SID, long errorid, long language_id) {
		try {
			if (errorid < 0) {
				ErrorValue eValues = errorManagement
						.getErrorValuesById(errorid * (-1));
				if (eValues != null) {
					Fieldlanguagesvalues errorValue = fieldManager
							.getFieldByIdAndLanguage(
									eValues.getFieldvalues_id(), language_id);
					Fieldlanguagesvalues typeValue = fieldManager
							.getFieldByIdAndLanguage(errorManagement
									.getErrorType(eValues.getErrortype_id())
									.getFieldvalues_id(), language_id);
					if (errorValue != null) {
						return new ErrorResult(errorid, errorValue.getValue(),
								typeValue.getValue());
					}
				}
			} else {
				return new ErrorResult(errorid,
						"Error ... please check your input", "Error");
			}
		} catch (Exception err) {
			log.error("[getErrorByCode] ", err);
		}
		return null;
	}

	/**
	 * Adds a new Usre like through the Frontend, but also does activates the
	 * Account To do SSO see the methods to create a hash and use those ones!
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
	 * @param states_id
	 *            a valid states_id
	 * @param town
	 *            any town
	 * @param language_id
	 *            the language_id
	 * @param baseURL
	 *            the baseURL is needed to send the Initial Email correctly to
	 *            that User, otherwise the Link in the EMail that the new User
	 *            will reveive is not valid
	 *            
	 * @return - id of the user added or error code
	 * @throws AxisFault
	 */
	public Long addNewUser(String SID, String username, String userpass,
			String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id, String baseURL)
			throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				String jName_timeZone = configurationDao.getConfValue("default.timezone", String.class, "");

				Long user_id = userManagement.registerUser(username, userpass,
						lastname, firstname, email, new Date(), street,
						additionalname, fax, zip, states_id, town, language_id,
						"", false, baseURL, true, // generate SIP Data if the config is enabled
						jName_timeZone);

				if (user_id == null || user_id < 0) {
					return user_id;
				}

				User user = userManagement.getUserById(user_id);

				// activate the User
				user.setStatus(1);
				user.setUpdatetime(new Date());

				userManagement.updateUser(user);

				return user_id;

			} else {
				return new Long(-26);
			}
		} catch (Exception err) {
			log.error("setUserObject", err);
			throw new AxisFault(err.getMessage());
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
	 * @param states_id
	 *            a valid states_id
	 * @param town
	 *            any town
	 * @param language_id
	 *            the language_id
	 * @param baseURL
	 *            the baseURL is needed to send the Initial Email correctly to
	 *            that User, otherwise the Link in the EMail that the new User
	 *            will reveive is not valid
	 * @param jNameTimeZone
	 *            the name of the timezone for the user
	 *            
	 * @return - id of the user added or the error code
	 * @throws AxisFault
	 */
	public Long addNewUserWithTimeZone(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id, String baseURL,
			String jNameTimeZone) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				Long user_id = userManagement.registerUser(username, userpass,
						lastname, firstname, email, new Date(), street,
						additionalname, fax, zip, states_id, town, language_id,
						"", false, baseURL, true, // generate
											// SIP
											// Data
											// if
											// the
											// config
											// is
											// enabled
						jNameTimeZone); 

				if (user_id < 0) {
					return user_id;
				}

				User user = userManagement.getUserById(user_id);

				// activate the User
				user.setStatus(1);
				user.setUpdatetime(new Date());

				userManagement.updateUser(user);

				return user_id;

			} else {
				return new Long(-26);
			}
		} catch (Exception err) {
			log.error("setUserObject", err);
			throw new AxisFault(err.getMessage());
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
	 * @param states_id
	 *            a valid states_id
	 * @param town
	 *            any town
	 * @param language_id
	 *            the language_id
	 * @param jNameTimeZone
	 *            the name of the timezone for the user
	 * @param externalUserId
	 *            externalUserId
	 * @param externalUserType
	 *            externalUserType
	 *            
	 * @return - id of user added or error code
	 * @throws AxisFault
	 */
	public Long addNewUserWithExternalType(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id,
			String jNameTimeZone, String externalUserId, String externalUserType)
			throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelUtil.checkAdminLevel(user_level)) {

				User testUser = userManagement.getUserByExternalIdAndType(
						externalUserId, externalUserType);

				if (testUser != null) {
					throw new Exception("User does already exist!");
				}

				// This will send no email to the users
				Long user_id = userManagement.registerUserNoEmail(username,
						userpass, lastname, firstname, email, new Date(),
						street, additionalname, fax, zip, states_id, town,
						language_id, "", false, true, // generate SIP Data if the config is enabled
						jNameTimeZone);

				if (user_id < 0) {
					return user_id;
				}

				User user = userManagement.getUserById(user_id);

				// activate the User
				user.setStatus(1);
				user.setUpdatetime(new Date());
				user.setExternalUserId(externalUserId);
				user.setExternalUserType(externalUserType);

				userManagement.updateUser(user);

				return user_id;

			} else {
				return new Long(-26);
			}

		} catch (Exception err) {
			log.error("addNewUserWithExternalType", err);
			throw new AxisFault(err.getMessage());
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
	 * @throws AxisFault
	 */
	public Long deleteUserById(String SID, Long userId) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelUtil.checkAdminLevel(user_level)) {

				// Setting user deleted
				usersDao.deleteUserID(userId);

				return userId;

			} else {
				return new Long(-26);
			}

		} catch (Exception err) {
			log.error("deleteUserById", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * 
	 * Delete a certain user by its external user id
	 * 
	 * @param SID
	 *            The SID from getSession
	 * @param externalUserId
	 *            externalUserId
	 * @param externalUserType
	 *            externalUserId
	 *            
	 * @return - id of user deleted, or error code
	 * @throws AxisFault
	 */
	public Long deleteUserByExternalUserIdAndType(String SID,
			String externalUserId, String externalUserType) throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelUtil.checkAdminLevel(user_level)) {

				User userExternal = userManagement.getUserByExternalIdAndType(
						externalUserId, externalUserType);

				Long userId = userExternal.getUser_id();

				// Setting user deleted
				usersDao.deleteUserID(userId);

				return userId;

			} else {
				return new Long(-26);
			}

		} catch (Exception err) {
			log.error("deleteUserById", err);
			throw new AxisFault(err.getMessage());
		}
	}

	/**
	 * deprecated use setUserObjectAndGenerateRoomHash
	 * 
	 * Description: sets the SessionObject for a certain SID, after setting this
	 * Session-Object you can use the SID + a RoomId to enter any Room.
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
	 *            
	 * @return - 1 in case of success, -1 otherwise
	 * @throws AxisFault
	 */
	@Deprecated
	public Long setUserObject(String SID, String username, String firstname,
			String lastname, String profilePictureUrl, String email)
			throws AxisFault {
		log.debug("UserService.setUserObject");

		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl, email);

				log.debug("username " + username);
				log.debug("firstname " + firstname);
				log.debug("lastname " + lastname);
				log.debug("profilePictureUrl " + profilePictureUrl);
				log.debug("email " + email);

				// XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				return new Long(1);
			} else {
				return new Long(-26);
			}
		} catch (Exception err) {
			log.error("setUserObject", err);
			throw new AxisFault(err.getMessage());
		}
		// return new Long(-1);
	}

	/**
	 * deprecated use setUserObjectAndGenerateRoomHash
	 * 
	 * Description: sets the SessionObject for a certain SID, after setting this
	 * Session-Object you can use the SID + a RoomId to enter any Room.
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
	 *            
	 * @return - 1 in case of success, -1 otherwise
	 * @throws AxisFault
	 */
	@Deprecated
	public Long setUserObjectWithExternalUser(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, String externalUserId, String externalUserType)
			throws AxisFault {
		log.debug("UserService.setUserObject");

		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug("username " + username);
				log.debug("firstname " + firstname);
				log.debug("lastname " + lastname);
				log.debug("profilePictureUrl " + profilePictureUrl);
				log.debug("email " + email);
				log.debug("externalUserId " + externalUserId);
				log.debug("externalUserType " + externalUserType);

				// XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				return new Long(1);
			} else {
				return new Long(-26);
			}
		} catch (Exception err) {
			log.error("setUserObjectWithExternalUser", err);
			throw new AxisFault(err.getMessage());
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
	 * @param room_id
	 *            the room id the user should be logged in
	 * @param becomeModeratorAsInt
	 *            0 means no Moderator, 1 means Moderator
	 * @param showAudioVideoTestAsInt
	 *            0 means don't show Audio/Video Test, 1 means show Audio/Video
	 *            Test Application before the user is logged into the room
	 *            
	 * @return - secure hash or error code
	 * @throws AxisFault
	 */
	public String setUserObjectAndGenerateRoomHash(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, String externalUserId, String externalUserType,
			Long room_id, int becomeModeratorAsInt, int showAudioVideoTestAsInt)
			throws AxisFault {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug("username " + username);
				log.debug("firstname " + firstname);
				log.debug("lastname " + lastname);
				log.debug("profilePictureUrl " + profilePictureUrl);
				log.debug("email " + email);
				log.debug("externalUserId " + externalUserId);
				log.debug("externalUserType " + externalUserType);

				// XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);

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

				String hash = soapLoginDao.addSOAPLogin(SID, room_id,
						becomeModerator, showAudioVideoTest, false, // allowSameURLMultipleTimes
						null, // recording_id
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
			throw new AxisFault(err.getMessage());
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
	 * @param room_id
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
			String externalUserType, Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt) {
		log.debug("UserService.setUserObject");

		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug("username " + username);
				log.debug("firstname " + firstname);
				log.debug("lastname " + lastname);
				log.debug("profilePictureUrl " + profilePictureUrl);
				log.debug("email " + email);
				log.debug("externalUserId " + externalUserId);
				log.debug("externalUserType " + externalUserType);

				// XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);

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

				String hash = soapLoginDao.addSOAPLogin(SID, room_id,
						becomeModerator, showAudioVideoTest, true, // allowSameURLMultipleTimes
						null, // recording_id
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
	 * @param room_id
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
			String externalUserType, Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int allowRecording) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug("username " + username);
				log.debug("firstname " + firstname);
				log.debug("lastname " + lastname);
				log.debug("profilePictureUrl " + profilePictureUrl);
				log.debug("email " + email);
				log.debug("externalUserId " + externalUserId);
				log.debug("externalUserType " + externalUserType);
				log.debug("allowRecording " + allowRecording);

				// XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);

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

				String hash = soapLoginDao.addSOAPLogin(SID, room_id,
						becomeModerator, showAudioVideoTest, true, // allowSameURLMultipleTimes
						null, // recording_id
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
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug("username " + username);
				log.debug("firstname " + firstname);
				log.debug("lastname " + lastname);
				log.debug("profilePictureUrl " + profilePictureUrl);
				log.debug("email " + email);
				log.debug("externalUserId " + externalUserId);
				log.debug("externalUserType " + externalUserType);

				// XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				String hash = soapLoginDao.addSOAPLogin(SID, null, false, true,
						true, // allowSameURLMultipleTimes
						null, // recording_id
						false, // showNickNameDialogAsInt
						"dashboard", // LandingZone,
						true // allowRecording
						);

				if (hash != null) {
					return hash;
				}

			} else {

				log.debug("Invalid access via SOAP " + SID + " UserD"
						+ users_id + " " + user_level);

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
	 * @param room_id
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
			Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int showNickNameDialogAsInt) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, lastname, profilePictureUrl,
						email, externalUserId, externalUserType);

				log.debug("username " + username);
				log.debug("firstname " + firstname);
				log.debug("lastname " + lastname);
				log.debug("profilePictureUrl " + profilePictureUrl);
				log.debug("email " + email);
				log.debug("externalUserId " + externalUserId);
				log.debug("externalUserType " + externalUserType);
				log.debug("showNickNameDialogAsInt" + showNickNameDialogAsInt);

				// XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);

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

				String hash = soapLoginDao.addSOAPLogin(SID, room_id,
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
	 * @param recording_id
	 *            the id of the recording, get a List of all Recordings with
	 *            RoomService::getFlvRecordingByExternalRoomType
	 *            
	 * @return - hash of the recording, or error id
	 */
	public String setUserObjectAndGenerateRecordingHashByURL(String SID,
			String username, String firstname, String lastname,
			String externalUserId, String externalUserType, Long recording_id) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				RemoteSessionObject remoteSessionObject = new RemoteSessionObject(
						username, firstname, "", "", "", externalUserId,
						externalUserType);

				log.debug("username " + username);
				log.debug("firstname " + firstname);
				log.debug("lastname " + lastname);
				log.debug("profilePictureUrl " + "");
				log.debug("email " + "");
				log.debug("externalUserId " + externalUserId);
				log.debug("externalUserType " + externalUserType);

				// XStream xStream = new XStream(new XppDriver());
				XStream xStream = new XStream(new DomDriver("UTF-8"));
				xStream.setMode(XStream.NO_REFERENCES);
				String xmlString = xStream.toXML(remoteSessionObject);

				log.debug("xmlString " + xmlString);

				sessiondataDao.updateUserRemoteSession(SID, xmlString);

				String hash = soapLoginDao.addSOAPLogin(SID, null, false,
						false, true, // allowSameURLMultipleTimes
						recording_id, // recording_id
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
	 * @param user_id
	 *            the user id
	 * @param organisation_id
	 *            the organization id
	 * @param insertedby
	 *            user id of the operating user
	 * @return - id of the user added, or error id in case of the error
	 */
	public Long addUserToOrganisation(String SID, Long user_id,
			Long organisation_id, Long insertedby) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

				return organisationManager.addUserToOrganisation(user_id,
						organisation_id, users_id);

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
	 * @param organisation_id
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
	public SearchResult<User> getUsersByOrganisation(String SID,
			long organisation_id, int start, int max, String orderby,
			boolean asc) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(user_level)) {
				return organisationManager
						.getUsersSearchResultByOrganisationId(organisation_id,
								start, max, orderby, asc);
			} else {
				log.error("Need Administration Account");
				SearchResult<User> sResult = new SearchResult<User>();
				sResult.setErrorId(-26L);
				return sResult;
			}
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
	 * @throws AxisFault
	 */
	public Long addOrganisation(String SID, String name) throws AxisFault {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		if (authLevelUtil.checkWebServiceLevel(user_level)) {
			return organisationManager.addOrganisation(name, users_id);
		}
		log.error("Could not create organization");
		return -1L;
	}

}
