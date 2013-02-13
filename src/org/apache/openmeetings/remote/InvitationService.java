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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.SessiondataDao;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.basic.dao.OmTimeZoneDao;
import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.basic.OmTimeZone;
import org.apache.openmeetings.persistence.beans.invitation.Invitations;
import org.apache.openmeetings.persistence.beans.user.User;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class InvitationService implements IPendingServiceCallback {

	private static final Logger log = Red5LoggerFactory.getLogger(
			InvitationService.class, OpenmeetingsVariables.webAppRootKey);
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UsersDao userDAO;
	@Autowired
	private UserManager userManager;
	@Autowired
	private OmTimeZoneDao omTimeZoneDaoImpl;
	@Autowired
	private InvitationManager invitationManager;

	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
		log.debug("InvitationService resultReceived" + arg0);
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
     * @param jNameTimeZone
	 * @return - invitation object in case of success, "Sys - Error" string or null in case of error
	 */
	public Object sendInvitationHash(String SID, String username,
			String message, String baseurl, String email, String subject,
			Long room_id, String conferencedomain, Boolean isPasswordProtected,
			String invitationpass, Integer valid, Date validFromDate,
			String validFromTime, Date validToDate, String validToTime,
			Long language_id, String jNameTimeZone, boolean sendMail) {

		try {
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

			Long users_id = sessiondataDao.checkSession(SID);
			Long user_level = userManager.getUserLevelByID(users_id);

			OmTimeZone omTimeZone = omTimeZoneDaoImpl
					.getOmTimeZone(jNameTimeZone);

			// If everything fails
			if (omTimeZone == null) {
				omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(configurationDao.getConfValue("default.timezone", String.class, "Europe/Berlin"));
			}

			Calendar date = Calendar.getInstance();
			date.setTime(validFromDate);
			
			String timeZoneName = omTimeZone.getIcal();
			Calendar calFrom = Calendar.getInstance(TimeZone.getTimeZone(timeZoneName));
			calFrom.set(Calendar.YEAR, date.get(Calendar.YEAR));
			calFrom.set(Calendar.MONTH, date.get(Calendar.MONTH));
			calFrom.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
			calFrom.set(Calendar.HOUR_OF_DAY, validFromHour);
			calFrom.set(Calendar.MINUTE, validFromMinute);
			calFrom.set(Calendar.SECOND, 0);

			date.setTime(validToDate);
			Calendar calTo = Calendar.getInstance(TimeZone.getTimeZone(timeZoneName));
			calTo.set(Calendar.YEAR, date.get(Calendar.YEAR));
			calTo.set(Calendar.MONTH, date.get(Calendar.MONTH));
			calTo.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
			calTo.set(Calendar.HOUR_OF_DAY, validToHour);
			calTo.set(Calendar.MINUTE, validToMinute);
			calTo.set(Calendar.SECOND, 0);

			Date dFrom = calFrom.getTime();
			Date dTo = calTo.getTime();

			Invitations invitation = invitationManager
					.addInvitationLink(user_level, username, message, baseurl,
							email, subject, room_id, conferencedomain,
							isPasswordProtected, invitationpass, valid, dFrom,
							dTo, users_id, baseurl, language_id, sendMail,
							dFrom, dTo, null, username, omTimeZone);

			if (invitation != null) {
				return invitation;
			} else {
				return "Sys - Error";
			}
		} catch (Exception err) {
			log.error("[sendInvitationHash]", err);
		}

		return null;
	}

	public String sendInvitationByHash(String SID, String invitationHash, String message, String baseurl, String subject, Long language_id) {
		User us = userDAO.get(sessiondataDao.checkSession(SID));
		Invitations inv = (Invitations)invitationManager.getInvitationByHashCode(invitationHash, true);
		return invitationManager.sendInvitionLink(us, inv, message, baseurl, subject, language_id);
	}
	
	public Object getInvitationByHash(String hashCode) {
		return invitationManager.getInvitationByHashCode(
				hashCode, true);
	}

	public Object checkInvitationPass(String hashCode, String pass) {
		return invitationManager.checkInvitationPass(hashCode,
				pass);
	}
}
