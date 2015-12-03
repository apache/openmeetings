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
import static org.apache.openmeetings.util.OpenmeetingsVariables.wicketApplicationName;

import java.util.HashMap;
import java.util.List;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserService;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.wicket.Application;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides method to manipulate {@link User}
 * 
 * @author sebawagner
 * 
 */
public class UserService implements IUserService {
	private static final Logger log = Red5LoggerFactory.getLogger(UserService.class, webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ServerDao serverDao;
	@Autowired
	private ISlaveHTTPConnectionManager slaveHTTPConnectionManager;

	/**
	 * get user by id, admin only
	 * 
	 * @param SID
	 * @param userId
	 * @return User with the id given
	 */
	public User getUserById(String SID, long userId) {
		Long authUserId = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(authUserId))) {
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
			sessiondataDao.checkSession(SID);
			return "ok";
		} catch (Exception err) {
			log.error("[refreshSession]", err);
		}
		return "error";
	}

	/**
	 * gets a whole user-list(admin-role only)
	 * 
	 * @param SID
	 * @param start
	 * @param max
	 * @param orderby
	 * @return whole user-list
	 */
	public List<User> getUserList(String SID, int start, int max, String orderby, boolean asc) {
		Long userId = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(userId))) {
			return userDao.get("", start, max, orderby + (asc ? " ASC" : " DESC"));
		}
		return null;
	}

	/**
	 * kicks a user from the server, also from slaves if needed, this method is
	 * only invoked by the connection administration UI
	 * 
	 * @param SID
	 * @param streamid
	 * @param serverId
	 *            0 means the session is locally, otherwise we have to perform a
	 *            REST call
	 * @return - true if user has sufficient permissions, false otherwise
	 */
	public Boolean kickUserByStreamId(String SID, String streamid, long serverId) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			// admins only
			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(userId))) {
				if (serverId == 0) {
					Client rcl = sessionManager.getClientByStreamId(streamid, null);

					if (rcl == null) {
						return true;
					}
					String scopeName = "hibernate";
					if (rcl.getRoomId() != null) {
						scopeName = rcl.getRoomId().toString();
					}
					IScope currentScope = scopeApplicationAdapter.getRoomScope(scopeName);

					HashMap<Integer, String> messageObj = new HashMap<Integer, String>();
					messageObj.put(0, "kick");
					scopeApplicationAdapter.sendMessageById(messageObj, streamid, currentScope);

					scopeApplicationAdapter.roomLeaveByScope(rcl, currentScope, true);

					return true;
				} else {
					Server server = serverDao.get(serverId);
					Client rcl = sessionManager.getClientByStreamId(
							streamid, server);
					slaveHTTPConnectionManager.kickSlaveUser(server, rcl.getPublicSID());
					
					// true means only the REST call is performed, it is no
					// confirmation that the user is really kicked from the
					// slave
					return true;
				}
			}
		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return false;
	}

	/**
	 * Kick a user by its publicSID.<br/>
	 * <br/>
	 * <i>Note:</i>
	 * This method will not perform a call to the slave, cause this call can only be 
	 * invoked from inside the conference room, that means all clients are on the
	 * same server, no matter if clustered or not.
	 * 
	 * @param SID
	 * @param publicSID
	 * @return - true in case user have sufficient permissions, null otherwise
	 */
	public Boolean kickUserByPublicSID(String SID, String publicSID) {
		try {
			Long userId = sessiondataDao.checkSession(SID);
			// users only
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				Client rcl = sessionManager.getClientByPublicSID(publicSID, null);

				if (rcl == null) {
					return true;
				}
				String scopeName = "hibernate";
				if (rcl.getRoomId() != null) {
					scopeName = rcl.getRoomId().toString();
				}
				IScope currentScope = this.scopeApplicationAdapter
						.getRoomScope(scopeName);

				HashMap<Integer, String> messageObj = new HashMap<Integer, String>();
				messageObj.put(0, "kick");

				scopeApplicationAdapter.sendMessageById(messageObj, rcl.getStreamid(), currentScope);
				scopeApplicationAdapter.roomLeaveByScope(rcl, currentScope, true);

				return true;
			}
		} catch (Exception err) {
			log.error("[kickUserByPublicSID]", err);
		}
		return null;
	}

	@Override
	public Boolean kickUserBySessionId(String SID, long userId, String sessionId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			// admin only
			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(users_id))) {
				((IApplication)Application.get(wicketApplicationName)).invalidateClient(userId, sessionId);
			}
		} catch (Exception err) {
			log.error("[kickUserBySessionId]", err);
		}
		return null;
	}
}
