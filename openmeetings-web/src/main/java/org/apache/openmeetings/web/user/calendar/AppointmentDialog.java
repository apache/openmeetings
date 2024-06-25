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

import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CHANGE;
import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getDate;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getDateTime;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isMyRoomsEnabled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.Appointment.Reminder;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.db.util.FormatHelper;
import org.apache.openmeetings.service.calendar.caldav.AppointmentManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.GroupChoiceProvider;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.common.datetime.OmDateTimePicker;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.user.OmWysiwygToolbar;
import org.apache.openmeetings.web.user.rooms.RoomEnterBehavior;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.openmeetings.web.util.UserMultiChoice;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.LambdaChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.CollectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.select2.Select2MultiChoice;

import org.wicketstuff.jquery.ui.plugins.wysiwyg.WysiwygEditor;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class AppointmentDialog extends Modal<Appointment> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(AppointmentDialog.class);

	private AppointmentForm form;
	private BootstrapAjaxButton save;
	private BootstrapAjaxLink<String> delete;
	private BootstrapAjaxLink<String> enterRoom;
	private final CalendarPanel calendarPanel;
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final WebMarkupContainer sipContainer = new WebMarkupContainer("sip-container");
	private final RadioGroup<InviteeType> rdi = new RadioGroup<>("inviteeType", Model.of(InviteeType.USER));
	private final Select2MultiChoice<Group> groups = new Select2MultiChoice<>("groups"
			, new CollectionModel<>(new ArrayList<>())
			, new GroupChoiceProvider());
	private final UserMultiChoice attendees = new UserMultiChoice("attendees", new CollectionModel<>(new ArrayList<>()));
	private enum InviteeType {
		USER
		, GROUP
	}

	@Inject
	private RoomDao roomDao;
	@Inject
	private UserDao userDao;
	@Inject
	private AppointmentDao apptDao;
	@Inject
	private GroupUserDao groupUserDao;
	@Inject
	private AppointmentManager apptManager;

	public AppointmentDialog(String id, CalendarPanel calendarPanel, CompoundPropertyModel<Appointment> model) {
		super(id, model);
		setMarkupId(id);
		log.debug(" -- AppointmentDialog -- Current model {}", getModel().getObject());
		this.calendarPanel = calendarPanel;
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("815"));
		size(Size.Large);

		add(form = new AppointmentForm("appForm", getModel()));
		addButton(save = new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("144"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				Appointment a = form.getModelObject();
				a.setRoom(form.createRoom ? form.appRoom : form.groom.getModelObject());
				final List<MeetingMember> mms = a.getMeetingMembers() == null ? new ArrayList<>() : a.getMeetingMembers();
				Set<Long> currentIds = new HashSet<>();
				List<User> users = new ArrayList<>();
				if (InviteeType.GROUP == rdi.getModelObject()) {
					//lets iterate through all group users
					for (Group g : groups.getModelObject()) {
						for (GroupUser gu : groupUserDao.get(g.getId(), 0, Integer.MAX_VALUE)) {
							User u = gu.getUser();
							if (!currentIds.contains(u.getId())) {
								users.add(u);
								currentIds.add(u.getId());
							}
						}
					}
				} else {
					users = new ArrayList<>(attendees.getModelObject());
					for (User u : users) {
						if (u.getId() != null) {
							currentIds.add(u.getId());
						}
					}
				}

				//remove users
				for (Iterator<MeetingMember> i = mms.iterator(); i.hasNext();) {
					MeetingMember m = i.next();
					if (!currentIds.contains(m.getUser().getId())) {
						i.remove();
					}
				}
				Set<Long> originalIds = new HashSet<>();
				for (MeetingMember m : mms) {
					originalIds.add(m.getUser().getId());
				}
				//add users
				for (User u : users) {
					if (u.getId() == null || !originalIds.contains(u.getId())) {
						MeetingMember mm = new MeetingMember();
						mm.setUser(u);
						mm.setDeleted(false);
						mm.setAppointment(a);
						mms.add(mm);
					}
				}
				a.setMeetingMembers(mms);
				a.setStart(getDate(form.start.getModelObject()));
				a.setEnd(getDate(form.end.getModelObject()));
				a.setCalendar(form.cals.getModelObject());
				if (a.getCalendar() != null) {
					// Updates on the remote server and sets the href. Should be before dao update
					calendarPanel.updatedeleteAppointment(target, CalendarDialog.DIALOG_TYPE.UPDATE_APPOINTMENT, a);
				}
				apptDao.update(a, getUserId());
				target.add(feedback);
				calendarPanel.refresh(target);
				close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}
		});
		save.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		addButton(enterRoom = new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, null, Buttons.Type.Outline_Success, new ResourceModel("lbl.enter")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget handler) {
				RoomEnterBehavior.roomEnter((MainPage)getPage(), handler, AppointmentDialog.this.getModelObject().getRoom().getId());
			}
		});
		enterRoom.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		enterRoom.add(AttributeModifier.append("data-bs-dismiss", "modal"));
		delete = new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, null, Buttons.Type.Outline_Danger, new ResourceModel("80")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget handler) {
				deleteAppointment(handler);
				AppointmentDialog.this.close(handler);
			}
		};
		delete.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		addButton(delete.add(newOkCancelDangerConfirm(this, getString("833"))));
		addButton(OmModalCloseButton.of());
		super.onInitialize();
	}

	public void setModelObjectWithAjaxTarget(Appointment a, AjaxRequestTarget target) {
		form.setModelObject(a);
		form.start.setModelObject(getDateTime(a.getStart()));
		form.end.setModelObject(getDateTime(a.getEnd()));
		final boolean isOwner = isOwner(a);
		form.setEnabled(isOwner);
		log.debug(" -- setModelObjectWithAjaxTarget -- Current model {}", a);
		if (a.getId() != null) {
			delete.setVisible(isOwner);
			enterRoom.setVisible(a.getRoom() != null);
		} else {
			delete.setVisible(false);
			enterRoom.setVisible(false);
		}
		if (a.getRoom() != null) {
			sipContainer.replace(new Label("room.confno", a.getRoom().getConfno())).setVisible(a.getRoom().isSipEnabled());
		}
		save.setVisible(isOwner);
		target.add(form, delete, enterRoom, save);
		super.setModelObject(a);
	}

	protected void deleteAppointment(IPartialPageRequestHandler handler) {
		Appointment a = getModelObject();
		apptDao.delete(a, getUserId());
		calendarPanel.refresh(handler);
		if (a.getCalendar() != null && a.getHref() != null) {
			calendarPanel.updatedeleteAppointment(handler, CalendarDialog.DIALOG_TYPE.DELETE_APPOINTMENT, a);
		}
	}

	public static boolean isOwner(Appointment object) {
		return object.getOwner() != null && getUserId().equals(object.getOwner().getId());
	}

	private class AppointmentForm extends Form<Appointment> {
		private static final long serialVersionUID = 1L;
		private final boolean myRoomsAllowed;
		private boolean createRoom = true;
		private Room appRoom = new Room();
		private final OmDateTimePicker start = new OmDateTimePicker("start", Model.of(LocalDateTime.now()));
		private final OmDateTimePicker end = new OmDateTimePicker("end", Model.of(LocalDateTime.now()));
		private final PasswordTextField pwd = new PasswordTextField("password");
		private final Label owner = new Label("aowner", Model.of(""));
		private final WebMarkupContainer ownerPanel = new WebMarkupContainer("owner-row");
		private final WebMarkupContainer createRoomBlock = new WebMarkupContainer("create-room-block", new CompoundPropertyModel<>(appRoom));
		private final DropDownChoice<Room.Type> roomType = new RoomTypeDropDown("type");
		private final DropDownChoice<Room> groom = new DropDownChoice<>(
				"groom"
				, Model.of(new Room())
				, getRoomList()
				, new ChoiceRenderer<>("name", "id"));
		private DropDownChoice<OmCalendar> cals = new DropDownChoice<>(
				"calendar",
				new LoadableDetachableModel<List<? extends OmCalendar>>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected List<? extends OmCalendar> load() {
						return getCalendarList();
					}

					private List<OmCalendar> getCalendarList(){
						return apptManager.getCalendars(getUserId());
					}
				},
				new ChoiceRenderer<>("title", "id")
		);
		private final WebMarkupContainer groupContainer = new WebMarkupContainer("groupContainer");

		public AppointmentForm(String id, IModel<Appointment> model) {
			super(id, model);
			setOutputMarkupId(true);

			myRoomsAllowed = isMyRoomsEnabled();
			createRoom = myRoomsAllowed;
		}

		@Override
		protected void onModelChanged() {
			super.onModelChanged();

			Appointment a = getModelObject();
			if (a.getReminder() == null) {
				a.setReminder(Reminder.NONE);
			}
			if (a.getRoom() == null) {
				a.setRoom(createAppRoom());
			}
			createRoom = myRoomsAllowed && a.getRoom().isAppointment();
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
				java.util.Calendar from = WebSession.getCalendar();
				from.setTime(a.getStart());
				java.util.Calendar to = WebSession.getCalendar();
				to.setTime(a.getEnd());

				if (from.equals(to)) {
					to.add(java.util.Calendar.HOUR_OF_DAY, 1);
					a.setEnd(to.getTime());
				}
				cals.setEnabled(true);
			} else {
				cals.setEnabled(false);
			}

			rdi.setModelObject(InviteeType.USER);
			attendees.setModelObject(new ArrayList<>());
			if (a.getMeetingMembers() != null) {
				for (MeetingMember mm : a.getMeetingMembers()) {
					attendees.getModelObject().add(mm.getUser());
				}
			}
			pwd.setEnabled(a.isPasswordProtected());
			owner.setDefaultModel(Model.of(FormatHelper.formatUser(a.getOwner())));
			ownerPanel.setVisible(!isOwner(a));
		}

		@Override
		protected void onInitialize() {

			add(feedback.setOutputMarkupId(true));
			//General
			add(ownerPanel.add(owner));
			boolean showGroups = AuthLevelUtil.hasAdminLevel(getRights()) || AuthLevelUtil.hasGroupAdminLevel(getRights());
			groups.getSettings().setDropdownParent(AppointmentDialog.this.getMarkupId());
			add(rdi.add(new AjaxFormChoiceComponentUpdatingBehavior() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					boolean groupsEnabled = InviteeType.GROUP == rdi.getModelObject();
					target.add(groups.setEnabled(groupsEnabled), attendees.setEnabled(!groupsEnabled));
				}
			}));
			groupContainer.add(
				groups.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true)
				, new Radio<>("group", Model.of(InviteeType.GROUP))
			);
			if (showGroups) {
				groups.add(AjaxFormComponentUpdatingBehavior.onUpdate(EVT_CHANGE, target -> {
					// added to update model
				})).setEnabled(false);
			}
			attendees.getSettings().setDropdownParent(AppointmentDialog.this.getMarkupId());
			rdi.add(attendees.add(AjaxFormComponentUpdatingBehavior.onUpdate(EVT_CHANGE, target -> {
					// added to update model
				}))
				, groupContainer.setVisible(showGroups)
			);
			rdi.add(new Radio<>("user", Model.of(InviteeType.USER)));

			add(new TextField<String>("location"));
			OmWysiwygToolbar toolbar = new OmWysiwygToolbar("toolbarContainer");
			add(toolbar);
			add(new WysiwygEditor("description", toolbar));

			//room
			add(new AjaxCheckBox("createRoom", new PropertyModel<>(this, "createRoom")) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					createRoom = getConvertedInput();
					target.add(createRoomBlock.setEnabled(createRoom), groom.setEnabled(!createRoom));
				}
			}.setVisible(myRoomsAllowed));
			add(createRoomBlock.add(roomType, new CheckBox("moderated")).setEnabled(createRoom).setVisible(myRoomsAllowed).setOutputMarkupId(true));
			groom.setRequired(true).setEnabled(!createRoom).setOutputMarkupId(true);
			add(sipContainer.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
			sipContainer.add(new Label("room.confno", "")).setVisible(false);

			//Advanced
			add(new DropDownChoice<>(
					"reminder"
					, List.of(Reminder.values())
					, new LambdaChoiceRenderer<>(Reminder::name, art -> getString("appointment.reminder." + art.name()))
					));
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

			groups.setLabel(new ResourceModel("126"));
			add(new RequiredTextField<String>("title").setLabel(new ResourceModel("572")));
			add(start.setLabel(new ResourceModel("label.start")).setRequired(true)
					, end.setLabel(new ResourceModel("label.end")).setRequired(true)
					, groom.setLabel(new ResourceModel("406")));
			super.onInitialize();
		}

		private List<Room> getRoomList() {
			List<Room> result = new ArrayList<>();
			result.addAll(roomDao.getPublicRooms());
			for (GroupUser ou : userDao.get(getUserId()).getGroupUsers()) {
				result.addAll(roomDao.getGroupRooms(ou.getGroup().getId()));
			}
			if (getModelObject().getRoom() != null && getModelObject().getRoom().isAppointment()) {
				result.add(getModelObject().getRoom());
			}
			return result;
		}

		@Override
		protected void onValidate() {
			if (null != start.getConvertedInput() && null != end.getConvertedInput() && end.getConvertedInput().isBefore(start.getConvertedInput())) {
				error(getString("1592"));
			}
		}
	}

	public static Room createAppRoom() {
		Room r = new Room();
		r.setAppointment(true);
		r.hide(RoomElement.MICROPHONE_STATUS);
		if (r.getType() == null) {
			r.setType(Room.Type.CONFERENCE);
		}
		return r;
	}
}
