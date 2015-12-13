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
package org.apache.openmeetings.remote;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.NaviBuilder;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.LdapConfigDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.basic.dao.SOAPLoginDao;
import org.apache.openmeetings.data.conference.FeedbackManager;
import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.logs.ConferenceLogDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.ldap.LdapLoginManagement;
import org.apache.openmeetings.persistence.beans.basic.Configuration;
import org.apache.openmeetings.persistence.beans.basic.LdapConfig;
import org.apache.openmeetings.persistence.beans.basic.Naviglobal;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.basic.RemoteSessionObject;
import org.apache.openmeetings.persistence.beans.basic.SOAPLogin;
import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.persistence.beans.user.State;
import org.apache.openmeetings.persistence.beans.user.Userdata;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.rss.LoadAtomRssFeed;
import org.apache.openmeetings.session.ISessionManager;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author swagner
 * 
 */
public class MainService implements IPendingServiceCallback {

	private static final Logger log = Red5LoggerFactory.getLogger(MainService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private StateDao statemanagement;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private NaviBuilder navimanagement;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private ConferenceLogDao conferenceLogDao;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private SOAPLoginDao soapLoginDao;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private FeedbackManager feedbackManager;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private LoadAtomRssFeed loadAtomRssFeed;
	@Autowired
	private LdapLoginManagement ldapLoginManagement;

	// External User Types
	public static final String EXTERNAL_USER_TYPE_LDAP = "LDAP";

	/**
	 * get Navigation
	 * 
	 * @param SID
	 * @param language_id
	 * @return - list of global navigation menus
	 */
	public List<Naviglobal> getNavi(String SID, long language_id, Long organisation_id) {
		try {
			Long user_id = sessiondataDao.checkSession(SID);
			// log.error("getNavi 1: "+users_id);
			Long user_level = userManager.getUserLevelByIdAndOrg(user_id,
					organisation_id);
			// log.error("getNavi 2: "+user_level);
			return navimanagement.getMainMenu(user_level, user_id, language_id);
		} catch (Exception err) {
			log.error("[getNavi] ", err);
		}
		return null;
	}

	/**
	 * gets a user by its SID
	 * 
	 * @param SID
	 * @param USER_ID
	 * @return - user with SID given
	 */
	public User getUser(String SID, int USER_ID) {
		User users = new User();
		Long users_id = sessiondataDao.checkSession(SID);
		long user_level = userManager.getUserLevelByID(users_id);
		if (user_level > 2) {
			users = usersDao.get(new Long(USER_ID));
		} else {
			users.setFirstname("No rights to do this");
		}
		return users;
	}

	public Client getCurrentRoomClient(String SID) {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug("getCurrentRoomClient -1- " + SID);
			log.debug("getCurrentRoomClient -2- " + streamid);

			Client currentClient = this.sessionManager
					.getClientByStreamId(streamid, null);
			return currentClient;
		} catch (Exception err) {
			log.error("[getCurrentRoomClient]", err);
		}
		return null;
	}

	/**
	 * This Method is just for testing you can find the corresponding Client
	 * Function in xmlcrm/auth/checkLoginData.lzx
	 * 
	 * @param myObject2
	 * @return - the size of the map passed, or -1 in case of error
	 */
	public int testObject(Object myObject2) {
		try {
			@SuppressWarnings("rawtypes")
			LinkedHashMap myObject = (LinkedHashMap) myObject2;
			log.debug("testObject " + myObject.size());
			log.debug("testObject " + myObject.get(1));
			log.debug("testObject " + myObject.get("stringObj"));
			return myObject.size();
		} catch (Exception e) {
			log.error("ex: ", e);
		}
		return -1;
	}

	/**
	 * load this session id before doing anything else
	 * 
	 * @return a unique session identifier
	 */
	public Sessiondata getsessiondata() {
		return sessiondataDao.startsession();
	}

	public Long setCurrentUserOrganization(String SID, Long organization_id) {
		try {
			sessiondataDao.updateUserOrg(SID, organization_id);
			return 1L;
		} catch (Exception err) {
			log.error("[setCurrentUserOrganization]", err);
		}
		return -1L;
	}

	public User loginByRemember(String SID, String remoteHashId) {
		try {

			Client currentClient;
			IConnection current = Red5.getConnectionLocal();

			User o = null;

			currentClient = sessionManager.getClientByStreamId(current
					.getClient().getId(), null);

			o = userManager.loginUserByRemoteHash(SID, remoteHashId);

			if (o == null)
				return null;

			if (o.getOrganisation_users().isEmpty()) {
				throw new Exception("Users has no organization assigned");
			}

			o.setSessionData(sessiondataDao.getSessionByHash(remoteHashId));
			currentClient.setUser_id(o.getUser_id());
			SessionVariablesUtil.setUserId(current.getClient(), o.getUser_id());
			
			if (currentClient.getUser_id() != null
					&& currentClient.getUser_id() > 0) {

				currentClient.setFirstname(o.getFirstname());
				currentClient.setLastname(o.getLastname());
				
				scopeApplicationAdapter.syncMessageToCurrentScope("roomConnect", currentClient, false);

			}

			return o;

		} catch (Exception err) {
			log.error("[loginByRemember]", err);
		}
		return null;
	}

	public Object webLoginUser(String SID, String usernameOrEmail,
			String Userpass, Boolean storePermanent, Long language_id,
			Long ldapConfigId) {

		Object returnValue = this.loginUser(SID, usernameOrEmail, Userpass,
				storePermanent, language_id, ldapConfigId);

		if (returnValue instanceof Long) {
			return returnValue;
		} else if (returnValue instanceof User) {
			User us = (User) returnValue;
			if (authLevelUtil.checkUserLevel(
					us.getLevel_id())) {
				return us;
			} else {
				return -52L;
			}
		}

		return returnValue;

	}

	/**
	 * auth function, use the SID you get by getsessiondata
	 * 
	 * @param SID
	 * @param usernameOrEmail
	 * @param Userpass
	 * @param storePermanent
	 * @param language_id
	 * @param ldapConfigId
	 * @return a valid user account or an empty user with an error message and
	 *         level -1
	 */
	public Object loginUser(String SID, String usernameOrEmail,
			String Userpass, Boolean storePermanent, Long language_id,
			Long ldapConfigId) {

		boolean withLdap = false;

		if (ldapConfigId > 0) {
			withLdap = true;
		}

		try {
			log.warn("loginUser: " + SID + " " + usernameOrEmail);

			Client currentClient;
			IConnection current = Red5.getConnectionLocal();

			if (current == null)
				return null;
			
			Object o;

			if (withLdap) {
				log.debug("Ldap Login");

				currentClient = sessionManager
						.getClientByStreamId(current.getClient().getId(), null);

				// LDAP Loggedin Users cannot use the permanent Login Flag

				LdapConfig ldapConfig = ldapConfigDao.get(ldapConfigId);

				String ldapLogin = usernameOrEmail;
				if (ldapConfig.getAddDomainToUserName() != null
						&& ldapConfig.getAddDomainToUserName()) {
					ldapLogin = usernameOrEmail + "@" + ldapConfig.getDomain();
				}

				o = ldapLoginManagement.doLdapLogin(ldapLogin,
						Userpass, currentClient, current.getClient(), SID,
						ldapConfig.getConfigFileName());
			} else {

				currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);

				o = userManager.loginUser(SID, usernameOrEmail, Userpass,
						currentClient, current.getClient(), storePermanent);
			}

			if (o == null)
				return null;

			if (!o.getClass().isAssignableFrom(User.class))
				return o;

			if (currentClient.getUser_id() != null
					&& currentClient.getUser_id() > 0) {

				User u = (User) o;
				currentClient.setFirstname(u.getFirstname());
				currentClient.setLastname(u.getLastname());

				Collection<Set<IConnection>> conCollection = current.getScope()
						.getConnections();
				for (Set<IConnection> conset : conCollection) {
					for (IConnection cons : conset) {
						if (cons != null) {
							Client rcl = this.sessionManager
									.getClientByStreamId(cons.getClient()
											.getId(), null);
							if (rcl != null && rcl.getIsScreenClient() != null
									&& rcl.getIsScreenClient()) {
								// continue;
							} else {
								if (cons instanceof IServiceCapableConnection) {
									if (!cons.equals(current)) {
										// log.error("sending roomDisconnect to "
										// + cons);
										// RoomClient rcl =
										// this.sessionManager.getClientByStreamId(cons.getClient().getId());
										// Send to all connected users
										((IServiceCapableConnection) cons)
												.invoke("roomConnect",
														new Object[] { currentClient },
														this);
										// log.error("sending roomDisconnect to "
										// + cons);
									}
								}
							}
						}
					}
				}
			}

			return o;

		} catch (Exception err) {
			log.error("loginUser : ", err);
		}

		return null;

		/*
		 * try { log.debug("loginUser 111: "+SID+" "+Username); IConnection
		 * current = Red5.getConnectionLocal(); RoomClient currentClient =
		 * Application.getClientList().get(current.getClient().getId()); Object
		 * obj = userManagement.loginUser(SID,Username,Userpass, currentClient);
		 * 
		 * if (currentClient.getUser_id()!=null && currentClient.getUser_id()>0)
		 * { Users us = (Users) obj;
		 * currentClient.setFirstname(us.getFirstname());
		 * currentClient.setLastname(us.getLastname()); Iterator<IConnection> it
		 * = current.getScope().getConnections(); while (it.hasNext()) {
		 * //log.error("hasNext == true"); IConnection cons = it.next();
		 * //log.error("cons Host: "+cons); if (cons instanceof
		 * IServiceCapableConnection) { if (!cons.equals(current)){
		 * //log.error("sending roomDisconnect to " + cons); RoomClient rcl =
		 * Application.getClientList().get(cons.getClient().getId()); //Send to
		 * all connected users ((IServiceCapableConnection)
		 * cons).invoke("roomConnect",new Object[] { currentClient }, this);
		 * //log.error("sending roomDisconnect to " + cons); } } } }
		 * 
		 * return obj; } catch (Exception err) { log.error("loginUser",err); }
		 * return null;
		 */
	}

	public Object secureLoginByRemote(String SID, String secureHash) {
		try {

			log.debug("############### secureLoginByRemote " + secureHash);
			System.out.println("############### secureLoginByRemote "
					+ secureHash);

			String clientURL = Red5.getConnectionLocal().getRemoteAddress();

			log.debug("swfURL " + clientURL);

			SOAPLogin soapLogin = soapLoginDao.getSOAPLoginByHash(secureHash);

			if (soapLogin.getUsed()) {

				if (soapLogin.getAllowSameURLMultipleTimes()) {

					if (!soapLogin.getClientURL().equals(clientURL)) {
						log.debug("does not equal " + clientURL);
						return -42L;
					}

				} else {
					log.debug("Already used " + secureHash);
					return -42L;
				}
			}

			Long loginReturn = this.loginUserByRemote(soapLogin
					.getSessionHash());

			IConnection current = Red5.getConnectionLocal();
			String streamId = current.getClient().getId();
			Client currentClient = this.sessionManager
					.getClientByStreamId(streamId, null);

			if (currentClient.getUser_id() != null) {
				sessiondataDao.updateUser(SID, currentClient.getUser_id());
			}

			currentClient.setAllowRecording(soapLogin.getAllowRecording());
			this.sessionManager.updateClientByStreamId(streamId,
					currentClient, false, null);

			if (loginReturn == null) {

				log.debug("loginReturn IS NULL for SID: "
						+ soapLogin.getSessionHash());

				return -1L;
			} else if (loginReturn < 0) {

				log.debug("loginReturn IS < 0 for SID: "
						+ soapLogin.getSessionHash());

				return loginReturn;
			} else {

				soapLogin.setUsed(true);
				soapLogin.setUseDate(new Date());

				soapLogin.setClientURL(clientURL);

				soapLoginDao.updateSOAPLogin(soapLogin);

				// Create Return Object and hide the validated
				// sessionHash that is stored server side
				// this hash should be never thrown back to the user

				SOAPLogin returnSoapLogin = new SOAPLogin();

				returnSoapLogin.setRoom_id(soapLogin.getRoom_id());
				returnSoapLogin.setBecomemoderator(soapLogin
						.getBecomemoderator());
				returnSoapLogin.setShowAudioVideoTest(soapLogin
						.getShowAudioVideoTest());
				returnSoapLogin.setRoomRecordingId(soapLogin
						.getRoomRecordingId());
				returnSoapLogin.setShowNickNameDialog(soapLogin
						.getShowNickNameDialog());
				returnSoapLogin.setLandingZone(soapLogin.getLandingZone());

				return returnSoapLogin;

			}

		} catch (Exception err) {
			log.error("[secureLoginByRemote]", err);
		}
		return null;
	}

	/**
	 * Function is called if the user loggs in via a secureHash and sets the
	 * param showNickNameDialog in the Object SOAPLogin to true the user gets
	 * displayed an additional dialog to enter his nickname
	 * 
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @return - 1 in case of success, -1 otherwise
	 */
	public Long setUserNickName(String firstname, String lastname, String email) {
		try {

			IConnection current = Red5.getConnectionLocal();
			String streamId = current.getClient().getId();
			Client currentClient = this.sessionManager
					.getClientByStreamId(streamId, null);

			currentClient.setFirstname(firstname);
			currentClient.setLastname(lastname);
			currentClient.setMail(email);

			// Log the User
			conferenceLogDao.addConferenceLog(
					"nicknameEnter", currentClient.getUser_id(), streamId,
					null, currentClient.getUserip(), currentClient.getScope(),
					currentClient.getExternalUserId(),
					currentClient.getExternalUserType(),
					currentClient.getMail(), currentClient.getFirstname(),
					currentClient.getLastname());

			this.sessionManager.updateClientByStreamId(streamId,
					currentClient, false, null);

			return 1L;

		} catch (Exception err) {
			log.error("[setUserNickName] ", err);
		}
		return new Long(-1);
	}

	/**
	 * Attention! This SID is NOT the default session id! its the Session id
	 * retrieved in the call from the SOAP Gateway!
	 * 
	 * @param SID
	 * @return - 1 in case of success, -1 otherwise
	 */
	public Long loginUserByRemote(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkWebServiceLevel(
					user_level)) {

				Sessiondata sd = sessiondataDao.getSessionByHash(SID);
				if (sd == null || sd.getSessionXml() == null) {
					return new Long(-37);
				} else {

					// XStream xStream = new XStream(new XppDriver());
					XStream xStream = new XStream(new DomDriver("UTF-8"));
					xStream.setMode(XStream.NO_REFERENCES);

					String xmlString = sd.getSessionXml();
					RemoteSessionObject userObject = (RemoteSessionObject) xStream
							.fromXML(xmlString);

					log.debug("userObject.getUsername(), userObject.getFirstname(), userObject.getLastname() "
							+ userObject.getUsername()
							+ ", "
							+ userObject.getFirstname()
							+ ", "
							+ userObject.getLastname());

					IConnection current = Red5.getConnectionLocal();
					String streamId = current.getClient().getId();
					Client currentClient = this.sessionManager
							.getClientByStreamId(streamId, null);

					// Check if this User is simulated in the OpenMeetings
					// Database

					log.debug("userObject.getExternalUserId() -1- "
							+ userObject.getExternalUserId());

					if (userObject.getExternalUserId() != null
							&& userObject.getExternalUserId() != null && 
							!"".equals(userObject.getExternalUserId())) {

						// If so we need to check that we create this user in
						// OpenMeetings and update its record

						User user = userManager.getUserByExternalIdAndType(
								userObject.getExternalUserId(),
								userObject.getExternalUserType());

						if (user == null) {
							String jName_timeZone = configurationDao.getConfValue("default.timezone", String.class, "");

							long userId = userManager
									.addUserWithExternalKey(1, 0, 0,
											userObject.getFirstname(),
											userObject.getUsername(),
											userObject.getLastname(), 1L,
											true, "", // password is empty by default
											null, null, "",
											userObject.getExternalUserId(),
											userObject.getExternalUserType(),
											true, userObject.getEmail(),
											jName_timeZone,
											userObject.getPictureUrl());

							currentClient.setUser_id(userId);
							SessionVariablesUtil.setUserId(current.getClient(), userId);
						} else {

							user.setPictureuri(userObject.getPictureUrl());

							userManager.updateUser(user);

							currentClient.setUser_id(user.getUser_id());
							SessionVariablesUtil.setUserId(current.getClient(), user.getUser_id());
						}
					}

					log.debug("userObject.getExternalUserId() -2- "
							+ currentClient.getUser_id());

					currentClient
							.setUserObject(userObject.getUsername(),
									userObject.getFirstname(),
									userObject.getLastname());
					currentClient.setPicture_uri(userObject.getPictureUrl());
					currentClient.setMail(userObject.getEmail());

					log.debug("UPDATE USER BY STREAMID " + streamId);

					if (currentClient.getUser_id() != null) {
						sessiondataDao.updateUser(SID,
								currentClient.getUser_id());
					}

					this.sessionManager.updateClientByStreamId(streamId,
							currentClient, false, null);

					return new Long(1);
				}
			}
		} catch (Exception err) {
			log.error("[loginUserByRemote] ", err);
		}
		return new Long(-1);
	}

	/**
	 * this function returns a user object with organization objects set only
	 * the organization is not available for users that are using a HASH mechanism
	 * cause the SOAP/REST API does not guarantee that the user connected to the HASH
	 * has a valid user object set
	 * 
	 * @param SID
	 */
	public User markSessionAsLogedIn(String SID) {
		try {
			sessiondataDao.updateUserWithoutSession(SID, -1L);
			
			Long defaultRpcUserid = configurationDao.getConfValue(
					"default.rpc.userid", Long.class, "-1");
			User defaultRpcUser = userManager.getUserById(defaultRpcUserid);
			
			User user = new User();
			user.setOrganisation_users(defaultRpcUser.getOrganisation_users());
			
			return user;
			
		} catch (Exception err) {
			log.error("[markSessionAsLogedIn]", err);
		}
		return null;
	}

	/**
	 * clear this session id
	 * 
	 * @param SID
	 * @return string value if completed
	 */
	public Long logoutUser(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = this.sessionManager
					.getClientByStreamId(current.getClient().getId(), null);
			
			scopeApplicationAdapter.roomLeaveByScope(currentClient,current.getScope(), false);
			
			currentClient.setUserObject(null, null, null, null);
			
			return userManager.logout(SID, users_id);
		} catch (Exception err) {
			log.error("[logoutUser]",err);
		}
		return -1L;
	}

	/**
	 * get a list of all states, needs no authentification to load
	 * 
	 * @return List of State-Objects or null
	 */
	public List<State> getStates() {
		return statemanagement.getStates();
	}

	public List<OmTimeZone> getTimeZones() {
		return omTimeZoneDaoImpl.getOmTimeZones();
	}

	/**
	 * Load if the users can register itself by using the form without logging
	 * in, needs no authentification to load
	 * 
	 * @param SID
	 * @return configuration with key "allow_frontend_register"
	 */
	public Configuration allowFrontendRegister(String SID) {
		return configurationDao.get("allow_frontend_register");
	}
	
	public List<Configuration> getGeneralOptions(String SID) {
		try {
			
			List<Configuration> cList = new LinkedList<Configuration>();
			
			cList.add(configurationDao.get("exclusive.audio.keycode"));
			cList.add(configurationDao.get("red5sip.enable"));
			cList.add(configurationDao.get("max_upload_size"));
			
			return cList;
			
		} catch (Exception err) {
			log.error("[getLoginOptions]",err);
		}
		return null;
	}

	public List<Configuration> getLoginOptions(String SID) {
		try {

			List<Configuration> cList = new LinkedList<Configuration>();
			cList.add(configurationDao
					.get("allow_frontend_register"));
			cList.add(configurationDao.get("show.facebook.login"));
			cList.add(configurationDao
					.get("user.login.minimum.length"));
			cList.add(configurationDao
					.get("user.pass.minimum.length"));
			cList.add(configurationDao
					.get("user.pass.minimum.length"));
			cList.add(configurationDao.get("ldap_default_id"));

			return cList;
		} catch (Exception err) {
			log.error("[getLoginOptions]", err);
		}
		return null;

	}

	/**
	 * Add a user register by an Object see [registerUser] for the index of the
	 * Object To allow the registering the config_key *allow_frontend_register*
	 * has to be the value 1 otherwise the user will get an error code
	 * 
	 * @param regObjectObj
	 * @return new users_id OR null if an exception, -1 if an error, -4 if mail
	 *         already taken, -5 if username already taken, -3 if login or pass
	 *         or mail is empty
	 */
	public Long registerUserByObject(Object regObjectObj) {
		try {
			@SuppressWarnings("unchecked")
			Map<?, ?> regObject = (Map<Object, Object>) regObjectObj;

			String domain = regObject.get("domain").toString();
			String port = regObject.get("port").toString();
			String webapp = regObject.get("webapp").toString();

			String baseURL = "http://" + domain + ":" + port + webapp;
			if (port.equals("80")) {
				baseURL = "http://" + domain + webapp;
			} else if (port.equals("443")) {
				baseURL = "https://" + domain + webapp;
			}

			return userManager.registerUser(regObject.get("Username")
					.toString(), regObject.get("Userpass").toString(),
					regObject.get("lastname").toString(),
					regObject.get("firstname").toString(),
					regObject.get("email").toString(), new Date(), regObject
							.get("street").toString(),
					regObject.get("additionalname").toString(),
					regObject.get("fax").toString(), regObject.get("zip")
							.toString(),
					Long.valueOf(regObject.get("states_id").toString())
							.longValue(), regObject.get("town").toString(),
					Long.valueOf(regObject.get("language_id").toString())
							.longValue(), "", false, baseURL, true,
					regObject.get("jNameTimeZone").toString());
		} catch (Exception ex) {
			log.error("registerUserByObject", ex);
		}
		return new Long(-1);
	}

	/**
	 * Register a new User To allow the registering the config_key
	 * *allow_frontend_register* has to be the value 1 otherwise the user will
	 * get an error code
	 * 
	 * @deprecated use registerUserByObject instead
	 * @param SID
	 * @param Username
	 * @param Userpass
	 * @param lastname
	 * @param firstname
	 * @param email
	 * @param age
	 * @param street
	 * @param additionalname
	 * @param fax
	 * @param zip
	 * @param states_id
	 * @param town
	 * @param language_id
	 * @return new users_id OR null if an exception, -1 if an error, -4 if mail
	 *         already taken, -5 if username already taken, -3 if login or pass
	 *         or mail is empty
	 */
	@Deprecated
	public Long registerUser(String SID, String Username, String Userpass,
			String lastname, String firstname, String email, Date age,
			String street, String additionalname, String fax, String zip,
			long states_id, String town, long language_id, String phone) {
		return userManager.registerUser(Username, Userpass, lastname,
				firstname, email, age, street, additionalname, fax, zip,
				states_id, town, language_id, phone, false, "", true, "");
	}

	/**
	 * logs a user out and deletes his account
	 * 
	 * @param SID
	 * @return - id of user being deleted, or error code
	 */
	public Long deleteUserIDSelf(String SID) {
		Long users_id = sessiondataDao.checkSession(SID);
		long user_level = userManager.getUserLevelByID(users_id);
		if (user_level >= 1) {
			userManager.logout(SID, users_id);
			return usersDao.deleteUserID(users_id);
		} else {
			return new Long(-10);
		}
	}

	/**
	 * send an invitation to another user by Mail
	 * 
	 * @deprecated
	 * @param SID
	 * @param username
	 * @param message
	 * @param domain
	 * @param room
	 * @param roomtype
	 * @param baseurl
	 * @param email
	 * @param subject
	 * @param room_id
	 * @return - "success" string in case of success, error message of null otherwise
	 */
	@Deprecated
	public String sendInvitation(String SID, String username, String message,
			String domain, String room, String roomtype, String baseurl,
			String email, String subject, Long room_id) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return invitationManager.sendInvitionLink(user_level,
				username, message, domain, room, roomtype, baseurl, email,
				usersDao.get(users_id).getAdresses().getEmail(), subject, room_id, null, null);
	}

	/**
	 * send some feedback, this will only work for the online demo-version
	 * 
	 * @param SID
	 * @param username
	 * @param message
	 * @param email
	 * @return - "success" string in case of success, error message of null otherwise
	 */
	public String sendFeedback(String SID, String username, String message,
			String email) {
		return feedbackManager.sendFeedback(username, email,
				message);
	}

	public List<Userdata> getUserdata(String SID) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkUserLevel(user_level)) {
			return userManager.getUserdataDashBoard(users_id);
		}
		return null;
	}

	/**
	 * @deprecated
	 * @param SID
	 * @return - map of rss messages
	 */
	@Deprecated
	public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>> getRssFeeds(
			String SID) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return loadAtomRssFeed.getRssFeeds(user_level);
	}

	/**
	 * 
	 * @param SID
	 * @param urlEndPoint
	 * @return - map of rss messages
	 */
	public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> getRssFeedByURL(
			String SID, String urlEndPoint) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkUserLevel(user_level)) {
			return loadAtomRssFeed.parseRssFeed(urlEndPoint);
		} else {
			return null;
		}
	}

	/**
	 * TODO: Is this function in usage?
	 * 
	 * @deprecated
	 * @param SID
	 * @param domain
	 * @return - empty map
	 */
	@Deprecated
	public LinkedHashMap<Integer, Client> getUsersByDomain(String SID,
			String domain) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkUserLevel(user_level)) {
			LinkedHashMap<Integer, Client> lMap = new LinkedHashMap<Integer, Client>();
			// Integer counter = 0;
			// for (Iterator<String> it =
			// Application.getClientList().keySet().iterator();it.hasNext();) {
			// RoomClient rc = Application.getClientList().get(it.next());
			// //if (rc.getDomain().equals(domain)) {
			// lMap.put(counter, rc);
			// counter++;
			// //}
			// }
			return lMap;
		} else {
			return null;
		}
	}

	public int closeRoom(String SID, Long room_id, Boolean status) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {

				roomManager.closeRoom(room_id, status);

				if (status) {
					Map<String, String> message = new HashMap<String, String>();
					message.put("message", "roomClosed");
					this.scopeApplicationAdapter.sendMessageByRoomAndDomain(
							room_id, message);
				}

				return 1;

			}

			return 1;
		} catch (Exception err) {
			log.error("[closeRoom]", err);
		}
		return -1;
	}

	public void resultReceived(IPendingServiceCall arg0) {
		log.debug("[resultReceived]" + arg0);
	}

	public List<Configuration> getDashboardConfiguration(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkUserLevel(user_level)) {
				return configurationDao.getConfKeys(new String[] {
						"dashboard.show.chat", //
						"dashboard.show.myrooms", //
						"dashboard.show.rssfeed", //
						"default.dashboard.tab", //
						"default.landing.zone" });
			}
		} catch (Exception err) {
			log.error("[getDashboardConfiguration]", err);
		}
		return null;
	}
}
