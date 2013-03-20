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
package org.apache.openmeetings.web.components.user.calendar;

import java.util.Date;

import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.UserPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.Options;
import com.googlecode.wicket.jquery.ui.calendar.Calendar;

public class CalendarPanel extends UserPanel {

	private static final long serialVersionUID = -6536379497642291437L;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.appendJavaScript("calendarInit();");
	}

	public CalendarPanel(String id) {
		super(id);
		
		final Form<Date> form = new Form<Date>("form");
		add(form);
		
		final AppointmentDialog dialog = new AppointmentDialog("appointment", WebSession.getString(815), Model.of(new Appointment()));
		add(dialog);
		
		Options options = new Options();
		//options.set("theme", true);
		options.set("header", "{left: 'prev,next today', center: 'title', right: 'month,agendaWeek,agendaDay'}");
		options.set("allDaySlot", false);
		options.set("axisFormat", "'HH(:mm)'");
		options.set("defaultEventMinutes", 60);
		options.set("timeFormat", "{agenda: 'HH:mm{ - HH:mm}', '': 'HH(:mm)'}");
		options.set("height", "function() {return getCalendarHeight();}");
		//options.set("height", "getCalendarHeight()");
		options.set("windowResize", "function() { this.option('height', getCalendarHeight()); }");
		
		Calendar calendar = new Calendar("calendar", new AppointmentModel(), options) {
			private static final long serialVersionUID = 8442068089963449950L;

			@Override
			protected boolean isSelectable() {
				return true;
			}
			
			@Override
			protected boolean isEditable() {
				return true;
			}
			
			@Override
			protected boolean isEventDropEnabled() {
				return true;
			}
			
			@Override
			protected boolean isEventResizeEnabled() {
				return true;
			}
			
			@Override
			protected void onDayClick(AjaxRequestTarget target, Date date) {
				java.util.Calendar start = WebSession.getCalendar();
				start.setTime(date);
				java.util.Calendar end = WebSession.getCalendar();
				end.setTime(date);
				
				if (start.equals(end)) {
					end.add(java.util.Calendar.HOUR_OF_DAY, 1);
				}
				Appointment a = new Appointment();
				a.setAppointmentStarttime(start.getTime());
				a.setAppointmentEndtime(end.getTime());
				dialog.setModelObject(a);
				
				dialog.open(target);
			}
			
			@Override
			protected void onSelect(AjaxRequestTarget target, Date start, Date end, boolean allDay) {
				Appointment a = new Appointment();
				a.setAppointmentStarttime(start);
				a.setAppointmentEndtime(end);
				dialog.setModelObject(a);
				
				dialog.open(target);
			}
			
			@Override
			protected void onEventClick(AjaxRequestTarget target, int eventId) {
				Appointment a = Application.getBean(AppointmentDao.class).getAppointmentById((long)eventId);
				dialog.setModelObject(a);
				
				dialog.open(target);
			}
			
			@Override
			protected void onEventDrop(AjaxRequestTarget target, int eventId, long delta, boolean allDay) {
				AppointmentDao dao = Application.getBean(AppointmentDao.class);
				Appointment a = dao.getAppointmentById((long)eventId);
				
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getAppointmentStarttime());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta); //FIXME?
				a.setAppointmentStarttime(cal.getTime());
				
				cal.setTime(a.getAppointmentEndtime());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta); //FIXME?
				a.setAppointmentEndtime(cal.getTime());
				
				dao.updateAppointment(a);
				//FIXME add feedback info
			}
			
			@Override
			protected void onEventResize(AjaxRequestTarget target, int eventId, long delta) {
				AppointmentDao dao = Application.getBean(AppointmentDao.class);
				Appointment a = dao.getAppointmentById((long)eventId);
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getAppointmentEndtime());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta); //FIXME?
				a.setAppointmentEndtime(cal.getTime());
				
				dao.updateAppointment(a);
				//FIXME add feedback info
			}
		};
		form.add(calendar);
	}
}
