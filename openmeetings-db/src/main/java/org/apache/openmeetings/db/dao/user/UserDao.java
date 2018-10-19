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

import static org.apache.openmeetings.db.util.DaoHelper.getStringParam;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultTimezone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLoginLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;
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
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openmeetings.db.dao.IGroupAdminDataProviderDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.AsteriskSipUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Salutation;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.util.crypt.ICrypt;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD operations for {@link User}
 *
 * @author swagner, solomax, vasya
 *
 */
@Repository
@Transactional
public class UserDao implements IGroupAdminDataProviderDao<User> {
	private static final Logger log = Red5LoggerFactory.getLogger(UserDao.class, getWebAppRootKey());
	private static final String[] searchFields = {"lastname", "firstname", "login", "address.email", "address.town"};

	@PersistenceContext
	private EntityManager em;

	public static Set<Right> getDefaultRights() {
		Set<Right> rights = new HashSet<>();
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
	public static User getNewUserInstance(User currentUser) {
		User user = new User();
		user.setSalutation(Salutation.mr);
		user.setRights(getDefaultRights());
		user.setLanguageId(getDefaultLang());
		user.setTimeZoneId(getTimeZone(currentUser).getID());
		user.setForceTimeZoneCheck(false);
		user.setAge(new Date());
		user.setLastlogin(new Date());
		Address address = new Address();
		address.setCountry(Locale.getDefault().getCountry());
		user.setAddress(address);
		user.setShowContactData(false);
		user.setShowContactDataToContacts(false);

		return user;
	}

	@Override
	public List<User> get(long first, long count) {
		return setLimits(
				em.createNamedQuery("getNondeletedUsers", User.class)
				, first, count).getResultList();
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

	private List<User> get(String search, Long start, Long count, String order, boolean filterContacts, Long currentUserId, boolean filterDeleted) {
		Map<String, Object> params = new HashMap<>();
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", getAdditionalJoin(filterContacts), search, true, filterDeleted, false
				, getAdditionalWhere(filterContacts, currentUserId, params), order, searchFields), User.class);
		setAdditionalParams(setLimits(q, start, count), params);
		return q.getResultList();
	}

	//This is AdminDao method
	public List<User> get(String search, boolean excludeContacts, long first, long count) {
		Map<String, Object> params = new HashMap<>();
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", null, search, true, true, false
				, getAdditionalWhere(excludeContacts, params), null, searchFields), User.class);
		setAdditionalParams(setLimits(q, first, count), params);
		return q.getResultList();
	}

	public List<User> get(String search, boolean filterContacts, Long currentUserId) {
		return get(search, null, null, null, filterContacts, currentUserId, true);
	}

	public List<User> get(String search, long start, long count, String sort, boolean filterContacts, Long currentUserId) {
		return get(search, start, count, sort, filterContacts, currentUserId, true);
	}

	@Override
	public List<User> adminGet(String search, long start, long count, String order) {
		return get(search, start, count, order, false, null, false);
	}

	@Override
	public List<User> adminGet(String search, Long adminId, long start, long count, String order) {
		TypedQuery<User> q = em.createQuery(DaoHelper.getSearchQuery("GroupUser gu, IN(gu.user)", "u", null, search, true, false, false
				, "gu.group.id IN (SELECT gu1.group.id FROM GroupUser gu1 WHERE gu1.moderator = true AND gu1.user.id = :adminId)", order, searchFields), User.class);
		q.setParameter("adminId", adminId);
		return setLimits(q, start, count).getResultList();
	}

	private long count(String search, boolean filterContacts, Long currentUserId, boolean filterDeleted) {
		Map<String, Object> params = new HashMap<>();
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("User", "u", getAdditionalJoin(filterContacts), search, true, filterDeleted, true
				, getAdditionalWhere(filterContacts, currentUserId, params), null, searchFields), Long.class);
		setAdditionalParams(q, params);
		return q.getSingleResult();
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

	public long countUsers(String search, Long currentUserId) {
		return count(search, false, currentUserId);
	}

	public long count(String search, boolean filterContacts, Long currentUserId) {
		return count(search, filterContacts, currentUserId, true);
	}

	@Override
	public long adminCount(String search) {
		return count(search, false, Long.valueOf(-1), false);
	}

	@Override
	public long adminCount(String search, Long adminId) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("GroupUser gu, IN(gu.user)", "u", null, search, true, false, true
				, "gu.group.id IN (SELECT gu1.group.id FROM GroupUser gu1 WHERE gu1.moderator = true AND gu1.user.id = :adminId)", null, searchFields), Long.class);
		q.setParameter("adminId", adminId);
		return q.getSingleResult();
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
			u = em.merge(u);
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

	private User updatePassword(Long id, String pwd, Long updatedBy) throws NoSuchAlgorithmException {
		//OpenJPA is not allowing to set fields not being fetched before
		User u = get(id, true);
		u.updatePassword(pwd);
		return update(u, updatedBy);
	}

	// Why the password field is not set via the Model is because its FetchType is Lazy
	public User update(User user, String password, Long updatedBy) throws NoSuchAlgorithmException {
		User u = update(user, updatedBy);
		if (u != null && !Strings.isEmpty(password)) {
			u = updatePassword(u.getId(), password, updatedBy);
		}
		return u;
	}

	@Override
	public User get(Long id) {
		return get(id, false);
	}

	private User get(Long id, boolean force) {
		User u = null;
		if (id != null && id.longValue() > 0) {
			OpenJPAEntityManager oem = OpenJPAPersistence.cast(em);
			boolean qrce = oem.getFetchPlan().getQueryResultCacheEnabled();
			try {
				oem.getFetchPlan().setQueryResultCacheEnabled(false); //update in cache during update
				TypedQuery<User> q = oem.createNamedQuery("getUserById", User.class).setParameter("id", id);
				@SuppressWarnings("unchecked")
				OpenJPAQuery<User> kq = OpenJPAPersistence.cast(q);
				kq.getFetchPlan().addFetchGroup("groupUsers");
				if (force) {
					kq.getFetchPlan().addFetchGroup("backupexport");
				}
				List<User> list = kq.getResultList();
				u = list.size() == 1 ? list.get(0) : null;
			} finally {
				oem.getFetchPlan().setQueryResultCacheEnabled(qrce);
			}
		} else {
			log.info("[get]: No user id given");
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

	// created here so this action would be executed in Transaction
	public void purge(User u, Long userId) {
		if (u != null && u.getId() != null) {
			em.createNamedQuery("purgeChatUserName")
				.setParameter("purged", "Purged User")
				.setParameter("userId", u.getId())
				.executeUpdate();
			em.createNamedQuery("clearLogUserIpByUser")
				.setParameter("userId", u.getId())
				.executeUpdate();
			if (!Strings.isEmpty(u.getAddress().getEmail())) {
				em.createNamedQuery("purgeMailMessages")
					.setParameter("email", String.format("%%%s%%", u.getAddress().getEmail()))
					.executeUpdate();
			}
			u.setActivatehash(null);
			u.setResethash(null);
			u.setDeleted(true);
			u.setSipUser(new AsteriskSipUser());
			u.setAddress(new Address());
			u.setAge(new Date());
			u.setExternalId(null);
			final String purged = String.format("Purged %s", UUID.randomUUID());
			u.setFirstname(purged);
			u.setLastname(purged);
			u.setLogin(purged);
			u.setGroupUsers(new ArrayList<>());
			u.setRights(new HashSet<>());
			u.setTimeZoneId(getDefaultTimezone());
			File pic = OmFileHelper.getUserProfilePicture(u.getId(), u.getPictureUri(), null);
			u.setPictureUri(null);
			ICrypt crypt = CryptProvider.get();
			try {
				u.updatePassword(crypt.randomPassword(25));
			} catch (NoSuchAlgorithmException e) {
				log.error("Unexpected exception while updating password");
			}
			update(u, userId);
			// this should be last action, so file will be deleted in case there were no errors
			if (pic != null) {
				pic.delete();
			}
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
			oem.getFetchPlan().setQueryResultCacheEnabled(false); //update in cache during update
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
	 * @param login - login to check
	 * @param type - user {@link Type} to check
	 * @param domainId - domain to check
	 * @param id - id of current user to allow self update
	 * @return <code>true</code> in case login is allowed
	 */
	public boolean checkLogin(String login, Type type, Long domainId, Long id) {
		User u = getByLogin(login, type, domainId);
		return u == null || u.getId().equals(id);
	}

	/**
	 * Checks if a mail is already taken by someone else
	 *
	 * @param email - email to check
	 * @param type - user {@link Type} to check
	 * @param domainId - domain to check
	 * @param id - id of current user to allow self update
	 * @return <code>true</code> in case email is allowed
	 */
	public boolean checkEmail(String email, Type type, Long domainId, Long id) {
		log.debug("checkEmail: email = {}, id = {}", email, id);
		User u = getByEmail(email, type, domainId);
		return u == null || u.getId().equals(id);
	}

	public boolean validLogin(String login) {
		return !Strings.isEmpty(login) && login.length() >= getMinLoginLength();
	}

	private static User getSingle(List<User> list) {
		User u = null;
		if (list.size() == 1) {
			u = list.get(0);
			u.getGroupUsers().size(); // this will initiate lazy collection
		}
		return u;
	}

	public User getByLogin(String login, Type type, Long domainId) {
		return getSingle(em.createNamedQuery("getUserByLogin", User.class)
				.setParameter("login", login)
				.setParameter("type", type)
				.setParameter("domainId", domainId == null ? Long.valueOf(0) : domainId)
				.getResultList());
	}

	public User getByEmail(String email) {
		return getByEmail(email, User.Type.user, null);
	}

	public User getByEmail(String email, User.Type type, Long domainId) {
		return getSingle(em.createNamedQuery("getUserByEmail", User.class)
				.setParameter("email", email)
				.setParameter("type", type)
				.setParameter("domainId", domainId == null ? Long.valueOf(0) : domainId)
				.getResultList());
	}

	public User getUserByHash(String hash) {
		if (Strings.isEmpty(hash)) {
			return null;
		}
		return getSingle(em.createNamedQuery("getUserByHash", User.class)
					.setParameter("resethash", hash)
					.setParameter("type", User.Type.user)
					.getResultList());
	}

	/**
	 * @param search - term to search
	 * @return - number of matching user
	 */
	public Long selectMaxFromUsersWithSearch(String search) {
		try {
			// get all users
			TypedQuery<Long> query = em.createNamedQuery("selectMaxFromUsersWithSearch", Long.class);
			query.setParameter("search", StringUtils.lowerCase(search));
			List<Long> ll = query.getResultList();
			log.info("selectMaxFromUsers {}", ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] ", ex2);
		}
		return null;
	}

	/**
	 * Returns true if the password is correct
	 *
	 * @param userId - id of the user to check
	 * @param password - password to check
	 * @return <code>true</code> if entered password is correct
	 */
	public boolean verifyPassword(Long userId, String password) {
		List<String> l = em.createNamedQuery("getPassword", String.class)
			.setParameter(PARAM_USER_ID, userId).getResultList();
		if (l == null || l.size() != 1) {
			return false;
		}
		String hash = l.get(0);
		ICrypt crypt = CryptProvider.get();
		if (crypt.verify(password, hash)) {
			return true;
		}
		if (crypt.fallback(password, hash)) {
			log.warn("Password for user with ID {} crypted with outdated Crypt, updating ...", userId);
			try {
				User u = updatePassword(userId, password, userId);
				log.warn("Password for user {} updated successfully", u);
				return true;
			} catch (NoSuchAlgorithmException e) {
				log.error("Unexpected exception while updating password");
			}
		}
		return false;
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
		List<User> list = em.createNamedQuery("getContactByEmailAndUser", User.class)
				.setParameter("email", email).setParameter("type", User.Type.contact).setParameter("ownerId", owner.getId())
				.getResultList();
		if (list.isEmpty()) {
			User to = new User();
			to.setType(Type.contact);
			String login = owner.getId() + "_" + email; //UserId prefix is used to ensure unique login
			to.setLogin(login.length() < getMinLoginLength() ? UUID.randomUUID().toString() : login);
			to.setFirstname(firstName);
			to.setLastname(lastName);
			to.setLanguageId(null == langId || null == LabelDao.getLocale(langId) ? owner.getLanguageId() : langId.longValue());
			to.setOwnerId(owner.getId());
			to.setAddress(new Address());
			to.getAddress().setEmail(email);
			to.setTimeZoneId(Strings.isEmpty(tzId) ? owner.getTimeZoneId() : tzId);
			return to;
		}
		return list.get(0);
	}

	/**
	 * @param hash - activation hash
	 * @return user with this hash
	 */
	public User getByActivationHash(String hash) {
		return getSingle(em.createQuery("SELECT u FROM User as u WHERE u.activatehash = :activatehash AND u.deleted = false", User.class)
				.setParameter("activatehash", hash).getResultList());
	}

	private <T> TypedQuery<T> getUserProfileQuery(Class<T> clazz, Long userId, String text, String offers, String search, String orderBy, boolean asc) {
		Map<String, Object> params = new HashMap<>();
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

	public List<User> searchUserProfile(Long userId, String text, String offers, String search, String orderBy, long start, long max, boolean asc) {
		return setLimits(getUserProfileQuery(User.class, userId, text, offers, search, orderBy, asc)
				, start, max).getResultList();
	}

	public Long searchCountUserProfile(Long userId, String text, String offers, String search) {
		return getUserProfileQuery(Long.class, userId, text, offers, search, null, false).getSingleResult();
	}

	public User getExternalUser(String extId, String extType) {
		return getSingle(em.createNamedQuery("getExternalUser", User.class)
				.setParameter("externalId", extId)
				.setParameter("externalType", extType)
				.getResultList());
	}

	@Override
	public List<User> get(String search, long start, long count, String order) {
		return get(search, start, count, order, false, Long.valueOf(-1));
	}

	public Set<Right> getRights(Long id) {
		Set<Right> rights = new HashSet<>();

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
	 * @param userOrEmail - login or email of the user being tested
	 * @param userpass - password of the user being tested
	 * @return User object in case of successful login
	 * @throws OmException in case of any issue
	 */
	public User login(String userOrEmail, String userpass) throws OmException {
		List<User> users = em.createNamedQuery("getUserByLoginOrEmail", User.class)
				.setParameter("userOrEmail", userOrEmail)
				.setParameter("type", Type.user)
				.getResultList();

		log.debug("login:: {} users were found", users.size());

		if (users.isEmpty()) {
			log.debug("No users was found: {}", userOrEmail);
			return null;
		}
		User u = users.get(0);

		if (!verifyPassword(u.getId(), userpass)) {
			log.debug("Password does not match: {}", u);
			return null;
		}
		// Check if activated
		if (!AuthLevelUtil.hasLoginLevel(u.getRights())) {
			log.debug("Not activated: {}", u);
			throw new OmException("error.notactivated");
		}
		log.debug("loginUser " + u.getGroupUsers());
		if (u.getGroupUsers().isEmpty()) {
			log.debug("No Group assigned: {}", u);
			throw new OmException("error.nogroup");
		}

		u.setLastlogin(new Date());
		return update(u, u.getId());
	}

	public List<User> getByExpiredHash(long ttl) {
		return em.createNamedQuery("getUserByExpiredHash", User.class)
				.setParameter("date", new Date(System.currentTimeMillis() - ttl))
				.getResultList();
	}
}
