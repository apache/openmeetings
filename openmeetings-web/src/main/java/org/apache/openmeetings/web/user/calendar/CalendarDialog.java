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
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.service.calendar.caldav.AppointmentManager;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.apache.openmeetings.web.app.WebSession.getUserId;

/**
 * Multipurpose Calendar Dialog form. This provides the ability to ask for a user prompt,
 * for Creating, and Syncing of Calendars. Along with that also,
 * during the Creation and Deletion of Appointments.
 */
public class CalendarDialog extends AbstractFormDialog {
    private static final Logger log = LoggerFactory.getLogger(CalendarDialog.class);
    private CalendarPanel calendarPanel;

    private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
    private DialogButton save = new DialogButton("save", "Save");
    private DialogButton cancel = new DialogButton("cancel", "Cancel");
    private DialogButton delete = new DialogButton("delete", "Delete");
    private UserCalendarForm form;
    private MessageDialog confirmDelete;
    private List<OmCalendar> cals;
    private int calIndex = 0;

    public enum DIALOG_TYPE {
        UPDATE_CALENDAR,
        SYNC_CALENDAR,
        UPDATE_APPOINTMENT,
        DELETE_APPOINTMENT
    }
    private DIALOG_TYPE type = DIALOG_TYPE.UPDATE_CALENDAR;
    private Appointment appointment = null;

    public CalendarDialog(String id, String title, final CalendarPanel calendarPanel, CompoundPropertyModel<OmCalendar> model) {
        super(id, title, true);
        this.calendarPanel = calendarPanel;
        form = new UserCalendarForm("calform", model);
        add(form);
        confirmDelete = new MessageDialog("confirmDelete", Application.getString(814), Application.getString(833), DialogButtons.OK_CANCEL, DialogIcon.WARN){
            private static final long serialVersionUID = 1L;

            @Override
            public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
                if (button != null && button.match(AbstractDialog.OK)){
                    calendarPanel.getAppointmentManager().deleteCalendar(form.getModelObject());
                    calendarPanel.refresh(handler);
                    calendarPanel.refreshCalendars(handler);
                }
            }
        };
        add(confirmDelete);
    }

    //Open the Dialog with a specific type of Appointment Based Prompts
    public void open(IPartialPageRequestHandler handler, DIALOG_TYPE type, Appointment a){
        this.type = type;
        appointment = a;
        if(isOwner(a)) {
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

    //Open the Dialog with a specific type of Calendar Based Prompts
    public void open(IPartialPageRequestHandler handler, DIALOG_TYPE type, OmCalendar c){
        this.type = type;
        switch (type){
            case UPDATE_CALENDAR:
                setFormModelObject(c, (AjaxRequestTarget) handler);
                this.open(handler);
                break;
            case SYNC_CALENDAR:
                if(setCalendarList(handler)) {
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
        switch (type){
            case UPDATE_CALENDAR:
                OmCalendar c = form.getModelObject();
                c.setHref(form.url.getModelObject());
                AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
                try {
                    if(c.getId() == null) {
                        appointmentManager.provideCredentials(c, new UsernamePasswordCredentials(form.username.getModelObject(),
                                form.pass.getModelObject()));
                    }
                    appointmentManager.createCalendar(c);
                } catch (URIException e) {
                    log.error("Unable to parse URL: " + c.getHref());
                }
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
        target.add(feedback);
    }

    public void syncCalendar(OmCalendar c, IPartialPageRequestHandler handler){
        AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
        try {
            appointmentManager.provideCredentials(c, new UsernamePasswordCredentials(form.username.getModelObject(),
                    form.pass.getModelObject()));
            appointmentManager.syncItem(c);
            calendarPanel.refresh(handler);
            log.trace("Calendar " + c.getTitle() + " Successfully synced.");
        } catch (URIException e) {
            log.error("Unable to parse URL: " + c.getHref());
        }
    }

    public void deleteAppointment(Appointment a){
        AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
        appointmentManager.deleteItem(a);
        appointment = null;
    }

    public void updateAppointment(Appointment a){
        AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
        appointmentManager.updateItem(a);
        appointment = null;
    }

    public static boolean isOwner(Appointment object) {
        return object.getOwner() != null && getUserId().equals(object.getOwner().getId());
    }

    public static boolean isOwner(OmCalendar object) {
        return object.getOwner() != null && getUserId().equals(object.getOwner().getId());
    }

    public boolean setCalendarList(IPartialPageRequestHandler target){
        type = DIALOG_TYPE.SYNC_CALENDAR;
        AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
        cals = appointmentManager.getCalendars();
        appointmentManager.createHttpClient();
        calIndex = 0;
        setButtons(target);
        return setFormModelObject();
    }

    // Sets the form object when in need of syncing. Returns true if model is set
    public boolean setFormModelObject() {
        AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();

        if(cals != null && !cals.isEmpty() && calIndex < cals.size()) {
            OmCalendar calendar = cals.get(calIndex++);
            if(!appointmentManager.testConnection(calendar)) {
                form.setModelObject(calendar);
                form.url.setModelObject(calendar.getHref());
                return true;
            } else {
                appointmentManager.syncItem(calendar);
                return setFormModelObject();
            }
        }

        cals = null;
        appointmentManager.cleanupHttpClient();
        return false;
    }

    // Sets the form model object if the calendar cannot be reached. Returns true if model is set
    public boolean setFormModelObject(Appointment a, IPartialPageRequestHandler target) {
        OmCalendar c = a.getCalendar();
        if(calendarPanel.getAppointmentManager().testConnection(c))
            return false;

        form.setModelObject(c);
        form.url.setModelObject(c.getHref());
        setButtons(target);
        return true;
    }

    public void setFormModelObject(OmCalendar c, AjaxRequestTarget target) {
        if(c != null) {
            form.setModelObject(c);
            form.url.setModelObject(c.getHref());
        }
        setButtons(target);
    }

    public void setButtons(IPartialPageRequestHandler target){
        switch (type){
            case UPDATE_APPOINTMENT:
            case DELETE_APPOINTMENT:
            case SYNC_CALENDAR:
                delete.setVisible(false, target);
                break;
            case UPDATE_CALENDAR:
                OmCalendar c = form.getModelObject();
                if(c.getId() == null)
                    delete.setVisible(false, target);
                else delete.setVisible(isOwner(c), target);
                save.setVisible(isOwner(c), target);
        }
    }

    @Override
    public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
        if (delete.equals(button)) {
            confirmDelete.open(handler);
        }
    }

    protected void onError(AjaxRequestTarget target) {
        target.add(feedback);
    }

    private class UserCalendarForm extends Form<OmCalendar> {

        private TextField<String> username = new TextField<String>("login", Model.of(""));
        private PasswordTextField pass = new PasswordTextField("password", Model.of(""));
        UrlTextField url = new UrlTextField("url", Model.<String>of(""));
        RequiredTextField<String> title = new RequiredTextField<String>("title");

        public UserCalendarForm(String id, CompoundPropertyModel<OmCalendar> model) {
            super(id, model);
            setOutputMarkupId(true);
            add(title);
            add(feedback.setOutputMarkupId(true));

            add(url);
            add(username);

            add(pass.setRequired(false));
        }

        @Override
        protected void onModelChanged() {
            switch (type) {
                case UPDATE_CALENDAR:
                    title.setEnabled(true);
                    if(getModelObject().getId() != null) {
                        url.setEnabled(false);
                        pass.setEnabled(false);
                        username.setEnabled(false);
                    }
                    else{
                        url.setEnabled(true);
                        pass.setEnabled(true);
                        username.setEnabled(true);
                    }
                    break;
                case SYNC_CALENDAR:
                case UPDATE_APPOINTMENT:
                case DELETE_APPOINTMENT:
                    title.setEnabled(false);
                    url.setEnabled(false);
                    username.setEnabled(true);
                    pass.setEnabled(true);
                    break;
            }
            username.setModelObject("");
        }

        @Override
        protected void onValidate() {
            switch (type){
                case UPDATE_CALENDAR:
                    if(getModelObject().getId() != null)
                        return;
                case UPDATE_APPOINTMENT:
                case DELETE_APPOINTMENT:
                case SYNC_CALENDAR:
                    AppointmentManager appointmentManager = calendarPanel.getAppointmentManager();
                    try {
                        OmCalendar calendar = getModelObject();
                        if(url.isEnabled())
                            calendar.setHref(url.getInput());
                        appointmentManager.provideCredentials(calendar, new UsernamePasswordCredentials(username.getInput(),
                                pass.getInput()));
                        if(appointmentManager.testConnection(calendar))
                            return;
                    } catch (Exception e) {
                        log.error("Error executing the TestConnection");
                    }

                    error(Application.getString("caldav.error"));
                    break;
            }
        }
    }
}