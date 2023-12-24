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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.HashMap;
import java.util.Map;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.util.FormatHelper;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.injection.Injector;

import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.Validatable;
import org.wicketstuff.select2.Response;

import com.github.openjson.JSONStringer;

import jakarta.inject.Inject;

public class UserChoiceProvider extends RestrictiveChoiceProvider<User> {
	private static final long serialVersionUID = 1L;
	private static final long PAGE_SIZE = 10;
	private final Map<String, User> newContacts = new HashMap<>();

	@Inject
	private UserDao userDao;

	public UserChoiceProvider() {
		Injector.get().inject(this);
	}

	public User getUser(String value) {
		User u = null;
		if (!Strings.isEmpty(value)) {
			String email = null;
			String fName = null;
			String lName = null;
			int idx = value.indexOf('<');
			if (idx > -1) {
				int idx1 = value.indexOf('>', idx);
				if (idx1 > -1) {
					email = value.substring(idx + 1, idx1);

					String name = value.substring(0, idx).replace("\"", "");
					int idx2 = name.indexOf(' ');
					if (idx2 > -1) {
						fName = name.substring(0, idx2);
						lName = name.substring(idx2 + 1);
					} else {
						fName = "";
						lName = name;
					}

				}
			} else {
				email = value;
			}
			if (!Strings.isEmpty(email)) {
				Validatable<String> valEmail = new Validatable<>(email);
				RfcCompliantEmailAddressValidator.getInstance().validate(valEmail);
				if (valEmail.isValid()) {
					u = userDao.getContact(email, fName, lName, getUserId());
				}
			}
		}
		return u;
	}

	@Override
	public String toId(User u) {
		String id = "" + u.getId();
		if (u.getId() == null) {
			newContacts.put(u.getLogin(), u);
			id = u.getLogin();
		}
		return id;
	}

	@Override
	public String getDisplayValue(User object) {
		return FormatHelper.formatUser(object, true);
	}

	@Override
	public void query(String term, int page, Response<User> response) {
		User c = getUser(term);
		if (c != null) {
			response.add(c);
		}
		response.addAll(userDao.get(term, page * PAGE_SIZE, PAGE_SIZE, null, true, getUserId()));

		response.setHasMore(page < userDao.countUsers(term, getUserId()) / PAGE_SIZE);
	}

	@Override
	public User fromId(String id) {
		User u;
		if (newContacts.containsKey(id)) {
			u = newContacts.get(id);
		} else {
			u = userDao.get(Long.valueOf(id));
		}
		return u;
	}

	@Override
	public void toJson(User choice, JSONStringer stringer) {
		super.toJson(choice, stringer);
		stringer.key("contact").value(choice.getType() == Type.CONTACT);
	}
}
