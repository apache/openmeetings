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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.app.WebSession.getRights;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import jakarta.inject.Inject;

public class GroupChoiceProvider extends ChoiceProvider<Group> {
	private static final long serialVersionUID = 1L;
	public static final long PAGE_SIZE = 5;

	@Inject
	private GroupDao groupDao;
	@Inject
	private UserDao userDao;

	public GroupChoiceProvider() {
		Injector.get().inject(this);
	}

	@Override
	public void query(String term, int page, Response<Group> response) {
		long count;
		if (AuthLevelUtil.hasAdminLevel(getRights())) {
			count = groupDao.count(term);
			response.addAll(groupDao.get(term, page * PAGE_SIZE, PAGE_SIZE, null));
		} else if (AuthLevelUtil.hasGroupAdminLevel(getRights())) {
			response.addAll(groupDao.adminGet(term, getUserId(), page * PAGE_SIZE, PAGE_SIZE, null));
			count = groupDao.adminCount(term, getUserId());
		} else {
			User u = userDao.get(getUserId());
			for (GroupUser ou : u.getGroupUsers()) {
				if (Strings.isEmpty(term) || ou.getGroup().getName().toLowerCase(Locale.ROOT).contains(term.toLowerCase(Locale.ROOT))) {
					response.add(ou.getGroup());
				}
			}
			count = u.getGroupUsers().size();
		}
		response.setHasMore(page * PAGE_SIZE + response.getResults().size() < count);
	}

	@Override
	public Collection<Group> toChoices(Collection<String> ids) {
		Collection<Group> c = new ArrayList<>();
		for (String id : ids) {
			c.add(groupDao.get(Long.valueOf(id)));
		}
		return c;
	}

	@Override
	public String getDisplayValue(Group choice) {
		return choice.getName();
	}

	@Override
	public String getIdValue(Group choice) {
		Long id = choice.getId();
		return id == null ? null : "" + id;
	}
}
