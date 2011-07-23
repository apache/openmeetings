package org.openmeetings.app.data.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.dao.UserSipDataDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.adresses.Adresses;
import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.persistence.beans.domain.Organisation_Users;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.recording.RoomClient;
import org.openmeetings.app.persistence.beans.user.UserSipData;
import org.openmeetings.app.persistence.beans.user.Userdata;
import org.openmeetings.app.persistence.beans.user.Userlevel;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.persistence.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.sip.xmlrpc.OpenXGHttpClient;
import org.openmeetings.app.templates.ResetPasswordTemplate;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.openmeetings.utils.mail.MailHandler;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.io.utils.ObjectMap;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IScope;

/**
 * 
 * @author swagner
 *
 */
public class Usermanagement {

	private static final Logger log = Red5LoggerFactory.getLogger(Usermanagement.class, ScopeApplicationAdapter.webAppRootKey);

	private static Usermanagement instance = null;

	private Usermanagement() {
	}

	public static synchronized Usermanagement getInstance() {
		if (instance == null) {
			instance = new Usermanagement();
		}
		return instance;
	}
	
	/**
	 * query for a list of users
	 * @param users_id
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @return
	 */
	public SearchResult getUsersList(long user_level, int start, int max, String orderby, boolean asc){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				SearchResult sresult = new SearchResult();
				sresult.setObjectName(Users.class.getName());
				sresult.setRecords(UsersDaoImpl.getInstance().selectMaxFromUsers());
				
				//get all users
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				CriteriaBuilder cb = session.getCriteriaBuilder();
				CriteriaQuery<Users> cq = cb.createQuery(Users.class);
				Root<Users> c = cq.from(Users.class);
				Predicate condition = cb.equal(c.get("deleted"), "false");
				cq.where(condition);
				cq.distinct(asc);
				if (asc){
					cq.orderBy(cb.asc(c.get(orderby)));
				} else {
					cq.orderBy(cb.desc(c.get(orderby)));
				}
				TypedQuery<Users> q = session.createQuery(cq);
				q.setFirstResult(start);
				q.setMaxResults(max);
				List<Users> ll = q.getResultList();
				tx.commit();
				HibernateUtil.closeSession(idf);
				return sresult;				
			}
		} catch (Exception ex2) {
			log.error("[getUsersList] "+ex2);
		}
		return null;
	}
	
	
	
	
	
	public SearchResult getAllUserByRange(String search, int start, int max, String orderby, boolean asc){
		try {
				SearchResult sresult = new SearchResult();
				sresult.setObjectName(Users.class.getName());
				sresult.setRecords(UsersDaoImpl.getInstance().getAllUserMax(search));
				
				String[] searchItems = search.split(" ");
				
				
				log.debug("getUserContactsBySearch: "+ search);
				//log.debug("getUserContactsBySearch: "+ userId);
				
				String hql = 	"select u from  Users u "+				
								"WHERE u.deleted = 'false' ";
								
				
				hql +=		"AND ( ";
				for(int i=0;i<searchItems.length; i++){
					if (i != 0) {
						hql +=	" OR ";
					}
					hql +=	"( " +
								"lower(u.lastname) LIKE '" + StringUtils.lowerCase("%"+searchItems[i]+"%") + "' " +
								"OR lower(u.firstname) LIKE '" + StringUtils.lowerCase("%"+searchItems[i]+"%") +  "' " +
								"OR lower(u.login) LIKE '" + StringUtils.lowerCase("%"+searchItems[i]+"%") + "' " +
								"OR lower(u.adresses.email) LIKE '" + StringUtils.lowerCase("%"+searchItems[i]+"%") + "' " +
							") ";
									
				}
	
				hql += " ) ";
				if (orderby != null && orderby.length() > 0){
					hql += "ORDER BY " + orderby;
				}
				
				if (asc) {
					hql += " ASC ";
				} else {
					hql += " DESC ";
				}
				
				log.debug("Show HQL: "+hql);
				
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				Query query = session.createQuery(hql);
				//query.setParameter("macomUserId", userId);
				
				//query
				//if (asc) ((Criteria) query).addOrder(Order.asc(orderby));
				//else ((Criteria) query).addOrder(Order.desc(orderby));
				query.setFirstResult(start);
				query.setMaxResults(max);			
				List<Users> ll = query.getResultList();
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				sresult.setResult(ll);
		
				return sresult;		

		} catch (Exception ex2) {
			log.error("[getAllUserByRange] ",ex2);
			ex2.printStackTrace();
		}
		return null;
	}
	
	
	
	
	/**
	 * 
	 * @param user_level
	 * @param user_id
	 * @return
	 */
	public Users checkAdmingetUserById(long user_level, long user_id){
		//FIXME: We have to check here for the User only cause the Org-Moderator otherwise cannot access it
		if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
			return UsersDaoImpl.getInstance().getUser(user_id);
		}
		return null;
	}
	
	public List getUserByMod(Long user_level, long user_id){
		return null;
	}

	

	/**
	 * login logic
	 * @param SID
	 * @param Username
	 * @param Userpass
	 * @return
	 */
	public Object loginUser(String SID, String userOrEmail, String userpass, RoomClient currentClient, 
			Boolean storePermanent, Long language_id) {
		try {
			log.debug("Login user SID : " +SID + " Stored Permanent :" + storePermanent); 
			String hql = "SELECT c from Users AS c " +
					"WHERE " +
					"(c.login LIKE :userOrEmail OR c.adresses.email LIKE :userOrEmail  ) " +
					"AND c.deleted <> :deleted";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			Query query = session.createQuery(hql);
			query.setParameter("userOrEmail", userOrEmail);
			query.setParameter("deleted", "true");
			
			List<Users> ll = query.getResultList();
			
			tx.commit();
			HibernateUtil.closeSession(idf);

			log.debug("debug SIZE: " + ll.size());
			
			if (ll.size()==0) {
				return new Long(-10);
			} else {
				Users users = (Users) ll.get(0);
				
				//Refresh User Object
				users = this.refreshUserObject(users);
				
				if (ManageCryptStyle.getInstance().getInstanceOfCrypt().verifyPassword(userpass, users.getPassword())) {
					log.info("chsum OK: "+ users.getUser_id());
					Boolean bool = Sessionmanagement.getInstance().updateUser(SID, users.getUser_id(),storePermanent, language_id);
					if (bool==null){
						//Exception
						return new Long(-1);
					} else if (!bool) {
						//invalid Session-Object
						return new Long(-35);
					}
					
					//Check if activated
					if (users.getStatus() != null && users.getStatus().equals(0)) {
						return -41L;
					}
					
					users.setUserlevel(getUserLevel(users.getLevel_id()));		
					updateLastLogin(users);
					//If invoked via SOAP this is NULL
					if (currentClient!=null){
						currentClient.setUser_id(users.getUser_id());
					}
					
//					System.out.println("loginUser "+users.getOrganisation_users());
//					if (users.getOrganisation_users() != null) {
//						System.out.println("loginUser size "+users.getOrganisation_users().size());
//					}
					
					return users;
				} else {
					return new Long(-11);
				}
			}
		
		} catch (Exception ex2) {
			log.error("[loginUser]: ",ex2);
		}
		return new Long(-1);
	}
	
	public Users refreshUserObject(Users us) {
		try {
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			us = session.merge(us);
			session.refresh(us);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return us;
		} catch (Exception ex2) {
			log.error("[loginUser]: ",ex2);
		}
		return null;
	}
	
	public Users loginUserByRemoteHash(String SID, String remoteHash) {
		try {
			
			Sessiondata sessionData = Sessionmanagement.getInstance().getSessionByHash(remoteHash);
			
			
			if (sessionData != null) {
				
				Users u = Usermanagement.getInstance().getUserById(sessionData.getUser_id());
			
				Sessionmanagement.getInstance().updateUserWithoutSession(SID, u.getUser_id());
				
				return u;
			
			} else {
				
				return null;
				
			}
		
		} catch (Exception ex2) {
			log.error("[loginUserByRemoteHash]: ",ex2);
		}
		return null;
	}

	public Long logout(String SID, long USER_ID) {
		Sessionmanagement.getInstance().updateUser(SID, 0, false,null);
		return new Long(-12);
	}

	private void updateLastLogin(Users us) {
		try {
			us.setLastlogin(new Date());
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			if (us.getUser_id() == null) {
				session.persist(us);
			    } else {
			    	if (!session.contains(us)) {
			    		session.merge(us);
			    }
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (Exception ex2) {
			log.error("updateLastLogin",ex2);
		}
	}

	/**
	 * suche eines Bentzers
	 * @param user_level
	 * @param searchstring
	 * @param max
	 * @param start
	 * @return
	 */
	public List searchUser(long user_level, String searchcriteria, String searchstring, int max, int start, String orderby, boolean asc) {
		if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				CriteriaBuilder cb = session.getCriteriaBuilder();
				CriteriaQuery<Users> cq = cb.createQuery(Users.class);
				Root<Users> c = cq.from(Users.class);
				Expression<String> literal = cb.literal((String) "%" + searchstring + "%");
				//crit.add(Restrictions.ilike(searchcriteria, "%" + searchstring + "%"));
				Path<String> path = c.get(searchcriteria);
				Predicate predicate = cb.like(path, literal);
				Predicate condition = cb.notEqual(c.get("deleted"), "true");
				cq.where(condition, predicate);
				cq.distinct(asc);
				if (asc){
					cq.orderBy(cb.asc(c.get(orderby)));
				} else {
					cq.orderBy(cb.desc(c.get(orderby)));
				}
				TypedQuery<Users> q = session.createQuery(cq);
				q.setFirstResult(start);
				q.setMaxResults(max);
				List<Users> contactsZ = q.getResultList();
				tx.commit();
				HibernateUtil.closeSession(idf);
				return contactsZ;
			} catch (Exception ex2) {
				log.error("searchUser",ex2);
			}
		}
		return null;
	}

	public List getUserdataDashBoard(Long user_id) {
		if (user_id.longValue() > 0) {
			try {
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				Query query = session.createQuery("select c from Userdata as c where c.user_id = :user_id AND c.deleted <> :deleted");
				query.setParameter("user_id", user_id.longValue());
				query.setParameter("deleted", "true");
				List ll = query.getResultList();
				tx.commit();
				HibernateUtil.closeSession(idf);
				return ll;
			} catch (Exception ex2) {
				log.error("getUserdataDashBoard",ex2);
			}
		}
		return null;
	}

	private int getUserdataNoByKey(Long USER_ID, String DATA_KEY) {
		int userdata = 0;
		if (USER_ID.longValue() > 0) {
			try {
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				Query query = session.createQuery("select c from Userdata as c where c.user_id = :user_id AND c.data_key = :data_key AND c.deleted <> :deleted");
				query.setParameter("user_id", USER_ID.longValue());
				query.setParameter("data_key", DATA_KEY);
				query.setParameter("deleted", "true");
				userdata = query.getResultList().size();
				tx.commit();
				HibernateUtil.closeSession(idf);
			} catch (Exception ex2) {
				log.error("getUserdataNoByKey",ex2);
			}
		} else {
			System.out.println("Error: No USER_ID given");
		}
		return userdata;
	}

	public Userdata getUserdataByKey(Long user_id, String DATA_KEY) {
		Userdata userdata = new Userdata();
		if (user_id.longValue() > 0) {
			try {
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				Query query = session.createQuery("select c from Userdata as c where c.user_id = :user_id AND c.data_key = :data_key AND c.deleted <> :deleted");
				query.setParameter("user_id", user_id.longValue());
				query.setParameter("data_key", DATA_KEY);
				query.setParameter("deleted", "true");
				for (Iterator it2 = query.getResultList().iterator(); it2.hasNext();) {
					userdata = (Userdata) it2.next();
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
			} catch (Exception ex2) {
				log.error("getUserdataByKey",ex2);
			}
		} else {
			userdata.setComment("Error: No USER_ID given");
		}
		return userdata;
	}

	public Long updateUser(long user_level, Long user_id, Long level_id,
			String login, String password, String lastname, String firstname,
			Date age, String street, String additionalname, String zip, long states_id, String town,
			int availible, String telefon, String fax,
			String mobil, String email, String comment, int status, List organisations,
			int title_id, String phone, String sip_user, String sip_pass, String sip_auth, 
			Boolean generateSipUserData, String jNameTimeZone, Boolean forceTimeZoneCheck, 
			String userOffers, String userSearchs, Boolean showContactData, Boolean showContactDataToContacts) {

		if (AuthLevelmanagement.getInstance().checkUserLevel(user_level) && user_id != 0) {
			try {
				Users us = UsersDaoImpl.getInstance().getUser(user_id);
				
				// Check for duplicates
				boolean checkName = true;
				
				if (!login.equals(us.getLogin())){
					checkName = UsersDaoImpl.getInstance().checkUserLogin(login);
				}
				boolean checkEmail = true;
				
				// Compare old address with new address
				if (!email.equals(us.getAdresses().getEmail())){
					
					// Its a new one - check, whether another user already uses that one...
					checkEmail = Emailmanagement.getInstance().checkUserEMail(email);
				}
				
				if (generateSipUserData) {
					
					if (password.length() == 0) {
						//Cannot perform a SIP Creation without password re-enter
						return new Long(-43);
					}
				}
				
				if (checkName && checkEmail) {
//					log.info("user_id " + user_id);
					
					us.setLastname(lastname);
					us.setFirstname(firstname);
					us.setAge(age);
					us.setLogin(login);
					us.setUpdatetime(new Date());
					us.setAvailible(availible);
					us.setStatus(status);
					us.setTitle_id(title_id);
					us.setOmTimeZone(OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jNameTimeZone));
					us.setForceTimeZoneCheck(forceTimeZoneCheck);
					
					us.setUserOffers(userOffers);
					us.setUserSearchs(userSearchs);
					us.setShowContactData(showContactData);
					us.setShowContactDataToContacts(showContactDataToContacts);
					
					if (level_id != 0)
						us.setLevel_id(new Long(level_id));
					if (password.length() != 0) {
						if (password.length()>=6){
							us.setPassword(ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(password));
						} else {
							return new Long(-7);
						}
					}					
					
					
					Addressmanagement.getInstance().updateAdress(us.getAdresses().getAdresses_id(), street, zip, town, states_id, additionalname, comment, fax, email, phone);
					//Emailmanagement.getInstance().updateUserEmail(mail.getMail().getMail_id(),user_id, email);
					
					//add or delete organisations from this user
					if (organisations!=null){
						Organisationmanagement.getInstance().updateUserOrganisationsByUser(us, organisations);
					}
					
					if (generateSipUserData) {
						
						UserSipData userSipData = OpenXGHttpClient.getInstance().openSIPgUserCreateUser(firstname, "", lastname, us.getAdresses().getEmail(), password, login);
					
						if (us.getUserSipData() == null) {
							Long userSipDataId = UserSipDataDaoImpl.getInstance().addUserSipData(userSipData);
						
							us.setUserSipData(UserSipDataDaoImpl.getInstance().getUserSipDataById(userSipDataId));
						} else {
							
							us.getUserSipData().setUsername(userSipData.getUsername());
							us.getUserSipData().setUserpass(userSipData.getUserpass());
							us.getUserSipData().setAuthId(userSipData.getAuthId());
							
							UserSipDataDaoImpl.getInstance().updateUserSipData(us.getUserSipData());
						}
						
					} else if (us.getUserSipData() == null) {
						UserSipData userSipData = new UserSipData();
						
						userSipData.setUsername(sip_user);
						userSipData.setUserpass(sip_pass);
						userSipData.setAuthId(sip_auth);
						
						Long userSipDataId = UserSipDataDaoImpl.getInstance().addUserSipData(userSipData);
						
						us.setUserSipData(UserSipDataDaoImpl.getInstance().getUserSipDataById(userSipDataId));
						
					} else {
						
						UserSipData userSipData = UserSipDataDaoImpl.getInstance().getUserSipDataById(us.getUserSipData().getUserSipDataId());
						
						userSipData.setUsername(sip_user);
						userSipData.setUserpass(sip_pass);
						userSipData.setAuthId(sip_auth);
						
						UserSipDataDaoImpl.getInstance().updateUserSipData(userSipData);
						
						us.setUserSipData(userSipData);
						
					}
					
					//log.info("USER " + us.getLastname());
					Object idf = HibernateUtil.createSession();
					EntityManager session = HibernateUtil.getSession();
					EntityTransaction tx = session.getTransaction();
					tx.begin();

					if (us.getUser_id() == null) {
						session.persist(us);
					    } else {
					    	if (!session.contains(us)) {
					    		session.merge(us);
					    }
					}
					
					tx.commit();
					HibernateUtil.closeSession(idf);
					
					return us.getUser_id();
					
				} else {
					if (!checkName) {
						return new Long(-15);
					} else if (!checkEmail) {
						return new Long(-17);
					}
				}
			} catch (Exception ex2) {
				log.error("[updateUser]",ex2);
			}
		} else {
			log.error("Error: Permission denied");
			return new Long(-1);
		}
		return new Long(-1);
	}

	public String updateUserdata(int DATA_ID, long USER_ID, String DATA_KEY,
			String DATA, String Comment) {
		String res = "Fehler beim Update";
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			String hqlUpdate = "update userdata set DATA_KEY= :DATA_KEY, USER_ID = :USER_ID, DATA = :DATA, updatetime = :updatetime, comment = :Comment where DATA_ID= :DATA_ID";
			int updatedEntities = session.createQuery(hqlUpdate).setParameter(
                    "DATA_KEY", DATA_KEY).setParameter("USER_ID", USER_ID)
					.setParameter("DATA", DATA).setParameter("updatetime", new Long(-1))
					.setParameter("Comment", Comment).setParameter("DATA_ID",
                            DATA_ID).executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (Exception ex2) {
			log.error("updateUserdata",ex2);
		}
		return res;
	}

	public String updateUserdataByKey(Long USER_ID, String DATA_KEY,
			String DATA, String Comment) {
		String res = "Fehler beim Update";
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			String hqlUpdate = "update Userdata set data = :data, updatetime = :updatetime, "
					+ "comment = :comment where user_id= :user_id AND data_key = :data_key";
			int updatedEntities = session.createQuery(hqlUpdate).setParameter(
                    "data", DATA).setParameter("updatetime",
                    new Long(-1)).setParameter(
                    "comment", Comment).setParameter("user_id", USER_ID.longValue())
					.setParameter("data_key", DATA_KEY).executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (Exception ex2) {
			log.error("updateUserdataByKey",ex2);
		}
		return res;
	}

	public String deleteUserdata(int DATA_ID) {
		String res = "Fehler beim deleteUserdata";
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			String hqlUpdate = "delete userdata where DATA_ID= :DATA_ID";
			int updatedEntities = session.createQuery(hqlUpdate).setParameter("DATA_ID", DATA_ID).executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (Exception ex2) {
			log.error("deleteUserdata",ex2);
		}
		return res;
	}

	public String deleteUserdataByUserAndKey(int users_id, String DATA_KEY) {
		String res = "Fehler beim deleteUserdataByUserAndKey";
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			String hqlUpdate = "delete userdata where users_id= :users_id AND DATA_KEY = :DATA_KEY";
			int updatedEntities = session.createQuery(hqlUpdate).setParameter(
                    "users_id", users_id).setParameter("DATA_KEY", DATA_KEY)
					.executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (Exception ex2) {
			log.error("deleteUserdataByUserAndKey",ex2);
		}
		return res;
	}

	public String addUserdata(long USER_ID, String DATA_KEY, String DATA,
			String Comment) {
		String ret = "Fehler beim speichern der Userdata";
		Userdata userdata = new Userdata();
		userdata.setData_key(DATA_KEY);
		userdata.setData(DATA);
		userdata.setStarttime(new Date());
		userdata.setUpdatetime(null);
		userdata.setComment(Comment);
		userdata.setUser_id(new Long(USER_ID));
		userdata.setDeleted("false");
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			userdata = session.merge(userdata);
			session.flush();
			session.refresh(userdata);
			tx.commit();
			HibernateUtil.closeSession(idf);
			ret = "success";
		} catch (Exception ex2) {
			log.error("addUserdata",ex2);
		}
		return ret;
	}

	private Userlevel getUserLevel(Long level_id) {
		Userlevel userlevel = new Userlevel();
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery("select c from Userlevel as c where c.level_id = :level_id AND c.deleted <> :deleted");
			query.setParameter("level_id", level_id.longValue());
			query.setParameter("deleted", "true");
			for (Iterator it2 = query.getResultList().iterator(); it2.hasNext();) {
				userlevel = (Userlevel) it2.next();
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (Exception ex2) {
			log.error("[getUserLevel]" ,ex2);
		}
		return userlevel;
	}

	/**
	 * get user-role
	 * 1 - user
	 * 2 - moderator
	 * 3 - admin
	 * @param user_id
	 * @return
	 */
	public Long getUserLevelByID(Long user_id) {
		
		try {
			if (user_id==null) return new Long(0);
			//For direct access of linked users
			if (user_id==-1){
				return new Long(1);
			}
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			Query query = session.createQuery("select c from Users as c where c.user_id = :user_id AND c.deleted <> 'true'");
			query.setParameter("user_id", user_id);
	        Users us = null;
	        try {
	            us = (Users)query.getSingleResult();
	        } catch (NoResultException e) {
	            //u=null}
	        }
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (us!=null){
				return us.getLevel_id();
			} else {
				return -1L;
			}
		} catch (Exception ex2) {
			log.error("[getUserLevelByID]" ,ex2);
		}
		return null;
	}

	public Long getUserLevelByIdAndOrg(Long user_id, Long organisation_id) {
		
		try {
			if (user_id==null) return new Long(0);
			//For direct access of linked users
			if (user_id==-1){
				return new Long(1);
			}
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			Query query = session.createQuery("select c from Users as c where c.user_id = :user_id AND c.deleted <> 'true'");
			query.setParameter("user_id", user_id);
	        Users us = null;
	        try {
	            us = (Users)query.getSingleResult();
	        } catch (NoResultException e) {
	            //u=null}
	        }
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (us!=null){
				
				if (us.getLevel_id() > 2) {
					return us.getLevel_id();
				} else {
					
					log.debug("user_id, organisation_id"+user_id+", "+organisation_id);
				
					Organisation_Users ou = Organisationmanagement.getInstance().getOrganisation_UserByUserAndOrganisation(user_id, organisation_id);
					
					log.debug("ou: "+ou);
					
					if (ou != null) {
						if (ou.getIsModerator() != null && ou.getIsModerator()) {
							return 2L;
						} else {
							return us.getLevel_id();
						}
					} else {
						return us.getLevel_id();
					}
				}
				
			} else {
				return -1L;
			}
		} catch (Exception ex2) {
			log.error("[getUserLevelByID]" ,ex2);
		}
		return null;
	}

	/**
	 * Method to register a new User, User will automatically be added to the
	 * default user_level(1) new users will be automatically added to the
	 * Organisation with the id specified in the configuration value
	 * default_domain_id
	 * 
	 * @param user_level
	 * @param level_id
	 * @param availible
	 * @param status
	 * @param login
	 * @param Userpass
	 * @param lastname
	 * @param firstname
	 * @param email
	 * @param age
	 * @param street
	 * @param additionalname
	 * @param fax
	 * @param zip
	 * @param states_id
	 * @param town
	 * @param language_id
	 * @return
	 */  
	public Long registerUser(String login, String Userpass, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, String phone, String baseURL, 
			boolean generateSipUserData, String jNameTimeZone) {
		try {
			// Checks if FrontEndUsers can register
			if (Configurationmanagement.getInstance().getConfKey(3,"allow_frontend_register").getConf_value().equals("1")) {
				
				Boolean sendConfirmation = false;
				Integer sendEmailWithVerficationCode = Integer.valueOf(Configurationmanagement.getInstance().
						getConfKey(3,"sendEmailWithVerficationCode").getConf_value()).intValue();
				
				//Send Confirmation can only be true when the baseURL is set,
				//when you add a new user through the Administration panel
				//the baseURL is not set so sendConfirmation = false and there will be
				//neither an Email nor will this method return a negative error id
				if (baseURL.length() != 0 && sendEmailWithVerficationCode == 1) {
					sendConfirmation = true;
				}
				
				//TODO: Read and generate SIP-Data via RPC-Interface Issue 1098
				
				Long user_id = this.registerUserInit(3, 1, 0, 1, login, Userpass,lastname, firstname, email, age, 
										street, additionalname,fax, zip, states_id, town, 
										language_id, true, new LinkedList(), phone, baseURL, 
										sendConfirmation,"","","", generateSipUserData, jNameTimeZone, false,
										"",
										"",
										false,
										true);
				
				// Get the default organisation_id of registered users
				if (user_id>0){
					long organisation_id = Long.valueOf(Configurationmanagement.getInstance().getConfKey(3,"default_domain_id").getConf_value()).longValue();
					Organisationmanagement.getInstance().addUserToOrganisation(user_id,organisation_id, user_id, "");
				}
				
				if (sendConfirmation) {
					return new Long(-40);
				}
				
				return user_id;
			}
		} catch (Exception e) {
			log.error("[registerUser]",e);
		}
		return null;
	}
	
	public Long registerUserNoEmail(String login, String Userpass, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, String phone, 
			boolean generateSipUserData, String jNameTimeZone) {
		try {
			// Checks if FrontEndUsers can register
			if (Configurationmanagement.getInstance().getConfKey(3,"allow_frontend_register").getConf_value().equals("1")) {
				
				Boolean sendConfirmation = false;
				Boolean sendWelcomeMessage = false;
				String baseURL = "";
				
				//TODO: Read and generate SIP-Data via RPC-Interface Issue 1098
				
				Long user_id = this.registerUserInit(3, 1, 0, 1, login, Userpass,lastname, firstname, email, age, 
										street, additionalname,fax, zip, states_id, town, 
										language_id, sendWelcomeMessage, new LinkedList(), phone, baseURL, 
										sendConfirmation,"","","", generateSipUserData, jNameTimeZone, false,
										"",
										"",
										false,
										true);
				
				// Get the default organisation_id of registered users
				if (user_id>0){
					long organisation_id = Long.valueOf(Configurationmanagement.getInstance().getConfKey(3,"default_domain_id").getConf_value()).longValue();
					Organisationmanagement.getInstance().addUserToOrganisation(user_id,organisation_id, user_id, "");
				}
				
				if (sendConfirmation) {
					return new Long(-40);
				}
				
				return user_id;
			}
		} catch (Exception e) {
			log.error("[registerUserNoEmail]",e);
		}
		return null;
	}

	/**
	 * Adds a user including his adress-data,auth-date,mail-data
	 * 
	 * @param user_level
	 * @param level_id
	 * @param availible
	 * @param status
	 * @param login
	 * @param Userpass
	 * @param lastname
	 * @param firstname
	 * @param email
	 * @param age
	 * @param street
	 * @param additionalname
	 * @param fax
	 * @param zip
	 * @param states_id
	 * @param town
	 * @param language_id
	 * @param phone
	 * @return new users_id OR null if an exception, -1 if an error, -4 if mail
	 *         already taken, -5 if username already taken, -3 if login or pass
	 *         or mail is empty
	 */
	public Long registerUserInit(long user_level, long level_id, int availible,
			int status, String login, String Userpass, String lastname,
			String firstname, String email, Date age, String street,
			String additionalname, String fax, String zip, long states_id,
			String town, long language_id, boolean sendWelcomeMessage, 
			List organisations, String phone, String baseURL, Boolean sendConfirmation, 
			String sip_user, String sip_pass, String sip_auth, boolean generateSipUserData,
			String jName_timezone, Boolean forceTimeZoneCheck, 
			String userOffers, String userSearchs, Boolean showContactData, Boolean showContactDataToContacts) throws Exception {
		//TODO: make phone number persistent
		// User Level must be at least Admin
		// Moderators will get a temp update of there UserLevel to add Users to
		// their Group
		if (AuthLevelmanagement.getInstance().checkModLevel(user_level)) {
			// Check for required data
			if (login.length()>=4 && Userpass.length()>=4) {
				// Check for duplicates
				boolean checkName = UsersDaoImpl.getInstance().checkUserLogin(login);
				boolean checkEmail = Emailmanagement.getInstance().checkUserEMail(email);
				if (checkName && checkEmail) {
					
					String hash = ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(login + CalendarPatterns.getDateWithTimeByMiliSeconds(new Date()));
					String link = baseURL+"activateUser?u="+hash;
					
					if (sendWelcomeMessage && email.length()!=0) {
						//We need to pass the baseURL to check if this is really set to be send
						String sendMail = Emailmanagement.getInstance().sendMail(login, Userpass, email, link, sendConfirmation);
						if (!sendMail.equals("success")) return new Long(-19);
					}						
					Long address_id = Addressmanagement.getInstance().saveAddress(street, zip, town, states_id, additionalname, "",fax, phone, email);
					if (address_id==null) {
						return new Long(-22);
					}
					
					//If this user needs first to click his E-Mail verification code then set the status to 0
					if (sendConfirmation) {
						status = 0;
					}
					
					Long user_id = addUser(level_id, availible, status,firstname, login, lastname, language_id, 
									Userpass,address_id, age, hash, 
									sip_user, sip_pass, sip_auth, generateSipUserData, jName_timezone, 
									forceTimeZoneCheck, 
									userOffers, userSearchs, showContactData, showContactDataToContacts);
					log.debug("Added user-Id " + user_id);
					if (user_id==null) {
						return new Long(-111);
					}
					
					/*
					Long adress_emails_id = Emailmanagement.getInstance().registerEmail(email, address_id,"");
					if (adress_emails_id==null) {
						return new Long(-112);
					}
					*/
					
					Organisationmanagement.getInstance().addUserOrganisationsByHashMap(user_id, organisations);
					
					if (address_id > 0 && user_id > 0 ) {
						return user_id;
					} else {
						return new Long(-16);
					}
				} else {
					if (!checkName) {
						return new Long(-15);
					} else if (!checkEmail) {
						return new Long(-17);
					}
				}
			} else {
				return new Long(-13);
			}
		}
		return new Long(-1);
	}

	/**
	 * @author swagner This Methdo adds a User to the User-Table
	 * @param level_id
	 *            The User Level, 1=User, 2=GroupAdmin/Moderator,
	 *            3=SystemAdmin/Admin
	 * @param availible
	 *            The user is activated
	 * @param status
	 *            The user is not blocked by System admins
	 * @param firstname
	 * @param login
	 *            Username for login
	 * @param lastname
	 * @param language_id
	 * @param Userpass
	 *            is MD5-crypted
	 * @param adress_id
	 * @return user_id or error null
	 */
	public Long addUser(long level_id, int availible, int status,
			String firstname, String login, String lastname, long language_id,
			String userpass, long adress_id, Date age, String hash, 
			String sip_user, String sip_pass, String sip_auth, 
			boolean generateSipUserData, String jName_timezone,
			Boolean forceTimeZoneCheck, 
			String userOffers, String userSearchs, Boolean showContactData, Boolean showContactDataToContacts) {
		try {
			
			Users users = new Users();
			users.setFirstname(firstname);
			users.setLogin(login);
			users.setLastname(lastname);
			users.setAge(age);
			users.setAdresses(Addressmanagement.getInstance().getAdressbyId(adress_id));
			users.setAvailible(availible);
			users.setLastlogin(new Date());
			users.setLasttrans(new Long(0));
			users.setLevel_id(level_id);
			users.setStatus(status);
			users.setTitle_id(new Integer(1));
			users.setStarttime(new Date());
			users.setActivatehash(hash);
			users.setOmTimeZone(OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jName_timezone));
			users.setForceTimeZoneCheck(forceTimeZoneCheck);
			
			users.setUserOffers(userOffers);
			users.setUserSearchs(userSearchs);
			users.setShowContactData(showContactData);
			users.setShowContactDataToContacts(showContactDataToContacts);
			
			if (generateSipUserData) {
				
				UserSipData userSipData = OpenXGHttpClient.getInstance().openSIPgUserCreateUser(firstname, "", lastname, users.getAdresses().getEmail(), userpass, login);
				
				Long userSipDataId = UserSipDataDaoImpl.getInstance().addUserSipData(userSipData);
				
				users.setUserSipData(UserSipDataDaoImpl.getInstance().getUserSipDataById(userSipDataId));
				
			} else {
				UserSipData userSipData = new UserSipData();
				
				userSipData.setUsername(sip_user);
				userSipData.setUserpass(sip_pass);
				userSipData.setAuthId(sip_auth);
				
				Long userSipDataId = UserSipDataDaoImpl.getInstance().addUserSipData(userSipData);
				
				users.setUserSipData(UserSipDataDaoImpl.getInstance().getUserSipDataById(userSipDataId));
			}
			
			// this is needed cause the language is not a needed data at
			// registering
			if (language_id != 0) {
				users.setLanguage_id(new Long(language_id));
			} else {
				users.setLanguage_id(null);
			}
			users.setPassword(ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(userpass));
			users.setRegdate(new Date());
			users.setDeleted("false");

			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			users = session.merge(users);
			session.flush();
			Long user_id = users.getUser_id();
			tx.commit();
			HibernateUtil.closeSession(idf);

			return user_id;

		} catch (Exception ex2) {
			log.error("[registerUser]" ,ex2);
		}
		return null;
	}
	
	public Users getUserByExternalIdAndType(Long externalUserId, String externalUserType) {
		
		try {
			
			String hql = "select c from Users as c " +
					"where c.externalUserId = :externalUserId " +
					"AND c.externalUserType LIKE :externalUserType " +
					"AND c.deleted <> :deleted";
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			Query query = session.createQuery(hql);
			query.setParameter("externalUserId", externalUserId);
			query.setParameter("externalUserType", externalUserType);
			query.setParameter("deleted", "true");
			
			List<Users> users = query.getResultList();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (users.size() > 0) {
				return users.get(0);
			}
			
		} catch (Exception ex2) {
			log.error("[getUserByExternalIdAndType]" ,ex2);
		}
		return null;
	}
	
	public Long addUserWithExternalKey(long level_id, int availible, int status,
			String firstname, String login, String lastname, long language_id,
			String userpass, Long adress_id, Date age, String hash, 
			Long externalUserId, String externalUserType, 
			boolean generateSipUserData, String email, String jNameTimeZone,
			String pictureuri) {
		try {
			Users users = new Users();
			users.setFirstname(firstname);
			users.setLogin(login);
			users.setLastname(lastname);
			users.setAge(age);
			
			if (adress_id != null && adress_id > 0) {
			    users.setAdresses(Addressmanagement.getInstance().getAdressbyId(adress_id));
			} else {
				adress_id = Addressmanagement.getInstance().saveAddress("", "", "", 1L, "", "", "", "", email);
				users.setAdresses(Addressmanagement.getInstance().getAdressbyId(adress_id));
			}
			
			users.setAvailible(availible);
			users.setLastlogin(new Date());
			users.setLasttrans(new Long(0));
			users.setLevel_id(level_id);
			users.setStatus(status);
			users.setTitle_id(new Integer(1));
			users.setStarttime(new Date());
			users.setActivatehash(hash);
			users.setPictureuri(pictureuri);
			users.setOmTimeZone(OmTimeZoneDaoImpl.getInstance().getOmTimeZone(jNameTimeZone));
			
			if (generateSipUserData) {
				
				UserSipData userSipData = OpenXGHttpClient.getInstance().openSIPgUserCreateUser(firstname, "", lastname, users.getAdresses().getEmail(), userpass, login);
				
				Long userSipDataId = UserSipDataDaoImpl.getInstance().addUserSipData(userSipData);
				
				users.setUserSipData(UserSipDataDaoImpl.getInstance().getUserSipDataById(userSipDataId));
				
			} else {
				UserSipData userSipData = new UserSipData();
				
				userSipData.setUsername("");
				userSipData.setUserpass("");
				userSipData.setAuthId("");
				
				Long userSipDataId = UserSipDataDaoImpl.getInstance().addUserSipData(userSipData);
				
				users.setUserSipData(UserSipDataDaoImpl.getInstance().getUserSipDataById(userSipDataId));
			}
			
			users.setExternalUserId(externalUserId);
			users.setExternalUserType(externalUserType);
			
			// this is needed cause the language is not a needed data at
			// registering
			if (language_id != 0) {
				users.setLanguage_id(new Long(language_id));
			} else {
				users.setLanguage_id(null);
			}
			users.setPassword(ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(userpass));
			users.setRegdate(new Date());
			users.setDeleted("false");

			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			session.merge(users);
			long user_id = users.getUser_id();
			tx.commit();
			HibernateUtil.closeSession(idf);

			return user_id;

		} catch (Exception ex2) {
			log.error("[registerUser]" ,ex2);
		}
		return null;
	}
	
	public Long addUser(Users usr) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			usr = session.merge(usr);
			Long user_id = usr.getUser_id();
			tx.commit();
			HibernateUtil.closeSession(idf);
			return user_id;
		} catch (Exception ex2) {
			log.error("[addUser]" ,ex2);
		}
		return null;
	}
	
	public Long addUserBackup(Users usr) {
		try {
			
			Long adresses_id = Addressmanagement.getInstance().saveAddressObj(usr.getAdresses());
			
			usr.setAdresses(Addressmanagement.getInstance().getAdressbyId(adresses_id));
			
			Long userSipDataId = UserSipDataDaoImpl.getInstance().addUserSipData(usr.getUserSipData());
			if (userSipDataId != null) {
				usr.setUserSipData(UserSipDataDaoImpl.getInstance().getUserSipDataById(userSipDataId));
			}
			
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			usr = session.merge(usr);
			Long user_id = usr.getUser_id();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return user_id;
			
			
		} catch (Exception ex2) {
			log.error("[addUserBackup]" ,ex2);
		}
		return null;
	}
	
	public void addUserLevel(String description, int myStatus) {
		try {
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Userlevel uslevel = new Userlevel();
			uslevel.setStarttime(new Date());
			uslevel.setDescription(description);
			uslevel.setStatuscode(new Integer(myStatus));
			uslevel.setDeleted("false");
			session.merge(uslevel);
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (Exception ex2) {
			log.error("[addUserLevel]" ,ex2);
		}
	}
	
	
	
	/**
	 * Update User by Object
	 * @param user_level
	 * @param values
	 * @param users_id
	 * @return
	 */
	
	
	public Long saveOrUpdateUser(Long user_level,ObjectMap values, Long users_id){
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
				Long returnLong = null;
	
				Long user_id = Long.parseLong(values.get("user_id").toString());
				
				if (user_id != null && user_id >0) {					
					
					returnLong = user_id;
					Users savedUser = UsersDaoImpl.getInstance().getUser(user_id);
					savedUser.setAge((Date) values.get("age"));
					savedUser.setFirstname(values.get("firstname").toString());
					savedUser.setLastname(values.get("lastname").toString());
					savedUser.setTitle_id(Integer.parseInt(values.get("title_id").toString()));
					
					savedUser.setOmTimeZone(OmTimeZoneDaoImpl.getInstance().getOmTimeZone((values.get("jnameTimeZone").toString())));
					
					String password = values.get("password").toString();
					
					if (password.length()>3){
						savedUser.setPassword(ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(password));
					}
					
					String email = values.get("email").toString();
					
					
					if (!email.equals(savedUser.getAdresses().getEmail())){
						boolean checkEmail = Emailmanagement.getInstance().checkUserEMail(email);
						if (!checkEmail) {
							// mail already used by another user!
							returnLong = new Long(-11);
						}
						else{
							savedUser.getAdresses().setEmail(email);
						}
					}			
					
					String phone = values.get("phone").toString();
					savedUser.getAdresses().setPhone(phone);
					savedUser.getAdresses().setComment(values.get("comment").toString());
					savedUser.getAdresses().setStreet(values.get("street").toString());
					savedUser.getAdresses().setTown(values.get("town").toString());
					savedUser.getAdresses().setAdditionalname(values.get("additionalname").toString());
					savedUser.getAdresses().setZip(values.get("zip").toString());
					savedUser.setForceTimeZoneCheck(false);
					savedUser.getAdresses().setStates(Statemanagement.getInstance().getStateById(Long.parseLong(values.get("state_id").toString())));
					
					Addressmanagement.getInstance().updateAdress(savedUser.getAdresses());
					savedUser.setShowContactData(Boolean.valueOf(values.get("showContactData").toString()));
					savedUser.setShowContactDataToContacts(Boolean.valueOf(values.get("showContactDataToContacts").toString()));
					savedUser.setUserOffers(values.get("userOffers").toString());
					savedUser.setUserSearchs(values.get("userSearchs").toString());
					
					//savedUser.setAdresses(Addressmanagement.getInstance().getAdressbyId(user.getAdresses().getAdresses_id()));
					
					Object idf = HibernateUtil.createSession();
					EntityManager session = HibernateUtil.getSession();
					EntityTransaction tx = session.getTransaction();
					tx.begin();

					if (savedUser.getUser_id() == null) {
						session.persist(savedUser);
					    } else {
					    	if (!session.contains(savedUser)) {
					    		session.merge(savedUser);
					    }
					}
					session.flush();
					
					tx.commit();
					HibernateUtil.closeSession(idf);
					
					return returnLong;
				}
				
			} else {
				log.error("[saveOrUpdateUser] invalid auth "+users_id+ " "+new Date());
			}
		} catch (Exception ex) {
			log.error("[saveOrUpdateUser]",ex);
		}
		
		return null;
	}
	
	/**
	 * reset a username by a given mail oder login by sending a mail to the registered EMail-Address
	 * @param email
	 * @param username
	 * @param appLink
	 * @return
	 */
	public Long resetUser(String email, String username, String appLink) {
		try {
			
			log.debug("resetUser "+email);
			
			//check if Mail given
			if (email.length()>0){
				Adresses addr = Addressmanagement.getInstance().retrieveAddressByEmail(email);
				//log.debug("addr_e "+addr_e);
				if (addr!=null) {
					//log.debug("getAdresses_id "+addr_e.getAdresses_id());
					Users us = UsersDaoImpl.getInstance().getUserByAdressesId(addr.getAdresses_id());
					if (us!=null) {
						this.sendHashByUser(us, appLink);
						return new Long(-4);
					} else {
						return new Long(-9);
					}
				} else {
					return new Long(-9);
				}
			//check if username given
			} else if (username.length()>0){
				Users us = UsersDaoImpl.getInstance().getUserByName(username);
				if (us!=null) {
					this.sendHashByUser(us, appLink);
					return new Long(-4);
				} else {
					return new Long(-3);
				}
			}
		} catch (Exception e) {
			log.error("[resetUser]",e);
			return new Long(-1);
		}
		return new Long(-2);
	}
	
	private void sendHashByUser(Users us, String appLink) throws Exception {
		String loginData = us.getLogin()+new Date();
		log.debug("User: "+us.getLogin());
		us.setResethash(ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(loginData));
		UsersDaoImpl.getInstance().updateUser(us);
		String reset_link = appLink+"?lzproxied=solo&hash="+us.getResethash();
		
		String email = us.getAdresses().getEmail();
		
		Long default_lang_id = Long.valueOf(Configurationmanagement.getInstance().
        		getConfKey(3,"default_lang_id").getConf_value()).longValue();
		
		String template = ResetPasswordTemplate.getInstance().getResetPasswordTemplate(reset_link, default_lang_id);
		
		Fieldlanguagesvalues labelid517 = Fieldmanagment.getInstance().getFieldByIdAndLanguage(new Long(517), default_lang_id);
    	
		MailHandler.sendMail(email, labelid517.getValue(), template);
	}
	
	/**
	 * 
	 * Find User by Id
	 */
	//-----------------------------------------------------------------------------------------------------
	public Users getUserById(Long id) throws Exception{
		log.debug("Usermanagement.getUserById");
		
		if (id == 0) {
			return null;
		}
		
		Object idf = HibernateUtil.createSession();
		EntityManager session = HibernateUtil.getSession();
		EntityTransaction tx = session.getTransaction();
		tx.begin();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Users> cq = cb.createQuery(Users.class);
		Root<Users> c = cq.from(Users.class);
		Predicate condition = cb.equal(c.get("deleted"), "false");
		Predicate subCondition = cb.equal(c.get("user_id"), id);
		cq.where(condition,subCondition);
		TypedQuery<Users> q = session.createQuery(cq);
        Users u = null;
        try {
            u = (Users)q.getSingleResult();
            tx.commit();
        } catch (NoResultException e) {
        	tx.rollback();
            //u=null}
        }
        catch (NonUniqueResultException ex){
        	tx.rollback();
        }

		HibernateUtil.closeSession(idf);
		
		return u;
		
	}
	
	public Users getUserByIdAndDeleted(Long id) throws Exception{
		log.debug("Usermanagement.getUserById");
		
		Object idf = HibernateUtil.createSession();
		EntityManager session = HibernateUtil.getSession();
		EntityTransaction tx = session.getTransaction();
		tx.begin();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Users> cq = cb.createQuery(Users.class);
		Root<Users> c = cq.from(Users.class);
		Predicate condition = cb.equal(c.get("user_id"), id);
		cq.where(condition);
		TypedQuery<Users> q = session.createQuery(cq);
        Users u = null;
        try {
            u = (Users)q.getSingleResult();
        } catch (NoResultException e) {
            //u=null}
        }
		
		tx.commit();
		HibernateUtil.closeSession(idf);
		
		return u;
		
	}
	//-----------------------------------------------------------------------------------------------------
	
	/**
	 * @author o.becherer
	 * Find User by LoginName (test existence of a active user with login - name
	 */
	//-----------------------------------------------------------------------------------------------------
	public Users getUserByLogin(String login) throws Exception{
		log.debug("Usermanagement.getUserByLogin : " + login);
		
		Object idf = HibernateUtil.createSession();
		EntityManager session = HibernateUtil.getSession();
		EntityTransaction tx = session.getTransaction();
		tx.begin();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Users> cq = cb.createQuery(Users.class);
		Root<Users> c = cq.from(Users.class);
		Predicate condition = cb.equal(c.get("deleted"), "false");
		Predicate subCondition = cb.equal(c.get("login"), login);
		cq.where(condition,subCondition);
		TypedQuery<Users> q = session.createQuery(cq);
        Users u = null;
        try {
            u = (Users)q.getSingleResult();
        } catch (NoResultException e) {
            //u=null}
        }
		
		tx.commit();
		HibernateUtil.closeSession(idf);
		
		return u;
		
	}
	//-----------------------------------------------------------------------------------------------------
	
	/**
	 * @author swagner
	 * Find User by LoginName or EMail (test existence of a active user with login - name
	 */
	//-----------------------------------------------------------------------------------------------------
	public Users getUserByLoginOrEmail(String userOrEmail) throws Exception{
		log.debug("Usermanagement.getUserByLoginOrEmail : " + userOrEmail);
		
		String hql = "SELECT c from Users AS c " +
				"WHERE " +
				"(c.login LIKE :userOrEmail OR c.adresses.email LIKE :userOrEmail  ) " +
				"AND c.externalUserId IS NULL " +
				"AND c.deleted <> :deleted";
		
		Object idf = HibernateUtil.createSession();
		EntityManager session = HibernateUtil.getSession();
		EntityTransaction tx = session.getTransaction();
		tx.begin();
		
		session.flush();
		
		Query query = session.createQuery(hql);
		query.setParameter("userOrEmail", userOrEmail);
		query.setParameter("deleted", "true");
		
		List<Users> ll = query.getResultList();
		tx.commit();
		HibernateUtil.closeSession(idf);
		
		if (ll.size() > 1) {
			log.error("ALERT :: There are two users in the database that have either same login or Email ");
			return ll.get(0);
			//throw new Exception("ALERT :: There are two users in the database that have either same login or Email ");
		} else if (ll.size() == 1){
			return ll.get(0);
		} else {
			return null;
		}
		
	}
	
	public Users getUserByEmail(String userOrEmail) throws Exception{
		log.debug("Usermanagement.getUserByEmail : " + userOrEmail);
		
		String hql = "SELECT c from Users AS c " +
				"WHERE " +
				"c.adresses.email LIKE :userOrEmail";
		
		Object idf = HibernateUtil.createSession();
		EntityManager session = HibernateUtil.getSession();
		EntityTransaction tx = session.getTransaction();
		tx.begin();
		
		session.flush();
		
		Query query = session.createQuery(hql);
		query.setParameter("userOrEmail", userOrEmail);
		
		List<Users> ll = query.getResultList();
		tx.commit();
		HibernateUtil.closeSession(idf);
		
		if (ll.size() > 1) {
			log.error("ALERT :: There are two users in the database that have same Email ");
			return ll.get(0);
			//throw new Exception("ALERT :: There are two users in the database that have either same login or Email ");
		} else if (ll.size() == 1){
			return ll.get(0);
		} else {
			return null;
		}
		
	}
	
	/**
	 * @author o.becherer
	 * Updating User Object
	 */
	//-----------------------------------------------------------------------------------------------------
	public void updateUserObject(Users user, boolean encryptPasswd) throws Exception{
		log.debug("Usermanagement.getUserByLogin");
		
		
		if(encryptPasswd){
			String encrypted = ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase(user.getPassword());
			user.setPassword(encrypted);
		}
		
		UsersDaoImpl.getInstance().updateUser(user);		
		
		
	}
	//-----------------------------------------------------------------------------------------------------

	/**
	 * @param admin
	 * @param room_id
	 * @return
	 */
	public Boolean kickUserByStreamId(String SID, Long room_id) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			HashMap<String, RoomClient> MyUserList = ClientListManager
					.getInstance().getClientListByRoom(room_id);

			// admins only
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {

				Sessionmanagement.getInstance().clearSessionByRoomId(room_id);

				for (Iterator<String> iter = MyUserList.keySet().iterator(); iter
						.hasNext();) {
					String key = (String) iter.next();

					RoomClient rcl = MyUserList.get(key);

					if (rcl == null) {
						return true;
					}
					String scopeName = "hibernate";
					if (rcl.getRoom_id() != null) {
						scopeName = rcl.getRoom_id().toString();
					}
					IScope currentScope = ScopeApplicationAdapter.getInstance()
							.getRoomScope(scopeName);
					ScopeApplicationAdapter.getInstance().roomLeaveByScope(rcl,
							currentScope);

					HashMap<Integer, String> messageObj = new HashMap<Integer, String>();
					messageObj.put(0, "kick");
					ScopeApplicationAdapter.getInstance().sendMessageById(
							messageObj, rcl.getStreamid(), currentScope);

				}

				return true;
			}

		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return null;
	}
	
	public Boolean kickUserByPublicSID(String SID, String publicSID) {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(
					users_id);
			

			// admins only
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {

				RoomClient rcl = ClientListManager.getInstance().getClientByPublicSID(publicSID);

				if (rcl == null) {
					return true;
				}
				
				String scopeName = "hibernate";
				if (rcl.getRoom_id() != null) {
					scopeName = rcl.getRoom_id().toString();
				}
				IScope currentScope = ScopeApplicationAdapter.getInstance()
						.getRoomScope(scopeName);
				ScopeApplicationAdapter.getInstance().roomLeaveByScope(rcl,
						currentScope);

				HashMap<Integer, String> messageObj = new HashMap<Integer, String>();
				messageObj.put(0, "kick");
				ScopeApplicationAdapter.getInstance().sendMessageById(
						messageObj, rcl.getStreamid(), currentScope);


				return true;
			}

		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return null;
	}

	/**
	 * @param hash
	 * @return
	 */
	public Users getUserByActivationHash(String hash) {
        try {
            String hql = "SELECT u FROM Users as u " +
                            " where u.activatehash = :activatehash" +
                            " AND c.deleted <> :deleted";
            Object idf = HibernateUtil.createSession();
            EntityManager session = HibernateUtil.getSession();
            EntityTransaction tx = session.getTransaction();
			tx.begin();
            Query query = session.createQuery(hql);
            query.setParameter("activatehash", hash);
            query.setParameter("deleted", "true");
            Users u = null;
            try {
                u = (Users)query.getSingleResult();
            } catch (NoResultException e) {
                //u=null}
            }
            tx.commit();
            HibernateUtil.closeSession(idf);
            return u;
	    } catch (Exception e) {
	            log.error("[getUserByActivationHash]",e);
	    }
	    return null;

	}

    public void updateUser(Users user) {
        if (user.getUser_id() > 0) {
            try {
                Object idf = HibernateUtil.createSession();
                EntityManager session = HibernateUtil.getSession();
                EntityTransaction tx = session.getTransaction();
                tx.begin();
				if (user.getUser_id() == null) {
					session.persist(user);
				    } else {
				    	if (!session.contains(user)) {
				    		session.merge(user);
				    }
				}
                tx.commit();
                HibernateUtil.closeSession(idf);
            } catch (Exception ex2) {
                    log.error("[updateUser] ",ex2);
            }
        } else {
                log.error("[updateUser] "+"Error: No USER_ID given");
        }
    }

	/**
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @param search
	 * @return
	 */
	public SearchResult getUsersListWithSearch(Long user_level, int start,
			int max, String orderby, boolean asc, String search) {
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
				
				String hql = "select c from Users c " +
							"where c.deleted = 'false' " +
							"AND (" +
							"lower(c.login) LIKE :search " +
							"OR lower(c.firstname) LIKE :search " +
							"OR lower(c.lastname) LIKE :search " +
							")";
							
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
				
				if (search.length() == 0) {
					search = "%";
				} else {
					search = "%" + search + "%";
				}
				log.debug("getUsersList search: "+ search);
				
				SearchResult sresult = new SearchResult();
				sresult.setObjectName(Users.class.getName());
				sresult.setRecords(UsersDaoImpl.getInstance().selectMaxFromUsersWithSearch(search));
				
				//get all users
				Object idf = HibernateUtil.createSession();
				EntityManager session = HibernateUtil.getSession();
				EntityTransaction tx = session.getTransaction();
				tx.begin();
				session.flush();
				
				Query query = session.createQuery(hql); 
				query.setParameter("search", StringUtils.lowerCase(search));
				query.setMaxResults(max);
				query.setFirstResult(start);
				
				sresult.setResult(query.getResultList());
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				return sresult;				
			}
		} catch (Exception ex2) {
			log.error("[getUsersList] "+ex2);
		}
		return null;
	}

	public List<Users> searchUserProfile(String searchTxt, String userOffers,
			String userSearchs, String orderBy, int start, int max, boolean asc) {
		try {
				
			String hql = "select c from Users c " +
						"where c.deleted = 'false' ";
						
						if (searchTxt.length() != 0 && userOffers.length() != 0 && userSearchs.length() != 0) {
						
							hql += "AND " +
									"(" +
										"(" +
												"lower(c.login) LIKE :search " +
												"OR lower(c.firstname) LIKE :search " +
												"OR lower(c.lastname) LIKE :search " +
												"OR lower(c.adresses.email) LIKE :search " +
												"OR lower(c.adresses.town) LIKE :search " +
											")" +
										"AND" +
											"(" +
												"lower(c.userOffers) LIKE :userOffers " +
											")" +
										"AND" +
											"(" +
												"lower(c.userSearchs) LIKE :userSearchs " +
											")" +
									")";
						
						} else if (searchTxt.length() != 0 && userOffers.length() != 0) {
							
							hql += "AND " +
									"(" +
										"(" +
												"lower(c.login) LIKE :search " +
												"OR lower(c.firstname) LIKE :search " +
												"OR lower(c.lastname) LIKE :search " +
												"OR lower(c.adresses.email) LIKE :search " +
												"OR lower(c.adresses.town) LIKE :search " +
											")" +
										"AND" +
											"(" +
												"lower(c.userOffers) LIKE :userOffers " +
											")" +
									")";
							
						} else if (searchTxt.length() != 0 && userSearchs.length() != 0) {
							
							hql += "AND " +
									"(" +
										"(" +
												"lower(c.login) LIKE :search " +
												"OR lower(c.firstname) LIKE :search " +
												"OR lower(c.lastname) LIKE :search " +
												"OR lower(c.adresses.email) LIKE :search " +
												"OR lower(c.adresses.town) LIKE :search " +
											")" +
										"AND" +
											"(" +
												"lower(c.userSearchs) LIKE :userSearchs " +
											")" +
									")";
							
						} else if (userOffers.length() != 0 && userSearchs.length() != 0) {
							
							hql += "AND " +
									"(" +
											"(" +
												"lower(c.userOffers) LIKE :userOffers " +
											")" +
										"AND" +
											"(" +
												"lower(c.userSearchs) LIKE :userSearchs " +
											")" +
									")";
							
						} else if (searchTxt.length() != 0) {
							
							hql += "AND " +
									"(" +
										"(" +
												"lower(c.login) LIKE :search " +
												"OR lower(c.firstname) LIKE :search " +
												"OR lower(c.lastname) LIKE :search " +
												"OR lower(c.adresses.email) LIKE :search " +
												"OR lower(c.adresses.town) LIKE :search " +
											")" +
									")";
							
						} else if (userOffers.length() != 0) {

							hql += "AND " +
									"(" +
										"(" +
											"lower(c.userOffers) LIKE :userOffers " +
										")" +
									")";
							
						} else if (userSearchs.length() != 0) {

							hql += "AND " +
									"(" +
										"(" +
											"lower(c.userSearchs) LIKE :userSearchs " +
										")" +
										")";
							
						}
						
			hql += " ORDER BY " + orderBy;
			
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}
			
			if (searchTxt.length() != 0) {
				searchTxt = "%" + searchTxt + "%";
			}
			
			if (userOffers.length() != 0) {
				userOffers = "%" + userOffers + "%";
			}
			
			if (userSearchs.length() != 0) {
				userSearchs = "%" + userSearchs + "%";
			}
			
			log.debug("hql :: "+hql);
			
			//get all users
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			Query query = session.createQuery(hql); 
			
			if (searchTxt.length() != 0 && userOffers.length() != 0 && userSearchs.length() != 0) {
				
				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
				query.setParameter("userSearchs", StringUtils.lowerCase(userSearchs));
			
			} else if (searchTxt.length() != 0 && userOffers.length() != 0) {
				
				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
				
			} else if (searchTxt.length() != 0 && userSearchs.length() != 0) {
				
				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userSearchs", StringUtils.lowerCase(userSearchs));
				
			} else if (userOffers.length() != 0 && userSearchs.length() != 0) {
				
				query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
				query.setParameter("userSearchs", StringUtils.lowerCase(userSearchs));
				
			} else if (searchTxt.length() != 0) {
				
				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				
			} else if (userOffers.length() != 0) {

				query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
				
			} else if (userSearchs.length() != 0) {

				query.setParameter("userSearchs", StringUtils.lowerCase(userSearchs));
				
			}
			
			
			query.setMaxResults(max);
			query.setFirstResult(start);
			
			List<Users> userList = query.getResultList();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return userList;	
			
		} catch (Exception ex2) {
			log.error("[getUsersList] ",ex2);
		}
		
		return null;
	}
	
	public Long searchCountUserProfile(String searchTxt, String userOffers,
			String userSearchs) {
		try {
				
			String hql = "select count(c.user_id) from Users c " +
						"where c.deleted = 'false' ";
						
						if (searchTxt.length() != 0 && userOffers.length() != 0 && userSearchs.length() != 0) {
						
							hql += "AND " +
									"(" +
										"(" +
												"lower(c.login) LIKE :search " +
												"OR lower(c.firstname) LIKE :search " +
												"OR lower(c.lastname) LIKE :search " +
												"OR lower(c.adresses.email) LIKE :search " +
												"OR lower(c.adresses.town) LIKE :search " +
											")" +
										"AND" +
											"(" +
												"lower(c.userOffers) LIKE :userOffers " +
											")" +
										"AND" +
											"(" +
												"lower(c.userSearchs) LIKE :userSearchs " +
											")" +
									")";
						
						} else if (searchTxt.length() != 0 && userOffers.length() != 0) {
							
							hql += "AND " +
									"(" +
										"(" +
												"lower(c.login) LIKE :search " +
												"OR lower(c.firstname) LIKE :search " +
												"OR lower(c.lastname) LIKE :search) " +
												"OR lower(c.adresses.email) LIKE :search " +
												"OR lower(c.adresses.town) LIKE :search " +
											")" +
										"AND" +
											"(" +
												"lower(c.userOffers) LIKE :userOffers " +
											")" +
									")";
							
						} else if (searchTxt.length() != 0 && userSearchs.length() != 0) {
							
							hql += "AND " +
									"(" +
										"(" +
												"lower(c.login) LIKE :search " +
												"OR lower(c.firstname) LIKE :search " +
												"OR lower(c.lastname) LIKE :search " +
												"OR lower(c.adresses.email) LIKE :search " +
												"OR lower(c.adresses.town) LIKE :search " +
											")" +
										"AND" +
											"(" +
												"lower(c.userSearchs) LIKE :userSearchs " +
											")" +
									")";
							
						} else if (userOffers.length() != 0 && userSearchs.length() != 0) {
							
							hql += "AND " +
									"(" +
											"(" +
												"lower(c.userOffers) LIKE :userOffers " +
											")" +
										"AND" +
											"(" +
												"lower(c.userSearchs) LIKE :userSearchs " +
											")" +
									")";
							
						} else if (searchTxt.length() != 0) {
							
							hql += "AND " +
									"(" +
										"(" +
												"lower(c.login) LIKE :search " +
												"OR lower(c.firstname) LIKE :search " +
												"OR lower(c.lastname) LIKE :search " +
												"OR lower(c.adresses.email) LIKE :search " +
												"OR lower(c.adresses.town) LIKE :search " +
											")" +
									")";
							
						} else if (userOffers.length() != 0) {

							hql += "AND " +
									"(" +
										"(" +
											"lower(c.userOffers) LIKE :userOffers " +
										")" +
									")";
							
						} else if (userSearchs.length() != 0) {

							hql += "AND " +
									"(" +
										"(" +
											"lower(c.userSearchs) LIKE :userSearchs " +
										")" +
										")";
							
						}
						
			if (searchTxt.length() != 0) {
				searchTxt = "%" + searchTxt + "%";
			}
			
			if (userOffers.length() != 0) {
				userOffers = "%" + userOffers + "%";
			}
			
			if (userSearchs.length() != 0) {
				userSearchs = "%" + userSearchs + "%";
			}
			
			log.debug("hql :: "+hql);
			
			//get all users
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			Query query = session.createQuery(hql); 
			
			if (searchTxt.length() != 0 && userOffers.length() != 0 && userSearchs.length() != 0) {
				
				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
				query.setParameter("userSearchs", StringUtils.lowerCase(userSearchs));
			
			} else if (searchTxt.length() != 0 && userOffers.length() != 0) {
				
				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
				
			} else if (searchTxt.length() != 0 && userSearchs.length() != 0) {
				
				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				query.setParameter("userSearchs", StringUtils.lowerCase(userSearchs));
				
			} else if (userOffers.length() != 0 && userSearchs.length() != 0) {
				
				query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
				query.setParameter("userSearchs", StringUtils.lowerCase(userSearchs));
				
			} else if (searchTxt.length() != 0) {
				
				query.setParameter("search", StringUtils.lowerCase(searchTxt));
				
			} else if (userOffers.length() != 0) {

				query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
				
			} else if (userSearchs.length() != 0) {

				query.setParameter("userSearchs", StringUtils.lowerCase(userSearchs));
				
			}
			
			
			List userList = query.getResultList();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return (Long) userList.get(0);	
			
		} catch (Exception ex2) {
			log.error("[getUsersList] ",ex2);
		}
		
		return null;
	}
	
	
	public Long searchMaxUserProfile(String searchTxt, String userOffers,
			String userSearchs) {
		try {
				
			String hql = "select count(c.user_id) from Users c " +
						"where c.deleted = 'false' " +
						"AND " +
						"(" +
								"(" +
									"lower(c.login) LIKE :search " +
									"OR lower(c.firstname) LIKE :search " +
									"OR lower(c.lastname) LIKE :search " +
									"OR lower(c.adresses.email) LIKE :search " +
									"OR lower(c.adresses.town) LIKE :search " +
								")" +
							"OR" +
								"(" +
									"lower(c.userOffers) LIKE :userOffers " +
								")" +
							"OR" +
								"(" +
									"lower(c.userSearchs) LIKE :userSearchs " +
								")" +
						")";
						
			if (searchTxt.length() == 0) {
				searchTxt = "%";
			} else {
				searchTxt = "%" + searchTxt + "%";
			}
			
			if (userOffers.length() == 0) {
				userOffers = "%";
			} else {
				userOffers = "%" + userOffers + "%";
			}
			
			if (userSearchs.length() == 0) {
				userSearchs = "%";
			} else {
				userSearchs = "%" + userSearchs + "%";
			}
			
			//get all users
			Object idf = HibernateUtil.createSession();
			EntityManager session = HibernateUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			
			Query query = session.createQuery(hql); 
			query.setParameter("search", StringUtils.lowerCase(searchTxt));
			query.setParameter("userOffers", StringUtils.lowerCase(userOffers));
			query.setParameter("userSearchs", StringUtils.lowerCase(userSearchs));
			
			List ll = query.getResultList();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return (Long) ll.get(0);		
			
		} catch (Exception ex2) {
			log.error("[searchMaxUserProfile] "+ex2);
		}
		
		return null;
	}

}
