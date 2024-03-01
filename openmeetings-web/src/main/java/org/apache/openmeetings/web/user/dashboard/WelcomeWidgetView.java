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
package org.apache.openmeetings.web.user.dashboard;

import static org.apache.openmeetings.db.entity.user.PrivateMessage.INBOX_FOLDER_ID;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.pages.HashPage.APP_KEY;
import static org.apache.openmeetings.web.pages.HashPage.APP_TYPE_NETWORK;
import static org.apache.openmeetings.web.pages.HashPage.APP_TYPE_SETTINGS;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_EDIT;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_MESSAGES;

import org.apache.openmeetings.db.dao.user.PrivateMessageDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.UploadableProfileImagePanel;
import org.apache.openmeetings.web.pages.HashPage;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import org.wicketstuff.dashboard.Widget;
import org.wicketstuff.dashboard.web.WidgetView;

import jakarta.inject.Inject;

public class WelcomeWidgetView extends WidgetView {
	private static final long serialVersionUID = 1L;

	@Inject
	private UserDao userDao;
	@Inject
	private PrivateMessageDao msgDao;

	public WelcomeWidgetView(String id, Model<Widget> model) {
		super(id, model);

		User u = userDao.get(getUserId());
		add(new UploadableProfileImagePanel("img", getUserId()));
		add(new Label("firstname", Model.of(u.getFirstname())));
		add(new Label("lastname", Model.of(u.getLastname())));
		add(new Label("tz", Model.of(u.getTimeZoneId())));
		add(new AjaxLink<Void>("openUnread") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((MainPage)getPage()).updateContents(PROFILE_MESSAGES, target);
			}
		}.add(new Label("unread", Model.of(String.valueOf(msgDao.count(getUserId(), INBOX_FOLDER_ID, null))))));
		add(new AjaxLink<Void>("editProfile") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((MainPage)getPage()).updateContents(PROFILE_EDIT, target);
			}
		});
		add(new WebMarkupContainer("netTest").add(AttributeModifier.append("href"
				, RequestCycle.get().urlFor(HashPage.class, new PageParameters().add(APP_KEY, APP_TYPE_NETWORK)).toString())));
		add(new WebMarkupContainer("avTest").add(AttributeModifier.append("href"
				, RequestCycle.get().urlFor(HashPage.class, new PageParameters().add(APP_KEY, APP_TYPE_SETTINGS)).toString())));
	}
}
