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

import java.util.Date;

import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.calendar.Calendar;
import com.googlecode.wicket.jquery.ui.calendar.CalendarView;

public class CalendarPanel extends UserPanel {
	private static final long serialVersionUID = -6536379497642291437L;
	private Calendar calendar;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
	}

	private AppointmentDao getDao() {
		return Application.getBean(AppointmentDao.class);
	}
	
	public void refresh(AjaxRequestTarget target) {
		calendar.refresh(target);
	}
	
	public CalendarPanel(String id) {
		super(id);
		
		final Form<Date> form = new Form<Date>("form");
		add(form);
		
		final AppointmentDialog dialog = new AppointmentDialog("appointment", WebSession.getString(815), this, Model.of(new Appointment()));
		add(dialog);
		
		Options options = new Options();
		options.set("header", "{left: 'prevYear,prev,next,nextYear today', center: 'title', right: 'month,agendaWeek,agendaDay'}");
		options.set("allDaySlot", false);
		options.set("axisFormat", "'HH(:mm)'");
		options.set("defaultEventMinutes", 60);
		options.set("timeFormat", "{agenda: 'HH:mm{ - HH:mm}', '': 'HH(:mm)'}");
		options.set("windowResize", "function(view){setCalendarHeight();}");
		
		calendar = new Calendar("calendar", new AppointmentModel(), options) {
			private static final long serialVersionUID = 8442068089963449950L;
			
			@Override
			protected void onInitialize() {
				super.onInitialize();
				add(new CalendarFunctionsBehavior(getMarkupId()));
			}
			
			@Override
			public boolean isSelectable() {
				return true;
			}
			
			@Override
			public boolean isEditable() {
				return true;
			}
			
			@Override
			public boolean isEventDropEnabled() {
				return true;
			}
			
			@Override
			public boolean isEventResizeEnabled() {
				return true;
			}
			
			//no need to override onDayClick
			
			@Override
			public void onSelect(AjaxRequestTarget target, CalendarView view, Date start, Date end, boolean allDay) {
				Appointment a = new Appointment();
				a.setAppointmentName(WebSession.getString(1444));
				if (CalendarView.month == view && start.equals(end)) {
					java.util.Calendar now = WebSession.getCalendar();
					java.util.Calendar cal = WebSession.getCalendar();
					cal.setTime(start);
					cal.set(java.util.Calendar.HOUR, now.get(java.util.Calendar.HOUR));
					cal.set(java.util.Calendar.MINUTE, now.get(java.util.Calendar.MINUTE));
					cal.set(java.util.Calendar.SECOND, 0);
					cal.set(java.util.Calendar.MILLISECOND, 0);
					a.setAppointmentStarttime(cal.getTime());
					a.setAppointmentEndtime(cal.getTime());
				} else {
					a.setAppointmentStarttime(start);
					a.setAppointmentEndtime(end);
				}
				dialog.setModelObject(a);
				
				dialog.open(target);
			}
			
			@Override
			public void onEventClick(AjaxRequestTarget target, CalendarView view, int eventId) {
				Appointment a = getDao().getAppointmentById((long)eventId);
				dialog.setModelObject(a);
				
				dialog.open(target);
			}
			
			@Override
			public void onEventDrop(AjaxRequestTarget target, int eventId, long delta, boolean allDay) {
				AppointmentDao dao = getDao();
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
			public void onEventResize(AjaxRequestTarget target, int eventId, long delta) {
				AppointmentDao dao = getDao();
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
		add(new AbstractAjaxTimerBehavior(Duration.seconds(10)) {
			private static final long serialVersionUID = -4353305314396043476L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				refresh(target);
			}
		});
	}
}
