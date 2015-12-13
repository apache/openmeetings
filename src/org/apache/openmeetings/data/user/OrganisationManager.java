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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.dao.OrganisationDao;
import org.apache.openmeetings.data.user.dao.OrganisationUserDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.user.User;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author swagner
 * 
 */
@Transactional
public class OrganisationManager {

	private static Logger log = Red5LoggerFactory
			.getLogger(OrganisationManager.class,
					OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private OrganisationDao orgDao;
	@Autowired
	private OrganisationUserDao orgUserDao;
	@Autowired
	private AuthLevelUtil authLevelUtil;

	/**
	 * adds a new organisation if userlevel is admin
	 * 
	 * @param user_level
	 * @param orgname
	 * @param user_id
	 * @return
	 */
	public Long addOrganisation(Long user_level, String orgname, long user_id) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				Long orgId = this.addOrganisation(orgname, user_id);
				return orgId;
			}
		} catch (Exception err) {
			log.error("addOrganisation", err);
		}
		return null;
	}

	/**
	 * adds a new organisation to the table organisation
	 * 
	 * @param titelname
	 * @param user_id
	 */
	public Long addOrganisation(String orgname, long user_id) {
		try {
			Organisation org = new Organisation();
			org.setName(orgname);
			org.setInsertedby(new Long(user_id));
			org.setDeleted(false);
			org.setStarttime(new Date());
			org = em.merge(org);

			long id = org.getOrganisation_id();
			return id;
		} catch (Exception ex2) {
			log.error("[addOrganisation]", ex2);
		}
		return null;
	}

	public Long addOrganisationObj(Organisation org) {
		try {
			org.setStarttime(new Date());
			org = em.merge(org);
			long id = org.getOrganisation_id();
			return id;
		} catch (Exception ex2) {
			log.error("[addOrganisationObj]", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @return
	 */
	public SearchResult<Organisation> getOrganisations(long user_level, int start, int max,
			String orderby, boolean asc) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				SearchResult<Organisation> sresult = new SearchResult<Organisation>();
				sresult.setObjectName(Organisation.class.getName());
				sresult.setRecords(orgDao.count());
				sresult.setResult(this.getOrganisations(start, max, orderby,
						asc));
				return sresult;
			} else {
				log.error("[getOrganisations] noPremission");
			}
		} catch (Exception ex2) {
			log.error("[getOrganisations]", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param user_level
	 * @return
	 */
	public List<Organisation> getOrganisations(int start, int max,
			String orderby, boolean asc) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Organisation> cq = cb.createQuery(Organisation.class);
			Root<Organisation> c = cq.from(Organisation.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			cq.where(condition);
			cq.distinct(asc);
			if (asc) {
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<Organisation> q = em.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<Organisation> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getOrganisations]", ex2);
		}
		return null;
	}

	public List<Organisation> getOrganisations(Long user_level) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Organisation> cq = cb
						.createQuery(Organisation.class);
				Root<Organisation> c = cq.from(Organisation.class);
				Predicate condition = cb.equal(c.get("deleted"), false);
				cq.where(condition);
				TypedQuery<Organisation> q = em.createQuery(cq);
				List<Organisation> ll = q.getResultList();
				return ll;
			} else {
				log.error("[getOrganisations] noPremission");
			}
		} catch (Exception ex2) {
			log.error("[getOrganisations]", ex2);
		}
		return null;
	}

	/**
	 * updates an organisation if user_level is admin
	 * 
	 * @param user_level
	 * @param organisation_id
	 * @param orgname
	 * @param users_id
	 * @return
	 */
	public Long updateOrganisation(Long user_level, long organisation_id,
			String orgname, long users_id) {
		try {

			Organisation org = orgDao.get(organisation_id);
			org.setName(orgname);
			org.setUpdatedby(users_id);
			org.setUpdatetime(new Date());

			em.merge(org);

			return org.getOrganisation_id();
		} catch (Exception err) {
			log.error("updateOrganisation", err);
		}
		return null;
	}

	/**
	 * checks if a user is already stored
	 * 
	 * @param userIdToAdd
	 * @param usersStored
	 * @return
	 * @throws Exception
	 */
	private boolean checkUserAlreadyStored(Long userIdToAdd, List<?> usersStored)
			throws Exception {
		for (Iterator<?> it2 = usersStored.iterator(); it2.hasNext();) {
			User us = (User) it2.next();
			if (us.getUser_id().equals(userIdToAdd)) {
				log.debug("userIdToAdd found: " + userIdToAdd);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param user_id
	 * @param usersToStore
	 * @return
	 * @throws Exception
	 */
	private boolean checkUserShouldBeStored(Long user_id,
			@SuppressWarnings("rawtypes") LinkedHashMap usersToStore)
			throws Exception {
		for (Iterator<?> it2 = usersToStore.keySet().iterator(); it2.hasNext();) {
			Integer key = (Integer) it2.next();
			Long usersIdToCheck = Long
					.valueOf(usersToStore.get(key).toString()).longValue();
			log.debug("usersIdToCheck: " + usersIdToCheck);
			if (user_id.equals(usersIdToCheck)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO
	 * 
	 * @param org
	 * @param users
	 * @return
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private Long updateOrganisationUsersByHashMap(Organisation org,
			LinkedHashMap users, long insertedby) {
		try {
			LinkedList<Long> usersToAdd = new LinkedList<Long>();
			LinkedList<Long> usersToDel = new LinkedList<Long>();

			List usersStored = this.getUsersByOrganisationId(org
					.getOrganisation_id());

			for (Iterator it = users.keySet().iterator(); it.hasNext();) {
				Integer key = (Integer) it.next();
				Long userIdToAdd = Long.valueOf(users.get(key).toString())
						.longValue();
				log.debug("userIdToAdd: " + userIdToAdd);
				if (!this.checkUserAlreadyStored(userIdToAdd, usersStored))
					usersToAdd.add(userIdToAdd);
			}

			for (Iterator it = usersStored.iterator(); it.hasNext();) {
				User us = (User) it.next();
				Long userIdStored = us.getUser_id();
				log.debug("userIdStored: " + userIdStored);
				if (!this.checkUserShouldBeStored(userIdStored, users))
					usersToDel.add(userIdStored);
			}

			log.debug("usersToAdd.size " + usersToAdd.size());
			log.debug("usersToDel.size " + usersToDel.size());

			for (Iterator<Long> it = usersToAdd.iterator(); it.hasNext();) {
				Long user_id = it.next();
				this.addUserToOrganisation(user_id, org.getOrganisation_id(),
						insertedby);
			}

			for (Iterator<Long> it = usersToDel.iterator(); it.hasNext();) {
				Long user_id = it.next();
				this.deleteUserFromOrganisation(new Long(3), user_id,
						org.getOrganisation_id());
			}

		} catch (Exception err) {
			log.error("updateOrganisationUsersByHashMap", err);
		}
		return null;
	}

	/**
	 * get an organisation by id and only as admin
	 * 
	 * @param user_level
	 * @param organisation_id
	 * @return
	 */
	public Organisation getOrganisationById(long user_level,
			long organisation_id) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				return orgDao.get(organisation_id);
			} else {
				log.error("[getOrganisationById] authorization required");
			}
		} catch (Exception ex2) {
			log.error("[getOrganisationById]", ex2);
		}
		return null;
	}

	/**
	 * Gets an organisation by its id
	 * 
	 * @param organisation_id
	 * @return
	 */
	public Organisation getOrganisationById(long organisation_id) {
		return orgDao.get(organisation_id);
	}

	public Organisation getOrganisationByIdBackup(long organisation_id) {
		try {
			TypedQuery<Organisation> query = em.createNamedQuery("getAnyOrganisationById", Organisation.class);
			query.setParameter("organisation_id", organisation_id);
			Organisation o = null;
			try {
				o = query.getSingleResult();
			} catch (NoResultException e) {
				// o = null;
			}
			return o;
		} catch (Exception ex2) {
			log.error("[getOrganisationById]", ex2);
		}
		return null;
	}

	public Organisation getOrganisationByIdAndDeleted(long organisation_id) {
		return getOrganisationByIdBackup(organisation_id);
	}

	public Long deleteOrganisation(long user_level, long organisation_id,
			long updatedby) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				return this.deleteOrganisation(organisation_id, updatedby);
			}
		} catch (Exception ex2) {
			log.error("[deleteOrganisation]", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param organisation_id
	 * @param updatedby
	 * @return
	 */
	public Long deleteOrganisation(long organisation_id, long updatedby) {
		try {
			orgDao.delete(orgDao.get(organisation_id), updatedby);
		} catch (Exception ex2) {
			log.error("[deleteOrganisation]", ex2);
		}
		return organisation_id;
	}

	/**
	 * Adds a user to a given organisation-unit
	 * 
	 * @param user_id
	 * @param organisation_id
	 * @param insertedby
	 * @return
	 */
	public Long addUserToOrganisation(Long user_id, Long organisation_id,
			Long insertedby) {
		try {
			if (this.getOrganisation_UserByUserAndOrganisation(user_id, organisation_id) == null) {
				return addOrganisationUserObj(user_id,
						getOrgUser(organisation_id, insertedby));
			} else {
				return -35L;
			}
		} catch (Exception ex2) {
			log.error("[addUserToOrganisation]", ex2);
		}
		return null;
	}
	
	public Organisation_Users getOrgUser(Long organisation_id, Long insertedby) {
		
		Organisation_Users orgUser = new Organisation_Users(
				orgDao.get(organisation_id));
		orgUser.setDeleted(false);
		
		return orgUser;
	}
	
	public Long addOrganisationUserObj(Long user_id, Organisation_Users orgUser) {
		try {
			User u = usersDao.get(user_id);
			
			orgUser.setStarttime(new Date());
			orgUser = em.merge(orgUser);

			//user should be updated to have recent organisation_users list
			List<Organisation_Users> l = u.getOrganisation_users();
			l.add(orgUser);
			u.setOrganisation_users(l);
			usersDao.update(u, -1L);
			
			return orgUser.getOrganisation_users_id();
		} catch (Exception ex2) {
			log.error("[addUserToOrganisation]", ex2);
		}
		return null;
	}

	public Organisation_Users getOrganisation_UserByUserAndOrganisation(
			long user_id, long organisation_id) {
		try {
			log.debug("getOrganisation_UserByUserAndOrganisation " + user_id
					+ "  " + organisation_id);

			TypedQuery<Organisation_Users> q = em.createNamedQuery("getOrganisation_UserByUserAndOrganisation", Organisation_Users.class);
			q.setParameter("organisation_id", organisation_id);
			q.setParameter("user_id", user_id);
			List<Organisation_Users> oul = q.getResultList();
			return oul.size() > 0 ? oul.get(0) : null;
		} catch (Exception ex2) {
			log.error("[getOrganisation_UserByUserAndOrganisation]", ex2);
		}
		return null;
	}

	public Long deleteUserFromOrganisation(Long user_level, long user_id,
			long organisation_id) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {

				log.error("deleteUserFromOrganisation " + user_id + "  "
						+ organisation_id);
				//user should be updated to have recent organisation_users list
				Long id = null;
				User u = usersDao.get(user_id);
				List<Organisation_Users> l = u.getOrganisation_users();
				for (Organisation_Users ou : l) {
					if (ou.getOrganisation().getOrganisation_id().equals(organisation_id)) {
						l.remove(ou);
						id = ou.getOrganisation_users_id();
						em.remove(ou);
						break;
					}
				}
				u.setOrganisation_users(l);
				usersDao.update(u, -1L);

				return id;
			} else {
				log.error("[deleteUserFromOrganisation] authorization required");
			}
		} catch (Exception ex2) {
			log.error("[deleteuserFromOrganisation]", ex2);
		}
		return null;
	}

	public SearchResult<User> getUsersSearchResultByOrganisationId(
			long organisation_id, int start, int max, String orderby,
			boolean asc) {
		try {

			SearchResult<User> sResult = new SearchResult<User>();
			sResult.setObjectName(User.class.getName());
			sResult.setRecords(orgUserDao.count(organisation_id));
			sResult.setResult(getUsersByOrganisationId(organisation_id,
					start, max, orderby, asc));
			return sResult;

		} catch (Exception ex2) {
			log.error("[getUsersSearchResultByOrganisationId]", ex2);
		}
		return null;
	}

	/**
	 * get a list of all users of an organisation
	 * 
	 * @param user_level
	 * @param organisation_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<User> getUsersByOrganisationId(long organisation_id, int start,
			int max, String orderby, boolean asc) {
		try {
			String hql =
				"SELECT c FROM "
				+ "User c "
				+ ", IN(c.organisation_users) ou "
				+ "WHERE c.deleted = false AND ou.organisation.organisation_id = :organisation_id ";
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

			TypedQuery<User> q = em.createQuery(hql, User.class);
			q.setParameter("organisation_id", organisation_id);
			q.setFirstResult(start);
			q.setMaxResults(max);

			List<User> userL = q.getResultList();
			return userL;
		} catch (Exception ex2) {
			log.error("[getUsersByOrganisationId]", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param organisation_id
	 * @return
	 */
	public List<User> getUsersByOrganisationId(long organisation_id) {
		try {

			// get all users
			TypedQuery<User> q = em.createNamedQuery("getUsersByOrganisationId", User.class);
			q.setParameter("organisation_id", organisation_id);
			List<User> userL = q.getResultList();
			Collections.sort(userL, new UsersLoginComparator());
			return userL;

		} catch (Exception ex2) {
			log.error("[getUsersByOrganisationId]", ex2);
		}
		return null;
	}

	class UsersLoginComparator implements Comparator<User> {
		public int compare(User o1, User o2) {
			if (o1 == null || o2 == null)
				return 0;

			return o1.getLogin().compareTo(o2.getLogin());
		}
	}

	/**
	 * Filter all Organisations by user TODO: Add sorting
	 * 
	 * @param user_level
	 * @param users_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @return
	 */
	public List<Organisation> getOrganisationsByUserId(long user_level,
			long user_id, int start, int max, String orderby, boolean asc) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				TypedQuery<Organisation> q = em.createNamedQuery("getOrganisationsByUserId", Organisation.class);
				q.setParameter("user_id", user_id);
				q.setFirstResult(start);
				q.setMaxResults(max);

				List<Organisation> userOrg = q.getResultList();

				return userOrg;
			}
		} catch (Exception ex2) {
			log.error("[getOrganisationsByUserId]", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param user_level
	 * @param user_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<Organisation> getRestOrganisationsByUserId(long user_level,
			long user_id, int start, int max, String orderby, boolean asc) {
		try {
			if (authLevelUtil.checkAdminLevel(user_level)) {
				String qSQL =
					"SELECT o FROM Organisation AS o "
					+ "WHERE o.organisation_id NOT IN ("
					+ "	SELECT ou.organisation.organisation_id "
					+ "	FROM User u, IN(u.organisation_users) ou WHERE u.deleted = false AND u.user_id = :user_id)";
				TypedQuery<Organisation> q = em.createQuery(qSQL, Organisation.class);
				q.setParameter("user_id", user_id);
				q.setFirstResult(start);
				q.setMaxResults(max);
				return q.getResultList();
			}
		} catch (Exception ex2) {
			log.error("[getRestOrganisationsByUserId]", ex2);
		}
		return null;
	}

	/**
	 * checks if the orgId is in the list of orgIds which have been send as
	 * organisations
	 * 
	 * @param orgId
	 * @param org
	 * @return
	 * @throws Exception
	 */
	private boolean checkOrgInList(Long orgId, List<Long> org) throws Exception {
		return org != null && org.contains(orgId);
	}

	/**
	 * checks if an orgId is already stored in the Users Organisations Object
	 * 
	 * @param orgId
	 * @param org
	 * @return
	 * @throws Exception
	 */
	private boolean checkOrgInStoredList(long orgId, List<Organisation_Users> org) throws Exception {
		// log.debug("checkOrgInStoredList "+orgId);
		for (Organisation_Users orgUsers : org) {
			// log.debug("checkOrgInStoredList 2 "+orgUsers.getOrganisation().getOrganisation_id());
			if (orgUsers.getOrganisation().getOrganisation_id().equals(orgId)) {
				// log.debug("checkOrgInStoredList 3 found");
				return true;
			}
		}
		return false;
	}

	/**
	 * updates users-organisations by a given params
	 * 
	 * @param us
	 * @param organisations
	 * @return
	 */
	//FIXME need to refactor
	public Long updateUserOrganisationsByUser(User us, List<Long> organisations) {
		try {
			LinkedList<Long> orgIdsToAdd = new LinkedList<Long>();
			LinkedList<Long> orgIdsToDelete = new LinkedList<Long>();

			if (us.getOrganisation_users() != null) {
				for (Long orgIdToAdd : organisations) {
					boolean isAlreadyStored = this.checkOrgInStoredList(
							orgIdToAdd, us.getOrganisation_users());
					if (!isAlreadyStored)
						orgIdsToAdd.add(orgIdToAdd);
				}

				for (Organisation_Users orgUsers : us.getOrganisation_users()) {
					Long orgIdStored = orgUsers.getOrganisation()
							.getOrganisation_id();
					// log.error("updateUserOrganisationsByUser check1 : "+orgIdStored);
					boolean shouldBeStored = this.checkOrgInList(orgIdStored,
							organisations);
					if (!shouldBeStored)
						orgIdsToDelete.add(orgIdStored);
				}

				// log.error("updateUserOrganisationsByUser size ADD: "+orgIdsToAdd.size());
				for (Long orgToAdd : orgIdsToAdd) {
					this.addUserToOrganisation(us.getUser_id(), orgToAdd,
							us.getUser_id());
				}

				// log.error("updateUserOrganisationsByUser size DELETE: "+orgIdsToDelete.size());
				for (Long orgToDel : orgIdsToDelete) {
					this.deleteUserFromOrganisation(new Long(3),
							us.getUser_id(), orgToDel);
				}
			}
		} catch (Exception err) {
			log.error("[updateUserOrganisationsByUser] ", err);
		}
		return null;
	}

	/**
	 * adds all send organisations to this User-Object (for newly
	 * registered/created Users)
	 * 
	 * @param us
	 * @param org
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Long addUserOrganisationsByHashMap(long us, List org) {
		try {
			if (org != null) {
				for (Iterator it = org.iterator(); it.hasNext();) {
					Integer key = (Integer) it.next();
					Long newOrgId = key.longValue();
					this.addUserToOrganisation(us, newOrgId, new Long(1));
				}
			}
		} catch (Exception ex) {
			log.error("addUserOrganisationsByHashMap", ex);
		}
		return null;
	}
}
