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
package org.openmeetings.app.data.user.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.persistence.beans.user.UserSipData;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserSipDataDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(UserSipDataDaoImpl.class, OpenmeetingsVariables.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public UserSipData getUserSipDataById(Long userSipDataId) {
		try {
			
			if (userSipDataId == null) {
				return null;
			}
			
			String hql = "select c from UserSipData as c where c.userSipDataId = :userSipDataId";
			
			TypedQuery<UserSipData> query = em.createQuery(hql, UserSipData.class);
			query.setParameter("userSipDataId", userSipDataId);
			UserSipData userSipData = null;
			try {
				userSipData = query.getSingleResult();
		    } catch (NoResultException ex) {
		    }
			
			return userSipData;
		} catch (Exception ex2) {
			log.error("getUserSipDataById",ex2);
		}
		return null;
	}

	public Long addUserSipData(UserSipData userSipData) {
		try {
			
			if (userSipData == null) {
				return null;
			}
			
			userSipData.setInserted(new Date());
			
			userSipData = em.merge(userSipData);
			Long userSipDataId = userSipData.getUserSipDataId();
			
			return userSipDataId;
		} catch (Exception ex2) {
			log.error("[addUserSipData] ",ex2);
		}
		return null;
	}
	
	public Long updateUserSipData(UserSipData userSipData) {
		try {
			
			if (userSipData == null) {
				return null;
			}
			
			userSipData.setUpdated(new Date());
			
			if (userSipData.getUserSipDataId() == 0) {
				em.persist(userSipData);
		    } else {
		    	if (!em.contains(userSipData)) {
		    		em.merge(userSipData);
			    }
			}
			
			return userSipData.getUserSipDataId();
		} catch (Exception ex2) {
			log.error("[updateUserSipData] ",ex2);
		}
		return null;
	}	
	
}
