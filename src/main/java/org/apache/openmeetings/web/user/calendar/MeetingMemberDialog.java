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

import static org.apache.openmeetings.web.admin.groups.GroupUsersPanel.getUser;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.data.user.dao.UserDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.MeetingMember;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.persistence.beans.user.User.Type;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.UserAutoCompleteTextField;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class MeetingMemberDialog extends AbstractFormDialog<Appointment> {

	private static final long serialVersionUID = 4895460684702708401L;
	private final Form<User> formUsers;
	private FeedbackPanel feedbackDialog = new FeedbackPanel("feedbackDialog");
	private UserAutoCompleteTextField searchUser;
	private List<User> usersInList = new ArrayList<User>();
	private final List<User> usersToAdd = new ArrayList<User>();
	private final List<User> attendeesInList = new ArrayList<User>();
	private final List<User> attendeesToRemove = new ArrayList<User>();
	DialogButton send = new DialogButton(WebSession.getString(175));
	private DialogButton cancel = new DialogButton(WebSession.getString(219));
	private Component attendeeContainer;
	@SuppressWarnings("unused")
	private User userBeingSearched = null; //Model object for UserAutoCompleteTextField, accessible via PropertyModel

	@Override
	public int getWidth() {
		return 500;
	}
	
	public MeetingMemberDialog(String id, String string, IModel<Appointment> model, Component attendeeContainer ) {
		super(id, string, model);
		this.attendeeContainer = attendeeContainer;
		formUsers = new Form<User>("formUsers");
		formUsers.add(feedbackDialog.setOutputMarkupId(true));

		IModel<List<User>> listUsersModel = new PropertyModel<List<User>>(MeetingMemberDialog.this, "usersInList");
		IModel<List<User>> selectedUsersModel = new PropertyModel<List<User>>(MeetingMemberDialog.this, "usersToAdd");
		final ListMultipleChoice<User> users = new ListMultipleChoice<User>("users"
				, selectedUsersModel
				, listUsersModel
				, new IChoiceRenderer<User>() {
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(User object) {
				return getUser(object);
			}

			public String getIdValue(User object, int index) {
				return "" + object.getUser_id();
			}
		});
		
		formUsers.add(searchUser = new UserAutoCompleteTextField("searchUser", new PropertyModel<User>(this, "userBeingSearched")));
		formUsers.add(new AjaxButton("search", Model.of(WebSession.getString(182L))) {
			private static final long serialVersionUID = -4752180617634945030L;

			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
				usersToAdd.clear();
				usersInList.clear();
				usersInList.addAll(Application.getBean(UserDao.class).get(searchUser.inputToString()));
				target.add(users);
			}
		});
		formUsers.add(users.setOutputMarkupId(true));

		IModel<List<User>> listAttendeesModel = new PropertyModel<List<User>>(MeetingMemberDialog.this, "attendeesInList");
		IModel<List<User>> selectedAttendeesModel = new PropertyModel<List<User>>(MeetingMemberDialog.this, "attendeesToRemove");
		final ListMultipleChoice<User> attendees = new ListMultipleChoice<User>("attendees"
				, selectedAttendeesModel
				, listAttendeesModel
				, new IChoiceRenderer<User>() {
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(User object) {
				return getUser(object);
			}

			public String getIdValue(User object, int index) {
				return "" + object.getUser_id();
			}
		});
		formUsers.add(attendees.setOutputMarkupId(true));

		formUsers.add(new AjaxButton("add", Model.of(">>")){
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				for (User u : usersToAdd) {
					if (!attendeesInList.contains(u)){
						attendeesInList.add(u);
					}
				}
				target.add(attendees);
			}
		});
		
		formUsers.add(new AjaxButton("remove", Model.of("<<")){
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				for (User u : attendeesToRemove) {
					if (attendeesInList.contains(u)){
						attendeesInList.remove(u);
					}
				}
				target.add(attendees);
			}
		});

		formUsers.add(new AjaxButton("addExtern"){
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form)
			{
				User ex = searchUser.getModelObject();
				if (ex != null &&  !attendeesInList.contains(ex)){
					attendeesInList.add(ex);
					target.add(attendees);
					userBeingSearched = null; 
				}
			}
		});
		
		add(formUsers.setOutputMarkupId(true));
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
	public Form<?> getForm() {
		return formUsers;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedbackDialog);
		
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		Appointment app = getModelObject();
		final List<MeetingMember> meetingMembers = app.getMeetingMembers() == null ? new ArrayList<MeetingMember>() : app.getMeetingMembers();
		for (User u : attendeesInList) {
			boolean found = false;
			for (MeetingMember m : meetingMembers) {
				if (u.getAdresses().getEmail().equals(m.getUser().getAdresses().getEmail())) {
					found = true;
					break;
				}
			}
			if (!found) {
				MeetingMember mm = new MeetingMember();
				if (u.getType() == Type.contact) {
					u.setOwnerId(getUserId());
					u.setLanguage_id(getLanguage());
				}
				mm.setUser(u);
				mm.setDeleted(false);
				mm.setInserted(app.getInserted());
				mm.setUpdated(app.getUpdated());
				mm.setAppointment(app);
				meetingMembers.add(mm);
			}
		}
		app.setMeetingMembers(meetingMembers);
		target.add(attendeeContainer);
	}
}
