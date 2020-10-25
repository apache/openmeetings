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

import static org.apache.openmeetings.web.app.Application.get;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.user.PrivateMessageDao;
import org.apache.openmeetings.db.dao.user.UserContactDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.openmeetings.service.mail.template.RequestContactConfirmTemplate;
import org.apache.openmeetings.service.mail.template.RequestContactTemplate;
import org.apache.openmeetings.web.app.Application;

public class ContactsHelper {
	private ContactsHelper() {}

	public static Object addUserToContactList(long userIdToAdd) {
		boolean isContact = get().getBean(UserContactDao.class).isContact(userIdToAdd, getUserId());

		if (isContact) {
			return "error.contact.added";
		}
		UserContact contact = get().getBean(UserContactDao.class).add(userIdToAdd, getUserId(), true);

		User user = contact.getOwner();
		User userToAdd = contact.getContact();

		String subj = user.getDisplayName() + " " + Application.getString("1193");
		String message = RequestContactTemplate.getEmail(userToAdd, user);

		get().getBean(PrivateMessageDao.class).addPrivateMessage(
				subj, message, user, userToAdd, userToAdd, true, contact.getId());

		if (userToAdd.getAddress() != null) {
			get().getBean(MailHandler.class).send(userToAdd.getAddress().getEmail(), subj, message);
		}

		return contact;
	}

	public static Object acceptUserContact(long userContactId) {
		UserContactDao dao = get().getBean(UserContactDao.class);
		UserContact contact = dao.get(userContactId);

		if (contact == null) {
			return "error.contact.denied";
		}

		if (!contact.isPending()) {
			return "error.contact.approved";
		}

		dao.updateContactStatus(userContactId, false);

		contact = dao.get(userContactId);
		User user = contact.getOwner();

		dao.add(user.getId(), getUserId(), false);

		if (user.getAddress() != null) {
			String message = RequestContactConfirmTemplate.getEmail(contact);

			String subj = contact.getContact().getDisplayName() + " " + Application.getString("1198");

			get().getBean(PrivateMessageDao.class).addPrivateMessage(
					subj, message, contact.getContact(), user, user, false, 0L);

			get().getBean(MailHandler.class).send(user.getAddress().getEmail(), subj, message);
		}
		return userContactId;
	}
}
