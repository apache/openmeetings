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
package org.apache.openmeetings.web.pages.auth;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_AT_REGISTER;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_EMAIL_VERIFICATION;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.ActivatePage;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.ResetPage;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import com.googlecode.wicket.jquery.ui.widget.dialog.ButtonAjaxBehavior;

public class TestLoginUI extends AbstractWicketTester {
	private final static String PATH_REGISTER = "register:form";

	private void checkLogin(String login, String pass) {
		FormTester formTester = tester.newFormTester("signin:signin");
		formTester.setValue("login", login);
		formTester.setValue("pass", pass);
		formTester.submit("submit");

		tester.assertNoErrorMessage();
		tester.assertRenderedPage(MainPage.class);
		WebSession ws = (WebSession)tester.getSession();
		assertTrue("Login should be successful", ws.isSignedIn());
	}

	@Test
	public void testValidLogin() {
		tester.startPage(MainPage.class);
		tester.assertRenderedPage(SignInPage.class);

		checkLogin(adminUsername, userpass);
	}

	@Test
	public void testEmptyLogin() {
		tester.startPage(SignInPage.class);
		tester.assertRenderedPage(SignInPage.class);

		FormTester formTester = tester.newFormTester("signin:signin");
		formTester.submit("submit");

		checkErrors(2);
	}

	private FormTester showRegister() {
		tester.startPage(SignInPage.class);
		tester.assertRenderedPage(SignInPage.class);

		ButtonAjaxBehavior b = getButtonBehavior("signin", "register");
		tester.executeBehavior(b);
		return tester.newFormTester(PATH_REGISTER);
	}

	@Test
	public void testEmptyRegister() {
		FormTester formTester = showRegister();
		formTester.submit("submit");
		checkErrors(8);
	}

	@Test
	public void testRegister() throws ReflectiveOperationException, SecurityException {
		tester.startPage(SignInPage.class);
		tester.assertRenderedPage(SignInPage.class);

		String uid = UUID.randomUUID().toString();
		performRegister(uid, "account.created");
	}

	private FormTester showForget() {
		tester.startPage(SignInPage.class);
		tester.assertRenderedPage(SignInPage.class);
		tester.clickLink("signin:signin:forget");
		return tester.newFormTester("forget:form");
	}

	@Test
	public void testEmptyForget() {
		FormTester formTester = showForget();
		formTester.submit("submit");
		checkErrors(2);
	}

	@Test
	public void testForget() throws SecurityException, ReflectiveOperationException {
		tester.startPage(SignInPage.class);
		tester.assertRenderedPage(SignInPage.class);

		performForget(UUID.randomUUID().toString());
	}

	@Test
	public void testReset() {
		tester.startPage(ResetPage.class, new PageParameters().add(ResetPage.RESET_PARAM, UUID.randomUUID().toString()));
		tester.assertRenderedPage(SignInPage.class);
	}

	private String getCaptcha(String path) throws ReflectiveOperationException, SecurityException {
		// hacks with reflection
		Image captchaImg = (Image)tester.getComponentFromLastRenderedPage(path);
		Method m1 = Image.class.getDeclaredMethod("getImageResource");
		m1.setAccessible(true);
		CaptchaImageResource captcha = (CaptchaImageResource)m1.invoke(captchaImg);
		Method m2 = CaptchaImageResource.class.getDeclaredMethod("getImageData", Attributes.class);
		m2.setAccessible(true);
		m2.invoke(captcha, (Attributes)null);
		return captcha.getChallengeId();
	}

	private void performRegister(String uid, String lbl) throws ReflectiveOperationException, SecurityException {
		ButtonAjaxBehavior b1 = getButtonBehavior("signin", "register");
		tester.executeBehavior(b1);
		FormTester formTester = tester.newFormTester(PATH_REGISTER);
		formTester.setValue("login", getLogin(uid));
		formTester.setValue("email", getEmail(uid));
		formTester.setValue("firstName", String.format("first%s", uid));
		formTester.setValue("lastName", String.format("last%s", uid));
		formTester.setValue("password", userpass);
		formTester.setValue("confirmPassword", userpass);
		formTester.setValue("captcha:captchaText", getCaptcha("register:form:captcha:captcha"));
		formTester.submit("submit");
		checkErrors(0);
		tester.assertLabel("register:confirmRegistration:container:message", getEscapedString(lbl));
		ButtonAjaxBehavior b2 = getButtonBehavior("register:confirmRegistration", "OK");
		tester.executeBehavior(b2);
	}

	private void performForget(String uid) throws ReflectiveOperationException, SecurityException {
		int type = rnd.nextInt(2);
		FormTester forgetTester = showForget();
		forgetTester.select("type", type);
		forgetTester.setValue("name", type == 0 ? getEmail(uid) : getLogin(uid));
		forgetTester.setValue("captcha:captchaText", getCaptcha("forget:form:captcha:captcha"));
		forgetTester.submit("submit");
		checkErrors(0);
		tester.assertLabel("forget:confirmDialog:container:message", getEscapedString("321"));
	}

	// complex test
	@Test
	public void testCompleteRegister() throws ReflectiveOperationException, SecurityException {
		// set activation properties
		List<Configuration> cfgs = cfgDao.get(CONFIG_EMAIL_AT_REGISTER, CONFIG_EMAIL_VERIFICATION);
		for (Configuration c : cfgs) {
			c.setValueB(true);
			cfgDao.update(c, null);
		}
		try {
			tester.startPage(SignInPage.class);
			tester.assertRenderedPage(SignInPage.class);

			String uid = String.valueOf(Math.abs(rnd.nextLong())); // number uid is used to prevent password validation errors
			performRegister(uid, "warn.notverified");

			// activate
			User u = userDao.getByLogin(getLogin(uid), User.Type.user, null);
			assertNotNull(u);
			assertFalse(u.getRights().contains(User.Right.Login));
			tester.startPage(ActivatePage.class, new PageParameters().add(ActivatePage.ACTIVATION_PARAM, u.getActivatehash()));
			tester.assertRenderedPage(SignInPage.class);

			// check activated
			u = userDao.getByLogin(getLogin(uid), User.Type.user, null);
			assertNotNull(u);
			assertNull(u.getActivatehash());
			assertTrue(u.getRights().contains(User.Right.Login));
			checkLogin(getEmail(uid), userpass);

			// logout
			Locale loc = tester.getSession().getLocale();
			tester.getSession().invalidateNow();
			tester.getSession().setLocale(loc);

			// forget by 'random'
			performForget(uid);

			// reset password
			u = userDao.getByEmail(getEmail(uid));
			assertNotNull(u);
			assertNotNull(u.getResethash());
			tester.startPage(ResetPage.class, new PageParameters().add(ResetPage.RESET_PARAM, u.getResethash()));
			tester.assertRenderedPage(ResetPage.class);

			// check reset
			String passwd = "q1W@e3r4t5";
			FormTester resetTester = tester.newFormTester("resetPassword:form");
			resetTester.setValue("password", passwd);
			resetTester.setValue("confirmPassword", passwd);
			resetTester.submit("submit");
			checkErrors(0);
			tester.assertLabel("resetPassword:confirmReset:container:message", getEscapedString("332"));
		} finally {
			for (Configuration c : cfgs) {
				c.setValueB(false);
				cfgDao.update(c, null);
			}
		}
	}
}
