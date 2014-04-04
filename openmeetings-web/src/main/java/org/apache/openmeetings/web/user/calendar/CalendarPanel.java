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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getBaseUrl;
import static org.apache.openmeetings.web.app.WebSession.getClientTimeZone;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentReminderTypDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.calendar.Calendar;
import com.googlecode.wicket.jquery.ui.calendar.CalendarView;

public class CalendarPanel extends UserPanel {
	private static final Logger log = Red5LoggerFactory.getLogger(CalendarPanel.class, webAppRootKey);
	private static final long serialVersionUID = 1L;
	private static final String javaScriptMarkup = "setCalendarHeight();";
	private static final String javaScriptAddDatepicker = "addCalButton('left', 'Datepicker', 'datepicker');";
	private static final SimpleDateFormat formatDateJava = new SimpleDateFormat("MM/dd/yy");
	private final AbstractAjaxTimerBehavior refreshTimer = new AbstractAjaxTimerBehavior(Duration.seconds(10)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onTimer(AjaxRequestTarget target) {
			target.appendJavaScript("setCalendarHeight();");
			refresh(target);
		}
	};
	private Calendar calendar;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
	}

	@Override
	public void cleanup(AjaxRequestTarget target) {
		refreshTimer.stop(target);
	}
	
	private AppointmentDao getDao() {
		return getBean(AppointmentDao.class);
	}
	
	private AppointmentReminderTypDao getAppointmentReminderTypDao() {
		return getBean(AppointmentReminderTypDao.class);
	}
	
	public void refresh(AjaxRequestTarget target) {
		calendar.refresh(target);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		AjaxRequestTarget target = getRequestCycle().find(AjaxRequestTarget.class);
		if (target != null) {
			target.appendJavaScript(javaScriptMarkup);
			target.appendJavaScript(javaScriptAddDatepicker);
		} else {
			response.render(JavaScriptHeaderItem.forScript(javaScriptMarkup, this.getId()));
		}
	}

	public CalendarPanel(String id) {
		super(id);
		
		final Form<Date> form = new Form<Date>("form");
		add(form);
		
		final AppointmentDialog dialog = new AppointmentDialog("appointment", WebSession.getString(815)
				, this, new CompoundPropertyModel<Appointment>(getDefault()));
		add(dialog);
		
		Options options = new Options();
		options.set("header", "{left: 'prevYear,prev,next,nextYear today', center: 'title', right: 'month,agendaWeek,agendaDay'}");
		options.set("allDaySlot", false);
		options.set("axisFormat", "'HH(:mm)'");
		options.set("defaultEventMinutes", 60);
		options.set("timeFormat", "{agenda: 'HH:mm{ - HH:mm}', '': 'HH(:mm)'}");

		options.set("buttonText", "{month: '" + WebSession.getString(801) +
								"', week: '" + WebSession.getString(800) + 
								"', day: '"  + WebSession.getString(799) + 
								"', today: '"  + WebSession.getString(1555) + 
								"'}");

		JSONArray monthes = new JSONArray();
		JSONArray shortMonthes = new JSONArray();
		JSONArray days = new JSONArray();
		JSONArray shortDays = new JSONArray();
		try {
			// first week day must be Sunday
			days.put(0, WebSession.getString(466));
			shortDays.put(0, WebSession.getString(459));
			for (int i=0; i < 12; i++){
				monthes.put(i, WebSession.getString(469 + i));
				shortMonthes.put(i, WebSession.getString(1556 + i));
				if (i+1 < 7){
					days.put(i+1, WebSession.getString(460 + i));
					shortDays.put(i+1, WebSession.getString(453 + i));					
				}
			}
		} catch (JSONException e) {
			log.error("Unexpected error while creating label lists", e);
		}
		options.set("monthNames", monthes.toString());
		options.set("monthNamesShort", shortMonthes.toString());
		options.set("dayNames", days.toString());
		options.set("dayNamesShort", shortDays.toString());
		
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
			public boolean isDayClickEnabled() {
				return true;
			}
			
			@Override
			public boolean isEventClickEnabled() {
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
				target.appendJavaScript("setDatepickerDate('datepicker','" +  formatDateJava.format(start) + "');");
				Appointment a = getDefault();
				if (CalendarView.month == view && start.equals(end)) {
					java.util.Calendar cNow = java.util.Calendar.getInstance(getClientTimeZone());
					java.util.Calendar cStart = java.util.Calendar.getInstance(getClientTimeZone());
					cStart.setTime(start);
					cStart.set(java.util.Calendar.HOUR_OF_DAY, cNow.get(java.util.Calendar.HOUR_OF_DAY));
					cStart.set(java.util.Calendar.MINUTE, cNow.get(java.util.Calendar.MINUTE));
					cStart.set(java.util.Calendar.SECOND, 0);
					cStart.set(java.util.Calendar.MILLISECOND, 0);
					a.setStart(cStart.getTime());
					cStart.add(java.util.Calendar.HOUR_OF_DAY, 1);
					a.setEnd(cStart.getTime());
				} else {
					a.setStart(start);
					a.setEnd(end);
				}
				dialog.setModelObjectWithAjaxTarget(a, target);
				
				dialog.open(target);
			}
			
			@Override
			public void onEventClick(AjaxRequestTarget target, CalendarView view, int eventId) {
				Appointment a = getDao().get((long)eventId);
				dialog.setModelObjectWithAjaxTarget(a, target);
				
				dialog.open(target);
			}
			
			@Override
			public void onEventDrop(AjaxRequestTarget target, int eventId, long delta, boolean allDay) {
				AppointmentDao dao = getDao();
				Appointment a = dao.get((long)eventId);
				
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getStart());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta); //FIXME?
				a.setStart(cal.getTime());
				
				cal.setTime(a.getEnd());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta); //FIXME?
				a.setEnd(cal.getTime());
				
				dao.update(a, getBaseUrl(), getUserId());
				//FIXME add feedback info
			}

			@Override
			public void onEventResize(AjaxRequestTarget target, int eventId, long delta) {
				AppointmentDao dao = getDao();
				Appointment a = dao.get((long)eventId);
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getEnd());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta); //FIXME?
				a.setEnd(cal.getTime());
				
				dao.update(a, getBaseUrl(), getUserId());
				//FIXME add feedback info
			}
		};
		
		form.add(calendar);
		add(refreshTimer);
	}
	
	private Appointment getDefault() {
		Appointment a = new Appointment();
		a.setRemind(getAppointmentReminderTypDao().get(3L)); //TODO: Make configurable
		a.setOwner(getBean(UserDao.class).get(getUserId()));
		a.setTitle(WebSession.getString(1444));
		log.debug(" -- getDefault -- Current model " + a);
		return a;
	}
}
