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

import org.apache.openmeetings.db.dao.user.UserContactsDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ProfileImagePanel;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;

public class UserProfilePanel extends UserPanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer address = new WebMarkupContainer("address");
	private final Label addressDenied = new Label("addressDenied", "");

	public UserProfilePanel(String id, long userId) {
		this(id, new CompoundPropertyModel<User>(getBean(UserDao.class).get(userId)));
	}
	
	public UserProfilePanel(String id, CompoundPropertyModel<User> model) {
		super(id, model);

		add(new ProfileImagePanel("img", model.getObject().getId()));
		add(new Label("firstname"));
		add(new Label("lastname"));
		add(new Label("timeZoneId"));
		add(new Label("regdate"));
		add(new TextArea<String>("userOffers").setEnabled(false));
		add(new TextArea<String>("userSearchs").setEnabled(false));
		if (getUserId() == model.getObject().getId() || model.getObject().isShowContactData() 
				|| (model.getObject().isShowContactDataToContacts() && getBean(UserContactsDao.class).checkUserContacts(model.getObject().getId(), getUserId()) > 0))
		{
			addressDenied.setVisible(false);
			address.add(new Label("adresses.phone"));
			address.add(new Label("adresses.street"));
			address.add(new Label("adresses.additionalname"));
			address.add(new Label("adresses.zip"));
			address.add(new Label("adresses.town"));
			address.add(new Label("adresses.states.name"));
			address.add(new Label("adresses.comment"));
		} else {
			address.setVisible(false);
			addressDenied.setDefaultModelObject(WebSession.getString(Boolean.TRUE.equals(model.getObject().isShowContactDataToContacts()) ? 1269 : 1268));
		}
		add(address.setDefaultModel(model));
		add(addressDenied);
	}
}
