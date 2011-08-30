package org.openmeetings.app.data.user.dao;

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
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UsersDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(UsersDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ManageCryptStyle manageCryptStyle;

	/**
	 * 
	 * @param user_id
	 * @return
	 */
	public Users getUser(Long user_id) {
		if (user_id != null && user_id > 0) {
			try {
				Query query = em.createQuery("select c from Users as c where c.user_id = :user_id");
				query.setParameter("user_id", user_id);

				Users users = null;
				try {
					users = (Users) query.getSingleResult();
				} catch (NoResultException ex) {
				}

				// Somehow the Organizations are missing here o

				return users;
				// TODO: Add Usergroups to user
				// users.setUsergroups(ResHandler.getGroupmanagement().getUserGroups(user_id));
			} catch (Exception ex2) {
				log.error("getUser", ex2);
			}
		} else {
			log.info("[getUser] " + "Info: No USER_ID given");
		}
		return null;
	}

	public void updateUser(Users user) {
		if (user.getUser_id() > 0) {
			try {
				if (user.getUser_id() == null) {
					em.persist(user);
				} else {
					if (!em.contains(user)) {
						em.merge(user);
					}
				}
			} catch (Exception ex2) {
				log.error("[updateUser] ", ex2);
			}
		} else {
			log.info("[updateUser] " + "Error: No USER_ID given");
		}
	}

	public Long deleteUserID(long USER_ID) {
		try {
			if (USER_ID != 0) {
				Users us = getUser(USER_ID);
				us.setDeleted("true");
				us.setUpdatetime(new Date());
				// result +=
				// Groupmanagement.getInstance().deleteUserFromAllGroups(new
				// Long(USER_ID));

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

	/**
	 * returns the maximum
	 * 
	 * @return
	 */
	public Long selectMaxFromUsers() {
		try {
			// get all users
			Query query = em.createQuery("select count(c.user_id) from Users c where c.deleted = 'false'");
			List<?> ll = query.getResultList();
			log.info("selectMaxFromUsers" + ll.get(0));
			return (Long) ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] ", ex2);
		}
		return null;
	}

	public List<Users> getAllUsers() {
		try {

			// get all users
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Users> cq = cb.createQuery(Users.class);
			Root<Users> c = cq.from(Users.class);
			Predicate condition = cb.equal(c.get("deleted"), "false");
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

			// get all users
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Users> cq = cb.createQuery(Users.class);
			Root<Users> c = cq.from(Users.class);
			Predicate condition = cb.equal(c.get("deleted"), "false");
			cq.where(condition);
			TypedQuery<Users> q = em.createQuery(cq);
			List<Users> ll = q.getResultList();

			return ll;

		} catch (Exception ex2) {
			log.error("[getAllUsers] ", ex2);
		}
		return null;
	}

	public Long getAllUserMax(String search) {
		try {

			String[] searchItems = search.split(" ");

			log.debug("getUserContactsBySearch: " + search);
			// log.debug("getUserContactsBySearch: "+ userId);

			String hql = "select count(u.user_id) from  Users u "
					+ "WHERE u.deleted = 'false' ";

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

			Query query = em.createQuery(hql);

			// log.debug("id: "+folderId);
			List<?> ll = query.getResultList();

			// log.error((Long)ll.get(0));
			Long i = (Long) ll.get(0);

			return new Long(i);
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
			Query query = em.createQuery("select c from Users as c where c.login = :DataValue AND c.deleted <> :deleted");
			query.setParameter("DataValue", DataValue);
			query.setParameter("deleted", "true");
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
			Query query = em.createQuery(hql);
			query.setParameter("login", login);
			query.setParameter("deleted", "true");
			Users us = null;
			try {
				us = (Users) query.getSingleResult();
			} catch (NoResultException ex) {
			}
			return us;
		} catch (Exception e) {
			log.error("[getUserByAdressesId]", e);
		}
		return null;
	}

	public Users getUserByAdressesId(Long adresses_id) {
		try {
			String hql = "SELECT u FROM Users as u "
					+ " where u.adresses.adresses_id = :adresses_id"
					+ " AND u.deleted <> :deleted";
			Query query = em.createQuery(hql);
			query.setParameter("adresses_id", adresses_id);
			query.setParameter("deleted", "true");
			Users us = null;
			try {
				us = (Users) query.getSingleResult();
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
			Query query = em.createQuery(hql);
			query.setParameter("resethash", hash);
			query.setParameter("deleted", "true");
			Users us = null;
			try {
				us = (Users) query.getSingleResult();
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
					+ "where c.deleted = 'false' " + "AND ("
					+ "lower(c.login) LIKE :search "
					+ "OR lower(c.firstname) LIKE :search "
					+ "OR lower(c.lastname) LIKE :search " + ")";

			// get all users
			Query query = em.createQuery(hql);
			query.setParameter("search", StringUtils.lowerCase(search));
			List<?> ll = query.getResultList();
			log.info("selectMaxFromUsers" + ll.get(0));
			return (Long) ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] ", ex2);
		}
		return null;
	}

}
