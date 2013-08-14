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

import static org.apache.openmeetings.persistence.beans.basic.Configuration.DEFAUT_LANG_KEY;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.cluster.SlaveHTTPConnectionManager;
import org.apache.openmeetings.data.basic.AuthLevelUtil;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.calendar.daos.MeetingMemberDao;
import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.PrivateMessageFolderDao;
import org.apache.openmeetings.data.user.dao.PrivateMessagesDao;
import org.apache.openmeetings.data.user.dao.SalutationDao;
import org.apache.openmeetings.data.user.dao.UserContactsDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.room.Client;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.Salutation;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.persistence.beans.user.UserContact;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.session.ISessionManager;
import org.apache.openmeetings.utils.TimezoneUtil;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.apache.openmeetings.utils.mail.MailHandler;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.apache.openmeetings.web.util.ContactsHelper;
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
public class UserService {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UserService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private SalutationDao salutationmanagement;
	@Autowired
	private OrganisationManager organisationManager;
	@Autowired
	private ManageCryptStyle cryptManager;
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
	private UsersDao usersDao;
	@Autowired
	private UserContactsDao userContactsDao;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private AuthLevelUtil authLevelUtil;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private ServerDao serverDao;
	@Autowired
	private SlaveHTTPConnectionManager slaveHTTPConnectionManager;

	/**
	 * get user by id, admin only
	 * 
	 * @param SID
	 * @param user_id
	 * @return User with the id given
	 */
	public User getUserById(String SID, long user_id) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return userManager.checkAdmingetUserById(user_level, user_id);
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
	 * get all availible Salutations
	 * 
	 * @param SID
	 * @return all availible Salutations
	 */
	public List<Salutation> getUserSalutations(String SID, long language_id) {
		return salutationmanagement.getUserSalutations(language_id);
	}

	/**
	 * get a list of all users of an organisation
	 * 
	 * @param SID
	 * @param organisation_id
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return list of all users of an organisation
	 */
	public List<User> getOrgUserList(String SID, long organisation_id,
			int start, int max, String orderby, boolean asc) {
		return organisationManager.getUsersByOrganisationId(organisation_id,
				start, max, orderby, asc);
	}

	public List<User> getUserListByModForm(String SID) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return userManager.getUserByMod(user_level, users_id);
	}

	/**
	 * get a list of all organisations of an user
	 * 
	 * @param SID
	 * @param client_user
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return list of all organisations of an user
	 */
	public List<Organisation> getOrganisationListByUser(String SID,
			long client_user, int start, int max, String orderby, boolean asc) {
		Long users_id = sessiondataDao.checkSession(SID);
		long user_level = userManager.getUserLevelByID(users_id);
		return organisationManager.getOrganisationsByUserId(user_level,
				client_user, start, max, orderby, asc);
	}

	/**
	 * gets a list of all not assigned organisations of a user
	 * 
	 * @param SID
	 * @param client_user
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return list of all not assigned organisations of a user
	 */
	public List<Organisation> getRestOrganisationListByUser(String SID,
			long client_user, int start, int max, String orderby, boolean asc) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return organisationManager.getRestOrganisationsByUserId(user_level,
				client_user, start, max, orderby, asc);
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
	public SearchResult<User> getUserList(String SID, int start, int max,
			String orderby, boolean asc) {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);
		return userManager
				.getUsersList(user_level, start, max, orderby, asc);
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
			Long user_level = userManager.getUserLevelByID(users_id);
			// admins only
			if (authLevelUtil.checkAdminLevel(user_level)) {

				if (serverId == 0) {

					Client rcl = this.sessionManager
							.getClientByStreamId(streamid, null);

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
					this.scopeApplicationAdapter.sendMessageById(messageObj,
							streamid, currentScope);

					this.scopeApplicationAdapter.roomLeaveByScope(rcl,
							currentScope, true);

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
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				User us = userManager.getUserById(users_id);

				us.setTimeZoneId(timezoneUtil.getTimezoneByInternalJName(jname).getID());
				us.setForceTimeZoneCheck(false);
				us.setUpdatetime(new Date());

				userManager.updateUser(us);
				
				return us;

			}
		} catch (Exception err) {
			log.error("[updateUserTimeZone]", err);
		}
		return null;
	}

	@Deprecated
	public Long requestUserToContactList(String SID, Long userToAdd_id,
			String domain, String port, String webapp) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {
				return ContactsHelper.addUserToContactList(userToAdd_id);
			}
		} catch (Exception err) {
			log.error("[requestuserToContactList]", err);
		}
		return null;
	}

	public List<UserContact> getPendingUserContacts(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<UserContact> uList = userContactsDao
						.getContactRequestsByUserAndStatus(users_id, true);

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
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<UserContact> uList = userContactsDao
						.getContactsByUserAndStatus(users_id, false);

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
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				UserContact userContacts = userContactsDao
						.get(userContactId);

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

	private Date createCalendarDate(TimeZone timezone, String dateOnly,
			String time) {
		Integer hour = Integer.valueOf(time.substring(0, 2)).intValue();
		Integer minute = Integer.valueOf(time.substring(3, 5)).intValue();

		log.info("createCalendar Hour: " + hour);
		log.info("createCalendar Minute: " + minute);

		Calendar cal = TimezoneUtil.getCalendarInTimezone(dateOnly, timezone);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public Long composeMail(String SID, List<String> recipients,
			String subject, String message, Boolean bookedRoom,
			String validFromDate, String validFromTime, String validToDate,
			String validToTime, Long parentMessageId, Long roomtype_id,
			String domain, String port, String webapp) {
		try {

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {
				User from = userManager.getUserById(users_id);
				TimeZone timezone = timezoneUtil.getTimezoneByUser(from);

				Date appointmentstart = createCalendarDate(timezone,
						validFromDate, validFromTime);
				Date appointmentend = createCalendarDate(timezone, validToDate,
						validToTime);

				log.info("validFromDate: "
						+ CalendarPatterns
								.getDateWithTimeByMiliSeconds(appointmentstart));
				log.info("validToDate: "
						+ CalendarPatterns
								.getDateWithTimeByMiliSeconds(appointmentend));

				Room room = null;

				String baseURL = "http://" + domain + ":" + port + webapp;
				if (port.equals("80")) {
					baseURL = "http://" + domain + webapp;
				} else if (port.equals("443")) {
					baseURL = "https://" + domain + webapp;
				}

				Long room_id = null;
				Long appointmentId = null;

				if (bookedRoom) {
					room_id = roomManager.addRoom(3, // Userlevel
							subject, // name
							roomtype_id, // RoomType
							"", // Comment
							new Long(100), // Number of participants
							false, // public
							null, // Organisations
							true, // Appointment
							false, // Demo Room => Meeting Timer
							null, // Meeting Timer time in seconds
							false, // Is Moderated Room
							null, // Moderation List Room
							true, // Allow User Questions
							false, // isAudioOnly
							true, // allowFontStyles
							false, // isClosed
							"", // redirectURL
							"", // conferencePIN
							null, // ownerId
							null, null, false, // hideTopBar
							false, // hideChat
							false, // hideActivitiesAndActions
							false, // hideFilesExplorer
							false, // hideActionsMenu
							false, // hideScreenSharing
							false, // hideWhiteboard
							false, // showMicrophoneStatus
							false, // chatModerated
							false, // chatOpened
							false, // filesOpened
							false, // autoVideoSelect
							false //sipEnabled
							);

					room = roomDao.get(room_id);

					String sendJNameTimeZone = from.getTimeZoneId();

					appointmentId = this.addAppointmentToUser(subject, message,
							from, recipients, room, appointmentstart,
							appointmentend, true, true, sendJNameTimeZone);

				}

				recipients.add(from.getAdresses().getEmail());

				String sendJNameTimeZone = from.getTimeZoneId();

				String profile_link = baseURL + "?cuser=1";

				for (String email : recipients) {

					// Map receipent = (Map) recipients.get(iter.next());

					// String email = receipent.get("email").toString();

					Long language_id = from.getLanguage_id();
					if (language_id == null) {
						language_id = configurationDao.getConfValue(DEFAUT_LANG_KEY, Long.class, "1");
					}
					String invitation_link = null;
					User to = userManager.getUserByEmail(email);

					if (bookedRoom) {
						// Add the appointment to the calendar of the user (if
						// its an internal user)
						// if the user is the sender then we already added the
						// appointment as we created the
						// room, the invitations always belong to the
						// appointment of the meeting creator
						if (to != null && !to.getUser_id().equals(from.getUser_id())) {
							this.addAppointmentToUser(subject, message, to,
									recipients, room, appointmentstart,
									appointmentend, false, true,
									sendJNameTimeZone);
						}

						String username = to == null ? "" : to.getFirstname() + " " + to.getLastname();;
						Invitations invitation = invitationManager
								.addInvitationLink(
										new Long(2), // userlevel
										username, // username
										message,
										baseURL, // baseURl
										email, // email
										subject, // subject
										room_id, // room_id
										"public",
										false, // passwordprotected
										"", // invitationpass
										2, // valid type
										appointmentstart, // valid from
										appointmentend, // valid to
										from.getUser_id(), // created by
										baseURL, from.getUser_id(),
										false, // really send mail sendMail
										appointmentstart, appointmentend,
										appointmentId, 
										from.getFirstname() + " " + from.getLastname(),
										timezoneUtil.getTimezoneByUser(from));

						invitation_link = baseURL + "?invitationHash="
								+ invitation.getHash();

					}

					if (to != null) {

						if (!to.getUser_id().equals(from.getUser_id())) {
							// One message to the Send
							privateMessagesDao.addPrivateMessage(subject,
									message, parentMessageId, from, to, from,
									bookedRoom, room, false, 0L, email);

							// One message to the Inbox
							privateMessagesDao.addPrivateMessage(subject,
									message, parentMessageId, from, to, to,
									bookedRoom, room, false, 0L, email);

							// One copy of the Inbox message to the user
							if (to.getLanguage_id() != null) {
								language_id = to.getLanguage_id();
							}
						}

					} else {

						// One message to the Send
						privateMessagesDao.addPrivateMessage(subject, message,
								parentMessageId, from, to, from, bookedRoom,
								room, false, 0L, email);

						// there is no Inbox for external users

					}

					// We do not send an email to the one that has created the
					// private message
					if (to != null && to.getUser_id().equals(from.getUser_id())) {
						continue;
					}

					Fieldlanguagesvalues fValue1301 = fieldManager
							.getFieldByIdAndLanguage(1301L, language_id);
					Fieldlanguagesvalues fValue1302 = fieldManager
							.getFieldByIdAndLanguage(1302L, language_id);
					Fieldlanguagesvalues labelid504 = fieldManager
							.getFieldByIdAndLanguage(new Long(504), language_id);
					Fieldlanguagesvalues labelid503 = fieldManager
							.getFieldByIdAndLanguage(new Long(503), language_id);

					String aLinkHTML = "";
					if (to != null) {
						aLinkHTML = "<br/><br/><a href='" + profile_link + "'>"
								+ fValue1302.getValue() + "</a><br/>";
					}

					if (invitation_link == null) {
						invitation_link = "";
					} else {
						invitation_link = "<br/>" //
								+ CalendarPatterns
										.getDateWithTimeByMiliSecondsAndTimeZone(
												appointmentstart, timezone)
								+ "<br/> - <br/>" //
								+ CalendarPatterns
										.getDateWithTimeByMiliSecondsAndTimeZone(
												appointmentstart, timezone)
								+ "<br/>"
								+ labelid503.getValue()
								+ "<br/><a href='" + invitation_link
								+ "'>"
								+ labelid504.getValue() + "</a><br/>";
					}

					mailHandler.send(email, fValue1301.getValue() + " "
							+ subject, message.replaceAll("\\<.*?>", "")
							+ aLinkHTML + invitation_link);

				}

			}

		} catch (Exception err) {
			log.error("[composeMail]", err);
		}
		return null;
	}

	/*
	 * Date appointmentstart = calFrom.getTime(); Date appointmentend =
	 * calTo.getTime();
	 */
	private Long addAppointmentToUser(String subject, String message, User to,
			List<String> recipients, Room room, Date appointmentstart,
			Date appointmentend, Boolean invitor, Boolean isConnectedEvent,
			String sendJNameTimeZone) throws Exception {

		Long appointmentId = appointmentDao.addAppointment(subject,
				to.getUser_id(), "", message, appointmentstart, appointmentend,
				false, false, false, false, 1L, 2L, room, to.getLanguage_id(),
				false, "", isConnectedEvent);

		for (String email : recipients) {

			User meetingMember = userManager.getUserByEmail(email);

			if (meetingMember != null) {

				String firstname = meetingMember.getFirstname();
				String lastname = meetingMember.getLastname();

				meetingMemberDao.addMeetingMember(firstname, lastname, "0",
						"0", appointmentId, meetingMember.getUser_id(), email,
						meetingMember.getPhoneForSMS(), invitor,
						timezoneUtil.getTimezoneByUser(meetingMember), isConnectedEvent);

			} else {

				meetingMemberDao.addMeetingMember("", "", "0", "0",
						appointmentId, null, email, "", invitor,
						timezoneUtil.getTimezoneByInternalJName(sendJNameTimeZone),
						isConnectedEvent);

			}
		}

		return appointmentId;

	}

	public Boolean checkUserIsInContactList(String SID, Long user_id) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				List<UserContact> uList = userContactsDao
						.getContactsByUserAndStatus(users_id, false);

				for (UserContact userContact : uList) {

					if (userContact.getContact().getUser_id().equals(user_id)) {
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

	public void shareCalendarUserContact(String SID, Long userContactId,
			Boolean shareCalendar) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				UserContact userContacts = userContactsDao
						.get(userContactId);

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
			Long user_level = userManager.getUserLevelByID(users_id);
			// users only
			if (authLevelUtil.checkUserLevel(user_level)) {

				Client rcl = this.sessionManager.getClientByPublicSID(
						publicSID, false, null);

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

				this.scopeApplicationAdapter.sendMessageById(messageObj,
						rcl.getStreamid(), currentScope);

				this.scopeApplicationAdapter.roomLeaveByScope(rcl,
						currentScope, true);

				return true;
			}

		} catch (Exception err) {
			log.error("[kickUserByStreamId]", err);
		}
		return null;
	}
}
