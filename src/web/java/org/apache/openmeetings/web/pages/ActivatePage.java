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
package org.apache.openmeetings.web.pages;

import java.util.Date;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ActivatePage extends BaseNotInitedPage {
	private static final long serialVersionUID = 1L;
	private final String ACTIVATION_PARAM = "u";
	
	public ActivatePage(PageParameters pp) {
		String userHash = pp.get(ACTIVATION_PARAM).toString();
		if (userHash != null) {
			User user = Application.getBean(UserDao.class).getUserByActivationHash(userHash);

			if (user != null && user.getStatus() == 0) {
				// activate
				user.setStatus(1);
				user.setUpdatetime(new Date());

				Application.getBean(UserDao.class).update(user, null);
	
			}
		}
		setResponsePage(Application.get().getSignInPageClass());
	}
}
