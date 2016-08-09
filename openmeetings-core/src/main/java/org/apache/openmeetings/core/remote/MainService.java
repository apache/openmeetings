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

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.openmeetings.core.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.core.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
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
import org.apache.openmeetings.db.entity.server.RemoteSessionObject;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.Userdata;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.wicket.util.string.Strings;
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
	@Autowired
	private SOAPLoginDao soapLoginDao;
	@Autowired
	private TimezoneUtil timezoneUtil;

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

	/**
	 * load this session id before doing anything else
	 * 
	 * @return a unique session identifier
	 */
	public Sessiondata getsessiondata() {
		return sessionDao.create();
	}

	public Long setCurrentUserGroup(String SID, Long groupId) {
		try {
			sessionDao.updateUserGroup(SID, groupId);
			return 1L;
		} catch (Exception err) {
			log.error("[setCurrentUserGroup]", err);
		}
		return -1L;
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
	
	public User loginWicket(String wicketSID, Long wicketroomid) {
		log.debug("[loginWicket] wicketSID: '{}'; wicketroomid: '{}'", wicketSID, wicketroomid);
		Sessiondata sd = sessionDao.check(wicketSID);
		Long userId = sd.getUserId();
		User u = userId == null ? null : userDao.get(userId);
		if (u != null && wicketroomid != null) {
			log.debug("[loginWicket] user and roomid are not empty: " + userId + ", " + wicketroomid);
			if (isRoomAllowedToUser(roomDao.get(wicketroomid), u)) {
				IConnection current = Red5.getConnectionLocal();
				String streamId = current.getClient().getId();
				Client currentClient = sessionManager.getClientByStreamId(streamId, null);
				
				if (!u.getGroupUsers().isEmpty()) {
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
					
					return u;
				}
			}
		}
		return null;
	}
	
	public Object secureLoginByRemote(String SID, String secureHash) {
		try {
			log.debug("############### secureLoginByRemote " + secureHash);

			String clientURL = Red5.getConnectionLocal().getRemoteAddress();

			log.debug("swfURL " + clientURL);

			SOAPLogin soapLogin = soapLoginDao.get(secureHash);
			if (soapLogin == null) {
				log.warn("Unable to find login by hash: {}" + secureHash);
				return -1L;
			}

			if (soapLogin.isUsed()) {
				if (soapLogin.getAllowSameURLMultipleTimes()) {
					if (!soapLogin.getClientURL().equals(clientURL)) {
						log.debug("does not equal " + clientURL);
						return -42L;
					}
				} else {
					log.debug("Already used " + secureHash);
					return -42L;
				}
			}

			Long loginReturn = loginUserByRemote(soapLogin.getSessionHash());

			IConnection current = Red5.getConnectionLocal();
			String streamId = current.getClient().getId();
			Client currentClient = sessionManager.getClientByStreamId(streamId, null);

			if (currentClient.getUserId() != null) {
				sessionDao.updateUser(SID, currentClient.getUserId());
			}

			currentClient.setAllowRecording(soapLogin.isAllowRecording());
			sessionManager.updateClientByStreamId(streamId, currentClient, false, null);

			if (loginReturn == null) {
				log.debug("loginReturn IS NULL for SID: " + soapLogin.getSessionHash());

				return -1L;
			} else if (loginReturn < 0) {
				log.debug("loginReturn IS < 0 for SID: " + soapLogin.getSessionHash());

				return loginReturn;
			} else {

				soapLogin.setUsed(true);
				soapLogin.setUseDate(new Date());

				soapLogin.setClientURL(clientURL);

				soapLoginDao.update(soapLogin);

				// Create Return Object and hide the validated
				// sessionHash that is stored server side
				// this hash should be never thrown back to the user

				SOAPLogin returnSoapLogin = new SOAPLogin();

				returnSoapLogin.setRoomId(soapLogin.getRoomId());
				returnSoapLogin.setBecomemoderator(soapLogin.isBecomemoderator());
				returnSoapLogin.setShowAudioVideoTest(soapLogin.getShowAudioVideoTest());
				returnSoapLogin.setRecordingId(soapLogin.getRecordingId());
				returnSoapLogin.setShowNickNameDialog(soapLogin.getShowNickNameDialog());
				returnSoapLogin.setLandingZone(soapLogin.getLandingZone());

				return returnSoapLogin;
			}

		} catch (Exception err) {
			log.error("[secureLoginByRemote]", err);
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

	/**
	 * Attention! This SID is NOT the default session id! its the Session id
	 * retrieved in the call from the SOAP Gateway!
	 * 
	 * @param sid
	 * @return - 1 in case of success, -1 otherwise
	 */
	public Long loginUserByRemote(String sid) {
		try {
			Sessiondata sd = sessionDao.check(sid);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(sd.getUserId()))) {
				if (sd.getXml() == null) {
					return -37L;
				} else {
					RemoteSessionObject userObject = RemoteSessionObject.fromXml(sd.getXml());
					if (userObject == null) {
						log.warn("Failed to get user object by XML");
						return -1L;
					}

					log.debug(userObject.toString());

					IConnection current = Red5.getConnectionLocal();
					String streamId = current.getClient().getId();
					Client currentClient = sessionManager.getClientByStreamId(streamId, null);

					// Check if this User is simulated in the OpenMeetings
					// Database

					if (!Strings.isEmpty(userObject.getExternalUserId())) {
						// If so we need to check that we create this user in
						// OpenMeetings and update its record

						User user = userDao.getExternalUser(userObject.getExternalUserId(), userObject.getExternalUserType());

						if (user == null) {
							String iCalTz = configurationDao.getConfValue("default.timezone", String.class, "");

							Address a = userDao.getAddress(null, null, null, Locale.getDefault().getCountry(), null, null, null, userObject.getEmail());

							Set<Right> rights = UserDao.getDefaultRights();
							rights.remove(Right.Login);
							rights.remove(Right.Dashboard);
							User u = userDao.addUser(rights, userObject.getFirstname(), userObject.getUsername(),
											userObject.getLastname(), 1L, "" // password is empty by default
											, a, false, null, null, timezoneUtil.getTimeZone(iCalTz), false
											, null, null, false, false, userObject.getExternalUserId()
											, userObject.getExternalUserType(), null, userObject.getPictureUrl());

							long userId = u.getId();
							currentClient.setUserId(userId);
							SessionVariablesUtil.setUserId(current.getClient(), userId);
						} else {
							user.setPictureuri(userObject.getPictureUrl());

							userDao.update(user, sd.getUserId());

							currentClient.setUserId(user.getId());
							SessionVariablesUtil.setUserId(current.getClient(), user.getId());
						}
					}

					log.debug("userObject.getExternalUserId() -2- " + currentClient.getUserId());

					currentClient.setUserObject(userObject.getUsername(), userObject.getFirstname(), userObject.getLastname());
					currentClient.setPicture_uri(userObject.getPictureUrl());
					currentClient.setEmail(userObject.getEmail());

					log.debug("UPDATE USER BY STREAMID " + streamId);

					if (currentClient.getUserId() != null) {
						sessionDao.updateUser(sid, currentClient.getUserId());
					}

					sessionManager.updateClientByStreamId(streamId, currentClient, false, null);

					return 1L;
				}
			}
		} catch (Exception err) {
			log.error("[loginUserByRemote] ", err);
		}
		return -1L;
	}

	/**
	 * clear this session id
	 * 
	 * @param sid
	 * @return string value if completed
	 */
	public Long logoutUser(String sid) {
		try {
			Sessiondata sd = sessionDao.check(sid);
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);
			
			scopeApplicationAdapter.roomLeaveByScope(currentClient,current.getScope(), false);
			
			currentClient.setUserObject(null, null, null, null);
			
			return userManager.logout(sid, sd.getUserId());
		} catch (Exception err) {
			log.error("[logoutUser]",err);
		}
		return -1L;
	}

	public List<Configuration> getGeneralOptions(String SID) {
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
