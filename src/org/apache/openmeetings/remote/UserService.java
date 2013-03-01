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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.cluster.SlaveHTTPConnectionManager;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.calendar.daos.MeetingMemberDao;
import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.PrivateMessageFolderDao;
import org.apache.openmeetings.data.user.dao.PrivateMessagesDao;
import org.apache.openmeetings.data.user.dao.SalutationDao;
import org.apache.openmeetings.data.user.dao.UserContactsDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.PrivateMessage;
import org.apache.openmeetings.persistence.beans.user.PrivateMessageFolder;
import org.apache.openmeetings.persistence.beans.user.Salutation;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.persistence.beans.user.UserContact;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.templates.RequestContactConfirmTemplate;
import org.apache.openmeetings.templates.RequestContactTemplate;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.apache.openmeetings.utils.mail.MailHandler;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.apache.openmeetings.utils.math.TimezoneUtil;
import org.red5.io.utils.ObjectMap;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides method to manipulate {@link User}
 * 
 * @author sebawagner
 * 
 */
public class UserService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UserService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private SalutationDao salutationmanagement;
	@Autowired
	private OrganisationManager organisationManager;
	@Autowired
	private ManageCryptStyle cryptManager;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private PrivateMessagesDao privateMessagesDao;
	@Autowired
	private PrivateMessageFolderDao privateMessageFolderDao;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private UserContactsDao userContactsDao;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private RequestContactTemplate requestContactTemplate;
	@Autowired
	private RequestContactConfirmTemplate requestContactConfirmTemplate;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private ServerDao serverDao;
	@Autowired
	private SlaveHTTPConnectionManager slaveHTTPConnectionManager;

	/**
	 * get your own user-object
	 * 
	 * @param SID
	 * @return get the user bound to this session
	 */
	public User getUserSelf(String SID) {
		Long users_id = sessiondataDao.checkSession(SID);
		return usersDao.get(users_id);
	}

	public Long resetUserPwd(String SID, String email, String login,
			String applink) {
		sessiondataDao.checkSession(SID);
		return userManager.resetUser(email, login, applink);
	}

	public Object getUserByHash(String SID, String hash) {
		sessiondataDao.checkSession(SID);
		return usersDao.getUserByHash(hash);
	}

	public Object resetPassByHash(String SID, String hash, String pass) {
		sessiondataDao.checkSession(SID);
		return usersDao.resetPassByHash(hash, pass);
	}

	/**
	 * get user by id, admin only
	 * 
	 * @param SID
	 * @param user_id
	 * @return User with the id given
	 */
	public User getUserById(String SID, long user_id) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return userManager.checkAdmingetUserById(user_level, user_id);
	}

	/**
	 * refreshes the current SID
	 * 
	 * @param SID
	 * @return "ok" string in case of success, "error" string in case of the error
	 */
	public String refreshSession(String SID) {
		try {
			sessiondataDao.checkSession(SID);
			return "ok";
		} catch (Exception err) {
			log.error("[refreshSession]", err);
		}
		return "error";
	}

	/**
	 * get all availible Salutations
	 * 
	 * @param SID
	 * @return all availible Salutations
	 */
	public List<Salutation> getUserSalutations(String SID, long language_id) {
		return salutationmanagement.getUserSalutations(language_id);
	}

	/**
	 * 
	 * @param SID
	 * @param searchcriteria
	 *            login,lastname,firstname,user_id
	 * @param searchstring
	 * @param max
	 * @param start
	 * @param orderby
	 *            login,lastname,firstname,user_id
	 * @param asc
	 * @return List of the users found
	 */
	public List<User> searchUser(String SID, String searchcriteria,
			String searchstring, int max, int start, String orderby, boolean asc) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return userManager.searchUser(user_level, searchcriteria,
				searchstring, max, start, orderby, asc);
	}

	/**
	 * get a list of all users of an organisation
	 * 
	 * @param SID
	 * @param organisation_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return list of all users of an organisation
	 */
	public List<User> getOrgUserList(String SID, long organisation_id,
			int start, int max, String orderby, boolean asc) {
		return organisationManager.getUsersByOrganisationId(organisation_id,
				start, max, orderby, asc);
	}

	public List<User> getUserListByModForm(String SID) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return userManager.getUserByMod(user_level, users_id);
	}

	/**
	 * get a list of all organisations of an user
	 * 
	 * @param SID
	 * @param client_user
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return list of all organisations of an user
	 */
	public List<Organisation> getOrganisationListByUser(String SID,
			long client_user, int start, int max, String orderby, boolean asc) {
		Long users_id = sessiondataDao.checkSession(SID);
		long user_level = userManager.getUserLevelByID(users_id);
		return organisationManager.getOrganisationsByUserId(user_level,
				client_user, start, max, orderby, asc);
	}

	/**
	 * gets a list of all not assigned organisations of a user
	 * 
	 * @param SID
	 * @param client_user
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return list of all not assigned organisations of a user
	 */
	public List<Organisation> getRestOrganisationListByUser(String SID,
			long client_user, int start, int max, String orderby, boolean asc) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return organisationManager.getRestOrganisationsByUserId(user_level,
				client_user, start, max, orderby, asc);
	}

	/**
	 * gets a whole user-list(admin-role only)
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @return whole user-list
	 */
	public SearchResult<User> getUserList(String SID, int start, int max,
			String orderby, boolean asc) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return userManager
				.getUsersList(user_level, start, max, orderby, asc);
	}

	public SearchResult<User> getUserListWithSearch(String SID, int start,
			int max, String orderby, boolean asc, String search) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return userManager.getUsersListWithSearch(user_level, start, max,
				orderby, asc, search);
	}

	/**
	 * gets a user-list by search criteria
	 * 
	 * @param SID
	 * @param search
	 * @param start
	 * @param max
	 * @param orderby
	 * @return user-list by search criteria
	 */
	public SearchResult<User> getAllUserBySearchRange(String SID,
			String search, int start, int max, String orderby, boolean asc) {
		return userManager.getAllUserByRange(search, start, max, orderby,
				asc);
	}

	/**
	 * updates the user profile, every user can update his own profile
	 * 
	 * @param SID
	 * @param values
	 * @return user_id or NULL or negativ value (error_id)
	 */
	public Long updateUserSelfSmall(String SID,
			@SuppressWarnings("rawtypes") ObjectMap values) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (user_level != null && user_level >= 1) {
				return userManager.saveOrUpdateUser(new Long(3), values,
						users_id);
			} else {
				return new Long(-2);
			}
		} catch (Exception err) {
			log.error("[updateUserSelfSmall] " + err);
			return new Long(-1);
		}
	}

	/**
	 * 
	 * @param SID
	 * @param regObjectObj
	 * @return - id of the user updated in case of success, null otherwise
	 */
	@SuppressWarnings({ "rawtypes" })
	public Long saveOrUpdateUser(String SID, Object regObjectObj) {
		try {
			LinkedHashMap argObjectMap = (LinkedHashMap) regObjectObj;
			Long user_idClient = null;
			if (argObjectMap.get("user_idClient") != null) {
				user_idClient = Long.valueOf(
						argObjectMap.get("user_idClient").toString())
						.longValue();
			}
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			List<?> orgO = (List<?>) argObjectMap.get("organisations");
			List<Long> orgIds = new ArrayList<Long>(orgO.size());
			for (Object o : orgO) {
				orgIds.add(Long.valueOf((Integer) o));
			}
			Date age = null;
			if (argObjectMap.get("userage") instanceof Date) {
				age = (Date) argObjectMap.get("userage");
			}

			log.debug("Mail : " + argObjectMap.get("email").toString());
			log.debug("Phone : " + argObjectMap.get("phone").toString());

			long userId;
			if (user_idClient == null || user_idClient == 0) {
				userId = userManager.registerUserInit(
						user_level,
						Long.valueOf(argObjectMap.get("level_id").toString())
								.longValue(),
						Integer.valueOf(
								argObjectMap.get("availible").toString())
								.intValue(),
						Integer.valueOf(argObjectMap.get("status").toString())
								.intValue(),
						argObjectMap.get("login").toString(),
						argObjectMap.get("password").toString(),
						argObjectMap.get("lastname").toString(),
						argObjectMap.get("firstname").toString(),
						argObjectMap.get("email").toString(),
						age,
						argObjectMap.get("street").toString(),
						argObjectMap.get("additionalname").toString(),
						argObjectMap.get("fax").toString(),
						argObjectMap.get("zip").toString(),
						Long.valueOf(argObjectMap.get("states_id").toString())
								.longValue(),
						argObjectMap.get("town").toString(),
						new Long(argObjectMap.get("language_id").toString()),
						true,
						orgIds,
						argObjectMap.get("phone").toString(),
						Boolean.valueOf(argObjectMap.get("sendSMS").toString())
								.booleanValue(),
						"",
						false,
						argObjectMap.get("jNameTimeZone").toString(),
						Boolean.valueOf(
								argObjectMap.get("forceTimeZoneCheck")
										.toString()).booleanValue(),
						argObjectMap.get("userOffers").toString(),
						argObjectMap.get("userSearchs").toString(),
						Boolean.valueOf(
								argObjectMap.get("showContactData").toString())
								.booleanValue(),
						Boolean.valueOf(
								argObjectMap.get("showContactDataToContacts")
										.toString()).booleanValue());
			} else {
				userId = userManager.updateUser(
						user_level,
						user_idClient,
						Long.valueOf(argObjectMap.get("level_id").toString())
								.longValue(),
						argObjectMap.get("login").toString(),
						argObjectMap.get("password").toString(),
						argObjectMap.get("lastname").toString(),
						argObjectMap.get("firstname").toString(),
						age,
						argObjectMap.get("street").toString(),
						argObjectMap.get("additionalname").toString(),
						argObjectMap.get("zip").toString(),
						Long.valueOf(argObjectMap.get("states_id").toString())
								.longValue(),
						argObjectMap.get("town").toString(),
						new Long(argObjectMap.get("language_id").toString()),
						Integer.valueOf(
								argObjectMap.get("availible").toString())
								.intValue(),
						argObjectMap.get("telefon").toString(),
						argObjectMap.get("fax").toString(),
						argObjectMap.get("mobil").toString(),
						argObjectMap.get("email").toString(),
						argObjectMap.get("comment").toString(),
						Integer.valueOf(argObjectMap.get("status").toString())
								.intValue(),
						orgIds,
						Integer.valueOf(
								argObjectMap.get("salutations_id").toString())
								.intValue(),
						argObjectMap.get("phone").toString(),
						Boolean.valueOf(argObjectMap.get("sendSMS").toString())
								.booleanValue(),
						argObjectMap.get("jNameTimeZone").toString(),
						Boolean.valueOf(
								argObjectMap.get("forceTimeZoneCheck")
										.toString()).booleanValue(),
						argObjectMap.get("userOffers").toString(),
						argObjectMap.get("userSearchs").toString(),
						Boolean.valueOf(
								argObjectMap.get("showContactData").toString())
								.booleanValue(),
						Boolean.valueOf(
								argObjectMap.get("showContactDataToContacts")
										.toString()).booleanValue());
			}

			return userId;
			
		} catch (Exception ex) {
			log.error("[saveOrUpdateUser]: ", ex);
		}
		return null;
	}

	/**
	 * deletes a user
	 * 
	 * @param SID
	 * @param user_idClient
	 * @return - id of the user deleted in case of success, error code otherwise
	 */
	public Long deleteUserAdmin(String SID, Long user_idClient) {
		log.debug("deleteUserAdmin");
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			// admins only
			if (authLevelUtil.checkAdminLevel(user_level)) {
				// no self destruction ;-)
				if (!users_id.equals(user_idClient)) {

					// Setting user deleted
					Long userId = usersDao.deleteUserID(user_idClient);

					return userId;
				} else {
					return new Long(-38);
				}
			} else {
				return new Long(-11);
			}
		} catch (Exception err) {
			log.error("[deleteUserAdmin]", err);
		}
		return null;
	}

	/**
	 * kicks a user from the server, also from slaves if needed, this method is
	 * only invoked by the connection administration UI
	 * 
	 * @param SID
	 * @param streamid
	 * @param serverId
	 *            0 means the session is locally, otherwise we have to perform a
	 *            REST call
	 * @return - true if user has sufficient permissions, false otherwise
	 */
	public Boolean kickUserByStreamId(String SID, String streamid, long serverId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// admins only
			if (authLevelUtil.checkAdminLevel(user_level)) {

				if (serverId == 0) {

					Client rcl = this.sessionManager
							.getClientByStreamId(streamid, null);

					if (rcl == null) {
						return true;
					}
					String scopeName = "hibernate";
					if (rcl.getRoom_id() != null) {
						scopeName = rcl.getRoom_id().toString();
					}
					IScope currentScope = this.scopeApplicationAdapter
							.getRoomScope(scopeName);

					HashMap<Integer, String> messageObj = new HashMap<Integer, String>();
					messageObj.put(0, "kick");
					this.scopeApplicationAdapter.sendMessageById(messageObj,
							streamid, currentScope);

					this.scopeApplicationAdapter.roomLeaveByScope(rcl,
							currentScope, true);

					return true;

				} else {

					Server server = serverDao.get(serverId);
					Client rcl = sessionManager.getClientByStreamId(
							streamid, server);
					slaveHTTPConnectionManager.kickSlaveUser(server, rcl.getPublicSID());
					
					// true means only the REST call is performed, it is no
					// confirmation that the user is really kicked from the
					// slave
					return true;
				}
			}
		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return false;
	}

	public User updateUserSelfTimeZone(String SID, String jname) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				User us = userManager.getUserById(users_id);

				us.setOmTimeZone(omTimeZoneDaoImpl.getOmTimeZone(jname));
				us.setForceTimeZoneCheck(false);
				us.setUpdatetime(new Date());

				userManager.updateUser(us);

				return us;

			}
		} catch (Exception err) {
			log.error("[updateUserTimeZone]", err);
		}
		return null;
	}

	public SearchResult<User> searchUserProfile(String SID, String searchTxt,
			String userOffers, String userSearchs, String orderBy, int start,
			int max, boolean asc) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				SearchResult<User> searchResult = new SearchResult<User>();
				searchResult.setObjectName(User.class.getName());
				List<User> userList = userManager.searchUserProfile(
						searchTxt, userOffers, userSearchs, orderBy, start,
						max, asc);
				searchResult.setResult(userList);
				Long resultInt = userManager.searchCountUserProfile(
						searchTxt, userOffers, userSearchs);
				searchResult.setRecords(resultInt);

				return searchResult;
			}
		} catch (Exception err) {
			log.error("[searchUserProfile]", err);
		}
		return null;
	}

	public Long requestUserToContactList(String SID, Long userToAdd_id,
			String domain, String port, String webapp) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				Long countContacts = userContactsDao.checkUserContacts(
						userToAdd_id, users_id);

				if (countContacts != null && countContacts > 0) {
					return -45L;
				}

				String hash = cryptManager
						.getInstanceOfCrypt()
						.createPassPhrase(
								CalendarPatterns
										.getDateWithTimeByMiliSeconds(new Date()));

				Long userContactId = userContactsDao.addUserContact(
						userToAdd_id, users_id, true, hash);

				User user = userManager.getUserById(users_id);

				User userToAdd = userManager.getUserById(userToAdd_id);

				Long language_id = userToAdd.getLanguage_id();
				if (language_id == null) {
					language_id = configurationDao.getConfValue("default_lang_id", Long.class, "1");
				}

				String message = "";

				Fieldlanguagesvalues fValue1192 = fieldManager
						.getFieldByIdAndLanguage(1192L, language_id);
				Fieldlanguagesvalues fValue1193 = fieldManager
						.getFieldByIdAndLanguage(1193L, language_id);
				Fieldlanguagesvalues fValue1190 = fieldManager
						.getFieldByIdAndLanguage(1190L, language_id);
				Fieldlanguagesvalues fValue1191 = fieldManager
						.getFieldByIdAndLanguage(1191L, language_id);
				Fieldlanguagesvalues fValue1196 = fieldManager
						.getFieldByIdAndLanguage(1196L, language_id);

				message += fValue1192.getValue() + " "
						+ userToAdd.getFirstname() + " "
						+ userToAdd.getLastname() + "<br/><br/>";
				message += user.getFirstname() + " " + user.getLastname() + " "
						+ fValue1193.getValue() + "<br/>";
				message += fieldManager.getString(1194L, language_id)
						+ "<br/>";

				String baseURL = "http://" + domain + ":" + port + webapp;
				if (port.equals("80")) {
					baseURL = "http://" + domain + webapp;
				} else if (port.equals("443")) {
					baseURL = "https://" + domain + webapp;
				}

				privateMessagesDao
						.addPrivateMessage(
								user.getFirstname() + " " + user.getLastname()
										+ " " + fValue1193.getValue(), message,
								0L, user, userToAdd, userToAdd, false, null,
								true, userContactId, userToAdd.getAdresses()
										.getEmail());

				String link = baseURL + "?cuser=" + hash;

				String accept_link = link + "&tAccept=yes";
				String deny_link = link + "&tAccept=no";

				String aLinkHTML = "<a href='" + accept_link + "'>"
						+ fValue1190.getValue() + "</a><br/>";
				String denyLinkHTML = "<a href='" + deny_link + "'>"
						+ fValue1191.getValue() + "</a><br/>";
				String profileLinkHTML = "<a href='" + link + "'>"
						+ fValue1196.getValue() + "</a><br/>";

				String template = requestContactTemplate
						.getRequestContactTemplate(message, aLinkHTML,
								denyLinkHTML, profileLinkHTML);

				if (userToAdd.getAdresses() != null) {
					mailHandler.send(userToAdd.getAdresses().getEmail(),
							user.getFirstname() + " " + user.getLastname()
									+ " " + fValue1193.getValue(), template);
				}

				return userContactId;
			}
		} catch (Exception err) {
			log.error("[requestuserToContactList]", err);
		}
		return null;
	}

	public List<UserContact> getPendingUserContacts(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<UserContact> uList = userContactsDao
						.getContactRequestsByUserAndStatus(users_id, true);

				return uList;
			}

		} catch (Exception err) {
			log.error("[getPendingUserContact]", err);
		}
		return null;
	}

	public Object changeUserContactByHash(String SID, String hash,
			Boolean status) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				UserContact userContact = userContactsDao
						.getContactsByHash(hash);

				if (userContact == null) {

					log.error("changeUserContactByHash " + hash);

					return -48L;
				}

				if (userContact.getContact().getUser_id().equals(users_id)) {

					return this.changePendingStatusUserContacts(SID,
							userContact.getUserContactId(), status);

				} else {
					return -48L;
				}

			}

		} catch (Exception err) {
			log.error("[changeUserContactByHash]", err);
		}
		return null;
	}

	public List<UserContact> getUserContacts(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<UserContact> uList = userContactsDao
						.getContactsByUserAndStatus(users_id, false);

				return uList;
			}

		} catch (Exception err) {
			log.error("[getPendingUserContact]", err);
		}
		return null;
	}

	public Long checkPendingStatus(String SID, Long userContactId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				UserContact userContacts = userContactsDao
						.getUserContacts(userContactId);

				if (userContacts == null) {
					return -46L;
				}

				if (userContacts.getPending() != null
						&& !userContacts.getPending()) {
					return -47L;
				}

				return userContactId;
			}
		} catch (Exception err) {
			log.error("[checkPendingStatus]", err);
		}
		return null;
	}

	public Integer removeContactUser(String SID, Long userContactId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				UserContact userContacts = userContactsDao
						.getUserContacts(userContactId);

				if (userContacts == null) {
					return -49;
				}

				return userContactsDao.deleteUserContact(userContactId);

			}
		} catch (Exception err) {
			log.error("[removeContactUser]", err);
		}
		return null;
	}

	public Long changePendingStatusUserContacts(String SID, Long userContactId,
			Boolean pending) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				UserContact userContacts = userContactsDao
						.getUserContacts(userContactId);

				if (userContacts == null) {
					return -46L;
				}

				if (userContacts.getPending() != null
						&& !userContacts.getPending()) {
					return -47L;
				}

				if (pending) {

					userContactsDao.updateContactStatus(userContactId, false);

					userContacts = userContactsDao
							.getUserContacts(userContactId);

					userContactsDao.addUserContact(userContacts.getOwner()
							.getUser_id(), users_id, false, "");

					User user = userContacts.getOwner();

					if (user.getAdresses() != null) {

						Long language_id = user.getLanguage_id();
						if (language_id == null) {
							language_id = configurationDao.getConfValue("default_lang_id", Long.class, "1");
						}

						String message = "";

						Fieldlanguagesvalues fValue1192 = fieldManager
								.getFieldByIdAndLanguage(1192L, language_id);
						Fieldlanguagesvalues fValue1198 = fieldManager
								.getFieldByIdAndLanguage(1198L, language_id);

						message += fValue1192.getValue() + " "
								+ user.getFirstname() + " "
								+ user.getLastname() + "<br/><br/>";
						message += userContacts.getContact().getFirstname()
								+ " " + userContacts.getContact().getLastname()
								+ " " + fValue1198.getValue();

						String template = requestContactConfirmTemplate
								.getRequestContactTemplate(message);

						privateMessagesDao.addPrivateMessage(
								user.getFirstname() + " " + user.getLastname()
										+ " " + fValue1198.getValue(), message,
								0L, userContacts.getContact(), user, user,
								false, null, false, 0L, user.getAdresses()
										.getEmail());

						mailHandler.send(user.getAdresses().getEmail(),
								userContacts.getContact().getFirstname()
										+ " "
										+ userContacts.getContact()
												.getLastname() + " "
										+ fValue1198.getValue(), template);

					}

				} else {

					userContactsDao.deleteUserContact(userContactId);

				}

				return userContactId;
			}

		} catch (Exception err) {
			log.error("[getPendingUserContact]", err);
		}
		return null;
	}

	private Date createCalendarDate(TimeZone timezone, String dateOnly,
			String time) {
		Integer hour = Integer.valueOf(time.substring(0, 2)).intValue();
		Integer minute = Integer.valueOf(time.substring(3, 5)).intValue();

		log.info("createCalendar Hour: " + hour);
		log.info("createCalendar Minute: " + minute);

		Calendar cal = TimezoneUtil.getCalendarInTimezone(dateOnly, timezone);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public Long composeMail(String SID, List<String> recipients,
			String subject, String message, Boolean bookedRoom,
			String validFromDate, String validFromTime, String validToDate,
			String validToTime, Long parentMessageId, Long roomtype_id,
			String domain, String port, String webapp) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {
				User from = userManager.getUserById(users_id);
				TimeZone timezone = timezoneUtil.getTimezoneByUser(from);

				Date appointmentstart = createCalendarDate(timezone,
						validFromDate, validFromTime);
				Date appointmentend = createCalendarDate(timezone, validToDate,
						validToTime);

				log.info("validFromDate: "
						+ CalendarPatterns
								.getDateWithTimeByMiliSeconds(appointmentstart));
				log.info("validToDate: "
						+ CalendarPatterns
								.getDateWithTimeByMiliSeconds(appointmentend));

				Room room = null;

				String baseURL = "http://" + domain + ":" + port + webapp;
				if (port.equals("80")) {
					baseURL = "http://" + domain + webapp;
				} else if (port.equals("443")) {
					baseURL = "https://" + domain + webapp;
				}

				Long room_id = null;
				Long appointmentId = null;

				if (bookedRoom) {
					room_id = roomManager.addRoom(3, // Userlevel
							subject, // name
							roomtype_id, // RoomType
							"", // Comment
							new Long(100), // Number of participants
							false, // public
							null, // Organisations
							true, // Appointment
							false, // Demo Room => Meeting Timer
							null, // Meeting Timer time in seconds
							false, // Is Moderated Room
							null, // Moderation List Room
							true, // Allow User Questions
							false, // isAudioOnly
							true, // allowFontStyles
							false, // isClosed
							"", // redirectURL
							"", // conferencePIN
							null, // ownerId
							null, null, false, // hideTopBar
							false, // hideChat
							false, // hideActivitiesAndActions
							false, // hideFilesExplorer
							false, // hideActionsMenu
							false, // hideScreenSharing
							false, // hideWhiteboard
							false, // showMicrophoneStatus
							false, // chatModerated
							false, // chatOpened
							false, // filesOpened
							false, // autoVideoSelect
							false //sipEnabled
							);

					room = roomDao.get(room_id);

					String sendJNameTimeZone = from.getOmTimeZone().getJname();

					appointmentId = this.addAppointmentToUser(subject, message,
							from, recipients, room, appointmentstart,
							appointmentend, true, true, sendJNameTimeZone);

				}

				recipients.add(from.getAdresses().getEmail());

				String sendJNameTimeZone = from.getOmTimeZone().getJname();

				String profile_link = baseURL + "?cuser=1";

				for (String email : recipients) {

					// Map receipent = (Map) recipients.get(iter.next());

					// String email = receipent.get("email").toString();

					Long language_id = from.getLanguage_id();
					if (language_id == null) {
						language_id = configurationDao.getConfValue("default_lang_id", Long.class, "1");
					}
					String invitation_link = null;
					User to = userManager.getUserByEmail(email);

					if (bookedRoom) {
						// Add the appointment to the calendar of the user (if
						// its an internal user)
						// if the user is the sender then we already added the
						// appointment as we created the
						// room, the invitations always belong to the
						// appointment of the meeting creator
						if (to != null
								&& !to.getUser_id().equals(from.getUser_id())) {
							this.addAppointmentToUser(subject, message, to,
									recipients, room, appointmentstart,
									appointmentend, false, true,
									sendJNameTimeZone);
						}

						Invitations invitation = invitationManager
								.addInvitationLink(
										new Long(2), // userlevel
										from.getFirstname() + " "
												+ from.getLastname(), // username
										message,
										baseURL, // baseURl
										from.getAdresses().getEmail(), // email
										subject, // subject
										room_id, // room_id
										"public",
										false, // passwordprotected
										"", // invitationpass
										2, // valid type
										appointmentstart, // valid from
										appointmentend, // valid to
										from.getUser_id(), // created by
										baseURL, from.getUser_id(),
										false, // really send mail sendMail
										appointmentstart, appointmentend,
										appointmentId, from.getFirstname()
												+ " " + from.getLastname(),
										from.getOmTimeZone());

						invitation_link = baseURL + "?invitationHash="
								+ invitation.getHash();

					}

					if (to != null) {

						if (!to.getUser_id().equals(from.getUser_id())) {
							// One message to the Send
							privateMessagesDao.addPrivateMessage(subject,
									message, parentMessageId, from, to, from,
									bookedRoom, room, false, 0L, email);

							// One message to the Inbox
							privateMessagesDao.addPrivateMessage(subject,
									message, parentMessageId, from, to, to,
									bookedRoom, room, false, 0L, email);

							// One copy of the Inbox message to the user
							if (to.getLanguage_id() != null) {
								language_id = to.getLanguage_id();
							}
						}

					} else {

						// One message to the Send
						privateMessagesDao.addPrivateMessage(subject, message,
								parentMessageId, from, to, from, bookedRoom,
								room, false, 0L, email);

						// there is no Inbox for external users

					}

					// We do not send an email to the one that has created the
					// private message
					if (to != null && to.getUser_id().equals(from.getUser_id())) {
						continue;
					}

					Fieldlanguagesvalues fValue1301 = fieldManager
							.getFieldByIdAndLanguage(1301L, language_id);
					Fieldlanguagesvalues fValue1302 = fieldManager
							.getFieldByIdAndLanguage(1302L, language_id);
					Fieldlanguagesvalues labelid504 = fieldManager
							.getFieldByIdAndLanguage(new Long(504), language_id);
					Fieldlanguagesvalues labelid503 = fieldManager
							.getFieldByIdAndLanguage(new Long(503), language_id);

					String aLinkHTML = "";
					if (to != null) {
						aLinkHTML = "<br/><br/><a href='" + profile_link + "'>"
								+ fValue1302.getValue() + "</a><br/>";
					}

					if (invitation_link == null) {
						invitation_link = "";
					} else {
						invitation_link = "<br/>" //
								+ CalendarPatterns
										.getDateWithTimeByMiliSecondsAndTimeZone(
												appointmentstart, timezone)
								+ "<br/> - <br/>" //
								+ CalendarPatterns
										.getDateWithTimeByMiliSecondsAndTimeZone(
												appointmentstart, timezone)
								+ "<br/>"
								+ labelid503.getValue()
								+ "<br/><a href='" + invitation_link
								+ "'>"
								+ labelid504.getValue() + "</a><br/>";
					}

					mailHandler.send(email, fValue1301.getValue() + " "
							+ subject, message.replaceAll("\\<.*?>", "")
							+ aLinkHTML + invitation_link);

				}

			}

		} catch (Exception err) {
			log.error("[composeMail]", err);
		}
		return null;
	}

	/*
	 * Date appointmentstart = calFrom.getTime(); Date appointmentend =
	 * calTo.getTime();
	 */
	private Long addAppointmentToUser(String subject, String message, User to,
			List<String> recipients, Room room, Date appointmentstart,
			Date appointmentend, Boolean invitor, Boolean isConnectedEvent,
			String sendJNameTimeZone) throws Exception {

		Long appointmentId = appointmentDao.addAppointment(subject,
				to.getUser_id(), "", message, appointmentstart, appointmentend,
				false, false, false, false, 1L, 2L, room, to.getLanguage_id(),
				false, "", isConnectedEvent, sendJNameTimeZone);

		for (String email : recipients) {

			User meetingMember = userManager.getUserByEmail(email);

			if (meetingMember != null) {

				String firstname = meetingMember.getFirstname();
				String lastname = meetingMember.getLastname();

				meetingMemberDao.addMeetingMember(firstname, lastname, "0",
						"0", appointmentId, meetingMember.getUser_id(), email,
						meetingMember.getPhoneForSMS(), invitor,
						meetingMember.getOmTimeZone(), isConnectedEvent);

			} else {

				meetingMemberDao.addMeetingMember("", "", "0", "0",
						appointmentId, null, email, "", invitor,
						omTimeZoneDaoImpl.getOmTimeZone(sendJNameTimeZone),
						isConnectedEvent);

			}
		}

		return appointmentId;

	}

	public Long getNumberUnreadMessages(String SID) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				SearchResult<PrivateMessage> searchResult = new SearchResult<PrivateMessage>();
				searchResult.setObjectName(User.class.getName());
				return privateMessagesDao
						.getNumberMessages(users_id, 0L, false);

			}
		} catch (Exception err) {
			log.error("[getNumberUnreadMessages]", err);
		}
		return null;
	}

	public SearchResult<PrivateMessage> getInbox(String SID, String search,
			String orderBy, int start, Boolean asc, Integer max) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				SearchResult<PrivateMessage> searchResult = new SearchResult<PrivateMessage>();
				searchResult.setObjectName(User.class.getName());
				List<PrivateMessage> userList = privateMessagesDao
						.getPrivateMessagesByUser(users_id, search, orderBy,
								start, asc, 0L, max);

				searchResult.setResult(userList);

				Long resultInt = privateMessagesDao.countPrivateMessagesByUser(
						users_id, search, 0L);

				searchResult.setRecords(resultInt);

				return searchResult;

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}

	public SearchResult<PrivateMessage> getSend(String SID, String search,
			String orderBy, Integer start, Boolean asc, Integer max) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				SearchResult<PrivateMessage> searchResult = new SearchResult<PrivateMessage>();
				searchResult.setObjectName(User.class.getName());
				List<PrivateMessage> userList = privateMessagesDao
						.getSendPrivateMessagesByUser(users_id, search,
								orderBy, start, asc, 0L, max);

				searchResult.setResult(userList);

				Long resultInt = privateMessagesDao
						.countSendPrivateMessagesByUser(users_id, search, 0L);

				searchResult.setRecords(resultInt);

				return searchResult;

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}

	public SearchResult<PrivateMessage> getTrash(String SID, String search,
			String orderBy, Integer start, Boolean asc, Integer max) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				SearchResult<PrivateMessage> searchResult = new SearchResult<PrivateMessage>();
				searchResult.setObjectName(User.class.getName());
				List<PrivateMessage> userList = privateMessagesDao
						.getTrashPrivateMessagesByUser(users_id, search,
								orderBy, start, asc, max);

				searchResult.setResult(userList);

				Long resultInt = privateMessagesDao
						.countTrashPrivateMessagesByUser(users_id, search);

				searchResult.setRecords(resultInt);

				return searchResult;

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}

	public SearchResult<PrivateMessage> getFolder(String SID,
			Long privateMessageFolderId, String search, String orderBy,
			Integer start, Boolean asc, Integer max) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				SearchResult<PrivateMessage> searchResult = new SearchResult<PrivateMessage>();
				searchResult.setObjectName(User.class.getName());
				List<PrivateMessage> userList = privateMessagesDao
						.getFolderPrivateMessagesByUser(users_id, search,
								orderBy, start, asc, privateMessageFolderId,
								max);

				searchResult.setResult(userList);

				Long resultInt = privateMessagesDao
						.countFolderPrivateMessagesByUser(users_id,
								privateMessageFolderId, search);

				searchResult.setRecords(resultInt);

				return searchResult;

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}

	public Long getFolderCount(String SID, Long privateMessageFolderId) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				return privateMessagesDao.countFolderPrivateMessagesByUser(
						users_id, privateMessageFolderId, "");

			}

		} catch (Exception err) {
			log.error("[getInbox]", err);
		}
		return null;
	}

	public Integer moveMailsToFolder(String SID,
			@SuppressWarnings("rawtypes") List privateMessageIntsIds,
			Long newFolderId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<Long> privateMessageIds = new LinkedList<Long>();

				for (Object pMessageId : privateMessageIntsIds) {
					privateMessageIds.add(Long.valueOf(pMessageId.toString())
							.longValue());
				}

				return privateMessagesDao.moveMailsToFolder(privateMessageIds,
						newFolderId);

			}
		} catch (Exception err) {
			log.error("[moveMailsToFolder]", err);
		}
		return null;
	}

	public Integer moveMailsToTrash(String SID,
			@SuppressWarnings("rawtypes") List privateMessageIntsIds,
			Boolean isTrash) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<Long> privateMessageIds = new LinkedList<Long>();

				for (Object pMessageId : privateMessageIntsIds) {
					privateMessageIds.add(Long.valueOf(pMessageId.toString())
							.longValue());
				}

				log.debug("moveMailsToTrash :: " + isTrash);

				return privateMessagesDao.updatePrivateMessagesToTrash(
						privateMessageIds, isTrash, 0L);

			}
		} catch (Exception err) {
			log.error("[moveMailsToTrash]", err);
		}
		return -1;
	}

	public Integer deletePrivateMessages(String SID,
			@SuppressWarnings("rawtypes") List privateMessageIntsIds) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<Long> privateMessageIds = new LinkedList<Long>();

				for (Object pMessageId : privateMessageIntsIds) {
					privateMessageIds.add(Long.valueOf(pMessageId.toString())
							.longValue());
				}

				return privateMessagesDao
						.deletePrivateMessages(privateMessageIds);

			}
		} catch (Exception err) {
			log.error("[markReadStatusMails]", err);
		}
		return -1;
	}

	public Integer markReadStatusMails(String SID,
			@SuppressWarnings("rawtypes") List privateMessageIntsIds,
			Boolean isRead) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<Long> privateMessageIds = new LinkedList<Long>();

				for (Object pMessageId : privateMessageIntsIds) {
					privateMessageIds.add(Long.valueOf(pMessageId.toString())
							.longValue());
				}

				log.debug("markReadStatusMails :: " + isRead);

				return privateMessagesDao.updatePrivateMessagesReadStatus(
						privateMessageIds, isRead);

			}
		} catch (Exception err) {
			log.error("[markReadStatusMails]", err);
		}
		return -1;
	}

	public Integer markReadStatusMail(String SID, Long privateMessageId,
			Boolean isRead) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<Long> privateMessageIds = new LinkedList<Long>();
				privateMessageIds.add(privateMessageId);

				return privateMessagesDao.updatePrivateMessagesReadStatus(
						privateMessageIds, isRead);

				// PrivateMessages privateMessage =
				// privateMessagesDao.getPrivateMessagesById(privateMessageId);
				//
				// privateMessage.setIsRead(isRead);
				//
				// privateMessagesDao.updatePrivateMessages(privateMessage);

			}
		} catch (Exception err) {
			log.error("[markReadStatusMail]", err);
		}
		return null;
	}

	public List<PrivateMessageFolder> getPrivateMessageFoldersByUser(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				return privateMessageFolderDao
						.getPrivateMessageFolderByUserId(users_id);

			}

		} catch (Exception err) {
			log.error("[getPrivateMessageFolderByUser]", err);
		}
		return null;
	}

	public Long addPrivateMessageFolder(String SID, String folderName) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				privateMessageFolderDao.addPrivateMessageFolder(folderName,
						users_id);

			}

		} catch (Exception err) {
			log.error("[addPrivateMessageFolder]", err);
		}
		return null;
	}

	public Boolean checkUserIsInContactList(String SID, Long user_id) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<UserContact> uList = userContactsDao
						.getContactsByUserAndStatus(users_id, false);

				for (UserContact userContact : uList) {

					if (userContact.getContact().getUser_id().equals(user_id)) {
						return true;
					}

				}

				return false;

			}

		} catch (Exception err) {
			log.error("[checkUserIsInContactList]", err);
		}
		return null;
	}

	public void shareCalendarUserContact(String SID, Long userContactId,
			Boolean shareCalendar) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				UserContact userContacts = userContactsDao
						.getUserContacts(userContactId);

				userContacts.setShareCalendar(shareCalendar);

				userContactsDao.updateContact(userContacts);

			}

		} catch (Exception err) {
			log.error("[shareCalendarUserContact]", err);
		}
	}

	public Long updatePrivateMessageFolder(String SID,
			Long privateMessageFolderId, String folderName) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				PrivateMessageFolder privateMessageFolder = privateMessageFolderDao
						.getPrivateMessageFolderById(privateMessageFolderId);

				privateMessageFolder.setFolderName(folderName);
				privateMessageFolder.setUpdated(new Date());

				privateMessageFolderDao
						.updatePrivateMessages(privateMessageFolder);

				return privateMessageFolderId;

			}

		} catch (Exception err) {
			log.error("[updatePrivateMessageFolder]", err);
		}
		return null;
	}

	public Long deletePrivateMessageFolder(String SID,
			Long privateMessageFolderId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				PrivateMessageFolder privateMessageFolder = privateMessageFolderDao
						.getPrivateMessageFolderById(privateMessageFolderId);

				privateMessageFolderDao
						.deletePrivateMessages(privateMessageFolder);

			}

		} catch (Exception err) {
			log.error("[deletePrivateMessageFolder]", err);
		}
		return null;
	}

	public List<UserContact> getUserContactsWithShareCalendar(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				return userContactsDao.getContactsByShareCalendar(users_id,
						true);

			}

		} catch (Exception err) {
			log.error("[getContactsByShareCalendar]", err);
		}
		return null;
	}

	/**
	 * Kick a user by its publicSID.<br/>
	 * <br/>
	 * <i>Note:</i>
	 * This method will not perform a call to the slave, cause this call can only be 
	 * invoked from inside the conference room, that means all clients are on the
	 * same server, no matter if clustered or not.
	 * 
	 * @param SID
	 * @param publicSID
	 * @return - true in case user have sufficient permissions, null otherwise
	 */
	public Boolean kickUserByPublicSID(String SID, String publicSID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				Client rcl = this.sessionManager.getClientByPublicSID(
						publicSID, false, null);

				if (rcl == null) {
					return true;
				}
				String scopeName = "hibernate";
				if (rcl.getRoom_id() != null) {
					scopeName = rcl.getRoom_id().toString();
				}
				IScope currentScope = this.scopeApplicationAdapter
						.getRoomScope(scopeName);

				HashMap<Integer, String> messageObj = new HashMap<Integer, String>();
				messageObj.put(0, "kick");

				this.scopeApplicationAdapter.sendMessageById(messageObj,
						rcl.getStreamid(), currentScope);

				this.scopeApplicationAdapter.roomLeaveByScope(rcl,
						currentScope, true);

				return true;
			}

		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return null;
	}
}
