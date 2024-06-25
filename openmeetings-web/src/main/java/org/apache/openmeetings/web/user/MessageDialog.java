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
package org.apache.openmeetings.web.user;

import static org.apache.openmeetings.db.entity.user.PrivateMessage.INBOX_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.SENT_FOLDER_ID;
import static org.apache.openmeetings.web.app.Application.getContactsLink;
import static org.apache.openmeetings.web.app.Application.getInvitationLink;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;
import static org.apache.openmeetings.web.util.CalendarWebHelper.getZoneId;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isMyRoomsEnabled;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.PrivateMessageDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.manager.IInvitationManager;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.common.datetime.OmDateTimePicker;
import org.apache.openmeetings.web.user.calendar.AppointmentDialog;
import org.apache.openmeetings.util.CalendarHelper;
import org.apache.openmeetings.web.util.CalendarWebHelper;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.openmeetings.web.util.UserMultiChoice;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.CollectionModel;


import org.wicketstuff.jquery.ui.plugins.wysiwyg.WysiwygEditor;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class MessageDialog extends Modal<PrivateMessage> {
	private static final long serialVersionUID = 1L;
	private final Form<PrivateMessage> form;
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final WebMarkupContainer roomParamsBlock = new WebMarkupContainer("roomParamsBlock");
	private final WebMarkupContainer roomParams = new WebMarkupContainer("roomParams");
	private final OmDateTimePicker start = new OmDateTimePicker("start", Model.of(LocalDateTime.now()));
	private final OmDateTimePicker end = new OmDateTimePicker("end", Model.of(LocalDateTime.now()));
	private boolean isPrivate = false;
	private final IModel<Collection<User>> modelTo = new CollectionModel<>(new ArrayList<>());

	@Inject
	private RoomDao roomDao;
	@Inject
	private UserDao userDao;
	@Inject
	private AppointmentDao apptDao;
	@Inject
	private PrivateMessageDao msgDao;
	@Inject
	private IInvitationManager inviteManager;
	@Inject
	private MailHandler handler;

	public MessageDialog(String id, CompoundPropertyModel<PrivateMessage> model) {
		super(id, model);
		setMarkupId(id);
		form = new Form<>("form", getModel());
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("1209"));
		setUseCloseHandler(true);
		size(Modal.Size.Large);

		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("218"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				PrivateMessage m = MessageDialog.this.getModelObject();
				m.setInserted(new Date());
				User owner = userDao.get(getUserId());
				if (m.isBookedRoom()) {
					Room r = m.getRoom();
					r.setName(m.getSubject());
					r.setComment("");
					r.setCapacity(100L);
					r.setAppointment(true);
					r.setAllowUserQuestions(true);
					r = roomDao.update(r, getUserId());
					Appointment a = new Appointment();
					a.setTitle(m.getSubject());
					a.setDescription(m.getMessage());
					a.setRoom(r);
					a.setStart(CalendarWebHelper.getDate(start.getModelObject()));
					a.setEnd(CalendarWebHelper.getDate(end.getModelObject()));
					List<MeetingMember> attendees = new ArrayList<>();
					for (User to : modelTo.getObject()) {
						MeetingMember mm = new MeetingMember();
						mm.setUser(to);
						mm.setDeleted(false);
						mm.setAppointment(a);
						attendees.add(mm);
					}
					a.setOwner(owner);
					a.setMeetingMembers(attendees);
					apptDao.update(a, getUserId(), false);
					m.setRoom(r);
				} else {
					m.setRoom(null);
				}
				for (User to : modelTo.getObject()) {
					if (to.getId() == null) {
						userDao.update(to, getUserId());
					}
					//to send
					PrivateMessage p = new PrivateMessage(m);
					p.setTo(to);
					p.setFolderId(SENT_FOLDER_ID);
					msgDao.update(p, getUserId());
					//to inbox
					p = new PrivateMessage(m);
					p.setOwner(to);
					p.setFolderId(INBOX_FOLDER_ID);
					msgDao.update(p, getUserId());
					if (to.getAddress() != null) {
						String aLinkHTML = 	(isPrivate && to.getType() == Type.USER) ? "<br/><br/>" + "<a href='" + getContactsLink() + "'>"
									+ Application.getString("1302", to.getLanguageId()) + "</a><br/>" : "";
						String invitationLink = "";
						if (p.isBookedRoom()) {
							Invitation i = inviteManager.getInvitation(to, p.getRoom(),
									false, null, Valid.PERIOD, owner, to.getLanguageId()
									, CalendarHelper.getDate(start.getModelObject(), to.getTimeZoneId())
									, CalendarHelper.getDate(end.getModelObject(), to.getTimeZoneId()), null);

							invitationLink = getInvitationLink(i, WebSession.get().getExtendedProperties().getBaseUrl());

							if (invitationLink == null) {
								invitationLink = "";
							} else {
								invitationLink = "<br/>" //
										+ Application.getString("template.room.invitation.text", to.getLanguageId())
										+ "<br/><a href='" + invitationLink
										+ "'>"
										+ Application.getString("504", to.getLanguageId()) + "</a><br/>";
							}
						}

						String subj = p.getSubject() == null ? "" : p.getSubject();
						handler.send(to.getAddress().getEmail(),
								Application.getString("1301", to.getLanguageId()) + subj,
								(p.getMessage() == null ? "" : p.getMessage().replaceAll("\\<[^>]*+>", "")) + aLinkHTML + invitationLink);
					}
				}
				MessageDialog.this.close(target);
				onSend(target);
			}
		}); // send
		addButton(OmModalCloseButton.of());

		form.add(feedback.setOutputMarkupId(true));
		final UserMultiChoice recepients = new UserMultiChoice("to", modelTo);
		recepients.getSettings().setDropdownParent(MessageDialog.this.getMarkupId());
		form.add(recepients.setRequired(true));
		form.add(new TextField<String>("subject"));
		OmWysiwygToolbar toolbar = new OmWysiwygToolbar("toolbarContainer");
		form.add(toolbar);
		form.add(new WysiwygEditor("message", toolbar));
		form.add(roomParamsBlock.setOutputMarkupId(true));
		final CheckBox bookedRoom = new CheckBox("bookedRoom");
		form.add(bookedRoom.setOutputMarkupId(true).add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				PrivateMessage p = MessageDialog.this.getModelObject();
				p.setBookedRoom(!p.isBookedRoom());
				roomParams.setVisible(p.isBookedRoom());
				target.add(bookedRoom, roomParamsBlock);
			}
		}));
		bookedRoom.setVisible(isMyRoomsEnabled());
		roomParamsBlock.add(roomParams);
		roomParams.add(new RoomTypeDropDown("room.type"));
		roomParams.add(start);
		roomParams.add(end);
		add(form.setOutputMarkupId(true));

		super.onInitialize();
	}

	public MessageDialog reset(boolean isPrivate) {
		LocalDateTime now = ZonedDateTime.now(getZoneId()).toLocalDateTime();
		start.setModelObject(now);
		end.setModelObject(now.plus(1, ChronoUnit.HOURS));
		modelTo.setObject(new ArrayList<>());
		PrivateMessage p = new PrivateMessage();
		p.setFrom(userDao.get(getUserId()));
		p.setOwner(p.getFrom());
		p.setIsRead(false);
		p.setFolderId(INBOX_FOLDER_ID);
		p.setRoom(AppointmentDialog.createAppRoom());
		setModelObject(p);
		roomParams.setVisible(getModelObject().isBookedRoom());
		form.setModelObject(p);
		this.isPrivate = isPrivate;
		return this;
	}

	public void show(IPartialPageRequestHandler handler, Long userId) {
		getModelObject().setTo(userDao.get(userId));
		show(handler);
	}

	@Override
	public Modal<PrivateMessage> show(IPartialPageRequestHandler handler) {
		if (getModel().getObject().getTo() != null) {
			modelTo.getObject().add(getModel().getObject().getTo());
		}
		handler.add(form);
		return super.show(handler);
	}

	protected void onSend(IPartialPageRequestHandler handler) {
		// can be overriden
	}

	@Override
	protected void onDetach() {
		modelTo.detach();
		super.onDetach();
	}
}
