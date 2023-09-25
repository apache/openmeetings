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
package org.apache.openmeetings.service.mail.template.subject;

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;

import java.util.Locale;
import java.util.TimeZone;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.LocaleHelper;
import org.apache.wicket.markup.html.basic.Label;

public class AppointmentReminderTemplate extends AppointmentTemplate {
	private static final long serialVersionUID = 1L;

	private AppointmentReminderTemplate(Locale locale, Appointment a, TimeZone tz) {
		super(locale, a, tz);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new Label("titleLbl", getString("1158", locale)));
	}

	public static SubjectEmailTemplate get(User u, Appointment a, TimeZone tz) {
		ensureApplication(u.getLanguageId());
		return new AppointmentReminderTemplate(LocaleHelper.getLocale(u), a, tz).create();
	}

	@Override
	public String getPrefix() {
		return app.getOmString("1158", locale);
	}
}
