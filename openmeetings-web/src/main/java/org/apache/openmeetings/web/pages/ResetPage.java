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

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.pages.auth.ResetPasswordDialog;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ResetPage extends BaseNotInitedPage {
	private static final long serialVersionUID = 1L;
	public static final String RESET_PARAM = "hash";

	public ResetPage(PageParameters pp) {
		String resetHash = pp.get(RESET_PARAM).toString();
		if (resetHash != null) {
			User user = Application.getBean(UserDao.class).getUserByHash(resetHash);
			if (user != null) {
				add(new ResetPasswordDialog("resetPassword", user));
				return;
			}
		}
		setResponsePage(Application.get().getSignInPageClass());
	}
}
