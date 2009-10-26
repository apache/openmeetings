package org.openmeetings.app.data.user.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

public class UsersDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(UsersDaoImpl.class, "openmeetings");

	private static UsersDaoImpl instance = null;

	
	private UsersDaoImpl() {
	}

	public static synchronized UsersDaoImpl getInstance() {
		if (instance == null) {
			instance = new UsersDaoImpl();
		}
		return instance;
	}
	
	/**
	 * 
	 * @param user_id
	 * @return
	 */
	public Users getUser(Long user_id) {
		if (user_id != null && user_id > 0) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery("select c from Users as c where c.user_id = :user_id");
				query.setLong("user_id", user_id);
				Users users = (Users) query.uniqueResult();
				session.refresh(users);
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				//Somehow the Organizations are missing here o
				
				
				return users;
				// TODO: Add Usergroups to user
				// users.setUsergroups(ResHandler.getGroupmanagement().getUserGroups(user_id));
			} catch (HibernateException ex) {
				log.error("getUser",ex);
			} catch (Exception ex2) {
				log.error("getUser",ex2);
			}
		} else {
			log.error("[getUser] "+"Error: No USER_ID given");
		}
		return null;
	}
	
	public void updateUser(Users user) {
		if (user.getUser_id() > 0) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				session.update(user);
				tx.commit();
				HibernateUtil.closeSession(idf);
			} catch (HibernateException ex) {
				log.error("[updateUser] ",ex);
			} catch (Exception ex2) {
				log.error("[updateUser] ",ex2);
			}
		} else {
			log.error("[updateUser] "+"Error: No USER_ID given");
		}
	}
	

	public Long deleteUserID(long USER_ID) {
		try {
			if (USER_ID != 0) {
				Users us = UsersDaoImpl.getInstance().getUser(USER_ID);
				us.setDeleted("true");
				us.setUpdatetime(new Date());
				// result +=
				// Groupmanagement.getInstance().deleteUserFromAllGroups(new
				// Long(USER_ID));

				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				session.update(us);
				tx.commit();
				
				HibernateUtil.closeSession(idf);
				return us.getUser_id();
				// result +=
				// ResHandler.getBestellmanagement().deleteWarenkorbByUserID(USER_ID);
				// result +=
				// ResHandler.getEmailmanagement().deleteEMailByUserID(USER_ID);
				// result +=
				// ResHandler.getContactmanagement().deleteContactUsergroups(USER_ID);
				// result +=
				// ResHandler.getContactmanagement().deleteUserContact(USER_ID);

			}
		} catch (HibernateException ex) {
			log.error("[deleteUserID]" ,ex);
		} catch (Exception ex2) {
			log.error("[deleteUserID]" ,ex2);
		}
		return null;
	}
	

	/**
	 * returns the maximum
	 * @return
	 */
	public Long selectMaxFromUsers(){
		try {
			//get all users
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("select count(c.user_id) from Users c where c.deleted = 'false'"); 
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			log.info("selectMaxFromUsers"+(Long)ll.get(0));
			return (Long)ll.get(0);				
		} catch (HibernateException ex) {
			log.error("[selectMaxFromUsers] "+ex);
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] "+ex2);
		}
		return null;
	}
	
	public List<Users> getAllUsers(){
		try {
			
			//get all users
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Users.class, "openmeetings");
			crit.add(Restrictions.eq("deleted", "false"));

			List<Users> ll = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ll;				

		} catch (HibernateException ex) {
			log.error("[getAllUsers] "+ex);
		} catch (Exception ex2) {
			log.error("[getAllUsers] "+ex2);
		}
		return null;
	}	

	public Long getAllUserMax(String search) {
		try {
			
			String[] searchItems = search.split(" ");
			
			
			log.debug("getUserContactsBySearch: "+ search);
			//log.debug("getUserContactsBySearch: "+ userId);
			
			String hql = 	"select count(u) from  Users u "+					
							"WHERE u.deleted = 'false' ";
							
			
			hql +=		"AND ( ";
			for(int i=0;i<searchItems.length; i++){
				if (i != 0) {
					hql +=	" OR ";
				}
				hql +=	"( " +
							"lower(u.lastname) LIKE lower('%"+searchItems[i]+"%') OR lower(u.firstname) LIKE lower('%"+searchItems[i]+"%') " +
							//"OR lower(u.username) LIKE lower('%"+searchItems[i]+"%') " +
							//"OR lower(u.titel) LIKE lower('%"+searchItems[i]+"%') " +
							//"OR lower(u.email) LIKE lower('%"+searchItems[i]+"%') " +
							//"OR lower(u.firma) LIKE lower('%"+searchItems[i]+"%') " +
						") ";
								
			}
			hql += " )" ;
			
			log.debug("Show HQL: "+hql);						
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			
			//log.debug("id: "+folderId);
			
			//query.setLong("macomUserId", userId);
			//query.setLong("messageFolder", folderId);
			//query
						
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			
			//log.error((Long)ll.get(0));
			Long i = (Long)ll.get(0);
			
			return new Long(i);
		} catch (HibernateException ex) {
			log.error("[getAllUserMax]: " + ex);
		} catch (Exception ex2) {
			log.error("[getAllUserMax]: " + ex2);
		}
		return null;
	}
	
	/**
	 * check for duplicates
	 * @param DataValue
	 * @return
	 */
	public boolean checkUserLogin(String DataValue) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("select c from Users as c where c.login = :DataValue AND deleted != :deleted");
			query.setString("DataValue", DataValue);
			query.setString("deleted", "true");
			int count = query.list().size();

			tx.commit();
			HibernateUtil.closeSession(idf);
			if (count != 0) {
				return false;
			}			
		} catch (HibernateException ex) {
			log.error("[checkUserData]" ,ex);
		} catch (Exception ex2) {
			log.error("[checkUserData]" ,ex2);
		}
		return true;
	}


	public Users getUserByName(String login) {
		try {
			String hql = "SELECT u FROM Users as u " +
					" where u.login = :login" +
					" AND deleted != :deleted";
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("login", login);
			query.setString("deleted", "true");
			Users us = (Users) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return us;			
		} catch (Exception e) {
			log.error("[getUserByAdressesId]",e);
		}
		return null;
	}
	
	public Users getUserByAdressesId(Long adresses_id) {
		try {
			String hql = "SELECT u FROM Users as u " +
					" where u.adresses.adresses_id = :adresses_id" +
					" AND deleted != :deleted";
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setLong("adresses_id", adresses_id);
			query.setString("deleted", "true");
			Users us = (Users) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return us;			
		} catch (Exception e) {
			log.error("[getUserByAdressesId]",e);
		}
		return null;
	}
	
	public Object getUserByHash (String hash) {
		try {
			if (hash.length()==0) return new Long(-5);
			String hql = "SELECT u FROM Users as u " +
					" where u.resethash = :resethash" +
					" AND deleted != :deleted";
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("resethash", hash);
			query.setString("deleted", "true");
			Users us = (Users) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			if (us!=null) {
				return us;		
			} else {
				return new Long(-5);
			}
		} catch (Exception e) {
			log.error("[getUserByAdressesId]",e);
		}
		return new Long(-1);
	}
	
	public Object resetPassByHash (String hash, String pass) {
		try {
			Object u = this.getUserByHash(hash);
			if (u instanceof Users) {
				Users us = (Users) u;
				us.setPassword(ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(pass));
				us.setResethash("");
				UsersDaoImpl.getInstance().updateUser(us);
				return new Long(-8);
			} else {
				return u;
			}
		} catch (Exception e) {
			log.error("[getUserByAdressesId]",e);
		}
		return new Long(-1);
	}

	/**
	 * @param search
	 * @return
	 */
	public Long selectMaxFromUsersWithSearch(String search){
		try {
			
			String hql = "select count(c.user_id) from Users c " +
					"where c.deleted = 'false' " +
					"AND (" +
					"lower(c.login) LIKE lower(:search) " +
					"OR lower(c.firstname) LIKE lower(:search) " +
					"OR lower(c.lastname) LIKE lower(:search) " +
					")";
			
			//get all users
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery(hql); 
			query.setString("search", search);
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			log.info("selectMaxFromUsers"+(Long)ll.get(0));
			return (Long)ll.get(0);				
		} catch (HibernateException ex) {
			log.error("[selectMaxFromUsers] "+ex);
		} catch (Exception ex2) {
			log.error("[selectMaxFromUsers] "+ex2);
		}
		return null;
	}

}
