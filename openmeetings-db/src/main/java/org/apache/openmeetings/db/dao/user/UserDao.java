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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.DaoHelper;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
import org.apache.wicket.util.string.Strings;
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
public class UserDao implements IDataProviderDao<User> {
	private static final Logger log = Red5LoggerFactory.getLogger(UserDao.class, webAppRootKey);

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
	
	private String getAdditionalJoin(boolean filterContacts) {
		return filterContacts ? "LEFT JOIN u.organisation_users ou" : null;
	}
	
	private String getAdditionalWhere(boolean excludeContacts, Map<String, Object> params) {
		if (excludeContacts) {
			params.put("contact", Type.contact);
			return "u.type <> :contact";
		}
		return null;
	}
	
	private String getAdditionalWhere(boolean filterContacts, Long ownerId, Map<String, Object> params) {
		if (filterContacts) {
			params.put("ownerId", ownerId);
			params.put("contact", Type.contact);
			return "((u.type <> :contact AND ou.organisation.organisation_id IN (SELECT ou.organisation.organisation_id FROM Organisation_Users ou WHERE ou.user.user_id = :ownerId)) "
				+ "OR (u.type = :contact AND u.ownerId = :ownerId))";
		}
		return null;
	}
	
	private void setAdditionalParams(TypedQuery<?> q, Map<String, Object> params) {
		for (String key : params.keySet()) {
			q.setParameter(key, params.get(key));
		}
	}

	public List<User> get(String search, int start, int count, String sort, boolean filterContacts, long currentUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", getAdditionalJoin(filterContacts), search, true, true, false
				, getAdditionalWhere(filterContacts, currentUserId, params), sort, searchFields), User.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		setAdditionalParams(q, params);
		return q.getResultList();
	}
	
	public long count() {
		// get all users
		TypedQuery<Long> q = em.createNamedQuery("countNondeletedUsers", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		return count(search, true, -1);
	}
	
	public long count(String search, long currentUserId) {
		return count(search, false, currentUserId);
	}
	
	public long count(String search, boolean filterContacts, long currentUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", getAdditionalJoin(filterContacts), search, true, true, true
				, getAdditionalWhere(filterContacts, currentUserId, params), null, searchFields), Long.class);
		setAdditionalParams(q, params);
		return q.getSingleResult();
	}
	
	//This is AdminDao method
	public List<User> get(String search, boolean excludeContacts, int first, int count) {
		Map<String, Object> params = new HashMap<String, Object>();
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", null, search, true, true, false
				, getAdditionalWhere(excludeContacts, params), null, searchFields), User.class);
		setAdditionalParams(q, params);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<User> get(String search, boolean filterContacts, long currentUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", getAdditionalJoin(filterContacts), search, true, true, false
				, getAdditionalWhere(filterContacts, currentUserId, params), null, searchFields), User.class);
		setAdditionalParams(q, params);
		return q.getResultList();
	}

	public User update(User u, Long userId) {
		if (u.getOrganisation_users() != null) {
			for (Organisation_Users ou : u.getOrganisation_users()) {
				ou.setUser(u);
			}
		}
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
		User u = null;
		if (user_id > 0) {
			OpenJPAEntityManager oem = OpenJPAPersistence.cast(em);
			boolean qrce = oem.getFetchPlan().getQueryResultCacheEnabled();
			oem.getFetchPlan().setQueryResultCacheEnabled(false);
			TypedQuery<User> q = oem.createNamedQuery("getUserById", User.class).setParameter("id", user_id);
			@SuppressWarnings("unchecked")
			OpenJPAQuery<User> kq = OpenJPAPersistence.cast(q);
			kq.getFetchPlan().addFetchGroup("orgUsers");
			if (force) {
				kq.getFetchPlan().addFetchGroup("backupexport");
			}
			u = kq.getSingleResult();
			oem.getFetchPlan().setQueryResultCacheEnabled(qrce);
		} else {
			log.info("[get] " + "Info: No USER_ID given");
		}
		return u;
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

	public List<User> get(Collection<String> ids) {
		return em.createNamedQuery("getUsersByIds", User.class).setParameter("ids", ids).getResultList();
	}

	public List<User> getAllUsers() {
		TypedQuery<User> q = em.createNamedQuery("getNondeletedUsers", User.class);
		return q.getResultList();
	}

	public List<User> getAllBackupUsers() {
		try {
			TypedQuery<User> q = em.createNamedQuery("getAllUsers", User.class);
			@SuppressWarnings("unchecked")
			OpenJPAQuery<User> kq = OpenJPAPersistence.cast(q);
			kq.getFetchPlan().addFetchGroups("backupexport", "orgUsers");
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
	
	public User getUserByName(String login, Type type) {
		User us = null;
		try {
			us = em.createNamedQuery("getUserByLogin", User.class)
					.setParameter("login", login)
					.setParameter("type", type)
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
	
	public User getContact(String email, String firstName, String lastName, Long langId, String tzId, long ownerId) {
		return getContact(email, firstName, lastName, langId, tzId, get(ownerId));
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

	private <T> TypedQuery<T> getUserProfileQuery(Class<T> clazz, long userId, String text, String offers, String search, String orderBy, boolean asc) {
		Map<String, Object> params = new HashMap<String, Object>();
		boolean filterContacts = true;
		boolean count = clazz.isAssignableFrom(Long.class);
		
		StringBuilder sb = new StringBuilder("SELECT ");
		sb.append(count ? "COUNT(" : "").append("u").append(count ? ") " : " ")
			.append("FROM User u ").append(getAdditionalJoin(filterContacts)).append(" WHERE u.deleted = false AND ")
			.append(getAdditionalWhere(filterContacts, userId, params));
		if (!Strings.isEmpty(offers)) {
			sb.append(" AND (LOWER(u.userOffers) LIKE :userOffers) ");
			params.put("userOffers", getStringParam(offers));
		}
		if (!Strings.isEmpty(search)) {
			sb.append(" AND (LOWER(u.userSearchs) LIKE :userSearchs) ");
			params.put("userSearchs", getStringParam(search));
		}
		if (!Strings.isEmpty(text)) {
			sb.append(" AND (LOWER(u.login) LIKE :search ")
				.append("OR LOWER(u.firstname) LIKE :search ")
				.append("OR LOWER(u.lastname) LIKE :search ")
				.append("OR LOWER(u.adresses.email) LIKE :search ")
				.append("OR LOWER(u.adresses.town) LIKE :search " + ") ");
			params.put("search", getStringParam(text));
		}
		if (!count && !Strings.isEmpty(orderBy)) {
			sb.append(" ORDER BY ").append(orderBy).append(asc ? " ASC" : " DESC");
		}
		TypedQuery<T> query = em.createQuery(sb.toString(), clazz);
		setAdditionalParams(query, params);
		return query;
	}
	
	private String getStringParam(String param) {
		return param == null ? "%" : "%" + StringUtils.lowerCase(param) + "%";
	}
	
	public List<User> searchUserProfile(long userId, String text, String offers, String search, String orderBy, int start, int max, boolean asc) {
		return getUserProfileQuery(User.class, userId, text, offers, search, orderBy, asc).setFirstResult(start).setMaxResults(max).getResultList();
	}

	public Long searchCountUserProfile(long userId, String text, String offers, String search) {
		return getUserProfileQuery(Long.class, userId, text, offers, search, null, false).getSingleResult();
	}

	public User getExternalUser(String extId, String extType) {
		return em.createNamedQuery("getExternalUser", User.class)
				.setParameter("externalId", extId)
				.setParameter("externalType", extType)
				.getSingleResult();
	}

	public List<User> get(String search, int start, int count, String order) {
		return get(search, start, count, order, false, -1);
	}
}
