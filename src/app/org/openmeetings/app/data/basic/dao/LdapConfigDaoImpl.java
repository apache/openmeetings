package org.openmeetings.app.data.basic.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.basic.LdapConfig;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class LdapConfigDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(LdapConfigDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private LdapConfigDaoImpl() {
	}

	private static LdapConfigDaoImpl instance = null;
 
	public static synchronized LdapConfigDaoImpl getInstance() {
		if (instance == null) {
			instance = new LdapConfigDaoImpl();
		}
		return instance;
	}
	
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
				log.debug("addLdapConfig :1: "+UsersDaoImpl.getInstance().getUser(insertedby));
				ldapConfig.setInsertedby(UsersDaoImpl.getInstance().getUser(insertedby));
			}
			
			log.debug("addLdapConfig :2: "+insertedby);
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
	
			ldapConfig = session.merge(ldapConfig);
			Long ldapConfigId = ldapConfig.getLdapConfigId();
	
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			if (ldapConfigId > 0) {
				return ldapConfigId;
			} else {
				throw new Exception("Could not store SOAPLogin");
			}
			
		} catch (Exception ex2) {
			log.error("[addLdapConfig]: ",ex2);
		}
		return null;
	}
	

	public Long addLdapConfigByObject(LdapConfig ldapConfig) {
		try {
			
			ldapConfig.setDeleted("false");
			ldapConfig.setInserted(new Date());
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
	
			ldapConfig = session.merge(ldapConfig);
			Long ldapConfigId = ldapConfig.getLdapConfigId();
	
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			if (ldapConfigId > 0) {
				return ldapConfigId;
			} else {
				throw new Exception("Could not store SOAPLogin");
			}
			
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
				log.debug("updateLdapConfig :1: "+UsersDaoImpl.getInstance().getUser(updatedby));
				ldapConfig.setUpdatedby(UsersDaoImpl.getInstance().getUser(updatedby));
			}
			
			log.debug("updateLdapConfig :2: "+updatedby);
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
	
			ldapConfig = session.merge(ldapConfig);
			ldapConfigId = ldapConfig.getLdapConfigId();
	
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return ldapConfigId;
			
		} catch (Exception ex2) {
			log.error("[updateLdapConfig]: ",ex2);
		}
		return -1L;
	}
	
	public LdapConfig getLdapConfigById(Long ldapConfigId) {
		try {
			
			String hql = "select c from LdapConfig c " +
				       	"WHERE c.ldapConfigId = :ldapConfigId " +
				       	"AND c.deleted LIKE :deleted";
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
	
			Query query = session.createQuery(hql);
			query.setParameter("ldapConfigId", ldapConfigId);
			query.setParameter("deleted", "false");
	
			LdapConfig ldapConfig = null;
			try {
				ldapConfig = (LdapConfig) query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return ldapConfig;
			
		} catch (Exception ex2) {
			log.error("[getLdapConfigById]: ",ex2);
		}
		return null;
	}
	
	public List<LdapConfig> getLdapConfigs(int start, int max, String orderby, boolean asc) {
		try {
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<LdapConfig> cq = cb.createQuery(LdapConfig.class);
			Root<LdapConfig> c = cq.from(LdapConfig.class);
			Predicate condition = cb.equal(c.get("deleted"), "false");
			cq.where(condition);
			cq.distinct(asc);
			if (asc){
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<LdapConfig> q = session.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<LdapConfig> ll = q.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);		
			return ll;
		} catch (Exception ex2) {
			log.error("[getLdapConfigs]" ,ex2);
		}
		return null;
	}
	
	public Long selectMaxFromLdapConfig(){
		try {
			log.debug("selectMaxFromConfigurations ");
			//get all users
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery("select count(c.ldapConfigId) from LdapConfig c where c.deleted LIKE 'false'"); 
			List ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			log.debug("selectMaxFromLdapConfig"+(Long)ll.get(0));
			return (Long)ll.get(0);				
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
			
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
	
			ldapConfig = session.find(LdapConfig.class, ldapConfig.getLdapConfigId());
			session.remove(ldapConfig);
	
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return ldapConfigId;
			
		} catch (Exception ex2) {
			log.error("[deleteLdapConfigById]: ",ex2);
		}
		return null;
	}

	public List<LdapConfig> getActiveLdapConfigs() {
		try {
			log.debug("selectMaxFromConfigurations ");
			
			String hql = "select c from LdapConfig c " +
					"where c.deleted LIKE 'false' " +
					"AND c.isActive = :isActive ";
			
			//get all users
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			query.setParameter("isActive", true);
			List<LdapConfig> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return ll;				
		} catch (Exception ex2) {
			log.error("[getActiveLdapConfigs] ",ex2);
		}
		return null;
	}

	public List<LdapConfig> getLdapConfigs() {
		try {
			log.debug("selectMaxFromConfigurations ");
			
			String hql = "select c from LdapConfig c " +
					"where c.deleted LIKE 'false' ";
			
			//get all users
			Object idf = PersistenceSessionUtil.createSession();
			EntityManager session = PersistenceSessionUtil.getSession();
			EntityTransaction tx = session.getTransaction();
			tx.begin();
			Query query = session.createQuery(hql);
			List<LdapConfig> ll = query.getResultList();
			tx.commit();
			PersistenceSessionUtil.closeSession(idf);
			
			return ll;				
		} catch (Exception ex2) {
			log.error("[getActiveLdapConfigs] ",ex2);
		}
		return null;
	}	
	
}
