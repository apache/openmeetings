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
package org.apache.openmeetings.data.logs;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.logs.ConferenceLog;
import org.apache.openmeetings.persistence.beans.logs.ConferenceLogType;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ConferenceLogDao {

	private static final Logger log = Red5LoggerFactory.getLogger(ConferenceLogDao.class, OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ConferenceLogTypeDao conferenceLogTypeDao;

	public Long addConferenceLog(String eventType, Long userId, String streamid, 
			Long room_id, String userip, String scopeName, 
			String externalUserId, String externalUserType, String email,
			String firstname, String lastname) {
		try {
			
			ConferenceLogType confLogType = conferenceLogTypeDao.getConferenceLogTypeByEventName(eventType);
			if (confLogType == null) {
				conferenceLogTypeDao.addConferenceLogType(eventType);
				confLogType = conferenceLogTypeDao.getConferenceLogTypeByEventName(eventType);
			}
			
			ConferenceLog confLog = new ConferenceLog();
			confLog.setConferenceLogType(confLogType);
			confLog.setInserted(new Date());
			confLog.setUserId(userId);
			confLog.setStreamid(streamid);
			confLog.setScopeName(scopeName);
			confLog.setRoom_id(room_id);
			confLog.setUserip(userip);
			confLog.setExternalUserId(externalUserId);
			confLog.setExternalUserType(externalUserType);
			confLog.setFirstname(firstname);
			confLog.setLastname(lastname);
			confLog.setEmail(email);
			
			confLog = em.merge(confLog);
			Long confLogId = confLog.getConferenceLogId();

			return confLogId;
		} catch (Exception ex2) {
			log.error("[addConferenceLog]: ",ex2);
		}
		return null;
	}
	
	
}
