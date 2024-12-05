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
package org.apache.openmeetings.web;

import static java.util.UUID.randomUUID;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.user.calendar.CalendarPanel;
import org.apache.openmeetings.util.OmException;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.util.tester.FormTester;
import org.junit.jupiter.api.Test;

import org.wicketstuff.jquery.ui.calendar6.CalendarView;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;

class TestCalendar extends AbstractWicketTesterTest {
	private static final String PATH_APPOINTMENT_DLG = String.format("%s:calendarAppointment", PATH_CHILD);
	private static final String PATH_APPOINTMENT_DLG_FRM = String.format("%s:appForm", PATH_APPOINTMENT_DLG);

	@Test
	void testEventCreate() throws OmException {
		testArea(regularUsername, p -> {
			Navbar menu = (Navbar)p.get(PATH_MENU);
			assertNotNull(menu);
			tester.executeBehavior((AbstractAjaxBehavior)menu.get("container:collapse:navLeftListEnclosure:navLeftList:0:component:dropdown-menu:buttons:1:button").getBehaviorById(0));

			tester.assertComponent(PATH_CHILD, CalendarPanel.class);
			CalendarPanel cal = (CalendarPanel)p.get(PATH_CHILD);
			tester.executeAllTimerBehaviors(cal);

			User u = userDao.getByLogin(regularUsername, User.Type.USER, null);
			//test create month
			tester.getRequest().setParameter("allDay", String.valueOf(false));
			tester.getRequest().setParameter("startDate", LocalDateTime.of(2017, 11, 13, 13, 13).atOffset(UTC).format(ISO_OFFSET_DATE_TIME));
			tester.getRequest().setParameter("endDate", LocalDateTime.of(2017, 11, 13, 13, 13).atOffset(UTC).format(ISO_OFFSET_DATE_TIME));
			tester.getRequest().setParameter("viewName", CalendarView.dayGridMonth.name());
			tester.executeBehavior((AbstractAjaxBehavior)cal.get("form:calendar").getBehaviorById(0)); //select-event
			FormTester appTester = tester.newFormTester(PATH_APPOINTMENT_DLG_FRM);
			//check inviteeType:groupContainer:groups is invisible for regular user
			String title = "title" + randomUUID();
			appTester.setValue("title", title);
			AbstractAjaxBehavior save = getButtonBehavior(PATH_APPOINTMENT_DLG, 0);
			tester.executeBehavior(save);

			List<Appointment> appts = appointmentDao.searchByTitle(u.getId(), title);
			assertEquals(1, appts.size(), "Appointment should be created");
			assertEquals(title, appts.get(0).getTitle(), "Appointment should be created");
		});
	}
}
