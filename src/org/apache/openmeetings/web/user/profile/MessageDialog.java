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
package org.apache.openmeetings.web.user.profile;

import static org.apache.openmeetings.web.util.RoomTypeDropDown.getRoomTypes;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.user.PrivateMessage;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.openmeetings.web.util.UserAutoCompleteTextField;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class MessageDialog extends AbstractFormDialog<PrivateMessage> {
	private static final long serialVersionUID = 1L;
	private final Form<PrivateMessage> form;
	private DialogButton send = new DialogButton(WebSession.getString(218));
	private DialogButton cancel = new DialogButton(WebSession.getString(219));
	private final WebMarkupContainer roomParamsBlock = new WebMarkupContainer("roomParamsBlock");
	private final WebMarkupContainer roomParams = new WebMarkupContainer("roomParams");
	private final IModel<Date> modelStart = Model.of(new Date());
	private final IModel<Date> modelEnd = Model.of(new Date());

	@Override
	public int getWidth() {
		return 650;
	}
	
	@Override
	protected void onOpen(AjaxRequestTarget target) {
		modelStart.setObject(new Date());
		modelEnd.setObject(new Date()); //TODO should we add 1 hour or generalize with Calendar???
		PrivateMessage p = new PrivateMessage();
		Room r = new Room();
		r.setAppointment(true);
		r.setRoomtype(getRoomTypes().get(0));
		p.setRoom(r);
		setModelObject(p);
		roomParams.setVisible(getModelObject().isBookedRoom());
		form.setModelObject(p);
		target.add(form);
		super.onOpen(target);
	}
	
	public MessageDialog(String id, CompoundPropertyModel<PrivateMessage> model) {
		super(id, WebSession.getString(1209), model);
		form = new Form<PrivateMessage>("form", getModel());
		
		form.add(new UserAutoCompleteTextField("to"));
		form.add(new TextField<String>("subject"));
		form.add(new TextArea<String>("message"));
		form.add(roomParamsBlock.setOutputMarkupId(true));
		final CheckBox bookedRoom = new CheckBox("bookedRoom");
		form.add(bookedRoom.setOutputMarkupId(true).add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				PrivateMessage p = MessageDialog.this.getModelObject();
				p.setBookedRoom(!p.isBookedRoom());
				roomParams.setVisible(p.isBookedRoom());
				target.add(bookedRoom, roomParamsBlock);
			}
		}));
		roomParamsBlock.add(roomParams);
		roomParams.add(new RoomTypeDropDown("room.roomtype"));
		roomParams.add(new DateTimeField("start", modelStart));
		roomParams.add(new DateTimeField("end", modelEnd));
		add(form.setOutputMarkupId(true));
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(send, cancel);
	}
	
	@Override
	protected DialogButton getSubmitButton() {
		return send;
	}

	@Override
	public Form<PrivateMessage> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		// TODO Auto-generated method stub

	}
}
