package org.openmeetings.app.data.basic.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.hibernate.beans.basic.LdapConfig;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class LdapConfigDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(LdapConfigDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	public Long addLdapConfig(String name, Boolean addDomainToUserName, String configFileName, 
			String domain, Long insertedby, Boolean isActive) {
		try {
			
			LdapConfig ldapConfig = new LdapConfig();
			ldapConfig.setAddDomainToUserName(addDomainToUserName);
			ldapConfig.setConfigFileName(configFileName);
			ldapConfig.setDeleted("false");
			ldapConfig.setDomain(domain);
			ldapConfig.setIsActive(isActive);
			ldapConfig.setName(name);
			ldapConfig.setInserted(new Date());
			if (insertedby != null) {
				ldapConfig.setInsertedby(UsersDaoImpl.getInstance().getUser(insertedby));
			}
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
	
			Long ldapConfigId = (Long) session.save(ldapConfig);
	
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (ldapConfigId > 0) {
				return ldapConfigId;
			} else {
				throw new Exception("Could not store SOAPLogin");
			}
			
		} catch (HibernateException ex) {
			log.error("[addLdapConfig]: ",ex);
		} catch (Exception ex2) {
			log.error("[addLdapConfig]: ",ex2);
		}
		return null;
	}
	
	public Long updateLdapConfig(Long ldapConfigId, String name, Boolean addDomainToUserName, 
			String configFileName, String domain, Long updatedby, Boolean isActive) {
		try {
			
			LdapConfig ldapConfig = this.getLdapConfigById(ldapConfigId);
			
			if (ldapConfig == null) {
				return -1L;
			}
			
			ldapConfig.setAddDomainToUserName(addDomainToUserName);
			ldapConfig.setConfigFileName(configFileName);
			ldapConfig.setDeleted("false");
			ldapConfig.setDomain(domain);
			ldapConfig.setIsActive(isActive);
			ldapConfig.setName(name);
			ldapConfig.setUpdated(new Date());
			if (updatedby != null) {
				ldapConfig.setUpdatedby(UsersDaoImpl.getInstance().getUser(updatedby));
			}
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
	
			session.update(ldapConfig);
	
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ldapConfigId;
			
		} catch (HibernateException ex) {
			log.error("[updateLdapConfig]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateLdapConfig]: ",ex2);
		}
		return -1L;
	}
	
	public LdapConfig getLdapConfigById(Long ldapConfigId) {
		try {
			
			String hql = "Select * From LdapConfig c " +
				       	"WHERE c.ldapConfigId = :ldapConfigId " +
				       	"AND c.deleted LIKE :deleted";
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
	
			Query query = session.createQuery(hql);
			query.setLong("ldapConfigId", ldapConfigId);
			query.setString("deleted", "false");
	
			
			LdapConfig ldapConfig = (LdapConfig) query.uniqueResult();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ldapConfig;
			
		} catch (HibernateException ex) {
			log.error("[getLdapConfigById]: ",ex);
		} catch (Exception ex2) {
			log.error("[getLdapConfigById]: ",ex2);
		}
		return null;
	}
	
	public List<LdapConfig> getLdapConfigs(int start, int max, String orderby, boolean asc) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(LdapConfig.class);
			crit.add(Restrictions.eq("deleted", "false"));
			crit.setFirstResult(start);
			crit.setMaxResults(max);
			if (asc) crit.addOrder(Order.asc(orderby));
			else crit.addOrder(Order.desc(orderby));
			List<LdapConfig> ll = crit.list();
			tx.commit();
			HibernateUtil.closeSession(idf);		
			return ll;
		} catch (HibernateException ex) {
			log.error("[getLdapConfigs]" ,ex);
		} catch (Exception ex2) {
			log.error("[getLdapConfigs]" ,ex2);
		}
		return null;
	}
	
	public Long selectMaxFromLdapConfig(){
		try {
			log.debug("selectMaxFromConfigurations ");
			//get all users
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("select count(c.ldapConfigId) from LdapConfig c where c.deleted LIKE 'false'"); 
			List ll = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			log.debug("selectMaxFromLdapConfig"+(Long)ll.get(0));
			return (Long)ll.get(0);				
		} catch (HibernateException ex) {
			log.error("[selectMaxFromLdapConfig] ",ex);
		} catch (Exception ex2) {
			log.error("[selectMaxFromLdapConfig] ",ex2);
		}
		return null;
	}	
	
	public Long deleteLdapConfigById(Long ldapConfigId) {
		try {
			
			LdapConfig ldapConfig = this.getLdapConfigById(ldapConfigId);
			
			if (ldapConfig == null) {
				return null;
			}
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
	
			session.delete(ldapConfig);
	
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return ldapConfigId;
			
		} catch (HibernateException ex) {
			log.error("[deleteLdapConfigById]: ",ex);
		} catch (Exception ex2) {
			log.error("[deleteLdapConfigById]: ",ex2);
		}
		return null;
	}
	
}
