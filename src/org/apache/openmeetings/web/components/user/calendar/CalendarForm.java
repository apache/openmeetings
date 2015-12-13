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

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.calendar.daos.AppointmentReminderTypDao;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentReminderTyps;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.room.RoomType;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.web.app.Application;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class CalendarForm extends Form<Appointment> {
	private static final long serialVersionUID = -1764738237821487526L;
	private boolean createRoom = true;

	public CalendarForm(String id, IModel<Appointment> model) {
		super(id, model);
		setOutputMarkupId(true);
		
		add(new RequiredTextField<String>("appointmentName"));
		add(new TextArea<String>("appointmentDescription"));
		add(new TextField<String>("appointmentLocation"));
		add(new DateTimeField("appointmentStarttime"));
		add(new DateTimeField("appointmentEndtime"));
		final PasswordTextField pwd = new PasswordTextField("password");
		pwd.setEnabled(isPwdProtected());
		pwd.setOutputMarkupId(true);
		add(pwd);
		
		add(new DropDownChoice<AppointmentReminderTyps>(
				"remind"
				, Application.getBean(AppointmentReminderTypDao.class).getAppointmentReminderTypList()
				, new ChoiceRenderer<AppointmentReminderTyps>("name", "typId")));
		
		final DropDownChoice<RoomType> roomType = new DropDownChoice<RoomType>(
				"room.roomtype"
				, Application.getBean(RoomManager.class).getAllRoomTypes()
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
				CalendarForm.this.getModelObject().setIsPasswordProtected(getConvertedInput());
				pwd.setEnabled(isPwdProtected());
				target.add(pwd);
			}
		});
	}
	
	private boolean isPwdProtected() {
		return Boolean.TRUE.equals(getModelObject().getIsPasswordProtected());
	}
	
	private List<Room> getRoomList() {
		//FIXME need to be reviewed
		List<Room> result = new ArrayList<Room>();
		RoomDao dao = Application.getBean(RoomDao.class);
		result.addAll(dao.getPublicRooms());
		for (Organisation_Users ou : Application.getBean(UsersDao.class).get(WebSession.getUserId()).getOrganisation_users()) {
			result.addAll(dao.getOrganisationRooms(ou.getOrganisation().getOrganisation_id()));
		}
		if (getModelObject().getRoom() != null && getModelObject().getRoom().getAppointment()) { //FIXME review
			result.add(getModelObject().getRoom());
		}
		return result;
	}
}
