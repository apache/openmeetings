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
package org.apache.openmeetings.service.mail.template.subject;

import java.util.Locale;

import org.apache.openmeetings.service.mail.template.AbstractTemplatePage;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.markup.html.panel.Fragment;

public abstract class SubjectEmailTemplate extends AbstractTemplatePage {
	private static final long serialVersionUID = 1L;
	private String email = null;
	private String subject = null;
	private boolean created = false;

	SubjectEmailTemplate(Locale locale) {
		super(locale);
	}

	SubjectEmailTemplate create() {
		email = renderEmail();
		subject = ComponentRenderer.renderComponent(getSubjectFragment()).toString();
		created = true;
		return this;
	}

	abstract Fragment getSubjectFragment();

	public final String getEmail() {
		if (!created) {
			throw new IllegalStateException("Not created!!");
		}
		return email;
	}

	public final String getSubject() {
		if (!created) {
			throw new IllegalStateException("Not created!!");
		}
		return subject;
	}
}
