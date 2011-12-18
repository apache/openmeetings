package org.openmeetings.axis.services;

import java.util.Date;

import org.apache.axis2.AxisFault;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.SOAPLoginDaoImpl;
import org.openmeetings.app.data.beans.basic.ErrorResult;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.persistence.beans.basic.ErrorValues;
import org.openmeetings.app.persistence.beans.basic.RemoteSessionObject;
import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
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
			UserWebService.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private ErrorManagement errorManagement;
	@Autowired
	private Organisationmanagement organisationmanagement;
	@Autowired
	private SOAPLoginDaoImpl soapLoginDao;
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private MainService mainService;
	@Autowired
	private AuthLevelmanagement authLevelManagement;

	/**
	 * load this session id before doing anything else Returns an Object of Type
	 * Sessiondata, this contains a sessionId, use that sessionId in all Methods
	 * 
	 * @return
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
	 * @return
	 */
	public Long loginUser(String SID, String username, String userpass)
			throws AxisFault {
		try {
			Object obj = userManagement.loginUser(SID, username, userpass,
					null, false);
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
	 * @return
	 */
	public ErrorResult getErrorByCode(String SID, Long errorid, Long language_id) {
		try {
			if (errorid < 0) {
				ErrorValues eValues = errorManagement
						.getErrorValuesById(errorid * (-1));
				if (eValues != null) {
					Fieldlanguagesvalues errorValue = fieldmanagment
							.getFieldByIdAndLanguage(
									eValues.getFieldvalues_id(), language_id);
					Fieldlanguagesvalues typeValue = fieldmanagment
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
	 * @return
	 * @throws AxisFault
	 */
	public Long addNewUser(String SID, String username, String userpass,
			String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id, String baseURL)
			throws AxisFault {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkWebServiceLevel(user_level)) {

				Configuration conf = cfgManagement.getConfKey(3L,
						"default.timezone");
				String jName_timeZone = "";

				if (conf != null) {
					jName_timeZone = conf.getConf_value();
				}

				Long user_id = userManagement.registerUser(username, userpass,
						lastname, firstname, email, new Date(), street,
						additionalname, fax, zip, states_id, town, language_id,
						"", baseURL, true, // generate
											// SIP
											// Data
											// if
											// the
											// config
											// is
											// enabled
						jName_timeZone);

				if (user_id < 0) {
					return user_id;
				}

				Users user = userManagement.getUserById(user_id);

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
	 * @return
	 * @throws AxisFault
	 */
	public Long addNewUserWithTimeZone(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id, String baseURL,
			String jNameTimeZone) throws AxisFault {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkWebServiceLevel(user_level)) {

				Long user_id = userManagement.registerUser(username, userpass,
						lastname, firstname, email, new Date(), street,
						additionalname, fax, zip, states_id, town, language_id,
						"", baseURL, true, // generate
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

				Users user = userManagement.getUserById(user_id);

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
	 * @return
	 * @throws AxisFault
	 */
	public Long addNewUserWithExternalType(String SID, String username,
			String userpass, String lastname, String firstname, String email,
			String additionalname, String street, String zip, String fax,
			long states_id, String town, long language_id,
			String jNameTimeZone, Long externalUserId, String externalUserType)
			throws AxisFault {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkAdminLevel(user_level)) {

				Users testUser = userManagement.getUserByExternalIdAndType(
						externalUserId, externalUserType);

				if (testUser != null) {
					throw new Exception("User does already exist!");
				}

				// This will send no email to the users
				Long user_id = userManagement.registerUserNoEmail(username,
						userpass, lastname, firstname, email, new Date(),
						street, additionalname, fax, zip, states_id, town,
						language_id, "", true, // generate SIP Data if
												// the config is enabled
						jNameTimeZone);

				if (user_id < 0) {
					return user_id;
				}

				Users user = userManagement.getUserById(user_id);

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
	 * @return
	 * @throws AxisFault
	 */
	public Long deleteUserById(String SID, Long userId) throws AxisFault {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkAdminLevel(user_level)) {

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
	 * @return
	 * @throws AxisFault
	 */
	public Long deleteUserByExternalUserIdAndType(String SID,
			Long externalUserId, String externalUserType) throws AxisFault {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			if (authLevelManagement.checkAdminLevel(user_level)) {

				Users userExternal = userManagement.getUserByExternalIdAndType(
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
	 * @return
	 * @throws AxisFault
	 */
	@Deprecated
	public Long setUserObject(String SID, String username, String firstname,
			String lastname, String profilePictureUrl, String email)
			throws AxisFault {
		log.debug("UserService.setUserObject");

		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

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

				sessionManagement.updateUserRemoteSession(SID, xmlString);

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
	 * @return
	 * @throws AxisFault
	 */
	@Deprecated
	public Long setUserObjectWithExternalUser(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType)
			throws AxisFault {
		log.debug("UserService.setUserObject");

		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

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

				sessionManagement.updateUserRemoteSession(SID, xmlString);

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
	 * @return
	 * @throws AxisFault
	 */
	public String setUserObjectAndGenerateRoomHash(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType,
			Long room_id, int becomeModeratorAsInt, int showAudioVideoTestAsInt)
			throws AxisFault {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

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

				sessionManagement.updateUserRemoteSession(SID, xmlString);

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
	 * @return
	 */
	public String setUserObjectAndGenerateRoomHashByURL(String SID,
			String username, String firstname, String lastname,
			String profilePictureUrl, String email, Long externalUserId,
			String externalUserType, Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt) {
		log.debug("UserService.setUserObject");

		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

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

				sessionManagement.updateUserRemoteSession(SID, xmlString);

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
	 * @return
	 */
	public String setUserObjectAndGenerateRoomHashByURLAndRecFlag(String SID,
			String username, String firstname, String lastname,
			String profilePictureUrl, String email, Long externalUserId,
			String externalUserType, Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int allowRecording) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

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

				sessionManagement.updateUserRemoteSession(SID, xmlString);

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
	 * @return
	 */
	public String setUserObjectMainLandingZone(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType) {
		log.debug("UserService.setUserObjectMainLandingZone");

		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

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

				sessionManagement.updateUserRemoteSession(SID, xmlString);

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
	 * @return
	 */
	public String setUserAndNickName(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType,
			Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int showNickNameDialogAsInt) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

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

				sessionManagement.updateUserRemoteSession(SID, xmlString);

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
	 * @return
	 */
	public String setUserObjectAndGenerateRecordingHashByURL(String SID,
			String username, String firstname, String lastname,
			Long externalUserId, String externalUserType, Long recording_id) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

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

				sessionManagement.updateUserRemoteSession(SID, xmlString);

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
	 * @param comment
	 *            any comment
	 * @return
	 */
	public Long addUserToOrganisation(String SID, Long user_id,
			Long organisation_id, Long insertedby, String comment) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {

				return organisationmanagement.addUserToOrganisation(user_id,
						organisation_id, users_id, comment);

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
	 * @return
	 */
	public SearchResult getUsersByOrganisation(String SID,
			long organisation_id, int start, int max, String orderby,
			boolean asc) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (authLevelManagement.checkWebServiceLevel(user_level)) {
				return organisationmanagement
						.getUsersSearchResultByOrganisationId(organisation_id,
								start, max, orderby, asc);
			} else {
				log.error("Need Administration Account");
				SearchResult sResult = new SearchResult();
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
	 * @return
	 */
	public Boolean kickUserByPublicSID(String SID, String publicSID) {
		try {
			Boolean salida = false;

			salida = userManagement.kickUserByPublicSID(SID, publicSID);

			if (salida == null)
				salida = false;

			return salida;
		} catch (Exception err) {
			log.error("[kickUser]", err);
		}
		return null;
	}

}
