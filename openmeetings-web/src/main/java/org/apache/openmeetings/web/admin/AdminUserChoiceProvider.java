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
package org.apache.openmeetings.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.injection.Injector;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import jakarta.inject.Inject;

public abstract class AdminUserChoiceProvider extends ChoiceProvider<User> {
	private static final long serialVersionUID = 1L;
	public static final long PAGE_SIZE = 20;

	@Inject
	private UserDao userDao;

	protected AdminUserChoiceProvider() {
		Injector.get().inject(this);
	}

	@Override
	public String getIdValue(User choice) {
		Long id = choice.getId();
		return id == null ? null : String.valueOf(id);
	}

	@Override
	public void query(String term, int page, Response<User> response) {
		response.addAll(userDao.get(term, true, page * PAGE_SIZE, PAGE_SIZE));
		response.setHasMore(page * PAGE_SIZE + response.getResults().size() < userDao.count(term, true, -1L));
	}

	@Override
	public Collection<User> toChoices(Collection<String> inIds) {
		List<Long> ids = new ArrayList<>();
		for (String id : inIds) {
			ids.add(Long.valueOf(id));
		}
		return userDao.get(ids);
	}
}
