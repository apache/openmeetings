package org.openmeetings.app.remote;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.basic.dao.LdapConfigDaoImpl;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.hibernate.beans.basic.LdapConfig;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

/**
 * 
 * @author sebastianwagner
 *
 */
public class LdapConfigService {
	
	//Spring loaded Bean
	private LdapConfigDaoImpl ldapConfigDaoImpl;
	
	private static final Logger log = Red5LoggerFactory.getLogger(LdapConfigService.class, ScopeApplicationAdapter.webAppRootKey);
	
	public LdapConfigDaoImpl getLdapConfigDaoImpl() {
		return ldapConfigDaoImpl;
	}
	public void setLdapConfigDaoImpl(LdapConfigDaoImpl ldapConfigDaoImpl) {
		this.ldapConfigDaoImpl = ldapConfigDaoImpl;
	}

	public Long deleteLdapConfigById(String SID, Long ldapConfigId) {
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
        	return this.ldapConfigDaoImpl.deleteLdapConfigById(ldapConfigId);
        }
        return null;
	}
	
	public LdapConfig getLdapConfigById(String SID, Long ldapConfigId) {
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
        	return this.ldapConfigDaoImpl.getLdapConfigById(ldapConfigId);
        }
        return null;
	}
	
	public List<LdapConfig> getActiveLdapConfigs() {
		try {
			List<LdapConfig> ldapConfigs = this.ldapConfigDaoImpl.getActiveLdapConfigs();
			
			//Add localhost Domain
			LdapConfig ldapConfig = new LdapConfig();
			
			ldapConfig.setName("local DB [internal]");
			ldapConfig.setLdapConfigId(-1);
			
			List<LdapConfig> returnldapConfigs = new LinkedList<LdapConfig>();
			returnldapConfigs.add(ldapConfig);
			
			for (LdapConfig ldapConfigStored : ldapConfigs) {
				returnldapConfigs.add(ldapConfigStored);
			}
			
	        return returnldapConfigs;
	        
		} catch (Exception err) {
			log.error("[getActiveLdapConfigs]",err);
		}
		return null;
	}
	
	/**
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @param language_id
	 * @return
	 */
	public SearchResult getLdapConfigs(String SID, int start, int max, String orderby, boolean asc){
        Long users_id = Sessionmanagement.getInstance().checkSession(SID);
        Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
        if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
        	
        	SearchResult searchResult = new SearchResult();
        	searchResult.setObjectName(LdapConfig.class.getName());
        	searchResult.setResult(this.ldapConfigDaoImpl.getLdapConfigs(start, max, orderby, asc));
        	searchResult.setRecords(this.ldapConfigDaoImpl.selectMaxFromLdapConfig());
        	
        	return searchResult;
        }
		return null;
	}
	
	/**
	 * 
	 * @param SID
	 * @param values
	 * @return
	 */
	public Long saveOrUpdateLdapConfig(String SID, LinkedHashMap<Object,Object> values)  {
		try {
			Long users_id = Sessionmanagement.getInstance().checkSession(SID);
			Long user_level = Usermanagement.getInstance().getUserLevelByID(users_id);
			if (AuthLevelmanagement.getInstance().checkAdminLevel(user_level)){
			
				Long ldapConfigId = Long.valueOf(values.get("ldapConfigId").toString()).longValue();
				Boolean addDomainToUserName = Boolean.valueOf(values.get("addDomainToUserName").toString()).booleanValue();
				String configFileName = values.get("configFileName").toString(); 
				String name = values.get("tName").toString(); 
				String domain = values.get("domain").toString(); 
				Boolean isActive = Boolean.valueOf(values.get("isActive").toString()).booleanValue();
			
				if (ldapConfigId<=0) {
					return this.ldapConfigDaoImpl.addLdapConfig(name, addDomainToUserName, configFileName, 
								domain, users_id, isActive);
				} else {
					return this.ldapConfigDaoImpl.updateLdapConfig(ldapConfigId, name, addDomainToUserName, 
								configFileName, domain, users_id, isActive);
				}
				
			}
			return new Long(-26);	
		} catch (Exception e) {
			log.error("[saveOrUpdateLdapConfig]",e);
		}
		return new Long(-1);	
	}	

}
