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

package org.apache.openmeetings.web.admin;

import static org.apache.openmeetings.web.admin.groups.GroupUsersPanel.getUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.user.AdminUserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class AddUsersDialog extends AbstractFormDialog<User> {
	private static final long serialVersionUID = 1L;
	private final Form<User> formUsers;
	private FeedbackPanel feedbackDialog = new FeedbackPanel("feedbackDialog");
	private final AdminCommonUserForm<?> commonForm;
	private String userSearchText;
	private final IModel<List<User>> listUsersModel = new ListModel<User>(new ArrayList<User>());
	private final IModel<List<User>> selectedUsersModel = new ListModel<User>(new ArrayList<User>());
	private final ListMultipleChoice<User> users = new ListMultipleChoice<User>("users"
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
	DialogButton send = new DialogButton(WebSession.getString(175));
	private DialogButton cancel = new DialogButton(WebSession.getString(219));

	public AddUsersDialog(String id, String title, AdminCommonUserForm<?> commonForm) {
		super(id, title, true);
		formUsers = new Form<User>("formUsers", getModel());
		this.commonForm = commonForm;
		formUsers.add(feedbackDialog.setOutputMarkupId(true));

		formUsers.add(new TextField<String>("searchText", new PropertyModel<String>(AddUsersDialog.this, "userSearchText")));
		formUsers.add(new AjaxButton("search", Model.of(WebSession.getString(182L))) {
			private static final long serialVersionUID = -4752180617634945030L;

			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
				listUsersModel.getObject().clear();
				selectedUsersModel.getObject().clear();
				listUsersModel.getObject().addAll(Application.getBean(AdminUserDao.class).get(userSearchText));
				target.add(users);
			}
		});
		formUsers.add(users.setOutputMarkupId(true));
		add(formUsers.setOutputMarkupId(true));
	}

	@Override
	protected void onOpen(AjaxRequestTarget target) {
		listUsersModel.getObject().clear();
		selectedUsersModel.getObject().clear();
		target.add(users);
		super.onOpen(target);
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
		commonForm.submitView(target, selectedUsersModel.getObject());
	}
}
