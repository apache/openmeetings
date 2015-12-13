/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.remote;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.LdapConfigDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.basic.LdapConfig;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author sebastianwagner
 *
 */
public class LdapConfigService {
	
	//Spring loaded Bean
	@Autowired
	private LdapConfigDao ldapConfigDaoImpl;
	@Autowired
	private SessiondataDao sessiondataDao;
    @Autowired
    private UserManager userManager;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	
	private static final Logger log = Red5LoggerFactory.getLogger(LdapConfigService.class, OpenmeetingsVariables.webAppRootKey);
	
	public Long deleteLdapConfigById(String SID, Long ldapConfigId) {
        Long users_id = sessiondataDao.checkSession(SID);
        Long user_level = userManager.getUserLevelByID(users_id);
        if (authLevelUtil.checkAdminLevel(user_level)){
        	return this.ldapConfigDaoImpl.deleteLdapConfigById(ldapConfigId);
        }
        return null;
	}
	
	public LdapConfig getLdapConfigById(String SID, Long ldapConfigId) {
        Long users_id = sessiondataDao.checkSession(SID);
        Long user_level = userManager.getUserLevelByID(users_id);
        if (authLevelUtil.checkAdminLevel(user_level)){
        	return this.ldapConfigDaoImpl.get(ldapConfigId);
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
	 * @return - the list of lgap config object being searched
	 */
	public SearchResult<LdapConfig> getLdapConfigs(String SID, int start, int max, String orderby, boolean asc){
        Long users_id = sessiondataDao.checkSession(SID);
        Long user_level = userManager.getUserLevelByID(users_id);
        if (authLevelUtil.checkAdminLevel(user_level)){
        	
        	SearchResult<LdapConfig> searchResult = new SearchResult<LdapConfig>();
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
	 * @return - id of added or updated config in case of success, error code otherwise
	 */
	public Long saveOrUpdateLdapConfig(String SID, LinkedHashMap<Object,Object> values)  {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkAdminLevel(user_level)){
			
				long ldapConfigId = Long.valueOf(
						values.get("ldapConfigId").toString()).longValue();
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
