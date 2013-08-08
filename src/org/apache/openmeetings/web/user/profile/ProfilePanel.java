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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.common.ComunityUserForm;
import org.apache.openmeetings.web.common.FormSaveRefreshPanel;
import org.apache.openmeetings.web.common.UploadableProfileImagePanel;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.time.Duration;

public class ProfilePanel extends UserPanel {
	private static final long serialVersionUID = -5837090230776586182L;

	public ProfilePanel(String id) {
		super(id);
		setOutputMarkupId(true);
		
		Form<User> form = new Form<User>("form", new CompoundPropertyModel<User>(getBean(UsersDao.class).get(getUserId()))) {
			private static final long serialVersionUID = -4968428244553170528L;
			private final UserForm userForm;

			{
				add(new FormSaveRefreshPanel<User>("buttons", this) {
					private static final long serialVersionUID = 6578425915881674309L;

					@Override
					protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
						User u = getModelObject();
						try {
							getBean(UsersDao.class).update(u, getUserId());
							String pass = userForm.getPasswordField().getConvertedInput();
							if (pass != null && !pass.isEmpty()) {
								u = getBean(UsersDao.class).updatePassword(u.getUser_id(), pass, getUserId());
							}
						} catch (Exception e) {
							// FIXME update feedback with the error details
						}
						setModelObject(u);
						target.add(ProfilePanel.this);
					}

					@Override
					protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
						// FIXME update feedback with the error details
					}

					@Override
					protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
						User user = getModelObject();
						if (user.getUser_id() != null) {
							user = getBean(UsersDao.class).get(user.getUser_id());
						} else {
							user = new User();
						}
						setModelObject(user);
						target.add(ProfilePanel.this);
					}

					@Override
					protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
						// FIXME update feedback with the error details
					}
				});
				add(userForm = new UserForm("general", getModel()));
				add(new UploadableProfileImagePanel("img", getUserId()));
				add(new ComunityUserForm("comunity", getModel()));
				
				// attach an ajax validation behavior to all form component's keydown
				// event and throttle it down to once per second
				AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown", Duration.ONE_SECOND);
			}
		};
		add(form);
	}
}
