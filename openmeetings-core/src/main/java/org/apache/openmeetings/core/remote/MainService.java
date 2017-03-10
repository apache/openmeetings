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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SIP_ENABLED;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.core.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.log.ConferenceLog;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.Userdata;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author swagner
 *
 */
public class MainService implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(MainService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private SessiondataDao sessionDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private IUserManager userManager;
	@Autowired
	private ConferenceLogDao conferenceLogDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private AppointmentDao appointmentDao;

	// External User Types
	public static final String EXTERNAL_USER_TYPE_LDAP = "LDAP";


	/**
	 * gets a user by its SID
	 *
	 * @param sid
	 * @param userId
	 * @return - user with SID given
	 */
	public User getUser(String sid, long userId) {
		User users = new User();
		Sessiondata sd = sessionDao.check(sid);
		Set<Right> rights = userDao.getRights(sd.getUserId());
		if (AuthLevelUtil.hasAdminLevel(rights) || AuthLevelUtil.hasWebServiceLevel(rights)) {
			users = userDao.get(userId);
		} else {
			users.setFirstname("No rights to do this");
		}
		return users;
	}

	public Client getCurrentRoomClient(String SID) {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug("getCurrentRoomClient -1- " + SID);
			log.debug("getCurrentRoomClient -2- " + streamid);

			Client currentClient = sessionManager.getClientByStreamId(streamid, null);
			return currentClient;
		} catch (Exception err) {
			log.error("[getCurrentRoomClient]", err);
		}
		return null;
	}

	public boolean isRoomAllowedToUser(Room r, User u) {
		boolean allowed = false;
		if (r != null) {
			if (r.isAppointment()) {
				Appointment a = appointmentDao.getByRoom(r.getId());
				if (a != null && !a.isDeleted()) {
					allowed = a.getOwner().getId().equals(u.getId());
					log.debug("[loginWicket] appointed room, isOwner ? " + allowed);
					if (!allowed) {
						for (MeetingMember mm : a.getMeetingMembers()) {
							if (mm.getUser().getId().equals(u.getId())) {
								allowed = true;
								break;
							}
						}
					}
					/*
					TODO need to be reviewed
					Calendar c = WebSession.getCalendar();
					if (c.getTime().after(a.getStart()) && c.getTime().before(a.getEnd())) {
						allowed = true;
					} else {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm"); //FIXME format
						deniedMessage = Application.getString(1271) + String.format(" %s - %s", sdf.format(a.getStart()), sdf.format(a.getEnd()));
					}
					*/
				}
			} else {
				allowed = r.getIspublic() || (r.getOwnerId() != null && r.getOwnerId().equals(u.getId()));
				log.debug("[loginWicket] public ? " + r.getIspublic() + ", ownedId ? " + r.getOwnerId() + " " + allowed);
				if (!allowed && null != r.getRoomGroups()) {
					for (RoomGroup ro : r.getRoomGroups()) {
						for (GroupUser ou : u.getGroupUsers()) {
							if (ro.getGroup().getId().equals(ou.getGroup().getId())) {
								allowed = true;
								break;
							}
						}
						if (allowed) {
							break;
						}
					}
				}
			}
		}
		return allowed;
	}

	public List<Object> loginWicket(String wicketSID, Long wicketroomid) {
		log.debug("[loginWicket] wicketSID: '{}'; wicketroomid: '{}'", wicketSID, wicketroomid);
		Sessiondata sd = sessionDao.check(wicketSID);
		Long userId = sd.getUserId();
		User u = userId == null ? null : userDao.get(userId);
		Room r = roomDao.get(wicketroomid);
		if (u != null && r != null) {
			log.debug("[loginWicket] user and roomid are not empty: " + userId + ", " + wicketroomid);
			if (wicketroomid.equals(sd.getRoomId()) || isRoomAllowedToUser(r, u)) {
				IConnection current = Red5.getConnectionLocal();
				String streamId = current.getClient().getId();
				Client currentClient = sessionManager.getClientByStreamId(streamId, null);

				if (User.Type.user != u.getType() || (User.Type.user == u.getType() && !u.getGroupUsers().isEmpty())) {
					u.setSessionData(sd);
					currentClient.setUserId(u.getId());
					currentClient.setRoomId(wicketroomid);
					SessionVariablesUtil.setUserId(current.getClient(), u.getId());

					currentClient.setUsername(u.getLogin());
					currentClient.setFirstname(u.getFirstname());
					currentClient.setLastname(u.getLastname());
					currentClient.setPicture_uri(u.getPictureuri());
					currentClient.setEmail(u.getAddress() == null ? null : u.getAddress().getEmail());
					sessionManager.updateClientByStreamId(streamId, currentClient, false, null);

					scopeApplicationAdapter.sendMessageToCurrentScope("roomConnect", currentClient, false);

					return Arrays.<Object>asList(u, r);
				}
			}
		}
		return null;
	}

	/**
	 * Function is called if the user loggs in via a secureHash and sets the
	 * param showNickNameDialog in the Object SOAPLogin to true the user gets
	 * displayed an additional dialog to enter his nickname
	 *
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @return - 1 in case of success, -1 otherwise
	 */
	public Long setUserNickName(String firstname, String lastname, String email) {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamId = current.getClient().getId();
			Client currentClient = sessionManager.getClientByStreamId(streamId, null);

			currentClient.setFirstname(firstname);
			currentClient.setLastname(lastname);
			currentClient.setEmail(email);

			// Log the User
			conferenceLogDao.add(
					ConferenceLog.Type.nicknameEnter, currentClient.getUserId(), streamId,
					null, currentClient.getUserip(), currentClient.getScope());

			sessionManager.updateClientByStreamId(streamId, currentClient, false, null);
			scopeApplicationAdapter.sendMessageToCurrentScope("nickNameSet", currentClient, true);

			return 1L;
		} catch (Exception err) {
			log.error("[setUserNickName] ", err);
		}
		return new Long(-1);
	}

	public List<Configuration> getGeneralOptions() {
		try {
			return configurationDao.get("exclusive.audio.keycode", CONFIG_SIP_ENABLED, CONFIG_MAX_UPLOAD_SIZE_KEY, "mute.keycode", CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY);
		} catch (Exception err) {
			log.error("[getGeneralOptions]",err);
		}
		return null;
	}

	public List<Userdata> getUserdata(String sid) {
		Sessiondata sd = sessionDao.check(sid);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
			return userManager.getUserdataDashBoard(sd.getUserId());
		}
		return null;
	}

	@Override
	public void resultReceived(IPendingServiceCall arg0) {
		log.debug("[resultReceived]" + arg0);
	}
}
