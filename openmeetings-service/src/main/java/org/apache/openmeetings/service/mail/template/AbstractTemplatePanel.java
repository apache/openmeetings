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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.spring.injection.annot.SpringBean;

public abstract class AbstractTemplatePanel extends Panel {
	private static final long serialVersionUID = 1L;
	public static final String COMP_ID = "template";
	protected final Locale locale;

	@SpringBean
	protected IApplication app;

	protected AbstractTemplatePanel(Locale locale) {
		super(COMP_ID);
		this.locale = locale;
		add(new TransparentWebMarkupContainer("container").add(AttributeModifier.append("dir", Session.isRtlLanguage(this.locale) ? "rtl" : "ltr")));
	}

	public static IWebSession getOmSession() {
		return (IWebSession)WebSession.get();
	}

	public String getString(String id, Locale locale, String... params) {
		return app.getOmString(id, locale, params);
	}
}
