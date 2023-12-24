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
package org.apache.openmeetings.service.mail.template;

import java.util.Locale;

import org.apache.openmeetings.IApplication;
import org.apache.openmeetings.IWebSession;
import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Request;

import jakarta.inject.Inject;

public abstract class AbstractTemplatePage extends WebPage {
	private static final long serialVersionUID = 1L;
	public static final String COMP_ID = "container";
	protected final Locale locale;
	protected final ComponentRenderer renderer;

	@Inject
	protected IApplication app;

	protected AbstractTemplatePage(Locale locale) {
		this.locale = locale;
		add(new TransparentWebMarkupContainer(COMP_ID).add(AttributeModifier.append("dir", Session.isRtlLanguage(this.locale) ? "rtl" : "ltr")));
		final Application a = Application.get();
		renderer = new ComponentRenderer(a) {
			@Override
			protected Request newRequest() {
				return new ServletWebRequest(
						new MockHttpServletRequest(a, new MockHttpSession(app.getServletContext()), app.getServletContext())
						, "");
			}
		};
	}

	public static IWebSession getOmSession() {
		return (IWebSession)WebSession.get();
	}

	public String getString(String id, Locale locale, String... params) {
		return app.getOmString(id, locale, params);
	}

	protected String renderEmail() {
		return renderer.renderPage(() -> this).toString();
	}
}
