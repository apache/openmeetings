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
package org.apache.openmeetings.service.notifier;

import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;

import java.util.TimeZone;

import jakarta.annotation.PostConstruct;

import org.apache.openmeetings.core.notifier.INotifier;
import org.apache.openmeetings.core.notifier.NotifierService;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.manager.IInvitationManager;
import org.apache.openmeetings.service.mail.template.subject.AppointmentReminderTemplate;
import org.apache.openmeetings.service.mail.template.subject.SubjectEmailTemplate;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class MailNotifier implements INotifier {
	@Inject
	private NotifierService notifier;
	@Inject
	private IInvitationManager invitationManager;

	@PostConstruct
	private void register() {
		notifier.addNotifier(this);
	}

	@Override
	public void notify(User u, Appointment a, Invitation inv) throws Exception {
		TimeZone tz = getTimeZone(u);
		SubjectEmailTemplate t = AppointmentReminderTemplate.get(u, a, tz);
		invitationManager.sendInvitationLink(inv, MessageType.CREATE, t.getSubject(), t.getEmail(), false, null);
	}
}
