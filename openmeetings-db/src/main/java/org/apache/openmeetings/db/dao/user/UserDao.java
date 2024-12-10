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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.util.DaoHelper.fillLazy;
import static org.apache.openmeetings.db.util.DaoHelper.getRoot;
import static org.apache.openmeetings.db.util.DaoHelper.getStringParam;
import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.db.util.DaoHelper.setLimits;
import static org.apache.openmeetings.db.util.DaoHelper.single;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_USER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultTimezone;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinLoginLength;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.openmeetings.db.dao.IGroupAdminDataProviderDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.AsteriskSipUser;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.util.crypt.ICrypt;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD operations for {@link User}
 *
 * @author swagner, vasya
 *
 */
@Repository
@Transactional
public class UserDao implements IGroupAdminDataProviderDao<User> {
	private static final Logger log = LoggerFactory.getLogger(UserDao.class);
	private static final String PARAM_EMAIL = "email";
	private static final List<String> searchFields = List.of("lastname", "firstname", "login", "address.email", "address.town");
	private static final List<String> guSearchFields = searchFields.stream().map(f -> "user." + f).toList();
	private static final String FIELD_GROUP = "group";
	public static final String FETCH_GROUP_GROUP = "Group_Users";
	public static final String FETCH_GROUP_BACKUP = "Backup_Export";

	@PersistenceContext
	private EntityManager em;

	public static Set<Right> getDefaultRights() {
		Set<Right> rights = new HashSet<>();
		rights.add(Right.LOGIN);
		rights.add(Right.DASHBOARD);
		rights.add(Right.ROOM);
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
		user.setRights(getDefaultRights());
		user.setLanguageId(getDefaultLang());
		user.setTimeZoneId(getTimeZone(currentUser).getID());
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

	private Predicate getContactsFilter(CriteriaBuilder builder, CriteriaQuery<?> query) {
		Root<User> root = getRoot(query, User.class);
		return builder.notEqual(root.get("type"), Type.CONTACT);
	}

	private Predicate getOwnerContactsFilter(Long ownerId, CriteriaBuilder builder, CriteriaQuery<?> query) {
		Root<User> root = getRoot(query, User.class);
		root.join("groupUsers", JoinType.LEFT);

		Subquery<Long> subquery = query.subquery(Long.class);
		Root<GroupUser> subRoot = subquery.from(GroupUser.class);
		subquery.select(subRoot.get(FIELD_GROUP).get("id"));
		subquery.where(builder.equal(subRoot.get("user").get("id"), ownerId));
		return builder.or(
				builder.and(builder.notEqual(root.get("type"), Type.CONTACT), root.get("groupUsers").get(FIELD_GROUP).get("id").in(subquery))
				, builder.and(builder.equal(root.get("type"), Type.CONTACT), builder.equal(root.get("ownerId"), ownerId))
				);
	}

	private List<User> get(String search, Long start, Long count, SortParam<String> sort, boolean filterContacts, Long currentUserId, boolean filterDeleted) {
		if (filterContacts) {
			return DaoHelper.get(em, User.class
					, true, search, searchFields, true
					, (b, q) -> getOwnerContactsFilter(currentUserId, b, q)
					, sort, start, count);
		} else {
			return DaoHelper.get(em, User.class, false, search, searchFields, filterDeleted
					, null, sort, start, count);
		}
	}

	// This is AdminDao method
	public List<User> get(String search, boolean excludeContacts, long start, long count) {
		return DaoHelper.get(em, User.class, false, search, searchFields, true
				, excludeContacts ? this::getContactsFilter : null
				, null, start, count);
	}

	public List<User> get(String search, long start, long count, SortParam<String> sort, boolean filterContacts, Long currentUserId) {
		return get(search, start, count, sort, filterContacts, currentUserId, true);
	}

	@Override
	public List<User> adminGet(String search, long start, long count, SortParam<String> sort) {
		return get(search, start, count, sort, false, null, false);
	}

	private Predicate getAdminFilter(Long adminId, CriteriaBuilder builder, CriteriaQuery<?> query) {
		Root<GroupUser> root = getRoot(query, GroupUser.class);
		return builder.in(root.get(FIELD_GROUP).get("id")).value(DaoHelper.groupAdminQuery(adminId, builder, query));
	}

	@Override
	public List<User> adminGet(String search, Long adminId, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, GroupUser.class, User.class
				, (builder, root) -> root.get("user")
				, true, search, guSearchFields, false
				, (b, q) -> getAdminFilter(adminId, b, q)
				, sort, start, count);
	}

	private Predicate getProfileFilter(Long userId, String userOffers, String userSearches, CriteriaBuilder builder, CriteriaQuery<?> query) {
		Root<User> root = getRoot(query, User.class);
		Predicate result = getOwnerContactsFilter(userId, builder, query);
		if (!Strings.isEmpty(userOffers)) {
			result = builder.and(result, DaoHelper.like("userOffers", getStringParam(userOffers), builder, root));
		}
		if (!Strings.isEmpty(userSearches)) {
			result = builder.and(result, DaoHelper.like("userSearches", getStringParam(userSearches), builder, root));
		}
		return result;
	}

	public List<User> searchUserProfile(Long userId, String search, String userOffers, String userSearches, SortParam<String> sort, long start, long count) {
		return DaoHelper.get(em, User.class
				, true, search, searchFields, true
				, (b, q) -> getProfileFilter(userId, userOffers, userSearches, b, q)
				, sort, start, count);
	}

	private long count(String search, boolean filterContacts, Long currentUserId, boolean filterDeleted) {
		if (filterContacts) {
			return DaoHelper.count(em, User.class
					, CriteriaBuilder::countDistinct
					, search, searchFields, filterDeleted
					, (b, q) -> getOwnerContactsFilter(currentUserId, b, q));
		} else {
			return DaoHelper.count(em, User.class, search, searchFields, filterDeleted
					, null);
		}
	}

	@Override
	public long count() {
		return em.createNamedQuery("countNondeletedUsers", Long.class)
				.getSingleResult();
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
		return DaoHelper.count(em, GroupUser.class
				, (builder, root) -> builder.countDistinct(root.get("user"))
				, search, guSearchFields, false
				, (b, q) -> getAdminFilter(adminId, b, q));
	}

	public Long searchCountUserProfile(Long userId, String search, String userOffers, String userSearches) {
		return DaoHelper.count(em, User.class
				, CriteriaBuilder::countDistinct
				, search, searchFields, true
				, (b, q) -> getProfileFilter(userId, userOffers, userSearches, b, q));
	}

	@Override
	public User update(User u, Long userId) {
		if (u.getId() == null) {
			if (u.getRegdate() == null) {
				u.setRegdate(new Date());
			}
			em.persist(u);
		} else {
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
			List<String> groups = new ArrayList<>(2);
			groups.add(FETCH_GROUP_GROUP);
			if (force) {
				groups.add(FETCH_GROUP_BACKUP);
			}
			u = single(fillLazy(em
					, oem -> oem.createNamedQuery("getUserById", User.class).setParameter("id", id)
					, groups.toArray(new String[groups.size()])));
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
					.setParameter(PARAM_EMAIL, String.format("%%%s%%", u.getAddress().getEmail()))
					.executeUpdate();
			}
			u.setActivatehash(null);
			u.setResethash(null);
			u.setDeleted(true);
			u.setSipUser(new AsteriskSipUser());
			u.setAddress(new Address());
			u.setAge(LocalDate.now());
			u.setExternalId(null);
			final String purged = "Purged " + randomUUID();
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
				try {
					Files.deleteIfExists(pic.toPath());
				} catch (IOException e) {
					log.error("Unexpected exception while delete pic");
				}
			}
		}
	}

	public List<User> get(Collection<Long> ids) {
		return em.createNamedQuery("getUsersByIds", User.class).setParameter("ids", ids).getResultList();
	}

	public List<User> getAllUsers() {
		return fillLazy(em
				, oem -> oem.createNamedQuery("getNondeletedUsers", User.class)
				, FETCH_GROUP_GROUP);
	}

	public List<User> getAllBackupUsers() {
		return fillLazy(em
				, oem -> oem.createNamedQuery("getAllUsers", User.class)
				, FETCH_GROUP_BACKUP, FETCH_GROUP_GROUP);
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

	public User getByLogin(String inLogin, Type type, Long domainId) {
		String login = inLogin == null ? null : inLogin.trim().toLowerCase(Locale.ROOT);
		return single(fillLazy(em
				, oem -> oem.createNamedQuery("getUserByLogin", User.class)
					.setParameter("login", login)
					.setParameter("type", type)
					.setParameter("domainId", domainId == null ? Long.valueOf(0) : domainId)
				, FETCH_GROUP_GROUP));
	}

	public User getByEmail(String email) {
		return getByEmail(email, User.Type.USER, null);
	}

	public User getByEmail(String inEmail, User.Type type, Long domainId) {
		String email = inEmail == null ? null : inEmail.trim().toLowerCase(Locale.ROOT);
		return single(fillLazy(em
				, oem -> oem.createNamedQuery("getUserByEmail", User.class)
					.setParameter(PARAM_EMAIL, email)
					.setParameter("type", type)
					.setParameter("domainId", domainId == null ? Long.valueOf(0) : domainId)
				, FETCH_GROUP_GROUP));
	}

	public User getUserByHash(String hash) {
		if (Strings.isEmpty(hash)) {
			return null;
		}
		return single(fillLazy(em
				, oem -> oem.createNamedQuery("getUserByHash", User.class)
					.setParameter("resethash", hash)
					.setParameter("type", User.Type.USER)
				, FETCH_GROUP_GROUP));
	}

	/**
	 * @param search - term to search
	 * @return - number of matching user
	 */
	public Long selectMaxFromUsersWithSearch(String search) {
		try {
			// get all users
			List<Long> ll = em.createNamedQuery("selectMaxFromUsersWithSearch", Long.class)
					.setParameter("search", StringUtils.lowerCase(search, Locale.ROOT))
					.getResultList();
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
		String hash = only(em.createNamedQuery("getPassword", String.class)
				.setParameter(PARAM_USER_ID, userId).getResultList());
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
				.setParameter(PARAM_EMAIL, email).setParameter("type", User.Type.CONTACT).setParameter("ownerId", owner.getId())
				.getResultList();
		if (list.isEmpty()) {
			User to = new User();
			to.setType(Type.CONTACT);
			String login = owner.getId() + "_" + email; //UserId prefix is used to ensure unique login
			to.setLogin(login.length() < getMinLoginLength() ? randomUUID().toString() : login);
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
		return single(fillLazy(em
				, oem -> oem.createQuery("SELECT u FROM User as u WHERE u.activatehash = :activatehash AND u.deleted = false", User.class)
				.setParameter("activatehash", hash)
				, FETCH_GROUP_GROUP));
	}

	public User getExternalUser(String extId, String extType) {
		return single(fillLazy(em
				, oem -> oem.createNamedQuery("getExternalUser", User.class)
					.setParameter("externalId", extId)
					.setParameter("externalType", extType)
					.setParameter("type", Type.EXTERNAL)
				, FETCH_GROUP_GROUP));
	}

	@Override
	public List<User> get(String search, long start, long count, SortParam<String> sort) {
		return get(search, start, count, sort, false, Long.valueOf(-1));
	}

	public Set<Right> getRights(Long id) {
		Set<Right> rights = new HashSet<>();

		if (id == null) {
			return rights;
		}
		// For direct access of linked users
		if (id.longValue() < 0) {
			rights.add(Right.ROOM);
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
	 * @param inUserOrEmail - login or email of the user being tested
	 * @param userpass - password of the user being tested
	 * @return User object in case of successful login
	 * @throws OmException in case of any issue
	 */
	public User login(String inUserOrEmail, String userpass) throws OmException {
		String userOrEmail = inUserOrEmail == null ? null : inUserOrEmail.trim().toLowerCase(Locale.ROOT);
		List<User> users = em.createNamedQuery("getUserByLoginOrEmail", User.class)
				.setParameter("userOrEmail", userOrEmail)
				.setParameter("type", Type.USER)
				.getResultList();

		log.debug("login:: {} users were found", users.size());

		if (users.isEmpty()) {
			log.debug("No users were found: {}", userOrEmail == null ? null : userOrEmail.replaceAll("\\p{C}", "?"));
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
		log.debug("login user groups {}", u.getGroupUsers());
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
