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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmeetings.axis.services.IUserWebService#getSession()
	 */
	public Sessiondata getSession() {
		log.debug("SPRING LOADED getSession -- ");
		return mainService.getsessiondata();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#loginUser(java.lang.String
	 * , java.lang.String, java.lang.String)
	 */
	public Long loginUser(String SID, String username, String userpass) {
		log.debug("UserService.loginuser");
		try {
			Object obj = userManagement.loginUser(SID, username, userpass,
					null, false);
			if (obj == null) {
				return new Long(-1);
			}
			String objName = obj.getClass().getName();
			log.debug("objName: " + objName);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#getErrorByCode(java.lang
	 * .String, java.lang.Long, java.lang.Long)
	 */
	public ErrorResult getErrorByCode(String SID, Long errorid, Long language_id) {
		log.debug("UserService.getErrorbyCode");
		try {
			if (errorid < 0) {
				ErrorValues eValues = errorManagement
						.getErrorValuesById(errorid * (-1));
				if (eValues != null) {
					Fieldlanguagesvalues errorValue = fieldmanagment
							.getFieldByIdAndLanguage(
									eValues.getFieldvalues_id(), language_id);
					Fieldlanguagesvalues typeValue = fieldmanagment
							.getFieldByIdAndLanguage(errorManagement.getErrorType(eValues.getErrortype_id())
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#addNewUser(java.lang.String
	 * , java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, long, java.lang.String, long, java.lang.String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#addNewUserWithTimeZone
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, long, java.lang.String, long,
	 * java.lang.String, java.lang.String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#addNewUserWithExternalType
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, long, java.lang.String, long,
	 * java.lang.String, java.lang.Long, java.lang.String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#deleteUserById(java.lang
	 * .String, java.lang.Long)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmeetings.axis.services.IUserWebService#
	 * deleteUserByExternalUserIdAndType(java.lang.String, java.lang.Long,
	 * java.lang.String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#setUserObject(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#setUserObjectWithExternalUser
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Long, java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmeetings.axis.services.IUserWebService#
	 * setUserObjectAndGenerateRoomHash(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.String, java.lang.Long, int, int)
	 */
	public String setUserObjectAndGenerateRoomHash(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType,
			Long room_id, int becomeModeratorAsInt, int showAudioVideoTestAsInt)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmeetings.axis.services.IUserWebService#
	 * setUserObjectAndGenerateRoomHashByURL(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Long, java.lang.String, java.lang.Long, int, int)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmeetings.axis.services.IUserWebService#
	 * setUserObjectAndGenerateRoomHashByURLAndRecFlag(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.Long, java.lang.String, java.lang.Long, int,
	 * int, int)
	 */
	public String setUserObjectAndGenerateRoomHashByURLAndRecFlag(String SID,
			String username, String firstname, String lastname,
			String profilePictureUrl, String email, Long externalUserId,
			String externalUserType, Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int allowRecording) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#setUserObjectMainLandingZone
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Long, java.lang.String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#setUserAndNickName(java
	 * .lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Long, java.lang.String,
	 * java.lang.Long, int, int, int)
	 */
	public String setUserAndNickName(String SID, String username,
			String firstname, String lastname, String profilePictureUrl,
			String email, Long externalUserId, String externalUserType,
			Long room_id, int becomeModeratorAsInt,
			int showAudioVideoTestAsInt, int showNickNameDialogAsInt) {
		try {

			log.debug("UserService.setUserObjectAndGenerateRoomHashByURLAndNick");

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmeetings.axis.services.IUserWebService#
	 * setUserObjectAndGenerateRecordingHashByURL(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.Long,
	 * java.lang.String, java.lang.Long)
	 */
	public String setUserObjectAndGenerateRecordingHashByURL(String SID,
			String username, String firstname, String lastname,
			Long externalUserId, String externalUserType, Long recording_id) {
		log.debug("UserService.setUserObject");

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#addUserToOrganisation(
	 * java.lang.String, java.lang.Long, java.lang.Long, java.lang.Long,
	 * java.lang.String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#getUsersByOrganisation
	 * (java.lang.String, long, int, int, java.lang.String, boolean)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmeetings.axis.services.IUserWebService#kickUserByPublicSID(java
	 * .lang.String, java.lang.String)
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
