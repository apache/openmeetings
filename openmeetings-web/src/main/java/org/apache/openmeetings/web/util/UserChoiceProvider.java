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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.validation.Validatable;
import org.json.JSONException;
import org.json.JSONWriter;

import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Response;

public class UserChoiceProvider extends ChoiceProvider<User> {
	private static final long serialVersionUID = 1L;
	private final static int PAGE_SIZE = 10;
	private Map<String, User> newContacts = new Hashtable<String, User>();
	
	public static User getUser(String value) {
		User u = null;
		//FIXME refactor this
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
		Validatable<String> valEmail = new Validatable<String>(email);
		RfcCompliantEmailAddressValidator.getInstance().validate(valEmail);
		if (valEmail.isValid()) {
			u = getBean(UserDao.class).getContact(email, fName, lName, getUserId());
		}
		return u;
	}

	protected Object getId(User u) {
		String id = "" + u.getUser_id();
		if (u.getUser_id() == null) {
			newContacts.put(u.getLogin(), u);
			id = u.getLogin();
		}
		return id;
	}

	@Override
	public void query(String term, int page, Response<User> response) {
		User c = getUser(term);
		if (c != null) {
			response.add(c);
		}
		UserDao dao = getBean(UserDao.class);
		response.addAll(dao.get(term, page * PAGE_SIZE, PAGE_SIZE, null, getUserId()));

		response.setHasMore(page < dao.count(term, getUserId()) / PAGE_SIZE);
	}

	@Override
	public Collection<User> toChoices(Collection<String> ids) {
		Collection<User> c = new ArrayList<User>();
		for (String id : ids) {
			if (newContacts.containsKey(id)) {
				c.add(newContacts.get(id));
			} else {
				c.add(getBean(UserDao.class).get(Long.valueOf(id)));
			}
		}
		return c;
	}

    public void toJson(User choice, JSONWriter writer) throws JSONException {
    	writer
    		.key("id").value(getId(choice))
    		.key("text").value(FormatHelper.formatUser(choice, true))
    		.key("contact").value(choice.getType() == Type.contact);
    };
}
