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

import static org.apache.openmeetings.db.entity.user.PrivateMessage.INBOX_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.SENT_FOLDER_ID;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getBaseUrl;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.RoomTypeDropDown.getRoomTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.PrivateMessagesDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.mail.MailHandler;
import org.apache.openmeetings.util.LinkHelper;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.ContactsHelper;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.openmeetings.web.util.UserMultiChoice;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;

import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.WysiwygEditor;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.DefaultWysiwygToolbar;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class MessageDialog extends AbstractFormDialog<PrivateMessage> {
	private static final long serialVersionUID = 1L;
	private final Form<PrivateMessage> form;
	private FeedbackPanel feedback = new FeedbackPanel("feedback");
	String sendLbl = WebSession.getString(218);
	DialogButton send = new DialogButton(sendLbl);
	private DialogButton cancel = new DialogButton(WebSession.getString(219));
	private final WebMarkupContainer roomParamsBlock = new WebMarkupContainer("roomParamsBlock");
	private final WebMarkupContainer roomParams = new WebMarkupContainer("roomParams");
	private final IModel<Date> modelStart = Model.of(new Date());
	private final IModel<Date> modelEnd = Model.of(new Date());
	private boolean isPrivate = false; 
	private final IModel<Collection<User>> modelTo = new CollectionModel<User>(new ArrayList<User>());

	@Override
	public int getWidth() {
		return 650;
	}
	
	public void open(AjaxRequestTarget target, long userId) {
		getModelObject().setTo(getBean(UserDao.class).get(userId));
		open(target);
	}
	
	public MessageDialog reset(boolean isPrivate) {
		modelStart.setObject(new Date());
		modelEnd.setObject(new Date()); //TODO should we add 1 hour or generalize with Calendar???
		modelTo.setObject(new ArrayList<User>());
		PrivateMessage p = new PrivateMessage();
		p.setFrom(getBean(UserDao.class).get(getUserId()));
		p.setOwner(p.getFrom());
		p.setIsRead(false);
		p.setFolderId(INBOX_FOLDER_ID);
		Room r = new Room();
		r.setAppointment(true);
		r.setRoomtype(getRoomTypes().get(0));
		p.setRoom(r);
		setModelObject(p);
		roomParams.setVisible(getModelObject().isBookedRoom());
		form.setModelObject(p);
		this.isPrivate = isPrivate;
		return this;
	}
	
	@Override
	protected void onOpen(AjaxRequestTarget target) {
		if (getModel().getObject().getTo() != null) {
			modelTo.getObject().add(getModel().getObject().getTo());
		}
		target.add(form);
		super.onOpen(target);
	}
	
	public MessageDialog(String id, CompoundPropertyModel<PrivateMessage> model) {
		super(id, WebSession.getString(1209), model);
		form = new Form<PrivateMessage>("form", getModel());
		
		form.add(feedback.setOutputMarkupId(true));
		form.add(new UserMultiChoice("to", modelTo).setRequired(true));
		form.add(new TextField<String>("subject"));
		DefaultWysiwygToolbar toolbar = new DefaultWysiwygToolbar("toolbarContainer");
		form.add(toolbar);
		form.add(new WysiwygEditor("message", toolbar));
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
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		PrivateMessage p = getModelObject();
		p.setInserted(new Date());
		if (p.isBookedRoom()) {
			Room r = p.getRoom();
			r.setName(p.getSubject());
			r.setComment("");
			r.setNumberOfPartizipants(100L);
			r.setAppointment(false);
			r.setAllowUserQuestions(true);
			r.setAllowFontStyles(true);
			r = getBean(RoomDao.class).update(r, getUserId());
			p.setRoom(r);
		} else {
			p.setRoom(null);
		}
		PrivateMessagesDao msgDao = getBean(PrivateMessagesDao.class);
		for (User to : modelTo.getObject()) {
			UserDao userDao = getBean(UserDao.class); 
			if (to.getUser_id() == null) {
				userDao.update(to, getUserId());
			}
			//to send
			p = new PrivateMessage(p);
			p.setTo(to);
			p.setFolderId(SENT_FOLDER_ID);
			msgDao.update(p, getUserId());
			//to inbox
			p = new PrivateMessage(p);
			p.setOwner(to);
			p.setFolderId(INBOX_FOLDER_ID);
			msgDao.update(p, getUserId());
			if (to.getAdresses() != null) {
				String aLinkHTML = 	(isPrivate && to.getType() == Type.user) ? "<br/><br/>" + "<a href='" + ContactsHelper.getLink() + "'>"
							+ WebSession.getString(1302) + "</a><br/>" : "";
				String invitation_link = "";
				if (p.isBookedRoom()) {
					Invitation i = getBean(InvitationManager.class).getInvitation(to, p.getRoom(),
							false, null, Valid.Period
							, userDao.get(getUserId()), getBaseUrl(), userDao.get(getUserId()).getLanguage_id(),
							modelStart.getObject(), modelEnd.getObject(), null);
					
					invitation_link = LinkHelper.getInvitationLink(i);

					if (invitation_link == null) {
						invitation_link = "";
					} else {
						invitation_link = "<br/>" //
								+ WebSession.getString(503)
								+ "<br/><a href='" + invitation_link
								+ "'>"
								+ WebSession.getString(504) + "</a><br/>";
					}
				}
				
				getBean(MailHandler.class).send(to.getAdresses().getEmail(),
						WebSession.getString(1301) + p.getSubject(),
						(p.getMessage() == null ? "" : p.getMessage().replaceAll("\\<.*?>", "")) + aLinkHTML + invitation_link);
			}
		}
	}
}
