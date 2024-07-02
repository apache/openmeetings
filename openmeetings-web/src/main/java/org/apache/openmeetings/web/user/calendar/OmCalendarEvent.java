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

import static org.apache.openmeetings.web.util.CalendarWebHelper.getDateTime;

import org.apache.openmeetings.db.entity.calendar.Appointment;

import org.wicketstuff.jquery.ui.calendar6.CalendarEvent;

public class OmCalendarEvent extends CalendarEvent {
	private static final long serialVersionUID = 1L;

	public OmCalendarEvent(Appointment a) {
		super(String.valueOf(a.getId()), a.getTitle(), null);
		setStart(getDateTime(a.getStart()));
		setEnd(getDateTime(a.getEnd()));
		setEditable(AppointmentDialog.isOwner(a));
		setAllDay(false);
	}
}
