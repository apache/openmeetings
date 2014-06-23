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
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ProfileImagePanel;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;

public class UserProfilePanel extends UserPanel {
	private static final long serialVersionUID = 1L;

	private String getAddress(User u) {
		String result = "";
		if (getUserId() == u.getId() || Boolean.TRUE.equals(u.getShowContactData())) {
			//FIXME, more details should be added
			if (u.getAdresses() != null) {
				result = u.getAdresses().getStreet();
			} else {
				result = "[address]"; //FIXME
			}
		} else if (Boolean.TRUE.equals(u.getShowContactDataToContacts())) {
			result = WebSession.getString(1269);
		} else {
			result = WebSession.getString(1268);
		}
		return result;
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
		add(new Label("address", getAddress(u)));
	}
}
