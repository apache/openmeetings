package org.openmeetings.app.data.basic.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.basic.LdapConfig;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LdapConfigDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(LdapConfigDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private UsersDaoImpl usersDao;
	
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
				log.debug("addLdapConfig :1: "+usersDao.getUser(insertedby));
				ldapConfig.setInsertedby(usersDao.getUser(insertedby));
			}
			
			log.debug("addLdapConfig :2: "+insertedby);
			
			ldapConfig = em.merge(ldapConfig);
			Long ldapConfigId = ldapConfig.getLdapConfigId();
			
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
			
			ldapConfig = em.merge(ldapConfig);
			Long ldapConfigId = ldapConfig.getLdapConfigId();
			
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
				log.debug("updateLdapConfig :1: "+usersDao.getUser(updatedby));
				ldapConfig.setUpdatedby(usersDao.getUser(updatedby));
			}
			
			log.debug("updateLdapConfig :2: "+updatedby);
			
			ldapConfig = em.merge(ldapConfig);
			ldapConfigId = ldapConfig.getLdapConfigId();
			
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
			
			TypedQuery<LdapConfig> query = em.createQuery(hql, LdapConfig.class);
			query.setParameter("ldapConfigId", ldapConfigId);
			query.setParameter("deleted", "false");
	
			LdapConfig ldapConfig = null;
			try {
				ldapConfig = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
			return ldapConfig;
			
		} catch (Exception ex2) {
			log.error("[getLdapConfigById]: ",ex2);
		}
		return null;
	}
	
	public List<LdapConfig> getLdapConfigs(int start, int max, String orderby, boolean asc) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
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
			TypedQuery<LdapConfig> q = em.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<LdapConfig> ll = q.getResultList();
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
			TypedQuery<Long> query = em.createQuery("select count(c.ldapConfigId) from LdapConfig c where c.deleted LIKE 'false'", Long.class); 
			List<Long> ll = query.getResultList();
			log.debug("selectMaxFromLdapConfig" + ll.get(0));
			return ll.get(0);
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
			
			ldapConfig = em.find(LdapConfig.class, ldapConfig.getLdapConfigId());
			em.remove(ldapConfig);
			
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
			TypedQuery<LdapConfig> query = em.createQuery(hql, LdapConfig.class);
			query.setParameter("isActive", true);
			List<LdapConfig> ll = query.getResultList();
			
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
			TypedQuery<LdapConfig> query = em.createQuery(hql, LdapConfig.class);
			List<LdapConfig> ll = query.getResultList();
			
			return ll;				
		} catch (Exception ex2) {
			log.error("[getActiveLdapConfigs] ",ex2);
		}
		return null;
	}	
	
}
