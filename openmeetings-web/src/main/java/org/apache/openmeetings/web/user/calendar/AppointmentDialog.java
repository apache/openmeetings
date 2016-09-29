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

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.JQueryUIBehavior;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.DefaultWysiwygToolbar;
import com.googlecode.wicket.jquery.ui.widget.dialog.*;
import com.googlecode.wicket.kendo.ui.form.datetime.local.DateTimePicker;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.FormatHelper;
import org.apache.openmeetings.service.calendar.caldav.AppointmentManager;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.OmDateTimePicker;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.user.rooms.RoomEnterBehavior;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.openmeetings.web.util.UserMultiChoice;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.*;
import org.apache.wicket.model.util.CollectionModel;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.threeten.bp.LocalDateTime;

import java.util.*;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getDate;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getDateTime;

public class AppointmentDialog extends AbstractFormDialog<Appointment> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentDialog.class, webAppRootKey);
	
	private AppointmentForm form;
	private DialogButton save = new DialogButton("save", Application.getString(813));
	private DialogButton cancel = new DialogButton("cancel", Application.getString(1130));
	private DialogButton delete = new DialogButton("delete", Application.getString(814));
	private DialogButton enterRoom = new DialogButton("enterRoom", Application.getString(1282));
	private final CalendarPanel calendarPanel;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	final MessageDialog confirmDelete;
	private IModel<Collection<User>> attendeesModel = new CollectionModel<User>(new ArrayList<User>());
	private final WebMarkupContainer sipContainer = new WebMarkupContainer("sip-container");
	
	@Override
	public int getWidth() {
		return 650;
	}
	
	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("dialogClass", Options.asString("appointment"));
	}
	
	public void setModelObjectWithAjaxTarget(Appointment a, AjaxRequestTarget target) {
		form.setModelObject(a);
		form.start.setModelObject(getDateTime(a.getStart()));
		form.end.setModelObject(getDateTime(a.getEnd()));
		form.setEnabled(isOwner(a));
		log.debug(" -- setModelObjectWithAjaxTarget -- Current model " + a);
		if (a.getId() != null) {
			delete.setVisible(isOwner(a), target);
			enterRoom.setVisible(a.getRoom() != null, target);
		} else {
			delete.setVisible(false, target);
			enterRoom.setVisible(false, target);
		}
		if (a.getRoom() != null) {
			target.add(sipContainer.replace(new Label("room.confno", a.getRoom().getConfno())).setVisible(a.getRoom().isSipEnabled()));
		}
		save.setVisible(isOwner(a), target);
		super.setModelObject(a);
	}
	
	public AppointmentDialog(String id, String title, CalendarPanel calendarPanel, CompoundPropertyModel<Appointment> model) {
		super(id, title, model, true);
		log.debug(" -- AppointmentDialog -- Current model " + getModel().getObject());
		this.calendarPanel = calendarPanel;
		setOutputMarkupId(true);
		form = new AppointmentForm("appForm", model);
		add(form);
		confirmDelete = new MessageDialog("confirmDelete", Application.getString(814), Application.getString(833), DialogButtons.OK_CANCEL, DialogIcon.WARN){
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				if (button != null && button.match(AbstractDialog.OK)){
					deleteAppointment(handler);
				}
			}
		};
		add(confirmDelete);
	}

	protected void deleteAppointment(IPartialPageRequestHandler handler) {
		Appointment a = getModelObject();
		getBean(AppointmentDao.class).delete(a, getUserId());
		calendarPanel.refresh(handler);
		if(a.getCalendar() != null && a.getHref() != null)
			calendarPanel.updatedeleteAppointment(handler, CalendarDialog.DIALOG_TYPE.DELETE_APPOINTMENT, a);
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(enterRoom, save, delete, cancel);
	}
	
	@Override
	public DialogButton getSubmitButton() {
		return save;
	}

	@Override
	public Form<?> getForm() {
		return form;
	}

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		handler.add(form.add(new JQueryUIBehavior("#tabs", "tabs")));
	}
	
	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		if (delete.equals(button)) {
			confirmDelete.open(handler);
		} else if (enterRoom.equals(button)) {
			RoomEnterBehavior.roomEnter((MainPage)getPage(), handler, getModelObject().getRoom().getId());
		}
	}
	
	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		Appointment a = form.getModelObject();
		a.setRoom(form.createRoom ? form.appRoom : form.groom.getModelObject());
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
		a.setStart(getDate(form.start.getModelObject()));
		a.setEnd(getDate(form.end.getModelObject()));
		a.setCalendar(form.cals.getModelObject());
		getBean(AppointmentDao.class).update(a, getUserId());
		if(a.getCalendar() != null)
			calendarPanel.updatedeleteAppointment(target, CalendarDialog.DIALOG_TYPE.UPDATE_APPOINTMENT, a);
		target.add(feedback);
		calendarPanel.refresh(target);
	}
	
	public static boolean isOwner(Appointment object) {
		return object.getOwner() != null && getUserId().equals(object.getOwner().getId());
	}
	
	@Override
	protected void onDetach() {
		attendeesModel.detach();
		super.onDetach();
	}
	
	private class AppointmentForm extends Form<Appointment> {
		private static final long serialVersionUID = 1L;
		private boolean createRoom = true;
		private Room appRoom = null;
		private final DateTimePicker start = new OmDateTimePicker("start", Model.of(LocalDateTime.now()));
		private final DateTimePicker end = new OmDateTimePicker("end", Model.of(LocalDateTime.now()));
		private final PasswordTextField pwd = new PasswordTextField("password");
		private final Label owner = new Label("aowner", Model.of(""));
		private final WebMarkupContainer ownerPanel = new WebMarkupContainer("owner-row");
		private final WebMarkupContainer createRoomBlock = new WebMarkupContainer("create-room-block", new CompoundPropertyModel<Room>(appRoom));
		private final DropDownChoice<Room.Type> roomType = new RoomTypeDropDown("type");
		private final DropDownChoice<Room> groom = new DropDownChoice<Room>(
				"groom"
				, Model.of(new Room())
				, getRoomList()
				, new ChoiceRenderer<Room>("name", "id"));
		private DropDownChoice<OmCalendar> cals = new DropDownChoice<OmCalendar>(
				"calendar",
				new LoadableDetachableModel<List<? extends OmCalendar>>() {
					@Override
					protected List<? extends OmCalendar> load() {
						return getCalendarList();
					}
				},
				new ChoiceRenderer<OmCalendar>("title", "id")
		);

		private Room createAppRoom() {
			Room r = new Room();
			r.setAppointment(true);
			if (r.getType() == null) {
				r.setType(Room.Type.conference);
			}
			return r;
		}
		
		@Override
		protected void onModelChanged() {
			super.onModelChanged();

			Appointment a = getModelObject();
			if (a.getReminder() == null) {
				a.setReminder(Reminder.none);
			}
			if (a.getRoom() == null) {
				a.setRoom(createAppRoom());
			}
			createRoom = a.getRoom().isAppointment();
			if (createRoom) {
				appRoom = a.getRoom();
			} else {
				groom.setModelObject(a.getRoom());
				appRoom = createAppRoom();
			}
			createRoomBlock.setDefaultModelObject(appRoom);
			createRoomBlock.setEnabled(createRoom);
			groom.setEnabled(!createRoom);
			if (a.getId() == null) {
				java.util.Calendar start = WebSession.getCalendar();
				start.setTime(a.getStart());
				java.util.Calendar end = WebSession.getCalendar();
				end.setTime(a.getEnd());
				
				if (start.equals(end)) {
					end.add(java.util.Calendar.HOUR_OF_DAY, 1);
					a.setEnd(end.getTime());
				}
				cals.setEnabled(true);
			} else
				cals.setEnabled(false);

			attendeesModel.setObject(new ArrayList<User>());
			if (a.getMeetingMembers() != null) {
				for (MeetingMember mm : a.getMeetingMembers()) {
					attendeesModel.getObject().add(mm.getUser());
				}
			}
			pwd.setEnabled(a.isPasswordProtected());
			owner.setDefaultModel(Model.of(FormatHelper.formatUser(a.getOwner())));
			ownerPanel.setVisible(!isOwner(a));
		}
		
		public AppointmentForm(String id, CompoundPropertyModel<Appointment> model) {
			super(id, model);
			setOutputMarkupId(true);
			
			add(feedback.setOutputMarkupId(true));
			//General
			add(new RequiredTextField<String>("title").setLabel(Model.of(Application.getString(572))));
			add(start);
			add(end);
			add(ownerPanel.add(owner));
			add(new UserMultiChoice("attendees", attendeesModel));
			add(new TextField<String>("location"));
			DefaultWysiwygToolbar toolbar = new DefaultWysiwygToolbar("toolbarContainer");
			add(toolbar);
			add(new WysiwygEditor("description", toolbar));
			
			//room
			add(new AjaxCheckBox("createRoom", new PropertyModel<Boolean>(this, "createRoom")) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					createRoom = getConvertedInput();
					target.add(createRoomBlock.setEnabled(createRoom), groom.setEnabled(!createRoom));
				}
			});
			add(createRoomBlock.add(roomType, new CheckBox("moderated")).setEnabled(createRoom).setOutputMarkupId(true));
			add(groom.setRequired(true).setLabel(Model.of(Application.getString(406))).setEnabled(!createRoom).setOutputMarkupId(true));
			add(sipContainer.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
			sipContainer.add(new Label("room.confno", "")).setVisible(false);
			
			//Advanced
			add(new DropDownChoice<Reminder>(
					"reminder"
					, Arrays.asList(Reminder.values())
					, new IChoiceRenderer<Reminder>() {
						private static final long serialVersionUID = 1L;

						@Override
						public Object getDisplayValue(Reminder art) {
							return getString("appointment.reminder." + art.name());
						}

						@Override
						public String getIdValue(Reminder art, int index) {
							return art.name();
						}

						@Override
						public Reminder getObject(String id, IModel<? extends List<? extends Reminder>> choices) {
							for (Reminder art : choices.getObject()) {
								if (art.name().equals(id)) {
									return art;
								}
							}
							return null;
						}
					}));
			add(new AjaxCheckBox("passwordProtected") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					AppointmentForm.this.getModelObject().setPasswordProtected(getConvertedInput());
					pwd.setEnabled(AppointmentForm.this.getModelObject().isPasswordProtected());
					target.add(pwd);
				}
			});
			pwd.setEnabled(getModelObject().isPasswordProtected());
			pwd.setOutputMarkupId(true);
			add(pwd);
			add(cals.setNullValid(true).setLabel(Model.of("calendar")).setOutputMarkupId(true));
		}
		
		private List<Room> getRoomList() {
			//FIXME need to be reviewed
			List<Room> result = new ArrayList<Room>();
			RoomDao dao = getBean(RoomDao.class);
			result.addAll(dao.getPublicRooms());
			for (GroupUser ou : getBean(UserDao.class).get(getUserId()).getGroupUsers()) {
				result.addAll(dao.getGroupRooms(ou.getGroup().getId()));
			}
			if (getModelObject().getRoom() != null && getModelObject().getRoom().isAppointment()) { //FIXME review
				result.add(getModelObject().getRoom());
			}
			return result;
		}

		private List<OmCalendar> getCalendarList(){
			return getBean(AppointmentManager.class).getCalendars(getUserId());
		}
		
		@Override
		protected void onValidate() {
			if (end.getConvertedInput().isBefore(start.getConvertedInput())) {
				error(Application.getString(1592));
			}
		}
	}
}
