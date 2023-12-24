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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REMINDER_MESSAGE;

import jakarta.annotation.PostConstruct;

import org.apache.openmeetings.core.notifier.INotifier;
import org.apache.openmeetings.core.notifier.NotifierService;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class TextNotifier implements INotifier {
	private static final Logger log = LoggerFactory.getLogger(TextNotifier.class);
	@Inject
	private NotifierService notifier;
	@Inject
	protected TaskExecutor taskExecutor;
	@Inject
	protected ConfigurationDao cfgDao;

	@PostConstruct
	private void register() {
		notifier.addNotifier(this);
	}

	@Override
	public void notify(User u, Appointment a, Invitation inv) throws Exception {
		if (u.getAddress() == null || Strings.isEmpty(u.getAddress().getPhone())) {
			log.debug("User has no Phone, skip sending notification");
			return;
		}
		final String phone = u.getAddress().getPhone();
		String msg = cfgDao.getString(CONFIG_REMINDER_MESSAGE, null);
		if (Strings.isEmpty(msg)) {
			msg = String.format("%s %s", LabelDao.getString("1158", u.getLanguageId()), a.getTitle());
		}
		final String reminderMsg = msg;
		taskExecutor.execute(() -> log.debug("Sending Text to: {}, msg is: {}", phone, reminderMsg));
	}
}
