package org.openmeetings.app.data.user;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.domain.Organisation;
import org.openmeetings.app.hibernate.beans.domain.Organisation_Users;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

/**
 * 
 * @author swagner
 *
 */
public class Organisationmanagement {
	
	private static Logger log = Red5LoggerFactory.getLogger(Organisationmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	private static Organisationmanagement instance = null;

	public static synchronized Organisationmanagement getInstance() {
		if (instance == null) {
			instance = new Organisationmanagement();
		}
		return instance;
	}
	
	/**
	 * adds a new organisation if userlevel is admin
	 * @param user_level
	 * @param orgname
	 * @param user_id
	 * @return
	 */
	public Long addOrganisation(Long user_level, String orgname, long user_id){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				Long orgId = this.addOrganisation(orgname, user_id);		
				return orgId;
			}
		} catch (Exception err){
			log.error("addOrganisation",err);
		}
		return null;
	}

	/**
	 * adds a new organisation to the table organisation
	 * @param titelname
	 * @param user_id
	 */
	public Long addOrganisation(String orgname, long user_id) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Organisation org = new Organisation();
			org.setName(orgname);
			org.setInsertedby(new Long(user_id));
			org.setDeleted("false");
			org.setStarttime(new Date());
			org = session.merge(org);
			session.flush();
			long id = org.getOrganisation_id();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return id;
		} catch (Exception ex2) {
			log.error("[addOrganisation]" ,ex2);
		}
		return null;
	}
	
	public Long addOrganisationObj(Organisation org) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			org.setStarttime(new Date());
			org = session.merge(org);
			session.flush();
			long id = org.getOrganisation_id();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return id;
		} catch (Exception ex2) {
			log.error("[addOrganisationObj]" ,ex2);
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
	public SearchResult getOrganisations(long user_level, int start ,int max, String orderby, boolean asc) {
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				SearchResult sresult = new SearchResult();
				sresult.setObjectName(Organisation.class.getName());
				sresult.setRecords(this.selectMaxFromOrganisations());
				sresult.setResult(this.getOrganisations(start, max, orderby,asc));
				return sresult;
			} else {
				log.error("[getOrganisations] noPremission");
			}
		} catch (Exception ex2) {
			log.error("[getOrganisations]" ,ex2);
		}
		return null;
	}
	
	/**
	 * 
	 * @param user_level
	 * @return
	 */
	public List<Organisation> getOrganisations(int start ,int max, String orderby, boolean asc) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Organisation> cq = cb.createQuery(Organisation.class);
			Root<Organisation> c = cq.from(Organisation.class);
			Predicate condition = cb.equal(c.get("deleted"), "false");
			cq.where(condition);
			cq.distinct(asc);
			if (asc){
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<Organisation> q = session.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<Organisation> ll = q.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return ll;
		} catch (Exception ex2) {
			log.error("[getOrganisations]" ,ex2);
		}
		return null;
	}	
	
	public List<Organisation> getOrganisations(Long user_level) {
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				CriteriaBuilder cb = session.getCriteriaBuilder();
				CriteriaQuery<Organisation> cq = cb.createQuery(Organisation.class);
				Root<Organisation> c = cq.from(Organisation.class);
				Predicate condition = cb.equal(c.get("deleted"), "false");
				cq.where(condition);
				TypedQuery<Organisation> q = session.createQuery(cq);
				List<Organisation> ll = q.getResultList();
				tx.commit();
				HibernateUtil.closeSession(idf);
				return ll;
			} else {
				log.error("[getOrganisations] noPremission");
			}			
		} catch (Exception ex2) {
			log.error("[getOrganisations]" ,ex2);
		}
		return null;
	}	
	
	
	/**
	 * 
	 * @return
	 */
	private Long selectMaxFromOrganisations(){
		try {
			//get all users
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery("select max(c.organisation_id) from Organisation c where c.deleted = 'false'"); 
			List ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			log.debug("selectMaxFromOrganisations"+(Long)ll.get(0));
			return (Long)ll.get(0);				
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] ",ex2);
		}
		return null;
	}	
	
	/**
	 * updates an organisation if user_level is admin
	 * @param user_level
	 * @param organisation_id
	 * @param orgname
	 * @param users_id
	 * @param users
	 * @return
	 */
	public Long updateOrganisation(Long user_level, long organisation_id, String orgname, 
			long users_id){
		try {			
			Organisation org = this.getOrganisationById(organisation_id);			
			org.setName(orgname);
			org.setUpdatedby(users_id);
			org.setUpdatetime(new Date());
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (org.getOrganisation_id() == null) {
				session.persist(org);
			    } else {
			    	if (!session.contains(org)) {
			    		session.merge(org);
			    }
			}
			session.flush();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return org.getOrganisation_id();
		} catch (Exception err){
			log.error("updateOrganisation",err);
		}
		return null;
	}
	
	/**
	 * checks if a user is already stored
	 * @param userIdToAdd
	 * @param usersStored
	 * @return
	 * @throws Exception
	 */
	private boolean checkUserAlreadyStored(Long userIdToAdd, List usersStored) throws Exception{
		for (Iterator it2 = usersStored.iterator();it2.hasNext();){
			Users us = (Users) it2.next();
			if (us.getUser_id().equals(userIdToAdd)){
				log.error("userIdToAdd found: "+userIdToAdd);
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
	private boolean checkUserShouldBeStored(Long user_id, LinkedHashMap usersToStore) throws Exception {
		for (Iterator it2 = usersToStore.keySet().iterator();it2.hasNext();){
			Integer key = (Integer) it2.next();
			Long usersIdToCheck = Long.valueOf(usersToStore.get(key).toString()).longValue();
			log.error("usersIdToCheck: "+usersIdToCheck);
			if (user_id.equals(usersIdToCheck)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * TODO
	 * @param org
	 * @param users
	 * @return
	 */
	private Long updateOrganisationUsersByHashMap(Organisation org , LinkedHashMap users, long insertedby){
		try {
			LinkedList<Long> usersToAdd = new LinkedList<Long>();
			LinkedList<Long> usersToDel = new LinkedList<Long>();
			
			List usersStored = this.getUsersByOrganisationId(org.getOrganisation_id());
			
			for (Iterator it = users.keySet().iterator();it.hasNext();){
				Integer key = (Integer) it.next();
				Long userIdToAdd = Long.valueOf(users.get(key).toString()).longValue();
				log.error("userIdToAdd: "+userIdToAdd);
				if (!this.checkUserAlreadyStored(userIdToAdd, usersStored)) usersToAdd.add(userIdToAdd);
			}
			
			for (Iterator it = usersStored.iterator();it.hasNext();){
				Users us = (Users) it.next();
				Long userIdStored = us.getUser_id();
				log.error("userIdStored: "+userIdStored);
				if (!this.checkUserShouldBeStored(userIdStored, users)) usersToDel.add(userIdStored);
			}
			
			log.error("usersToAdd.size "+usersToAdd.size());
			log.error("usersToDel.size "+usersToDel.size());
			
			for (Iterator<Long> it = usersToAdd.iterator();it.hasNext();){
				Long user_id = it.next();
				this.addUserToOrganisation(user_id, org.getOrganisation_id(), insertedby, "");
			}
			
			for (Iterator<Long> it = usersToDel.iterator();it.hasNext();){
				Long user_id = it.next();
				this.deleteUserFromOrganisation(new Long(3), user_id, org.getOrganisation_id());
			}
			
			
		} catch (Exception err){
			log.error("updateOrganisationUsersByHashMap",err);
		}
		return null;
	}
	
	/**
	 * get an organisation by id and only as admin
	 * @param user_level
	 * @param organisation_id
	 * @return
	 */
	public Organisation getOrganisationById(long user_level, long organisation_id) {
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				return this.getOrganisationById(organisation_id);
			} else {
				log.error("[getOrganisationById] authorization required");
			}
		} catch (Exception ex2) {
			log.error("[getOrganisationById]",ex2);
		}
		return null;
	}

	/**
	 * Gets an organisation by its id
	 * @param organisation_id
	 * @return
	 */
	public Organisation getOrganisationById(long organisation_id) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery("select c from Organisation as c where c.organisation_id = :organisation_id AND c.deleted <> :deleted");
			query.setParameter("organisation_id", organisation_id);
			query.setParameter("deleted", "true");
            Organisation o = null;
            try {
                o = (Organisation) query.getSingleResult();
            } catch (NoResultException e) {
                //o = null;
            }
            tx.commit();
			HibernateUtil.closeSession(idf);
			return o;
		} catch (Exception ex2) {
			log.error("[getOrganisationById]",ex2);
		}
		return null;
	}
	
	public Organisation getOrganisationByIdBackup(long organisation_id) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery("select c from Organisation as c where c.organisation_id = :organisation_id");
			query.setParameter("organisation_id", organisation_id);
            Organisation o = null;
            try {
                o = (Organisation) query.getSingleResult();
            } catch (NoResultException e) {
                //o = null;
            }
			tx.commit();
			HibernateUtil.closeSession(idf);
			return o;
		} catch (Exception ex2) {
			log.error("[getOrganisationById]",ex2);
		}
		return null;
	}
	
	public Organisation getOrganisationByIdAndDeleted(long organisation_id) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery("select c from Organisation as c where c.organisation_id = :organisation_id");
			query.setParameter("organisation_id", organisation_id);
            Organisation o = null;
            try {
                o = (Organisation) query.getSingleResult();
            } catch (NoResultException e) {
                //o = null;
            }
			tx.commit();
			HibernateUtil.closeSession(idf);
			return o;
		} catch (Exception ex2) {
			log.error("[getOrganisationByIdAndDeleted]",ex2);
		}
		return null;
	}
	
	public Long deleteOrganisation(long user_level,long organisation_id, long updatedby){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				return this.deleteOrganisation(organisation_id, updatedby);
			}
		} catch (Exception ex2) {
			log.error("[deleteOrganisation]",ex2);
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param organisation_id
	 * @param updatedby
	 * @return
	 */
	public Long deleteOrganisation(long organisation_id, long updatedby){
		try {
			
				Organisation org = this.getOrganisationById(organisation_id);
				org.setDeleted("true");
				org.setUpdatedby(updatedby);
				
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				if (org.getOrganisation_id() == null) {
					session.persist(org);
				    } else {
				    	if (!session.contains(org)) {
				    		session.merge(org);
				    }
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				return org.getOrganisation_id();

		} catch (Exception ex2) {
			log.error("[deleteOrganisation]",ex2);
		}
		return null;
	}

	/**
	 * Adds a user to a given organisation-unit
	 * @param user_id
	 * @param organisation_id
	 * @param insertedby
	 * @return
	 */
	public Long addUserToOrganisation(Long user_id, Long organisation_id,
			Long insertedby, String comment) {
		try {
			if (this.getOrganisation_UserByUserAndOrganisation(user_id, organisation_id)==null) {
				Organisation org = this.getOrganisationById(organisation_id);
				log.debug("org: " + org.getName());
			
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				Organisation_Users orgUser = new Organisation_Users();
				orgUser.setOrganisation(org);
				orgUser.setUser_id(user_id);
				orgUser.setDeleted("false");
				orgUser.setStarttime(new Date());
				orgUser.setComment(comment);
				orgUser = session.merge(orgUser);
				//We need this flush
				session.flush();
				long id = orgUser.getOrganisation_users_id();
				
				
				tx.commit();
				HibernateUtil.closeSession(idf);
				return id;
			} else {
				return -35L;
			}
		} catch (Exception ex2) {
			log.error("[addUserToOrganisation]",ex2);
		}
		return null;
	}
	
	public Long addOrganisationUserObj(Organisation_Users orgUser) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			orgUser.setStarttime(new Date());
			orgUser = session.merge(orgUser);
			//We need this flush
			session.flush();
			long id = orgUser.getOrganisation_users_id();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			return id;
		} catch (Exception ex2) {
			log.error("[addUserToOrganisation]",ex2);
		}
		return null;
	}
	
	public Organisation_Users getOrganisation_UserByUserAndOrganisation(long user_id, long organisation_id){
		try {
			
			log.debug("getOrganisation_UserByUserAndOrganisation "+user_id+ "  "+organisation_id);

			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query q = session.createQuery("select c from Organisation_Users c where c.deleted = 'false' AND c.organisation.organisation_id = :organisation_id AND c.user_id = :user_id");
			q.setParameter("organisation_id", organisation_id);
			q.setParameter("user_id", user_id);
			List<Organisation_Users> ll = q.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			log.debug("getOrganisation_UserByUserAndOrganisation: "+ll.size());
			if (ll.size()>0){
				return (Organisation_Users) ll.get(0);
			}
			
		} catch (Exception ex2) {
			log.error("[getOrganisation_UserByUserAndOrganisation]",ex2);
		}
		return null;
	}
	
	public Long deleteUserFromOrganisation(Long user_level, long user_id, long organisation_id){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				log.error("deleteUserFromOrganisation "+user_id+ "  "+organisation_id);
				
				Organisation_Users orgUser = this.getOrganisation_UserByUserAndOrganisation(user_id,organisation_id);
				log.error("org: " + orgUser.getOrganisation().getName());
				orgUser.setDeleted("true");
				orgUser.setUpdatetime(new Date());
				
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				if (orgUser.getOrganisation_users_id() == null) {
					session.persist(orgUser);
				    } else {
				    	if (!session.contains(orgUser)) {
				    		session.merge(orgUser);
				    }
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
				return orgUser.getOrganisation_users_id();
			} else { 
				log.error("[deleteUserFromOrganisation] authorization required");
			}
		} catch (Exception ex2) {
			log.error("[deleteuserFromOrganisation]",ex2);
		}
		return null;
	}
	
	
	private boolean checkUserContainsOrganisation(long users_id, long organisation_id){
		try {
			Users us = UsersDaoImpl.getInstance().getUser(users_id);
			for (Iterator it = us.getOrganisation_users().iterator(); it.hasNext();){
				Organisation_Users orguser = (Organisation_Users) it.next();
				if (orguser.getOrganisation().getOrganisation_id()==organisation_id)
					return true;
			}
		} catch (Exception ex2) {
			log.error("[checkUserContainsOrganisation]",ex2);
		}
		return false;
	}
	
	public SearchResult getUsersSearchResultByOrganisationId(long organisation_id, int start, int max, String orderby, boolean asc){
		try {
			
			SearchResult sResult = new SearchResult();
			sResult.setObjectName(Users.class.getName());
			sResult.setRecords(this.selectMaxUsersByOrganisationId(organisation_id));
			sResult.setResult(this.getUsersByOrganisationId(organisation_id, start, max, orderby, asc));
			return sResult;
				
		} catch (Exception ex2) {
			log.error("[getUsersSearchResultByOrganisationId]",ex2);
		}
		return null;
	}
	
	private Long selectMaxUsersByOrganisationId(long organisation_id) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();

			Query query = session.createQuery("select c.organisation_users_id from Organisation_Users c where c.deleted = 'false' AND c.organisation.organisation_id = :organisation_id");
			query.setParameter("organisation_id", organisation_id);
			
			List ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			log.debug("selectMaxUsersByOrganisationId"+ll.size());
			return new Long(ll.size());	
			
		} catch (Exception ex2) {
			log.error("[getUsersByOrganisationId]",ex2);
		}
		return null;
	}
	
	/**
	 * get a list of all users of an organisation
	 * @param user_level
	 * @param organisation_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List getUsersByOrganisationId(long organisation_id, int start, int max, String orderby, boolean asc){
		try {
				//get all users
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				String hql = "select c from Organisation_Users c " +
							"where c.deleted = 'false' " +
							"AND c.organisation.organisation_id = :organisation_id ";
				if (orderby.startsWith("c.")){
					hql += "ORDER BY "+orderby;
				} else {
					hql += "ORDER BY " + "c." + orderby;
				}
				if (asc) {
					hql += " ASC";
				} else {
					hql += " DESC";
				}
				
				Query q = session.createQuery(hql);
				q.setParameter("organisation_id", organisation_id);
				q.setFirstResult(start);
				q.setMaxResults(max);
				List<Organisation_Users> userOrg = q.getResultList();
				tx.commit();
				HibernateUtil.closeSession(idf);
				List<Users> userL = new LinkedList<Users>();
				for (Iterator<Organisation_Users> it = userOrg.iterator();it.hasNext();){
					Organisation_Users us = (Organisation_Users) it.next();
					
					//Only add this single Organization add this point cause
					//cause all the other are not needed at this point
					Users user = UsersDaoImpl.getInstance().getUser(us.getUser_id());
					
					user.setOrganisation_users(new HashSet<Organisation_Users>());
					user.getOrganisation_users().add(us);
					
					userL.add(user);
				}
				//Collections.sort(userL,new UsersFirstnameComperator());
				return userL;
		} catch (Exception ex2) {
			log.error("[getUsersByOrganisationId]",ex2);
		}
		return null;
	}
	
	/**
	 * 
	 * @param organisation_id
	 * @return
	 */
	public List<Users> getUsersByOrganisationId(long organisation_id){
		try {

			//get all users
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query q = session.createQuery("select c from Organisation_Users c where c.deleted = 'false' AND c.organisation.organisation_id = :organisation_id");
			q.setParameter("organisation_id", organisation_id);
			List<Organisation_Users> userOrg = q.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			List<Users> userL = new LinkedList<Users>();
			for (Iterator<Organisation_Users> it = userOrg.iterator();it.hasNext();){
				Organisation_Users us = it.next();
				userL.add(UsersDaoImpl.getInstance().getUser(us.getUser_id()));
			}
			Collections.sort(userL,new UsersLoginComperator());
			return userL;
			
		} catch (Exception ex2) {
			log.error("[getUsersByOrganisationId]",ex2);
		}
		return null;
	}	
	
	class UsersLoginComperator implements Comparator<Users> {
		public int compare(Users o1, Users o2) {
			if(o1!=null || o2!=null)
			return 0;
			
			return o1.getLogin().compareTo(o2.getLogin()) ;
		}
	}
	
	
	/**
	 * Filter all Organisations by user
	 * TODO: Add sorting
	 * @param user_level
	 * @param users_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @return
	 */
	public List getOrganisationsByUserId(long user_level, long user_id, int start, int max, String orderby, boolean asc){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
					Object idf = HibernateUtil.createSession();
					EntityManager session = HibernateUtil.getSession();
					EntityTransaction tx = session.getTransaction();
					tx.begin();
					String hql = "select c from Organisation_Users c " +
								"where c.deleted = 'false' " +
								"AND c.user_id = :user_id " +
								"GROUP BY c.organisation.organisation_id";
					Query q = session.createQuery(hql);
					q.setParameter("user_id", user_id);
					q.setFirstResult(start);
					q.setMaxResults(max);
					List<Organisation_Users> userOrgIds = q.getResultList();
					tx.commit();
					HibernateUtil.closeSession(idf);
					
					LinkedList<Organisation> userOrg = new LinkedList<Organisation>();
					
					for (Iterator<Organisation_Users> it = userOrgIds.iterator();it.hasNext();){
						Long org_id = (Long) it.next().getOrganisation().getOrganisation_id();
						userOrg.add(this.getOrganisationById(org_id));
					}

					return userOrg;
			}
		} catch (Exception ex2) {
			log.error("[getOrganisationsByUserId]",ex2);
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
	public List getRestOrganisationsByUserId(long user_level, long user_id, int start, int max, String orderby, boolean asc){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				//get all organisations
				List allOrgs = this.getOrganisations(0, 1000000, orderby, asc);
				List orgUser = this.getOrganisationsByUserId(user_level, user_id, start, max, orderby, asc);
				
				List<Organisation> returnList = new LinkedList<Organisation>();
				boolean notInList = true;
				
				for (Iterator it = allOrgs.iterator();it.hasNext();){
					Organisation org = (Organisation) it.next();
					notInList = true;
					for (Iterator it2 = orgUser.iterator();it2.hasNext();){
						Organisation orgObj = (Organisation) it2.next();
//						log.error("orgObj ID: "+orgObj.getOrganisation_id());
//						log.error("orgUser ID: "+org.getOrganisation_id());
						if (orgObj.getOrganisation_id().equals(org.getOrganisation_id())){
							notInList=false;
//							log.error("found notinList: "+notInList);
							break;
						}
					}
//					log.error("notinList: "+notInList);
					if (notInList) returnList.add(org);
				}
				
				return returnList;
			}
		} catch (Exception ex2) {
			log.error("[getRestOrganisationsByUserId]",ex2);
		}
		return null;
	}
	
	/**
	 * checks if the orgId is in the list of orgIds which have been send as organisations
	 * @param orgId
	 * @param org
	 * @return
	 * @throws Exception
	 */
	private boolean checkOrgInList(Long orgId, List org) throws Exception{
//		log.error("checkOrgInList "+orgId);
		for (Iterator it = org.iterator();it.hasNext();){
			Integer key = (Integer) it.next();
			Long newOrgId = key.longValue();
//			log.error("[checkOrgInList 1]: newOrgId "+newOrgId);
//			log.error("[checkOrgInList 2]: org "+orgId);
			if (newOrgId.equals(orgId)) {
//				log.error("checkOrgInList 3 found");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks if an orgId is already stored in the Users Organisations Object
	 * @param orgId
	 * @param org
	 * @return
	 * @throws Exception
	 */
	private boolean checkOrgInStoredList(long orgId, Set org) throws Exception {
//		log.error("checkOrgInStoredList "+orgId);
		for (Iterator it = org.iterator();it.hasNext();){
			Organisation_Users orgUsers = (Organisation_Users) it.next();
//			log.error("checkOrgInStoredList 2 "+orgUsers.getOrganisation().getOrganisation_id());
			if (orgUsers.getOrganisation().getOrganisation_id().equals(orgId)) {
//				log.error("checkOrgInStoredList 3 found");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * updates users-organisations by a given params
	 * @param us
	 * @param organisations
	 * @return
	 */
	public Long updateUserOrganisationsByUser(Users us, List organisations){
		try {
			LinkedList<Long> orgIdsToAdd = new LinkedList<Long>();
			LinkedList<Long> orgIdsToDelete = new LinkedList<Long>();
			
			if (us.getOrganisation_users()!=null){
				
				for (Iterator it = organisations.iterator();it.hasNext();){
					Integer key = (Integer) it.next();
					Long orgIdToAdd = key.longValue();
					boolean isAlreadyStored = this.checkOrgInStoredList(orgIdToAdd, us.getOrganisation_users());
					if (!isAlreadyStored) orgIdsToAdd.add(orgIdToAdd);
				}
				
				for (Iterator it = us.getOrganisation_users().iterator();it.hasNext();){
					Organisation_Users orgUsers = (Organisation_Users) it.next();
					Long orgIdStored = orgUsers.getOrganisation().getOrganisation_id();
//					log.error("updateUserOrganisationsByUser check1 : "+orgIdStored);
					boolean shouldBeStored = this.checkOrgInList(orgIdStored,organisations);
					if (!shouldBeStored) orgIdsToDelete.add(orgIdStored);
				}

//				log.error("updateUserOrganisationsByUser size ADD: "+orgIdsToAdd.size());
				for (Iterator it = orgIdsToAdd.iterator();it.hasNext();){
					Long orgToAdd = (Long) it.next();
					this.addUserToOrganisation(us.getUser_id(), orgToAdd, us.getUser_id(), "");
				}

//				log.error("updateUserOrganisationsByUser size DELETE: "+orgIdsToDelete.size());
				for (Iterator it = orgIdsToDelete.iterator();it.hasNext();){
					Long orgToDel = (Long) it.next();
					this.deleteUserFromOrganisation(new Long(3), us.getUser_id(), orgToDel);
				}				
			}
		} catch (Exception err){
			log.error("[updateUserOrganisationsByUser] ",err);
		}
		return null;
	}
	
	/**
	 * adds all send organisations to this User-Object (for newly registered/created Users)
	 * @param us
	 * @param org
	 * @return
	 */
	public Long addUserOrganisationsByHashMap(long us, List org){
		try {
			if (org!=null){
				for (Iterator it = org.iterator();it.hasNext();){
					Integer key = (Integer) it.next();
					Long newOrgId = key.longValue();
					this.addUserToOrganisation(us, newOrgId, new Long(1), "");
				}
			}
		} catch (Exception ex) {
			log.error("addUserOrganisationsByHashMap",ex);
		}
		return null;
	}

}
