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
package org.apache.openmeetings.data.user;

import static org.apache.openmeetings.db.util.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SOAP_REGISTER_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.OrganisationUserDao;
import org.apache.openmeetings.db.dao.user.StateDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.Userdata;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.mail.MailHandler;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.util.AuthLevelUtil;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.DaoHelper;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author swagner
 * 
 */
@Transactional
public class UserManager implements IUserManager {
	private static final Logger log = Red5LoggerFactory.getLogger(UserManager.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private StateDao statemanagement;
	@Autowired
	private OrganisationDao orgDao;
	@Autowired
	private OrganisationUserDao orgUserDao;
	@Autowired
	private UserDao usersDao;
	@Autowired
	private EmailManager emailManagement;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private TimezoneUtil timezoneUtil;

	public SearchResult<User> getAllUserByRange(String search, int start, int max,
			String orderby, boolean asc) {
		try {
			SearchResult<User> sresult = new SearchResult<User>();
			sresult.setObjectName(User.class.getName());
			sresult.setRecords(usersDao.count(search));

			String sort = null;
			if (orderby != null && orderby.length() > 0) {
				sort = orderby;
			}
			if (asc) {
				sort += " ASC ";
			} else {
				sort += " DESC ";
			}
			String hql = DaoHelper.getSearchQuery("User", "u", search, true, false, sort, UserDao.searchFields);

			log.debug("Show HQL: " + hql);

			TypedQuery<User> query = em.createQuery(hql, User.class);
			// query.setParameter("macomUserId", userId);

			// query
			// if (asc) ((Criteria) query).addOrder(Order.asc(orderby));
			// else ((Criteria) query).addOrder(Order.desc(orderby));
			query.setFirstResult(start);
			query.setMaxResults(max);
			List<User> ll = query.getResultList();

			sresult.setResult(ll);

			return sresult;

		} catch (Exception ex2) {
			log.error("[getAllUserByRange] ", ex2);
		}
		return null;
	}

	public User loginUserByRemoteHash(String SID, String remoteHash) {
		try {

			Sessiondata sessionData = sessiondataDao
					.getSessionByHash(remoteHash);

			if (sessionData != null) {

				User u = usersDao.get(sessionData.getUser_id());

				sessiondataDao.updateUserWithoutSession(SID, u.getUser_id());

				return u;
			}

		} catch (Exception ex2) {
			log.error("[loginUserByRemoteHash]: ", ex2);
		}
		return null;
	}

	public Long logout(String SID, long USER_ID) {
		sessiondataDao.updateUser(SID, 0, false, null);
		return -12L;
	}

	public List<Userdata> getUserdataDashBoard(Long user_id) {
		if (user_id != null && user_id.longValue() > 0) {
			try {
				TypedQuery<Userdata> query = em.createQuery("select c from Userdata as c where c.user_id = :user_id AND c.deleted = false", Userdata.class);
				query.setParameter("user_id", user_id);
				List<Userdata> ll = query.getResultList();
				return ll;
			} catch (Exception ex2) {
				log.error("getUserdataDashBoard", ex2);
			}
		}
		return null;
	}

	public Userdata getUserdataByKey(Long user_id, String DATA_KEY) {
		Userdata userdata = new Userdata();
		if (user_id != null && user_id.longValue() > 0) {
			try {
				TypedQuery<Userdata> query = em.createQuery("select c from Userdata as c where c.user_id = :user_id AND c.data_key = :data_key AND c.deleted = false", Userdata.class);
				query.setParameter("user_id", user_id);
				query.setParameter("data_key", DATA_KEY);
				for (Iterator<Userdata> it2 = query.getResultList().iterator(); it2.hasNext();) {
					userdata = it2.next();
				}
			} catch (Exception ex2) {
				log.error("getUserdataByKey", ex2);
			}
		} else {
			userdata.setComment("Error: No USER_ID given");
		}
		return userdata;
	}

	public String updateUserdata(int DATA_ID, long USER_ID, String DATA_KEY,
			String DATA, String Comment) {
		String res = "Fehler beim Update";
		try {
			String hqlUpdate = "update userdata set DATA_KEY= :DATA_KEY, USER_ID = :USER_ID, DATA = :DATA, updatetime = :updatetime, comment = :Comment where DATA_ID= :DATA_ID";
			int updatedEntities = em.createQuery(hqlUpdate)
					.setParameter("DATA_KEY", DATA_KEY)
					.setParameter("USER_ID", USER_ID)
					.setParameter("DATA", DATA)
					.setParameter("updatetime", -1L)
					.setParameter("Comment", Comment)
					.setParameter("DATA_ID", DATA_ID).executeUpdate();
			res = "Success" + updatedEntities;
		} catch (Exception ex2) {
			log.error("updateUserdata", ex2);
		}
		return res;
	}

	public String updateUserdataByKey(Long USER_ID, String DATA_KEY,
			String DATA, String Comment) {
		String res = "Fehler beim Update";
		try {
			String hqlUpdate = "UPDATE Userdata set data = :data, updatetime = :updatetime, "
					+ "comment = :comment where user_id= :user_id AND data_key = :data_key";
			int updatedEntities = em.createQuery(hqlUpdate)
					.setParameter("data", DATA)
					.setParameter("updatetime", -1L)
					.setParameter("comment", Comment)
					.setParameter("user_id", USER_ID)
					.setParameter("data_key", DATA_KEY).executeUpdate();
			res = "Success" + updatedEntities;
		} catch (Exception ex2) {
			log.error("updateUserdataByKey", ex2);
		}
		return res;
	}

	public String addUserdata(long USER_ID, String DATA_KEY, String DATA,
			String Comment) {
		String ret = "Fehler beim speichern der Userdata";
		Userdata userdata = new Userdata();
		userdata.setData_key(DATA_KEY);
		userdata.setData(DATA);
		userdata.setStarttime(new Date());
		userdata.setUpdatetime(null);
		userdata.setComment(Comment);
		userdata.setUser_id(USER_ID);
		userdata.setDeleted(false);
		try {
			em.merge(userdata);
			ret = "success";
		} catch (Exception ex2) {
			log.error("addUserdata", ex2);
		}
		return ret;
	}

	/**
	 * Method to register a new User, User will automatically be added to the
	 * default user_level(1) new users will be automatically added to the
	 * Organisation with the id specified in the configuration value
	 * default_domain_id
	 * 
	 * @param user_level
	 * @param availible
	 * @param status
	 * @param login
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
	 * @return
	 */
	public Long registerUser(String login, String Userpass, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, String phone, boolean sendSMS, boolean generateSipUserData, String jNameTimeZone) {
		
		String baseURL = configurationDao.getBaseUrl();
		boolean sendConfirmation = baseURL != null
				&& !baseURL.isEmpty()
				&& 1 == configurationDao.getConfValue("sendEmailWithVerficationCode", Integer.class, "0");
		
		return registerUser(login, Userpass, lastname, firstname, email, age,
				street, additionalname, fax, zip, states_id, town, language_id,
				phone, sendSMS, generateSipUserData, jNameTimeZone, sendConfirmation);
	}

	public Long registerUserNoEmail(String login, String Userpass,
			String lastname, String firstname, String email, Date age,
			String street, String additionalname, String fax, String zip,
			long states_id, String town, long language_id, String phone, boolean sendSMS, 
			boolean generateSipUserData, String jNameTimeZone) {
		
		return registerUser(login, Userpass, lastname, firstname, email, age,
				street, additionalname, fax, zip, states_id, town, language_id,
				phone, sendSMS, generateSipUserData, jNameTimeZone, false);
	}

	private Long registerUser(String login, String Userpass, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, String phone, boolean sendSMS,
			boolean generateSipUserData, String jNameTimeZone, Boolean sendConfirmation) {
		try {
			// Checks if FrontEndUsers can register
			if ("1".equals(configurationDao.getConfValue(CONFIG_SOAP_REGISTER_KEY, String.class, "0"))) {
				
				// TODO: Read and generate SIP-Data via RPC-Interface Issue 1098

				Long user_id = registerUserInit(UserDao.getDefaultRights(), login,
						Userpass, lastname, firstname, email, age, street,
						additionalname, fax, zip, states_id, town, language_id,
						true, Arrays.asList(configurationDao.getConfValue("default_domain_id", Long.class, null)), phone,
						sendSMS, sendConfirmation, timezoneUtil.getTimeZone(jNameTimeZone), false, "", "", false, true, null);

				if (user_id > 0 && sendConfirmation) {
					return -40L;
				}

				return user_id;
			}
		} catch (Exception e) {
			log.error("[registerUser]", e);
		}
		return null;
	}

	/**
	 * @param user_level
	 * @param availible
	 * @param status
	 * @param login
	 * @param password
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
	 * @param sendWelcomeMessage
	 * @param organisations
	 * @param phone
	 * @param sendSMS
	 * @param sendConfirmation
	 * @param timezone
	 * @param forceTimeZoneCheck
	 * @param userOffers
	 * @param userSearchs
	 * @param showContactData
	 * @param showContactDataToContacts
	 * @return new users_id OR null if an exception, -1 if an error, -4 if mail
	 *         already taken, -5 if username already taken, -3 if login or pass
	 *         or mail is empty
	 * @throws Exception
	 */
	public Long registerUserInit(Set<Right> rights, String login, String password, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, boolean sendWelcomeMessage,
			List<Long> organisations, String phone, boolean sendSMS, Boolean sendConfirmation,
			TimeZone timezone, Boolean forceTimeZoneCheck,
			String userOffers, String userSearchs, Boolean showContactData,
			Boolean showContactDataToContacts, String activatedHash) throws Exception {
		// TODO: make phone number persistent
		// Check for required data
		if (login.length() >= getMinLoginLength(configurationDao)) {
			// Check for duplicates
			boolean checkName = usersDao.checkLogin(login, User.Type.user, null, null);
			boolean checkEmail = Strings.isEmpty(email) || usersDao.checkEmail(email, User.Type.user, null, null);
			if (checkName && checkEmail) {

				String link = configurationDao.getBaseUrl();
				String hash = activatedHash;
				if (hash == null){
					hash = ManageCryptStyle.getInstanceOfCrypt().createPassPhrase(login
							+ CalendarPatterns.getDateWithTimeByMiliSeconds(new Date()));
				}
				link += "activate?u=" + hash;

				if (sendWelcomeMessage && email.length() != 0) {
					String sendMail = emailManagement.sendMail(login,
							password, email, link, sendConfirmation);
					if (!sendMail.equals("success"))
						return -19L;
				}
				Address adr =  usersDao.getAddress(street, zip, town, states_id, additionalname, fax, phone, email);

				// If this user needs first to click his E-Mail verification
				// code then set the status to 0
				if (sendConfirmation && rights.contains(Right.Login)) {
					rights.remove(Right.Login);
				}

				List<Organisation_Users> orgList = new ArrayList<Organisation_Users>();
				for (Long id : organisations) {
					orgList.add(new Organisation_Users(orgDao.get(id)));
				}
				User u = usersDao.addUser(rights, firstname, login, lastname, language_id,
						password, adr, sendSMS, age, hash, timezone,
						forceTimeZoneCheck, userOffers, userSearchs, showContactData,
						showContactDataToContacts, null, null, orgList, null);
				if (u == null) {
					return -111L;
				}
				log.debug("Added user-Id " + u.getUser_id());

				/*
				 * Long adress_emails_id =
				 * emailManagement.registerEmail(email, address_id,""); if
				 * (adress_emails_id==null) { return new Long(-112); }
				 */

				if (adr.getAdresses_id() > 0 && u.getUser_id() > 0) {
					return u.getUser_id();
				} else {
					return -16L;
				}
			} else {
				if (!checkName) {
					return -15L;
				} else if (!checkEmail) {
					return -17L;
				}
			}
		}
		return -1L;
	}

	/**
	 * @param admin
	 * @param room_id
	 * @return
	 */
	public Boolean kickUserByStreamId(String SID, Long room_id) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);

			// admins only
			if (AuthLevelUtil.hasAdminLevel(usersDao.getRights(users_id))) {

				sessiondataDao.clearSessionByRoomId(room_id);

				for (Client rcl : sessionManager.getClientListByRoom(room_id)) {
					if (rcl == null) {
						return true;
					}
					String scopeName = "hibernate";
					if (rcl.getRoom_id() != null) {
						scopeName = rcl.getRoom_id().toString();
					}
					IScope currentScope = scopeApplicationAdapter
							.getRoomScope(scopeName);
					scopeApplicationAdapter.roomLeaveByScope(rcl, currentScope, true);

					HashMap<Integer, String> messageObj = new HashMap<Integer, String>();
					messageObj.put(0, "kick");
					scopeApplicationAdapter.sendMessageById(messageObj,
							rcl.getStreamid(), currentScope);

				}

				return true;
			}

		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return null;
	}

	public Boolean kickUserByPublicSID(String SID, String publicSID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);

			// admins only
			if (AuthLevelUtil.hasWebServiceLevel(usersDao.getRights(users_id))) {

				Client rcl = sessionManager
						.getClientByPublicSID(publicSID, false, null);

				if (rcl == null) {
					return true;
				}

				String scopeName = "hibernate";
				if (rcl.getRoom_id() != null) {
					scopeName = rcl.getRoom_id().toString();
				}
				IScope currentScope = scopeApplicationAdapter
						.getRoomScope(scopeName);

				HashMap<Integer, String> messageObj = new HashMap<Integer, String>();
				messageObj.put(0, "kick");
				scopeApplicationAdapter.sendMessageById(messageObj,
						rcl.getStreamid(), currentScope);

				scopeApplicationAdapter.roomLeaveByScope(rcl, currentScope, true);

				return true;
			}

		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return null;
	}
}
