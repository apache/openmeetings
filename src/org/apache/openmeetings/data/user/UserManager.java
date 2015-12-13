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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.dao.StateDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.persistence.beans.user.Address;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.persistence.beans.user.Userdata;
import org.apache.openmeetings.persistence.beans.user.Userlevel;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.templates.ResetPasswordTemplate;
import org.apache.openmeetings.utils.DaoHelper;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.apache.openmeetings.utils.mail.MailHandler;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.red5.io.utils.ObjectMap;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IClient;
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
public class UserManager {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UserManager.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private StateDao statemanagement;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private OrganisationManager organisationManager;
	@Autowired
	private ManageCryptStyle cryptManager;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private EmailManager emailManagement;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private ResetPasswordTemplate resetPasswordTemplate;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private ISessionManager sessionManager;

	/**
	 * query for a list of users
	 * 
	 * @param users_id
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @return
	 */
	public SearchResult<User> getUsersList(long user_level, int start, int max,
			String orderby, boolean asc) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				SearchResult<User> sresult = new SearchResult<User>();
				sresult.setObjectName(User.class.getName());
				sresult.setRecords(usersDao.count());

				// get all users
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<User> cq = cb.createQuery(User.class);
				Root<User> c = cq.from(User.class);
				Predicate condition = cb.equal(c.get("deleted"), false);
				cq.where(condition);
				cq.distinct(asc);
				if (asc) {
					cq.orderBy(cb.asc(c.get(orderby)));
				} else {
					cq.orderBy(cb.desc(c.get(orderby)));
				}
				TypedQuery<User> q = em.createQuery(cq);
				q.setFirstResult(start);
				q.setMaxResults(max);
				List<User> ll = q.getResultList();
				sresult.setResult(ll);
				return sresult;
			}
		} catch (Exception ex2) {
			log.error("[getUsersList] " + ex2);
		}
		return null;
	}

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
			String hql = DaoHelper.getSearchQuery("User", "u", search, true, false, sort, UsersDao.searchFields);

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
			ex2.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param user_level
	 * @param user_id
	 * @return
	 */
	public User checkAdmingetUserById(long user_level, long user_id) {
		// FIXME: We have to check here for the User only cause the
		// Org-Moderator otherwise cannot access it
		if (authLevelUtil.checkUserLevel(user_level)) {
			return usersDao.get(user_id);
		}
		return null;
	}

	public List<User> getUserByMod(Long user_level, long user_id) {
		return null;
	}

	/**
	 * login logic
	 * 
	 * @param SID
	 * @param Username
	 * @param Userpass
	 * @return
	 */
	public Object loginUser(String SID, String userOrEmail, String userpass,
			Client currentClient, IClient client, Boolean storePermanent) {
		try {
			log.debug("Login user SID : " + SID + " Stored Permanent :"
					+ storePermanent);
			String hql = "SELECT c from User AS c "
					+ "WHERE "
					+ "(c.login LIKE :userOrEmail OR c.adresses.email LIKE :userOrEmail  ) "
					+ "AND c.deleted <> :deleted";

			TypedQuery<User> query = em.createQuery(hql, User.class);
			query.setParameter("userOrEmail", userOrEmail);
			query.setParameter("deleted", true);

			List<User> ll = query.getResultList();

			log.debug("debug SIZE: " + ll.size());

			if (ll.size() == 0) {
				return new Long(-10);
			} else {
				User users = ll.get(0);

				// Refresh User Object
				users = this.refreshUserObject(users);

				if (usersDao.verifyPassword(users.getUser_id(), userpass)) {

					Boolean bool = sessiondataDao.updateUser(SID,
							users.getUser_id(), storePermanent,
							users.getLanguage_id());
					if (bool == null) {
						// Exception
						return new Long(-1);
					} else if (!bool) {
						// invalid Session-Object
						return new Long(-35);
					}

					// Check if activated
					if (users.getStatus() != null
							&& users.getStatus().equals(0)) {
						return -41L;
					}

					users.setUserlevel(getUserLevel(users.getLevel_id()));
					updateLastLogin(users);
					// If invoked via SOAP this is NULL
					if (currentClient != null) {
						currentClient.setUser_id(users.getUser_id());
						SessionVariablesUtil.setUserId(client, users.getUser_id());
					}

					log.debug("loginUser " + users.getOrganisation_users());
					if (!users.getOrganisation_users().isEmpty()) {
						log.debug("loginUser size "
								+ users.getOrganisation_users().size());
					} else {
						throw new Exception("No Organization assigned to user");
					}

					return users;
				} else {
					return new Long(-11);
				}
			}

		} catch (Exception ex2) {
			log.error("[loginUser]: ", ex2);
		}
		return new Long(-1);
	}

	public User refreshUserObject(User us) {
		try {

			us = em.merge(us);
			return us;
		} catch (Exception ex2) {
			log.error("[loginUser]: ", ex2);
		}
		return null;
	}

	public User loginUserByRemoteHash(String SID, String remoteHash) {
		try {

			Sessiondata sessionData = sessiondataDao
					.getSessionByHash(remoteHash);

			if (sessionData != null) {

				User u = getUserById(sessionData.getUser_id());

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
		return new Long(-12);
	}

	private void updateLastLogin(User us) {
		try {
			us.setLastlogin(new Date());
			if (us.getUser_id() == null) {
				em.persist(us);
			} else {
				if (!em.contains(us)) {
					em.merge(us);
				}
			}
		} catch (Exception ex2) {
			log.error("updateLastLogin", ex2);
		}
	}

	/**
	 * suche eines Bentzers
	 * 
	 * @param user_level
	 * @param searchstring
	 * @param max
	 * @param start
	 * @return
	 */
	public List<User> searchUser(long user_level, String searchcriteria,
			String searchstring, int max, int start, String orderby, boolean asc) {
		if (authLevelUtil.checkAdminLevel(user_level)) {
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<User> cq = cb.createQuery(User.class);
				Root<User> c = cq.from(User.class);
				Expression<String> literal = cb.literal("%" + searchstring + "%");
				Path<String> path = c.get(searchcriteria);
				Predicate predicate = cb.like(path, literal);
				Predicate condition = cb.notEqual(c.get("deleted"), true);
				cq.where(condition, predicate);
				cq.distinct(asc);
				if (asc) {
					cq.orderBy(cb.asc(c.get(orderby)));
				} else {
					cq.orderBy(cb.desc(c.get(orderby)));
				}
				TypedQuery<User> q = em.createQuery(cq);
				q.setFirstResult(start);
				q.setMaxResults(max);
				List<User> contactsZ = q.getResultList();
				return contactsZ;
			} catch (Exception ex2) {
				log.error("searchUser", ex2);
			}
		}
		return null;
	}

	public List<Userdata> getUserdataDashBoard(Long user_id) {
		if (user_id.longValue() > 0) {
			try {
				TypedQuery<Userdata> query = em
						.createQuery("select c from Userdata as c where c.user_id = :user_id AND c.deleted <> :deleted", Userdata.class);
				query.setParameter("user_id", user_id.longValue());
				query.setParameter("deleted", true);
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
		if (user_id.longValue() > 0) {
			try {
				TypedQuery<Userdata> query = em
						.createQuery("select c from Userdata as c where c.user_id = :user_id AND c.data_key = :data_key AND c.deleted <> :deleted", Userdata.class);
				query.setParameter("user_id", user_id.longValue());
				query.setParameter("data_key", DATA_KEY);
				query.setParameter("deleted", true);
				for (Iterator<Userdata> it2 = query.getResultList().iterator(); it2
						.hasNext();) {
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

	public Long updateUser(long user_level, Long user_id, Long level_id,
			String login, String password, String lastname, String firstname,
			Date age, String street, String additionalname, String zip,
			long states_id, String town, Long language_id, int availible,
			String telefon, String fax, String mobil, String email,
			String comment, int status, List<Long> organisations, int salutations_id,
			String phone, boolean sendSMS, String jNameTimeZone,
			Boolean forceTimeZoneCheck, String userOffers, String userSearchs,
			Boolean showContactData, Boolean showContactDataToContacts) {

		if (authLevelUtil.checkUserLevel(user_level) && user_id != 0) {
			try {
				User us = usersDao.get(user_id);

				// Check for duplicates
				boolean checkName = true;

				if (!login.equals(us.getLogin())) {
					checkName = usersDao.checkUserLogin(login);
				}
				boolean checkEmail = true;

				// Compare old address with new address
				if (!email.equals(us.getAdresses().getEmail())) {

					// Its a new one - check, whether another user already uses
					// that one...
					checkEmail = emailManagement.checkUserEMail(email);
				}

				if (checkName && checkEmail) {
					// log.info("user_id " + user_id);

					// add or delete organisations from this user
					if (organisations != null) {
						organisationManager.updateUserOrganisationsByUser(
								us, organisations);
					}
					us = usersDao.get(user_id);

					us.setLastname(lastname);
					us.setFirstname(firstname);
					us.setAge(age);
					us.setLogin(login);
					us.setUpdatetime(new Date());
					us.setAvailible(availible);
					us.setStatus(status);
					us.setSalutations_id((long)salutations_id);
					us.setOmTimeZone(omTimeZoneDaoImpl
							.getOmTimeZone(jNameTimeZone));
					us.setLanguage_id(language_id);
					us.setForceTimeZoneCheck(forceTimeZoneCheck);

					us.setSendSMS(sendSMS);
					us.setUserOffers(userOffers);
					us.setUserSearchs(userSearchs);
					us.setShowContactData(showContactData);
					us.setShowContactDataToContacts(showContactDataToContacts);

					if (level_id != 0) {
						us.setLevel_id(level_id);
					}
					if (password.length() != 0) {
						try {
							us.updatePassword(cryptManager, configurationDao, password);
						} catch (Exception e) {
							return new Long(-7);
						}
					}
					us.setAdresses(street, zip, town, statemanagement.getStateById(states_id),
							additionalname, comment, fax, phone, email);

					em.merge(us);

					return us.getUser_id();

				} else {
					if (!checkName) {
						return new Long(-15);
					} else if (!checkEmail) {
						return new Long(-17);
					}
				}
			} catch (Exception ex2) {
				log.error("[updateUser]", ex2);
			}
		} else {
			log.error("Error: Permission denied");
			return new Long(-1);
		}
		return new Long(-1);
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
					.setParameter("updatetime", new Long(-1))
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
					.setParameter("updatetime", new Long(-1))
					.setParameter("comment", Comment)
					.setParameter("user_id", USER_ID.longValue())
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
		userdata.setUser_id(new Long(USER_ID));
		userdata.setDeleted(false);
		try {
			userdata = em.merge(userdata);
			ret = "success";
		} catch (Exception ex2) {
			log.error("addUserdata", ex2);
		}
		return ret;
	}

	private Userlevel getUserLevel(Long level_id) {
		Userlevel userlevel = new Userlevel();
		try {
			TypedQuery<Userlevel> query = em
					.createQuery("select c from Userlevel as c where c.level_id = :level_id AND c.deleted <> :deleted", Userlevel.class);
			query.setParameter("level_id", level_id.longValue());
			query.setParameter("deleted", true);
			for(Iterator<Userlevel> it2 = query.getResultList().iterator(); it2
					.hasNext();) {
				userlevel = it2.next();
			}
		} catch (Exception ex2) {
			log.error("[getUserLevel]", ex2);
		}
		return userlevel;
	}

	/**
	 * get user-role 1 - user 2 - moderator 3 - admin
	 * 
	 * @param user_id
	 * @return
	 */
	public Long getUserLevelByID(Long user_id) {

		try {
			if (user_id == null)
				return new Long(0);
			// For direct access of linked users
			if (user_id == -1) {
				return new Long(1);
			}

			TypedQuery<User> query = em
					.createQuery("select c from User as c where c.user_id = :user_id AND c.deleted <> true", User.class);
			query.setParameter("user_id", user_id);
			User us = null;
			try {
				us = query.getSingleResult();
			} catch (NoResultException e) {
				// u=null}
			}

			if (us != null) {
				return us.getLevel_id();
			} else {
				return -1L;
			}
		} catch (Exception ex2) {
			log.error("[getUserLevelByID]", ex2);
		}
		return null;
	}

	public Long getUserLevelByIdAndOrg(Long user_id, Long organisation_id) {

		try {
			if (user_id == null)
				return new Long(0);
			// For direct access of linked users
			if (user_id == -1) {
				return new Long(1);
			}

			TypedQuery<User> query = em
					.createQuery("select c from User as c where c.user_id = :user_id AND c.deleted <> true", User.class);
			query.setParameter("user_id", user_id);
			User us = null;
			try {
				us = query.getSingleResult();
			} catch (NoResultException e) {
				// u=null}
			}

			if (us != null) {

				if (us.getLevel_id() > 2) {
					return us.getLevel_id();
				} else {

					log.debug("user_id, organisation_id" + user_id + ", "
							+ organisation_id);

					Organisation_Users ou = organisationManager
							.getOrganisation_UserByUserAndOrganisation(user_id,
									organisation_id);

					log.debug("ou: " + ou);

					if (ou != null) {
						if (ou.getIsModerator() != null && ou.getIsModerator()) {
							return 2L;
						} else {
							return us.getLevel_id();
						}
					} else {
						return us.getLevel_id();
					}
				}

			} else {
				return -1L;
			}
		} catch (Exception ex2) {
			log.error("[getUserLevelByID]", ex2);
		}
		return null;
	}

	/**
	 * Method to register a new User, User will automatically be added to the
	 * default user_level(1) new users will be automatically added to the
	 * Organisation with the id specified in the configuration value
	 * default_domain_id
	 * 
	 * @param user_level
	 * @param level_id
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
			String town, long language_id, String phone, boolean sendSMS, String baseURL,
			boolean generateSipUserData, String jNameTimeZone) {
		
		boolean sendConfirmation = baseURL != null
				&& !baseURL.isEmpty()
				&& 1 == configurationDao.getConfValue(
						"sendEmailWithVerficationCode", Integer.class, "0");
		
		return registerUser(login, Userpass, lastname, firstname, email, age,
				street, additionalname, fax, zip, states_id, town, language_id,
				phone, sendSMS, baseURL, generateSipUserData, jNameTimeZone, sendConfirmation);
	}

	public Long registerUserNoEmail(String login, String Userpass,
			String lastname, String firstname, String email, Date age,
			String street, String additionalname, String fax, String zip,
			long states_id, String town, long language_id, String phone, boolean sendSMS, 
			boolean generateSipUserData, String jNameTimeZone) {
		
		return registerUser(login, Userpass, lastname, firstname, email, age,
				street, additionalname, fax, zip, states_id, town, language_id,
				phone, sendSMS, "", generateSipUserData, jNameTimeZone, false);
	}

	private Long registerUser(String login, String Userpass, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, String phone, boolean sendSMS, String baseURL,
			boolean generateSipUserData, String jNameTimeZone, Boolean sendConfirmation) {
		try {
			// Checks if FrontEndUsers can register
			if ("1".equals(configurationDao.getConfValue(
					"allow_frontend_register", String.class, "0"))) {
				
				// TODO: Read and generate SIP-Data via RPC-Interface Issue 1098

				Long user_id = this.registerUserInit(3, 1, 0, 1, login,
						Userpass, lastname, firstname, email, age, street,
						additionalname, fax, zip, states_id, town, language_id,
						true, Arrays.asList(configurationDao.getConfValue(
								"default_domain_id", Long.class, null)), phone,
						sendSMS, baseURL,
						sendConfirmation, jNameTimeZone, false, "", "", false, true);

				if (sendConfirmation) {
					return new Long(-40);
				}

				return user_id;
			}
		} catch (Exception e) {
			log.error("[registerUser]", e);
		}
		return null;
	}

	/**
	 * Adds a user including his adress-data,auth-date,mail-data
	 * 
	 * @param user_level
	 * @param level_id
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
	 * @param phone
	 * @return new users_id OR null if an exception, -1 if an error, -4 if mail
	 *         already taken, -5 if username already taken, -3 if login or pass
	 *         or mail is empty
	 */
	public Long registerUserInit(long user_level, long level_id, int availible,
			int status, String login, String password, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, boolean sendWelcomeMessage,
			List<Long> organisations, String phone, boolean sendSMS, String baseURL,
			Boolean sendConfirmation, String jname_timezone, Boolean forceTimeZoneCheck,
			String userOffers, String userSearchs, Boolean showContactData,
			Boolean showContactDataToContacts) throws Exception {
		return registerUserInit(user_level, level_id, availible,
				status, login, password, lastname,
				firstname, email, age, street,
				additionalname, fax, zip, states_id,
				town, language_id, sendWelcomeMessage,
				organisations, phone, sendSMS, baseURL,
				sendConfirmation,
				omTimeZoneDaoImpl.getOmTimeZone(jname_timezone), forceTimeZoneCheck,
				userOffers, userSearchs, showContactData,
				showContactDataToContacts);
	}
	
	/**
	 * @param user_level
	 * @param level_id
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
	 * @param baseURL
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
	public Long registerUserInit(long user_level, long level_id, int availible,
			int status, String login, String password, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, boolean sendWelcomeMessage,
			List<Long> organisations, String phone, boolean sendSMS, String baseURL,
			Boolean sendConfirmation,
			OmTimeZone timezone, Boolean forceTimeZoneCheck,
			String userOffers, String userSearchs, Boolean showContactData,
			Boolean showContactDataToContacts) throws Exception {
		// TODO: make phone number persistent
		// User Level must be at least Admin
		// Moderators will get a temp update of there UserLevel to add Users to
		// their Group
		if (authLevelUtil.checkModLevel(user_level)) {

			Integer userLoginMinimumLength = configurationDao.getConfValue(
					"user.login.minimum.length", Integer.class, "4");
			if (userLoginMinimumLength == null) {
				throw new Exception(
						"user.login.minimum.length problem");
			}

			// Check for required data
			if (login.length() >= userLoginMinimumLength.intValue()) {
				// Check for duplicates
				boolean checkName = usersDao.checkUserLogin(login);
				boolean checkEmail = emailManagement.checkUserEMail(email);
				if (checkName && checkEmail) {

					String hash = cryptManager
							.getInstanceOfCrypt()
							.createPassPhrase(
									login
											+ CalendarPatterns
													.getDateWithTimeByMiliSeconds(new Date()));
					String link = baseURL + "activateUser?u=" + hash;

					if (sendWelcomeMessage && email.length() != 0) {
						// We need to pass the baseURL to check if this is
						// really set to be send
						String sendMail = emailManagement.sendMail(login,
								password, email, link, sendConfirmation);
						if (!sendMail.equals("success"))
							return new Long(-19);
					}
					Address adr =  new Address();
					adr.setStreet(street);
					adr.setZip(zip);
					adr.setTown(town);
					adr.setStates(statemanagement.getStateById(states_id));
					adr.setAdditionalname(additionalname);
					adr.setComment("");
					adr.setFax(fax);
					adr.setPhone(phone);
					adr.setEmail(email);

					// If this user needs first to click his E-Mail verification
					// code then set the status to 0
					if (sendConfirmation) {
						status = 0;
					}

					Long user_id = addUser(level_id, availible, status,
							firstname, login, lastname, language_id, password,
							adr, sendSMS, age, hash, timezone,
							forceTimeZoneCheck, userOffers, userSearchs,
							showContactData, showContactDataToContacts, organisations);
					log.debug("Added user-Id " + user_id);
					if (user_id == null) {
						return new Long(-111);
					}

					/*
					 * Long adress_emails_id =
					 * emailManagement.registerEmail(email, address_id,""); if
					 * (adress_emails_id==null) { return new Long(-112); }
					 */

					if (adr.getAdresses_id() > 0 && user_id > 0) {
						return user_id;
					} else {
						return new Long(-16);
					}
				} else {
					if (!checkName) {
						return new Long(-15);
					} else if (!checkEmail) {
						return new Long(-17);
					}
				}
			} else {
				return new Long(-13);
			}
		}
		return new Long(-1);
	}

	/**
	 * @author swagner This Methdo adds a User to the User-Table
	 * @param level_id
	 *            The User Level, 1=User, 2=GroupAdmin/Moderator,
	 *            3=SystemAdmin/Admin
	 * @param availible
	 *            The user is activated
	 * @param status
	 *            The user is not blocked by System admins
	 * @param firstname
	 * @param login
	 *            Username for login
	 * @param lastname
	 * @param language_id
	 * @param Userpass
	 *            is MD5-crypted
	 * @param Address adress
	 * @return user_id or error null
	 */
	public Long addUser(long level_id, int availible, int status,
			String firstname, String login, String lastname, long language_id,
			String userpass, Address adress, boolean sendSMS, Date age, String hash,
			OmTimeZone timezone,
			Boolean forceTimeZoneCheck, String userOffers, String userSearchs,
			Boolean showContactData, Boolean showContactDataToContacts, List<Long> orgIds) {
		try {

			User users = new User();
			users.setFirstname(firstname);
			users.setLogin(login);
			users.setLastname(lastname);
			users.setAge(age);
			users.setAdresses(adress);
			users.setSendSMS(sendSMS);
			users.setAvailible(availible);
			users.setLastlogin(new Date());
			users.setLasttrans(new Long(0));
			users.setLevel_id(level_id);
			users.setStatus(status);
			users.setSalutations_id(1L);
			users.setStarttime(new Date());
			users.setActivatehash(hash);
			users.setOmTimeZone(timezone);
			users.setForceTimeZoneCheck(forceTimeZoneCheck);

			users.setUserOffers(userOffers);
			users.setUserSearchs(userSearchs);
			users.setShowContactData(showContactData);
			users.setShowContactDataToContacts(showContactDataToContacts);

			// this is needed cause the language is not a needed data at
			// registering
			if (language_id != 0) {
				users.setLanguage_id(new Long(language_id));
			} else {
				users.setLanguage_id(null);
			}
			users.updatePassword(cryptManager, configurationDao, userpass);
			users.setRegdate(new Date());
			users.setDeleted(false);
			
			//new user add organizations without checks
			if (orgIds != null) {
				List<Organisation_Users> orgList = users.getOrganisation_users();
				for (Long orgId : orgIds) {
					orgList.add(organisationManager.getOrgUser(orgId, null));
				}
			}
			return addUser(users);

		} catch (Exception ex2) {
			log.error("[registerUser]", ex2);
		}
		return null;
	}

	public User getUserByExternalIdAndType(String externalUserId,
			String externalUserType) {

		try {

			String hql = "select c from User as c "
					+ "where c.externalUserId LIKE :externalUserId "
					+ "AND c.externalUserType LIKE :externalUserType "
					+ "AND c.deleted <> :deleted";

			TypedQuery<User> query = em.createQuery(hql, User.class);
			query.setParameter("externalUserId", externalUserId);
			query.setParameter("externalUserType", externalUserType);
			query.setParameter("deleted", true);

			List<User> users = query.getResultList();

			if (users.size() > 0) {
				return users.get(0);
			}

		} catch (Exception ex2) {
			log.error("[getUserByExternalIdAndType]", ex2);
		}
		return null;
	}

	public Long addUserWithExternalKey(long level_id, int availible,
			int status, String firstname, String login, String lastname,
			long language_id, boolean emptyPass, String userpass, Address address, Date age,
			String hash, String externalUserId, String externalUserType,
			boolean generateSipUserData, String email, String jNameTimeZone,
			String pictureuri) {
		try {
			User users = new User();
			users.setFirstname(firstname);
			users.setLogin(login);
			users.setLastname(lastname);
			users.setAge(age);

			if (address != null) {
				users.setAdresses(address);
			} else {
				users.setAdresses("", "", "", statemanagement.getStateById(1L), "",
						"", "", "", email);
			}

			users.setAvailible(availible);
			users.setLastlogin(new Date());
			users.setLasttrans(new Long(0));
			users.setLevel_id(level_id);
			users.setStatus(status);
			users.setSalutations_id(1L);
			users.setStarttime(new Date());
			users.setActivatehash(hash);
			users.setPictureuri(pictureuri);
			users.setOmTimeZone(omTimeZoneDaoImpl.getOmTimeZone(jNameTimeZone));

			users.setExternalUserId(externalUserId);
			users.setExternalUserType(externalUserType);

			// this is needed cause the language is not a needed data at
			// registering
			if (language_id != 0) {
				users.setLanguage_id(new Long(language_id));
			} else {
				users.setLanguage_id(null);
			}
			users.updatePassword(cryptManager, configurationDao, userpass, emptyPass);
			users.setRegdate(new Date());
			users.setDeleted(false);

			em.persist(users);

			em.refresh(users);

			// em.flush();

			long user_id = users.getUser_id();

			return user_id;

		} catch (Exception ex2) {
			log.error("[registerUser]", ex2);
		}
		return null;
	}

	public Long addUser(User usr) {
		try {
			em.persist(usr);
			//em.refresh(usr);
			em.flush();

			return usr.getUser_id();
		} catch (Exception ex2) {
			log.error("[addUser]", ex2);
		}
		return null;
	}

	public void addUserLevel(String description, int myStatus) {
		try {
			Userlevel uslevel = new Userlevel();
			uslevel.setStarttime(new Date());
			uslevel.setDescription(description);
			uslevel.setStatuscode(new Integer(myStatus));
			uslevel.setDeleted(false);
			em.merge(uslevel);
		} catch (Exception ex2) {
			log.error("[addUserLevel]", ex2);
		}
	}

	/**
	 * Update User by Object
	 * 
	 * @param user_level
	 * @param values
	 * @param users_id
	 * @return
	 */

	public Long saveOrUpdateUser(Long user_level, ObjectMap<?, ?> values,
			Long users_id) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				Long returnLong = null;

				Long user_id = Long.parseLong(values.get("user_id").toString());

				if (user_id != null && user_id > 0) {

					returnLong = user_id;
					User savedUser = usersDao.get(user_id);
					savedUser.setAge((Date) values.get("age"));
					savedUser.setFirstname(values.get("firstname").toString());
					savedUser.setLastname(values.get("lastname").toString());
					savedUser.setSalutations_id(Long.parseLong(values.get(
							"salutations_id").toString()));

					savedUser.setLanguage_id(Long.parseLong(values.get(
							"languages_id").toString()));
					savedUser.setOmTimeZone(omTimeZoneDaoImpl
							.getOmTimeZone((values.get("jnameTimeZone")
									.toString())));

					String password = values.get("password").toString();
					if (password != null && !password.isEmpty()) {
						savedUser.updatePassword(cryptManager, configurationDao, password);
					}

					String email = values.get("email").toString();

					if (!email.equals(savedUser.getAdresses().getEmail())) {
						boolean checkEmail = emailManagement
								.checkUserEMail(email);
						if (!checkEmail) {
							// mail already used by another user!
							returnLong = new Long(-11);
						} else {
							savedUser.getAdresses().setEmail(email);
						}
					}

					String phone = values.get("phone").toString();
					savedUser.getAdresses().setPhone(phone);
					savedUser.getAdresses().setComment(
							values.get("comment").toString());
					savedUser.getAdresses().setStreet(
							values.get("street").toString());
					savedUser.getAdresses().setTown(
							values.get("town").toString());
					savedUser.getAdresses().setAdditionalname(
							values.get("additionalname").toString());
					savedUser.getAdresses()
							.setZip(values.get("zip").toString());
					savedUser.setSendSMS(false);
					savedUser.setForceTimeZoneCheck(false);
					savedUser.getAdresses().setStates(
							statemanagement.getStateById(Long.parseLong(values
									.get("state_id").toString())));

					savedUser.setShowContactData(Boolean.valueOf(values.get(
							"showContactData").toString()));
					savedUser.setShowContactDataToContacts(Boolean
							.valueOf(values.get("showContactDataToContacts")
									.toString()));
					savedUser
							.setUserOffers(values.get("userOffers").toString());
					savedUser.setUserSearchs(values.get("userSearchs")
							.toString());

					// savedUser.setAdresses(addressmanagement.getAdressbyId(user.getAdresses().getAdresses_id()));

					if (savedUser.getUser_id() == null) {
						em.persist(savedUser);
					} else {
						if (!em.contains(savedUser)) {
							em.merge(savedUser);
						}
					}

					return returnLong;
				}

			} else {
				log.error("[saveOrUpdateUser] invalid auth " + users_id + " "
						+ new Date());
			}
		} catch (Exception ex) {
			log.error("[saveOrUpdateUser]", ex);
		}

		return null;
	}

	/**
	 * reset a username by a given mail oder login by sending a mail to the
	 * registered EMail-Address
	 * 
	 * @param email
	 * @param username
	 * @param appLink
	 * @return
	 */
	public Long resetUser(String email, String username, String appLink) {
		try {

			log.debug("resetUser " + email);

			// check if Mail given
			if (email.length() > 0) {
				// log.debug("getAdresses_id "+addr_e.getAdresses_id());
				User us = usersDao.getUserByEmail(email);
				if (us != null) {
					this.sendHashByUser(us, appLink);
					return new Long(-4);
				} else {
					return new Long(-9);
				}
			} else if (username.length() > 0) {
				User us = usersDao.getUserByName(username);
				if (us != null) {
					this.sendHashByUser(us, appLink);
					return new Long(-4);
				} else {
					return new Long(-3);
				}
			}
		} catch (Exception e) {
			log.error("[resetUser]", e);
			return new Long(-1);
		}
		return new Long(-2);
	}

	private void sendHashByUser(User us, String appLink) throws Exception {
		String loginData = us.getLogin() + new Date();
		log.debug("User: " + us.getLogin());
		us.setResethash(cryptManager.getInstanceOfCrypt().createPassPhrase(
				loginData));
		usersDao.update(us, -1L);
		String reset_link = appLink + "?lzproxied=solo&hash="
				+ us.getResethash();

		String email = us.getAdresses().getEmail();

		Long default_lang_id = configurationDao.getConfValue("default_lang_id", Long.class, "1");

		String template = resetPasswordTemplate.getResetPasswordTemplate(
				reset_link, default_lang_id);

		mailHandler.send(email, fieldManager.getString(517L, default_lang_id), template);
	}

	/**
	 * 
	 * Find User by Id
	 */
	// -----------------------------------------------------------------------------------------------------
	public User getUserById(Long id) {
		log.debug("Usermanagement.getUserById");

		if (id == null || id <= 0) {
			return null;
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> c = cq.from(User.class);
		Predicate condition = cb.equal(c.get("deleted"), false);
		Predicate subCondition = cb.equal(c.get("user_id"), id);
		cq.where(condition, subCondition);
		TypedQuery<User> q = em.createQuery(cq);
		User u = null;
		try {
			u = q.getSingleResult();
		} catch (NoResultException e) {
			// u=null}
		} catch (NonUniqueResultException ex) {
		}

		return u;
	}

	public User getUserByIdAndDeleted(Long id) throws Exception {
		log.debug("Usermanagement.getUserById");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> c = cq.from(User.class);
		Predicate condition = cb.equal(c.get("user_id"), id);
		cq.where(condition);
		TypedQuery<User> q = em.createQuery(cq);
		User u = null;
		try {
			u = q.getSingleResult();
		} catch (NoResultException e) {
			// u=null}
		}

		return u;

	}

	// -----------------------------------------------------------------------------------------------------

	/**
	 * @author o.becherer Find User by LoginName (test existence of a active
	 *         user with login - name
	 */
	// -----------------------------------------------------------------------------------------------------
	public User getUserByLogin(String login) throws Exception {
		log.debug("Usermanagement.getUserByLogin : " + login);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> c = cq.from(User.class);
		Predicate condition = cb.equal(c.get("deleted"), false);
		Predicate subCondition = cb.equal(c.get("login"), login);
		cq.where(condition, subCondition);
		TypedQuery<User> q = em.createQuery(cq);
		User u = null;
		try {
			u = q.getSingleResult();
		} catch (NoResultException e) {
			// u=null}
		}

		return u;

	}

	// -----------------------------------------------------------------------------------------------------

	/**
	 * @author swagner Find User by LoginName or EMail (test existence of a
	 *         active user with login - name
	 */
	// -----------------------------------------------------------------------------------------------------
	public User getUserByLoginOrEmail(String userOrEmail) throws Exception {
		// log.debug("Usermanagement.getUserByLoginOrEmail : " + userOrEmail);

		String hql = "SELECT c from User AS c "
				+ "WHERE "
				+ "(c.login LIKE :userOrEmail OR c.adresses.email LIKE :userOrEmail  ) "
				+ "AND c.externalUserId IS NULL " + "AND c.deleted <> :deleted";

		TypedQuery<User> query = em.createQuery(hql, User.class);
		query.setParameter("userOrEmail", userOrEmail);
		query.setParameter("deleted", true);

		List<User> ll = query.getResultList();

		if (ll.size() > 1) {
			log.error("ALERT :: There are two users in the database that have either same login or Email ");
			return ll.get(0);
			// throw new
			// Exception("ALERT :: There are two users in the database that have either same login or Email ");
		} else if (ll.size() == 1) {
			return ll.get(0);
		} else {
			return null;
		}

	}

	public User getUserByEmail(String userOrEmail) throws Exception {
		log.debug("Usermanagement.getUserByEmail : " + userOrEmail);

		String hql = "SELECT c from User AS c " + "WHERE "
				+ "c.adresses.email LIKE :userOrEmail";

		TypedQuery<User> query = em.createQuery(hql, User.class);
		query.setParameter("userOrEmail", userOrEmail);

		List<User> ll = query.getResultList();

		if (ll.size() > 1) {
			log.error("ALERT :: There are two users in the database that have same Email ");
			return ll.get(0);
			// throw new
			// Exception("ALERT :: There are two users in the database that have either same login or Email ");
		} else if (ll.size() == 1) {
			return ll.get(0);
		} else {
			return null;
		}

	}

	// -----------------------------------------------------------------------------------------------------

	/**
	 * @param admin
	 * @param room_id
	 * @return
	 */
	public Boolean kickUserByStreamId(String SID, Long room_id) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = getUserLevelByID(users_id);

			// admins only
			if (authLevelUtil.checkAdminLevel(user_level)) {

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
			Long user_level = getUserLevelByID(users_id);

			// admins only
			if (authLevelUtil.checkWebServiceLevel(user_level)) {

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

	/**
	 * @param hash
	 * @return
	 */
	public User getUserByActivationHash(String hash) {
		try {
			String hql = "SELECT u FROM User as u "
					+ " where u.activatehash = :activatehash"
					+ " AND u.deleted <> :deleted";
			TypedQuery<User> query = em.createQuery(hql, User.class);
			query.setParameter("activatehash", hash);
			query.setParameter("deleted", true);
			User u = null;
			try {
				u = query.getSingleResult();
			} catch (NoResultException e) {
				// u=null}
			}
			return u;
		} catch (Exception e) {
			log.error("[getUserByActivationHash]", e);
		}
		return null;

	}

	public void updateUser(User user) {
		usersDao.update(user, null);
	}

	/**
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @param search
	 * @return
	 */
	public SearchResult<User> getUsersListWithSearch(Long user_level, int start,
			int max, String orderby, boolean asc, String search) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {

				String hql = "select c from User c "
						+ "where c.deleted = false " + "AND ("
						+ "lower(c.login) LIKE :search "
						+ "OR lower(c.firstname) LIKE :search "
						+ "OR lower(c.lastname) LIKE :search " + ")";

				if (orderby.startsWith("c.")) {
					hql += "ORDER BY " + orderby;
				} else {
					hql += "ORDER BY " + "c." + orderby;
				}

				if (asc) {
					hql += " ASC";
				} else {
					hql += " DESC";
				}

				if (search.length() == 0) {
					search = "%";
				} else {
					search = "%" + search + "%";
				}
				log.debug("getUsersList search: " + search);

				SearchResult<User> sresult = new SearchResult<User>();
				sresult.setObjectName(User.class.getName());
				sresult.setRecords(usersDao
						.selectMaxFromUsersWithSearch(search));

				// get all users
				TypedQuery<User> query = em.createQuery(hql, User.class);
				query.setParameter("search", StringUtils.lowerCase(search));
				query.setMaxResults(max);
				query.setFirstResult(start);

				sresult.setResult(query.getResultList());

				return sresult;
			}
		} catch (Exception ex2) {
			log.error("[getUsersList] " + ex2);
		}
		return null;
	}

	public List<User> searchUserProfile(String searchTxt, String userOffers,
			String userSearchs, String orderBy, int start, int max, boolean asc) {
		try {

			String hql = "select c from User c "
					+ "where c.deleted = false ";

			if (searchTxt.length() != 0 && userOffers.length() != 0
					&& userSearchs.length() != 0) {

				hql += "AND " + "(" + "(" + "lower(c.login) LIKE :search "
						+ "OR lower(c.firstname) LIKE :search "
						+ "OR lower(c.lastname) LIKE :search "
						+ "OR lower(c.adresses.email) LIKE :search "
						+ "OR lower(c.adresses.town) LIKE :search " + ")"
						+ "AND" + "(" + "lower(c.userOffers) LIKE :userOffers "
						+ ")" + "AND" + "("
						+ "lower(c.userSearchs) LIKE :userSearchs " + ")" + ")";

			} else if (searchTxt.length() != 0 && userOffers.length() != 0) {

				hql += "AND " + "(" + "(" + "lower(c.login) LIKE :search "
						+ "OR lower(c.firstname) LIKE :search "
						+ "OR lower(c.lastname) LIKE :search "
						+ "OR lower(c.adresses.email) LIKE :search "
						+ "OR lower(c.adresses.town) LIKE :search " + ")"
						+ "AND" + "(" + "lower(c.userOffers) LIKE :userOffers "
						+ ")" + ")";

			} else if (searchTxt.length() != 0 && userSearchs.length() != 0) {

				hql += "AND " + "(" + "(" + "lower(c.login) LIKE :search "
						+ "OR lower(c.firstname) LIKE :search "
						+ "OR lower(c.lastname) LIKE :search "
						+ "OR lower(c.adresses.email) LIKE :search "
						+ "OR lower(c.adresses.town) LIKE :search " + ")"
						+ "AND" + "("
						+ "lower(c.userSearchs) LIKE :userSearchs " + ")" + ")";

			} else if (userOffers.length() != 0 && userSearchs.length() != 0) {

				hql += "AND " + "(" + "("
						+ "lower(c.userOffers) LIKE :userOffers " + ")" + "AND"
						+ "(" + "lower(c.userSearchs) LIKE :userSearchs " + ")"
						+ ")";

			} else if (searchTxt.length() != 0) {

				hql += "AND " + "(" + "(" + "lower(c.login) LIKE :search "
						+ "OR lower(c.firstname) LIKE :search "
						+ "OR lower(c.lastname) LIKE :search "
						+ "OR lower(c.adresses.email) LIKE :search "
						+ "OR lower(c.adresses.town) LIKE :search " + ")" + ")";

			} else if (userOffers.length() != 0) {

				hql += "AND " + "(" + "("
						+ "lower(c.userOffers) LIKE :userOffers " + ")" + ")";

			} else if (userSearchs.length() != 0) {

				hql += "AND " + "(" + "("
						+ "lower(c.userSearchs) LIKE :userSearchs " + ")" + ")";

			}

			hql += " ORDER BY " + orderBy;

			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			if (searchTxt.length() != 0) {
				searchTxt = "%" + searchTxt + "%";
			}

			if (userOffers.length() != 0) {
				userOffers = "%" + userOffers + "%";
			}

			if (userSearchs.length() != 0) {
				userSearchs = "%" + userSearchs + "%";
			}

			log.debug("hql :: " + hql);

			// get all users
			TypedQuery<User> query = em.createQuery(hql, User.class);

			if (searchTxt.length() != 0 && userOffers.length() != 0
					&& userSearchs.length() != 0) {

				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userOffers",
						StringUtils.lowerCase(userOffers));
				query.setParameter("userSearchs",
						StringUtils.lowerCase(userSearchs));

			} else if (searchTxt.length() != 0 && userOffers.length() != 0) {

				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userOffers",
						StringUtils.lowerCase(userOffers));

			} else if (searchTxt.length() != 0 && userSearchs.length() != 0) {

				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userSearchs",
						StringUtils.lowerCase(userSearchs));

			} else if (userOffers.length() != 0 && userSearchs.length() != 0) {

				query.setParameter("userOffers",
						StringUtils.lowerCase(userOffers));
				query.setParameter("userSearchs",
						StringUtils.lowerCase(userSearchs));

			} else if (searchTxt.length() != 0) {

				query.setParameter("search", StringUtils.lowerCase(searchTxt));

			} else if (userOffers.length() != 0) {

				query.setParameter("userOffers",
						StringUtils.lowerCase(userOffers));

			} else if (userSearchs.length() != 0) {

				query.setParameter("userSearchs",
						StringUtils.lowerCase(userSearchs));

			}

			query.setMaxResults(max);
			query.setFirstResult(start);

			List<User> userList = query.getResultList();

			return userList;

		} catch (Exception ex2) {
			log.error("[getUsersList] ", ex2);
		}

		return null;
	}

	public Long searchCountUserProfile(String searchTxt, String userOffers,
			String userSearchs) {
		try {

			String hql = "select count(c.user_id) from User c "
					+ "where c.deleted = false ";

			if (searchTxt.length() != 0 && userOffers.length() != 0
					&& userSearchs.length() != 0) {

				hql += "AND " + "(" + "(" + "lower(c.login) LIKE :search "
						+ "OR lower(c.firstname) LIKE :search "
						+ "OR lower(c.lastname) LIKE :search "
						+ "OR lower(c.adresses.email) LIKE :search "
						+ "OR lower(c.adresses.town) LIKE :search " + ")"
						+ "AND" + "(" + "lower(c.userOffers) LIKE :userOffers "
						+ ")" + "AND" + "("
						+ "lower(c.userSearchs) LIKE :userSearchs " + ")" + ")";

			} else if (searchTxt.length() != 0 && userOffers.length() != 0) {

				hql += "AND " + "(" + "(" + "lower(c.login) LIKE :search "
						+ "OR lower(c.firstname) LIKE :search "
						+ "OR lower(c.lastname) LIKE :search) "
						+ "OR lower(c.adresses.email) LIKE :search "
						+ "OR lower(c.adresses.town) LIKE :search " + ")"
						+ "AND" + "(" + "lower(c.userOffers) LIKE :userOffers "
						+ ")" + ")";

			} else if (searchTxt.length() != 0 && userSearchs.length() != 0) {

				hql += "AND " + "(" + "(" + "lower(c.login) LIKE :search "
						+ "OR lower(c.firstname) LIKE :search "
						+ "OR lower(c.lastname) LIKE :search "
						+ "OR lower(c.adresses.email) LIKE :search "
						+ "OR lower(c.adresses.town) LIKE :search " + ")"
						+ "AND" + "("
						+ "lower(c.userSearchs) LIKE :userSearchs " + ")" + ")";

			} else if (userOffers.length() != 0 && userSearchs.length() != 0) {

				hql += "AND " + "(" + "("
						+ "lower(c.userOffers) LIKE :userOffers " + ")" + "AND"
						+ "(" + "lower(c.userSearchs) LIKE :userSearchs " + ")"
						+ ")";

			} else if (searchTxt.length() != 0) {

				hql += "AND " + "(" + "(" + "lower(c.login) LIKE :search "
						+ "OR lower(c.firstname) LIKE :search "
						+ "OR lower(c.lastname) LIKE :search "
						+ "OR lower(c.adresses.email) LIKE :search "
						+ "OR lower(c.adresses.town) LIKE :search " + ")" + ")";

			} else if (userOffers.length() != 0) {

				hql += "AND " + "(" + "("
						+ "lower(c.userOffers) LIKE :userOffers " + ")" + ")";

			} else if (userSearchs.length() != 0) {

				hql += "AND " + "(" + "("
						+ "lower(c.userSearchs) LIKE :userSearchs " + ")" + ")";

			}

			if (searchTxt.length() != 0) {
				searchTxt = "%" + searchTxt + "%";
			}

			if (userOffers.length() != 0) {
				userOffers = "%" + userOffers + "%";
			}

			if (userSearchs.length() != 0) {
				userSearchs = "%" + userSearchs + "%";
			}

			log.debug("hql :: " + hql);

			// get all users
			TypedQuery<Long> query = em.createQuery(hql, Long.class);

			if (searchTxt.length() != 0 && userOffers.length() != 0
					&& userSearchs.length() != 0) {

				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userOffers",
						StringUtils.lowerCase(userOffers));
				query.setParameter("userSearchs",
						StringUtils.lowerCase(userSearchs));

			} else if (searchTxt.length() != 0 && userOffers.length() != 0) {

				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userOffers",
						StringUtils.lowerCase(userOffers));

			} else if (searchTxt.length() != 0 && userSearchs.length() != 0) {

				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userSearchs",
						StringUtils.lowerCase(userSearchs));

			} else if (userOffers.length() != 0 && userSearchs.length() != 0) {

				query.setParameter("userOffers",
						StringUtils.lowerCase(userOffers));
				query.setParameter("userSearchs",
						StringUtils.lowerCase(userSearchs));

			} else if (searchTxt.length() != 0) {

				query.setParameter("search", StringUtils.lowerCase(searchTxt));

			} else if (userOffers.length() != 0) {

				query.setParameter("userOffers",
						StringUtils.lowerCase(userOffers));

			} else if (userSearchs.length() != 0) {

				query.setParameter("userSearchs",
						StringUtils.lowerCase(userSearchs));

			}

			List<Long> userList = query.getResultList();

			return userList.get(0);

		} catch (Exception ex2) {
			log.error("[getUsersList] ", ex2);
		}

		return null;
	}

	public Long searchMaxUserProfile(String searchTxt, String userOffers,
			String userSearchs) {
		try {

			String hql = "select count(c.user_id) from User c "
					+ "where c.deleted = false " + "AND " + "(" + "("
					+ "lower(c.login) LIKE :search "
					+ "OR lower(c.firstname) LIKE :search "
					+ "OR lower(c.lastname) LIKE :search "
					+ "OR lower(c.adresses.email) LIKE :search "
					+ "OR lower(c.adresses.town) LIKE :search " + ")" + "OR"
					+ "(" + "lower(c.userOffers) LIKE :userOffers " + ")"
					+ "OR" + "(" + "lower(c.userSearchs) LIKE :userSearchs "
					+ ")" + ")";

			if (searchTxt.length() == 0) {
				searchTxt = "%";
			} else {
				searchTxt = "%" + searchTxt + "%";
			}

			if (userOffers.length() == 0) {
				userOffers = "%";
			} else {
				userOffers = "%" + userOffers + "%";
			}

			if (userSearchs.length() == 0) {
				userSearchs = "%";
			} else {
				userSearchs = "%" + userSearchs + "%";
			}

			// get all users
			TypedQuery<Long> query = em.createQuery(hql, Long.class);
			query.setParameter("search", StringUtils.lowerCase(searchTxt));
			query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
			query.setParameter("userSearchs",
					StringUtils.lowerCase(userSearchs));

			List<Long> ll = query.getResultList();

			return ll.get(0);

		} catch (Exception ex2) {
			log.error("[searchMaxUserProfile] " + ex2);
		}

		return null;
	}

}
