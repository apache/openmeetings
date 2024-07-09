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

import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getDate;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getZoneId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.apache.openmeetings.web.util.TouchPunchResourceReference;
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
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONObject;
import org.wicketstuff.jquery.core.Options;
import org.wicketstuff.jquery.ui.calendar6.Calendar;
import org.wicketstuff.jquery.ui.calendar6.CalendarView;
import org.wicketstuff.jquery.ui.calendar6.DateTimeDelta;
import org.wicketstuff.jquery.ui.calendar6.EventSource.GoogleCalendar;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import jakarta.inject.Inject;

public class CalendarPanel extends UserBasePanel {
	private static final Logger log = LoggerFactory.getLogger(CalendarPanel.class);
	private static final long serialVersionUID = 1L;
	private static final ResourceReference CALJS = new JavaScriptResourceReference(CalendarPanel.class, "calendar-functions.js");
	private static final ResourceReference BS5_THEME = new WebjarsJavaScriptResourceReference("fullcalendar__bootstrap5/current/index.global.js");
	private final AbstractAjaxTimerBehavior refreshTimer = new AbstractAjaxTimerBehavior(Duration.ofSeconds(10)) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onTimer(AjaxRequestTarget target) {
			refresh(target);
		}
	};
	private AbstractAjaxTimerBehavior syncTimer = new AbstractAjaxTimerBehavior(Duration.ofMinutes(4)) {
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

	@Inject
	private AppointmentDao apptDao;
	@Inject
	private AppointmentManager apptManager;
	@Inject
	private UserDao userDao;

	public CalendarPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		final Form<Date> form = new Form<>("form");
		add(form);

		dialog = new AppointmentDialog("calendarAppointment", this, new CompoundPropertyModel<>(getDefault()));
		add(dialog);

		boolean isRtl = isRtl();
		Options options = new Options()
				.set("direction", Options.asString(isRtl ? "rtl" : "ltr"))
				.set("height", Options.asString("100%"))
				.set("customButtons", "{gotoBtn: {text: ' ', click: onOmGotoClick}}")
				.set("headerToolbar", "{start: 'prevYear,prev,next,nextYear today,gotoBtn', center: 'title', end: 'dayGridMonth,timeGridWeek,timeGridDay'}")
				.set("allDaySlot", false)
				.set("nowIndicator", true)
				.set("defaultTimedEventDuration", Options.asString("01:00"))
				.set("selectMirror", true)

				.set("themeSystem", Options.asString("bootstrap5"))
				.set("buttonIcons", new JSONObject()
						.put("close", "bi fa-solid fa-times")
						.put("prev", isRtl ? "bi fa-solid fa-chevron-right" : "bi fa-solid fa-chevron-left")
						.put("next", isRtl ? "bi fa-solid fa-chevron-left" : "bi fa-solid fa-chevron-right")
						.put("prevYear", isRtl ? "bi fa-solid fa-angles-right" : "bi fa-solid fa-angles-left")
						.put("nextYear", isRtl ? "bi fa-solid fa-angles-left" : "bi fa-solid fa-angles-right")
						.toString())
				.set("buttonText", new JSONObject()
						.put("month", getString("801"))
						.put("week", getString("800"))
						.put("day", getString("799"))
						.put("today", getString("1555"))
						.toString())

				.set("locale", Options.asString(WebSession.get().getLocale().toLanguageTag()));

		calendar = new Calendar("calendar", new AppointmentModel(), options) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isSelectable() {
				return true;
			}

			@Override
			public boolean isDateClickEnabled() {
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
				if (CalendarView.dayGridMonth == view) {
					LocalDateTime now = ZonedDateTime.now(getZoneId()).toLocalDateTime();
					s = start.withHour(now.getHour()).withMinute(now.getMinute());
					e = s.plus(1, ChronoUnit.HOURS);
				}
				a.setStart(getDate(s));
				a.setEnd(getDate(e));
				dialog.setModelObjectWithAjaxTarget(a, target);

				dialog.show(target);
			}

			@Override
			public void onEventClick(AjaxRequestTarget target, CalendarView view, String eventId) {
				if (!StringUtils.isNumeric(eventId)) {
					return;
				}
				Appointment a = apptDao.get(Long.valueOf(eventId));
				dialog.setModelObjectWithAjaxTarget(a, target);

				dialog.show(target);
			}

			@Override
			public void onEventDrop(AjaxRequestTarget target, String eventId, DateTimeDelta delta, boolean allDay) {
				if (!StringUtils.isNumeric(eventId)) {
					refresh(target);
					return;
				}
				Appointment a = apptDao.get(Long.valueOf(eventId));
				if (!AppointmentDialog.isOwner(a)) {
					return;
				}
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getStart());
				cal.add(java.util.Calendar.YEAR, delta.years());
				cal.add(java.util.Calendar.MONTH, delta.months());
				cal.add(java.util.Calendar.DAY_OF_MONTH, delta.days());
				cal.add(java.util.Calendar.MILLISECOND, delta.millis());
				a.setStart(cal.getTime());

				cal.setTime(a.getEnd());
				cal.add(java.util.Calendar.YEAR, delta.years());
				cal.add(java.util.Calendar.MONTH, delta.months());
				cal.add(java.util.Calendar.DAY_OF_MONTH, delta.days());
				cal.add(java.util.Calendar.MILLISECOND, delta.millis());
				a.setEnd(cal.getTime());

				apptDao.update(a, getUserId());

				if (a.getCalendar() != null) {
					updatedeleteAppointment(target, CalendarDialog.DIALOG_TYPE.UPDATE_APPOINTMENT, a);
				}
			}

			@Override
			public void onEventResize(AjaxRequestTarget target, String eventId, DateTimeDelta delta) {
				if (!StringUtils.isNumeric(eventId)) {
					refresh(target);
					return;
				}
				Appointment a = apptDao.get(Long.valueOf(eventId));
				if (!AppointmentDialog.isOwner(a)) {
					return;
				}
				java.util.Calendar cal = WebSession.getCalendar();
				cal.setTime(a.getEnd());
				cal.add(java.util.Calendar.YEAR, delta.years());
				cal.add(java.util.Calendar.MONTH, delta.months());
				cal.add(java.util.Calendar.DAY_OF_MONTH, delta.days());
				cal.add(java.util.Calendar.MILLISECOND, delta.millis());
				a.setEnd(cal.getTime());

				apptDao.update(a, getUserId());

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
		calendarListContainer.add(new ListView<>("items", new LoadableDetachableModel<List<OmCalendar>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<OmCalendar> load() {
				List<OmCalendar> cals = new ArrayList<>(apptManager.getCalendars(getUserId()));
				cals.addAll(apptManager.getGoogleCalendars(getUserId()));
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
				item.add(AjaxEventBehavior.onEvent(EVT_CLICK, target -> {
					calendarDialog.show(target, CalendarDialog.DIALOG_TYPE.UPDATE_CALENDAR, cal);
					target.add(calendarDialog);
				}));
			}
		});

		add(new BootstrapAjaxLink<String>("syncCalendarButton", null, Buttons.Type.Outline_Primary, new ResourceModel("calendar.sync")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				syncCalendar(target);
			}
		}.setSize(Buttons.Size.Small));

		add(new BootstrapAjaxLink<String>("submitCalendar", null, Buttons.Type.Outline_Primary, new ResourceModel("calendar.addCalendar")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				calendarDialog.show(target, CalendarDialog.DIALOG_TYPE.UPDATE_CALENDAR, getDefaultCalendar());
				target.add(calendarDialog);
			}
		}.setSize(Buttons.Size.Small));

		add(calendarListContainer);

		super.onInitialize();
	}

	@Override
	public void cleanup(IPartialPageRequestHandler handler) {
		refreshTimer.stop(handler);
		syncTimer.stop(handler);
		if (client != null) {
			apptManager.cleanupIdleConnections();
			if (context != null) {
				context.getCredentialsProvider().clear();
			}
		}
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
		response.render(JavaScriptHeaderItem.forReference(CALJS));
		response.render(JavaScriptHeaderItem.forReference(TouchPunchResourceReference.instance()));
		response.render(JavaScriptHeaderItem.forReference(BS5_THEME));
		response.render(JavaScriptHeaderItem.forScript("FullCalendar.Bootstrap5.Internal.BootstrapTheme.prototype.rtlIconClasses = null", "fullcalendar-bootstrap-reset-rtl"));
	}

	// Client creation here, because the client is not created until necessary
	public HttpClient getHttpClient() {
		if (client == null) {
			//Ensure there's always a client
			client = apptManager.createHttpClient();
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
		List<OmCalendar> gcals = apptManager.getGoogleCalendars(getUserId());
		for (OmCalendar gcal : gcals) {
			//Href has the Calendar ID and Token has the API Key.
			calendar.addSource(new GoogleCalendar(gcal.getHref(), gcal.getToken()));
		}
	}

	private OmCalendar getDefaultCalendar() {
		OmCalendar c = new OmCalendar();
		c.setDeleted(false);
		c.setOwner(userDao.get(getUserId()));
		c.setTitle(getString("calendar.defaultTitle"));
		return c;
	}

	//Function which delegates the syncing of the Calendar to CalendarDialog
	public void syncCalendar(AjaxRequestTarget target) {
		calendarDialog.show(target, CalendarDialog.DIALOG_TYPE.SYNC_CALENDAR, (OmCalendar) null);
	}

	//Function which delegates the update / deletion of appointment on the Calendar to CalendarDialog
	public void updatedeleteAppointment(IPartialPageRequestHandler target, CalendarDialog.DIALOG_TYPE type, Appointment a) {
		calendarDialog.show(target, type, a);
	}

	private Appointment getDefault() {
		Appointment a = new Appointment();
		a.setReminder(Reminder.ICAL);
		a.setOwner(userDao.get(getUserId()));
		a.setTitle(getString("1444"));
		log.debug(" -- getDefault -- Current model {}", a);
		return a;
	}
}
