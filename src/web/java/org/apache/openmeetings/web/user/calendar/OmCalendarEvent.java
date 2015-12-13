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
package org.apache.openmeetings.web.user.calendar;

import static org.apache.openmeetings.web.app.WebSession.ISO8601_FORMAT_STRING;
import static org.apache.openmeetings.web.app.WebSession.getCalendar;
import static org.apache.openmeetings.web.app.WebSession.getUserTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.calendar.CalendarEvent;

public class OmCalendarEvent extends CalendarEvent {
	private static final long serialVersionUID = 1L;
	private final DateFormat ISO8601;

	public OmCalendarEvent(int id, String title, Date start, Date end) {
		super(id, title, start, end);
		setAllDay(false);
		ISO8601 = new SimpleDateFormat(ISO8601_FORMAT_STRING);
		ISO8601.setCalendar(getCalendar());
		ISO8601.setTimeZone(getUserTimeZone());
	}

	@Override
	protected Options createOptions() {
		Options o = super.createOptions();
		if (getStart() != null) {
			o.set("start", "\"" + ISO8601.format(getStart()) + "\"");
		}
		if (getEnd() != null) {
			o.set("end", "\"" + ISO8601.format(getEnd()) + "\"");
		}
		return o;
	}
}
