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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentReminderTypDao;
import org.apache.openmeetings.data.calendar.management.AppointmentLogic;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentReminderTyps;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.room.RoomType;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class AppointmentDialog extends AbstractFormDialog<Appointment> {
	private static final long serialVersionUID = 7553035786264113827L;
	private AppointmentForm form;
	private DialogButton save = new DialogButton(WebSession.getString(813));
	private DialogButton cancel = new DialogButton(WebSession.getString(1130));
	private DialogButton delete = new DialogButton(WebSession.getString(814));
	private final CalendarPanel calendar;
	protected final FeedbackPanel feedback;
	
	@Override
	public void setModelObject(Appointment object) {
		form.setModelObject(object);
		super.setModelObject(object);
	}
	
	public AppointmentDialog(String id, String title, CalendarPanel calendar, IModel<Appointment> model) {
		super(id, title, model, true);
		this.calendar = calendar;
		setOutputMarkupId(true);
		feedback = new FeedbackPanel("feedback");
		form = new AppointmentForm("appForm", new CompoundPropertyModel<Appointment>(this.getModel()));
		add(form);
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(cancel, delete, save);
	}
	
	@Override
	protected DialogButton getSubmitButton() {
		return save;
	}

	@Override
	public Form<?> getForm() {
		return this.form;
	}

	@Override
	protected void onOpen(AjaxRequestTarget target) {
		target.add(this.form);
	}
	
	@Override
	public void onClose(AjaxRequestTarget target, DialogButton button) {
		if (delete.equals(button)) {
			getBean(AppointmentLogic.class).deleteAppointment(getModelObject().getAppointmentId(), getUserId(), getLanguage());
			calendar.refresh(target);
		}
	}
	
	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		getBean(AppointmentDao.class).update(form.getModelObject(), getUserId());
		target.add(feedback);
		calendar.refresh(target);
	}
	
	private class AppointmentForm extends Form<Appointment> {
		private static final long serialVersionUID = -1764738237821487526L;
		private boolean createRoom = true;

		@Override
		protected void onModelChanged() {
			super.onModelChanged();
			
			Appointment a = getModelObject();
			
			List<AppointmentReminderTyps> remindTypes = getRemindTypes();
			if (a.getRemind() == null && !remindTypes.isEmpty()) {
				a.setRemind(remindTypes.get(0));
			}
			
			List<RoomType> roomTypes = getRoomTypes();
			if (a.getRoom() == null) {
				Room r = new Room();
				r.setAppointment(true);
				a.setRoom(r);
			}
			if (a.getRoom().getRoomtype() == null && !roomTypes.isEmpty()) {
				a.getRoom().setRoomtype(roomTypes.get(0));
			}
			if (a.getAppointmentId() == null) {
				java.util.Calendar start = WebSession.getCalendar();
				start.setTime(a.getAppointmentStarttime());
				java.util.Calendar end = WebSession.getCalendar();
				end.setTime(a.getAppointmentEndtime());
				
				if (start.equals(end)) {
					end.add(java.util.Calendar.HOUR_OF_DAY, 1);
					a.setAppointmentEndtime(end.getTime());
				}
			}
		}
		
		public AppointmentForm(String id, IModel<Appointment> model) {
			super(id, model);
			setOutputMarkupId(true);
			
			add(feedback.setOutputMarkupId(true));
			add(new RequiredTextField<String>("appointmentName").setLabel(Model.of(WebSession.getString(572))));
			add(new TextArea<String>("appointmentDescription"));
			add(new TextField<String>("appointmentLocation"));
			add(new DateTimeField("appointmentStarttime"));
			add(new DateTimeField("appointmentEndtime"));
			final PasswordTextField pwd = new PasswordTextField("password");
			pwd.setEnabled(isPwdProtected());
			pwd.setOutputMarkupId(true);
			add(pwd);
			
			List<AppointmentReminderTyps> remindTypes = getRemindTypes();
			add(new DropDownChoice<AppointmentReminderTyps>(
					"remind"
					, remindTypes
					, new ChoiceRenderer<AppointmentReminderTyps>("name", "typId")));
			
			List<RoomType> roomTypes = getRoomTypes();
			final DropDownChoice<RoomType> roomType = new DropDownChoice<RoomType>(
					"room.roomtype"
					, roomTypes
					, new ChoiceRenderer<RoomType>("name", "roomtypes_id"));
			roomType.setEnabled(createRoom);
			roomType.setOutputMarkupId(true);
			add(roomType);
			
			final DropDownChoice<Room> room = new DropDownChoice<Room>(
					"room"
					, getRoomList()
					, new ChoiceRenderer<Room>("name", "rooms_id"));
			room.setEnabled(!createRoom);
			room.setOutputMarkupId(true);
			add(room);
			add(new AjaxCheckBox("createRoom", new PropertyModel<Boolean>(this, "createRoom")) {
				private static final long serialVersionUID = -3743113990890386035L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					createRoom = getConvertedInput();
					target.add(roomType.setEnabled(createRoom), room.setEnabled(!createRoom));
				}
			});
			add(new AjaxCheckBox("isPasswordProtected") {
				private static final long serialVersionUID = 6041200584296439976L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					AppointmentForm.this.getModelObject().setIsPasswordProtected(getConvertedInput());
					pwd.setEnabled(isPwdProtected());
					target.add(pwd);
				}
			});
		}
		
		private boolean isPwdProtected() {
			return Boolean.TRUE.equals(getModelObject().getIsPasswordProtected());
		}
		
		private List<RoomType> getRoomTypes() {
			return getBean(RoomManager.class).getAllRoomTypes(getLanguage());
		}
		
		private List<AppointmentReminderTyps> getRemindTypes() {
			return getBean(AppointmentReminderTypDao.class).getAppointmentReminderTypList();
		}
		
		private List<Room> getRoomList() {
			//FIXME need to be reviewed
			List<Room> result = new ArrayList<Room>();
			RoomDao dao = getBean(RoomDao.class);
			result.addAll(dao.getPublicRooms());
			for (Organisation_Users ou : getBean(UsersDao.class).get(getUserId()).getOrganisation_users()) {
				result.addAll(dao.getOrganisationRooms(ou.getOrganisation().getOrganisation_id()));
			}
			if (getModelObject().getRoom() != null && getModelObject().getRoom().getAppointment()) { //FIXME review
				result.add(getModelObject().getRoom());
			}
			return result;
		}
	}
}
