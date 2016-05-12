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

import org.apache.openmeetings.db.dao.room.IInvitationManager;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.util.CalendarHelper;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.threeten.bp.LocalDateTime;

public class InvitationService implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(InvitationService.class, webAppRootKey);
	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private IInvitationManager invitationManager;
	@Autowired
	private RoomDao roomDao;

	@Override
	public void resultReceived(IPendingServiceCall arg0) {
		log.debug("InvitationService resultReceived" + arg0);
	}

	private static Date getDate(String date, String time, String tzId) {
		LocalDateTime d = LocalDateTime.of(
				Integer.parseInt(date.substring(6)) //year
				, Integer.parseInt(date.substring(3, 5)) //month
				, Integer.parseInt(date.substring(0, 2)) //dayOfMonth
				, Integer.parseInt(time.substring(0, 2)) //hour
				, Integer.parseInt(time.substring(3, 5)) //minute
				);
		return CalendarHelper.getDate(d, tzId);
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
	 * @param roomId
	 * @param conferencedomain
	 * @param isPasswordProtected
	 * @param invitationpass
	 * @param valid
	 * @param validFromDate
	 * @param validFromTime
	 * @param validToDate
	 * @param validToTime
	 * @param languageId
     * @param iCalTz
	 * @return - invitation object in case of success, "Sys - Error" string or null in case of error
	 */
	public Object sendInvitationHash(String SID, String firstname, String lastname,
			String message, String email, String subject,
			Long roomId, String conferencedomain, Boolean isPasswordProtected,
			String invitationpass, Integer valid, String validFromDate,
			String validFromTime, String validToDate, String validToTime,
			Long languageId, String iCalTz, boolean sendMail) {

		try {
			Long userId = sessiondataDao.checkSession(SID);

			if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
				log.debug("sendInvitationHash: ");
	
				Date from = getDate(validFromDate, validFromTime, iCalTz);
				Date to = getDate(validToDate, validToTime, iCalTz);
	
				User owner = null;
				if (userId < 0) {
					owner = userDao.get(-userId);
				}
				userId = owner == null ? userId : owner.getOwnerId();
				User invitee = userDao.getContact(email, firstname, lastname, userId);
				Invitation invitation = invitationManager.getInvitation(invitee, roomDao.get(roomId),
								isPasswordProtected, invitationpass, Valid.fromInt(valid)
								, userDao.get(userId), languageId,
								from, to, null);

				if (invitation != null) {
					if (sendMail) {
						invitationManager.sendInvitationLink(invitation, MessageType.Create, subject, message, false);
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

	public String sendInvitationByHash(String SID, String invitationHash, String message, String subject, Long languageId) throws Exception {
		Long userId = sessiondataDao.checkSession(SID);

		if (AuthLevelUtil.hasUserLevel(userDao.getRights(userId))) {
			Invitation inv = (Invitation)invitationManager.getInvitationByHashCode(invitationHash, false);
			inv.getInvitee().setLanguageId(languageId);
			invitationManager.sendInvitationLink(inv, MessageType.Create, subject, message, false);
		} else {
			return "Need User Privileges to perfom the Action";
		}
		return "Success";
	}
	
	public Object getInvitationByHash(String SID, String hashCode) {
		Object o = invitationManager.getInvitationByHashCode(hashCode, true);
		if (o instanceof Invitation) {
			Invitation i = (Invitation)o;
			if (i.isAllowEntry()) {
				User u = i.getInvitee();
				Long userId = -u.getId(); //TODO check this, extremely weird
				sessiondataDao.updateUser(SID, userId);
				IConnection current = Red5.getConnectionLocal();
				String streamId = current.getClient().getId();
				Client client = sessionManager.getClientByStreamId(streamId, null);

				client.setFirstname(u.getFirstname());
				client.setLastname(u.getLastname());
				client.setUserId(userId);
				client.setUsername(u.getLogin());
				client.setEmail(u.getAddress() == null ? null : u.getAddress().getEmail());
				client.setPicture_uri(u.getPictureuri());
				client.setLanguage("" + u.getLanguageId());
				client.setExternalUserId(u.getExternalId());
				client.setExternalUserType(u.getExternalType());

				sessionManager.updateClientByStreamId(streamId, client, false, null);
			}
		}
		return o;
	}

	public Object checkInvitationPass(String hashCode, String pass) {
		return invitationManager.checkInvitationPass(hashCode, pass);
	}
}
