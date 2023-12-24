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

import static org.apache.openmeetings.web.util.ProfileImageResourceReference.getUrl;

import org.apache.openmeetings.db.dao.user.UserDao;

import jakarta.inject.Inject;

public class ProfileImagePanel extends ImagePanel {
	private static final long serialVersionUID = 1L;
	private final long userId;

	@Inject
	private UserDao userDao;

	public ProfileImagePanel(String id, final long userId) {
		super(id);
		this.userId = userId;
	}

	@Override
	protected String getImageUrl() {
		return getUrl(getRequestCycle(), userDao.get(userId));
	}
}
