package org.openmeetings.app.data.user;

import java.util.Iterator;
import java.util.Date;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmeetings.app.hibernate.beans.user.Users_Usergroups;
import org.openmeetings.app.hibernate.beans.user.Usergroups;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.utils.math.CalendarPatterns;

/**
 * 
 * @author swagner
 *
 */
public class Groupmanagement {

	private static final Logger log = Logger.getLogger(Groupmanagement.class);

	private static Groupmanagement instance;

	private Groupmanagement() {
	}

	public static synchronized Groupmanagement getInstance() {
		if (instance != null) {
			instance = new Groupmanagement();
		}
		return instance;
	}

	private boolean checkUserLevel(Long user_level) {
		if (user_level.longValue() > 1) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkConfLevel(Long user_level) {
		if (user_level.longValue() > 2) {
			return true;
		} else {
			return false;
		}
	}

	public Users_Usergroups[] getUserGroups(Long USER_ID) {
		Users_Usergroups[] usersusergroups = new Users_Usergroups[1];
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session
					.createQuery("select c from Users_Usergroups as c where c.user_id = :user_id");
			query.setLong("user_id", USER_ID.longValue());
			int count = query.list().size();
			usersusergroups = new Users_Usergroups[count];
			int k = 0;
			for (Iterator it2 = query.iterate(); it2.hasNext();) {
				usersusergroups[k] = (Users_Usergroups) it2.next();
				k++;
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("getUserGroups",ex);
		} catch (Exception ex2) {
			log.error("getUserGroups",ex2);
		}
		return usersusergroups;
	}

	public Users_Usergroups getUserGroupsSingle(long USER_ID) {
		Users_Usergroups usersusergroups = new Users_Usergroups();
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session
					.createQuery("select c from Users_Usergroups as c where c.user_id = :user_id");
			query.setLong("user_id", USER_ID);
			for (Iterator it2 = query.iterate(); it2.hasNext();) {
				usersusergroups = (Users_Usergroups) it2.next();
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("getUserGroupsSingle",ex);
		} catch (Exception ex2) {
			log.error("getUserGroupsSingle",ex2);
		}
		return usersusergroups;
	}

	public String addUserToGroup(Long user_level, Long GROUP_ID, Long USER_ID,
			String comment) {
		String res = "addUserToGroup";
		if (checkUserLevel(user_level)) {
			Users_Usergroups usersusergroups = new Users_Usergroups();
			usersusergroups.setUsergroup_id(GROUP_ID);
			usersusergroups.setUser_id(USER_ID);
			usersusergroups.setComment(comment);
			usersusergroups.setStarttime(new Date());
			usersusergroups.setUpdatetime(null);
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				session.save(usersusergroups);
				session.flush();
				session.clear();
				session.refresh(usersusergroups);
				tx.commit();
				HibernateUtil.closeSession(idf);
			} catch (HibernateException ex) {
				log.error("addUserToGroup",ex);
			} catch (Exception ex2) {
				log.error("addUserToGroup",ex2);
			}
		} else {
			res = "Error: Permission denied";
		}
		return res;
	}

	public String updateUserGroup(Long user_level, Long users_usergroups_id,
			Long usergroup_id, Long user_id, String comment) {
		String res = "updateUserGroup";
		if (checkUserLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				String hqlUpdate = " UPDATE Users_Usergroups set "
						+ " usergroup_id = :usergroup_id, user_id = :user_id, "
						+ " updatetime = :updatetime, comment = :comment "
						+ " where users_usergroups_id= :users_usergroups_id";
				int updatedEntities = session.createQuery(hqlUpdate).setLong(
						"usergroup_id", usergroup_id.longValue()).setLong(
						"user_id", user_id.longValue()).setLong("updatetime",
						new Long(-1)).setString(
						"comment", comment).setLong("users_usergroups_id",
						users_usergroups_id.longValue()).executeUpdate();
				res = "Success: " + updatedEntities;
				tx.commit();
				HibernateUtil.closeSession(idf);
			} catch (HibernateException ex) {
				log.error("updateUserGroup",ex);
			} catch (Exception ex2) {
				log.error("updateUserGroup",ex2);
			}
		} else {
			res = "Error: Permission denied";
		}
		return res;
	}

	public String deleteUserGroupByID(Long user_level, Long users_usergroups_id) {
		String res = "deleteUserGroupByID";
		if (checkUserLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				String hqlUpdate = "delete users_usergroups where users_usergroups_id= :users_usergroups_id";
				int updatedEntities = session.createQuery(hqlUpdate).setLong(
						"UID", users_usergroups_id.longValue()).executeUpdate();
				res = "Success" + updatedEntities;
				tx.commit();
				HibernateUtil.closeSession(idf);

			} catch (HibernateException ex) {
				log.error("deleteUserGroupByID",ex);
			} catch (Exception ex2) {
				log.error("deleteUserGroupByID",ex2);
			}
		} else {
			res = "Error: Permission denied";
		}
		return res;
	}

	public String deleteUserFromAllGroups(Long user_id) {
		String res = "deleteUserFromAllGroups";
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			String hqlUpdate = "delete users_usergroups where user_id= :user_id";
			int updatedEntities = session.createQuery(hqlUpdate).setLong(
					"user_id", user_id.longValue()).executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("deleteUserFromAllGroups",ex);
		} catch (Exception ex2) {
			log.error("deleteUserFromAllGroups",ex2);
		}
		return res;
	}

	public String deleteAllGroupUsers(Long usergroup_id) {
		String res = "deleteAllGroupUsers";
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			String hqlUpdate = "delete users_usergroups where usergroup_id= :usergroup_id";
			int updatedEntities = session.createQuery(hqlUpdate).setLong(
					"usergroup_id", usergroup_id.longValue()).executeUpdate();
			res = "Success" + updatedEntities;
			tx.commit();
			HibernateUtil.closeSession(idf);

		} catch (HibernateException ex) {
			log.error("deleteAllGroupUsers",ex);
		} catch (Exception ex2) {
			log.error("deleteAllGroupUsers",ex2);
		}
		return res;
	}

	public Users_Usergroups getGroupUsers(Long user_level, Long usergroup_id) {
		Users_Usergroups groups = new Users_Usergroups();
		if (checkUserLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session
						.createQuery("select c from Usergroups as c where c.usergroup_id = :usergroup_id");
				query.setLong("usergroup_id", usergroup_id.longValue());
				for (Iterator it2 = query.iterate(); it2.hasNext();) {
					groups = (Users_Usergroups) it2.next();
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
				//TODO: setzen der Usergroups einer Gruppe
				//groups.setUsergroups(getUsergroupsUsers(GROUP_ID));
			} catch (HibernateException ex) {
				log.error("getGroupUsers",ex);
			} catch (Exception ex2) {
				log.error("getGroupUsers",ex2);
			}
		} else {
			groups.setComment("Error: Permission denied");
		}
		return groups;
	}

	public Usergroups[] getUsergroupsUsers(Long usergroup_id) {
		Usergroups[] usergroups = new Usergroups[1];
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session
					.createQuery("select c from Usergroups as c where c.usergroup_id = :usergroup_id");
			query.setLong("usergroup_id", usergroup_id.longValue());
			int count = query.list().size();
			usergroups = new Usergroups[count];
			int k = 0;
			for (Iterator it2 = query.iterate(); it2.hasNext();) {
				usergroups[k] = (Usergroups) it2.next();
				k++;
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
			for (int vars = 0; vars < usergroups.length; vars++) {
				//Todo:setzend er Passenden Benutzergruppen
				//usergroups[vars].setUsers(ResHandler.getUsermanagement().getUserForGroup(usergroups[vars].getUSER_ID()));
			}
		} catch (HibernateException ex) {
			log.error("getUsergroupsUsers",ex);
		} catch (Exception ex2) {
			log.error("getUsergroupsUsers",ex2);
		}
		return usergroups;
	}

	public Usergroups[] getAllGroupUsers(Long user_level) {
		Usergroups[] usergroups = new Usergroups[1];
		if (checkConfLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery("from Usergroups");
				int count = query.list().size();
				usergroups = new Usergroups[count];
				int k = 0;
				for (Iterator it2 = query.iterate(); it2.hasNext();) {
					usergroups[k] = (Usergroups) it2.next();
					k++;
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
				for (int vars = 0; vars < usergroups.length; vars++) {
					//Todo:setzen der Benutzer dieser Gruppe
					//groups[vars].setUsergroups(getUsergroupsUsers(groups[vars].getGROUP_ID()));
				}
			} catch (HibernateException ex) {
				log.error("getAllGroupUsers",ex);
			} catch (Exception ex2) {
				log.error("getAllGroupUsers",ex2);
			}
		} else {
			usergroups[0] = new Usergroups();
			usergroups[0].setName("Error: Permission denied");
		}
		return usergroups;
	}

	public Usergroups getGroup(Long usergroup_id) {
		Usergroups usergroups = new Usergroups();
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session
					.createQuery("select c from Usergroups as c where c.usergroup_id = :usergroup_id");
			query.setLong("usergroup_id", usergroup_id.longValue());
			for (Iterator it2 = query.iterate(); it2.hasNext();) {
				usergroups = (Usergroups) it2.next();
			}
			tx.commit();
			HibernateUtil.closeSession(idf);
		} catch (HibernateException ex) {
			log.error("getGroup",ex);
		} catch (Exception ex2) {
			log.error("getGroup",ex2);
		}
		return usergroups;
	}

	public Usergroups[] getAllGroup(Long user_level) {
		Usergroups[] usergroups = new Usergroups[1];
		if (checkConfLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery("from Usergroups");
				int count = query.list().size();
				usergroups = new Usergroups[count];
				int k = 0;
				for (Iterator it2 = query.iterate(); it2.hasNext();) {
					usergroups[k] = (Usergroups) it2.next();
					k++;
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
				for (int vars = 0; vars < usergroups.length; vars++) {
					//groups[vars].setUsers(ResHandler.getUsermanagement().getUser(groups[vars].getUSER_ID()));
				}
			} catch (HibernateException ex) {
				log.error("getAllGroup",ex);
			} catch (Exception ex2) {
				log.error("getAllGroup",ex2);
			}
		} else {
			usergroups[0] = new Usergroups();
			usergroups[0].setName("Error: Permission denied");
		}
		return usergroups;
	}

	public Users_Usergroups getSingleGroup(Long user_level, Long usergroup_id) {
		Users_Usergroups groups = new Users_Usergroups();
		if (checkConfLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session
						.createQuery("select c from Usergroups as c where c.usergroup_id = :usergroup_id");
				query.setLong("usergroup_id", usergroup_id.longValue());
				for (Iterator it2 = query.iterate(); it2.hasNext();) {
					groups = (Users_Usergroups) it2.next();
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
				//TODO: Benutzer einer gruppe setzten
				//groups.setUsers(ResHandler.getUsermanagement().getUser(groups()));

			} catch (HibernateException ex) {
				log.error("getSingleGroup",ex);
			} catch (Exception ex2) {
				log.error("getSingleGroup",ex2);
			}
		} else {
			groups.setComment("Error: Permission denied");
		}
		return groups;
	}

	public String addGroup(Long user_level, Long USER_ID, Long level_id,
			String name, String description, String comment) {
		String res = "Addgroup";
		if (checkUserLevel(user_level)) {
			Usergroups usergroups = new Usergroups();
			//Todo: Add business logic for users-history
			//usergroups.setUSER_ID(USER_ID);
			usergroups.setLevel_id(level_id);
			usergroups.setName(name);
			//    		usergroups.setDescription(description);
			//    		usergroups.setComment(comment);
			usergroups.setStarttime(new Date());
			usergroups.setUpdatetime(null);
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				session.save(usergroups);
				session.flush();
				session.clear();
				session.refresh(usergroups);
				tx.commit();
				HibernateUtil.closeSession(idf);
			} catch (HibernateException ex) {
				log.error("addGroup",ex);
			} catch (Exception ex2) {
				log.error("addGroup",ex2);
			}
		} else {
			res = "Error: Permission denied";
		}
		return res;
	}

	public String updateGroup(Long user_level, Long USER_ID, Long level_id,
			Long usergroup_id, String name, String description, String comment) {
		String res = "UpdateGroup";
		if (checkUserLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				String hqlUpdate = "update Usergroups set user_id = :user_id,level_id = :level_id, name = :name, description = :description, updatetime = :updatetime, comment = :comment where usergroup_id= :usergroup_id";
				int updatedEntities = session.createQuery(hqlUpdate).setLong(
						"user_id", USER_ID.longValue()).setLong("level_id",
						level_id.longValue()).setString("name", name)
						.setString("description", description).setLong(
								"updatetime",
								new Long(-1))
						.setString("comment", comment).setLong("usergroup_id",
								usergroup_id.longValue()).executeUpdate();
				res = "Success" + updatedEntities;
				tx.commit();
				HibernateUtil.closeSession(idf);
			} catch (HibernateException ex) {
				log.error("updateGroup",ex);
			} catch (Exception ex2) {
				log.error("updateGroup",ex2);
			}
		} else {
			res = "Error: Permission denied";
		}
		return res;
	}

	public String deleteGroup(Long user_level, Long usergroup_id) {
		String res = "UpdateGroup";
		if (checkUserLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				String hqlUpdate = "delete Usergroups where usergroup_id= :usergroup_id";
				int updatedEntities = session.createQuery(hqlUpdate).setLong(
						"usergroup_id", usergroup_id.longValue())
						.executeUpdate();
				res = "Success" + updatedEntities;
				tx.commit();
				HibernateUtil.closeSession(idf);
				deleteAllGroupUsers(usergroup_id);
			} catch (HibernateException ex) {
				log.error("deleteGroup",ex);
			} catch (Exception ex2) {
				log.error("deleteGroup",ex2);
			}
		} else {
			res = "Error: Permission denied";
		}
		return res;
	}

	public Usergroups[] getAllGroupFree(Long user_level) {
		Usergroups[] groups = new Usergroups[1];
		if (checkConfLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session
						.createQuery("select c from usergroup_id as c where c.level_id = :level_id");
				query.setLong("level_id", 1);
				int count = query.list().size();
				groups = new Usergroups[count];
				int k = 0;
				for (Iterator it2 = query.iterate(); it2.hasNext();) {
					groups[k] = (Usergroups) it2.next();
					k++;
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
				for (int vars = 0; vars < groups.length; vars++) {
					//groups[vars].setUsers(ResHandler.getUsermanagement().getUser(groups[vars].getUSER_ID()));
				}
			} catch (HibernateException ex) {
				log.error("getAllGroupFree",ex);
			} catch (Exception ex2) {
				log.error("getAllGroupFree",ex2);
			}
		} else {
			groups[0] = new Usergroups();
			groups[0].setComment("Error: Permission denied");
		}
		return groups;
	}

	public Usergroups getSingleGroupFree(Long user_level, Long usergroup_id) {
		Usergroups groups = new Usergroups();
		if (checkConfLevel(user_level)) {
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session
						.createQuery("select c from Usergroups as c where c.usergroup_id = :usergroup_id AND c.level_id = :level_id");
				query.setLong("usergroup_id", usergroup_id.longValue());
				query.setLong("level_id", 1);
				for (Iterator it2 = query.iterate(); it2.hasNext();) {
					groups = (Usergroups) it2.next();
				}
				tx.commit();
				HibernateUtil.closeSession(idf);
				//Todo: Set user
				//groups.setUsers(ResHandler.getUsermanagement().getUser(groups.getUSER_ID()));

			} catch (HibernateException ex) {
				log.error("getSingleGroupFree",ex);
			} catch (Exception ex2) {
				log.error("getSingleGroupFree",ex2);
			}
		} else {
			groups.setComment("Error: Permission denied");
		}
		return groups;
	}
}
