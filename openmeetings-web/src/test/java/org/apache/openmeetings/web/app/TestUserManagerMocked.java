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
package org.apache.openmeetings.web.app;

import static org.apache.openmeetings.util.OpenmeetingsVariables.setCryptClassName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.user.OAuthUser;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.service.mail.EmailManager;
import org.apache.openmeetings.util.crypt.SCryptImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
public class TestUserManagerMocked {
	@Mock
	private GroupDao groupDao;
	@Mock
	private UserDao userDao;
	@Mock
	private EmailManager emailManager;
	@Mock
	private IClientManager cm;
	@InjectMocks
	private UserManager userManager;

	@Test
	public void oauthTest() throws NoSuchAlgorithmException, IOException {
		setCryptClassName(SCryptImplementation.class.getCanonicalName());
		doReturn(true).when(userDao).validLogin(anyString());
		doReturn(true).when(userDao).checkEmail(anyString(), eq(Type.OAUTH), any(Long.class), nullable(Long.class));
		when(userDao.update(any(User.class), nullable(String.class), any(Long.class))).then(new Answer<User>() {
			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (User)args[0];
			}
		});
		final String json = "{\"email\":\"openmeetings@pod.land\",\"email_verified\":true,\"id\":78207,\"nationalcode_verified\":false,\"phone_number_verified\":false,\"preferred_username\":\"openmeetings\",\"sub\":\"78207\",\"user_metadata\":\"{\\\"mail.auto-forward\\\":true,\\\"email\\\":{\\\"auto_forward\\\":true}}\"}";
		OAuthUser user = new OAuthUser(json, new OAuthServer()
				.addMapping(OAuthUser.PARAM_LOGIN, "preferred_username")
				.addMapping(OAuthUser.PARAM_EMAIL, "email"));
		User u = userManager.loginOAuth(user, 1);
		assertNotNull(u, "Valid user should be created");
		assertEquals("openmeetings", u.getLogin(), "User should have valid login");
		assertEquals("openmeetings@pod.land", u.getAddress().getEmail(), "User should have valid email");
	}
}
