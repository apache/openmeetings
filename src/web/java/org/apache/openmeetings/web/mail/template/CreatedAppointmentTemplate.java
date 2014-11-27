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
package org.apache.openmeetings.web.mail.template;

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.TimeZone;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.string.Strings;

public class CreatedAppointmentTemplate extends AbstractAppointmentTemplate {
	private static final long serialVersionUID = 1L;

	private String getTitleLbl(FieldLanguagesValuesDao dao) {
		return dao.getString(1151L, langId).replaceAll("\\$APP_NAME", getBean(ConfigurationDao.class).getAppName());
	}
	
	public CreatedAppointmentTemplate(Long langId, Appointment a, TimeZone tz, String invitorName) {
		super(langId, a, tz, invitorName);

		FieldLanguagesValuesDao dao = getBean(FieldLanguagesValuesDao.class);
		add(new Label("titleLbl", getTitleLbl(dao)));
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
		FieldLanguagesValuesDao dao = getBean(FieldLanguagesValuesDao.class);
		StringBuilder sb = new StringBuilder();
		sb.append(getTitleLbl(dao)).append(" ").append(a.getTitle())
			.append(" ").append(CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz))
			.append(" - ").append(CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz));

		return sb.toString();
	}
}
