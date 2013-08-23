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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.logs.ConferenceLogType;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ConferenceLogTypeDao {

	private static final Logger log = Red5LoggerFactory.getLogger(ConferenceLogTypeDao.class, OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;

	public Long addConferenceLogType(String eventType) {
		try {
			ConferenceLogType confLogType = new ConferenceLogType();
			confLogType.setEventType(eventType);
			confLogType.setInserted(new Date());
			confLogType = em.merge(confLogType);
			return confLogType.getConferenceLogTypeId();
		} catch (Exception ex2) {
			log.error("[addConferenceLogType]: ", ex2);
		}
		return null;
	}
	
	public ConferenceLogType getConferenceLogTypeByEventName(String eventType) {
		try {
			TypedQuery<ConferenceLogType> query = em.createNamedQuery("getConferenceLogTypeByEventName", ConferenceLogType.class);
			query.setParameter("eventType",eventType);
			
			//Seems like this does throw an error sometimes cause it does not return a unique Result
			//ConferenceLogType confLogType = (ConferenceLogType) query.getSingleResult();
			List<ConferenceLogType> confLogTypes = query.getResultList();
			if (confLogTypes != null && confLogTypes.size() > 0) {
				return confLogTypes.get(0);
			}
		} catch (Exception ex2) {
			log.error("[getConferenceLogTypeByEventName]: ", ex2);
		}
		return null;
	}
	
}
