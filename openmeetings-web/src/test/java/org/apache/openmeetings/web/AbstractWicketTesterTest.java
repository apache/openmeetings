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
package org.apache.openmeetings.web;

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;
import static org.apache.openmeetings.web.common.OmWebSocketPanel.CONNECTED_MSG;
import static org.apache.wicket.util.string.Strings.escapeMarkup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.util.OmException;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.feedback.ExactLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.protocol.ws.tester.WebSocketTester;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWicketTesterTest extends AbstractOmServerTest {
	private static final Logger log = LoggerFactory.getLogger(AbstractWicketTesterTest.class);
	public static final String PATH_CHILD = "main-container:main:contents:child";
	public static final String PATH_MENU = "main-container:main:topControls:menu:menu";
	protected WicketTester tester;

	public static WicketTester getWicketTester(Application app) {
		return getWicketTester(app, -1);
	}

	public static WicketTester getWicketTester(Application app, long langId) {
		ensureApplication(langId); // to ensure WebSession is attached

		return new WicketTester(app, app.getServletContext(), false);
	}

	public static void destroy(WicketTester tester) {
		if (tester != null) {
			tester.destroy();
		}
	}

	public String getString(String lbl) {
		return Application.getString(lbl, tester.getSession().getLocale());
	}

	public String getEscapedString(String lbl) {
		return escapeMarkup(getString(lbl)).toString();
	}

	@BeforeEach
	public void setUp() throws Exception {
		tester = getWicketTester(app);
		assertNotNull(WebSession.get(), "Web session should not be null");
		Locale[] locales = Locale.getAvailableLocales();
		Locale l = locales[rnd.nextInt(locales.length)];
		log.info("{} Locale is selected", l);
		tester.getSession().setLocale(l);
	}

	public void login(String login, String password) {
		WebSession s = WebSession.get();
		try {
			if (login != null && password != null) {
				s.signIn(login, password, Type.USER, null);
			} else {
				s.signIn(adminUsername, userpass, Type.USER, null);
			}
		} catch (OmException e) {
			fail(e.getMessage());
		}
		assertTrue(s.isSignedIn(), "Web session is not signed in for user: " + (login != null ? login : adminUsername));
	}

	public AbstractAjaxBehavior getButtonBehavior(String path, int idx) {
		return getButtonBehavior(tester, path, idx);
	}

	public static <T extends Serializable> AbstractAjaxBehavior getButtonBehavior(WicketTester tester, String path, int idx) {
		Args.notNull(path, "path");
		return (AbstractAjaxBehavior)tester.getComponentFromLastRenderedPage(path + ":dialog:footer:buttons:" + idx + ":button").getBehaviorById(0);
	}

	protected void testArea(String user, Consumer<MainPage> consumer) throws OmException {
		assertTrue(((WebSession)tester.getSession()).signIn(user, userpass, User.Type.USER, null));
		MainPage page = tester.startPage(MainPage.class);
		tester.assertRenderedPage(MainPage.class);
		tester.executeBehavior((AbstractAjaxBehavior)page.getBehaviorById(1));
		tester.executeBehavior((AbstractAjaxBehavior)page.get("main-container").getBehaviorById(0));
		WebSocketTester webSocketTester = new WebSocketTester(tester, page);
		webSocketTester.sendMessage(CONNECTED_MSG);

		consumer.accept(page);
		tester.getSession().invalidateNow();
		webSocketTester.destroy();
	}

	public void checkErrors(int count) {
		checkErrors(tester, count);
	}

	public static List<FeedbackMessage> getErrors(WicketTester tester) {
		return tester.getFeedbackMessages(new ExactLevelFeedbackMessageFilter(FeedbackMessage.ERROR));
	}

	public static int countErrors(WicketTester tester) {
		return getErrors(tester).size();
	}

	public static void checkErrors(WicketTester tester, int count) {
		List<FeedbackMessage> errors = getErrors(tester);
		if (count != errors.size()) {
			for (FeedbackMessage fm : errors) {
				log.debug("Error {}", fm);
			}
		}
		assertEquals(count, errors.size(), String.format("There should be exactly %s errors", count));
	}
}
