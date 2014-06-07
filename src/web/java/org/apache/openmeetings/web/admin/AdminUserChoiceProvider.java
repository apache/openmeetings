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

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public abstract class AdminUserChoiceProvider extends TextChoiceProvider<User> {
	private static final long serialVersionUID = 1L;
	protected static int PAGE_SIZE = 20;

	@Override
	protected Object getId(User choice) {
		return choice.getUser_id();
	}

	@Override
	public void query(String term, int page, Response<User> response) {
		response.addAll(getBean(UserDao.class).get(term, true, page * PAGE_SIZE, PAGE_SIZE));
		response.setHasMore(PAGE_SIZE == response.getResults().size());
	}

	@Override
	public Collection<User> toChoices(Collection<String> ids) {
		return new ArrayList<User>(getBean(UserDao.class).get(ids));
	}
}
