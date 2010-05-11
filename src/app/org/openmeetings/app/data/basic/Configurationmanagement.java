package org.openmeetings.app.data.basic;

import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.basic.Configuration;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.openmeetings.utils.mappings.CastMapToObject;

public class Configurationmanagement {

	private static final Logger log = Red5LoggerFactory.getLogger(Configurationmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	private Configurationmanagement() {
	}

	private static Configurationmanagement instance = null;

	public static synchronized Configurationmanagement getInstance() {
		if (instance == null) {
			instance = new Configurationmanagement();
		}

		return instance;
	}
 
	public Configuration getConfKey(long user_level, String CONF_KEY) {
		try {
			if (AuthLevelmanagement.getInstance().checkUserLevel(user_level)) {
				Configuration configuration = null;
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery("select c from Configuration as c where c.conf_key = :conf_key and c.deleted = :deleted");
				query.setString("conf_key", CONF_KEY);
				query.setString("deleted", "false");
				
				List<Configuration> configs = query.list();
				
				if (configs != null && configs.size() > 0) {
					configuration = (Configuration) configs.get(0);
				}
				
				tx.commit();
				HibernateUtil.closeSession(idf);
				return configuration;
			} else {
				log.error("[getAllConf] Permission denied "+user_level);
			}
		} catch (HibernateException ex) {
			log.error("[getConfKey]: " ,ex);
		} catch (Exception ex2) {
			log.error("[getConfKey]: " ,ex2);
		}		
		return null;
	}
	
	public Configuration getConfByConfigurationId(long user_level, long configuration_id) {
		try {
			log.debug("getConfByConfigurationId1: user_level "+user_level);
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
				Configuration configuration = null;
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				Query query = session.createQuery("select c from Configuration as c where c.configuration_id = :configuration_id");
				query.setLong("configuration_id", configuration_id);
				query.setMaxResults(1);
				configuration = (Configuration) query.uniqueResult();
				tx.commit();
				HibernateUtil.closeSession(idf);
				log.debug("getConfByConfigurationId4: "+configuration);
				
				if (configuration!=null && configuration.getUser_id()!=null) {
					configuration.setUsers(UsersDaoImpl.getInstance().getUser(configuration.getUser_id()));
				}
				return configuration;
			} else {
				log.error("[getConfByConfigurationId] Permission denied "+user_level);
			}
		} catch (HibernateException ex) {
			log.error("[getConfByConfigurationId]: " ,ex);
		} catch (Exception ex2) {
			log.error("[getConfByConfigurationId]: " ,ex2);
		}		
		return null;
	}

	public SearchResult getAllConf(long user_level, int start ,int max, String orderby, boolean asc) {
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
				SearchResult sresult = new SearchResult();
				sresult.setRecords(this.selectMaxFromConfigurations());
				sresult.setResult(this.getConfigurations(start, max, orderby, asc));
				sresult.setObjectName(Configuration.class.getName());
				return sresult;
			} else {
				log.error("[getAllConf] Permission denied "+user_level);
			}
		} catch (HibernateException ex) {
			log.error("[getAllConf]: " ,ex);
		} catch (Exception ex2) {
			log.error("[getAllConf]: " ,ex2);
		}		
		return null;
	}
	
	public List getConfigurations(int start, int max, String orderby, boolean asc) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(Configuration.class, ScopeApplicationAdapter.webAppRootKey);
			crit.add(Restrictions.eq("deleted", "false"));
			crit.setFirstResult(start);
			crit.setMaxResults(max);
			if (asc) crit.addOrder(Order.asc(orderby));
			else crit.addOrder(Order.desc(orderby));
			List ll = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);		
			return ll;
		} catch (HibernateException ex) {
			log.error("[getConfigurations]" ,ex);
		} catch (Exception ex2) {
			log.error("[getConfigurations]" ,ex2);
		}
		return null;
	}
	
	
	/**
	 * 
	 * @return
	 */
	private Long selectMaxFromConfigurations(){
		try {
			log.debug("selectMaxFromConfigurations ");
			//get all users
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("select max(c.configuration_id) from Configuration c where c.deleted = 'false'"); 
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			log.debug("selectMaxFromConfigurations"+(Long)ll.get(0));
			return (Long)ll.get(0);				
		} catch (HibernateException ex) {
			log.error("[selectMaxFromConfigurations] ",ex);
		} catch (Exception ex2) {
			log.error("[selectMaxFromConfigurations] ",ex2);
		}
		return null;
	}		

	public String addConfByKey(long user_level, String CONF_KEY,
			String CONF_VALUE, Long USER_ID, String comment) {
		String ret = "Add Configuration";
		if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
			Configuration configuration = new Configuration();
			configuration.setConf_key(CONF_KEY);
			configuration.setConf_value(CONF_VALUE);
			configuration.setStarttime(new Date());
			configuration.setDeleted("false");
			configuration.setComment(comment);
			if (USER_ID!=null) configuration.setUser_id(USER_ID);
			try {
				Object idf = HibernateUtil.createSession();
				Session session = HibernateUtil.getSession();
				Transaction tx = session.beginTransaction();
				session.save(configuration);
				session.flush();
				session.clear();
				session.refresh(configuration);
				tx.commit();
				HibernateUtil.closeSession(idf);
				ret = "Erfolgreich";
			} catch (HibernateException ex) {
				log.error("[addConfByKey]: " ,ex);
			} catch (Exception ex2) {
				log.error("[addConfByKey]: " ,ex2);
			}
		} else {
			ret = "Error: Permission denied";
		}
		return ret;
	}
	
	public Long saveOrUpdateConfiguration(long user_level, LinkedHashMap values, Long users_id) {
		try {
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
				Configuration conf = (Configuration) CastMapToObject.getInstance().castByGivenObject(values, Configuration.class);
				if (conf.getConfiguration_id().equals(null) || conf.getConfiguration_id() == 0 ){
					log.info("add new Configuration");
					conf.setStarttime(new Date());
					conf.setDeleted("false");
					return this.addConfig(conf);
				} else {
					log.info("update Configuration ID: "+conf.getConfiguration_id());
					Configuration conf2 = this.getConfByConfigurationId(3L, conf.getConfiguration_id());
					conf2.setComment(conf.getComment());
					conf2.setConf_key(conf.getConf_key());
					conf2.setConf_value(conf.getConf_value());
					conf2.setUser_id(users_id);
					conf2.setDeleted("false");
					conf2.setUpdatetime(new Date());
					return this.updateConfig(conf2);
				}
			} else {
				log.error("[saveOrUpdateConfByConfigurationId] Error: Permission denied");
				return new Long(-100);
			}				
		} catch (HibernateException ex) {
			log.error("[updateConfByUID]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updateConfByUID]: " ,ex2);
		}
		return new Long(-1);
	}

	public Long addConfig(Configuration conf){
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Long configuration_id = (Long) session.save(conf);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return configuration_id;
		} catch (HibernateException ex) {
			log.error("[updateConfByUID]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updateConfByUID]: " ,ex2);
		}
		return new Long(-1);		
	}
	
	public Long updateConfig(Configuration conf){
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(conf);
			tx.commit();
			HibernateUtil.closeSession(idf);
			return conf.getConfiguration_id();
		} catch (HibernateException ex) {
			log.error("[updateConfByUID]: " ,ex);
		} catch (Exception ex2) {
			log.error("[updateConfByUID]: " ,ex2);
		}
		return new Long(-1);		
	}	

	public Long deleteConfByConfiguration(long user_level, LinkedHashMap values, Long users_id) {
		try {	
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)) {
				Configuration conf = (Configuration) CastMapToObject.getInstance().castByGivenObject(values, Configuration.class);
				conf.setUsers(UsersDaoImpl.getInstance().getUser(users_id));
				conf.setUpdatetime(new Date());
				conf.setDeleted("true");
				
				Configuration conf2 = this.getConfByConfigurationId(3L, conf.getConfiguration_id());
				conf2.setComment(conf.getComment());
				conf2.setConf_key(conf.getConf_key());
				conf2.setConf_value(conf.getConf_value());
				conf2.setUser_id(users_id);
				conf2.setDeleted("true");
				conf2.setUpdatetime(new Date());
				
				this.updateConfig(conf2);
				return new Long(1);
			} else {
				log.error("Error: Permission denied");
				return new Long(-100);
			}
		} catch (HibernateException ex) {
			log.error("[deleteConfByUID]: " ,ex);
		} catch (Exception ex2) {
			log.error("[deleteConfByUID]: " ,ex2);
		}		
		return new Long(-1);
	}
}
