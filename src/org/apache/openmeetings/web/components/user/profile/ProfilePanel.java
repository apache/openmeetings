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
package org.apache.openmeetings.web.components.user.profile;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.components.ComunityUserForm;
import org.apache.openmeetings.web.components.FormSaveRefreshPanel;
import org.apache.openmeetings.web.components.GeneralUserForm;
import org.apache.openmeetings.web.components.UploadableProfileImagePanel;
import org.apache.openmeetings.web.components.UserPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

public class ProfilePanel extends UserPanel {
	private static final long serialVersionUID = -5837090230776586182L;

	public ProfilePanel(String id) {
		super(id);
		
		Form<User> form = new Form<User>("form", new CompoundPropertyModel<User>(getBean(UsersDao.class).get(getUserId()))) {
			private static final long serialVersionUID = -4968428244553170528L;

			{
				add(new FormSaveRefreshPanel<User>("buttons", this) {
					private static final long serialVersionUID = 6578425915881674309L;

					@Override
					protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
						// TODO Auto-generated method stub
						
					}

					@Override
					protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
						// TODO Auto-generated method stub
						
					}

					@Override
					protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
						// TODO Auto-generated method stub
						
					}

					@Override
					protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
						// TODO Auto-generated method stub
						
					}
				});
				add(new GeneralUserForm("general", getModel()));
				add(new UploadableProfileImagePanel("img", getUserId()));
				add(new ComunityUserForm("comunity", getModel()));
			}
		};
		add(form);
	}
}
