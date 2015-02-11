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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.openmeetings.core.data.conference.RoomManager;
import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentCategoryDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.IUserService;
import org.apache.openmeetings.db.dao.user.PrivateMessageFolderDao;
import org.apache.openmeetings.db.dao.user.PrivateMessagesDao;
import org.apache.openmeetings.db.dao.user.SalutationDao;
import org.apache.openmeetings.db.dao.user.UserContactsDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.db.entity.user.Salutation;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.TimezoneUtil;
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
	private AppointmentDao appointmentDao;
	@Autowired
	private AppointmentCategoryDao appointmentCategoryDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private IUserManager userManager;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SalutationDao salutationmanagement;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private PrivateMessagesDao privateMessagesDao;
	@Autowired
	private PrivateMessageFolderDao privateMessageFolderDao;
	@Autowired
	private UserContactsDao userContactsDao;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private ServerDao serverDao;
	@Autowired
	private ISlaveHTTPConnectionManager slaveHTTPConnectionManager;
	@Autowired
	private FieldLanguagesValuesDao labelDao;
	@Autowired
	private RoomTypeDao roomTypeDao;

	/**
	 * get user by id, admin only
	 * 
	 * @param SID
	 * @param user_id
	 * @return User with the id given
	 */
	public User getUserById(String SID, long user_id) {
		Long users_id = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
			return userDao.get(user_id);
		}
		return null;
	}

	/**
	 * get all availible Salutations
	 * 
	 * @param SID
	 * @return all availible Salutations
	 */
	public List<Salutation> getUserSalutations(String SID, long language_id) {
		return salutationmanagement.getUserSalutations(language_id);
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
		Long users_id = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasAdminLevel(userDao.getRights(users_id))) {
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
			Long users_id = sessiondataDao.checkSession(SID);
			// admins only
			if (AuthLevelUtil.hasAdminLevel(userDao.getRights(users_id))) {
				if (serverId == 0) {
					Client rcl = sessionManager.getClientByStreamId(streamid, null);

					if (rcl == null) {
						return true;
					}
					String scopeName = "hibernate";
					if (rcl.getRoom_id() != null) {
						scopeName = rcl.getRoom_id().toString();
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

	public User updateUserSelfTimeZone(String SID, String jname) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			// users only
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
				User us = userDao.get(users_id);

				us.setTimeZoneId(timezoneUtil.getTimezoneByInternalJName(jname).getID());
				us.setForceTimeZoneCheck(false);
				us.setUpdatetime(new Date());

				userDao.update(us, users_id);
				
				return us;
			}
		} catch (Exception err) {
			log.error("[updateUserTimeZone]", err);
		}
		return null;
	}

	public List<UserContact> getPendingUserContacts(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			// users only
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
				List<UserContact> uList = userContactsDao.getContactRequestsByUserAndStatus(users_id, true);

				return uList;
			}
		} catch (Exception err) {
			log.error("[getPendingUserContact]", err);
		}
		return null;
	}

	public List<UserContact> getUserContacts(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			// users only
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
				List<UserContact> uList = userContactsDao.getContactsByUserAndStatus(users_id, false);

				return uList;
			}
		} catch (Exception err) {
			log.error("[getPendingUserContact]", err);
		}
		return null;
	}

	public Integer removeContactUser(String SID, Long userContactId) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			// users only
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
				UserContact userContacts = userContactsDao.get(userContactId);

				if (userContacts == null) {
					return -49;
				}

				return userContactsDao.deleteUserContact(userContactId);
			}
		} catch (Exception err) {
			log.error("[removeContactUser]", err);
		}
		return null;
	}

	public Boolean checkUserIsInContactList(String SID, Long user_id) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			// users only
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
				List<UserContact> uList = userContactsDao.getContactsByUserAndStatus(users_id, false);

				for (UserContact userContact : uList) {
					if (userContact.getContact().getId().equals(user_id)) {
						return true;
					}
				}

				return false;
			}
		} catch (Exception err) {
			log.error("[checkUserIsInContactList]", err);
		}
		return null;
	}

	public void shareCalendarUserContact(String SID, Long userContactId, Boolean shareCalendar) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);

			// users only
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
				UserContact userContacts = userContactsDao.get(userContactId);

				userContacts.setShareCalendar(shareCalendar);

				userContactsDao.updateContact(userContacts);
			}
		} catch (Exception err) {
			log.error("[shareCalendarUserContact]", err);
		}
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
			Long users_id = sessiondataDao.checkSession(SID);
			// users only
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
				Client rcl = sessionManager.getClientByPublicSID(publicSID, false, null);

				if (rcl == null) {
					return true;
				}
				String scopeName = "hibernate";
				if (rcl.getRoom_id() != null) {
					scopeName = rcl.getRoom_id().toString();
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
			log.error("[kickUserByStreamId]", err);
		}
		return null;
	}
}
