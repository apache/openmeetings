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
