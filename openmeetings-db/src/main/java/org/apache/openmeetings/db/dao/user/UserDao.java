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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LANG_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
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
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Salutation;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.db.util.UserHelper;
import org.apache.openmeetings.util.DaoHelper;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.util.crypt.CryptProvider;
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

	public final static String[] searchFields = {"lastname", "firstname", "login", "address.email", "address.town"};

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ConfigurationDao cfgDao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private TimezoneUtil timezoneUtil;

	public static Set<Right> getDefaultRights() {
		Set<Right> rights = new HashSet<User.Right>();
		rights.add(Right.Login);
		rights.add(Right.Dashboard);
		rights.add(Right.Room);
		return rights;
	}
	/**
	 * Get a new instance of the {@link User} entity, with all default values
	 * set
	 * 
	 * @param currentUser - the user to copy time zone from
	 * @return new User instance
	 */
	public User getNewUserInstance(User currentUser) {
		User user = new User();
		user.setSalutation(Salutation.mr); // TODO: Fix default selection to be configurable
		user.setRights(getDefaultRights());
		user.setLanguageId(cfgDao.getConfValue(CONFIG_DEFAULT_LANG_KEY, Long.class, "1"));
		user.setTimeZoneId(timezoneUtil.getTimeZone(currentUser).getID());
		user.setForceTimeZoneCheck(false);
		user.setSendSMS(false);
		user.setAge(new Date());
		Address address = new Address();
		address.setCountry(Locale.getDefault().getCountry());
		user.setAddress(address);
		user.setShowContactData(false);
		user.setShowContactDataToContacts(false);

		return user;
	}

	@Override
	public List<User> get(int first, int count) {
		TypedQuery<User> q = em.createNamedQuery("getNondeletedUsers", User.class);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	private static String getAdditionalJoin(boolean filterContacts) {
		return filterContacts ? "LEFT JOIN u.groupUsers ou" : null;
	}
	
	private static String getAdditionalWhere(boolean excludeContacts, Map<String, Object> params) {
		if (excludeContacts) {
			params.put("contact", Type.contact);
			return "u.type <> :contact";
		}
		return null;
	}
	
	private static String getAdditionalWhere(boolean filterContacts, Long ownerId, Map<String, Object> params) {
		if (filterContacts) {
			params.put("ownerId", ownerId);
			params.put("contact", Type.contact);
			return "((u.type <> :contact AND ou.group.id IN (SELECT ou.group.id FROM GroupUser ou WHERE ou.user.id = :ownerId)) "
				+ "OR (u.type = :contact AND u.ownerId = :ownerId))";
		}
		return null;
	}
	
	private static void setAdditionalParams(TypedQuery<?> q, Map<String, Object> params) {
		for (Map.Entry<String, Object> me: params.entrySet()) {
			q.setParameter(me.getKey(), me.getValue());
		}
	}

	public List<User> get(String search, int start, int count, String sort, boolean filterContacts, Long currentUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", getAdditionalJoin(filterContacts), search, true, true, false
				, getAdditionalWhere(filterContacts, currentUserId, params), sort, searchFields), User.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		setAdditionalParams(q, params);
		return q.getResultList();
	}
	
	@Override
	public long count() {
		// get all users
		TypedQuery<Long> q = em.createNamedQuery("countNondeletedUsers", Long.class);
		return q.getSingleResult();
	}

	@Override
	public long count(String search) {
		return count(search, false, Long.valueOf(-1));
	}
	
	public long count(String search, Long currentUserId) {
		return count(search, false, currentUserId);
	}
	
	public long count(String search, boolean filterContacts, Long currentUserId) {
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

	public List<User> get(String search, boolean filterContacts, Long currentUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", getAdditionalJoin(filterContacts), search, true, true, false
				, getAdditionalWhere(filterContacts, currentUserId, params), null, searchFields), User.class);
		setAdditionalParams(q, params);
		return q.getResultList();
	}

	@Override
	public User update(User u, Long userId) {
		if (u.getId() == null) {
			if (u.getRegdate() == null) {
				u.setRegdate(new Date());
			}
			u.setInserted(new Date());
			em.persist(u);
		} else {
			u.setUpdated(new Date());
			u =	em.merge(u);
		}
		return u;
	}
	
	//this method is required to be able to drop reset hash
	public User resetPassword(User u, String password) throws NoSuchAlgorithmException {
		if (u != null) {
			u.setResethash(null);
			u = update(u, password, u.getId());
		}
		return u;
	}
	
	// TODO: Why the password field is not set via the Model is because its
	// FetchType is Lazy, this extra hook here might be not needed with a
	// different mechanism to protect the password from being read
	// sebawagner, 01.10.2012
	public User update(User user, String password, Long updatedBy) throws NoSuchAlgorithmException {
		User u = update(user, updatedBy);
		if (u != null && !Strings.isEmpty(password)) {
			//OpenJPA is not allowing to set fields not being fetched before
			User u1 = get(u.getId(), true);
			u1.updatePassword(cfgDao, password);
			u = update(u1, updatedBy);
		}
		return u;
	}
	
	@Override
	public User get(Long id) {
		return get(id, false);
	}
	
	@Override
	public User get(long id) {
		return get(Long.valueOf(id), false);
	}
	
	private User get(Long id, boolean force) {
		User u = null;
		if (id != null && id.longValue() > 0) {
			OpenJPAEntityManager oem = OpenJPAPersistence.cast(em);
			boolean qrce = oem.getFetchPlan().getQueryResultCacheEnabled();
			try {
				oem.getFetchPlan().setQueryResultCacheEnabled(false); //FIXME update in cache during update
				TypedQuery<User> q = oem.createNamedQuery("getUserById", User.class).setParameter("id", id);
				@SuppressWarnings("unchecked")
				OpenJPAQuery<User> kq = OpenJPAPersistence.cast(q);
				kq.getFetchPlan().addFetchGroup("groupUsers");
				if (force) {
					kq.getFetchPlan().addFetchGroup("backupexport");
				}
				try {
					u = kq.getSingleResult();
				} catch (NoResultException ne) {
					//no-op
				}
			} finally {
				oem.getFetchPlan().setQueryResultCacheEnabled(qrce);
			}
		} else {
			log.info("[get] " + "Info: No user id given");
		}
		return u;
	}

	@Override
	public void delete(User u, Long userId) {
		if (u != null && u.getId() != null) {
			u.setGroupUsers(new ArrayList<>());
			u.setDeleted(true);
			u.setUpdated(new Date());
			u.setSipUser(null);
			Address adr = u.getAddress();
			if (adr != null) {
				adr.setDeleted(true);
			}
			update(u, userId);
		}
	}

	public List<User> get(Collection<Long> ids) {
		return em.createNamedQuery("getUsersByIds", User.class).setParameter("ids", ids).getResultList();
	}

	public List<User> getAllUsers() {
		TypedQuery<User> q = em.createNamedQuery("getNondeletedUsers", User.class);
		return q.getResultList();
	}

	public List<User> getAllBackupUsers() {
		OpenJPAEntityManager oem = OpenJPAPersistence.cast(em);
		boolean qrce = oem.getFetchPlan().getQueryResultCacheEnabled();
		try {
			oem.getFetchPlan().setQueryResultCacheEnabled(false); //FIXME update in cache during update
			TypedQuery<User> q = oem.createNamedQuery("getAllUsers", User.class);
			@SuppressWarnings("unchecked")
			OpenJPAQuery<User> kq = OpenJPAPersistence.cast(q);
			kq.getFetchPlan().addFetchGroups("backupexport", "groupUsers");
			return kq.getResultList();
		} finally {
			oem.getFetchPlan().setQueryResultCacheEnabled(qrce);
		}
	}

	/**
	 * check for duplicates
	 * 
	 * @param login
	 * @param type
	 * @param domainId
	 * @param id
	 * @return
	 */
	public boolean checkLogin(String login, Type type, Long domainId, Long id) {
		try {
			User u = getByLogin(login, type, domainId);
			return u == null || u.getId().equals(id);
		} catch (Exception e) {
			//exception is thrown in case of non-unique result
			return false;
		}
	}

	/**
	 * Checks if a mail is already taken by someone else
	 * 
	 * @param email
	 * @param type
	 * @param domainId
	 * @param id
	 * @return
	 */
	public boolean checkEmail(String email, Type type, Long domainId, Long id) {
		log.debug("checkEmail: email = {}, id = {}", email, id);
		try {
			User u = getByEmail(email, type, domainId);
			return u == null || u.getId().equals(id);
		} catch (Exception e) {
			//exception is thrown in case of non-unique result
			return false;
		}
	}

	public boolean validLogin(String login) {
		return !Strings.isEmpty(login) && login.length() >= UserHelper.getMinLoginLength(cfgDao);
	}
	
	public User getByLogin(String login, Type type, Long domainId) {
		User u = null;
		try {
			u = em.createNamedQuery("getUserByLogin", User.class)
					.setParameter("login", login)
					.setParameter("type", type)
					.setParameter("domainId", domainId == null ? Long.valueOf(0) : domainId)
					.getSingleResult();
		} catch (NoResultException ex) {
		}
		return u;
	}

	public User getByEmail(String email) {
		return getByEmail(email, User.Type.user, null);
	}

	public User getByEmail(String email, User.Type type, Long domainId) {
		User u = null;
		try {
			u = em.createNamedQuery("getUserByEmail", User.class)
					.setParameter("email", email)
					.setParameter("type", type)
					.setParameter("domainId", domainId == null ? Long.valueOf(0) : domainId)
					.getSingleResult();
		} catch (NoResultException ex) {
		}
		return u;
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
		List<String> l = em.createNamedQuery("getPassword", String.class)
			.setParameter("userId", userId).getResultList();
		if (l == null || l.size() != 1) {
			return false;
		}
		return CryptProvider.get().verify(password, l.get(0));
	}

	public User getContact(String email, Long ownerId) {
		return getContact(email, "", "", ownerId);
	}
	
	public User getContact(String email, User owner) {
		return getContact(email, "", "", null, null, owner);
	}
	
	public User getContact(String email, String firstName, String lastName, Long ownerId) {
		return getContact(email, firstName, lastName, null, null, get(ownerId));
	}
	
	public User getContact(String email, String firstName, String lastName, Long langId, String tzId, Long ownerId) {
		return getContact(email, firstName, lastName, langId, tzId, get(ownerId));
	}
	
	public User getContact(String email, String firstName, String lastName, Long langId, String tzId, User owner) {
		User to = null;
		try {
			to = em.createNamedQuery("getContactByEmailAndUser", User.class)
					.setParameter("email", email).setParameter("type", User.Type.contact).setParameter("ownerId", owner.getId()).getSingleResult();
		} catch (Exception e) {
			//no-op
		}
		if (to == null) {
			to = new User();
			to.setType(Type.contact);
			String login = owner.getId() + "_" + email; //UserId prefix is used to ensure unique login
			to.setLogin(login.length() < getMinLoginLength(cfgDao) ? UUID.randomUUID().toString() : login);
			to.setFirstname(firstName);
			to.setLastname(lastName);
			to.setLanguageId(null == langId ? owner.getLanguageId() : langId.longValue());
			to.setOwnerId(owner.getId());
			to.setAddress(new Address());
			to.getAddress().setEmail(email);
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

	private <T> TypedQuery<T> getUserProfileQuery(Class<T> clazz, Long userId, String text, String offers, String search, String orderBy, boolean asc) {
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
				.append("OR LOWER(u.address.email) LIKE :search ")
				.append("OR LOWER(u.address.town) LIKE :search " + ") ");
			params.put("search", getStringParam(text));
		}
		if (!count && !Strings.isEmpty(orderBy)) {
			sb.append(" ORDER BY ").append(orderBy).append(asc ? " ASC" : " DESC");
		}
		TypedQuery<T> query = em.createQuery(sb.toString(), clazz);
		setAdditionalParams(query, params);
		return query;
	}
	
	private static String getStringParam(String param) {
		return param == null ? "%" : "%" + StringUtils.lowerCase(param) + "%";
	}
	
	public List<User> searchUserProfile(Long userId, String text, String offers, String search, String orderBy, int start, int max, boolean asc) {
		return getUserProfileQuery(User.class, userId, text, offers, search, orderBy, asc).setFirstResult(start).setMaxResults(max).getResultList();
	}

	public Long searchCountUserProfile(Long userId, String text, String offers, String search) {
		return getUserProfileQuery(Long.class, userId, text, offers, search, null, false).getSingleResult();
	}

	public User getExternalUser(String extId, String extType) {
		User u = null;
		try {
			u = em.createNamedQuery("getExternalUser", User.class)
				.setParameter("externalId", extId)
				.setParameter("externalType", extType)
				.getSingleResult();
		} catch (NoResultException ex) {
		}
		return u;
	}

	@Override
	public List<User> get(String search, int start, int count, String order) {
		return get(search, start, count, order, false, Long.valueOf(-1));
	}
	
	public Set<Right> getRights(Long id) {
		Set<Right> rights = new HashSet<Right>();

		if (id == null) {
			return rights;
		}
		// For direct access of linked users
		if (id.longValue() < 0) {
			rights.add(Right.Room);
			return rights;
		}

		User u = get(id);
		if (u != null) {
			return u.getRights();
		}
		return rights;
	}
	
	/**
	 * login logic
	 * 
	 * @param userOrEmail: login or email of the user being tested
	 * @param userpass: password of the user being tested
	 * @return User object in case of successful login
	 * @throws OmException in case of any issue 
	 */
	public User login(String userOrEmail, String userpass) throws OmException {
		List<User> users = em.createNamedQuery("getUserByLoginOrEmail", User.class)
				.setParameter("userOrEmail", userOrEmail)
				.setParameter("type", Type.user)
				.getResultList();

		log.debug("login:: {} users were found", users.size());

		if (users.size() == 0) {
			log.debug("No users was found: {}", userOrEmail);
			throw new OmException(-10L);
		}
		User u = users.get(0);

		if (!verifyPassword(u.getId(), userpass)) {
			log.debug("Password does not match: {}", u);
			throw new OmException(-11L);
		}
		// Check if activated
		if (!AuthLevelUtil.hasLoginLevel(u.getRights())) {
			log.debug("Not activated: {}", u);
			throw new OmException(-41L);
		}
		log.debug("loginUser " + u.getGroupUsers());
		if (u.getGroupUsers().isEmpty()) {
			log.debug("No Group assigned: {}", u);
			throw new OmException("No Group assigned to user");
		}
		
		u.setLastlogin(new Date());
		return update(u, u.getId());
	}
	
	public Address getAddress(String street, String zip, String town, String country, String additionalname, String fax, String phone, String email) {
		Address a =  new Address();
		a.setStreet(street);
		a.setZip(zip);
		a.setTown(town);
		a.setCountry(country);
		a.setAdditionalname(additionalname);
		a.setComment("");
		a.setFax(fax);
		a.setPhone(phone);
		a.setEmail(email);
		return a;
	}
	
	public User addUser(Set<Right> rights, String firstname, String login, String lastname, long languageId,
			String userpass, Address adress, boolean sendSMS, Date age, String hash, TimeZone timezone,
			boolean forceTimeZoneCheck, String userOffers, String userSearchs, boolean showContactData,
			boolean showContactDataToContacts, String externalId, String externalType, List<Long> groupIds, String pictureuri) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		User u = new User();
		u.setFirstname(firstname);
		u.setLogin(login);
		u.setLastname(lastname);
		u.setAge(age);
		u.setAddress(adress);
		u.setSendSMS(sendSMS);
		u.setRights(rights);
		u.setLastlogin(new Date());
		u.setLasttrans(new Long(0));
		u.setSalutation(Salutation.mr);
		u.setActivatehash(hash);
		u.setTimeZoneId(timezone.getID());
		u.setForceTimeZoneCheck(forceTimeZoneCheck);
		u.setExternalId(externalId);
		u.setExternalType(externalType);
		if (!Strings.isEmpty(u.getExternalType())) {
			u.setType(Type.external);
		}

		u.setUserOffers(userOffers);
		u.setUserSearchs(userSearchs);
		u.setShowContactData(showContactData);
		u.setShowContactDataToContacts(showContactDataToContacts);

		// this is needed cause the language is not a needed data at registering
		u.setLanguageId(languageId != 0 ? languageId : 1);
		if (!Strings.isEmpty(userpass)) {
			u.updatePassword(cfgDao, userpass);
		}
		u.setDeleted(false);
		u.setPictureuri(pictureuri);
		if (groupIds != null) {
			for (Long grpId : groupIds) {
				u.getGroupUsers().add(new GroupUser(groupDao.get(grpId), u));
			}
		}
		
		return update(u, null);
	}
}
