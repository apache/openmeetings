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
import com.googlecode.wicket.jquery.ui.widget.dialog.*;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.AppointmentManager;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

/**
 * Multipurpose Calendar Dialog form. This provides the ability to ask for a user prompt,
 * for Creating, and Syncing of Calendars. Along with that also,
 * during the Creation and Deletion of Appointments.
 */
public class CalendarDialog extends AbstractFormDialog {
	private static final Logger log = LoggerFactory.getLogger(CalendarDialog.class);
	private static final long serialVersionUID = 1L;
	private CalendarPanel calendarPanel;

	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private DialogButton save = new DialogButton("save", Application.getString(813));
	private DialogButton cancel = new DialogButton("cancel", Application.getString(1130));
	private DialogButton delete = new DialogButton("delete", Application.getString(814));
	private UserCalendarForm form;
	private MessageDialog confirmDelete;
	private List<OmCalendar> cals; //List of calendars for syncing
	private int calIndex = 0;

	public enum DIALOG_TYPE {
		UPDATE_CALENDAR,
		SYNC_CALENDAR,
		UPDATE_APPOINTMENT,
		DELETE_APPOINTMENT
	}

	//Defines the current mode of in which the Dialog is functioning in
	private DIALOG_TYPE type = DIALOG_TYPE.UPDATE_CALENDAR;
	private Appointment appointment = null;

	public CalendarDialog(String id, String title, final CalendarPanel calendarPanel, CompoundPropertyModel<OmCalendar> model) {
		super(id, title, true);
		this.calendarPanel = calendarPanel;
		form = new UserCalendarForm("calform", model);
		add(form);
		confirmDelete = new MessageDialog("confirmDelete", Application.getString(814), Application.getString(833), DialogButtons.OK_CANCEL, DialogIcon.WARN) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				if (button != null && button.match(AbstractDialog.OK)) {
					calendarPanel.getAppointmentManager().deleteCalendar(form.getModelObject());
					calendarPanel.refresh(handler);
					calendarPanel.refreshCalendars(handler);
				}
			}
		};
		add(confirmDelete);
	}

	/**
	 * Open the Dialog with a specific type of Appointment Based Prompts
	 */
	public void open(IPartialPageRequestHandler handler, DIALOG_TYPE type, Appointment a) {
		this.type = type;
		appointment = a;
		if (isOwner(a)) {
			if (setFormModelObject(a, handler)) {
				this.open(handler);
				handler.add(this);
			} else {
				switch (type) {
					case UPDATE_APPOINTMENT:
						updateAppointment(a);
						break;
					case DELETE_APPOINTMENT:
						deleteAppointment(a);
				}

				calendarPanel.refresh(handler);
			}
		}

	}

	/**
	 * Open the Dialog with a specific type of Calendar Based Prompts
	 */
	public void open(IPartialPageRequestHandler handler, DIALOG_TYPE type, OmCalendar c) {
		this.type = type;
		switch (type) {
			case UPDATE_CALENDAR:
				setFormModelObject(c);
				setButtons(handler);
				this.open(handler);
				break;
			case SYNC_CALENDAR:
				if (setCalendarList(handler)) {
					this.open(handler);
					handler.add(this);
				} else {
					calendarPanel.refresh(handler);
				}

		}
	}

	@Override
	public int getWidth() {
		return 650;
	}

	public DialogButton getSubmitButton() {
		return save;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(save, cancel, delete);
	}

	public Form<?> getForm() {
		return form;
	}

	protected void onSubmit(AjaxRequestTarget target) {
		switch (type) {
			case UPDATE_CALENDAR:
				OmCalendar c = form.getModelObject();
				AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
				c.setHref(form.url.getModelObject());
				HttpClient client = calendarPanel.getHttpClient();

				if (form.gcal.getModelObject()) {
					c.setSyncType(OmCalendar.SyncType.GOOGLE_CALENDAR);
					c.setToken(form.username.getModelObject());
					if (c.getId() == null)
						calendarPanel.populateGoogleCalendar(c, target);
				} else if (c.getId() == null && form.username.getModelObject() != null) {
					appointmentManager.provideCredentials(client, c, new UsernamePasswordCredentials(form.username.getModelObject(),
							form.pass.getModelObject()));
				}

				appointmentManager.createCalendar(client, c);
				calendarPanel.refreshCalendars(target);
				calendarPanel.refresh(target);
				break;
			case SYNC_CALENDAR:
				syncCalendar(form.getModelObject(), target);
				if (setFormModelObject()) {
					setButtons(target);
					this.open(target);
					target.add(this);
				}
				break;
			case UPDATE_APPOINTMENT:
				updateAppointment(appointment);
				calendarPanel.refresh(target);
				break;
			case DELETE_APPOINTMENT:
				deleteAppointment(appointment);
				calendarPanel.refresh(target);
				break;
		}
		clearFormModel(target);
		target.add(feedback);
	}

	/**
	 * Performs syncing of the Calendar.
	 *
	 * @param c       Calendar to sync
	 * @param handler Handler used to update the CalendarPanel
	 */
	private void syncCalendar(OmCalendar c, IPartialPageRequestHandler handler) {
		AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
		HttpClient client = calendarPanel.getHttpClient();
		if (form.username.getModelObject() != null) {
			appointmentManager.provideCredentials(client, c, new UsernamePasswordCredentials(form.username.getModelObject(),
					form.pass.getModelObject()));
		}
		appointmentManager.syncItem(client, c);
		calendarPanel.refresh(handler);
		log.trace("Calendar " + c.getTitle() + " Successfully synced.");
	}

	/**
	 * Performs Deletion of Appointment on the Calendar.
	 *
	 * @param a Appointment to delete
	 */
	private void deleteAppointment(Appointment a) {
		AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
		appointmentManager.deleteItem(calendarPanel.getHttpClient(), a);
		appointment = null;
	}

	/**
	 * Performs updation of Appointment on the Calendar.
	 *
	 * @param a Appointment to update
	 */
	private void updateAppointment(Appointment a) {
		AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
		appointmentManager.updateItem(calendarPanel.getHttpClient(), a);
		appointment = null;
	}

	private static boolean isOwner(Appointment object) {
		return object.getOwner() != null && getUserId().equals(object.getOwner().getId());
	}

	private static boolean isOwner(OmCalendar object) {
		return object.getOwner() != null && getUserId().equals(object.getOwner().getId());
	}

	/**
	 * Sets the calendar list to sync and
	 * syncs all them until a calendar whose
	 *
	 * @param target Ajax target to update the buttons.
	 * @return <code>true</code> if a Calendar needs to be synced
	 * else all calendars should have gotten synced
	 */
	private boolean setCalendarList(IPartialPageRequestHandler target) {
		type = DIALOG_TYPE.SYNC_CALENDAR;
		AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
		cals = appointmentManager.getCalendars(getUserId());
		appointmentManager.createHttpClient();
		calIndex = 0;
		setButtons(target);
		return setFormModelObject();
	}

	// Sets the form object when in need of syncing. Returns true if model is set
	private boolean setFormModelObject() {
		AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();

		if (cals != null && !cals.isEmpty() && calIndex < cals.size()) {
			OmCalendar calendar = cals.get(calIndex++);
			HttpClient client = calendarPanel.getHttpClient();
			if (!appointmentManager.testConnection(client, calendar)) {
				form.setModelObject(calendar);
				form.url.setModelObject(calendar.getHref());
				return true;
			} else {
				appointmentManager.syncItem(client, calendar);
				return setFormModelObject();
			}
		}

		cals = null;
		return false;
	}

	// Sets the form model object if the calendar cannot be reached. Returns true if model is set
	private boolean setFormModelObject(Appointment a, IPartialPageRequestHandler target) {
		OmCalendar c = a.getCalendar();
		if (calendarPanel.getAppointmentManager().testConnection(calendarPanel.getHttpClient(), c))
			return false;

		setFormModelObject(c);
		setButtons(target);
		return true;
	}

	private void setFormModelObject(OmCalendar c) {
		if (c != null) {
			form.setModelObject(c);
			form.url.setModelObject(c.getHref());
			if (c.getSyncType() == OmCalendar.SyncType.GOOGLE_CALENDAR) {
				form.username.setDefaultModelObject(c.getToken());
				form.gcal.setDefaultModelObject(true);
			} else {
				form.gcal.setDefaultModelObject(false);
			}
		}
	}

	public void setButtons(IPartialPageRequestHandler target) {
		switch (type) {
			case UPDATE_APPOINTMENT:
			case DELETE_APPOINTMENT:
			case SYNC_CALENDAR:
				delete.setVisible(false, target);
				save.setVisible(true, target);
				break;
			case UPDATE_CALENDAR:
				OmCalendar c = form.getModelObject();
				if (c.getId() == null) {
					delete.setVisible(false, target);
				} else {
					delete.setVisible(isOwner(c), target);
				}
				save.setVisible(isOwner(c), target);
		}
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		switch (type) {
			case UPDATE_CALENDAR:
				if (delete.equals(button)) {
					confirmDelete.open(handler);
				}
				break;
			case UPDATE_APPOINTMENT:
				//If the Appointment to put on the server was a new one, but the user cancelled it.
				// Then remove the calendar from the Appointment
				if (cancel.equals(button) && appointment.getHref() == null) {
					appointment.setCalendar(null);
					getBean(AppointmentDao.class).update(appointment, getUserId());
					calendarPanel.refresh(handler);
				}
				break;
			case DELETE_APPOINTMENT:
			case SYNC_CALENDAR:
		}
		clearFormModel(handler);
	}

	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	private void clearFormModel(IPartialPageRequestHandler handler) {
		form.clearInput();
		form.username.setModelObject(null);
		handler.add(form);
	}

	private class UserCalendarForm extends Form<OmCalendar> {

		private TextField<String> username = new TextField<>("login", Model.of(""));
		private PasswordTextField pass = new PasswordTextField("password", Model.of(""));
		RequiredTextField<String> title = new RequiredTextField<>("title");

		// Fields required for adding Google Calendar
		Label urlLabel = new Label("urlLabel", Application.getString("calendar.url")),
				userLabel = new Label("userLabel", Application.getString(114)),
				passLabel = new Label("passLabel", Application.getString(115));

		AjaxCheckBox gcal; // Checkbox for Google Calendar
		UrlTextField url = new UrlTextField("url", Model.of(""), new UrlValidator() {
			//Custom UrlValidator
			@Override
			public void validate(IValidatable<String> validatable) {
				//Only Validate when It's not a Google Calendar
				if (!gcal.getModelObject()) {
					super.validate(validatable);
				}
			}
		}) {
			@Override
			protected String[] getInputTypes() {
				//Can be URL or a Text
				return new String[]{"url", "text"};
			}
		};

		public UserCalendarForm(String id, CompoundPropertyModel<OmCalendar> model) {
			super(id, model);
			setOutputMarkupId(true);
			add(title);
			add(feedback.setOutputMarkupId(true));

			add(url.setRequired(true).setOutputMarkupId(true));
			add(username.setOutputMarkupPlaceholderTag(true));
			add(urlLabel.setOutputMarkupId(true));

			add(pass.setRequired(false).setOutputMarkupPlaceholderTag(true));

			add(userLabel.setOutputMarkupPlaceholderTag(true));
			add(passLabel.setOutputMarkupPlaceholderTag(true));

			gcal = new AjaxCheckBox("gcal", Model.of(false)) {
				//Checkbox, which when in "true" state will be in
				// the Google Calendar State, otherwise in the CalDAV state
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					setGCalVisibility(getModelObject());
					target.add(UserCalendarForm.this);
				}
			};

			add(gcal);
		}

		@Override
		protected void onModelChanged() {
			OmCalendar calendar = getModelObject();
			switch (type) {
				case UPDATE_CALENDAR:
					title.setEnabled(true);
					if (calendar.getId() != null) {
						url.setEnabled(false);
						pass.setEnabled(false);
						username.setEnabled(false);
						gcal.setEnabled(false);
					} else {
						gcal.setEnabled(true);
						url.setEnabled(true);
						pass.setEnabled(true);
						username.setEnabled(true);
					}
					setGCalVisibility(calendar);
					break;
				case SYNC_CALENDAR:
				case UPDATE_APPOINTMENT:
				case DELETE_APPOINTMENT:
					title.setEnabled(false);
					url.setEnabled(false);
					username.setEnabled(true);
					pass.setEnabled(true);
					gcal.setEnabled(false);
					setGCalVisibility(calendar);
					break;
			}
		}

		private void setGCalVisibility(OmCalendar calendar) {
			setGCalVisibility(calendar.getSyncType() == OmCalendar.SyncType.GOOGLE_CALENDAR);
		}

		//Sets the visibility of the Labels and the TextFields based on
		// whether it's a Google calendar or not.
		private void setGCalVisibility(boolean isGoogleCalendar) {
			if (isGoogleCalendar) {
				gcal.setModelObject(true);
				pass.setVisible(false);
				passLabel.setVisible(false);

				//Google Calendar ID
				urlLabel.setDefaultModelObject(Application.getString("calendar.googleID"));
				url.setEnabled(true);
				url.setLabel(Model.<String>of(Application.getString("calendar.googleID")));

				//Google API Key
				userLabel.setDefaultModelObject(Application.getString("calendar.googleKey"));
				username.setEnabled(true);
			} else {
				gcal.setModelObject(false);
				pass.setVisible(true);
				passLabel.setVisible(true);

				userLabel.setDefaultModelObject(Application.getString(114));
				username.setModelObject(null);

				urlLabel.setDefaultModelObject(Application.getString("calendar.url"));
				url.setLabel(Model.of(Application.getString("calendar.url")));
			}

			//Add new AttributeModifier to change the type of URLTextField, to text for
			//Google Calendar and to URL for a normal CalDAV calendar
			url.add(new AttributeModifier("type", new AbstractReadOnlyModel() {
				public Object getObject() {
					return gcal.getModelObject() ? "text" : "url";
				}
			}) {
				@Override
				public boolean isTemporary(Component component) {
					//This is a temporary model.
					return true;
				}
			});
		}


		/**
		 * Validates the credentials and the server entered by the user by
		 * performing a HTTP Options Method.
		 */
		@Override
		protected void onValidate() {
			if (!hasError()) {
				switch (type) {
					case UPDATE_CALENDAR:
						if (getModelObject().getId() != null || gcal.getModelObject())
							return;
					case UPDATE_APPOINTMENT:
					case DELETE_APPOINTMENT:
					case SYNC_CALENDAR:
						AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
						try {
							OmCalendar calendar = getModelObject();
							if (url.isEnabled())
								calendar.setHref(url.getInput());
							HttpClient client = calendarPanel.getHttpClient();
							appointmentManager.provideCredentials(client, calendar,
									new UsernamePasswordCredentials(username.getInput(), pass.getInput()));
							if (appointmentManager.testConnection(client, calendar))
								return;
						} catch (Exception e) {
							log.error("Error executing the TestConnection");
						}

						error(Application.getString("calendar.error"));
						break;
				}
			}
		}
	}
}
