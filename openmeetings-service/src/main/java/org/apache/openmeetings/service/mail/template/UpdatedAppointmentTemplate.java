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
package org.apache.openmeetings.service.mail.template;

import static org.apache.openmeetings.util.OpenmeetingsVariables.wicketApplicationName;

import java.util.TimeZone;

import org.apache.openmeetings.core.IApplication;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.string.Strings;

public class UpdatedAppointmentTemplate extends AbstractAppointmentTemplate {
	private static final long serialVersionUID = 1L;

	public UpdatedAppointmentTemplate(Long langId, Appointment a, TimeZone tz, String invitorName) {
		super(langId, a, tz, invitorName);

		FieldLanguagesValuesDao dao = ((IApplication)Application.get(wicketApplicationName)).getOmBean(FieldLanguagesValuesDao.class);
		add(new Label("titleLbl", dao.getString(1155L, langId)));
		add(new Label("title", a.getTitle()));
		add(new WebMarkupContainer("descContainer")
			.add(new Label("descLbl", dao.getString(1152L, langId)))
			.add(new Label("desc", a.getDescription()))
			.setVisible(!Strings.isEmpty(a.getDescription()))
			);
		add(new Label("startLbl", dao.getString(1153L, langId)));
		add(new Label("start", CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz)));
		add(new Label("endLbl", dao.getString(1154L, langId)));
		add(new Label("end", CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz)));
		add(new Label("invitorLbl", dao.getString(1156L, langId)));
		add(new Label("invitor", invitorName));
	}
	
	@Override
	public String getSubject() {
		FieldLanguagesValuesDao dao = ((IApplication)Application.get(wicketApplicationName)).getOmBean(FieldLanguagesValuesDao.class);
		StringBuilder sb = new StringBuilder();
		sb.append(dao.getString(1155L, langId)).append(" ").append(a.getTitle())
			.append(" ").append(CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz))
			.append(" - ").append(CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz));

		return sb.toString();
	}
}
