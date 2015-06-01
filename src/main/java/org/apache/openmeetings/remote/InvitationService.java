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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Calendar;

import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.IInvitationManager.MessageType;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.AuthLevelUtil;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class InvitationService implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationService.class, webAppRootKey);
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private RoomDao roomDao;

	public void resultReceived(IPendingServiceCall arg0) {
		log.debug("InvitationService resultReceived" + arg0);
	}

	private Calendar getDate(String date, String time, String tzId) {
		Calendar c = Calendar.getInstance(timezoneUtil.getTimeZone(tzId));
		c.set(Calendar.YEAR, Integer.parseInt(date.substring(6)));
		c.set(Calendar.MONTH, Integer.parseInt(date.substring(3, 5)) - 1);
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0, 2)));
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
		c.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, 5)));
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c;
	}
	
	/**
	 * send an invitation to another user by Mail
	 * 
	 * @param SID
	 * @param firstname
	 * @param lastname
	 * @param message
	 * @param email
	 * @param subject
	 * @param room_id
	 * @param conferencedomain
	 * @param isPasswordProtected
	 * @param invitationpass
	 * @param valid
	 * @param validFromDate
	 * @param validFromTime
	 * @param validToDate
	 * @param validToTime
	 * @param language_id
     * @param iCalTz
	 * @return - invitation object in case of success, "Sys - Error" string or null in case of error
	 */
	public Object sendInvitationHash(String SID, String firstname, String lastname,
			String message, String email, String subject,
			Long room_id, String conferencedomain, Boolean isPasswordProtected,
			String invitationpass, Integer valid, String validFromDate,
			String validFromTime, String validToDate, String validToTime,
			Long language_id, String iCalTz, boolean sendMail) {

		try {
			Long users_id = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
				log.debug("sendInvitationHash: ");
	
				Calendar calFrom = getDate(validFromDate, validFromTime, iCalTz);
				Calendar calTo = getDate(validToDate, validToTime, iCalTz);
	
				User owner = null;
				if (users_id < 0) {
					owner = userDao.get(-users_id);
				}
				users_id = owner == null ? users_id : owner.getOwnerId();
				User invitee = userDao.getContact(email, firstname, lastname, users_id);
				Invitation invitation = invitationManager.getInvitation(invitee, roomDao.get(room_id),
								isPasswordProtected, invitationpass, Valid.fromInt(valid)
								, userDao.get(users_id), language_id,
								calFrom.getTime(), calTo.getTime(), null);

				if (invitation != null) {
					if (sendMail) {
						invitationManager.sendInvitionLink(invitation, MessageType.Create, subject, message, false);
					}

					return invitation;
				} else {
					return "Sys - Error";
				}
			} else {
				return "Need User Privileges to perfom the Action";
			}

		} catch (Exception err) {
			log.error("[sendInvitationHash]", err);
		}

		return null;
	}

	public String sendInvitationByHash(String SID, String invitationHash, String message, String subject
			, Long language_id) throws Exception {
		Long users_id = sessiondataDao.checkSession(SID);

		if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
			Invitation inv = (Invitation)invitationManager.getInvitationByHashCode(invitationHash, false);
			inv.getInvitee().setLanguage_id(language_id);
			invitationManager.sendInvitionLink(inv, MessageType.Create, subject, message, false);
		} else {
			return "Need User Privileges to perfom the Action";
		}
		return "Success";
	}
	
	public Object getInvitationByHash(String hashCode) {
		return invitationManager.getInvitationByHashCode(hashCode, true);
	}

	public Object checkInvitationPass(String hashCode, String pass) {
		return invitationManager.checkInvitationPass(hashCode, pass);
	}
}
