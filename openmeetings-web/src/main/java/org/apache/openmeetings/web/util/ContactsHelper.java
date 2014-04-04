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

import org.apache.openmeetings.db.dao.user.PrivateMessagesDao;
import org.apache.openmeetings.db.dao.user.UserContactsDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.openmeetings.mail.MailHandler;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.mail.template.RequestContactConfirmTemplate;
import org.apache.openmeetings.web.mail.template.RequestContactTemplate;

public class ContactsHelper {
	public static long addUserToContactList(long userIdToAdd) {
		Long countContacts = getBean(UserContactsDao.class).checkUserContacts(userIdToAdd, getUserId());

		if (countContacts != null && countContacts > 0) {
			return -45L;
		}
		String hash = ManageCryptStyle.getInstanceOfCrypt()
			.createPassPhrase(CalendarPatterns.getDateWithTimeByMiliSeconds(new Date()));

		Long userContactId = getBean(UserContactsDao.class).addUserContact(userIdToAdd, getUserId(), true, hash);

		User user = getBean(UserDao.class).get(getUserId());
		User userToAdd = getBean(UserDao.class).get(userIdToAdd);

		String subj = user.getFirstname() + " " + user.getLastname() + " " + WebSession.getString(1193);
		String message = RequestContactTemplate.getEmail(userToAdd, user);

		getBean(PrivateMessagesDao.class).addPrivateMessage(
			subj, message, 0L, user, userToAdd, userToAdd, false, null, true, userContactId);

		if (userToAdd.getAdresses() != null) {
			getBean(MailHandler.class).send(userToAdd.getAdresses().getEmail(), subj, message);
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
			String message = RequestContactConfirmTemplate.getEmail(user, contact);

			String subj = contact.getContact().getFirstname() + " " + contact.getContact().getLastname() + " " + WebSession.getString(1198);

			getBean(PrivateMessagesDao.class).addPrivateMessage(
					subj, message,
					0L, contact.getContact(), user, user, false, null, false, 0L);

			getBean(MailHandler.class).send(user.getAdresses().getEmail(), subj, message);
		}
		return userContactId;
	}
	
	public static String getLink() {
		return PROFILE_MESSAGES.getLink();		
	}
}
