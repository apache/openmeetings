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

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getBaseUrl;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.RoomTypeDropDown.getRoomTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentReminderTypDao;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.user.dao.UserDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentReminderTyps;
import org.apache.openmeetings.persistence.beans.calendar.MeetingMember;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.room.RoomType;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.user.rooms.RoomEnterBehavior;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.DefaultWysiwygToolbar;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

public class AppointmentDialog extends AbstractFormDialog<Appointment> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentDialog.class, webAppRootKey);
	
	private AppointmentForm form;
	private DialogButton save = new DialogButton(WebSession.getString(813));
	private DialogButton cancel = new DialogButton(WebSession.getString(1130));
	private DialogButton delete = new DialogButton(WebSession.getString(814));
	private DialogButton enterRoom = new DialogButton(WebSession.getString(1282));
	private final CalendarPanel calendar;
	protected final FeedbackPanel feedback;
	final MeetingMemberDialog addAttendees;
	final MessageDialog confirmDelete;
	
	@Override
	public int getWidth() {
		return 650;
	}
	
	//@Override
	public void setModelObjectWithAjaxTarget(Appointment object, AjaxRequestTarget target) {
		form.setModelObject(object);
		form.setEnabled(object.getOwner() == null || getUserId() == object.getOwner().getUser_id());
		log.debug(" -- setModelObjectWithAjaxTarget -- Current model " + object);
		if (object.getId() != null) {
			delete.setVisible(true, target);
			enterRoom.setVisible(object.getRoom() != null, target);
		} else {
			delete.setVisible(false, target);
			enterRoom.setVisible(false, target);
		}
		super.setModelObject(object);
	}
	
	public AppointmentDialog(String id, String title, CalendarPanel calendar, IModel<Appointment> model) {
		super(id, title, model, true);
		log.debug(" -- AppointmentDialog -- Current model " + getModel().getObject());
		this.calendar = calendar;
		setOutputMarkupId(true);
		feedback = new FeedbackPanel("feedback");
		form = new AppointmentForm("appForm", model);
		add(form);
		addAttendees = new MeetingMemberDialog("addAttendees", WebSession.getString(812), model, form.get("attendeeContainer"));
		add(addAttendees);
		confirmDelete = new MessageDialog("confirmDelete", WebSession.getString(814), WebSession.getString(833), DialogButtons.OK_CANCEL, DialogIcon.WARN){
			private static final long serialVersionUID = 1L;

			public void onClose(AjaxRequestTarget target, DialogButton button) {
				if (button.equals(DialogButtons.OK)){
					deleteAppointment(target);
				}
			}
		};
		add(confirmDelete);
	}

	protected void deleteAppointment(AjaxRequestTarget target) {
		getBean(AppointmentDao.class).delete(getModelObject(), getBaseUrl(), getUserId());
		calendar.refresh(target);		
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(enterRoom, save, delete, cancel);
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
			confirmDelete.open(target);
		} else if (enterRoom.equals(button)) {
			RoomEnterBehavior.roomEnter((MainPage)getPage(), target, getModelObject().getRoom().getRooms_id());
		}
	}
	
	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		getBean(AppointmentDao.class).update(form.getModelObject(), getBaseUrl(), getUserId());
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
			if (a.getId() == null) {
				java.util.Calendar start = WebSession.getCalendar();
				start.setTime(a.getStart());
				java.util.Calendar end = WebSession.getCalendar();
				end.setTime(a.getEnd());
				
				if (start.equals(end)) {
					end.add(java.util.Calendar.HOUR_OF_DAY, 1);
					a.setEnd(end.getTime());
				}
			}
		}
		
		public AppointmentForm(String id, IModel<Appointment> model) {
			super(id, model);
			setOutputMarkupId(true);
			add(new AttributeAppender("class", new Model<String>("appointmentPopUp"), " "));
			
			add(feedback.setOutputMarkupId(true));
			add(new RequiredTextField<String>("title").setLabel(Model.of(WebSession.getString(572))));
			DefaultWysiwygToolbar toolbar = new DefaultWysiwygToolbar("toolbarContainer");
			add(toolbar);
			add(new WysiwygEditor("description", toolbar));
			add(new TextField<String>("location"));
			add(new DateTimeField("start"));
			add(new DateTimeField("end"));
			final PasswordTextField pwd = new PasswordTextField("password");
			pwd.setEnabled(getModelObject().isPasswordProtected());
			pwd.setOutputMarkupId(true);
			add(pwd);
			
			List<AppointmentReminderTyps> remindTypes = getRemindTypes();
			add(new DropDownChoice<AppointmentReminderTyps>(
					"remind"
					, remindTypes
					, new ChoiceRenderer<AppointmentReminderTyps>("label.value", "typId")));
			
			final DropDownChoice<RoomType> roomType = new RoomTypeDropDown("room.roomtype");
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
			add(new AjaxCheckBox("passwordProtected") {
				private static final long serialVersionUID = 6041200584296439976L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					AppointmentForm.this.getModelObject().setPasswordProtected(getConvertedInput());
					pwd.setEnabled(AppointmentForm.this.getModelObject().isPasswordProtected());
					target.add(pwd);
				}
			});
			
			final WebMarkupContainer addMeetingMember = new WebMarkupContainer("addMeetingMember");
			addMeetingMember.add(new AjaxEventBehavior("onclick") {
				private static final long serialVersionUID = 7016550192188649714L;

				protected void onEvent(AjaxRequestTarget target) {
					addAttendees.open(target);
	        	}
	        });
			add(addMeetingMember);
			
			final WebMarkupContainer attendeeContainer = new WebMarkupContainer("attendeeContainer");
			attendeeContainer.add(new ListView<MeetingMember>("meetingMembers"){

				private static final long serialVersionUID = -2609044181991754097L;

				@Override
				protected void populateItem(final ListItem<MeetingMember> item) {
					MeetingMember mm = item.getModelObject();
					item.add(new Label("attendeeName", mm.getUser().getFirstname() + " " + mm.getUser().getLastname()));
					item.add(new Label("attendeeEmail", mm.getUser().getAdresses().getEmail()));
					item.add(new WebMarkupContainer("attendeeDelete").add(new AjaxEventBehavior("onclick"){
						private static final long serialVersionUID = 1L;

						@Override
						protected void onEvent(AjaxRequestTarget target) {
							MeetingMember mm = item.getModelObject();
							AppointmentForm.this.getModelObject().getMeetingMembers().remove(mm);
							target.add(attendeeContainer);
						}
					})); 
				}
			});
			add(attendeeContainer.setOutputMarkupId(true));

		}
		
		private List<AppointmentReminderTyps> getRemindTypes() {
			return getBean(AppointmentReminderTypDao.class).getAppointmentReminderTypList(getLanguage());
		}
		
		private List<Room> getRoomList() {
			//FIXME need to be reviewed
			List<Room> result = new ArrayList<Room>();
			RoomDao dao = getBean(RoomDao.class);
			result.addAll(dao.getPublicRooms());
			for (Organisation_Users ou : getBean(UserDao.class).get(getUserId()).getOrganisation_users()) {
				result.addAll(dao.getOrganisationRooms(ou.getOrganisation().getOrganisation_id()));
			}
			if (getModelObject().getRoom() != null && getModelObject().getRoom().getAppointment()) { //FIXME review
				result.add(getModelObject().getRoom());
			}
			return result;
		}
	}
}
