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

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.LdapConfigDao;
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
	
	public List<LdapConfig> getActiveLdapConfigs() {
		try {
			List<LdapConfig> ldapConfigs = this.ldapConfigDaoImpl.getActiveLdapConfigs();
			
			//Add localhost Domain
			LdapConfig ldapConfig = new LdapConfig();
			
			ldapConfig.setName("local DB [internal]");
			ldapConfig.setLdapConfigId(-1);
			
			List<LdapConfig> returnldapConfigs = new ArrayList<LdapConfig>();
			returnldapConfigs.add(ldapConfig);
			returnldapConfigs.addAll(ldapConfigs);
			
	        return returnldapConfigs;
		} catch (Exception err) {
			log.error("[getActiveLdapConfigs]",err);
		}
		return null;
	}
	
}
