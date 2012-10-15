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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.OmDAO;
import org.apache.openmeetings.data.basic.dao.ConfigurationDaoImpl;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDaoImpl;
import org.apache.openmeetings.persistence.beans.adresses.Adresses;
import org.apache.openmeetings.persistence.beans.user.Users;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD operations for {@link Users}
 * 
 * @author swagner, solomax
 * 
 */
@Transactional
public class UsersDaoImpl implements OmDAO<Users> {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UsersDaoImpl.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ManageCryptStyle manageCryptStyle;
	@Autowired
	private SalutationDaoImpl salutationDaoImpl;
	@Autowired
	private ConfigurationDaoImpl configurationDaoImpl;
	@Autowired
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;
	@Autowired
	private StateDaoImpl stateDaoImpl;

	/**
	 * Get a new instance of the {@link Users} entity, with all default values
	 * set
	 * 
	 * @param currentUser
	 *            the timezone of the current user is copied to the new default
	 *            one (if the current user has one)
	 * @return
	 */
	public Users getNewUserInstance(Users currentUser) {
		Users user = new Users();
		user.setSalutations_id(1L); // TODO: Fix default selection to be
									// configurable
		user.setLevel_id(1L);
		user.setLanguage_id(configurationDaoImpl.getConfValue(
				"default_lang_id", Long.class, "1"));
		user.setOmTimeZone(omTimeZoneDaoImpl.getDefaultOmTimeZone(currentUser));
		user.setForceTimeZoneCheck(false);
		user.setSendSMS(false);
		user.setAge(new Date());
		Adresses adresses = new Adresses();
		adresses.setStates(stateDaoImpl.getStateById(1L));
		user.setAdresses(adresses);
		user.setStatus(1);
		user.setShowContactData(false);
		user.setShowContactDataToContacts(false);

		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#get(int, int)
	 */
	public List<Users> get(int first, int count) {
		return getNondeletedUsers(first, count);
	}

	// FIXME need to be generalized with other copy/pasted methods
	public List<Users> get(String search) {
		String[] searchItems = search.split(" ");
		StringBuilder hql = new StringBuilder(
				"SELECT u FROM Users u WHERE u.deleted = false ");

		hql.append("AND ( ");
		for (int i = 0; i < searchItems.length; ++i) {
			if (searchItems[i].isEmpty()) {
				continue;
			}
			if (i != 0) {
				hql.append(" OR ");
			}
			StringBuilder placeholder = new StringBuilder();
			placeholder.append("%")
					.append(StringUtils.lowerCase(searchItems[i])).append("%");

			hql.append("(lower(u.lastname) LIKE '").append(placeholder)
					.append("' OR lower(u.firstname) LIKE '")
					.append(placeholder).append("' OR lower(u.login) LIKE '")
					.append(placeholder)
					.append("' OR lower(u.adresses.email) LIKE '")
					.append(placeholder).append("' ) ");
		}

		hql.append(" ) ");
		TypedQuery<Users> q = em.createQuery(hql.toString(), Users.class);
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.data.OmDAO#update(org.apache.openmeetings.persistence
	 * .beans.OmEntity, long)
	 */
	public void update(Users u, long userId) {
		updateUser(u);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.openmeetings.data.OmDAO#delete(org.apache.openmeetings.persistence
	 * .beans.OmEntity, long)
	 */
	public void delete(Users u, long userId) {
		deleteUserID(u.getUser_id());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#get(long)
	 */
	public Users get(long user_id) {
		if (user_id > 0) {
			try {
				TypedQuery<Users> query = em.createQuery(
						"select c from Users as c where c.user_id = :user_id",
						Users.class);
				query.setParameter("user_id", user_id);

				Users users = null;
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

	public void updateUser(Users user) {
		try {
			if (user.getUser_id() == null) {
				user.setStarttime(new Date());
				em.persist(user);
			} else {
				user.setUpdatetime(new Date());
				if (!em.contains(user)) {
					em.merge(user);
				}
			}
		} catch (Exception ex2) {
			log.error("[updateUser] ", ex2);
		}
	}

	public Long deleteUserID(long userId) {
		try {
			if (userId != 0) {
				Users us = get(userId);
				us.setDeleted(true);
				us.setUpdatetime(new Date());
				Adresses adr = us.getAdresses();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.openmeetings.data.OmDAO#count()
	 */
	public long count() {
		// get all users
		TypedQuery<Long> query = em.createQuery(
				"select count(c.user_id) from Users c where c.deleted = false",
				Long.class);
		List<Long> ll = query.getResultList();
		return ll.get(0);
	}

	public List<Users> getNondeletedUsers(int first, int count) {
		TypedQuery<Users> q = em.createNamedQuery("getNondeletedUsers",
				Users.class);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<Users> getAllUsers() {
		try {
			// get all non-deleted users
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Users> cq = cb.createQuery(Users.class);
			Root<Users> c = cq.from(Users.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			cq.where(condition);
			TypedQuery<Users> q = em.createQuery(cq);
			List<Users> ll = q.getResultList();

			return ll;
		} catch (Exception ex2) {
			log.error("[getAllUsers] ", ex2);
		}
		return null;
	}

	public List<Users> getAllUsersDeleted() {
		try {
			TypedQuery<Users> q = em.createNamedQuery("getAllUsers",
					Users.class);
			@SuppressWarnings("unchecked")
			OpenJPAQuery<Users> kq = OpenJPAPersistence.cast(q);
			kq.getFetchPlan().addFetchGroup("passwordexport");
			return kq.getResultList();
		} catch (Exception ex2) {
			log.error("[getAllUsersDeleted] ", ex2);
		}
		return null;
	}

	public Long getAllUserMax(String search) {
		try {

			String[] searchItems = search.split(" ");

			log.debug("getUserContactsBySearch: " + search);
			// log.debug("getUserContactsBySearch: "+ userId);

			String hql = "select count(u.user_id) from  Users u "
					+ "WHERE u.deleted = false ";

			hql += "AND ( ";
			for (int i = 0; i < searchItems.length; i++) {
				if (i != 0) {
					hql += " OR ";
				}
				hql += "( " + "lower(u.lastname) LIKE '"
						+ StringUtils.lowerCase("%" + searchItems[i] + "%")
						+ "' " + "OR lower(u.firstname) LIKE '"
						+ StringUtils.lowerCase("%" + searchItems[i] + "%")
						+ "' " + "OR lower(u.login) LIKE '"
						+ StringUtils.lowerCase("%" + searchItems[i] + "%")
						+ "' " + "OR lower(u.adresses.email) LIKE '"
						+ StringUtils.lowerCase("%" + searchItems[i] + "%")
						+ "' " + ") ";

			}
			hql += " )";

			log.debug("Show HQL: " + hql);

			TypedQuery<Long> query = em.createQuery(hql, Long.class);

			// log.debug("id: "+folderId);
			List<Long> ll = query.getResultList();

			// log.error((Long)ll.get(0));
			Long i = ll.get(0);

			return i;
		} catch (Exception ex2) {
			log.error("[getAllUserMax]: ", ex2);
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
			TypedQuery<Users> query = em
					.createQuery(
							"select c from Users as c where c.login = :DataValue AND c.deleted <> :deleted",
							Users.class);
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

	public Users getUserByName(String login) {
		try {
			String hql = "SELECT u FROM Users as u "
					+ " where u.login = :login" + " AND u.deleted <> :deleted";
			TypedQuery<Users> query = em.createQuery(hql, Users.class);
			query.setParameter("login", login);
			query.setParameter("deleted", true);
			Users us = null;
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

	public Users getUserByEmail(String email) {
		try {
			String hql = "SELECT u FROM Users as u "
					+ " where u.adresses.email = :email"
					+ " AND u.deleted <> :deleted";
			TypedQuery<Users> query = em.createQuery(hql, Users.class);
			query.setParameter("email", email);
			query.setParameter("deleted", true);
			Users us = null;
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
			String hql = "SELECT u FROM Users as u "
					+ " where u.resethash = :resethash"
					+ " AND u.deleted <> :deleted";
			TypedQuery<Users> query = em.createQuery(hql, Users.class);
			query.setParameter("resethash", hash);
			query.setParameter("deleted", true);
			Users us = null;
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
			if (u instanceof Users) {
				Users us = (Users) u;
				us.setPassword(manageCryptStyle.getInstanceOfCrypt()
						.createPassPhrase(pass));
				us.setResethash("");
				updateUser(us);
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

			String hql = "select count(c.user_id) from Users c "
					+ "where c.deleted = false " + "AND ("
					+ "lower(c.login) LIKE :search "
					+ "OR lower(c.firstname) LIKE :search "
					+ "OR lower(c.lastname) LIKE :search " + ")";

			// get all users
			TypedQuery<Long> query = em.createQuery(hql, Long.class);
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
		query.setParameter("password", manageCryptStyle.getInstanceOfCrypt()
				.createPassPhrase(password));
		return query.getResultList().get(0) == 1;

	}

	/**
	 * Password needs extra hook because being FetchType.Lazy
	 * 
	 * @param u
	 * @param newPassword
	 * @param userId
	 * @return
	 */
	public int updatePassword(Users u, String newPassword) {
		Query query = em.createNamedQuery("updatePassword");
		query.setParameter("password", manageCryptStyle.getInstanceOfCrypt()
				.createPassPhrase(newPassword));
		query.setParameter("userId", u.getUser_id());
		return query.executeUpdate();

	}

}
