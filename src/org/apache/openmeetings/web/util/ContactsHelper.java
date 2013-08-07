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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_MESSAGES;

import java.util.Date;

import org.apache.openmeetings.data.user.dao.PrivateMessagesDao;
import org.apache.openmeetings.data.user.dao.UserContactsDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.persistence.beans.user.UserContact;
import org.apache.openmeetings.templates.RequestContactConfirmTemplate;
import org.apache.openmeetings.templates.RequestContactTemplate;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.apache.openmeetings.utils.mail.MailHandler;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.apache.openmeetings.web.app.WebSession;

public class ContactsHelper {
	public static long addUserToContactList(long userIdToAdd) {
		String hash = getBean(ManageCryptStyle.class).getInstanceOfCrypt()
			.createPassPhrase(CalendarPatterns.getDateWithTimeByMiliSeconds(new Date()));

		Long userContactId = getBean(UserContactsDao.class).addUserContact(userIdToAdd, getUserId(), true, hash);

		User user = getBean(UsersDao.class).get(getUserId());
		User userToAdd = getBean(UsersDao.class).get(userIdToAdd);

		String fValue1192 = WebSession.getString(1192);
		String fValue1193 = WebSession.getString(1193);
		String fValue1196 = WebSession.getString(1196);

		StringBuilder message = new StringBuilder();
		message.append(fValue1192).append(" ")
			.append(userToAdd.getFirstname()).append(" ")
			.append(userToAdd.getLastname()).append("<br/><br/>")
			.append(user.getFirstname()).append(" ").append(user.getLastname())
			.append(" ").append(fValue1193).append("<br/>")
			.append(WebSession.getString(1194)).append("<br/>");

		getBean(PrivateMessagesDao.class).addPrivateMessage(
			user.getFirstname() + " " + user.getLastname() + " " + fValue1193
			, message.toString(), 0L, user, userToAdd, userToAdd, false, null
			, true, userContactId, userToAdd.getAdresses().getEmail());

		String link = WebSession.get().getBaseUrl() + "#" + PROFILE_MESSAGES.getArea().name() + "/" + PROFILE_MESSAGES.getType();

		String profileLinkHTML = "<a href='" + link + "'>" + fValue1196 + "</a><br/>";

		String template = getBean(RequestContactTemplate.class)
				.getRequestContactTemplate(message.toString(), "", "", profileLinkHTML);

		if (userToAdd.getAdresses() != null) {
			getBean(MailHandler.class).send(userToAdd.getAdresses().getEmail(),
					user.getFirstname() + " " + user.getLastname() + " " + fValue1193, template);
		}

		return userContactId;
	}

	public static Long acceptUserContact(long userContactId) {
		UserContactsDao dao = getBean(UserContactsDao.class);
		UserContact contact = dao.get(userContactId);

		if (contact == null) {
			return -46L;
		}

		if (!contact.getPending()) {
			return -47L;
		}

		dao.updateContactStatus(userContactId, false);

		contact = dao.get(userContactId);

		dao.addUserContact(contact.getOwner().getUser_id(), getUserId(), false, "");

		User user = contact.getOwner();

		if (user.getAdresses() != null) {

			String fValue1192 = WebSession.getString(1192);
			String fValue1198 = WebSession.getString(1198);

			StringBuilder message = new StringBuilder();
			message.append(fValue1192).append(" ").append(user.getFirstname()).append(" ")
				.append(user.getLastname()).append("<br/><br/>")
				.append(contact.getContact().getFirstname()).append(" ")
				.append(contact.getContact().getLastname()).append(" ").append(fValue1198);

			String template = getBean(RequestContactConfirmTemplate.class).getRequestContactTemplate(message.toString());

			getBean(PrivateMessagesDao.class).addPrivateMessage(
					user.getFirstname() + " " + user.getLastname() + " " + fValue1198, message.toString(),
					0L, contact.getContact(), user, user, false, null, false, 0L, user.getAdresses().getEmail());

			getBean(MailHandler.class).send(user.getAdresses().getEmail(),
					contact.getContact().getFirstname() + " " + contact.getContact().getLastname() + " " + fValue1198, template);

		}
		return userContactId;
	}
}
