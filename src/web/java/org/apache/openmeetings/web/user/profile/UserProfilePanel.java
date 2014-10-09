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

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.State;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ProfileImagePanel;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;

public class UserProfilePanel extends UserPanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer address = new WebMarkupContainer("address");
	private final Label addressDenied = new Label("addressDenied", "");

	private void setAddress(User u) {
		if (getUserId() == u.getUser_id() || Boolean.TRUE.equals(u.getShowContactData())) {
			addressDenied.setVisible(false);
			Address a = u.getAdresses() == null ? new Address() : u.getAdresses();
			address.add(new Label("phone", a.getPhone()));
			address.add(new Label("street", a.getStreet()));
			address.add(new Label("additionalname", a.getAdditionalname()));
			address.add(new Label("zip", a.getZip()));
			address.add(new Label("town", a.getTown()));
			State s = a.getStates();
			address.add(new Label("state", s == null ? null : s.getName()));
			address.add(new Label("address", a.getComment()));
		} else {
			address.setVisible(false);
			addressDenied.setDefaultModelObject(WebSession.getString(Boolean.TRUE.equals(u.getShowContactDataToContacts()) ? 1269 : 1268));
		}
	}
	
	public UserProfilePanel(String id, long userId) {
		super(id);

		User u = getBean(UserDao.class).get(userId);
		add(new ProfileImagePanel("img", userId));
		add(new Label("firstname", u.getFirstname()));
		add(new Label("lastname", u.getLastname()));
		add(new Label("tz", u.getTimeZoneId()));
		add(new Label("created", u.getRegdate()));
		add(new TextArea<String>("offer", Model.of(u.getUserOffers())).setEnabled(false));
		add(new TextArea<String>("interest", Model.of(u.getUserSearchs())).setEnabled(false));
		setAddress(u);
		add(address);
		add(addressDenied);
	}
}
