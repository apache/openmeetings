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

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;

import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.basic.Configuration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Remotely available via RTMP
 * 
 * @author swagner
 * 
 */
public class ConfigurationService {
	private static final Logger log = Red5LoggerFactory.getLogger(ConfigurationService.class, webAppRootKey);

	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
    @Autowired
    private UserManager userManager;
	
	/*
	 * Configuration Handlers
	 */    
    public SearchResult<Configuration> getAllConf(String SID, int start ,int max, String orderby, boolean asc){
        Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkAdminLevel(user_level)) {
			return configurationDao.getAllConf(start, max, orderby, asc);
		} else {
			log.error("[getConfByConfigurationId] Permission denied "
					+ user_level);
		}
		return null;
    }
    
	/**
	 * Get a configuration by id, users object is lazy loaded
	 * 
	 * @param SID
	 * @param configuration_id
	 * @return - configuration with the id given, null otherwise
	 */
    public Configuration getConfByConfigurationId(String SID,long configuration_id){
        Long users_id = sessiondataDao.checkSession(SID);
        Long user_level = userManager.getUserLevelByID(users_id);     	
		if (authLevelUtil.checkAdminLevel(user_level)) {
			return configurationDao.get(configuration_id);
		} else {
			log.error("[getConfByConfigurationId] Permission denied "
					+ user_level);
		}
		return null;
    }
    
    /**
	 * Save or update a configuration
     * 
     * @param SID
     * @param id
     * @param key
     * @param val
     * @param comment
	 * @return - id of configuration being updated, null otherwise
     */
    public Long saveOrUpdateConfiguration(String SID, Long id, String key, String val, String comment){
        Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkAdminLevel(user_level)) {
			Configuration c = configurationDao.forceGet(key);
			if (c != null && !c.getConfiguration_id().equals(id)) {
				return -56L;
			}
			if (c == null) {
				c = new Configuration();
				c.setConf_key(key);
			}
			c.setComment(comment);
			c.setConf_value(val);
			return configurationDao.update(c, users_id).getConfiguration_id();
		} else {
			log.error("[saveOrUpdateConfiguration] Permission denied " + user_level);
		}
		return null;
    }
    
    /**
	 * delete a configuration
     * 
     * @param SID
     * @param id
	 * @return - id of configuration being deleted in case of success, null otherwise
     */
    public Long deleteConfiguration(String SID, Long id){
        Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		if (authLevelUtil.checkAdminLevel(user_level)) {
			Configuration c = configurationDao.get(id);
			if (c != null) {
				configurationDao.delete(c, users_id);
			}
			return id;
		} else {
			log.error("[deleteConfiguration] Permission denied "
					+ user_level);
		}
		return null;
    }
	    
}
