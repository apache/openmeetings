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

import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_EMAIL;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_FNAME;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_LNAME;
import static org.apache.openmeetings.db.dto.user.OAuthUser.PARAM_LOGIN;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setCryptClassName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.security.NoSuchAlgorithmException;

import org.apache.openmeetings.web.RegularTest;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestInfoMethod;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestTokenMethod;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.db.manager.IClientManager;
import org.apache.openmeetings.service.mail.EmailManager;
import org.apache.openmeetings.util.crypt.SCryptImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@RegularTest
@ExtendWith(MockitoExtension.class)
class TestUserManagerMocked {
	@Mock
	private GroupDao groupDao;
	@Mock
	private UserDao userDao;
	@Mock
	private EmailManager emailManager;
	@Mock
	private IClientManager cm;
	@InjectMocks
	private UserManager userManager = Mockito.spy(new UserManager());

	@Test
	void oauthTest() throws NoSuchAlgorithmException, IOException, InterruptedException {
		OAuthServer server = new OAuthServer()
				.setName("Google")
				.setIconUrl("https://www.google.com/images/google_favicon_128.png")
				.setEnabled(false)
				.setClientId("secret-client-id")
				.setClientSecret("secret-client-secret")
				.setRequestKeyUrl("https://accounts.google.com/o/oauth2/auth?redirect_uri={$redirect_uri}&response_type=code&client_id={$client_id}"
						+ "&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile")
				.setRequestTokenUrl("https://accounts.google.com/o/oauth2/token")
				.setRequestTokenMethod(RequestTokenMethod.POST)
				.setRequestTokenAttributes("code={$code}&client_id={$client_id}&client_secret={$client_secret}&redirect_uri={$redirect_uri}&grant_type=authorization_code")
				.setRequestInfoUrl("https://www.googleapis.com/oauth2/v1/userinfo?access_token={$access_token}")
				.setRequestInfoMethod(RequestInfoMethod.GET)
				.addMapping(PARAM_LOGIN, "preferred_username")
				.addMapping(PARAM_EMAIL, "email")
				.addMapping(PARAM_FNAME, "given_name")
				.addMapping(PARAM_LNAME, "family_name");
		setCryptClassName(SCryptImplementation.class.getCanonicalName());
		doReturn(true).when(userDao).validLogin(anyString());
		doReturn(true).when(userDao).checkEmail(eq("openmeetings@pod.land"), eq(Type.OAUTH), nullable(Long.class), nullable(Long.class));
		doAnswer(new Answer<User>() {
			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (User)args[0];
			}
		}).when(userDao).update(any(User.class), nullable(String.class), any(Long.class));
		final String userJson = "{\"email\":\"openmeetings@pod.land\",\"email_verified\":true,\"id\":78207,\"nationalcode_verified\":false,\"phone_number_verified\":false,\"preferred_username\":\"openmeetings\",\"sub\":\"78207\",\"user_metadata\":\"{\\\"mail.auto-forward\\\":true,\\\"email\\\":{\\\"auto_forward\\\":true}}\"}";
		final String tokenJson = "{\"accessToken\": \"aaa\", \"refreshToken\": \"bbb\", \"tokenType\": \"cccc\", \"userId\": \"ddddd\", \"expiresIn\": \"eeeeee\"}";
		doAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				HttpRequest req = (HttpRequest)args[0];
				if (req.uri().getHost().equals("accounts.google.com")) {
					return tokenJson;
				}
				return userJson;
			}
		}).when(userManager).doRequest(any(HttpRequest.class));

		User u = userManager.loginOAuth("bla-bla-bla", server);
		assertNotNull(u, "Valid user should be created");
		assertEquals("openmeetings", u.getLogin(), "User should have valid login");
		assertEquals("openmeetings@pod.land", u.getAddress().getEmail(), "User should have valid email");
	}
}
