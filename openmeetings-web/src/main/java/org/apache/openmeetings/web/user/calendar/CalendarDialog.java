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
import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;

import java.util.List;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.AppointmentManager;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.UrlTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

/**
 * Multipurpose Calendar Dialog form. This provides the ability to ask for a user prompt,
 * for Creating, and Syncing of Calendars. Along with that also,
 * during the Creation and Deletion of Appointments.
 */
public class CalendarDialog extends Modal<OmCalendar> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(CalendarDialog.class);
	private CalendarPanel calendarPanel;

	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private BootstrapAjaxButton save;
	private BootstrapAjaxLink<String> delete;
	private UserCalendarForm form;
	private List<OmCalendar> cals; //List of calendars for syncing
	private int calIndex = 0;

	@Inject
	private UserDao userDao;
	@Inject
	private AppointmentManager apptManager;

	public enum DIALOG_TYPE {
		UPDATE_CALENDAR,
		SYNC_CALENDAR,
		UPDATE_APPOINTMENT,
		DELETE_APPOINTMENT
	}

	//Defines the current mode of in which the Dialog is functioning in
	private DIALOG_TYPE type = DIALOG_TYPE.UPDATE_CALENDAR;
	private Appointment appointment = null;

	public CalendarDialog(String id, final CalendarPanel calendarPanel, CompoundPropertyModel<OmCalendar> model) {
		super(id, model);
		this.calendarPanel = calendarPanel;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("calendar.dialogTitle"));

		form = new UserCalendarForm("calform", getModel());
		add(form);

		addButton(save = new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("144"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				switch (type) {
					case UPDATE_CALENDAR:
						OmCalendar c = form.getModelObject();
						c.setHref(form.url.getModelObject());
						c.setOwner(userDao.get(c.getOwner().getId())); // owner might need to be refreshed
						HttpClient client = calendarPanel.getHttpClient();
						HttpClientContext context = calendarPanel.getHttpClientContext();

						if (Boolean.TRUE.equals(form.gcal.getModelObject())) {
							c.setSyncType(OmCalendar.SyncType.GOOGLE_CALENDAR);
							c.setToken(form.username.getModelObject());
							if (c.getId() == null) {
								calendarPanel.populateGoogleCalendar(c, target);
							}
						} else if (c.getId() == null && form.username.getModelObject() != null) {
							apptManager.provideCredentials(context, c, new UsernamePasswordCredentials(form.username.getModelObject(),
									form.pass.getModelObject()));
						}

						apptManager.createCalendar(client, context, c);
						calendarPanel.refreshCalendars(target);
						calendarPanel.refresh(target);
						break;
					case SYNC_CALENDAR:
						syncCalendar(form.getModelObject(), target);
						if (setFormModelObject()) {
							setButtons(target);
							target.add(show(target));
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

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}
		});
		save.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		delete = new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, null, Buttons.Type.Outline_Danger, new ResourceModel("80")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget handler) {
				apptManager.deleteCalendar(form.getModelObject());
				calendarPanel.refresh(handler);
				calendarPanel.refreshCalendars(handler);
			}
		};
		delete.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		addButton(delete.add(newOkCancelDangerConfirm(this, getString("833"))));
		addButton(OmModalCloseButton.of());
		super.onInitialize();
	}

	/**
	 * Open the Dialog with a specific type of Appointment Based Prompts
	 *
	 * @param handler - the {@link IPartialPageRequestHandler}
	 * @param type - the {@link DIALOG_TYPE} being opened
	 * @param a - the {@link Appointment}
	 */
	public void show(IPartialPageRequestHandler handler, DIALOG_TYPE type, Appointment a) {
		this.type = type;
		appointment = a;
		if (isOwner(a)) {
			if (setFormModelObject(a, handler)) {
				handler.add(show(handler));
			} else {
				switch (type) {
					case UPDATE_APPOINTMENT:
						updateAppointment(a);
						break;
					case DELETE_APPOINTMENT:
						deleteAppointment(a);
						break;
					case SYNC_CALENDAR, UPDATE_CALENDAR:
						break;
					default:
						break;
				}

				calendarPanel.refresh(handler);
			}
		}

	}

	/**
	 * Open the Dialog with a specific type of Calendar Based Prompts
	 *
	 * @param handler - the {@link IPartialPageRequestHandler}
	 * @param type - the {@link DIALOG_TYPE} being opened
	 * @param c - the {@link OmCalendar}
	 */
	public void show(IPartialPageRequestHandler handler, DIALOG_TYPE type, OmCalendar c) {
		this.type = type;
		switch (type) {
			case UPDATE_CALENDAR:
				setFormModelObject(c);
				setButtons(handler);
				show(handler);
				break;
			case SYNC_CALENDAR:
				if (setCalendarList(handler)) {
					show(handler);
					handler.add(this);
				} else {
					calendarPanel.refresh(handler);
				}
				break;
			case DELETE_APPOINTMENT, UPDATE_APPOINTMENT:
				break;
			default:
				break;
		}
	}

	/**
	 * Performs syncing of the Calendar.
	 *
	 * @param c       Calendar to sync
	 * @param handler Handler used to update the CalendarPanel
	 */
	private void syncCalendar(OmCalendar c, IPartialPageRequestHandler handler) {
		HttpClient client = calendarPanel.getHttpClient();
		HttpClientContext context = calendarPanel.getHttpClientContext();
		if (form.username.getModelObject() != null) {
			apptManager.provideCredentials(context, c, new UsernamePasswordCredentials(form.username.getModelObject(),
					form.pass.getModelObject()));
		}
		apptManager.syncItem(client, context, c);
		calendarPanel.refresh(handler);
		log.trace("Calendar {} Successfully synced.", c.getTitle());
	}

	/**
	 * Performs Deletion of Appointment on the Calendar.
	 *
	 * @param a Appointment to delete
	 */
	private void deleteAppointment(Appointment a) {
		apptManager.deleteItem(calendarPanel.getHttpClient(), calendarPanel.getHttpClientContext(), a);
		appointment = null;
	}

	/**
	 * Performs updation of Appointment on the Calendar.
	 *
	 * @param a Appointment to update
	 */
	private void updateAppointment(Appointment a) {
		apptManager.updateItem(calendarPanel.getHttpClient(), calendarPanel.getHttpClientContext(), a);
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
		cals = apptManager.getCalendars(getUserId());
		calendarPanel.getHttpClient();
		calIndex = 0;
		setButtons(target);
		return setFormModelObject();
	}

	// Sets the form object when in need of syncing. Returns true if model is set
	private boolean setFormModelObject() {
		if (cals != null && !cals.isEmpty() && calIndex < cals.size()) {
			OmCalendar calendar = cals.get(calIndex++);
			HttpClient client = calendarPanel.getHttpClient();
			HttpClientContext context = calendarPanel.getHttpClientContext();
			if (!apptManager.testConnection(client, context, calendar)) {
				form.setModelObject(calendar);
				form.url.setModelObject(calendar.getHref());
				return true;
			} else {
				apptManager.syncItem(client, context, calendar);
				return setFormModelObject();
			}
		}

		cals = null;
		return false;
	}

	// Sets the form model object if the calendar cannot be reached. Returns true if model is set
	private boolean setFormModelObject(Appointment a, IPartialPageRequestHandler target) {
		OmCalendar c = a.getCalendar();
		if (apptManager.testConnection(calendarPanel.getHttpClient(), calendarPanel.getHttpClientContext(), c)) {
			return false;
		}

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
			case UPDATE_APPOINTMENT, DELETE_APPOINTMENT, SYNC_CALENDAR:
				target.add(delete.setVisible(false), save.setVisible(true));
				break;
			case UPDATE_CALENDAR:
				OmCalendar c = form.getModelObject();
				if (c.getId() == null) {
					target.add(delete.setVisible(false));
				} else {
					target.add(delete.setVisible(isOwner(c)));
				}
				target.add(save.setVisible(isOwner(c)));
				break;
			default:
				break;
		}
	}

	private void clearFormModel(IPartialPageRequestHandler handler) {
		form.clearInput();
		form.username.setModelObject(null);
		handler.add(form);
	}

	private class UserCalendarForm extends Form<OmCalendar> {
		private static final long serialVersionUID = 1L;
		private TextField<String> username = new TextField<>("login", Model.of(""));
		private PasswordTextField pass = new PasswordTextField("password", Model.of(""));
		RequiredTextField<String> title = new RequiredTextField<>("title");

		// Fields required for adding Google Calendar
		private Label urlLabel;
		private Label userLabel;
		private Label passLabel;

		AjaxCheckBox gcal; // Checkbox for Google Calendar
		UrlTextField url = new UrlTextField("url", Model.of(""), new UrlValidator() {
			private static final long serialVersionUID = 1L;

			//Custom UrlValidator
			@Override
			public void validate(IValidatable<String> validatable) {
				//Only Validate when It's not a Google Calendar i.e a URL
				if (Boolean.FALSE.equals(gcal.getModelObject())) {
					super.validate(validatable);
				}
			}
		}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String[] getInputTypes() {
				//Can be URL or a Text
				return new String[]{"url", "text"};
			}
		};

		public UserCalendarForm(String id, IModel<OmCalendar> model) {
			super(id, model);
			setOutputMarkupId(true);
		}

		@Override
		protected void onInitialize() {
			urlLabel = new Label("urlLabel", getString("calendar.url"));
			userLabel = new Label("userLabel", getString("114"));
			passLabel = new Label("passLabel", getString("110"));
			add(title);
			add(feedback.setOutputMarkupId(true));

			add(url.setRequired(true).setOutputMarkupId(true));
			add(username.setOutputMarkupPlaceholderTag(true));
			add(urlLabel.setOutputMarkupId(true));

			add(pass.setRequired(false).setOutputMarkupPlaceholderTag(true));

			add(userLabel.setOutputMarkupPlaceholderTag(true));
			add(passLabel.setOutputMarkupPlaceholderTag(true));

			gcal = new AjaxCheckBox("gcal", Model.of(false)) {
				private static final long serialVersionUID = 1L;

				//Checkbox, which when in "true" state will be in
				// the Google Calendar State, otherwise in the CalDAV state
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					setGCalVisibility(getModelObject());
					target.add(UserCalendarForm.this);
				}
			};

			add(gcal);
			super.onInitialize();
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
				case SYNC_CALENDAR, UPDATE_APPOINTMENT, DELETE_APPOINTMENT:
					title.setEnabled(false);
					url.setEnabled(false);
					username.setEnabled(true);
					pass.setEnabled(true);
					gcal.setEnabled(false);
					setGCalVisibility(calendar);
					break;
				default:
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
				urlLabel.setDefaultModelObject(getString("calendar.googleID"));
				url.setEnabled(true);

				//Google API Key
				userLabel.setDefaultModelObject(getString("calendar.googleKey"));
				username.setEnabled(true);
			} else {
				gcal.setModelObject(false);
				pass.setVisible(true);
				passLabel.setVisible(true);

				userLabel.setDefaultModelObject(getString("114"));
				username.setModelObject(null);

				urlLabel.setDefaultModelObject(getString("calendar.url"));
			}
			url.setLabel(Model.of((String)urlLabel.getDefaultModelObject()));

			//Add new AttributeModifier to change the type of URLTextField, to text for
			//Google Calendar and to URL for a normal CalDAV calendar
			url.add(AttributeModifier.replace("type", Boolean.TRUE.equals(gcal.getModelObject()) ? "text" : "url"));
		}


		/**
		 * Validates the credentials and the server entered by the user by
		 * performing a HTTP Options Method.
		 * @see Form#onValidate()
		 */
		@Override
		protected void onValidate() {
			if (hasError()) {
				return;
			}
			switch (type) {
				case UPDATE_CALENDAR:
					if (getModelObject().getId() != null || gcal.getModelObject()) {
						return;
					}
				case UPDATE_APPOINTMENT, DELETE_APPOINTMENT, SYNC_CALENDAR:
					try {
						OmCalendar calendar = getModelObject();
						if (url.isEnabled()) {
							calendar.setHref(url.getInput());
						}
						HttpClient client = calendarPanel.getHttpClient();
						HttpClientContext context = calendarPanel.getHttpClientContext();

						apptManager.provideCredentials(context, calendar,
								new UsernamePasswordCredentials(username.getInput(), pass.getInput()));
						if (apptManager.testConnection(client, context, calendar)) {
							return;
						}
					} catch (Exception e) {
						log.error("Error executing the TestConnection", e);
					}

					error(getString("calendar.error"));
					break;
				default:
					break;
			}
		}
	}
}
