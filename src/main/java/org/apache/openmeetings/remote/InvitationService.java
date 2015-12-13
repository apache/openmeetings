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
import java.util.Date;
import java.util.TimeZone;

import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.user.UserManager;
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
	private UserManager userManager;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private TimezoneUtil timezoneUtil;
	@Autowired
	private RoomDao roomDao;

	public void resultReceived(IPendingServiceCall arg0) {
		log.debug("InvitationService resultReceived" + arg0);
	}

	private String getBaseUrl(String baseUrl) {
		String url = null;
		if (baseUrl != null) {
			url = baseUrl.toLowerCase();
			if (url.endsWith("swf")) {
				url = url.substring(0, url.length() - 4);
			}
		}	
		return url;
	}
	
	/**
	 * send an invitation to another user by Mail
	 * 
	 * @param SID
	 * @param username
	 * @param message
	 * @param baseurl
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
	public Object sendInvitationHash(String SID, String username,
			String message, String baseurl, String email, String subject,
			Long room_id, String conferencedomain, Boolean isPasswordProtected,
			String invitationpass, Integer valid, Date validFromDate,
			String validFromTime, Date validToDate, String validToTime,
			Long language_id, String iCalTz, boolean sendMail) {

		try {
			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			if (AuthLevelUtil.checkUserLevel(user_level)) {
				log.debug("sendInvitationHash: ");
	
				Integer validFromHour = Integer.valueOf(
						validFromTime.substring(0, 2)).intValue();
				Integer validFromMinute = Integer.valueOf(
						validFromTime.substring(3, 5)).intValue();
	
				Integer validToHour = Integer.valueOf(validToTime.substring(0, 2))
						.intValue();
				Integer validToMinute = Integer
						.valueOf(validToTime.substring(3, 5)).intValue();
	
				log.info("validFromHour: " + validFromHour);
				log.info("validFromMinute: " + validFromMinute);
	
				Calendar date = Calendar.getInstance();
				date.setTime(validFromDate);
				
				TimeZone timeZone = timezoneUtil.getTimeZone(iCalTz);
				
				Calendar calFrom = Calendar.getInstance(timeZone);
				calFrom.set(Calendar.YEAR, date.get(Calendar.YEAR));
				calFrom.set(Calendar.MONTH, date.get(Calendar.MONTH));
				calFrom.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
				calFrom.set(Calendar.HOUR_OF_DAY, validFromHour);
				calFrom.set(Calendar.MINUTE, validFromMinute);
				calFrom.set(Calendar.SECOND, 0);
	
				date.setTime(validToDate);
				Calendar calTo = Calendar.getInstance(timeZone);
				calTo.set(Calendar.YEAR, date.get(Calendar.YEAR));
				calTo.set(Calendar.MONTH, date.get(Calendar.MONTH));
				calTo.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
				calTo.set(Calendar.HOUR_OF_DAY, validToHour);
				calTo.set(Calendar.MINUTE, validToMinute);
				calTo.set(Calendar.SECOND, 0);
	
				Date dFrom = calFrom.getTime();
				Date dTo = calTo.getTime();
	
				User invitee = userDao.getContact(email, users_id);
				Invitation invitation = invitationManager.getInvitation(invitee, roomDao.get(room_id),
								isPasswordProtected, invitationpass, Valid.fromInt(valid)
								, userDao.get(users_id), getBaseUrl(baseurl), language_id,
								dFrom, dTo, null);

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

	public String sendInvitationByHash(String SID, String invitationHash, String message, String baseurl, String subject
			, Long language_id) throws Exception {
		Long users_id = sessiondataDao.checkSession(SID);
		Long user_level = userManager.getUserLevelByID(users_id);

		if (AuthLevelUtil.checkUserLevel(user_level)) {
			Invitation inv = (Invitation)invitationManager.getInvitationByHashCode(invitationHash, true);
			inv.setBaseUrl(getBaseUrl(baseurl));
			inv.getInvitee().setLanguage_id(language_id);
			invitationManager.sendInvitionLink(inv, MessageType.Create, subject, message, false);
		} else {
			return "Need User Privileges to perfom the Action";
		}
		return "Success";
	}
	
	public Object getInvitationByHash(String hashCode) {
		return invitationManager.getInvitationByHashCode(
				hashCode, true);
	}

	public Object checkInvitationPass(String hashCode, String pass) {
		return invitationManager.checkInvitationPass(hashCode, pass);
	}
}
