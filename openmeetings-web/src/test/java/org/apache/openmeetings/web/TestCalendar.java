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

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.web.user.calendar.CalendarPanel;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.wicket.jquery.ui.calendar.CalendarView;
import com.googlecode.wicket.jquery.ui.widget.dialog.ButtonAjaxBehavior;
import com.googlecode.wicket.jquery.ui.widget.menu.Menu;

public class TestCalendar extends AbstractWicketTester {
	private static final String PATH_APPOINTMENT_DLG = String.format("%s:appointment", PATH_CHILD);
	private static final String PATH_APPOINTMENT_DLG_FRM = String.format("%s:appForm", PATH_APPOINTMENT_DLG);
	@Autowired
	private AppointmentDao appointmentDao;

	@Test
	public void testEventCreate() throws OmException {
		testArea(regularUsername, p -> {
			Menu menu = (Menu)p.get(PATH_MENU);
			Assert.assertNotNull(menu);
			tester.getRequest().setParameter("hash", menu.getItemList().get(0).getItems().get(1).getId());
			tester.executeBehavior((AbstractAjaxBehavior)menu.getBehaviorById(0));

			tester.assertComponent(PATH_CHILD, CalendarPanel.class);
			CalendarPanel cal = (CalendarPanel)p.get(PATH_CHILD);
			tester.executeAllTimerBehaviors(cal);

			User u = userDao.getByLogin(regularUsername, User.Type.user, null);
			//test create month
			tester.getRequest().setParameter("allDay", String.valueOf(false));
			tester.getRequest().setParameter("startDate", LocalDateTime.of(2017, 11, 13, 13, 13).toString());
			tester.getRequest().setParameter("endDate", LocalDateTime.of(2017, 11, 13, 13, 13).toString());
			tester.getRequest().setParameter("viewName", CalendarView.month.name());
			tester.executeBehavior((AbstractAjaxBehavior)cal.get("form:calendar").getBehaviorById(0)); //select-event
			FormTester appTester = tester.newFormTester(PATH_APPOINTMENT_DLG_FRM);
			//check inviteeType:groupContainer:groups is invisible for regular user
			String title = String.format("title%s", UUID.randomUUID());
			appTester.setValue("title", title);
			ButtonAjaxBehavior save = getButtonBehavior(PATH_APPOINTMENT_DLG, "save");
			tester.executeBehavior(save);

			List<Appointment> appts = appointmentDao.searchByTitle(u.getId(), title);
			assertEquals("Appointment should be created", 1, appts.size());
			assertEquals("Appointment should be created", title, appts.get(0).getTitle());
		});
	}
}
