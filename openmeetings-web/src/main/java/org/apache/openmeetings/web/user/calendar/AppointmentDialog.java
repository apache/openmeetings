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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.RoomTypeDropDown.getRoomTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentReminderTypDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.AppointmentReminderType;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomType;
import org.apache.openmeetings.db.entity.user.OrganisationUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.user.rooms.RoomEnterBehavior;
import org.apache.openmeetings.web.util.FormatHelper;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.openmeetings.web.util.UserMultiChoice;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.CollectionModel;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.DefaultWysiwygToolbar;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;
import com.googlecode.wicket.kendo.ui.form.datetime.DateTimePicker;

public class AppointmentDialog extends AbstractFormDialog<Appointment> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentDialog.class, webAppRootKey);
	
	private AppointmentForm form;
	private DialogButton save = new DialogButton(WebSession.getString(813));
	private DialogButton cancel = new DialogButton(WebSession.getString(1130));
	private String deleteLbl = WebSession.getString(814);
	private DialogButton delete = new DialogButton(deleteLbl);
	private String enterRoomLbl = WebSession.getString(1282);
	private DialogButton enterRoom = new DialogButton(enterRoomLbl);
	private final CalendarPanel calendarPanel;
	protected final FeedbackPanel feedback;
	final MessageDialog confirmDelete;
	private IModel<Collection<User>> attendeesModel = new CollectionModel<User>(new ArrayList<User>());
	
	@Override
	public int getWidth() {
		return 650;
	}
	
	public void setModelObjectWithAjaxTarget(Appointment object, AjaxRequestTarget target) {
		form.setModelObject(object);
		form.setEnabled(isOwner(object));
		log.debug(" -- setModelObjectWithAjaxTarget -- Current model " + object);
		if (object.getId() != null) {
			delete.setVisible(isOwner(object), target);
			enterRoom.setVisible(object.getRoom() != null, target);
		} else {
			delete.setVisible(false, target);
			enterRoom.setVisible(false, target);
		}
		save.setVisible(isOwner(object), target);
		super.setModelObject(object);
	}
	
	public AppointmentDialog(String id, String title, CalendarPanel calendarPanel, IModel<Appointment> model) {
		super(id, title, model, true);
		log.debug(" -- AppointmentDialog -- Current model " + getModel().getObject());
		this.calendarPanel = calendarPanel;
		setOutputMarkupId(true);
		feedback = new FeedbackPanel("feedback");
		form = new AppointmentForm("appForm", model);
		add(form);
		confirmDelete = new MessageDialog("confirmDelete", WebSession.getString(814), WebSession.getString(833), DialogButtons.OK_CANCEL, DialogIcon.WARN){
			private static final long serialVersionUID = 1L;

			public void onClose(AjaxRequestTarget target, DialogButton button) {
				if (button != null && button.match(AbstractDialog.LBL_OK)){
					deleteAppointment(target);
				}
			}
		};
		add(confirmDelete);
	}

	protected void deleteAppointment(AjaxRequestTarget target) {
		getBean(AppointmentDao.class).delete(getModelObject(), getUserId());
		calendarPanel.refresh(target);		
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
			RoomEnterBehavior.roomEnter((MainPage)getPage(), target, getModelObject().getRoom().getId());
		}
	}
	
	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
        Appointment a = form.getModelObject();
        final List<MeetingMember> attendees = a.getMeetingMembers() == null ? new ArrayList<MeetingMember>() : a.getMeetingMembers();
        Set<Long> currentIds = new HashSet<Long>();
        for (User u : attendeesModel.getObject()) {
        	if (u.getId() != null) {
        		currentIds.add(u.getId());
        	}
        }
        
        //remove users
        for (Iterator<MeetingMember> i = attendees.iterator(); i.hasNext();) {
        	MeetingMember m = i.next();
        	if (!currentIds.contains(m.getUser().getId())) {
        		i.remove();
        	}
        }
        Set<Long> originalIds = new HashSet<Long>();
        for (MeetingMember m : attendees) {
        	originalIds.add(m.getUser().getId());
        }
        //add users
        for (User u : attendeesModel.getObject()) {
        	if (u.getId() == null || !originalIds.contains(u.getId())) {
        		MeetingMember mm = new MeetingMember();
        		mm.setUser(u);
        		mm.setDeleted(false);
        		mm.setInserted(a.getInserted());
        		mm.setUpdated(a.getUpdated());
        		mm.setAppointment(a);
        		attendees.add(mm);
        	}
        }
        a.setMeetingMembers(attendees);
        getBean(AppointmentDao.class).update(a, getUserId());
		target.add(feedback);
		calendarPanel.refresh(target);
	}
	
	public static boolean isOwner(Appointment object) {
		return object.getOwner() != null && getUserId() == object.getOwner().getId();
	}
	
	private class AppointmentForm extends Form<Appointment> {
		private static final long serialVersionUID = 1L;
		private boolean createRoom = true;
		private DateTimePicker start;
		private DateTimePicker end;
		private final PasswordTextField pwd = new PasswordTextField("password");
		private final Label owner = new Label("aowner", Model.of(""));
		private final DropDownChoice<RoomType> roomType = new RoomTypeDropDown("room.roomtype");
		private final DropDownChoice<Room> room = new DropDownChoice<Room>(
				"room"
				, getRoomList()
				, new ChoiceRenderer<Room>("name", "id"));

		@Override
		protected void onModelChanged() {
			super.onModelChanged();
			
			Appointment a = getModelObject();
			List<AppointmentReminderType> remindTypes = getRemindTypes();
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
			createRoom = a.getRoom().isAppointment();
			roomType.setEnabled(createRoom);
			room.setEnabled(!createRoom);
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
			attendeesModel.setObject(new ArrayList<User>());
			if (a.getMeetingMembers() != null) {
				for (MeetingMember mm : a.getMeetingMembers()) {
					attendeesModel.getObject().add(mm.getUser());
				}
			}
			pwd.setEnabled(a.isPasswordProtected());
			owner.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true);
			owner.setDefaultModel(Model.of(FormatHelper.formatUser(a.getOwner())));
			owner.setVisible(a.getOwner() != null);
		}
		
		public AppointmentForm(String id, IModel<Appointment> model) {
			super(id, model);
			setOutputMarkupId(true);
			
			add(feedback.setOutputMarkupId(true));
			add(new RequiredTextField<String>("title").setLabel(Model.of(WebSession.getString(572))));
			DefaultWysiwygToolbar toolbar = new DefaultWysiwygToolbar("toolbarContainer");
			add(toolbar);
			add(new WysiwygEditor("description", toolbar));
			add(new TextField<String>("location"));
			add(start = new DateTimePicker("start", "yyyy/MM/dd", "HH:mm:ss")); //FIXME use user locale
			add(end = new DateTimePicker("end", "yyyy/MM/dd", "HH:mm:ss")); //FIXME use user locale
			pwd.setEnabled(getModelObject().isPasswordProtected());
			pwd.setOutputMarkupId(true);
			add(pwd);
			
			List<AppointmentReminderType> remindTypes = getRemindTypes();
			add(new DropDownChoice<AppointmentReminderType>(
					"remind"
					, remindTypes
					, new ChoiceRenderer<AppointmentReminderType>("label.value", "id")));
			
			roomType.setEnabled(createRoom);
			roomType.setOutputMarkupId(true);
			add(roomType);
			
			room.setEnabled(!createRoom);
			room.setOutputMarkupId(true);
			add(room);
			add(new AjaxCheckBox("createRoom", new PropertyModel<Boolean>(this, "createRoom")) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					createRoom = getConvertedInput();
					target.add(roomType.setEnabled(createRoom), room.setEnabled(!createRoom));
				}
			});
			add(new AjaxCheckBox("passwordProtected") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					AppointmentForm.this.getModelObject().setPasswordProtected(getConvertedInput());
					pwd.setEnabled(AppointmentForm.this.getModelObject().isPasswordProtected());
					target.add(pwd);
				}
			});
			add(new UserMultiChoice("attendees", attendeesModel));
				
			add(owner);
		}
		
		private List<AppointmentReminderType> getRemindTypes() {
			return getBean(AppointmentReminderTypDao.class).getList(getLanguage());
		}
		
		private List<Room> getRoomList() {
			//FIXME need to be reviewed
			List<Room> result = new ArrayList<Room>();
			RoomDao dao = getBean(RoomDao.class);
			result.addAll(dao.getPublicRooms());
			for (OrganisationUser ou : getBean(UserDao.class).get(getUserId()).getOrganisationUsers()) {
				result.addAll(dao.getOrganisationRooms(ou.getOrganisation().getId()));
			}
			if (getModelObject().getRoom() != null && getModelObject().getRoom().isAppointment()) { //FIXME review
				result.add(getModelObject().getRoom());
			}
			return result;
		}
		
		@Override
		protected void onValidate() {
			if (end.getConvertedInput().before(start.getConvertedInput())) {
				error(WebSession.getString(1592));
			}
		}
	}
}
