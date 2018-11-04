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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getDate;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getZoneId;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.AppointmentManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.UserBasePanel;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.calendar.Calendar;
import com.googlecode.wicket.jquery.ui.calendar.CalendarView;
import com.googlecode.wicket.jquery.ui.calendar.EventSource.GoogleCalendar;
import com.googlecode.wicket.jquery.ui.form.button.Button;

public class CalendarPanel extends UserBasePanel {
	private static final Logger log = Red5LoggerFactory.getLogger(CalendarPanel.class, getWebAppRootKey());
	private static final long serialVersionUID = 1L;
	private static final String JS_MARKUP = "setCalendarHeight();";
	private final AbstractAjaxTimerBehavior refreshTimer = new AbstractAjaxTimerBehavior(Duration.seconds(10)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onTimer(AjaxRequestTarget target) {
			target.appendJavaScript("setCalendarHeight();");
			refresh(target);
		}
	};
	private AbstractAjaxTimerBehavior syncTimer = new AbstractAjaxTimerBehavior(Duration.minutes(4)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onTimer(AjaxRequestTarget target) {
			log.debug("CalDAV Syncing has begun");
			syncCalendar(target);
		}
	};
	private Calendar calendar;
	private CalendarDialog calendarDialog;
	private AppointmentDialog dialog;
	private final WebMarkupContainer calendarListContainer = new WebMarkupContainer("calendarListContainer");

	// Non-Serializable HttpClient.
	private transient HttpClient client = null;

	// Context for the HttpClient. Mainly used for credentials.
	private transient HttpClientContext context = null;

	public CalendarPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		final Form<Date> form = new Form<>("form");
		add(form);

		dialog = new AppointmentDialog("appointment", this, new CompoundPropertyModel<>(getDefault()));
		add(dialog);

		boolean isRtl = isRtl();
		Options options = new Options();
		options.set("isRTL", isRtl);
		options.set("header", isRtl ? "{left: 'agendaDay,agendaWeek,month', center: 'title', right: 'today nextYear,next,prev,prevYear'}"
				: "{left: 'prevYear,prev,next,nextYear today', center: 'title', right: 'month,agendaWeek,agendaDay'}");
		options.set("allDaySlot", false);
		options.set("axisFormat", Options.asString("H(:mm)"));
		options.set("defaultEventMinutes", 60);
		options.set("timeFormat", Options.asString("H(:mm)"));

		options.set("buttonText", new JSONObject()
				.put("month", getString("801"))
				.put("week", getString("800"))
				.put("day", getString("799"))
				.put("today", getString("1555")).toString());

		options.set("locale", Options.asString(WebSession.get().getLocale().toLanguageTag()));

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
			public void onEventClick(AjaxRequestTarget target, CalendarView view, String eventId) {
				if (!StringUtils.isNumeric(eventId)) {
					return;
				}
				Appointment a = getDao().get(Long.valueOf(eventId));
				dialog.setModelObjectWithAjaxTarget(a, target);

				dialog.open(target);
			}

			@Override
			public void onEventDrop(AjaxRequestTarget target, String eventId, long delta, boolean allDay) {
				if (!StringUtils.isNumeric(eventId)) {
					refresh(target);
					return;
				}
				AppointmentDao dao = getDao();
				Appointment a = dao.get(Long.valueOf(eventId));

				if (!AppointmentDialog.isOwner(a)) {
					return;
				}
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getStart());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta);
				a.setStart(cal.getTime());

				cal.setTime(a.getEnd());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta);
				a.setEnd(cal.getTime());

				dao.update(a, getUserId());

				if (a.getCalendar() != null) {
					updatedeleteAppointment(target, CalendarDialog.DIALOG_TYPE.UPDATE_APPOINTMENT, a);
				}
			}

			@Override
			public void onEventResize(AjaxRequestTarget target, String eventId, long delta) {
				if (!StringUtils.isNumeric(eventId)) {
					refresh(target);
					return;
				}
				AppointmentDao dao = getDao();
				Appointment a = dao.get(Long.valueOf(eventId));
				if (!AppointmentDialog.isOwner(a)) {
					return;
				}
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getEnd());
				cal.add(java.util.Calendar.MILLISECOND, (int)delta);
				a.setEnd(cal.getTime());

				dao.update(a, getUserId());

				if (a.getCalendar() != null) {
					updatedeleteAppointment(target, CalendarDialog.DIALOG_TYPE.UPDATE_APPOINTMENT, a);
				}
			}
		};

		form.add(calendar);

		populateGoogleCalendars();

		add(refreshTimer);
		add(syncTimer);

		calendarDialog = new CalendarDialog("calendarDialog", this, new CompoundPropertyModel<>(getDefaultCalendar()));

		add(calendarDialog);

		calendarListContainer.setOutputMarkupId(true);
		calendarListContainer.add(new ListView<OmCalendar>("items", new LoadableDetachableModel<List<OmCalendar>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<OmCalendar> load() {
				AppointmentManager manager = getAppointmentManager();
				List<OmCalendar> cals = new ArrayList<>(manager.getCalendars(getUserId()));
				cals.addAll(manager.getGoogleCalendars(getUserId()));
				return cals;
			}
		}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<OmCalendar> item) {
				item.setOutputMarkupId(true);
				final OmCalendar cal = item.getModelObject();
				item.add(new WebMarkupContainer("item")
						.add(new Label("name", cal.getTitle())));
				item.add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						calendarDialog.open(target, CalendarDialog.DIALOG_TYPE.UPDATE_CALENDAR, cal);
						target.add(calendarDialog);
					}
				});
			}
		});

		add(new Button("syncCalendarButton").add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				syncCalendar(target);
			}
		}));

		add(new Button("submitCalendar").add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				calendarDialog.open(target, CalendarDialog.DIALOG_TYPE.UPDATE_CALENDAR, getDefaultCalendar());
				target.add(calendarDialog);
			}
		}));

		add(calendarListContainer);

		super.onInitialize();
	}

	@Override
	public void cleanup(IPartialPageRequestHandler handler) {
		refreshTimer.stop(handler);
		syncTimer.stop(handler);
		if (client != null) {
			getAppointmentManager().cleanupIdleConnections();
			context.getCredentialsProvider().clear();
		}
	}

	private static AppointmentDao getDao() {
		return getBean(AppointmentDao.class);
	}

	public void refresh(IPartialPageRequestHandler handler) {
		calendar.refresh(handler);
	}

	//Reloads the Calendar List on Appointment Dialog and the list of Calendars
	public void refreshCalendars(IPartialPageRequestHandler handler) {
		handler.add(dialog, calendarListContainer);
	}

	Calendar getCalendar() {
		return calendar;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		Optional<AjaxRequestTarget> target = getRequestCycle().find(AjaxRequestTarget.class);
		if (target.isPresent()) {
			target.get().appendJavaScript(JS_MARKUP);
			target.get().appendJavaScript("addCalButton('datepicker');");
		} else {
			response.render(JavaScriptHeaderItem.forScript(JS_MARKUP, this.getId()));
		}
	}

	// Client creation here, because the client is not created until necessary
	public HttpClient getHttpClient() {
		if (client == null) {
			//Ensure there's always a client
			client = getAppointmentManager().createHttpClient();
		}

		return client;
	}

	public HttpClientContext getHttpClientContext() {
		if (context == null) {
			context = HttpClientContext.create();
			context.setCredentialsProvider(new BasicCredentialsProvider());
		}

		return context;
	}

	//Adds a new Event Source to the Calendar
	public void populateGoogleCalendar(OmCalendar gcal, IPartialPageRequestHandler target) {
		calendar.addSource(new GoogleCalendar(gcal.getHref(), gcal.getToken()));
		refresh(target);
	}

	// Function which populates the already existing Google Calendars.
	private void populateGoogleCalendars() {
		AppointmentManager appointmentManager = getAppointmentManager();
		List<OmCalendar> gcals = appointmentManager.getGoogleCalendars(getUserId());
		for (OmCalendar gcal : gcals) {

			//Href has the Calendar ID and Token has the API Key.
			calendar.addSource(new GoogleCalendar(gcal.getHref(), gcal.getToken()));
		}
	}

	private OmCalendar getDefaultCalendar() {
		OmCalendar c = new OmCalendar();
		c.setDeleted(false);
		c.setOwner(getBean(UserDao.class).get(getUserId()));
		c.setTitle(getString("calendar.defaultTitle"));
		return c;
	}

	//Function which delegates the syncing of the Calendar to CalendarDialog
	public void syncCalendar(AjaxRequestTarget target) {
		calendarDialog.open(target, CalendarDialog.DIALOG_TYPE.SYNC_CALENDAR, (OmCalendar) null);
	}

	//Function which delegates the update / deletion of appointment on the Calendar to CalendarDialog
	public void updatedeleteAppointment(IPartialPageRequestHandler target, CalendarDialog.DIALOG_TYPE type, Appointment a) {
		calendarDialog.open(target, type, a);
	}

	private Appointment getDefault() {
		Appointment a = new Appointment();
		a.setReminder(Reminder.ical);
		a.setOwner(getBean(UserDao.class).get(getUserId()));
		a.setTitle(getString("1444"));
		log.debug(" -- getDefault -- Current model " + a);
		return a;
	}

	public AppointmentManager getAppointmentManager() {
		return getBean(AppointmentManager.class);
	}
}
