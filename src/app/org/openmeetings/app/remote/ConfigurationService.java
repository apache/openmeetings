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
package org.openmeetings.app.remote;

import java.util.LinkedHashMap;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 *
 */
public class ConfigurationService {
	@Autowired
	private Sessionmanagement sessionManagement;
	@Autowired
	private Configurationmanagement cfgManagement;
    @Autowired
    private Usermanagement userManagement;
	
	/*
	 * Configuration Handlers
	 */    
    public SearchResult<Configuration> getAllConf(String SID, int start ,int max, String orderby, boolean asc){
        Long users_id = sessionManagement.checkSession(SID);
        Long user_level = userManagement.getUserLevelByID(users_id);     	
        return cfgManagement.getAllConf(user_level, start, max, orderby, asc);
    }
    
    public Configuration getConfByConfigurationId(String SID,long configuration_id){
        Long users_id = sessionManagement.checkSession(SID);
        Long user_level = userManagement.getUserLevelByID(users_id);     	
        return cfgManagement.getConfByConfigurationId(user_level,configuration_id);
    }
    
    public Long saveOrUpdateConfiguration(String SID,@SuppressWarnings("rawtypes") LinkedHashMap values){
        Long users_id = sessionManagement.checkSession(SID);
        Long user_level = userManagement.getUserLevelByID(users_id);     	
        return cfgManagement.saveOrUpdateConfiguration(user_level,values, users_id);
    }
    
    public Long deleteConfiguration(String SID,@SuppressWarnings("rawtypes") LinkedHashMap values){
        Long users_id = sessionManagement.checkSession(SID);
        Long user_level = userManagement.getUserLevelByID(users_id);     	
        return cfgManagement.deleteConfByConfiguration(user_level, values, users_id);
    }
	    
}
