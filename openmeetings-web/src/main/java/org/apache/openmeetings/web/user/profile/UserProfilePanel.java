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

import static org.apache.openmeetings.db.util.LocaleHelper.getCountryName;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import org.apache.openmeetings.db.dao.user.UserContactDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.ProfileImagePanel;
import org.apache.openmeetings.web.common.UserBasePanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;

import jakarta.inject.Inject;


public class UserProfilePanel extends UserBasePanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer address = new WebMarkupContainer("address");
	private final Label addressDenied = new Label("addressDenied", "");
	private final WebMarkupContainer infoPanel = new WebMarkupContainer("info-panel");

	@Inject
	private UserDao userDao;
	@Inject
	private UserContactDao contactDao;

	public UserProfilePanel(String id, long userId) {
		super(id);
		setDefaultModel(new CompoundPropertyModel<>(userDao.get(userId)));
	}

	@Override
	protected void onInitialize() {
		User u = (User)getDefaultModelObject();

		infoPanel.add(new ProfileImagePanel("img", u.getId()));
		infoPanel.add(new Label("firstname"));
		infoPanel.add(new Label("lastname"));
		infoPanel.add(new Label("timeZoneId"));
		infoPanel.add(new Label("regdate"));
		infoPanel.add(new TextArea<String>("userOffers").setEnabled(false));
		infoPanel.add(new TextArea<String>("userSearchs").setEnabled(false));
		if (getUserId().equals(u.getId()) || u.isShowContactData()
				|| (u.isShowContactDataToContacts() && contactDao.isContact(u.getId(), getUserId())))
		{
			addressDenied.setVisible(false);
			address.add(new Label("address.phone"));
			address.add(new Label("address.street"));
			address.add(new Label("address.additionalname"));
			address.add(new Label("address.zip"));
			address.add(new Label("address.town"));
			address.add(new Label("country", getCountryName(u.getAddress().getCountry(), getLocale())));
			address.add(new Label("address.comment"));
		} else {
			address.setVisible(false);
			addressDenied.setDefaultModelObject(getString(u.isShowContactDataToContacts() ? "1269" : "1268"));
		}
		infoPanel.add(address.setDefaultModel(getDefaultModel()));
		infoPanel.add(addressDenied);

		add(infoPanel.setOutputMarkupId(true));
		super.onInitialize();
	}
}
