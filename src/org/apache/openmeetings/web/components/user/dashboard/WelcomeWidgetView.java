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
package org.apache.openmeetings.web.components.user.dashboard;

import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.data.user.dao.PrivateMessagesDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.UploadableProfileImagePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class WelcomeWidgetView extends WidgetView {
	private static final long serialVersionUID = -6257866996099503210L;

	public WelcomeWidgetView(String id, Model<Widget> model) {
		super(id, model);

		User u = Application.getBean(UsersDao.class).get(getUserId());
		add(new UploadableProfileImagePanel("img", getUserId()));
		 //FIXME this need to be aligned according to Locale
		add(new Label("firstname", Model.of(u.getFirstname())));
		add(new Label("lastname", Model.of(u.getLastname())));
		add(new Label("tz", Model.of(u.getOmTimeZone().getIcal())));
		add(new AjaxLink<Void>("openUnread") {
			private static final long serialVersionUID = -1847619557485964386L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				//FIXME should have action
			}
		}.add(new Label("unread", Model.of("" + Application.getBean(PrivateMessagesDao.class)
				.getNumberMessages(getUserId(), 0L, false)))));
		add(new AjaxLink<Void>("editProfile") {
			private static final long serialVersionUID = -1847619557485964386L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				//FIXME should have action
			}
		});
	}
}
