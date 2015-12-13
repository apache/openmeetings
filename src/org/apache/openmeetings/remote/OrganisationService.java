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
import java.util.List;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.user.User;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class OrganisationService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			OrganisationService.class, OpenmeetingsVariables.webAppRootKey);
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private OrganisationManager organisationManager;
	@Autowired
	private AuthLevelUtil authLevelUtil;

	/**
	 * Loads a List of all available Organizations (Admin-role only)
	 * 
	 * @param SID
	 * @return - List of all available Organizations
	 */
	public SearchResult<Organisation> getOrganisations(String SID, int start, int max,
			String orderby, boolean asc) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			long user_level = userManager.getUserLevelByID(users_id);
			return organisationManager.getOrganisations(user_level, start,
					max, orderby, asc);
		} catch (Exception e) {
			log.error("getOrganisations", e);
		}
		return null;
	}

	public List<Organisation> getAllOrganisations(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			return organisationManager.getOrganisations(user_level);
		} catch (Exception e) {
			log.error("getAllOrganisations", e);
		}
		return null;
	}

	/**
	 * get an organisation by a given id
	 * 
	 * @param SID
	 * @param organisation_id
	 * @return - organisation with given id
	 */
	public Organisation getOrganisationById(String SID, long organisation_id) {
		Long users_id = sessiondataDao.checkSession(SID);
		long user_level = userManager.getUserLevelByID(users_id);
		return organisationManager.getOrganisationById(user_level,
				organisation_id);
	}

	/**
	 * deletes a organisation by a given id
	 * 
	 * @param SID
	 * @param organisation_id
	 * @return - id of organisation in case of success, null otherwise
	 */
	public Long deleteOrganisation(String SID, long organisation_id) {
		Long users_id = sessiondataDao.checkSession(SID);
		long user_level = userManager.getUserLevelByID(users_id);
		return organisationManager.deleteOrganisation(user_level,
				organisation_id, users_id);
	}

	/**
	 * adds or updates an Organisation
	 * 
	 * @param SID
	 * @param regObjectObj
	 * @return - id of added or updated organisation in case of success, null otherwise
	 */
	public Long saveOrUpdateOrganisation(String SID, Object regObjectObj) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			long user_level = userManager.getUserLevelByID(users_id);
			@SuppressWarnings("rawtypes")
			LinkedHashMap<?, ?> argObjectMap = (LinkedHashMap) regObjectObj;
			long organisation_id = Long.valueOf(
					argObjectMap.get("organisation_id").toString()).longValue();
			if (organisation_id == 0) {
				return organisationManager.addOrganisation(user_level,
						argObjectMap.get("orgname").toString(), users_id);
			} else {
				return organisationManager.updateOrganisation(user_level,
						organisation_id,
						argObjectMap.get("orgname").toString(), users_id);
			}
		} catch (Exception err) {
			log.error("saveOrUpdateOrganisation", err);
		}
		return null;

	}

	/**
	 * gets all users of a given organisation
	 * 
	 * @param SID
	 * @param organisation_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return all users of a given organisation
	 */
	public SearchResult<User> getUsersByOrganisation(String SID,
			long organisation_id, int start, int max, String orderby,
			boolean asc) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkAdminLevel(user_level)) {
				return organisationManager
						.getUsersSearchResultByOrganisationId(organisation_id,
								start, max, orderby, asc);
			} else {
				log.error("Need Administration Account");
				SearchResult<User> sResult = new SearchResult<User>();
				sResult.setErrorId(-26L);
				return sResult;
			}
		} catch (Exception err) {
			log.error("getUsersByOrganisation", err);
		}
		return null;
	}

	public Long addUserToOrganisation(String SID, Long organisation_id,
			Long user_id) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			if (authLevelUtil.checkAdminLevel(user_level)) {
				return organisationManager.addUserToOrganisation(user_id,
						organisation_id, users_id);
			} else {
				return -26L;
			}
		} catch (Exception err) {
			log.error("getUsersByOrganisation", err);
		}
		return null;
	}

	public Long deleteUserFromOrganisation(String SID, Long organisation_id,
			Long user_id, String comment) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			return organisationManager.deleteUserFromOrganisation(
					user_level, user_id, organisation_id);
		} catch (Exception err) {
			log.error("getUsersByOrganisation", err);
		}
		return null;
	}

}
