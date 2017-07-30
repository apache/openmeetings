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
package org.apache.openmeetings.core.remote;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.List;

import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides method to manipulate {@link User}
 *
 * @author sebawagner
 *
 */
public class UserService {
	private static final Logger log = Red5LoggerFactory.getLogger(UserService.class, webAppRootKey);

	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private UserDao userDao;

	/**
	 * get user by id, admin only
	 *
	 * @param sid
	 * @param userId
	 * @return User with the id given
	 */
	public User getUserById(String sid, long userId) {
		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
			return userDao.get(userId);
		}
		return null;
	}

	/**
	 * refreshes the current SID
	 *
	 * @param SID
	 * @return "ok" string in case of success, "error" string in case of the error
	 */
	public String refreshSession(String SID) {
		try {
			sessionDao.check(SID);
			return "ok";
		} catch (Exception err) {
			log.error("[refreshSession]", err);
		}
		return "error";
	}

	/**
	 * gets a whole user-list(admin-role only)
	 *
	 * @param sid
	 * @param start
	 * @param max
	 * @param orderby
	 * @return whole user-list
	 */
	public List<User> getUserList(String sid, int start, int max, String orderby, boolean asc) {
		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(sd.getUserId()))) {
			return userDao.get("", start, max, orderby + (asc ? " ASC" : " DESC"));
		}
		return null;
	}
}
