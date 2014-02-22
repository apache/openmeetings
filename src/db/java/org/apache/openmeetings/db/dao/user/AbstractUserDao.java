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
package org.apache.openmeetings.db.dao.user;

import static org.apache.openmeetings.db.util.UserHelper.getMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAUT_LANG_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.DaoHelper;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD operations for {@link User}
 * 
 * @author swagner, solomax, vasya
 * 
 */
@Transactional
public class AbstractUserDao  {
	private static final Logger log = Red5LoggerFactory.getLogger(AbstractUserDao.class, webAppRootKey);

	public final static String[] searchFields = {"lastname", "firstname", "login", "adresses.email", "adresses.town"};

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private StateDao stateDaoImpl;
	@Autowired
	private TimezoneUtil timezoneUtil;

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
		user.setLanguage_id(cfgDao.getConfValue(CONFIG_DEFAUT_LANG_KEY, Long.class, "1"));
		user.setTimeZoneId(timezoneUtil.getTimeZone(currentUser).getID());
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
	
	private String getAdditionalWhere(boolean isAdmin) {
		return isAdmin ? null : "(u.type <> :contact OR (u.type = :contact AND u.ownerId = :ownerId))";
	}
	
	private void setAdditionalParams(TypedQuery<?> q, boolean isAdmin, long currentUserId) {
		if (!isAdmin) {
			q.setParameter("ownerId", currentUserId);
			q.setParameter("contact", Type.contact);
		}
	}

	public List<User> get(String search, int start, int count, String sort, boolean isAdmin, long currentUserId) {
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", search, true, false, getAdditionalWhere(isAdmin), sort, searchFields), User.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		setAdditionalParams(q, isAdmin, currentUserId);
		return q.getResultList();
	}
	
	public long count() {
		// get all users
		TypedQuery<Long> q = em.createNamedQuery("countNondeletedUsers", Long.class);
		return q.getSingleResult();
	}

	public long count(String search, boolean isAdmin, long currentUserId) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", search, true, true, getAdditionalWhere(isAdmin), null, searchFields), Long.class);
		setAdditionalParams(q, isAdmin, currentUserId);
		return q.getSingleResult();
	}
	
	public List<User> get(String search, boolean isAdmin, long currentUserId) {
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", search, true, false, getAdditionalWhere(isAdmin), null, searchFields), User.class);
		if (!isAdmin) {
			q.setParameter("ownerId", currentUserId);
			q.setParameter("contact", Type.contact);
		}
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
	
	// TODO: Why the password field is not set via the Model is because its
	// FetchType is Lazy, this extra hook here might be not needed with a
	// different mechanism to protect the password from being read
	// sebawagner, 01.10.2012
	public User update(User user, String password, long updatedBy) throws NoSuchAlgorithmException {
		User u = update(user, updatedBy);
		if (password != null && !password.isEmpty()) {
			//OpenJPA is not allowing to set fields not being fetched before
			User u1 = get(u.getUser_id(), true);
			u1.updatePassword(cfgDao, password);
			update(u1, updatedBy);
		}
		return u;
	}
	
	public void delete(User u, Long userId) {
		deleteUserID(u.getUser_id());
	}

	public User get(long user_id) {
		return get(user_id, false);
	}
	
	private User get(long user_id, boolean force) {
		if (user_id > 0) {
			TypedQuery<User> query = em.createNamedQuery("getUserById",
					User.class);
			query.setParameter("user_id", user_id);

			User users = null;
			try {
				if (force) {
					@SuppressWarnings("unchecked")
					OpenJPAQuery<User> kq = OpenJPAPersistence.cast(query);
					kq.getFetchPlan().addFetchGroup("backupexport");
					users = kq.getSingleResult();
				} else {
					users = query.getSingleResult();
				}
			} catch (NoResultException ex) {
			}
			return users;
		} else {
			log.info("[getUser] " + "Info: No USER_ID given");
		}
		return null;
	}

	public Long deleteUserID(long userId) {
		try {
			if (userId != 0) {
				User us = get(userId);
				for (Organisation_Users ou : us.getOrganisation_users()){
					em.remove(ou);
				}
				us.setOrganisation_users(null);
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
			TypedQuery<User> q = em.createNamedQuery("getAllUsers", User.class);
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
	public boolean checkUserLogin(String login, Long id) {
		log.debug("checkUserLogin: login = {}, id = {}", login, id);
		long count = em.createNamedQuery("checkUserLogin", Long.class)
				.setParameter("login", login)
				.setParameter("id", id == null ? 0 : id)
				.getSingleResult();
		return count == 0;
	}

	/**
	 * Checks if a mail is already taken by someone else
	 * 
	 * @param email
	 * @return
	 */
	public boolean checkUserEMail(String email, Long id) {
		log.debug("checkUserMail: email = {}, id = {}", email, id);
		if (email == null || email.length() == 0) {
			return true;
		}
		long count = em.createNamedQuery("checkUserEmail", Long.class)
			.setParameter("email", email)
			.setParameter("id", id == null ? 0 : id)
			.setParameter("type", Type.contact)
			.getSingleResult();
		log.debug("size: " + count);

		return count == 0;
	}
	
	public User getUserByName(String login) {
		User us = null;
		try {
			us = em.createNamedQuery("getUserByLogin", User.class)
					.setParameter("login", login)
					.setParameter("type", User.Type.user)
					.getSingleResult();
		} catch (NoResultException ex) {
		}
		return us;
	}

	public User getUserByEmail(String email) {
		User us = null;
		try {
			us = em.createNamedQuery("getUserByEmail", User.class)
					.setParameter("email", email)
					.setParameter("type", User.Type.user)
					.getSingleResult();
		} catch (NoResultException ex) {
		}
		return us;
	}

	public Object getUserByHash(String hash) {
		if (hash.length() == 0) {
			return new Long(-5);
		}
		User us = null;
		try {
			us = em.createNamedQuery("getUserByHash", User.class)
					.setParameter("resethash", hash)
					.setParameter("type", User.Type.user)
					.getSingleResult();
		} catch (NoResultException ex) {
		} catch (Exception e) {
			log.error("[getUserByHash]", e);
		}
		if (us != null) {
			return us;
		} else {
			return new Long(-5);
		}
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
		TypedQuery<Long> query = em.createNamedQuery("checkPassword", Long.class);
		query.setParameter("userId", userId);
		query.setParameter("password", ManageCryptStyle.getInstanceOfCrypt().createPassPhrase(password));
		return query.getResultList().get(0) == 1;

	}

	public User getContact(String email, long ownerId) {
		return getContact(email, "", "", ownerId);
	}
	
	public User getContact(String email, User owner) {
		return getContact(email, "", "", null, null, owner);
	}
	
	public User getContact(String email, String firstName, String lastName, long ownerId) {
		return getContact(email, firstName, lastName, null, null, get(ownerId));
	}
	
	public User getContact(String email, String firstName, String lastName, Long langId, String tzId, User owner) {
		User to = null;
		try {
			to = em.createNamedQuery("getContactByEmailAndUser", User.class)
					.setParameter("email", email).setParameter("type", User.Type.contact).setParameter("ownerId", owner.getUser_id()).getSingleResult();
		} catch (Exception e) {
			//no-op
		}
		if (to == null) {
			to = new User();
			to.setType(Type.contact);
			String login = owner.getUser_id() + "_" + email; //UserId prefix is used to ensure unique login
			to.setLogin(login.length() < getMinLoginLength(cfgDao) ? UUID.randomUUID().toString() : login);
			to.setFirstname(firstName);
			to.setLastname(lastName);
			to.setLanguage_id(null == langId ? owner.getLanguage_id() : langId);
			to.setOwnerId(owner.getUser_id());
			to.setAdresses(new Address());
			to.getAdresses().setEmail(email);
			to.setTimeZoneId(null == tzId ? owner.getTimeZoneId() : tzId);
		}
		return to;
	}

	/**
	 * @param hash
	 * @return
	 */
	public User getUserByActivationHash(String hash) {
		TypedQuery<User> query = em.createQuery("SELECT u FROM User as u WHERE u.activatehash = :activatehash"
				+ " AND u.deleted = false", User.class);
		query.setParameter("activatehash", hash);
		User u = null;
		try {
			u = query.getSingleResult();
		} catch (NoResultException e) {
			// u=null}
		}
		return u;
	}

	private StringBuilder getUserProfileQuery(boolean count, String text, String offers, String search) {
		StringBuilder sb = new StringBuilder("SELECT ");
		sb.append(count ? "COUNT(" : "").append("u").append(count ? ") " : " ")
			.append("FROM User u WHERE u.deleted = false AND ")
			.append(getAdditionalWhere(false));
		if (offers != null && offers.length() != 0) {
			sb.append("AND (LOWER(u.userOffers) LIKE :userOffers) ");
		}
		if (search != null && search.length() != 0) {
			sb.append("AND (LOWER(u.userSearchs) LIKE :userSearchs) ");
		}
		if (text != null && text.length() != 0) {
			sb.append("AND (LOWER(u.login) LIKE :search ")
				.append("OR LOWER(u.firstname) LIKE :search ")
				.append("OR LOWER(u.lastname) LIKE :search ")
				.append("OR LOWER(u.adresses.email) LIKE :search ")
				.append("OR LOWER(u.adresses.town) LIKE :search " + ") ");
		}
		return sb;
	}
	
	public List<User> searchUserProfile(long userId, String text, String offers, String search, String orderBy, int start, int max, boolean asc) {
		StringBuilder sb = getUserProfileQuery(false, text, offers, search);
		sb.append(" ORDER BY ").append(orderBy).append(asc ? " ASC" : " DESC");

		log.debug("hql :: " + sb.toString());
		TypedQuery<User> query = em.createQuery(sb.toString(), User.class);
		setAdditionalParams(query, false, userId);

		if (text != null && text.length() != 0) {
			query.setParameter("search", StringUtils.lowerCase("%" + text + "%"));
		}
		if (offers != null && offers.length() != 0) {
			query.setParameter("userOffers", StringUtils.lowerCase("%" + offers + "%"));
		}
		if (search != null && search.length() != 0) {
			query.setParameter("userSearchs", StringUtils.lowerCase("%" + search + "%"));
		}
		return query.setFirstResult(start).setMaxResults(max).getResultList();
	}

	public Long searchCountUserProfile(long userId, String text, String offers, String search) {
		StringBuilder sb = getUserProfileQuery(true, text, offers, search);
		
		log.debug("hql :: " + sb.toString());
		TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class);
		setAdditionalParams(query, false, userId);
		
		if (text != null && text.length() != 0) {
			query.setParameter("search", StringUtils.lowerCase("%" + text + "%"));
		}
		if (offers != null && offers.length() != 0) {
			query.setParameter("userOffers", StringUtils.lowerCase("%" + offers + "%"));
		}
		if (search != null && search.length() != 0) {
			query.setParameter("userSearchs", StringUtils.lowerCase("%" + search + "%"));
		}
		return query.getSingleResult();
	}

	public User getExternalUser(String extId, String extType) {
		return em.createNamedQuery("getExternalUser", User.class)
				.setParameter("externalId", extId)
				.setParameter("externalType", extType)
				.getSingleResult();
	}
}
