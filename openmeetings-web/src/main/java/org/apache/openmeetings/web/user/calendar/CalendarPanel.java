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

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.calendar.Calendar;
import com.googlecode.wicket.jquery.ui.calendar.CalendarView;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.AppointmentManager;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Date;
import java.util.List;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CALENDAR_FIRST_DAY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getDate;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getZoneId;

public class CalendarPanel extends UserPanel {
	private static final Logger log = Red5LoggerFactory.getLogger(CalendarPanel.class, webAppRootKey);
	private static final long serialVersionUID = 1L;
	private static final String javaScriptMarkup = "setCalendarHeight();";
	private final String javaScriptAddDatepicker;
	private final AbstractAjaxTimerBehavior refreshTimer = new AbstractAjaxTimerBehavior(Duration.seconds(10)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onTimer(AjaxRequestTarget target) {
			target.appendJavaScript("setCalendarHeight();");
			refresh(target);
		}
	};
    private AbstractAjaxTimerBehavior syncTimer = new AbstractAjaxTimerBehavior(Duration.minutes(3)) {
        @Override
        protected void onTimer(AjaxRequestTarget target) {
            log.debug("CalDAV Syncing has begun");
            syncCalendar(target);
        }
    };
	private Calendar calendar;
	private final CalendarDialog calendarDialog;
    private AppointmentDialog dialog;
	private final WebMarkupContainer calendarListContainer;
	
	@Override
	public void onMenuPanelLoad(IPartialPageRequestHandler handler) {
		super.onMenuPanelLoad(handler);
	}

	@Override
	public void cleanup(IPartialPageRequestHandler handler) {
		refreshTimer.stop(handler);
	}
	
	private static AppointmentDao getDao() {
		return getBean(AppointmentDao.class);
	}
	
	public void refresh(IPartialPageRequestHandler handler) {
		calendar.refresh(handler);
	}

	//Reloads the Calendar List on Appointment Dialog and the list of Calendars
	public void refreshCalendars(IPartialPageRequestHandler handler){
		handler.add(dialog, calendarListContainer);
	}
	
	Calendar getCalendar() {
		return calendar;
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
		
		dialog = new AppointmentDialog("appointment", Application.getString(815)
				, this, new CompoundPropertyModel<Appointment>(getDefault()));
		add(dialog);
		
		boolean isRtl = isRtl();
		javaScriptAddDatepicker = String.format("addCalButton(%s, 'datepicker');", isRtl);
		Options options = new Options();
		options.set("isRTL", isRtl);
		options.set("header", isRtl ? "{left: 'agendaDay,agendaWeek,month', center: 'title', right: 'today nextYear,next,prev,prevYear'}"
				: "{left: 'prevYear,prev,next,nextYear today', center: 'title', right: 'month,agendaWeek,agendaDay'}");
		options.set("allDaySlot", false);
		options.set("axisFormat", Options.asString("H(:mm)"));
		options.set("defaultEventMinutes", 60);
		options.set("timeFormat", Options.asString("H(:mm)"));

		options.set("buttonText", "{month: '" + Application.getString(801) +
								"', week: '" + Application.getString(800) + 
								"', day: '"  + Application.getString(799) + 
								"', today: '"  + Application.getString(1555) + 
								"'}");

		JSONArray monthes = new JSONArray();
		JSONArray shortMonthes = new JSONArray();
		JSONArray days = new JSONArray();
		JSONArray shortDays = new JSONArray();
		try {
			// first week day must be Sunday
			days.put(0, Application.getString(466));
			shortDays.put(0, Application.getString(459));
			for (int i = 0; i < 12; i++) {
				monthes.put(i, Application.getString(469 + i));
				shortMonthes.put(i, Application.getString(1556 + i));
				if (i + 1 < 7) {
					days.put(i + 1, Application.getString(460 + i));
					shortDays.put(i + 1, Application.getString(453 + i));
				}
			}
		} catch (JSONException e) {
			log.error("Unexpected error while creating label lists", e);
		}
		options.set("monthNames", monthes.toString());
		options.set("monthNamesShort", shortMonthes.toString());
		options.set("dayNames", days.toString());
		options.set("dayNamesShort", shortDays.toString());
		options.set("firstDay", getBean(ConfigurationDao.class).getConfValue(CONFIG_CALENDAR_FIRST_DAY, String.class, "0"));
		
		calendar = new Calendar("calendar", new AppointmentModel(), options) {
			private static final long serialVersionUID = 1L;
			
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
			public void onSelect(AjaxRequestTarget target, CalendarView view, LocalDateTime start, LocalDateTime end, boolean allDay) {
				Appointment a = getDefault();
				LocalDateTime s = start, e = end;
				if (CalendarView.month == view) {
					LocalDateTime now = ZonedDateTime.now(getZoneId()).toLocalDateTime();
					s = start.withHour(now.getHour()).withMinute(now.getMinute());
					e = s.plus(1, ChronoUnit.HOURS);
				}
				a.setStart(getDate(s));
				a.setEnd(getDate(e));
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

				if (!AppointmentDialog.isOwner(a)) {
					return;
				}
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getStart());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta); //FIXME?
				a.setStart(cal.getTime());
				
				cal.setTime(a.getEnd());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta); //FIXME?
				a.setEnd(cal.getTime());
				
				dao.update(a, getUserId());

				if(a.getCalendar() != null)
					updatedeleteAppointment(target, CalendarDialog.DIALOG_TYPE.UPDATE_APPOINTMENT, a);
				//FIXME add feedback info
			}

			@Override
			public void onEventResize(AjaxRequestTarget target, int eventId, long delta) {
				AppointmentDao dao = getDao();
				Appointment a = dao.get((long)eventId);
				if (!AppointmentDialog.isOwner(a)) {
					return;
				}
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getEnd());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta); //FIXME?
				a.setEnd(cal.getTime());
				
				dao.update(a, getUserId());

				if(a.getCalendar() != null)
					updatedeleteAppointment(target, CalendarDialog.DIALOG_TYPE.UPDATE_APPOINTMENT, a);
				//FIXME add feedback info
			}
		};
		
		form.add(calendar);
		add(refreshTimer);
        add(syncTimer);

        calendarDialog = new CalendarDialog("calendarDialog", "Calendar User Dialog", this, new CompoundPropertyModel<OmCalendar>(getDefaultCalendar()));

        add(calendarDialog);

        calendarListContainer = new WebMarkupContainer("calendarListContainer");
        calendarListContainer.setOutputMarkupId(true);
        calendarListContainer.add(new ListView<OmCalendar>("items", new LoadableDetachableModel<List<OmCalendar>>() {
            @Override
            protected List<OmCalendar> load() {
                return getBean(AppointmentManager.class).getCalendars();
            }
        }) {

            protected void populateItem(final ListItem<OmCalendar> item) {
                item.setOutputMarkupId(true);
                final OmCalendar calendar = item.getModelObject();
				item.add(new Button("item", new PropertyModel<String>(calendar, "title")).add(new AjaxEventBehavior("click") {
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						calendarDialog.open(target, CalendarDialog.DIALOG_TYPE.UPDATE_CALENDAR, calendar);
						target.add(calendarDialog);
					}
				}));
            }
        });

        add(new Button("syncCalendarButton").add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                syncCalendar(target);
            }
        }));

        add(new Button("submitCalendar").add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                calendarDialog.open(target, CalendarDialog.DIALOG_TYPE.UPDATE_CALENDAR,getDefaultCalendar());
                target.add(calendarDialog);
            }
        }));

        add(calendarListContainer);
	}

	private OmCalendar getDefaultCalendar(){
		OmCalendar calendar = new OmCalendar();
		calendar.setDeleted(false);
		calendar.setOwner(getBean(UserDao.class).get(getUserId()));
		calendar.setTitle("Default Calendar Title");
		return calendar;
	}

	public void syncCalendar(AjaxRequestTarget target) {
		calendarDialog.open(target, CalendarDialog.DIALOG_TYPE.SYNC_CALENDAR, (OmCalendar) null);
	}

	public void updatedeleteAppointment(IPartialPageRequestHandler target, CalendarDialog.DIALOG_TYPE type, Appointment a){
		calendarDialog.open(target, type, a);
	}

	private static Appointment getDefault() {
		Appointment a = new Appointment();
		a.setReminder(Reminder.ical); //TODO: Make configurable
		a.setOwner(getBean(UserDao.class).get(getUserId()));
		a.setTitle(Application.getString(1444));
		log.debug(" -- getDefault -- Current model " + a);
		return a;
	}

	public AppointmentManager getAppointmentManager(){
		return getBean(AppointmentManager.class);
	}
}
