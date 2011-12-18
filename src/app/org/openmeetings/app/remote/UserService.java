package org.openmeetings.app.remote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.calendar.daos.AppointmentDaoImpl;
import org.openmeetings.app.data.calendar.daos.MeetingMemberDaoImpl;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Salutationmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.PrivateMessageFolderDaoImpl;
import org.openmeetings.app.data.user.dao.PrivateMessagesDaoImpl;
import org.openmeetings.app.data.user.dao.UserContactsDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.domain.Organisation;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.persistence.beans.rooms.Rooms;
import org.openmeetings.app.persistence.beans.user.PrivateMessageFolder;
import org.openmeetings.app.persistence.beans.user.PrivateMessages;
import org.openmeetings.app.persistence.beans.user.Salutations;
import org.openmeetings.app.persistence.beans.user.UserContacts;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.templates.RequestContactConfirmTemplate;
import org.openmeetings.app.templates.RequestContactTemplate;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.openmeetings.utils.mail.MailHandler;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.io.utils.ObjectMap;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IScope;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class UserService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UserService.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	private ClientListManager clientListManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private AppointmentDaoImpl appointmentDao;
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;
	@Autowired
	private Salutationmanagement salutationmanagement;
	@Autowired
	private Organisationmanagement organisationmanagement;
	@Autowired
	private ManageCryptStyle manageCryptStyle;
	@Autowired
	private Roommanagement roommanagement;
	@Autowired
	private MeetingMemberDaoImpl meetingMemberDao;
	@Autowired
	private PrivateMessagesDaoImpl privateMessagesDao;
	@Autowired
	private PrivateMessageFolderDaoImpl privateMessageFolderDao;
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private UserContactsDaoImpl userContactsDao;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private RequestContactTemplate requestContactTemplate;
	@Autowired
	private RequestContactConfirmTemplate requestContactConfirmTemplate;
	@Autowired
	private AuthLevelmanagement authLevelManagement;

	/**
	 * get your own user-object
	 * 
	 * @param SID
	 * @param user_id
	 * @return
	 */
	public Users getUserSelf(String SID) {
		Long users_id = sessionManagement.checkSession(SID);
		return usersDao.getUser(users_id);
	}

	public Long resetUserPwd(String SID, String email, String login,
			String applink) {
		sessionManagement.checkSession(SID);
		return userManagement.resetUser(email, login, applink);
	}

	public Object getUserByHash(String SID, String hash) {
		sessionManagement.checkSession(SID);
		return usersDao.getUserByHash(hash);
	}

	public Object resetPassByHash(String SID, String hash, String pass) {
		sessionManagement.checkSession(SID);
		return usersDao.resetPassByHash(hash, pass);
	}

	/**
	 * get user by id, admin only
	 * 
	 * @param SID
	 * @param user_id
	 * @return
	 */
	public Users getUserById(String SID, long user_id) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return userManagement.checkAdmingetUserById(user_level, user_id);
	}

	/**
	 * refreshes the current SID
	 * 
	 * @param SID
	 * @return
	 */
	public String refreshSession(String SID) {
		try {
			sessionManagement.checkSession(SID);
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
	 * @return
	 */
	public List<Salutations> getUserSalutations(String SID, long language_id) {
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
	 * @return
	 */
	public List<Users> searchUser(String SID, String searchcriteria,
			String searchstring, int max, int start, String orderby, boolean asc) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return userManagement.searchUser(user_level, searchcriteria,
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
	 * @return
	 */
	public List<Users> getOrgUserList(String SID, long organisation_id,
			int start, int max, String orderby, boolean asc) {
		return organisationmanagement.getUsersByOrganisationId(organisation_id,
				start, max, orderby, asc);
	}

	public List<Users> getUserListByModForm(String SID) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return userManagement.getUserByMod(user_level, users_id);
	}

	/**
	 * gat a list of all organisations of an user
	 * 
	 * @param SID
	 * @param client_user
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<Organisation> getOrganisationListByUser(String SID,
			long client_user, int start, int max, String orderby, boolean asc) {
		Long users_id = sessionManagement.checkSession(SID);
		long user_level = userManagement.getUserLevelByID(users_id);
		return organisationmanagement.getOrganisationsByUserId(user_level,
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
	 * @return
	 */
	public List<Organisation> getRestOrganisationListByUser(String SID,
			long client_user, int start, int max, String orderby, boolean asc) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return organisationmanagement.getRestOrganisationsByUserId(user_level,
				client_user, start, max, orderby, asc);
	}

	/**
	 * gets a whole user-list(admin-role only)
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @return
	 */
	public SearchResult getUserList(String SID, int start, int max,
			String orderby, boolean asc) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return userManagement
				.getUsersList(user_level, start, max, orderby, asc);
	}

	public SearchResult getUserListWithSearch(String SID, int start, int max,
			String orderby, boolean asc, String search) {
		Long users_id = sessionManagement.checkSession(SID);
		Long user_level = userManagement.getUserLevelByID(users_id);
		return userManagement.getUsersListWithSearch(user_level, start, max,
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
	 * @return
	 */
	public SearchResult getAllUserBySearchRange(String SID, String search,
			int start, int max, String orderby, boolean asc) {
		return userManagement.getAllUserByRange(search, start, max, orderby,
				asc);
	}

	/**
	 * updates the user profile, every user can update his own profile
	 * 
	 * @param SID
	 * @param argObject
	 * @return user_id or NULL or negativ value (error_id)
	 */
	public Long updateUserSelfSmall(String SID,
			@SuppressWarnings("rawtypes") ObjectMap values) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			if (user_level != null && user_level >= 1) {
				return userManagement.saveOrUpdateUser(new Long(3), values,
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
	 * @return
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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			
			List<?> orgO = (List<?>)argObjectMap.get("organisations");
			List<Long> orgIds = new ArrayList<Long>(orgO.size());
			for (Object o : orgO) {
				orgIds.add(Long.valueOf((Integer)o));
			}
			Date age = null;
			if (argObjectMap.get("userage") instanceof Date) {
				age = (Date) argObjectMap.get("userage");
			}

			log.debug("Mail : " + argObjectMap.get("email").toString());
			log.debug("Phone : " + argObjectMap.get("phone").toString());

			if (user_idClient == null || user_idClient == 0) {
				return userManagement.registerUserInit(
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
						"",
						false,
						argObjectMap.get("sip_user").toString(),
						argObjectMap.get("sip_pass").toString(),
						argObjectMap.get("sip_auth").toString(),
						Boolean.valueOf(
								argObjectMap.get("generateSipUserData")
										.toString()).booleanValue(),
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
				return userManagement
						.updateUser(
								user_level,
								user_idClient,
								Long.valueOf(
										argObjectMap.get("level_id").toString())
										.longValue(),
								argObjectMap.get("login").toString(),
								argObjectMap.get("password").toString(),
								argObjectMap.get("lastname").toString(),
								argObjectMap.get("firstname").toString(),
								age,
								argObjectMap.get("street").toString(),
								argObjectMap.get("additionalname").toString(),
								argObjectMap.get("zip").toString(),
								Long.valueOf(
										argObjectMap.get("states_id")
												.toString()).longValue(),
								argObjectMap.get("town").toString(),
								new Long(argObjectMap.get("language_id").toString()),
								Integer.valueOf(
										argObjectMap.get("availible")
												.toString()).intValue(),
								argObjectMap.get("telefon").toString(),
								argObjectMap.get("fax").toString(),
								argObjectMap.get("mobil").toString(),
								argObjectMap.get("email").toString(),
								argObjectMap.get("comment").toString(),
								Integer.valueOf(
										argObjectMap.get("status").toString())
										.intValue(),
								orgIds,
								Integer.valueOf(
										argObjectMap.get("title_id").toString())
										.intValue(),
								argObjectMap.get("phone").toString(),
								argObjectMap.get("sip_user").toString(),
								argObjectMap.get("sip_pass").toString(),
								argObjectMap.get("sip_auth").toString(),
								Boolean.valueOf(
										argObjectMap.get("generateSipUserData")
												.toString()).booleanValue(),
								argObjectMap.get("jNameTimeZone").toString(),
								Boolean.valueOf(
										argObjectMap.get("forceTimeZoneCheck")
												.toString()).booleanValue(),
								argObjectMap.get("userOffers").toString(),
								argObjectMap.get("userSearchs").toString(),
								Boolean.valueOf(
										argObjectMap.get("showContactData")
												.toString()).booleanValue(),
								Boolean.valueOf(
										argObjectMap.get(
												"showContactDataToContacts")
												.toString()).booleanValue());
			}
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
	 * @return
	 */
	public Long deleteUserAdmin(String SID, Long user_idClient) {
		log.debug("deleteUserAdmin");
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			// admins only
			if (authLevelManagement.checkAdminLevel(user_level)) {
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

	public Boolean kickUserByStreamId(String SID, String streamid) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// admins only
			if (authLevelManagement.checkAdminLevel(user_level)) {
				RoomClient rcl = this.clientListManager
						.getClientByStreamId(streamid);

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

				this.scopeApplicationAdapter
						.roomLeaveByScope(rcl, currentScope);

				return true;
			}

		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return null;
	}

	public Users updateUserSelfTimeZone(String SID, String jname) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				Users us = userManagement.getUserById(users_id);

				us.setOmTimeZone(omTimeZoneDaoImpl.getOmTimeZone(jname));
				us.setForceTimeZoneCheck(false);
				us.setUpdatetime(new Date());

				userManagement.updateUser(us);

				return us;

			}
		} catch (Exception err) {
			log.error("[updateUserTimeZone]", err);
		}
		return null;
	}

	public SearchResult searchUserProfile(String SID, String searchTxt,
			String userOffers, String userSearchs, String orderBy, int start,
			int max, boolean asc) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				SearchResult searchResult = new SearchResult();
				searchResult.setObjectName(Users.class.getName());
				List<Users> userList = userManagement.searchUserProfile(
						searchTxt, userOffers, userSearchs, orderBy, start,
						max, asc);
				searchResult.setResult(userList);
				Long resultInt = userManagement.searchCountUserProfile(
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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				Long countContacts = userContactsDao.checkUserContacts(
						userToAdd_id, users_id);

				if (countContacts != null && countContacts > 0) {
					return -45L;
				}

				String hash = manageCryptStyle
						.getInstanceOfCrypt()
						.createPassPhrase(
								CalendarPatterns
										.getDateWithTimeByMiliSeconds(new Date()));

				Long userContactId = userContactsDao.addUserContact(
						userToAdd_id, users_id, true, hash);

				Users user = userManagement.getUserById(users_id);

				Users userToAdd = userManagement.getUserById(userToAdd_id);

				Long language_id = userToAdd.getLanguage_id();
				if (language_id == null) {
					language_id = Long.valueOf(
							cfgManagement.getConfKey(3, "default_lang_id")
									.getConf_value()).longValue();
				}

				String message = "";

				Fieldlanguagesvalues fValue1192 = fieldmanagment
						.getFieldByIdAndLanguage(1192L, language_id);
				Fieldlanguagesvalues fValue1193 = fieldmanagment
						.getFieldByIdAndLanguage(1193L, language_id);
				Fieldlanguagesvalues fValue1194 = fieldmanagment
						.getFieldByIdAndLanguage(1194L, language_id);
				Fieldlanguagesvalues fValue1190 = fieldmanagment
						.getFieldByIdAndLanguage(1190L, language_id);
				Fieldlanguagesvalues fValue1191 = fieldmanagment
						.getFieldByIdAndLanguage(1191L, language_id);
				Fieldlanguagesvalues fValue1196 = fieldmanagment
						.getFieldByIdAndLanguage(1196L, language_id);

				message += fValue1192.getValue() + " "
						+ userToAdd.getFirstname() + " "
						+ userToAdd.getLastname() + "<br/><br/>";
				message += user.getFirstname() + " " + user.getLastname() + " "
						+ fValue1193.getValue() + "<br/>";
				message += fValue1194.getValue() + "<br/>";

				String baseURL = "http://" + domain + ":" + port + webapp;
				if (port.equals("80")) {
					baseURL = "http://" + domain + webapp;
				} else if (port.equals("443")) {
					baseURL = "https://" + domain + webapp;
				}

				privateMessagesDao.addPrivateMessage(user.getFirstname() + " "
						+ user.getLastname() + " " + fValue1193.getValue(),
						message, 0L, user, userToAdd, userToAdd, false, null,
						true, userContactId);

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
					mailHandler.sendMail(userToAdd.getAdresses().getEmail(),
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

	public List<UserContacts> getPendingUserContacts(String SID) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				List<UserContacts> uList = userContactsDao
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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				UserContacts userContact = userContactsDao
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

	public List<UserContacts> getUserContacts(String SID) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				List<UserContacts> uList = userContactsDao
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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				UserContacts userContacts = userContactsDao
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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				UserContacts userContacts = userContactsDao
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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				UserContacts userContacts = userContactsDao
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

					Users user = userContacts.getOwner();

					if (user.getAdresses() != null) {

						Long language_id = user.getLanguage_id();
						if (language_id == null) {
							language_id = Long.valueOf(
									cfgManagement.getConfKey(3,
											"default_lang_id").getConf_value())
									.longValue();
						}

						String message = "";

						Fieldlanguagesvalues fValue1192 = fieldmanagment
								.getFieldByIdAndLanguage(1192L, language_id);
						Fieldlanguagesvalues fValue1198 = fieldmanagment
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
								false, null, false, 0L);

						mailHandler.sendMail(user.getAdresses().getEmail(),
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

	private Calendar createCalendar(Date date, String time) {
		Integer hour = Integer.valueOf(
				time.substring(0, 2)).intValue();
		Integer minute = Integer.valueOf(
				time.substring(3, 5)).intValue();

		log.info("createCalendar Hour: " + hour);
		log.info("createCalendar Minute: " + minute);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal;
	}
	
	public Long composeMail(String SID, List<String> recipients,
			String subject, String message, Boolean bookedRoom,
			Date validFromDate, String validFromTime, Date validToDate,
			String validToTime, Long parentMessageId, Long roomtype_id,
			String domain, String port, String webapp) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {
				Calendar calFrom = createCalendar(validFromDate, validFromTime);

				Calendar calTo = createCalendar(validToDate, validToTime);

				Date appointmentstart = calFrom.getTime();
				Date appointmentend = calTo.getTime();

				log.info("validFromDate: "
						+ CalendarPatterns
								.getDateWithTimeByMiliSeconds(appointmentstart));
				log.info("validToDate: "
						+ CalendarPatterns
								.getDateWithTimeByMiliSeconds(appointmentend));

				Users from = userManagement.getUserById(users_id);

				Rooms room = null;

				if (bookedRoom) {
					Long room_id = roommanagement.addRoom(3, // Userlevel
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
							false, // isClosed
							"", // redirectURL
							"", // sipNumber
							"", // conferencePIN
							null, // ownerId
							null, null, false);

					room = roommanagement.getRoomById(room_id);

				}

				recipients.add(from.getAdresses().getEmail());

				String sendJNameTimeZone = from.getOmTimeZone().getJname();

				String baseURL = "http://" + domain + ":" + port + webapp;
				if (port.equals("80")) {
					baseURL = "http://" + domain + webapp;
				} else if (port.equals("443")) {
					baseURL = "https://" + domain + webapp;
				}

				String profile_link = baseURL + "?cuser=1";

				for (String email : recipients) {

					// Map receipent = (Map) recipients.get(iter.next());

					// String email = receipent.get("email").toString();

					Users to = userManagement.getUserByEmail(email);

					if (to == null) {
						throw new Exception("Could not find user " + email);
					}

					Boolean invitor = false;
					if (email.equals(from.getAdresses().getEmail())) {
						invitor = true;
					} else {

						// One message to the Send
						privateMessagesDao.addPrivateMessage(subject, message,
								parentMessageId, from, to, from, bookedRoom,
								room, false, 0L);

						// One message to the Inbox
						privateMessagesDao.addPrivateMessage(subject, message,
								parentMessageId, from, to, to, bookedRoom,
								room, false, 0L);

						// One copy of the Inbox message to the user
						Long language_id = to.getLanguage_id();
						if (language_id == null) {
							language_id = Long.valueOf(
									cfgManagement.getConfKey(3,
											"default_lang_id").getConf_value())
									.longValue();
						}

						Fieldlanguagesvalues fValue1301 = fieldmanagment
								.getFieldByIdAndLanguage(1301L, language_id);
						Fieldlanguagesvalues fValue1302 = fieldmanagment
								.getFieldByIdAndLanguage(1302L, language_id);

						String aLinkHTML = "<br/><br/><a href='" + profile_link
								+ "'>" + fValue1302.getValue() + "</a><br/>";

						mailHandler.sendMail(email, fValue1301.getValue() + " "
								+ subject, message.replaceAll("\\<.*?>", "")
								+ aLinkHTML);

					}

					if (bookedRoom) {

						// But add the appointment to everybody
						this.addAppointmentToUser(subject, message, to,
								recipients, room, appointmentstart,
								appointmentend, invitor, true,
								sendJNameTimeZone);

					}
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
	private void addAppointmentToUser(String subject, String message, Users to,
			List<String> recipients, Rooms room, Date appointmentstart,
			Date appointmentend, Boolean invitor, Boolean isConnectedEvent,
			String sendJNameTimeZone) throws Exception {
		
		

		Long appointmentId = appointmentDao.addAppointment(subject,
				to.getUser_id(), "", message, appointmentstart, appointmentend,
				false, false, false, false, 1L, 2L, room, to.getLanguage_id(),
				false, "", isConnectedEvent, sendJNameTimeZone);

		for (String email : recipients) {

			Users meetingMember = userManagement.getUserByEmail(email);

			String firstname = meetingMember.getFirstname();
			String lastname = meetingMember.getLastname();

			meetingMemberDao.addMeetingMember(firstname, lastname, "0", "0",
					appointmentId, meetingMember.getUser_id(), email, invitor,
					meetingMember.getOmTimeZone(), isConnectedEvent);

		}

	}

	public SearchResult getInbox(String SID, String search, String orderBy,
			int start, Boolean asc, Integer max) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				SearchResult searchResult = new SearchResult();
				searchResult.setObjectName(Users.class.getName());
				List<PrivateMessages> userList = privateMessagesDao
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

	public SearchResult getSend(String SID, String search, String orderBy,
			Integer start, Boolean asc, Integer max) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				SearchResult searchResult = new SearchResult();
				searchResult.setObjectName(Users.class.getName());
				List<PrivateMessages> userList = privateMessagesDao
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

	public SearchResult getTrash(String SID, String search, String orderBy,
			Integer start, Boolean asc, Integer max) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				SearchResult searchResult = new SearchResult();
				searchResult.setObjectName(Users.class.getName());
				List<PrivateMessages> userList = privateMessagesDao
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

	public SearchResult getFolder(String SID, Long privateMessageFolderId,
			String search, String orderBy, Integer start, Boolean asc,
			Integer max) {
		try {

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				SearchResult searchResult = new SearchResult();
				searchResult.setObjectName(Users.class.getName());
				List<PrivateMessages> userList = privateMessagesDao
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

			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				List<UserContacts> uList = userContactsDao
						.getContactsByUserAndStatus(users_id, false);

				for (UserContacts userContact : uList) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);

			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				UserContacts userContacts = userContactsDao
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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

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

	public List<UserContacts> getUserContactsWithShareCalendar(String SID) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				return userContactsDao.getContactsByShareCalendar(users_id,
						true);

			}

		} catch (Exception err) {
			log.error("[getContactsByShareCalendar]", err);
		}
		return null;
	}

	public Boolean kickUserByPublicSID(String SID, String publicSID) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
			Long user_level = userManagement.getUserLevelByID(users_id);
			// users only
			if (authLevelManagement.checkUserLevel(user_level)) {

				RoomClient rcl = this.clientListManager
						.getClientByPublicSID(publicSID);

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

				this.scopeApplicationAdapter
						.roomLeaveByScope(rcl, currentScope);

				return true;
			}

		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return null;
	}
}
