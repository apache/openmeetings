package org.openmeetings.app.data.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.dao.UserSipDataDaoImpl;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.adresses.Adresses;
import org.openmeetings.app.hibernate.beans.basic.Sessiondata;
import org.openmeetings.app.hibernate.beans.domain.Organisation_Users;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.recording.RoomClient;
import org.openmeetings.app.hibernate.beans.user.UserSipData;
import org.openmeetings.app.hibernate.beans.user.Userdata;
import org.openmeetings.app.hibernate.beans.user.Userlevel;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.sip.xmlrpc.OpenXGHttpClient;
import org.openmeetings.app.templates.ResetPasswordTemplate;
import org.openmeetings.utils.crypt.ManageCryptStyle;
import org.openmeetings.utils.mail.MailHandler;
import org.openmeetings.utils.mappings.CastMapToObject;
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
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Criteria crit = session.createCriteria(Users.class, ScopeApplicationAdapter.webAppRootKey);
				crit.add(Restrictions.eq("deleted", "false"));
				if (asc) crit.addOrder(Order.asc(orderby));
				else crit.addOrder(Order.desc(orderby));
				crit.setMaxResults(max);
				crit.setFirstResult(start);
				sresult.setResult(crit.list());
				tx.commit();
				HibernateUtil.closeSession(idf);
				return sresult;				
			}
		} catch (HibernateException ex) {
			log.error("[getUsersList] "+ex);
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
								"lower(u.lastname) LIKE lower('%"+searchItems[i]+"%') " +
								"OR lower(u.firstname) LIKE lower('%"+searchItems[i]+"%') " +
								"OR lower(u.login) LIKE lower('%"+searchItems[i]+"%') " +
								"OR lower(u.adresses.email) LIKE lower('%"+searchItems[i]+"%') " +
								//"OR lower(u.titel) LIKE lower('%"+searchItems[i]+"%') " +
								//"OR lower(u.email) LIKE lower('%"+searchItems[i]+"%') " +
								//"OR lower(u.firma) LIKE lower('%"+searchItems[i]+"%') " +
							") ";
									
				}
	
				hql += " ) ORDER BY "+orderby;
				
				if (asc) {
					hql += " ASC ";
				} else {
					hql += " DESC ";
				}
				
				log.debug("Show HQL: "+hql);
				
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery(hql);
				//query.setLong("macomUserId", userId);
				
				//query
				//if (asc) ((Criteria) query).addOrder(Order.asc(orderby));
				//else ((Criteria) query).addOrder(Order.desc(orderby));
				query.setFirstResult(start);
				query.setMaxResults(max);			
				List<Users> ll = query.list();
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				sresult.setResult(ll);
		
				return sresult;		

		} catch (HibernateException ex) {
			log.error("[getAllUserByRange] ",ex);
			ex.printStackTrace();
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
			
			String hql = "SELECT c from Users AS c " +
					"WHERE " +
					"(c.login LIKE :userOrEmail OR c.adresses.email LIKE :userOrEmail  ) " +
					"AND c.deleted != :deleted";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Query query = session.createQuery(hql);
			query.setString("userOrEmail", userOrEmail);
			query.setString("deleted", "true");
			
			List<Users> ll = query.list();
			
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
		
		} catch (HibernateException ex) {
			log.error("[loginUser]: ",ex);
		} catch (Exception ex2) {
			log.error("[loginUser]: ",ex2);
		}
		return new Long(-1);
	}
	
	public Users refreshUserObject(Users us) {
		try {
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			session.flush();
			session.refresh(us);
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return us;
		} catch (HibernateException ex) {
			log.error("[loginUser]: ",ex);
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
		
		} catch (HibernateException ex) {
			log.error("[loginUserByRemoteHash]: ",ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(us);
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("updateLastLogin",ex);
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
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Criteria crit = session.createCriteria(Users.class, ScopeApplicationAdapter.webAppRootKey);
				crit.add(Restrictions.ilike(searchcriteria, "%" + searchstring + "%"));
				if (asc) crit.addOrder(Order.asc(orderby));
				else crit.addOrder(Order.desc(orderby));
				crit.add(Restrictions.ne("deleted", "true"));
				crit.setMaxResults(max);
				crit.setFirstResult(start);
				List contactsZ = crit.list();
				tx.commit();
				HibernateUtil.closeSession(idf);
				return contactsZ;
			} catch (HibernateException ex) {
				log.error("searchUser",ex);
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
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery("select c from Userdata as c where c.user_id = :user_id AND deleted != :deleted");
				query.setLong("user_id", user_id.longValue());
				query.setString("deleted", "true");
				List ll = query.list();
				tx.commit();
				HibernateUtil.closeSession(idf);
				return ll;
			} catch (HibernateException ex) {
				log.error("getUserdataDashBoard",ex);
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
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery("select c from Userdata as c where c.user_id = :user_id AND c.data_key = :data_key AND deleted != :deleted");
				query.setLong("user_id", USER_ID.longValue());
				query.setString("data_key", DATA_KEY);
				query.setString("deleted", "true");
				userdata = query.list().size();
				tx.commit();
				HibernateUtil.closeSession(idf);
			} catch (HibernateException ex) {
				log.error("getUserdataNoByKey",ex);
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
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery("select c from Userdata as c where c.user_id = :user_id AND c.data_key = :data_key AND deleted != :deleted");
				query.setLong("user_id", user_id.longValue());
				query.setString("data_key", DATA_KEY);
				query.setString("deleted", "true");
				for (Iterator it2 = query.iterate(); it2.hasNext();) {
					userdata = (Userdata) it2.next();
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
			} catch (HibernateException ex) {
				log.error("getUserdataByKey",ex);
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
			Boolean generateSipUserData, String jNameTimeZone, Boolean forceTimeZoneCheck) {

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
					Session session = HibernateUtil.getSession();
					Transaction tx = session.beginTransaction();

					session.update(us);
					
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
			} catch (HibernateException ex) {
				log.error("[updateUser]",ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			String hqlUpdate = "update userdata set DATA_KEY= :DATA_KEY, USER_ID = :USER_ID, DATA = :DATA, updatetime = :updatetime, comment = :Comment where DATA_ID= :DATA_ID";
			int updatedEntities = session.createQuery(hqlUpdate).setString(
					"DATA_KEY", DATA_KEY).setLong("USER_ID", USER_ID)
					.setString("DATA", DATA).setLong("updatetime",new Long(-1))
					.setString("Comment", Comment).setInteger("DATA_ID",
							DATA_ID).executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("updateUserdata",ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			String hqlUpdate = "update Userdata set data = :data, updatetime = :updatetime, "
					+ "comment = :comment where user_id= :user_id AND data_key = :data_key";
			int updatedEntities = session.createQuery(hqlUpdate).setString(
					"data", DATA).setLong("updatetime",
					new Long(-1)).setString(
					"comment", Comment).setLong("user_id", USER_ID.longValue())
					.setString("data_key", DATA_KEY).executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("updateUserdataByKey",ex);
		} catch (Exception ex2) {
			log.error("updateUserdataByKey",ex2);
		}
		return res;
	}

	public String deleteUserdata(int DATA_ID) {
		String res = "Fehler beim deleteUserdata";
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			String hqlUpdate = "delete userdata where DATA_ID= :DATA_ID";
			int updatedEntities = session.createQuery(hqlUpdate).setInteger("DATA_ID", DATA_ID).executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("deleteUserdata",ex);
		} catch (Exception ex2) {
			log.error("deleteUserdata",ex2);
		}
		return res;
	}

	public String deleteUserdataByUserAndKey(int users_id, String DATA_KEY) {
		String res = "Fehler beim deleteUserdataByUserAndKey";
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			String hqlUpdate = "delete userdata where users_id= :users_id AND DATA_KEY = :DATA_KEY";
			int updatedEntities = session.createQuery(hqlUpdate).setInteger(
					"users_id", users_id).setString("DATA_KEY", DATA_KEY)
					.executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("deleteUserdataByUserAndKey",ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.save(userdata);
			session.flush();
			session.clear();
			session.refresh(userdata);
			tx.commit();
			HibernateUtil.closeSession(idf);
			ret = "success";
		} catch (HibernateException ex) {
			log.error("addUserdata",ex);
		} catch (Exception ex2) {
			log.error("addUserdata",ex2);
		}
		return ret;
	}

	private Userlevel getUserLevel(Long level_id) {
		Userlevel userlevel = new Userlevel();
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("select c from Userlevel as c where c.level_id = :level_id AND deleted != :deleted");
			query.setLong("level_id", level_id.longValue());
			query.setString("deleted", "true");
			for (Iterator it2 = query.iterate(); it2.hasNext();) {
				userlevel = (Userlevel) it2.next();
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("[getUserLevel]" ,ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Query query = session.createQuery("select c from Users as c where c.user_id = :user_id AND deleted <> 'true'");
			query.setLong("user_id", user_id);
			Users us = (Users) query.uniqueResult();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (us!=null){
				return us.getLevel_id();
			} else {
				return -1L;
			}
		} catch (HibernateException ex) {
			log.error("[getUserLevelByID]" ,ex);
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Query query = session.createQuery("select c from Users as c where c.user_id = :user_id AND deleted <> 'true'");
			query.setLong("user_id", user_id);
			Users us = (Users) query.uniqueResult();
			
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
		} catch (HibernateException ex) {
			log.error("[getUserLevelByID]" ,ex);
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
										sendConfirmation,"","","", generateSipUserData, jNameTimeZone, false);
				
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
			String jName_timezone, Boolean forceTimeZoneCheck) throws Exception {
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
					
					Long user_id = this.addUser(level_id, availible, status,firstname, login, lastname, language_id, 
									Userpass,address_id, age, hash, 
									sip_user, sip_pass, sip_auth, generateSipUserData, jName_timezone, 
									forceTimeZoneCheck);
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
			Boolean forceTimeZoneCheck) {
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			long user_id = (Long) session.save(users);
			tx.commit();
			HibernateUtil.closeSession(idf);

			return user_id;

		} catch (HibernateException ex) {
			log.error("[registerUser]" ,ex);
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
					"AND deleted != :deleted";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Query query = session.createQuery(hql);
			query.setLong("externalUserId", externalUserId);
			query.setString("externalUserType", externalUserType);
			query.setString("deleted","true");
			
			List<Users> users = query.list();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (users.size() > 0) {
				return users.get(0);
			}
			
		} catch (HibernateException ex) {
			log.error("[getUserByExternalIdAndType]" ,ex);
		} catch (Exception ex2) {
			log.error("[getUserByExternalIdAndType]" ,ex2);
		}
		return null;
	}
	
	public Long addUserWithExternalKey(long level_id, int availible, int status,
			String firstname, String login, String lastname, long language_id,
			String userpass, Long adress_id, Date age, String hash, 
			Long externalUserId, String externalUserType, 
			boolean generateSipUserData, String email, String jNameTimeZone) {
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
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			long user_id = (Long) session.save(users);
			tx.commit();
			HibernateUtil.closeSession(idf);

			return user_id;

		} catch (HibernateException ex) {
			log.error("[registerUser]" ,ex);
		} catch (Exception ex2) {
			log.error("[registerUser]" ,ex2);
		}
		return null;
	}
	
	public Long addUser(Users usr) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long user_id = (Long) session.save(usr);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return user_id;
		} catch (HibernateException ex) {
			log.error("[addUser]" ,ex);
		} catch (Exception ex2) {
			log.error("[addUser]" ,ex2);
		}
		return null;
	}
	
	public Long addUserBackup(Users usr) {
		try {
			
			Long adresses_id = Addressmanagement.getInstance().saveAddressObj(usr.getAdresses());
			
			usr.setAdresses(Addressmanagement.getInstance().getAdressbyId(adresses_id));
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long user_id = (Long) session.save(usr);
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			
			for (Iterator<Organisation_Users> orgUserIterator = usr.getOrganisation_users().iterator();orgUserIterator.hasNext();) {
				
				Organisation_Users organisationUsers = orgUserIterator.next();
				
				Organisationmanagement.getInstance().addOrganisationUserObj(organisationUsers);
				
			}
			
			return user_id;
			
			
		} catch (HibernateException ex) {
			log.error("[addUserBackup]" ,ex);
		} catch (Exception ex2) {
			log.error("[addUserBackup]" ,ex2);
		}
		return null;
	}
	
	public void addUserLevel(String description, int myStatus) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Userlevel uslevel = new Userlevel();
			uslevel.setStarttime(new Date());
			uslevel.setDescription(description);
			uslevel.setStatuscode(new Integer(myStatus));
			uslevel.setDeleted("false");
			session.save(uslevel);
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("[addUserLevel]" ,ex);
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
					
					//Addressmanagement.getInstance().updateAdress(savedUser.getAdresses());
					
					
					//savedUser.setAdresses(Addressmanagement.getInstance().getAdressbyId(user.getAdresses().getAdresses_id()));
					
					Object idf = HibernateUtil.createSession();
					Session session = HibernateUtil.getSession();
					Transaction tx = session.beginTransaction();

					session.update(savedUser);
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
		
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		
		Criteria crit = session.createCriteria(Users.class, ScopeApplicationAdapter.webAppRootKey);
		crit.add(Restrictions.eq("user_id", id));
		crit.add(Restrictions.eq("deleted", "false"));
		//crit.add(Restrictions.eq("status", 1));
		Users u = (Users)crit.uniqueResult();
		
		tx.commit();
		HibernateUtil.closeSession(idf);
		
		return u;
		
	}
	
	public Users getUserByIdAndDeleted(Long id) throws Exception{
		log.debug("Usermanagement.getUserById");
		
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		
		Criteria crit = session.createCriteria(Users.class, ScopeApplicationAdapter.webAppRootKey);
		crit.add(Restrictions.eq("user_id", id));
		//crit.add(Restrictions.eq("status", 1));
		Users u = (Users)crit.uniqueResult();
		
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
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		
		Criteria crit = session.createCriteria(Users.class, ScopeApplicationAdapter.webAppRootKey);
		crit.add(Restrictions.eq("login", login));
		crit.add(Restrictions.eq("deleted", "false"));
		//crit.add(Restrictions.eq("status", 1));
		Users u = (Users)crit.uniqueResult();
		
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
				"(c.login LIKE :userOrEmail OR c.adresses.email LIKE :userOrEmail  )" +
				"AND c.externalUserId IS NULL " +
				"AND c.deleted != :deleted";
		
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		
		session.flush();
		
		Query query = session.createQuery(hql);
		query.setString("userOrEmail", userOrEmail);
		query.setString("deleted", "true");
		
		List<Users> ll = query.list();
		
		if (ll.size() > 1) {
			throw new Exception("ALERT :: There are two users in the database that have either same login or Email ");
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

	/**
	 * @param hash
	 * @return
	 */
	public Users getUserByActivationHash(String hash) {
        try {
            String hql = "SELECT u FROM Users as u " +
                            " where u.activatehash = :activatehash" +
                            " AND deleted != :deleted";
            Object idf = HibernateUtil.createSession();
            Session session = HibernateUtil.getSession();
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery(hql);
            query.setString("activatehash", hash);
            query.setString("deleted", "true");
            Users us = (Users) query.uniqueResult();
            tx.commit();
            HibernateUtil.closeSession(idf);
            return us;
	    } catch (Exception e) {
	            log.error("[getUserByActivationHash]",e);
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
							"lower(c.login) LIKE lower(:search) " +
							"OR lower(c.firstname) LIKE lower(:search) " +
							"OR lower(c.lastname) LIKE lower(:search) " +
							")";
							
				hql += " ORDER BY " + orderby;
				
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
				
				SearchResult sresult = new SearchResult();
				sresult.setObjectName(Users.class.getName());
				sresult.setRecords(UsersDaoImpl.getInstance().selectMaxFromUsersWithSearch(search));
				
				//get all users
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				
				Query query = session.createQuery(hql); 
				query.setString("search", search);
				query.setMaxResults(max);
				query.setFirstResult(start);
				
//				Criteria crit = session.createCriteria(Users.class, ScopeApplicationAdapter.webAppRootKey);
//				crit.add(Restrictions.eq("deleted", "false"));
//				if (asc) crit.addOrder(Order.asc(orderby));
//				else crit.addOrder(Order.desc(orderby));
//				crit.setMaxResults(max);
//				crit.setFirstResult(start);
				
				sresult.setResult(query.list());
				tx.commit();
				HibernateUtil.closeSession(idf);
				
				return sresult;				
			}
		} catch (HibernateException ex) {
			log.error("[getUsersList] "+ex);
		} catch (Exception ex2) {
			log.error("[getUsersList] "+ex2);
		}
		return null;
	}

}
