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
package org.apache.openmeetings.data.user.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.IDataProviderDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.persistence.beans.user.Address;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.utils.DaoHelper;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD operations for {@link User}
 * 
 * @author swagner, solomax
 * 
 */
@Transactional
public class UsersDao implements IDataProviderDao<User> {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UsersDao.class, OpenmeetingsVariables.webAppRootKey);

	public final static String[] searchFields = {"lastname", "firstname", "login", "adresses.email"};

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ManageCryptStyle cryptManager;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private StateDao stateDaoImpl;

	/**
	 * Get a new instance of the {@link User} entity, with all default values
	 * set
	 * 
	 * @param currentUser
	 *            the timezone of the current user is copied to the new default
	 *            one (if the current user has one)
	 * @return
	 */
	public User getNewUserInstance(User currentUser) {
		User user = new User();
		user.setSalutations_id(1L); // TODO: Fix default selection to be
									// configurable
		user.setLevel_id(1L);
		user.setLanguage_id(configurationDao.getConfValue(
				"default_lang_id", Long.class, "1"));
		user.setOmTimeZone(omTimeZoneDaoImpl.getDefaultOmTimeZone(currentUser));
		user.setForceTimeZoneCheck(false);
		user.setSendSMS(false);
		user.setAge(new Date());
		Address adresses = new Address();
		adresses.setStates(stateDaoImpl.getStateById(1L));
		user.setAdresses(adresses);
		user.setStatus(1);
		user.setShowContactData(false);
		user.setShowContactDataToContacts(false);

		return user;
	}

	public List<User> get(int first, int count) {
		TypedQuery<User> q = em.createNamedQuery("getNondeletedUsers", User.class);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<User> get(String search, int start, int count, String sort) {
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", search, true, false, sort, searchFields), User.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	public long count() {
		// get all users
		TypedQuery<Long> q = em.createNamedQuery("countNondeletedUsers", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public List<User> get(String search) {
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", search, true, false, null, searchFields), User.class);
		return q.getResultList();
	}

	public User update(User u, Long userId) {
		if (u.getUser_id() == null) {
			u.setStarttime(new Date());
			em.persist(u);
		} else {
			u.setUpdatetime(new Date());
			u =	em.merge(u);
		}
		return u;
	}
	
	public void delete(User u, Long userId) {
		deleteUserID(u.getUser_id());
	}

	public User get(long user_id) {
		if (user_id > 0) {
			try {
				TypedQuery<User> query = em.createNamedQuery("getUserById",
						User.class);
				query.setParameter("user_id", user_id);

				User users = null;
				try {
					users = query.getSingleResult();
				} catch (NoResultException ex) {
				}
				return users;
			} catch (Exception ex2) {
				log.error("getUser", ex2);
			}
		} else {
			log.info("[getUser] " + "Info: No USER_ID given");
		}
		return null;
	}

	public Long deleteUserID(long userId) {
		try {
			if (userId != 0) {
				User us = get(userId);
				us.setDeleted(true);
				us.setUpdatetime(new Date());
				us.setSipUser(null);
				Address adr = us.getAdresses();
				if (adr != null) {
					adr.setDeleted(true);
				}

				if (us.getUser_id() == null) {
					em.persist(us);
				} else {
					if (!em.contains(us)) {
						em.merge(us);
					}
				}
				return us.getUser_id();
			}
		} catch (Exception ex2) {
			log.error("[deleteUserID]", ex2);
		}
		return null;
	}

	public List<User> getAllUsers() {
		try {
			TypedQuery<User> q = em.createNamedQuery("getNondeletedUsers", User.class);
			return q.getResultList();
		} catch (Exception ex2) {
			log.error("[getAllUsers] ", ex2);
		}
		return null;
	}

	public List<User> getAllUsersDeleted() {
		try {
			TypedQuery<User> q = em.createNamedQuery("getAllUsers",
					User.class);
			@SuppressWarnings("unchecked")
			OpenJPAQuery<User> kq = OpenJPAPersistence.cast(q);
			kq.getFetchPlan().addFetchGroup("backupexport");
			return kq.getResultList();
		} catch (Exception ex2) {
			log.error("[getAllUsersDeleted] ", ex2);
		}
		return null;
	}

	/**
	 * check for duplicates
	 * 
	 * @param DataValue
	 * @return
	 */
	public boolean checkUserLogin(String DataValue) {
		try {
			TypedQuery<User> query = em
					.createNamedQuery("checkUserLogin", User.class);
			query.setParameter("DataValue", DataValue);
			query.setParameter("deleted", true);
			int count = query.getResultList().size();

			if (count != 0) {
				return false;
			}
		} catch (Exception ex2) {
			log.error("[checkUserData]", ex2);
		}
		return true;
	}

	public User getUserByName(String login) {
		try {
			TypedQuery<User> query = em.createNamedQuery("getUserByName", User.class);
			query.setParameter("login", login);
			query.setParameter("deleted", true);
			User us = null;
			try {
				us = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return us;
		} catch (Exception e) {
			log.error("[getUserByAdressesId]", e);
		}
		return null;
	}

	public User getUserByEmail(String email) {
		try {
			TypedQuery<User> query = em.createNamedQuery("getUserByEmail", User.class);
			query.setParameter("email", email);
			query.setParameter("deleted", true);
			User us = null;
			try {
				us = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return us;
		} catch (Exception e) {
			log.error("[getUserByAdressesId]", e);
		}
		return null;
	}

	public Object getUserByHash(String hash) {
		try {
			if (hash.length() == 0)
				return new Long(-5);
			TypedQuery<User> query = em.createNamedQuery("getUserByHash", User.class);
			query.setParameter("resethash", hash);
			query.setParameter("deleted", true);
			User us = null;
			try {
				us = query.getSingleResult();
			} catch (NoResultException ex) {
			}
			if (us != null) {
				return us;
			} else {
				return new Long(-5);
			}
		} catch (Exception e) {
			log.error("[getUserByAdressesId]", e);
		}
		return new Long(-1);
	}

	public Object resetPassByHash(String hash, String pass) {
		try {
			Object u = this.getUserByHash(hash);
			if (u instanceof User) {
				User us = (User) u;
				us.updatePassword(cryptManager, configurationDao, pass);
				us.setResethash("");
				update(us, -1L);
				return new Long(-8);
			} else {
				return u;
			}
		} catch (Exception e) {
			log.error("[getUserByAdressesId]", e);
		}
		return new Long(-1);
	}

	/**
	 * @param search
	 * @return
	 */
	public Long selectMaxFromUsersWithSearch(String search) {
		try {
			// get all users
			TypedQuery<Long> query = em.createNamedQuery("selectMaxFromUsersWithSearch", Long.class);
			query.setParameter("search", StringUtils.lowerCase(search));
			List<Long> ll = query.getResultList();
			log.info("selectMaxFromUsers" + ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] ", ex2);
		}
		return null;
	}

	/**
	 * Returns true if the password is correct
	 * 
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean verifyPassword(Long userId, String password) {
		TypedQuery<Long> query = em.createNamedQuery("checkPassword",
				Long.class);
		query.setParameter("userId", userId);
		query.setParameter("password", cryptManager.getInstanceOfCrypt()
				.createPassPhrase(password));
		return query.getResultList().get(0) == 1;

	}
}
